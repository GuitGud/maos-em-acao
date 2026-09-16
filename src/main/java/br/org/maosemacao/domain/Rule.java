package br.org.maosemacao.domain;
public class Rule extends RuntimeException {
    private final int status;
    public Rule(String message) { this(message, 422); }
    public Rule(String message, int status) { super(message); this.status=status; }
    public int status() { return status; }
    public static void require(boolean condition, String message) { if (!condition) throw new Rule(message); }
    public static String text(String value, int max) {
        require(value != null && !value.isBlank() && value.strip().length() <= max, "Texto obrigatório ou tamanho excedido (máximo " + max + ")");
        return value.strip();
    }
}
