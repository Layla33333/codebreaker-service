package edu.cnm.deepdive.codebreaker.service;

import edu.cnm.deepdive.codebreaker.model.dao.GameRepository;
import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {

  private final GameRepository repository;
  private final Random rng;

  @Autowired
  public GameService(GameRepository repository, Random rng) {
    this.repository = repository;
    this.rng = rng;
  }

  public Optional<Game> get(UUID id) {
    return repository.findById(id);
  }

  public Optional<Game> get(UUID key, User user) {
    return repository.findByExternalKeyAndUser(key, user);
  }

  public void delete(UUID id) {
    repository.deleteById(id);
  }

  public void delete(UUID key, User user) {
    repository
        .findByExternalKeyAndUser(key, user)
        .ifPresent(repository::delete);
  }

  public Game startGame (String pool, int length, User user) {
    int[] codePoints = preprocess(pool);
    String code = generateCode(codePoints, length);
    Game game = new Game();
    game.setUser(user);
    game.setPool(new String(codePoints, 0, codePoints.length));
    game.setText(code);
    game.setLength(length);
    game.setPoolSize(codePoints.length);
    return repository.save(game);
  }

  private int[] preprocess(String pool) {
    return pool
        .codePoints()
        .peek((codePoint) -> {

          if (!Character.isDefined(codePoint)) {
            throw new IllegalArgumentException(
                String.format("Undefined character in pool: %d", codePoint));
          } else if (Character.isWhitespace(codePoint)) {
            throw new IllegalArgumentException(
                String.format("Whitespace character in pool: %d, codePoint"));
          }
        })
        .sorted()
        .distinct()
        .toArray();
  }

  private String generateCode(int[] codePoints, int length) {
    int[] code = IntStream
        .generate(() -> codePoints[rng.nextInt(codePoints.length)])
        .limit(length)
        .toArray();
    return new String(code, 0, code.length);
  }

}