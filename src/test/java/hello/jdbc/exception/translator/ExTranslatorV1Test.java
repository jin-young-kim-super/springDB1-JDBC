package hello.jdbc.exception.translator;

import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import hello.jdbc.repository.ex.MyDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

@Slf4j
public class ExTranslatorV1Test {

    Repository repository;
    Service service;

    @BeforeEach
    void init() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSSWORD);
        this.repository = new Repository(dataSource);
        this.service = new Service(this.repository);
    }

    @Test
    void duplicateKeySave() {
        service.create("myId");
        service.create("myId"); // 키 중복 발생
    }


    @Slf4j
    @RequiredArgsConstructor
    static class Service {
        private final Repository repository;

        public void create(String memberId) {
            try {
                repository.save(new Member(memberId, 0)); // 로그인 ID 중복 발생
                log.info("saveId={}", memberId);
            }catch (MyDuplicateKeyException e){ // 키 중복 시 복구를 위해 언체크 예외를 잡음
                log.info("키 중복, 복구 시도");
                String newMemberId = generateNewId(memberId);
                log.info("새로운 ID={}", newMemberId);
                repository.save(new Member(newMemberId,0));
            } catch (MyDbException e){ // 강사 왈 : catch를 여러 개 사용가능함을 보여 주기위해 썼다고 함
                                       // 복구 대상 언체크 예외가 아니기에 원래는 불필요한 것이다.
                log.info("데이터 접근 계층 에외",e);
                throw e;
            }
        }

        private String generateNewId(String memberId) {
            return memberId + new Random().nextInt(1000);
        }
    }

    @RequiredArgsConstructor
    static class Repository {

        private final DataSource dataSource;

        public Member save(Member member) {
            String sql = "insert into member(member_id,money) values(?,?)";
            Connection connection = null;
            PreparedStatement pstmt = null;

            try{
                connection = dataSource.getConnection();
                pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, member.getMemberId());
                pstmt.setInt(2,member.getMoney());
                pstmt.executeUpdate();
                return member;
            }catch (SQLException e){
                // h2 db
                // 23505 : 키 중복 에러
                if(e.getErrorCode() == 23505) {
                    throw new MyDuplicateKeyException(e);
                }
                throw new MyDbException(e); // 이걸 던져도 서비스 계층에서는 코드 변경이 없다. 언체크 예외이고, 복구 대상 예외가 아니기 때문에!
            }finally {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(connection);
            }
        }
    }







}
