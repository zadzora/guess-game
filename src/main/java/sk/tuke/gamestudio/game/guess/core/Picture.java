package sk.tuke.gamestudio.game.guess.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Picture {

    private Tile_State tState = Tile_State.VISIBLE;
    private String secret;
    private Tile_State[][] tileState;
    private Pictures pictures = new Pictures();
    private int rowCount;
    private int columnCount;
    private  char[] mixedLetters;

    private GameState state = GameState.PLAYING;

    public Picture(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        tileState = new Tile_State[rowCount][columnCount];
        for(int i = 0;i<rowCount;i++){
            for(int j = 0;j<columnCount;j++){
                tileState[i][j] = Tile_State.VISIBLE;
            }
        }

    }

    public GameState getState() {
        return state;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    public Tile_State[][] getTileState() {
        return tileState;
    }

    public void openTile(int row, int column) {
        if(tileState[row][column] == Tile_State.VISIBLE) {
            tileState[row][column] = Tile_State.INVISIBLE;
        }
    }

    public int randNum() {
        Random rand = new Random();
        return rand.nextInt(5);
    }

    public String getAnimalSecret() {
        this.secret = pictures.picAnimalName();
        return secret;
    }

    public String getFruitSecret() {
        this.secret = pictures.picFruitName();
        return secret;
    }

    public void secretAplhabets(String secret){
        int upgradedLength = secret.length();
        char[] alphabet = secret.toCharArray();
        List<Character> chars = new ArrayList();
        List<Character> checkList = new ArrayList<>();
        for(int i = 0;i<upgradedLength;i++) {
            checkList.add(alphabet[i]);
        }

        int checkCount = checkList.size();

        for(int j = 0;j<secret.length();j++) {
            for(int p = 0;p<secret.length();p++){
                if(alphabet[j] == alphabet[p]){
                    if(j != p) {
                        checkList.remove(p);

                    }
                }
            }
        }
        if(checkCount != checkList.size()) {
            upgradedLength -= (checkCount - checkList.size()) / 2;
        }

        Random rand = new Random();
        for(int i = 0;i<upgradedLength;i++){
            int randNum = rand.nextInt(secret.length());
            if(!chars.contains(alphabet[randNum])) {
                mixedLetters[i] = alphabet[randNum];
                chars.add(alphabet[randNum]);
                //    chars.remove(alphabet[randNum]);
            }else {
                i--;
            }
        }
    }

    public void printLetters(){
        for(int i = 0;i<mixedLetters.length;i++) {
            System.out.print(mixedLetters[i] + " ");
        }
    }

    public char[] getMixedLetters() {
        return mixedLetters;
    }
}
