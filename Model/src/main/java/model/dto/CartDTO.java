package model.dto;

import java.time.Instant;

public record CartDTO(Long clientId, GameDTO game, Instant date) implements RepositoryDTO {

    @Override
    public String toString() {
        return "CartDTO{" +
               "game=" + game +
               ", date=" + date +
               '}';
    }
}
