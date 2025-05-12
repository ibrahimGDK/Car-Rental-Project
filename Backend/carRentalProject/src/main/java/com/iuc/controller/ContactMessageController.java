package com.iuc.controller;

import com.iuc.dto.ContactMessageDTO;
import com.iuc.dto.request.ContactMessageRequest;
import com.iuc.dto.response.ResponseMessage;
import com.iuc.dto.response.SfResponse;
import com.iuc.entities.ContactMessage;
import com.iuc.mapper.ContactMessageMapper;
import com.iuc.service.ContactMessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/contactmessage")
public class ContactMessageController {
    // @Autowired // field injection yapmamak için commente alındı
    private final ContactMessageService contactMessageService;

    private final ContactMessageMapper contactMessageMapper;

    public ContactMessageController(ContactMessageService contactMessageService, ContactMessageMapper contactMessageMapper) {
        this.contactMessageService = contactMessageService;
        this.contactMessageMapper = contactMessageMapper;
    }

    //!!! create ContactMessage
    @PostMapping("/visitors")
    public ResponseEntity<SfResponse> createMessage(@Valid @RequestBody ContactMessageRequest contactMessageRequest){
        // bana gelen DTO yu POJO ya çevirmek için mapStruct yapısını kullanacağım
        ContactMessage contactMessage =
                contactMessageMapper.contactMessageRequestToContactMessage(contactMessageRequest);
        contactMessageService.saveMessage(contactMessage);

        SfResponse response = new SfResponse("ContactMessage successfully created", true);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    //!!! getaLL ContactMessages
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContactMessageDTO>> getAllContactMessage() {
        List<ContactMessage> contactMessageList =  contactMessageService.getAll();
        //mapStruct ( POJOs -> DTOs )
        List<ContactMessageDTO> contactMessageDTOList = contactMessageMapper.map(contactMessageList); // bizim yazdığımız map metodu
        return ResponseEntity.ok(contactMessageDTOList); //   return new ResponseEntity<>(contactMessageDTOList, HttpStatus.OK);

    }

    // !!! // pageable
    @GetMapping("/pages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ContactMessageDTO>> getAllContactMessageWithPage(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") String prop, // neye göre sıralanacagını belirtiyoruz
            @RequestParam(value="direction",
                    required = false,
                    defaultValue = "DESC") Sort.Direction direction) {Pageable pageable = PageRequest.of(page, size, Sort.by(direction, prop));
        Page<ContactMessage> contactMessagePage = contactMessageService.getAll(pageable);
        Page<ContactMessageDTO> pageDTO = getPageDTO(contactMessagePage);

        return ResponseEntity.ok(pageDTO);
    }


    // !!! spesifik olarak bir ContactMessage PathVariable ile alalım
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContactMessageDTO> getMessageWithPath(@PathVariable("id") Long id) {

        ContactMessage contactMessage = contactMessageService.getContactMessage(id);

        ContactMessageDTO contactMessageDTO = contactMessageMapper.contactMessageToDTO(contactMessage);

        return ResponseEntity.ok(contactMessageDTO);

    }
    // !!! spesifik olarak bir ContactMessage RequestParam ile alalım
    @GetMapping("/request")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContactMessageDTO> getMessageWithRequestParam(@RequestParam("id") Long id) {

        ContactMessage contactMessage = contactMessageService.getContactMessage(id);

        ContactMessageDTO contactMessageDTO = contactMessageMapper.contactMessageToDTO(contactMessage);

        return ResponseEntity.ok(contactMessageDTO);

    }
    //!!! Delete işlemi
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SfResponse> deleteContactMessage(@PathVariable Long id){
        contactMessageService.deleteContactMessage(id);

        SfResponse sfResponse = new SfResponse(ResponseMessage.CONTACTMESSAGE_DELETE_RESPONSE,true);

        return ResponseEntity.ok(sfResponse);
    }

    // Update
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SfResponse> updateContactMessage( @PathVariable Long id,
                                                            @Valid @RequestBody ContactMessageRequest contactMessageRequest) {

        ContactMessage contactMessage = contactMessageMapper.contactMessageRequestToContactMessage(contactMessageRequest);

        contactMessageService.updateContactMessage(id,contactMessage);

        SfResponse sfResponse = new SfResponse(ResponseMessage.CONTACTMESSAGE_UPDATE_RESPONSE,true);

        return ResponseEntity.ok(sfResponse);
    }



    //!!! getPageDTO
    private Page<ContactMessageDTO> getPageDTO(Page<ContactMessage> contactMessagePage){

        return contactMessagePage.map(  // map methodu Page yapısından geliyor
                contactMessage -> contactMessageMapper.contactMessageToDTO(contactMessage));

    }


}
