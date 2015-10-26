import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ManagementService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * A ProcessTest is ...
 * @author dmitry.serdyuk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testContext.xml"})
public class ProcessTest
{
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();
    
    @SuppressWarnings("unchecked")
    @Test
    @Deployment(resources="diagrams/p_verify.bpmn20.xml")
    public void sampleTest() {
        Map<Long,String> stdMsgs = new HashMap<Long, String>();
        stdMsgs.put(1L,"First message");
        stdMsgs.put(2L,"Second message");
        List<String> errorMsgs = new ArrayList<String>();
        Map<String,Object> initialVars = new HashMap<String,Object>();
        initialVars.put("stdMsgs",stdMsgs);
        initialVars.put("errorMsgs",errorMsgs);
        
        //Start the process
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("p_verify", initialVars);
        
        //Verify process variables
        Map<String, Object> afterVariables = runtimeService.getVariables(processInstance.getId());
        List<String> afterErrorMsgs = (List<String>) afterVariables.get("errorMsgs");
        assertEquals(2, afterErrorMsgs.size());
        assertEquals("First message", afterErrorMsgs.get(0));
        assertEquals("Second message", afterErrorMsgs.get(1));

        
        //Finish the process
        Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).activityId("pauseForTest").singleResult();
        runtimeService.signal(execution.getId());
    }
}
