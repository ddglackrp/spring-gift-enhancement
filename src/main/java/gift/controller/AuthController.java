package gift.controller;

import gift.dto.request.MemberRequestDto;
import gift.dto.response.MemberResponseDto;
import gift.service.AuthService;
import gift.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    public AuthController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @GetMapping("/login")
    public String loginForm(){
        return "login/loginForm";
    }

    @GetMapping("/join")
    public String joinForm(@ModelAttribute MemberRequestDto memberRequestDto,
                           Model model){

        model.addAttribute("joinForm", memberRequestDto);
        return "login/joinForm";
    }

    @PostMapping("/join")
    public String join(@ModelAttribute @Valid MemberRequestDto memberRequestDto){
        authService.memberJoin(memberRequestDto);
        return "redirect:/";
    }

    @ResponseBody
    @PostMapping("/members/register")
    public ResponseEntity<Map<String, String>> memberSignUp(@RequestBody @Valid MemberRequestDto memberRequestDto){
        authService.memberJoin(memberRequestDto);

        Map<String, String> response = getToken(memberRequestDto.email());

        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @ResponseBody
    @PostMapping("/members/login")
    public ResponseEntity<Map<String, String>> memberLogin(@RequestBody @Valid MemberRequestDto memberRequestDto){
        MemberResponseDto memberResponseDto = authService.findOneByEmailAndPassword(memberRequestDto);

        Map<String, String> response = getToken(memberResponseDto.email());

        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    public Map<String, String> getToken(String memberRequestDto) {
        UUID uuid = UUID.randomUUID();
        tokenService.tokenSave(uuid.toString(), memberRequestDto);

        Map<String, String> response = new HashMap<>();
        response.put("token", uuid.toString());
        return response;
    }

}
