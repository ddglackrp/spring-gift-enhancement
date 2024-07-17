package gift.auth;

import gift.domain.member.Member;
import gift.exception.EntityNotFoundException;
import gift.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImp implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findMemberByEmail(email).orElseThrow(()->new EntityNotFoundException("유저를 찾을 수 없습니다 username : "+email));
        if(member != null){
            return new UserDetailsImp(member);
        }
        return null;
    }
}
