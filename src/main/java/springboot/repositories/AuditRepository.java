package springboot.repositories;

import mvc.model.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AuditRepository extends JpaRepository<Audit, Long> {
    @Query(
            "SELECT a FROM Audit a WHERE a.person_id = ?1"
    )
    List<Audit> findAuditsById(int person_id);
}