package exceptions.advice;

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
    protected ResponseEntity<Object> handleNoSuchElementInternalError(Exception e) {
        ExceptionMapper exceptionMapper = new ExceptionMapper(e, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(exceptionMapper, new HttpHeaders(), exceptionMapper.getStatus());
    }
}
