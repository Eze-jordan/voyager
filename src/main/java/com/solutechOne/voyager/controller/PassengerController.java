package com.solutechOne.voyager.controller;

import com.solutechOne.voyager.model.Passenger;
import com.solutechOne.voyager.service.PassengerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/V1/passengers")
public class PassengerController {

    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    // Créer un passager
    @PostMapping
    public ResponseEntity<Passenger> createPassenger(@RequestBody PassengerRequest request) {
        Passenger createdPassenger = passengerService.createPassenger(
                request.getName(),
                request.getSurname(),
                request.getBirthDate(),
                request.getNationality(),
                request.getGender(),
                request.getEmail(),
                request.getPassword()
        );
        return ResponseEntity.status(201).body(createdPassenger);
    }

    // Obtenir un passager par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Passenger> getPassengerById(@PathVariable("id") String passengerId) {
        Passenger passenger = passengerService.getPassengerById(passengerId);
        return ResponseEntity.ok(passenger);
    }

    // Obtenir un passager par son email
    @GetMapping("/email/{email}")
    public ResponseEntity<Passenger> getPassengerByEmail(@PathVariable("email") String email) {
        Passenger passenger = passengerService.getPassengerByEmail(email);
        return ResponseEntity.ok(passenger);
    }

    // DTO pour la création d'un passager
    public static class PassengerRequest {
        private String name;
        private String surname;
        private String birthDate;
        private String nationality;
        private String gender;
        private String email;
        private String password;

        // Getters et setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}