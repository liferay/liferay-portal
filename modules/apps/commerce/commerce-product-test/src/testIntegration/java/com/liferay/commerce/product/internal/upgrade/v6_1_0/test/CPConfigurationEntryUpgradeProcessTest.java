/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v6_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationEntryLocalService;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueLocalService;
import com.liferay.commerce.product.service.CPSpecificationOptionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Ivica Cardic
 */
@RunWith(Arquillian.class)
public class CPConfigurationEntryUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext());

		CPConfigurationList masterCPConfigurationList =
			_cpConfigurationListLocalService.getMasterCPConfigurationList(
				commerceCatalog.getGroupId());

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				commerceCatalog.getGroupId(),
				masterCPConfigurationList.getCPConfigurationListId(), false,
				RandomTestUtil.randomString(), 2, 1, 1, 2024, 0, 0, 0, 0, 0, 0,
				0, true, ServiceContextTestUtil.getServiceContext());

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		CPConfigurationEntry cpConfigurationEntry =
			_cpConfigurationEntryLocalService.createCPConfigurationEntry(
				RandomTestUtil.nextLong());

		cpConfigurationEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());
		cpConfigurationEntry.setGroupId(cpConfigurationList.getGroupId());
		cpConfigurationEntry.setUserId(TestPropsValues.getUserId());
		cpConfigurationEntry.setClassNameId(
			_portal.getClassNameId(CPDefinition.class));
		cpConfigurationEntry.setClassPK(cpDefinition.getCPDefinitionId());
		cpConfigurationEntry.setCPConfigurationListId(
			cpConfigurationList.getCPConfigurationListId());
		cpConfigurationEntry.setAllowedOrderQuantities("1001.78 2,333.00");
		cpConfigurationEntry.setBackOrders(true);
		cpConfigurationEntry.setCPDefinitionInventoryEngine("cpde");
		cpConfigurationEntry.setDepth(1.0);
		cpConfigurationEntry.setDisplayAvailability(true);
		cpConfigurationEntry.setDisplayStockQuantity(true);
		cpConfigurationEntry.setFreeShipping(true);
		cpConfigurationEntry.setHeight(1.0);
		cpConfigurationEntry.setLowStockActivity("lowstoc");
		cpConfigurationEntry.setMaxOrderQuantity(BigDecimal.TEN);
		cpConfigurationEntry.setMinOrderQuantity(BigDecimal.ONE);
		cpConfigurationEntry.setMinStockQuantity(BigDecimal.ONE);
		cpConfigurationEntry.setMultipleOrderQuantity(BigDecimal.ONE);
		cpConfigurationEntry.setPurchasable(true);
		cpConfigurationEntry.setShippable(true);
		cpConfigurationEntry.setShippingExtraPrice(1.0);
		cpConfigurationEntry.setShipSeparately(true);
		cpConfigurationEntry.setTaxExempt(true);
		cpConfigurationEntry.setWeight(1.0);
		cpConfigurationEntry.setWidth(1.0);

		cpConfigurationEntry =
			_cpConfigurationEntryLocalService.updateCPConfigurationEntry(
				cpConfigurationEntry);

		_runUpgrade();

		EntityCacheUtil.clearCache();

		cpConfigurationEntry =
			_cpConfigurationEntryLocalService.getCPConfigurationEntry(
				cpConfigurationEntry.getCPConfigurationEntryId());

		Assert.assertEquals(
			"1,001 78 2 333 0",
			cpConfigurationEntry.getAllowedOrderQuantities());
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private static final String _CLASS_NAME =
		"com.liferay.commerce.product.internal.upgrade.v6_1_0." +
			"CPConfigurationEntryUpgradeProcess";

	@Inject
	private static CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private static CPConfigurationEntryLocalService
		_cpConfigurationEntryLocalService;

	@Inject
	private static CPConfigurationListLocalService
		_cpConfigurationListLocalService;

	@Inject
	private static CPDefinitionSpecificationOptionValueLocalService
		_cpDefinitionSpecificationOptionValueLocalService;

	@Inject
	private static CPSpecificationOptionLocalService
		_cpSpecificationOptionLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.product.internal.upgrade.registry.CommerceProductServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private Portal _portal;

}