/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.upgrade.v6_6_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.message.boards.constants.MBConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.test.util.MBTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class DLFileEntryUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		Group group1 = GroupTestUtil.addGroup();

		MBMessage mbMessage1 = MBTestUtil.addMessage(
			group1.getGroupId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Folder folder1 = mbMessage1.addAttachmentsFolder();

		FileEntry fileEntry1 = PortletFileRepositoryUtil.addPortletFileEntry(
			group1.getGroupId(), TestPropsValues.getUserId(),
			MBMessage.class.getName(), 0, MBConstants.SERVICE_NAME,
			folder1.getFolderId(), TestDataConstants.TEST_BYTE_ARRAY,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			false);

		Group group2 = GroupTestUtil.addGroup();

		MBMessage mbMessage2 = MBTestUtil.addMessage(
			group2.getGroupId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Folder folder2 = PortletFileRepositoryUtil.addPortletFolder(
			group1.getGroupId(), TestPropsValues.getUserId(),
			MBConstants.SERVICE_NAME,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			String.valueOf(mbMessage2.getMessageId()),
			ServiceContextTestUtil.getServiceContext(group1.getGroupId()));

		FileEntry fileEntry2 = PortletFileRepositoryUtil.addPortletFileEntry(
			group1.getGroupId(), TestPropsValues.getUserId(),
			MBMessage.class.getName(), 0, MBConstants.SERVICE_NAME,
			folder2.getFolderId(), TestDataConstants.TEST_BYTE_ARRAY,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			false);

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.message.boards.internal.upgrade.v6_6_1." +
				"DLFileEntryUpgradeProcess");

		upgradeProcess.upgrade();

		_multiVMPool.clear();

		DLFileEntry dlFileEntry1 = _dlFileEntryLocalService.getDLFileEntry(
			fileEntry1.getFileEntryId());

		Assert.assertEquals(
			MBMessage.class.getName(), dlFileEntry1.getClassName());
		Assert.assertEquals(
			mbMessage1.getMessageId(), dlFileEntry1.getClassPK());

		DLFileEntry dlFileEntry2 = _dlFileEntryLocalService.getDLFileEntry(
			fileEntry2.getFileEntryId());

		Assert.assertEquals(0, dlFileEntry2.getClassNameId());
		Assert.assertEquals(0, dlFileEntry2.getClassPK());
	}

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject(
		filter = "(&(component.name=com.liferay.message.boards.internal.upgrade.registry.MBServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}