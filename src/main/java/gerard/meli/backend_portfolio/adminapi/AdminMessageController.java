package gerard.meli.backend_portfolio.adminapi;

import gerard.meli.backend_portfolio.adminapi.AdminDtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/messages")
@RequiredArgsConstructor
public class AdminMessageController {

    private final AdminMessageService service;

    /** Exemple : /api/admin/messages?page=0&size=20&unread=true */
    @GetMapping
    public PageResponse<MessageResponse> list(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size,
                                              @RequestParam(defaultValue = "false") boolean unread) {
        return service.list(page, size, unread);
    }

    @GetMapping("/unread-count")
    public UnreadCount unreadCount() {
        return service.unreadCount();
    }

    @PatchMapping("/{id}/read")
    public MessageResponse setRead(@PathVariable UUID id, @RequestBody ReadRequest request) {
        return service.setRead(id, request.read());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}