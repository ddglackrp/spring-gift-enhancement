package gift.controller;

import java.time.Instant;
import java.util.stream.Collectors;

import gift.auth.UserDetailsServiceImp;
import gift.auth.annotation.SignInMember;
import gift.domain.member.Member;
import gift.dto.request.MemberRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller for the token resource.
 *
 * @author Josh Cummings
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class TokenController {

    private final JwtEncoder encoder;

    private final UserDetailsServiceImp userDetailsService;


    @PostMapping("/token")
    public String token(@RequestBody @Valid MemberRequestDto memberRequestDto) {
        UserDetails findMember = userDetailsService.loadUserByUsername(memberRequestDto.email());

        log.info("email = {}",findMember.getUsername());

        Instant now = Instant.now();
        long expiry = 36000L;
        // @formatter:off
        String scope = findMember.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(findMember.getUsername())
                .claim("scope", scope)
                .build();
        // @formatter:on
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}