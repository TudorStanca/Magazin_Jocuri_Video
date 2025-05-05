package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name = "cart")
public class Cart implements Identifiable<CartId> {
    @EmbeddedId
    private CartId id;

    @MapsId("clientId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @MapsId("gameId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    public Cart() {}

    public Cart(Client client, Game game, Instant date) {
        this.id = new CartId(client.getId(), game.getId());
        this.client = client;
        this.game = game;
        this.date = date;
    }

    @Override
    public CartId getId() {
        return id;
    }

    public void setId(CartId id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.id = new CartId(client.getId(), game.getId());
        this.client = client;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.id = new CartId(client.getId(), game.getId());
        this.game = game;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Cart{" +
               "id=" + id +
               ", client=" + client +
               ", game=" + game +
               ", date=" + date +
               '}';
    }
}