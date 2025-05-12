package com.iuc.service;

import com.iuc.entities.ImageFile;
import com.iuc.exception.ResourceNotFoundException;
import com.iuc.exception.message.ErrorMessage;
import com.iuc.repository.ImageFileRepository;
import org.springframework.stereotype.Service;

@Service
public class ImageFileService {

    private final ImageFileRepository imageFileRepository;

    public ImageFileService(ImageFileRepository imageFileRepository) {
        this.imageFileRepository = imageFileRepository;
    }

    public ImageFile findImageById(String imageId) {
        return imageFileRepository.findImageById(imageId).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.IMAGE_NOT_FOUND_MESSAGE,imageId)));
    }
}
