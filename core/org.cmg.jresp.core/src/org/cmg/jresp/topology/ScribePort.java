/**
 * Copyright (c) 2013 Concurrency and Mobility Group.
 * Universita' di Firenze
 *	
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Michele Loreti
 */
package org.cmg.jresp.topology;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.cmg.jresp.comp.Node;
import org.cmg.jresp.protocol.UnicastMessage;
import org.cmg.jresp.protocol.jRESPMessage;

import com.google.gson.JsonSyntaxException;

import rice.environment.Environment;
import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;
import rice.p2p.scribe.ScribeContent;
import rice.p2p.scribe.ScribeImpl;
import rice.p2p.scribe.ScribeMultiClient;
import rice.p2p.scribe.Topic;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.commonapi.PastryIdFactory;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;

/**
 * @author Michele Loreti
 *
 */
public class ScribePort extends AbstractPort {

	private PastryNode pastryNode;
	private ScribePortApplication application;

	public ScribePort(PastryNode pastryNode, GroupPredicate[] predicates) {
		this.pastryNode = pastryNode;
		this.application = new ScribePortApplication(predicates);
	}

	public class ScribePortApplication implements Application, ScribeMultiClient {

		private Endpoint endpoint;
		private Topic generalTopic;
		private ScribeImpl scribeInstance;
		private HashMap<GroupPredicate, Topic> topics;
		private HashMap<GroupPredicate, Boolean> subscribed;
		private boolean allEnabled = false;

		public ScribePortApplication(GroupPredicate[] predicates) {
			this.endpoint = ScribePort.this.pastryNode.buildEndpoint(this, "jRESPPastry");
			this.scribeInstance = new ScribeImpl(pastryNode, "jRESPScribe");
			this.generalTopic = new Topic(new PastryIdFactory(pastryNode.getEnvironment()), "jRESPMainTopic");
			this.topics = new HashMap<GroupPredicate, Topic>();
			this.subscribed = new HashMap<GroupPredicate, Boolean>();
			for (GroupPredicate p : predicates) {
				topics.put(p, new Topic(new PastryIdFactory(pastryNode.getEnvironment()), p.toString()));
				subscribed.put(p, false);
			}
			this.endpoint.register();
		}

		@Override
		public boolean forward(RouteMessage message) {
			return true;
		}

		@Override
		public void deliver(Id id, rice.p2p.commonapi.Message message) {
			if (message instanceof jRESPScribeMessage) {
				jRESPScribeMessage m = (jRESPScribeMessage) message;
				try {
					receiveMessage(m.getMessage());
				} catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void update(rice.p2p.commonapi.NodeHandle handle, boolean joined) {
		}

		public void sendMessage(Id target, jRESPMessage message) {
			this.endpoint.route(target, new jRESPScribeMessage(ScribePort.this.pastryNode.getId(), target, message),
					null);
		}

		@Override
		public boolean anycast(Topic topic, ScribeContent content) {
			return false;
		}

		@Override
		public void deliver(Topic topic, ScribeContent content) {
			if (content instanceof ScribeGroupMessage) {
				try {
					ScribePort.this.receiveMessage(((ScribeGroupMessage) content).getMessage());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void childAdded(Topic topic, NodeHandle child) {
		}

		@Override
		public void childRemoved(Topic topic, NodeHandle child) {
		}

		@Override
		public void subscribeFailed(Topic topic) {
		}

		@Override
		public void subscribeFailed(Collection<Topic> topics) {
		}

		@Override
		public void subscribeSuccess(Collection<Topic> topics) {
			for (Topic t : topics) {
				System.out.println("SUCCESS TOPIC: " + t);
			}
		}

		public void sendMessage(jRESPMessage message, GroupPredicate g) {
			Topic t = null;
			if (g != null) {
				t = topics.get(g);
			}
			if (t != null) {
				scribeInstance.publish(t, new ScribeGroupMessage(pastryNode.getId(), message));
			} else {
				scribeInstance.publish(generalTopic, new ScribeGroupMessage(pastryNode.getId(), message));
			}
		}

		public void subscribe() {
			this.scribeInstance.subscribe(generalTopic, this);
		}

		public synchronized void revalidateAttributes() {
			if (allEnabled) {
				return;
			}
			for (GroupPredicate p : topics.keySet()) {
				int satCounter = 0;
				for (MessageDispatcher n : ScribePort.this.nodes.values()) {
					if (n instanceof Node) {
						if (p.evaluate(((Node) n).getInterface())) {
							satCounter++;
						}
					} else {
						satCounter++;
						break;
					}
				}
				if ((satCounter > 0) && (!subscribed.get(p))) {
					this.scribeInstance.subscribe(topics.get(p), this);
					subscribed.put(p, true);
				} else {
					if (subscribed.get(p)) {
						this.scribeInstance.unsubscribe(topics.get(p), this);
						subscribed.put(p, false);
					}
				}
			}
		}

		public synchronized void enableAllPredicate() {
			if (!allEnabled) {
				allEnabled = true;
				for (GroupPredicate p : subscribed.keySet()) {
					if (!subscribed.get(p)) {
						this.scribeInstance.subscribe(topics.get(p), this);
						subscribed.put(p, true);
					}
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.jresp.topology.AbstractPort#canSendTo(org.cmg.jresp.topology.
	 * Target)
	 */
	@Override
	public boolean canSendTo(Target l) {
		return (l instanceof Group)
				|| ((l instanceof PointToPoint) && (((PointToPoint) l).address instanceof ScribePortAddress));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cmg.jresp.topology.AbstractPort#send(org.cmg.jresp.topology.Address,
	 * org.cmg.jresp.protocol.UnicastMessage)
	 */
	@Override
	protected void send(Address address, UnicastMessage message) throws IOException, InterruptedException {
		Id target = ((ScribePortAddress) address).getId();
		application.sendMessage(target, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cmg.jresp.topology.AbstractPort#send(org.cmg.jresp.protocol.Message)
	 */
	@Override
	protected void send(jRESPMessage m) throws IOException, InterruptedException {
		application.sendMessage(m, m.getGroupPredicate());
	}

	@Override
	public Address getAddress() {
		return new ScribePortAddress(this.pastryNode.getId());
	}

	public static ScribePort createScribePort(InetAddress bindAddress, int bindport, InetSocketAddress bootstrapNode,
			Environment environment) throws IOException, InterruptedException {
		return createScribePort(bindAddress, bindport, bootstrapNode, environment, new GroupPredicate[0]);
	}

	public static ScribePort createScribePort(InetAddress bindAddress, int bindport, InetSocketAddress bootstrapNode,
			Environment environment, GroupPredicate[] predicates) throws IOException, InterruptedException {
		NodeIdFactory nidFactory = new RandomNodeIdFactory(environment);
		PastryNodeFactory factory = new SocketPastryNodeFactory(nidFactory, bindAddress, bindport, environment);
		// NodeHandle bootHandle =
		// ((SocketPastryNodeFactory)factory).getNodeHandle(bootstrapNode);

		// construct a node, passing the null boothandle on the first loop will
		// cause the node to start its own ring
		PastryNode node = factory.newNode();
		if (bootstrapNode == null) {
			node.boot(Collections.EMPTY_LIST);
		} else {
			node.boot(bootstrapNode);
		}
		synchronized (node) {
			while (!node.isReady() && !node.joinFailed()) {
				System.out.println("Trying to join the FreePastry ring. Retry in 500ms.");
				// delay so we don't busy-wait
				node.wait(500);

				// abort if can't join
				if (node.joinFailed()) {
					throw new IOException("Could not join the FreePastry ring.  Reason:" + node.joinFailedReason());
				}
			}
		}
		System.out.println("NODE STARTED: " + node.isReady());
		return new ScribePort(node, predicates);
	}

	public PastryNode getNode() {
		return pastryNode;
	}

	public void subscribe() {
		application.subscribe();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmg.jresp.topology.AbstractPort#register(org.cmg.jresp.topology.
	 * MessageDispatcher)
	 */
	@Override
	public synchronized void register(final MessageDispatcher n) {
		super.register(n);
		if (n instanceof Node) {
			((Node) n).addObserver(new Observer() {

				@Override
				public void update(Observable o, Object arg) {
					application.revalidateAttributes();
				}

			});
		} else {
			application.enableAllPredicate();
		}
	}

}
