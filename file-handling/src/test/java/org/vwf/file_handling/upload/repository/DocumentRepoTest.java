package org.vwf.file_handling.upload.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.vwf.file_handling.upload.entity.Document;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
// uses internally self configured h2 db
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DocumentRepoTest {

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void findAllReturnsNonNull() {
        List<Document> docs = documentRepository.findAll();
        assertThat(docs).isNotEmpty(); // AssertJ method
    }

    @Test
    public void findByFileNameReturnsEmpty() {
        Optional<Document> byId = documentRepository.findById(20L);
        assertThat(byId).isNotPresent();
        Long someValue = documentRepository.getDocumentValue();
        assertThat(someValue).isNotNegative();
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
        Document mockDoc = new Document();
//        mockDoc.setUser(finalUser); // not mandatory
        mockDoc.setDocumentId(10L);
        mockDoc.setFileType("image/gif");
        mockDoc.setOriginalFileName("someFile.pdf");

        documentRepository.save(mockDoc);

        Optional<Document> byId = documentRepository.findById(10L);
        assertThat(byId).isNotEmpty();
        assertThat(byId).isExactlyInstanceOf(Optional.class);
        Long byImageFileName = documentRepository.getDocumentValue();
        assertThat(byImageFileName).isNotNegative();
    }
}
