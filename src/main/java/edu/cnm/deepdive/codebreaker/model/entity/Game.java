package edu.cnm.deepdive.codebreaker.model.entity;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;

@Entity
public class Game {

  private  UUID id;

  private  UUID externalKey;

  private User user;

  private Date created;

  private String pool;

  private int length;

  private String text;




}
