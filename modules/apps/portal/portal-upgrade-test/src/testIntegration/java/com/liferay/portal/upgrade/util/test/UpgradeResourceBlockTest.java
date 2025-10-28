/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.DBAssertionUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.util.BaseUpgradeResourceBlock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class UpgradeResourceBlockTest extends BaseUpgradeResourceBlock {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_regularRole = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_siteRole = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		_connection = DataAccess.getConnection();

		_companyLocalService.forEachCompany(
			company -> {
				runSQL(
					StringBundler.concat(
						"create table ResourceBlock (mvccVersion LONG default ",
						"0 not null, resourceBlockId LONG not null primary ",
						"key, companyId LONG, groupId LONG, name VARCHAR(75)  ",
						"null, permissionsHash VARCHAR(75) null, ",
						"referenceCount LONG)"));

				runSQL(
					StringBundler.concat(
						"create table ResourceBlockPermission (mvccVersion ",
						"LONG default 0 not null, resourceBlockPermissionId ",
						"LONG not null primary key, companyId LONG, ",
						"resourceBlockId LONG, roleId LONG, actionIds LONG)"));

				runSQL(
					"create table UpgradeResourceBlockTest(id_ LONG not null " +
						"primary key, userId LONG, resourceBlockId LONG)");

				runSQL(
					StringBundler.concat(
						"create table ResourceTypePermission (mvccVersion ",
						"LONG default 0 not null, resourceTypePermissionId ",
						"LONG not null primary key, companyId LONG, groupId ",
						"LONG, name VARCHAR(75) null, roleId LONG, actionIds ",
						"LONG)"));

				long resourceBlockId = -1;

				StringBundler sb = new StringBundler(11);

				sb.append("insert into UpgradeResourceBlockTest(id_, userId, ");
				sb.append("resourceBlockId) values (");
				sb.append(_RESOURCE_PRIMARY_KEY);
				sb.append(", ");
				sb.append(_USER_ID);
				sb.append(", ");
				sb.append(resourceBlockId);
				sb.append(")");

				runSQL(sb.toString());

				_insertResourceTypePermission(
					-2, 0, _regularRole.getRoleId(), _COMPANY_SCOPE_ACTION_IDS);

				_insertResourceTypePermission(
					-3, _GROUP_ID, _siteRole.getRoleId(),
					_GROUP_SCOPE_ACTION_IDS);

				_insertResourceTypePermission(
					-4, 0, _siteRole.getRoleId(),
					_GROUP_TEMPLATE_SCOPE_ACTION_IDS);

				sb.setIndex(0);

				sb.append(
					"insert into ResourceBlock(mvccVersion, resourceBlockId, ");
				sb.append("companyId, groupId, name, ");
				sb.append("permissionsHash, referenceCount");
				sb.append(") values (1, ");
				sb.append(resourceBlockId);
				sb.append(", ");
				sb.append(_COMPANY_ID);
				sb.append(", ");
				sb.append(_GROUP_ID);
				sb.append(", '");
				sb.append(UpgradeResourceBlockTest.class.getName());
				sb.append("', 'hash', 1)");

				runSQL(sb.toString());

				sb.setIndex(0);

				sb.append("insert into ResourceBlockPermission(mvccVersion, ");
				sb.append(
					"resourceBlockPermissionId, companyId, resourceBlockId, ");
				sb.append("roleId, actionIds) values (1, -5, ");
				sb.append(_COMPANY_ID);
				sb.append(", ");
				sb.append(resourceBlockId);
				sb.append(", ");
				sb.append(_regularRole.getRoleId());
				sb.append(", ");
				sb.append(_INDIVIDUAL_SCOPE_ACTION_IDS);
				sb.append(")");

				runSQL(sb.toString());
			});
	}

	@After
	public void tearDown() throws Exception {
		_companyLocalService.forEachCompany(
			company -> {
				dropTable("ResourceBlock");

				dropTable("ResourceBlockPermission");

				dropTable("ResourceTypePermission");

				runSQL(
					"delete from ResourcePermission where name = '" +
						UpgradeResourceBlockTest.class.getName() + "'");

				dropTable(getTableName());

				_connection.close();
			});
	}

	@Test
	public void testUpgradeWithoutUserId() throws Exception {
		_testUpgrade(false);
	}

	@Test
	public void testUpgradeWithUserId() throws Exception {
		_testUpgrade(true);
	}

	@Override
	protected String getClassName() {
		return UpgradeResourceBlockTest.class.getName();
	}

	@Override
	protected String getPrimaryKeyName() {
		return "id_";
	}

	@Override
	protected String getTableName() {
		return "UpgradeResourceBlockTest";
	}

	@Override
	protected boolean hasUserId() {
		return _hasUserId;
	}

	private void _assertResourcePermission(
			ResultSet resultSet, int scope, long primKeyId, long roleId,
			long ownerId, long actionIds, boolean viewActionId)
		throws Exception {

		Assert.assertTrue(resultSet.next());

		Assert.assertEquals(_COMPANY_ID, resultSet.getLong("companyId"));
		Assert.assertEquals(
			UpgradeResourceBlockTest.class.getName(),
			resultSet.getString("name"));
		Assert.assertEquals(scope, resultSet.getLong("scope"));
		Assert.assertEquals(
			String.valueOf(primKeyId), resultSet.getString("primKey"));
		Assert.assertEquals(primKeyId, resultSet.getLong("primKeyId"));
		Assert.assertEquals(roleId, resultSet.getLong("roleId"));
		Assert.assertEquals(ownerId, resultSet.getLong("ownerId"));
		Assert.assertEquals(actionIds, resultSet.getLong("actionIds"));
		Assert.assertEquals(viewActionId, resultSet.getBoolean("viewActionId"));
	}

	private void _assertRowsRemoved(String tableName, String primaryKeyName)
		throws Exception {

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				StringBundler.concat(
					"select * from ", tableName, " where ", primaryKeyName,
					" < 0"));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			Assert.assertFalse(resultSet.next());
		}
	}

	private void _insertResourceTypePermission(
			long resourceTypePermissionId, long groupId, long roleId,
			long actionIds)
		throws Exception {

		runSQL(
			StringBundler.concat(
				"insert into ResourceTypePermission(mvccVersion, ",
				"resourceTypePermissionId, companyId, groupId, name, roleId, ",
				"actionIds) values (1, ", resourceTypePermissionId, ", ",
				_COMPANY_ID, ", ", groupId, ", '",
				UpgradeResourceBlockTest.class.getName(), "', ", roleId, ", ",
				actionIds, ")"));
	}

	private void _testUpgrade(boolean hasUserId) throws Exception {
		_hasUserId = hasUserId;

		DBAssertionUtil.assertColumns(
			getTableName(), "id_", "userId", "resourceBlockId");

		for (UpgradeStep upgradeStep : getUpgradeSteps()) {
			UpgradeProcess upgradeProcess = (UpgradeProcess)upgradeStep;

			upgradeProcess.upgrade();
		}

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"select * from ResourcePermission where name = '" +
					UpgradeResourceBlockTest.class.getName() +
						"' order by scope");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			_assertResourcePermission(
				resultSet, ResourceConstants.SCOPE_COMPANY, _COMPANY_ID,
				_regularRole.getRoleId(), 0, _COMPANY_SCOPE_ACTION_IDS, true);

			_assertResourcePermission(
				resultSet, ResourceConstants.SCOPE_GROUP, _GROUP_ID,
				_siteRole.getRoleId(), 0, _GROUP_SCOPE_ACTION_IDS, false);

			_assertResourcePermission(
				resultSet, ResourceConstants.SCOPE_GROUP_TEMPLATE, 0,
				_siteRole.getRoleId(), 0, _GROUP_TEMPLATE_SCOPE_ACTION_IDS,
				true);

			long userId = 0;

			if (_hasUserId) {
				userId = _USER_ID;
			}

			_assertResourcePermission(
				resultSet, ResourceConstants.SCOPE_INDIVIDUAL,
				_RESOURCE_PRIMARY_KEY, _regularRole.getRoleId(), userId,
				_INDIVIDUAL_SCOPE_ACTION_IDS, false);

			Assert.assertFalse(resultSet.next());
		}

		DBAssertionUtil.assertColumns(getTableName(), "id_", "userId");

		_assertRowsRemoved("ResourceBlock", "resourceBlockId");
		_assertRowsRemoved(
			"ResourceBlockPermission", "resourceBlockPermissionId");
		_assertRowsRemoved(
			"ResourceTypePermission", "resourceTypePermissionId");
	}

	private static final long _COMPANY_ID = -6;

	private static final long _COMPANY_SCOPE_ACTION_IDS = 3;

	private static final long _GROUP_ID = -7;

	private static final long _GROUP_SCOPE_ACTION_IDS = 6;

	private static final long _GROUP_TEMPLATE_SCOPE_ACTION_IDS = 9;

	private static final long _INDIVIDUAL_SCOPE_ACTION_IDS = 12;

	private static final long _RESOURCE_PRIMARY_KEY = -8;

	private static final long _USER_ID = -9;

	@Inject
	private static CompanyLocalService _companyLocalService;

	private static Connection _connection;

	private boolean _hasUserId;

	@DeleteAfterTestRun
	private Role _regularRole;

	@DeleteAfterTestRun
	private Role _siteRole;

}