package javagamesolver.games;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import javagamesolver.interfaces.Game;
import javagamesolver.interfaces.GamePlayer;

public class Connect_Four implements Game {

    private record PiecePosition(int row, int col) {
    }

    private enum GamePiece {
        EMPTY, PLAYER1, PLAYER2
    };

    private final GamePiece[][] board;

    private final boolean player1Turn;

    private GamePiece[][] deepCopyBoard(GamePiece[][] board) {
        GamePiece[][] new_board = new GamePiece[board.length][];

        for (int i = 0; i < new_board.length; i++) {
            new_board[i] = Arrays.copyOf(board[i], board[i].length);
        }

        return new_board;
    }

    private Connect_Four(GamePiece[][] board, boolean player1Turn) {
        this.board = deepCopyBoard(board);
        this.player1Turn = player1Turn;
    }

    public Connect_Four() {
        GamePiece[][] board = new GamePiece[6][7];
        for (GamePiece[] row : board) {
            Arrays.fill(row, GamePiece.EMPTY);
        }

        this.board = board;
        this.player1Turn = true;

        System.out.println(this.toString());
    }

    /*
     * Copy existing board and create new connect four game with pos being played
     */
    private Game set_position(Connect_Four game, PiecePosition pos) {
        GamePiece piece = game.player1Turn ? GamePiece.PLAYER1 : GamePiece.PLAYER2;
        Connect_Four new_game = new Connect_Four(game.board, !game.player1Turn);
        new_game.board[pos.row][pos.col] = piece;
        return new_game;
    }

    @Override
    public List<Game> possible_games() {
        int num_col = this.board[0].length;
        int row_len = this.board.length;
        List<PiecePosition> valid_moves = new LinkedList<PiecePosition>();
        for (int col = 0; col < num_col; col++) {
            for (int row = row_len - 1; row >= 0; row--) {
                if (board[row][col] == GamePiece.EMPTY) {
                    valid_moves.add(new PiecePosition(row, col));
                    break;
                }
            }
        }

        List<Game> games = new LinkedList<Game>();
        for (PiecePosition move : valid_moves) {
            games.add(set_position(this, move));
        }

        return games;
    }

    @Override
    public boolean is_game_over() {
        if (this.possible_games().isEmpty()) {
            return true;
        }

        return this.is_winning_state();
    }

    private boolean horizontal_win(GamePiece playerPiece) {
        for (GamePiece[] row : this.board) {
            int count = 0;

            for (GamePiece piece : row) {
                if (piece == playerPiece) {
                    ++count;
                    if (count >= 4) {
                        return true;
                    }
                } else {
                    count = 0;
                }
            }
        }

        return false;
    }

    // This could be implemented by transposing the board and calling horizontal win
    // but that requires more compute resources
    private boolean vertical_win(GamePiece piece) {
        int col_len = this.board[0].length;
        int row_len = this.board.length;
        for (int col = 0; col < col_len; col++) {
            int count = 0;
            for (int row = 0; row < row_len; row++) {
                if (board[row][col] == piece) {
                    ++count;
                    if (count >= 4) {
                        return true;
                    }
                } else {
                    count = 0;
                }
            }
        }

        return false;
    }

    private boolean check_diagonal_up(GamePiece piece, int r, int c) {
        int col_len = this.board[0].length;
        int count = 0;
        for (int row = r, col = c; col < col_len && row >= 0; row--, col++) {
            if (board[row][col] == piece) {
                ++count;
                if (count >= 4) {
                    return true;
                }
            } else {
                count = 0;
            }

        }

        return false;
    }

    private boolean check_diagonal_down(GamePiece piece, int r, int c) {
        int col_len = this.board[0].length;
        int row_len = this.board.length;

        int count = 0;
        for (int row = r, col = c; col < col_len && row < row_len; row++, col++) {
            if (board[row][col] == piece) {
                ++count;
                if (count >= 4) {
                    return true;
                }
            } else {
                count = 0;
            }

        }

        return false;
    }

    private boolean diagonal_win(GamePiece piece) {
        int col_len = this.board[0].length;
        int row_len = this.board.length;

        // Check all first column diagonals
        for (int row = 0; row < row_len; row++) {
            if (check_diagonal_up(piece, row, 0) ||
                    check_diagonal_down(piece, row, 0)) {
                return true;
            }

        }

        for (int col = 1; col < col_len; col++) {
            if (check_diagonal_up(piece, row_len - 1, col) ||
                    check_diagonal_down(piece, 0, col)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean is_winning_state() {
        // Check if the last move won the game
        GamePiece piece = this.player1Turn ? GamePiece.PLAYER2 : GamePiece.PLAYER1;
        return this.horizontal_win(piece) || this.vertical_win(piece) || this.diagonal_win(piece);
    }

    @Override
    public int reward_value(GamePlayer player) {
        // Check if the last player to move won
        boolean winningState = is_winning_state();
        if (winningState) {
            return this.player_turn() == player ? -1 : 1;
        }

        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (GamePiece[] row : this.board) {
            for (GamePiece piece : row) {
                switch (piece) {
                    case GamePiece.EMPTY -> sb.append("_ ");
                    case GamePiece.PLAYER1 -> sb.append("1 ");
                    case GamePiece.PLAYER2 -> sb.append("2 ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public GamePlayer player_turn() {
        return this.player1Turn ? GamePlayer.PLAYER1 : GamePlayer.PLAYER2;
    }

    @Override
    public Game player_move(int col) {
        int row_len = this.board.length;
        for (int row = row_len - 1; row >= 0; row--) {
            if (this.board[row][col] == GamePiece.EMPTY) {
                return set_position(this, new PiecePosition(row, col));
            }
        }

        return this;
    }

}