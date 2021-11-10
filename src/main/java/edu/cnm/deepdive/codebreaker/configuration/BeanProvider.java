package edu.cnm.deepdive.codebreaker.configuration;

import java.util.Random;
import org.apache.commons.rng.simple.JDKRandomBridge;
import org.apache.commons.rng.simple.RandomSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanProvider {

  @Bean
  public Random getRandom() {
    return new JDKRandomBridge(RandomSource.XO_RO_SHI_RO_128_PP, null);
  }
}
