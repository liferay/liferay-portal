/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.test.util;

import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLTrashLocalServiceUtil;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.ExportImportLocalServiceUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portlet.documentlibrary.store.DLStoreImpl;

import java.io.File;
import java.io.InputStream;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Preston Crary
 */
public abstract class BaseStoreTestCase {

	@Before
	public void setUp() {
		_companyId = RandomTestUtil.nextLong();
		_repositoryId = RandomTestUtil.nextLong();

		_store = getStore();
	}

	@After
	public void tearDown() throws PortalException {
		_store.deleteDirectory(_companyId);
	}

	@Test
	public void testAddFile() throws Exception {
		String fileName = RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		Assert.assertTrue(
			_store.hasFile(
				_companyId, _repositoryId, fileName, Store.VERSION_DEFAULT));
	}

	@Test
	public void testDeleteCompanyDirectory() throws Exception {
		String dirName = RandomTestUtil.randomString();

		String fileName1 = dirName + "/" + RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName1, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		long repositoryId = RandomTestUtil.nextLong();

		String fileName2 = dirName + "/" + RandomTestUtil.randomString();

		_store.addFile(
			_companyId, repositoryId, fileName2, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		_store.deleteDirectory(_companyId);

		Assert.assertFalse(
			_store.hasFile(
				_companyId, _repositoryId, fileName1, Store.VERSION_DEFAULT));
		Assert.assertFalse(
			_store.hasFile(
				_companyId, repositoryId, fileName2, Store.VERSION_DEFAULT));
	}

	@Test
	public void testDeleteDirectory() throws Exception {
		String dirName = RandomTestUtil.randomString();

		String fileName1 = dirName + "/" + RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName1, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		String fileName2 = dirName + "/" + RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName2, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		_store.deleteDirectory(_companyId, _repositoryId, dirName);

		Assert.assertFalse(
			_store.hasFile(
				_companyId, _repositoryId, fileName1, Store.VERSION_DEFAULT));
		Assert.assertFalse(
			_store.hasFile(
				_companyId, _repositoryId, fileName2, Store.VERSION_DEFAULT));
	}

	@Test
	public void testDeleteDirectoryWithTwoLevelDeep() throws Exception {
		String dirName = RandomTestUtil.randomString();

		String subdirName = dirName + "/" + RandomTestUtil.randomString();

		String fileName1 = dirName + "/" + RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName1, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		String fileName2 = subdirName + "/" + RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName2, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		_store.deleteDirectory(_companyId, _repositoryId, dirName);

		Assert.assertFalse(
			_store.hasFile(
				_companyId, _repositoryId, fileName1, Store.VERSION_DEFAULT));
		Assert.assertFalse(
			_store.hasFile(
				_companyId, _repositoryId, fileName2, Store.VERSION_DEFAULT));
	}

	@Test
	public void testDeleteFile() throws Exception {
		String fileName = RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		addVersions(fileName, 1);

		_store.deleteFile(
			_companyId, _repositoryId, fileName, Store.VERSION_DEFAULT);

		Assert.assertFalse(
			_store.hasFile(
				_companyId, _repositoryId, fileName, Store.VERSION_DEFAULT));
		Assert.assertTrue(
			_store.hasFile(_companyId, _repositoryId, fileName, "1.1"));
	}

	@Test
	public void testDeleteTrashEntry() throws Exception {
		Store originalStore = ReflectionTestUtil.getFieldValue(
			DLStoreImpl.class, "_wrappedStore");

		DLStoreImpl.setStore(_store);

		try {
			Group group = GroupTestUtil.addGroup();

			FileEntry fileEntry = DLAppTestUtil.addFileEntry(
				group.getGroupId());

			DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

			DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

			Assert.assertTrue(
				_store.hasFile(
					dlFileEntry.getCompanyId(),
					dlFileEntry.getDataRepositoryId(), dlFileEntry.getName(),
					dlFileVersion.getStoreFileName()));

			DLTrashLocalServiceUtil.moveFileEntryToTrash(
				dlFileEntry.getUserId(), dlFileEntry.getRepositoryId(),
				dlFileEntry.getFileEntryId());

			Assert.assertTrue(
				_store.hasFile(
					dlFileEntry.getCompanyId(),
					dlFileEntry.getDataRepositoryId(), dlFileEntry.getName(),
					dlFileVersion.getStoreFileName()));

			TrashHandler trashHandler =
				TrashHandlerRegistryUtil.getTrashHandler(
					DLFileEntry.class.getName());

			trashHandler.deleteTrashEntry(dlFileEntry.getPrimaryKey());

			Assert.assertFalse(
				_store.hasFile(
					dlFileEntry.getCompanyId(),
					dlFileEntry.getDataRepositoryId(), dlFileEntry.getName(),
					dlFileVersion.getStoreFileName()));

			GroupTestUtil.deleteGroup(group);
		}
		finally {
			DLStoreImpl.setStore(originalStore);
		}
	}

	@Test
	public void testExportImportFileEntry() throws Exception {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		Store originalStore = ReflectionTestUtil.getFieldValue(
			DLStoreImpl.class, "_wrappedStore");

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		DLStoreImpl.setStore(_store);

		try {
			Group group = GroupTestUtil.addGroup();
			Group importedGroup = GroupTestUtil.addGroup();

			FileEntry fileEntry = DLAppTestUtil.addFileEntry(
				group.getGroupId());

			DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

			DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

			Assert.assertTrue(
				_store.hasFile(
					dlFileEntry.getCompanyId(),
					dlFileEntry.getDataRepositoryId(), dlFileEntry.getName(),
					dlFileVersion.getStoreFileName()));

			File larFile = _exportLayoutsAsFile(group.getGroupId());

			_importLayouts(importedGroup.getGroupId(), larFile);

			larFile.delete();

			FileEntry importedFileEntry =
				DLAppLocalServiceUtil.getFileEntryByUuidAndGroupId(
					fileEntry.getUuid(), importedGroup.getGroupId());

			DLFileEntry importedDLFileEntry =
				(DLFileEntry)importedFileEntry.getModel();

			DLFileVersion importedDLFileVersion =
				importedDLFileEntry.getFileVersion();

			Assert.assertTrue(
				_store.hasFile(
					importedDLFileEntry.getCompanyId(),
					importedDLFileEntry.getDataRepositoryId(),
					importedDLFileEntry.getName(),
					importedDLFileVersion.getStoreFileName()));

			GroupTestUtil.deleteGroup(group);
			GroupTestUtil.deleteGroup(importedGroup);
		}
		finally {
			DLStoreImpl.setStore(originalStore);

			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@Test
	public void testGetFileAsStream() throws Exception {
		String fileName = RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		addVersions(fileName, 5);

		try (InputStream inputStream = _store.getFileAsStream(
				_companyId, _repositoryId, fileName, "1.5")) {

			for (int i = 0; i < _DATA_SIZE; i++) {
				Assert.assertEquals(DATA_VERSION[i], (byte)inputStream.read());
			}

			Assert.assertEquals(-1, inputStream.read());
		}
	}

	@Test
	public void testGetFileNames() throws Exception {
		String fileName1 = RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName1, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		String fileName2 = RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName2, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		String[] fileNames = _store.getFileNames(
			_companyId, _repositoryId, StringPool.BLANK);

		Assert.assertEquals(Arrays.toString(fileNames), 2, fileNames.length);

		Set<String> fileNamesSet = SetUtil.fromArray(fileNames);

		Assert.assertTrue(
			fileNamesSet.toString(), fileNamesSet.contains(fileName1));
		Assert.assertTrue(
			fileNamesSet.toString(), fileNamesSet.contains(fileName2));
	}

	@Test
	public void testGetFileNamesWithDirectoryOneLevelDeep() throws Exception {
		String dirName = RandomTestUtil.randomString();

		String fileName1 = dirName + "/" + RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName1, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		String fileName2 = dirName + "/" + RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName2, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		String[] fileNames = _store.getFileNames(
			_companyId, _repositoryId, dirName);

		Assert.assertEquals(Arrays.toString(fileNames), 2, fileNames.length);

		Set<String> fileNamesSet = SetUtil.fromArray(fileNames);

		Assert.assertTrue(
			fileNamesSet.toString(), fileNamesSet.contains(fileName1));
		Assert.assertTrue(
			fileNamesSet.toString(), fileNamesSet.contains(fileName2));
	}

	@Test
	public void testGetFileNamesWithDirectoryTwoLevelDeep() throws Exception {
		String dirName = RandomTestUtil.randomString();

		String subdirName = dirName + "/" + RandomTestUtil.randomString();

		String fileName1 = dirName + "/" + RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName1, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		String fileName2 = subdirName + "/" + RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName2, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		String fileName3 =
			RandomTestUtil.randomString() + "/" + RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName3, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		String[] fileNames = _store.getFileNames(
			_companyId, _repositoryId, dirName);

		Assert.assertEquals(Arrays.toString(fileNames), 2, fileNames.length);

		Set<String> fileNamesSet = SetUtil.fromArray(fileNames);

		Assert.assertTrue(
			fileNamesSet.toString(), fileNamesSet.contains(fileName1));
		Assert.assertTrue(
			fileNamesSet.toString(), fileNamesSet.contains(fileName2));

		fileNames = _store.getFileNames(_companyId, _repositoryId, subdirName);

		Assert.assertEquals(Arrays.toString(fileNames), 1, fileNames.length);
		Assert.assertEquals(fileName2, fileNames[0]);
	}

	@Test
	public void testGetFileNamesWithInvalidDirectory() {
		String dirName = RandomTestUtil.randomString();

		String[] fileNames = _store.getFileNames(
			_companyId, _repositoryId, dirName);

		Assert.assertEquals(Arrays.toString(fileNames), 0, fileNames.length);
	}

	@Test
	public void testGetFileNamesWithInvalidRepository() {
		String[] fileNames = _store.getFileNames(
			_companyId, _repositoryId, StringPool.BLANK);

		Assert.assertEquals(Arrays.toString(fileNames), 0, fileNames.length);
	}

	@Test
	public void testGetFileNamesWithTwoLevelsDeep() throws Exception {
		String dirName = RandomTestUtil.randomString();

		String subdirName = dirName + "/" + RandomTestUtil.randomString();

		String fileName1 = dirName + "/" + RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName1, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		String fileName2 = subdirName + "/" + RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName2, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		String[] fileNames = _store.getFileNames(
			_companyId, _repositoryId, StringPool.BLANK);

		Assert.assertEquals(Arrays.toString(fileNames), 2, fileNames.length);

		Set<String> fileNamesSet = SetUtil.fromArray(fileNames);

		Assert.assertTrue(
			fileNamesSet.toString(), fileNamesSet.contains(fileName1));
		Assert.assertTrue(
			fileNamesSet.toString(), fileNamesSet.contains(fileName2));
	}

	@Test
	public void testGetFileSize() throws Exception {
		String fileName = RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		long size = _store.getFileSize(
			_companyId, _repositoryId, fileName, StringPool.BLANK);

		Assert.assertEquals(_DATA_SIZE, size);
	}

	@Test(expected = NoSuchFileException.class)
	public void testGetFileSizeNoSuchFileException() throws Exception {
		_store.getFileSize(
			_companyId, _repositoryId, RandomTestUtil.randomString(),
			StringPool.BLANK);
	}

	@Test
	public void testGetFileVersions() throws Exception {
		String fileName = RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		addVersions(fileName, 5);

		String[] fileVersions = _store.getFileVersions(
			_companyId, _repositoryId, fileName);

		for (int i = 0; i < 5; i++) {
			Assert.assertEquals("1." + i, fileVersions[i]);
		}
	}

	@Test
	public void testHasFile() throws Exception {
		String fileName = RandomTestUtil.randomString();

		_store.addFile(
			_companyId, _repositoryId, fileName, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(DATA_VERSION));

		addVersions(fileName, 5);

		String versionLabel = "1.";

		for (int i = 0; i < 5; i++) {
			Assert.assertTrue(
				_store.hasFile(
					_companyId, _repositoryId, fileName, versionLabel + i));
		}
	}

	@Test
	public void testVerifyCompanyStores() throws Exception {
		String fileName = RandomTestUtil.randomString();
		String warnMessage = StringBundler.concat(
			"Manually remove unused store ", _companyId,
			" that belongs to company ", _companyId,
			" if it is no longer used anywhere else");

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				getStoreClassName(), LoggerTestUtil.WARN)) {

			_store.verifyCompanyStores();

			List<String> messages = logCapture.getMessages();

			Assert.assertFalse(
				messages.toString(), messages.contains(warnMessage));

			_store.addFile(
				_companyId, _repositoryId, fileName, Store.VERSION_DEFAULT,
				new UnsyncByteArrayInputStream(DATA_VERSION));

			_store.verifyCompanyStores();

			messages = logCapture.getMessages();

			Assert.assertTrue(
				messages.toString(), messages.contains(warnMessage));
		}
	}

	protected void addVersions(String fileName, int newVersionCount)
		throws Exception {

		String versionLabel = "1.";

		for (int i = 1; i <= newVersionCount; i++) {
			_store.addFile(
				_companyId, _repositoryId, fileName, versionLabel + i,
				new UnsyncByteArrayInputStream(DATA_VERSION));
		}
	}

	protected abstract Store getStore();

	protected String getStoreClassName() {
		return PropsUtil.get(PropsKeys.DL_STORE_IMPL);
	}

	protected static final byte[] DATA_VERSION =
		new byte[BaseStoreTestCase._DATA_SIZE];

	private File _exportLayoutsAsFile(long groupId) throws Exception {
		return ExportImportLocalServiceUtil.exportLayoutsAsFile(
			ExportImportConfigurationLocalServiceUtil.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportLayoutSettingsMap(
							TestPropsValues.getUser(), groupId, false,
							new long[0], _getParameterMap())));
	}

	private Map<String, String[]> _getParameterMap() {
		return HashMapBuilder.put(
			PortletDataHandlerKeys.DATA_STRATEGY,
			new String[] {PortletDataHandlerKeys.DATA_STRATEGY_MIRROR_OVERWRITE}
		).put(
			PortletDataHandlerKeys.DELETIONS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.PERMISSIONS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_SETUP_ALL,
			new String[] {Boolean.TRUE.toString()}
		).build();
	}

	private void _importLayouts(long groupId, File larFile) throws Exception {
		ExportImportLocalServiceUtil.importLayouts(
			ExportImportConfigurationLocalServiceUtil.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportLayoutSettingsMap(
							TestPropsValues.getUser(), groupId, false, null,
							_getParameterMap())),
			larFile);
	}

	private static final int _DATA_SIZE = 1024 * 65;

	static {
		for (int i = 0; i < _DATA_SIZE; i++) {
			DATA_VERSION[i] = (byte)i;
		}
	}

	private long _companyId;
	private long _repositoryId;
	private Store _store;

}