import java.util.List;
import java.util.Map;

public class FlowRepoH2Impl implements FlowRepo{
    @Override
    public void ready(ActionBus actionBus) {

    }

    @Override
    public void done(ActionBus actionBus) {

    }

    @Override
    public void error(ActionBus actionBus) {

    }

    @Override
    public void whenHandlerSuccess(ActionBus actionBus,String actionName, Map<String, Object> params) {

    }

    @Override
    public void whenHandlerError(ActionBus actionBus,String actionName, Map<String, Object> params, Exception e) {

    }

    @Override
    public void action(ActionBus actionBus, String actionName) {

    }

    @Override
    public void trigger(ActionBus actionBus, String actionName) {

    }

    @Override
    public List<Map<String, String>> query(String busName) {
        return null;
    }

    Map<String,String> actions(){
        return null;
    }

}
