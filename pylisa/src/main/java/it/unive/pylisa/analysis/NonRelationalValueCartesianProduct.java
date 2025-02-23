package it.unive.pylisa.analysis;

import it.unive.lisa.analysis.SemanticDomain.Satisfiability;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.NonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.ListRepresentation;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;
import java.util.Map.Entry;

@SuppressWarnings("unchecked")
public class NonRelationalValueCartesianProduct<T extends NonRelationalValueCartesianProduct<T, T1, T2>,
		T1 extends NonRelationalValueDomain<T1>,
		T2 extends NonRelationalValueDomain<T2>>
		extends BaseNonRelationalValueDomain<T> {

	public final T1 left;

	public final T2 right;

	public NonRelationalValueCartesianProduct(T1 left, T2 right) {
		this.left = left;
		this.right = right;
	}

	protected NonRelationalValueCartesianProduct<T, T1, T2> mk(T1 left, T2 right) {
		return new NonRelationalValueCartesianProduct<>(left, right);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NonRelationalValueCartesianProduct<?, ?, ?> other = (NonRelationalValueCartesianProduct<?, ?, ?>) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}

	@Override
	public DomainRepresentation representation() {
		return new ListRepresentation(left.representation(), right.representation());
	}

	@Override
	public T top() {
		return (T) mk(left.top(), right.top());
	}

	@Override
	public boolean isTop() {
		return left.isTop() && right.isTop();
	}

	@Override
	public T bottom() {
		return (T) mk(left.bottom(), right.bottom());
	}

	@Override
	public boolean isBottom() {
		return left.isBottom() && right.isBottom();
	}

	@Override
	protected T lubAux(T other)
			throws SemanticException {
		return (T) mk(left.lub(other.left), right.lub(other.right));
	}

	@Override
	protected T wideningAux(T other)
			throws SemanticException {
		return (T) mk(left.widening(other.left), right.widening(other.right));
	}

	@Override
	protected boolean lessOrEqualAux(T other) throws SemanticException {
		return left.lessOrEqual(other.left) && right.lessOrEqual(other.right);
	}

	@Override
	public T eval(ValueExpression expression,
			ValueEnvironment<T> environment, ProgramPoint pp)
			throws SemanticException {
		ValueEnvironment<T1> lenv = new ValueEnvironment<>(left);
		ValueEnvironment<T2> renv = new ValueEnvironment<>(right);
		for (Entry<Identifier, T> entry : environment) {
			lenv = lenv.putState(entry.getKey(), entry.getValue().left);
			renv = renv.putState(entry.getKey(), entry.getValue().right);
		}

		return (T) mk(left.eval(expression, lenv, pp), right.eval(expression, renv, pp));
	}

	@Override
	public Satisfiability satisfies(ValueExpression expression,
			ValueEnvironment<T> environment, ProgramPoint pp)
			throws SemanticException {
		ValueEnvironment<T1> lenv = new ValueEnvironment<>(left);
		ValueEnvironment<T2> renv = new ValueEnvironment<>(right);
		for (Entry<Identifier, T> entry : environment) {
			lenv = lenv.putState(entry.getKey(), entry.getValue().left);
			renv = renv.putState(entry.getKey(), entry.getValue().right);
		}

		return left.satisfies(expression, lenv, pp).glb(right.satisfies(expression, renv, pp));
	}

	@Override
	public ValueEnvironment<T> assume(
			ValueEnvironment<T> environment, ValueExpression expression,
			ProgramPoint pp) throws SemanticException {
		ValueEnvironment<T1> lenv = new ValueEnvironment<>(left);
		ValueEnvironment<T2> renv = new ValueEnvironment<>(right);
		for (Entry<Identifier, T> entry : environment) {
			lenv = lenv.putState(entry.getKey(), entry.getValue().left);
			renv = renv.putState(entry.getKey(), entry.getValue().right);
		}

		ValueEnvironment<T1> lassume = left.assume(lenv, expression, pp);
		ValueEnvironment<T2> rassume = right.assume(renv, expression, pp);

		ValueEnvironment<T> res = new ValueEnvironment<>((T) this);
		for (Entry<Identifier, T1> entry : lassume)
			res = res.putState(entry.getKey(), (T) mk(entry.getValue(), rassume.getState(entry.getKey())));
		for (Entry<Identifier, T2> entry : rassume)
			if (!res.getKeys().contains(entry.getKey()))
				res = res.putState(entry.getKey(), (T) mk(left.bottom(), entry.getValue()));

		return res;
	}

	@Override
	public T glb(T other)
			throws SemanticException {
		return (T) mk(left.glb(other.left), right.glb(other.right));
	}

	@Override
	public T variable(Identifier id, ProgramPoint pp)
			throws SemanticException {
		return (T) mk(left.variable(id, pp), right.variable(id, pp));
	}

	@Override
	public boolean canProcess(SymbolicExpression expression) {
		return left.canProcess(expression) || right.canProcess(expression);
	}

	@Override
	public boolean tracksIdentifiers(Identifier id) {
		return left.tracksIdentifiers(id) || right.tracksIdentifiers(id);
	}
}
