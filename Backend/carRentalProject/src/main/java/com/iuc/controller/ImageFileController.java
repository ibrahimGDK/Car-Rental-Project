package com.iuc.controller;

import com.iuc.dto.ImageFileDTO;
import com.iuc.dto.response.ImageSavedResponse;
import com.iuc.dto.response.ResponseMessage;
import com.iuc.dto.response.SfResponse;
import com.iuc.entities.ImageFile;
import com.iuc.service.ImageFileService;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/files")
public class ImageFileController {
    private final ImageFileService imageFileService;

    public ImageFileController(ImageFileService imageFileService) {
        this.imageFileService = imageFileService;
    }
    //!!!!  UPLOAD
    //Önce Image sisteme yüklenir, sonra ID döner. ID üzerinden car objesi DB'ye kaydedilir
    //ImageId: 8a8e8082869453000186945343200000
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImageSavedResponse> uploadFile(
            @RequestParam("file") MultipartFile file){//görsel dosyanın parçalanmış halde gönderilmesini sağlar.(Parça derken ismi, bytları vs.)
        // Requestten gelecek görsel dosyayı MultipartFile türünde file dosyası ile maple
        // POSTMAN'de Body-key bölümüne file yazacağız
        String imageId = imageFileService.saveImage(file);
        ImageSavedResponse response = new ImageSavedResponse(imageId, ResponseMessage.IMAGE_SAVED_RESPONSE_MESSAGE,true);
        return ResponseEntity.ok(response);
    }

    //DOWNLOAD
    @GetMapping("/download/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String id){//byte arraylar zaten fotoğraflarımdır. Bu yüzden dönen  byte[] olmalı
        ImageFile imageFile= imageFileService.getImageById(id); // Tüm çağırmaları ImageFile üzerinden yaptığımız için ImageFile gelecek

        return ResponseEntity.ok().header(//body de ne göndereceksem onları header a koyarım
                HttpHeaders.CONTENT_DISPOSITION, // istemciye bu yanıtın bir dosya indirmesi gerektiğini belirtir.
                "attachment;filename=" + imageFile.getName() + "\"")
                .contentType(MediaType.parseMediaType(imageFile.getType())) //indirirken DB'ki isim otomatik gelir. Aksi durumda indirirken biz dosya ismini belirtmemizz gerekir
                .body(imageFile.getImageData().getData());
    }
    //Image DISPLAY
    @GetMapping("/display/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<byte[]> displayFile(@PathVariable String id){
        ImageFile imageFile= imageFileService.getImageById(id);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.IMAGE_PNG);// Yanıtın içeriğinin bir PNG resmi olduğunu belirtir. (Bu kısmı dinamik hale getirip, dosyanın gerçek MIME tipine göre de ayarlayabilirsiniz.)

        return new ResponseEntity<>(imageFile.getImageData().getData(),
                header,
                HttpStatus.OK);
    }
    //----GET ALL IMAGES------//*Image'lar istendiği zaman imagelerin kendisi değil URLler gelmeli. Aksi durumda tüm imageler gelirse sistem çöker
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ImageFileDTO>> getAllImages(){
        List<ImageFileDTO> allImageDTO = imageFileService.getAllImages();

        return ResponseEntity.ok(allImageDTO);
    }

    //!!! ******** Delete Image ************
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SfResponse> deleteImageFile(@PathVariable String id) {
        imageFileService.removeById(id);

        SfResponse response = new SfResponse(
                ResponseMessage.IMAGE_DELETED_RESPONSE_MESSAGE,true);
        return ResponseEntity.ok(response);
    }
}