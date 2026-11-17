/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.repository.external;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.processor.DLProcessorHelper;
import com.liferay.document.library.kernel.processor.DLProcessorHelperUtil;
import com.liferay.document.library.kernel.service.DLAppHelperLocalService;
import com.liferay.document.library.repository.external.model.ExtRepositoryFileVersionAdapter;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.RepositoryEntry;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.RepositoryEntryLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.key.secret.SecretResolverUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Caio Farias
 */
public class ExtRepositoryAdapterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_dlProcessorHelperSnapshot = ReflectionTestUtil.getAndSetFieldValue(
			DLProcessorHelperUtil.class, "_dlProcessorHelperSnapshot",
			new Snapshot<DLProcessorHelper>(
				DLProcessorHelperUtil.class, DLProcessorHelper.class) {

				@Override
				public DLProcessorHelper get() {
					return _dlProcessorHelper;
				}

			});
		_dlRepositoryGuestPassword = GetterUtil.getString(
			PropsUtil.get(PropsKeys.DL_REPOSITORY_GUEST_PASSWORD));
		_extRepositoryAdapter = _getExtRepositoryAdapter();
		_secretResolverSnapshot = ReflectionTestUtil.getAndSetFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			new Snapshot<SecretResolver>(
				SecretResolverUtil.class, SecretResolver.class) {

				@Override
				public SecretResolver get() {
					return _secretResolver;
				}

			});

		_setUpExtRepositoryFileEntries();
	}

	@After
	public void tearDown() {
		CentralizedThreadLocal.clearShortLivedCentralizedThreadLocals();

		ReflectionTestUtil.setFieldValue(
			DLProcessorHelperUtil.class, "_dlProcessorHelperSnapshot",
			_dlProcessorHelperSnapshot);
		PropsUtil.set(
			PropsKeys.DL_REPOSITORY_GUEST_PASSWORD, _dlRepositoryGuestPassword);
		ReflectionTestUtil.setFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			_secretResolverSnapshot);
	}

	@Test
	public void testCheckInFileEntry() throws Exception {
		String changeLog = RandomTestUtil.randomString();

		_extRepositoryAdapter.checkInFileEntry(
			RandomTestUtil.randomLong(), _fileEntryIds.get(0),
			DLVersionNumberIncrease.NONE, changeLog, null);

		ExtRepositoryFileEntry extRepositoryFileEntry =
			_extRepositoryFileEntries.get(0);

		Mockito.verify(
			_extRepository
		).checkInExtRepositoryFileEntry(
			extRepositoryFileEntry.getExtRepositoryModelKey(),
			DLVersionNumberIncrease.NONE, changeLog
		);

		Mockito.verify(
			_dlProcessorHelper, Mockito.times(2)
		).cleanUp(
			Mockito.any(FileVersion.class)
		);
	}

	@Test
	public void testGetFileEntries() throws Exception {
		ExtRepositoryFileEntry extRepositoryFileEntry =
			_extRepositoryFileEntries.get(0);

		Mockito.doThrow(
			new PortalException()
		).when(
			_extRepository
		).getExtRepositoryFileVersions(
			extRepositoryFileEntry
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ExtRepositoryAdapter.class.getName(), LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				_fileEntryIds.subList(1, 20),
				TransformUtil.transform(
					_extRepositoryAdapter.getFileEntries(
						DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, 0, 20,
						null),
					FileEntry::getFileEntryId));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to get external repository object " +
					extRepositoryFileEntry.getExtRepositoryModelKey(),
				logEntry.getMessage());
		}
	}

	@Test
	public void testGetFileEntry() throws Exception {
		_testGetFileEntryWhenAssetEntryCheckFails();
		_testGetFileEntryWhenFileEntryIsDisplayed();
		_testGetFileEntryWhenFileEntryIsNotDisplayed();
	}

	@Test
	public void testGetFoldersAndFileEntries() throws Exception {
		List<Object> foldersAndFileEntries =
			_extRepositoryAdapter.getFoldersAndFileEntries(
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, 0, 20, null);

		Assert.assertEquals(
			foldersAndFileEntries.toString(), 20, foldersAndFileEntries.size());

		Mockito.verify(
			_dlAppHelperLocalService, Mockito.times(20)
		).checkAssetEntry(
			Mockito.anyLong(), Mockito.argThat(foldersAndFileEntries::contains),
			Mockito.any(FileVersion.class)
		);

		Mockito.verifyNoMoreInteractions(_dlAppHelperLocalService);

		Mockito.verify(
			_extRepository, Mockito.times(20)
		).getExtRepositoryFileVersions(
			Mockito.any(ExtRepositoryFileEntry.class)
		);
	}

	@Test
	public void testGetFoldersAndFileEntriesCount() throws Exception {
		ExtRepositoryFolder extRepositoryFolder = Mockito.mock(
			ExtRepositoryFolder.class);

		Mockito.when(
			extRepositoryFolder.getExtRepositoryModelKey()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		List<ExtRepositoryObject> extRepositoryObjects = new ArrayList<>(
			_extRepositoryFileEntries);

		extRepositoryObjects.add(extRepositoryFolder);

		Mockito.when(
			_extRepository.getExtRepositoryObjects(
				ExtRepositoryObjectType.OBJECT,
				_extRepository.getRootFolderKey())
		).thenReturn(
			extRepositoryObjects
		);

		Assert.assertEquals(
			26,
			_extRepositoryAdapter.getFoldersAndFileEntriesCount(
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, null));

		String mimeType = RandomTestUtil.randomString();

		for (ExtRepositoryFileEntry extRepositoryFileEntry :
				_extRepositoryFileEntries.subList(0, 5)) {

			Mockito.when(
				extRepositoryFileEntry.getMimeType()
			).thenReturn(
				mimeType
			);
		}

		Assert.assertEquals(
			6,
			_extRepositoryAdapter.getFoldersAndFileEntriesCount(
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				new String[] {mimeType}));

		Mockito.verifyNoInteractions(_dlAppHelperLocalService);

		Mockito.verify(
			_extRepository, Mockito.never()
		).getExtRepositoryFileVersions(
			Mockito.any(ExtRepositoryFileEntry.class)
		);
	}

	@Test
	public void testGetLatestExtRepositoryFileVersionAdapter()
		throws Exception {

		ExtRepositoryFileVersion extRepositoryFileVersion = Mockito.mock(
			ExtRepositoryFileVersion.class);

		Mockito.when(
			extRepositoryFileVersion.getExtRepositoryModelKey()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		String version = RandomTestUtil.randomString();

		Mockito.when(
			extRepositoryFileVersion.getVersion()
		).thenReturn(
			version
		);

		Mockito.doReturn(
			extRepositoryFileVersion
		).when(
			_extRepository
		).getLatestExtRepositoryFileVersion(
			Mockito.any(ExtRepositoryFileEntry.class)
		);

		FileEntry fileEntry = _extRepositoryAdapter.getFileEntry(
			_fileEntryIds.get(0));

		ExtRepositoryFileVersionAdapter extRepositoryFileVersionAdapter =
			(ExtRepositoryFileVersionAdapter)fileEntry.getFileVersion();

		Assert.assertSame(
			extRepositoryFileVersion,
			extRepositoryFileVersionAdapter.getExtRepositoryModel());

		extRepositoryFileVersionAdapter =
			(ExtRepositoryFileVersionAdapter)fileEntry.getFileVersion(version);

		Assert.assertSame(
			extRepositoryFileVersion,
			extRepositoryFileVersionAdapter.getExtRepositoryModel());

		Mockito.verify(
			_extRepository, Mockito.never()
		).getExtRepositoryFileVersions(
			Mockito.any(ExtRepositoryFileEntry.class)
		);
	}

	@Test
	public void testGetPassword() {
		String keyReferenceString = KeyReferenceUtil.toKeyReferenceString(
			new KeyReference(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				KeyReference.Type.SECRET));
		String password = RandomTestUtil.randomString();

		_secretResolver = Mockito.mock(SecretResolver.class);

		Mockito.when(
			_secretResolver.resolve(CompanyConstants.SYSTEM, keyReferenceString)
		).thenReturn(
			password
		);

		_testGetPassword(password, keyReferenceString);
		_testGetPassword(password, password);
	}

	@Test
	public void testMoveFileEntry() throws Exception {
		ExtRepositoryFileEntry extRepositoryFileEntry = Mockito.mock(
			ExtRepositoryFileEntry.class);
		String extRepositoryModelKey = RandomTestUtil.randomString();

		Mockito.when(
			extRepositoryFileEntry.getExtRepositoryModelKey()
		).thenReturn(
			extRepositoryModelKey
		);

		Mockito.doReturn(
			extRepositoryFileEntry
		).when(
			_extRepository
		).moveExtRepositoryObject(
			Mockito.eq(ExtRepositoryObjectType.FILE), Mockito.anyString(),
			Mockito.anyString(), Mockito.any()
		);

		long fileEntryId = _fileEntryIds.get(0);

		_extRepositoryAdapter.moveFileEntry(
			RandomTestUtil.randomLong(), fileEntryId,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, null);

		Mockito.verify(
			_repositoryEntryLocalService
		).updateRepositoryEntry(
			fileEntryId, extRepositoryModelKey
		);
	}

	@Test
	public void testUpdateFileEntry() throws Exception {
		ExtRepositoryFileEntry extRepositoryFileEntry =
			_extRepositoryFileEntries.get(0);
		String title = RandomTestUtil.randomString();

		Mockito.when(
			extRepositoryFileEntry.getTitle()
		).thenReturn(
			title
		);

		Mockito.doReturn(
			extRepositoryFileEntry
		).when(
			_extRepository
		).updateExtRepositoryFileEntry(
			Mockito.anyString(), Mockito.anyString(),
			Mockito.any(InputStream.class)
		);

		String changeLog = RandomTestUtil.randomString();

		_extRepositoryAdapter.updateFileEntry(
			RandomTestUtil.randomLong(), _fileEntryIds.get(0),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), title,
			null, null, changeLog, DLVersionNumberIncrease.NONE,
			Mockito.mock(InputStream.class), 0, null, null, null, null);

		Mockito.verify(
			_extRepository
		).checkInExtRepositoryFileEntry(
			extRepositoryFileEntry.getExtRepositoryModelKey(),
			DLVersionNumberIncrease.NONE, changeLog
		);

		Mockito.verify(
			_dlProcessorHelper, Mockito.times(2)
		).cleanUp(
			Mockito.any(FileVersion.class)
		);
	}

	private ExtRepositoryAdapter _getExtRepositoryAdapter() {
		ExtRepositoryAdapter extRepositoryAdapter = new ExtRepositoryAdapter(
			_extRepository);

		extRepositoryAdapter.setDLAppHelperLocalService(
			_dlAppHelperLocalService);
		extRepositoryAdapter.setRepositoryEntryLocalService(
			_repositoryEntryLocalService);

		return extRepositoryAdapter;
	}

	private void _setUpExtRepositoryFileEntries() throws Exception {
		Mockito.when(
			_repositoryEntryLocalService.getRepositoryEntry(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.anyString())
		).thenReturn(
			Mockito.mock(RepositoryEntry.class)
		);

		for (int i = 0; i < 25; i++) {
			String extRepositoryModelKey = RandomTestUtil.randomString();

			ExtRepositoryFileEntry extRepositoryFileEntry = Mockito.mock(
				ExtRepositoryFileEntry.class);

			Mockito.when(
				extRepositoryFileEntry.getExtRepositoryModelKey()
			).thenReturn(
				extRepositoryModelKey
			);

			Mockito.when(
				extRepositoryFileEntry.getMimeType()
			).thenReturn(
				RandomTestUtil.randomString()
			);

			Mockito.when(
				_extRepository.getExtRepositoryObject(
					ExtRepositoryObjectType.FILE, extRepositoryModelKey)
			).thenReturn(
				extRepositoryFileEntry
			);

			_extRepositoryFileEntries.add(extRepositoryFileEntry);

			long fileEntryId = RandomTestUtil.randomLong();

			RepositoryEntry repositoryEntry = Mockito.mock(
				RepositoryEntry.class);

			Mockito.when(
				repositoryEntry.getMappedId()
			).thenReturn(
				extRepositoryModelKey
			);

			Mockito.when(
				repositoryEntry.getRepositoryEntryId()
			).thenReturn(
				fileEntryId
			);

			Mockito.when(
				_repositoryEntryLocalService.fetchRepositoryEntry(fileEntryId)
			).thenReturn(
				repositoryEntry
			);

			Mockito.when(
				_repositoryEntryLocalService.getRepositoryEntry(
					Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
					Mockito.eq(extRepositoryModelKey))
			).thenReturn(
				repositoryEntry
			);

			_fileEntryIds.add(fileEntryId);
		}

		Mockito.when(
			_extRepository.getExtRepositoryFileVersions(
				Mockito.any(ExtRepositoryFileEntry.class))
		).thenAnswer(
			invocation -> {
				ExtRepositoryFileVersion extRepositoryFileVersion =
					Mockito.mock(ExtRepositoryFileVersion.class);

				Mockito.when(
					extRepositoryFileVersion.getExtRepositoryModelKey()
				).thenReturn(
					RandomTestUtil.randomString()
				);

				return Collections.singletonList(extRepositoryFileVersion);
			}
		);

		String rootFolderKey = RandomTestUtil.randomString();

		Mockito.when(
			_extRepository.getExtRepositoryObjects(
				ExtRepositoryObjectType.FILE, rootFolderKey)
		).thenReturn(
			new ArrayList<>(_extRepositoryFileEntries)
		);

		Mockito.when(
			_extRepository.getExtRepositoryObjects(
				ExtRepositoryObjectType.OBJECT, rootFolderKey)
		).thenReturn(
			new ArrayList<>(_extRepositoryFileEntries)
		);

		Mockito.when(
			_extRepository.getRootFolderKey()
		).thenReturn(
			rootFolderKey
		);
	}

	private void _testGetFileEntryWhenAssetEntryCheckFails() throws Exception {
		PortalException portalException = new PortalException();

		Mockito.doThrow(
			portalException
		).doNothing(
		).when(
			_dlAppHelperLocalService
		).checkAssetEntry(
			Mockito.anyLong(), Mockito.any(FileEntry.class),
			Mockito.any(FileVersion.class)
		);

		Assert.assertSame(
			portalException,
			Assert.assertThrows(
				PortalException.class,
				() -> _extRepositoryAdapter.getFileEntry(
					_fileEntryIds.get(23))));

		FileEntry fileEntry = _extRepositoryAdapter.getFileEntry(
			_fileEntryIds.get(23));

		Mockito.verify(
			_dlAppHelperLocalService, Mockito.times(2)
		).checkAssetEntry(
			Mockito.anyLong(), Mockito.eq(fileEntry),
			Mockito.any(FileVersion.class)
		);
	}

	private void _testGetFileEntryWhenFileEntryIsDisplayed() throws Exception {
		_extRepositoryAdapter.getFoldersAndFileEntries(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, 0, 20, null);

		Mockito.clearInvocations(_dlAppHelperLocalService);

		_extRepositoryAdapter.getFileEntry(_fileEntryIds.get(0));

		Mockito.verifyNoInteractions(_dlAppHelperLocalService);
	}

	private void _testGetFileEntryWhenFileEntryIsNotDisplayed()
		throws Exception {

		FileEntry fileEntry = _extRepositoryAdapter.getFileEntry(
			_fileEntryIds.get(24));

		_extRepositoryAdapter.getFileEntry(_fileEntryIds.get(24));

		Mockito.verify(
			_dlAppHelperLocalService
		).checkAssetEntry(
			Mockito.anyLong(), Mockito.eq(fileEntry),
			Mockito.any(FileVersion.class)
		);
	}

	private void _testGetPassword(String expectedPassword, String password) {
		PropsUtil.set(PropsKeys.DL_REPOSITORY_GUEST_PASSWORD, password);

		ExtRepositoryAdapter extRepositoryAdapter = new ExtRepositoryAdapter(
			Mockito.mock(ExtRepository.class));

		Assert.assertEquals(
			expectedPassword,
			ReflectionTestUtil.invoke(
				extRepositoryAdapter, "_getPassword", new Class<?>[0]));
	}

	private final DLAppHelperLocalService _dlAppHelperLocalService =
		Mockito.mock(DLAppHelperLocalService.class);
	private final DLProcessorHelper _dlProcessorHelper = Mockito.mock(
		DLProcessorHelper.class);
	private Snapshot<DLProcessorHelper> _dlProcessorHelperSnapshot;
	private String _dlRepositoryGuestPassword;
	private final ExtRepository _extRepository = Mockito.mock(
		ExtRepository.class,
		Mockito.withSettings(
		).defaultAnswer(
			Mockito.CALLS_REAL_METHODS
		));
	private ExtRepositoryAdapter _extRepositoryAdapter;
	private final List<ExtRepositoryFileEntry> _extRepositoryFileEntries =
		new ArrayList<>();
	private final List<Long> _fileEntryIds = new ArrayList<>();
	private final RepositoryEntryLocalService _repositoryEntryLocalService =
		Mockito.mock(RepositoryEntryLocalService.class);
	private SecretResolver _secretResolver;
	private Snapshot<SecretResolver> _secretResolverSnapshot;

}