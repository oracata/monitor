package com.oracat.model;

public class Machine {


    private String ip ;



    private boolean connectstatu=false;
    private String memery="unkown";
    private String cpu="unkown";
    private String network="unkown";
    private String disc="unkown";
    private String io="unkown";



    public boolean isConnectstatu() {
        return connectstatu;
    }

    public void setConnectstatu(boolean connectstatu) {
        this.connectstatu = connectstatu;
    }

    public String  getMemery()
    {
        return this.memery;
    }

    public void setMemery(String memery)
    {
        this.memery=memery.replace(" ", "*");


    }



    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public String  getCPU()
    {
        return this.cpu;
    }

    public void setCPU(String cpu)
    {
        this.cpu=cpu.replace(" ", "*");


    }


    public String  getNetwork()
    {
        return this.network;
    }

    public void setNetwork(String network)
    {
        this.network=network.replace(" ", "*");

    }


    public String  getDisc()
    {
        return this.disc;
    }

    public void setDisc(String disc)
    {

        this.disc=disc.replace(" ", "*");

    }

    public String  getIo()
    {
        return this.io;
    }

    public void setIo(String io)
    {
        this.io=io.replace(" ", "*");

    }

    public void init() {
        // TODO Auto-generated method stub
        this.memery="unkown";
        this.cpu="unkown";
        this.network="unkown";
        this.disc="unkown";
        this.io="unkown";

    }
}
