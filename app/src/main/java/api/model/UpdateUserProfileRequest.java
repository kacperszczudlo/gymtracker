package api.model;

public class UpdateUserProfileRequest {
    private String gender;
    private Integer height;
    private Double weight;
    private Double waistCircumference;
    private Double armCircumference;
    private Double hipCircumference;

    public UpdateUserProfileRequest(String gender, Integer height, Double weight,
                                    Double waistCircumference, Double armCircumference, Double hipCircumference) {
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.waistCircumference = waistCircumference;
        this.armCircumference = armCircumference;
        this.hipCircumference = hipCircumference;
    }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getWaistCircumference() { return waistCircumference; }
    public void setWaistCircumference(Double waistCircumference) { this.waistCircumference = waistCircumference; }

    public Double getArmCircumference() { return armCircumference; }
    public void setArmCircumference(Double armCircumference) { this.armCircumference = armCircumference; }

    public Double getHipCircumference() { return hipCircumference; }
    public void setHipCircumference(Double hipCircumference) { this.hipCircumference = hipCircumference; }
}
