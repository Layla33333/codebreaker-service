package edu.cnm.deepdive.codebreaker.service;

import edu.cnm.deepdive.codebreaker.model.dao.GameRepository;
import edu.cnm.deepdive.codebreaker.model.dao.GuessRepository;
import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.model.entity.Guess;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {

  private final GameRepository gameRepository;
  private final GuessRepository guessRepository;
  private final Random rng;

  @Autowired
  public GameService(GameRepository gameRepository,
      GuessRepository guessRepository, Random rng) {
    this.gameRepository = gameRepository;
    this.guessRepository = guessRepository;
    this.rng = rng;
  }

  public Optional<Game> get(UUID id) {
    return gameRepository.findById(id);
  }

  public Optional<Game> get(UUID key, User user) {
    return gameRepository.findByExternalKeyAndUser(key, user);
  }

  public void delete(UUID id) {
    gameRepository.deleteById(id);
  }

  public void delete(UUID key, User user) {
    gameRepository
        .findByExternalKeyAndUser(key, user)
        .ifPresent(gameRepository::delete);
  }

  public Game startGame(Game game, User user) {
    int[] codePoints = preprocess(game.getPool());
    String code = generateCode(codePoints, game.getLength());
    game.setUser(user);
    game.setPool(new String(codePoints, 0, codePoints.length));
    game.setText(code);
    game.setPoolSize(codePoints.length);
    return gameRepository.save(game);
  }

  public Guess processGuess(UUID gameKey, Guess guess, User user)
      throws IllegalStateException, IllegalArgumentException, NoSuchElementException {
    return gameRepository
        .findByExternalKeyAndUser(gameKey, user)
        .map((game) -> {
          int[] guessCodePoints = preprocessGuess(guess, game);
          int[] codeCodePoints = getCodePoints(game.getText());
          computeMatches(guess, guessCodePoints, codeCodePoints);
          guess.setGame(game);
          return guessRepository.save(guess);
        })
        .orElseThrow();
  }

  public Optional getGuess(UUID gameKey, UUID guessKey, User user) {
    return gameRepository
        .findByExternalKeyAndUser(gameKey, user)
        .flatMap((game) -> guessRepository.findByGameAndExternalKey(game, guessKey));
  }

  private void computeMatches(Guess guess, int[] guessCodePoints, int[] codeCodepoints) {
    int exactMatches = 0;
    Map<Integer, Integer> guessCodePointCounts = new HashMap<>();
    Map<Integer, Integer> codeCodepointCounts = new HashMap<>();
    for (int i = 0; i < guessCodePoints.length; i++) {
      if (guessCodePoints[i] == codeCodepoints[i]) {
        exactMatches++;
      } else {
        guessCodePointCounts.put(guessCodePoints[i],
            1 + guessCodePointCounts.getOrDefault(guessCodePoints[i], 0));
        guessCodePointCounts.put(codeCodepoints[i],
            1 + guessCodePointCounts.getOrDefault(codeCodepoints[i], 0));

      }
    }

    //TODO Compute exactMatches
    guess.setExactMatches(exactMatches);
    int nearMatches = guessCodePointCounts
        .entrySet()
        .stream()
        .mapToInt((entry) ->
            Math.min(entry.getValue(), codeCodepointCounts.getOrDefault(entry.getKey(), 0)))
        .sum();

    //TODO Compute nearMatches.
    guess.setNearMatches(nearMatches);
  }

  private int[] preprocessGuess(Guess guess, Game game) {
    if (game.isSolved()) {
      throw new IllegalArgumentException("Game is already solved");
    }
    int[] guessCodePoints = getCodePoints(guess.getText());
    if (guessCodePoints.length != game.getLength()) {
      throw new IllegalArgumentException
          (String.format("Guess must have the same length (%d) as the secret code.",
              game.getLength()));
    }
    Set<Integer> poolCodePoints = game
        .getPool()
        .codePoints()
        .boxed()
        .collect(Collectors.toSet());
    if (IntStream
        .of(guessCodePoints)
        .anyMatch((codepoint) -> !poolCodePoints.contains(codepoint))) {
      throw new IllegalArgumentException(String.format(
          "Guess may only contain the characters in the pool \"%s\"", game.getPool()));

    }
    return guessCodePoints;
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

  private int[] getCodePoints(String source) {
    return source
        .codePoints()
        .toArray();
  }

}