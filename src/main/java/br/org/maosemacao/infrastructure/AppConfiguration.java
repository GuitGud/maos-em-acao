package br.org.maosemacao.infrastructure;
import br.org.maosemacao.application.*;
import org.springframework.context.annotation.Bean;
import java.time.Clock;
@org.springframework.context.annotation.Configuration
public class AppConfiguration {
    @Bean Clock clock() { return Clock.systemUTC(); }
    @Bean Events events(Store store,Clock clock) { return new Events(new NotificationObserver(store,clock)); }
    @Bean Reports reports(Store store) { return new Reports.AccessProxy(new Reports.Summary(store)); }
    @Bean Actions actions(Store store,Events events,Clock clock,Reports reports) { return new Actions(store,events,clock,reports); }
}
