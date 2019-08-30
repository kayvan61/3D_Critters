package assignment5;

public class MailBox<T> {
    private T value;

    public MailBox(T val) {
        value = val;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T s) {
        value = s;
    }

}
