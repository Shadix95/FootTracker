package com.foottracker.foottracker.exception;

import com.foottracker.foottracker.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.OffsetDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleFootballDataError(HttpClientErrorException e, HttpServletRequest request) {
        int apiStatus = e.getStatusCode().value();
        log.warn("Erreur {} renvoyee par football-data.org", apiStatus);

        return switch (apiStatus) {
            case 403 -> buildResponse(HttpStatus.FORBIDDEN,
                    "Cle API invalide ou competition non incluse dans l'offre gratuite", request);
            case 404 -> buildResponse(HttpStatus.NOT_FOUND,
                    "Ressource inconnue chez football-data.org (code de competition errone ?)", request);
            case 429 -> buildResponse(HttpStatus.TOO_MANY_REQUESTS,
                    "Quota de 10 requetes par minute depasse, reessayez dans une minute", request);
            default -> buildResponse(HttpStatus.BAD_GATEWAY,
                    "Erreur inattendue de football-data.org (code " + apiStatus + ")", request);
        };
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleApiUnreachable(ResourceAccessException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "football-data.org est injoignable", request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.valueOf(e.getStatusCode().value()), e.getReason(), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleBadParameter(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST,
                "Valeur invalide pour le parametre '" + e.getName() + "' : " + e.getValue(), request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Parametre obligatoire manquant : " + e.getParameterName(), request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleUnknownUrl(NoResourceFoundException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Cette URL n'existe pas", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleWrongMethod(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED,
                "Methode " + e.getMethod() + " non autorisee sur cette URL", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherErrors(Exception e, HttpServletRequest request) {
        log.error("Erreur inattendue", e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du serveur", request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(status).body(body);
    }
}
