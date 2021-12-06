package edu.cnm.deepdive.codebreaker.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;

public interface ScoreSummary {

  @JsonIgnore
  UUID getUserId();

  String getDisplayName();

  @JsonIgnore
  UUID getExternalKey();

  double getAverageGuessCount();

  double getAverageTime();

}
