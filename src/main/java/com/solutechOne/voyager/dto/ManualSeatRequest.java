package com.solutechOne.voyager.dto;

public class ManualSeatRequest {

    private String meansId;
    private String classId;
    private Integer seatOrderNumber;
    private String seatReference;

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

    public Integer getSeatOrderNumber() {
        return seatOrderNumber;
    }

    public void setSeatOrderNumber(Integer seatOrderNumber) {
        this.seatOrderNumber = seatOrderNumber;
    }

    public String getSeatReference() {
        return seatReference;
    }

    public void setSeatReference(String seatReference) {
        this.seatReference = seatReference;
    }
}