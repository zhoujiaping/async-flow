package util

import java.util.concurrent.Delayed
import java.util.concurrent.TimeUnit

class DelayTask implements Delayed {
    long time
    Closure handler
    String msg

    @Override
    long getDelay(TimeUnit unit) {
        unit.convert(time - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
    }

    @Override
    int compareTo(Delayed o) {
        getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS)
    }
}
