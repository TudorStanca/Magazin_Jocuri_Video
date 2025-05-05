package model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "owned_games")
public class OwnedGame implements Identifiable<OwnedGameId> {
    @EmbeddedId
    private OwnedGameId id;

    @MapsId("clientId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @MapsId("gameId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ColumnDefault("0")
    @Column(name = "nr_hours")
    private Integer nrHours;

    @Override
    public OwnedGameId getId() {
        return id;
    }

    public void setId(OwnedGameId id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Integer getNrHours() {
        return nrHours;
    }

    public void setNrHours(Integer nrHours) {
        this.nrHours = nrHours;
    }
}