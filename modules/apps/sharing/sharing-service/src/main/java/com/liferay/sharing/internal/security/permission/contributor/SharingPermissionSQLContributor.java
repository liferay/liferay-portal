/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.internal.security.permission.contributor;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.security.permission.contributor.PermissionSQLContributor;
import com.liferay.sharing.configuration.SharingConfiguration;
import com.liferay.sharing.configuration.SharingConfigurationFactory;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.model.SharingEntryTable;
import com.liferay.sharing.service.SharingEntryLocalService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Extends inline permission SQL queries to also consider sharing entries when
 * returning results.
 *
 * @author Alejandro Tardín
 * @review
 */
public class SharingPermissionSQLContributor
	implements PermissionSQLContributor {

	public SharingPermissionSQLContributor(
		ClassNameLocalService classNameLocalService,
		GroupLocalService groupLocalService,
		SharingConfigurationFactory sharingConfigurationFactory,
		SharingEntryLocalService sharingEntryLocalService,
		UserGroupLocalService userGroupLocalService,
		UserLocalService userLocalService) {

		_classNameLocalService = classNameLocalService;
		_groupLocalService = groupLocalService;
		_sharingConfigurationFactory = sharingConfigurationFactory;
		_sharingEntryLocalService = sharingEntryLocalService;
		_userGroupLocalService = userGroupLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	public void collectPermittedClassPKs(
		String className, long userId, long[] groupIds,
		Set<Long> permittedClassPKs) {

		SharingConfiguration sharingConfiguration =
			_sharingConfigurationFactory.getSystemSharingConfiguration();

		if (!sharingConfiguration.isEnabled()) {
			return;
		}

		List<SharingEntry> sharingEntries =
			_sharingEntryLocalService.getToUserSharingEntries(
				userId, _classNameLocalService.getClassNameId(className));

		if (sharingEntries.isEmpty()) {
			return;
		}

		Set<Long> disabledGroupIds = new HashSet<>();

		for (long groupId : groupIds) {
			if (groupId == GroupConstants.DEFAULT_LIVE_GROUP_ID) {
				continue;
			}

			SharingConfiguration groupSharingConfiguration =
				_getSharingConfiguration(groupId);

			if (!groupSharingConfiguration.isEnabled()) {
				disabledGroupIds.add(groupId);
			}
		}

		Set<Long> userGroupIds = SetUtil.fromArray(
			_userLocalService.getUserGroupPrimaryKeys(userId));

		for (SharingEntry sharingEntry : sharingEntries) {
			if (!disabledGroupIds.contains(sharingEntry.getGroupId()) &&
				userGroupIds.contains(sharingEntry.getToUserGroupId())) {

				permittedClassPKs.add(sharingEntry.getClassPK());
			}
		}
	}

	@Override
	public Predicate getPermissionPredicate(
		PermissionChecker permissionChecker, String className,
		Column<?, Long> classPKColumn, long[] groupIds) {

		SharingConfiguration sharingConfiguration =
			_sharingConfigurationFactory.getSystemSharingConfiguration();

		if (!sharingConfiguration.isEnabled()) {
			return null;
		}

		List<Long> disableGroupIds = new ArrayList<>();

		if (groupIds != null) {
			for (long groupId : groupIds) {
				if (groupId == GroupConstants.DEFAULT_LIVE_GROUP_ID) {
					continue;
				}

				SharingConfiguration groupSharingConfiguration =
					_getSharingConfiguration(groupId);

				if (!groupSharingConfiguration.isEnabled()) {
					disableGroupIds.add(groupId);
				}
			}

			if (disableGroupIds.size() == groupIds.length) {
				return null;
			}
		}

		return classPKColumn.in(
			DSLQueryFactoryUtil.select(
				SharingEntryTable.INSTANCE.classPK
			).from(
				SharingEntryTable.INSTANCE
			).where(
				() -> {
					Predicate predicate = _getUserAndUserGroupPredicate(
						permissionChecker
					).and(
						SharingEntryTable.INSTANCE.classNameId.eq(
							_classNameLocalService.getClassNameId(className))
					);

					if (disableGroupIds.isEmpty()) {
						return predicate;
					}

					return predicate.and(
						SharingEntryTable.INSTANCE.groupId.notIn(
							disableGroupIds.toArray(new Long[0])));
				}
			));
	}

	@Override
	public String getPermissionSQL(
		String className, String classPKField, String groupIdField,
		long[] groupIds) {

		SharingConfiguration sharingConfiguration =
			_sharingConfigurationFactory.getSystemSharingConfiguration();

		if (!sharingConfiguration.isEnabled()) {
			return StringPool.BLANK;
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		StringBundler sb = new StringBundler(7);

		sb.append(classPKField);
		sb.append(" IN (SELECT SharingEntry.classPK FROM SharingEntry WHERE ");

		_addDisabledGroupsSQL(sb, groupIds);

		List<UserGroup> userGroups = _userGroupLocalService.getUserUserGroups(
			permissionChecker.getUserId());

		sb.append("(");

		if (!userGroups.isEmpty()) {
			sb.append("(SharingEntry.toUserGroupId IN ( ");
			sb.append(
				StringUtil.merge(
					TransformUtil.transformToLongArray(
						userGroups, UserGroup::getUserGroupId),
					StringPool.COMMA));
			sb.append(")) OR ");
		}

		sb.append("(SharingEntry.toUserId = ");
		sb.append(permissionChecker.getUserId());
		sb.append(")) AND (SharingEntry.classNameId = ");
		sb.append(_classNameLocalService.getClassNameId(className));
		sb.append("))");

		return sb.toString();
	}

	private void _addDisabledGroupsSQL(StringBundler sb, long[] groupIds) {
		if (ArrayUtil.isEmpty(groupIds)) {
			return;
		}

		int groupCount = 0;

		for (long groupId : groupIds) {
			if (groupId == GroupConstants.DEFAULT_LIVE_GROUP_ID) {
				continue;
			}

			SharingConfiguration sharingConfiguration =
				_getSharingConfiguration(groupId);

			if (!sharingConfiguration.isEnabled()) {
				if (groupCount == 0) {
					sb.append("(SharingEntry.groupId NOT IN (");
				}
				else {
					sb.append(StringPool.COMMA_AND_SPACE);
				}

				sb.append(groupId);

				groupCount++;
			}
		}

		if (groupCount > 0) {
			sb.append(")) AND");
		}
	}

	private SharingConfiguration _getSharingConfiguration(long groupId) {
		try {
			return _sharingConfigurationFactory.getGroupSharingConfiguration(
				_groupLocalService.getGroup(groupId));
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private Predicate _getUserAndUserGroupPredicate(
		PermissionChecker permissionChecker) {

		return SharingEntryTable.INSTANCE.toUserId.eq(
			permissionChecker.getUserId()
		).or(
			() -> {
				User user = permissionChecker.getUser();

				List<UserGroup> userGroups = user.getUserGroups();

				if (userGroups.isEmpty()) {
					return null;
				}

				return SharingEntryTable.INSTANCE.toUserGroupId.in(
					TransformUtil.transformToArray(
						userGroups, UserGroup::getUserGroupId, Long.class));
			}
		);
	}

	private final ClassNameLocalService _classNameLocalService;
	private final GroupLocalService _groupLocalService;
	private final SharingConfigurationFactory _sharingConfigurationFactory;
	private final SharingEntryLocalService _sharingEntryLocalService;
	private final UserGroupLocalService _userGroupLocalService;
	private final UserLocalService _userLocalService;

}