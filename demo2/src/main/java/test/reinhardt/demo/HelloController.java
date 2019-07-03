package test.reinhardt.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class HelloController {
    @RequestMapping("/")
    @ResponseBody
    public String index(){
        return "Hello World!";
    }


    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    public String say(){
//        return girlProperties.getCupSize();
        return "hi";
    }
}
