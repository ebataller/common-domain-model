package org.isda.cdm.functions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import org.isda.cdm.Contract;
import org.isda.cdm.TradableProduct;
import org.isda.cdm.TradeState;
import org.junit.jupiter.api.Test;

import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.regnosys.rosetta.common.serialisation.RosettaObjectMapper;

import cdm.observable.common.functions.CashPriceQuantityNoOfUnitsTriangulation;

public class CashPriceQuantityNoOfUnitsTriangulationTest extends AbstractFunctionTest {

	private static final String EQUITY_DIR = "result-json-files/products/equity/";
	
	@Inject
	private CashPriceQuantityNoOfUnitsTriangulation func;
	
	@Test
	void shouldTriangulateEquityPriceNotionalAndNoOfUnitsAndReturnSuccess() throws IOException {
		TradeState contract = getTradeState(EQUITY_DIR + "eqs-ex01-single-underlyer-execution-long-form.json");
		TradableProduct tradableProduct = contract.getTrade().getTradableProduct();
		
		boolean success = func.evaluate(tradableProduct.getPriceNotation(), tradableProduct.getQuantityNotation());
		
		assertTrue(success);
	}
	
	@Test
	void shouldReturnSuccessNotApplicableBecauseNoOfUnitsNotDefined() throws IOException {
		TradeState contract = getTradeState(EQUITY_DIR + "eqs-ex10-short-form-interestLeg-driving-schedule-dates.json");
		TradableProduct tradableProduct = contract.getTrade().getTradableProduct();
		
		boolean success = func.evaluate(tradableProduct.getPriceNotation(), tradableProduct.getQuantityNotation());
		
		assertTrue(success);
	}
	
	private TradeState getTradeState(String resourceName) throws IOException {
		URL url = Resources.getResource(resourceName);
		String json = Resources.toString(url, Charset.defaultCharset());
		return RosettaObjectMapper.getNewRosettaObjectMapper().readValue(json, TradeState.class);
	}
}
