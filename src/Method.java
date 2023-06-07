public class Method {
    private String name;
    private String returnType;
    private String visibility;

    public Method(String name, String returnType, String visibility) {
        this.name = name;
        this.returnType = returnType;
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getVisibility() {
        return visibility;
    }
}
