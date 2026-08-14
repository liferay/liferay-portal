/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v6_8_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.petra.string.StringBundler;
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

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Michele Vigilante
 */
@RunWith(Arquillian.class)
public class CommerceProductStatusUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgradeCPDefinitionOptionRel() throws Exception {
		CPDefinitionOptionRel cpDefinitionOptionRel =
			_addCPDefinitionOptionRel();

		long cpDefinitionOptionRelId =
			cpDefinitionOptionRel.getCPDefinitionOptionRelId();

		_updateStatus(
			"CPDefinitionOptionRel", "CPDefinitionOptionRelId",
			cpDefinitionOptionRelId, WorkflowConstants.STATUS_DENIED);

		_runUpgrade();

		cpDefinitionOptionRel =
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRel(
				cpDefinitionOptionRelId);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			cpDefinitionOptionRel.getStatus());
	}

	@Test
	public void testUpgradeCPDefinitionOptionValueRel() throws Exception {
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_addCPDefinitionOptionValueRel();

		long cpDefinitionOptionValueRelId =
			cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId();

		_updateStatus(
			"CPDefinitionOptionValueRel", "CPDefinitionOptionValueRelId",
			cpDefinitionOptionValueRelId, WorkflowConstants.STATUS_DENIED);

		_runUpgrade();

		cpDefinitionOptionValueRel =
			_cpDefinitionOptionValueRelLocalService.
				getCPDefinitionOptionValueRel(cpDefinitionOptionValueRelId);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			cpDefinitionOptionValueRel.getStatus());
	}

	private CPDefinitionOptionRel _addCPDefinitionOptionRel() throws Exception {
		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			TestPropsValues.getGroupId());

		return CPTestUtil.addCPDefinitionOptionRel(
			TestPropsValues.getGroupId(), cpDefinition.getCPDefinitionId(),
			true, 1);
	}

	private CPDefinitionOptionValueRel _addCPDefinitionOptionValueRel()
		throws Exception {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_addCPDefinitionOptionRel();

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			cpDefinitionOptionRel.getCPDefinitionOptionValueRels();

		return cpDefinitionOptionValueRels.get(0);
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		EntityCacheUtil.clearCache();
	}

	private void _updateStatus(
			String tableName, String primaryKeyColumnName, long primaryKey,
			int status)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"update ", tableName, " set status = ? where ",
					primaryKeyColumnName, " = ?"))) {

			preparedStatement.setInt(1, status);
			preparedStatement.setLong(2, primaryKey);

			preparedStatement.executeUpdate();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.commerce.product.internal.upgrade.v6_8_0." +
			"CommerceProductStatusUpgradeProcess";

	@Inject
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	@Inject
	private CPDefinitionOptionValueRelLocalService
		_cpDefinitionOptionValueRelLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.product.internal.upgrade.registry.CommerceProductServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}