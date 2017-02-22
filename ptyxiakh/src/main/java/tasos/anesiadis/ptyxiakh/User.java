package tasos.anesiadis.ptyxiakh;

import com.google.gson.JsonObject;

public class User {

    private String userId;
    private String username;
    private String firstName;
    private String lastName;

    boolean selected = false;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setUserId(String userId) {

        this.userId = userId;

    }

    public String getUserId() {

        return userId;

    }

    public void setUserName(String username) {

        this.username = username;

    }

    public String getUserName() {

        return username;

    }

    public void setFirstName(String firstName) {

        this.firstName = firstName;

    }

    public String getFirstName() {

        return firstName;

    }

    public void setLastName(String lastName) {

        this.lastName = lastName;

    }

    public String getLastName() {

        return lastName;

    }

    public void parseJsonObject(JsonObject jsonObject) {
        // TODO Auto-generated method stub

        userId = jsonObject.get("UserID").getAsString();
        firstName = jsonObject.get("FirstName").getAsString();
        lastName = jsonObject.get("LastName").getAsString();
        username = jsonObject.get("UserName").getAsString();

    }
}
