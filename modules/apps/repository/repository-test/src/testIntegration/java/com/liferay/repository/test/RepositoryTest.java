/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.repository.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderServiceUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.RepositoryLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.repository.liferayrepository.LiferayRepository;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alexander Chow
 */
@RunWith(Arquillian.class)
public class RepositoryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddFileEntryInRepository() throws Exception {
		Repository repository = RepositoryLocalServiceUtil.addRepository(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			PortalUtil.getClassNameId(LiferayRepository.class),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new UnicodeProperties(), true,
			new ServiceContext());

		long[] primaryKeys = populateRepository(repository.getRepositoryId());

		Assert.assertEquals(
			1,
			DLAppServiceUtil.getFoldersCount(
				repository.getRepositoryId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID));
		Assert.assertEquals(
			1,
			DLAppServiceUtil.getFileEntriesAndFileShortcutsCount(
				repository.getRepositoryId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				WorkflowConstants.STATUS_ANY));
		Assert.assertEquals(
			1,
			DLAppServiceUtil.getFileEntriesAndFileShortcutsCount(
				repository.getRepositoryId(), primaryKeys[1],
				WorkflowConstants.STATUS_ANY));
	}

	@Test
	public void testDeleteAllRepositories() throws Exception {
		long[] repositoryIds = new long[2];

		long classNameId = PortalUtil.getClassNameId(LiferayRepository.class);

		Repository repository = RepositoryLocalServiceUtil.addRepository(
			null, TestPropsValues.getUserId(), _group.getGroupId(), classNameId,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new UnicodeProperties(), true,
			new ServiceContext());

		repositoryIds[0] = repository.getRepositoryId();

		DLFolder dlFolder = DLFolderServiceUtil.addFolder(
			null, _group.getGroupId(), _group.getGroupId(), false,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new ServiceContext());

		repository = RepositoryLocalServiceUtil.addRepository(
			null, TestPropsValues.getUserId(), _group.getGroupId(), classNameId,
			dlFolder.getFolderId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new UnicodeProperties(), true, new ServiceContext());

		repositoryIds[1] = repository.getRepositoryId();

		DLAppLocalServiceUtil.deleteAllRepositories(_group.getGroupId());

		for (long repositoryId : repositoryIds) {
			try {
				RepositoryProviderUtil.getLocalRepository(repositoryId);

				Assert.fail(
					"Should not be able to access repository " + repositoryId);
			}
			catch (Exception exception) {
			}
		}
	}

	@Test
	public void testFileEntriesAreDeletedWhenDeletingAllRepositories()
		throws Exception {

		Repository dlRepository = RepositoryLocalServiceUtil.addRepository(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			PortalUtil.getClassNameId(LiferayRepository.class),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new UnicodeProperties(), true,
			new ServiceContext());

		long[] fileEntryIds = new long[2];

		long[] primaryKeys = populateRepository(dlRepository.getRepositoryId());

		fileEntryIds[0] = primaryKeys[0];
		fileEntryIds[1] = primaryKeys[2];

		DLAppLocalServiceUtil.deleteAllRepositories(_group.getGroupId());

		for (long fileEntryId : fileEntryIds) {
			try {
				LocalRepository localRepository =
					RepositoryProviderUtil.getLocalRepository(
						dlRepository.getRepositoryId());

				localRepository.getFileEntry(fileEntryId);

				Assert.fail(
					StringBundler.concat(
						"Should not be able to get file entry ", fileEntryId,
						" from repository ", dlRepository.getRepositoryId()));
			}
			catch (Exception exception) {
			}
		}
	}

	@Test
	public void testGetMountFoldersCountWithHiddenRepository()
		throws Exception {

		RepositoryLocalServiceUtil.addRepository(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			PortalUtil.getClassNameId(LiferayRepository.class),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new UnicodeProperties(), true,
			new ServiceContext());

		Assert.assertEquals(
			0,
			DLFolderServiceUtil.getMountFoldersCount(
				_group.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID));
	}

	@Test
	public void testGetMountFoldersCountWithNotHiddenRepository()
		throws Exception {

		RepositoryLocalServiceUtil.addRepository(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			PortalUtil.getClassNameId(LiferayRepository.class),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new UnicodeProperties(), false,
			new ServiceContext());

		Assert.assertEquals(
			1,
			DLFolderServiceUtil.getMountFoldersCount(
				_group.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID));
	}

	protected long[] populateRepository(long repositoryId) throws Exception {
		InputStream inputStream = new UnsyncByteArrayInputStream(
			_TEST_CONTENT.getBytes());

		LocalRepository localRepository =
			RepositoryProviderUtil.getLocalRepository(repositoryId);

		FileEntry fileEntry = localRepository.addFileEntry(
			null, TestPropsValues.getUserId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, inputStream,
			_TEST_CONTENT.length(), null, null, null, new ServiceContext());

		Folder folder = localRepository.addFolder(
			null, TestPropsValues.getUserId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new ServiceContext());

		inputStream = new UnsyncByteArrayInputStream(_TEST_CONTENT.getBytes());

		FileEntry folderFileEntry = localRepository.addFileEntry(
			null, TestPropsValues.getUserId(), folder.getFolderId(),
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, inputStream,
			_TEST_CONTENT.length(), null, null, null, new ServiceContext());

		return new long[] {
			fileEntry.getFileEntryId(), folder.getFolderId(),
			folderFileEntry.getFileEntryId()
		};
	}

	private static final String _TEST_CONTENT =
		"LIFERAY\nEnterprise. Open Source. For Life.";

	@DeleteAfterTestRun
	private Group _group;

}