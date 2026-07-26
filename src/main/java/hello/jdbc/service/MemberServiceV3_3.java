package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;


import java.sql.SQLException;


/**
 * 트랜잭션 - @Transactional AOP
 * -> 트랜잭션 관련 코드 전부 제거
 */

@Slf4j
//@Transactional : 클래스 위에 @Transcational을 붙이면, 모든 메서드에 트랜잭션이 적용된다
public class MemberServiceV3_3 {

    //private final TransactionTemplate transactionTemplate;
    private final MemberRepositoryV3 memberRepository;


//    public MemberServiceV3_3(MemberRepositoryV3 memberRepository, PlatformTransactionManager transactionManager) {
//        this.memberRepository = memberRepository;
//        this.transactionTemplate = new TransactionTemplate(transactionManager);
//    }

    public MemberServiceV3_3(MemberRepositoryV3 memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional // 트랜잭션 프록시에서 비지니스 로직이 실행된다
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        // 비니지스 로직 시작
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);
        memberRepository.update(fromId, fromMember.getMoney() - money);
        validate(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private static void validate(Member toMember) {
        if(toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
