package util.helpers;

import java.util.Collection;

@FunctionalInterface
public interface RepopulateFunc<T> {
    Collection<T> repopulate(Collection<T> population, CrossoverFunc<T> crossoverFunc, int maxPopulation, long seed);
}
