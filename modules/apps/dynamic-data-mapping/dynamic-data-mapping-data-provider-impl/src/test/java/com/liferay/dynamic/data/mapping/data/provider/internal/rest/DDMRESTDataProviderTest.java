/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.data.provider.internal.rest;

import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderInputParametersSettings;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderInstanceSettings;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderRequest;
import com.liferay.dynamic.data.mapping.data.provider.configuration.DDMDataProviderConfiguration;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.web.service.client.JSONWebServiceClient;
import com.liferay.portal.json.web.service.client.JSONWebServiceClientFactory;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Nathaly Gomes
 */
public class DDMRESTDataProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			SystemProperties.get("http.proxyHost")
		).thenReturn(
			_PROXY_HOST
		);

		Mockito.when(
			SystemProperties.get("http.proxyPort")
		).thenReturn(
			String.valueOf(_PROXY_PORT)
		);

		ReflectionTestUtil.setFieldValue(_ddmrestDataProvider, "_http", _http);
		ReflectionTestUtil.setFieldValue(
			_ddmrestDataProvider, "_secretResolver", _secretResolver);
	}

	@After
	public void tearDown() {
		_systemPropertiesMockedStatic.close();
	}

	@Test
	public void testGetData() throws Exception {
		DDMDataProviderInstanceService ddmDataProviderInstanceService =
			Mockito.mock(DDMDataProviderInstanceService.class);

		DDMDataProviderInstance ddmDataProviderInstance = Mockito.mock(
			DDMDataProviderInstance.class);

		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			ddmDataProviderInstance.getCompanyId()
		).thenReturn(
			companyId
		);

		long dataProviderInstanceId = RandomTestUtil.randomLong();

		Mockito.when(
			ddmDataProviderInstance.getDataProviderInstanceId()
		).thenReturn(
			dataProviderInstanceId
		);

		String ddmDataProviderId = RandomTestUtil.randomString();

		Mockito.when(
			ddmDataProviderInstanceService.fetchDataProviderInstanceByUuid(
				ddmDataProviderId)
		).thenReturn(
			ddmDataProviderInstance
		);

		ReflectionTestUtil.setFieldValue(
			_ddmrestDataProvider, "_ddmDataProviderInstanceService",
			ddmDataProviderInstanceService);

		DDMDataProviderInstanceSettings ddmDataProviderInstanceSettings =
			Mockito.mock(DDMDataProviderInstanceSettings.class);

		DDMRESTDataProviderSettings ddmRESTDataProviderSettings = Mockito.mock(
			DDMRESTDataProviderSettings.class);

		Mockito.when(
			ddmRESTDataProviderSettings.inputParameters()
		).thenReturn(
			new DDMDataProviderInputParametersSettings[0]
		);

		String password = RandomTestUtil.randomString();

		Mockito.when(
			ddmRESTDataProviderSettings.password()
		).thenReturn(
			password
		);

		Mockito.when(
			ddmRESTDataProviderSettings.url()
		).thenReturn(
			"http://" + RandomTestUtil.randomString()
		);

		Mockito.when(
			ddmDataProviderInstanceSettings.getSettings(
				ddmDataProviderInstance, DDMRESTDataProviderSettings.class)
		).thenReturn(
			ddmRESTDataProviderSettings
		);

		ReflectionTestUtil.setFieldValue(
			_ddmrestDataProvider, "_ddmDataProviderInstanceSettings",
			ddmDataProviderInstanceSettings);

		String resolvedPassword = RandomTestUtil.randomString();

		Mockito.when(
			_secretResolver.resolve(
				companyId,
				StringBundler.concat(
					DDMDataProviderInstance.class.getSimpleName(),
					StringPool.SLASH, companyId, StringPool.SLASH,
					dataProviderInstanceId, "/password"),
				password)
		).thenReturn(
			resolvedPassword
		);

		JSONWebServiceClientFactory jsonWebServiceClientFactory = Mockito.mock(
			JSONWebServiceClientFactory.class);

		JSONWebServiceClient jsonWebServiceClient = Mockito.mock(
			JSONWebServiceClient.class);

		Mockito.when(
			jsonWebServiceClient.doGet(
				Mockito.anyString(), Mockito.any(String[].class))
		).thenReturn(
			"{}"
		);

		Mockito.when(
			jsonWebServiceClientFactory.getInstance(
				Mockito.anyMap(), Mockito.eq(false))
		).thenReturn(
			jsonWebServiceClient
		);

		ReflectionTestUtil.setFieldValue(
			_ddmrestDataProvider, "_jsonWebServiceClientFactory",
			jsonWebServiceClientFactory);

		ReflectionTestUtil.setFieldValue(
			_ddmrestDataProvider, "_ddmDataProviderConfiguration",
			Mockito.mock(DDMDataProviderConfiguration.class));
		ReflectionTestUtil.setFieldValue(
			_ddmrestDataProvider, "_portalCache",
			Mockito.mock(PortalCache.class));

		_ddmrestDataProvider.getData(
			DDMDataProviderRequest.Builder.newBuilder(
			).withDDMDataProviderId(
				ddmDataProviderId
			).build());

		ArgumentCaptor<Map<String, Object>> argumentCaptor =
			ArgumentCaptor.forClass(Map.class);

		Mockito.verify(
			jsonWebServiceClientFactory
		).getInstance(
			argumentCaptor.capture(), Mockito.eq(false)
		);

		Map<String, Object> properties = argumentCaptor.getValue();

		Assert.assertEquals(resolvedPassword, properties.get("password"));
	}

	@Test
	public void testGetProxySettingsMap() {
		Mockito.when(
			_http.isNonProxyHost(Mockito.anyString())
		).thenReturn(
			true
		);

		Assert.assertTrue(
			MapUtil.isEmpty(
				_ddmrestDataProvider.getProxySettingsMap(
					RandomTestUtil.randomString())));

		Mockito.when(
			_http.isNonProxyHost(Mockito.anyString())
		).thenReturn(
			false
		);

		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"proxyHostName", _PROXY_HOST
			).put(
				"proxyHostPort", _PROXY_PORT
			).build(),
			_ddmrestDataProvider.getProxySettingsMap(
				RandomTestUtil.randomString()));
	}

	private static final String _PROXY_HOST = RandomTestUtil.randomString();

	private static final int _PROXY_PORT = RandomTestUtil.randomInt();

	private final DDMRESTDataProvider _ddmrestDataProvider =
		new DDMRESTDataProvider();
	private final Http _http = Mockito.mock(Http.class);
	private final SecretResolver _secretResolver = Mockito.mock(
		SecretResolver.class);
	private final MockedStatic<SystemProperties> _systemPropertiesMockedStatic =
		Mockito.mockStatic(SystemProperties.class);

}