package vn.edu.tdc.zuke_customer.data_models;

public class Account {
    //Properties
    private int id;
    private String username;
    private String password;
    private int role_id;
    private String status;
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Account(int id, String username, String password, int role_id, String status, String image) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role_id = role_id;
        this.status = status;
        this.image = image;
    }

    public Account() {
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role_id=" + role_id +
                ", status='" + status + '\'' +
                '}';
    }
}