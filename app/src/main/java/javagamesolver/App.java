/*
 * This source file was generated by the Gradle 'init' task
 */
package javagamesolver;

import java.util.Scanner;

import javagamesolver.games.Connect_Four;
import javagamesolver.interfaces.Game;
import javagamesolver.strategies.MCTS;
import javagamesolver.interfaces.GameStrategy;

public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        Game game = new Connect_Four();
        GameStrategy strategy = new MCTS();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Started game!");
        while (true) {
            game = strategy.make_move(game);

            System.out.println("Board \n" + game.toString());

            if (game.is_winning_state()) {
                System.out.println("You lost!");
                break;
            }

            if (game.is_game_over()) {
                System.out.println("Tie!");
                break;
            }

            System.out.println("Enter column to drop piece in: ");
            int col = scanner.nextInt();

            game = game.player_move(col);

            System.out.println("Board: \n" + game.toString());

            if (game.is_winning_state()) {
                System.out.println("You Won!");
                break;
            }

            if (game.is_game_over()) {
                System.out.println("Tie!");
                break;
            }
        }

        scanner.close();
    }
}
