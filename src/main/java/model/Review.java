package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "reviews")
public class Review extends EntityId<Long> {
    @NotNull
    @Column(name = "stars", nullable = false)
    @Enumerated(EnumType.STRING)
    private StarRating stars;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    public Review() {}

    public Review(@NotNull StarRating stars, String description, Game game) {
        this.stars = stars;
        this.description = description;
        this.game = game;
    }

    public Review(@NotNull StarRating stars, String description) {
        this.stars = stars;
        this.description = description;
    }

    public StarRating getStars() {
        return stars;
    }

    public void setStars(StarRating stars) {
        this.stars = stars;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public String toString() {
        return "Review{" +
               "stars=" + stars +
               ", description='" + description + '\'' +
               ", game=" + game +
               '}';
    }
}