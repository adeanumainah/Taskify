package com.uas.java1.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uas.java1.exception.TidakDitemukan;
import com.uas.java1.model.Task;
import com.uas.java1.model.User;
import com.uas.java1.repository.UserRepository;
import com.uas.java1.service.TaskService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user/tasks")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class UserTaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    // endpoint untuk melihat semua task (umum, seluruh data task)
    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    // endpoint untuk melihat detail task berdasarkan ID
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    // endpoint untuk melihat task yang hanya assigned ke user yang sedang login
    @GetMapping("/my-tasks")
    public List<Task> getMyTasks(Authentication authentication) {
        String username = authentication.getName();

        // cari user berdasarkan username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new TidakDitemukan("User tidak ditemukan: " + username));

        return taskService.getTasksByAssignedUser(user);
    }
}