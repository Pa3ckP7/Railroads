package util.DataContainers;

import genetic.Agent;
import railroads.Board;

import java.util.ArrayList;

public record EvolutionResults(
        int generation,
        Agent bestAgent,
        int bestScore,
        int[] scores
) {
}
