package server.data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class FailedAuthentications {
    private static final int LOGIN_ATTEMPTS = 3;
    private static final int LOCK_TIME = 30;
    private Map<String, Integer> failedAuthentications;
    private Map<String, LocalDateTime> lockedUsers;

    public FailedAuthentications() {
        failedAuthentications = new HashMap<String, Integer>();
        lockedUsers = new HashMap<String, LocalDateTime>();
    }

    public boolean isLocked(String username) {
        if (!lockedUsers.containsKey(username)) {
            return false;
        }

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime startOfLockPeriod = lockedUsers.get(username);

        long elapsedSeconds = ChronoUnit.SECONDS.between(startOfLockPeriod, currentTime);

        if (elapsedSeconds > LOCK_TIME) {
            lockedUsers.remove(username);
            return false;
        }

        return true;
    }

    private void lockUser(String username) {
        lockedUsers.put(username, LocalDateTime.now());
    }

    public void addFailedAuthentication(String username) {
        if (!failedAuthentications.containsKey(username)) {
            failedAuthentications.put(username, 1);
        } else {
            failedAuthentications.replace(username, failedAuthentications.get(username),
                    failedAuthentications.get(username) + 1);

            if (failedAuthentications.get(username) > LOGIN_ATTEMPTS) {
                lockUser(username);
                failedAuthentications.remove(username);
            }
        }
    }
}
