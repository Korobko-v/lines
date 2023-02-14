package ru.korobko.model;

/**
 * Слово
 */
public class Word {

    /**
     * Номер
     */
    private String value;

    /**
     * Позиция в списке
     */
    private int position;

    public Word(String value, int position) {
        this.value = value;
        this.position = position;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
