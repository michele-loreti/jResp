package org.cmg.jresp.knowledge2;

public class TupleContainer {
	Tuple t;
	int count;

	public TupleContainer(Tuple t) {
		this.t = t;
		count = 1;
	}

	public int getCounter() {
		return count;
	}

	public Tuple getTuple() {
		return t;
	}
	protected boolean doIncrementContainerCounter() {
		this.count++;
		return true;
	}
	protected boolean doDecrementContainerCounter() {
		if(count>0){
			this.count--;
			return true;
		}
		return false;
	}
	protected boolean isPossibleToDecrement(){
		return count>0;
	}
	public boolean matchWithTemplate(Template template){
		for (int i=0;i<template.lenght();i++){
			TemplateField field =template.get(i);
			if(!field.match(t.get(i))){
				return false;
			}
		}
		return true;
	}
}
