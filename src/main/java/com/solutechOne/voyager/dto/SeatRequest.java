package com.solutechOne.voyager.dto;

import com.solutechOne.voyager.model.Seat;

public class SeatRequest {
    private String meansId;
    private String classId;
    private Seat seat;  // Le siège à créer

    // Getters et setters
    public String getMeansId() {
        return meansId;
    }

    public void setMeansId(String meansId) {
        this.meansId = meansId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }
}
