package javagamesolver.strategies;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javagamesolver.interfaces.Game;
import javagamesolver.interfaces.GamePlayer;
import javagamesolver.interfaces.GameStrategy;

public class MCTS implements GameStrategy {

    private int calculate_score(Game game, GamePlayer player) {
        List<Game> possible_games = game.possible_games();
        if (game.is_game_over()) {
            return game.reward_value(player);
        }
        int index = ThreadLocalRandom.current().nextInt(possible_games.size());
        Game new_game = possible_games.get(index);
        return calculate_score(new_game, player);
    }

    @Override
    public Game make_move(Game game) {
        List<Game> possible_games = game.possible_games();
        GamePlayer player = game.player_turn();
        // This state should be impossible. You must always verify that the game is
        // ongoing before trying to make a move.
        if (possible_games.isEmpty()) {
            return game;
        }

        Map<String, Object> best_game_and_score = possible_games.parallelStream()
                .map(possible_game -> {
                    int score = IntStream.range(0, 100000).parallel()
                            .map(_indice -> calculate_score(possible_game, player))
                            .sum();
                    return Map.of("score", score, "game", possible_game);
                }).reduce(Map.of("score", Integer.MIN_VALUE), (a, b) -> {
                    if ((int) a.get("score") > (int) b.get("score")) {
                        return a;
                    }
                    return b;
                });

        return (Game) best_game_and_score.get("game");
    }

}
