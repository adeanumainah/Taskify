package com.uas.java1.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.uas.java1.model.NotifikasiKirimTugas;
import com.uas.java1.model.StatusPengirimanTugas;
import com.uas.java1.repository.NotifikasiKirimTugasRepository;
import com.uas.java1.service.EmailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotifikasiKirimTugasScheduler {

    private final NotifikasiKirimTugasRepository notifikasiKirimTugasRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 8 * * *")
    public void kirimPengingatTugas() {
        LocalDateTime besok = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime akhirBesok = besok.plusDays(1).minusSeconds(1);

        List<NotifikasiKirimTugas> notifBelumTerkirim = notifikasiKirimTugasRepository
                .findByTaskDeadlineBetweenAndStatusPengirimanTugas(besok, akhirBesok, StatusPengirimanTugas.BELUM_TERKIRIM);
                
        for (NotifikasiKirimTugas notif : notifBelumTerkirim) {
            String penerimaEmail = notif.getUser().getEmail();
            String subjek = "Pengingat Tugas";
            String isi = "Hai, Kamu Punya tugas yang belum di kerjakan nih : " + notif.getTask().getTitle();
            try{
                emailService.kirimNotifikasi(penerimaEmail, subjek, isi);
            }catch(Exception error){
                System.out.println("Tidak terkirim");
            }
        }

    }
}