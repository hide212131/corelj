package hide212131.corelj.visitor.vmsim;

public final class ObjectValue {
    private final int generatedBy;
    private final String type;

    public ObjectValue(final int gerenatedBy) {
        this(gerenatedBy, null);
    }

    public ObjectValue(final int gerenatedBy, String type) {
        this.generatedBy = gerenatedBy;
        this.type = type;
    }

    public int getGeneratedBy() {
        return generatedBy;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ObjectValue{" + "generatedBy=" + generatedBy + ", type=" + type + '}';
    }

}
