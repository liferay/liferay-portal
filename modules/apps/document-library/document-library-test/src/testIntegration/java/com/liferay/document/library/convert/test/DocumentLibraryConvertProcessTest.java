/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.convert.test;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.model.AMImageEntry;
import com.liferay.adaptive.media.image.service.AMImageEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.document.library.content.exception.NoSuchContentException;
import com.liferay.document.library.content.service.DLContentLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.processor.ImageProcessorUtil;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.message.boards.test.util.MBTestUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.convert.ConvertProcess;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.ShardedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.documentlibrary.store.DLStoreImpl;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 * @author Sergio González
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class DocumentLibraryConvertProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_convertProcess.setParameterValues(
			new String[] {_CLASS_NAME_DB_STORE, Boolean.TRUE.toString()});

		_defaultStore = ReflectionTestUtil.getAndSetFieldValue(
			_convertProcess, "_store", _fileSystemStore);

		DLStoreImpl.setStore(_fileSystemStore);

		_companyLocalService.forEachCompanyId(
			companyId -> {
				_groups.add(GroupTestUtil.addGroupToCompany(companyId));

				_amImageConfigurationEntries.put(
					companyId, _deleteAMImageConfigurationEntries(companyId));
			});
	}

	@After
	public void tearDown() throws Exception {
		_companyLocalService.forEachCompanyId(
			companyId -> {
				_deleteAMImageConfigurationEntries(companyId);
				_addAMImageConfigurationEntries(
					companyId, _amImageConfigurationEntries.get(companyId));
			});

		ReflectionTestUtil.setFieldValue(_convertProcess, "_store", _dbStore);

		DLStoreImpl.setStore(_dbStore);

		_convertProcess.setParameterValues(
			new String[] {
				_CLASS_NAME_FILE_SYSTEM_STORE, Boolean.TRUE.toString()
			});

		try {
			_convertProcess.convert();
		}
		finally {
			PropsValues.DL_STORE_IMPL = PropsUtil.get(PropsKeys.DL_STORE_IMPL);

			ReflectionTestUtil.setFieldValue(
				_convertProcess, "_store", _defaultStore);

			DLStoreImpl.setStore(_defaultStore);
		}
	}

	@Test
	public void testMigrateAMImageEntries() throws Exception {
		List<AMImageEntry> amImageEntries = new ArrayList<>();

		_forEach(
			_groups,
			group -> {
				User user = UserTestUtil.getAdminUser(group.getCompanyId());

				byte[] bytes = FileUtil.getBytes(
					getClass(), "dependencies/liferay.jpg");

				FileEntry fileEntry = _addFileEntry(
					group, user, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
					RandomTestUtil.randomString() + ".jpg",
					ContentTypes.IMAGE_JPEG, bytes);

				AMImageConfigurationEntry amImageConfigurationEntry =
					_amImageConfigurationHelper.addAMImageConfigurationEntry(
						group.getCompanyId(), RandomTestUtil.randomString(),
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString(),
						HashMapBuilder.put(
							"max-height", String.valueOf(100)
						).put(
							"max-width", String.valueOf(200)
						).build());

				amImageEntries.add(
					_amImageEntryLocalService.addAMImageEntry(
						amImageConfigurationEntry, fileEntry.getFileVersion(),
						100, 200, new UnsyncByteArrayInputStream(bytes),
						12345));
			});

		_convertProcess.convert();

		_forEach(
			amImageEntries,
			amImageEntry -> {
				FileVersion fileVersion = _dlAppLocalService.getFileVersion(
					amImageEntry.getFileVersionId());

				_dlContentLocalService.getContent(
					amImageEntry.getCompanyId(), CompanyConstants.SYSTEM,
					_getFileVersionPath(
						fileVersion, amImageEntry.getConfigurationUuid()),
					fileVersion.getVersion());
			});
	}

	@Test
	public void testMigrateDLAndDeleteFilesInSourceStore() throws Exception {
		_testMigrateAndCheckOldRepositoryFiles(Boolean.TRUE);
	}

	@Test
	public void testMigrateDLAndKeepFilesInSourceStore() throws Exception {
		_testMigrateAndCheckOldRepositoryFiles(Boolean.FALSE);
	}

	@Test
	public void testMigrateDLWhenFileEntryEmpty() throws Exception {
		Map<Long, FileEntry> fileEntries = new HashMap<>();

		_forEach(
			_groups,
			group -> {
				User user = UserTestUtil.getAdminUser(group.getCompanyId());

				fileEntries.put(
					group.getCompanyId(),
					_addFileEntry(
						group, user, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
						RandomTestUtil.randomString(),
						ContentTypes.APPLICATION_OCTET_STREAM, null));
			});

		_convertProcess.convert();

		_companyLocalService.forEachCompanyId(
			companyId -> _getContent(fileEntries.get(companyId)));
	}

	@Test
	public void testMigrateDLWhenFileEntryInFolder() throws Exception {
		Map<Long, FileEntry> fileEntries = new HashMap<>();

		_forEach(
			_groups,
			group -> {
				User user = UserTestUtil.getAdminUser(group.getCompanyId());

				Folder folder = _addFolder(group, user);

				fileEntries.put(
					group.getCompanyId(),
					_addFileEntry(
						group, user, folder.getFolderId(),
						RandomTestUtil.randomString() + ".txt",
						ContentTypes.TEXT_PLAIN,
						TestDataConstants.TEST_BYTE_ARRAY));
			});

		_convertProcess.convert();

		_companyLocalService.forEachCompanyId(
			companyId -> _getContent(fileEntries.get(companyId)));
	}

	@Test
	public void testMigrateDLWhenFileEntryInRootFolder() throws Exception {
		Map<Long, FileEntry> fileEntries = new HashMap<>();

		_forEach(
			_groups,
			group -> fileEntries.put(
				group.getCompanyId(),
				_addFileEntry(
					group, UserTestUtil.getAdminUser(group.getCompanyId()),
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
					RandomTestUtil.randomString() + ".txt",
					ContentTypes.TEXT_PLAIN,
					TestDataConstants.TEST_BYTE_ARRAY)));

		_convertProcess.convert();

		_companyLocalService.forEachCompanyId(
			companyId -> _getContent(fileEntries.get(companyId)));
	}

	@Test
	public void testMigrateImages() throws Exception {
		_companyLocalService.forEachCompanyId(
			companyId -> _images.add(
				_imageLocalService.updateImage(
					companyId, _counterLocalService.increment(),
					FileUtil.getBytes(
						getClass(), "dependencies/liferay.jpg"))));

		_convertProcess.convert();

		_forEach(
			_images,
			image -> _dlContentLocalService.getContent(
				image.getCompanyId(), _REPOSITORY_ID,
				image.getImageId() + ".jpg", Store.VERSION_DEFAULT));
	}

	@Test
	public void testMigrateMB() throws Exception {
		List<MBMessage> mbMessages = new ArrayList<>();

		_forEach(
			_groups, group -> mbMessages.add(_addMBMessageAttachment(group)));

		_convertProcess.convert();

		_forEach(
			mbMessages,
			mbMessage -> {
				DLFileEntry dlFileEntry = _getDLFileEntry(mbMessage);

				String title = dlFileEntry.getTitle();

				Assert.assertTrue(title.endsWith(".docx"));

				_getContent(dlFileEntry, StringPool.BLANK);
			});
	}

	@Test
	public void testStoreUpdatedAfterConversion() throws Exception {
		_convertProcess.convert();

		Assert.assertEquals(_CLASS_NAME_DB_STORE, PropsValues.DL_STORE_IMPL);
	}

	private void _addAMImageConfigurationEntries(
			long companyId,
			Collection<AMImageConfigurationEntry> amImageConfigurationEntries)
		throws IOException, PortalException {

		for (AMImageConfigurationEntry amImageConfigurationEntry :
				amImageConfigurationEntries) {

			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				companyId, amImageConfigurationEntry.getName(),
				amImageConfigurationEntry.getDescription(),
				amImageConfigurationEntry.getUUID(),
				amImageConfigurationEntry.getProperties());
		}
	}

	private FileEntry _addFileEntry(
			Group group, User user, long folderId, String fileName,
			String mimeType, byte[] bytes)
		throws Exception {

		return _dlAppLocalService.addFileEntry(
			null, user.getUserId(), group.getGroupId(), folderId, fileName,
			mimeType, bytes, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), user.getUserId()));
	}

	private Folder _addFolder(Group group, User user) throws PortalException {
		return _dlAppLocalService.addFolder(
			null, user.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), user.getUserId()));
	}

	private MBMessage _addMBMessageAttachment(Group group) throws Exception {
		User user = UserTestUtil.getAdminUser(group.getCompanyId());

		return _mbMessageLocalService.addMessage(
			user.getUserId(), user.getFullName(), group.getGroupId(),
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, "Subject", "Body",
			MBMessageConstants.DEFAULT_FORMAT,
			MBTestUtil.getInputStreamOVPs(
				"OSX_Test.docx", getClass(), StringPool.BLANK),
			false, 0, false,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private Collection<AMImageConfigurationEntry>
			_deleteAMImageConfigurationEntries(long companyId)
		throws IOException {

		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				companyId);

		for (AMImageConfigurationEntry amImageConfigurationEntry :
				amImageConfigurationEntries) {

			_amImageConfigurationHelper.forceDeleteAMImageConfigurationEntry(
				companyId, amImageConfigurationEntry.getUUID());
		}

		return amImageConfigurationEntries;
	}

	private <T extends ShardedModel> void _forEach(
			List<T> entries, UnsafeConsumer<T, Exception> unsafeConsumer)
		throws Exception {

		for (T entry : entries) {
			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						entry.getCompanyId())) {

				unsafeConsumer.accept(entry);
			}
		}
	}

	private void _getContent(DLFileEntry dlFileEntry, String version)
		throws NoSuchContentException {

		_dlContentLocalService.getContent(
			dlFileEntry.getCompanyId(),
			DLFolderConstants.getDataRepositoryId(
				dlFileEntry.getRepositoryId(), dlFileEntry.getFolderId()),
			dlFileEntry.getName(), version);
	}

	private void _getContent(FileEntry fileEntry) throws PortalException {
		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

		_getContent(dlFileEntry, dlFileVersion.getStoreFileName());
	}

	private DLFileEntry _getDLFileEntry(MBMessage mbMessage)
		throws PortalException {

		List<FileEntry> fileEntries = mbMessage.getAttachmentsFileEntries(0, 1);

		Assert.assertFalse(fileEntries.toString(), fileEntries.isEmpty());

		FileEntry fileEntry = fileEntries.get(0);

		return _dlFileEntryLocalService.getDLFileEntry(
			fileEntry.getFileEntryId());
	}

	private String _getFileVersionPath(
		FileVersion fileVersion, String configurationUuid) {

		return StringBundler.concat(
			"adaptive/", configurationUuid, "/", fileVersion.getGroupId(), "/",
			fileVersion.getRepositoryId(), "/", fileVersion.getFileEntryId(),
			"/", fileVersion.getFileVersionId(), "/");
	}

	private void _testMigrateAndCheckOldRepositoryFiles(Boolean delete)
		throws Exception {

		_convertProcess.setParameterValues(
			new String[] {_CLASS_NAME_DB_STORE, delete.toString()});

		List<FileEntry> fileEntries = new ArrayList<>();

		_forEach(
			_groups,
			group -> {
				User user = UserTestUtil.getAdminUser(group.getCompanyId());

				fileEntries.add(
					_addFileEntry(
						group, user, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
						RandomTestUtil.randomString() + ".txt",
						ContentTypes.TEXT_PLAIN,
						TestDataConstants.TEST_BYTE_ARRAY));

				Folder folder = _addFolder(group, user);

				FileEntry folderFileEntry = _addFileEntry(
					group, user, folder.getFolderId(), "liferay.jpg",
					ContentTypes.IMAGE_JPEG,
					FileUtil.getBytes(getClass(), "dependencies/liferay.jpg"));

				ImageProcessorUtil.generateImages(
					null, folderFileEntry.getFileVersion());

				fileEntries.add(folderFileEntry);
			});

		_convertProcess.convert();

		for (FileEntry fileEntry : fileEntries) {
			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						fileEntry.getCompanyId())) {

				DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

				DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

				Assert.assertNotEquals(
					delete,
					_fileSystemStore.hasFile(
						dlFileEntry.getCompanyId(),
						dlFileEntry.getDataRepositoryId(),
						dlFileEntry.getName(),
						dlFileVersion.getStoreFileName()));

				_getContent(dlFileEntry, dlFileVersion.getStoreFileName());
			}
		}
	}

	private static final String _CLASS_NAME_DB_STORE =
		"com.liferay.portal.store.db.DBStore";

	private static final String _CLASS_NAME_FILE_SYSTEM_STORE =
		"com.liferay.portal.store.file.system.FileSystemStore";

	private static final long _REPOSITORY_ID = 0;

	private final Map<Long, Collection<AMImageConfigurationEntry>>
		_amImageConfigurationEntries = new HashMap<>();

	@Inject
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Inject
	private AMImageEntryLocalService _amImageEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.document.library.internal.convert.document.library.DocumentLibraryConvertProcess"
	)
	private ConvertProcess _convertProcess;

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject(filter = "store.type=" + _CLASS_NAME_DB_STORE)
	private Store _dbStore;

	private Store _defaultStore;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLContentLocalService _dlContentLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject(filter = "store.type=" + _CLASS_NAME_FILE_SYSTEM_STORE)
	private Store _fileSystemStore;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	@Inject
	private ImageLocalService _imageLocalService;

	@DeleteAfterTestRun
	private final List<Image> _images = new ArrayList<>();

	@Inject
	private MBMessageLocalService _mbMessageLocalService;

}