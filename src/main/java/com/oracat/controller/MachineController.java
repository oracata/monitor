package com.oracat.controller;

import com.oracat.TomcatStarter;
import com.oracat.model.Machine;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Map;

@Controller
public class MachineController {



    @RequestMapping("/")
    public ModelAndView getIndex() {
        ModelAndView mav = new ModelAndView("main");

        return mav;
    }

    @RequestMapping("/b2bmachine")
    public ModelAndView getB2bmachine() {
        ModelAndView mav = new ModelAndView("b2bmachine");

        //遍历hashmap
        Iterator it=TomcatStarter.testmachineHashMap.entrySet().iterator();
        while(it.hasNext()) {


            Map.Entry<String, Machine> entry = (Map.Entry<String, Machine>) it.next();


            Machine test = entry.getValue();
            System.out.println("*****cpu:****"+test.getCPU());
            mav.addObject("test", test);
        }

        return mav;
    }
}