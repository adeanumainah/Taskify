package com.uas.java1.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uas.java1.model.NotifikasiKirimTugas;
import com.uas.java1.model.StatusPengirimanTugas;

public interface NotifikasiKirimTugasRepository extends JpaRepository<NotifikasiKirimTugas, Long> {
    List<NotifikasiKirimTugas> findByTaskDeadlineBetweenAndStatusPengirimanTugas(LocalDateTime awal, LocalDateTime akhir, StatusPengirimanTugas status);

}