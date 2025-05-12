package com.iuc.mapper;
import com.iuc.dto.ContactMessageDTO;
import com.iuc.dto.request.ContactMessageRequest;
import com.iuc.entities.ContactMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")// herhangi bir sınıfı enjekte edip kullanabilirim
public interface ContactMessageMapper {

    // ContactMessage--> ContactMessageDTO
    ContactMessageDTO contactMessageToDTO(ContactMessage contactMessage);

    //contactMessageRequest-->ContactMessage
    @Mapping(target="id", ignore = true)//maplerken fieldlarda tutarsızlık varsa ---DTO id yok-- id yi ignore etttik
    ContactMessage contactMessageRequestToContactMessage(ContactMessageRequest contactMessageRequest);

    //List<ContactMessage>  -- > List<ContactMessageDTO>
    List<ContactMessageDTO> map(List<ContactMessage> contactMessageList); // getAllContactMessage()
}
