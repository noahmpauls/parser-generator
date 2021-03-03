package com.noahmpauls.compilers.generator;

import java.util.Objects;

public class Action {

    public enum ActionType {
        SHIFT,
        REDUCE,
        GOTO,
        ACCEPT,
        ERROR
    }

    public final ActionType type;
    public final int value;

    Action(ActionType type, int value) {
        this.type = type;
        this.value = value;
    }

    public static Action shift(int value) {
        return new Action(ActionType.SHIFT, value);
    }

    public static Action reduce(int value) {
        return new Action(ActionType.REDUCE, value);
    }

    public static Action goTo(int value) {
        return new Action(ActionType.GOTO, value);
    }

    public static Action accept() {
        return new Action(ActionType.ACCEPT, 0);
    }

    public static Action error() {
        return new Action(ActionType.ERROR, -1);
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof Action && sameValue((Action) that);
    }

    private boolean sameValue(Action that) {
        return this.value == that.value
                && this.type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        switch(type){
            case SHIFT:
                return "s" + value;
            case REDUCE:
                return "r" + value;
            case GOTO:
                return "g" + value;
            case ACCEPT:
                return "acc";
            case ERROR:
                return "";
            default:
                return "???";
        }
    }
}
