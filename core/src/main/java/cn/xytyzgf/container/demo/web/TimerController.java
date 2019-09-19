package cn.xytyzgf.container.demo.web;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description:
 * @CreateDate: Created in 2019/9/17 18:05
 * @Author: gaofeng.zhang
 */

@RestController
public class TimerController {


    @GetMapping("/offWork")
    public Object getOffTime(String beginTime) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date parse = simpleDateFormat.parse(beginTime);

        long time = parse.getTime() + (long) ((8 + 1.5) * 60 * 60 * 1000);

        String format = simpleDateFormat.format(time);

        System.out.println(" the offtime is " + format);


        return JSONObject.parse("{\"offWorkTime:\":\"" + format + "\"}");

    }
}
