/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.change.tracking.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.base.BaseTable;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Groups_RolesTable;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.Users_RolesTable;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.roles.admin.constants.RolesAdminPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;

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
 * @author Cheryl Tang
 */
@RunWith(Arquillian.class)
public class RolesAdminPortletTest {

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
			0, RolesAdminPortletTest.class.getName(), null);

		_group = GroupTestUtil.addGroup();

		_role = _roleLocalService.getRole(
			_group.getCompanyId(), "Publications User");
		_userGroup = UserGroupTestUtil.addUserGroup(_group.getGroupId());
	}

	@After
	public void tearDown() throws Exception {
		_ctCollectionLocalService.deleteCTCollection(_ctCollection);

		GroupTestUtil.deleteGroup(_group);
	}

	@Test
	public void testEditRoleAssignmentsForUserGroups() throws Exception {
		Groups_RolesTable groups_rolesTable = Groups_RolesTable.INSTANCE;

		Column<Groups_RolesTable, Long> groupIdColumn =
			groups_rolesTable.groupId;

		long groupId = _userGroup.getGroupId();
		long roleId = _role.getRoleId();

		_processAction(
			_ctCollection,
			HashMapBuilder.put(
				"addGroupIds", String.valueOf(groupId)
			).put(
				"roleId", String.valueOf(roleId)
			).build());

		_assertNoCTEntry();

		List<Object> results = _roleLocalService.dslQuery(
			_getDSLQuery(
				groupIdColumn, groups_rolesTable, "groupId", roleId, groupId,
				CTConstants.CT_COLLECTION_ID_PRODUCTION));

		Assert.assertEquals(results.toString(), 1, results.size());

		_processAction(
			_ctCollection,
			HashMapBuilder.put(
				"removeGroupIds", String.valueOf(groupId)
			).put(
				"roleId", String.valueOf(roleId)
			).build());

		_assertNoCTEntry();

		results = _roleLocalService.dslQuery(
			_getDSLQuery(
				groupIdColumn, groups_rolesTable, "groupId", roleId, groupId,
				CTConstants.CT_COLLECTION_ID_PRODUCTION));

		Assert.assertEquals(results.toString(), 0, results.size());
	}

	@Test
	public void testEditRoleAssignmentsForUsers() throws Exception {
		Users_RolesTable users_rolesTable = Users_RolesTable.INSTANCE;

		Column<Users_RolesTable, Long> userIdColumn = users_rolesTable.userId;

		long roleId = _role.getRoleId();
		long userId = TestPropsValues.getUserId();

		_processAction(
			_ctCollection,
			HashMapBuilder.put(
				"addUserIds", String.valueOf(userId)
			).put(
				"roleId", String.valueOf(roleId)
			).build());

		_assertNoCTEntry();

		List<Object> results = _roleLocalService.dslQuery(
			_getDSLQuery(
				userIdColumn, users_rolesTable, "userId", roleId, userId,
				CTConstants.CT_COLLECTION_ID_PRODUCTION));

		Assert.assertEquals(results.toString(), 1, results.size());

		_processAction(
			_ctCollection,
			HashMapBuilder.put(
				"removeUserIds", String.valueOf(userId)
			).put(
				"roleId", String.valueOf(roleId)
			).build());

		_assertNoCTEntry();

		results = _roleLocalService.dslQuery(
			_getDSLQuery(
				userIdColumn, users_rolesTable, "userId", roleId, userId,
				CTConstants.CT_COLLECTION_ID_PRODUCTION));

		Assert.assertEquals(results.toString(), 0, results.size());
	}

	private void _assertNoCTEntry() throws Exception {
		Assert.assertEquals(
			0,
			_ctEntryLocalService.getCTCollectionCTEntriesCount(
				_ctCollection.getCtCollectionId()));
	}

	private DSLQuery _getDSLQuery(
		Expression<?> selectExpression, BaseTable<?> table,
		String rightPKColumnName, long leftPrimaryKey, long rightPrimaryKey,
		long ctCollectionId) {

		Column<?, Long> leftColumn = table.getColumn("roleId", Long.class);
		Column<?, Long> rightColumn = table.getColumn(
			rightPKColumnName, Long.class);
		Column<?, Long> ctCollectionIdColumn = table.getColumn(
			"ctCollectionId", Long.class);
		Column<?, Long> companyIdColumn = table.getColumn(
			"companyId", Long.class);

		return DSLQueryFactoryUtil.selectDistinct(
			selectExpression
		).from(
			table
		).where(
			leftColumn.eq(
				leftPrimaryKey
			).and(
				rightColumn.eq(
					rightPrimaryKey
				).and(
					ctCollectionIdColumn.eq(ctCollectionId)
				).and(
					companyIdColumn.eq(_ctCollection.getCompanyId())
				)
			)
		);
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _processAction(
			CTCollection ctCollection, Map<String, String> params)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		for (Map.Entry<String, String> entry : params.entrySet()) {
			mockLiferayPortletActionRequest.addParameter(
				entry.getKey(), entry.getValue());
		}

		mockLiferayPortletActionRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST,
			mockLiferayPortletActionRequest.getHttpServletRequest());
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, RolesAdminPortletKeys.ROLES_ADMIN);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			ReflectionTestUtil.invoke(
				_portlet, "editRoleAssignments",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse());
		}
	}

	private static CTCollection _ctCollection;

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private static CTEntryLocalService _ctEntryLocalService;

	private static Group _group;

	@Inject
	private static RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private static UserGroup _userGroup;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.roles.admin.web.internal.portlet.RolesAdminPortlet"
	)
	private Portlet _portlet;

	private Role _role;

}