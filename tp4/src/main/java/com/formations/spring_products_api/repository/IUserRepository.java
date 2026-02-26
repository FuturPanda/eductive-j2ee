package com.formations.spring_products_api.repository;

import com.formations.spring_products_api.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	@Query("SELECT u FROM User u WHERE u.email = :email AND u.deleted = false")
	Optional<User> findOneByEmailAndDeletedFalse(@Param("email") String email);

	boolean existsByEmail(String email);
}
