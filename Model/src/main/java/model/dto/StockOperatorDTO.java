package model.dto;

import java.util.List;

public class StockOperatorDTO extends UserDTO {

    private String company;
    private List<GameDTO> games;

    public StockOperatorDTO(Long id, String username, byte[] password, byte[] salt,
                            String company, List<GameDTO> games) {
        super(id, username, password, salt);
        this.company = company;
        this.games = games;
    }

    public String getCompany() {
        return company;
    }

    public List<GameDTO> getGames() {
        return games;
    }

    @Override
    public String toString() {
        return "StockOperatorDTO{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", company='" + company + '\'' +
               ", games=" + (games != null ? games.size() : 0) +
               '}';
    }
}
