/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.change.tracking.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.Users_UserGroupsTable;
import com.liferay.portal.kernel.portlet.LiferayActionRequest;
import com.liferay.portal.kernel.portlet.LiferayStateAwareResponse;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.ActionRequestFactory;
import com.liferay.user.groups.admin.constants.UserGroupsAdminPortletKeys;

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
 * @author Noor Najjar
 */
@RunWith(Arquillian.class)
public class UserGroupsAdminPortletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		_group = GroupTestUtil.addGroup();

		_layout = _layoutLocalService.getLayout(TestPropsValues.getPlid());

		_user = UserTestUtil.addUser();
		_userGroup = UserGroupTestUtil.addUserGroup(_group.getGroupId());
	}

	@After
	public void tearDown() throws Exception {
		_ctCollectionLocalService.deleteCTCollection(_ctCollection);

		GroupTestUtil.deleteGroup(_group);
	}

	@Test
	public void testDeleteUserGroups() throws Exception {
		UserGroup userGroup = UserGroupTestUtil.addUserGroup(
			_group.getGroupId());

		long userGroupId = userGroup.getUserGroupId();

		_processAction(
			"deleteUserGroups", _ctCollection,
			HashMapBuilder.put(
				"deleteUserGroupIds", String.valueOf(userGroupId)
			).build());

		_assertNoCTEntry();

		Assert.assertNull(_userGroupLocalService.fetchUserGroup(userGroupId));
	}

	@Test
	public void testEditUserGroup() throws Exception {
		String name = RandomTestUtil.randomString();

		_processAction(
			"editUserGroup", _ctCollection,
			HashMapBuilder.put(
				"description", RandomTestUtil.randomString()
			).put(
				"name", name
			).put(
				"userGroupId", String.valueOf(_userGroup.getUserGroupId())
			).build());

		_assertNoCTEntry();

		UserGroup userGroup = _userGroupLocalService.getUserGroup(
			_userGroup.getUserGroupId());

		Assert.assertEquals(name, userGroup.getName());
	}

	@Test
	public void testEditUserGroupAssignments() throws Exception {
		long userGroupId = _userGroup.getUserGroupId();
		long userId = _user.getUserId();

		_processAction(
			"editUserGroupAssignments", _ctCollection,
			HashMapBuilder.put(
				"addUserIds", String.valueOf(userId)
			).put(
				"userGroupId", String.valueOf(userGroupId)
			).build());

		_assertNoCTEntry();

		List<Object> results = _getProductionTableMappings(userGroupId, userId);

		Assert.assertEquals(results.toString(), 1, results.size());

		_processAction(
			"editUserGroupAssignments", _ctCollection,
			HashMapBuilder.put(
				"removeUserIds", String.valueOf(userId)
			).put(
				"userGroupId", String.valueOf(userGroupId)
			).build());

		_assertNoCTEntry();

		results = _getProductionTableMappings(userGroupId, userId);

		Assert.assertEquals(results.toString(), 0, results.size());
	}

	private void _assertNoCTEntry() throws Exception {
		Assert.assertEquals(
			0,
			_ctEntryLocalService.getCTCollectionCTEntriesCount(
				_ctCollection.getCtCollectionId()));
	}

	private List<Object> _getProductionTableMappings(
		long userGroupId, long userId) {

		Users_UserGroupsTable users_userGroupsTable =
			Users_UserGroupsTable.INSTANCE;

		return _userGroupLocalService.dslQuery(
			DSLQueryFactoryUtil.selectDistinct(
				users_userGroupsTable.userId
			).from(
				users_userGroupsTable
			).where(
				users_userGroupsTable.userGroupId.eq(
					userGroupId
				).and(
					users_userGroupsTable.userId.eq(userId)
				).and(
					users_userGroupsTable.ctCollectionId.eq(
						CTConstants.CT_COLLECTION_ID_PRODUCTION)
				).and(
					users_userGroupsTable.companyId.eq(
						_ctCollection.getCompanyId())
				)
			));
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(_layout.getLayoutSet());
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)_layout.getLayoutType());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _processAction(
			String actionName, CTCollection ctCollection,
			Map<String, String> params)
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
		mockLiferayPortletActionRequest.setAttribute(WebKeys.LAYOUT, _layout);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, UserGroupsAdminPortletKeys.USER_GROUPS_ADMIN);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		LiferayActionRequest liferayActionRequest = ActionRequestFactory.create(
			mockLiferayPortletActionRequest.getHttpServletRequest(),
			_portletLocalService.getPortletById(
				UserGroupsAdminPortletKeys.USER_GROUPS_ADMIN),
			null, null, null, null, null, TestPropsValues.getPlid());

		liferayActionRequest.setPortletRequestDispatcherRequest(
			mockLiferayPortletActionRequest.getHttpServletRequest());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			_portlet.processAction(
				liferayActionRequest,
				new CustomMockLiferayPortletActionResponse());
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTEntryLocalService _ctEntryLocalService;

	private Group _group;
	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(
		filter = "component.name=com.liferay.user.groups.admin.web.internal.portlet.UserGroupsAdminPortlet"
	)
	private Portlet _portlet;

	@Inject
	private PortletLocalService _portletLocalService;

	@DeleteAfterTestRun
	private User _user;

	@DeleteAfterTestRun
	private UserGroup _userGroup;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

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