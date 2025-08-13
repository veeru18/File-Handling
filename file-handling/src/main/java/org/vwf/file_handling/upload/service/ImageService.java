package org.vwf.file_handling.upload.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.vwf.file_handling.upload.constant.*;
import org.vwf.file_handling.upload.dto.ImageDTO;
import org.vwf.file_handling.upload.entity.Image;
import org.vwf.file_handling.upload.entity.User;
import org.vwf.file_handling.upload.exceptions.*;
import org.vwf.file_handling.upload.repository.ImageRepository;
import org.vwf.file_handling.upload.repository.UserRepository;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {
    private static final Logger log = LoggerFactory.getLogger(ImageService.class);

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public ApiResponse<JSONObject> writeImageToDb(Long userId, MultipartFile multipartFile) throws Exception {
        log.info("Inside uploadFile method, userId:{}", userId);
        JSONObject responseObject = new JSONObject();
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.IMAGE_SAVE_FAIL.getMessage()));
        if (ObjectUtils.isEmpty(multipartFile) || multipartFile.isEmpty())
            throw new FileNotFoundException(ErrorMessage.FILE_UPLOAD_FAILED.getMessage());
        // validating its content type(extension) and filename
        HelperService.validateContentTypeAndFilename(multipartFile, AppConstants.IMAGE);
        Image image = new Image();
        image.setImageFileName(multipartFile.getOriginalFilename());
        image.setImageType(multipartFile.getContentType());

        long startTime = System.currentTimeMillis();
        Image imageData = saveImageData(existingUser, image, multipartFile);

        long stopTime = System.currentTimeMillis();
        double writeTimeInSecs = HelperService.calculateExecutionTime(stopTime, startTime);
        log.info("execution for write into db in seconds: {}", writeTimeInSecs);


        responseObject.put("write to db time in seconds", writeTimeInSecs);

        // converting to DTO here
        responseObject.put("image", objectMapper.convertValue(imageData, ImageDTO.class));
        // success response
        return ApiResponse.success(ResponseMessage.IMAGE_SAVE_SUCCESS.getMessage(), responseObject);
    }

    private Image saveImageData(User user, Image thisImage, MultipartFile multipartFile) throws Exception {
        Optional<Image> existImgRecord = imageRepository.findByImageFileName(thisImage.getImageFileName());
        String filename, contentType;
        Image previousImgState = null;
        if (existImgRecord.isEmpty()) {
            filename = multipartFile.getOriginalFilename();
            contentType = MediaType.IMAGE_PNG_VALUE;
            thisImage.setImageFileName(filename);
            //default value gets changed if we have multipart file
            thisImage.setImageType(contentType);

            thisImage.setUser(user);
        } else {
            previousImgState = existImgRecord.get();
            filename = thisImage.getImageFileName();
            contentType = thisImage.getImageType();
        }

        boolean isSameHashData = false;
        if (ObjectUtils.isNotEmpty(multipartFile) && !multipartFile.isEmpty()) {
            byte[] imageBytes = multipartFile.getBytes();
            String encodedImageData = Base64.getEncoder().encodeToString(imageBytes);
            filename = multipartFile.getOriginalFilename();

            contentType = multipartFile.getContentType();
            if (ObjectUtils.isNotEmpty(thisImage.getImageData())) {
                isSameHashData = compareImageDataUsingHash(encodedImageData, thisImage.getImageData());
            }
            // it fails only at update using same imageData,
            if (isSameHashData)
                throw new ImageAlreadyExistsException(ErrorMessage.IMAGE_ALREADY_EXISTS.getMessage());

            thisImage.setImageData(encodedImageData); // image to bytes converted here
        } else {
            String encodedImageData = thisImage.getImageData();
            if (ObjectUtils.isNotEmpty(encodedImageData)) {
                if (!isEncodedData(encodedImageData)) {
                    encodedImageData = Base64.getEncoder().encodeToString(encodedImageData.getBytes());
                }
                isSameHashData = compareImageDataUsingHash(encodedImageData,
                        ObjectUtils.isEmpty(previousImgState) ? "" : previousImgState.getImageData());

                // it fails only at update using same imageData,
                if (isSameHashData)
                    throw new ImageAlreadyExistsException(ErrorMessage.IMAGE_ALREADY_EXISTS.getMessage());

                thisImage.setImageData(encodedImageData);
            }
        }
        // when its new save or old update with new fileData, repo.save isn't called
        thisImage.setImageType(contentType);
        thisImage.setImageFileName(filename);
        return imageRepository.save(thisImage);
    }

    private boolean isEncodedData(String encodedData) {
        try {
            Base64.getDecoder().decode(encodedData);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public boolean compareImageDataUsingHash(String newBase64Data, String existingBase64Data) throws Exception {
        log.info("Inside the compareImageDataHashes method");
        if (StringUtils.isAnyEmpty(newBase64Data, existingBase64Data))
            throw new EncodedDataEmptyException(ErrorMessage.ENCODED_DATA_EMPTY_ERROR.getMessage());
        // comparing 2 images with their byte array content's hash which compares only a part, for quick compare
        MessageDigest md = MessageDigest.getInstance("MD5");
        // even when images different names but same image data aka same image,
        // it shouldn't be stored thus creating hashes for compare
        byte[] updatableImgHash = md.digest(Base64.getDecoder().decode(newBase64Data));
        byte[] existingImgHash = md.digest(Base64.getDecoder().decode(existingBase64Data));
        return Arrays.equals(updatableImgHash, existingImgHash);
    }

    public ResponseEntity<InputStreamResource> getFile(Long userId, Long imageId, String disType) {
        log.info("Inside getFile method..");
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage());
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException(ErrorMessage.IMAGE_NOT_FOUND.getMessage()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(image.getImageType()));
        String originalFileName = image.getImageFileName();
        headers.setContentDispositionFormData(disType, originalFileName);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(image.getImageData()));
        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(new InputStreamResource(byteArrayInputStream));
    }
}
