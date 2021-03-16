import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

public class FlowController {
    FlowRepo repo;
    //如果某个事件处理器中有依赖其他事件放置的参数，那么可能触发它可能会出现取不到数据的情况
    public void resume(String busName){
        ActionBus bus = ActionBus.get(busName);
        List<Map<String,String>> info = repo.query(busName);
        info.forEach(it->{
            bus.trigger(it.get("action"), JSONObject.parseObject(it.get("params")));
        });
    }
}
