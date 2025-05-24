package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "games")
public class Game extends EntityId<Long> {
    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 100)
    @Column(name = "genre", length = 100)
    private String genre;

    @Size(max = 100)
    @Column(name = "platform", length = 100)
    private String platform;

    @NotNull
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_operator_id")
    private StockOperator stockOperator;

    @OneToMany(mappedBy = "game")
    private Set<Review> reviews = new LinkedHashSet<>();

    public Game() {}

    public Game(String name, String genre, String platform, BigDecimal price, StockOperator stockOperator) {
        this.name = name;
        this.genre = genre;
        this.platform = platform;
        this.price = price;
        this.stockOperator = stockOperator;
    }

    public Game(String name, String genre, String platform, BigDecimal price) {
        this.name = name;
        this.genre = genre;
        this.platform = platform;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public StockOperator getStockOperator() {
        return stockOperator;
    }

    public void setStockOperator(StockOperator stockOperator) {
        this.stockOperator = stockOperator;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public String toString() {
        return "Game{" +
               "name='" + name + '\'' +
               ", genre='" + genre + '\'' +
               ", platform='" + platform + '\'' +
               ", price=" + price +
               '}';
    }
}