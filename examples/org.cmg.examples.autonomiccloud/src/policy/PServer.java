package policy;
		
import org.cmg.jresp.policy.*;
import org.cmg.jresp.policy.automaton.*;
import org.cmg.jresp.policy.facpl.*;
import org.cmg.jresp.policy.facpl.algorithm.*;
import org.cmg.jresp.policy.facpl.elements.*;
import org.cmg.jresp.policy.facpl.elements.util.*;
import org.cmg.jresp.policy.facpl.function.comparison.*;
import org.cmg.jresp.simulation.policy.*;
import org.cmg.jresp.knowledge.*;
import org.cmg.jresp.topology.*;
	
@SuppressWarnings("unused")
public class PServer extends PolicySet {
	
	public PServer (){
	
	addId("PServer");
	
	addCombiningAlg(PermitUnlessDeny.class);
	
	addElement(new S1());
	addElement(new S2());
	addElement(new S3());
	addElement(new S4());
	}
	
	class S1 extends Rule {
		
		S1 (){
			addId("S1");
			
			addEffect(RuleEffect.DENY);
			
			addTarget(new TargetTreeRepresentation(TargetConnector.AND, new TargetTreeRepresentation(TargetConnector.AND, new TargetTreeRepresentation(TargetConnector.AND, new TargetTreeRepresentation(TargetConnector.AND, new TargetTreeRepresentation(new TargetExpression(Equal.class,new RequestAttributeName("action","id")
			,ActionThisID.GET
			)) 
			, new TargetTreeRepresentation(new TargetExpression(Equal.class,new RequestAttributeName("subject","id")
			,ActionThisID.THIS
			)) 
			)
			, new TargetTreeRepresentation(new TargetExpression(PatternMatch.class,new RequestAttributeName("action","arg")
			,new Template( new ActualTemplateField(("task")),  new FormalTemplateField(String.class),  new FormalTemplateField(Integer.class) 
			)
			)) 
			)
			, new TargetTreeRepresentation(new TargetExpression(Equal.class,new RequestAttributeName("subject","level")
			,1
			)) 
			)
			, new TargetTreeRepresentation(new TargetExpression(Equal.class,new RequestAttributeName("object","level")
			,2
			)) 
			)
			);
			
		}
	}
	class S2 extends Rule {
		
		S2 (){
			addId("S2");
			
			addEffect(RuleEffect.DENY);
			
			addTarget(new TargetTreeRepresentation(TargetConnector.AND, new TargetTreeRepresentation(TargetConnector.AND, new TargetTreeRepresentation(TargetConnector.AND, new TargetTreeRepresentation(new TargetExpression(Equal.class,new RequestAttributeName("action","id")
			,ActionThisID.GET
			)) 
			, new TargetTreeRepresentation(new TargetExpression(Equal.class,new RequestAttributeName("subject","id")
			,ActionThisID.THIS
			)) 
			)
			, new TargetTreeRepresentation(new TargetExpression(PatternMatch.class,new RequestAttributeName("action","arg")
			,new Template( new ActualTemplateField(("task")),  new FormalTemplateField(String.class),  new FormalTemplateField(Integer.class) 
			)
			)) 
			)
			, new TargetTreeRepresentation(new TargetExpression(GreaterThan.class,new RequestAttributeName("subject","load")
			,90
			)) 
			)
			);
			
			addObligation(
				new ScelObligationExpression(RuleEffect.DENY,ObligationType.BEFORE,ActionThisID.READ,"load"
				)
			); 
		}
	}
	class S3 extends Rule {
		
		S3 (){
			addId("S3");
			
			addEffect(RuleEffect.DENY);
			
			addTarget(new TargetTreeRepresentation(TargetConnector.AND, new TargetTreeRepresentation(TargetConnector.AND, new TargetTreeRepresentation(new TargetExpression(Equal.class,new RequestAttributeName("action","id")
			,ActionThisID.READ
			)) 
			, new TargetTreeRepresentation(new TargetExpression(PatternMatch.class,new RequestAttributeName("action","arg")
			,"load"
			)) 
			)
			, new TargetTreeRepresentation(new TargetExpression(GreaterThan.class,new RequestAttributeName("subject","load")
			,60
			)) 
			)
			);
			
		}
	}
	class S4 extends Rule {
		
		S4 (){
			addId("S4");
			
			addEffect(RuleEffect.PERMIT);
			
			addTarget(new TargetTreeRepresentation(TargetConnector.AND, new TargetTreeRepresentation(new TargetExpression(Equal.class,new RequestAttributeName("action","id")
			,ActionThisID.PUT
			)) 
			, new TargetTreeRepresentation(new TargetExpression(Equal.class,new RequestAttributeName("subject","id")
			,ActionThisID.THIS
			)) 
			)
			);
			
			addObligation(
				new ScelObligationExpression(RuleEffect.PERMIT,ObligationType.BEFORE,ActionThisID.PUT, new Tuple(("log"),
				new RequestAttributeName("action","arg")
				),(Self.SELF)
				)
			); 
		}
	}
	
	public static PolicyAutomaton getAutomaton(){
		final PolicyAutomaton policy_automaton = new PolicyAutomaton(
			new FacplPolicyState(new PServer())
		);
		return policy_automaton;	
	}
	
	public static SimulationPolicyAutomaton getSimulationAutomaton(){
		final SimulationPolicyAutomaton policy_automaton = new SimulationPolicyAutomaton(
			new FacplPolicyState(new PServer())
		);
		return policy_automaton;					
	}
	
}
