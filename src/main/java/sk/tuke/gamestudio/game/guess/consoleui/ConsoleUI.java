package sk.tuke.gamestudio.game.guess.consoleui;

import org.springframework.beans.factory.annotation.Autowired;
import sk.tuke.gamestudio.game.guess.core.*;
import sk.tuke.gamestudio.entity.*;
import sk.tuke.gamestudio.service.*;

import java.util.*;

public class ConsoleUI{


    private static final String GAME_NAME = "guess";
    private char[][] tiles = new char[][]{};
    private char[][] tiles2 = new char[][]{};

    private Tile_State[][] state;
    private Tile_State VISIBLE = Tile_State.VISIBLE;
    private Tile_State INVISIBLE = Tile_State.INVISIBLE;

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_TILE = "\u001b[33;1m";

    private Picture picture;
    private Pictures pics;
    private int megaHint = 3;
    private int possibleMoves = 16;
    private Counter counter = new Counter();
    private boolean gotSecret = false;
    private  char[] mixedLetters;
    //Random numbers..


    //Picture numbers
    private int prNumber;
    private int pcNumber;

    private int playerScore;
    private String secret;
    private int picsNumber = 0;
    private LinkedList<String> usedPictures = new LinkedList<>();
    private boolean endOFGame = false;

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;

    public void play(Picture picture){
        possibleMoves = 16;
        this.picture = picture;
        this.pics = new Pictures();
        this.prNumber = picture.getRowCount();
        this.pcNumber = picture.getColumnCount();

        tiles = pics.getDefaultPic();
        tiles2 = pics.getDefaultPic();


        state = new Tile_State[prNumber][pcNumber];
        for(int i = 0;i<prNumber;i++){
            for(int j = 0;j<pcNumber;j++){
                state[i][j] = VISIBLE;
            }
        }

        System.out.print(ANSI_BLUE +"Welcome to Guess Who "+ANSI_RESET);
        printAverageRating();
        //System.out.println("AV Rating: "+ ratingService.getAverageRating(GAME_NAME));
        System.out.println(ANSI_RED +"Help" + ANSI_RESET);
        System.out.print(ANSI_YELLOW + "I -hidden field" + "  ");
        System.out.println("# -empty field" + "  " + ANSI_RESET);
        System.out.println(ANSI_BLUE + "Guess a picture: " + ANSI_RESET);

        do {
            //FIX TO CRASHES AFTER USED ALL PICTURES
            picsNumber++;
            if(picsNumber== 6){ //NUM OF PICS+1
                System.out.println(ANSI_GREEN + "You guessed all pictures we have." + ANSI_RESET);
                printScores();
                return;
            }
            start();
        } while(picture.getState() == GameState.PLAYING);


        if(picture.getState() == GameState.SOLVED) {
            System.out.println(ANSI_GREEN + "Solved!" + ANSI_RESET);
        } else if(picture.getState() == GameState.FAILED){
            System.out.println(ANSI_RED + "Failed!" + ANSI_RESET);
        }

        System.out.println(ANSI_BLUE + "Do you want play again ? (" + ANSI_RESET + ANSI_GREEN + "y" + ANSI_RESET + ANSI_BLUE + ")" + ANSI_RESET);
        Scanner sc= new Scanner(System.in);
        String str= sc.nextLine();
        if (str.equals("y")) {
            resetPicture();
            play(picture);
        }else{
            System.out.println(ANSI_CYAN + "End of the game" + ANSI_RESET);
            System.out.println(ANSI_BLUE + "Your score final is: "+ ANSI_GREEN+ playerScore + ANSI_RESET);
            counter.isMaxScore(playerScore);
            scoreService.addScore(
                    new Score(System.getProperty("user.name"),  counter.getMaxScore(), GAME_NAME, new Date()));
            printScores();

        }

        leaveCommentandRating();
        printAverageRating();

    }


    private void start() {
        gotSecret = false;
        setGuessedPicture();
        print2();
        this.secret = pics.getSecretName();
        mixedLetters = new char[secret.length()];
        secretAplhabets(secret);
        pictureInfo();
        usedPictures.add(pics.getSecretName());


        while (possibleMoves != 0 && counter.getScore() > 0) {
            handleInput();
            if(gotSecret){
                return;
            }
        }

        System.out.println( ANSI_RED + "Out of hints." + ANSI_RESET);
        showWholePicture();
        print2();
        System.out.println("Secret: " + ANSI_GREEN + this.secret + ANSI_RESET);
        System.out.println("Your score is: "+ ANSI_CYAN + playerScore + ANSI_CYAN);
        picture.setState(GameState.FAILED);

    }

    private void handleInput(){
        Scanner sc= new Scanner(System.in);
        System.out.println( ANSI_BLUE + "Guess word: // Press (" + ANSI_GREEN + "y" + ANSI_BLUE + ") for hint"+ANSI_RED +" or " +ANSI_BLUE+ "("+ ANSI_RED + "m" + ANSI_BLUE +") for MEGAHINT" + ANSI_RESET);
        String str= sc.nextLine();

        if (str.equals("y")) {
            possibleMoves--;
            System.out.println("Used hint");
            boolean yrs = false;
            counter.usedHint(yrs);
            hint();
            print2();
            pictureInfo();

        }else if(str.equals("m")){
            if(megaHint > 0){
            System.out.println("Used MEGAHINT");
            if(possibleMoves > 1) { // FIX- ak chybalo posledne policko, zabrani crashnutiu hry
                megaHint--;
                boolean jrs = false;
                counter.usedHint(jrs);
                hint();
                hint();
                possibleMoves-=2;
            }else{
                boolean jrs = false;
                counter.usedHint(jrs);
                hint();
            }

            if(megaHint == 0){
                System.out.println( ANSI_YELLOW+"That was your last MEGAHINT!"+ANSI_RESET);
            }
            }else {
                System.out.println(ANSI_RED +"You already used all MEGAHINT´s." + ANSI_RESET);
            }
            print2();
            pictureInfo();

        }else if(str.equals(secret)){
            showWholePicture();
            print2();
            playerScore += counter.getScore();
            System.out.println("Secret: " + ANSI_GREEN + this.secret + ANSI_RESET);
            System.out.println("Your score is: "+ ANSI_CYAN + playerScore + ANSI_CYAN);
            picture.setState(GameState.SOLVED);
            gotSecret = true;

        }else{
            System.out.println( ANSI_RED + "Wrong word!"+ ANSI_RESET);
            counter.wrongWord();
            print2();
            pictureInfo();
        }
    }


    private void print2(){
        for(int i = 0;i<prNumber;i++){
            for(int j = 0;j<pcNumber;j++){
                if(tiles[i][j] != tiles2[i][j]){
                    System.out.print(ANSI_TILE + tiles[i][j] + "  " + ANSI_RESET);
                }else {
                    System.out.print(tiles[i][j] + "  ");
                }
            }
            System.out.println(" ");
        }
    }

    public void hint() {
        Random rand = new Random();
        int rowNumber = rand.nextInt(4);

        int columnNumber = rand.nextInt(4);

        if(state[rowNumber][columnNumber] == VISIBLE) {
            state[rowNumber][columnNumber] = INVISIBLE;
            tiles[rowNumber][columnNumber] = tiles2[rowNumber][columnNumber];
        }else
            hint();
    }

    private void showWholePicture(){
        tiles = tiles2;
    }

    private void secretAplhabets(String secret){
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

    private void printLetters(){
        for(int i = 0;i<mixedLetters.length;i++) {
            System.out.print(mixedLetters[i] + " ");
        }
    }

    private void pictureInfo(){
        int secretLength = secret.length();
        System.out.print("Secret length: ");
        for(int i = 0;i<secretLength;i++) {
            System.out.print("_ ");
        }
        System.out.println();
        System.out.print("Words: ");
        printLetters();

        System.out.println();
        System.out.print("Picture Score: " + ANSI_YELLOW + counter.getScore() + ANSI_RESET + "  ");
        System.out.print("Player Score: " + ANSI_CYAN +  playerScore + ANSI_RESET + "  ");
        System.out.println("Mega Hints: " + ANSI_RED + megaHint + ANSI_RESET);
        System.out.println("-------------------");
    }

    private void setGuessedPicture(){
        char[][] guessedPic = pics.returnPic();

        //NOT SAME PIC 2 TIMES
        //Crashes if all pics were used
        if(usedPictures.contains(pics.getSecretName())){
            setGuessedPicture();
        }else
        {
            tiles2 = guessedPic;
        }


    }

    private void resetPicture(){
        picture.setState(GameState.PLAYING);
        counter.setScore(480);
        // RESET PICTURE
        for(int i = 0;i<prNumber;i++){
            for(int j = 0;j<pcNumber;j++){
                tiles[i][j] = 'I';
            }
        }
        //RESET STATE
        for(int i = 0;i<prNumber;i++){
            for(int j = 0;j<pcNumber;j++){
                state[i][j] = Tile_State.VISIBLE;
            }
        }
    }

    private void leaveCommentandRating(){
        Scanner cl= new Scanner(System.in);

        if(endOFGame){
            return;
        }
        System.out.println( ANSI_BLUE + "Do you want to leave a comment and rate? (" +ANSI_GREEN +"y" + ANSI_BLUE + ")" +ANSI_RESET);
        String cll= cl.nextLine();
        if (cll.equals("y")) {
            Scanner plComment = new Scanner(System.in);
            System.out.print(ANSI_BLUE+"Type your comment here:  "+ANSI_RESET);
            String plMessage = plComment.nextLine();
            commentService.addComment(
                    new Comment(System.getProperty("user.name"), GAME_NAME ,plMessage, new Date()));

            System.out.print(ANSI_BLUE +"Leave your rate (1-5)  "+ANSI_RESET);
            int rateS = cl.nextInt();
            if(rateS > 0 && rateS < 6){
                ratingService.setRating(
                        new Rating(System.getProperty("user.name"), GAME_NAME,rateS,new Date())
                );
            }else{
                System.out.println("Your rate was not included.");
            }



            System.out.println("Your comment and rate was added. Thanks for your time!");
            printComment();

            endOFGame = true;
            return;
        }
        endOFGame = true;
        System.out.println("Goodbye.");
        return;
    }


    private void printScores() {
        List<Score> scores = scoreService.getTopScores(GAME_NAME);

        Collections.sort(scores);

        System.out.println("");
        System.out.println( ANSI_RED +"Top scores:" + ANSI_RESET);

        for (int i = 0; i < scores.size(); i++) {
            System.out.println(scores.get(i));
        }
    }


    private void printComment(){
        List<Comment> comments = commentService.getComments(GAME_NAME);
        System.out.println("");
        System.out.println( ANSI_RED +"Last 5 comments:" + ANSI_RESET);

        for (int i = 0; i < comments.size(); i++) {
            System.out.println(comments.get(i));
        }

    }


    private void printAverageRating(){

        System.out.print("Rating: ");
        if (ratingService == null && ratingService.getAverageRating(GAME_NAME) == 0) {
            System.out.println("No rating record.");
            return;
        }
        for(int i = 0;i < ratingService.getAverageRating(GAME_NAME);i++){
            System.out.print(ANSI_YELLOW+"* "+ANSI_RESET);
        }
        System.out.println("");
    }


}

