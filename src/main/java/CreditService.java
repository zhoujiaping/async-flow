import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

public class CreditService {
    Logger logger = LoggerFactory.getLogger(EventBus.class);
    EventBus creditEventBus = new EventBus("credit");
    @PostConstruct
    public void init(){
        creditEventBus.on("ready",params->{
            Object creditReq = params.get("creditReq");
            saveFlow();
            check();
            params.put("xxx","yyy");
            //creditEventBus.asyncTrigger("check-end", params);
            creditEventBus.triggerIntervalIfError("check-end",10,3,params);
        }).on("check-end",params->{
            seal();
            creditEventBus.asyncTrigger("seal-end", params);
        }).on("seal-end",params->{
            risk();
            creditEventBus.asyncTrigger("risk-end", params);
        }).on("risk-end",params->{
            api();
            creditEventBus.asyncTrigger("api-end", params);
        }).on("api-end",params->{
            //
        }).on("seal-error",params->{
            updateCredit9999();
            creditEventBus.asyncTrigger("finished",params);
        });
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
        creditEventBus.asyncTrigger(eventName,Maps.of("creditReq",bizData));
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
