package async

import org.slf4j.LoggerFactory

import java.util.concurrent.TimeUnit

class BeanTaskExecutor {
    def static final logger = LoggerFactory.getLogger(BeanTaskExecutor)
    String beanName
    TaskExecutor taskExecutor

    static BeanTaskExecutor create(TaskExecutor taskExecutor, String beanName) {
        new BeanTaskExecutor([
                beanName    : beanName,
                taskExecutor: taskExecutor
        ])
    }

    def async(String method, String args, Map extra) {
        taskExecutor.async(new Task([
                beanName: beanName,
                method  : method,
                args    : args,
                extra   : extra
        ]))
    }

    def async(String method, String args) {
        async(method, args, [:])
    }

    def async(long delay, TimeUnit unit, String method, String args) {
        async(delay, unit, method, args, [:])
    }

    def async(long delay, TimeUnit unit, String method, String args, Map extra) {
        taskExecutor.async(delay, unit, new Task([
                beanName: beanName,
                method  : method,
                args    : args,
                extra   : extra
        ]))
    }
}
