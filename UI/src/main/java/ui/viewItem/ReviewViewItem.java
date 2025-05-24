package ui.viewItem;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.Review;
import model.StarRating;

public class ReviewViewItem {

    private Long id;
    private ObjectProperty<StarRating> starRating;
    private StringProperty description;

    public ReviewViewItem(Long id, StarRating starRating, String description) {
        this.id = id;
        this.starRating = new SimpleObjectProperty<>(starRating);
        this.description = new SimpleStringProperty(description);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StarRating getStarRating() {
        return starRating.get();
    }

    public void setStarRating(StarRating starRating) {
        this.starRating.set(starRating);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public ObjectProperty<StarRating> starRatingProperty() {
        return starRating;
    }

    public StringProperty descriptionProperty() {
        return description;
    }
}
