package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach
    void beforEach() {
        // 기본 DriverManger - 항상 새로운 커넥션 회득
        // -> getConnection 할 떄마다 TCP/IP 연결 맺어서 커넥션 반환(존~~~~나 느림)
        //DriverManagerDataSource dataSource = new DriverManagerDataSource(ConnectionConst.URL,ConnectionConst.USERNAME,ConnectionConst.PASSSWORD);

        // Hicakri CP 사용 : 별도의 쓰레드로 미리 커넥션을 맺어 놓은 다음에 그 풀 안의 커넥션을 반환(존~~나 빠름)
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(ConnectionConst.URL);
        dataSource.setUsername(ConnectionConst.USERNAME);
        dataSource.setPassword(ConnectionConst.PASSSWORD);
        this.repository = new MemberRepositoryV1(dataSource);
    }

    @Test
    void save() throws SQLException {
        Member member = new Member("memberV10", 10000);
        repository.save(member);
    }

    @Test
    void findById() throws SQLException {

        Member member = new Member("memberV11", 100);
        repository.save(member);

        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}",findMember);

        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void update() throws SQLException {
        Member member = new Member("memberV12", 100);
        repository.save(member);

        repository.update(member.getMemberId(),20);
        Member findMember = repository.findById(member.getMemberId());
        assertThat(findMember.getMoney()).isEqualTo(20);
    }

    @Test
    void delete() throws SQLException {
        Member member = new Member("memberV13", 100);
        repository.save(member);

        repository.delete(member.getMemberId());
        assertThatThrownBy(()-> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
    }
}