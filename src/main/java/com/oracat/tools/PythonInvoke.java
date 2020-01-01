package com.oracat.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class PythonInvoke {
    /**
     * java项目调用python脚本
     * 输入：py文件的绝对路径
     * 输出：
     */
    public static void invokePy(String []para) {
        System.out.println("执行命令："+ Arrays.toString(para));
        try {
            Process process=Runtime.getRuntime().exec(para);

//接收子进程的I/O操作（py脚本执行的输入输出）
//process.getInputStream() 将子进程的i/o流重定向到父进程，由父进程控制子进程的i/o
            BufferedReader reader=new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK")); //windows下编码GBK防止java读取乱码　　　　　　　　

            String line;
            while((line=reader.readLine())!=null) {
                System.out.println(line);
            }
            reader.close();
            process.waitFor();
            process.destroy();//结束子进程

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("执行命令结束....");
    }
}