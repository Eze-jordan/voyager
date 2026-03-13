package com.solutechOne.voyager.service;

import com.solutechOne.voyager.model.Seat;
import com.solutechOne.voyager.model.TransportMeans;
import com.solutechOne.voyager.model.TravelClass;
import com.solutechOne.voyager.repositories.SeatRepository;
import com.solutechOne.voyager.repositories.TransportMeansRepository;
import com.solutechOne.voyager.repositories.TravelClassRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SeatService {

    private final SeatRepository seatRepository;
    private final TransportMeansRepository transportMeansRepository;
    private final TravelClassRepository travelClassRepository;

    public SeatService(SeatRepository seatRepository,
                       TransportMeansRepository transportMeansRepository,
                       TravelClassRepository travelClassRepository) {
        this.seatRepository = seatRepository;
        this.transportMeansRepository = transportMeansRepository;
        this.travelClassRepository = travelClassRepository;
    }

    public enum SeatGenerationMode { RANDOM, NUMBERED }

    private String generateSeatId() {
        return "seat-" + UUID.randomUUID();
    }

    // ✅ Bus classique: 6 sièges par rangée (A..F)
    private String generateSeatReference(int seatOrderNumber) {
        int seatsPerRow = 6;
        int row = (seatOrderNumber - 1) / seatsPerRow + 1;
        int colIndex = (seatOrderNumber - 1) % seatsPerRow;
        char col = (char) ('A' + colIndex);
        return String.format("%02d%c", row, col);
    }

    public Seat createSeat(String meansId, String classId, Seat seat) {

        if (meansId == null || meansId.isBlank()) {
            throw new RuntimeException("meansId est obligatoire");
        }

        TransportMeans means = transportMeansRepository.findById(meansId)
                .orElseThrow(() -> new RuntimeException("TransportMeans introuvable : " + meansId));

        TravelClass travelClass = null;
        if (classId != null && !classId.isBlank()) {
            travelClass = travelClassRepository.findById(classId)
                    .orElseThrow(() -> new RuntimeException("TravelClass introuvable : " + classId));
        }

        if (seat.getSeatId() == null) {
            seat.setSeatId(generateSeatId());
        }

        seat.setMeans(means);
        seat.setTravelClass(travelClass);

        // Auto seatOrderNumber si manquant
        if (seat.getSeatOrderNumber() == null) {
            int nextOrder = seatRepository.countByMeans_MeansId(meansId) + 1;
            seat.setSeatOrderNumber(nextOrder);
        }

        // Bloquer si dépasse totalSeat
        if (means.getTotalSeat() != null && seat.getSeatOrderNumber() > means.getTotalSeat()) {
            throw new RuntimeException("Impossible: seatOrderNumber dépasse totalSeat (" + means.getTotalSeat() + ")");
        }

        // Auto seatReference si manquant
        if (seat.getSeatReference() == null || seat.getSeatReference().isBlank()) {
            seat.setSeatReference(generateSeatReference(seat.getSeatOrderNumber()));
        }

        if (seat.getSeatStatus() == null) {
            seat.setSeatStatus(Seat.SeatStatus.ACTIF);
        }

        // Anti-doublons
        if (seatRepository.existsByMeans_MeansIdAndSeatOrderNumber(meansId, seat.getSeatOrderNumber())) {
            throw new RuntimeException("seatOrderNumber déjà utilisé pour ce moyen de transport");
        }
        if (seatRepository.existsByMeans_MeansIdAndSeatReference(meansId, seat.getSeatReference())) {
            throw new RuntimeException("seatReference déjà utilisée pour ce moyen de transport");
        }

        return seatRepository.save(seat);
    }

    public List<Seat> getSeatsByMeansId(String meansId) {
        return seatRepository.findByMeans_MeansId(meansId);
    }

    public List<Seat> getSeatsByClassId(String classId) {
        return seatRepository.findByTravelClass_ClassId(classId);
    }

    public Optional<Seat> getSeatById(String seatId) {
        return seatRepository.findById(seatId);
    }

    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    public Seat updateSeat(String seatId, Seat updatedSeat) {
        return seatRepository.findById(seatId)
                .map(seat -> {
                    if (updatedSeat.getSeatReference() != null) seat.setSeatReference(updatedSeat.getSeatReference());
                    if (updatedSeat.getSeatOrderNumber() != null) seat.setSeatOrderNumber(updatedSeat.getSeatOrderNumber());
                    if (updatedSeat.getSeatStatus() != null) seat.setSeatStatus(updatedSeat.getSeatStatus());
                    return seatRepository.save(seat);
                })
                .orElseThrow(() -> new RuntimeException("Seat introuvable: " + seatId));
    }

    public void deleteSeat(String seatId) {
        seatRepository.deleteById(seatId);
    }

    @Transactional
    public String generateSeats(String meansId, Integer totalSeats, String classId,
                                SeatGenerationMode mode, boolean reset) {

        if (meansId == null || meansId.isBlank()) throw new RuntimeException("meansId est obligatoire");
        if (mode == null) throw new RuntimeException("mode est obligatoire (RANDOM ou NUMBERED)");

        TransportMeans means = transportMeansRepository.findById(meansId)
                .orElseThrow(() -> new RuntimeException("TransportMeans introuvable : " + meansId));

        TravelClass travelClass = null;
        if (classId != null && !classId.isBlank()) {
            travelClass = travelClassRepository.findById(classId)
                    .orElseThrow(() -> new RuntimeException("TravelClass introuvable : " + classId));
        }

        int seatsToCreate = (totalSeats != null) ? totalSeats :
                (means.getTotalSeat() != null ? means.getTotalSeat() : 0);

        if (seatsToCreate <= 0) throw new RuntimeException("totalSeats doit être > 0");

        if (means.getTotalSeat() != null && seatsToCreate > means.getTotalSeat()) {
            throw new RuntimeException("totalSeats (" + seatsToCreate + ") dépasse totalSeat (" + means.getTotalSeat() + ")");
        }

        if (reset && seatRepository.existsByMeans_MeansId(meansId)) {
            seatRepository.deleteByMeans_MeansId(meansId);
        }

        if (mode == SeatGenerationMode.RANDOM) {
            return "Mode RANDOM: aucun siège généré (places non numérotées).";
        }

        int startOrder = seatRepository.countByMeans_MeansId(meansId) + 1;
        int existing = startOrder - 1;

        if (!reset && existing >= seatsToCreate) {
            return "Déjà " + existing + " sièges existants. Aucun siège généré.";
        }

        for (int order = startOrder; order <= seatsToCreate; order++) {
            Seat seat = new Seat();
            seat.setSeatId(generateSeatId());
            seat.setMeans(means);
            seat.setTravelClass(travelClass);
            seat.setSeatOrderNumber(order);
            seat.setSeatReference(generateSeatReference(order));
            seat.setSeatStatus(Seat.SeatStatus.ACTIF);
            seatRepository.save(seat);
        }

        return "Mode NUMBERED: sièges générés jusqu'à " + seatsToCreate + " place(s).";
    }
}