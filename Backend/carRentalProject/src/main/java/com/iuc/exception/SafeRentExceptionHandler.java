package com.iuc.exception;

import com.iuc.exception.message.ApiResponseError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.*;
import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.*;
import org.springframework.web.servlet.mvc.method.annotation.*;

import java.util.*;
import java.util.stream.*;

@ControllerAdvice // merkezi exception classını belli  etmek için
public class SafeRentExceptionHandler extends ResponseEntityExceptionHandler {

    // AMACIM : custom bir exception sistemini kurmak, gelebilecek exceptionları override ederek, istediğim yapıda cevap verilmesini sağlamak

    /*
        porojenin neresinden exception gelirse gelsin buradan handle edilecek
        Gelebilecek tüm exceptionların burada setlenemesi gerekli
        -Bunu yapmazsak tahmin edemeyeceğimiz exceptionlar client tarafına fırlar ve anlamsız olur.

        *exception nereden gelirse hangi exception olursa olsun bu classda handle edilecek ve ApıResponseError
         classındaki formata göre düznlenerek gönderilecek. ApıResponseError classında ki tüm fieldları istediğim gibi düzenleyebileceğim.
         *Yüzlerce exception var ancak fırlama ihtimali yüksek olan 6-7 exceptionı overrride edeceğiz. Bunların haricinde fırlama ihtimali
         olanları da paren exception ile handle edeceğiz
        *Central exception handler sayesinde loglamalarıda tek yerde yapabiliyorum
 */
    Logger logger = LoggerFactory.getLogger(SafeRentExceptionHandler.class);//Factory Design Pattern kullanılmış
    // loglanmasını istediğimiz class ismini parametre olarak yazarız
    private ResponseEntity<Object> buildResponseEntity(ApiResponseError error) {
        logger.error(error.getMessage());//bu satırla tüm exceptionları loglamış oluyoruz. buraya yazmasaydık aşağıdaki her metoda ayrı ayrı yazmak zorunda kalırdık
        return new ResponseEntity<>(error,error.getStatus());
    }
    // protected zorunlu değil. bunun sayesinde bu clasa inherit ettiğimiz diğer classlardan da ulaşılabilsin diye. direk public yazmıyoruz ki exception classının dışından ulaşılmasın diye
    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleResourceNotFoundException( // dönen herşeyi karşılayabilmesi için object yazdık, metot adını biz verdik
                                                                      ResourceNotFoundException ex, //exceptionun kendisine ulaşmak için bu parametreyi yazdık // exception fırlayınca bu metoda gelsinki burada handle edeyim
                                                                      WebRequest request  // exception fırlarken hangi request üzerinden geldiğini öğrenmek için
    ) {

        ApiResponseError error = new ApiResponseError(HttpStatus.NOT_FOUND, // status kodunu biz setledik. exceptionun türüne uygun olanı
                ex.getMessage(),// ex= exceptionun kendisiydi yukarıda. onun mesajını almış olduk
                request.getDescription(false) // gereksiz bilgiler gelmesim
        );

        return buildResponseEntity(error); //her defasında uzun uzun yazmamak için bu şekilde yazdık. yukarıda tek seferlik bir metot oluşturduk

    }

    @ExceptionHandler(ConflictException.class)
    protected ResponseEntity<Object> handleConflictException(
            ConflictException ex, WebRequest request) {
        ApiResponseError error = new ApiResponseError(HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getDescription(false));

        return buildResponseEntity(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        ApiResponseError error = new ApiResponseError(HttpStatus.FORBIDDEN,
                ex.getMessage(),
                request.getDescription(false));

        return buildResponseEntity(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<Object> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        ApiResponseError error = new ApiResponseError(HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getDescription(false));

        return buildResponseEntity(error);
    }

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<Object> handleBadRequestException(
            BadRequestException ex, WebRequest request) {
        ApiResponseError error = new ApiResponseError(HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getDescription(false));

        return buildResponseEntity(error);
    }


    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        //Bir metodun argümanları geçerli olmadığında (validasyon hataları) çağrılır.

        // birden fazla argumanın notValid olması ihtimaline karşı List yapısı kullanıyoruz
        List<String> errors = ex.getBindingResult().//fırlayan exceptionların listesini verir
                getFieldErrors().//her bir field ın errorunu verir. kaç argümanda validasyona takıldıysa herbirini verir. mesela client ın girdiği name ve telNo NotValid. Bu ikisini aşağıda akışa girecek. Bunlardan herbir i aslında farklı errorlar.
                stream().//akışa çevirdik
                map(e->e.getDefaultMessage()). // akıştan her errorun default mesajlarına akışı çevirir
                collect(Collectors.toList()); // bunları liste atar

        ApiResponseError error = new ApiResponseError(HttpStatus.BAD_REQUEST,
                errors.get(0).toString(),// index 0 çünkü birden çok notValid field varsa onları teker teker düzelttiriyoruz. ilki düzeldikten sonra ikinci hatanın indeksi bu kez 0 olacak
                request.getDescription(false));
        return buildResponseEntity(error);
    }

    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        //Tür uyumsuzluğu hatası oluştuğunda çağrılır.
        ApiResponseError error = new ApiResponseError(HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getDescription(false))  ;
        return buildResponseEntity(error);
    }

    protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        //Dönüşüm yapılamadığında çağrılır.
        ApiResponseError error = new ApiResponseError(HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                request.getDescription(false))  ;
        return buildResponseEntity(error);
    }

    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        //HTTP mesajı okunamaz olduğunda çağrılır.
        ApiResponseError error = new ApiResponseError(HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getDescription(false))  ;
        return buildResponseEntity(error);
    }



    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
        //RuntimeException fırlatıldığında bu metodun çağrılmasını sağlar.

        ApiResponseError error = new ApiResponseError(HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                request.getDescription(false));
        return buildResponseEntity(error);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleGeneralException( Exception ex, WebRequest request) {
        //Genel Exception türünde bir hata oluştuğunda bu metodun çağrılmasını sağlar.

        ApiResponseError error = new ApiResponseError(HttpStatus.INTERNAL_SERVER_ERROR, // parent exception olduğu için genel bir status code seçtik
                ex.getMessage(),
                request.getDescription(false));

        return buildResponseEntity(error);
    }
}
