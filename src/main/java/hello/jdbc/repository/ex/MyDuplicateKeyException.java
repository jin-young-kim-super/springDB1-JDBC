package hello.jdbc.repository.ex;

import java.sql.SQLException;

/**
 * 로그인 ID 중복 시에만 던져지는 언체크 예외
 * -> MyDbException 예외를 상속시킴으로써, DBException임을 명시한다.
 * 이런 식으로 목적에 맞게 데이버 베이스 예외를 계층화시킬 수도 있음
 *
 * -> 또한 직접 만든 체크 전환용 언체크 예외이기 때문에 향후 JDBC 의존 레포지토리가 아니라, JPA 의존 레포지토리로 바뀌어도
 * JPA 체크 예외를 MyDbException, MyDuplicateKeyException 등의 직접 만든 언체크 예외로 던지면 되기에 서비스 계층에서의 코드 변화는 없다.
 */

public class MyDuplicateKeyException extends MyDbException {

    public MyDuplicateKeyException() {
    }

    public MyDuplicateKeyException(String message) {
        super(message);
    }

    public MyDuplicateKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDuplicateKeyException(Throwable cause) {
        super(cause);
    }
}
