package NetCastJava;

import java.io.IOException;

public class NetCastExceptions {
    public static class AccessTokenError extends IOException {
        public AccessTokenError() {
        }

        public AccessTokenError(String msg) {
            super(msg);
        }
    }

    public static class SessionIdError extends IOException {
        public SessionIdError() {
        }

        public SessionIdError(String msg) {
            super(msg);
        }
    }

}
