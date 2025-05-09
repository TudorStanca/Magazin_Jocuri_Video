package model.dto;

import java.time.Instant;

public record CartDTO(GameDTO game, Instant date) {

    @Override
    public String toString() {
        return "CartDTO{" +
               "game=" + game +
               ", date=" + date +
               '}';
    }
}
