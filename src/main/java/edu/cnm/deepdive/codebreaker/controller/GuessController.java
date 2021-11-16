package edu.cnm.deepdive.codebreaker.controller;


import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.model.entity.Guess;
import edu.cnm.deepdive.codebreaker.service.GameService;
import edu.cnm.deepdive.codebreaker.service.UserService;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games/{gameKey}/guesses")
public class GuessController {

  private final GameService gameService;
  private final UserService userService;

  public GuessController(GameService gameService,
      UserService userService) {
    this.gameService = gameService;
    this.userService = userService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public Guess post(@PathVariable UUID gameKey, @RequestBody Guess guess) {
    return gameService.processGuess(gameKey, guess, userService.getCurrentUser());
  }


  @GetMapping(value = "/{guessKey}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Guess get(@PathVariable UUID gameKey, @PathVariable UUID guessKey) {
    return (Guess) gameService
        .getGuess(gameKey, guessKey, userService.getCurrentUser())
        .orElseThrow();
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<Guess> get(@PathVariable UUID gameKey) {
    return gameService
        .get(gameKey, userService.getCurrentUser())
        .map(Game::getGuesses)
        .orElseThrow();
  }
}
