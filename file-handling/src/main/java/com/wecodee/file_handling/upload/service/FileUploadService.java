package com.wecodee.file_handling.upload.service;

import com.wecodee.file_handling.upload.constant.ApiResponse;
import com.wecodee.file_handling.upload.constant.HelperService;
import com.wecodee.file_handling.upload.constant.ResponseMessage;
import com.wecodee.file_handling.upload.entity.Document;
import com.wecodee.file_handling.upload.repository.DocumentRepository;
import com.wecodee.file_handling.upload.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileUploadService {
    private static final Logger log = LoggerFactory.getLogger(FileUploadService.class);

    @Value("${app.upload.dir}")
    private String uploadDirectory;

    private final DocumentRepository documentRepository;
    private final HelperService helperService;
    private final UserRepository userRepository;

    @Transactional
    public ApiResponse<JSONObject> uploadFile(Long userId, MultipartFile multipartFile) {
        log.info("Inside uploadFile method, userId:{}", userId);
        JSONObject responseObject = new JSONObject();
        try {
            if (!userRepository.existsById(userId))
                return ApiResponse.failure(ResponseMessage.DOCUMENT_SAVE_FAILED.getMessage(), responseObject);
            Document document = new Document();
            String mediaType = multipartFile.getContentType();
            if(ObjectUtils.isEmpty(mediaType) || (mediaType.contains("video")
//                    || (contentType.contains("application") && !contentType.equals(MediaType.APPLICATION_JSON_VALUE))
            ))
                return ApiResponse.failure("Unsupported file type/Already a compressed file type");
            document.setFileType(mediaType);
            String originalFilename = multipartFile.getOriginalFilename();
            if (ObjectUtils.isEmpty(originalFilename) || !originalFilename.contains("."))
                throw new RuntimeException("Filename/extension doesn't exist please check");

            document.setOriginalFileName(originalFilename);
            // fileName generated here
            Long documentId = documentRepository.getDocumentValue(); //use a sequence to generate
            String sysGenFilename = generateStoredFileName(userId, documentId, originalFilename, mediaType);
            document.setStoredFileName(sysGenFilename);

//            String directory = "C:\\Veeresh\\Stored Documents" + "\\";

            // to avoid platform dependent seperator
            // generically using /opt/app/uploads path to be platform independent
            Path path = Paths.get(uploadDirectory, "Stored Documents", String.valueOf(userId), "compressed");
            if (!Files.exists(path))
                Files.createDirectories(path);

            String fileLocation = path + "\\" + sysGenFilename;
            document.setFileLocation(fileLocation);
            long startTime = System.currentTimeMillis();

            byte[] compressedFile;
            if(mediaType.startsWith("image"))
                compressedFile = helperService.convertToWebP(multipartFile, 0.6f);
            // file compressed here if greater than 0.1MB(100KB)
            else {
                // LZ-77 the encoder behind the gzip/deflate compression doesn't look at media Type
                // it rearranges bytes arrays and get it corrupted and make the files "unreadable" by OS soft
                // even though it doesn't decompress already compressed formats like pdf/png/zip/jpg
                String[] mediaTypes = {"jpg", "jpeg", "png", "webp", "gif", "mp3", "aac", "flac", "mp4", "mpeg", "av1", "mkv", "zip", "pdf"};
                if(Arrays.stream(mediaTypes).noneMatch(mediaType::contains))
                    compressedFile = helperService.compressFile(multipartFile);
                else {
                    fileLocation = fileLocation.replaceAll("compressed_","");
                    compressedFile = multipartFile.getBytes();
                }
            }
            long compressedTime = System.currentTimeMillis();
            // execution for compression in seconds
            double compressionTimeInSecs = HelperService.calculateExecutionTime(compressedTime, startTime);
            System.out.println("execution for compression in seconds: "+ compressionTimeInSecs);
            // file written here
            HelperService.writeToFile(compressedFile, fileLocation);
            long stopTime = System.currentTimeMillis();
            // execution for write in seconds
            double writeTimeInSecs = HelperService.calculateExecutionTime(stopTime, compressedTime);
            System.out.println("execution for write in seconds: "+ writeTimeInSecs);
            document.setDocumentId(documentId);
            Document savedDocument = documentRepository.save(document);
            log.info("File saved successfully.. originalName: {} and path stored: {}", originalFilename, fileLocation);

            responseObject.put("compressedData size", HelperService.sizeInMb(compressedFile.length));
            responseObject.put("compression time taken", compressionTimeInSecs);
            responseObject.put("write to file time", writeTimeInSecs);
            responseObject.put("docId", savedDocument.getDocumentId());
            // success response
            return ApiResponse.success(ResponseMessage.DOCUMENT_SAVE_SUCCESS.getMessage(), responseObject);
        } catch (Exception e) {
            log.error("Exception in uploadFile", e);
            responseObject.put("exceptionMessage", e.getMessage());
            return ApiResponse.failure(ResponseMessage.DOCUMENT_SAVE_FAILED.getMessage(), responseObject);
        }
    }

    private String generateStoredFileName(Long userId, Long documentId, String originalFilename, String mediaType) {
        if(mediaType.startsWith("image/")) {
            String newFileName = originalFilename.replaceAll(
                    originalFilename.substring(originalFilename.lastIndexOf(".")), ".webp");
            return "compressed_" + userId + "$DOC-" + documentId + "$" + newFileName;
        }
        return "compressed_" + userId + "$DOC-" + documentId + "$" + originalFilename;
    }

    public ResponseEntity<InputStreamResource> getFile(Long userId, Long documentId, String dispositionType) {
        log.info("Inside getFile method..");
        try {
            if(!userRepository.existsById(userId))
                throw new RuntimeException("UserId doesnot exists");
            Optional<Document> documentOptional = documentRepository.findById(documentId);
            if(documentOptional.isEmpty())
                throw new RuntimeException("DocumentId doesnot exists");
            Document document = documentOptional.get();
            String fileLocation = document.getFileLocation();
            Path compressedFilePath = Paths.get(fileLocation);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(document.getFileType()));
            String originalFileName = document.getOriginalFileName();
            headers.setContentDispositionFormData(dispositionType, originalFileName);

            return ResponseEntity.status(HttpStatus.OK)
                    .headers(headers)
                    .body(helperService.readFromFile(userId, compressedFilePath, originalFileName));
        } catch (Exception e) {
            log.info("exception in getFile method", e);
            return ResponseEntity.badRequest().body(null);
        }
    }
}
