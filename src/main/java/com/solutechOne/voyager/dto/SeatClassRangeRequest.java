package com.solutechOne.voyager.dto;

public class SeatClassRangeRequest {
    private Integer startOrder;
    private Integer endOrder;
    private String classId;

    public Integer getStartOrder() {
        return startOrder;
    }

    public void setStartOrder(Integer startOrder) {
        this.startOrder = startOrder;
    }

    public Integer getEndOrder() {
        return endOrder;
    }

    public void setEndOrder(Integer endOrder) {
        this.endOrder = endOrder;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

}