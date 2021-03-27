package async

import org.slf4j.LoggerFactory
import util.DelayTask

import java.util.concurrent.TimeUnit

class MQConsumer {
    def static final logger = LoggerFactory.getLogger(MQConsumer)
    String topic
    Closure onMessage
    MQProducer producer
    Thread thread = new Thread({
        for (; !Thread.currentThread().isInterrupted();) {
            def delayTask = producer.queues[topic].take()
            delayTask.handler(delayTask.msg)
        }
    })

    def init() {
        producer = AppContext.getBean("mqProducer")
        thread.start()
    }

    def shutdown() {
        thread.interrupt()
        while (thread.isAlive()) {
            logger.info("waiting for MQConsumer.thread to be shutdown...")
        }
    }

    def sendDelayMsg(long delay, TimeUnit unit, String msg) {
        producer.quques[topic].add(new DelayTask([
                time   : System.currentTimeMillis() - unit.toMillis(delay),
                msg    : msg,
                handler: onMessage
        ]))
    }
}
