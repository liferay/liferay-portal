/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.CommerceDiscountLocalService;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListDiscountRel;
import com.liferay.commerce.price.list.service.CommercePriceListDiscountRelLocalService;
import com.liferay.commerce.test.util.price.list.CommercePriceListTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class CommercePriceListDiscountRelLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_commerceDiscount = _commerceDiscountLocalService.addCommerceDiscount(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			CommerceDiscountConstants.TARGET_PRODUCTS, false, null, true,
			BigDecimal.ONE, CommerceDiscountConstants.LEVEL_L1, BigDecimal.ONE,
			BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
			CommerceDiscountConstants.TYPE_PERCENTAGE, 0, false, true, 1, 1,
			2020, 1, 1, 0, 0, 0, 0, 0, true,
			ServiceContextTestUtil.getServiceContext());

		_group = GroupTestUtil.addGroup();

		_commercePriceList = CommercePriceListTestUtil.addCommercePriceList(
			_group.getGroupId(), false,
			CommercePriceListConstants.TYPE_PRICE_LIST, 1.0);
	}

	@Test
	public void testDeleteCommercePriceListDiscountRels() throws Exception {
		CommercePriceListDiscountRel commercePriceListDiscountRel =
			_commercePriceListDiscountRelLocalService.
				addCommercePriceListDiscountRel(
					TestPropsValues.getUserId(),
					_commercePriceList.getCommercePriceListId(),
					_commerceDiscount.getCommerceDiscountId(), 0,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId()));

		_commercePriceListDiscountRelLocalService.
			deleteCommercePriceListDiscountRels(
				_commercePriceList.getCommercePriceListId());

		Assert.assertEquals(
			Collections.emptyList(),
			_commercePriceListDiscountRelLocalService.
				getCommercePriceListDiscountRels(
					_commercePriceList.getCommercePriceListId()));

		List<SystemEvent> systemEvents =
			_systemEventLocalService.getSystemEvents(
				0,
				_portal.getClassNameId(
					commercePriceListDiscountRel.getModelClassName()),
				commercePriceListDiscountRel.getPrimaryKey(),
				SystemEventConstants.TYPE_DELETE);

		Assert.assertEquals(systemEvents.toString(), 1, systemEvents.size());

		SystemEvent systemEvent = systemEvents.get(0);

		Assert.assertEquals(
			SystemEventConstants.TYPE_DELETE, systemEvent.getType());
	}

	private CommerceDiscount _commerceDiscount;

	@Inject
	private CommerceDiscountLocalService _commerceDiscountLocalService;

	private CommercePriceList _commercePriceList;

	@Inject
	private CommercePriceListDiscountRelLocalService
		_commercePriceListDiscountRelLocalService;

	private Group _group;

	@Inject
	private Portal _portal;

	@Inject
	private SystemEventLocalService _systemEventLocalService;

}