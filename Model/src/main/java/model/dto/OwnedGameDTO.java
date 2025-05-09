package model.dto;

public record OwnedGameDTO(GameDTO game, Integer nrHours) implements RepositoryDTO {

    @Override
    public String toString() {
        return "OwnedGameDTO{" +
               "game=" + game +
               ", nrHours=" + nrHours +
               '}';
    }
}
