package it.unive.pylisa.libraries.pandas;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.type.Type;
import it.unive.pylisa.cfg.type.PyClassType;
import it.unive.pylisa.libraries.LibrarySpecificationProvider;
import it.unive.pylisa.symbolic.operators.dataframes.AccessKeys;

public class Keys extends it.unive.lisa.program.cfg.statement.UnaryExpression implements PluggableStatement {

	private Statement st;

	public Keys(CFG cfg, CodeLocation location, Expression dataframe) {
		super(cfg, location, "keys", PyClassType.lookup(LibrarySpecificationProvider.PANDAS_DF).getReference(),
				dataframe);
	}

	public static Keys build(CFG cfg, CodeLocation location, Expression[] exprs) {
		return new Keys(cfg, location, exprs[0]);
	}

	@Override
	final public void setOriginatingStatement(Statement st) {
		this.st = st;
	}

	@Override
	public <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
					InterproceduralAnalysis<A, H, V, T> interprocedural,
					AnalysisState<A, H, V, T> state,
					SymbolicExpression arg,
					StatementStore<A, H, V, T> expressions)
					throws SemanticException {
		CodeLocation loc = getLocation();
		PyClassType dftype = PyClassType.lookup(LibrarySpecificationProvider.PANDAS_DF);
		PyClassType seriestype = PyClassType.lookup(LibrarySpecificationProvider.PANDAS_SERIES);
		Type seriesref = ((PyClassType) seriestype).getReference();

		HeapDereference deref = new HeapDereference(dftype, arg, loc);
		AnalysisState<A, H, V, T> copied = PandasSemantics.copyDataframe(state, deref, st);

		AnalysisState<A, H, V, T> result = state.bottom();
		for (SymbolicExpression id : copied.getComputedExpressions()) {
			UnaryExpression transform = new UnaryExpression(seriestype, id, AccessKeys.INSTANCE, loc);
			AnalysisState<A, H, V, T> tmp = copied.smallStepSemantics(transform, st);

			HeapReference ref = new HeapReference(seriesref, id, loc);
			result = result.lub(tmp.smallStepSemantics(ref, st));
		}
		return result;
	}
}
