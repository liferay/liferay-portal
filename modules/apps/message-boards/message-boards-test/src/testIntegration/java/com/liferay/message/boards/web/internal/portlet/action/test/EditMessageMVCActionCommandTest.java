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
		_testProcessActionWithDifferentUserTempFileEntry();
		_testProcessActionWithFileEntry();
		_testProcessActionWithTempFileEntry();
	}

	private MBMessage _addMessage(long fileEntryId, Group group)
		throws Exception {

		return _addMessage(
			fileEntryId, group,
			UserTestUtil.addGroupUser(group, RoleConstants.SITE_MEMBER));
	}

	private MBMessage _addMessage(long fileEntryId, Group group, User user)
		throws Exception {

		UserTestUtil.setUser(user);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				fileEntryId, LayoutTestUtil.addTypePortletLayout(group), user);

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertTrue(
			SessionErrors.isEmpty(mockLiferayPortletActionRequest));

		List<MBMessage> messages = _mbMessageLocalService.getGroupMessages(
			group.getGroupId(), WorkflowConstants.STATUS_ANY, 0, 1);

		Assert.assertEquals(messages.toString(), 1, messages.size());

		return messages.get(0);
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
			long fileEntryId, Layout layout, User user)
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
			"portletResource", MBPortletKeys.MESSAGE_BOARDS);
		mockLiferayPortletActionRequest.setParameter(
			"subject", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.setParameter(
			Constants.CMD, Constants.ADD);
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

	private void _testProcessActionWithDifferentUserTempFileEntry()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		FileEntry tempFileEntry = _addTempFileEntry(
			group, UserTestUtil.addGroupUser(group, RoleConstants.SITE_MEMBER));

		MBMessage message = _addMessage(tempFileEntry.getFileEntryId(), group);

		Assert.assertEquals(0, message.getAttachmentsFileEntriesCount());

		Assert.assertNotNull(
			_dlFileEntryLocalService.fetchDLFileEntry(
				tempFileEntry.getFileEntryId()));
	}

	private void _testProcessActionWithFileEntry() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomBytes(), null, null, null, serviceContext);

		MBMessage message = _addMessage(
			fileEntry.getFileEntryId(), GroupTestUtil.addGroup());

		Assert.assertEquals(0, message.getAttachmentsFileEntriesCount());

		Assert.assertNotNull(
			_dlFileEntryLocalService.fetchDLFileEntry(
				fileEntry.getFileEntryId()));
	}

	private void _testProcessActionWithTempFileEntry() throws Exception {
		Group group = GroupTestUtil.addGroup();

		User user = UserTestUtil.addGroupUser(group, RoleConstants.SITE_MEMBER);

		FileEntry tempFileEntry = _addTempFileEntry(group, user);

		MBMessage message = _addMessage(
			tempFileEntry.getFileEntryId(), group, user);

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