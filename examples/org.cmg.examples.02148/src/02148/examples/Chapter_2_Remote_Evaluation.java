package examples;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Self;
import org.cmg.jresp.topology.VirtualPort;

public class Chapter_2_Remote_Evaluation {

	// This virtual port will be used by all the nodes
	public static VirtualPort vp = new VirtualPort(8080);

	public static void main(String[] argv) {

		// We first create the server node
		Node server = new Node("server", new TupleSpace());
		// ... and the agent for serving code execution requests
		Agent code_server = new Server("server");
		server.addAgent(code_server);
		server.addPort(vp);
		// Note that we start the node since clients need to communicate with the node
		// starting the server *before* avoids clients to crash
		server.start();

		int N = 1;
		Node[] client = new Node[N];
		Agent[] clients = new Client[N];
		for(int i=0; i<N; i++){
			client[i] = new Node("client"+i, new TupleSpace());
			clients[i] = new Client(i);
			client[i].addAgent(clients[i]);
			client[i].addPort(vp);
		}
		
		for(int i=0; i<N; i++){
			client[i].start();
		}

	}

	// This agent providing the factorial function is the one that clients will request to execute
	public static class Fact extends Agent {
		
		protected int n;

		// The constructor specified the argument to the factorial function
		public Fact(int n) {
			super("Factorial");
			this.n = n;
		}

		// This is the function invoked when the agent starts running in a node
		@Override
		protected void doRun() {
			try { 
				System.out.println(name + " running Fact(" + n + ")...");
				int fact = 1; 
		        for (int i = 1; i <= n; i++) {
		            fact *= i;
		        }
		        // The result is put in the current node (a sandbox)
				put(new Tuple("result",fact),Self.SELF);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static class Client extends Agent {

		protected PointToPoint server;
		protected int i;

		// This constructor records the name of the agent
		public Client(int i) {
			super("client"+i);
			this.i = i ;
			// We need to create a target for the server
			server = new PointToPoint("server", vp.getAddress());
		}

		@Override
		protected void doRun() {
			Agent prog = new Fact(i);
			Tuple sandboxT,resultT;
			PointToPoint sandbox;
			try {
				// Send code
				System.out.println(name + " sending code request...");
				put(new Tuple("run",prog,"for",name),server);

				// Get sandbox
				System.out.println(name + " getting sandbox address...");
				sandboxT = get(new Template(
						new ActualTemplateField("sandbox"),
						new FormalTemplateField(String.class)),
						Self.SELF);
				sandbox =  new PointToPoint(sandboxT.getElementAt(String.class,1), vp.getAddress());

				// Get result
				System.out.println(name + " getting result...");
				resultT = get(new Template(
						new ActualTemplateField("result"),
						new FormalTemplateField(Integer.class)),
						sandbox);
				System.out.println(name + " got " + resultT.getElementAt(String.class,1));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static class Server extends Agent {

		public Server(String who) {
			super(who);
		}

		@Override
		protected void doRun() {
			Agent prog ;
			String client_name ;
			Tuple t;
			PointToPoint client;
			Node sandboxNode;
			Template request = new Template(
					new ActualTemplateField("run"),
					new FormalTemplateField(Agent.class),
					new ActualTemplateField("for"),
					new FormalTemplateField(String.class)
					);

			try {
				String sandboxID; // id for sandboxes
				int id = 0; // counter for unique ids
				while(true){
					// Getting code execution request
					System.out.println(name + " getting request...");
					t = get(request,Self.SELF);
					client_name = t.getElementAt(String.class,3);

					// Create sandbox and launch program
					sandboxID = "sandbox-" + client_name + "-" + id;
					id++;
					System.out.println(name + " creating sandbox " + sandboxID + "...");
					// Note that nodes can be created at run-time by an agent
					sandboxNode = new Node(sandboxID, new TupleSpace());
					// And agents can be obtained as ordinary tuple fields
					prog = t.getElementAt(Agent.class,1); 
					sandboxNode.addAgent(prog);
					sandboxNode.addPort(vp);
					System.out.println(name + " starting sandbox" + sandboxID + "...");
					sandboxNode.start();

					// Send sandbox name to client
					System.out.println(name + " sending " + sandboxID + " to " + client_name + "...");
					client = new PointToPoint(client_name,vp.getAddress());
					put(new Tuple("sandbox",sandboxID),client);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}