package hello.jdbc.exception.translator;

import hello.jdbc.connection.ConnectionConst;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 스프링의 DB 계층 예외 추상황
 */

@Slf4j
public class SpringExceptionTranslatorTest {

    DataSource dataSource;

    @BeforeEach
    void init() {
        this.dataSource = new DriverManagerDataSource(ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSSWORD);
    }


    @Test
    void sqlExceptionErrorCode() {
        String sql = "select bad grammer"; // SQL 문법 오류

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement psmt = connection.prepareStatement(sql);
            psmt.executeQuery();
        } catch (SQLException e) { // 체크 예외
            // h2의 에러코드
            assertThat(e.getErrorCode()).isEqualTo(42122);
            log.info("error",e);
        }
    }


    @Test
    void exceptionTranslator() {
        String sql = "select bad grammer"; // SQL 문법 오류

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement psmt = connection.prepareStatement(sql);
            psmt.executeQuery();
        } catch (SQLException e) { // 체크 예외
            // h2의 에러코드
            assertThat(e.getErrorCode()).isEqualTo(42122);
            // SQLErrorCodeSQLExceptionTranslator : 스프링의 예외 변환기(dataSource를 인자로 넘김으로써, 어떤 DB 벤더의 에러코드라도 자동으로 해당 에러코드에 대한 스프링 예외를 반환)
            SQLErrorCodeSQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
            // springException : 여기에 DataAccessException의 자식 예외인 BadGrammerException을 반환한다.(BadGrammerException은 스프링 예외)
            // -> 즉, 개발자가 일일이 에러코드에 해당하는 스프링 예외를 찾지 않아도 자동으로 해당 예외객체를 반환(레포지토리에서는 이 언체크 예외인 DataAccessException을 던지면 되거, 서비스 계층에서 잡아서 복구하고 싶은 예외가 있으면 BadGrammerException을 잡아서 처리하면 된다)
            DataAccessException springException = exTranslator.translate("select", sql, e);
            log.info("spring Exception",springException);
            Assertions.assertThat(springException.getClass()).isEqualTo(BadSqlGrammarException.class);
        }
    }







}
