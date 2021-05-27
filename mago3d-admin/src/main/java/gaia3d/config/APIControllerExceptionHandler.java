package gaia3d.config;

import gaia3d.support.LogMessageSupport;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

/**
 * APIController Exception 처리
 *
 * @author Cheon JeongDae
 */
@AllArgsConstructor
@RestControllerAdvice(basePackages = {"gaia3d.controller.api"})
public class APIControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    /**
     * DataAccessException 처리
     * @param request request
     * @param e DataAccessException
     * @return 에러결과가 담긴 ResponseEntity
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Object> error(WebRequest request, DataAccessException e) {
        //String errorCode = "db.exception";
        String errorCode = getLocaleErrorCode(request, "db.exception");
        String message = getErrorMessage(e.getCause(), e.getMessage(), e.toString());
        LogMessageSupport.printMessage(e, "@@ DataAccessException. message = {}", message);
        return handleExceptionInternal(e, errorCode, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * RuntimeException 처리
     * @param request request
     * @param e RuntimeException
     * @return 에러결과가 담긴 ResponseEntity
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> error(WebRequest request, RuntimeException e) {
        //String errorCode = "runtime.exception";
        String errorCode = getLocaleErrorCode(request, "runtime.exception");
        String message = getErrorMessage(e.getCause(), e.getMessage(), e.toString());
        LogMessageSupport.printMessage(e, "@@ RuntimeException. message = {}", message);
        return handleExceptionInternal(e, errorCode, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * IOException 처리
     * @param request request
     * @param e IOException
     * @return 에러결과가 담긴 ResponseEntity
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> error(WebRequest request, IOException e) {
        //String errorCode = "io.exception";
        String errorCode = getLocaleErrorCode(request, "io.exception");
        String message = getErrorMessage(e.getCause(), e.getMessage(), e.toString());
        LogMessageSupport.printMessage(e, "@@ IOException. message = {}", message);
        return handleExceptionInternal(e, errorCode, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Exception 처리
     * @param request request
     * @param e Exception
     * @return 에러결과가 담긴 ResponseEntity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> error(WebRequest request, Exception e) {
        //String errorCode = "unknown.exception";
        String errorCode = getLocaleErrorCode(request, "unknown.exception");
        String message = getErrorMessage(e.getCause(), e.getMessage(), e.toString());
        LogMessageSupport.printMessage(e, "@@ Exception. message = {}", message);
        return handleExceptionInternal(e, errorCode, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Locale이 적용된 에러 메세지 가져오기
     * @param request request
     * @param errorCode errorCode
     * @return Locale이 적용된 에러 메세지
     */
    private String getLocaleErrorCode(WebRequest request, String errorCode) {
        return messageSource.getMessage(errorCode, null, request.getLocale());
    }

    /**
     * 에러 메세지 처리
     * @param cause cause
     * @param exceptionMessage exceptionMessage
     * @param exceptionString exceptionString
     * @return 에러 메세지
     */
    private String getErrorMessage(Throwable cause, String exceptionMessage, String exceptionString) {
        String message = cause != null ? cause.getMessage() : exceptionMessage;
        if (message == null) {
            if (cause != null && cause.toString().length() > 10) {
                message = cause.toString();
            } else {
                message = exceptionString;
            }
        }
        return message;
    }

}
