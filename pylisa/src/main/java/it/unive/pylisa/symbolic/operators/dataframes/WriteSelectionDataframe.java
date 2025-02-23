package it.unive.pylisa.symbolic.operators.dataframes;

import it.unive.lisa.caches.Caches;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.type.Type;
import it.unive.lisa.util.collections.externalSet.ExternalSet;
import it.unive.pylisa.cfg.type.PyClassType;
import it.unive.pylisa.libraries.LibrarySpecificationProvider;

public class WriteSelectionDataframe implements BinaryOperator {

	public static final WriteSelectionDataframe INSTANCE = new WriteSelectionDataframe();

	private WriteSelectionDataframe() {
	}

	@Override
	public String toString() {
		return "write";
	}

	@Override
	public ExternalSet<Type> typeInference(ExternalSet<Type> left, ExternalSet<Type> right) {
		PyClassType series = PyClassType.lookup(LibrarySpecificationProvider.PANDAS_SERIES);
		if (left.noneMatch(t -> t.equals(series)))
			return Caches.types().mkEmptySet();
		if (left.noneMatch(t -> t.equals(series)))
			return Caches.types().mkEmptySet();
		return Caches.types().mkSingletonSet(series);
	}
}
