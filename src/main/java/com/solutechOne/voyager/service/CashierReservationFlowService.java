package com.solutechOne.voyager.service;

import com.solutechOne.voyager.enums.BasketStatus;
import com.solutechOne.voyager.enums.Sexe;
import com.solutechOne.voyager.enums.UserRole;
import com.solutechOne.voyager.enums.UserStatus;
import com.solutechOne.voyager.model.Basket;
import com.solutechOne.voyager.model.Reservation;
import com.solutechOne.voyager.model.Travel;
import com.solutechOne.voyager.model.User;
import com.solutechOne.voyager.repositories.BasketRepository;
import com.solutechOne.voyager.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

    @Service
    @Transactional
    public class CashierReservationFlowService {

        private final UserRepository userRepository;
        private final BasketRepository basketRepository;
        private final TravelService travelService;
        private final ReservationService reservationService;
        private final BasketService basketService;

        public CashierReservationFlowService(
                UserRepository userRepository,
                BasketRepository basketRepository,
                TravelService travelService,
                ReservationService reservationService,
                BasketService basketService
        ) {
            this.userRepository = userRepository;
            this.basketRepository = basketRepository;
            this.travelService = travelService;
            this.reservationService = reservationService;
            this.basketService = basketService;
        }

        /*
         * =====================================================
         * 1. OUVERTURE DU PANIER PAR UN CAISSIER
         * =====================================================
         */

        public Basket openBasket(
                String cashierId,
                String buyerPhone,
                String buyerWhatsapp,
                String buyerEmail
        ) {
            User cashier = requireActiveCashier(cashierId);

            Basket basket = new Basket();

            /*
             * La compagnie n'est pas envoyée par le frontend.
             * Elle est récupérée depuis le compte du caissier.
             */
            basket.setCompany(cashier.getCompany());

            /*
             * On conserve l'utilisateur ayant ouvert le panier.
             */
            basket.setCashier(cashier);

            basket.setBuyerPhone(normalize(buyerPhone));
            basket.setBuyerWhatsapp(normalize(buyerWhatsapp));
            basket.setBuyerEmail(normalize(buyerEmail));

            basket.setNumberOfReservations(0);
            basket.setBasketStatus(BasketStatus.EN_COURS);

            return basketRepository.save(basket);
        }

        /*
         * =====================================================
         * 2. AJOUT D'UN VOYAGE AU PANIER DU CAISSIER
         * =====================================================
         */

        public Travel addTravel(
                String cashierId,
                String basketId,
                String departureId,
                String arrivalId
        ) {
            requireEditableBasketForCashier(cashierId, basketId);

            /*
             * On utilise la méthode déjà existante.
             * Le nouveau service ajoute seulement le contrôle du caissier.
             */
            return travelService.create(
                    basketId,
                    departureId,
                    arrivalId
            );
        }

        /*
         * =====================================================
         * 3. CRÉATION D'UNE RÉSERVATION PAR LE CAISSIER
         * =====================================================
         */

        public Reservation addReservation(
                String cashierId,
                String travelId,
                String passengerName,
                String passengerFirstname,
                LocalDate passengerDateOfBirth,
                Sexe passengerSex,
                String passengerNationality,
                String passengerMail,
                String passengerPhone,
                String passengerWhatsapp,
                String ticketId
        ) {
            Travel travel = travelService.getById(travelId);

            if (travel.getBasket() == null) {
                throw new IllegalStateException(
                        "Le voyage n'est associé à aucun panier"
                );
            }

            requireEditableBasketForCashier(
                    cashierId,
                    travel.getBasket().getBasketId()
            );

            /*
             * On utilise la méthode de réservation existante.
             */
            return reservationService.createReservation(
                    travelId,
                    passengerName,
                    passengerFirstname,
                    passengerDateOfBirth,
                    passengerSex,
                    passengerNationality,
                    passengerMail,
                    passengerPhone,
                    passengerWhatsapp,
                    ticketId
            );
        }

        /*
         * =====================================================
         * 4. DÉMARRAGE DU PAIEMENT PAR LE CAISSIER
         * =====================================================
         */

        public Basket startPayment(
                String cashierId,
                String basketId,
                String paymentService,
                String paymentId,
                String paymentAccount
        ) {
            Basket basket = requireEditableBasketForCashier(
                    cashierId,
                    basketId
            );

            if (basket.getNumberOfReservations() == null
                    || basket.getNumberOfReservations() <= 0) {
                throw new IllegalStateException(
                        "Le panier ne contient aucune réservation"
                );
            }

            /*
             * La méthode existante recalcule le panier et le place
             * en PAYMENT_PENDING.
             */
            return basketService.markPaymentPending(
                    basketId,
                    paymentService,
                    paymentId,
                    paymentAccount
            );
        }

        /*
         * =====================================================
         * 5. CONSULTATION DES PANIERS D'UN CAISSIER
         * =====================================================
         */

        @Transactional(readOnly = true)
        public List<Basket> getBasketsByCashier(String cashierId) {
            requireActiveCashier(cashierId);

            return basketRepository.findByCashier_Id(cashierId);
        }

        /*
         * =====================================================
         * 6. CONSULTATION D'UN PANIER DU CAISSIER
         * =====================================================
         */

        @Transactional(readOnly = true)
        public Basket getBasketByCashier(
                String cashierId,
                String basketId
        ) {
            requireActiveCashier(cashierId);

            Basket basket = basketRepository.findById(basketId)
                    .orElseThrow(() -> new RuntimeException(
                            "Basket not found: " + basketId
                    ));

            validateBasketCashier(basket, cashierId);

            return basket;
        }

        /*
         * =====================================================
         * CONTRÔLES
         * =====================================================
         */

        private User requireActiveCashier(String cashierId) {
            if (cashierId == null || cashierId.isBlank()) {
                throw new IllegalArgumentException(
                        "Cashier ID is required"
                );
            }

            User cashier = userRepository.findById(cashierId)
                    .orElseThrow(() -> new RuntimeException(
                            "Cashier not found: " + cashierId
                    ));

            /*
             * On ne force pas le rôle lors de la création du User.
             * On vérifie simplement ici que l'utilisateur qui lance
             * le parcours de caisse possède bien le rôle CAISSE.
             */
            if (cashier.getRole() != UserRole.CAISSE) {
                throw new IllegalStateException(
                        "L'utilisateur " + cashierId
                                + " ne possède pas le rôle CAISSE"
                );
            }

            if (cashier.getStatus() != UserStatus.ACTIF) {
                throw new IllegalStateException(
                        "Le compte caisse est inactif"
                );
            }

            if (cashier.getCompany() == null) {
                throw new IllegalStateException(
                        "Le compte caisse n'est associé à aucune compagnie"
                );
            }

            return cashier;
        }

        private Basket requireEditableBasketForCashier(
                String cashierId,
                String basketId
        ) {
            requireActiveCashier(cashierId);

            Basket basket = basketRepository.findById(basketId)
                    .orElseThrow(() -> new RuntimeException(
                            "Basket not found: " + basketId
                    ));

            validateBasketCashier(basket, cashierId);

            if (basket.getBasketStatus() != BasketStatus.EN_COURS) {
                throw new IllegalStateException(
                        "Le panier ne peut plus être modifié. Statut actuel : "
                                + basket.getBasketStatus()
                );
            }

            return basket;
        }

        private void validateBasketCashier(
                Basket basket,
                String cashierId
        ) {
            if (basket.getCashier() == null) {
                throw new IllegalStateException(
                        "Le panier n'est associé à aucun caissier"
                );
            }

            if (!cashierId.equals(basket.getCashier().getId())) {
                throw new IllegalStateException(
                        "Ce panier n'appartient pas au caissier "
                                + cashierId
                );
            }
        }

        /*
         * =====================================================
         * UTILITAIRE
         * =====================================================
         */

        private String normalize(String value) {
            return value == null || value.isBlank()
                    ? null
                    : value.trim();
        }
    }

