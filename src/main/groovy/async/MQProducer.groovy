package async

import util.DelayTask

import java.util.concurrent.DelayQueue

class MQProducer {
    Map<String, DelayQueue<DelayTask>> queues = [
            async: new DelayQueue<>()]
}
