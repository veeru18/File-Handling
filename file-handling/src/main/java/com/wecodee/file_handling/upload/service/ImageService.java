package com.wecodee.file_handling.upload.service;

import com.wecodee.file_handling.upload.constant.ApiResponse;
import com.wecodee.file_handling.upload.constant.HelperService;
import com.wecodee.file_handling.upload.constant.ResponseMessage;
import com.wecodee.file_handling.upload.entity.Document;
import com.wecodee.file_handling.upload.entity.Image;
import com.wecodee.file_handling.upload.entity.User;
import com.wecodee.file_handling.upload.repository.ImageRepository;
import com.wecodee.file_handling.upload.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public ApiResponse<JSONObject> writeImageToDb(Long userId, MultipartFile multipartFile) {
        log.info("Inside uploadFile method, userId:{}", userId);
        JSONObject responseObject = new JSONObject();
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty())
                return ApiResponse.failure(ResponseMessage.DOCUMENT_SAVE_FAILED.getMessage(), responseObject);
            if (ObjectUtils.isEmpty(multipartFile) || multipartFile.isEmpty())
                throw new RuntimeException("uploaded image fileData is empty, check logs");
            Image image = new Image();
            image.setImageFileName(multipartFile.getOriginalFilename());
            image.setImageType(multipartFile.getContentType());

            long startTime = System.currentTimeMillis();
            Image imageData = saveImageData(user.get(), image, multipartFile);

            long stopTime = System.currentTimeMillis();
            double writeTimeInSecs = HelperService.calculateExecutionTime(stopTime, startTime);
            System.out.println("execution for write into db in seconds: "+ writeTimeInSecs);


            responseObject.put("image", imageData);
            responseObject.put("write to file time", writeTimeInSecs);
            // success response
            return ApiResponse.success("Image save success", responseObject);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Image saveImageData(User user, Image thisImage, MultipartFile multipartFile) throws Exception {
        Optional<Image> existImgRecord = imageRepository.findByImageFileName(thisImage.getImageFileName());
        String filename, contentType;
        Image previousImgState = null;
        if (existImgRecord.isEmpty()) {
//            Long imageId = imageRepository.getImageIdValue();
//            filename = imageType + imageId + ".png";
            filename = multipartFile.getOriginalFilename();
            contentType = MediaType.IMAGE_PNG_VALUE;
//            thisImage.setImageId(imageId);
            thisImage.setImageFileName(filename);
            //default value gets changed if we have multipart file
            thisImage.setImageType(contentType);

            thisImage.setUser(user);
        } else {
            thisImage = existImgRecord.get();
            filename = thisImage.getImageFileName();
            contentType = thisImage.getImageType();
            previousImgState = thisImage;
        }

        boolean isSameHashData = false;
        if (ObjectUtils.isNotEmpty(multipartFile) && !multipartFile.isEmpty()) {
            byte[] imageBytes = multipartFile.getBytes();
            String encodedImageData = Base64.getEncoder().encodeToString(imageBytes);
            filename = multipartFile.getOriginalFilename();

            contentType = multipartFile.getContentType();
            if (ObjectUtils.isNotEmpty(thisImage.getImageData())) {
                isSameHashData = compareImageDataAfterHash(encodedImageData, thisImage.getImageData());
            }
            if(!isSameHashData)
                thisImage.setImageData(encodedImageData); // image to bytes converted here
        } else {
            String imageData = thisImage.getImageData();
            if(ObjectUtils.isNotEmpty(imageData)) {
                if(!isEncodedData(imageData)) {
                    imageData = Base64.getEncoder().encodeToString(imageData.getBytes());
                }
                isSameHashData = compareImageDataAfterHash(imageData, thisImage.getImageData());
                if (!isSameHashData)
                    thisImage.setImageData(imageData);
            }
        }
//        existingImage.(true);
        // it fails only at update using same imageData,
        // when its new save or old update with new fileData, repo.save isn't called
        if (!isSameHashData) {
            thisImage.setImageType(contentType);
            thisImage.setImageFileName(filename);
            imageRepository.save(thisImage);
            return thisImage;
        } else {
            // sending previously existing image from db
            return previousImgState;
        }
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

    public boolean compareImageDataAfterHash(String newBase64Data, String existingBase64Data) throws Exception {
        log.info("Inside the compareImageDataHashes method");
        // comparing 2 images with their byte array content's hash which compares only a part, for quick compare
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        // even when images different names but same image data aka same image,
        // it shouldn't be stored thus creating hashes for compare
        byte[] updatableImgHash = md.digest(Base64.getDecoder().decode(newBase64Data));
        byte[] existingImgHash = md.digest(Base64.getDecoder().decode(existingBase64Data));
        return Arrays.equals(updatableImgHash, existingImgHash);
    }

    public ResponseEntity<InputStreamResource> getFile(Long userId, Long imageId, String disType) {
        log.info("Inside getFile method..");
        try {
            if(!userRepository.existsById(userId))
                throw new RuntimeException("UserId doesnot exists");
            Optional<Image> imageOptional = imageRepository.findById(imageId);
            if(imageOptional.isEmpty())
                throw new RuntimeException("DocumentId doesnot exists");
            Image image = imageOptional.get();
//            String fileLocation = image.getFileLocation();
//            Path compressedFilePath = Paths.get(fileLocation);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(image.getImageType()));
            String originalFileName = image.getImageFileName();
            headers.setContentDispositionFormData(disType, originalFileName);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(image.getImageData()));
            return ResponseEntity.status(HttpStatus.OK)
                    .headers(headers)
                    .body(new InputStreamResource(byteArrayInputStream));
        } catch (Exception e) {
            log.info("exception in getFile method", e);
            return ResponseEntity.badRequest().body(null);
        }
    }
}
