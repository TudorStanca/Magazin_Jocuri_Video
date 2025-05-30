package model.dto;

import java.math.BigDecimal;
import java.util.List;

public record GameDTO(Long id, String name, String genre, String platform, BigDecimal price, Long stockOperatorId, List<ReviewDTO> reviews) implements RepositoryDTO {

    @Override
    public String toString() {
        return "GameDTO{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", genre='" + genre + '\'' +
               ", platform='" + platform + '\'' +
               ", price=" + price +
               ", stockOperatorId=" + stockOperatorId +
               ", reviews=" + (reviews != null ? reviews.size() : 0) +
               '}';
    }
}
