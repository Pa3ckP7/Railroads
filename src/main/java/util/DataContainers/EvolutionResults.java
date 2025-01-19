package util.DataContainers;

import genetic.Agent;

public record EvolutionResults(
        int generation,
        Agent bestAgent,
        long highestScore,
        long lowestScore,
        long[] scores
) {
}
