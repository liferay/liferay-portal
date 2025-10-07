/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.sql.dsl.spi.ast.DefaultASTNodeListener;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTable;
import com.liferay.portal.kernel.model.PortletPreferencesTable;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.InlineSQLHelper;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Christopher Kian
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class InlineSQLHelperImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_groupOne = GroupTestUtil.addGroup();
		_groupTwo = GroupTestUtil.addGroup();

		_groupIds = new long[] {_groupOne.getGroupId(), _groupTwo.getGroupId()};

		_user = UserTestUtil.addUser();

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
	}

	@Test
	public void testClauseOrdering() throws Exception {
		_addGroupRole(_groupOne, RoleConstants.SITE_MEMBER);
		_addGroupRole(_groupTwo, RoleConstants.SITE_MEMBER);

		_setPermissionChecker();

		_assertClauseOrdering(_SQL_PLAIN + _SQL_WHERE, _WHERE_CLAUSE);
		_assertClauseOrdering(_SQL_PLAIN + _SQL_GROUP_BY, _GROUP_BY_CLAUSE);
		_assertClauseOrdering(_SQL_PLAIN + _SQL_ORDER_BY, _ORDER_BY_CLAUSE);
		_assertClauseOrdering(
			_SQL_PLAIN + _SQL_WHERE + _SQL_GROUP_BY, _GROUP_BY_CLAUSE);
		_assertClauseOrdering(
			_SQL_PLAIN + _SQL_WHERE + _SQL_ORDER_BY, _ORDER_BY_CLAUSE);
		_assertClauseOrdering(
			_SQL_PLAIN + _SQL_GROUP_BY + _SQL_ORDER_BY, _ORDER_BY_CLAUSE);
		_assertClauseOrdering(
			StringBundler.concat(
				_SQL_PLAIN, _SQL_WHERE, _SQL_GROUP_BY, _SQL_ORDER_BY),
			_ORDER_BY_CLAUSE);
	}

	@Test
	public void testCompanyScope() throws Exception {
		_role = RoleTestUtil.addRole(
			"scopeCompanyRole", RoleConstants.TYPE_REGULAR);

		_roleLocalService.addUserRole(_user.getUserId(), _role);

		_resourcePermissionLocalService.addResourcePermission(
			CompanyThreadLocal.getCompanyId(), _CLASS_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_role.getCompanyId()), _role.getRoleId(),
			ActionKeys.VIEW);

		_setPermissionChecker();

		String sql = _replacePermissionCheckJoin(
			_SQL_PLAIN, _groupOne.getGroupId());

		Assert.assertSame(_SQL_PLAIN, sql);

		Assert.assertTrue(_inlineSQLHelper.isEnabled(_groupOne.getGroupId()));
	}

	@Test
	public void testGetRoles() throws Exception {
		_groupThree = GroupTestUtil.addGroup();

		_role = RoleTestUtil.addRole("testRole", RoleConstants.TYPE_SITE);

		_addGroupRole(_groupThree, "testRole");

		_setPermissionChecker();

		Role guestRole = _roleLocalService.getRole(
			_groupThree.getCompanyId(), RoleConstants.GUEST);
		Role siteMemberRole = _roleLocalService.getRole(
			_groupThree.getCompanyId(), RoleConstants.SITE_MEMBER);
		Role userRole = _roleLocalService.getRole(
			_groupThree.getCompanyId(), RoleConstants.USER);

		long[] roleIds = ReflectionTestUtil.invoke(
			_inlineSQLHelper, "_getRoleIds", new Class<?>[] {long.class},
			_groupThree.getGroupId());

		String msg = StringUtil.merge(roleIds);

		Assert.assertTrue(msg, ArrayUtil.contains(roleIds, _role.getRoleId()));
		Assert.assertTrue(
			msg, ArrayUtil.contains(roleIds, guestRole.getRoleId()));
		Assert.assertTrue(
			msg, ArrayUtil.contains(roleIds, siteMemberRole.getRoleId()));
		Assert.assertTrue(
			msg, ArrayUtil.contains(roleIds, userRole.getRoleId()));
	}

	@Test
	public void testGroupAdminResourcePermission() throws Exception {
		_addGroupRole(_groupOne, RoleConstants.SITE_ADMINISTRATOR);
		_addGroupRole(_groupTwo, RoleConstants.SITE_MEMBER);

		_setPermissionChecker();

		String sql = _replacePermissionCheckJoin(_SQL_PLAIN, _groupIds);

		Assert.assertTrue(
			sql,
			sql.contains(
				StringBundler.concat(
					" OR (JournalArticle.groupId IN (", _groupOne.getGroupId(),
					"))")));
	}

	@Test
	public void testGroupScope() throws Exception {
		_role = RoleTestUtil.addRole("scopeGroupRole", RoleConstants.TYPE_SITE);

		_addGroupRole(_groupOne, "scopeGroupRole");

		_resourcePermissionLocalService.addResourcePermission(
			CompanyThreadLocal.getCompanyId(), _CLASS_NAME,
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(_groupOne.getGroupId()), _role.getRoleId(),
			ActionKeys.VIEW);

		_setPermissionChecker();

		String sql = _inlineSQLHelper.replacePermissionCheck(
			_SQL_PLAIN, _CLASS_NAME, _CLASS_PK_FIELD,
			new long[] {_groupOne.getGroupId()});

		Assert.assertSame(_SQL_PLAIN, sql);

		Assert.assertTrue(_inlineSQLHelper.isEnabled(_groupOne.getGroupId()));
	}

	@Test
	public void testGroupTemplateScope() throws Exception {
		_role = RoleTestUtil.addRole(
			"scopeGroupTemplateRole", RoleConstants.TYPE_SITE);

		_addGroupRole(_groupOne, "scopeGroupTemplateRole");

		_resourcePermissionLocalService.addResourcePermission(
			CompanyThreadLocal.getCompanyId(), _CLASS_NAME,
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			_role.getRoleId(), ActionKeys.VIEW);

		_setPermissionChecker();

		String sql = _replacePermissionCheckJoin(
			_SQL_PLAIN, _groupOne.getGroupId());

		Assert.assertSame(_SQL_PLAIN, sql);

		Assert.assertTrue(_inlineSQLHelper.isEnabled(_groupOne.getGroupId()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidCompany() throws Exception {
		_company = CompanyTestUtil.addCompany();

		_groupThree = GroupTestUtil.addGroupToCompany(_company.getCompanyId());

		_addGroupRole(_groupThree, RoleConstants.SITE_MEMBER);

		_addGroupRole(_groupOne, RoleConstants.SITE_MEMBER);

		_setPermissionChecker();

		_inlineSQLHelper.replacePermissionCheck(
			_SQL_PLAIN, _CLASS_NAME, _CLASS_PK_FIELD,
			new long[] {_groupOne.getGroupId(), _groupThree.getGroupId()});
	}

	@Test
	public void testIsNotEnabledForCompanyAdminWithNewCompany()
		throws Exception {

		_company = CompanyTestUtil.addCompany();

		_user = UserTestUtil.addCompanyAdminUser(_company);

		_setPermissionChecker();

		Assert.assertFalse(_inlineSQLHelper.isEnabled());
	}

	@Test
	public void testIsNotEnabledForOmniadmin() throws Exception {
		Role role = _roleLocalService.getRole(
			_user.getCompanyId(), RoleConstants.ADMINISTRATOR);

		_userLocalService.addRoleUser(role.getRoleId(), _user);

		_setPermissionChecker();

		Assert.assertFalse(_inlineSQLHelper.isEnabled(_groupIds));
	}

	@Test
	public void testIsNotEnabledSiteAdmin() throws Exception {
		_addGroupRole(_groupOne, RoleConstants.SITE_ADMINISTRATOR);

		_setPermissionChecker();

		Assert.assertFalse(_inlineSQLHelper.isEnabled(_groupOne.getGroupId()));
		Assert.assertTrue(_inlineSQLHelper.isEnabled(_groupTwo.getGroupId()));
	}

	@Test
	public void testReplaceQuery() throws Exception {
		_addGroupRole(_groupOne, RoleConstants.SITE_ADMINISTRATOR);
		_addGroupRole(_groupTwo, RoleConstants.SITE_MEMBER);

		_setPermissionChecker();

		JoinStep joinStep = DSLQueryFactoryUtil.select(
		).from(
			LayoutTable.INSTANCE
		);

		DSLQuery dslQuery = _inlineSQLHelper.replacePermissionCheck(
			joinStep, Layout.class, LayoutTable.INSTANCE.plid, _groupIds);

		Assert.assertEquals(
			StringBundler.concat(
				"select * from Layout where (Layout.plid in (select distinct ",
				"ResourcePermission.primKeyId from ResourcePermission where ",
				"ResourcePermission.companyId = ? and ResourcePermission.name ",
				"= ? and ResourcePermission.scope = ? and ",
				"ResourcePermission.viewActionId = ? and ",
				"(ResourcePermission.roleId in (?, ?, ?, ?) or ",
				"ResourcePermission.ownerId = ?)) or Layout.groupId in (?))"),
			dslQuery.toString());

		_assertValidSql(dslQuery);

		Assert.assertEquals(
			dslQuery.toString(),
			String.valueOf(
				joinStep.where(
					_inlineSQLHelper.getPermissionWherePredicate(
						Layout.class, LayoutTable.INSTANCE.plid, _groupIds))));

		GroupByStep groupByStep = joinStep.innerJoinON(
			PortletPreferencesTable.INSTANCE,
			PortletPreferencesTable.INSTANCE.plid.eq(LayoutTable.INSTANCE.plid)
		).where(
			LayoutTable.INSTANCE.companyId.eq(0L)
		);

		dslQuery = _inlineSQLHelper.replacePermissionCheck(
			groupByStep, Layout.class, LayoutTable.INSTANCE.plid, _groupIds);

		Assert.assertEquals(
			StringBundler.concat(
				"select * from Layout inner join PortletPreferences on ",
				"PortletPreferences.plid = Layout.plid where Layout.companyId ",
				"= ? and (Layout.plid in (select distinct ",
				"ResourcePermission.primKeyId from ResourcePermission where ",
				"ResourcePermission.companyId = ? and ResourcePermission.name ",
				"= ? and ResourcePermission.scope = ? and ",
				"ResourcePermission.viewActionId = ? and ",
				"(ResourcePermission.roleId in (?, ?, ?, ?) or ",
				"ResourcePermission.ownerId = ?)) or Layout.groupId in (?))"),
			dslQuery.toString());

		_assertValidSql(dslQuery);

		dslQuery = _inlineSQLHelper.replacePermissionCheck(
			groupByStep, Layout.class, LayoutTable.INSTANCE.plid);

		Assert.assertEquals(
			StringBundler.concat(
				"select * from Layout inner join PortletPreferences on ",
				"PortletPreferences.plid = Layout.plid where Layout.companyId ",
				"= ? and (Layout.plid in (select distinct ",
				"ResourcePermission.primKeyId from ResourcePermission where ",
				"ResourcePermission.companyId = ? and ResourcePermission.name ",
				"= ? and ResourcePermission.scope = ? and ",
				"ResourcePermission.viewActionId = ? and ",
				"(ResourcePermission.roleId in (?, ?) or ",
				"ResourcePermission.ownerId = ?)))"),
			dslQuery.toString());

		_assertValidSql(dslQuery);
	}

	@Test
	public void testSQLComposition() throws Exception {
		_addGroupRole(_groupOne, RoleConstants.SITE_MEMBER);
		_addGroupRole(_groupTwo, RoleConstants.SITE_MEMBER);

		_setPermissionChecker();

		String sql = _replacePermissionCheckJoin(_SQL_PLAIN, _groupIds);

		_checkSQLComposition(sql);

		sql = _replacePermissionCheckJoin(_SQL_PLAIN + _SQL_WHERE, _groupIds);

		_assertWhereClause(sql, _CLASS_PK_FIELD);

		Assert.assertTrue(
			sql,
			sql.endsWith(
				" AND " + _SQL_WHERE.substring(_WHERE_CLAUSE.length())));

		_assertValidSql(sql);
	}

	@Test
	public void testSQLCompositionNested() throws Exception {
		_addGroupRole(_groupOne, RoleConstants.SITE_MEMBER);
		_addGroupRole(_groupTwo, RoleConstants.SITE_MEMBER);

		_setPermissionChecker();

		String sql = _replacePermissionCheckJoin(
			StringBundler.concat(
				"SELECT COUNT(*) FROM JournalArticle LEFT JOIN (SELECT ",
				"JournalArticleLocalization.articlePK FROM ",
				"JournalArticleLocalization WHERE ",
				"JournalArticleLocalization.languageId = 'en_US') ",
				"JournalArticleLocalization ON (JournalArticle.id_ = ",
				"JournalArticleLocalization.articlePK) WHERE ",
				"JournalArticle.urlTitle like '%test%'"),
			_groupIds);

		_checkSQLComposition(sql);
	}

	private void _addGroupRole(Group group, String roleName) throws Exception {
		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), roleName);

		_userGroupRoleLocalService.addUserGroupRoles(
			new long[] {_user.getUserId()}, group.getGroupId(),
			role.getRoleId());
	}

	private void _assertClauseOrdering(String sql, String endingClause)
		throws Exception {

		String actualSql = _replacePermissionCheckJoin(sql, _groupIds);

		int wherePos = actualSql.lastIndexOf(_WHERE_CLAUSE);
		int groupByPos = actualSql.indexOf(_GROUP_BY_CLAUSE);
		int orderByPos = actualSql.indexOf(_ORDER_BY_CLAUSE);

		Assert.assertNotEquals(actualSql, -1, wherePos);

		if (endingClause.equals(_WHERE_CLAUSE)) {
			Assert.assertEquals(actualSql, -1, groupByPos);
			Assert.assertEquals(actualSql, -1, orderByPos);
		}
		else if (endingClause.equals(_GROUP_BY_CLAUSE)) {
			Assert.assertTrue(actualSql, wherePos < groupByPos);
			Assert.assertEquals(actualSql, -1, orderByPos);
		}
		else {
			Assert.assertTrue(actualSql, wherePos < orderByPos);
		}
	}

	private void _assertValidSql(DSLQuery dslQuery) throws Exception {
		DefaultASTNodeListener defaultASTNodeListener =
			new DefaultASTNodeListener();

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				dslQuery.toSQL(defaultASTNodeListener))) {

			List<Object> scalarValues =
				defaultASTNodeListener.getScalarValues();

			for (int i = 0; i < scalarValues.size(); i++) {
				preparedStatement.setObject(i + 1, scalarValues.get(i));
			}

			preparedStatement.executeQuery();
		}
	}

	private void _assertValidSql(String sql) throws Exception {
		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			preparedStatement.execute();
		}
	}

	private void _assertWhereClause(String sql, String classPK) {
		Assert.assertTrue(
			sql,
			sql.contains(
				StringBundler.concat(_WHERE_CLAUSE, "(", classPK, " IN (")));
	}

	private void _checkSQLComposition(String sql) throws Exception {
		_assertWhereClause(sql, _CLASS_PK_FIELD);

		Assert.assertTrue(
			sql,
			sql.contains(
				StringBundler.concat(
					_RESOURCE_PERMISSION, ".name = '", _CLASS_NAME, "'")));

		Assert.assertTrue(
			sql,
			sql.contains(
				StringBundler.concat(
					_RESOURCE_PERMISSION, ".companyId = ",
					CompanyThreadLocal.getCompanyId())));

		_assertValidSql(sql);
	}

	private String _replacePermissionCheckJoin(String sql, long... groupIds) {
		return _inlineSQLHelper.replacePermissionCheck(
			sql, _CLASS_NAME, _CLASS_PK_FIELD, groupIds);
	}

	private void _setPermissionChecker() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));
	}

	private static final String _CLASS_NAME =
		"com.liferay.journal.model.JournalArticle";

	private static final String _CLASS_PK_FIELD =
		"JournalArticle.resourcePrimKey";

	private static final String _GROUP_BY_CLAUSE = " GROUP BY ";

	private static final String _ORDER_BY_CLAUSE = " ORDER BY ";

	private static final String _RESOURCE_PERMISSION = "ResourcePermission";

	private static final String _SQL_GROUP_BY = " GROUP BY " + _CLASS_PK_FIELD;

	private static final String _SQL_ORDER_BY = " ORDER BY " + _CLASS_PK_FIELD;

	private static final String _SQL_PLAIN =
		"SELECT COUNT(*) FROM JournalArticle";

	private static final String _SQL_WHERE =
		" WHERE " + _CLASS_PK_FIELD + " != 0";

	private static final String _WHERE_CLAUSE = " WHERE ";

	@DeleteAfterTestRun
	private Company _company;

	private long[] _groupIds;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private Group _groupOne;

	@DeleteAfterTestRun
	private Group _groupThree;

	@DeleteAfterTestRun
	private Group _groupTwo;

	@Inject
	private InlineSQLHelper _inlineSQLHelper;

	private PermissionChecker _originalPermissionChecker;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}