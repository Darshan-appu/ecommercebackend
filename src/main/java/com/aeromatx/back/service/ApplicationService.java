package com.aeromatx.back.service;



import com.aeromatx.back.dto.application.ApplicationResponseDTO;
import com.aeromatx.back.entity.Application;
import com.aeromatx.back.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;



@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Transactional
    public List<ApplicationResponseDTO> getApplications() {
    // Some logic to create a list of DTOs
    List<ApplicationResponseDTO> dtos = new ArrayList<>();
    // Populate the list...
    return dtos;
}

    public Application save(Application application) {
        return applicationRepository.save(application);
    }

    public List<Application> findAll() {
        return applicationRepository.findAll();
    }

     public Optional<Application> findById(Long id) {
        return applicationRepository.findById(id);
    }

    public boolean existsById(Long id) {
        return applicationRepository.existsById(id);
    }

    public void deleteById(Long id) {
        applicationRepository.deleteById(id);
    }

    
}
