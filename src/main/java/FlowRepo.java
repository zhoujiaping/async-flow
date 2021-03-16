import java.util.List;
import java.util.Map;

/**
 * ready的时候，保存一条流程，当前动作为ready
 * done的时候，将流程的当前动作更新为done
 *
 * */
public interface FlowRepo {

    void ready(ActionBus actionBus);

    void done(ActionBus actionBus);

    void error(ActionBus actionBus);

    void whenHandlerSuccess(ActionBus actionBus,String actionName, Map<String, Object> params);

    void whenHandlerError(ActionBus actionBus,String actionName, Map<String, Object> params, Exception e);

    void action(ActionBus actionBus, String actionName);

    void trigger(ActionBus actionBus, String actionName);

    List<Map<String, String>> query(String busName);
}
