package gerard.meli.backend_portfolio.profile;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    /**
     * Il n'y a qu'un seul profil : on charge la liste (traductions comprises) puis on prend le premier.
     * On évite "findFirst" + fetch de collection, qui oblige Hibernate à paginer en mémoire.
     */
    @EntityGraph(attributePaths = "translations")
    List<Profile> findAllBy();

    default Optional<Profile> findFirstBy() {
        return findAllBy().stream().findFirst();
    }
}