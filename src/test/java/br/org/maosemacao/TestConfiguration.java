package br.org.maosemacao;
import org.springframework.context.annotation.*;
import java.time.*;
@org.springframework.boot.test.context.TestConfiguration
public class TestConfiguration {
    @Bean @Primary public Clock fixedClock() { return Clock.fixed(Instant.parse("2030-01-01T12:00:00Z"),ZoneOffset.UTC); }
}
