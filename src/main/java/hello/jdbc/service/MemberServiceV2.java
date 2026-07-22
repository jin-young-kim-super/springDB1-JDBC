package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Q. 트랜잭션은 어디서 시작해야 하고, 어디서 끝내야 할까?
 * A. 서비스 계층! 그 이유는 비지니스 로직 오류 발생 시, 해당 비지니스 오류 관련 데이터를 모두 롤백을 해야 하기 때문이다.
 */


@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final MemberRepositoryV2 memberRepository;
    private final DataSource dataSource;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        Connection connection = dataSource.getConnection();

        try {
            connection.setAutoCommit(false); // 트랜잭션 시작
            bizLogic(fromId, toId, money, connection);
            connection.commit(); // 트랜잭션 끝1
        }catch (Exception e) {
            connection.rollback(); // 트랜잭션 끝2
            throw new IllegalStateException(e);
        } finally {
            if(connection != null) {
                try {
                    // 커넥션 풀을 사용하지 않는다면 setAutoCommit(true)를 안 해도 된다.
                    // -> setAutoCommit(true)을 하지 않고 커넥션 풀에 반납을 하면 setAutoCommit(false)가 유지가 되 버린다.
                    // 그러나 커넥션 풀을 사용하지 않고 매회 커넥션 신규 생성 시 setAutoCommit은 false로 자동 세팅되게 DB에 설정이 되 있끼에
                    // 별 문제가 안된다.
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (Exception e) {
                    log.error("error",e);
                    throw new IllegalStateException(e);
                }
            }
        }
    }

    private void bizLogic(String fromId, String toId, int money, Connection connection) throws SQLException {
        // 비니지스 로직 시작
        Member fromMember = memberRepository.findById(connection, fromId);
        Member toMember = memberRepository.findById(connection, toId);
        memberRepository.update(connection, fromId, fromMember.getMoney() - money);
        validate(toMember);
        memberRepository.update(connection, toId, toMember.getMoney() + money);
    }

    private static void validate(Member toMember) {
        if(toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
