package com.wecodee.file_handling.upload.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wecodee.file_handling.upload.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class FileUploadServiceTest {

    @InjectMocks
    private FileUploadService fileUploadService;

    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // manually set the @Value field
        ReflectionTestUtils.setField(fileUploadService, "uploadDirectory", "./resources/static/docs");
    }

    @Test
    void getFile_returnsFile() {

    }
}
