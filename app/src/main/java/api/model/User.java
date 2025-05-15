package api.model;

public class User {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private Integer height;

    // Gettery i settery
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
}
