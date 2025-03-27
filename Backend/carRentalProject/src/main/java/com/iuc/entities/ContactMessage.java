package com.iuc.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)//cant set ID because it is done authmatically
    private Long id; // non-primitive-because of null

    @Size(min = 1,max = 50,message = "Your name '${validatedValue}' must be between {min} and {max} chars long ")
    @NotNull(message = "Please provide your name")
    @Column(length = 50, nullable = false)
    private String name;

    @Size(min = 5,max = 50,message = "Your subject '${validatedValue}' must be between {min} and {max} chars long ")
    @NotNull(message = "Please provide your subject")
    @Column(length = 50, nullable = false)
    private String subject;

    @Size(min = 2,max = 200,message = "Your body '${validatedValue}' must be between {min} and {max} chars long ")
    @NotNull(message = "Please provide your body")
    @Column(length = 200, nullable = false)
    private String body;

    @Email(message = "privide valid email")
    @Column(length = 200, nullable = false)
    private String email;
}
