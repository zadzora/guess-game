package sk.tuke.gamestudio.game.guess;


import sk.tuke.gamestudio.game.guess.consoleui.ConsoleUI;
import sk.tuke.gamestudio.game.guess.core.Picture;

public class Main {

    public static void main(String[] args) {
        Picture picture = new Picture(4, 4);

        ConsoleUI ui = new ConsoleUI();
        ui.play(picture);
    }
}
