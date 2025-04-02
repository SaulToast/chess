package ui;

import java.util.Arrays;
import java.util.Scanner;

import exceptions.ResponseException;
import static ui.EscapeSequences.*;

public class Prelogin extends Repl {

    public Prelogin(ChessClient client, Scanner scanner) {
        super(client, scanner);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            if (tokens.length == 0) { return help(); }
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> client.registerUser(params);
                case "login" -> client.loginUser(params);
                case "quit" -> client.quit();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }


    protected void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN);
    }

    public String help() {
        return  """

                - register <username> <password> <email>
                - login <username> <password>
                - quit
                - help
                """;
    }
}
