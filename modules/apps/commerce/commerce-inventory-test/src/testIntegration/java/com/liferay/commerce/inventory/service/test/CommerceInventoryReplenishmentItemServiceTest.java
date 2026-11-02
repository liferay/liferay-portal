/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.inventory.model.CommerceInventoryReplenishmentItem;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryReplenishmentItemLocalService;
import com.liferay.commerce.inventory.service.CommerceInventoryReplenishmentItemService;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryReplenishmentItemUtil;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.Date;
import java.util.List;

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
public class CommerceInventoryReplenishmentItemServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(true);
		_cpInstance = CommerceInventoryTestUtil.addRandomCPInstanceSku(
			TestPropsValues.getGroupId());

		_commerceInventoryReplenishmentItemLocalService.
			addCommerceInventoryReplenishmentItem(
				null, TestPropsValues.getUserId(),
				_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				new Date(), BigDecimal.TEN, _cpInstance.getSku(),
				StringPool.BLANK);

		_role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null,
			RoleConstants.TYPE_REGULAR, null,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
		_user = UserTestUtil.addUser();

		_roleLocalService.addUserRole(_user.getUserId(), _role);
	}

	@Test
	public void testDeleteCommerceInventoryReplenishmentItems()
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceInventoryReplenishmentItemService.
				deleteCommerceInventoryReplenishmentItems(
					_user.getCompanyId(), _cpInstance.getSku(),
					StringPool.BLANK);

			Assert.assertFalse(
				ListUtil.isEmpty(
					_getCommerceInventoryReplenishmentItemsByCommerceInventoryWarehouseId(
						_commerceInventoryWarehouse.
							getCommerceInventoryWarehouseId(),
						QueryUtil.ALL_POS, QueryUtil.ALL_POS)));
		}

		_resourcePermissionLocalService.setResourcePermissions(
			_commerceInventoryWarehouse.getCompanyId(),
			CommerceInventoryWarehouse.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(
				_commerceInventoryWarehouse.getCommerceInventoryWarehouseId()),
			_role.getRoleId(), new String[] {ActionKeys.UPDATE});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceInventoryReplenishmentItemService.
				deleteCommerceInventoryReplenishmentItems(
					_user.getCompanyId(), _cpInstance.getSku(),
					StringPool.BLANK);

			Assert.assertTrue(
				ListUtil.isEmpty(
					_getCommerceInventoryReplenishmentItemsByCommerceInventoryWarehouseId(
						_commerceInventoryWarehouse.
							getCommerceInventoryWarehouseId(),
						QueryUtil.ALL_POS, QueryUtil.ALL_POS)));
		}
	}

	@Test
	public void testGetCommerceInventoryReplenishmentItemsByCompanyIdSkuAndUnitOfMeasureKey()
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			Assert.assertTrue(
				ListUtil.isEmpty(
					_commerceInventoryReplenishmentItemService.
						getCommerceInventoryReplenishmentItemsByCompanyIdSkuAndUnitOfMeasureKey(
							_user.getCompanyId(), _cpInstance.getSku(),
							StringPool.BLANK, QueryUtil.ALL_POS,
							QueryUtil.ALL_POS)));
		}

		_resourcePermissionLocalService.setResourcePermissions(
			_commerceInventoryWarehouse.getCompanyId(),
			CommerceInventoryWarehouse.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(
				_commerceInventoryWarehouse.getCommerceInventoryWarehouseId()),
			_role.getRoleId(), new String[] {ActionKeys.VIEW});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			Assert.assertFalse(
				ListUtil.isEmpty(
					_commerceInventoryReplenishmentItemService.
						getCommerceInventoryReplenishmentItemsByCompanyIdSkuAndUnitOfMeasureKey(
							_user.getCompanyId(), _cpInstance.getSku(),
							StringPool.BLANK, QueryUtil.ALL_POS,
							QueryUtil.ALL_POS)));
		}
	}

	private List<CommerceInventoryReplenishmentItem>
			_getCommerceInventoryReplenishmentItemsByCommerceInventoryWarehouseId(
				long commerceInventoryWarehouseId, int start, int end)
		throws Exception {

		return TransactionTestUtil.invoke(
			CommerceInventoryReplenishmentItemUtil.getPersistence(),
			commerceInventoryReplenishmentItemPersistence ->
				commerceInventoryReplenishmentItemPersistence.
					findByCommerceInventoryWarehouseId(
						commerceInventoryWarehouseId, start, end));
	}

	@Inject
	private CommerceInventoryReplenishmentItemLocalService
		_commerceInventoryReplenishmentItemLocalService;

	@Inject
	private CommerceInventoryReplenishmentItemService
		_commerceInventoryReplenishmentItemService;

	private CommerceInventoryWarehouse _commerceInventoryWarehouse;
	private CPInstance _cpInstance;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	private User _user;

}