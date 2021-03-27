package async

class CreditResultCallbackController {
    BeanTaskExecutor executor

    def init() {
        executor = AppContext.getBean("taskExecutor", "creditService")
    }

    def callback() {
        def args = ""
        executor.async('handlerRiskResult', args)
    }
}
