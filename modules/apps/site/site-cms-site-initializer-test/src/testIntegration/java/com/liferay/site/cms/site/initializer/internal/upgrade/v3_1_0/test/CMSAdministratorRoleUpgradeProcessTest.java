/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.v3_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mario Gomes
 */
@RunWith(Arquillian.class)
public class CMSAdministratorRoleUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		Role cmsAdministratorRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.CMS_ADMINISTRATOR);

		String externalReferenceCode = RandomTestUtil.randomString();

		cmsAdministratorRole.setExternalReferenceCode(externalReferenceCode);

		cmsAdministratorRole = _roleLocalService.updateRole(
			cmsAdministratorRole);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		role.setExternalReferenceCode(_EXTERNAL_REFERENCE_CODE);

		role = _roleLocalService.updateRole(role);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.WARN)) {

			_upgrade();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Unable to assign the external reference code \"",
					_EXTERNAL_REFERENCE_CODE, "\" to the \"",
					RoleConstants.CMS_ADMINISTRATOR, "\" role in company ",
					cmsAdministratorRole.getCompanyId(),
					" because another role already uses it"),
				logEntry.getMessage());
		}

		cmsAdministratorRole = _roleLocalService.getRole(
			cmsAdministratorRole.getRoleId());

		Assert.assertEquals(
			externalReferenceCode,
			cmsAdministratorRole.getExternalReferenceCode());

		_roleLocalService.deleteRole(role);

		_upgrade();

		cmsAdministratorRole = _roleLocalService.getRole(
			cmsAdministratorRole.getRoleId());

		Assert.assertEquals(
			_EXTERNAL_REFERENCE_CODE,
			cmsAdministratorRole.getExternalReferenceCode());
	}

	private void _upgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private static final String _CLASS_NAME =
		"com.liferay.site.cms.site.initializer.internal.upgrade.v3_1_0." +
			"CMSAdministratorRoleUpgradeProcess";

	private static final String _EXTERNAL_REFERENCE_CODE =
		RoleConstants.toSystemRoleExternalReferenceCode(
			RoleConstants.CMS_ADMINISTRATOR);

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.site.cms.site.initializer.internal.upgrade.registry.SiteCMSSiteInitializerUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}