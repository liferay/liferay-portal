/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayActionRequest;
import com.liferay.portal.kernel.portlet.LiferayStateAwareResponse;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockPortletSession;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.ActionRequestFactory;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.Event;
import jakarta.portlet.Portlet;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian I. Kim
 */
@RunWith(Arquillian.class)
@Sync
public class EditUserMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() {
		_originalName = PrincipalThreadLocal.getName();
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
		PrincipalThreadLocal.setName(_originalName);
	}

	@Test
	public void testAddUserPortrait() throws Exception {
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

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), PortletKeys.PORTAL,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			new String[] {ActionKeys.ADD_USER});

		User user = UserTestUtil.addUser();

		_userLocalService.addRoleUsers(
			role.getRoleId(), new long[] {user.getUserId()});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			try {
				ReflectionTestUtil.invoke(
					_mvcActionCommand, "_addUser",
					new Class<?>[] {ActionRequest.class},
					_getMockLiferayPortletActionRequest(fileEntry));

				Assert.fail();
			}
			catch (PrincipalException.MustHavePermission principalException) {
				Assert.assertEquals(
					fileEntry.getFileEntryId(), principalException.resourceId);
			}

			_resourcePermissionLocalService.setResourcePermissions(
				TestPropsValues.getCompanyId(), DLFileEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(fileEntry.getFileEntryId()), role.getRoleId(),
				new String[] {ActionKeys.VIEW});

			user = ReflectionTestUtil.invoke(
				_mvcActionCommand, "_addUser",
				new Class<?>[] {ActionRequest.class},
				_getMockLiferayPortletActionRequest(fileEntry));

			Assert.assertNotEquals(0, user.getPortraitId());
		}
	}

	@Test
	public void testEditUserWithPrefixAndSuffixFields() throws Exception {
		String[] fieldEditableDomainFirstName = PropsUtil.getArray(
			PropsKeys.FIELD_EDITABLE_DOMAINS, new Filter("firstName"));
		String[] fieldEditableDomainPrefix = PropsUtil.getArray(
			PropsKeys.FIELD_EDITABLE_DOMAINS, new Filter("prefix"));
		String[] fieldEditableDomainSuffix = PropsUtil.getArray(
			PropsKeys.FIELD_EDITABLE_DOMAINS, new Filter("suffix"));
		String[] fieldEditableUserTypes = PropsValues.FIELD_EDITABLE_USER_TYPES;

		PropsUtil.set(
			PropsKeys.FIELD_EDITABLE_DOMAINS + "[firstName]", StringPool.STAR);
		PropsUtil.set(
			PropsKeys.FIELD_EDITABLE_DOMAINS + "[prefix]", StringPool.BLANK);
		PropsUtil.set(
			PropsKeys.FIELD_EDITABLE_DOMAINS + "[suffix]", StringPool.BLANK);
		PropsUtil.set(PropsKeys.FIELD_EDITABLE_USER_TYPES, StringPool.BLANK);

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		PermissionChecker adminPermissionChecker =
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser());

		PermissionThreadLocal.setPermissionChecker(adminPermissionChecker);

		Assert.assertTrue(
			UsersAdminUtil.hasUpdateFieldPermission(
				adminPermissionChecker, TestPropsValues.getUser(),
				TestPropsValues.getUser(), "suffix"));

		ListType prefixListType = _listTypeLocalService.getListType(
			TestPropsValues.getCompanyId(), "dr",
			ListTypeConstants.CONTACT_PREFIX);
		ListType suffixListType = _listTypeLocalService.getListType(
			TestPropsValues.getCompanyId(), "ii",
			ListTypeConstants.CONTACT_SUFFIX);

		User user = _userLocalService.addUser(
			0, TestPropsValues.getCompanyId(), false, "test", "test", false,
			RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@example.com", LocaleUtil.US,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), prefixListType.getListTypeId(),
			suffixListType.getListTypeId(), true, 1, 1, 1970, StringPool.BLANK,
			UserConstants.TYPE_REGULAR, null, null, null, null, true,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
				TestPropsValues.getUserId()));

		PermissionChecker userPermissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, userPermissionChecker)) {

			Assert.assertFalse(
				UsersAdminUtil.hasUpdateFieldPermission(
					userPermissionChecker, user, user, "suffix"));

			String firstName = RandomTestUtil.randomString();

			_processAction(
				"/users_admin/edit_user",
				HashMapBuilder.put(
					Constants.CMD, Constants.UPDATE
				).put(
					"firstName", firstName
				).put(
					"p_u_i_d", String.valueOf(user.getUserId())
				).build(),
				userPermissionChecker, user.getUserId());

			user = _userLocalService.getUser(user.getUserId());

			Assert.assertEquals(user.getFirstName(), firstName);

			Contact contact = user.getContact();

			Assert.assertEquals(
				prefixListType.getListTypeId(), contact.getPrefixListTypeId());
			Assert.assertEquals(
				suffixListType.getListTypeId(), contact.getSuffixListTypeId());

			firstName = RandomTestUtil.randomString();

			_processAction(
				"/users_admin/edit_user",
				HashMapBuilder.put(
					Constants.CMD, Constants.UPDATE
				).put(
					"firstName", firstName
				).put(
					"p_u_i_d", String.valueOf(user.getUserId())
				).put(
					"prefixListTypeValue", "Dr"
				).put(
					"suffixListTypeValue", "II"
				).build(),
				userPermissionChecker, user.getUserId());

			user = _userLocalService.getUser(user.getUserId());

			Assert.assertEquals(user.getFirstName(), firstName);

			contact = user.getContact();

			Assert.assertEquals(
				prefixListType.getListTypeId(), contact.getPrefixListTypeId());
			Assert.assertEquals(
				suffixListType.getListTypeId(), contact.getSuffixListTypeId());
		}

		PropsUtil.set(
			PropsKeys.FIELD_EDITABLE_DOMAINS + "[firstName]",
			StringUtil.merge(fieldEditableDomainFirstName, StringPool.COMMA));
		PropsUtil.set(
			PropsKeys.FIELD_EDITABLE_DOMAINS + "[prefix]",
			StringUtil.merge(fieldEditableDomainPrefix, StringPool.COMMA));
		PropsUtil.set(
			PropsKeys.FIELD_EDITABLE_DOMAINS + "[suffix]",
			StringUtil.merge(fieldEditableDomainSuffix, StringPool.COMMA));
		PropsUtil.set(
			PropsKeys.FIELD_EDITABLE_USER_TYPES,
			StringUtil.merge(fieldEditableUserTypes, StringPool.COMMA));
	}

	@Test
	public void testProcessAction() throws Exception {
		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		PermissionChecker adminPermissionChecker =
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser());

		PermissionThreadLocal.setPermissionChecker(adminPermissionChecker);

		_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
			null, TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID, User.class.getName(), 0, 0,
			"Single Approver", 1);

		User user = _userLocalService.addUserWithWorkflow(
			0, TestPropsValues.getCompanyId(), false, "test", "test", false,
			RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@liferay.com", LocaleUtil.US,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), 0, 0, true, 1, 1, 1970,
			StringPool.BLANK, UserConstants.TYPE_REGULAR, null, null, null,
			null, true,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
				TestPropsValues.getUserId()));

		Assert.assertEquals(WorkflowConstants.STATUS_PENDING, user.getStatus());

		_processAction(
			"/users_admin/edit_user",
			HashMapBuilder.put(
				Constants.CMD, Constants.RESTORE
			).put(
				"deleteUserIds", String.valueOf(user.getUserId())
			).build(),
			adminPermissionChecker, TestPropsValues.getUserId());

		List<WorkflowTask> workflowTasks =
			_workflowTaskManager.getWorkflowTasksByWorkflowInstance(
				TestPropsValues.getCompanyId(), null,
				_workflowInstanceLinkLocalService.fetchWorkflowInstanceLink(
					TestPropsValues.getCompanyId(),
					WorkflowConstants.DEFAULT_GROUP_ID, User.class.getName(),
					user.getUserId()
				).getWorkflowInstanceId(),
				null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(workflowTasks.toString(), 1, workflowTasks.size());

		WorkflowTask workflowTask = workflowTasks.get(0);

		Assert.assertTrue(workflowTask.isCompleted());

		user = _userLocalService.getUser(user.getUserId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, user.getStatus());
	}

	@Test
	public void testUpdatePortrait() throws Exception {
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

		_userLocalService.addRoleUsers(
			role.getRoleId(), new long[] {user.getUserId()});

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		Map<String, String> params = HashMapBuilder.put(
			Constants.CMD, Constants.UPDATE
		).put(
			"fileEntryId", String.valueOf(fileEntry.getFileEntryId())
		).put(
			"p_u_i_d", String.valueOf(user.getUserId())
		).build();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			_processAction(
				"/users_admin/edit_user", params, permissionChecker,
				user.getUserId());

			user = _userLocalService.getUser(user.getUserId());

			Assert.assertEquals(0, user.getPortraitId());

			_resourcePermissionLocalService.setResourcePermissions(
				TestPropsValues.getCompanyId(), DLFileEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(fileEntry.getFileEntryId()), role.getRoleId(),
				new String[] {ActionKeys.VIEW});

			_processAction(
				"/users_admin/edit_user", params, permissionChecker,
				user.getUserId());

			user = _userLocalService.getUser(user.getUserId());

			Assert.assertNotEquals(0, user.getPortraitId());
		}
	}

	@Test
	public void testUpdateUser() throws Exception {
		ListType listType = _listTypeLocalService.createListType(
			Integer.MAX_VALUE + 1L);

		listType.setCompanyId(TestPropsValues.getCompanyId());
		listType.setName(StringUtil.toLowerCase(RandomTestUtil.randomString()));
		listType.setType(ListTypeConstants.CONTACT_PREFIX);

		_listType = _listTypeLocalService.addListType(listType);

		_user = UserTestUtil.addUser();

		_processAction(
			"/users_admin/edit_user",
			HashMapBuilder.put(
				Constants.CMD, Constants.UPDATE
			).put(
				"p_u_i_d", String.valueOf(_user.getUserId())
			).put(
				"prefixListTypeValue", _listType.getName()
			).build(),
			PermissionThreadLocal.getPermissionChecker(),
			TestPropsValues.getUserId());

		User user = _userLocalService.getUser(_user.getUserId());

		Contact contact = user.getContact();

		Assert.assertEquals(
			_listType.getListTypeId(), contact.getPrefixListTypeId());
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			FileEntry fileEntry)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, UsersAdminPortletKeys.USERS_ADMIN);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			_getThemeDisplay(PermissionThreadLocal.getPermissionChecker()));
		mockLiferayPortletActionRequest.setParameter("birthdayDay", "1");
		mockLiferayPortletActionRequest.setParameter("birthdayYear", "1970");
		mockLiferayPortletActionRequest.setParameter(
			"emailAddress",
			StringUtil.toLowerCase(RandomTestUtil.randomString()) +
				"@liferay.com");
		mockLiferayPortletActionRequest.setParameter(
			"fileEntryId", String.valueOf(fileEntry.getFileEntryId()));
		mockLiferayPortletActionRequest.setParameter(
			"firstName", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.setParameter(
			"lastName", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.setParameter(
			"screenName",
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		mockLiferayPortletActionRequest.setPortletSession(
			new MockPortletSession());

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay(PermissionChecker permissionChecker)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		Layout layout = _layoutLocalService.getLayout(
			TestPropsValues.getPlid());

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());

		themeDisplay.setPermissionChecker(permissionChecker);
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _processAction(
			String actionName, Map<String, String> params,
			PermissionChecker permissionChecker, long userId)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			ActionRequest.ACTION_NAME, actionName);

		for (Map.Entry<String, String> entry : params.entrySet()) {
			mockLiferayPortletActionRequest.addParameter(
				entry.getKey(), entry.getValue());
		}

		mockLiferayPortletActionRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST,
			mockLiferayPortletActionRequest.getHttpServletRequest());
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, UsersAdminPortletKeys.USERS_ADMIN);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(permissionChecker));
		mockLiferayPortletActionRequest.setAttribute(WebKeys.USER_ID, userId);

		LiferayActionRequest liferayActionRequest = ActionRequestFactory.create(
			mockLiferayPortletActionRequest.getHttpServletRequest(),
			_portletLocalService.getPortletById(
				UsersAdminPortletKeys.USERS_ADMIN),
			null, null, null, null, null, TestPropsValues.getPlid());

		liferayActionRequest.setPortletRequestDispatcherRequest(
			mockLiferayPortletActionRequest.getHttpServletRequest());

		_portlet.processAction(
			liferayActionRequest,
			new EditUserMVCActionCommandTest.
				CustomMockLiferayPortletActionResponse());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@DeleteAfterTestRun
	private ListType _listType;

	@Inject
	private ListTypeLocalService _listTypeLocalService;

	@Inject(filter = "mvc.command.name=/users_admin/edit_user")
	private MVCActionCommand _mvcActionCommand;

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;

	@Inject(
		filter = "component.name=com.liferay.users.admin.web.internal.portlet.UsersAdminPortlet"
	)
	private Portlet _portlet;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

	private class CustomMockLiferayPortletActionResponse
		extends MockLiferayPortletActionResponse
		implements LiferayStateAwareResponse {

		@Override
		public List<Event> getEvents() {
			return Collections.emptyList();
		}

		@Override
		public String getRedirectLocation() {
			return StringPool.BLANK;
		}

		@Override
		public boolean isCalledSetRenderParameter() {
			return false;
		}

	}

}