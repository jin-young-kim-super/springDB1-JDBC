package hello.jdbc.repository.ex;

/**
 * 체크 예외를 MyDbException 언체크 예외(런타임 예외)로 전환할 것이다.
 */

public class MyDbException extends RuntimeException{

    public MyDbException() {
    }

    public MyDbException(String message) {
        super(message);
    }

    public MyDbException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDbException(Throwable cause) {
        super(cause);
    }
}
