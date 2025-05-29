package com.iuc.test.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.iuc.controller.ImageFileController;
import com.iuc.dto.ImageFileDTO;
import com.iuc.dto.response.ResponseMessage;
import com.iuc.entities.ImageData;
import com.iuc.entities.ImageFile;
import com.iuc.service.ImageFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ImageFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageFileService imageFileService;

    @Autowired
    private ObjectMapper objectMapper;

    private ImageFile sampleImageFile;
    private ImageFileDTO sampleImageFileDTO;

    @BeforeEach
    void setUp() throws Exception {
        byte[] imageBytes = new byte[]{1, 2, 3, 4};

        ImageData imageData = new ImageData();
        imageData.setId(1L);
        imageData.setData(imageBytes);

        sampleImageFile = new ImageFile();
        sampleImageFile.setId("abc123-uuid");
        sampleImageFile.setName("test.png");
        sampleImageFile.setType("image/png");
        sampleImageFile.setImageData(imageData);
        sampleImageFile.setLength(imageBytes.length);

        sampleImageFileDTO = new ImageFileDTO();
        sampleImageFileDTO.setName("test.png");
        sampleImageFileDTO.setType("image/png");
        sampleImageFileDTO.setSize(imageBytes.length);
        sampleImageFileDTO.setUrl("http://localhost/files/download/abc123-uuid");
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "CUSTOMER"})
    void testDownloadFile() throws Exception {
        when(imageFileService.getImageById("abc123-uuid")).thenReturn(sampleImageFile);

        mockMvc.perform(get("/files/download/abc123-uuid"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment;filename=" + sampleImageFile.getName() + "\""))
                .andExpect(content().contentType(sampleImageFile.getType()))
                .andExpect(content().bytes(sampleImageFile.getImageData().getData()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "CUSTOMER"})
    void testDisplayFile() throws Exception {
        when(imageFileService.getImageById("abc123-uuid")).thenReturn(sampleImageFile);

        mockMvc.perform(get("/files/display/abc123-uuid"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"))
                .andExpect(content().bytes(sampleImageFile.getImageData().getData()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllImages() throws Exception {
        when(imageFileService.getAllImages()).thenReturn(List.of(sampleImageFileDTO));

        mockMvc.perform(get("/files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("test.png"))
                .andExpect(jsonPath("$[0].type").value("image/png"))
                .andExpect(jsonPath("$[0].size").value(sampleImageFileDTO.getSize()))
                .andExpect(jsonPath("$[0].url").value(sampleImageFileDTO.getUrl()));
    }


}