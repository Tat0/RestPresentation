package exceptions.advice;

import org.springframework.http.HttpStatus;

public class ExceptionMapper {
    private HttpStatus status;
    private String message;
    private String systemError;

    public ExceptionMapper(Exception e, HttpStatus status) {
        this.message = e.getMessage();
        this.status = status;
        this.systemError = new StringBuilder()
                .append(status.value())
                .append(": ")
                .append(status.getReasonPhrase())
                .append(". Request has failed. Error message: ")
                .append(e.getMessage()).toString();
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
