package br.org.maosemacao.infrastructure;
import br.org.maosemacao.application.*;
import br.org.maosemacao.domain.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
@Component
public class Bootstrap implements CommandLineRunner {
    private final Store store; private final Actions actions; private final PasswordEncoder passwords;
    @Value("${app.admin.email}") String email;
    @Value("${app.admin.password}") String password;
    @Value("${app.demo}") boolean demo;
    public Bootstrap(Store store,Actions actions,PasswordEncoder passwords) { this.store=store;this.actions=actions;this.passwords=passwords; }
    @Transactional public void run(String... args) {
        if(!store.all(Volunteer.class).isEmpty()) return;
        if(password.isBlank()) {
            if(!demo) { System.out.println("Defina ADMIN_PASSWORD (mínimo 12 caracteres) para criar a conta gestora, ou DEMO_MODE=true para demonstração local."); return; }
            password="DemoGestor2026!";
        }
        Rule.require(password.length()>=12,"ADMIN_PASSWORD deve ter ao menos 12 caracteres");
        Volunteer admin=store.save(new Volunteer(null,"Gestão Mãos em Ação",email.toLowerCase(),passwords.encode(password),true,"ADMIN"));
        if(!demo) return;
        Volunteer vol=store.save(new Volunteer(null,"Ana Voluntária","ana@example.test",passwords.encode("DemoVoluntario2026!"),true,"VOLUNTEER"));
        Item rice=actions.createItem(admin,"Arroz","kg"); Item beans=actions.createItem(admin,"Feijão","kg"); actions.createItem(admin,"Kit de higiene","kit");
        Campaign c=actions.createCampaign(admin,"Alimento na mesa","Uma rede de cuidado para levar alimentos a quem mais precisa. Participe da coleta, organização e distribuição.");
        actions.need(admin,c.id(),rice.id(),300); actions.need(admin,c.id(),beans.id(),200); actions.publishCampaign(admin,c.id());
        actions.movement(admin,c.id(),rice.id(),120,"DOACAO","Arrecadação comunitária de demonstração");
        actions.movement(admin,c.id(),beans.id(),80,"DOACAO","Parceiros da comunidade (demonstração)");
        LocalDateTime start=LocalDateTime.now(Clock.systemUTC()).plusDays(2).withHour(12).withMinute(0).withSecond(0).withNano(0);
        Shift s=actions.createShift(admin,c.id(),"Organização e montagem de cestas",start,start.plusHours(3),12); actions.enroll(vol,s.id());
        actions.createCampaign(admin,"Cuidado que aquece","Campanha de acolhimento e doação de kits para instituições da comunidade.");
    }
}
