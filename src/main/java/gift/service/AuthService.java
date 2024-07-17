package gift.service;

import gift.domain.member.Member;
import gift.dto.request.MemberRequestDto;
import gift.dto.response.MemberResponseDto;
import gift.exception.EmailDuplicationException;
import gift.exception.MemberNotFoundException;
import gift.repository.member.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public AuthService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    public MemberResponseDto memberJoin(MemberRequestDto memberRequestDto){
        Member member = new Member.Builder()
                .password(bCryptPasswordEncoder.encode(memberRequestDto.password()))
                .email(memberRequestDto.email())
                .build();

        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(memberRequestDto.email());

        if(memberByEmail.isPresent()){
            throw new EmailDuplicationException();
        }

        Member savedMember = memberRepository.save(member);

        return MemberResponseDto.from(savedMember);
    }

    public MemberResponseDto findOneByEmailAndPassword(MemberRequestDto memberRequestDto){
        Member findMember = memberRepository.findMemberByEmailAndPassword(memberRequestDto.email(), memberRequestDto.password())
                .orElseThrow(MemberNotFoundException::new);

        return MemberResponseDto.from(findMember);
    }

}
