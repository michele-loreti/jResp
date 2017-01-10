package examples;

import java.util.Random;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.Attribute;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.knowledge.ts.random.RandomSpace;
import org.cmg.jresp.policy.ActionThisID;
import org.cmg.jresp.policy.RequestAttributeName;
import org.cmg.jresp.policy.automaton.PolicyAutomaton;
import org.cmg.jresp.policy.facpl.FacplPolicyState;
import org.cmg.jresp.policy.facpl.ObligationType;
import org.cmg.jresp.policy.facpl.RuleEffect;
import org.cmg.jresp.policy.facpl.algorithm.DenyUnlessPermit;
import org.cmg.jresp.policy.facpl.algorithm.PermitUnlessDeny;
import org.cmg.jresp.policy.facpl.elements.ExpressionItem;
import org.cmg.jresp.policy.facpl.elements.PolicySet;
import org.cmg.jresp.policy.facpl.elements.Rule;
import org.cmg.jresp.policy.facpl.elements.ScelObligationExpression;
import org.cmg.jresp.policy.facpl.elements.TargetConnector;
import org.cmg.jresp.policy.facpl.elements.TargetExpression;
import org.cmg.jresp.policy.facpl.elements.util.FormalObligationVariable;
import org.cmg.jresp.policy.facpl.elements.util.TargetTreeRepresentation;
import org.cmg.jresp.policy.facpl.function.arithmetic.Add;
import org.cmg.jresp.policy.facpl.function.comparison.Equal;
import org.cmg.jresp.policy.facpl.function.comparison.GreaterThan;
import org.cmg.jresp.policy.facpl.function.comparison.PatternMatch;
import org.cmg.jresp.topology.Group;
import org.cmg.jresp.topology.HasValue;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Self;
import org.cmg.jresp.topology.VirtualPort;

public class Chapter_4_SecureSocialNetwork {

	// In this example we will create several nodes
	// and agents will communicate also to remote nodes.
	// For this purpose we create a virtual port to which all nodes will be connected
	protected static VirtualPort vp = new VirtualPort(8080);

	public static void main(String[] argv) {

		// Access control policy fo the bag
		PolicyAutomaton	p = new PolicyAutomaton();
		FacplPolicyState start = new FacplPolicyState(new SocialNetworkPolicy());
		p.addState(start);

		Node UserSpace = new Node("UserSpace",new RandomSpace());
		UserSpace.addPort(vp);
		UserSpace.start();

		// We create some user nodes
		Node Alice = new Node("Alice",new TupleSpace());
		Alice.addPort(vp);
		Alice.setPolicy(p);
		Agent a = new User("Alice");
		Alice.addAgent(a);

		Node Bob = new Node("Bob",new TupleSpace());
		Bob.addPort(vp);
		Bob.setPolicy(p);
		Agent b = new User("Bob");
		Bob.addAgent(b);
		
		Node Charlie = new Node("Charlie",new TupleSpace());
		Charlie.addPort(vp);
		Charlie.setPolicy(p);
		Agent c = new User("Charlie");
		Charlie.addAgent(c);

		Alice.start();
		Bob.start();
		Charlie.start();
		
		// A malicious user
		Node Hacker = new Node("Hacker",new TupleSpace());
		Hacker.addPort(vp);
		Agent h = new Hacker("Hacker");
		Hacker.addAgent(h);
		Hacker.start();

	}

	public static class User extends Agent {

		PointToPoint friend, UserSpace;

		// This constructor records the name of the agent
		public User(String node_name) {
			super(node_name);
		}

		// This is the function invoked when the agent starts running in a node
		@Override
		protected void doRun() {
			Tuple t;
			Random random = new Random();
			
			try {
				System.out.println(name + " login in...");
				UserSpace = new PointToPoint("UserSpace", vp.getAddress());
				put(new Tuple(name),UserSpace);
				while (true) {
					Thread.sleep(1000+(random.nextInt(5)*1000));
					System.out.println(name + " looking for a friend...");
					t = query(new Template(new FormalTemplateField(String.class)),UserSpace);
					friend = new PointToPoint(t.getElementAt(String.class, 0), vp.getAddress());
					if (friend.getName() == name) continue;
					Thread.sleep(1000+(random.nextInt(5)*1000));
					System.out.println(name + " posting a message to " + friend.getName() + "'s board...");
					put(new Tuple(name,"hi!"),friend);
					Thread.sleep(1000+(random.nextInt(5)*1000));
					System.out.println(name + " reading a message from " + friend.getName() + "'s board...");
					query(new Template(new FormalTemplateField(String.class),new FormalTemplateField(String.class)),friend);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	public static class Hacker extends Agent {

		PointToPoint user, UserSpace;

		// This constructor records the name of the agent
		public Hacker(String node_name) {
			super(node_name);
		}

		// This is the function invoked when the agent starts running in a node
		@Override
		protected void doRun() {
			Tuple t;
			Random random = new Random();
			
			try {
				UserSpace = new PointToPoint("UserSpace", vp.getAddress());
				while (true) {
					Thread.sleep(1000+(random.nextInt(5)*1000));
					System.out.println(name + " looking for a user...");
					t = query(new Template(new FormalTemplateField(String.class)),UserSpace);
					user = new PointToPoint(t.getElementAt(String.class, 0), vp.getAddress());
					Thread.sleep(1000+(random.nextInt(5)*1000));
					System.out.println(name + " deleting a message from " + user.getName() + "'s board...");
					get(new Template(new FormalTemplateField(String.class),new FormalTemplateField(String.class)),user);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static class SocialNetworkPolicy extends PolicySet {

		public SocialNetworkPolicy() {
			addId("SocialNetworkPolicy");
			addCombiningAlg(DenyUnlessPermit.class);
			//addCombiningAlg(PermitUnlessDeny.class);
			addElement(new AllowLocalRegister());
			addElement(new AllowLocalLookForFriends());
			addElement(new AllowLocalPut());
			addElement(new AllowLocalGet());
			addElement(new AllowLocalQuery());
			addElement(new AllowRemotePut());
			addElement(new AllowRemoteQuery());
		}
		
		class AllowLocalRegister extends Rule {

			AllowLocalRegister() {
				addId("AllowLocalRegister");

				addEffect(RuleEffect.PERMIT);

				addTarget(new TargetTreeRepresentation(TargetConnector.AND,
						new TargetTreeRepresentation(
								TargetConnector.AND,
								new TargetTreeRepresentation(new TargetExpression(Equal.class,
										new RequestAttributeName("action", "id"), ActionThisID.PUT)),
								new TargetTreeRepresentation(
										new TargetExpression(PatternMatch.class, new RequestAttributeName("action", "arg"),
												new Template(new FormalTemplateField(String.class)
														)))),
						new TargetTreeRepresentation(new TargetExpression(Equal.class,
								new RequestAttributeName("subject", "id"), ActionThisID.THIS))));
			}
		}
		
		class AllowLocalLookForFriends extends Rule {

			AllowLocalLookForFriends() {
				addId("AllowLocalRegister");

				addEffect(RuleEffect.PERMIT);

				addTarget(new TargetTreeRepresentation(TargetConnector.AND,
						new TargetTreeRepresentation(
								TargetConnector.AND,
								new TargetTreeRepresentation(new TargetExpression(Equal.class,
										new RequestAttributeName("action", "id"), ActionThisID.QRY)),
								new TargetTreeRepresentation(
										new TargetExpression(PatternMatch.class, new RequestAttributeName("action", "arg"),
												new Template(new FormalTemplateField(String.class)
														)))),
						new TargetTreeRepresentation(new TargetExpression(Equal.class,
								new RequestAttributeName("subject", "id"), ActionThisID.THIS))));
			}
		}

		class AllowLocalPut extends Rule {

			AllowLocalPut() {
				addId("AllowLocalPut");

				addEffect(RuleEffect.PERMIT);

				addTarget(new TargetTreeRepresentation(TargetConnector.AND,
						new TargetTreeRepresentation(
								TargetConnector.AND,
								new TargetTreeRepresentation(new TargetExpression(Equal.class,
										new RequestAttributeName("action", "id"), ActionThisID.PUT)),
								new TargetTreeRepresentation(
										new TargetExpression(PatternMatch.class, new RequestAttributeName("action", "arg"),
												new Template(new FormalTemplateField(String.class),
														new FormalTemplateField(String.class))))),
						new TargetTreeRepresentation(new TargetExpression(Equal.class,
								new RequestAttributeName("subject", "id"), ActionThisID.THIS))));
			}
		}

		class AllowLocalGet extends Rule {

			AllowLocalGet() {
				addId("AllowLocalGet");

				addEffect(RuleEffect.PERMIT);

				addTarget(new TargetTreeRepresentation(TargetConnector.AND,
						new TargetTreeRepresentation(
								TargetConnector.AND,
								new TargetTreeRepresentation(new TargetExpression(Equal.class,
										new RequestAttributeName("action", "id"), ActionThisID.GET)),
								new TargetTreeRepresentation(
										new TargetExpression(PatternMatch.class, new RequestAttributeName("action", "arg"),
												new Template(new FormalTemplateField(String.class),
														new FormalTemplateField(String.class))))),
						new TargetTreeRepresentation(new TargetExpression(Equal.class,
								new RequestAttributeName("subject", "id"), ActionThisID.THIS))));
			}
		}

		class AllowLocalQuery extends Rule {

			AllowLocalQuery() {
				addId("AllowLocalQuery");

				addEffect(RuleEffect.PERMIT);

				addTarget(new TargetTreeRepresentation(TargetConnector.AND,
						new TargetTreeRepresentation(
								TargetConnector.AND,
								new TargetTreeRepresentation(new TargetExpression(Equal.class,
										new RequestAttributeName("action", "id"), ActionThisID.QRY)),
								new TargetTreeRepresentation(
										new TargetExpression(PatternMatch.class, new RequestAttributeName("action", "arg"),
												new Template(new FormalTemplateField(String.class),
														new FormalTemplateField(String.class))))),
						new TargetTreeRepresentation(new TargetExpression(Equal.class,
								new RequestAttributeName("subject", "id"), ActionThisID.THIS))));
			}
		}

		class AllowRemotePut extends Rule {

			AllowRemotePut() {
				addId("FullAccessToSelf");

				addEffect(RuleEffect.PERMIT);

				addTarget(new TargetTreeRepresentation(TargetConnector.AND,
						new TargetTreeRepresentation(
								TargetConnector.AND,
								new TargetTreeRepresentation(new TargetExpression(Equal.class,
										new RequestAttributeName("action", "id"), ActionThisID.ACCEPT_PUT)),
								new TargetTreeRepresentation(
										new TargetExpression(PatternMatch.class, new RequestAttributeName("action", "arg"),
												new Template(new FormalTemplateField(String.class),
														new FormalTemplateField(String.class))))),
						new TargetTreeRepresentation(new TargetExpression(Equal.class,
								new RequestAttributeName("subject", "id"), ActionThisID.THIS))));
			}
		}


		class AllowRemoteQuery extends Rule {

			AllowRemoteQuery() {
				addId("AllowRemoteQuery");

				addEffect(RuleEffect.PERMIT);

				addTarget(new TargetTreeRepresentation(TargetConnector.AND,
						new TargetTreeRepresentation(
								TargetConnector.AND,
								new TargetTreeRepresentation(new TargetExpression(Equal.class,
										new RequestAttributeName("action", "id"), ActionThisID.ACCEPT_QRY)),
								new TargetTreeRepresentation(
										new TargetExpression(PatternMatch.class, new RequestAttributeName("action", "arg"),
												new Template(new FormalTemplateField(String.class),
														new FormalTemplateField(String.class))))),
						new TargetTreeRepresentation(new TargetExpression(Equal.class,
								new RequestAttributeName("subject", "id"), ActionThisID.THIS))));
			}
		}

	}

}