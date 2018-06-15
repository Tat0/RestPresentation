package exceptions.advice;

import exceptions.RestException;
import org.springframework.http.HttpStatus;

public class ExceptionResponse {
    private HttpStatus status;
    private String message;
    private String error;

    public ExceptionResponse(RestException e) {
        this.status = e.getStatus();
        this.message = e.getMessage();
        this.error = createErrorField(e.getStatus(), e.getMessage());
    }

    public ExceptionResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
        this.error = createErrorField(status, message);
    }

    private String createErrorField(HttpStatus status, String message) {
        return new StringBuilder()
                .append(status.value())
                .append(": ")
                .append(status.getReasonPhrase())
                .append(". System error: ")
                .append(message).toString();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }
}
