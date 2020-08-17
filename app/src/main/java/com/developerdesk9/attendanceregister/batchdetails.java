package com.developerdesk9.attendanceregister;

public class batchdetails {
    String sid;
    String sname;

    public batchdetails(String sid, String sname) {
        this.sid = sid;
        this.sname = sname;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }
}
