package ui.viewItem;

import javafx.beans.property.*;

import java.math.BigDecimal;

public class GameViewItem {
    private Long id;
    private StringProperty name;
    private StringProperty genre;
    private StringProperty platform;
    private ObjectProperty<BigDecimal> price;
    private IntegerProperty nrHours;

    public GameViewItem(Long id, String name, String genre, String platform, BigDecimal price, Integer nrHours) {
        this(id, name, genre, platform, price);
        this.nrHours.set(nrHours);
    }

    public GameViewItem(Long id, String name, String genre, String platform, BigDecimal price) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.genre = new SimpleStringProperty(genre);
        this.platform = new SimpleStringProperty(platform);
        this.price = new SimpleObjectProperty<>(price);
        this.nrHours = new SimpleIntegerProperty(0);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getGenre() {
        return genre.get();
    }

    public void setGenre(String genre) {
        this.genre.set(genre);
    }

    public String getPlatform() {
        return platform.get();
    }

    public void setPlatform(String platform) {
        this.platform.set(platform);
    }

    public BigDecimal getPrice() {
        return price.get();
    }

    public void setPrice(BigDecimal price) {
        this.price.set(price);
    }

    public Integer getNrHours() {
        return nrHours.get();
    }

    public void setNrHours(Integer nrHours) {
        this.nrHours.set(nrHours);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty genreProperty() {
        return genre;
    }

    public StringProperty platformProperty() {
        return platform;
    }

    public ObjectProperty<BigDecimal> priceProperty() {
        return price;
    }

    public IntegerProperty nrHoursProperty() {
        return nrHours;
    }
}
