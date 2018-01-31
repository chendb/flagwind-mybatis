package com.flagwind.persistent.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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


	// region 链式方法

	public CombineClause join(Clause... clauses) {
		if (clauses == null) {
			return this;
		}
		for (Clause clause : clauses) {
			this.add(clause);
		}
		return this;
	}

	// endregion

	// region 静态构造方法

	/**
	 * 构建and组合条件
	 */
	public static CombineClause and(Clause... clauses) {
		List<Clause> list = Arrays.asList(clauses);
		return new CombineClause(ClauseCombine.And, list);
	}

	/**
	 * 构建or组合条件
	 */
	public static CombineClause or(Clause... clauses) {
		List<Clause> list = Arrays.asList(clauses);
		return new CombineClause(ClauseCombine.Or, list);
	}

	// endregion

}

