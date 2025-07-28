package dto;

import models.Gene;

import java.io.Serializable;
import java.util.HashSet;

public record TrackEvalResult(HashSet<Gene> genes, boolean success) implements Serializable {
}
