package async

import com.alibaba.fastjson.JSON
import org.slf4j.LoggerFactory

import java.util.concurrent.TimeUnit

class CreditService {
    def static final logger = LoggerFactory.getLogger(CreditService)
    BeanTaskExecutor executor

    def init() {
        executor = BeanTaskExecutor.create(AppContext.getBean('taskExecutor'), 'creditService')
    }

    def creditApply(req) {
        check(req)
        "commit credit apply success"
    }

    def check(req) {
        logger.info("check...req=$req")
        executor.async(10, TimeUnit.SECONDS, 'uploadImg', JSON.toJSONString(req), [retryTimes: 0])
    }

    def uploadImg(String args, Map extra) {
        int retryTimes = extra['retryTimes']
        extra['retryTimes'] = retryTimes + 1
        String sealArgs
        Exception ex
        try {
            sealArgs = doUploadImg(args, extra)
        } catch (e) {
            ex = e
        }
        if (ex) {
            logger.error(ex.message)
            if (retryTimes > 2) {
                throw new RuntimeException("uploadImg failed, retryTimes=$retryTimes")
            } else {
                executor.async(10, TimeUnit.SECONDS, 'uploadImg', args, extra)
            }
        } else {
            executor.async('seal', sealArgs)
        }
    }
    volatile int fails = 0

    String doUploadImg(String args, Map extra) {
        logger.info("uploadImsg...args=$args,extra=$extra")
        if (fails < 2) {
            fails++
            throw new RuntimeException("uploadImg fail...")
        } else {
            logger.info("uploadImg success...")
        }
    }

    def seal(String args, Map extra) {
        logger.info("seal...args=$args,extra=$extra")
        executor.async(10, TimeUnit.SECONDS, 'risk', args)
    }

    def risk(String args, Map extra) {
        logger.info("risk...args=$args,extra=$extra")
        //waiting for trigger handleRiskResult
    }

    def handlRiskResult(String args, Map extra) {
        logger.info("handleRiskResult...args=$args,extra=$extra")
    }

}
