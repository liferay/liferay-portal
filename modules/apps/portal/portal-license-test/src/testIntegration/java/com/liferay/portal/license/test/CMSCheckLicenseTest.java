/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.util.LicenseUtil;

import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class CMSCheckLicenseTest extends BaseCheckLicenseTestCase {

	public static void assume() {
		Assume.assumeTrue(isReleaseBundle() && isCMS());
	}

	@Override
	protected App[] getApps() {
		return new App[] {App.CMP};
	}

	@Override
	protected void testCheckLicense(String fileName) throws Exception {
		Path target = Path.of(LicenseUtil.LICENSE_REPOSITORY_DIR, fileName);

		try (InputStream inputStream =
				CMSCheckLicenseTest.class.getResourceAsStream(
					"dependencies/" + fileName)) {

			Files.createDirectories(target.getParent());

			Files.copy(
				inputStream, target, StandardCopyOption.REPLACE_EXISTING);
		}

		assertLicensePropertiesNotExisted(getPortalProductId());

		try {
			checkLicense(getPortalProductId());

			Assert.fail();
		}
		catch (LogEntriesException logEntriesException) {
			List<LogEntry> logEntries = logEntriesException.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			String message = logEntry.getMessage();

			Assert.assertTrue(
				message, message.endsWith(" license validation failed"));

			String productName = message.substring(
				0, message.indexOf(" license validation failed"));

			Throwable throwable = logEntry.getThrowable();

			Assert.assertEquals(
				StringBundler.concat(
					"This product does not support the \"", productName,
					"\" portal license"),
				throwable.getMessage());
		}
	}

}