package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;


import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC Template - JDBC의 반복 코드 제거
 */
@Slf4j
public class MemberRepositoryV5 implements MemberRepository{

//    private final DataSource dataSource;
//    private final SQLExceptionTranslator exTranslator;

    private final JdbcTemplate template;

    public MemberRepositoryV5(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
        //this.exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

    @Override
    public Member save(Member member) {

        String sql = "insert into member(member_id, money) values(?, ?)";
        template.update(sql, member.getMemberId(), member.getMoney());
        return member;
        // 위 3줄이 아래의 모든 코드를 다 처리한다.ㄷㄷㄷㄷㄷㄷ


//        Connection connection = null;
//        PreparedStatement pstmt = null;
//        try {
//            connection = getConnection();
//            pstmt = connection.prepareStatement(sql);
//            pstmt.setString(1,member.getMemberId());
//            pstmt.setInt(2,member.getMoney());
//            int row = pstmt.executeUpdate();
//            return member;
//        } catch (SQLException e) {
//            //throw new MyDbException(e); // 체크 예외 -> 언체크 예외(런타임 예외)
//            throw exTranslator.translate("save",sql,e); // 체크 예외 -> 스프링 언체크 예외
//        } finally {
//            close(connection,pstmt,null);
//        }
    }

    @Override
    public Member findById(String memberId)  {

        String sql = "select * from member where member_id = ?";
        return template.queryForObject(sql, memberRowMapper(), memberId);

        // 위 3줄이 아래 모든 코드를 대체한다

//        Connection connection = null;
//        PreparedStatement psmt = null;
//        ResultSet rs = null;
//
//        try{
//            connection = getConnection();
//            psmt = connection.prepareStatement(sql);
//            psmt.setString(1,memberId);
//
//            rs = psmt.executeQuery(); // select는 executeUpdate()가 아닌 executeQuery()를 사용
//            if(rs.next()) {
//                Member member = new Member();
//                member.setMemberId(rs.getString("member_id"));
//                member.setMoney(rs.getInt("money"));
//                return member;
//            } else {
//                throw new NoSuchElementException("member not found memberId = " + memberId);
//            }
//        }catch (SQLException e) {
//            //throw new MyDbException(e); // 체크 예외 -> 언체크 예외(런타임 예외)
//            throw exTranslator.translate("save",sql,e); // 체크 예외 -> 스프링 언체크 예외
//        } finally {
//            close(connection,psmt,rs);
//        }
    }

    // DB 데이터 -> 객체 변환
    private RowMapper<Member> memberRowMapper() {
        return (resultSet,rowNum) -> {
            Member member = new Member();
            member.setMemberId(resultSet.getString("member_id"));
            member.setMoney(resultSet.getInt("money"));
            return member;
        };
    }


    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money=? where member_id=?";
        template.update(sql, money, memberId);

        // 위 2줄이 아래 모든 코드를 대체한다
//        Connection connection = null;
//        PreparedStatement pstmt = null;
//        try {
//            connection = getConnection();
//            pstmt = connection.prepareStatement(sql);
//            pstmt.setString(2,memberId);
//            pstmt.setInt(1,money);
//            int row = pstmt.executeUpdate();
//            log.info("row={}",row);
//        } catch (SQLException e) {
//            //throw new MyDbException(e); // 체크 예외 -> 언체크 예외(런타임 예외)
//            throw exTranslator.translate("update",sql,e); // 체크 예외 -> 스프링 언체크 예외
//        } finally {
//            close(connection,pstmt,null);
//        }
    }

    @Override
    public void delete(String memberId) {

        String sql = "delete from member where member_id = ?";
        template.update(sql,memberId);

        // 위 2줄이 아래 모든 코드를 대체한다

//        Connection connection = null;
//        PreparedStatement pstmt = null;
//        try {
//            connection = getConnection();
//            pstmt = connection.prepareStatement(sql);
//            pstmt.setString(1,memberId);
//            int row = pstmt.executeUpdate();
//            log.info("row={}",row);
//        } catch (SQLException e) {
//            //throw new MyDbException(e); // 체크 예외 -> 언체크 예외(런타임 예외)
//            throw exTranslator.translate("save",sql,e); // 체크 예외 -> 스프링 언체크 예외
//        } finally {
//            close(connection,pstmt,null);
//        }
    }

//    private void close(Connection connection, Statement stmt, ResultSet rs) {
//        JdbcUtils.closeResultSet(rs);
//        JdbcUtils.closeStatement(stmt);
//        //JdbcUtils.closeConnection(connection);
//        // 트랜잭션 동기화 매니저에 커넥션을 반환할 때에도 DataSourceUtils를 사용해야 한다.
//        DataSourceUtils.releaseConnection(connection,dataSource);
//    }
//
//    private  Connection getConnection() throws SQLException {
//        // 트랜잭션 동기화 매니저에서 커넥션 획득
//        Connection connection = DataSourceUtils.getConnection(dataSource);
//        log.info("get connection = {} class={}",connection,connection.getClass());
//        return connection;
//    }
}
