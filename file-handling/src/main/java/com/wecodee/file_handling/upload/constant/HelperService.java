package com.wecodee.file_handling.upload.constant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wecodee.file_handling.upload.exceptions.InvalidContentTypeException;
import com.wecodee.file_handling.upload.exceptions.InvalidFormatTypeException;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

//import javax.crypto.Cipher;
//import javax.crypto.SecretKey;
//import javax.crypto.SecretKeyFactory;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.PBEKeySpec;
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.charset.StandardCharsets;
//import java.security.spec.KeySpec;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

@Component
public class HelperService {
    private static final Logger log = LoggerFactory.getLogger(HelperService.class);
    private static final String SECRET_KEY = "veeresh";
    private static final String SALT = "veeresh6362";

    @Value("${app.upload.dir}")
    private String uploadDirectory;

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
                (Objects.isNull(filename) || Objects.nonNull(AppConstants.IMG_EXTENSIONS_ALLOWED
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

    public InputStreamResource readFromFile(Long userId, Path compressedFilePath, String originalFileName) {
        log.info("Inside readFromFile method");
        try {
            Path exactPath = Paths.get(uploadDirectory, "Stored Documents", String.valueOf(userId), "compressed");
//            Path exactPath = Paths.get("C:", "Veeresh", "Stored Documents", String.valueOf(userId));
            if (!Files.exists(compressedFilePath))
                throw new RuntimeException("File does not exist at " + compressedFilePath);
//            File file = new File(path.toString());
            byte[] bytes = decompressData(Files.readAllBytes(compressedFilePath));
            String decompressedPath = exactPath + "\\" + originalFileName;
            // Just writing to view the docs sent, whereas compressed(aka name changed) ones are unable to be opened
            writeToFile(bytes, decompressedPath);
            //after write of decompressed file, reading it again
            File decompressedFile = new File(decompressedPath);
            return new InputStreamResource(new FileInputStream(decompressedFile));
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
        if (originalSize > 0.1) {
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
        ObjectMapper objectMapper = getObjectMapper();
        return objectMapper.convertValue(a, Map.class)
                .equals(objectMapper.convertValue(b, Map.class));
    }

    public ObjectMapper getObjectMapper() {
        log.info("In getObjectMapper");
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // objectMapper changed because of dates received as json array format becoz of jackson-jsr310 dependency
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    private static String getTotalPages(int totalResults, int pageSize) {
        int result = 0;
        if (totalResults % pageSize == 0) {
            result = totalResults / pageSize;
        } else {
            result = totalResults / pageSize + 1;
        }
        return String.valueOf(result);
    }

    public static <T> JSONObject getPaginatedList(List<T> sourceList, int pageNumber, int pageSize) {
        pageNumber = pageNumber + 1;
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
            throw new IllegalArgumentException("invalid page size: " + pageSize);
        }
        int fromIndex = (page - 1) * pageSize;
        if (sourceList == null || sourceList.size() < fromIndex) {
            return Collections.emptyList();
        }
        // toIndex exclusive
        return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
    }

    public static Date getCurrentDateTime() {
        return new Date(new Date().getTime());
    }

    public static Date getCurrentDateAndTime() {
        return new Date();
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

    public static Date findFromDateByPeriod(int period) throws ParseException {
        LocalDate localDate = LocalDate.now().minusDays(period - 1);
        Date fromDate = (Date) java.sql.Date.valueOf(localDate);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        fromDate = sdf.parse(sdf.format(fromDate));
        return fromDate;
    }

    public static Object getObjectFromFunctionData(String functionJsonData, Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Object object = mapper.readValue(functionJsonData, obj.getClass());
        return object;
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

//    public String encryption(String strToEncrypt) {
//        log.debug("encryption method is executing..");
//        try {
//            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//            IvParameterSpec ivspec = new IvParameterSpec(iv);
//            // Create SecretKeyFactory object
//            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
//            // Create KeySpec object and assign with
//            // constructor
//            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
//            SecretKey tmp = factory.generateSecret(spec);
//            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
//
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
//            // Return encrypted string
//            String encryptedString = Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
//            System.out.println(" encrypted string = " + encryptedString);
//            return encryptedString;
//        } catch (Exception e) {
//            log.error(" exception in encryption " + e);
//            return null;
//        }
//    }
//
//    public String decryption(String strToDecrypt) {
//        log.debug("decryption method is executing.. ");
//        try {
//            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0,
//                    0, 0, 0, 0, 0, 0, 0, 0};
//            // Create IvParameterSpec object and assign with
//            // constructor
//            IvParameterSpec ivspec
//                    = new IvParameterSpec(iv);
//
//            // Create SecretKeyFactory Object
//            SecretKeyFactory factory
//                    = SecretKeyFactory.getInstance(
//                    "PBKDF2WithHmacSHA256");
//
//            // Create KeySpec object and assign with
//            // constructor
//            KeySpec spec = new PBEKeySpec(
//                    SECRET_KEY.toCharArray(), SALT.getBytes(),
//                    65536, 256);
//            SecretKey tmp = factory.generateSecret(spec);
//            SecretKeySpec secretKey = new SecretKeySpec(
//                    tmp.getEncoded(), "AES");
//
//            Cipher cipher = Cipher.getInstance(
//                    "AES/CBC/PKCS5PADDING");
//            //AES/CBC/PKCS5Padding
//            cipher.init(Cipher.DECRYPT_MODE, secretKey,
//                    ivspec);
//            // Return decrypted string
//            String decryptedString = new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
//            System.out.println(" decrypted string " + decryptedString);
//            return decryptedString;
//        } catch (Exception e) {
//            log.error(" exception in decryption method " + e);
//            return null;
//        }
//    }
}
