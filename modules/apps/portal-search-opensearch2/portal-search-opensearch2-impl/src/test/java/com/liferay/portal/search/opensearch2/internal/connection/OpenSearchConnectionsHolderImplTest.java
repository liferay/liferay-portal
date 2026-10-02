/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.connection;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.search.opensearch2.configuration.OpenSearchConnectionConfiguration;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Olivia Yu
 */
public class OpenSearchConnectionsHolderImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_configurableUtilMockedStatic = Mockito.mockStatic(
			ConfigurableUtil.class);

		_openSearchConnectionsHolderImpl.http = _http;

		ReflectionTestUtil.setFieldValue(
			_openSearchConnectionsHolderImpl, "_secretResolver",
			_secretResolver);

		_configurableUtilMockedStatic.when(
			() -> ConfigurableUtil.createConfigurable(
				Mockito.eq(OpenSearchConnectionConfiguration.class),
				Mockito.anyMap())
		).thenReturn(
			_openSearchConnectionConfiguration
		);
	}

	@After
	public void tearDown() {
		_configurableUtilMockedStatic.close();
	}

	@Test
	public void testActivate() {
		Mockito.when(
			_openSearchConnectionConfiguration.active()
		).thenReturn(
			false
		);

		boolean authenticationEnabled = RandomTestUtil.randomBoolean();

		Mockito.when(
			_openSearchConnectionConfiguration.authenticationEnabled()
		).thenReturn(
			authenticationEnabled
		);

		boolean compressionEnabled = RandomTestUtil.randomBoolean();

		Mockito.when(
			_openSearchConnectionConfiguration.compressionEnabled()
		).thenReturn(
			compressionEnabled
		);

		String connectionId = RandomTestUtil.randomString();

		Mockito.when(
			_openSearchConnectionConfiguration.connectionId()
		).thenReturn(
			connectionId
		);

		boolean httpSSLEnabled = RandomTestUtil.randomBoolean();

		Mockito.when(
			_openSearchConnectionConfiguration.httpSSLEnabled()
		).thenReturn(
			httpSSLEnabled
		);

		int maxConnections = RandomTestUtil.randomInt();

		Mockito.when(
			_openSearchConnectionConfiguration.maxConnections()
		).thenReturn(
			maxConnections
		);

		int maxConnectionsPerRoute = RandomTestUtil.randomInt();

		Mockito.when(
			_openSearchConnectionConfiguration.maxConnectionsPerRoute()
		).thenReturn(
			maxConnectionsPerRoute
		);

		String[] networkHostAddresses = RandomTestUtil.randomStrings(10);

		Mockito.when(
			_openSearchConnectionConfiguration.networkHostAddresses()
		).thenReturn(
			networkHostAddresses
		);

		String password = RandomTestUtil.randomString();

		Mockito.when(
			_openSearchConnectionConfiguration.password()
		).thenReturn(
			password
		);

		String proxyHost = RandomTestUtil.randomString();

		Mockito.when(
			_openSearchConnectionConfiguration.proxyHost()
		).thenReturn(
			proxyHost
		);

		String proxyPassword = RandomTestUtil.randomString();

		Mockito.when(
			_openSearchConnectionConfiguration.proxyPassword()
		).thenReturn(
			proxyPassword
		);

		int proxyPort = RandomTestUtil.randomInt();

		Mockito.when(
			_openSearchConnectionConfiguration.proxyPort()
		).thenReturn(
			proxyPort
		);

		String proxyUserName = RandomTestUtil.randomString();

		Mockito.when(
			_openSearchConnectionConfiguration.proxyUserName()
		).thenReturn(
			proxyUserName
		);

		String truststorePassword = RandomTestUtil.randomString();

		Mockito.when(
			_openSearchConnectionConfiguration.truststorePassword()
		).thenReturn(
			truststorePassword
		);

		String truststorePath = RandomTestUtil.randomString();

		Mockito.when(
			_openSearchConnectionConfiguration.truststorePath()
		).thenReturn(
			truststorePath
		);

		String truststoreType = RandomTestUtil.randomString();

		Mockito.when(
			_openSearchConnectionConfiguration.truststoreType()
		).thenReturn(
			truststoreType
		);

		String userName = RandomTestUtil.randomString();

		Mockito.when(
			_openSearchConnectionConfiguration.username()
		).thenReturn(
			userName
		);

		String resolvedPassword = _mockResolve(password);
		String resolvedProxyPassword = _mockResolve(proxyPassword);
		String resolvedTruststorePassword = _mockResolve(truststorePassword);

		_openSearchConnectionsHolderImpl.activate(new HashMap<>());

		OpenSearchConnection openSearchConnection =
			_openSearchConnectionsHolderImpl.getOpenSearchConnection(
				connectionId);

		Assert.assertFalse(openSearchConnection.isActive());
		Assert.assertEquals(
			Boolean.valueOf(authenticationEnabled),
			ReflectionTestUtil.getFieldValue(
				openSearchConnection, "_authenticationEnabled"));
		Assert.assertEquals(
			Boolean.valueOf(compressionEnabled),
			ReflectionTestUtil.getFieldValue(
				openSearchConnection, "_compressionEnabled"));
		Assert.assertEquals(
			connectionId, openSearchConnection.getConnectionId());
		Assert.assertEquals(
			Boolean.valueOf(httpSSLEnabled),
			ReflectionTestUtil.getFieldValue(
				openSearchConnection, "_httpSSLEnabled"));
		Assert.assertEquals(
			Integer.valueOf(maxConnections),
			ReflectionTestUtil.getFieldValue(
				openSearchConnection, "_maxConnections"));
		Assert.assertEquals(
			Integer.valueOf(maxConnectionsPerRoute),
			ReflectionTestUtil.getFieldValue(
				openSearchConnection, "_maxConnectionsPerRoute"));
		Assert.assertArrayEquals(
			networkHostAddresses,
			ReflectionTestUtil.getFieldValue(
				openSearchConnection, "_networkHostAddresses"));
		Assert.assertEquals(
			resolvedPassword,
			ReflectionTestUtil.getFieldValue(
				openSearchConnection, "_password"));
		Assert.assertEquals(
			resolvedTruststorePassword,
			ReflectionTestUtil.getFieldValue(
				openSearchConnection, "_truststorePassword"));
		Assert.assertEquals(
			truststorePath,
			ReflectionTestUtil.getFieldValue(
				openSearchConnection, "_truststorePath"));
		Assert.assertEquals(
			truststoreType,
			ReflectionTestUtil.getFieldValue(
				openSearchConnection, "_truststoreType"));
		Assert.assertEquals(
			userName,
			ReflectionTestUtil.getFieldValue(
				openSearchConnection, "_userName"));

		ProxyConfig proxyConfig = ReflectionTestUtil.getFieldValue(
			openSearchConnection, "_proxyConfig");

		Assert.assertEquals(proxyHost, proxyConfig.getHost());
		Assert.assertEquals(resolvedProxyPassword, proxyConfig.getPassword());
		Assert.assertEquals(proxyPort, proxyConfig.getPort());
		Assert.assertTrue(proxyConfig.shouldApplyCredentials());
		Assert.assertEquals(proxyUserName, proxyConfig.getUserName());
	}

	private String _mockResolve(String value) {
		String resolvedValue = RandomTestUtil.randomString();

		Mockito.when(
			_secretResolver.resolve(CompanyConstants.SYSTEM, value)
		).thenReturn(
			resolvedValue
		);

		return resolvedValue;
	}

	private MockedStatic<ConfigurableUtil> _configurableUtilMockedStatic;
	private final Http _http = Mockito.mock(Http.class);
	private final OpenSearchConnectionConfiguration
		_openSearchConnectionConfiguration = Mockito.mock(
			OpenSearchConnectionConfiguration.class);
	private final OpenSearchConnectionsHolderImpl
		_openSearchConnectionsHolderImpl =
			new OpenSearchConnectionsHolderImpl();
	private final SecretResolver _secretResolver = Mockito.mock(
		SecretResolver.class);

}