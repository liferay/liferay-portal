/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Brittney Nguyen
 */
public class MonitorFactoryTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testNewMonitorUnknownTypeFailsLoud() {
		MonitorConfig monitorConfig = new MonitorConfig(
			"a", "unknown-type", MonitorConfig.Severity.MEDIUM, 0, null, null);

		try {
			MonitorFactory.newMonitor(monitorConfig);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

}