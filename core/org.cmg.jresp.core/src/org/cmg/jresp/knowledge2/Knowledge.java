package org.cmg.jresp.knowledge2;

import java.util.LinkedList;

import org.cmg.jresp.knowledge2.Template;
import org.cmg.jresp.knowledge2.Tuple;

public interface Knowledge {
public boolean put(Tuple t) throws InterruptedException;
public Tuple get(Template template) throws InterruptedException;
public Tuple getp(Template template) throws InterruptedException;
public LinkedList<Tuple> getAll(Template template) throws InterruptedException;
public LinkedList<Tuple> getAll() throws InterruptedException;
public Tuple query(Template template) throws InterruptedException;
public Tuple queryp(Template template) throws InterruptedException;
public LinkedList<Tuple> queryAll(Template template) throws InterruptedException;
public LinkedList<Tuple> queryAll() throws InterruptedException;
}
