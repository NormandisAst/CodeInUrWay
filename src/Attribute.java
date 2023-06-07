public class Attribute {
    private String name;
    private String type;
    private String visibility;

    public Attribute(String name, String type, String visibility) {
        this.name = name;
        this.type = type;
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getVisibility() {
        return visibility;
    }
}
