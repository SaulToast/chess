package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public ChessPosition(ChessPosition other) {
        this.row = other.row;
        this.col = other.col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChessPosition)) {
            return false;
        }
        return this.row == ((ChessPosition) obj).row && this.col == ((ChessPosition) obj).col;
    }

    @Override
    public String toString() {
        char file = (char) ('a' + col - 1);
        int rank = row;
        return "" + file + rank;
    }
}
