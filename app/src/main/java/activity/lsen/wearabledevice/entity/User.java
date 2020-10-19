package activity.lsen.wearabledevice.entity;
import java.io.Serializable;

public class User implements Serializable{
//public class User{
//
    static final long serialVersionUID=3035211517434472581L;
    private int id;
    private String type;
    private String userName;
    private String userPassword;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }





}
