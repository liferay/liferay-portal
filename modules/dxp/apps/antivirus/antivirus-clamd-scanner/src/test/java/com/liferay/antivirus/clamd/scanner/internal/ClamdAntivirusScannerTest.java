/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.antivirus.clamd.scanner.internal;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lucas Miranda
 */
public class ClamdAntivirusScannerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testActivate() {
		ClamdAntivirusScanner clamdAntivirusScanner =
			new ClamdAntivirusScanner();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ClamdAntivirusScanner.class.getName(), LoggerTestUtil.WARN)) {

			clamdAntivirusScanner.activate(
				HashMapBuilder.<String, Object>put(
					"hostname", ""
				).build());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"The ClamAV hostname is blank, so scans will connect to the " +
					"loopback address",
				logEntry.getMessage());

			clamdAntivirusScanner.activate(
				HashMapBuilder.<String, Object>put(
					"hostname", RandomTestUtil.randomString()
				).build());

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());
		}
	}

}