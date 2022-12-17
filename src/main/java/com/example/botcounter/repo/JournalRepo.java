package com.example.botcounter.repo;

import com.example.botcounter.entity.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalRepo extends JpaRepository<Journal, Long> {
    Journal findByUserIdAndDate(Long userId, String date);
    boolean existsByUserIdAndDate(Long userId, String date);
    int countAllByUserId(Long userId);
    //@Query(value = "DELETE FROM journals j WHERE j.user_id = :userId", nativeQuery = true)
    void deleteAllByUserId(Long userId);
    @Query(value = "SELECT SUM(balance) FROM journals j WHERE j.user_id = :userId", nativeQuery = true)
    Long sumBalanceByUserId(@Param("userId") Long userId);
}
