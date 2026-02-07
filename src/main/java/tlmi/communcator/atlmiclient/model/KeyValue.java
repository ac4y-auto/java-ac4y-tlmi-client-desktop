package tlmi.communcator.atlmiclient.model;

public class KeyValue {

    public KeyValue(String key, String value){
        setKey(key);
        setValue(value);
    }

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value;

}