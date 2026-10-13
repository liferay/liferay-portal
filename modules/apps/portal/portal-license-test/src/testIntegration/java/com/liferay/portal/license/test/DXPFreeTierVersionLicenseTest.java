/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.log.LogEntry;

import java.lang.reflect.Field;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class DXPFreeTierVersionLicenseTest extends BaseLicenseTestCase {

	public static void assume() {
		Assume.assumeTrue(isReleaseBundle() && !isCMS());
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		_disableKeyValidatorSafeCloseable = disableValidateWithSafeCloseable();

		_propertiesField = findField("properties.field");
	}

	@AfterClass
	public static void tearDownClass() {
		_disableKeyValidatorSafeCloseable.close();
	}

	@Test
	public void testIgnoredPatchedVersion() throws Exception {
		_testVersion(true, true, true);
		_testVersion(true, false, true);
	}

	@Test
	public void testIgnoredVersion() throws Exception {
		String ignoredVersion = _getIgnoredVersion();

		if (Validator.isNotNull(ignoredVersion)) {
			Assert.assertEquals(ignoredVersion, getCurrentVersion());
		}
	}

	@Test
	public void testNonignoredNonpatchedVersions() throws Exception {
		_testVersion(false, true, false);
		_testVersion(false, false, false);
	}

	@Test
	public void testNonignoredPatchedVersions() throws Exception {
		_testVersion(false, true, true);
		_testVersion(false, false, true);
	}

	private String _generateRandomVersion(boolean ltsVersion, boolean patch) {
		int year = RandomTestUtil.randomInt(1000, 9999);

		int patchVersison = 0;

		if (patch) {
			patchVersison = RandomTestUtil.randomInt(1, 9999);
		}

		if (ltsVersion) {
			return StringBundler.concat(year, ".Q1.", patchVersison, " LTS");
		}

		return StringBundler.concat(
			year, ".Q", RandomTestUtil.randomInt(1, 4), ".", patchVersison);
	}

	private String _getIgnoredVersion() throws Exception {
		Properties properties = (Properties)_propertiesField.get(null);

		return properties.getProperty("license.free.tier.ignored.version");
	}

	private void _testVersion(
			boolean ignored, boolean ltsVersion, boolean patch)
		throws Exception {

		String ignoredVersion = _getIgnoredVersion();

		String version = null;

		if (ignored) {
			version = ignoredVersion;
		}
		else {
			while (true) {
				version = _generateRandomVersion(ltsVersion, patch);

				if (!Objects.equals(ignoredVersion, version)) {
					break;
				}
			}
		}

		assertLicensePropertiesNotExisted(getPortalProductId());

		assertPortalLicenseNotRegistered();

		try (SafeCloseable safeCloseable1 = setVersionWithSafeCloseable(
				version);
			SafeCloseable safeCloseable2 = resetLicenseDataWithSafeCloseble()) {

			deployFreeTierPortalLicense(Time.HOUR);

			if (!patch || ignored) {
				assertLicensePropertiesExisted(getPortalProductId());

				assertPortalLicenseRegistered();
			}
			else {
				Assert.fail(
					StringBundler.concat(
						"Unable to see error message \"License is not ",
						"suppported in ", version, "\""));
			}
		}
		catch (LogEntriesException logEntriesException) {
			List<LogEntry> logEntries = logEntriesException.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertEquals(
				throwable.getMessage(),
				"License is not suppported in " + version);
		}
	}

	private static SafeCloseable _disableKeyValidatorSafeCloseable;
	private static Field _propertiesField;

}