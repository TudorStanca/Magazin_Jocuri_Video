package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "stock_operators")
public class StockOperator extends User {
    @Size(max = 255)
    @Column(name = "company")
    private String company;

    @OneToMany(mappedBy = "stockOperator")
    private Set<Game> games = new LinkedHashSet<>();

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
}