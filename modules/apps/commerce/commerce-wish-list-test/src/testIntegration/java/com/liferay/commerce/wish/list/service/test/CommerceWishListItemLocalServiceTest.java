/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.wish.list.model.CommerceWishList;
import com.liferay.commerce.wish.list.service.CommerceWishListItemLocalService;
import com.liferay.commerce.wish.list.service.CommerceWishListService;
import com.liferay.commerce.wish.list.test.util.CommerceWishListItemTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
public class CommerceWishListItemLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_cpInstance = CPTestUtil.addCPInstance(_group.getGroupId());

		_user = UserTestUtil.addUser();
	}

	@Test
	public void testAddOrUpdateCommerceWishListItem() throws Exception {
		CommerceWishList commerceWishList =
			_commerceWishListService.addCommerceWishList(
				_group.getGroupId(), RandomTestUtil.randomString(), true);

		CPDefinition cpDefinition = _cpInstance.getCPDefinition();

		CProduct cProduct = cpDefinition.getCProduct();

		_commerceWishListItemLocalService.addOrUpdateCommerceWishListItem(
			_user.getUserId(), commerceWishList.getCommerceWishListId(),
			_cpInstance.getCPInstanceUuid(), cProduct.getCProductId(), "");

		Assert.assertEquals(
			1,
			CommerceWishListItemTestUtil.getCommerceWishListItemsCount(
				commerceWishList.getCommerceWishListId()));

		_commerceWishListItemLocalService.addOrUpdateCommerceWishListItem(
			_user.getUserId(), commerceWishList.getCommerceWishListId(),
			_cpInstance.getCPInstanceUuid(), cProduct.getCProductId(), "");

		Assert.assertEquals(
			1,
			CommerceWishListItemTestUtil.getCommerceWishListItemsCount(
				commerceWishList.getCommerceWishListId()));
	}

	@Inject
	private CommerceWishListItemLocalService _commerceWishListItemLocalService;

	@Inject
	private CommerceWishListService _commerceWishListService;

	private CPInstance _cpInstance;
	private Group _group;
	private User _user;

}