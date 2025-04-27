package kz.saya.project.ascender.Controllers;

import kz.saya.project.ascender.Listeners.EndpointsListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/info")
public class AscenderController {
    private final EndpointsListener endpointsListener;

    public AscenderController(EndpointsListener endpointsListener) {
        this.endpointsListener = endpointsListener;
    }

    @GetMapping("/endpoints")
    public ResponseEntity<List<String>> getAllEndpoints() {
        return ResponseEntity.ok(endpointsListener.getEndpoints());
    }
}
