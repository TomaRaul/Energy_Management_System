package com.ds.ems.services;

import com.ds.ems.dtos.UserCreatedEvent;
import com.ds.ems.entities.UserInfo;
import com.ds.ems.repositories.UserInfoRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
//@RequiredArgsConstructor
//@Slf4j
public class UserSyncService {

    private final UserInfoRepository userInfoRepository;
    private static final Logger log = LoggerFactory.getLogger(UserSyncService.class);

    public UserSyncService(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    @Transactional
    public void handleUserCreated(UserCreatedEvent event) {
        try {
            log.info("Received user created event for user ID: {}", event.getUserId());

            // verifica user existent
            if (userInfoRepository.existsByUserId(event.getUserId())) {
                log.info("User {} already exists in device database, skipping", event.getUserId());
                return;
            }

            // inregistrare user nou
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(event.getUserId());

            userInfoRepository.save(userInfo);

            log.info("User {} synchronized successfully in device database", event.getUserId());

        } catch (Exception e) {
            log.error("Failed to synchronize user {}", event.getUserId(), e);
            throw e; // Re-throw pentru retry in Rabbit
        }
    }
}
