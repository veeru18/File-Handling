package org.vwf.file_handling.upload.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "UF_DOCUMENTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    @Id
    private Long documentId;
    private String originalFileName;
    private String storedFileName;
    private String fileType;
    private String fileLocation; //storing it as file in a disk drive memory location

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    @Override
    public String toString() {
        return "Document{" +
                "documentId=" + documentId +
                ", originalFileName='" + originalFileName + '\'' +
                ", storedFileName='" + storedFileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileLocation='" + fileLocation + '\'' +
                ", user=" + user +
                '}';
    }
}
