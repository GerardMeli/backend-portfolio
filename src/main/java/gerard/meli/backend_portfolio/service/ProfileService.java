package gerard.meli.backend_portfolio.service;

import gerard.meli.backend_portfolio.domain.*;
import gerard.meli.backend_portfolio.dto.*;
import gerard.meli.backend_portfolio.exception.*;
import gerard.meli.backend_portfolio.mapper.*;
import gerard.meli.backend_portfolio.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileService {

    private final ProfileRepository repository;
    private final ProfileMapper mapper;

    public ProfileService(ProfileRepository repository, ProfileMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public ProfileDto get() {
        return mapper.toDto(repository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new NotFoundException("Le profil n'est pas encore configuré.")));
    }

    /** Crée le profil s'il n'existe pas encore, sinon le met à jour (un seul profil). */
    public ProfileDto save(ProfileDto dto) {
        Profile profile = repository.findFirstByOrderByIdAsc().orElseGet(Profile::new);
        mapper.update(dto, profile);
        return mapper.toDto(repository.save(profile));
    }
}
