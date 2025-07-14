package dto;

import models.Gene;

import java.util.HashSet;

public record EvaluatedSolution (
    HashSet<Gene> rawEvaluation,
    long evaluation,
    Solution solution,
    boolean success,
    HashSet<Gene> flood
    ){}
