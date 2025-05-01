package com.iuc.exception.message;

import com.fasterxml.jackson.annotation.*;
import org.springframework.http.*;
import java.time.*;

public class ApiResponseError {

    // BU CLASS TAKİ AMACIM : custom error mesajlartının ana soblonunu oluşturmak
    private HttpStatus status;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss") // bu filed ı JSON da değişiklik yapacaksak bu anotasyonu kullanırız
    private LocalDateTime timestamp;                                            // shae.nin STRINGinde  patterne göre değişiklik yap
    // bunun setterini sildik

    private String message;

    private String requestURI ;

    // Constructor
    // hangi filedların kullanılmasını istiyorsak ona göre aşağıdaki contructurlardan birini seçeriz
    private ApiResponseError(){
        timestamp = LocalDateTime.now();
    }/* ApiResponseError'daki tüm fieldların setlenmesini istiyorsak parametresiz contructor'ı PRIVATE yaparız
     */

    public ApiResponseError(HttpStatus status){
        this(); // yukardaki parametresiz private const. çağırılıyor
        this.message="Unexpected Error";
        this.status = status ;

    }

    public ApiResponseError(HttpStatus status, String message, String requestURI) {
        this(status); // yukardaki 1 parametreli, public const. çağrılıyor
        this.message = message;
        this.requestURI = requestURI;
    }



    // GETTER -SETTER


    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }
}
