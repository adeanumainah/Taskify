package com.uas.java1.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.uas.java1.dto.TaskDto;
import com.uas.java1.exception.TidakDitemukan;
import com.uas.java1.model.Task;
import com.uas.java1.model.User;
import com.uas.java1.repository.TaskRepository;
import com.uas.java1.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task tidak ditemukan"));
    }

    public Task createTask(TaskDto taskDto) {
        // assignedTo tidak boleh null
        if (taskDto.getAssignedTo() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field 'assigned_to' wajib diisi");
        }

        // assignedTo tidak boleh 0, 1, atau negatif
        if (taskDto.getAssignedTo() <= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Field 'assigned_to' tidak boleh bernilai 1, 0, atau angka negatif");
        }

        // title dan description tidak boleh sama
        if (taskDto.getTitle().trim().equalsIgnoreCase(taskDto.getDescription().trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Title dan Description tidak boleh sama persis");
        }

        // pastikan user dengan ID tersebut ada
        User assignedUser = userRepository.findById(taskDto.getAssignedTo())
                .orElseThrow(() -> new TidakDitemukan("User dengan ID " + taskDto.getAssignedTo() + " tidak ditemukan"));

        Task newTask = Task.builder()
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .assignedTo(assignedUser)
                .deadline(taskDto.getDeadline())
                .status("In-Progress")
                .build();

        return taskRepository.save(newTask);
    }

    public Task updateTask(Long id, Task updatedTask) {
        Task existingTask = getTaskById(id);

        // validasi hanya jika status masih In-Progress
        if (!"In-Progress".equalsIgnoreCase(existingTask.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Task hanya bisa diubah jika statusnya masih 'In-Progress'");
        }

        // update description
        existingTask.setDescription(updatedTask.getDescription());

        // validasi dan update deadline
        if (updatedTask.getDeadline() != null && !updatedTask.getDeadline().isEqual(existingTask.getDeadline())) {
            LocalDate today = LocalDate.now();
            long daysToDeadline = ChronoUnit.DAYS.between(today, existingTask.getDeadline());

            if (daysToDeadline >= 3) {
                if (updatedTask.getDeadline().isBefore(existingTask.getDeadline().minusDays(1))) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Deadline hanya boleh dimajukan maksimal H-2 dari deadline sebelumnya");
                }
                existingTask.setDeadline(updatedTask.getDeadline());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Deadline tidak bisa diubah karena sudah kurang dari 3 hari");
            }
        }

        return taskRepository.save(existingTask);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task tidak ditemukan"));
        taskRepository.delete(task);
    }

    public List<Task> getTasksByAssignedUser(User user) {
        return taskRepository.findByAssignedTo(user);
    }
}
