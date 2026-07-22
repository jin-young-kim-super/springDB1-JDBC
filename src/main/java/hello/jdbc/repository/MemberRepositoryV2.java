package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 *  JDBC - Connection을 매개변수로 전달하여 Connection 동기화!!!
 *  -> 하나의 트랜잭션은 반드시 같은 커넥션을 유지해야 한다. 그 방법 중 하나로 매개변수로 connection을 전달하는 것이다.
 */
@Slf4j
public class MemberRepositoryV2 {

    private final DataSource dataSource;

    public MemberRepositoryV2(DataSource dataSource) {
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
    public Member findById(Connection connection, String memberId) throws SQLException {

        String sql = "select * from member where member_id=?";

        // 이제부터는 매개변수로 넘어온 Connection을 사용해야 하기 때문에 새롭게 커넥션 획득을 하면 x
        //Connection connection = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;

        try{
            //connection = getConnection();
            psmt = connection.prepareStatement(sql);
            psmt.setString(1,memberId);

            rs = psmt.executeQuery();
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
            // connection은 여기서 닫으면 이제 안 된다.
            // -> 트랜잭션에서는 서비스 계층에서 commit/rollback 시 커넥션을 닫아야 한다.
            // close(connection,psmt,rs);
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(psmt);
            //JdbcUtils.closeConnection(connection);
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

    public void update(Connection connection, String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id = ?";

        //Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            //connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(2,memberId);
            pstmt.setInt(1,money);
            int row = pstmt.executeUpdate();
            log.info("row={}",row);
        } catch (SQLException e) {
            log.error("db error",e);
            throw e;
        } finally {
            //close(connection,pstmt,null);
            JdbcUtils.closeStatement(pstmt);
            //JdbcUtils.closeConnection(connection);
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
        // JdbcUtils : 스프링이 제공하는 편의 메서드
        // -> close 할 떄 유용!
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(connection);
    }

    private  Connection getConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        log.info("get connection = {} class={}",connection,connection.getClass());
        return connection;
    }
}
