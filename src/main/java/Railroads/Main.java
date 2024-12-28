package Railroads;

public class Main {
    public static void main(String[] args) {
        Board board = new Board(20,20, 5, 420);
        board.randomize();
        board.printBoard();
    }
}
