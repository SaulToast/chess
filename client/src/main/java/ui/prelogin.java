package ui;

import java.util.Scanner;

import exceptions.ResponseException;
import facade.ServerFacade;

import static ui.EscapeSequences.*;

public class prelogin {

    private final ServerFacade facade;
    private Scanner scanner;

    public prelogin(ServerFacade facade, Scanner scanner) {
        this.facade = facade;
        this.scanner = scanner;

    }

    public void run() {
        System.out.print("");
        var result = "";
        while (!result.equals("quit")) {

            printPrompt();
            result = scanner.nextLine();
            System.out.print(RESET_TEXT_COLOR);
        }
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
