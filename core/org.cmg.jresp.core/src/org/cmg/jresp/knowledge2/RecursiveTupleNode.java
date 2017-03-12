package org.cmg.jresp.knowledge2;


public class RecursiveTupleNode extends TupleNode{
public RecursiveTupleNode(){
	super();
	this.next=new RecursiveTupleSpace();
}
public RecursiveTupleNode(Object field){
	super(field);
	this.next=new RecursiveTupleSpace();
}
}
