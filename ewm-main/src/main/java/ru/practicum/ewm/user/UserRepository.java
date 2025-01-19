package ru.practicum.ewm.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.user.dto.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
            SELECT u
            FROM User u
            WHERE u.id IN :ids
            """)
    List<User> findByIdIn(@Param("ids") List<Long> ids, Pageable pageable);

    boolean existsByEmail(String email);

}
