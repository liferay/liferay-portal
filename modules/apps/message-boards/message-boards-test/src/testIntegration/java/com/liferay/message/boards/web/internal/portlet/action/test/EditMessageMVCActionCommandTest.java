/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockPortletSession;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.ByteArrayInputStream;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class EditMessageMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-115508")
	public void testProcessAction() throws Exception {
		_testProcessActionAddWithDifferentUserTempFileEntry();
		_testProcessActionAddWithFileEntry();
		_testProcessActionAddWithTempFileEntry();
		_testProcessActionUpdateWithDifferentUserTempFileEntry();
		_testProcessActionUpdateWithFileEntry();
		_testProcessActionUpdateWithTempFileEntry();
	}

	private FileEntry _addFileEntry() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomBytes(), null, null, null, serviceContext);
	}

	private MBMessage _addMessage(Group group, User user) throws Exception {
		return _mbMessageLocalService.addMessage(
			user.getUserId(), user.getFullName(), group.getGroupId(),
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			"html", Collections.emptyList(), false, 0, false,
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), user.getUserId()));
	}

	private FileEntry _addTempFileEntry(Group group, User user)
		throws Exception {

		return TempFileEntryUtil.addTempFileEntry(
			group.getGroupId(), user.getUserId(),
			MBMessageConstants.TEMP_FOLDER_NAME,
			RandomTestUtil.randomString() + ".txt",
			new ByteArrayInputStream(RandomTestUtil.randomBytes()),
			ContentTypes.TEXT_PLAIN);
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			String cmd, long fileEntryId, Layout layout, long messageId,
			User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			PortletConfigFactoryUtil.create(
				_portletLocalService.getPortletById(
					MBPortletKeys.MESSAGE_BOARDS),
				null));
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, MBPortletKeys.MESSAGE_BOARDS);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(layout, user));
		mockLiferayPortletActionRequest.setParameter(
			"body",
			StringBundler.concat(
				"[img data-image-id=\"", fileEntryId, "\"]",
				RandomTestUtil.randomString(), "[/img]"));
		mockLiferayPortletActionRequest.setParameter(
			"mbCategoryId",
			String.valueOf(MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID));
		mockLiferayPortletActionRequest.setParameter(
			"messageId", String.valueOf(messageId));
		mockLiferayPortletActionRequest.setParameter(
			"portletResource", MBPortletKeys.MESSAGE_BOARDS);
		mockLiferayPortletActionRequest.setParameter(
			"subject", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.setParameter(Constants.CMD, cmd);
		mockLiferayPortletActionRequest.setPortletSession(
			new MockPortletSession());

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay(Layout layout, User user)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(layout.getCompanyId()));
		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setPlid(layout.getPlid());
		themeDisplay.setScopeGroupId(layout.getGroupId());
		themeDisplay.setSiteGroupId(layout.getGroupId());
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	private MBMessage _processAction(
			String cmd, long fileEntryId, Group group, long messageId,
			User user)
		throws Exception {

		UserTestUtil.setUser(user);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				cmd, fileEntryId, LayoutTestUtil.addTypePortletLayout(group),
				messageId, user);

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertTrue(
			SessionErrors.isEmpty(mockLiferayPortletActionRequest));

		List<MBMessage> groupMessages = _mbMessageLocalService.getGroupMessages(
			group.getGroupId(), WorkflowConstants.STATUS_ANY, 0, 1, null);

		Assert.assertEquals(groupMessages.toString(), 1, groupMessages.size());

		return groupMessages.get(0);
	}

	private void _testProcessActionAddWithDifferentUserTempFileEntry()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		FileEntry tempFileEntry = _addTempFileEntry(
			group, UserTestUtil.addGroupUser(group, RoleConstants.SITE_MEMBER));

		MBMessage message = _processAction(
			Constants.ADD, tempFileEntry.getFileEntryId(), group, 0,
			UserTestUtil.addGroupUser(group, RoleConstants.SITE_MEMBER));

		Assert.assertEquals(0, message.getAttachmentsFileEntriesCount());

		Assert.assertNotNull(
			_dlFileEntryLocalService.fetchDLFileEntry(
				tempFileEntry.getFileEntryId()));
	}

	private void _testProcessActionAddWithFileEntry() throws Exception {
		FileEntry fileEntry = _addFileEntry();

		Group group = GroupTestUtil.addGroup();

		MBMessage message = _processAction(
			Constants.ADD, fileEntry.getFileEntryId(), group, 0,
			UserTestUtil.addGroupUser(group, RoleConstants.SITE_MEMBER));

		Assert.assertEquals(0, message.getAttachmentsFileEntriesCount());

		Assert.assertNotNull(
			_dlFileEntryLocalService.fetchDLFileEntry(
				fileEntry.getFileEntryId()));
	}

	private void _testProcessActionAddWithTempFileEntry() throws Exception {
		Group group = GroupTestUtil.addGroup();

		User user = UserTestUtil.addGroupUser(group, RoleConstants.SITE_MEMBER);

		FileEntry tempFileEntry = _addTempFileEntry(group, user);

		MBMessage message = _processAction(
			Constants.ADD, tempFileEntry.getFileEntryId(), group, 0, user);

		Assert.assertEquals(1, message.getAttachmentsFileEntriesCount());

		Assert.assertNull(
			_dlFileEntryLocalService.fetchDLFileEntry(
				tempFileEntry.getFileEntryId()));
	}

	private void _testProcessActionUpdateWithDifferentUserTempFileEntry()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		FileEntry tempFileEntry = _addTempFileEntry(
			group, UserTestUtil.addGroupUser(group, RoleConstants.SITE_MEMBER));

		User user = UserTestUtil.addGroupUser(group, RoleConstants.SITE_MEMBER);

		MBMessage message = _addMessage(group, user);

		message = _processAction(
			Constants.UPDATE, tempFileEntry.getFileEntryId(), group,
			message.getMessageId(), user);

		Assert.assertEquals(0, message.getAttachmentsFileEntriesCount());

		Assert.assertNotNull(
			_dlFileEntryLocalService.fetchDLFileEntry(
				tempFileEntry.getFileEntryId()));
	}

	private void _testProcessActionUpdateWithFileEntry() throws Exception {
		FileEntry fileEntry = _addFileEntry();

		Group group = GroupTestUtil.addGroup();

		User user = UserTestUtil.addGroupUser(group, RoleConstants.SITE_MEMBER);

		MBMessage message = _addMessage(group, user);

		message = _processAction(
			Constants.UPDATE, fileEntry.getFileEntryId(), group,
			message.getMessageId(), user);

		Assert.assertEquals(0, message.getAttachmentsFileEntriesCount());

		Assert.assertNotNull(
			_dlFileEntryLocalService.fetchDLFileEntry(
				fileEntry.getFileEntryId()));
	}

	private void _testProcessActionUpdateWithTempFileEntry() throws Exception {
		Group group = GroupTestUtil.addGroup();

		User user = UserTestUtil.addGroupUser(group, RoleConstants.SITE_MEMBER);

		FileEntry tempFileEntry = _addTempFileEntry(group, user);

		MBMessage message = _addMessage(group, user);

		message = _processAction(
			Constants.UPDATE, tempFileEntry.getFileEntryId(), group,
			message.getMessageId(), user);

		Assert.assertEquals(1, message.getAttachmentsFileEntriesCount());

		Assert.assertNull(
			_dlFileEntryLocalService.fetchDLFileEntry(
				tempFileEntry.getFileEntryId()));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private MBMessageLocalService _mbMessageLocalService;

	@Inject(filter = "mvc.command.name=/message_boards/edit_message")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private PortletLocalService _portletLocalService;

}