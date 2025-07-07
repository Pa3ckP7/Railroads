package dto;

import models.Agent;
import railroads.Board;

public record Solution (
    Agent signer,
    Board solution
){}
