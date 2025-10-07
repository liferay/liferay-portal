/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.TableOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.OrphanReferencesDataCleanupUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class TableOrphanReferencesDataCleanupPreupgradeProcessTest
	extends BaseOrphanReferencesDataCleanupPreupgradeProcessTestCase {

	@Before
	public void setUp() throws Exception {
		_companyId1 = RandomTestUtil.nextLong();
		_companyId2 = RandomTestUtil.nextLong();

		db.runSQL(
			connection,
			StringBundler.concat(
				"delete from PortletPreferences where not exists (select 1 ",
				"from Company where Company.companyID = PortletPreferences.",
				"ownerId) and ownerId is not null and ownerId != 0 and ",
				"ownerType = ", PortletKeys.PREFS_OWNER_TYPE_COMPANY));
	}

	@Override
	protected UnsafeRunnable<Exception> getInsertDataUnsafeRunnable() {
		return () -> {
			_insert(_companyId1, PortletKeys.PREFS_OWNER_TYPE_COMPANY);
			_insert(_companyId1, PortletKeys.PREFS_OWNER_TYPE_GROUP);
			_insert(_companyId2, PortletKeys.PREFS_OWNER_TYPE_COMPANY);
		};
	}

	@Override
	protected UnsafeBiConsumer<LogCapture, LogCapture, Exception>
		getLogAssertionUnsafeBiConsumer() {

		return (logCapture1, logCapture2) -> {
			List<LogEntry> logEntries = logCapture1.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 2, logEntries.size());

			List<String> messages = logCapture1.getMessages();

			Assert.assertTrue(
				messages.contains(
					getExpectedMessage(
						1, "ownerId", "PortletPreferences",
						new String[] {"companyId"}, "Company", _companyId1)));
			Assert.assertTrue(
				messages.contains(
					getExpectedMessage(
						1, "ownerId", "PortletPreferences",
						new String[] {"companyId"}, "Company", _companyId2)));
		};
	}

	@Override
	protected String getLoggerClassName() {
		return OrphanReferencesDataCleanupUtil.class.getName();
	}

	@Override
	protected UpgradeProcess getUpgradeProcess() {
		return new TableOrphanReferencesDataCleanupPreupgradeProcess(
			"ownerType = " + PortletKeys.PREFS_OWNER_TYPE_COMPANY, "ownerId",
			"PortletPreferences", "companyId", "Company");
	}

	private void _insert(long companyId, int ownerType) throws Exception {
		db.runSQL(
			connection,
			StringBundler.concat(
				"insert into PortletPreferences (mvccVersion, ctCollectionId, ",
				"portletPreferencesId, ownerId, ownerType, companyId) values ",
				"(0, 0, ", RandomTestUtil.nextLong(), ", ", companyId, ", ",
				ownerType, ", ", companyId, ")"));
	}

	private long _companyId1;
	private long _companyId2;

}