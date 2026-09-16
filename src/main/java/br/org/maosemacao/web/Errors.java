package br.org.maosemacao.web;
import br.org.maosemacao.domain.Rule;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.ConcurrencyFailureException;
import java.util.Map;
@RestControllerAdvice
public class Errors {
    @ExceptionHandler(Rule.class) ResponseEntity<?> rule(Rule ex) { return ResponseEntity.status(ex.status()).body(Map.of("message",ex.getMessage())); }
    @ExceptionHandler({MethodArgumentNotValidException.class,HttpMessageNotReadableException.class}) ResponseEntity<?> validation(Exception ex) { return ResponseEntity.badRequest().body(Map.of("message","Dados inválidos. Confira os campos, o e-mail e a senha (12 a 64 caracteres).")); }
    @ExceptionHandler({DataIntegrityViolationException.class,ConcurrencyFailureException.class}) ResponseEntity<?> conflict(Exception ex) { return ResponseEntity.status(409).body(Map.of("message","Conflito com outro registro ou atualização. Atualize a página e tente novamente.")); }
}
