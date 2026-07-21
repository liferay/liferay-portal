/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v6_5_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CPOptionValue;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.commerce.product.service.CPOptionValueLocalService;
import com.liferay.commerce.product.service.CPTaxCategoryLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocaleUtil;
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
public class CommerceProductStatusUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgradeCommerceCatalogStatus() throws Exception {
		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext());

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update CommerceCatalog set status = ? where " +
						"commerceCatalogId = ?")) {

			preparedStatement.setInt(1, WorkflowConstants.STATUS_DENIED);
			preparedStatement.setLong(
				2, commerceCatalog.getCommerceCatalogId());

			preparedStatement.executeUpdate();
		}

		_runUpgrade();

		commerceCatalog = _commerceCatalogLocalService.getCommerceCatalog(
			commerceCatalog.getCommerceCatalogId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, commerceCatalog.getStatus());
	}

	@Test
	public void testUpgradeCPOptionStatus() throws Exception {
		CPOption cpOption = CPTestUtil.addCPOption(
			TestPropsValues.getGroupId(), false);

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update CPOption set status = ? where CPOptionId = ?")) {

			preparedStatement.setInt(1, WorkflowConstants.STATUS_DENIED);
			preparedStatement.setLong(2, cpOption.getCPOptionId());

			preparedStatement.executeUpdate();
		}

		_runUpgrade();

		cpOption = _cpOptionLocalService.getCPOption(cpOption.getCPOptionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, cpOption.getStatus());
	}

	@Test
	public void testUpgradeCPOptionValueStatus() throws Exception {
		CPOptionValue cpOptionValue = CPTestUtil.addCPOptionValue(
			CPTestUtil.addCPOption(TestPropsValues.getGroupId(), false));

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update CPOptionValue set status = ? where " +
						"CPOptionValueId = ?")) {

			preparedStatement.setInt(1, WorkflowConstants.STATUS_DENIED);
			preparedStatement.setLong(2, cpOptionValue.getCPOptionValueId());

			preparedStatement.executeUpdate();
		}

		_runUpgrade();

		cpOptionValue = _cpOptionValueLocalService.getCPOptionValue(
			cpOptionValue.getCPOptionValueId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, cpOptionValue.getStatus());
	}

	@Test
	public void testUpgradeCPTaxCategoryStatus() throws Exception {
		CPTaxCategory cpTaxCategory =
			_cpTaxCategoryLocalService.addCPTaxCategory(
				null, RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				ServiceContextTestUtil.getServiceContext());

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update CPTaxCategory set status = ? where " +
						"CPTaxCategoryId = ?")) {

			preparedStatement.setInt(1, WorkflowConstants.STATUS_DENIED);
			preparedStatement.setLong(2, cpTaxCategory.getCPTaxCategoryId());

			preparedStatement.executeUpdate();
		}

		_runUpgrade();

		cpTaxCategory = _cpTaxCategoryLocalService.getCPTaxCategory(
			cpTaxCategory.getCPTaxCategoryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, cpTaxCategory.getStatus());
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		EntityCacheUtil.clearCache();
	}

	private static final String _CLASS_NAME =
		"com.liferay.commerce.product.internal.upgrade.v6_5_0." +
			"CommerceProductStatusUpgradeProcess";

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CPOptionLocalService _cpOptionLocalService;

	@Inject
	private CPOptionValueLocalService _cpOptionValueLocalService;

	@Inject
	private CPTaxCategoryLocalService _cpTaxCategoryLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.product.internal.upgrade.registry.CommerceProductServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}