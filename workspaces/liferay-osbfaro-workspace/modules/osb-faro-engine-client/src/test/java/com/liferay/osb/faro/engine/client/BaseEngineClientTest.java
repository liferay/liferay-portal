/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client;

import com.liferay.osb.faro.engine.client.http.client.ClientHttpRequestFactoryUtil;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.util.FaroThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

/**
 * @author Leslie Wong
 */
public class BaseEngineClientTest {

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_baseEngineClient, "_httpComponentsClientHttpRequestFactory",
			_httpComponentsClientHttpRequestFactory);
	}

	@After
	public void tearDown() throws Exception {
		FaroThreadLocal.setOrigin(null);

		_httpComponentsClientHttpRequestFactory.destroy();
	}

	@Test
	public void testGetRestTemplateWithOrigin() {
		FaroThreadLocal.setOrigin(RandomTestUtil.randomString());

		Assert.assertSame(
			_httpComponentsClientHttpRequestFactory,
			_getClientHttpRequestFactory());
	}

	@Test
	public void testGetRestTemplateWithoutOrigin() {
		ClientHttpRequestFactory clientHttpRequestFactory =
			_getClientHttpRequestFactory();

		Assert.assertNotSame(
			_httpComponentsClientHttpRequestFactory, clientHttpRequestFactory);
		Assert.assertNotSame(
			clientHttpRequestFactory, _getClientHttpRequestFactory());
	}

	private ClientHttpRequestFactory _getClientHttpRequestFactory() {
		return ReflectionTestUtil.getFieldValue(
			_baseEngineClient.getRestTemplate(Mockito.mock(FaroProject.class)),
			"requestFactory");
	}

	private final BaseEngineClient _baseEngineClient = new BaseEngineClient() {
	};
	private final HttpComponentsClientHttpRequestFactory
		_httpComponentsClientHttpRequestFactory =
			ClientHttpRequestFactoryUtil.
				createHttpComponentsClientHttpRequestFactory(
					RandomTestUtil.randomInt(), RandomTestUtil.randomInt(),
					RandomTestUtil.randomInt());

}