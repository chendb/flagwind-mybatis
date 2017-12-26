package com.flagwind.persistent.model;

import java.util.ArrayList;
import java.util.Collection;

public class CombineClause extends ArrayList<Clause> implements Clause {

	private static final long serialVersionUID = 7081878195945581519L;
	private ClauseCombine combine;

	public CombineClause(ClauseCombine combin) {
		this.combine = combin;
	}

	public CombineClause(ClauseCombine combin, Collection<Clause> clauses) {
		super(clauses);
		this.combine = combin;
	}

	public ClauseCombine getCombine() {
		return combine;
	}

	public void setCombine(ClauseCombine clauseCombine) {
		this.combine = clauseCombine;
	}

	public CombineClause join(Clause clause) {
		this.add(clause);
		return this;
	}

	public CombineClause join(boolean flag, Clause clause) {
		if (flag) {
			this.add(clause);
		}
		return this;
	}

}
