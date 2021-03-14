import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Flow {
    static Map<String, EventBus> flows = new ConcurrentHashMap<>();
    EventBus bus;
    public static void regestFlow(EventBus eventBus){
        if(flows.put(eventBus.name,eventBus)!=null){
            throw new RuntimeException("");
        };
    }
    public static void startFlow(String flowName, String event, Map<String,Object> params){
        EventBus bus = flows.get(flowName);
        bus.asyncTrigger(event,params);
    }
}
