package com.solutechOne.voyager.securite;

public class JwtResponse {
    private String message;
    private String token;

    private String typeCompte;   // USER / COMPANY
    // seulement si USER

    public JwtResponse() {}

    public JwtResponse(String message, String token, String typeCompte) {
        this.message = message;
        this.token = token;
        this.typeCompte = typeCompte;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTypeCompte() {
        return typeCompte;
    }

    public void setTypeCompte(String typeCompte) {
        this.typeCompte = typeCompte;
    }

}
