import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ActionBus {
    public static final String READY = "#ready";
    public static final String DONE = "#done";
    public static final String ERROR = "#error";

    protected static Logger logger = LoggerFactory.getLogger(ActionBus.class);
    protected String name;
    protected ScheduledThreadPoolExecutor executor;
    protected FlowRepo repo;

    public ActionBus executor(ScheduledThreadPoolExecutor executor){
        this.executor = executor;
        return this;
    }
    public ActionBus repo(FlowRepo repo){
        this.repo = repo;
        return this;
    }


    protected Map<String, Consumer<Map<String, Object>>> actions = new LinkedHashMap<>();

    protected static final Map<String,ActionBus> busMap = new ConcurrentHashMap<>();
    public static void put(ActionBus creditEventBus) {
        busMap.put(creditEventBus.name,creditEventBus);
    }
    public static ActionBus get(String id){
        return busMap.get(id);
    }

    public ActionBus action(String actionName, Consumer<Map<String, Object>> action) {
        if(actionName.startsWith("#")){
            logger.warn("actionName starts with # is used for internal!");
        }
        //repo.action(this,actionName);
        actions.put(actionName, params -> {
            try {
                action.accept(params);
                repo.whenHandlerSuccess(this,actionName, params);
            } catch (Exception e) {
                repo.whenHandlerError(this,actionName, params, e);
                throw e;
            }
        });
        return this;
    }
    public ActionBus readyAction(Consumer<Map<String, Object>> action) {
        repo.ready(this);
        return action(READY,action);
    }
    public ActionBus errorAction(Consumer<Map<String, Object>> action) {
        repo.error(this);
        return action(ERROR,action);
    }
    public ActionBus ready(Map<String, Object> params){
        return trigger(READY,params);
    }
    public ActionBus done(Map<String, Object> params){
        repo.done(this);
        return this;
    }
    public ActionBus error(Map<String, Object> params){
        return trigger(ERROR,params);
    }

    public ActionBus(String name) {
        this.name = name;
    }

    //参数建议用比较通用的map、list，这样可以实现任意从节点执行流程的功能
    public ActionBus trigger(String actionName, Map<String, Object> params) {
        return timeoutTrigger(actionName, 0, params);
    }

    public ActionBus timeoutTrigger(String actionName, int delaySeconds, Map<String, Object> params) {
        repo.trigger(this,actionName);
        executor.schedule(() -> {
            try {
                actions.get(actionName).accept(params);
            } catch (Exception e) {
                logger.error("未被捕获的异常", e);
            }
        }, delaySeconds, TimeUnit.SECONDS);
        return this;
    }

    //重试n次+第一次，共执行n+1次
    public ActionBus retryTrigger(String actionName, int intervalSeconds, int retryTimes, Map<String, Object> params) {
        retryTrigger0(actionName, intervalSeconds, retryTimes, params, null);
        return this;
    }

    private void retryTrigger0(String actionName, int intervalSeconds, int retryTimes, Map<String, Object> params, Exception error) {
        if (retryTimes < 0) {
            if (error == null) {
                throw new RuntimeException("retryTimes不能小于0");
            } else {
                throw new RuntimeException("处理事件" + this.name + "#" + actionName + "异常", error);
            }
        }
        executor.schedule(() -> {
            try {
                actions.get(actionName).accept(params);
            } catch (Exception e) {
                logger.error("处理{}事件异常,retryTimes={}", actionName, retryTimes, e);
                retryTrigger0(actionName, intervalSeconds, retryTimes - 1, params, e);
            }
        }, intervalSeconds, TimeUnit.SECONDS);
    }
}
