/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.validator.vies.internal;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountEntryValidatorConstants;
import com.liferay.account.manager.AccountEntryValidatorResultManager;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.validator.AccountEntryValidatorResult;
import com.liferay.account.validator.vies.configuration.VIESAccountEntryValidatorConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;

import java.net.InetSocketAddress;

import java.nio.charset.StandardCharsets;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Crescenzo Rega
 */
public class VIESAccountEntryValidatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_viesAccountEntryValidator, "accountEntryValidatorResultManager",
			_accountEntryValidatorResultManager);
		ReflectionTestUtil.setFieldValue(
			_viesAccountEntryValidator, "_addressLocalService",
			_addressLocalService);
		ReflectionTestUtil.setFieldValue(
			_viesAccountEntryValidator, "_configurationProvider",
			_configurationProvider);
	}

	@After
	public void tearDown() {
		if (_httpServer != null) {
			_httpServer.stop(0);
		}
	}

	@Test
	public void testGetAccountEntryValidatorConfiguration() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		VIESAccountEntryValidatorConfiguration
			viesAccountEntryValidatorConfiguration = Mockito.mock(
				VIESAccountEntryValidatorConfiguration.class);

		Mockito.when(
			_configurationProvider.getCompanyConfiguration(
				VIESAccountEntryValidatorConfiguration.class, companyId)
		).thenReturn(
			viesAccountEntryValidatorConfiguration
		);

		Assert.assertSame(
			viesAccountEntryValidatorConfiguration,
			_viesAccountEntryValidator.getAccountEntryValidatorConfiguration(
				companyId));
	}

	@Test
	public void testGetClassPK() throws Exception {
		_mockVIESAccountEntryValidatorConfiguration(
			new String[] {RandomTestUtil.randomString()}, true);

		Assert.assertNull(
			_viesAccountEntryValidator.getClassPK(
				null,
				JSONUtil.put("billingAddressId", RandomTestUtil.randomLong())));

		AccountEntry accountEntry = _mockAccountEntry(
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS);

		Assert.assertNull(
			_viesAccountEntryValidator.getClassPK(
				accountEntry, JSONUtil.put("billingAddressId", 0L)));

		long accountEntryId = RandomTestUtil.randomLong();

		Mockito.when(
			accountEntry.getAccountEntryId()
		).thenReturn(
			accountEntryId
		);

		long billingAddressId = RandomTestUtil.randomLong();

		Assert.assertEquals(
			accountEntryId + "_" + billingAddressId,
			_viesAccountEntryValidator.getClassPK(
				accountEntry,
				JSONUtil.put("billingAddressId", billingAddressId)));
	}

	@Test
	public void testValidate() throws Exception {
		try (MockedStatic<ConfigurationProviderUtil>
				configurationProviderUtilMockedStatic = Mockito.mockStatic(
					ConfigurationProviderUtil.class)) {

			_mockVIESAccountEntryValidatorConfiguration(
				new String[] {RandomTestUtil.randomString()}, false);

			Assert.assertNull(
				_validate(
					RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
					RandomTestUtil.randomString()));

			_mockVIESAccountEntryValidatorConfiguration(
				new String[] {RandomTestUtil.randomString()}, true);

			Assert.assertNull(
				_viesAccountEntryValidator.validate(
					_mockAccountEntry(
						AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON),
					JSONUtil.put(
						"billingAddressId", RandomTestUtil.randomLong())));

			Mockito.verifyNoInteractions(_addressLocalService);

			Assert.assertNull(
				_validate(
					RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
					RandomTestUtil.randomString()));

			long billingAddressId = RandomTestUtil.randomLong();
			String countryA2 = RandomTestUtil.randomString();

			_mockAddress(billingAddressId, countryA2);

			_mockVIESAccountEntryValidatorConfiguration(
				new String[] {RandomTestUtil.randomString()}, true);

			Assert.assertNull(
				_validate(
					billingAddressId, RandomTestUtil.randomLong(),
					RandomTestUtil.randomString()));

			_mockVIESAccountEntryValidatorConfiguration(new String[0], true);

			Assert.assertNull(
				_validate(
					billingAddressId, RandomTestUtil.randomLong(),
					RandomTestUtil.randomString()));

			VIESAccountEntryValidatorConfiguration
				viesAccountEntryValidatorConfiguration =
					_mockVIESAccountEntryValidatorConfiguration(
						new String[] {countryA2}, true);

			Mockito.when(
				viesAccountEntryValidatorConfiguration.viesEndpointURL()
			).thenReturn(
				"http://localhost:" + _PORT + "/check-vat-number"
			);

			configurationProviderUtilMockedStatic.when(
				() -> ConfigurationProviderUtil.getCompanyConfiguration(
					Mockito.eq(VIESAccountEntryValidatorConfiguration.class),
					Mockito.anyLong())
			).thenReturn(
				viesAccountEntryValidatorConfiguration
			);

			AccountEntryValidatorResult accountEntryValidatorResult = _validate(
				billingAddressId, RandomTestUtil.randomLong(), null);

			Assert.assertEquals(
				AccountEntryValidatorConstants.RESULT_FAILURE,
				accountEntryValidatorResult.getResultStatus());
			Assert.assertFalse(accountEntryValidatorResult.isValid());

			String blockedVatNumber = RandomTestUtil.randomString();
			String invalidInputVatNumber = RandomTestUtil.randomString();
			String invalidVatNumber = RandomTestUtil.randomString();
			String unavailableVatNumber = RandomTestUtil.randomString();
			String unexpectedVatNumber = RandomTestUtil.randomString();
			String validVatNumber = RandomTestUtil.randomString();

			_startHttpServer(
				HashMapBuilder.put(
					blockedVatNumber,
					JSONUtil.put(
						"errorWrappers",
						JSONUtil.putAll(JSONUtil.put("error", "VAT_BLOCKED"))
					).toString()
				).put(
					invalidInputVatNumber,
					JSONUtil.put(
						"errorWrappers",
						JSONUtil.putAll(JSONUtil.put("error", "INVALID_INPUT"))
					).toString()
				).put(
					invalidVatNumber,
					JSONUtil.put(
						"valid", false
					).toString()
				).put(
					unavailableVatNumber,
					JSONUtil.put(
						"errorWrappers",
						JSONUtil.putAll(JSONUtil.put("error", "MS_UNAVAILABLE"))
					).toString()
				).put(
					unexpectedVatNumber,
					JSONUtil.put(
						"errorWrappers",
						JSONUtil.putAll(
							JSONUtil.put(
								"error", RandomTestUtil.randomString()))
					).toString()
				).put(
					validVatNumber,
					JSONUtil.put(
						"valid", true
					).toString()
				).build());

			accountEntryValidatorResult = _validate(
				billingAddressId, RandomTestUtil.randomLong(),
				blockedVatNumber);

			Assert.assertEquals(
				"vies-vat-blocked-error",
				accountEntryValidatorResult.getResultMessage());
			Assert.assertEquals(
				AccountEntryValidatorConstants.RESULT_FAILURE,
				accountEntryValidatorResult.getResultStatus());
			Assert.assertFalse(accountEntryValidatorResult.isValid());

			accountEntryValidatorResult = _validate(
				billingAddressId, RandomTestUtil.randomLong(),
				invalidInputVatNumber);

			Assert.assertEquals(
				"vies-invalid-input-error",
				accountEntryValidatorResult.getResultMessage());
			Assert.assertEquals(
				AccountEntryValidatorConstants.RESULT_FAILURE,
				accountEntryValidatorResult.getResultStatus());
			Assert.assertFalse(accountEntryValidatorResult.isValid());

			accountEntryValidatorResult = _validate(
				billingAddressId, RandomTestUtil.randomLong(),
				invalidVatNumber);

			Assert.assertEquals(
				AccountEntryValidatorConstants.RESULT_FAILURE,
				accountEntryValidatorResult.getResultStatus());
			Assert.assertFalse(accountEntryValidatorResult.isValid());

			accountEntryValidatorResult = _validate(
				billingAddressId, RandomTestUtil.randomLong(),
				unavailableVatNumber);

			Assert.assertEquals(
				"vies-unexpected-error",
				accountEntryValidatorResult.getResultMessage());
			Assert.assertEquals(
				AccountEntryValidatorConstants.RESULT_WARNING,
				accountEntryValidatorResult.getResultStatus());
			Assert.assertTrue(accountEntryValidatorResult.isValid());

			accountEntryValidatorResult = _validate(
				billingAddressId, RandomTestUtil.randomLong(),
				unexpectedVatNumber);

			Assert.assertEquals(
				"vies-unexpected-error",
				accountEntryValidatorResult.getResultMessage());
			Assert.assertEquals(
				AccountEntryValidatorConstants.RESULT_WARNING,
				accountEntryValidatorResult.getResultStatus());
			Assert.assertTrue(accountEntryValidatorResult.isValid());

			accountEntryValidatorResult = _validate(
				billingAddressId, RandomTestUtil.randomLong(), validVatNumber);

			Assert.assertEquals(
				AccountEntryValidatorConstants.RESULT_SUCCESS,
				accountEntryValidatorResult.getResultStatus());
			Assert.assertTrue(accountEntryValidatorResult.isValid());
		}
	}

	private AccountEntry _mockAccountEntry(String type) {
		AccountEntry accountEntry = Mockito.mock(AccountEntry.class);

		Mockito.when(
			accountEntry.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			accountEntry.getTaxIdNumber()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			accountEntry.getType()
		).thenReturn(
			type
		);

		return accountEntry;
	}

	private void _mockAddress(long addressId, String countryA2) {
		Country country = Mockito.mock(Country.class);

		Mockito.when(
			country.getA2()
		).thenReturn(
			countryA2
		);

		Address address = Mockito.mock(Address.class);

		Mockito.when(
			address.getClassName()
		).thenReturn(
			AccountEntry.class.getName()
		);

		Mockito.when(
			address.getCountry()
		).thenReturn(
			country
		);

		Mockito.when(
			_addressLocalService.fetchAddress(addressId)
		).thenReturn(
			address
		);
	}

	private VIESAccountEntryValidatorConfiguration
			_mockVIESAccountEntryValidatorConfiguration(
				String[] countryCodes, boolean enabled)
		throws Exception {

		VIESAccountEntryValidatorConfiguration
			viesAccountEntryValidatorConfiguration = Mockito.mock(
				VIESAccountEntryValidatorConfiguration.class);

		Mockito.when(
			viesAccountEntryValidatorConfiguration.countryCodes()
		).thenReturn(
			countryCodes
		);

		Mockito.when(
			viesAccountEntryValidatorConfiguration.enabled()
		).thenReturn(
			enabled
		);

		Mockito.when(
			_configurationProvider.getCompanyConfiguration(
				Mockito.eq(VIESAccountEntryValidatorConfiguration.class),
				Mockito.anyLong())
		).thenReturn(
			viesAccountEntryValidatorConfiguration
		);

		return viesAccountEntryValidatorConfiguration;
	}

	private void _startHttpServer(Map<String, String> responses)
		throws Exception {

		_httpServer = HttpServer.create(new InetSocketAddress(_PORT), 0);

		HttpContext httpContext = _httpServer.createContext(
			"/check-vat-number");

		httpContext.setHandler(
			httpExchange -> {
				JSONObject jsonObject = null;

				try {
					jsonObject = JSONFactoryUtil.createJSONObject(
						StringUtil.read(httpExchange.getRequestBody()));
				}
				catch (JSONException jsonException) {
					throw new IOException(jsonException);
				}

				String response = responses.get(
					jsonObject.getString("vatNumber"));

				byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

				httpExchange.sendResponseHeaders(200, bytes.length);

				try (OutputStream outputStream =
						httpExchange.getResponseBody()) {

					outputStream.write(bytes);

					outputStream.flush();
				}
			});

		_httpServer.start();
	}

	private AccountEntryValidatorResult _validate(
			long billingAddressId, long companyId, String taxIdNumber)
		throws Exception {

		AccountEntry accountEntry = _mockAccountEntry(
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS);

		Mockito.when(
			accountEntry.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			accountEntry.getTaxIdNumber()
		).thenReturn(
			taxIdNumber
		);

		return _viesAccountEntryValidator.validate(
			accountEntry, JSONUtil.put("billingAddressId", billingAddressId));
	}

	private static final int _PORT = 4252;

	private final AccountEntryValidatorResultManager
		_accountEntryValidatorResultManager = Mockito.mock(
			AccountEntryValidatorResultManager.class);
	private final AddressLocalService _addressLocalService = Mockito.mock(
		AddressLocalService.class);
	private final ConfigurationProvider _configurationProvider = Mockito.mock(
		ConfigurationProvider.class);
	private HttpServer _httpServer;
	private final VIESAccountEntryValidator _viesAccountEntryValidator =
		new VIESAccountEntryValidator();

}