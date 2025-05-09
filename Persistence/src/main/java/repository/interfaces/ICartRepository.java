package repository.interfaces;

import model.Cart;
import model.CartId;
import model.dto.CartDTO;

public interface ICartRepository extends IRepository<CartId, Cart, CartDTO> {
}
