package org.vwf.file_handling.upload.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.vwf.file_handling.filters.JwtFilter;
import org.vwf.file_handling.upload.entity.Image;
import org.vwf.file_handling.upload.entity.User;
import org.vwf.file_handling.upload.exceptions.ImageNotFoundException;
import org.vwf.file_handling.upload.exceptions.UserNotFoundException;
import org.vwf.file_handling.upload.repository.ImageRepository;
import org.vwf.file_handling.upload.repository.UserRepository;
import org.vwf.file_handling.upload.utility.HelperService;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Mock
    private ImageRepository imageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ImageService imageService;

    @BeforeEach
    public void setVarsOfDependencies() {
        log.info("Inside setter");
        JwtFilter.loggedInUserId = "veeresh.ta@wecodee.com";
    }

    @AfterEach
    public void reset() {
        log.info("Inside reset");
        JwtFilter.loggedInUserId = null;
    }

    @Test
    public void getImage_throwsException() {
        String email = "veeresh.ta@wecodee.com";
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("Yo");
        mockUser.setPhoneNumber("9916548890");
        mockUser.setEmail(email);
        // when() is static method of Mockito to test based on certain cases
        // that stubs(pushes) its required responses from its Autowired dependencies like thenThrow, thenReturn
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        when(imageRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ImageNotFoundException.class, ()-> imageService.getFile(1L,"inline"));

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(imageRepository, times(1)).findById(anyLong());
    }

    @Test
    public void getImage_throwsUserNotFoundException_whenUserMissing() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> imageService.getFile(1L, "inline"));

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(imageRepository, never()).findById(anyLong()); // not reached
    }

    @Test
    void getImage_returnsImage_whenExists() {
        // Arrange
        Long fileId = 1L;
        String email = "veeresh.ta@wecodee.com";

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("Yo");
        mockUser.setPhoneNumber("9916548890");
        mockUser.setEmail(email);

        Image mockImage = new Image();
        mockImage.setImageId(fileId);
        mockImage.setImageFileName("test.png");
        mockImage.setImageData(new String(new byte[]{1, 2, 3}));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        when(imageRepository.findById(fileId)).thenReturn(Optional.of(mockImage));

        // Act
        ResponseEntity<InputStreamResource> result = imageService.getFile(fileId, "inline");

        // Assert
        assertNotNull(result);
//        assertEquals(fileId, result.getId());
//        assertEquals("test.png", result.getFileName());
        verify(imageRepository, times(1)).findById(anyLong());
    }
}
