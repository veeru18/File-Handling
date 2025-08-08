package com.wecodee.file_handling.upload.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "UF_IMAGES")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long imageId;
    @Column(name = "IMAGE_DATA")// insertable, updatable, nullable's default is true
    @Basic(fetch = FetchType.LAZY)// to fetch it only when get() method is called
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)// can use @JsonIgnore for no serialization
    private String imageData; // storing it as byte[]/aka clob
    private String imageFileName;
    private String imageType;
//    private String extension;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    @Override
    public String toString() {
        return "Image{" +
                "imageId=" + imageId +
                ", imageData='" + imageData + '\'' +
                ", imageFileName='" + imageFileName + '\'' +
                ", imageType='" + imageType + '\'' +
                ", user=" + user +
                '}';
    }
}
