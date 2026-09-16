package br.org.maosemacao.domain;
import java.time.LocalDateTime;
public record Campaign(Long id, String name, String description, String status) {

    public Campaign edit(String name, String description) {
        Rule.require(!status.equals("ENCERRADA"), "Campanha encerrada não pode ser editada");
        return new Campaign(id, Rule.text(name, 120), Rule.text(description, 2000), status);
    }
    public Campaign publish(boolean hasNeeds) {
        Rule.require(status.equals("RASCUNHO"), "Somente rascunhos podem ser publicados");
        Rule.require(hasNeeds, "Cadastre pelo menos uma necessidade");
        return new Campaign(id, name, description, "PUBLICADA");
    }
    public Campaign close(boolean hasOpenShifts) {
        Rule.require(status.equals("PUBLICADA"), "Campanha deve estar publicada");
        Rule.require(!hasOpenShifts, "Finalize ou cancele os turnos abertos");
        return new Campaign(id, name, description, "ENCERRADA");
    }
    public void requirePublished() { Rule.require(status.equals("PUBLICADA"), "Campanha não está publicada"); }

}
