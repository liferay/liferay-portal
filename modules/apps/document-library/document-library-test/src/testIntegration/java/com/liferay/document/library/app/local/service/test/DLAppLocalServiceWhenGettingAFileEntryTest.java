/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.app.local.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.test.util.BaseDLAppTestCase;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class DLAppLocalServiceWhenGettingAFileEntryTest
	extends BaseDLAppTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testFetchFileEntry() throws Exception {
		_testFetchFileEntryByFileNameNotPresentInRootFolder();
		_testFetchFileEntryNotPresentInRootFolder();
		_testFetchFileEntryPresentInRootFolder();
	}

	@Test(expected = NoSuchFileEntryException.class)
	public void testShouldFailIfNotPresentInRootFolder() throws Exception {
		DLAppLocalServiceUtil.getFileEntry(
			group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString());
	}

	@Test
	public void testShouldReturnItIfExistsInRootFolder() throws Exception {
		FileEntry fileEntry1 = DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			new byte[0], null, null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		FileEntry fileEntry2 = DLAppLocalServiceUtil.getFileEntry(
			group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			fileEntry1.getTitle());

		Assert.assertEquals(
			fileEntry1.getFileEntryId(), fileEntry2.getFileEntryId());
	}

	private void _testFetchFileEntryByFileNameNotPresentInRootFolder()
		throws Exception {

		Assert.assertNull(
			DLAppLocalServiceUtil.fetchFileEntryByFileName(
				group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				StringUtil.randomString()));
	}

	private void _testFetchFileEntryNotPresentInRootFolder() throws Exception {
		Assert.assertNull(
			DLAppLocalServiceUtil.fetchFileEntry(
				group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				StringUtil.randomString()));
	}

	private void _testFetchFileEntryPresentInRootFolder() throws Exception {
		FileEntry fileEntry1 = DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			new byte[0], null, null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		FileEntry fileEntry2 = DLAppLocalServiceUtil.fetchFileEntry(
			group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			fileEntry1.getTitle());

		Assert.assertEquals(
			fileEntry1.getFileEntryId(), fileEntry2.getFileEntryId());
	}

}