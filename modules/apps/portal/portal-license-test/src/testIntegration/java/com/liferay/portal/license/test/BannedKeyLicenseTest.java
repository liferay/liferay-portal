/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.log.LogEntry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class BannedKeyLicenseTest extends BaseLicenseTestCase {

	public static void assume() {
		Assume.assumeTrue(isReleaseBundle());
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		Class<?> keyGeneratorClass = classLoader.loadClass(
			getProperty("key.generator.class"));

		for (Method method : keyGeneratorClass.getDeclaredMethods()) {
			Class<?>[] parameterTypes = method.getParameterTypes();

			if ((parameterTypes.length == 1) &&
				Map.class.isAssignableFrom(parameterTypes[0])) {

				method.setAccessible(true);

				_encryptMethod = method;

				break;
			}
		}

		Class<?> validateClass = getValidateClass();

		for (Field field : validateClass.getDeclaredFields()) {
			if (Set.class.isAssignableFrom(field.getType())) {
				field.setAccessible(true);

				_bannedKeysField = field;

				break;
			}
		}
	}

	@Test
	public void testBannedKeys() throws Exception {
		String[] bannedKeys = ArrayUtil.toStringArray(
			(Set<String>)_bannedKeysField.get(getValidateClass()));

		Assert.assertEquals(Arrays.toString(bannedKeys), 88, bannedKeys.length);

		String bannedKey = bannedKeys[bannedKeys.length - 1];

		try (SafeCloseable safeCloseable1 = setVersionWithSafeCloseable(
				"2026.Q1.0 LTS");
			SafeCloseable safeCloseable2 = resetLicenseDataWithSafeCloseble();
			SafeCloseable safeCloseable3 = setReturnValueWithSafeCloseable(
				_encryptMethod, bannedKey)) {

			deployEnterprisePortalLicense(bannedKey, Time.HOUR);

			Assert.fail(
				"Unable to see error message \"Corrupt license file. License " +
					"was not registered\"");
		}
		catch (LogEntriesException logEntriesException) {
			List<LogEntry> logEntries = logEntriesException.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			String message = logEntry.getMessage();

			Assert.assertTrue(
				message,
				message.startsWith(
					"Corrupt license file. License was not registered"));
		}
	}

	@Test
	public void testNonbannedKeys() throws Exception {
		String key = RandomTestUtil.randomString(8);

		Set<String> bannedKeys = (Set<String>)_bannedKeysField.get(
			getValidateClass());

		Assert.assertFalse(bannedKeys.toString(), bannedKeys.contains(key));

		try (SafeCloseable safeCloseable1 = setVersionWithSafeCloseable(
				"2026.Q1.0 LTS");
			SafeCloseable safeCloseable2 = resetLicenseDataWithSafeCloseble()) {

			deployEnterprisePortalLicense(key, Time.HOUR);

			Assert.fail(
				"Unable to see error message \"Corrupt license file. License " +
					"was not registered\"");
		}
		catch (LogEntriesException logEntriesException) {
			List<LogEntry> logEntries = logEntriesException.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			String message = logEntry.getMessage();

			Assert.assertTrue(
				message,
				message.startsWith(
					"Corrupt license file. License was not registered"));
		}

		try (SafeCloseable safeCloseable1 = setVersionWithSafeCloseable(
				"2026.Q1.0 LTS");
			SafeCloseable safeCloseable2 = resetLicenseDataWithSafeCloseble();
			SafeCloseable safeCloseable3 = setReturnValueWithSafeCloseable(
				_encryptMethod, key)) {

			deployEnterprisePortalLicense(key, Time.HOUR);

			assertPortalLicenseRegistered();
		}
	}

	private static Field _bannedKeysField;
	private static Method _encryptMethod;

}