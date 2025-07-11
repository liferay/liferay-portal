/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.BaseOrphanReferencesDataCleanupPreupgradeProcess;
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
public class BaseOrphanReferencesDataCleanupPreupgradeProcessTest
	extends BaseOrphanReferencesDataCleanupPreupgradeProcessTestCase {

	@Before
	public void setUp() {
		_companyId1 = RandomTestUtil.nextLong();
		_companyId2 = RandomTestUtil.nextLong();
	}

	@Override
	protected UnsafeRunnable<Exception> getInsertDataRunnable() {
		return () -> {
			_insertEntry(_companyId1, PortletKeys.PREFS_OWNER_TYPE_COMPANY);
			_insertEntry(_companyId1, PortletKeys.PREFS_OWNER_TYPE_GROUP);
			_insertEntry(_companyId2, PortletKeys.PREFS_OWNER_TYPE_COMPANY);
		};
	}

	@Override
	protected UnsafeConsumer<LogCapture, Exception> getLogAssertionConsumer() {
		return logCapture -> {
			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 2, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				getExpectedMessage(
					1, "PortletPreferences", "companyId", "Company",
					_companyId1),
				logEntry.getMessage());

			logEntry = logEntries.get(1);

			Assert.assertEquals(
				getExpectedMessage(
					1, "PortletPreferences", "companyId", "Company",
					_companyId2),
				logEntry.getMessage());
		};
	}

	@Override
	protected UpgradeProcess getUpgradeProcess() {
		return new BaseOrphanReferencesDataCleanupPreupgradeProcess(
			"ownerType = " + PortletKeys.PREFS_OWNER_TYPE_COMPANY, "ownerId",
			"PortletPreferences", "companyId", "Company");
	}

	private void _insertEntry(long companyId, int ownerType) throws Exception {
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