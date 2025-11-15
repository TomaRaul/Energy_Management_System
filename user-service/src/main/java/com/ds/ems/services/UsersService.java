package com.ds.ems.services;


import com.ds.ems.dtos.UsersDTO;
import com.ds.ems.dtos.UsersDetailsDTO;
import com.ds.ems.dtos.builders.UsersBuilder;
import com.ds.ems.handlers.exceptions.model.ResourceNotFoundException;
import com.ds.ems.repositories.UsersRepository;
import com.ds.ems.entities.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsersService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsersService.class);
    private final UsersRepository UsersRepository;

    @Autowired
    public UsersService(UsersRepository UsersRepository) {
        this.UsersRepository = UsersRepository;
    }

    public List<UsersDTO> findUsers() {
        List<Users> UsersList = UsersRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        return UsersList.stream()
                .map(UsersBuilder::toUsersDTO)
                .collect(Collectors.toList());
    }

    public UsersDetailsDTO findUsersById(Integer id) {
        Optional<Users> prosumerOptional = UsersRepository.findById(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("Users with id {} was not found in db", id);
            throw new ResourceNotFoundException(Users.class.getSimpleName() + " with id: " + id);
        }
        return UsersBuilder.toUsersDetailsDTO(prosumerOptional.get());
    }

    public int insert(UsersDetailsDTO UsersDTO) {
        Users Users = UsersBuilder.toEntity(UsersDTO);
        Users = UsersRepository.save(Users);
        LOGGER.debug("Users with id {} was inserted in db", Users.getId());
        return Users.getId();
    }

    public void deleteUser(int id) {
        LOGGER.info("Deleting user with ID: {}", id);

        // verifica ca user-ul exista
        if (!UsersRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }

        // sterge user-ul
        UsersRepository.deleteById(id);
        LOGGER.info("User deleted successfully: {}", id);
    }

    public void updateUser(Integer id, UsersDetailsDTO dto) {
        LOGGER.info("Updating user with ID: {}", id);

        // Gasește user-ul
        Users user = UsersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Actualizeaza campurile
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setRole(dto.getRole());
        user.setAge(dto.getAge());

        // Salveaza
        UsersRepository.save(user);

        LOGGER.info("User updated successfully: {}", id);
    }

}
