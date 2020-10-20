package org.isda.cdm.functions;

import cdm.base.staticdata.party.PartyRole;
import cdm.base.staticdata.party.PartyRoleEnum;
import cdm.base.staticdata.party.metafields.ReferenceWithMetaParty;
import com.rosetta.model.lib.validation.ValidationResult;
import org.isda.cdm.TradeNew;
import org.isda.cdm.TradeState;
import org.isda.cdm.meta.TradeStateMeta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelClassValidationTest {

	@Test
	void checkErrorMessages() {
		TradeState tradeState =
            TradeState.builder()
                .setTradeBuilder(TradeNew.builder()
                    .addPartyRole(PartyRole.builder()
                            .setPartyReference(ReferenceWithMetaParty.builder().setExternalReference("party1").build())
                            .setRole(PartyRoleEnum.DETERMINING_PARTY)
                            .build())
                    .addPartyRole(PartyRole.builder()
                            .setPartyReference(ReferenceWithMetaParty.builder().setExternalReference("party1").build())
                            .setRole(PartyRoleEnum.DETERMINING_PARTY)
                            .build()))
                .build();

		ValidationResult<? super TradeState> result = new TradeStateMeta().validator().validate(null, tradeState);
		assertEquals(
				"contractIdentifier - Expected cardinality lower bound of [1] found [0]; tradeDate - Expected cardinality lower bound of [1] found [0]; tradableProduct - Expected cardinality lower bound of [1] found [0]",
				result.getFailureReason().orElse("No error message"));
	}

}
