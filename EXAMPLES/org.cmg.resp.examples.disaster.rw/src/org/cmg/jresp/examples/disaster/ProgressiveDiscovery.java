package org.cmg.jresp.examples.disaster;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.topology.Group;
import org.cmg.jresp.topology.HasValue;
import org.cmg.jresp.topology.Self;
// Agente che regolamenta il Progressive Discovery, algoritmo di esplorazione.
public class ProgressiveDiscovery extends Agent {

	// robot identifier
	private int robotId;
	private Scenario scenario;

	public ProgressiveDiscovery(int robotId, Scenario s) {
		super("ProgressiveDiscovery");
		this.robotId = robotId;
		this.scenario = s;
	}

	@Override
	protected void doRun() throws IOException, InterruptedException {
		boolean stopper = false;
		while (!stopper) {
			Tuple t = query(new Template(new ActualTemplateField(
					"OUTOFNEST"), new FormalTemplateField(Boolean.class)), Self.SELF);
			stopper = t.getElementAt(Boolean.class, 1); // vero se fuori dal nest
			if (stopper){
			Tuple t2 = query(new Template(new ActualTemplateField(
					"NOLANDMARSKNEAR"), new FormalTemplateField(Boolean.class)), Self.SELF);
			stopper = t2.getElementAt(Boolean.class, 1); // vero se non ha un landmark vicino
			}

			if (stopper) { // mi fermo se entrambe le condizioni sono vere
				put(new Tuple("stop"), Self.SELF);
				//System.out.print("Robot " + robotId+ " become a Landmark (out of nest and no landmark near)\n");
				updateList();
			}
		}
	}

	private void updateList() throws InterruptedException, IOException {
		scenario.addLandmarkList(robotId); // Aggiunge l'id alla lista dello scenario.
		if (scenario.LandmarkListSize()==1){ // Se � il primo della lista, da l'ordine agli altri di seguirlo.
			put(new Tuple("list", robotId), new Group(new HasValue( "role" , Scenario.LANDMARK )));
		}
	}
}
