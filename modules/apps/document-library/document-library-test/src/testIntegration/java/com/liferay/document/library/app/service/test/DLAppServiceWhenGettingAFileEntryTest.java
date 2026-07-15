/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.app.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.app.service.test.util.DLAppServiceTestUtil;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileVersionLocalServiceUtil;
import com.liferay.document.library.test.util.BaseDLAppTestCase;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.Date;

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
public class DLAppServiceWhenGettingAFileEntryTest extends BaseDLAppTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_contentReviewerUser = UserTestUtil.addUser(group.getGroupId());

		RoleLocalServiceUtil.addUserRole(
			_contentReviewerUser.getUserId(),
			RoleLocalServiceUtil.getRole(
				_contentReviewerUser.getCompanyId(),
				RoleConstants.PORTAL_CONTENT_REVIEWER));

		_siteMemberUser = UserTestUtil.addUser(group.getGroupId());
	}

	@Test
	public void testFetchFileEntry() throws Exception {
		_testFetchFileEntryNotPresentInRootFolder();
		_testFetchFileEntryPresentInRootFolder();
	}

	@Test
	public void testGetExpiredFileEntryAsContentReviewer() throws Exception {
		FileEntry fileEntry = DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_expireFileEntry(fileEntry);

		UserTestUtil.setUser(_contentReviewerUser);

		fileEntry = dlAppService.getFileEntry(fileEntry.getFileEntryId());

		FileVersion fileVersion = fileEntry.getFileVersion();

		Assert.assertTrue(fileVersion.isExpired());
	}

	@Test
	public void testGetExpiredFileEntryAsOwner() throws Exception {
		UserTestUtil.setUser(_siteMemberUser);

		FileEntry fileEntry = DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_expireFileEntry(fileEntry);

		fileEntry = dlAppService.getFileEntry(fileEntry.getFileEntryId());

		FileVersion fileVersion = fileEntry.getFileVersion();

		Assert.assertTrue(fileVersion.isExpired());
	}

	@Test(expected = NoSuchFileEntryException.class)
	public void testShouldFailIfNotPresentInRootFolder() throws Exception {
		dlAppService.getFileEntry(
			group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString());
	}

	@Test
	public void testShouldReturnItIfExistsInRootFolder() throws Exception {
		FileEntry fileEntry1 = DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		FileEntry fileEntry2 = dlAppService.getFileEntry(
			group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			fileEntry1.getTitle());

		Assert.assertEquals(
			fileEntry1.getFileEntryId(), fileEntry2.getFileEntryId());
	}

	private void _expireFileEntry(FileEntry fileEntry) throws Exception {
		LocalDateTime localDateTime = LocalDateTime.now();

		LocalDateTime expirationLocalDateTime = localDateTime.minusMinutes(1);

		Date expirationDate = Date.from(
			expirationLocalDateTime.toInstant(ZoneOffset.UTC));

		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		dlFileEntry.setExpirationDate(expirationDate);

		dlFileEntry = DLFileEntryLocalServiceUtil.updateDLFileEntry(
			dlFileEntry);

		DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

		dlFileVersion.setExpirationDate(expirationDate);

		DLFileVersionLocalServiceUtil.updateDLFileVersion(dlFileVersion);

		DLFileEntryLocalServiceUtil.checkFileEntries(
			dlFileEntry.getCompanyId(), 60);
	}

	private void _testFetchFileEntryNotPresentInRootFolder() throws Exception {
		Assert.assertNull(
			dlAppService.fetchFileEntry(
				group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				StringUtil.randomString()));
	}

	private void _testFetchFileEntryPresentInRootFolder() throws Exception {
		FileEntry fileEntry1 = DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		FileEntry fileEntry2 = dlAppService.fetchFileEntry(
			group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			fileEntry1.getTitle());

		Assert.assertEquals(
			fileEntry1.getFileEntryId(), fileEntry2.getFileEntryId());
	}

	@DeleteAfterTestRun
	private User _contentReviewerUser;

	@DeleteAfterTestRun
	private User _siteMemberUser;

}