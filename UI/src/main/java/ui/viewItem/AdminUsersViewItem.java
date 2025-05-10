package ui.viewItem;

import javafx.beans.property.*;
import model.UserType;

public class AdminUsersViewItem {
    private LongProperty id;
    private StringProperty username;
    private ObjectProperty<UserType> userType;

    public AdminUsersViewItem(Long id, String username, UserType userType) {
        this.id = new SimpleLongProperty(id);
        this.username = new SimpleStringProperty(username);
        this.userType = new SimpleObjectProperty<>(userType);
    }

    public Long getId() {
        return id.get();
    }

    public void setId(Long id) {
        this.id.set(id);
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public UserType getUserType() {
        return userType.get();
    }

    public void setUserType(UserType userType) {
        this.userType.set(userType);
    }

    public LongProperty idProperty() {
        return id;
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public ObjectProperty<UserType> userTypeProperty() {
        return userType;
    }
}
