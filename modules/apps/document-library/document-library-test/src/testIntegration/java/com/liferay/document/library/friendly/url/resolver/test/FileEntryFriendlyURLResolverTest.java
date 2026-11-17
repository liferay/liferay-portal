/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.friendly.url.resolver.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.friendly.url.resolver.FileEntryFriendlyURLResolver;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class FileEntryFriendlyURLResolverTest {

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
	@TestInfo("LPD-116142")
	public void testResolveFriendlyURL() throws Exception {
		_testResolveFriendlyURL();
		_testResolveFriendlyURLAfterRevert();
	}

	private FileEntry _addFileEntry(String urlTitle) throws Exception {
		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(),
			ContentTypes.APPLICATION_OCTET_STREAM,
			RandomTestUtil.randomString(), urlTitle,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			(byte[])null, null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private void _testResolveFriendlyURL() throws Exception {
		String urlTitle = RandomTestUtil.randomString();

		_addFileEntry(urlTitle);

		FileEntry fileEntry = _fileEntryFriendlyURLResolver.resolveFriendlyURL(
			_group.getGroupId(), urlTitle);

		Assert.assertNotNull(fileEntry);

		Assert.assertNull(
			_fileEntryFriendlyURLResolver.resolveFriendlyURL(
				_group.getGroupId(), RandomTestUtil.randomString()));
	}

	private void _testResolveFriendlyURLAfterRevert() throws Exception {
		FileEntry fileEntry = _addFileEntry("urltitle1");

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_dlAppService.updateFileEntry(
			fileEntry.getFileEntryId(), RandomTestUtil.randomString(),
			ContentTypes.APPLICATION_OCTET_STREAM,
			RandomTestUtil.randomString(), "urltitle2", StringPool.BLANK,
			StringPool.BLANK, DLVersionNumberIncrease.MAJOR,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			serviceContext);

		_dlAppService.updateFileEntry(
			fileEntry.getFileEntryId(), RandomTestUtil.randomString(),
			ContentTypes.APPLICATION_OCTET_STREAM,
			RandomTestUtil.randomString(), "urltitle3", StringPool.BLANK,
			StringPool.BLANK, DLVersionNumberIncrease.MAJOR,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			serviceContext);

		_dlAppService.revertFileEntry(
			fileEntry.getFileEntryId(), "1.0", serviceContext);

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(FileEntry.class),
				fileEntry.getFileEntryId());

		Assert.assertEquals("urltitle3", friendlyURLEntry.getUrlTitle());

		FileEntry resolvedFileEntry1 =
			_fileEntryFriendlyURLResolver.resolveFriendlyURL(
				_group.getGroupId(), "urltitle1");

		Assert.assertEquals(
			fileEntry.getFileEntryId(), resolvedFileEntry1.getFileEntryId());

		FileEntry resolvedFileEntry2 =
			_fileEntryFriendlyURLResolver.resolveFriendlyURL(
				_group.getGroupId(), "urltitle2");

		Assert.assertEquals(
			fileEntry.getFileEntryId(), resolvedFileEntry2.getFileEntryId());

		FileEntry resolvedFileEntry3 =
			_fileEntryFriendlyURLResolver.resolveFriendlyURL(
				_group.getGroupId(), "urltitle3");

		Assert.assertEquals(
			fileEntry.getFileEntryId(), resolvedFileEntry3.getFileEntryId());
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLAppService _dlAppService;

	@Inject
	private FileEntryFriendlyURLResolver _fileEntryFriendlyURLResolver;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

}