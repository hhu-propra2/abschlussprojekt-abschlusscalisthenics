package propra2.leihOrDie.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseBuilder {
    public ResponseEntity createBadRequestResponse(String errorMessage) {
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity createSuccessResponse(String successMessage) {
        return new ResponseEntity<>(successMessage, HttpStatus.OK);
    }

    public ResponseEntity createUnauthorizedResponse() {
        String errorMessage = "Du bist nicht authorisiert diese Aktion auszuführen.";
        return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity createProPayErrorResponse() {
        String errorMessage = "Fehler bei der Verarbeitung der Zahlung.";
        return new ResponseEntity<>(errorMessage, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
