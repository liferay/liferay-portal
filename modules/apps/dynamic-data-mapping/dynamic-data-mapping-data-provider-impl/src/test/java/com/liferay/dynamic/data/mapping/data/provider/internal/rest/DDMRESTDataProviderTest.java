/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.data.provider.internal.rest;

import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.key.secret.SecretResolverUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

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

		_secretResolverSnapshot = ReflectionTestUtil.getAndSetFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			new Snapshot<SecretResolver>(
				SecretResolverUtil.class, SecretResolver.class) {

				@Override
				public SecretResolver get() {
					return _secretResolver;
				}

			});
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			_secretResolverSnapshot);

		_systemPropertiesMockedStatic.close();
	}

	@Test
	public void testGetPassword() {
		long companyId = RandomTestUtil.randomLong();
		long dataProviderInstanceId = RandomTestUtil.randomLong();
		String password = RandomTestUtil.randomString();

		_secretResolver = Mockito.mock(SecretResolver.class);

		Mockito.when(
			_secretResolver.resolve(Mockito.eq(companyId), Mockito.anyString())
		).thenReturn(
			password
		);

		DDMDataProviderInstance ddmDataProviderInstance = Mockito.mock(
			DDMDataProviderInstance.class);

		Mockito.when(
			ddmDataProviderInstance.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			ddmDataProviderInstance.getDataProviderInstanceId()
		).thenReturn(
			dataProviderInstanceId
		);

		Assert.assertEquals(
			password,
			_getPassword(
				ddmDataProviderInstance,
				_getKeyReferenceString(companyId, dataProviderInstanceId)));
		Assert.assertEquals(
			password, _getPassword(ddmDataProviderInstance, password));

		String keyReferenceString = _getKeyReferenceString(
			companyId + 1, dataProviderInstanceId);

		Assert.assertEquals(
			keyReferenceString,
			_getPassword(ddmDataProviderInstance, keyReferenceString));

		keyReferenceString = _getKeyReferenceString(
			companyId, dataProviderInstanceId + 1);

		Assert.assertEquals(
			keyReferenceString,
			_getPassword(ddmDataProviderInstance, keyReferenceString));
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

	private String _getKeyReferenceString(
		long companyId, long dataProviderInstanceId) {

		return KeyReferenceUtil.toKeyReferenceString(
			new KeyReference(
				StringBundler.concat(
					DDMDataProviderInstance.class.getSimpleName(),
					StringPool.SLASH, companyId, StringPool.SLASH,
					dataProviderInstanceId),
				RandomTestUtil.randomString(), KeyReference.Type.SECRET));
	}

	private String _getPassword(
		DDMDataProviderInstance ddmDataProviderInstance, String password) {

		return ReflectionTestUtil.invoke(
			_ddmrestDataProvider, "_getPassword",
			new Class<?>[] {DDMDataProviderInstance.class, String.class},
			ddmDataProviderInstance, password);
	}

	private static final String _PROXY_HOST = RandomTestUtil.randomString();

	private static final int _PROXY_PORT = RandomTestUtil.randomInt();

	private final DDMRESTDataProvider _ddmrestDataProvider =
		new DDMRESTDataProvider();
	private final Http _http = Mockito.mock(Http.class);
	private SecretResolver _secretResolver;
	private Snapshot<SecretResolver> _secretResolverSnapshot;
	private final MockedStatic<SystemProperties> _systemPropertiesMockedStatic =
		Mockito.mockStatic(SystemProperties.class);

}