package com.oracat.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;


import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.oracat.TomcatStarter;
import com.oracat.model.Machine;


public class MachineInfo {

    private Machine info=new Machine();
    private HashMap<String,String> hashMap;
    public MachineInfo(HashMap<String,String> hashMapXML) throws IOException, InterruptedException{
        // TODO Auto-generated constructor stub

        hashMap=hashMapXML;



        Connection con=new Connection(hashMapXML.get("ip"));

        try{
            con.connect();
            info.setConnectstatu(true);
        }
        catch(IOException e)
        {
            // 输出异常详细信息
            e.printStackTrace();
            //初始化
            info.init();
            info.setConnectstatu(false);
            System.out.println("connect fail! ");
            TomcatStarter.logger.info(hashMapXML.get("ip")+" connect fail!");
            con.close();
            //结束，返回
            return;
        }

        PwDecoder depassword= new PwDecoder(hashMapXML.get("password"));
        try{
            if(con.authenticateWithPassword(hashMapXML.get("username"),depassword.getPassword()))
            {

                TomcatStarter.logger.info(hashMapXML.get("ip")+" connect success!");

                Session	 sesMemery=con.openSession();
                info.setMemery(this.getMemery(sesMemery));
                sesMemery.close();

                Session	 sesCPU=con.openSession();
                info.setCPU(this.getCPU(sesCPU));
                sesCPU.close();

                Session	 sesDisc=con.openSession();
                info.setDisc(this.getDisc(sesDisc));
                sesDisc.close();

                Session	 sesIo=con.openSession();
                info.setIo(this.getIo(sesIo));
                sesIo.close();

               /*
                Session	 sesNetwork=con.openSession();
                info.setNetwork(this.getNetwork(sesNetwork));
                sesNetwork.close();
             */

                con.close();

            }
            else
            {
                info.setConnectstatu(false);

                System.out.println(hashMapXML.get("ip")+" authenticate fail!");
                TomcatStarter.logger.info(hashMapXML.get("ip")+" authenticate fail!");

                con.close();

            }
        }
        finally
        {

            con.close();
        }




    }


    private String getMemery(Session ses) throws IOException
    {
        String shell="";



        if(hashMap.get("jvm")!=null)
        {
            shell="free -g |grep Mem|awk '{ printf(\"%d\t%d\t%d\", $2,$3,$3*100/$2)}'|awk '{print $1\"G total,\",$2\"G used,\",$3\"%used\"}';free -g |grep Mem|awk '{ printf(\"%d\t%d\", $6+$7,($2-$4-$6-$7)*100/$2)}'|awk '{print $1\"G buffers/cache,\",$2\"% real used\"}';free -g |grep Swap|awk '{printf(\"%d\t%.2f\",$2,$3/$2)}'|awk '{print \"Swap:\"$1\"G,\",$2\"%used \"}'";

            String[]	jvmClass=hashMap.get("jvm").split("\\|");


            for(int i=0;i<jvmClass.length;i++)
            {

                shell=shell+";echo \"class:"+jvmClass[i]+"\"";
                shell=shell+";jstat -gccapacity `ps -ef |grep "+jvmClass[i]+" |grep -v grep|awk '{print $2}'` |sed '1d'|awk '{print \" heap MAX:\"($2+$8)/1024\"M\",\"PermGen MAX:\"$12/1024\"M,\",\"heap size:\"($3+$9)/1024\"M,\",\"PermGen size:\"$13/1024\"M\"}' ;jstat -gc `ps -ef |grep "+jvmClass[i]+" |grep -v grep|awk '{print $2}'`|sed '1d'|awk  '{printf(\"%.2f\t%.2f\",($3+$4+$6+$8)/1024,$10/1024)}'|awk '{print \" heap used:\"$1\"M,\",\"PermGen used:\"$2\"M\"}';jstat -gcutil `ps -ef |grep "+jvmClass[i]+" |grep -v grep|awk '{print $2}'`|sed  '1d'|awk '{print \" s0:\"$1\"%,\",\"S1:\"$2\"%,\",\"Eden:\"$3\"%,\",\"Old:\"$4\"%,\",\"Perm:\"$5\"% \"}'";


            }
        }
        else
        {
            //linux内存情况
            shell="free -g |grep Mem|awk '{ printf(\"%d\t%d\t%d\", $2,$3,$3*100/$2)}'|awk '{print $1\"G total,\",$2\"G used,\",$3\"%used\"}';free -g |grep Mem|awk '{ printf(\"%d\t%d\", $6+$7,($2-$4-$6-$7)*100/$2)}'|awk '{print $1\"G buffers/cache,\",$2\"% real used\"}';free -g |grep Swap|awk '{printf(\"%d\t%.2f\",$2,$3/$2)}'|awk '{print \"Swap:\"$1\"G,\",$2\"%used\"}'";
        }




        ses.execCommand(shell);

        InputStream out=new StreamGobbler(ses.getStdout());
        BufferedReader br=new BufferedReader(new InputStreamReader(out));

        String memery="";
        while(true)
        {
            String line=br.readLine();
            if(line==null)
            {
                break;
            }

            memery=memery+line+"\n";

        }

        br.close();


        return memery;

    }

    private String getCPU(Session ses) throws IOException
    {
        ses.execCommand("top -b -n 1|grep Cpu|awk '{print $2,$3,$5}';uptime|awk '{print $3,$4,$8,$9,$10,$11,$12}' ");

        InputStream out=new StreamGobbler(ses.getStdout());
        BufferedReader br=new BufferedReader(new InputStreamReader(out));

        String cpu="";
        while(true)
        {
            String line=br.readLine();
            if(line==null)
            {
                break;
            }

            cpu=cpu+line+"\n";

        }

        br.close();


        return cpu;

    }


    private String getDisc(Session ses) throws IOException
    {
        ses.execCommand("df -h |head -1 ;df -h|sed '1d'|sort -nrk 5 |head -3 ");

        InputStream out=new StreamGobbler(ses.getStdout());
        BufferedReader br=new BufferedReader(new InputStreamReader(out));

        String Disc="";
        while(true)
        {
            String line=br.readLine();


            if(line==null)
            {
                break;
            }


            //	byte[] bt=line.getBytes();
            //		String newline=new String(bt,"UTF-8");
            Disc=Disc+line+"\n";

        }




        br.close();


        return Disc;

    }


    private String getIo(Session ses) throws IOException
    {
        ses.execCommand(" iostat  |sed '1d' |head -3;iostat -d |sed '1d;2d' |head -1;iostat -d |sed '1d;3d' |head -5|sort -nrk 2");

        InputStream out=new StreamGobbler(ses.getStdout());
        BufferedReader br=new BufferedReader(new InputStreamReader(out));

        String io="";
        while(true)
        {
            String line=br.readLine();
            if(line==null)
            {
                break;
            }

            io=io+line+"\n";

        }

        br.close();


        return io;

    }



    private String getNetwork(Session ses) throws IOException, InterruptedException
    {



        String network="";
        // ses.execCommand(" /sbin/ifconfig eth0 |grep bytes|awk {'print substr($6,7,100)'};/sbin/ifconfig eth0 |grep bytes|awk {'print substr($2,7,100)'};sleep 2; /sbin/ifconfig eth0 |grep bytes|awk {'print substr($6,7,100)'};/sbin/ifconfig eth0 |grep bytes|awk {'print substr($2,7,100)'}");
        ses.execCommand("for eth in `/sbin/ifconfig |grep eth|awk '{print  $1}'`  ;do \n"+
                "echo $eth\" \"`/sbin/ifconfig ${eth} |grep bytes|awk {'print substr($6,7,100)'};/sbin/ifconfig ${eth} |grep bytes|awk {'print substr($2,7,100)'};sleep 2; /sbin/ifconfig ${eth} |grep bytes|awk {'print substr($6,7,100)'};/sbin/ifconfig ${eth} |grep bytes|awk {'print substr($2,7,100)'}` \n"+
                "done ");
        InputStream out=new StreamGobbler(ses.getStdout());
        BufferedReader br=new BufferedReader(new InputStreamReader(out));


        while(true)
        {
            String line=br.readLine();

            if(line==null)
            {
                break;
            }
            String[]  ss=line.split(" ");

            if(((Long.valueOf(ss[4])+Long.valueOf(ss[3]))-(Long.valueOf(ss[2])+Long.valueOf(ss[1])))<=0)
            {
                network=network+ss[0]+":no flow \n";
            }
            else{
                network=network+ss[0]+":"+String.valueOf(((Long.valueOf(ss[4])+Long.valueOf(ss[3]))-(Long.valueOf(ss[2])+Long.valueOf(ss[1])))/1024)+" KB/s \n";
            }

        }

        br.close();











        return network;

    }





    public Machine getInfo() {
        // TODO Auto-generated method stub
        return this.info;
    }



}
