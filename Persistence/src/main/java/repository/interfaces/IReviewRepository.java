package repository.interfaces;

import model.Review;
import model.dto.ReviewDTO;

public interface IReviewRepository extends IRepository<Long, Review, ReviewDTO>{

    Iterable<ReviewDTO> getAllReviewsForGame(Long gameId);
}
