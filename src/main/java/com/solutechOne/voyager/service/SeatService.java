package com.solutechOne.voyager.service;

import com.solutechOne.voyager.dto.SeatClassRangeRequest;
import com.solutechOne.voyager.dto.SeatRequest;
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

    private String generateSeatReference(int seatOrderNumber) {
        int seatsPerRow = 6;
        int row = (seatOrderNumber - 1) / seatsPerRow + 1;
        int colIndex = (seatOrderNumber - 1) % seatsPerRow;
        char col = (char) ('A' + colIndex);
        return String.format("%02d%c", row, col);
    }

    public Seat createSeat(SeatRequest seatRequest) {
        String meansId = seatRequest.getMeansId();
        String classId = seatRequest.getClassId();
        Seat seat = seatRequest.getSeat();

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

        if (seat.getSeatOrderNumber() == null) {
            int nextOrder = seatRepository.countByMeans_MeansId(meansId) + 1;
            seat.setSeatOrderNumber(nextOrder);
        }

        if (means.getTotalSeat() != null && seat.getSeatOrderNumber() > means.getTotalSeat()) {
            throw new RuntimeException("Impossible: seatOrderNumber dépasse totalSeat (" + means.getTotalSeat() + ")");
        }

        if (seat.getSeatReference() == null || seat.getSeatReference().isBlank()) {
            seat.setSeatReference(generateSeatReference(seat.getSeatOrderNumber()));
        }

        if (seat.getSeatStatus() == null) {
            seat.setSeatStatus(Seat.SeatStatus.ACTIF);
        }

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
    public String generateSeats(String meansId, Integer totalSeats, String classId, SeatGenerationMode mode) {

        if (meansId == null || meansId.isBlank()) {
            throw new RuntimeException("meansId est obligatoire");
        }

        if (mode == null) {
            throw new RuntimeException("mode est obligatoire (RANDOM ou NUMBERED)");
        }

        TransportMeans means = transportMeansRepository.findById(meansId)
                .orElseThrow(() -> new RuntimeException("TransportMeans introuvable : " + meansId));

        TravelClass travelClass = null;
        if (classId != null && !classId.isBlank()) {
            travelClass = travelClassRepository.findById(classId)
                    .orElseThrow(() -> new RuntimeException("TravelClass introuvable : " + classId));
        }

        int maxSeats = totalSeats != null ? totalSeats :
                means.getTotalSeat() != null ? means.getTotalSeat() : 0;

        if (maxSeats <= 0) {
            throw new RuntimeException("totalSeats doit être > 0");
        }

        if (means.getTotalSeat() != null && maxSeats > means.getTotalSeat()) {
            throw new RuntimeException("totalSeats (" + maxSeats + ") dépasse totalSeat (" + means.getTotalSeat() + ")");
        }

        if (mode == SeatGenerationMode.RANDOM) {
            return "Mode RANDOM: aucun siège généré.";
        }

        int created = 0;
        int skipped = 0;

        for (int order = 1; order <= maxSeats; order++) {

            String reference = generateSeatReference(order);

            boolean orderExists = seatRepository.existsByMeans_MeansIdAndSeatOrderNumber(meansId, order);
            boolean referenceExists = seatRepository.existsByMeans_MeansIdAndSeatReference(meansId, reference);

            if (orderExists || referenceExists) {
                skipped++;
                continue;
            }

            Seat seat = new Seat();
            seat.setSeatId(generateSeatId());
            seat.setMeans(means);
            seat.setTravelClass(travelClass);
            seat.setSeatOrderNumber(order);
            seat.setSeatReference(reference);
            seat.setSeatStatus(Seat.SeatStatus.ACTIF);

            seatRepository.save(seat);
            created++;
        }

        return "Génération terminée : " + created + " siège(s) créé(s), " + skipped + " siège(s) déjà existant(s).";
    }

    @Transactional
    public String assignSeatClassesByRange(String meansId, List<SeatClassRangeRequest> ranges) {
        if (meansId == null || meansId.isBlank()) {
            throw new RuntimeException("meansId est obligatoire");
        }

        TransportMeans means = transportMeansRepository.findById(meansId)
                .orElseThrow(() -> new RuntimeException("TransportMeans introuvable : " + meansId));

        if (ranges == null || ranges.isEmpty()) {
            throw new RuntimeException("La liste des plages de classes est obligatoire");
        }

        for (SeatClassRangeRequest range : ranges) {
            if (range.getStartOrder() == null || range.getEndOrder() == null) {
                throw new RuntimeException("startOrder et endOrder sont obligatoires");
            }

            if (range.getClassId() == null || range.getClassId().isBlank()) {
                throw new RuntimeException("classId est obligatoire");
            }

            if (range.getStartOrder() <= 0 || range.getEndOrder() < range.getStartOrder()) {
                throw new RuntimeException("Plage invalide : startOrder/endOrder");
            }

            if (means.getTotalSeat() != null && range.getEndOrder() > means.getTotalSeat()) {
                throw new RuntimeException("La plage dépasse totalSeat (" + means.getTotalSeat() + ")");
            }

            TravelClass travelClass = travelClassRepository.findById(range.getClassId())
                    .orElseThrow(() -> new RuntimeException("TravelClass introuvable : " + range.getClassId()));

            List<Seat> seats = seatRepository.findByMeans_MeansId(meansId)
                    .stream()
                    .filter(seat -> seat.getSeatOrderNumber() != null)
                    .filter(seat -> seat.getSeatOrderNumber() >= range.getStartOrder()
                            && seat.getSeatOrderNumber() <= range.getEndOrder())
                    .toList();

            for (Seat seat : seats) {
                seat.setTravelClass(travelClass);
                seatRepository.save(seat);
            }
        }

        return "Classes des sièges mises à jour avec succès";
    }

    @Transactional
    public Seat createManualSeat(String meansId, String classId, Integer seatOrderNumber, String seatReference) {

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

        if (seatOrderNumber == null || seatOrderNumber <= 0) {
            throw new RuntimeException("seatOrderNumber doit être > 0");
        }

        if (means.getTotalSeat() != null && seatOrderNumber > means.getTotalSeat()) {
            throw new RuntimeException("seatOrderNumber dépasse totalSeat (" + means.getTotalSeat() + ")");
        }

        if (seatReference == null || seatReference.isBlank()) {
            seatReference = generateSeatReference(seatOrderNumber);
        }

        if (seatRepository.existsByMeans_MeansIdAndSeatOrderNumber(meansId, seatOrderNumber)) {
            throw new RuntimeException("seatOrderNumber déjà utilisé pour ce moyen de transport");
        }

        if (seatRepository.existsByMeans_MeansIdAndSeatReference(meansId, seatReference)) {
            throw new RuntimeException("seatReference déjà utilisée pour ce moyen de transport");
        }

        Seat seat = new Seat();
        seat.setSeatId(generateSeatId());
        seat.setMeans(means);
        seat.setTravelClass(travelClass);
        seat.setSeatOrderNumber(seatOrderNumber);
        seat.setSeatReference(seatReference);
        seat.setSeatStatus(Seat.SeatStatus.ACTIF);

        return seatRepository.save(seat);
    }
}