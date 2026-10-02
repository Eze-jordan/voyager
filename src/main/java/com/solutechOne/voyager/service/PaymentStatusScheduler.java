package com.solutechOne.voyager.service;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PaymentStatusScheduler {

    private static final int MAX_ATTEMPTS = 3;
    private static final long DELAY_SECONDS = 30;

    private final TaskScheduler taskScheduler;
    private final PaymentStatusChecker paymentStatusChecker;

    public PaymentStatusScheduler(
            TaskScheduler taskScheduler,
            PaymentStatusChecker paymentStatusChecker
    ) {
        this.taskScheduler = taskScheduler;
        this.paymentStatusChecker = paymentStatusChecker;
    }

    public void scheduleStatusChecks(String paymentId) {
        scheduleCheck(paymentId, 1);
    }

    private void scheduleCheck(
            String paymentId,
            int attempt
    ) {

        taskScheduler.schedule(
                () -> executeCheck(paymentId, attempt),
                Instant.now().plusSeconds(DELAY_SECONDS)
        );
    }

    private void executeCheck(
            String paymentId,
            int attempt
    ) {

        try {

            System.out.println(
                    "PAYMENT STATUS CHECK "
                            + attempt
                            + "/"
                            + MAX_ATTEMPTS
                            + " - paymentId="
                            + paymentId
            );

            boolean finished =
                    paymentStatusChecker.check(paymentId);

            // SUCCESS / FAILED / CANCELLED / EXPIRED
            if (finished) {

                System.out.println(
                        "PAYMENT STATUS FINAL - paymentId="
                                + paymentId
                );

                return;
            }

            // Encore PENDING
            if (attempt < MAX_ATTEMPTS) {

                scheduleCheck(
                        paymentId,
                        attempt + 1
                );

            } else {

                System.out.println(
                        "PAYMENT STATUS STILL PENDING AFTER "
                                + MAX_ATTEMPTS
                                + " CHECKS - paymentId="
                                + paymentId
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "PAYMENT STATUS CHECK ERROR "
                            + attempt
                            + "/"
                            + MAX_ATTEMPTS
                            + " - paymentId="
                            + paymentId
                            + " - "
                            + e.getMessage()
            );

            // Une erreur réseau à 30 secondes ne doit pas
            // empêcher la vérification à 60 secondes.
            if (attempt < MAX_ATTEMPTS) {

                scheduleCheck(
                        paymentId,
                        attempt + 1
                );
            }
        }
    }
}