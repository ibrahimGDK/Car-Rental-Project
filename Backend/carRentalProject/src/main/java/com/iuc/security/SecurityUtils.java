package com.iuc.security;



import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.core.userdetails.*;

import java.util.*;

public class SecurityUtils {

    // !!! Controller veya Servis Katmanında anlık olarak login olan kullanıcıya ulaşmak için bu classı yazdık

    public static Optional<String> getCurrentUserLogin() {//NullPointerExcptn olmaması için Optinal yaptık
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication =securityContext.getAuthentication();//Authentice edilen kullanıcı geldi

        return Optional.ofNullable(extractPrincipal(authentication));
        //Optional.ofNullable--> üst satırda null gelirse Optional.ofNullable sayesinde null gitmez, ön tarafa içi boş class gönderir
    }

    //Authentice edilen kullanıcıdan email getiren metot
    private static String extractPrincipal(Authentication authentication){
        //authentication objesi UserDteails türünde mi yoksa String (email) türünde mi kontrol edelim. Biz AuthTokenFilter classından context'e  UserDetails türünde göndermiştik ancak yine de kontrol yapılmalı.
        if(authentication==null) {
            return null;
        } else if(authentication.getPrincipal() instanceof UserDetails) {//instanceof : UserDetails türünde mi_?  //authentication.getPrincipal()-->authenticat olmuş kullanıcının bilgilerini getirir
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        } else if(authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }

        return null;
    }
}
