package hello.jdbc.exception;


import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class CheckedTest {

    // 체크 예외
    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    static class Service {
        Repository repository = new Repository();
        /**
         * 예외를 잡아서 처리하는 코드
         */
        public void callCatch() {
            try{
                repository.call(); // 체크 예외를 잡음
            }catch (Exception e) {
                log.info("예외 처리, message={}",e.getMessage(),e);
            }
        }

        /**
         * 예외를 잡지 않고 던짐.
         * -> 체크 예외를 잡아도 뭘 할 수 있는 게 없을 때는 그냥 예외를 던진다.
         * 장점 : 개발자가 실수로 예외를 누락하지 않도록 컴파일 타임에서 잡아준다.(그럼으로, 개발자에게 예외를 잡을까 던질까하는 고민을 강제한다)
         * 단점 : 개발자가 모든 체크 예외를 잡거나 던지거나 하는 처리를 해줘야 하므로 매우 번거롭다.크게 신경 쓰고 싶지 않는 예외까지 모두 챙겨야 한다.
         */
        public void callThrow() throws MyCheckedException {
            repository.call();
        }

    }

    static class Repository {
        public void call() throws MyCheckedException { // 체크 예외를 던져야 함(컴파일 체크)
            throw new MyCheckedException("ex"); // 체크 예외
        }
    }



    @Test
    void checkedCatch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test // 체크 예외를 잡지 않고 던짐
    void CheckedThrow() throws MyCheckedException {
        Service service = new Service();
        Assertions.assertThatThrownBy(()-> service.callThrow()).isInstanceOf(MyCheckedException.class);
    }
}
