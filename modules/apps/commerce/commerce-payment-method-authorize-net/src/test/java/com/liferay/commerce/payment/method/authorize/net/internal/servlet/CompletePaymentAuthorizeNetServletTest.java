/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.authorize.net.internal.servlet;

import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.engine.CommercePaymentEngine;
import com.liferay.commerce.payment.helper.CommercePaymentHttpHelper;
import com.liferay.commerce.payment.method.authorize.net.internal.AuthorizeNetCommercePaymentMethod;
import com.liferay.commerce.payment.method.authorize.net.internal.configuration.AuthorizeNetGroupServiceConfiguration;
import com.liferay.commerce.payment.method.authorize.net.internal.constants.AuthorizeNetCommercePaymentMethodConstants;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Danny Situ
 */
public class CompletePaymentAuthorizeNetServletTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_bytes = RandomTestUtil.randomBytes();
		_signatureKey = RandomTestUtil.randomString();

		ReflectionTestUtil.setFieldValue(
			_completePaymentAuthorizeNetServlet, "_commerceOrderLocalService",
			_commerceOrderLocalService);
		ReflectionTestUtil.setFieldValue(
			_completePaymentAuthorizeNetServlet, "_commercePaymentEngine",
			_commercePaymentEngine);
		ReflectionTestUtil.setFieldValue(
			_completePaymentAuthorizeNetServlet, "_commercePaymentHttpHelper",
			_commercePaymentHttpHelper);
		ReflectionTestUtil.setFieldValue(
			_completePaymentAuthorizeNetServlet, "_configurationProvider",
			_configurationProvider);
		ReflectionTestUtil.setFieldValue(
			_completePaymentAuthorizeNetServlet, "_jsonFactory", _jsonFactory);
		ReflectionTestUtil.setFieldValue(
			_completePaymentAuthorizeNetServlet, "_portal", _portal);
	}

	@Test
	public void testDoGet() throws Exception {
		_testDoGet();
		_testDoGetWithCancel();
	}

	@Test
	public void testDoPost() throws Exception {
		_testDoPost();
		_testDoPostWithCompletedCommerceOrder();
		_testDoPostWithInvalidJSON();
		_testDoPostWithInvalidSignature();
		_testDoPostWithUnsupportedEventType();
	}

	@Test
	public void testIsValidSignature() throws Exception {
		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				_completePaymentAuthorizeNetServlet, "_isValidSignature",
				new Class<?>[] {byte[].class, String.class, String.class},
				_bytes, "sha512=" + RandomTestUtil.randomString(), null));
		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				_completePaymentAuthorizeNetServlet, "_isValidSignature",
				new Class<?>[] {byte[].class, String.class, String.class},
				_bytes, "sha512=" + RandomTestUtil.randomString(),
				_signatureKey));

		String signature = ReflectionTestUtil.invoke(
			_completePaymentAuthorizeNetServlet, "_generateHMAC",
			new Class<?>[] {byte[].class, String.class}, _bytes, _signatureKey);

		Assert.assertTrue(
			ReflectionTestUtil.invoke(
				_completePaymentAuthorizeNetServlet, "_isValidSignature",
				new Class<?>[] {byte[].class, String.class, String.class},
				_bytes, "sha512=" + StringUtil.toUpperCase(signature),
				_signatureKey));
	}

	private MockHttpServletResponse _get(boolean cancel, String redirect)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		Mockito.when(
			_portal.getPortalURL(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			"http://localhost"
		);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("redirect", redirect);

		if (cancel) {
			mockHttpServletRequest.setParameter("cancel", "true");
		}

		_completePaymentAuthorizeNetServlet.doGet(
			mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	private JSONObject _getAuthCaptureJSONObject(
		long commerceOrderId, String transactionId) {

		return JSONUtil.put(
			"eventType",
			AuthorizeNetCommercePaymentMethodConstants.
				AUTH_CAPTURE_CREATED_EVENT_TYPE
		).put(
			"payload",
			JSONUtil.put(
				"id", transactionId
			).put(
				"invoiceNumber", String.valueOf(commerceOrderId)
			)
		);
	}

	private CommerceOrder _mockCommerceOrder(
		long commerceOrderId, int paymentStatus) {

		CommerceOrder commerceOrder = Mockito.mock(CommerceOrder.class);

		Mockito.when(
			commerceOrder.getCommerceOrderId()
		).thenReturn(
			commerceOrderId
		);

		Mockito.when(
			commerceOrder.getCommercePaymentMethodKey()
		).thenReturn(
			AuthorizeNetCommercePaymentMethod.KEY
		);

		Mockito.when(
			commerceOrder.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			commerceOrder.getPaymentStatus()
		).thenReturn(
			paymentStatus
		);

		return commerceOrder;
	}

	private void _mockConfigurationProvider(String signatureKey)
		throws Exception {

		AuthorizeNetGroupServiceConfiguration
			authorizeNetGroupServiceConfiguration = Mockito.mock(
				AuthorizeNetGroupServiceConfiguration.class);

		Mockito.when(
			authorizeNetGroupServiceConfiguration.signatureKey()
		).thenReturn(
			signatureKey
		);

		Mockito.when(
			_configurationProvider.getConfiguration(
				Mockito.eq(AuthorizeNetGroupServiceConfiguration.class),
				Mockito.any())
		).thenReturn(
			authorizeNetGroupServiceConfiguration
		);
	}

	private MockHttpServletResponse _post(
			JSONObject jsonObject, String signatureKey)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		String jsonString = jsonObject.toString();

		byte[] bytes = jsonString.getBytes(StandardCharsets.UTF_8);

		mockHttpServletRequest.setContent(bytes);

		String signature = ReflectionTestUtil.invoke(
			_completePaymentAuthorizeNetServlet, "_generateHMAC",
			new Class<?>[] {byte[].class, String.class}, bytes, signatureKey);

		mockHttpServletRequest.addHeader(
			"X-ANET-Signature", "sha512=" + signature);

		_completePaymentAuthorizeNetServlet.doPost(
			mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	private void _testDoGet() throws Exception {
		long commerceOrderId = RandomTestUtil.randomLong();

		CommerceOrder commerceOrder = _mockCommerceOrder(
			commerceOrderId, CommerceOrderPaymentConstants.STATUS_PENDING);

		Mockito.when(
			_commercePaymentHttpHelper.getCommerceOrder(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			commerceOrder
		);

		MockHttpServletResponse mockHttpServletResponse = _get(
			false, "http://localhost/web/guest");

		Assert.assertEquals(
			"http://localhost/web/guest",
			mockHttpServletResponse.getRedirectedUrl());

		Mockito.verify(
			_commercePaymentEngine
		).completePayment(
			Mockito.eq(commerceOrderId), Mockito.isNull(),
			Mockito.any(HttpServletRequest.class)
		);
	}

	private void _testDoGetWithCancel() throws Exception {
		long commerceOrderId = RandomTestUtil.randomLong();

		CommerceOrder commerceOrder = _mockCommerceOrder(
			commerceOrderId, CommerceOrderPaymentConstants.STATUS_PENDING);

		Mockito.when(
			_commercePaymentHttpHelper.getCommerceOrder(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			commerceOrder
		);

		_get(true, "http://localhost/web/guest");

		Mockito.verify(
			_commercePaymentEngine
		).cancelPayment(
			Mockito.eq(commerceOrderId), Mockito.isNull(),
			Mockito.any(HttpServletRequest.class)
		);
	}

	private void _testDoPost() throws Exception {
		long commerceOrderId = RandomTestUtil.randomLong();

		CommerceOrder commerceOrder = _mockCommerceOrder(
			commerceOrderId, CommerceOrderPaymentConstants.STATUS_PENDING);

		Mockito.when(
			_commerceOrderLocalService.fetchCommerceOrder(commerceOrderId)
		).thenReturn(
			commerceOrder
		);

		_mockConfigurationProvider(_signatureKey);

		String transactionId = RandomTestUtil.randomString();

		MockHttpServletResponse mockHttpServletResponse = _post(
			_getAuthCaptureJSONObject(commerceOrderId, transactionId),
			_signatureKey);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		Mockito.verify(
			_commercePaymentEngine
		).completePayment(
			Mockito.eq(commerceOrderId), Mockito.eq(transactionId),
			Mockito.any(HttpServletRequest.class)
		);
	}

	private void _testDoPostWithCompletedCommerceOrder() throws Exception {
		long commerceOrderId = RandomTestUtil.randomLong();

		CommerceOrder commerceOrder = _mockCommerceOrder(
			commerceOrderId, CommerceOrderPaymentConstants.STATUS_COMPLETED);

		Mockito.when(
			_commerceOrderLocalService.fetchCommerceOrder(commerceOrderId)
		).thenReturn(
			commerceOrder
		);

		_mockConfigurationProvider(_signatureKey);

		String transactionId = RandomTestUtil.randomString();

		MockHttpServletResponse mockHttpServletResponse = _post(
			_getAuthCaptureJSONObject(commerceOrderId, transactionId),
			_signatureKey);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		Mockito.verify(
			commerceOrder
		).setTransactionId(
			transactionId
		);

		Mockito.verify(
			_commerceOrderLocalService
		).updateCommerceOrder(
			commerceOrder
		);
	}

	private void _testDoPostWithInvalidJSON() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setContent(RandomTestUtil.randomBytes());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_completePaymentAuthorizeNetServlet.doPost(
			mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			HttpServletResponse.SC_BAD_REQUEST,
			mockHttpServletResponse.getStatus());
	}

	private void _testDoPostWithInvalidSignature() throws Exception {
		long commerceOrderId = RandomTestUtil.randomLong();

		CommerceOrder commerceOrder = _mockCommerceOrder(
			commerceOrderId, CommerceOrderPaymentConstants.STATUS_PENDING);

		Mockito.when(
			_commerceOrderLocalService.fetchCommerceOrder(commerceOrderId)
		).thenReturn(
			commerceOrder
		);

		_mockConfigurationProvider(_signatureKey);

		MockHttpServletResponse mockHttpServletResponse = _post(
			_getAuthCaptureJSONObject(
				commerceOrderId, RandomTestUtil.randomString()),
			RandomTestUtil.randomString());

		Assert.assertEquals(
			HttpServletResponse.SC_UNAUTHORIZED,
			mockHttpServletResponse.getStatus());
	}

	private void _testDoPostWithUnsupportedEventType() throws Exception {
		MockHttpServletResponse mockHttpServletResponse = _post(
			JSONUtil.put("eventType", RandomTestUtil.randomString()),
			_signatureKey);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());
	}

	private byte[] _bytes;
	private final CommerceOrderLocalService _commerceOrderLocalService =
		Mockito.mock(CommerceOrderLocalService.class);
	private final CommercePaymentEngine _commercePaymentEngine = Mockito.mock(
		CommercePaymentEngine.class);
	private final CommercePaymentHttpHelper _commercePaymentHttpHelper =
		Mockito.mock(CommercePaymentHttpHelper.class);
	private final CompletePaymentAuthorizeNetServlet
		_completePaymentAuthorizeNetServlet =
			new CompletePaymentAuthorizeNetServlet();
	private final ConfigurationProvider _configurationProvider = Mockito.mock(
		ConfigurationProvider.class);
	private final JSONFactory _jsonFactory = new JSONFactoryImpl();
	private final Portal _portal = Mockito.mock(Portal.class);
	private String _signatureKey;

}