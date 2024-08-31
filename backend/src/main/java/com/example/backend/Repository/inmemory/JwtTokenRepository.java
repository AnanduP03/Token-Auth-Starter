package com.example.backend.Repository.inmemory;

import com.example.backend.Model.inmemory.JwtToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtTokenRepository extends CrudRepository<JwtToken,Long> {
    JwtToken findByUsername(String username);

    boolean existsByToken(String token);
    boolean existsByUsername(String username);

    @Transactional
    @Modifying
    void deleteByToken(String token);
}
