/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.verify.PreupgradeVerifyStoreFileSystemStructure;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.portal.verify.test.util.BaseVerifyProcessTestCase;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
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
public class PreupgradeVerifyStoreFileSystemStructureTest
	extends BaseVerifyProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_advancedFileSystemStoreConfiguration =
			_configurationAdmin.getConfiguration(
				"com.liferay.portal.store.file.system.configuration." +
					"AdvancedFileSystemStoreConfiguration",
				StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(
			_advancedFileSystemStoreConfiguration,
			HashMapDictionaryBuilder.<String, Object>put(
				"rootDir", _ROOT_DIR_ADVANCED_FILE_SYSTEM_STORE
			).build());

		_cacheEnabled = ReflectionTestUtil.getAndSetFieldValue(
			PortalInstancePool.class, "_cacheEnabled", false);
		_companyId = TestPropsValues.getCompanyId();
		_repositoryId = RandomTestUtil.nextLong();

		_fileSystemStoreConfiguration = _configurationAdmin.getConfiguration(
			"com.liferay.portal.store.file.system.configuration." +
				"FileSystemStoreConfiguration",
			StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(
			_fileSystemStoreConfiguration,
			HashMapDictionaryBuilder.<String, Object>put(
				"rootDir", _ROOT_DIR_FILE_SYSTEM_STORE
			).build());

		_upgradeDatabaseDLStorageCheckDisabledSafeCloseable =
			PropsValuesTestUtil.swapWithSafeCloseable(
				"UPGRADE_DATABASE_DL_STORAGE_CHECK_DISABLED", false);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(
			_advancedFileSystemStoreConfiguration);

		FileUtil.deltree(_ROOT_DIR_ADVANCED_FILE_SYSTEM_STORE);

		ConfigurationTestUtil.deleteConfiguration(
			_fileSystemStoreConfiguration);

		FileUtil.deltree(_ROOT_DIR_FILE_SYSTEM_STORE);

		ReflectionTestUtil.setFieldValue(
			PortalInstancePool.class, "_cacheEnabled", _cacheEnabled);

		_upgradeDatabaseDLStorageCheckDisabledSafeCloseable.close();
	}

	@Before
	@Override
	public void setUp() throws Exception {
		CompanyLocalServiceUtil.forEachCompanyId(
			companyId -> {
				Files.createDirectories(
					Paths.get(
						_ROOT_DIR_ADVANCED_FILE_SYSTEM_STORE,
						String.valueOf(companyId)));
				Files.createDirectories(
					Paths.get(
						_ROOT_DIR_FILE_SYSTEM_STORE,
						String.valueOf(companyId)));
			},
			PortalInstancePool.getCompanyIds());
	}

	@After
	public void tearDown() {
		FileUtil.deltree(_ROOT_DIR_ADVANCED_FILE_SYSTEM_STORE);
		FileUtil.deltree(_ROOT_DIR_FILE_SYSTEM_STORE);
	}

	@Test
	public void testVerifyAdvancedFileSystem() throws Exception {
		File file = _mkdirs(
			_ROOT_DIR_ADVANCED_FILE_SYSTEM_STORE, _companyId, _repositoryId,
			"100");

		_testVerify(
			true, null,
			StringBundler.concat(
				"File ", file, " name has more than 2 characters and no ",
				"extension in advanced file system structure"));

		_mkdirs(
			_ROOT_DIR_ADVANCED_FILE_SYSTEM_STORE, _companyId, _repositoryId,
			"10", "100.afsh");

		_testVerify(true, null, null);
	}

	@Test
	public void testVerifyFileSystem() throws Exception {
		FileUtil.deltree(_ROOT_DIR_FILE_SYSTEM_STORE + "/" + _companyId);

		_testVerify(
			false,
			StringBundler.concat(
				"Missing directories in ", _ROOT_DIR_FILE_SYSTEM_STORE,
				" for companies: [", _companyId, "]"),
			null);

		FileUtil.deltree(
			_ROOT_DIR_FILE_SYSTEM_STORE + "/" +
				PortalInstancePool.getCompanyIds()[0]);

		File file = _touch(
			_mkdirs(_ROOT_DIR_FILE_SYSTEM_STORE),
			PortalInstancePool.getCompanyIds()[0]);

		_testVerify(false, file + " is not a directory", null);

		file = _touch(
			_mkdirs(_ROOT_DIR_FILE_SYSTEM_STORE, _companyId, _repositoryId),
			RandomTestUtil.randomString());

		_testVerify(
			false, null,
			"Unexpected file " + file + " in file system structure");

		file = _touch(
			_mkdirs(_ROOT_DIR_FILE_SYSTEM_STORE, _companyId),
			RandomTestUtil.randomString());

		_testVerify(
			false, null,
			"Unexpected file " + file + " in file system structure");

		file = _touch(
			_mkdirs(
				_ROOT_DIR_FILE_SYSTEM_STORE, _companyId, _repositoryId, "100"),
			RandomTestUtil.randomString());

		_testVerify(
			false, null,
			"Unexpected file " + file + " not matching version label pattern");

		file = _mkdirs(
			_ROOT_DIR_FILE_SYSTEM_STORE, _companyId, _repositoryId, "100.txt");

		_testVerify(
			false, null,
			StringBundler.concat(
				"Unexpected file name directory ", file,
				" with extension in file system structure"));

		_mkdirs(
			_ROOT_DIR_FILE_SYSTEM_STORE, _companyId, _repositoryId, "100",
			"1.0");

		_testVerify(false, null, null);

		_touch(
			_mkdirs(
				_ROOT_DIR_FILE_SYSTEM_STORE, _companyId, _repositoryId, "100"),
			DLFileEntryConstants.PRIVATE_WORKING_COPY_VERSION +
				StringPool.TILDE + String.valueOf(UUID.randomUUID()));

		_testVerify(false, null, null);
	}

	@Override
	protected VerifyProcess getVerifyProcess() {
		return new PreupgradeVerifyStoreFileSystemStructure();
	}

	private void _assertLogCapture(
		String expectedLogEntryMessage, LogCapture logCapture) {

		List<LogEntry> logEntries = logCapture.getLogEntries();

		if (expectedLogEntryMessage == null) {
			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());

			return;
		}

		Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

		LogEntry logEntry = logEntries.get(0);

		Assert.assertEquals(expectedLogEntryMessage, logEntry.getMessage());
	}

	private File _mkdirs(Object... objects) {
		StringBundler sb = new StringBundler();

		for (Object object : objects) {
			sb.append(object);
			sb.append("/");
		}

		File file = new File(sb.toString());

		FileUtil.mkdirs(file);

		return file;
	}

	private void _testVerify(
			boolean advancedFileSystemStore, String expectedExceptionMessage,
			String expectedLogEntryMessage)
		throws Exception {

		String dlStoreImpl = ReflectionTestUtil.getAndSetFieldValue(
			PropsValues.class, "DL_STORE_IMPL",
			advancedFileSystemStore ?
				"com.liferay.portal.store.file.system.AdvancedFileSystemStore" :
					"com.liferay.portal.store.file.system.FileSystemStore");
		LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
			PreupgradeVerifyStoreFileSystemStructure.class.getName(),
			LoggerTestUtil.ERROR);

		try {
			testVerify();

			_assertLogCapture(expectedLogEntryMessage, logCapture);

			if ((expectedExceptionMessage != null) ||
				(expectedLogEntryMessage != null)) {

				Assert.fail();
			}
		}
		catch (Exception exception) {
			if (expectedExceptionMessage == null) {
				expectedExceptionMessage = StringBundler.concat(
					advancedFileSystemStore ? "Advanced file" : "File",
					" system store directory structure ",
					advancedFileSystemStore ?
						_ROOT_DIR_ADVANCED_FILE_SYSTEM_STORE :
							_ROOT_DIR_FILE_SYSTEM_STORE,
					" is invalid");
			}

			Assert.assertEquals(
				expectedExceptionMessage, exception.getMessage());

			_assertLogCapture(expectedLogEntryMessage, logCapture);
		}
		finally {
			logCapture.close();

			ReflectionTestUtil.setFieldValue(
				PropsValues.class, "DL_STORE_IMPL", dlStoreImpl);

			FileUtil.deltree(_ROOT_DIR_ADVANCED_FILE_SYSTEM_STORE);
			FileUtil.deltree(_ROOT_DIR_FILE_SYSTEM_STORE);

			CompanyLocalServiceUtil.forEachCompanyId(
				companyId -> {
					Files.createDirectories(
						Paths.get(
							_ROOT_DIR_ADVANCED_FILE_SYSTEM_STORE,
							String.valueOf(companyId)));
					Files.createDirectories(
						Paths.get(
							_ROOT_DIR_FILE_SYSTEM_STORE,
							String.valueOf(companyId)));
				},
				PortalInstancePool.getCompanyIds());
		}
	}

	private File _touch(File file, Object fileName) throws Exception {
		file = new File(file, String.valueOf(fileName));

		FileUtil.touch(file);

		return file;
	}

	private static final String _ROOT_DIR_ADVANCED_FILE_SYSTEM_STORE =
		Paths.get(
			PropsUtil.get(PropsKeys.LIFERAY_HOME), "test", "store",
			"advanced_file_system"
		).toString();

	private static final String _ROOT_DIR_FILE_SYSTEM_STORE = Paths.get(
		PropsUtil.get(PropsKeys.LIFERAY_HOME), "test", "store", "file_system"
	).toString();

	private static Configuration _advancedFileSystemStoreConfiguration;
	private static boolean _cacheEnabled;
	private static long _companyId;

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	private static Configuration _fileSystemStoreConfiguration;
	private static long _repositoryId;
	private static SafeCloseable
		_upgradeDatabaseDLStorageCheckDisabledSafeCloseable;

}