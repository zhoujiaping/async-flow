package async

import com.alibaba.fastjson.JSON

class Task {
    String uid = UUID.randomUUID().toString()
    String beanName
    String method
    String args
    Map extra = [:]

    @Override
    String toString() {
        JSON.toJSONString(this)
    }
}
