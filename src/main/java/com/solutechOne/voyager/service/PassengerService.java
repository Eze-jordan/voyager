package com.solutechOne.voyager.service;

import com.solutechOne.voyager.enums.PassengerStatus;
import com.solutechOne.voyager.enums.Role;
import com.solutechOne.voyager.model.Passenger;
import com.solutechOne.voyager.repositories.PassengerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;

    public PassengerService(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    // Créer un passager
    public Passenger createPassenger(String name, String surname, String birthDate, String nationality, String gender, String email, String password) {
        if (passengerRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        Passenger passenger = new Passenger();
        passenger.setName(name);
        passenger.setSurname(surname);
        passenger.setBirthDate(birthDate);
        passenger.setNationality(nationality);
        passenger.setGender(gender);
        passenger.setEmail(email); // Enregistrer l'email
        passenger.setPassword(password); // Hachage du mot de passe avant de l'enregistrer
        passenger.setStatus(PassengerStatus.ACTIF); // S'assurer que le statut est bien défini
        passenger.setRole(Role.PASSENGER); // Assigner un rôle par défaut

        return passengerRepository.save(passenger);
    }

    // Récupérer un passager par son ID
    public Passenger getPassengerById(String passengerId) {
        return passengerRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException("Passenger not found: " + passengerId));
    }

    // Récupérer un passager par son email
    public Passenger getPassengerByEmail(String email) {
        return passengerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Passenger not found with email: " + email));
    }
}