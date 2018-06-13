package exceptions.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

public class ExceptionMapper {
    private HttpStatus status;
    private String message;
    private String systemError;

    public ExceptionMapper(Exception e, HttpStatus status) {
        this.message = e.getMessage();
        this.status = status;
        this.systemError = status.value() + ": " + status.getReasonPhrase() + ". Request has failed. Error message: " + e.getMessage();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSystemError() {
        return systemError;
    }

    public void setSystemError(String systemError) {
        this.systemError = systemError;
    }
}
