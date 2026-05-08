/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.inventory.constants.CommerceInventoryActionKeys;
import com.liferay.commerce.inventory.constants.CommerceInventoryConstants;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemLocalService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemService;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

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
public class CommerceInventoryWarehouseItemServiceTest {

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

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			TestPropsValues.getUserId(), _commerceInventoryWarehouse,
			BigDecimal.TEN, _cpInstance.getSku(), StringPool.BLANK);

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
	public void testDeleteCommerceInventoryWarehouseItems() throws Exception {
		CPInstance cpInstance =
			CommerceInventoryTestUtil.addRandomCPInstanceSku(
				TestPropsValues.getGroupId());

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			TestPropsValues.getUserId(), _commerceInventoryWarehouse,
			BigDecimal.TEN, cpInstance.getSku(), StringPool.BLANK);

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure1 =
			CPTestUtil.addCPInstanceUnitOfMeasure(
				TestPropsValues.getGroupId(), _cpInstance.getCPInstanceId(),
				RandomTestUtil.randomString(), BigDecimal.TEN,
				_cpInstance.getSku());
		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure2 =
			CPTestUtil.addCPInstanceUnitOfMeasure(
				TestPropsValues.getGroupId(), _cpInstance.getCPInstanceId(),
				RandomTestUtil.randomString(), BigDecimal.TEN,
				_cpInstance.getSku());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceInventoryWarehouseItemService.
				deleteCommerceInventoryWarehouseItems(
					_user.getCompanyId(), _cpInstance.getSku(),
					cpInstanceUnitOfMeasure1.getKey());
		}

		Assert.assertNotNull(
			_commerceInventoryWarehouseItemLocalService.
				fetchCommerceInventoryWarehouseItem(
					_commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId(),
					_cpInstance.getSku(), cpInstanceUnitOfMeasure1.getKey()));
		Assert.assertNotNull(
			_commerceInventoryWarehouseItemLocalService.
				fetchCommerceInventoryWarehouseItem(
					_commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId(),
					_cpInstance.getSku(), cpInstanceUnitOfMeasure2.getKey()));
		Assert.assertNotNull(
			_commerceInventoryWarehouseItemLocalService.
				fetchCommerceInventoryWarehouseItem(
					_commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId(),
					cpInstance.getSku(), StringPool.BLANK));

		_resourcePermissionLocalService.setResourcePermissions(
			_commerceInventoryWarehouse.getCompanyId(),
			CommerceInventoryWarehouse.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(
				_commerceInventoryWarehouse.getCommerceInventoryWarehouseId()),
			_role.getRoleId(), new String[] {ActionKeys.UPDATE});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceInventoryWarehouseItemService.
				deleteCommerceInventoryWarehouseItems(
					_user.getCompanyId(), _cpInstance.getSku(),
					cpInstanceUnitOfMeasure1.getKey());
		}

		Assert.assertNull(
			_commerceInventoryWarehouseItemLocalService.
				fetchCommerceInventoryWarehouseItem(
					_commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId(),
					_cpInstance.getSku(), cpInstanceUnitOfMeasure1.getKey()));
		Assert.assertNotNull(
			_commerceInventoryWarehouseItemLocalService.
				fetchCommerceInventoryWarehouseItem(
					_commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId(),
					_cpInstance.getSku(), cpInstanceUnitOfMeasure2.getKey()));
		Assert.assertNotNull(
			_commerceInventoryWarehouseItemLocalService.
				fetchCommerceInventoryWarehouseItem(
					_commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId(),
					cpInstance.getSku(), StringPool.BLANK));
	}

	@Test
	public void testGetCommerceInventoryWarehouseItemsByCompanyId()
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceInventoryWarehouseItemService.
				getCommerceInventoryWarehouseItemsByCompanyId(
					_user.getCompanyId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				CommerceInventoryActionKeys.VIEW_INVENTORIES,
				exception.getMessage(), _user.getUserId());
		}

		RoleTestUtil.addResourcePermission(
			_role, CommerceInventoryConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceInventoryActionKeys.VIEW_INVENTORIES);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceInventoryWarehouseItemService.
				getCommerceInventoryWarehouseItemsByCompanyId(
					_user.getCompanyId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}
	}

	@Test
	public void testGetCommerceInventoryWarehouseItemsByCompanyIdSkuAndUnitOfMeasureKey()
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			Assert.assertTrue(
				ListUtil.isEmpty(
					_commerceInventoryWarehouseItemService.
						getCommerceInventoryWarehouseItemsByCompanyIdSkuAndUnitOfMeasureKey(
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
					_commerceInventoryWarehouseItemService.
						getCommerceInventoryWarehouseItemsByCompanyIdSkuAndUnitOfMeasureKey(
							_user.getCompanyId(), _cpInstance.getSku(),
							StringPool.BLANK, QueryUtil.ALL_POS,
							QueryUtil.ALL_POS)));
		}
	}

	@Test
	public void testGetCommerceInventoryWarehouseItemsCount1()
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceInventoryWarehouseItemService.
				getCommerceInventoryWarehouseItemsCount(
					_user.getCompanyId(), 0, _user.getGroupId(),
					_cpInstance.getSku(), StringPool.BLANK);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				CommerceInventoryActionKeys.VIEW_INVENTORIES,
				exception.getMessage(), _user.getUserId());
		}

		RoleTestUtil.addResourcePermission(
			_role, CommerceInventoryConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceInventoryActionKeys.VIEW_INVENTORIES);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceInventoryWarehouseItemService.
				getCommerceInventoryWarehouseItemsCount(
					_user.getCompanyId(), 0, _user.getGroupId(),
					_cpInstance.getSku(), StringPool.BLANK);
		}
	}

	@Test
	public void testGetCommerceInventoryWarehouseItemsCount2()
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			Assert.assertEquals(
				0,
				_commerceInventoryWarehouseItemService.
					getCommerceInventoryWarehouseItemsCount(
						_user.getCompanyId(), _cpInstance.getSku(),
						StringPool.BLANK));
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

			Assert.assertEquals(
				1,
				_commerceInventoryWarehouseItemService.
					getCommerceInventoryWarehouseItemsCount(
						_user.getCompanyId(), _cpInstance.getSku(),
						StringPool.BLANK));
		}
	}

	@Test
	public void testGetCommerceInventoryWarehouseItemsCountByCompanyId()
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceInventoryWarehouseItemService.
				getCommerceInventoryWarehouseItemsCountByCompanyId(
					_user.getCompanyId());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				CommerceInventoryActionKeys.VIEW_INVENTORIES,
				exception.getMessage(), _user.getUserId());
		}

		RoleTestUtil.addResourcePermission(
			_role, CommerceInventoryConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceInventoryActionKeys.VIEW_INVENTORIES);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceInventoryWarehouseItemService.
				getCommerceInventoryWarehouseItemsCountByCompanyId(
					_user.getCompanyId());
		}
	}

	@Test
	public void testGetCommerceInventoryWarehouseItemsCountByModifiedDate1()
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceInventoryWarehouseItemService.
				getCommerceInventoryWarehouseItemsCountByModifiedDate(
					_user.getCompanyId(), new Date(), new Date());

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				CommerceInventoryActionKeys.VIEW_INVENTORIES,
				exception.getMessage(), _user.getUserId());
		}

		RoleTestUtil.addResourcePermission(
			_role, CommerceInventoryConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceInventoryActionKeys.VIEW_INVENTORIES);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceInventoryWarehouseItemService.
				getCommerceInventoryWarehouseItemsCountByModifiedDate(
					_user.getCompanyId(), new Date(), new Date());
		}
	}

	@Test
	public void testGetCommerceInventoryWarehouseItemsCountByModifiedDate2()
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceInventoryWarehouseItemService.
				getCommerceInventoryWarehouseItemsCountByModifiedDate(
					_user.getCompanyId(), new Date(), new Date(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			Assert.fail();
		}
		catch (Exception exception) {
			_assertMessage(
				CommerceInventoryActionKeys.VIEW_INVENTORIES,
				exception.getMessage(), _user.getUserId());
		}

		RoleTestUtil.addResourcePermission(
			_role, CommerceInventoryConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			CommerceInventoryActionKeys.VIEW_INVENTORIES);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_commerceInventoryWarehouseItemService.
				getCommerceInventoryWarehouseItemsCountByModifiedDate(
					_user.getCompanyId(), new Date(), new Date(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}
	}

	private void _assertMessage(String actionKey, String message, long userId) {
		Assert.assertTrue(
			message.contains(
				StringBundler.concat(
					"User ", userId, " must have ", actionKey,
					" permission for")));
	}

	private CommerceInventoryWarehouse _commerceInventoryWarehouse;

	@Inject
	private CommerceInventoryWarehouseItemLocalService
		_commerceInventoryWarehouseItemLocalService;

	@Inject
	private CommerceInventoryWarehouseItemService
		_commerceInventoryWarehouseItemService;

	private CPInstance _cpInstance;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	private User _user;

}