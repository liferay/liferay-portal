/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.util;

import com.liferay.knowledge.base.configuration.KBGroupServiceConfiguration;
import com.liferay.knowledge.base.exception.KBArticleImportException;
import com.liferay.knowledge.base.internal.importer.KBArchive;
import com.liferay.knowledge.base.internal.importer.util.KBArchiveFactoryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.SettingsLocator;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Adolfo Pérez
 */
public class KBArchiveFactoryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		Mockito.doReturn(
			_kbGroupServiceConfiguration
		).when(
			_configurationProvider
		).getConfiguration(
			Mockito.any(), Mockito.any(SettingsLocator.class)
		);

		Mockito.when(
			_configurationProvider.getConfiguration(
				KBGroupServiceConfiguration.class, _groupServiceSettingsLocator)
		).thenReturn(
			_kbGroupServiceConfiguration
		);

		Mockito.when(
			_kbGroupServiceConfiguration.markdownImporterArticleExtensions()
		).thenReturn(
			new String[] {".md"}
		);

		Mockito.when(
			_kbGroupServiceConfiguration.markdownImporterArticleIntro()
		).thenReturn(
			"intro.md"
		);
	}

	@Test
	public void testEmptyZipReaderReturnsNoFolders() throws Exception {
		Mockito.when(
			_zipReader.getEntries()
		).thenReturn(
			Collections.<String>emptyList()
		);

		KBArchive kbArchive = KBArchiveFactoryUtil.createKBArchive(
			_configurationProvider, 1234L, _zipReader);

		Collection<KBArchive.Folder> folders = kbArchive.getFolders();

		Assert.assertTrue(folders.toString(), folders.isEmpty());
	}

	@Test
	public void testZipReaderWithALoneIntroFileReturnsFolder()
		throws Exception {

		Mockito.when(
			_zipReader.getEntries()
		).thenReturn(
			Arrays.asList("/intro.md")
		);

		KBArchive kbArchive = KBArchiveFactoryUtil.createKBArchive(
			_configurationProvider, 1234L, _zipReader);

		Collection<KBArchive.Folder> folders = kbArchive.getFolders();

		Iterator<KBArchive.Folder> iterator = folders.iterator();

		KBArchive.Folder folder = iterator.next();

		Assert.assertEquals(StringPool.SLASH, folder.getName());

		KBArchive.File introFile = folder.getIntroFile();

		Assert.assertEquals("/intro.md", introFile.getName());
	}

	@Test
	public void testZipReaderWithIntroFileAndArticleFiles() throws Exception {
		Mockito.when(
			_zipReader.getEntries()
		).thenReturn(
			Arrays.asList("/intro.md", "/x/a.md", "/y/z/b.md", "/y/z/c.md")
		);

		KBArchive kbArchive = KBArchiveFactoryUtil.createKBArchive(
			_configurationProvider, 1234L, _zipReader);

		Collection<KBArchive.Folder> folders = kbArchive.getFolders();

		Assert.assertEquals(folders.toString(), 3, folders.size());

		Iterator<KBArchive.Folder> folderIterator = folders.iterator();

		KBArchive.Folder folder0 = folderIterator.next();

		Assert.assertEquals(StringPool.SLASH, folder0.getName());

		KBArchive.File introFile0 = folder0.getIntroFile();

		Assert.assertEquals("/intro.md", introFile0.getName());

		Assert.assertNull(folder0.getParentFolderIntroFile());

		Collection<KBArchive.File> files0 = folder0.getFiles();

		Assert.assertTrue(files0.isEmpty());

		KBArchive.Folder folder1 = folderIterator.next();

		Assert.assertEquals("/x", folder1.getName());
		Assert.assertNull(folder1.getIntroFile());
		Assert.assertNotNull(folder1.getParentFolderIntroFile());

		Collection<KBArchive.File> files1 = folder1.getFiles();

		Assert.assertEquals(files1.toString(), 1, files1.size());

		KBArchive.Folder folder2 = folderIterator.next();

		Assert.assertEquals("/y/z", folder2.getName());
		Assert.assertNull(folder2.getIntroFile());
		Assert.assertNull(folder2.getParentFolderIntroFile());

		Collection<KBArchive.File> files2 = folder2.getFiles();

		Assert.assertEquals(files2.toString(), 2, files2.size());
	}

	@Test
	@TestInfo("LPD-107698")
	public void testZipReaderWithIntroFileInAnotherArticleExtension()
		throws Exception {

		Mockito.when(
			_kbGroupServiceConfiguration.markdownImporterArticleExtensions()
		).thenReturn(
			new String[] {".markdown", ".md"}
		);

		Mockito.when(
			_kbGroupServiceConfiguration.markdownImporterArticleIntro()
		).thenReturn(
			"intro.markdown"
		);

		Mockito.when(
			_zipReader.getEntries()
		).thenReturn(
			Arrays.asList("/x/00-intro.md", "/x/01-a.md", "/y/intro.markdown")
		);

		KBArchive kbArchive = KBArchiveFactoryUtil.createKBArchive(
			_configurationProvider, 1234L, _zipReader);

		Collection<KBArchive.Folder> folders = kbArchive.getFolders();

		Assert.assertEquals(folders.toString(), 2, folders.size());

		Iterator<KBArchive.Folder> folderIterator = folders.iterator();

		KBArchive.Folder folder0 = folderIterator.next();

		Assert.assertEquals("/x", folder0.getName());

		KBArchive.File introFile0 = folder0.getIntroFile();

		Assert.assertEquals("/x/00-intro.md", introFile0.getName());

		Collection<KBArchive.File> files0 = folder0.getFiles();

		Assert.assertEquals(files0.toString(), 1, files0.size());

		KBArchive.Folder folder1 = folderIterator.next();

		Assert.assertEquals("/y", folder1.getName());

		KBArchive.File introFile1 = folder1.getIntroFile();

		Assert.assertEquals("/y/intro.markdown", introFile1.getName());
	}

	@Test(expected = KBArticleImportException.class)
	public void testZipReaderWithNoEntriesFails() throws Exception {
		Mockito.when(
			_zipReader.getEntries()
		).thenReturn(
			null
		);

		KBArchiveFactoryUtil.createKBArchive(
			_configurationProvider, 1234L, _zipReader);
	}

	@Test
	public void testZipReaderWithNoIntroFileReturnsFolder() throws Exception {
		Mockito.when(
			_zipReader.getEntries()
		).thenReturn(
			Arrays.asList("/a.md", "/b.md")
		);

		KBArchive kbArchive = KBArchiveFactoryUtil.createKBArchive(
			_configurationProvider, 1234L, _zipReader);

		Collection<KBArchive.Folder> folders = kbArchive.getFolders();

		Assert.assertEquals(folders.toString(), 1, folders.size());

		Iterator<KBArchive.Folder> foldersIterator = folders.iterator();

		KBArchive.Folder folder = foldersIterator.next();

		Assert.assertNull(folder.getIntroFile());

		Collection<KBArchive.File> files = folder.getFiles();

		Assert.assertEquals(files.toString(), 2, files.size());

		Iterator<KBArchive.File> filesIterator = files.iterator();

		KBArchive.File file0 = filesIterator.next();

		Assert.assertEquals("/a.md", file0.getName());

		KBArchive.File file1 = filesIterator.next();

		Assert.assertEquals("/b.md", file1.getName());
	}

	@Test
	public void testZipReaderWithNoValidFilesReturnsNoFolders()
		throws Exception {

		Mockito.when(
			_zipReader.getEntries()
		).thenReturn(
			Arrays.asList("/a.txt", "/b.txt")
		);

		KBArchive kbArchive = KBArchiveFactoryUtil.createKBArchive(
			_configurationProvider, 1234L, _zipReader);

		Collection<KBArchive.Folder> folders = kbArchive.getFolders();

		Assert.assertTrue(folders.toString(), folders.isEmpty());
	}

	private final ConfigurationProvider _configurationProvider = Mockito.mock(
		ConfigurationProvider.class);
	private final GroupServiceSettingsLocator _groupServiceSettingsLocator =
		Mockito.mock(GroupServiceSettingsLocator.class);
	private final KBGroupServiceConfiguration _kbGroupServiceConfiguration =
		Mockito.mock(KBGroupServiceConfiguration.class);
	private final ZipReader _zipReader = Mockito.mock(ZipReader.class);

}