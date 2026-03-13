package com.solutechOne.voyager.securite;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static CustomUserDetails currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("Aucun utilisateur authentifié");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails cud) return cud;
        throw new IllegalStateException("Principal non supporté: " + principal.getClass().getName());
    }

    public static String currentCompanyIdOrThrow() {
        CustomUserDetails cud = currentUser();

        // ✅ Si tu as un compte COMPANY, c'est l'id du compte compagnie
        if ("COMPANY".equalsIgnoreCase(cud.getTypeCompte())) {
            return cud.getId();
        }

        // ✅ Si c'est un USER, tu as companyId dans CustomUserDetails
        if ("USER".equalsIgnoreCase(cud.getTypeCompte())) {
            if (cud.getCompanyId() == null) throw new IllegalStateException("User sans companyId");
            return cud.getCompanyId();
        }

        // Manager/super admin → pas de companyId
        throw new IllegalStateException("Ce type de compte n'est pas rattaché à une compagnie");
    }
}
