package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.guess.core.*;
import sk.tuke.gamestudio.service.CommentService;
import sk.tuke.gamestudio.service.RatingService;
import sk.tuke.gamestudio.service.ScoreService;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.servlet.annotation.WebServlet;
import java.util.Date;
import java.util.LinkedList;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
@WebServlet("/loginServlet")
@RequestMapping("/guess")
public class GuessController {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserController userController;

    private Picture picture;
    private Counter counter;
    private Tile_State[][] tilestate;
    private String secret;          // game secret, (rand from pictures)
    private String gamesecret;      // player guess
    private int firstPlay = 0;      // stating new game/first game
    private boolean isDone;
    private LinkedList<String> usedPictures = new LinkedList<>();   // to not repeat the pictures
    private String comment;         // player comment
    private String whichPictureWord ="Animal";  // guessing Animals/Fruits
    private int rating;             // actual player rating
    private boolean checkInt;       // checking if typed text is int
    private boolean loginNeeded;    // need to be logged in to leave a comment and rating
    private boolean marking;        // to html displaying
    private boolean bonus = false;              // if all pictures guessed
    private boolean whichPicture = false; //false- animal || true- fruit
    private boolean whichButtonTriggered;       // to html displaying
    private boolean bigOrSmall;     // if guessing tiles in center (lose more points)

    @RequestMapping
    public String guess(String row, String column, Model model) {
        if (picture == null)
            newGame();
        try {
            if(firstPlay == 0){
                whichButtonTriggered = false;
                System.out.println("BONUS: "+ bonus);
                tilestate = picture.getTileState();
                picture.setState(GameState.PLAYING);
                nextSecret();
                bonus = false;

                    if(whichPicture == false)
                        whichPictureWord = "Animal";

                marking = false;
                counter = new Counter();
                firstPlay++;
            }

            // check the middle field
            if(Integer.parseInt(row) == 1 || Integer.parseInt(row) == 2){
                if(Integer.parseInt(column) == 1 || Integer.parseInt(column) == 2){
                    bigOrSmall = true;
                }else{
                    bigOrSmall = false;
                }
            }else{
                bigOrSmall = false;
            }

            model.addAttribute("trigger",whichButtonTriggered);
            bonus = false;

            // guess right word
            if(secret.equals("bonus")){
                bonus = true;
                isDone = true;
                model.addAttribute("donePicture",isDone);
                model.addAttribute("bonus",bonus);
                showFullImage();
                picture.setState(GameState.SOLVED);
                if(userController.isLogged()){
                    if(picture.getState() == GameState.SOLVED){
                        scoreService.addScore(new Score(
                                userController.getLoggedUser(),
                                counter.getMaxScore(),
                                "guess",
                                new Date()
                        ));
                    }
                }
                prepareModel(model);
            }


            if(picture.getState() == GameState.SOLVED || picture.getState() == GameState.FAILED){
                marking = true;
            }
            if (picture.getState() == GameState.PLAYING) {
                    picture.openTile(Integer.parseInt(row), Integer.parseInt(column));
                    whichButtonTriggered = true;
                    model.addAttribute("trigger",whichButtonTriggered);
                    counter.usedHint(bigOrSmall);
                        if(picture.getState() == GameState.SOLVED || picture.getState() == GameState.FAILED){
                            showFullImage();
                            marking = true;
                        }
                }
        } catch (NumberFormatException e) {
            //Nothing happends
            e.printStackTrace();
        }

        prepareModel(model);
        return "guess";
    }

    public boolean isBonus() {
        return bonus;
    }

    @RequestMapping("/new")
    public String newGame(Model model) {
        newGame();
        resetToDefault(model);
        prepareModel(model);
        bonus = false;
        whichPicture = false;
        whichButtonTriggered = false;
        usedPictures.clear();
        isDone = false;
        firstPlay = 0;
        model.addAttribute("trigger",whichButtonTriggered);

        return "guess";
    }

    public GameState getGameState() {
        return picture.getState();
    }
    public String getSecret(){
        return this.secret;
    }

    public String pictureScore(){
        String score = String.valueOf(counter.getScore());
        if(counter.getScore() <= 0){
            picture.setState(GameState.FAILED);
            showFullImage();
        }

        return score;
    }

    private void nextSecret(){

        if(usedPictures.size() < 6) {
            if(whichPicture == true) {
                this.secret = picture.getFruitSecret();

            }else {
                this.secret = picture.getAnimalSecret();
            }
        }else{
            picture.setSecret("bonus");
            isDone = true;
            secret = "bonus";
        }

        if(usedPictures.contains(secret) && whichButtonTriggered == true)
            nextSecret();
        else
            usedPictures.add(secret);

    }

    @RequestMapping("/setFruit")
    public String setFruit(Model model){
        whichPicture = true;
        whichButtonTriggered = true;
        model.addAttribute("trigger",whichButtonTriggered);
        nextSecret();
        whichPictureWord = "Fruit";
        System.out.println("FRUIT");
        prepareModel(model);

        return "guess";
    }

    @RequestMapping("/setAnimal")
    public String setAnimal(Model model){
        whichPicture = false;
        whichButtonTriggered = true;
        model.addAttribute("trigger",whichButtonTriggered);
        nextSecret();
        whichPictureWord = "Animal";
        System.out.println("ANIMAL");
        prepareModel(model);

        return "guess";
    }

    public String getWhichPictureWord() {
        return whichPictureWord;
    }

    // select YES after picture
    @RequestMapping("/nextpicture")
    public String nextPic(String pic, Model model){
        System.out.println("Next picture");
        newGame();
        firstPlay++;
        whichButtonTriggered = true;
        model.addAttribute("trigger",whichButtonTriggered);
        counter.setMaxScore(counter.getScore());
        tilestate = picture.getTileState();
        nextSecret();
        marking = false;
        counter.setScore(counter.defaultScore());
        prepareModel(model);
        getHtmlField();

        return "guess";
    }

    // select NO after picture
    @RequestMapping("/nonextpicture")
    public String noNextPic(String pic, Model model){
        // END GAME
        counter.setMaxScore(counter.getScore());
        whichButtonTriggered = true;
        model.addAttribute("trigger",whichButtonTriggered);
        marking = false;
        isDone = true;
        model.addAttribute("donePicture",isDone);
        if(userController.isLogged()){
            if(picture.getState() == GameState.SOLVED){
                scoreService.addScore(new Score(
                        userController.getLoggedUser(),
                        counter.getMaxScore(),
                        "guess",
                        new Date()
                ));
            }
        }
        prepareModel(model);

        return "guess";
    }


    @RequestMapping("/rating")
    public String playerRating(String rating, Model model) {
        try {
            checkInt = false;
            loginNeeded = false;
            model.addAttribute("checkInt",checkInt);
            this.rating = Integer.parseInt(rating);
            System.out.println("Rating: " + rating);

            if(this.rating >=0 && this.rating < 6){
                if(!userController.isLogged()){
                    loginNeeded = true;
                    model.addAttribute("loginNeeded",loginNeeded);
                }

            if (userController.isLogged()){
                ratingService.setRating(new Rating(
                        userController.getLoggedUser(),
                        "guess",
                        this.rating,
                        new Date()
                ));
            }
            }else{
                checkInt = true;
                model.addAttribute("checkInt",checkInt);
           //     System.out.print("Type a number between 0-5");
            }
            prepareModel(model);

        }
        catch(Exception e) {
            checkInt = true;
            model.addAttribute("checkInt",checkInt);
            prepareModel(model);
         //   System.out.print("Type a Number, Not String");
        }

        return "guess";
    }

    //before staring new game
    public void resetToDefault(Model model){
        for(int i = 0;i<picture.getRowCount();i++){
            for (int j = 0;j < picture.getColumnCount();j++){
                tilestate[i][j] = Tile_State.VISIBLE;
            }
        }

        counter.setScore(480);
        counter.setMaxScore(-50000);
        prepareModel(model);
    }

    @RequestMapping("/comment")
    public String playerComment(String comment, Model model) {
        this.comment = comment;
        loginNeeded = false;
        if(!userController.isLogged()){
            loginNeeded = true;
            model.addAttribute("loginNeeded",loginNeeded);
        }

        if(userController.isLogged()){
                commentService.addComment(new Comment(
                        userController.getLoggedUser(),
                        "guess",
                        comment,
                        new Date()
                ));
        }
        prepareModel(model);

        return "guess";
    }


    @RequestMapping("/secret")
    public String login(String secret, Model model) {
        gamesecret = secret.toLowerCase();
        whichButtonTriggered = true;
        model.addAttribute("trigger",whichButtonTriggered);

        if(gamesecret.equals(this.secret)){
            picture.setState(GameState.SOLVED);
            showFullImage();

            if(picture.getState() == GameState.SOLVED || picture.getState() == GameState.FAILED)
                marking =true;

        }else if(!gamesecret.equals(this.secret) && !gamesecret.isEmpty()){
            counter.wrongWord();
        }
        prepareModel(model);
        return "guess";
    }

    public String getPlayerScore(){
        String playerScore = String.valueOf(counter.getMaxScore());

        return playerScore;
    }

    @RequestMapping("/guess/mark")
    public String mark(){
        marking = !marking;

        return "guess";
    }

    public boolean isMarking() {
        return marking;
    }

    public String getHtmlField() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class='tile'>\n");
        for (int row = 0; row < picture.getRowCount(); row++) {
            sb.append("<tr>\n");
            for (int column = 0; column < picture.getColumnCount(); column++) {
                sb.append("<td>\n");
                if (picture.equals(this.picture))
                    sb.append("<a href='" +
                            String.format("/guess?row=%s&column=%s", row, column)
                            + "'>\n");

                sb.append("<img src='/images/guess/"+ secret+"/" + getImageName(row,column)+ ".png'>");
                if (picture.equals(this.picture))
                    sb.append("</a>\n");
                sb.append("</td>\n");
            }
            sb.append("</tr>\n");
        }
        sb.append("</table>\n");

        return sb.toString();
    }

    //Show stars instead of number
    public String averageRating(){
        int averageR = ratingService.getAverageRating("guess");
        StringBuilder sb = new StringBuilder();
        for(int i = 0;i < averageR;i++) {
            sb.append("<img src='/images/guess//star22x22.png'>");
            sb.append(" ");
        }

        return sb.toString();
    }

    private void showFullImage(){
        for(int i = 0;i < picture.getRowCount();i++){
            for(int j = 0;j < picture.getColumnCount();j++){
                tilestate[i][j] = Tile_State.INVISIBLE;
            }
        }
    }

    private String getImageName(int row, int col) {
      if(tilestate[row][col] == Tile_State.VISIBLE){
          return "default";
      }else {
          return secret + row + col;
      }
    }

    public String ratingInStars(){

        StringBuilder sb = new StringBuilder();
        for(int i = 0;i < ratingService.getRating("guess",userController.getLoggedUser());i++) {
            sb.append("<img src='/images/guess//star22x22.png'>");
            sb.append(" ");
        }

        return sb.toString();
    }

    private void prepareModel(Model model) {
        model.addAttribute("scores", scoreService.getTopScores("guess"));
        model.addAttribute("comments", commentService.getComments("guess"));

        if (userController.isLogged()){
            String c = userController.getLoggedUser();
            try {
                String playerRating = String.valueOf(entityManager.createQuery("SELECT p FROM Rating p WHERE p.player =: c")
                        .setParameter("c",userController.getLoggedUser())
                        .getSingleResult());

                if(playerRating.contains(userController.getLoggedUser()));
                    model.addAttribute("ratings", ratingInStars());

          } catch (NoResultException e) {
                model.addAttribute("ratings","No rating records");
                System.out.println("No record of this player in database");
            }

        }else{
            System.out.println("Not logged = no rating");
            model.addAttribute("ratings","Player not logged");
        }

    }

    private void newGame() {
        picture = new Picture(4, 4);
    }
}
