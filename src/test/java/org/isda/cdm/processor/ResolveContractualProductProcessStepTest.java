package org.isda.cdm.processor;

import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.regnosys.rosetta.common.serialisation.RosettaObjectMapper;
import com.rosetta.model.lib.GlobalKey;
import com.rosetta.model.lib.meta.MetaFieldsI;
import org.isda.cdm.*;
import org.isda.cdm.functions.AbstractFunctionTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResolveContractualProductProcessStepTest extends AbstractFunctionTest {

    private static final String PRODUCTS_DIR = "result-json-files/products/";
    private static final String RATES_DIR = PRODUCTS_DIR + "rates/";

    @Inject
    ResolveContractualProductProcessStep resolveContractualProductProcessStep;


    @Test
    void postProcessStepResolvedQuantity() throws IOException {
        TradeState tradeState = getTradeState(RATES_DIR + "GBP-Vanilla-uti.json");
        TradeState.TradeStateBuilder builder = tradeState.toBuilder();
        resolveContractualProductProcessStep.runProcessStep(Contract.class, builder);
        TradeState resolvedTradeState = builder.build();

        List<InterestRatePayout> interestRatePayouts = Optional.ofNullable(resolvedTradeState)
                .map(TradeState::getTrade)
                .map(TradeNew::getTradableProduct)
                .map(TradableProduct::getProduct)
                .map(Product::getContractualProduct)
                .map(ContractualProduct::getEconomicTerms)
                .map(EconomicTerms::getPayout)
                .map(Payout::getInterestRatePayout)
                .orElseThrow(() -> new IllegalArgumentException("No InterestRatePayout found"));

        PayoutBase fixedLeg1 = getPayout(interestRatePayouts, "fixedLeg1");
        PayoutBase floatingLeg2 = getPayout(interestRatePayouts, "floatingLeg2");

        assertEquals(BigDecimal.valueOf(4352000), scaled(fixedLeg1.getPayoutQuantity().getResolvedQuantity().getAmount()));
        assertEquals(BigDecimal.valueOf(4352000), scaled(floatingLeg2.getPayoutQuantity().getResolvedQuantity().getAmount()));
    }

    private BigDecimal scaled(BigDecimal amount) {
        return amount.setScale(0, RoundingMode.HALF_UP);
    }

    private boolean externalKeyMatches(GlobalKey o, String key) {
        return Optional.ofNullable(o)
                .map(GlobalKey::getMeta)
                .map(MetaFieldsI::getExternalKey)
                .map(key::equals)
                .orElse(false);
    }

    private PayoutBase getPayout(List<? extends PayoutBase> payouts, String payoutExternalKey) {
        return payouts.stream()
                .filter(GlobalKey.class::isInstance)
                .filter(p -> externalKeyMatches((GlobalKey) p, payoutExternalKey))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No Payout found with external key " + payoutExternalKey));
    }

    private TradeState getTradeState(String resourceName) throws IOException {
        URL url = Resources.getResource(resourceName);
        String json = Resources.toString(url, Charset.defaultCharset());
        return RosettaObjectMapper.getNewRosettaObjectMapper().readValue(json, TradeState.class);
    }

}