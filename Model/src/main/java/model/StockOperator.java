package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "stock_operators")
@PrimaryKeyJoinColumn(name = "user_id")
public class StockOperator extends User {
    @Size(max = 255)
    @Column(name = "company")
    private String company;

    @OneToMany(mappedBy = "stockOperator")
    private Set<Game> games = new LinkedHashSet<>();

    public StockOperator() {}

    public StockOperator(String username, byte[] password, byte[] salt, String company) {
        super(username, password, salt);
        this.company = company;
    }

    public StockOperator(String company) {
        this.company = company;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Set<Game> getGames() {
        return games;
    }

    public void setGames(Set<Game> games) {
        this.games = games;
    }

    @Override
    public String toString() {
        return "StockOperator{" +
               "company='" + company + '\'' +
               '}';
    }
}