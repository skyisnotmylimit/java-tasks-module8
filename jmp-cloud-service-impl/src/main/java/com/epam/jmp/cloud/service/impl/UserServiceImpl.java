package com.epam.jmp.cloud.service.impl;

import com.epam.jmp.domain.User;
import com.epam.jmp.service.api.ResourceNotFoundException;
import com.epam.jmp.service.api.UserRepository;
import com.epam.jmp.service.api.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.findById(user.getId())
                .map(existing -> {
                    existing.setName(user.getName());
                    existing.setSurname(user.getSurname());
                    existing.setBirthday(user.getBirthday());
                    return userRepository.save(existing);
                })
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + user.getId()));
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
