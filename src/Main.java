import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final int SLEEP_TIME_MILLIS = 1000;
    private static final List<Integer> randoms = new ArrayList<>();

    private static final Thread writer = new Thread(() -> {
        while (true) {
            synchronized (randoms) {
                try {
                    Integer integer = new Random().nextInt();
                    randoms.add(integer);
                    LOGGER.log(Level.INFO, Thread.currentThread().getName() + " положил " + integer);
                    Thread.sleep(SLEEP_TIME_MILLIS);
                    randoms.notifyAll();
                    randoms.wait();
                } catch (InterruptedException e) {
                    LOGGER.log(Level.WARNING, "Interrupted exception in writer implementation." + e.getMessage());
                }
            }
        }
    });
    
    private static final Thread reader = new Thread(() -> {
        while (true) {
            synchronized (randoms) {
                try {
                    if (randoms.isEmpty()) {
                        randoms.wait();
                    }
                    if (!randoms.isEmpty()) {
                        LOGGER.log(Level.INFO, Thread.currentThread().getName() + " вытащил " + randoms.remove(0));
                        Thread.sleep(SLEEP_TIME_MILLIS);
                        randoms.notifyAll();
                        randoms.wait();
                    }
                } catch (InterruptedException e) {
                    LOGGER.log(Level.WARNING, "Interrupted exception in reader implementation." + e.getMessage());
                }
            }
        }
    });

    public static void main(String[] args) throws InterruptedException {
        reader.setDaemon(true);
        writer.setDaemon(true);
        writer.start();
        reader.start();
        writer.join();
        reader.join();
    }
}
