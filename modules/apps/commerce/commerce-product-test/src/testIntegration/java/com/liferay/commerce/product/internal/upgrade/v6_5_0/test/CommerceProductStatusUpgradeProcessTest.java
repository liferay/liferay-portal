/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v6_5_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.constants.CPMeasurementUnitConstants;
import com.liferay.commerce.product.model.CPMeasurementUnit;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.model.CPOptionValue;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPMeasurementUnitLocalService;
import com.liferay.commerce.product.service.CPOptionCategoryLocalService;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.commerce.product.service.CPOptionValueLocalService;
import com.liferay.commerce.product.service.CPSpecificationOptionLocalService;
import com.liferay.commerce.product.service.CPTaxCategoryLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
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
	public void testUpgradeCommerceCatalog() throws Exception {
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
	public void testUpgradeCommerceChannel() throws Exception {
		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			TestPropsValues.getGroupId(), RandomTestUtil.randomString());

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update CommerceChannel set status = ? where " +
						"commerceChannelId = ?")) {

			preparedStatement.setInt(1, WorkflowConstants.STATUS_DENIED);
			preparedStatement.setLong(
				2, commerceChannel.getCommerceChannelId());

			preparedStatement.executeUpdate();
		}

		_runUpgrade();

		commerceChannel = _commerceChannelLocalService.getCommerceChannel(
			commerceChannel.getCommerceChannelId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, commerceChannel.getStatus());
	}

	@Test
	public void testUpgradeCPMeasurementUnit() throws Exception {
		CPMeasurementUnit cpMeasurementUnit =
			_cpMeasurementUnitLocalService.addCPMeasurementUnit(
				null, RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomString(), RandomTestUtil.randomDouble(),
				false, RandomTestUtil.randomDouble(),
				CPMeasurementUnitConstants.TYPE_UNIT,
				ServiceContextTestUtil.getServiceContext());

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update CPMeasurementUnit set status = ? where " +
						"CPMeasurementUnitId = ?")) {

			preparedStatement.setInt(1, WorkflowConstants.STATUS_DENIED);
			preparedStatement.setLong(
				2, cpMeasurementUnit.getCPMeasurementUnitId());

			preparedStatement.executeUpdate();
		}

		_runUpgrade();

		cpMeasurementUnit = _cpMeasurementUnitLocalService.getCPMeasurementUnit(
			cpMeasurementUnit.getCPMeasurementUnitId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, cpMeasurementUnit.getStatus());
	}

	@Test
	public void testUpgradeCPOption() throws Exception {
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
	public void testUpgradeCPOptionCategory() throws Exception {
		CPOptionCategory cpOptionCategory = CPTestUtil.addCPOptionCategory(
			TestPropsValues.getGroupId());

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update CPOptionCategory set status = ? where " +
						"CPOptionCategoryId = ?")) {

			preparedStatement.setInt(1, WorkflowConstants.STATUS_DENIED);
			preparedStatement.setLong(
				2, cpOptionCategory.getCPOptionCategoryId());

			preparedStatement.executeUpdate();
		}

		_runUpgrade();

		cpOptionCategory = _cpOptionCategoryLocalService.getCPOptionCategory(
			cpOptionCategory.getCPOptionCategoryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, cpOptionCategory.getStatus());
	}

	@Test
	public void testUpgradeCPOptionValue() throws Exception {
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
	public void testUpgradeCPSpecificationOption() throws Exception {
		CPSpecificationOption cpSpecificationOption =
			CPTestUtil.addCPSpecificationOption(
				TestPropsValues.getGroupId(), true);

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update CPSpecificationOption set status = ? where " +
						"CPSpecificationOptionId = ?")) {

			preparedStatement.setInt(1, WorkflowConstants.STATUS_DENIED);
			preparedStatement.setLong(
				2, cpSpecificationOption.getCPSpecificationOptionId());

			preparedStatement.executeUpdate();
		}

		_runUpgrade();

		cpSpecificationOption =
			_cpSpecificationOptionLocalService.getCPSpecificationOption(
				cpSpecificationOption.getCPSpecificationOptionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			cpSpecificationOption.getStatus());
	}

	@Test
	public void testUpgradeCPTaxCategory() throws Exception {
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
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Inject
	private CPMeasurementUnitLocalService _cpMeasurementUnitLocalService;

	@Inject
	private CPOptionCategoryLocalService _cpOptionCategoryLocalService;

	@Inject
	private CPOptionLocalService _cpOptionLocalService;

	@Inject
	private CPOptionValueLocalService _cpOptionValueLocalService;

	@Inject
	private CPSpecificationOptionLocalService
		_cpSpecificationOptionLocalService;

	@Inject
	private CPTaxCategoryLocalService _cpTaxCategoryLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.product.internal.upgrade.registry.CommerceProductServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}