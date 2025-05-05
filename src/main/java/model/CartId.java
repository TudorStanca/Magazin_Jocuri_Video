package model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CartId implements Serializable {
    private static final long serialVersionUID = -1641228678008632355L;
    @NotNull
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @NotNull
    @Column(name = "game_id", nullable = false)
    private Long gameId;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CartId entity = (CartId) o;
        return Objects.equals(this.gameId, entity.gameId) &&
               Objects.equals(this.clientId, entity.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, clientId);
    }
}