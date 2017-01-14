package model;

/**
 * Created by fadibotros on 2016-12-10.
 */
public class NLUintent {
    private String userIntent;
    private Double score;

    public NLUintent(String intent, Double score){
        this.userIntent = intent;
        this.score = score;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getUserIntent() {
        return userIntent;
    }

    public void setUserIntent(String userIntent) {
        this.userIntent = userIntent;
    }

}
