package model;

import chess.ChessGame;

public record GameData(
    int GameID, 
    String WhiteUsername, 
    String BlackUsername, 
    String GameName,
    ChessGame game
) {}
