package sk.tuke.gamestudio.game.guess.core;

import javax.validation.constraints.Max;

public class Counter {
    private int Score;
    private int MaxScore;

    public Counter() {
        this.Score = 480;
        this.MaxScore = 0;
    }

    public int defaultScore(){
        return 480;
    }

    public int getScore() {
        return Score;
    }

    public int getMaxScore() {
        return MaxScore;
    }

    public void setMaxScore(int maxScore) {

        MaxScore += maxScore;
        if(MaxScore < 0)
            this.MaxScore = 0;
    }

    public void isMaxScore(int score){
        if(score > this.MaxScore){
            setMaxScore(score);
        }
    }

    public void setScore(int score) {
        Score = score;
    }

    public int usedHint(boolean bigorsmall) {
        if(bigorsmall == false)
            Score -=30;
        else
            Score -=70;

        if(Score < 0){
            setScore(0);
        }
        return Score;
    }
    public int wrongWord(){
        Score -=15;

        if(Score < 0){
            setScore(0);
        }
        return Score;
    }

}
