package com.developerdesk9.attendanceregister;

public class Faculty {

    String tname;
    String tid;


    public Faculty(){

    }

    public Faculty(String tname, String tid) {
        this.tname = tname;
        this.tid = tid;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
