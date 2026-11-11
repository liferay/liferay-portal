/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client;

import com.liferay.osb.faro.engine.client.http.client.ClientHttpRequestFactoryUtil;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.util.FaroRequestAudit;
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
import org.springframework.web.client.RestTemplate;

/**
 * @author Leslie Wong
 */
public class BaseEngineClientTest {

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_baseEngineClient,
			"_backgroundHttpComponentsClientHttpRequestFactory",
			_backgroundHttpComponentsClientHttpRequestFactory);
		ReflectionTestUtil.setFieldValue(
			_baseEngineClient, "_httpComponentsClientHttpRequestFactory",
			_httpComponentsClientHttpRequestFactory);
	}

	@After
	public void tearDown() throws Exception {
		FaroThreadLocal.setFaroRequestAudit(null);
		FaroThreadLocal.setOrigin(null);

		_backgroundHttpComponentsClientHttpRequestFactory.destroy();
		_httpComponentsClientHttpRequestFactory.destroy();
	}

	@Test
	public void testGetBackgroundRestTemplateWithFaroRequestAudit() {
		FaroThreadLocal.setFaroRequestAudit(new FaroRequestAudit());

		Assert.assertSame(
			_backgroundHttpComponentsClientHttpRequestFactory,
			_getClientHttpRequestFactory(
				_baseEngineClient.getBackgroundRestTemplate(
					Mockito.mock(FaroProject.class))));
	}

	@Test
	public void testGetRestTemplateWithFaroRequestAudit() {
		FaroThreadLocal.setFaroRequestAudit(new FaroRequestAudit());

		Assert.assertSame(
			_httpComponentsClientHttpRequestFactory,
			_getClientHttpRequestFactory(
				_baseEngineClient.getRestTemplate(
					Mockito.mock(FaroProject.class))));
	}

	@Test
	public void testGetRestTemplateWithOrigin() {
		FaroThreadLocal.setOrigin(RandomTestUtil.randomString());

		Assert.assertSame(
			_httpComponentsClientHttpRequestFactory,
			_getClientHttpRequestFactory(
				_baseEngineClient.getRestTemplate(
					Mockito.mock(FaroProject.class))));
	}

	@Test
	public void testGetRestTemplateWithoutOriginOrFaroRequestAudit() {
		Assert.assertSame(
			_backgroundHttpComponentsClientHttpRequestFactory,
			_getClientHttpRequestFactory(
				_baseEngineClient.getRestTemplate(
					Mockito.mock(FaroProject.class))));
	}

	private ClientHttpRequestFactory _getClientHttpRequestFactory(
		RestTemplate restTemplate) {

		return ReflectionTestUtil.getFieldValue(restTemplate, "requestFactory");
	}

	private final HttpComponentsClientHttpRequestFactory
		_backgroundHttpComponentsClientHttpRequestFactory =
			ClientHttpRequestFactoryUtil.
				createHttpComponentsClientHttpRequestFactory(
					RandomTestUtil.randomInt(), RandomTestUtil.randomInt(),
					RandomTestUtil.randomInt());
	private final BaseEngineClient _baseEngineClient = new BaseEngineClient() {
	};
	private final HttpComponentsClientHttpRequestFactory
		_httpComponentsClientHttpRequestFactory =
			ClientHttpRequestFactoryUtil.
				createHttpComponentsClientHttpRequestFactory(
					RandomTestUtil.randomInt(), RandomTestUtil.randomInt(),
					RandomTestUtil.randomInt());

}