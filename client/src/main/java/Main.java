import java.util.Scanner;

import chess.*;
import ui.ChessClient;
import ui.Prelogin;

public class Main {
    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        var client = new ChessClient(serverUrl);
        client.run();
    }
}