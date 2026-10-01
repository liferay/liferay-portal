/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.db.partition.test.util.BaseDBPartitionTestCase;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.data.cleanup.ResourcePermissionDataCleanupPreupgradeProcess;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge Avalos
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class ResourcePermissionDataCleanupPreupgradeProcessTest
	extends BaseDBPartitionTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseDBPartitionTestCase.setUpClass();
	}

	@Test
	public void testUpgrade() throws Exception {
		long defaultCompanyId = PortalInstancePool.getDefaultCompanyId();
		long resourcePermissionId = RandomTestUtil.randomLong();

		try {
			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					if (companyId != defaultCompanyId) {
						db.runSQL(
							StringBundler.concat(
								"insert into ResourcePermission (mvccVersion, ",
								"ctCollectionId, resourcePermissionId, ",
								"companyId, name, scope, primKey, primKeyId, ",
								"roleId, ownerId, actionIds, viewActionId) ",
								"values (0, 0, ", resourcePermissionId, ", ",
								companyId, ", '", Company.class.getName(),
								"', ", ResourceConstants.SCOPE_INDIVIDUAL,
								", '", companyId, "', ", companyId, ", ",
								RandomTestUtil.randomLong(),
								", 0, 1, [$TRUE$])"));
					}
				});

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					ResourcePermissionDataCleanupPreupgradeProcess.class.
						getName(),
					LoggerTestUtil.INFO)) {

				ResourcePermissionDataCleanupPreupgradeProcess
					resourcePermissionDataCleanupPreupgradeProcess =
						new ResourcePermissionDataCleanupPreupgradeProcess();

				resourcePermissionDataCleanupPreupgradeProcess.upgrade();

				List<String> messages = TransformUtil.transform(
					logCapture.getLogEntries(), LogEntry::getMessage);

				Assert.assertTrue(
					messages.contains(
						StringBundler.concat(
							"Skipping class name ", Company.class.getName(),
							" because Company is a view in a secondary ",
							"partition")));
				Assert.assertFalse(
					messages.contains("Table Company does not exist"));
			}
		}
		finally {
			DBPartitionUtil.forEachCompanyId(
				companyId -> db.runSQL(
					"delete from ResourcePermission where " +
						"resourcePermissionId = " + resourcePermissionId));
		}
	}

}