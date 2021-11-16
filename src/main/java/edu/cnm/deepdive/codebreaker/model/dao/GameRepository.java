package edu.cnm.deepdive.codebreaker.model.dao;

import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GameRepository extends JpaRepository<Game, UUID> {

  Optional<Game> findByExternalKey(UUID key);

  Optional<Game> findByExternalKeyAndUser(UUID key, User user);

  @Query("SELECT DISTINCT ga FROM Guess AS gu JOIN gu.game AS ga WHERE gu.exactMatches = ga.length AND ga.user = :user ORDER BY ga.created DESC")
  Iterable<Game> getAllSolved(User user);

}
