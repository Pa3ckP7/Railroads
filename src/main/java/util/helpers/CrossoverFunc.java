package util.helpers;

import railroads.Board;

import java.util.Collection;

public interface CrossoverFunc<T> {
    public T Cross(T a, T b, Board baseboard, float mutationChance, long seed);
}
