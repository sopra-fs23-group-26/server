package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.GameUndercover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("undercoverRepository")
public interface UndercoverRepository extends JpaRepository<GameUndercover, Long> {
    GameUndercover findById(long id);
}
