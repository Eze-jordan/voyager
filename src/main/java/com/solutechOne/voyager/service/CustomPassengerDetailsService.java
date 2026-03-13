package com.solutechOne.voyager.service;

import com.solutechOne.voyager.model.Passenger;
import com.solutechOne.voyager.repositories.PassengerRepository;
import com.solutechOne.voyager.securite.CustomPassengerDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomPassengerDetailsService implements UserDetailsService {

    private final PassengerRepository passengerRepository;

    public CustomPassengerDetailsService(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Passenger passenger = passengerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No passenger found with email: " + email));

        return new CustomPassengerDetails(
                passenger.getPassengerId(),
                passenger.getEmail(),
                passenger.getPassword(),
                passenger.getAuthorities(),
                passenger.getName() + " " + passenger.getSurname(),
                passenger.getStatus(),
                passenger.getRole().name()
        );
    }
}