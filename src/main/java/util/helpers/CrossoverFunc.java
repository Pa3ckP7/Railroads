package util.helpers;

import genetic.Agent;
import railroads.Board;

import java.util.Collection;
import java.util.function.BiFunction;

public interface CrossoverFunc {
    Agent Cross(Agent a, Agent b, long seed);
}

