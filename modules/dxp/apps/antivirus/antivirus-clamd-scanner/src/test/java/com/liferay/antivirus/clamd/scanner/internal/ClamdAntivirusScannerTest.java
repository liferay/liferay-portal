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
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ClamdAntivirusScanner.class.getName(), LoggerTestUtil.ERROR)) {

			ClamdAntivirusScanner clamdAntivirusScanner =
				new ClamdAntivirusScanner();

			clamdAntivirusScanner.activate(
				HashMapBuilder.<String, Object>put(
					"hostname", ""
				).build());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Set the ClamAV hostname because a blank hostname connects " +
					"to the loopback address",
				logEntry.getMessage());

			logEntries.clear();

			clamdAntivirusScanner.activate(
				HashMapBuilder.<String, Object>put(
					"hostname", RandomTestUtil.randomString()
				).build());

			Assert.assertTrue(logEntries.isEmpty());
		}
	}

}