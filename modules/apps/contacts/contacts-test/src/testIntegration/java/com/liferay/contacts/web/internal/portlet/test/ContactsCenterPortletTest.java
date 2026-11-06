/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.contacts.web.internal.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.contacts.constants.SocialRelationConstants;
import com.liferay.contacts.model.Entry;
import com.liferay.contacts.service.EntryLocalService;
import com.liferay.contacts.web.internal.constants.ContactsPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;
import com.liferay.social.kernel.service.SocialRelationLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.Portlet;
import jakarta.portlet.ResourceServingPortlet;

import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Tancredi Covioli
 */
@RunWith(Arquillian.class)
public class ContactsCenterPortletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testProcessAction() throws Exception {
		User user = UserTestUtil.addUser();

		Entry entry1 = _entryLocalService.addEntry(
			user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			String emailAddress =
				RandomTestUtil.randomString() + "@liferay.com";

			JSONObject jsonObject = _processAction(emailAddress, entry1, user);

			Assert.assertTrue(jsonObject.getBoolean("success"));

			entry1 = _entryLocalService.getEntry(entry1.getEntryId());

			Assert.assertEquals(emailAddress, entry1.getEmailAddress());
		}

		user = UserTestUtil.addUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			JSONObject jsonObject = _processAction(
				RandomTestUtil.randomString() + "@liferay.com", entry1, user);

			Assert.assertFalse(jsonObject.getBoolean("success"));

			Entry entry2 = _entryLocalService.getEntry(entry1.getEntryId());

			Assert.assertEquals(
				entry1.getEmailAddress(), entry2.getEmailAddress());
		}

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), Entry.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			ActionKeys.VIEW);

		_userLocalService.addRoleUser(role.getRoleId(), user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			JSONObject jsonObject = _processAction(
				RandomTestUtil.randomString() + "@liferay.com", entry1, user);

			Assert.assertTrue(jsonObject.getBoolean("success"));

			Entry entry2 = _entryLocalService.getEntry(entry1.getEntryId());

			Assert.assertEquals(
				entry1.getEmailAddress(), entry2.getEmailAddress());
		}
	}

	@Test
	public void testRender() throws Exception {
		Group group = GroupTestUtil.addGroup();

		Layout layout = _layoutLocalService.createLayout(
			RandomTestUtil.nextLong());

		layout.setGroupId(group.getGroupId());

		Assert.assertFalse(
			SessionErrors.contains(
				_render(layout, _membersPortlet, null, 0),
				PrincipalException.class));

		User user1 = UserTestUtil.addUser(group.getGroupId());

		Assert.assertFalse(
			SessionErrors.contains(
				_render(layout, _membersPortlet, null, user1.getUserId()),
				PrincipalException.class));
		Assert.assertTrue(
			SessionErrors.contains(
				_render(layout, _portlet, null, user1.getUserId()),
				PrincipalException.class));

		User user2 = UserTestUtil.addUser();

		Assert.assertTrue(
			SessionErrors.contains(
				_render(layout, _membersPortlet, null, user2.getUserId()),
				PrincipalException.class));

		layout.setGroupId(user1.getGroupId());

		Assert.assertTrue(
			SessionErrors.contains(
				_render(layout, _membersPortlet, null, user2.getUserId()),
				PrincipalException.class));

		_socialRelationLocalService.addRelation(
			user1.getUserId(), user2.getUserId(),
			SocialRelationConstants.TYPE_BI_CONNECTION);

		Assert.assertFalse(
			SessionErrors.contains(
				_render(layout, _membersPortlet, null, user2.getUserId()),
				PrincipalException.class));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user1, PermissionCheckerFactoryUtil.create(user1))) {

			Assert.assertFalse(
				SessionErrors.contains(
					_render(layout, _portlet, user1, user2.getUserId()),
					PrincipalException.class));
		}

		_userLocalService.updateStatus(
			user2.getUserId(), WorkflowConstants.STATUS_INACTIVE,
			new ServiceContext());

		Assert.assertTrue(
			SessionErrors.contains(
				_render(layout, _membersPortlet, null, user2.getUserId()),
				PrincipalException.class));
	}

	@Test
	public void testServeResource() throws Exception {
		User targetUser = UserTestUtil.addUser();

		MockLiferayResourceResponse mockLiferayResourceResponse =
			_serveResource(targetUser, null);

		Assert.assertEquals("", _getContent(mockLiferayResourceResponse));
		Assert.assertEquals(
			HttpServletResponse.SC_FORBIDDEN,
			_getStatus(mockLiferayResourceResponse));

		User user = UserTestUtil.addUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				_getContent(_serveResource(targetUser, user)));

			Assert.assertTrue(jsonObject.getBoolean("success"));

			JSONObject userJSONObject = jsonObject.getJSONObject("user");

			Assert.assertEquals(
				targetUser.getEmailAddress(),
				userJSONObject.getString("emailAddress"));
		}
	}

	@Test
	public void testUpdateProfile() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".png", ContentTypes.IMAGE_PNG,
			DLTestUtil.getImageBytes("png"), null, null, null, serviceContext);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		User user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			JSONObject jsonObject = _updateProfile(fileEntry, user);

			Assert.assertFalse(jsonObject.getBoolean("success"));

			user = _userLocalService.getUser(user.getUserId());

			Assert.assertEquals(0, user.getPortraitId());
		}

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), DLFileEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(fileEntry.getFileEntryId()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			JSONObject jsonObject = _updateProfile(fileEntry, user);

			Assert.assertTrue(jsonObject.getBoolean("success"));

			user = _userLocalService.getUser(user.getUserId());

			Assert.assertNotEquals(0, user.getPortraitId());
		}
	}

	private ThemeDisplay _createThemeDisplay(User user) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());

		Group group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setSiteGroupId(group.getGroupId());

		if (user != null) {
			themeDisplay.setSignedIn(true);
			themeDisplay.setUser(user);
		}

		return themeDisplay;
	}

	private String _getContent(
			MockLiferayResourceResponse mockLiferayResourceResponse)
		throws Exception {

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		return new String(byteArrayOutputStream.toByteArray());
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			String emailAddress, Entry entry, User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _createThemeDisplay(user));
		mockLiferayPortletActionRequest.setParameter(
			ActionRequest.ACTION_NAME, "updateEntry");
		mockLiferayPortletActionRequest.setParameter(
			"comments", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.setParameter(
			"emailAddress", emailAddress);
		mockLiferayPortletActionRequest.setParameter(
			"entryId", String.valueOf(entry.getEntryId()));
		mockLiferayPortletActionRequest.setParameter(
			"fullName", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.setParameter(
			"redirect", RandomTestUtil.randomString());

		return mockLiferayPortletActionRequest;
	}

	private int _getStatus(
		MockLiferayResourceResponse mockLiferayResourceResponse) {

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayResourceResponse.getHttpServletResponse();

		return mockHttpServletResponse.getStatus();
	}

	private JSONObject _processAction(
			String emailAddress, Entry entry, User user)
		throws Exception {

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		_portlet.processAction(
			_getMockLiferayPortletActionRequest(emailAddress, entry, user),
			mockLiferayPortletActionResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		return _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());
	}

	private MockLiferayPortletRenderRequest _render(
			Layout layout, Portlet portlet, User user, long userId)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX +
					"/error.jsp",
			new MockLiferayPortletContext("/error.jsp"));
		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX +
					"/view_user.jsp",
			new MockLiferayPortletContext("/view_user.jsp"));

		ThemeDisplay themeDisplay = _createThemeDisplay(user);

		themeDisplay.setLayout(layout);
		themeDisplay.setScopeGroupId(layout.getGroupId());

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletRenderRequest.setParameter(
			"mvcPath", "/view_user.jsp");
		mockLiferayPortletRenderRequest.setParameter(
			"userId", String.valueOf(userId));

		portlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return mockLiferayPortletRenderRequest;
	}

	private MockLiferayResourceResponse _serveResource(
			User targetUser, User user)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _createThemeDisplay(user));
		mockLiferayResourceRequest.setResourceID("getContact");
		mockLiferayResourceRequest.setParameter(
			"userId", String.valueOf(targetUser.getUserId()));

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		ResourceServingPortlet resourceServingPortlet =
			(ResourceServingPortlet)_portlet;

		resourceServingPortlet.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		return mockLiferayResourceResponse;
	}

	private JSONObject _updateProfile(FileEntry fileEntry, User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _createThemeDisplay(user));
		mockLiferayPortletActionRequest.setParameter(
			ActionRequest.ACTION_NAME, "updateFieldGroup");
		mockLiferayPortletActionRequest.setParameter("fieldGroup", "details");
		mockLiferayPortletActionRequest.setParameter(
			"fileEntryId", String.valueOf(fileEntry.getFileEntryId()));

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		_portlet.processAction(
			mockLiferayPortletActionRequest, mockLiferayPortletActionResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		return _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private EntryLocalService _entryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(filter = "jakarta.portlet.name=" + ContactsPortletKeys.MEMBERS)
	private Portlet _membersPortlet;

	@Inject(
		filter = "jakarta.portlet.name=" + ContactsPortletKeys.CONTACTS_CENTER
	)
	private Portlet _portlet;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private SocialRelationLocalService _socialRelationLocalService;

	@Inject
	private UserLocalService _userLocalService;

}