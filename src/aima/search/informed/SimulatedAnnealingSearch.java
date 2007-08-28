/*
 * Created on Sep 14, 2004
 *
 */
package aima.search.informed;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import aima.search.framework.Node;
import aima.search.framework.NodeExpander;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchUtils;
import aima.util.Util;

/**
 * @author Ravi Mohan
 * 
 */

public class SimulatedAnnealingSearch extends NodeExpander implements Search {

	private final int steps;

	private final Scheduler scheduler;

	public SimulatedAnnealingSearch() {
		this.steps = 10000;
		this.scheduler = new Scheduler();
	}

	public List search(Problem p) throws Exception {
		clearInstrumentation();
		Node current = new Node(p.getInitialState());
		Node next = null;
		List ret = new ArrayList();
		for (int step = 0; step < 1000; step = step + 1) {
			double temp = scheduler.getTemp(step);
			if (temp == 0.0) {
				String status = "not completed";
				if (p.isGoalState(current.getState())) {
					status = "success";
				}
				// System.out.println(current.getState());
				ret = SearchUtils.actionsFromNodes(current.getPathFromRoot());
				ret.add(status + "\nFinal state = \n" + current.getState());
				break;
			}
			List children = expandNode(current, p);
			// expansions++;
			// System.out.println("step = "+step+" expansions = "+expansions);
			if (children.size() > 0) {
				// TODO take care of no possible expansion situation?
				next = (Node) Util.selectRandomlyFromList(children);
				int deltaE = getValue(p, next) - getValue(p, current);
				// System.out.print("deltaE = "+deltaE+"\n");
				if ((deltaE > 0.0)
						|| (new Random().nextDouble() > Math.exp(deltaE / temp))) {
					current = next;
				}
			}

		}
		// System.out.println("Number of expansions = "+expansions);
		return ret;// Total Failure
	}

	private int getValue(Problem p, Node n) {
		return -1 * getHeuristic(p, n); // assumption greater heuristic value =>
		// HIGHER on hill; 0 == goal state;
		// SA deals with gardient DESCENT
	}

	private int getHeuristic(Problem p, Node aNode) {
		return p.getHeuristicFunction().getHeuristicValue(aNode.getState());
	}
}