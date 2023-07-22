import java.time.Duration;
import java.time.LocalDateTime;

public class dev {
    private static LocalDateTime start;

    public static void timerStart() {
        start = LocalDateTime.now();
    }

    public static void timerEnd() {
        LocalDateTime end = LocalDateTime.now();

        System.out.println("Timer: " + Duration.between(start, end));
    }
}
