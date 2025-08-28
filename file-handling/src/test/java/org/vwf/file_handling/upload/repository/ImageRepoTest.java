package org.vwf.file_handling.upload.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.vwf.file_handling.upload.entity.Image;
import org.vwf.file_handling.upload.entity.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@DataJpaTest
// doesn't use(by internally self configured h2 db), if not added in pom.xml
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ImageRepoTest {

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void findAllReturnsNonNull() {
        List<Image> images = imageRepository.findAll();
        assertThat(images).isNotEmpty(); // AssertJ method
    }

    @Test
    public void findByFileNameReturnsEmpty() {
        Optional<Image> byImageFileName = imageRepository.findByImageFileName(any());
        assertThat(byImageFileName).isNotPresent();
    }

    @Test
    public void findByFileNameReturnsNonEmpty() {
        // not mandatory
//        User mockUser = new User();
//        mockUser.setEmail("email");
//        mockUser.setUsername("user");
//        mockUser.setPhoneNumber("9876543210");
//        User finalUser = userRepository.save(mockUser);
        // mandatory check start here
        Image mockImage = new Image();
//        mockImage.setUser(finalUser); // not mandatory
        mockImage.setImageId(10L);
        mockImage.setImageType("image/gif");
        mockImage.setImageFileName("someFile.pdf");

        imageRepository.save(mockImage);

        Optional<Image> byImageFileName = imageRepository.findByImageFileName("someFile.pdf");
        assertThat(byImageFileName).isNotEmpty();
    }

}
