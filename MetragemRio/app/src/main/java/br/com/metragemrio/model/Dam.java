package br.com.metragemrio.model;

public class Dam {

    public static final String TYPE_ITUPORANGA = "ituporanga";
    public static final String TYPE_TAIO = "taio";

    public static final String TABLE_NAME = "dam";
    public static final String CAPACITY = "capacity";
    public static final String TOTAL = "total";
    public static final String OPEN = "open";
    public static final String CLOSED = "closed";
    public static final String NAME = "name";
    public static final String METERAGE_ID = "meterage_id";

    private String capacity;
    private float total;
    private int open;
    private int closed;
    private String name;
    private long meterage_id;

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getClosed() {
        return closed;
    }

    public void setClosed(int closed) {
        this.closed = closed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMeterage_id() {
        return meterage_id;
    }

    public void setMeterage_id(long meterage_id) {
        this.meterage_id = meterage_id;
    }
}
