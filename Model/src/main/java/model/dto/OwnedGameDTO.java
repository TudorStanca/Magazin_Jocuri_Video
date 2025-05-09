package model.dto;

public record OwnedGameDTO(GameDTO game, Integer nrHours) {

    @Override
    public String toString() {
        return "OwnedGameDTO{" +
               "game=" + game +
               ", nrHours=" + nrHours +
               '}';
    }
}
