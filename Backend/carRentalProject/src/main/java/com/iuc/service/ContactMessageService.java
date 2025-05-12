package com.iuc.service;

import com.iuc.entities.ContactMessage;
import com.iuc.exception.ResourceNotFoundException;
import com.iuc.exception.message.ErrorMessage;
import com.iuc.repository.ContactMessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactMessageService {
    private final ContactMessageRepository contactMessageRepository;

    public ContactMessageService(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    public void saveMessage(ContactMessage contactMessage) {
        contactMessageRepository.save(contactMessage);
    }

    public List<ContactMessage> getAll() {
        return contactMessageRepository.findAll();
    }
    public Page<ContactMessage> getAll(Pageable pageable){
        return contactMessageRepository.findAll(pageable);
    }

    public ContactMessage getContactMessage(Long id) {
        return contactMessageRepository.findById(id).orElseThrow(()->
                //new ResourceNotFoundException("ContactMessage isn't found with id : "+id));
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_EXCEPTION, id))// Error Message clasında ki  id %s ibaresini burada id diye belirttik
        );
    }

    public void deleteContactMessage(Long id) {
        ContactMessage contactMessage = getContactMessage(id);
        contactMessageRepository.delete(contactMessage);
    }

    public void updateContactMessage(Long id, ContactMessage contactMessage) {
        ContactMessage foundContactMessage = getContactMessage(id);

        foundContactMessage.setName(contactMessage.getName());
        foundContactMessage.setBody(contactMessage.getBody());
        foundContactMessage.setEmail(contactMessage.getEmail());
        foundContactMessage.setSubject(contactMessage.getSubject());

        contactMessageRepository.save(foundContactMessage);
    }
}
