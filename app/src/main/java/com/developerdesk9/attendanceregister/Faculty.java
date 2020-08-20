package com.developerdesk9.attendanceregister;

public class Faculty {

    String tname;
    String tid;
    String type;


    public Faculty(){

    }

    public Faculty(String tname, String tid, String type) {
        this.tname = tname;
        this.tid = tid;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
