package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 트랜잭션 동기화 매니저(close(),getConncetion() 부분 수정)
 * DataSourceUtils.getConnection() : 트랜잭션 시작 시, 트랜잭션 동기화 매니저에서 커넥션 획득
 * DataSourceUtils.releaseConnection() : 트랜잭션 종료 시, 트랜잭션 동기화 매니저에서 커넥션 해제
 * -> 트랜잭션 동기화 매니저 사용 시에는 반드시 DataSourceUtils를 사용해야 한다.
 */
@Slf4j
public class MemberRepositoryV3 {

    private final DataSource dataSource;

    public MemberRepositoryV3(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id,money) values(?,?)";

        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1,member.getMemberId());
            pstmt.setInt(2,member.getMoney());
            int row = pstmt.executeUpdate();// 쿼리 실행 : 영향을 받은 row 수를 반환한다.
            return member;
        } catch (SQLException e) {
            log.error("db error",e);
            throw e;
        } finally {
            close(connection,pstmt,null);
        }
    }

    public Member findById(String memberId) throws SQLException {

        String sql = "select * from member where member_id=?";

        Connection connection = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;

        try{
            connection = getConnection();
            psmt = connection.prepareStatement(sql);
            psmt.setString(1,memberId);

            rs = psmt.executeQuery(); // select는 executeUpdate()가 아닌 executeQuery()를 사용
            if(rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }
        }catch (SQLException e) {
            log.error("db error",e);
            throw e;
        } finally {
            close(connection,psmt,rs);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id = ?";

        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(2,memberId);
            pstmt.setInt(1,money);
            int row = pstmt.executeUpdate();
            log.info("row={}",row);
        } catch (SQLException e) {
            log.error("db error",e);
            throw e;
        } finally {
            close(connection,pstmt,null);
        }
    }

    public void delete(String memberId) throws SQLException {

        String sql = "delete from member where member_id = ?";

        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1,memberId);
            int row = pstmt.executeUpdate();
            log.info("row={}",row);
        } catch (SQLException e) {
            log.error("db error",e);
            throw e;
        } finally {
            close(connection,pstmt,null);
        }
    }

    private void close(Connection connection, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        //JdbcUtils.closeConnection(connection);
        // 트랜잭션 동기화 매니저에 커넥션을 반환할 때에도 DataSourceUtils를 사용해야 한다.
        DataSourceUtils.releaseConnection(connection,dataSource);
    }

    private  Connection getConnection() throws SQLException {
        // 트랜잭션 동기화 매니저에서 커넥션 획득
        Connection connection = DataSourceUtils.getConnection(dataSource);
        log.info("get connection = {} class={}",connection,connection.getClass());
        return connection;
    }
}
