package com.example.backend.Repository.persistent;


import com.example.backend.Model.persistent.RefreshToken;
import com.example.backend.Model.persistent.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByToken(String token);

    RefreshToken findByUser(User user);

    @Transactional
    @Modifying
    void deleteByUser(User user);

    boolean existsByUser(User user);
}
