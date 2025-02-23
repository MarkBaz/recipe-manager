package com.recipeapp.recipemanager.repository;

import com.recipeapp.recipemanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query(value = """
    SELECT * FROM users u
    WHERE LOWER(u.firstname) LIKE LOWER(CONCAT(:keyword, '%'))
    OR LOWER(u.firstname) LIKE LOWER(CONCAT('% ', :keyword, '%'))
    OR LOWER(u.lastname) LIKE LOWER(CONCAT(:keyword, '%'))
    OR LOWER(u.lastname) LIKE LOWER(CONCAT('% ', :keyword, '%'))
    """, nativeQuery = true)
    List<User> searchUsers(@Param("keyword") String keyword);

    boolean existsByEmail(String email);
}
