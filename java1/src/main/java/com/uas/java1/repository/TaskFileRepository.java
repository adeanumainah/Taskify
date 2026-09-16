package com.uas.java1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uas.java1.model.Task;
import com.uas.java1.model.TaskFile;

@Repository
public interface TaskFileRepository extends JpaRepository<TaskFile, Long> {

    List<TaskFile> findByTaskId(Long taskId);
    Optional<TaskFile> findFirstByTaskOrderByUploadedAtDesc(Task tas);
}