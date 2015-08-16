package com.psocha.goclock.time;

public interface PlayerTimeStatus {

    void registerMove();

    void decrementSecond();

    void reset();

    boolean isTimedOut();

    String clockTime();

    String timeExplanation();

    String timeDescription();
}
