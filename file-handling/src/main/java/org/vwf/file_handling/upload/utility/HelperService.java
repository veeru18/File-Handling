package org.vwf.file_handling.upload.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.vwf.file_handling.upload.constant.AppConstants;
import org.vwf.file_handling.upload.constant.ErrorMessage;
import org.vwf.file_handling.upload.exceptions.InvalidContentTypeException;
import org.vwf.file_handling.upload.exceptions.InvalidFormatTypeException;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

@Component
@RequiredArgsConstructor
public class HelperService {
    private static final Logger log = LoggerFactory.getLogger(HelperService.class);
    private static final String SECRET_KEY = "veeresh";
    private static final String SALT = "veeresh6362";

    @Value("${app.upload.dir}")
    private String uploadDirectory;
    private final ObjectMapper objectMapper;

    public static double sizeInMb(int compressedFileSize) {
        try {
            return compressedFileSize / (1024.0 * 1024.0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static double calculateExecutionTime(long currentTimeInMillis, long previousTimeInMillis) {
        return (currentTimeInMillis - previousTimeInMillis) / 1000.0;
    }

    public static void validateContentTypeAndFilename(MultipartFile multipartFile, String uploadType) {
        String contentType = multipartFile.getContentType();
        String filename = multipartFile.getOriginalFilename();
        if (ObjectUtils.isEmpty(contentType) || contentType.startsWith("video"))
            throw new InvalidContentTypeException(ErrorMessage.DOC_INVALID_TYPE.getMessage());
        if (uploadType.equals(AppConstants.IMAGE) &&
                (Objects.isNull(filename) || Objects.isNull(AppConstants.IMG_EXTENSIONS_ALLOWED
                        .getOrDefault(filename.substring(filename.lastIndexOf(".")), null))))
            throw new InvalidFormatTypeException(ErrorMessage.DOC_INVALID_FORMAT_TYPE.getMessage());
    }

    public static boolean writeToFile(byte[] data, String outputPath) throws IOException {
        File file = new File(outputPath);

        // Ensure parent directories exist
//        file.getParentFile().mkdirs();
        // max it can load to memory, 100mb medium size files, else use bufferedOutputStream on this outputStream
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
            fos.flush(); // Optional but good practice
            return true;
        }
    }

    public static String generateStoredFileName(Long userId, Long documentId, String originalFilename, String mediaType) {
        if (mediaType.startsWith("image/")) {
            String newFileName = originalFilename.replaceAll(
                    originalFilename.substring(originalFilename.lastIndexOf(".")), ".webp");
            return "compressed_" + userId + "$DOC-" + documentId + "$" + newFileName;
        }
        return userId + "$DOC-" + documentId + "$" + originalFilename;
    }

    public InputStreamResource readFromFile(Path compressedFilePath) {
        log.info("Inside readFromFile method");
        try {
//            Path exactPath = Paths.get(uploadDirectory, "Stored Documents", String.valueOf(userId), "compressed");
//            Path exactPath = Paths.get("C:", "Veeresh", "Stored Documents", String.valueOf(userId));
            if (!Files.exists(compressedFilePath))
                throw new RuntimeException("File does not exist at " + compressedFilePath);
//            File file = new File(path.toString());
//            byte[] bytes = decompressData(Files.readAllBytes(compressedFilePath));
//            String decompressedPath = exactPath + "\\" + originalFileName;
            // Just writing to view the docs sent, whereas compressed(aka name changed) ones are unable to be opened
//            writeToFile(bytes, decompressedPath);
            //after write of decompressed file, reading it again
//            File decompressedFile = new File(decompressedPath);
            return new InputStreamResource(new FileInputStream(compressedFilePath.toFile()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] compressData(byte[] inputData) throws IOException {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             DeflaterOutputStream deflaterStream = new DeflaterOutputStream(byteStream)) {

            deflaterStream.write(inputData); // operates on "byteStream" to compress
            deflaterStream.finish();  // Mark compression complete
            deflaterStream.flush();
            return byteStream.toByteArray();
        }
    }

    public byte[] compressFile(MultipartFile file) throws IOException {
        log.info("Inside compressFile method..");
        byte[] fileBytes = file.getBytes();
        double originalSize = findUploadedDataSizeInMb(fileBytes);
        log.info("original file size: {} MB", originalSize);
        byte[] compressedFile;
        // keeping 100kb as limit to compress
        if (originalSize > 1.0) {
            compressedFile = compressData(fileBytes);
            double compressedSize = findUploadedDataSizeInMb(compressedFile);
            log.info("compressed data size: {} MB", compressedSize);
        } else compressedFile = fileBytes;
        return compressedFile;
    }

    public byte[] compressString(String data) throws IOException {
        return compressData(data.getBytes(StandardCharsets.UTF_8));
    }


    public byte[] convertToWebP(MultipartFile inputImage, float quality) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(inputImage.getInputStream());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageOutputStream imgOutputStream = ImageIO.createImageOutputStream(baos);

        // getImageWriter might throw exception
        boolean hasWriter = ImageIO.getImageWritersByFormatName("webp").hasNext();
        if (!hasWriter)
            throw new RuntimeException("No ImageIOWriter exists");
        System.out.println("Image IO writer found,");

        ImageWriter writer = ImageIO.getImageWritersByFormatName("webp").next();
        writer.setOutput(imgOutputStream);

        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        String[] compressionTypes = param.getCompressionTypes();
        System.out.println(Arrays.toString(compressionTypes));
        param.setCompressionType("Lossy");
        param.setCompressionQuality(quality); // quality: 0.0 (lowest) to 1.0 (best), give prefarably be it 0.6f

        writer.write(null, new javax.imageio.IIOImage(bufferedImage, null, null), param);

        imgOutputStream.close();
        writer.dispose();

        return baos.toByteArray(); // WebP-compressed image
    }

    public byte[] decompressData(byte[] compressedData) throws IOException {
        try (ByteArrayInputStream input = new ByteArrayInputStream(compressedData);
             InflaterInputStream inflater = new InflaterInputStream(input); // operates on "input" to decompress
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inflater.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
            return output.toByteArray();
        }
    }

    public double findUploadedDataSizeInMb(byte[] mediaFile) {
        log.info("Inside findUploadedDataSizeInMb method");
        try {
//            byte[] decodedMedia = Base64.getDecoder().decode(mediaFile);
            double sizeInKb = (double) mediaFile.length / 1024.0;
            return sizeInKb / 1024.0;
        } catch (Exception e) {
            log.error("Excpetion in findUploadedDataSizeInMb: ", e);
            return Long.MIN_VALUE;
        }
    }

    public boolean containsSameContent(Object a, Object b) {
        return objectMapper.convertValue(a, Map.class)
                .equals(objectMapper.convertValue(b, Map.class));
    }

    private static Integer getTotalPages(int totalResults, int pageSize) {
        int result;
        if (totalResults % pageSize == 0) {
            result = totalResults / pageSize;
        } else {
            result = totalResults / pageSize + 1;
        }
        return result;
    }

    public static <T> JSONObject getPaginatedList(List<T> sourceList, int pageNumber, int pageSize) {
//        pageNumber = pageNumber + 1;
        List<T> paginatedList = getPage(sourceList, pageNumber, pageSize);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("currentPage", pageNumber);
        jsonObject.put("totalItems", sourceList.size());
        jsonObject.put("totalPages", getTotalPages(sourceList.size(), pageSize));
        jsonObject.put("items", paginatedList);
        return jsonObject;
    }

    private static <T> List<T> getPage(List<T> sourceList, int page, int pageSize) {
        if (pageSize <= 0 || page <= 0) {
            throw new IllegalArgumentException("Invalid page size: " + pageSize + " or page: "+page);
        }
        int fromIndex = (page - 1) * pageSize;
        if (sourceList == null || sourceList.size() < fromIndex) {
            return Collections.emptyList();
        }
        // toIndex exclusive
//        return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
        return sourceList.stream()
                .skip(fromIndex)
                .limit(pageSize).toList();
    }

    public static Date getCurrentDateTime() {
        return new Date(new Date().getTime());
    }

    public static Date getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = sdf.parse(sdf.format(new Date()));
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String formatDateAsString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getObjectFromFunctionData(String functionJsonData, Class<?> readClass) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(functionJsonData, readClass);
    }

    public static Date formatDate(Date fromDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            fromDate = sdf.parse(sdf.format(fromDate));
            return fromDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date formattingDateAndTime(Date fromDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
        try {
            fromDate = sdf.parse(sdf.format(fromDate));
            return fromDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatDateAndTimeAsString(Date fromDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
        try {
            return sdf.format(fromDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatDateAndTimeWithoutTimeZoneAsString(Date fromDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.format(fromDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String formatDateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = formatter.format(date);
        return strDate;
    }

    public Map<Boolean, String> validatePassword(String password) {
        Map<Boolean, String> validationMap = new HashMap<>();
        Map<String, Integer> charCountsMap = new HashMap<>();
        for (Character c : password.toCharArray()) {
            if (Character.isLetter(c))
                charCountsMap.put(AppConstants.CHAR, charCountsMap.getOrDefault(AppConstants.CHAR, 0) + 1);
            else if (Character.isDigit(c))
                charCountsMap.put(AppConstants.DIGIT, charCountsMap.getOrDefault(AppConstants.DIGIT, 0) + 1);
            else if (Character.isWhitespace(c))
                charCountsMap.put(AppConstants.WHITESPACE, charCountsMap.getOrDefault(AppConstants.WHITESPACE, 0) + 1);
            else
                charCountsMap.put(AppConstants.SPECIAL, charCountsMap.getOrDefault(AppConstants.SPECIAL, 0) + 1);
        }
        for (Map.Entry<String, Integer> entry : charCountsMap.entrySet()) {
            if (entry.getKey().equals(AppConstants.WHITESPACE) && entry.getValue() > 0) {
                validationMap.put(false, ErrorMessage.PASSWORD_CONTAINS_WHITESPACE.getMessage());
                return validationMap;
            } else if (entry.getValue() == 0) {
                validationMap.put(false, ErrorMessage.PWD_CHAR_COUNTS_NOT_VALID.getMessage()
                        + entry.getKey() + " character");
                return validationMap;
            }
        }
        return validationMap;
    }

}
