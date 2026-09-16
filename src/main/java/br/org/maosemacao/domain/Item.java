package br.org.maosemacao.domain;
import java.time.LocalDateTime;
public record Item(Long id, String name, String unit, int balance) {

    public Item move(int quantity) {
        long next = (long) balance + quantity;
        Rule.require(quantity != 0 && next >= 0 && next <= Integer.MAX_VALUE, "Movimentação inválida ou estoque insuficiente");
        return new Item(id, name, unit, (int) next);
    }

}
