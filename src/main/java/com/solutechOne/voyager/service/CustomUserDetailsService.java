package com.solutechOne.voyager.service;

import com.solutechOne.voyager.enums.CompanyStatus;
import com.solutechOne.voyager.enums.PassengerStatus;
import com.solutechOne.voyager.enums.StatutCompte;
import com.solutechOne.voyager.enums.UserStatus;
import com.solutechOne.voyager.model.Company;
import com.solutechOne.voyager.model.Manager;
import com.solutechOne.voyager.model.Passenger;
import com.solutechOne.voyager.repositories.CompanyRepository;
import com.solutechOne.voyager.repositories.ManagerRepository;
import com.solutechOne.voyager.repositories.PassengerRepository;
import com.solutechOne.voyager.repositories.UserRepository;
import com.solutechOne.voyager.securite.CustomUserDetails;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ManagerRepository managerRepository;
    private final PassengerRepository passengerRepository;


    public CustomUserDetailsService(UserRepository userRepository,
                                    CompanyRepository companyRepository,
                                    ManagerRepository managerRepository, PassengerRepository passengerRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.managerRepository = managerRepository;
        this.passengerRepository = passengerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 0) MANAGER
        var managerOpt = managerRepository.findByEmail(email);
        if (managerOpt.isPresent()) {
            Manager m = managerOpt.get();

            if (m.getStatutCompte() != StatutCompte.ACTIF) {
                throw new BadCredentialsException("⚠️ Compte manager " +
                        m.getStatutCompte().name().toLowerCase() + ". Connexion refusée.");
            }

            return new CustomUserDetails(
                    m.getIdManager(),
                    m.getEmail(),
                    m.getPassword(),
                    m.getAuthorities(),
                    m.getNomManager() + " " + (m.getPrenomManager() == null ? "" : m.getPrenomManager()),
                    "MANAGER",
                    m.getStatutCompte().name(),
                    m.getRole().name(),
                    null
            );
        }

        // 1) USER
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            var u = userOpt.get();

            if (u.getStatus() != UserStatus.ACTIF) {
                throw new BadCredentialsException("⚠️ Compte utilisateur " +
                        u.getStatus().name().toLowerCase() + ". Connexion refusée.");
            }

            String compId = (u.getCompany() != null ? u.getCompany().getCompanyId() : null);

            return new CustomUserDetails(
                    String.valueOf(u.getId()),
                    u.getEmail(),
                    u.getPassword(),
                    u.getAuthorities(),
                    u.getName() + " " + (u.getFirstname() == null ? "" : u.getFirstname()),
                    "USER",
                    u.getStatus().name(),
                    u.getRole().name(),
                    compId
            );
        }

        // 2) COMPANY
        var companyOpt = companyRepository.findByEmail(email);
        if (companyOpt.isPresent()) {
            Company c = companyOpt.get();

            if (c.getStatus() != CompanyStatus.ACTIVE) {
                throw new BadCredentialsException("⚠️ Compte compagnie " +
                        c.getStatus().name().toLowerCase() + ". Connexion refusée.");
            }

            return new CustomUserDetails(
                    String.valueOf(c.getCompanyId()),
                    c.getEmail(),
                    c.getPassword(),
                    c.getAuthorities(),
                    c.getName(),
                    "COMPANY",
                    c.getStatus().name(),
                    "COMPANY",
                    null
            );
        }
// 3) PASSENGER
        var passengerOpt = passengerRepository.findByEmail(email);
        if (passengerOpt.isPresent()) {
            Passenger p = passengerOpt.get();

            if (p.getStatus() != PassengerStatus.ACTIF) {
                throw new BadCredentialsException("⚠️ Compte passager " +
                        p.getStatus().name().toLowerCase() + ". Connexion refusée.");
            }

            return new CustomUserDetails(
                    p.getPassengerId(),
                    p.getEmail(),
                    p.getPassword(),
                    p.getAuthorities(),
                    p.getName() + " " + (p.getSurname() == null ? "" : p.getSurname()),
                    "PASSENGER",
                    p.getStatus().name(),
                    p.getRole().name(),
                    null
            );
        }
        throw new UsernameNotFoundException("❌ Aucun compte trouvé pour : " + email);
    }
}
