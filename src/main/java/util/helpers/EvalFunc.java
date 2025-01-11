package util.helpers;

@FunctionalInterface
public interface EvalFunc<T> {
    long eval(T obj);
}
