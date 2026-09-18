/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.s3;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.store.s3.configuration.S3StoreConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import io.netty.handler.codec.http.HttpRequest;

import jakarta.annotation.Generated;

import java.net.InetSocketAddress;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.ProxyAuthenticator;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Kevin Lee
 */
@Generated("")
public class IBMS3StoreUnitTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_s3StoreConfiguration = Mockito.mock(S3StoreConfiguration.class);

		Mockito.when(
			_s3StoreConfiguration.accessKey()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_s3StoreConfiguration.bucketName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_s3StoreConfiguration.connectionTimeout()
		).thenReturn(
			1000
		);

		Mockito.when(
			_s3StoreConfiguration.corePoolSize()
		).thenReturn(
			1
		);

		Mockito.when(
			_s3StoreConfiguration.httpClientMaxConnections()
		).thenReturn(
			1
		);

		Mockito.when(
			_s3StoreConfiguration.maxPoolSize()
		).thenReturn(
			1
		);

		Mockito.when(
			_s3StoreConfiguration.s3Region()
		).thenReturn(
			"us-east-1"
		);

		Mockito.when(
			_s3StoreConfiguration.s3StorageClass()
		).thenReturn(
			"STANDARD"
		);

		Mockito.when(
			_s3StoreConfiguration.secretKey()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		_configurableUtilMockedStatic.when(
			() -> ConfigurableUtil.createConfigurable(
				Mockito.eq(S3StoreConfiguration.class), Mockito.anyMap())
		).thenReturn(
			_s3StoreConfiguration
		);
	}

	@After
	public void tearDown() {
		_configurableUtilMockedStatic.close();
	}

	@Test
	public void testHasFile() throws Exception {
		_setUpHasFile(null, null);

		_testHasFile(true, null, null);

		String proxyUserName = RandomTestUtil.randomString();
		String proxyPassword = RandomTestUtil.randomString();

		_setUpHasFile(proxyUserName, proxyPassword);

		_testHasFile(false, proxyUserName, proxyPassword + "1");

		proxyUserName = RandomTestUtil.randomString();
		proxyPassword = RandomTestUtil.randomString();

		_setUpHasFile(proxyUserName, proxyPassword);

		_testHasFile(true, proxyUserName, proxyPassword);
	}

	private void _setUpHasFile(String proxyUserName, String proxyPassword) {
		Mockito.when(
			_s3StoreConfiguration.proxyHost()
		).thenReturn(
			_inetSocketAddress.getHostString()
		);

		Mockito.when(
			_s3StoreConfiguration.proxyPort()
		).thenReturn(
			_inetSocketAddress.getPort()
		);

		if (Validator.isNotNull(proxyUserName)) {
			Mockito.when(
				_s3StoreConfiguration.proxyAuthType()
			).thenReturn(
				"username-password"
			);

			Mockito.when(
				_s3StoreConfiguration.proxyPassword()
			).thenReturn(
				proxyPassword
			);

			Mockito.when(
				_s3StoreConfiguration.proxyUsername()
			).thenReturn(
				proxyUserName
			);
		}
		else {
			Mockito.when(
				_s3StoreConfiguration.proxyAuthType()
			).thenReturn(
				"none"
			);
		}
	}

	private void _testHasFile(
			boolean expectedProxy, String proxyUserName, String proxyPassword)
		throws Exception {

		AtomicBoolean proxy = new AtomicBoolean(false);

		HttpProxyServerBootstrap httpProxyServerBootstrap =
			DefaultHttpProxyServer.bootstrap();

		httpProxyServerBootstrap.withAddress(_inetSocketAddress);
		httpProxyServerBootstrap.withFiltersSource(
			new HttpFiltersSourceAdapter() {

				@Override
				public HttpFilters filterRequest(HttpRequest httpRequest) {
					proxy.set(true);

					return super.filterRequest(httpRequest);
				}

			});

		if (Validator.isNotNull(proxyUserName)) {
			httpProxyServerBootstrap.withProxyAuthenticator(
				new ProxyAuthenticator() {

					@Override
					public boolean authenticate(
						String userName, String password) {

						if (Objects.equals(userName, proxyUserName) &&
							Objects.equals(password, proxyPassword)) {

							return true;
						}

						return false;
					}

					@Override
					public String getRealm() {
						return null;
					}

				});
		}

		HttpProxyServer httpProxyServer = httpProxyServerBootstrap.start();

		try {
			IBMS3Store ibmS3Store = new IBMS3Store();

			ReflectionTestUtil.setFieldValue(
				ibmS3Store, "_secretResolver",
				(SecretResolver)(companyId, value) -> value);

			ibmS3Store.activate(Collections.emptyMap());

			try {
				ibmS3Store.hasFile(
					RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
					RandomTestUtil.randomString(), Store.VERSION_DEFAULT);

				Assert.fail("The store should fail to connect");
			}
			catch (SystemException systemException) {
				String message = systemException.getMessage();

				Assert.assertTrue(
					message.contains("Could not connect to proxy") ||
					message.contains("Status Code: 403") ||
					message.contains("Status Code: 407"));

				Assert.assertEquals(expectedProxy, proxy.get());
			}
			finally {
				ibmS3Store.deactivate();
			}
		}
		finally {
			httpProxyServer.stop();
		}
	}

	private final MockedStatic<ConfigurableUtil> _configurableUtilMockedStatic =
		Mockito.mockStatic(ConfigurableUtil.class);
	private final InetSocketAddress _inetSocketAddress = new InetSocketAddress(
		"localhost", 4250);
	private S3StoreConfiguration _s3StoreConfiguration;

}