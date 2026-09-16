package br.org.maosemacao.domain;
public interface PriorityStrategy {
    int score(int vulnerability, int members);
    class Vulnerability implements PriorityStrategy {
        public int score(int vulnerability, int members) { return vulnerability * 100 + members; }
    }
    class FamilySize implements PriorityStrategy {
        public int score(int vulnerability, int members) { return members * 100 + vulnerability; }
    }
    static PriorityStrategy named(String name) {
        return switch(name) {
            case "VULNERABILIDADE" -> new Vulnerability();
            case "TAMANHO_FAMILIA" -> new FamilySize();
            default -> throw new Rule("Critério de prioridade inválido");
        };
    }
}
