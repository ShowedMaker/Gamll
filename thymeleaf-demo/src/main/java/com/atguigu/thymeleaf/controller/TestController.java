package com.atguigu.thymeleaf.controller;

import com.atguigu.thymeleaf.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName TestController
 * @Description
 * @Author yzchao
 * @Date 2022/10/31 16:31
 * @Version V1.0
 */
@Controller
@RequestMapping("/thymeleaf")
public class TestController {


    @GetMapping()
    public String test(Model model){
        model.addAttribute("username","快跑手机卡平均分阿三发射点");
        model.addAttribute("password","按时间撒<span style=color:red>旦发射</span>点风格豆腐干");

        ArrayList<User> arrayList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setName("叶嘉威"+i);
            user.setAge(i);
            user.setAddress("深圳" + i);
            arrayList.add(user);
        }
        model.addAttribute("arrayList",arrayList);
        model.addAttribute("date",new Date());
        model.addAttribute("age",16);

        Map<String, Object> map = new HashMap<>();
        map.put("aa",11);
        map.put("bb",22);
        map.put("cc",33);
        model.addAttribute("map",map);

        model.addAttribute("url","/thymeleaf/jump");
        model.addAttribute("img","https://www.baidu.com/link?url=B_A2cxWTzn1l5WHay9NkNzIcV4a07WTMyWsT7ePyiWFu5ycpcDp1tlPHy55e9a_AwCp2LcibC23aqXt1BKtS_4kXFptj2S-HRAaMN_y7XlFeXaZA_KRNQBhFu169VFafrvplDJXN9nlOHLgmFfgFLBhwAXn06AHoLHLb5h57NTFeojO3Naj8zk2DTt2Vbgmv9slsKL9ssudLcxNdulmRMOejTfRzjo43ptT7GqYCkDnt9irZKfPl6ZaP2IfhzAEw6BuspvmQFS7HwQV194dkrNzQRWL_jbw9Lg6qrHqXIn5Edm7rjCGqyd_pQP6IT1fcyLWu_36oDpLU3q_4O_2AnWdkAVsV905aO_0ETtFRYvLTN4ZUC7ARzNz_lsZU0VTmvK4AuwvJspKYTiU4cW7-paP36OxqQ92KC8oSr8_zaD8e0JcO_eEduJNCpX9TefFxVur7z0iU1nfNDsonIoq5MwmnVMxjtws7jeXnrlAKIl7F_NOGTDOvCEVujZ-aRxF2BTYG1aMDY6ioKRluHNdYKUBBWsRp36KuWPmAxKPpWnSdNMuTEc2LljEZ-eMG6A_czlwHUgNFYQjfp5CYLhQfbc5FeWLe_mMuuBSfHh9to9tuQAJ8nxKIfYSB52toSy2azHah8_o0S1Wy3N8cKatHrpP62Ot34szjzrC-auJWWI3&wd=&eqid=cb387b8e0002a5ef00000006635fa372");
        return "test";
    }

    @GetMapping("/jump")
    public String jump(String username,String password){
        System.out.println("username = " + username);
        System.out.println("password = " + password);
        return "redirect:http://www.rywoodstore.com";
    }
}
