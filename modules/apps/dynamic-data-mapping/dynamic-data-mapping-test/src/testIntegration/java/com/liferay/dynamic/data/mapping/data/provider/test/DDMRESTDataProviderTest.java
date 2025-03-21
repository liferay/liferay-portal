/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.data.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProvider;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderException;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderRequest;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderResponse;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderResponseStatus;
import com.liferay.dynamic.data.mapping.data.provider.configuration.DDMDataProviderConfiguration;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ResourcePermissionTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.core.Application;

import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Marcellus Tavares
 */
@RunWith(Arquillian.class)
public class DDMRESTDataProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		ConfigurationTestUtil.saveConfiguration(
			DDMDataProviderConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"accessLocalNetwork", true
			).build());

		Bundle bundle = FrameworkUtil.getBundle(DDMRESTDataProviderTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			Application.class, new TestDDMDataProviderApplication(),
			HashMapDictionaryBuilder.put(
				"osgi.jaxrs.application.base", "/ddm"
			).put(
				"osgi.jaxrs.name",
				TestDDMDataProviderApplication.class.getName()
			).build());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ConfigurationTestUtil.saveConfiguration(
			DDMDataProviderConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"accessLocalNetwork", false
			).build());

		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Before
	public void setUp() throws Exception {
		_setUpPermissionThreadLocal();
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
	}

	@Test
	public void testGetData() throws Exception {
		_setUserPermissionChecker(false);

		String outputParameterId = StringUtil.randomString();

		long ddmDataProviderId = _addDDMDataProviderInstance(
			_createDDMDataProviderDDMFormValues(
				false, false, StringPool.BLANK, null, outputParameterId,
				"nameCurrentValue;name", "list", null, null,
				_GET_COUNTRIES_URL),
			false);

		DDMDataProviderResponse ddmDataProviderResponse =
			_ddmDataProvider.getData(
				_createDDMDataProviderRequest(
					ddmDataProviderId, null, null, null, null, null, null));

		List<KeyValuePair> keyValuePairs = ddmDataProviderResponse.getOutput(
			outputParameterId, List.class);

		Assert.assertTrue(
			keyValuePairs.containsAll(
				ListUtil.fromArray(
					new KeyValuePair("france", "France"),
					new KeyValuePair("spain", "Spain"),
					new KeyValuePair("united-states", "United States"),
					new KeyValuePair("brazil", "Brazil"))));
	}

	@Test
	public void testGetDataWithCache() throws Exception {
		_setUserPermissionChecker(false);

		long ddmDataProviderId = _addDDMDataProviderInstance(
			_createDDMDataProviderDDMFormValues(
				true, false, StringPool.BLANK, "name",
				StringUtil.randomString(), "nameCurrentValue;name", "list",
				null, null, _GET_COUNTRY_BY_NAME_URL),
			false);

		DDMDataProviderRequest ddmDataProviderRequest =
			_createDDMDataProviderRequest(
				ddmDataProviderId, null, "name", "brazil", null, null, null);

		_ddmDataProvider.getData(ddmDataProviderRequest);

		String cacheKey = StringBundler.concat(
			ddmDataProviderRequest.getDDMDataProviderId(), StringPool.AT,
			_GET_COUNTRY_BY_NAME_URL, "?name=brazil");

		Class<?> clazz = _ddmDataProvider.getClass();

		PortalCache<String, DDMDataProviderResponse> portalCache =
			PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.MULTI_VM, clazz.getName());

		Assert.assertNotNull(portalCache.get(cacheKey));

		portalCache.remove(cacheKey);
	}

	@Test
	public void testGetDataWithFilterParameter() throws Exception {
		_setUserPermissionChecker(false);

		String outputParameterId = StringUtil.randomString();

		long ddmDataProviderId = _addDDMDataProviderInstance(
			_createDDMDataProviderDDMFormValues(
				false, true, "name", null, outputParameterId,
				"nameCurrentValue;name", "list", null, null,
				_GET_COUNTRY_BY_NAME_URL),
			false);

		DDMDataProviderResponse ddmDataProviderResponse =
			_ddmDataProvider.getData(
				_createDDMDataProviderRequest(
					ddmDataProviderId, "brazil", null, null, null, null, null));

		List<KeyValuePair> keyValuePairs = ddmDataProviderResponse.getOutput(
			outputParameterId, List.class);

		Assert.assertEquals(keyValuePairs.toString(), 1, keyValuePairs.size());

		KeyValuePair keyValuePair = keyValuePairs.get(0);

		Assert.assertEquals("brazil", keyValuePair.getKey());
		Assert.assertEquals("Brazil", keyValuePair.getValue());
	}

	@Test
	public void testGetDataWithInputParameters() throws Exception {
		_setUserPermissionChecker(false);

		String outputParameterId = StringUtil.randomString();

		long ddmDataProviderId = _addDDMDataProviderInstance(
			_createDDMDataProviderDDMFormValues(
				false, false, StringPool.BLANK, "name", outputParameterId,
				"nameCurrentValue", "list", null, null,
				_GET_COUNTRY_BY_NAME_URL),
			false);

		DDMDataProviderResponse ddmDataProviderResponse =
			_ddmDataProvider.getData(
				_createDDMDataProviderRequest(
					ddmDataProviderId, null, "name", "brazil", null, null,
					null));

		List<KeyValuePair> keyValuePairs = ddmDataProviderResponse.getOutput(
			outputParameterId, List.class);

		Assert.assertEquals(keyValuePairs.toString(), 1, keyValuePairs.size());

		KeyValuePair keyValuePair = keyValuePairs.get(0);

		Assert.assertEquals("Brazil", keyValuePair.getKey());
		Assert.assertEquals("Brazil", keyValuePair.getValue());
	}

	@Test
	public void testGetDataWithInputParametersInURL() throws Exception {
		_setUserPermissionChecker(false);

		String outputParameterId = StringUtil.randomString();

		long ddmDataProviderId = _addDDMDataProviderInstance(
			_createDDMDataProviderDDMFormValues(
				false, false, StringPool.BLANK, "name", outputParameterId,
				"nameCurrentValue", "list", null, null,
				_GET_COUNTRY_BY_NAME_URL + "?name={name}"),
			false);

		DDMDataProviderResponse ddmDataProviderResponse =
			_ddmDataProvider.getData(
				_createDDMDataProviderRequest(
					ddmDataProviderId, null, "name", "brazil", null, null,
					null));

		List<KeyValuePair> keyValuePairs = ddmDataProviderResponse.getOutput(
			outputParameterId, List.class);

		Assert.assertEquals(keyValuePairs.toString(), 1, keyValuePairs.size());

		KeyValuePair keyValuePair = keyValuePairs.get(0);

		Assert.assertEquals("Brazil", keyValuePair.getKey());
		Assert.assertEquals("Brazil", keyValuePair.getValue());

		String firstName = RandomTestUtil.randomString();
		String lastName = RandomTestUtil.randomString();

		ddmDataProviderId = _addDDMDataProviderInstance(
			_createDDMDataProviderDDMFormValues(
				false, false, null, null, outputParameterId, "$.fullName",
				"text", null, null,
				String.format(
					"http://localhost:8080/o/ddm/get-full-name?name=%s&name=%s",
					firstName, lastName)),
			false);

		ddmDataProviderResponse = _ddmDataProvider.getData(
			DDMDataProviderRequest.Builder.newBuilder(
			).withDDMDataProviderId(
				String.valueOf(ddmDataProviderId)
			).build());

		Assert.assertEquals(
			firstName + StringPool.SPACE + lastName,
			ddmDataProviderResponse.getOutput(outputParameterId, String.class));
	}

	@Test
	public void testGetDataWithLocale() throws Exception {
		_setUserPermissionChecker(false);

		String outputParameterId = StringUtil.randomString();

		long ddmDataProviderId = _addDDMDataProviderInstance(
			_createDDMDataProviderDDMFormValues(
				false, false, StringPool.BLANK, null, outputParameterId,
				"nameCurrentValue;name", "list", null, null,
				_GET_COUNTRIES_URL),
			false);

		_testGetDataWithLocale(
			ddmDataProviderId, LocaleUtil.FRANCE, outputParameterId);
		_testGetDataWithLocale(
			ddmDataProviderId, LocaleUtil.GERMANY, outputParameterId);
		_testGetDataWithLocale(
			ddmDataProviderId, LocaleUtil.US, outputParameterId);
	}

	@Test
	public void testGetDataWithNumberOutput() throws Exception {
		_setUserPermissionChecker(false);

		String outputParameterId = StringUtil.randomString();

		long ddmDataProviderId = _addDDMDataProviderInstance(
			_createDDMDataProviderDDMFormValues(
				false, false, null, null, outputParameterId, "$.length()",
				"number", null, null, _GET_COUNTRIES_URL),
			false);

		DDMDataProviderResponse ddmDataProviderResponse =
			_ddmDataProvider.getData(
				_createDDMDataProviderRequest(
					ddmDataProviderId, null, null, null, null, null, null));

		Assert.assertEquals(
			_countryLocalService.getCompanyCountriesCount(
				TestPropsValues.getCompanyId()),
			(int)ddmDataProviderResponse.getOutput(
				outputParameterId, Number.class));
	}

	@Test
	public void testGetDataWithoutDataProvider() throws Exception {
		DDMDataProviderRequest.Builder builder =
			DDMDataProviderRequest.Builder.newBuilder();

		DDMDataProviderResponse ddmDataProviderResponse =
			_ddmDataProvider.getData(
				builder.withDDMDataProviderId(
					StringUtil.randomString()
				).build());

		Assert.assertEquals(
			DDMDataProviderResponseStatus.SERVICE_UNAVAILABLE,
			ddmDataProviderResponse.getStatus());
	}

	@Test
	public void testGetDataWithoutOutputParameters() throws Exception {
		_setUserPermissionChecker(false);

		String outputParameterId = StringUtil.randomString();

		long ddmDataProviderId = _addDDMDataProviderInstance(
			_createDDMDataProviderDDMFormValues(
				false, false, StringPool.BLANK, null, outputParameterId, null,
				null, null, null, _GET_COUNTRIES_URL),
			false);

		DDMDataProviderResponse ddmDataProviderResponse =
			_ddmDataProvider.getData(
				_createDDMDataProviderRequest(
					ddmDataProviderId, null, null, null, null, null, null));

		Assert.assertEquals(
			DDMDataProviderResponseStatus.OK,
			ddmDataProviderResponse.getStatus());
		Assert.assertEquals(
			ddmDataProviderResponse.toString(), 0,
			ddmDataProviderResponse.size());
	}

	@Test(expected = DDMDataProviderException.class)
	public void testGetDataWithoutViewDataProviderPermission()
		throws Exception {

		_setUserPermissionChecker(true);

		long ddmDataProviderId = _addDDMDataProviderInstance(
			_createDDMDataProviderDDMFormValues(
				false, false, StringPool.BLANK, null, StringUtil.randomString(),
				"nameCurrentValue;name", "list", null, null,
				_GET_COUNTRIES_URL),
			false);

		DDMDataProviderRequest ddmDataProviderRequest =
			_createDDMDataProviderRequest(
				ddmDataProviderId, null, null, null, null, null, null);

		_ddmDataProvider.getData(ddmDataProviderRequest);
	}

	@Test
	public void testGetDataWithPagination() throws Exception {
		_setUserPermissionChecker(false);

		String outputParameterId = StringUtil.randomString();

		long ddmDataProviderId = _addDDMDataProviderInstance(
			_createDDMDataProviderDDMFormValues(
				false, false, StringPool.BLANK, null, outputParameterId,
				"nameCurrentValue", "list", "7", "2", _GET_COUNTRIES_URL),
			false);

		DDMDataProviderResponse ddmDataProviderResponse =
			_ddmDataProvider.getData(
				_createDDMDataProviderRequest(
					ddmDataProviderId, null, null, null, null, "7", "2"));

		Assert.assertEquals(
			DDMDataProviderResponseStatus.OK,
			ddmDataProviderResponse.getStatus());

		List<KeyValuePair> keyValuePairs = ddmDataProviderResponse.getOutput(
			outputParameterId, List.class);

		Assert.assertEquals(keyValuePairs.toString(), 5, keyValuePairs.size());
	}

	@Test
	public void testGetDataWithTextOutput() throws Exception {
		_setUserPermissionChecker(false);

		String outputParameterId = StringUtil.randomString();

		long ddmDataProviderId = _addDDMDataProviderInstance(
			_createDDMDataProviderDDMFormValues(
				false, false, StringPool.BLANK, "name", outputParameterId,
				"$.nameCurrentValue", "text", null, null,
				_GET_COUNTRY_BY_NAME_URL + "?name={name}"),
			false);

		DDMDataProviderResponse ddmDataProviderResponse =
			_ddmDataProvider.getData(
				_createDDMDataProviderRequest(
					ddmDataProviderId, null, "name", "brazil", null, null,
					null));

		Assert.assertNotNull(ddmDataProviderResponse);

		Assert.assertEquals(
			"Brazil",
			ddmDataProviderResponse.getOutput(outputParameterId, String.class));
	}

	@Test
	public void testGetDataWithViewDataProviderPermission() throws Exception {
		_setUserPermissionChecker(true);

		String outputParameterId = StringUtil.randomString();

		long ddmDataProviderId = _addDDMDataProviderInstance(
			_createDDMDataProviderDDMFormValues(
				false, true, "name", null, outputParameterId,
				"nameCurrentValue;name", "list", null, null,
				_GET_COUNTRY_BY_NAME_URL),
			true);

		DDMDataProviderResponse ddmDataProviderResponse =
			_ddmDataProvider.getData(
				_createDDMDataProviderRequest(
					ddmDataProviderId, "canada", null, null, null, null, null));

		Assert.assertNotNull(ddmDataProviderResponse);

		List<KeyValuePair> keyValuePairs = ddmDataProviderResponse.getOutput(
			outputParameterId, List.class);

		Assert.assertEquals(keyValuePairs.toString(), 1, keyValuePairs.size());

		KeyValuePair keyValuePair = keyValuePairs.get(0);

		Assert.assertEquals("canada", keyValuePair.getKey());
		Assert.assertEquals("Canada", keyValuePair.getValue());
	}

	@Test
	public void testGetDataWithWebServiceError() throws Exception {
		_setUserPermissionChecker(false);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.dynamic.data.mapping.data.provider.internal." +
					"rest.DDMRESTDataProvider",
				LoggerTestUtil.WARN)) {

			String outputParameterId = StringUtil.randomString();

			DDMDataProviderResponse ddmDataProviderResponse =
				_ddmDataProvider.getData(
					_createDDMDataProviderRequest(
						_addDDMDataProviderInstance(
							_createDDMDataProviderDDMFormValues(
								false, false, StringPool.BLANK, null,
								outputParameterId, "nameCurrentValue;name",
								"list", null, null, "http://localhost"),
							false),
						null, null, null, null, null, null));

			Assert.assertTrue(
				ListUtil.isEmpty(
					ddmDataProviderResponse.getOutput(
						outputParameterId, List.class)));
			Assert.assertEquals(
				DDMDataProviderResponseStatus.SERVICE_UNAVAILABLE,
				ddmDataProviderResponse.getStatus());
		}
	}

	private long _addDDMDataProviderInstance(
			DDMFormValues ddmFormValues, boolean guestPermission)
		throws Exception {

		DDMDataProviderInstance ddmDataProviderInstance =
			_ddmDataProviderInstanceLocalService.addDataProviderInstance(
				TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				HashMapBuilder.put(
					LocaleUtil.US, "Test"
				).build(),
				null, ddmFormValues, "rest", new ServiceContext());

		long dataProviderInstanceId =
			ddmDataProviderInstance.getDataProviderInstanceId();

		if (guestPermission) {
			Role role = _roleLocalService.getRole(
				TestPropsValues.getCompanyId(), RoleConstants.GUEST);

			ResourcePermissionTestUtil.addResourcePermission(
				1L,
				"com.liferay.dynamic.data.mapping.model." +
					"DDMDataProviderInstance",
				String.valueOf(dataProviderInstanceId), role.getRoleId(),
				ResourceConstants.SCOPE_INDIVIDUAL);
		}

		return dataProviderInstanceId;
	}

	private DDMFormValues _createDDMDataProviderDDMFormValues(
		boolean cacheable, boolean filterable, String filterParameterName,
		String inputParameterName, String outputParameterId,
		String outputParameterPath, String outputParameterType,
		String paginationEnd, String paginationStart, String url) {

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			DDMFormFactory.create(_ddmDataProvider.getSettings()));

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"cacheable", String.valueOf(cacheable)));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"filterable", String.valueOf(filterable)));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"filterParameterName", filterParameterName));

		if (Validator.isNotNull(inputParameterName)) {
			DDMFormFieldValue inputParameters =
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					"inputParameters", null);

			ddmFormValues.addDDMFormFieldValue(inputParameters);

			inputParameters.addNestedDDMFormFieldValue(
				DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
					"inputParameterLabel", "input"));
			inputParameters.addNestedDDMFormFieldValue(
				DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
					"inputParameterName", inputParameterName));
			inputParameters.addNestedDDMFormFieldValue(
				DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
					"inputParameterRequired", Boolean.FALSE.toString()));
			inputParameters.addNestedDDMFormFieldValue(
				DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
					"inputParameterType", "[\"text\"]"));
		}

		if (Validator.isNotNull(outputParameterId) &&
			Validator.isNotNull(outputParameterPath) &&
			Validator.isNotNull(outputParameterType)) {

			DDMFormFieldValue outputParameters =
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					"outputParameters", null);

			ddmFormValues.addDDMFormFieldValue(outputParameters);

			outputParameters.addNestedDDMFormFieldValue(
				DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
					"outputParameterId", outputParameterId));
			outputParameters.addNestedDDMFormFieldValue(
				DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
					"outputParameterName", "output"));
			outputParameters.addNestedDDMFormFieldValue(
				DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
					"outputParameterPath", outputParameterPath));
			outputParameters.addNestedDDMFormFieldValue(
				DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
					"outputParameterType",
					"[\"" + outputParameterType + "\"]"));
		}

		if (Validator.isNotNull(paginationEnd) &&
			Validator.isNotNull(paginationStart)) {

			ddmFormValues.addDDMFormFieldValue(
				DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
					"pagination", Boolean.TRUE.toString()));
			ddmFormValues.addDDMFormFieldValue(
				DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
					"paginationEnd", paginationEnd));
			ddmFormValues.addDDMFormFieldValue(
				DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
					"paginationStart", paginationStart));
		}

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"password", TestPropsValues.USER_PASSWORD));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"timeout", "1000"));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"url", url));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"username", "test@liferay.com"));

		return ddmFormValues;
	}

	private DDMDataProviderRequest _createDDMDataProviderRequest(
		long ddmDataProviderId, String filterParameterValue,
		String inputParameterName, String inputParameterValue, Locale locale,
		String paginationEnd, String paginationStart) {

		return DDMDataProviderRequest.Builder.newBuilder(
		).withDDMDataProviderId(
			String.valueOf(ddmDataProviderId)
		).withLocale(
			locale
		).withParameter(
			inputParameterName, inputParameterValue
		).withParameter(
			"filterParameterValue", filterParameterValue
		).withParameter(
			"paginationEnd", paginationEnd
		).withParameter(
			"paginationStart", paginationStart
		).build();
	}

	private void _setUpPermissionThreadLocal() {
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
	}

	private void _setUserPermissionChecker(boolean guest) throws Exception {
		User user = TestPropsValues.getUser();

		if (guest) {
			user = _userLocalService.getGuestUser(
				TestPropsValues.getCompanyId());
		}

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
	}

	private void _testGetDataWithLocale(
			long ddmDataProviderId, Locale locale, String outputParameterId)
		throws Exception {

		DDMDataProviderResponse ddmDataProviderResponse =
			_ddmDataProvider.getData(
				_createDDMDataProviderRequest(
					ddmDataProviderId, null, null, null, locale, null, null));

		for (KeyValuePair keyValuePair :
				(List<KeyValuePair>)ddmDataProviderResponse.getOutput(
					outputParameterId, List.class)) {

			Assert.assertEquals(
				LanguageUtil.get(locale, "country." + keyValuePair.getKey()),
				keyValuePair.getValue());
		}
	}

	private static final String _GET_COUNTRIES_URL =
		"http://localhost:8080/api/jsonws/country/get-countries";

	private static final String _GET_COUNTRY_BY_NAME_URL =
		"http://localhost:8080/api/jsonws/country/get-country-by-name";

	private static ServiceRegistration<Application> _serviceRegistration;

	@Inject
	private CountryLocalService _countryLocalService;

	@Inject(
		filter = "ddm.data.provider.type=rest", type = DDMDataProvider.class
	)
	private DDMDataProvider _ddmDataProvider;

	@Inject(type = DDMDataProviderInstanceLocalService.class)
	private DDMDataProviderInstanceLocalService
		_ddmDataProviderInstanceLocalService;

	@Inject
	private Language _language;

	private PermissionChecker _originalPermissionChecker;

	@Inject(type = RoleLocalService.class)
	private RoleLocalService _roleLocalService;

	@Inject(type = UserLocalService.class)
	private UserLocalService _userLocalService;

}