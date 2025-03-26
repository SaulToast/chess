package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public abstract class Repl {
    protected final ChessClient client;
    protected final Scanner scanner;

    public Repl(ChessClient client, Scanner scanner) {
        this.client = client;
        this.scanner = scanner;
    }

    public void run() {

        var result = "start";
        while (!result.equals("")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result + RESET_TEXT_COLOR);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println(RESET_TEXT_COLOR);
    }

    protected abstract void printPrompt();
    protected abstract String eval(String input);
    public abstract String help();
}
