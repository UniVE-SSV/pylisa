package it.unive.pylisa.analysis.dataframes.operations;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.program.SyntheticLocation;
import it.unive.lisa.program.cfg.CodeLocation;

public class BottomOperation extends DataframeOperation {

	public BottomOperation() {
		super(SyntheticLocation.INSTANCE);
	}

	public BottomOperation(CodeLocation where) {
		super(where);
	}

	@Override
	public String toString() {
		return Lattice.BOTTOM_STRING;
	}

	@Override
	protected DataframeOperation lubAux(DataframeOperation other) throws SemanticException {
		return other;
	}

	@Override
	protected boolean lessOrEqualAux(DataframeOperation other) throws SemanticException {
		return true;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	@Override
	protected boolean lessOrEqualSameOperation(DataframeOperation other) {
		return true;
	}

	@Override
	protected DataframeOperation lubSameOperation(DataframeOperation other) {
		return this;
	}

	@Override
	protected int compareToSameClassAndLocation(DataframeOperation o) {
		return 0;
	}
}