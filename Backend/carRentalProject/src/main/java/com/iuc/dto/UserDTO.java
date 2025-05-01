package com.iuc.dto;

import com.iuc.entities.*;
import lombok.*;
import java.util.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    // !!! bu class repodan gelen pojo yu DTO ya çevirmek için kullanılacak

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private String zipCode;
    private Boolean builtIn ;

    private Set<String> roles;///Role Type da customer ve Administrator string yapıda olduğu için bu satırın dönen değerini de String yaptık. Ayrıca ön tarafa DB de olan isimlerini göndermek istemedik

    //lombok gelen @Setter roles'ü yapamaz. Onu ezmek için aşağıdakini yazdık

    /*
       Aşağıdaki metod, Role tipinde bir Set alır ve roles alanına uygun String değerlerini dönüştürür.
       Role nesnesinin getType() metodundan dönen RoleType objesinin getName() metoduyla bu
       RoleType'ın adını alır ve roles Set'ine ekler.
        */
    public void setRoles(Set<Role> roles) {
        Set<String> roleStr = new HashSet<>();
        roles.forEach(r->{
            roleStr.add(r.getType().getName()); // Customer , Administrator
        });
        this.roles=roleStr;
    }


}
