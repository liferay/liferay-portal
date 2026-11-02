/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.attachments.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBCategoryServiceUtil;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.message.boards.test.util.MBTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Julio Camarero
 * @author Roberto Díaz
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class MBAttachmentsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		addGroup();
	}

	@Test
	public void testDeleteAttachmentsWhenDeletingMessage() throws Exception {
		int initialFileEntriesCount =
			DLFileEntryLocalServiceUtil.getFileEntriesCount();

		addMessageAttachment();

		_message = replyMessage();

		addMessageAttachment();

		Assert.assertEquals(
			initialFileEntriesCount + 2,
			DLFileEntryLocalServiceUtil.getFileEntriesCount());

		MBMessageLocalServiceUtil.deleteMessage(_message);

		Assert.assertEquals(
			initialFileEntriesCount + 1,
			DLFileEntryLocalServiceUtil.getFileEntriesCount());
	}

	@Test
	public void testDeleteAttachmentsWhenDeletingRootMessage()
		throws Exception {

		int initialFileEntriesCount =
			DLFileEntryLocalServiceUtil.getFileEntriesCount();

		addMessageAttachment();

		Assert.assertEquals(
			initialFileEntriesCount + 1,
			DLFileEntryLocalServiceUtil.getFileEntriesCount());

		MBMessageLocalServiceUtil.deleteMessage(_message);

		Assert.assertEquals(
			initialFileEntriesCount,
			DLFileEntryLocalServiceUtil.getFileEntriesCount());
	}

	@Test
	public void testFoldersCountWhenAddingAttachmentsToSameMessage()
		throws Exception {

		int initialFoldersCount = DLFolderLocalServiceUtil.getDLFoldersCount();

		addMessageAttachment();

		int foldersCount = DLFolderLocalServiceUtil.getDLFoldersCount();

		Assert.assertEquals(initialFoldersCount + 3, foldersCount);

		addMessageAttachment();

		foldersCount = DLFolderLocalServiceUtil.getDLFoldersCount();

		Assert.assertEquals(initialFoldersCount + 3, foldersCount);
	}

	@Test
	public void testFoldersCountWhenAddingMessage() throws Exception {
		int initialFoldersCount = DLFolderLocalServiceUtil.getDLFoldersCount();

		addMessage();

		_message = replyMessage();

		Assert.assertEquals(
			initialFoldersCount, DLFolderLocalServiceUtil.getDLFoldersCount());
	}

	@Test
	public void testFoldersCountWhenAddingMessageAttachment() throws Exception {
		int initialFoldersCount = DLFolderLocalServiceUtil.getDLFoldersCount();

		addMessageAttachment();

		Assert.assertEquals(
			initialFoldersCount + 3,
			DLFolderLocalServiceUtil.getDLFoldersCount());
	}

	@Test
	public void testFoldersCountWhenAddingMessageAttachments()
		throws Exception {

		int foldersCount = DLFolderLocalServiceUtil.getDLFoldersCount();

		addMessageAttachment();

		Assert.assertEquals(
			foldersCount + 3, DLFolderLocalServiceUtil.getDLFoldersCount());

		foldersCount = DLFolderLocalServiceUtil.getDLFoldersCount();

		_message = replyMessage();

		addMessageAttachment();

		Assert.assertEquals(
			foldersCount + 1, DLFolderLocalServiceUtil.getDLFoldersCount());

		foldersCount = DLFolderLocalServiceUtil.getDLFoldersCount();

		_message = null;

		addMessageAttachment();

		Assert.assertEquals(
			foldersCount + 2, DLFolderLocalServiceUtil.getDLFoldersCount());

		foldersCount = DLFolderLocalServiceUtil.getDLFoldersCount();

		_group = null;
		_message = null;

		addMessageAttachment();

		Assert.assertEquals(
			foldersCount + 3, DLFolderLocalServiceUtil.getDLFoldersCount());
	}

	@Test
	public void testFoldersCountWhenAddingRootMessage() throws Exception {
		int initialFoldersCount = DLFolderLocalServiceUtil.getDLFoldersCount();

		addMessage();

		Assert.assertEquals(
			initialFoldersCount, DLFolderLocalServiceUtil.getDLFoldersCount());
	}

	@Test
	public void testFoldersCountWhenDeletingMessageWithAttachments()
		throws Exception {

		int initialFoldersCount = DLFolderLocalServiceUtil.getDLFoldersCount();

		addMessageAttachment();

		_message = replyMessage();

		addMessageAttachment();

		Assert.assertEquals(
			initialFoldersCount + 4,
			DLFolderLocalServiceUtil.getDLFoldersCount());

		MBMessageLocalServiceUtil.deleteMessage(_message);

		Assert.assertEquals(
			initialFoldersCount + 3,
			DLFolderLocalServiceUtil.getDLFoldersCount());
	}

	@Test
	public void testFoldersCountWhenDeletingMessageWithoutAttachments()
		throws Exception {

		int initialFoldersCount = DLFolderLocalServiceUtil.getDLFoldersCount();

		addMessage();

		_message = replyMessage();

		Assert.assertEquals(
			initialFoldersCount, DLFolderLocalServiceUtil.getDLFoldersCount());

		MBMessageLocalServiceUtil.deleteMessage(_message);

		Assert.assertEquals(
			initialFoldersCount, DLFolderLocalServiceUtil.getDLFoldersCount());
	}

	@Test
	public void testFoldersCountWhenDeletingRootMessageWithAttachments()
		throws Exception {

		int initialFoldersCount = DLFolderLocalServiceUtil.getDLFoldersCount();

		addMessageAttachment();

		Assert.assertEquals(
			initialFoldersCount + 3,
			DLFolderLocalServiceUtil.getDLFoldersCount());

		MBMessageLocalServiceUtil.deleteMessage(_message);

		Assert.assertEquals(
			initialFoldersCount + 1,
			DLFolderLocalServiceUtil.getDLFoldersCount());
	}

	@Test
	public void testFoldersCountWhenDeletingRootMessageWithoutAttachments()
		throws Exception {

		int initialFoldersCount = DLFolderLocalServiceUtil.getDLFoldersCount();

		addMessage();

		Assert.assertEquals(
			initialFoldersCount, DLFolderLocalServiceUtil.getDLFoldersCount());

		MBMessageLocalServiceUtil.deleteMessage(_message);

		Assert.assertEquals(
			initialFoldersCount, DLFolderLocalServiceUtil.getDLFoldersCount());
	}

	@Test
	public void testMoveToTrashAndDeleteMessageAttachment() throws Exception {
		addMessageAttachment();

		_trashMBAttachments(false);
	}

	@Test
	public void testMoveToTrashAndRestoreMessageAttachment() throws Exception {
		addMessageAttachment();

		_trashMBAttachments(true);
	}

	protected void addCategory() throws Exception {
		if (_group == null) {
			addGroup();
		}

		_category = MBCategoryServiceUtil.addCategory(
			null, TestPropsValues.getUserId(),
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			RandomTestUtil.randomString(), StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	protected void addGroup() throws Exception {
		_group = GroupTestUtil.addGroup();

		_groups.add(_group);
	}

	protected void addMessage() throws Exception {
		if (_category == null) {
			addCategory();
		}

		_message = MBTestUtil.addMessage(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			_category.getGroupId(), _category.getCategoryId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	protected void addMessageAttachment() throws Exception {
		if (_message == null) {
			if (_category == null) {
				addCategory();
			}

			User user = TestPropsValues.getUser();

			if (_group == null) {
				addGroup();
			}

			List<ObjectValuePair<String, InputStream>> objectValuePairs =
				MBTestUtil.getInputStreamOVPs(
					"company_logo.png", getClass(), StringPool.BLANK);

			_message = MBMessageLocalServiceUtil.addMessage(
				user.getUserId(), user.getFullName(), _group.getGroupId(),
				MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, "Subject",
				"Body", MBMessageConstants.DEFAULT_FORMAT, objectValuePairs,
				false, 0, false,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), user.getUserId()));
		}
		else {
			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId());

			List<ObjectValuePair<String, InputStream>> objectValuePairs =
				MBTestUtil.getInputStreamOVPs(
					"OSX_Test.docx", getClass(), StringPool.BLANK);

			_message = MBMessageLocalServiceUtil.updateMessage(
				TestPropsValues.getUserId(), _message.getMessageId(), "Subject",
				"Body", objectValuePairs, 0, false, serviceContext);
		}
	}

	protected MBMessage replyMessage() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		List<ObjectValuePair<String, InputStream>> inputStreamOVPs =
			Collections.emptyList();

		return MBMessageLocalServiceUtil.addMessage(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			_message.getGroupId(), _message.getCategoryId(),
			_message.getThreadId(), _message.getMessageId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			MBMessageConstants.DEFAULT_FORMAT, inputStreamOVPs, false, 0.0,
			false, serviceContext);
	}

	private void _trashMBAttachments(boolean restore) throws Exception {
		int initialNotInTrashCount = _message.getAttachmentsFileEntriesCount();
		int initialTrashEntriesCount =
			_message.getDeletedAttachmentsFileEntriesCount();

		List<FileEntry> fileEntries = _message.getAttachmentsFileEntries();

		List<String> existingFiles = new ArrayList<>();

		for (FileEntry fileEntry : fileEntries) {
			existingFiles.add(String.valueOf(fileEntry.getFileEntryId()));
		}

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		String fileName = "OSX_Test.docx";

		List<ObjectValuePair<String, InputStream>> objectValuePairs =
			MBTestUtil.getInputStreamOVPs(
				fileName, getClass(), StringPool.BLANK);

		_message = MBMessageLocalServiceUtil.updateMessage(
			TestPropsValues.getUserId(), _message.getMessageId(), "Subject",
			"Body", objectValuePairs, 0, false, serviceContext);

		Assert.assertEquals(
			initialNotInTrashCount + 1,
			_message.getAttachmentsFileEntriesCount());
		Assert.assertEquals(
			initialTrashEntriesCount,
			_message.getDeletedAttachmentsFileEntriesCount());

		long fileEntryId =
			MBMessageLocalServiceUtil.moveMessageAttachmentToTrash(
				TestPropsValues.getUserId(), _message.getMessageId(), fileName);

		Assert.assertEquals(
			initialNotInTrashCount, _message.getAttachmentsFileEntriesCount());
		Assert.assertEquals(
			initialTrashEntriesCount + 1,
			_message.getDeletedAttachmentsFileEntriesCount());

		if (restore) {
			MBMessageLocalServiceUtil.restoreMessageAttachmentFromTrash(
				TestPropsValues.getUserId(), _message.getMessageId(), fileName);

			Assert.assertEquals(
				initialNotInTrashCount + 1,
				_message.getAttachmentsFileEntriesCount());
			Assert.assertEquals(
				initialTrashEntriesCount,
				_message.getDeletedAttachmentsFileEntriesCount());

			MBMessageLocalServiceUtil.deleteMessageAttachment(
				_message.getMessageId(), fileName);
		}
		else {
			FileEntry fileEntry = PortletFileRepositoryUtil.getPortletFileEntry(
				fileEntryId);

			MBMessageLocalServiceUtil.deleteMessageAttachment(
				_message.getMessageId(), fileEntry.getTitle());

			Assert.assertEquals(
				initialNotInTrashCount,
				_message.getAttachmentsFileEntriesCount());
			Assert.assertEquals(
				initialTrashEntriesCount,
				_message.getDeletedAttachmentsFileEntriesCount());
		}
	}

	private MBCategory _category;
	private Group _group;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	private MBMessage _message;

}