import io.netty.util.HashedWheelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class EventBus {
    Logger logger = LoggerFactory.getLogger(EventBus.class);
    String id;
    String name;
    //ScheduledThreadPoolExecutor executor;
    HashedWheelTimer wheelTimer;

    public void setWheelTimer(HashedWheelTimer wheelTimer) {
        this.wheelTimer = wheelTimer;
    }

    Map<String, Consumer<Map<String,Object>>> handlers = new LinkedHashMap<>();

    public EventBus on(String eventName, Consumer<Map<String,Object>> handler){
        handlers.put(eventName, params->{
            try {
                handler.accept(params);
                onHandleSuccess(eventName,params);
            }catch (Exception e){
                onHandleError(eventName,params,e);
                throw e;
            }
        });

        return this;
    }

    public EventBus(String name){
        this.name = name;
        id = UUID.randomUUID().toString().replaceAll("-","");
    }
    /*
    public EventBus trigger(String eventName){
        handlers.get(eventName).accept(this);
        return this;
    }*/
    //参数建议用比较通用的map、list，这样可以实现任意从节点执行流程的功能
    public EventBus asyncTrigger(String eventName, Map<String, Object> params){
        wheelTimer.newTimeout((timeout)->{
            try {
                handlers.get(eventName).accept(params);
            }catch (Exception e){
                logger.error("未被捕获的异常",e);
            }
        },0, TimeUnit.SECONDS);
        return this;
    }
    public EventBus timeoutTrigger(String eventName, int delaySeconds, Map<String, Object> params){
        wheelTimer.newTimeout((timeout)->{
            try {
                handlers.get(eventName).accept(params);
            }catch (Exception e){
                logger.error("未被捕获的异常",e);
            }
        },delaySeconds,TimeUnit.SECONDS);
        return this;
    }
    public EventBus triggerIntervalIfError(String eventName, int intervalSeconds, int times, Map<String, Object> params){
        if(times<=0){
            return this;
        }
        wheelTimer.newTimeout((timeout)->{
            try {
                handlers.get(eventName).accept(params);
            }catch (Exception e){
                logger.error("error when handle event {}",eventName,e);
                triggerIntervalIfError(eventName,intervalSeconds,times-1,params);
            }
        },intervalSeconds,TimeUnit.SECONDS);
        return this;
    }

    private void onHandleSuccess(String eventName, Map<String, Object> params) {
        //update state of flow in database
    }

    private void onHandleError(String eventName, Map<String, Object> params, Exception e) {
        //update state of flow in database
    }

}
