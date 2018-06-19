package exceptions.advice;

import exceptions.RestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({NoSuchElementException.class})
    protected ResponseEntity<Object> handleNoSuchElementInternalError(NoSuchElementException e) {
        RestException response = new RestException(HttpStatus.NOT_FOUND, e.getMessage());
        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }

    @ExceptionHandler({RestException.class})
    protected ResponseEntity<Object> handleException(RestException e) {
        RestException response = new RestException(e);
        return new ResponseEntity<>(response, new HttpHeaders(), response.getStatus());
    }
}
