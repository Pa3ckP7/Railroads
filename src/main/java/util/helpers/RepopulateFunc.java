package util.helpers;


import genetic.Agent;
import java.util.Collection;
import java.util.function.BiFunction;

@FunctionalInterface
public interface RepopulateFunc {
    Collection<Agent> repopulate(Collection<Agent> population, CrossoverFunc crossoverFunc, long seed);
}
