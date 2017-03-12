package org.cmg.jresp.knowledge2;

public class IterativeTupleNode extends TupleNode{
	public IterativeTupleNode(){
		super();
		this.next=new IterativeTupleSpace();
	}
	public IterativeTupleNode(Object field){
		super(field);
		this.next=new IterativeTupleSpace();
	}
}
