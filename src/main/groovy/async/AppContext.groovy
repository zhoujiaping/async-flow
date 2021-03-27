package async

import util.NamedThreadFactory

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class AppContext {
    static AppContext app = new AppContext()
    Map beans = [:]
    Map beanMethods = [:]
    volatile boolean initialized

    def init() {
        int coreSize = Runtime.getRuntime().availableProcessors()
        def threadPoolExecutor = new ThreadPoolExecutor(coreSize, coreSize, 0, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(), new NamedThreadFactory('async'))
        beans['threadPoolExecutor'] = threadPoolExecutor

        def mqConsumer = new MQConsumer()
        mqConsumer.topic = 'async'
        beans['mqConsumer'] = mqConsumer

        beans['mqProducer'] = new MQProducer()

        def taskExecutor = new TaskExecutor()
        beans['taskExecutor'] = taskExecutor

        beans['creditService'] = new CreditService()
        beanMethods['creditService'] = CreditService.declaredMethods.collectEntries {
            [it.name, it]
        }
        beans['creditResultCallbackController'] = new CreditResultCallbackController()

        beans.each {
            k, v ->
                if (v.metaClass.respondsTo(v, 'init')) {
                    v.init()
                }
        }
    }

    def static getBean(String beanName) {
        if (!app.initialized) {
            synchronized (AppContext) {
                if (!app.initialized) {
                    app.initialized = true
                    app.init()
                }
            }
        }
        app.beans[beanName]
    }
}
