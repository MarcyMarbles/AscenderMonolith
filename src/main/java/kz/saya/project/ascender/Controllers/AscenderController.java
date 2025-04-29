package kz.saya.project.ascender.Controllers;

import kz.saya.project.ascender.Listeners.EndpointsListener;
import kz.saya.sbasesecurity.Service.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/info")
public class AscenderController extends BaseController {
    private final EndpointsListener endpointsListener;

    @Autowired
    public AscenderController(EndpointsListener endpointsListener, UserSecurityService userSecurityService) {
        super(userSecurityService);
        this.endpointsListener = endpointsListener;
    }

    @GetMapping("/endpoints")
    public ResponseEntity<List<String>> getAllEndpoints() {
        return ResponseEntity.ok(endpointsListener.getEndpoints());
    }
}
