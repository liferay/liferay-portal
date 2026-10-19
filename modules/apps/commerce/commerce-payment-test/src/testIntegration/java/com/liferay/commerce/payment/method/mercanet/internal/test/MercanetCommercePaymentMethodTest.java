/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.mercanet.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.engine.CommercePaymentEngine;
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
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class MercanetCommercePaymentMethodTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testProcessPayment() throws Exception {
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
				RandomTestUtil.randomLocaleStringMap(), true, null, "mercanet",
				99, null);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			user.getUserId(), commerceChannel.getGroupId(), commerceCurrency);

		commerceOrder.setCommercePaymentMethodKey("mercanet");

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest("GET", "");

		mockHttpServletRequest.setAttribute(WebKeys.LOCALE, LocaleUtil.US);
		mockHttpServletRequest.setAttribute(WebKeys.USER, user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_commercePaymentEngine.processPayment(
				commerceOrder.getCommerceOrderId(), null,
				mockHttpServletRequest);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				"Mercanet accepts only EUR currency", exception.getMessage());
		}

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		Assert.assertEquals(
			CommerceOrderPaymentConstants.STATUS_PENDING,
			commerceOrder.getPaymentStatus());
	}

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private CommercePaymentEngine _commercePaymentEngine;

	@Inject
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

}