/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.internal;

import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.tax.CommerceTaxCalculateRequest;
import com.liferay.commerce.tax.engine.internal.configuration.FunctionCommerceTaxEngineConfiguration;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxCategoryMappingLocalService;
import com.liferay.commerce.tax.service.CommerceTaxMethodLocalService;
import com.liferay.portal.catapult.PortalCatapult;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;

import java.math.BigDecimal;

import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Crescenzo Rega
 */
public class FunctionCommerceTaxEngineTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		_setUpCommerceChannelLocalService();
		_setUpCommerceTaxCategoryMappingLocalService();
		_setUpCommerceTaxMethodLocalService();
		_setUpDtoConverterRegistry();
		_setUpFunctionCommerceTaxEngineConfiguration();
		_setUpJSONFactory();
		_setUpPortalCatapult();
		_setUpUserService();
	}

	@Test
	public void testDeactivate() throws Exception {
		_functionCommerceTaxEngine.deactivate();

		Mockito.verify(
			_commerceTaxMethodLocalService, Mockito.never()
		).deleteCommerceTaxMethod(
			Mockito.anyLong()
		);
	}

	@Test
	public void testGetCommerceTaxValue() throws Exception {
		CommerceTaxCalculateRequest commerceTaxCalculateRequest =
			_setUpCommerceTaxCalculateRequest("EUR");

		Assert.assertNotNull(
			_functionCommerceTaxEngine.getCommerceTaxValue(
				commerceTaxCalculateRequest));

		JSONObject payloadJSONObject =
			_functionCommerceTaxEngine.getPayloadJSONObject(
				commerceTaxCalculateRequest);

		JSONObject commerceTaxCalculateRequestJSONObject =
			payloadJSONObject.getJSONObject("commerceTaxCalculateRequest");

		Assert.assertEquals(
			commerceTaxCalculateRequest.getCommerceCurrencyCode(),
			commerceTaxCalculateRequestJSONObject.get("currencyCode"));

		commerceTaxCalculateRequest = _setUpCommerceTaxCalculateRequest(null);

		Assert.assertNotNull(
			_functionCommerceTaxEngine.getCommerceTaxValue(
				commerceTaxCalculateRequest));

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByGroupId(
				Mockito.anyLong());

		payloadJSONObject = _functionCommerceTaxEngine.getPayloadJSONObject(
			commerceTaxCalculateRequest);

		commerceTaxCalculateRequestJSONObject = payloadJSONObject.getJSONObject(
			"commerceTaxCalculateRequest");

		Assert.assertEquals(
			commerceChannel.getCommerceCurrencyCode(),
			commerceTaxCalculateRequestJSONObject.get("currencyCode"));
	}

	private void _setUpCommerceChannelLocalService() throws Exception {
		CommerceChannel commerceChannel = Mockito.mock(CommerceChannel.class);

		Mockito.when(
			_commerceChannelLocalService.getCommerceChannelByGroupId(
				Mockito.anyLong())
		).thenReturn(
			commerceChannel
		);

		Mockito.when(
			commerceChannel.getCommerceCurrencyCode()
		).thenReturn(
			"USD"
		);

		Mockito.when(
			commerceChannel.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);
	}

	private CommerceTaxCalculateRequest _setUpCommerceTaxCalculateRequest(
		String commerceCurrencyCode) {

		CommerceTaxCalculateRequest commerceTaxCalculateRequest = Mockito.mock(
			CommerceTaxCalculateRequest.class);

		Mockito.when(
			commerceTaxCalculateRequest.getCommerceBillingAddressId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			commerceTaxCalculateRequest.getCommerceChannelGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			commerceTaxCalculateRequest.getCommerceCurrencyCode()
		).thenReturn(
			commerceCurrencyCode
		);

		Mockito.when(
			commerceTaxCalculateRequest.getCommerceShippingAddressId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			commerceTaxCalculateRequest.getPrice()
		).thenReturn(
			BigDecimal.valueOf(100.0)
		);

		Mockito.when(
			commerceTaxCalculateRequest.getTaxCategoryId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			commerceTaxCalculateRequest.isIncludeTax()
		).thenReturn(
			true
		);

		Mockito.when(
			commerceTaxCalculateRequest.isPercentage()
		).thenReturn(
			true
		);

		return commerceTaxCalculateRequest;
	}

	private void _setUpCommerceTaxCategoryMappingLocalService() {
		Mockito.when(
			_commerceTaxCategoryMappingLocalService.
				fetchCommerceTaxCategoryMapping(
					Mockito.anyLong(), Mockito.anyLong())
		).thenReturn(
			null
		);
	}

	private void _setUpCommerceTaxMethodLocalService() {
		CommerceTaxMethod commerceTaxMethod = Mockito.mock(
			CommerceTaxMethod.class);

		Mockito.when(
			_commerceTaxMethodLocalService.fetchCommerceTaxMethod(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			commerceTaxMethod
		);

		Mockito.when(
			commerceTaxMethod.getCommerceTaxMethodId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			commerceTaxMethod.getTypeSettingsUnicodeProperties()
		).thenReturn(
			RandomTestUtil.randomUnicodeProperties(4, 3, 3)
		);
	}

	private void _setUpDtoConverterRegistry() throws Exception {
		DTOConverter<?, ?> dtoConverter = Mockito.mock(DTOConverter.class);

		Mockito.doReturn(
			dtoConverter
		).when(
			_dtoConverterRegistry
		).getDTOConverter(
			Mockito.anyString()
		);

		Mockito.doReturn(
			new Object()
		).when(
			dtoConverter
		).toDTO(
			(DTOConverterContext)Mockito.any()
		);
	}

	private void _setUpFunctionCommerceTaxEngineConfiguration() {
		Mockito.when(
			_functionCommerceTaxEngineConfiguration.key()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_functionCommerceTaxEngineConfiguration.
				oAuth2ApplicationExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);
	}

	private void _setUpJSONFactory() throws Exception {
		Mockito.when(
			_jsonFactory.createJSONObject(Mockito.anyString())
		).thenReturn(
			JSONUtil.put("amount", 20.0)
		);

		Mockito.when(
			_jsonFactory.createJSONObject()
		).thenReturn(
			new JSONObjectImpl()
		);

		Mockito.when(
			_jsonFactory.looseSerializeDeep(Mockito.any())
		).thenReturn(
			RandomTestUtil.randomString()
		);
	}

	private void _setUpPortalCatapult() throws Exception {
		Future<byte[]> future = Mockito.mock(Future.class);

		Mockito.when(
			future.get()
		).thenReturn(
			RandomTestUtil.randomBytes()
		);

		Mockito.when(
			_portalCatapult.launch(
				Mockito.anyLong(), Mockito.any(), Mockito.anyString(),
				Mockito.any(), Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			future
		);
	}

	private void _setUpUserService() throws Exception {
		User user = Mockito.mock(User.class);

		Mockito.when(
			_userService.getCurrentUser()
		).thenReturn(
			user
		);

		Mockito.when(
			user.getUserId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);
	}

	@Mock
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Mock
	private CommerceTaxCategoryMappingLocalService
		_commerceTaxCategoryMappingLocalService;

	@Mock
	private CommerceTaxMethodLocalService _commerceTaxMethodLocalService;

	@Mock
	private DTOConverterRegistry _dtoConverterRegistry;

	@InjectMocks
	private FunctionCommerceTaxEngine _functionCommerceTaxEngine;

	@Mock
	private FunctionCommerceTaxEngineConfiguration
		_functionCommerceTaxEngineConfiguration;

	@Mock
	private JSONFactory _jsonFactory;

	@Mock
	private PortalCatapult _portalCatapult;

	@Mock
	private UserService _userService;

}