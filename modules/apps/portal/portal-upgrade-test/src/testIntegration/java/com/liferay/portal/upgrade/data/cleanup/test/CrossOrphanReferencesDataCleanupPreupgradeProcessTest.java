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
import com.liferay.portal.kernel.upgrade.data.cleanup.CrossOrphanReferencesDataCleanupPreupgradeProcess;
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
public class CrossOrphanReferencesDataCleanupPreupgradeProcessTest
	extends BaseOrphanReferencesDataCleanupPreupgradeProcessTestCase {

	@Before
	public void setUp() {
		_companyId1 = RandomTestUtil.nextLong();
		_companyId2 = RandomTestUtil.nextLong();
	}

	@Override
	protected UnsafeRunnable<Exception> getInsertDataRunnable() {
		return () -> {
			_insertEntry(_companyId1);
			_insertEntry(_companyId1);
			_insertEntry(_companyId2);
		};
	}

	@Override
	protected UnsafeConsumer<LogCapture, Exception> getLogAssertionConsumer() {
		return logCapture -> {
			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 4, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				getExpectedMessage(
					2, "Image", "companyId", "Company", _companyId1),
				logEntry.getMessage());

			logEntry = logEntries.get(1);

			Assert.assertEquals(
				getExpectedMessage(
					1, "Image", "companyId", "Company", _companyId2),
				logEntry.getMessage());

			logEntry = logEntries.get(2);

			Assert.assertEquals(
				getExpectedMessage(
					2, "Portlet", "companyId", "Company", _companyId1),
				logEntry.getMessage());

			logEntry = logEntries.get(3);

			Assert.assertEquals(
				getExpectedMessage(
					1, "Portlet", "companyId", "Company", _companyId2),
				logEntry.getMessage());
		};
	}

	@Override
	protected UpgradeProcess getUpgradeProcess() {
		return new CrossOrphanReferencesDataCleanupPreupgradeProcess(
			"companyId", "Company");
	}

	private void _insertEntry(long companyId) throws Exception {
		db.runSQL(
			connection,
			StringBundler.concat(
				"insert into Image (mvccVersion, ctCollectionId, imageId, ",
				"companyId) values (0, 0, ", RandomTestUtil.nextLong(), ", ",
				companyId, ")"));

		db.runSQL(
			connection,
			StringBundler.concat(
				"insert into Portlet (mvccVersion, id_, companyId, portletId, ",
				"active_) values (0, ", RandomTestUtil.nextLong(), ", ",
				companyId, ", '", RandomTestUtil.randomString(),
				"', [$FALSE$])"));
	}

	private long _companyId1;
	private long _companyId2;

}