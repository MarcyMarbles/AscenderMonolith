package kz.saya.project.ascender.Controllers;

import kz.saya.sbasecore.Entity.User;
import kz.saya.sbasesecurity.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
public abstract class BaseController {

    protected final AuthService authService;

    protected User currentUser() {
        return authService.getAuthenticatedUser();
    }

    protected ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(401).body("User not authenticated");
    }

    protected ResponseEntity<?> badRequest(String msg) {
        return ResponseEntity.badRequest().body(msg);
    }

    protected ResponseEntity<?> notFound(String msg) {
        return ResponseEntity.status(404).body(msg);
    }
}
