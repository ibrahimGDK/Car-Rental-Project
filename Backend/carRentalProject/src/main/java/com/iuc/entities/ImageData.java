package com.iuc.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

@Entity

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private byte[] data;

    public ImageData(byte[] data){
        this.data=data;
    }
    public ImageData(Long id){
        this.id=id;
    }
    public byte[] getData() {
        return data;
    }
}
