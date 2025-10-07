/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.onedrive.web.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.display.context.DLDisplayContextFactory;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.opener.model.DLOpenerFileEntryReference;
import com.liferay.document.library.opener.service.DLOpenerFileEntryReferenceLocalService;
import com.liferay.document.library.test.util.BaseDLAppTestCase;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
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
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class DLOpenerOneDriveManagerTest {

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
	public void testAddPlaceholderDLOpenerFileEntryReference()
		throws Exception {

		FileEntry fileEntry = DLAppServiceUtil.addFileEntry(
			null, _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, BaseDLAppTestCase.CONTENT.getBytes(), null, null,
			null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		DLOpenerFileEntryReference dlOpenerFileEntryReference =
			_dlOpenerFileEntryReferenceLocalService.
				fetchDLOpenerFileEntryReference("OneDrive", fileEntry);

		Assert.assertNull(dlOpenerFileEntryReference);

		Object fieldValue = ReflectionTestUtil.getFieldValue(
			_dlDisplayContextFactory, "_dlOpenerOneDriveManager");

		ReflectionTestUtil.invoke(
			fieldValue, "_addPlaceholderDLOpenerFileEntryReference",
			new Class<?>[] {FileEntry.class, long.class}, fileEntry,
			TestPropsValues.getUserId());

		dlOpenerFileEntryReference =
			_dlOpenerFileEntryReferenceLocalService.
				fetchDLOpenerFileEntryReference("OneDrive", fileEntry);

		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			dlOpenerFileEntryReference.getFileEntryId());
		Assert.assertEquals(
			"OneDrive", dlOpenerFileEntryReference.getReferenceType());

		ReflectionTestUtil.invoke(
			fieldValue, "_addPlaceholderDLOpenerFileEntryReference",
			new Class<?>[] {FileEntry.class, long.class}, fileEntry,
			TestPropsValues.getUserId());

		Assert.assertEquals(
			dlOpenerFileEntryReference,
			_dlOpenerFileEntryReferenceLocalService.
				fetchDLOpenerFileEntryReference("OneDrive", fileEntry));
	}

	@Inject(
		filter = "component.name=com.liferay.document.library.opener.onedrive.web.internal.display.context.DLOpenerOneDriveDLDisplayContextFactory"
	)
	private DLDisplayContextFactory _dlDisplayContextFactory;

	@Inject
	private DLOpenerFileEntryReferenceLocalService
		_dlOpenerFileEntryReferenceLocalService;

	@DeleteAfterTestRun
	private Group _group;

}