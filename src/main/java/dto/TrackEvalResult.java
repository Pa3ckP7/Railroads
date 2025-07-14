package dto;

import models.Gene;

import java.util.HashSet;

public record TrackEvalResult(HashSet<Gene> genes, boolean success) {
}
