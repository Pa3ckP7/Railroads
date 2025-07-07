package dto;

import models.Agent;

public record EvolutionResults(
        int generation,
        EvaluatedSolution bestSolution,
        EvaluatedSolution worstSolution
) {
}
