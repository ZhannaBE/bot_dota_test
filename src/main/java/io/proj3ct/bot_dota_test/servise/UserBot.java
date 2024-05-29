package io.proj3ct.bot_dota_test.servise;

public class UserBot {
    private long id;
    private int currentQuestion;
    private int score;
    private boolean isReady;

    UserBot(long id){
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public int getCurrentQuestion() {
        return currentQuestion;
    }

    public  void setCurrentQuestion(int currentQuestion){
        this.currentQuestion= currentQuestion;
    }

    public boolean isReady() {
        return isReady;
    }
    public void setReady(boolean ready){
        isReady=ready;
    }
    public void increaseScore(){
        score++;
    }
    public int getScore(){
        return score;
    }
}

