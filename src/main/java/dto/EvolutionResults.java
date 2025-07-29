package dto;

import models.Agent;

import java.io.Serializable;

public record EvolutionResults(
        int generation,
        EvaluatedSolution bestSolution,
        EvaluatedSolution worstSolution
) implements Serializable {
}
