package com.company.navcomponentanalyzer.core.model.parser;

public class Block {
    private State value;

    public Block() {
        init();
    }

    public State getState() {
        return value;
    }
    public void init() {
        this.value = State.NOT_STARTED;
    }
    public void setStarted() {
        this.value = State.STARTED;
    }
    public void setFinished() {
        this.value = State.FINISHED;
    }
    public boolean isFinished() {
        return value.equals(State.FINISHED);
    }
    public boolean isNotStarted() {
        return value.equals(State.NOT_STARTED);
    }
}
