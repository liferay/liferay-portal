/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.remote.internal;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.tax.CommerceTaxCalculateRequest;
import com.liferay.commerce.tax.CommerceTaxValue;
import com.liferay.commerce.tax.engine.remote.internal.configuration.RemoteCommerceTaxConfiguration;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;

import java.math.BigDecimal;

import java.net.InetSocketAddress;
import java.net.URI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Ivica Cardic
 */
public class RemoteCommerceTaxEngineTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_remoteCommerceTaxEngine = Mockito.spy(new RemoteCommerceTaxEngine());

		_remoteCommerceTaxEngine.activate();

		Mockito.doReturn(
			_getRemoteCommerceTaxConfiguration()
		).when(
			_remoteCommerceTaxEngine
		).getRemoteCommerceTaxConfiguration(
			Mockito.anyLong()
		);

		_commerceBillingAddress = _getCommerceAddress(
			"billingAddressCity", 1, "USD", "CA",
			"billingAddressExternalReferenceCode", 12.3, 45.7,
			"billingAddressPhoneNumber", "billingAddressStreet1",
			"billingAddressStreet2", "billingAddressStreet3", 1,
			"billingAddressZip");

		Mockito.doReturn(
			_commerceBillingAddress
		).when(
			_remoteCommerceTaxEngine
		).getCommerceAddress(
			1
		);

		_commerceShippingAddress = _getCommerceAddress(
			"shippingAddressCity", 2, "BOL", "BO",
			"shippingAddressExternalReferenceCode", 22.72, 11.87,
			"shippingAddressPhoneNumber", "shippingAddressStreet1",
			"shippingAddressStreet2", "shippingAddressStreet3", 2,
			"shippingAddressZip");

		Mockito.doReturn(
			_commerceShippingAddress
		).when(
			_remoteCommerceTaxEngine
		).getCommerceAddress(
			2
		);

		_commerceTaxMethod = _getCommerceTaxMethod();

		Mockito.doReturn(
			_commerceTaxMethod
		).when(
			_remoteCommerceTaxEngine
		).getCommerceTaxMethod(
			Mockito.anyLong()
		);

		_cpTaxCategory = _getCPTaxCategory();

		_startHttpServer();
	}

	@After
	public void tearDown() {
		_httpServer.stop(0);

		_remoteCommerceTaxEngine.deactivate();
	}

	@Test
	public void testGetCommerceTaxValue() throws Exception {
		CommerceTaxCalculateRequest commerceTaxCalculateRequest =
			_getCommerceTaxCalculateRequest();

		CommerceTaxValue commerceTaxValue =
			_remoteCommerceTaxEngine.getCommerceTaxValue(
				commerceTaxCalculateRequest);

		Assert.assertEquals("label", commerceTaxValue.getLabel());
		Assert.assertEquals("name", commerceTaxValue.getName());
		Assert.assertEquals(
			commerceTaxValue.getAmount(), BigDecimal.valueOf(118.8));

		_assertCommerceAddress(_commerceBillingAddress, "billing");

		Assert.assertEquals(
			commerceTaxCalculateRequest.isPercentage(),
			GetterUtil.getBoolean(_recordedParameterMap.get("percentage")));
		Assert.assertEquals(
			commerceTaxCalculateRequest.getPrice(),
			new BigDecimal(_recordedParameterMap.get("price")));

		_assertCommerceAddress(_commerceShippingAddress, "shipping");

		Assert.assertEquals(
			commerceTaxCalculateRequest.getTaxCategoryId(),
			GetterUtil.getLong(_recordedParameterMap.get("taxCategoryId")));

		CommerceTaxMethod commerceTaxMethod = _getCommerceTaxMethod();

		Assert.assertEquals(
			commerceTaxMethod.getEngineKey(),
			_recordedParameterMap.get("taxMethod"));

		Assert.assertEquals(
			String.valueOf(commerceTaxMethod.isPercentage()),
			_recordedParameterMap.get("taxMethodPercentage"));
	}

	private void _assertCommerceAddress(
			CommerceAddress commerceAddress, String prefix)
		throws Exception {

		Assert.assertEquals(
			commerceAddress.getCity(),
			_recordedParameterMap.get(prefix + "AddressCity"));

		Country country = commerceAddress.getCountry();

		Assert.assertEquals(
			country.getA3(),
			_recordedParameterMap.get(prefix + "AddressCountryISOCode"));

		Assert.assertEquals(
			commerceAddress.getExternalReferenceCode(),
			_recordedParameterMap.get(prefix + "AddressExternalReferenceCode"));
		Assert.assertEquals(
			commerceAddress.getCommerceAddressId(),
			GetterUtil.getLong(
				_recordedParameterMap.get(prefix + "AddressId")));
		Assert.assertEquals(
			String.valueOf(commerceAddress.getLatitude()),
			_recordedParameterMap.get(prefix + "AddressLatitude"));
		Assert.assertEquals(
			String.valueOf(commerceAddress.getLongitude()),
			_recordedParameterMap.get(prefix + "AddressLongitude"));
		Assert.assertEquals(
			commerceAddress.getPhoneNumber(),
			_recordedParameterMap.get(prefix + "AddressPhoneNumber"));

		Region region = commerceAddress.getRegion();

		Assert.assertEquals(
			_recordedParameterMap.get(prefix + "AddressRegionISOCode"),
			region.getRegionCode());

		Assert.assertEquals(
			commerceAddress.getStreet1(),
			_recordedParameterMap.get(prefix + "AddressStreet1"));
		Assert.assertEquals(
			commerceAddress.getStreet2(),
			_recordedParameterMap.get(prefix + "AddressStreet2"));
		Assert.assertEquals(
			commerceAddress.getStreet3(),
			_recordedParameterMap.get(prefix + "AddressStreet3"));
		Assert.assertEquals(
			commerceAddress.getZip(),
			_recordedParameterMap.get(prefix + "AddressZip"));
	}

	private CPTaxCategory _getCPTaxCategory() {
		CPTaxCategory cpTaxCategory = Mockito.mock(CPTaxCategory.class);

		Mockito.when(
			cpTaxCategory.getCPTaxCategoryId()
		).thenReturn(
			5L
		);

		return cpTaxCategory;
	}

	private CommerceAddress _getCommerceAddress(
			String city, long commerceAddressId,
			String commerceCountryThreeLettersISOCode,
			String commerceRegionCode, String externalReferenceCode,
			double latitude, double longitude, String phoneNumber,
			String street1, String street2, String street3, int type,
			String zip)
		throws Exception {

		CommerceAddress commerceAddress = Mockito.mock(CommerceAddress.class);

		Mockito.when(
			commerceAddress.getCity()
		).thenReturn(
			city
		);

		Mockito.when(
			commerceAddress.getCommerceAddressId()
		).thenReturn(
			commerceAddressId
		);

		Country country = _getCountry(commerceCountryThreeLettersISOCode);

		Mockito.when(
			commerceAddress.getCountry()
		).thenReturn(
			country
		);

		Mockito.when(
			commerceAddress.getExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		Mockito.when(
			commerceAddress.getLatitude()
		).thenReturn(
			latitude
		);

		Mockito.when(
			commerceAddress.getLongitude()
		).thenReturn(
			longitude
		);

		Mockito.when(
			commerceAddress.getPhoneNumber()
		).thenReturn(
			phoneNumber
		);

		Region region = _getRegion(commerceRegionCode);

		Mockito.when(
			commerceAddress.getRegion()
		).thenReturn(
			region
		);

		Mockito.when(
			commerceAddress.getStreet1()
		).thenReturn(
			street1
		);

		Mockito.when(
			commerceAddress.getStreet2()
		).thenReturn(
			street2
		);

		Mockito.when(
			commerceAddress.getStreet3()
		).thenReturn(
			street3
		);

		Mockito.when(
			commerceAddress.getType()
		).thenReturn(
			type
		);

		Mockito.when(
			commerceAddress.getZip()
		).thenReturn(
			zip
		);

		return commerceAddress;
	}

	private CommerceTaxCalculateRequest _getCommerceTaxCalculateRequest() {
		return new CommerceTaxCalculateRequest() {
			{
				setCommerceBillingAddressId(1);
				setCommerceChannelGroupId(4);
				setCommerceShippingAddressId(2);
				setCommerceTaxMethodId(3);
				setPercentage(true);
				setPrice(BigDecimal.valueOf(99));
				setTaxCategoryId(5);
			}
		};
	}

	private CommerceTaxMethod _getCommerceTaxMethod() {
		CommerceTaxMethod commerceTaxMethod = Mockito.mock(
			CommerceTaxMethod.class);

		Mockito.when(
			commerceTaxMethod.getCommerceTaxMethodId()
		).thenReturn(
			3L
		);

		Mockito.when(
			commerceTaxMethod.getEngineKey()
		).thenReturn(
			"commerceTaxMethodEngineKey"
		);

		Mockito.when(
			commerceTaxMethod.isPercentage()
		).thenReturn(
			true
		);

		return commerceTaxMethod;
	}

	private Country _getCountry(String threeLettersISOCode) {
		Country country = Mockito.mock(Country.class);

		Mockito.when(
			country.getA3()
		).thenReturn(
			threeLettersISOCode
		);

		return country;
	}

	private Region _getRegion(String code) {
		Region region = Mockito.mock(Region.class);

		Mockito.when(
			region.getRegionCode()
		).thenReturn(
			code
		);

		return region;
	}

	private RemoteCommerceTaxConfiguration
		_getRemoteCommerceTaxConfiguration() {

		RemoteCommerceTaxConfiguration remoteCommerceTaxConfiguration =
			Mockito.mock(RemoteCommerceTaxConfiguration.class);

		Mockito.when(
			remoteCommerceTaxConfiguration.taxValueEndpointURL()
		).thenReturn(
			"http://localhost:" + _PORT + "/commerce/tax-value"
		);

		return remoteCommerceTaxConfiguration;
	}

	private void _recordQueryParameters(URI uri) {
		List<String> parametersValues = StringUtil.split(
			uri.getQuery(), CharPool.AMPERSAND);

		for (String parameterValueString : parametersValues) {
			List<String> parameterValue = StringUtil.split(
				parameterValueString, CharPool.EQUAL);

			_recordedParameterMap.put(
				parameterValue.get(0), parameterValue.get(1));
		}
	}

	private void _startHttpServer() throws Exception {
		_httpServer = HttpServer.create(new InetSocketAddress(_PORT), 0);

		HttpContext httpContext = _httpServer.createContext(
			"/commerce/tax-value");

		httpContext.setHandler(new TaxValueHttpHandler());

		_httpServer.start();
	}

	private static final int _PORT = 4250;

	private CommerceAddress _commerceBillingAddress;
	private CommerceAddress _commerceShippingAddress;
	private CommerceTaxMethod _commerceTaxMethod;
	private CPTaxCategory _cpTaxCategory;
	private HttpServer _httpServer;
	private final ObjectMapper _objectMapper = new ObjectMapper();
	private final Map<String, String> _recordedParameterMap = new HashMap<>();
	private RemoteCommerceTaxEngine _remoteCommerceTaxEngine;
	private final CommerceTaxValue _returnedCommerceTaxValue =
		new CommerceTaxValue("name", "label", BigDecimal.valueOf(118.8));

	private class TaxValueHttpHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
			_recordQueryParameters(httpExchange.getRequestURI());

			String payload = _objectMapper.writeValueAsString(
				_returnedCommerceTaxValue);

			byte[] bytes = payload.getBytes();

			httpExchange.sendResponseHeaders(200, bytes.length);

			try (OutputStream outputStream = httpExchange.getResponseBody()) {
				outputStream.write(bytes);

				outputStream.flush();
			}
		}

	}

}