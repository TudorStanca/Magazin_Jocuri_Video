package model.dto;

import model.StarRating;

public record ReviewDTO (Long id, StarRating starRating, String description, Long gameId) implements RepositoryDTO {

    @Override
    public String toString() {
        return "ReviewDTO{" +
               "id=" + id +
               ", starRating=" + starRating +
               ", description='" + description + '\'' +
               '}';
    }
}
