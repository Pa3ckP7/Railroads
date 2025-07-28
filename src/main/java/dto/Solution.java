package dto;

import models.Agent;
import railroads.Board;

import java.io.Serializable;

public record Solution (
    Agent signer,
    Board solution
)implements Serializable {}
