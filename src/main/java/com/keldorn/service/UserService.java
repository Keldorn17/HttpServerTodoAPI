package com.keldorn.service;

import com.keldorn.domain.entity.Todo;
import com.keldorn.domain.entity.User;
import com.keldorn.repository.UserRepository;
import jakarta.persistence.EntityManager;

import java.util.List;

public class UserService {
    private final UserRepository userRepo;

    public UserService(EntityManager em) {
        this.userRepo = new UserRepository(em);
    }

    public User findById(int userId) {
        return userRepo.findById(userId);
    }

    public List<Todo> findTodosByUserId(int userId) {
        return userRepo.findTodosByUserId(userId);
    }

    public void delete(User user) {
        userRepo.delete(user);
    }

    public void save(User user) {
        userRepo.save(user);
    }
}
