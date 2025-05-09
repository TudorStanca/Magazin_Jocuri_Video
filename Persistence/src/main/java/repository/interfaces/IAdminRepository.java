package repository.interfaces;

import model.Admin;
import model.dto.AdminDTO;

import java.util.Optional;

public interface IAdminRepository extends IRepository<Long, Admin, AdminDTO>{
    Optional<AdminDTO> findByUsername(String username);
}
