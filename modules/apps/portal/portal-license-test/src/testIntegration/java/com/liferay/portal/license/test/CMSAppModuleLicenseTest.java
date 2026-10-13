/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.log.LogEntry;

import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class CMSAppModuleLicenseTest extends BaseAppModuleLicenseTestCase {

	public static void assume() {
		Assume.assumeTrue(isReleaseBundle() && isCMS());
	}

	@Test
	public void testUnsupportedAppLicense() throws Exception {
		try (SafeCloseable safeCloseable = resetLicenseDataWithSafeCloseble()) {
			assertLicensePropertiesNotExisted(getProductId(App.DSR));

			deployAppLicense(App.DSR, Time.HOUR);

			Assert.fail();
		}
		catch (LogEntriesException logEntriesException) {
			List<LogEntry> logEntries = logEntriesException.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"DSR license validation failed", logEntry.getMessage());

			Throwable throwable = logEntry.getThrowable();

			Assert.assertEquals(
				"This product does not support the \"DSR\" app license",
				throwable.getMessage());
		}
	}

	@Override
	protected App[] getApps() {
		return new App[] {App.CMP};
	}

}