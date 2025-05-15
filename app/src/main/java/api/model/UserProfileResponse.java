package api.model;


import java.math.BigDecimal;

public class UserProfileResponse {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private Integer height;

    private BigDecimal weight;
    private BigDecimal waistCircumference;
    private BigDecimal armCircumference;
    private BigDecimal hipCircumference;

    private String token;

    public UserProfileResponse() {
    }

    public UserProfileResponse(Integer id, String email, String firstName, String lastName, String gender,
                               Integer height, BigDecimal weight, BigDecimal waistCircumference,
                               BigDecimal armCircumference, BigDecimal hipCircumference, String token) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.waistCircumference = waistCircumference;
        this.armCircumference = armCircumference;
        this.hipCircumference = hipCircumference;
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getWaistCircumference() {
        return waistCircumference;
    }

    public void setWaistCircumference(BigDecimal waistCircumference) {
        this.waistCircumference = waistCircumference;
    }

    public BigDecimal getArmCircumference() {
        return armCircumference;
    }

    public void setArmCircumference(BigDecimal armCircumference) {
        this.armCircumference = armCircumference;
    }

    public BigDecimal getHipCircumference() {
        return hipCircumference;
    }

    public void setHipCircumference(BigDecimal hipCircumference) {
        this.hipCircumference = hipCircumference;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
