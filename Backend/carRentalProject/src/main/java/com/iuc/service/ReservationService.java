package com.iuc.service;

import com.iuc.entities.User;
import com.iuc.repository.ReservationRepository;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public boolean existByUser(User user) {
        return reservationRepository.existsByUser(user);
    }
}
