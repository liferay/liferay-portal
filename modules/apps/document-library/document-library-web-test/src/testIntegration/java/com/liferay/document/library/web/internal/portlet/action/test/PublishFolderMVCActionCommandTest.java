/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class PublishFolderMVCActionCommandTest {

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
	@TestInfo({"LPD-113633", "LPS-82532"})
	public void testGetFoldersAndFileEntriesAndFileShortcuts()
		throws Exception {

		Folder folder = _addFolder(DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		FileEntry fileEntry = _addFileEntry(
			folder.getFolderId(), WorkflowConstants.ACTION_PUBLISH);

		Folder subfolder = _addFolder(folder.getFolderId());

		FileEntry subfolderFileEntry = _addFileEntry(
			subfolder.getFolderId(), WorkflowConstants.ACTION_PUBLISH);

		FileEntry draftFileEntry = _addFileEntry(
			folder.getFolderId(), WorkflowConstants.ACTION_SAVE_DRAFT);

		List<StagedModel> stagedModels = ReflectionTestUtil.invoke(
			_mvcActionCommand, "_getFoldersAndFileEntriesAndFileShortcuts",
			new Class<?>[] {Folder.class}, folder);

		Set<Object> primaryKeyObjs = new HashSet<>();

		for (StagedModel stagedModel : stagedModels) {
			primaryKeyObjs.add(stagedModel.getPrimaryKeyObj());
		}

		Assert.assertFalse(
			primaryKeyObjs.contains(draftFileEntry.getFileEntryId()));
		Assert.assertTrue(primaryKeyObjs.contains(fileEntry.getFileEntryId()));
		Assert.assertTrue(primaryKeyObjs.contains(subfolder.getFolderId()));
		Assert.assertTrue(
			primaryKeyObjs.contains(subfolderFileEntry.getFileEntryId()));
		Assert.assertEquals(
			primaryKeyObjs.toString(), 3, primaryKeyObjs.size());
	}

	private FileEntry _addFileEntry(long folderId, int workflowAction)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setWorkflowAction(workflowAction);

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(), folderId,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, RandomTestUtil.randomBytes(), null, null, null,
			serviceContext);
	}

	private Folder _addFolder(long parentFolderId) throws Exception {
		return _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			parentFolderId, RandomTestUtil.randomString(), StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.document.library.web.internal.portlet.action.PublishFolderMVCActionCommand"
	)
	private MVCActionCommand _mvcActionCommand;

}