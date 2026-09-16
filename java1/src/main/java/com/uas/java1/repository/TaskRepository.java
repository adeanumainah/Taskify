package com.uas.java1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uas.java1.model.Task;
import com.uas.java1.model.User;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE YEAR(t.deadline) = :tahun AND MONTH(t.deadline) = :bulan")
    List<Task> findAllByDeadlineYearAndMonth(@Param("tahun") int tahun, @Param("bulan") int bulan);

    List<Task> findByAssignedTo(User user);

}