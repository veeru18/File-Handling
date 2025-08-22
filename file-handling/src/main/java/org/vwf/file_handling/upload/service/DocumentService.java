package org.vwf.file_handling.upload.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
import org.vwf.file_handling.filters.JwtFilter;
import org.vwf.file_handling.upload.constant.AppConstants;
import org.vwf.file_handling.upload.constant.ErrorMessage;
import org.vwf.file_handling.upload.constant.GenericResponse;
import org.vwf.file_handling.upload.entity.User;
import org.vwf.file_handling.upload.exceptions.UserNotFoundException;
import org.vwf.file_handling.upload.utility.HelperService;
import org.vwf.file_handling.upload.constant.ResponseMessage;
import org.vwf.file_handling.upload.dto.DocumentResponse;
import org.vwf.file_handling.upload.entity.Document;
import org.vwf.file_handling.upload.exceptions.FileNotFoundException;
import org.vwf.file_handling.upload.exceptions.FileUploadFailException;
import org.vwf.file_handling.upload.repository.DocumentRepository;
import org.vwf.file_handling.upload.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    @Value("${app.upload.dir}")
    private String uploadDirectory;

    private final DocumentRepository documentRepository;
    private final HelperService helperService;
    private final ObjectMapper objectMapper;
    private final JwtFilter jwtFilter;
    private final UserRepository userRepository;

    @Transactional
    public GenericResponse<DocumentResponse> uploadFile(MultipartFile multipartFile) throws IOException {
        log.info("Inside uploadFile method");
        if (Objects.isNull(multipartFile) || multipartFile.isEmpty())
            throw new FileUploadFailException(ErrorMessage.FILE_REQUEST_DATA_EMPTY.getMessage());
        User user = userRepository.findByEmail(jwtFilter.loggedInUserId)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));
        Document document = new Document();
        // validating its content type(extension) and filename
        HelperService.validateContentTypeAndFilename(multipartFile, AppConstants.FILE);
        String mediaType = multipartFile.getContentType();
        assert mediaType != null;
        document.setFileType(mediaType);
        String originalFilename = multipartFile.getOriginalFilename();
        document.setOriginalFileName(originalFilename);
        // fileName generated here
        Long documentId = documentRepository.getDocumentValue(); //use a sequence to generate
        String sysGenFilename = HelperService.generateStoredFileName(user.getId(), documentId, originalFilename, mediaType);
        document.setStoredFileName(sysGenFilename);

        // to avoid platform dependent seperator
        // generically using /opt/app/uploads path to be platform independent
        Path path = Paths.get(uploadDirectory, "Stored Documents", String.valueOf(user.getId()), "compressed");
        if (!Files.exists(path))
            Files.createDirectories(path);

        String fileLocation = path + "\\" + sysGenFilename;
        document.setFileLocation(fileLocation);
        long startTime = System.currentTimeMillis();

        byte[] compressedFile;
        if (mediaType.startsWith("image"))
            compressedFile = helperService.convertToWebP(multipartFile, 0.6f);
            // file compressed here if greater than 0.1MB(100KB)
        else {
            // LZ-77 the encoder algorithm behind the gzip/deflate compression doesn't look at media Type
            // it rearranges bytes arrays and get it corrupted and make the files "unreadable" by OS soft
            // even though it doesn't decompress already compressed data(best for textual data)
            if (AppConstants.FORMATS_LIST.stream().noneMatch(mediaType::contains)) {
                fileLocation = "compressed_" + fileLocation;
                compressedFile = helperService.compressFile(multipartFile);
            }
            else
                compressedFile = multipartFile.getBytes();
        }
        long compressedTime = System.currentTimeMillis();
        // execution for compression in seconds
        double compressionTimeInSecs = HelperService.calculateExecutionTime(compressedTime, startTime);
        log.info("execution for compression in seconds: {}", compressionTimeInSecs);
        // file written here
        HelperService.writeToFile(compressedFile, fileLocation);
        long stopTime = System.currentTimeMillis();
        // execution for write in seconds
        double writeTimeInSecs = HelperService.calculateExecutionTime(stopTime, compressedTime);
        log.info("execution for write in seconds: {}", writeTimeInSecs);
        document.setDocumentId(documentId);
        Document savedDocument = documentRepository.save(document);
        log.info("File saved successfully.. originalName: {} and path stored: {}", originalFilename, fileLocation);

        // converting to DTO here
        DocumentResponse documentResponse = objectMapper.convertValue(savedDocument, DocumentResponse.class);
        documentResponse.setFileSize(HelperService.sizeInMb(compressedFile.length));
        documentResponse.setFileWriteTime(writeTimeInSecs);
        documentResponse.setCompressionTime(compressionTimeInSecs);
        // success response
        return GenericResponse.success(ResponseMessage.DOCUMENT_SAVE_SUCCESS.getMessage(), documentResponse);
    }

    public ResponseEntity<InputStreamResource> getFile(Long documentId, String dispositionType) {
        log.info("Inside getFile method.. docID: {}, dispType: {}", documentId, dispositionType);
        if (Stream.of(documentId, dispositionType).anyMatch(ObjectUtils::isEmpty))
            throw new FileNotFoundException(ErrorMessage.FILE_REQUEST_DATA_EMPTY.getMessage());

        User user = userRepository.findByEmail(jwtFilter.loggedInUserId)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new FileNotFoundException(ErrorMessage.FILE_NOT_FOUND.getMessage()));
        String fileLocation = document.getFileLocation();
        Path compressedFilePath = Paths.get(fileLocation);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(document.getFileType()));
        String originalFileName = document.getOriginalFileName();
        headers.setContentDispositionFormData(dispositionType, originalFileName);

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(helperService.readFromFile(user.getId(), compressedFilePath, originalFileName));
    }
}
