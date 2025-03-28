package com.iuc.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageFile {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name="uuid",strategy = "uuid")
    private String id;//tahmin edilememesi için string
    private String name;
    private String type;
    private long length;

    @OneToOne(cascade = CascadeType.ALL)
    private ImageData imageData;
    public ImageFile(String name,String type,ImageData imageData){
        this.name=name;
        this.type=type;
        this.imageData=imageData;
        this.length=imageData.getData().length;
    }

}
