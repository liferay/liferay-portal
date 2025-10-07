/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.verify.PreupgradeVerifyStoreAccess;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.portal.verify.test.util.BaseVerifyProcessTestCase;

import java.io.FileNotFoundException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class PreupgradeVerifyStoreAccessTest extends BaseVerifyProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeTrue(
			com.liferay.portal.kernel.util.StringUtil.equals(
				PropsValues.DL_STORE_IMPL,
				"com.liferay.portal.store.file.system." +
					"AdvancedFileSystemStore") ||
			com.liferay.portal.kernel.util.StringUtil.equals(
				PropsValues.DL_STORE_IMPL,
				"com.liferay.portal.store.file.system.FileSystemStore"));
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		_rootDir =
			PropsUtil.get(PropsKeys.LIFERAY_HOME) + "/test/store/file_system";

		_configuration = _configurationAdmin.getConfiguration(
			"com.liferay.portal.store.file.system.configuration." +
				"FileSystemStoreConfiguration",
			StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(
			_configuration,
			HashMapDictionaryBuilder.<String, Object>put(
				"rootDir", _rootDir
			).build());

		_upgradeDatabaseDLStorageCheckDisabledSafeCloseable =
			PropsValuesTestUtil.swapWithSafeCloseable(
				"UPGRADE_DATABASE_DL_STORAGE_CHECK_DISABLED", false);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(_configuration);

		FileUtil.deltree(_rootDir);

		_upgradeDatabaseDLStorageCheckDisabledSafeCloseable.close();
	}

	@Test
	public void testVerifyNonexistentRootDirectory() throws Exception {
		Path originalRootDirPath = Paths.get(_rootDir);

		Path renamedRootDirPath = Paths.get(
			StringUtil.replace(_rootDir, "file_system", "file_system2"));

		if (Files.exists(originalRootDirPath)) {
			Files.move(originalRootDirPath, renamedRootDirPath);
		}

		Files.createFile(originalRootDirPath);

		try {
			testVerify();

			Assert.fail();
		}
		catch (Exception exception) {
			String message = exception.getMessage();

			Assert.assertTrue(
				message.contains(FileNotFoundException.class.getName()));
		}
		finally {
			Files.deleteIfExists(originalRootDirPath);

			if (Files.exists(renamedRootDirPath)) {
				Files.move(renamedRootDirPath, originalRootDirPath);
			}
		}
	}

	@Override
	protected VerifyProcess getVerifyProcess() {
		return new PreupgradeVerifyStoreAccess();
	}

	private static Configuration _configuration;

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	private static String _rootDir;
	private static SafeCloseable
		_upgradeDatabaseDLStorageCheckDisabledSafeCloseable;

}