package org.isda.cdm.processor;

import static org.isda.cdm.processor.CounterpartyMappingHelper.COUNTERPARTY_MAPPING_HELPER_KEY;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.regnosys.rosetta.common.translation.MappingContext;
import com.regnosys.rosetta.common.translation.MappingProcessor;
import com.regnosys.rosetta.common.translation.Path;
import com.rosetta.model.lib.RosettaModelObjectBuilder;
import com.rosetta.model.lib.path.RosettaPath;

import cdm.product.template.TradableProduct;

/**
 * FpML mapping processor.
 */
@SuppressWarnings("unused")
public class CounterpartyMappingProcessor extends MappingProcessor {

	public CounterpartyMappingProcessor(RosettaPath rosettaPath, List<Path> synonymPaths, MappingContext mappingContext) {
		super(rosettaPath, synonymPaths, mappingContext);
	}

	@Override
	public void map(Path synonymPath, RosettaModelObjectBuilder builder, RosettaModelObjectBuilder parent) {
		createHelper().addCounterparties((TradableProduct.TradableProductBuilder) builder);
	}

	@NotNull
	private CounterpartyMappingHelper createHelper() {
		return (CounterpartyMappingHelper) getContext().getMappingParams()
				.compute(COUNTERPARTY_MAPPING_HELPER_KEY, (key, value) -> new CounterpartyMappingHelper(getContext()));
	}
}
