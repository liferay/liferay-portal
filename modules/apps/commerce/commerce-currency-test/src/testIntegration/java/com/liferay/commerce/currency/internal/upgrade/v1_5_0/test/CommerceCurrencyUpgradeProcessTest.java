/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.internal.upgrade.v1_5_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class CommerceCurrencyUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(
				TestPropsValues.getCompanyId());

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"update CommerceCurrency set status = ? where " +
					"commerceCurrencyId = ?")) {

			preparedStatement.setInt(1, WorkflowConstants.STATUS_DENIED);
			preparedStatement.setLong(
				2, commerceCurrency.getCommerceCurrencyId());

			preparedStatement.executeUpdate();
		}

		_runUpgrade();

		commerceCurrency = _commerceCurrencyLocalService.getCommerceCurrency(
			commerceCurrency.getCommerceCurrencyId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, commerceCurrency.getStatus());
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		EntityCacheUtil.clearCache();
	}

	private static final String _CLASS_NAME =
		"com.liferay.commerce.currency.internal.upgrade.v1_5_0." +
			"CommerceCurrencyUpgradeProcess";

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.currency.internal.upgrade.registry.CommerceCurrencyServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}