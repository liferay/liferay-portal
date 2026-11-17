/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.exception.DuplicateCommerceDiscountException;
import com.liferay.commerce.discount.service.CommerceDiscountLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian I. Kim
 */
@RunWith(Arquillian.class)
public class CommerceDiscountLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testAddCommerceDiscount() throws Exception {
		_addCommerceDiscount(null);
		_addCommerceDiscount(null);

		Assert.assertEquals(
			2, _commerceDiscountLocalService.getCommerceDiscountsCount());

		String couponCode = RandomTestUtil.randomString();

		_addCommerceDiscount(couponCode);

		AssertUtils.assertFailure(
			DuplicateCommerceDiscountException.class, null,
			() -> _addCommerceDiscount(couponCode));

		Assert.assertEquals(
			3, _commerceDiscountLocalService.getCommerceDiscountsCount());
	}

	private void _addCommerceDiscount(String couponCode) throws Exception {
		_commerceDiscountLocalService.addCommerceDiscount(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			CommerceDiscountConstants.TARGET_PRODUCTS,
			Validator.isNotNull(couponCode), couponCode, true, BigDecimal.ONE,
			CommerceDiscountConstants.LEVEL_L1, BigDecimal.ONE, BigDecimal.ZERO,
			BigDecimal.ZERO, BigDecimal.ZERO,
			CommerceDiscountConstants.LIMITATION_TYPE_UNLIMITED, 0, false, true,
			1, 1, 2020, 1, 1, 0, 0, 0, 0, 0, true,
			ServiceContextTestUtil.getServiceContext());
	}

	@Inject
	private CommerceDiscountLocalService _commerceDiscountLocalService;

}