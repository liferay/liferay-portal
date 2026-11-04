/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.wish.list.exception.RequiredCommerceWishListException;
import com.liferay.commerce.wish.list.model.CommerceWishList;
import com.liferay.commerce.wish.list.service.CommerceWishListItemLocalService;
import com.liferay.commerce.wish.list.service.CommerceWishListLocalService;
import com.liferay.commerce.wish.list.service.CommerceWishListService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Date;

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
public class CommerceWishListLocalServiceTest {

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
		_user = UserTestUtil.addUser();
	}

	@Test(expected = RequiredCommerceWishListException.class)
	public void testDeleteCommerceWishList() throws Exception {
		CommerceWishList commerceWishList =
			_commerceWishListService.addCommerceWishList(
				_group.getGroupId(), RandomTestUtil.randomString(), true);

		_commerceWishListService.deleteCommerceWishList(
			commerceWishList.getCommerceWishListId());
	}

	@Test
	public void testDeleteCommerceWishLists() throws Exception {
		CommerceWishList commerceWishList =
			_commerceWishListLocalService.addCommerceWishList(
				_user.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), true);

		commerceWishList.setCreateDate(
			new Date(System.currentTimeMillis() - Time.DAY));

		commerceWishList = _commerceWishListLocalService.updateCommerceWishList(
			commerceWishList);

		CPInstance cpInstance = CPTestUtil.addCPInstance(_group.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		_commerceWishListItemLocalService.addOrUpdateCommerceWishListItem(
			_user.getUserId(), commerceWishList.getCommerceWishListId(),
			cpInstance.getCPInstanceUuid(), cpDefinition.getCProductId(),
			StringPool.BLANK);

		Assert.assertEquals(
			1,
			_commerceWishListItemLocalService.getCommerceWishListItemsCount(
				commerceWishList.getCommerceWishListId()));

		_commerceWishListLocalService.deleteCommerceWishLists(
			_user.getUserId(), new Date());

		Assert.assertNull(
			_commerceWishListLocalService.fetchCommerceWishList(
				commerceWishList.getCommerceWishListId()));
		Assert.assertEquals(
			0,
			_commerceWishListItemLocalService.getCommerceWishListItemsCount(
				commerceWishList.getCommerceWishListId()));
	}

	@Inject
	private CommerceWishListItemLocalService _commerceWishListItemLocalService;

	@Inject
	private CommerceWishListLocalService _commerceWishListLocalService;

	@Inject
	private CommerceWishListService _commerceWishListService;

	private Group _group;
	private User _user;

}