package javagamesolver.interfaces;

import java.util.List;

public interface Game {
    public List<Game> possible_games();

    public boolean is_game_over();

    public boolean is_winning_state();

    public int reward_value(GamePlayer player);

    public String toString();

    public GamePlayer player_turn();

    // Todo Refactor so that it takes a map and add a "read_move" method that reads
    // from stdin
    public Game player_move(int col);
}
