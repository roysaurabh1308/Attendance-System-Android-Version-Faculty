package com.developerdesk9.attendanceregister;

public class Details {
    String enrollment;
    String name;
    String attendance;
    String absent;
    String percent;

    public Details(String enrollment, String name, String attendance, String absent, String percent) {
        this.enrollment = enrollment;
        this.name = name;
        this.attendance = attendance;
        this.absent = absent;
        this.percent = percent;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public String getName() {
        return name;
    }

    public String getAttendance() {
        return attendance;
    }

    public String getAbsent() {
        return absent;
    }

    public String getPercent() {
        return percent;
    }

    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public void setAbsent(String absent) {
        this.absent = absent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }
}
