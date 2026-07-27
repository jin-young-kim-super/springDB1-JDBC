package hello.jdbc.exception;


import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class CheckedAppTest {

    static class Controller {
        Service service = new Service();

        // 개발자가 해결 불가능할 체크 예외이므로, 던져야 한다
        // -> 체크 예외가 올라올 때마다 계속 이 지랄을 한다, 해결 불가능 한데...
        public void request() throws SQLException, ConnectException {
            service.logic();
        }
    }


    static class Service {

        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        // 개발자가 해결 불가능할 체크 예외이므로, 던져야 한다
        // -> 체크 예외가 올라올 때마다 계속 이 지랄을 한다, 해결 불가능 한데...
        public void logic() throws SQLException, ConnectException {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {
        public void call() throws ConnectException {
            throw new ConnectException();
        }
    }

    static class Repository {
        public void call() throws SQLException {
            throw new SQLException();
        }
    }

    @Test
    void checked() {
        Controller controller = new Controller();
        assertThatThrownBy(() -> controller.request())
                .isInstanceOf(Exception.class);
    }

}
