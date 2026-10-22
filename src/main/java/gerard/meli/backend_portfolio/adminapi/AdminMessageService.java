package gerard.meli.backend_portfolio.adminapi;

import gerard.meli.backend_portfolio.adminapi.AdminDtos.*;
import gerard.meli.backend_portfolio.contact.ContactMessage;
import gerard.meli.backend_portfolio.contact.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminMessageService {

    private final ContactMessageRepository messages;
    private final AdminSupport support;

    @Transactional(readOnly = true)
    public PageResponse<MessageResponse> list(int page, int size, boolean unreadOnly) {
        PageRequest pageRequest = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
        Page<ContactMessage> result = unreadOnly
                ? messages.findByReadFalseOrderByCreatedAtDesc(pageRequest)
                : messages.findAllByOrderByCreatedAtDesc(pageRequest);
        return new PageResponse<>(
                result.getContent().stream().map(this::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public UnreadCount unreadCount() {
        return new UnreadCount(messages.countByReadFalse());
    }

    public MessageResponse setRead(UUID id, boolean read) {
        ContactMessage m = messages.findById(id).orElseThrow(() -> support.notFound("Message"));
        m.setRead(read);
        return toResponse(m);
    }

    public void delete(UUID id) {
        messages.delete(messages.findById(id).orElseThrow(() -> support.notFound("Message")));
    }

    private MessageResponse toResponse(ContactMessage m) {
        return new MessageResponse(m.getId(), m.getName(), m.getEmail(), m.getSubject(), m.getMessage(),
                m.getLocale(), m.getMarket(), m.isRead(), m.getCreatedAt());
    }
}