package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;


/**
 * 트랜잭션 템플릿
 */

@Slf4j
public class MemberServiceV3_2 {

    //private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate transactionTemplate;
    private final MemberRepositoryV3 memberRepository;


    public MemberServiceV3_2(MemberRepositoryV3 memberRepository, PlatformTransactionManager transactionManager) {
        this.memberRepository = memberRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    // 이제 지난 시간 코드와 다르게, 트랜잭션을 시작/commit/rollback의 반복 코드가 사라졌다.
    // 그러나 여기는 비지니스 코드가 핵심인 서비스 계층인데, 여전히 트랜잭션 템플릿 코드가 섞여 있다.
    // 만약에 여기 메서드에서 트랜잭션을 사용하지 않는다고 하면 이 코드를 다 뜯어 고쳐야 하는 문제점이있다.
    // 다음 시간에는 AOP와 Proxy를 활용하여 이 문제를 해결해보자.
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // 이 코드 안에서 우리가 저번 시간에 이 메서드에 적었던 코드가 전부 실행이 된다
        // -> 트랜잭션 템플릿의 구체적인 내부동작은 스프링 핵심원리 고급편에서 다루니, 지금은 사용법에 만족하자
        transactionTemplate.executeWithoutResult((transactionStatus ->  {
            try {
                bizLogic(fromId, toId, money);
            }catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }));
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
