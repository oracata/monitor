package com.oracat;

import com.oracat.model.Machine;
import com.oracat.tools.EnvConfig;
import com.oracat.tools.MachineInfo;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class TomcatStarter {

    //log加载
    public static final Logger logger=LoggerFactory.getLogger(TomcatStarter.class);
    //主机信息模型实列化
    public static ConcurrentHashMap<String, Machine> testmachineHashMap= new ConcurrentHashMap<String,Machine>();

    /**
     *
     * Tomcat 启动主类方法
     * @author huangyan
     * @date 2018/6/5 11:39
     * @return void
     */
    public static void main(String[] args) throws Exception {
        try {
            if (!EnvConfig.init()) {
                logger.info("加載配置文件失敗。");
                System.exit(0);
            }
            // 1.创建一个内嵌的Tomcat
            Tomcat tomcat = new Tomcat();


            // 2.设置Tomcat端口默认为8080
            final Integer webPort = Integer.parseInt(EnvConfig.port);
            tomcat.setPort(Integer.valueOf(webPort));


            // 3.设置工作目录,tomcat需要使用这个目录进行写一些东西
            final String baseDir = EnvConfig.basedir;
            tomcat.setBaseDir(baseDir);
            tomcat.getHost().setAutoDeploy(false);


            // 4. 设置webapp资源路径
            String webappDirLocation = "E:\\monitor\\tomcat\\basedir\\apache-tomcat-8.5.43\\webapps\\monitor";
            StandardContext ctx = (StandardContext) tomcat.addWebapp("/monitor", new File(webappDirLocation).getAbsolutePath());
            logger.info("configuring app with basedir: " + new File("" + webappDirLocation).getAbsolutePath());
            logger.info("project dir:"+new File("").getAbsolutePath());


            // 5. 设置上下文路每径
            String contextPath = "";
            ctx.setPath(contextPath);
            ctx.addLifecycleListener(new Tomcat.FixContextListener());
            ctx.setName("monitor");


            System.out.println("child Name:" + ctx.getName());
            tomcat.getHost().addChild(ctx);





            logger.info("服务器加载完配置，正在启动中……");
            tomcat.start();
            logger.info("服务器启动成功");
          //  tomcat.getServer().await();
        } catch (Exception exception) {
            logger.error("服务器启动失敗", exception);
        }

        XMLConfiguration testConfig=new XMLConfiguration("classpath:testip.xml");
        XMLConfiguration sysConfig=new XMLConfiguration("classpath:sys.xml");
        final long sleepTime=sysConfig.getLong("sleep_time");


        while(true)
        {


            //etl
            List<String> testipList=testConfig.getList("machine.ip");
            Iterator<String> testitr=testipList.iterator();
            int	i=0;




            //循环ip
            while(testitr.hasNext())
            {

                String ip=testitr.next();




                //生成 每个xml配置的hashmap
                HashMap<String,String> testHashMapXML=new HashMap<String,String>();
                HierarchicalConfiguration sub = (HierarchicalConfiguration)testConfig.subset("machine("+i+")");
                Iterator<String> itr=testConfig.getKeys(); //获取当前XMLConfiguration对象可以使用的key
                while(itr.hasNext()){
                    String[] key=itr.next().split("\\.");

                    testHashMapXML.put(key[1], sub.getString(key[1]));

                }



                //获取主机信息

                //MachineInfo machineInfo=new MachineInfo( ip, etlipConfig.getString("machine("+i+").username"),etlipConfig.getString("machine("+i+").password"));
                MachineInfo machineInfo=new MachineInfo(testHashMapXML);

                testmachineHashMap.put(ip+"("+testConfig.getString("machine("+i+").host")+")",machineInfo.getInfo());
                i++;
            }




            Thread.currentThread().sleep(sleepTime);

        }



    }



}
