package hello.jdbc.exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.error.MarkedYAMLException;

@Slf4j
public class UncheckedTest {

    /**
     * 언체크 예외는 자동으로 예외를 던져준다
     * 장점 : 개발자가 신경 쓰고 싶지 않은 예외에 대해서는 잡지도/던지지도 않아도 되니깐 코드가 깔끔
     * 단점 : 반대로 정말 중요한 예외라서 코드에 예외를 잡거나 던지거나 해서 명시적으로 예외를 표기하고 싶지만, 개발자의 누락하게 되면 컴파일 타임에 잡아 주지 못한다.
     */
    static class MyUncheckedException extends RuntimeException {

        public MyUncheckedException(String message) {
            super(message);
        }
    }

    static class Repository {
        public void call( ){
            throw new MyUncheckedException("ex"); // 언체크 예외이기에 잡고나 던지지 않아도 된다.(그럼, 자동으로 던져준다)
        }
    }

    // 언체크 예외는 예외를 잡거나/던지지 않아도 된다.
    // -> 만약 잡지 않으면 자동으로 던진다.
    static class Service {

        Repository repository = new Repository();

        public void callCatch() {
            try{
                repository.call();
            }catch (RuntimeException e) {
                log.info("예외 처리 message={}",e.getMessage(),e);
            }
        }

        public void callThrow() {
            repository.call(); // 언체크 예외기 때문에 잡지 않아도 된다. 그 경우 자동으로 예외를 던짐
        }
    }

    @Test
    void unCheckedCatch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unCheckedThrow() {
        Service service = new Service();
        Assertions.assertThatThrownBy(() -> service.callThrow() )
                .isInstanceOf(MyUncheckedException.class);
    }
}