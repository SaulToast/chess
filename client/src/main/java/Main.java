import chess.*;
import ui.ChessClient;
import static ui.EscapeSequences.*;

public class Main {
    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println(WHITE_QUEEN + " 240 Chess Client: " + piece);

        var client = new ChessClient(serverUrl);
        client.run();
    }
}