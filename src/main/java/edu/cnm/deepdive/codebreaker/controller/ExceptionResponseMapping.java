package edu.cnm.deepdive.codebreaker.controller;

import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionResponseMapping {

  @ExceptionHandler(NoSuchElementException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public void notFound() {

  }
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid request content")
  public void badRequest() {

  }
  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Game already solved")
  public void gameAlreadySolved(){

  }
  }

