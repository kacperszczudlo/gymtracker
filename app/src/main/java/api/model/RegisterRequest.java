package api.model;

public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String gender;
    private Integer height;

    private Double weight;
    private Double waistCircumference;
    private Double armCircumference;
    private Double hipCircumference;

    public RegisterRequest() {
    }

    public RegisterRequest(String firstName, String lastName, String email, String password,
                           String gender, Integer height,
                           Double weight, Double waistCircumference,
                           Double armCircumference, Double hipCircumference) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.waistCircumference = waistCircumference;
        this.armCircumference = armCircumference;
        this.hipCircumference = hipCircumference;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getWaistCircumference() {
        return waistCircumference;
    }

    public void setWaistCircumference(Double waistCircumference) {
        this.waistCircumference = waistCircumference;
    }

    public Double getArmCircumference() {
        return armCircumference;
    }

    public void setArmCircumference(Double armCircumference) {
        this.armCircumference = armCircumference;
    }

    public Double getHipCircumference() {
        return hipCircumference;
    }

    public void setHipCircumference(Double hipCircumference) {
        this.hipCircumference = hipCircumference;
    }
}
