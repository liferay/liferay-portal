/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.app.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.app.service.test.util.DLAppServiceTestUtil;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.BaseDLAppTestCase;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.QueryTerm;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alexander Chow
 */
@RunWith(Arquillian.class)
public class DLAppServiceWhenSearchingFileEntriesTest
	extends BaseDLAppTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testShouldFindFileEntryByAssetTagName() throws Exception {
		String fileName = RandomTestUtil.randomString();

		FileEntry fileEntry = DLAppServiceTestUtil.addFileEntry(
			RandomTestUtil.randomString(), group.getGroupId(),
			parentFolder.getFolderId(), fileName, fileName, null, null, null,
			new String[] {"hello", "world"});

		DLAppServiceTestUtil.search(fileEntry, "hello", true);
		DLAppServiceTestUtil.search(fileEntry, "world", true);
		DLAppServiceTestUtil.search(fileEntry, "liferay", false);
	}

	@Test
	public void testShouldFindFileEntryByAssetTagNameAfterUpdate()
		throws Exception {

		String fileName = RandomTestUtil.randomString();
		String description = StringPool.BLANK;
		String changeLog = StringPool.BLANK;
		byte[] bytes = CONTENT.getBytes();

		String[] assetTagNames = {"hello", "world"};

		FileEntry fileEntry = DLAppServiceTestUtil.addFileEntry(
			RandomTestUtil.randomString(), group.getGroupId(),
			parentFolder.getFolderId(), fileName, fileName, null, null, null,
			assetTagNames);

		assetTagNames = new String[] {"hello", "world", "liferay"};

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		serviceContext.setAssetTagNames(assetTagNames);

		fileEntry = dlAppService.updateFileEntry(
			fileEntry.getFileEntryId(), fileName, ContentTypes.TEXT_PLAIN,
			fileName, StringPool.BLANK, description, changeLog,
			DLVersionNumberIncrease.MINOR, bytes, null, null, null,
			serviceContext);

		DLAppServiceTestUtil.search(fileEntry, "hello", true);
		DLAppServiceTestUtil.search(fileEntry, "world", true);
		DLAppServiceTestUtil.search(fileEntry, "liferay", true);
	}

	@Test
	public void testShouldFindFileEntryInRootFolder() throws Exception {
		DLAppServiceTestUtil.searchFile(
			group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
	}

	@Test
	public void testShouldFindFileEntryInSubfolder() throws Exception {
		DLAppServiceTestUtil.searchFile(
			group.getGroupId(), parentFolder.getFolderId());
	}

	@Test
	@TestInfo("LPD-115721")
	public void testShouldFindViewableFileEntriesOnlyByQuery()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		FileEntry privateFileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			parentFolder.getFolderId(), RandomTestUtil.randomString() + ".txt",
			ContentTypes.TEXT_PLAIN, RandomTestUtil.randomBytes(), null, null,
			null, serviceContext);

		serviceContext = ServiceContextTestUtil.getServiceContext(
			targetGroup.getGroupId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		FileEntry targetGroupFileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), targetGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomBytes(), null, null, null, serviceContext);

		FileEntry fileEntry = DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId());

		User user = UserTestUtil.addUser();

		UserTestUtil.setUser(user);

		_testShouldFindViewableFileEntriesOnlyByQuery(
			fileEntry.getFileEntryId(), 1);
		_testShouldFindViewableFileEntriesOnlyByQuery(
			privateFileEntry.getFileEntryId(), 0);
		_testShouldFindViewableFileEntriesOnlyByQuery(
			targetGroupFileEntry.getFileEntryId(), 0);
		_testShouldFindViewableFileEntriesOnlyByQuery(user.getUserId(), 0);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private void _testShouldFindViewableFileEntriesOnlyByQuery(
			long entryClassPK, int expectedLength)
		throws Exception {

		Hits hits = dlAppService.search(
			group.getGroupId(), new SearchContext(),
			new TermQuery(
				new QueryTerm(
					Field.ENTRY_CLASS_PK, String.valueOf(entryClassPK))));

		Assert.assertEquals(hits.toString(), expectedLength, hits.getLength());
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

}