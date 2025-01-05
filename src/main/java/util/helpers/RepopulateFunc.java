package util.helpers;

import railroads.Board;

import java.util.Collection;

@FunctionalInterface
public interface RepopulateFunc<T> {
    Collection<T> repopulate(Collection<T> population, int[] scores, Board baseBoard, float mutation, CrossoverFunc<T> crossoverFunc, int maxPopulation, long seed);
}
