import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

public class CreditService {
    Logger logger = LoggerFactory.getLogger(ActionBus.class);
    ActionBus creditEventBus = new ActionBus("credit");
    @PostConstruct
    public void init(){
        creditEventBus.readyAction(params->{
            Object creditReq = params.get("creditReq");
            saveFlow();
            check();
            params.put("xxx","yyy");
            //creditEventBus.asyncTrigger("check-end", params);
            creditEventBus.retryTrigger("seal",10,3,params);
        }).action("seal", params->{
            seal();
            creditEventBus.trigger("risk", params);
        }).action("risk", params->{
            risk();
            creditEventBus.trigger("api", params);
        }).action("api", params->{
            try {
                api();
                creditEventBus.done(params);
            }catch (Exception e){
                updateCredit9999();
                creditEventBus.error(params);
            }
        });
        ActionBus.put(creditEventBus);
        //Flow.regestFlow(creditEventBus);
        //query flows which is not finished!
    }

    private void updateCredit9999() {
    }

    private void saveFlow() {
    }

    public void credit(Object bizData,String eventName){
        if(eventName == null || eventName.trim().equals("")){
            eventName = "ready";
        }
        creditEventBus.ready(Maps.of("creditReq",bizData));
        //creditEventBus.asyncTrigger(eventName,Maps.of("creditReq",bizData));
        //Flow.startFlow("credit",action,Maps.of("creditReq",bizData));
    }
    private void seal(){
        try{

        }catch (Exception e){
            //...
        }
    }
    private void check(){

    }
    private void risk(){

    }
    private void api(){

    }

}
