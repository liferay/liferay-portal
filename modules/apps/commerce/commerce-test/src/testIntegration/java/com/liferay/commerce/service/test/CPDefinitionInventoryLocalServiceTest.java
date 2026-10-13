/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CPDefinitionInventoryConstants;
import com.liferay.commerce.exception.CPDefinitionInventoryAllowedOrderQuantitiesException;
import com.liferay.commerce.exception.CPDefinitionInventoryMaxOrderQuantityException;
import com.liferay.commerce.exception.CPDefinitionInventoryMinOrderQuantityException;
import com.liferay.commerce.exception.CPDefinitionInventoryMultipleOrderQuantityException;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngine;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngineRegistry;
import com.liferay.commerce.inventory.constants.CommerceInventoryAvailabilityConstants;
import com.liferay.commerce.inventory.engine.CommerceInventoryEngine;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CPDefinitionInventoryLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import org.frutilla.FrutillaRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Ivica Cardic
 * @author Michele Vigilante
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
	public void testGetAvailabilityStatusResolvesAsUnavailableWhenStockIsZeroAndBackOrdersAreDisabled()
		throws Exception {

		frutillaRule.scenario(
			"Resolve availability as unavailable when stock is zero and back " +
				"orders are disabled"
		).given(
			"A CPInstance with displayAvailability on, back orders off and " +
				"no warehouse stock"
		).when(
			"The inventory engine resolves the availability status"
		).then(
			"The status is UNAVAILABLE and the localized label reads " +
				"Unavailable"
		);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		CPDefinitionInventory cpDefinitionInventory =
			_cpDefinitionInventoryLocalService.
				fetchCPDefinitionInventoryByCPDefinitionId(
					cpInstance.getCPDefinitionId());

		cpDefinitionInventory =
			_cpDefinitionInventoryLocalService.updateCPDefinitionInventory(
				cpDefinitionInventory.getCPDefinitionInventoryId(),
				cpDefinitionInventory.getCPDefinitionInventoryEngine(),
				cpDefinitionInventory.getLowStockActivity(), true,
				cpDefinitionInventory.isDisplayStockQuantity(),
				cpDefinitionInventory.getMinStockQuantity(), false,
				cpDefinitionInventory.getMinOrderQuantity(),
				cpDefinitionInventory.getMaxOrderQuantity(),
				cpDefinitionInventory.getAllowedOrderQuantities(),
				cpDefinitionInventory.getMultipleOrderQuantity());

		CPDefinitionInventoryEngine cpDefinitionInventoryEngine =
			_cpDefinitionInventoryEngineRegistry.getCPDefinitionInventoryEngine(
				cpDefinitionInventory);

		Assert.assertTrue(
			cpDefinitionInventoryEngine.isDisplayAvailability(0, cpInstance));

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), "USD");

		String availabilityStatus =
			_commerceInventoryEngine.getAvailabilityStatus(
				cpInstance.getCompanyId(),
				AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
				cpInstance.getGroupId(), _commerceChannel.getGroupId(),
				cpDefinitionInventoryEngine.getMinStockQuantity(0, cpInstance),
				cpInstance.getSku(), StringPool.BLANK);

		Assert.assertEquals(
			CommerceInventoryAvailabilityConstants.UNAVAILABLE,
			availabilityStatus);
		Assert.assertEquals(
			"Unavailable", LanguageUtil.get(LocaleUtil.US, availabilityStatus));
	}

	@Test
	public void testUpdateCPDefinitionInventory() throws Exception {
		frutillaRule.scenario(
			"Reject allowedOrderQuantities updates that are not comma " +
				"separated numbers"
		).given(
			"A CPDefinitionInventory persisted with a valid " +
				"allowedOrderQuantities value"
		).when(
			"The allowedOrderQuantities is updated to a value that contains " +
				"more than two decimal digits or not valid content"
		).then(
			"The update fails with a " +
				"CPDefinitionInventoryAllowedOrderQuantitiesException"
		);

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

	@Test
	public void testUpdateCPDefinitionInventoryAcceptsDecimalMinOrderQuantityBetweenZeroAndOne()
		throws Exception {

		frutillaRule.scenario(
			"Updating the minimum order quantity to a decimal between zero " +
				"and one"
		).given(
			"A CPDefinitionInventory with default values"
		).when(
			"The minimum order quantity is set to 0.5"
		).then(
			"The update succeeds and the persisted value matches the new " +
				"minimum order quantity"
		);

		BigDecimal minOrderQuantity = BigDecimal.valueOf(0.5);

		CPDefinitionInventory cpDefinitionInventory =
			_addDefaultCPDefinitionInventory();

		cpDefinitionInventory = _updateCPDefinitionInventory(
			cpDefinitionInventory, minOrderQuantity,
			cpDefinitionInventory.getMaxOrderQuantity(),
			cpDefinitionInventory.getMultipleOrderQuantity());

		Assert.assertTrue(
			BigDecimalUtil.eq(
				minOrderQuantity, cpDefinitionInventory.getMinOrderQuantity()));
	}

	@Test
	public void testUpdateCPDefinitionInventoryRejectsNegativeMinOrderQuantity()
		throws Exception {

		frutillaRule.scenario(
			"Updating the minimum order quantity to a negative value"
		).given(
			"A CPDefinitionInventory with default values"
		).when(
			"The minimum order quantity is set to a negative decimal"
		).then(
			"The update fails with a " +
				"CPDefinitionInventoryMinOrderQuantityException"
		);

		CPDefinitionInventory cpDefinitionInventory =
			_addDefaultCPDefinitionInventory();

		Assert.assertThrows(
			CPDefinitionInventoryMinOrderQuantityException.class,
			() -> _updateCPDefinitionInventory(
				cpDefinitionInventory, BigDecimal.valueOf(-0.5),
				cpDefinitionInventory.getMaxOrderQuantity(),
				cpDefinitionInventory.getMultipleOrderQuantity()));
	}

	@Test
	public void testUpdateCPDefinitionInventoryRejectsZeroOrderQuantities()
		throws Exception {

		frutillaRule.scenario(
			"Updating an order quantity to zero"
		).given(
			"A CPDefinitionInventory with default values"
		).when(
			"The maximum, minimum or multiple order quantity is set to zero"
		).then(
			"The update fails with an exception"
		);

		CPDefinitionInventory cpDefinitionInventory =
			_addDefaultCPDefinitionInventory();

		Assert.assertThrows(
			CPDefinitionInventoryMaxOrderQuantityException.class,
			() -> _updateCPDefinitionInventory(
				cpDefinitionInventory,
				cpDefinitionInventory.getMinOrderQuantity(), BigDecimal.ZERO,
				cpDefinitionInventory.getMultipleOrderQuantity()));
		Assert.assertThrows(
			CPDefinitionInventoryMinOrderQuantityException.class,
			() -> _updateCPDefinitionInventory(
				cpDefinitionInventory, BigDecimal.ZERO,
				cpDefinitionInventory.getMaxOrderQuantity(),
				cpDefinitionInventory.getMultipleOrderQuantity()));
		Assert.assertThrows(
			CPDefinitionInventoryMultipleOrderQuantityException.class,
			() -> _updateCPDefinitionInventory(
				cpDefinitionInventory,
				cpDefinitionInventory.getMinOrderQuantity(),
				cpDefinitionInventory.getMaxOrderQuantity(), BigDecimal.ZERO));
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	private CPDefinitionInventory _addDefaultCPDefinitionInventory()
		throws Exception {

		CPDefinitionInventory cpDefinitionInventory =
			_cpDefinitionInventoryLocalService.
				fetchCPDefinitionInventoryByCPDefinitionId(
					_cpDefinition.getCPDefinitionId());

		if (cpDefinitionInventory != null) {
			_cpDefinitionInventoryLocalService.deleteCPDefinitionInventory(
				cpDefinitionInventory);
		}

		return _cpDefinitionInventoryLocalService.addCPDefinitionInventory(
			_user.getUserId(), _cpDefinition.getCPDefinitionId(), "default",
			"default", false, false, BigDecimal.ONE, false,
			CPDefinitionInventoryConstants.DEFAULT_MIN_ORDER_QUANTITY,
			CPDefinitionInventoryConstants.DEFAULT_MAX_ORDER_QUANTITY, "",
			BigDecimal.ONE);
	}

	private CPDefinitionInventory _updateCPDefinitionInventory(
			CPDefinitionInventory cpDefinitionInventory,
			BigDecimal minOrderQuantity, BigDecimal maxOrderQuantity,
			BigDecimal multipleOrderQuantity)
		throws Exception {

		return _cpDefinitionInventoryLocalService.updateCPDefinitionInventory(
			cpDefinitionInventory.getCPDefinitionInventoryId(),
			cpDefinitionInventory.getCPDefinitionInventoryEngine(),
			cpDefinitionInventory.getLowStockActivity(),
			cpDefinitionInventory.isDisplayAvailability(),
			cpDefinitionInventory.isDisplayStockQuantity(),
			cpDefinitionInventory.getMinStockQuantity(),
			cpDefinitionInventory.isBackOrders(), minOrderQuantity,
			maxOrderQuantity, cpDefinitionInventory.getAllowedOrderQuantities(),
			multipleOrderQuantity);
	}

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogService _commerceCatalogService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceInventoryEngine _commerceInventoryEngine;

	private CPDefinition _cpDefinition;

	@Inject
	private CPDefinitionInventoryEngineRegistry
		_cpDefinitionInventoryEngineRegistry;

	@Inject
	private CPDefinitionInventoryLocalService
		_cpDefinitionInventoryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private User _user;

}