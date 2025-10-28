/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v7_1_2;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.db.DBTypeToSQLMap;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

/**
 * @author Riccardo Alberti
 */
public class CommerceAccountRoleUpgradeProcess extends UpgradeProcess {

	public CommerceAccountRoleUpgradeProcess(
		AccountRoleLocalService accountRoleLocalService,
		ClassNameLocalService classNameLocalService,
		GroupLocalService groupLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService,
		RoleLocalService roleLocalService) {

		_accountRoleLocalService = accountRoleLocalService;
		_classNameLocalService = classNameLocalService;
		_groupLocalService = groupLocalService;
		_resourcePermissionLocalService = resourcePermissionLocalService;
		_roleLocalService = roleLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select distinct UserGroupRole.ctCollectionId, ",
					"UserGroupRole.roleId from UserGroupRole inner join Role_ ",
					"on Role_.roleId = UserGroupRole.roleId inner join Group_ ",
					"on Group_.classNameId = '",
					_classNameLocalService.getClassNameId(AccountEntry.class),
					"' and Group_.groupId = UserGroupRole.groupId where ",
					"Role_.type_ =", RoleConstants.TYPE_SITE));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update Role_ set classNameId = ?, classPK = ?, type_ = " +
						"? where ctCollectionId = ? and roleId = ?")) {

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					long roleId = resultSet.getLong(2);

					if (_hasNonaccountEntryGroup(roleId)) {
						AccountRole accountRole = _copyToAccountRole(roleId);

						_updateUserGroupRole(accountRole.getRoleId(), roleId);
					}
					else {
						preparedStatement2.setLong(
							1,
							_classNameLocalService.getClassNameId(
								AccountRole.class));

						Role role = _roleLocalService.getRole(roleId);

						AccountRole accountRole =
							_accountRoleLocalService.createAccountRole(
								increment());

						accountRole.setCompanyId(role.getCompanyId());
						accountRole.setAccountEntryId(
							AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT);
						accountRole.setRoleId(role.getRoleId());

						accountRole = _accountRoleLocalService.addAccountRole(
							accountRole);

						preparedStatement2.setLong(
							2, accountRole.getAccountRoleId());

						preparedStatement2.setInt(
							3, RoleConstants.TYPE_ACCOUNT);
						preparedStatement2.setLong(
							4, resultSet.getLong("ctCollectionId"));
						preparedStatement2.setLong(5, roleId);

						preparedStatement2.addBatch();
					}
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	private void _copyResourcePermission(
		ResourcePermission resourcePermission, long roleId) {

		ResourcePermission newResourcePermission =
			_resourcePermissionLocalService.createResourcePermission(
				increment(ResourcePermission.class.getName()));

		newResourcePermission.setCompanyId(resourcePermission.getCompanyId());
		newResourcePermission.setName(resourcePermission.getName());
		newResourcePermission.setScope(resourcePermission.getScope());
		newResourcePermission.setPrimKey(resourcePermission.getPrimKey());
		newResourcePermission.setPrimKeyId(resourcePermission.getPrimKeyId());
		newResourcePermission.setRoleId(roleId);
		newResourcePermission.setOwnerId(resourcePermission.getOwnerId());
		newResourcePermission.setActionIds(resourcePermission.getActionIds());
		newResourcePermission.setViewActionId(
			resourcePermission.isViewActionId());

		_resourcePermissionLocalService.addResourcePermission(
			newResourcePermission);
	}

	private AccountRole _copyToAccountRole(long roleId) throws Exception {
		Role role = _roleLocalService.getRole(roleId);

		AccountRole accountRole = _accountRoleLocalService.addAccountRole(
			null, role.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			role.getName() + "_Account", role.getTitleMap(),
			role.getDescriptionMap());

		List<ResourcePermission> resourcePermissions =
			_resourcePermissionLocalService.getRoleResourcePermissions(
				role.getRoleId());

		for (ResourcePermission resourcePermission : resourcePermissions) {
			if (resourcePermission.getScope() ==
					ResourceConstants.SCOPE_GROUP) {

				Group group = _groupLocalService.getGroup(
					Long.valueOf(resourcePermission.getPrimKey()));

				if (group.getClassNameId() ==
						_classNameLocalService.getClassNameId(
							AccountEntry.class)) {

					resourcePermission.setRoleId(accountRole.getRoleId());

					resourcePermission =
						_resourcePermissionLocalService.
							updateResourcePermission(resourcePermission);
				}
			}
			else if ((resourcePermission.getScope() ==
						ResourceConstants.SCOPE_GROUP_TEMPLATE) ||
					 (resourcePermission.getScope() ==
						 ResourceConstants.SCOPE_INDIVIDUAL)) {

				_copyResourcePermission(
					resourcePermission, accountRole.getRoleId());
			}
		}

		return accountRole;
	}

	private boolean _hasNonaccountEntryGroup(long roleId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select count(*) from (select distinct UserGroupRole.",
					"groupId from UserGroupRole inner join Group_ on Group_.",
					"classNameId != ? and Group_.groupId = UserGroupRole.",
					"groupId where UserGroupRole.roleId = ? union select ",
					"distinct UserGroupGroupRole.groupId from ",
					"UserGroupGroupRole inner join Group_ on Group_.",
					"classNameId != ? and Group_.groupId = UserGroupGroupRole.",
					"groupId where UserGroupGroupRole.roleId = ?) as count"))) {

			preparedStatement.setLong(
				1, _classNameLocalService.getClassNameId(AccountEntry.class));
			preparedStatement.setLong(2, roleId);
			preparedStatement.setLong(
				3, _classNameLocalService.getClassNameId(AccountEntry.class));
			preparedStatement.setLong(4, roleId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					int count = resultSet.getInt(1);

					if (count > 0) {
						return true;
					}
				}

				return false;
			}
		}
	}

	private void _updateUserGroupRole(long newRoleId, long oldRoleId)
		throws Exception {

		DBTypeToSQLMap dbTypeToSQLMap = new DBTypeToSQLMap(
			StringBundler.concat(
				"update UserGroupRole set roleId = ", newRoleId,
				" from Group_ where Group_.classNameId = '",
				_classNameLocalService.getClassNameId(AccountEntry.class),
				"' and Group_.groupId = UserGroupRole.groupId and ",
				"UserGroupRole.roleId = ", oldRoleId));

		String sql = StringBundler.concat(
			"update UserGroupRole inner join Group_ on Group_.classNameId = '",
			_classNameLocalService.getClassNameId(AccountEntry.class),
			"' and Group_.groupId = UserGroupRole.groupId set roleId = ",
			newRoleId, " where roleId = ", oldRoleId);

		dbTypeToSQLMap.add(DBType.MARIADB, sql);
		dbTypeToSQLMap.add(DBType.MYSQL, sql);

		runSQL(dbTypeToSQLMap);
	}

	private final AccountRoleLocalService _accountRoleLocalService;
	private final ClassNameLocalService _classNameLocalService;
	private final GroupLocalService _groupLocalService;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private final RoleLocalService _roleLocalService;

}