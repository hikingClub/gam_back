package com.gam.hikingclub.service;

import com.gam.hikingclub.entity.Modified;
import com.gam.hikingclub.event.ModifiedEvent;
import com.gam.hikingclub.repository.ModifiedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModifiedService {
    private final ModifiedRepository modifiedRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void saveModified(Modified modified) {
        modifiedRepository.save(modified);
        eventPublisher.publishEvent(new ModifiedEvent(this, modified));
    }

}
