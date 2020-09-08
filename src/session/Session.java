package session;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Session {

    private static int sessionID = 0;
    private static final int TIME_TO_LIVE = 200;

    private LocalDateTime loginTime;

    public Session() {
        sessionID++;
        this.loginTime = LocalDateTime.now();
    }

    public boolean checkIfExpired() {
        LocalDateTime currentTime = LocalDateTime.now();

        long elapsedSeconds = ChronoUnit.SECONDS.between(loginTime, currentTime);

        return elapsedSeconds > TIME_TO_LIVE;
    }

    public int getSessionID() {
        return sessionID;
    }

}
