package br.com.metragemrio.model;

public class Meterage {

    public static final String TABLE_NAME = "meterage";
    public static final String STATUS = "status";
    public static final String TIMESTAMP = "timestamp";
    public static final String LEVEL = "level";

    private String status;
    private long timestamp;
    private float level;
    private Dams dams;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
    }

    public Dams getDams() {
        return dams;
    }

    public void setDams(Dams dams) {
        this.dams = dams;
    }
}
