package com.iuc.test.service;

import com.iuc.entities.ImageFile;
import com.iuc.exception.ResourceNotFoundException;
import com.iuc.repository.ImageFileRepository;
import com.iuc.service.ImageFileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageFileServiceTest {

    @Mock
    private ImageFileRepository imageFileRepository;

    @InjectMocks
    private ImageFileService imageFileService;

    @Test
    void testFindImageById_whenExists_shouldReturnImageFile() {
        String id = "abc123";
        ImageFile expectedImage = new ImageFile();
        expectedImage.setId(id);

        when(imageFileRepository.findImageById(id)).thenReturn(Optional.of(expectedImage));

        ImageFile result = imageFileService.findImageById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(imageFileRepository).findImageById(id);
    }

    @Test
    void testFindImageById_whenNotFound_shouldThrowException() {
        String id = "not_found";

        when(imageFileRepository.findImageById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> imageFileService.findImageById(id));
        verify(imageFileRepository).findImageById(id);
    }

    @Test
    void testSaveImage_shouldSaveAndReturnGeneratedId() throws IOException {
        byte[] content = "dummy image data".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", content);

        // save sırasında ID oluşturulmasını simüle et
        ArgumentCaptor<ImageFile> captor = ArgumentCaptor.forClass(ImageFile.class);
        doAnswer(invocation -> {
            ImageFile img = invocation.getArgument(0);
            img.setId("generated-id");
            return null;
        }).when(imageFileRepository).save(any(ImageFile.class));

        String result = imageFileService.saveImage(file);

        verify(imageFileRepository).save(captor.capture());
        ImageFile saved = captor.getValue();

        assertEquals("test.jpg", saved.getName());
        assertEquals("image/jpeg", saved.getType());
        assertEquals(content.length, saved.getLength());
        assertNotNull(saved.getImageData());
        assertEquals("generated-id", result);
    }

    @Test
    void testGetImageById_whenExists_shouldReturnImageFile() {
        String id = "img001";
        ImageFile file = new ImageFile();
        file.setId(id);

        when(imageFileRepository.findById(id)).thenReturn(Optional.of(file));

        ImageFile result = imageFileService.getImageById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(imageFileRepository).findById(id);
    }

    @Test
    void testGetImageById_whenNotExists_shouldThrowException() {
        when(imageFileRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> imageFileService.getImageById("missing"));
        verify(imageFileRepository).findById("missing");
    }



    @Test
    void testRemoveById_shouldDeleteFoundImageFile() {
        String id = "delete123";
        ImageFile file = new ImageFile();
        file.setId(id);

        when(imageFileRepository.findById(id)).thenReturn(Optional.of(file));

        imageFileService.removeById(id);

        verify(imageFileRepository).delete(file);
    }
}
