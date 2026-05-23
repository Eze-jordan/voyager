package com.solutechOne.voyager.dto;

import com.solutechOne.voyager.service.SeatService;

public class GenerateSeatRequest {

    private String meansId;
    private Integer totalSeats;
    private String classId;
    private SeatService.SeatGenerationMode mode;

    public String getMeansId() {
        return meansId;
    }

    public void setMeansId(String meansId) {
        this.meansId = meansId;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public SeatService.SeatGenerationMode getMode() {
        return mode;
    }

    public void setMode(SeatService.SeatGenerationMode mode) {
        this.mode = mode;
    }
}