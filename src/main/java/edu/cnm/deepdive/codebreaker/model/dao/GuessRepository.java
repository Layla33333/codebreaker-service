package edu.cnm.deepdive.codebreaker.model.dao;

import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.model.entity.Guess;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

public interface GuessRepository extends JpaRepositoryImplementation<Guess, UUID> {

  Optional<Guess> findByGameAndExternalKey(Game game, UUID externalKey);
}
