/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.internal.upgrade.v2_11_6.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseLocalService;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Matyas Wollner
 */
@RunWith(Arquillian.class)
public class CommerceInventoryWarehouseUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse();

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			commerceInventoryWarehouse.getStatus());

		long commerceInventoryWarehouseId =
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId();

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"update CIWarehouse set status = ? where CIWarehouseId = ?")) {

			preparedStatement.setInt(1, WorkflowConstants.STATUS_DENIED);
			preparedStatement.setLong(2, commerceInventoryWarehouseId);

			preparedStatement.executeUpdate();
		}

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.commerce.inventory.internal.upgrade.v2_11_6." +
				"CommerceInventoryWarehouseUpgradeProcess");

		upgradeProcess.upgrade();

		EntityCacheUtil.clearCache();

		commerceInventoryWarehouse =
			_commerceInventoryWarehouseLocalService.
				getCommerceInventoryWarehouse(commerceInventoryWarehouseId);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			commerceInventoryWarehouse.getStatus());
	}

	@Inject
	private CommerceInventoryWarehouseLocalService
		_commerceInventoryWarehouseLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.inventory.internal.upgrade.registry.CommerceInventoryServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}