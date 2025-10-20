/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.notification.term.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.notification.context.NotificationContextBuilder;
import com.liferay.notification.term.evaluator.NotificationTermEvaluatorTracker;
import com.liferay.notification.type.util.NotificationTypeUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Matyas Wollner
 */
@RunWith(Arquillian.class)
public class CommerceOrderAccountNotificationTermTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(
				TestPropsValues.getCompanyId());

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			TestPropsValues.getGroupId(), commerceCurrency.getCode());

		_commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			TestPropsValues.getUserId(), commerceChannel.getGroupId(),
			commerceCurrency);

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);
	}

	@Test
	public void testEvaluateTerms() throws Exception {
		String content = "[%COMMERCEORDER_ACCOUNT_NAME%]";

		Map<String, Object> termValues = HashMapBuilder.<String, Object>put(
			"externalReferenceCode", _commerceOrder.getExternalReferenceCode()
		).put(
			"groupId", _commerceOrder.getGroupId()
		).put(
			"id", _commerceOrder.getCommerceOrderId()
		).build();

		content = NotificationTypeUtil.evaluateTerms(
			content,
			new NotificationContextBuilder(
			).className(
				CommerceOrder.class.getName()
			).classPK(
				GetterUtil.getLong(termValues.get("id"))
			).externalReferenceCode(
				GetterUtil.getString(termValues.get("externalReferenceCode"))
			).groupId(
				GetterUtil.getLong(termValues.get("groupId"))
			).termValues(
				termValues
			).build(),
			_notificationTermEvaluatorTracker);

		Assert.assertEquals(_commerceOrder.getCommerceAccountName(), content);
	}

	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private NotificationTermEvaluatorTracker _notificationTermEvaluatorTracker;

}