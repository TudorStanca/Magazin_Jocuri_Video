package model.dto;

import java.util.List;

public record StockOperatorDTO (Long id, String username, String password, String company, List<GameDTO> games) implements RepositoryDTO {

    @Override
    public String toString() {
        return "StockOperatorDTO{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", password='" + password + '\'' +
               ", company='" + company + '\'' +
               ", games=" + (games != null ? games.size() : 0) +
               '}';
    }
}
