/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.paypal.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommercePaymentEntryConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.constants.CommercePaymentIntegrationConstants;
import com.liferay.commerce.payment.integration.CommercePaymentIntegration;
import com.liferay.commerce.payment.integration.CommercePaymentIntegrationRegistry;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.service.CommercePaymentEntryLocalService;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.Servlet;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Tancredi Covioli
 */
@RunWith(Arquillian.class)
public class PayPalCommercePaymentIntegrationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testIsValid() throws Exception {
		Group group = GroupTestUtil.addGroup();

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(group.getCompanyId());

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			group.getGroupId(), commerceCurrency.getCode());

		User user = UserTestUtil.addUser();

		_commercePaymentMethodGroupRelLocalService.
			addCommercePaymentMethodGroupRel(
				user.getUserId(), commerceChannel.getGroupId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), true, null,
				"paypal-integration", RandomTestUtil.randomDouble(), null);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			user.getUserId(), commerceChannel.getGroupId(), commerceCurrency);

		commerceOrder.setCommercePaymentMethodKey("paypal-integration");

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentEntryLocalService.addCommercePaymentEntry(
				user.getUserId(),
				PortalUtil.getClassNameId(CommerceOrder.class),
				commerceOrder.getCommerceOrderId(),
				commerceChannel.getCommerceChannelId(), BigDecimal.TEN, null,
				null, commerceCurrency.getCode(),
				LocaleUtil.toLanguageId(LocaleUtil.US), null, null,
				"paypal-integration",
				CommercePaymentIntegrationConstants.
					TYPE_INTERNAL_ONLINE_REDIRECT,
				null, RandomTestUtil.randomString(),
				CommercePaymentEntryConstants.TYPE_PAYMENT,
				ServiceContextTestUtil.getServiceContext(
					group.getCompanyId(), group.getGroupId(),
					user.getUserId()));

		commercePaymentEntry.setPaymentStatus(
			CommercePaymentEntryConstants.STATUS_CREATED);

		commercePaymentEntry =
			_commercePaymentEntryLocalService.updateCommercePaymentEntry(
				commercePaymentEntry);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.commerce.payment.internal.servlet." +
					"CommercePaymentServlet",
				LoggerTestUtil.OFF)) {

			_service(commercePaymentEntry, RandomTestUtil.randomString(), user);
		}

		commercePaymentEntry =
			_commercePaymentEntryLocalService.getCommercePaymentEntry(
				commercePaymentEntry.getCommercePaymentEntryId());

		Assert.assertEquals(
			CommercePaymentEntryConstants.STATUS_CREATED,
			commercePaymentEntry.getPaymentStatus());

		int paymentStatus = commerceOrder.getPaymentStatus();

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		Assert.assertEquals(paymentStatus, commerceOrder.getPaymentStatus());

		_service(
			commercePaymentEntry, commercePaymentEntry.getTransactionCode(),
			user);

		commercePaymentEntry =
			_commercePaymentEntryLocalService.getCommercePaymentEntry(
				commercePaymentEntry.getCommercePaymentEntryId());

		Assert.assertEquals(
			CommercePaymentEntryConstants.STATUS_CANCELLED,
			commercePaymentEntry.getPaymentStatus());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter(
			"token", RandomTestUtil.randomString());

		CommercePaymentIntegration commercePaymentIntegration =
			_commercePaymentIntegrationRegistry.getCommercePaymentIntegration(
				"paypal-integration");

		Assert.assertTrue(
			commercePaymentIntegration.isValid(
				mockHttpServletRequest, commercePaymentEntry));
	}

	private void _service(
			CommercePaymentEntry commercePaymentEntry, String token, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest("GET", "/o/commerce-payment");

		mockHttpServletRequest.addParameter("cancel", "true");
		mockHttpServletRequest.addParameter(
			"entryId",
			String.valueOf(commercePaymentEntry.getCommercePaymentEntryId()));
		mockHttpServletRequest.addParameter("redirect", "false");
		mockHttpServletRequest.addParameter("token", token);
		mockHttpServletRequest.setAttribute(WebKeys.LOCALE, LocaleUtil.US);
		mockHttpServletRequest.setAttribute(WebKeys.USER, user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_servlet.service(
				mockHttpServletRequest, new MockHttpServletResponse());
		}
	}

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private CommercePaymentEntryLocalService _commercePaymentEntryLocalService;

	@Inject
	private CommercePaymentIntegrationRegistry
		_commercePaymentIntegrationRegistry;

	@Inject
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Inject(
		filter = "osgi.http.whiteboard.servlet.name=com.liferay.commerce.payment.internal.servlet.CommercePaymentServlet"
	)
	private Servlet _servlet;

}