package kz.saya.project.ascender.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import kz.saya.sbasecore.Entity.User;
import kz.saya.sbasesecurity.Service.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Abstract base controller that provides common functionality for all controllers.
 * Contains UserSecurityService to extract the current user from the request.
 */
public abstract class BaseController {

    protected final UserSecurityService userSecurityService;

    public BaseController(UserSecurityService userSecurityService) {
        this.userSecurityService = userSecurityService;
    }

    /**
     * Extracts the user from the authorization token in the request.
     *
     * @param request The HTTP request containing the authorization header
     * @return The User object if found, null otherwise
     */
    protected User extractUserFromToken(HttpServletRequest request) {
        return userSecurityService.extractUserFromToken(request.getHeader("Authorization"));
    }

    /**
     * Creates an unauthorized response with a message.
     *
     * @param message The error message
     * @return ResponseEntity with UNAUTHORIZED status and the error message
     */
    protected ResponseEntity<?> unauthorized(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }

    /**
     * Creates a bad request response with a message.
     *
     * @param message The error message
     * @return ResponseEntity with BAD_REQUEST status and the error message
     */
    protected ResponseEntity<?> badRequest(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    /**
     * Creates a not found response with a message.
     *
     * @param message The error message
     * @return ResponseEntity with NOT_FOUND status and the error message
     */
    protected ResponseEntity<?> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }
}
