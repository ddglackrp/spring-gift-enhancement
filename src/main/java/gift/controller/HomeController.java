package gift.controller;

import gift.auth.annotation.SignInMember;
import gift.domain.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HomeController {

    @GetMapping("/")
    public String root(){
        return "어 왔니?";
    }

    @GetMapping("/home")
    public String home(@SignInMember Member member){
        return "누구나 접근할 수 있는 페이지 입니다.";
    }

}
