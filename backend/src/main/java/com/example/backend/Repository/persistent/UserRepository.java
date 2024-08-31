package com.example.backend.Repository.persistent;

import com.example.backend.Model.persistent.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<com.example.backend.Model.persistent.User,Long> {
    User findByUsername(String username);

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String username);
}
