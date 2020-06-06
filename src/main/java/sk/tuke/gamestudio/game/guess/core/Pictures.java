package sk.tuke.gamestudio.game.guess.core;

import java.util.Random;

public class Pictures {

    private String secretName = "Secret";


   private char[][] defaultPic = new char[][]{
            { 'I', 'I', 'I', 'I'},
            { 'I', 'I', 'I', 'I'},
            { 'I', 'I', 'I', 'I'},
            { 'I', 'I', 'I', 'I'}
    };

    public char[][] getDefaultPic() {
        return defaultPic;
    }

    private char[][] circle = new char[][]{
            { '#', '-', '-', '#'},
            { '|', '#', '#', '|'},
            { '|', '#', '#', '|'},
            { '#', '-', '-', '#'}
    };

   private char[][] rectangle = new char[][]{
            { '-', '-', '-', '-'},
            { '|', '#', '#', '|'},
            { '|', '#', '#', '|'},
            { '-', '-', '-', '-'}
    };


    private char[][] pillar = new char[][]{
            { 'X', 'X', 'X', 'X'},
            { '#', 'X', 'X', '#'},
            { '#', 'X', 'X', '#'},
            { 'X', 'X', 'X', 'X'}
    };

    private char[][] cross = new char[][]{
            { '#', '|', '|', '#'},
            { '-', '⌟', '⌞', '-'},
            { '-', '⌝', '⌜', '-'},
            { '#', '|', '|', '#'}
    };

    private char[][] house = new char[][]{
            { '◢', '▇', '▇', '◣'},
            { '▇', '⊞', '⊞', '▇'},
            { '▇', '⊞', '⊞', '▇'},
            { '▇', '☖', '▇', '▇'}
    };
    private char[][] face = new char[][]{
            { '╭', '—', '—', '╮'},
            { '⋐', 'O', 'O', '⋑'},
            { '|', '∠','#', '|'},
            { '╰', '⌓', '—', '╯'}
    };



    public String getSecretName() {
        return secretName;
    }

    private void setSecretName(String name) {
        this.secretName = name;
    }

    public String picAnimalName(){
        Random rand = new Random();
        int randNum = rand.nextInt(6);

        switch (randNum) {
            case 0:
                setSecretName("fox");
                break;
            case 1:
                setSecretName("fish");
                break;
            case 2:
                setSecretName("hippo");
                break;
            case 3:
                setSecretName("panda");
                break;
            case 4:
                setSecretName("penguin");
                break;
            case 5:
                setSecretName("tiger");
                break;
            default:
                setSecretName("fox");
                break;
        }
        return getSecretName();
    }

    public String picFruitName(){
        Random rand = new Random();
        int randNum = rand.nextInt(6);

        switch (randNum) {
            case 0:
                setSecretName("apple");
                break;
            case 1:
                setSecretName("pear");
                break;
            case 2:
                setSecretName("strawberry");
                break;
            case 3:
                setSecretName("lemon");
                break;
            case 4:
                setSecretName("papaya");
                break;
            case 5:
                setSecretName("cherry");
                break;
            default:
                setSecretName("apple");
                break;
        }
        return getSecretName();
    }


    public char[][] returnPic() {
        Random rand = new Random();
        int randNum = rand.nextInt(6);

        switch (randNum){
            case 0:
                setSecretName("face");
                return face;
            case 1:
                setSecretName("circle");
                return circle;
            case 2:
                setSecretName("rectangle");
                return rectangle;
            case 3:
                setSecretName("pillar");
                return pillar;
            case 4: setSecretName("cross");
                return cross;
            case 5: setSecretName("house");
                return house;
            default:
                setSecretName("circle");
                return circle;

        }

    }




}
