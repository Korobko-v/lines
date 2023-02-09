package ru.korobko.model;

/**
 * Номер телефона
 */
public class PhoneNumber {

    /**
     * Номер
     */
    private String value;

    /**
     * Позиция в списке
     */
    private int position;

    public PhoneNumber(String value, int position) {
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
