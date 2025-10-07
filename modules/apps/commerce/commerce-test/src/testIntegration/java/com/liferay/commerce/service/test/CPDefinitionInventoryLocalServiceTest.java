/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CPDefinitionInventoryConstants;
import com.liferay.commerce.exception.CPDefinitionInventoryAllowedOrderQuantitiesException;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CPDefinitionInventoryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Ivica Cardic
 */
@RunWith(Arquillian.class)
public class CPDefinitionInventoryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser();

		_commerceCatalog = _commerceCatalogService.addCommerceCatalog(
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), "USD", "en_US",
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		_cpDefinition = CPTestUtil.addCPDefinition(
			_commerceCatalog.getGroupId());
	}

	@Test
	public void testUpdateCPDefinitionInventory() throws Exception {
		CPDefinitionInventory cpDefinitionInventory1 =
			_cpDefinitionInventoryLocalService.
				fetchCPDefinitionInventoryByCPDefinitionId(
					_cpDefinition.getCPDefinitionId());

		if (cpDefinitionInventory1 != null) {
			_cpDefinitionInventoryLocalService.deleteCPDefinitionInventory(
				cpDefinitionInventory1);
		}

		CPDefinitionInventory cpDefinitionInventory2 =
			_cpDefinitionInventoryLocalService.addCPDefinitionInventory(
				_user.getUserId(), _cpDefinition.getCPDefinitionId(), "default",
				"default", false, false, BigDecimal.ONE, false,
				CPDefinitionInventoryConstants.DEFAULT_MIN_ORDER_QUANTITY,
				CPDefinitionInventoryConstants.DEFAULT_MAX_ORDER_QUANTITY,
				"701.78 2,333.00", BigDecimal.ONE);

		Assert.assertEquals(
			"701.78 2,333.00",
			cpDefinitionInventory2.getAllowedOrderQuantities());

		cpDefinitionInventory2.setAllowedOrderQuantities("1.001 2,333");

		Assert.assertThrows(
			CPDefinitionInventoryAllowedOrderQuantitiesException.class,
			() ->
				_cpDefinitionInventoryLocalService.updateCPDefinitionInventory(
					cpDefinitionInventory2.getCPDefinitionInventoryId(),
					cpDefinitionInventory2.getCPDefinitionInventoryEngine(),
					cpDefinitionInventory2.getLowStockActivity(),
					cpDefinitionInventory2.isDisplayAvailability(),
					cpDefinitionInventory2.isDisplayStockQuantity(),
					cpDefinitionInventory2.getMinStockQuantity(),
					cpDefinitionInventory2.isBackOrders(),
					cpDefinitionInventory2.getMinOrderQuantity(),
					cpDefinitionInventory2.getMaxOrderQuantity(),
					cpDefinitionInventory2.getAllowedOrderQuantities(),
					cpDefinitionInventory2.getMultipleOrderQuantity()));

		cpDefinitionInventory2.setAllowedOrderQuantities(
			" <div onclick=\"alert('test')\"></div>");

		Assert.assertThrows(
			CPDefinitionInventoryAllowedOrderQuantitiesException.class,
			() ->
				_cpDefinitionInventoryLocalService.updateCPDefinitionInventory(
					cpDefinitionInventory2.getCPDefinitionInventoryId(),
					cpDefinitionInventory2.getCPDefinitionInventoryEngine(),
					cpDefinitionInventory2.getLowStockActivity(),
					cpDefinitionInventory2.isDisplayAvailability(),
					cpDefinitionInventory2.isDisplayStockQuantity(),
					cpDefinitionInventory2.getMinStockQuantity(),
					cpDefinitionInventory2.isBackOrders(),
					cpDefinitionInventory2.getMinOrderQuantity(),
					cpDefinitionInventory2.getMaxOrderQuantity(),
					cpDefinitionInventory2.getAllowedOrderQuantities(),
					cpDefinitionInventory2.getMultipleOrderQuantity()));
	}

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogService _commerceCatalogService;

	private CPDefinition _cpDefinition;

	@Inject
	private CPDefinitionInventoryLocalService
		_cpDefinitionInventoryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private User _user;

}