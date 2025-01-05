package util.helpers;

@FunctionalInterface
public interface EvalFunc<T> {
    int eval(T obj);
}
