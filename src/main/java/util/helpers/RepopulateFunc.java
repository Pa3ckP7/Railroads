package util.helpers;

import railroads.Board;

import java.util.Collection;

@FunctionalInterface
public interface RepopulateFunc<T> {
    Collection<T> repopulate(Collection<T> population, CrossoverFunc<T> crossoverFunc, long seed);
}
