package core.Repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import core.domain.SdaMainMas;
import core.domain.SdaMainMasId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;

public interface SdaMainMasRepository extends JpaRepository<SdaMainMas, SdaMainMasId> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="10000")})
    Optional<SdaMainMas> findById(SdaMainMasId id);
}
