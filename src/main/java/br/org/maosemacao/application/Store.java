package br.org.maosemacao.application;
import java.util.List;
public interface Store {
    <T> T get(Class<T> type, long id);
    <T> T lock(Class<T> type, long id);
    <T> List<T> all(Class<T> type);
    <T> T save(T value);
    <T> void delete(Class<T> type, long id);
}
