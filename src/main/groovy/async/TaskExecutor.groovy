package async

import com.alibaba.fastjson.JSON
import org.slf4j.LoggerFactory

import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class TaskExecutor {
    static final def logger = LoggerFactory.getLogger(TaskExecutor)
    ThreadPoolExecutor executor
    TaskRepo taskRepo
    MQConsumer mqConsumer

    def init() {
        taskRepo = AppContext.getBean()
        executor = AppContext.getBean('threadPoolExecutor')
        mqConsumer = AppContext.getBean('mqConsumer')
        mqConsumer.onMessage = {
            msg ->
                executor.execute {
                    try {
                        Task task = JSON.parse(msg)
                        taskRepo.updateTaskToProcessing(task)
                        task.execute()
                        taskRepo.updateTaskToDone(task)
                    } catch (e) {
                        taskRepo.updateTaskToError(e)
                        throw e
                    }
                }
        }
    }

    def async(Task task) {
        taskRepo.insertTask(task) {
            mqConsumer.sendDelayMsg(0, TimeUnit.SECONDS, task.toString())
        }
    }

    def async(long delay, TimeUnit unit, Task task) {
        taskRepo.insertTask(task) {
            mqConsumer.sendDelayMsg(delay, unit, task.toString())
        }
    }

    def shutdown() {
        executor.shutdown()
        while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
            logger.info("waiting for TaskExecutor.executor to be shutdown...")
        }
    }
}
