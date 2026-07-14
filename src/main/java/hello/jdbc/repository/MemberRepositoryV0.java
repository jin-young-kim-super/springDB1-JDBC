package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 *  JDBC - DriverManger 사용
 */
@Slf4j
public class MemberRepositoryV0 {

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


    // Statement : PreparedStatement는 이것을 상속 받음
    // -> PreparedStatement는 파라미터 바인딩하는 기능을 제공
    private void close(Connection connection, Statement stmt, ResultSet rs) {
        // 참고로 close를 할 떄는 반드시 역순으로 해야 한다.
        // -> 소멸자를 생각해봐라.
        if(stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error("error",e);

            }
        }

        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("error",e);
            }
        }

        if(rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("error",e);
            }
        }

    }

    private static Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }


}
