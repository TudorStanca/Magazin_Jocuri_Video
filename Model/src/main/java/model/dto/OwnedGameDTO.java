package model.dto;

public record OwnedGameDTO(Long clientId, GameDTO game, Integer nrHours) implements RepositoryDTO {

    @Override
    public String toString() {
        return "OwnedGameDTO{" +
               "game=" + game +
               ", nrHours=" + nrHours +
               '}';
    }
}
