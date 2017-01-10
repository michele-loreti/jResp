package policy;

import org.cmg.jresp.policy.*;
import org.cmg.jresp.policy.automaton.*;
import org.cmg.jresp.policy.facpl.*;
import org.cmg.jresp.policy.facpl.algorithm.*;
import org.cmg.jresp.policy.facpl.elements.*;
import org.cmg.jresp.policy.facpl.elements.util.*;
import org.cmg.jresp.policy.facpl.function.arithmetic.Add;
import org.cmg.jresp.policy.facpl.function.comparison.*;
import org.cmg.jresp.simulation.policy.*;
import org.cmg.jresp.knowledge.*;
import org.cmg.jresp.topology.*;

@SuppressWarnings("unused")
public class PClient extends PolicySet {

	public PClient() {

		addId("PClient");

		addCombiningAlg(PermitUnlessDeny.class);

		addElement(new C1());
		addElement(new C2());
		addElement(new C3());
	}

	class C1 extends Rule {

		C1() {
			addId("C1");

			addEffect(RuleEffect.PERMIT);

			addTarget(new TargetTreeRepresentation(TargetConnector.AND,
					new TargetTreeRepresentation(
							TargetConnector.AND,
							new TargetTreeRepresentation(new TargetExpression(Equal.class,
									new RequestAttributeName("action", "id"), ActionThisID.PUT)),
							new TargetTreeRepresentation(
									new TargetExpression(PatternMatch.class, new RequestAttributeName("action", "arg"),
											new Template(new ActualTemplateField(("task")),
													new FormalTemplateField(String.class))))),
					new TargetTreeRepresentation(new TargetExpression(Equal.class,
							new RequestAttributeName("subject", "id"), ActionThisID.THIS))));

			addObligation(new ScelObligationExpression(RuleEffect.PERMIT, ObligationType.AFTER, ActionThisID.GET,
					new Template(new ActualTemplateField(("taskId")), new FormalTemplateField(Integer.class, "num")),
					(Self.SELF)));
			addObligation(new ScelObligationExpression(RuleEffect.PERMIT, ObligationType.AFTER, ActionThisID.PUT,
					new Tuple(new RequestAttributeName("action", "arg"),
							(new FormalObligationVariable<Integer>("num"))),
					(Self.SELF)));
			addObligation(new ScelObligationExpression(RuleEffect.PERMIT, ObligationType.AFTER, ActionThisID.PUT,
					new Tuple(("taskId"),
							new ExpressionItem(Add.class, new FormalObligationVariable<Integer>("num"), 1)),
					(Self.SELF)));
		}
	}

	class C2 extends Rule {

		C2() {
			addId("C2");

			addEffect(RuleEffect.PERMIT);

			addTarget(new TargetTreeRepresentation(TargetConnector.AND,
					new TargetTreeRepresentation(new TargetExpression(Equal.class,
							new RequestAttributeName("action", "id"), ActionThisID.GET)),
					new TargetTreeRepresentation(
							new TargetExpression(Equal.class, new RequestAttributeName("subject", "role"), "server"))));

			addObligation(new ScelObligationExpression(RuleEffect.PERMIT, ObligationType.AFTER, ActionThisID.PUT,
					new Tuple(("log"), ("task retrieved"), new RequestAttributeName("subject", "id")),
					new Group(new HasValue("role", ("gateway")))));
		}
	}

	class C3 extends Rule {

		C3() {
			addId("C3");

			addEffect(RuleEffect.DENY);

			addTarget(new TargetTreeRepresentation(TargetConnector.AND,
					new TargetTreeRepresentation(
							TargetConnector.AND,
							new TargetTreeRepresentation(new TargetExpression(Equal.class,
									new RequestAttributeName("action", "id"), ActionThisID.PUT)),
							new TargetTreeRepresentation(new TargetExpression(Equal.class,
									new RequestAttributeName("object", "id"), ActionThisID.THIS))),
					new TargetTreeRepresentation(
							new TargetExpression(GreaterThan.class, new RequestAttributeName("object", "load"), 90))));

		}
	}

	public static PolicyAutomaton getAutomaton() {
		final PolicyAutomaton policy_automaton = new PolicyAutomaton(new FacplPolicyState(new PClient()));
		return policy_automaton;
	}

	public static SimulationPolicyAutomaton getSimulationAutomaton() {
		final SimulationPolicyAutomaton policy_automaton = new SimulationPolicyAutomaton(
				new FacplPolicyState(new PClient()));
		return policy_automaton;
	}

}
