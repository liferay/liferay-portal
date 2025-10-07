/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngine;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationEntryLocalService;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CPDefinitionInventoryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.Portal;
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
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class CPDefinitionInventoryEngineTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), _user.getUserId());

		_commerceCatalog = _commerceCatalogService.addCommerceCatalog(
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), "USD", "en_US", _serviceContext);

		CPConfigurationList masterCPConfigurationList =
			_cpConfigurationListLocalService.getMasterCPConfigurationList(
				_commerceCatalog.getGroupId());

		_cpConfigurationList =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceCatalog.getGroupId(),
				masterCPConfigurationList.getCPConfigurationListId(), false,
				RandomTestUtil.randomString(), 2, 1, 1, 2024, 0, 0, 0, 0, 0, 0,
				0, true, new ServiceContext());

		_cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		_cpConfigurationEntry =
			_cpConfigurationEntryLocalService.addCPConfigurationEntry(
				RandomTestUtil.randomString(), _user.getUserId(),
				_cpConfigurationList.getGroupId(),
				_portal.getClassNameId(CPDefinition.class),
				_cpInstance.getCPDefinitionId(),
				_cpConfigurationList.getCPConfigurationListId(), 0, "1,234.00",
				true, 0, "cpde", 1.0, true, true, true, 1.0, "lowstoc",
				BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE,
				true, true, 1.0, true, true, 1.0, 1.0);
	}

	@Test
	public void testGetMaxOrderQuantity() throws Exception {
		BigDecimal maxOrderQuantity =
			_cpDefinitionInventoryEngine.getMaxOrderQuantity(0, _cpInstance);

		CPDefinitionInventory cpDefinitionInventory =
			_cpDefinitionInventoryLocalService.
				fetchCPDefinitionInventoryByCPDefinitionId(
					_cpInstance.getCPDefinitionId());

		Assert.assertTrue(
			BigDecimalUtil.eq(
				maxOrderQuantity, cpDefinitionInventory.getMaxOrderQuantity()));

		maxOrderQuantity = _cpDefinitionInventoryEngine.getMaxOrderQuantity(
			_cpConfigurationList.getCPConfigurationListId(), _cpInstance);

		CPConfigurationEntry cpConfigurationEntry =
			_cpConfigurationEntryLocalService.fetchCPConfigurationEntry(
				_classNameLocalService.getClassNameId(CPDefinition.class),
				_cpInstance.getCPDefinitionId(),
				_cpConfigurationList.getCPConfigurationListId());

		Assert.assertTrue(
			BigDecimalUtil.eq(
				maxOrderQuantity, cpConfigurationEntry.getMaxOrderQuantity()));
	}

	@Test
	public void testGetMinOrderQuantity() throws Exception {
		BigDecimal minOrderQuantity =
			_cpDefinitionInventoryEngine.getMinOrderQuantity(0, _cpInstance);

		CPDefinitionInventory cpDefinitionInventory =
			_cpDefinitionInventoryLocalService.
				fetchCPDefinitionInventoryByCPDefinitionId(
					_cpInstance.getCPDefinitionId());

		Assert.assertTrue(
			BigDecimalUtil.eq(
				minOrderQuantity, cpDefinitionInventory.getMinOrderQuantity()));

		minOrderQuantity = _cpDefinitionInventoryEngine.getMinOrderQuantity(
			_cpConfigurationList.getCPConfigurationListId(), _cpInstance);

		CPConfigurationEntry cpConfigurationEntry =
			_cpConfigurationEntryLocalService.fetchCPConfigurationEntry(
				_classNameLocalService.getClassNameId(CPDefinition.class),
				_cpInstance.getCPDefinitionId(),
				_cpConfigurationList.getCPConfigurationListId());

		Assert.assertTrue(
			BigDecimalUtil.eq(
				minOrderQuantity, cpConfigurationEntry.getMinOrderQuantity()));
	}

	@Test
	public void testGetMinStockQuantity() throws Exception {
		BigDecimal minStockQuantity =
			_cpDefinitionInventoryEngine.getMinStockQuantity(0, _cpInstance);

		CPDefinitionInventory cpDefinitionInventory =
			_cpDefinitionInventoryLocalService.
				fetchCPDefinitionInventoryByCPDefinitionId(
					_cpInstance.getCPDefinitionId());

		Assert.assertTrue(
			BigDecimalUtil.eq(
				minStockQuantity, cpDefinitionInventory.getMinStockQuantity()));

		minStockQuantity = _cpDefinitionInventoryEngine.getMinStockQuantity(
			_cpConfigurationList.getCPConfigurationListId(), _cpInstance);

		CPConfigurationEntry cpConfigurationEntry =
			_cpConfigurationEntryLocalService.fetchCPConfigurationEntry(
				_classNameLocalService.getClassNameId(CPDefinition.class),
				_cpInstance.getCPDefinitionId(),
				_cpConfigurationList.getCPConfigurationListId());

		Assert.assertTrue(
			BigDecimalUtil.eq(
				minStockQuantity, cpConfigurationEntry.getMinStockQuantity()));
	}

	@Test
	public void testGetMultipleOrderQuantity() throws Exception {
		BigDecimal multipleOrderQuantity =
			_cpDefinitionInventoryEngine.getMultipleOrderQuantity(
				0, _cpInstance);

		CPDefinitionInventory cpDefinitionInventory =
			_cpDefinitionInventoryLocalService.
				fetchCPDefinitionInventoryByCPDefinitionId(
					_cpInstance.getCPDefinitionId());

		Assert.assertTrue(
			BigDecimalUtil.eq(
				multipleOrderQuantity,
				cpDefinitionInventory.getMultipleOrderQuantity()));

		multipleOrderQuantity =
			_cpDefinitionInventoryEngine.getMultipleOrderQuantity(
				_cpConfigurationList.getCPConfigurationListId(), _cpInstance);

		CPConfigurationEntry cpConfigurationEntry =
			_cpConfigurationEntryLocalService.fetchCPConfigurationEntry(
				_classNameLocalService.getClassNameId(CPDefinition.class),
				_cpInstance.getCPDefinitionId(),
				_cpConfigurationList.getCPConfigurationListId());

		Assert.assertTrue(
			BigDecimalUtil.eq(
				multipleOrderQuantity,
				cpConfigurationEntry.getMultipleOrderQuantity()));
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogService _commerceCatalogService;

	private CPConfigurationEntry _cpConfigurationEntry;

	@Inject
	private CPConfigurationEntryLocalService _cpConfigurationEntryLocalService;

	@DeleteAfterTestRun
	private CPConfigurationList _cpConfigurationList;

	@Inject
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	@Inject
	private CPDefinitionInventoryEngine _cpDefinitionInventoryEngine;

	@Inject
	private CPDefinitionInventoryLocalService
		_cpDefinitionInventoryLocalService;

	private CPInstance _cpInstance;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;
	private User _user;

}