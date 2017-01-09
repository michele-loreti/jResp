package org.cmg.jresp.policy.facpl.elements.util;

import java.util.ArrayList;
import java.util.LinkedList;

import org.cmg.jresp.exceptions.MissingAttributeException;
import org.cmg.jresp.policy.AuthorizationRequest;
import org.cmg.jresp.policy.facpl.MatchDecision;
import org.cmg.jresp.policy.facpl.elements.TargetConnector;
import org.cmg.jresp.policy.facpl.elements.TargetExpression;

/**
 *
 * @author Andrea Margheri
 *
 */

public class TargetTreeRepresentation {

	private LinkedList<TargetTreeRepresentation> child;
	private Object root;

	public TargetTreeRepresentation(TargetConnector conn, TargetTreeRepresentation... nodes) {
		child = new LinkedList<TargetTreeRepresentation>();
		root = conn;
		for (TargetTreeRepresentation nodeTargetTree : nodes) {
			child.add(nodeTargetTree);
		}
	}

	public TargetTreeRepresentation(TargetExpression node) {
		root = node;
	}

	public TargetTreeRepresentation(Boolean b) {
		root = true;
	}

	public MatchDecision getDecisionValue(AuthorizationRequest request, String thisValue) {
		MatchDecision decision;
		if (root instanceof Boolean) {
			if (root.equals(true)) {
				return MatchDecision.MATCH;
			} else {
				return MatchDecision.NO_MATCH;
			}
		} else if (root instanceof TargetExpression) {
			// only one expression
			try {
				decision = ((TargetExpression) root).evaluateTarget(
						request.getAttributeValue(((TargetExpression) root).getStruct_name()), thisValue);
			} catch (Throwable e) {
				if (e instanceof MissingAttributeException) {
					// we assume that in case of absence of the attribute the
					// policy is no-match
					decision = MatchDecision.NO_MATCH;
				} else {
					decision = MatchDecision.INDETERMINATE;
				}
				// e.printStackTrace();
			}
		} else {
			ArrayList<MatchDecision> decisions = new ArrayList<MatchDecision>();
			for (TargetTreeRepresentation tr : this.child) {
				decisions.add(tr.getDecisionValue(request, thisValue));
			}
			decision = combineDecision((TargetConnector) root, decisions);
		}
		return decision;
	}

	protected MatchDecision combineDecision(TargetConnector conn, ArrayList<MatchDecision> args) {
		MatchDecision dc = args.get(0);
		if (conn.equals(TargetConnector.NOT)) {
			if (dc.equals(MatchDecision.NO_MATCH)) {
				dc = MatchDecision.MATCH;
			} else if (dc.equals(MatchDecision.MATCH)) {
				dc = MatchDecision.NO_MATCH;
			} else {
				dc = MatchDecision.INDETERMINATE;
			}
		} else {
			for (int i = 1; i < args.size(); i++) {
				MatchDecision arg1 = dc;
				MatchDecision arg2 = args.get(i);
				switch (conn) {
				case AND:
					if (arg1.equals(MatchDecision.NO_MATCH) || arg2.equals(MatchDecision.NO_MATCH)) {
						dc = MatchDecision.NO_MATCH;
					} else {
						if (arg1.equals(arg2)) {
							dc = arg1;
						} else {
							if ((arg1.equals(MatchDecision.MATCH) && arg2.equals(MatchDecision.INDETERMINATE))
									|| (arg1.equals(MatchDecision.INDETERMINATE) && arg2.equals(MatchDecision.MATCH))) {
								dc = MatchDecision.INDETERMINATE;
							}
						}
					}
					break;
				case OR:
					if (arg1.equals(MatchDecision.MATCH) || arg2.equals(MatchDecision.MATCH)) {
						dc = MatchDecision.MATCH;
					} else {
						if (arg1.equals(MatchDecision.INDETERMINATE) || arg2.equals(MatchDecision.INDETERMINATE)) {
							dc = MatchDecision.INDETERMINATE;
						} else {
							// No_Match vs No_Match
							dc = MatchDecision.NO_MATCH;
						}
					}
					break;
				default:
					dc = MatchDecision.INDETERMINATE;
					break;
				}
			}
		}
		return dc;
	}

}
