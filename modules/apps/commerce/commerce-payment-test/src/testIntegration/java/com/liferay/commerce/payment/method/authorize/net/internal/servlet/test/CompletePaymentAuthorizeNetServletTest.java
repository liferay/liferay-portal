/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.authorize.net.internal.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.util.CommerceGroupThreadLocal;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PortalImpl;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Luca Pellizzon
 */
@RunWith(Arquillian.class)
public class CompletePaymentAuthorizeNetServletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_originalCommerceGroup = CommerceGroupThreadLocal.get();

		_group = GroupTestUtil.addGroup();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_user = UserTestUtil.addUser();

		_commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		_commerceOrder.setCommercePaymentMethodKey("authorize-net");

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);
	}

	@After
	public void tearDown() throws Exception {
		CommerceGroupThreadLocal.set(_originalCommerceGroup);

		_commerceOrderLocalService.deleteCommerceOrder(
			_commerceOrder.getCommerceOrderId());
	}

	@Test
	public void testCrossOriginRedirect() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				PortalImpl.class.getName(), LoggerTestUtil.OFF)) {

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest("GET", "");

			User user = UserTestUtil.addUser();

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			PrincipalThreadLocal.setName(user.getUserId());

			String redirect = "https://www.google.com";

			mockHttpServletRequest.addParameter("redirect", redirect);

			mockHttpServletRequest.setAttribute(WebKeys.USER, user);

			MockHttpServletResponse mockHttpServletResponse =
				new MockHttpServletResponse();

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			Assert.assertNotEquals(
				redirect, mockHttpServletResponse.getRedirectedUrl());
		}
	}

	@Test
	public void testDoGet() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		PrincipalThreadLocal.setName(_user.getUserId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest("GET", "");

		mockHttpServletRequest.addParameter(
			"groupId", String.valueOf(_commerceOrder.getGroupId()));

		String redirect =
			_portal.getPortalURL(mockHttpServletRequest) + "/web/guest";

		mockHttpServletRequest.addParameter("redirect", redirect);

		mockHttpServletRequest.addParameter("uuid", _commerceOrder.getUuid());
		mockHttpServletRequest.setAttribute(WebKeys.USER, _user);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.commerce.payment.method.authorize.net.internal." +
					"servlet.CompletePaymentAuthorizeNetServlet",
				LoggerTestUtil.OFF)) {

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);
		}

		Assert.assertEquals(
			redirect, mockHttpServletResponse.getRedirectedUrl());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(
				_commerceOrder.getCommerceOrderId());

		Assert.assertEquals(
			_commerceOrder.getOrderStatus(), commerceOrder.getOrderStatus());
		Assert.assertEquals(
			_commerceOrder.getPaymentStatus(),
			commerceOrder.getPaymentStatus());
	}

	@Test
	public void testDoPost() throws Exception {
		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				_commerceChannel.getGroupId(),
				"com.liferay.commerce.payment.method.authorize.net"));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		String signatureKey = RandomTestUtil.randomString();

		modifiableSettings.setValue("signatureKey", signatureKey);

		modifiableSettings.store();

		String transactionId = RandomTestUtil.randomString();

		String json = JSONUtil.put(
			"eventType", "net.authorize.payment.authcapture.created"
		).put(
			"payload",
			JSONUtil.put(
				"id", transactionId
			).put(
				"invoiceNumber",
				String.valueOf(_commerceOrder.getCommerceOrderId())
			)
		).toString();

		byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.commerce.payment.method.authorize.net.internal." +
					"servlet.CompletePaymentAuthorizeNetServlet",
				LoggerTestUtil.OFF)) {

			MockHttpServletResponse mockHttpServletResponse = _post(
				bytes, RandomTestUtil.randomString());

			Assert.assertEquals(
				HttpServletResponse.SC_UNAUTHORIZED,
				mockHttpServletResponse.getStatus());
		}

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(
				_commerceOrder.getCommerceOrderId());

		Assert.assertEquals(
			_commerceOrder.getPaymentStatus(),
			commerceOrder.getPaymentStatus());

		MockHttpServletResponse mockHttpServletResponse = _post(
			bytes, signatureKey);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			_commerceOrder.getCommerceOrderId());

		Assert.assertEquals(
			CommerceOrderConstants.ORDER_STATUS_PENDING,
			commerceOrder.getOrderStatus());
		Assert.assertEquals(
			CommerceOrderPaymentConstants.STATUS_COMPLETED,
			commerceOrder.getPaymentStatus());
		Assert.assertEquals(transactionId, commerceOrder.getTransactionId());
	}

	private MockHttpServletResponse _post(byte[] bytes, String signatureKey)
		throws Exception {

		Mac mac = Mac.getInstance("HmacSHA512");

		mac.init(
			new SecretKeySpec(
				signatureKey.getBytes(StandardCharsets.UTF_8),
				mac.getAlgorithm()));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest("POST", "");

		mockHttpServletRequest.addHeader(
			"X-ANET-Signature",
			"sha512=" + StringUtil.bytesToHexString(mac.doFinal(bytes)));
		mockHttpServletRequest.setContent(bytes);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private Group _group;
	private Group _originalCommerceGroup;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "osgi.http.whiteboard.servlet.name=com.liferay.commerce.payment.method.authorize.net.internal.servlet.CompletePaymentAuthorizeNetServlet"
	)
	private Servlet _servlet;

	private User _user;

}