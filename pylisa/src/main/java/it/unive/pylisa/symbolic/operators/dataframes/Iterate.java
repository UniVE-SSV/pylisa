package it.unive.pylisa.symbolic.operators.dataframes;

import it.unive.lisa.caches.Caches;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.type.Type;
import it.unive.lisa.util.collections.externalSet.ExternalSet;
import it.unive.pylisa.cfg.type.PyClassType;
import it.unive.pylisa.libraries.LibrarySpecificationProvider;

public class Iterate implements UnaryOperator {

	public static final Iterate INSTANCE = new Iterate();

	private Iterate() {
	}

	@Override
	public String toString() {
		return "iterate";
	}

	@Override
	public ExternalSet<Type> typeInference(ExternalSet<Type> arg) {
		if (arg.noneMatch(t -> t.equals(PyClassType.lookup(LibrarySpecificationProvider.PANDAS_DF))))
			return Caches.types().mkEmptySet();
		return Caches.types().mkSingletonSet(PyClassType.lookup(LibrarySpecificationProvider.PANDAS_SERIES));
	}
}
