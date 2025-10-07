/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.file.system.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.store.test.util.BaseStoreTestCase;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class FileSystemStoreTest extends BaseStoreTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

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
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(_configuration);

		FileUtil.deltree(_rootDir);
	}

	@Test
	public void testDeleteDirectoryWithSlash() throws Exception {
		_testDeleteDirectoryWithSlash();
	}

	@Test
	public void testDeleteDirectoryWithSlashWithSymbolicLinkRootDir()
		throws Exception {

		Files.move(
			Paths.get(_rootDir), Paths.get(_rootDir + "-target"),
			StandardCopyOption.REPLACE_EXISTING);

		Files.createSymbolicLink(
			Paths.get(_rootDir), Paths.get(_rootDir + "-target"));

		Assert.assertTrue(Files.isSymbolicLink(Paths.get(_rootDir)));

		File targetRootDir = new File(_rootDir + "-target");

		Assert.assertTrue(targetRootDir.exists());
		Assert.assertTrue(targetRootDir.isDirectory());

		File[] targetRootDirFiles = targetRootDir.listFiles();

		Assert.assertTrue(targetRootDirFiles.length > 0);

		try {
			_testDeleteDirectoryWithSlash();
		}
		finally {
			Files.move(
				Paths.get(_rootDir + "-target"), Paths.get(_rootDir),
				StandardCopyOption.REPLACE_EXISTING);
		}
	}

	@Override
	protected Store getStore() {
		return _store;
	}

	private void _testDeleteDirectoryWithSlash() throws Exception {
		File rootDirFile = new File(_rootDir);

		Assert.assertTrue(rootDirFile.exists());
		Assert.assertTrue(rootDirFile.isDirectory());

		File[] originalFiles = rootDirFile.listFiles();

		Assert.assertTrue(originalFiles.length > 0);

		long companyId = RandomTestUtil.nextLong();
		long repositoryId = RandomTestUtil.nextLong();

		_store.addFile(
			companyId, repositoryId, "testFile", Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(new byte[] {0}));

		File testFile = new File(
			_rootDir,
			StringBundler.concat(
				companyId, StringPool.SLASH, repositoryId, "/testFile"));

		Assert.assertTrue(testFile.exists());

		_store.deleteDirectory(companyId, repositoryId, StringPool.SLASH);

		Assert.assertArrayEquals(originalFiles, rootDirFile.listFiles());
	}

	private static Configuration _configuration;

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	private static String _rootDir;

	@Inject(
		filter = "store.type=com.liferay.portal.store.file.system.FileSystemStore",
		type = Store.class
	)
	private Store _store;

}