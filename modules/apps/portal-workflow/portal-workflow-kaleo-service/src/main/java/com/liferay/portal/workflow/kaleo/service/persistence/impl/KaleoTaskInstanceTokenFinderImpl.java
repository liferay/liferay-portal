/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupGroupRole;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.workflow.kaleo.internal.util.RoleUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskInstanceTokenModelImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskInstanceTokenFinder;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskInstanceTokenQuery;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskInstanceTokenUtil;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = KaleoTaskInstanceTokenFinder.class)
public class KaleoTaskInstanceTokenFinderImpl
	extends KaleoTaskInstanceTokenFinderBaseImpl
	implements KaleoTaskInstanceTokenFinder {

	public static final String COUNT_BY_C_KTAI =
		KaleoTaskInstanceTokenFinder.class.getName() + ".countByC_KTAI";

	public static final String FIND_BY_C_KTAI =
		KaleoTaskInstanceTokenFinder.class.getName() + ".findByC_KTAI";

	@Override
	public int countKaleoTaskInstanceTokens(
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = buildKaleoTaskInstanceTokenQuerySQL(
				kaleoTaskInstanceTokenQuery, true, session);

			Iterator<Long> iterator = sqlQuery.iterate();

			if (iterator.hasNext()) {
				Long count = iterator.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<KaleoTaskInstanceToken> findKaleoTaskInstanceTokens(
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = buildKaleoTaskInstanceTokenQuerySQL(
				kaleoTaskInstanceTokenQuery, false, session);

			List<KaleoTaskInstanceToken> kaleoTaskInstanceTokens =
				new ArrayList<>();

			Iterator<Long> iterator = (Iterator<Long>)QueryUtil.iterate(
				sqlQuery, getDialect(), kaleoTaskInstanceTokenQuery.getStart(),
				kaleoTaskInstanceTokenQuery.getEnd());

			while (iterator.hasNext()) {
				long kaleoTaskInstanceTokenId = iterator.next();

				KaleoTaskInstanceToken kaleoTaskInstanceToken =
					KaleoTaskInstanceTokenUtil.findByPrimaryKey(
						kaleoTaskInstanceTokenId);

				kaleoTaskInstanceTokens.add(kaleoTaskInstanceToken);
			}

			return kaleoTaskInstanceTokens;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected boolean appendSearchCriteria(
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		if (ArrayUtil.isNotEmpty(
				kaleoTaskInstanceTokenQuery.getAssetPrimaryKeys()) ||
			ArrayUtil.isNotEmpty(kaleoTaskInstanceTokenQuery.getAssetTypes()) ||
			(kaleoTaskInstanceTokenQuery.getDueDateGT() != null) ||
			(kaleoTaskInstanceTokenQuery.getDueDateLT() != null) ||
			ArrayUtil.isNotEmpty(kaleoTaskInstanceTokenQuery.getTaskNames()) ||
			Validator.isNotNull(kaleoTaskInstanceTokenQuery.getAssetTitle())) {

			return true;
		}

		return false;
	}

	protected SQLQuery buildKaleoTaskInstanceTokenQuerySQL(
			KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery,
			boolean count, Session session)
		throws Exception {

		String sql = null;

		if (count) {
			sql = _customSQL.get(getClass(), COUNT_BY_C_KTAI);
		}
		else {
			sql = _customSQL.get(getClass(), FIND_BY_C_KTAI);
		}

		sql = _customSQL.appendCriteria(
			sql, getAssigneeClassName(kaleoTaskInstanceTokenQuery));
		sql = _customSQL.appendCriteria(
			sql, getAssigneeClassPKs(kaleoTaskInstanceTokenQuery));
		sql = _customSQL.appendCriteria(
			sql, getCompleted(kaleoTaskInstanceTokenQuery));
		sql = _customSQL.appendCriteria(
			sql, getKaleoInstanceIds(kaleoTaskInstanceTokenQuery));
		sql = _customSQL.appendCriteria(
			sql, getRoleIds(kaleoTaskInstanceTokenQuery));
		sql = _customSQL.appendCriteria(
			sql, getSearchByUserRoles(kaleoTaskInstanceTokenQuery));

		if (appendSearchCriteria(kaleoTaskInstanceTokenQuery)) {
			sql = _customSQL.appendCriteria(sql, " AND (");

			if (ArrayUtil.isNotEmpty(
					kaleoTaskInstanceTokenQuery.getAssetPrimaryKeys()) ||
				ArrayUtil.isNotEmpty(
					kaleoTaskInstanceTokenQuery.getAssetTypes())) {

				sql = _customSQL.appendCriteria(sql, " (");
			}

			sql = _customSQL.appendCriteria(
				sql, getAssetPrimaryKey(kaleoTaskInstanceTokenQuery));
			sql = _customSQL.appendCriteria(
				sql,
				getAssetTypes(
					kaleoTaskInstanceTokenQuery,
					ArrayUtil.isEmpty(
						kaleoTaskInstanceTokenQuery.getAssetPrimaryKeys())));

			if (ArrayUtil.isNotEmpty(
					kaleoTaskInstanceTokenQuery.getAssetPrimaryKeys()) ||
				ArrayUtil.isNotEmpty(
					kaleoTaskInstanceTokenQuery.getAssetTypes())) {

				sql = _customSQL.appendCriteria(sql, ") ");
			}

			sql = _customSQL.appendCriteria(
				sql,
				getDueDateGT(
					kaleoTaskInstanceTokenQuery,
					ArrayUtil.isEmpty(
						kaleoTaskInstanceTokenQuery.getAssetPrimaryKeys()) &&
					ArrayUtil.isEmpty(
						kaleoTaskInstanceTokenQuery.getAssetTypes())));
			sql = _customSQL.appendCriteria(
				sql,
				getDueDateLT(
					kaleoTaskInstanceTokenQuery,
					ArrayUtil.isEmpty(
						kaleoTaskInstanceTokenQuery.getAssetPrimaryKeys()) &&
					ArrayUtil.isEmpty(
						kaleoTaskInstanceTokenQuery.getAssetTypes()) &&
					(kaleoTaskInstanceTokenQuery.getDueDateGT() == null)));
			sql = _customSQL.appendCriteria(
				sql,
				getTaskNames(
					kaleoTaskInstanceTokenQuery,
					ArrayUtil.isEmpty(
						kaleoTaskInstanceTokenQuery.getAssetPrimaryKeys()) &&
					ArrayUtil.isEmpty(
						kaleoTaskInstanceTokenQuery.getAssetTypes()) &&
					(kaleoTaskInstanceTokenQuery.getDueDateGT() == null) &&
					(kaleoTaskInstanceTokenQuery.getDueDateLT() == null)));
			sql = _customSQL.appendCriteria(
				sql,
				getAssetTitle(
					kaleoTaskInstanceTokenQuery,
					ArrayUtil.isEmpty(
						kaleoTaskInstanceTokenQuery.getAssetPrimaryKeys()) &&
					ArrayUtil.isEmpty(
						kaleoTaskInstanceTokenQuery.getAssetTypes()) &&
					(kaleoTaskInstanceTokenQuery.getDueDateGT() == null) &&
					(kaleoTaskInstanceTokenQuery.getDueDateLT() == null) &&
					ArrayUtil.isEmpty(
						kaleoTaskInstanceTokenQuery.getTaskNames())));
			sql = _customSQL.appendCriteria(sql, ")");

			sql = _customSQL.replaceAndOperator(
				sql, kaleoTaskInstanceTokenQuery.isAndOperator());
		}

		OrderByComparator<KaleoTaskInstanceToken> orderByComparator =
			kaleoTaskInstanceTokenQuery.getOrderByComparator();

		if (orderByComparator != null) {
			StringBundler sb = new StringBundler(sql);

			appendOrderByComparator(
				sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

			sql = sb.toString();

			String[] orderByFields = orderByComparator.getOrderByFields();

			sb = new StringBundler((orderByFields.length * 3) + 1);

			sb.append(
				"DISTINCT KaleoTaskInstanceToken.kaleoTaskInstanceTokenId");

			for (String orderByField : orderByFields) {
				if (orderByField.equals("kaleoTaskInstanceTokenId")) {
					continue;
				}

				sb.append(", ");
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByField);
			}

			sql = StringUtil.replace(
				sql, "DISTINCT KaleoTaskInstanceToken.kaleoTaskInstanceTokenId",
				sb.toString());
		}

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		if (count) {
			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);
		}
		else {
			sqlQuery.addScalar("KaleoTaskInstanceTokenId", Type.LONG);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(kaleoTaskInstanceTokenQuery.getCompanyId());

		setAssigneeClassName(queryPos, kaleoTaskInstanceTokenQuery);
		setCompleted(queryPos, kaleoTaskInstanceTokenQuery);

		setAssetPrimaryKey(queryPos, kaleoTaskInstanceTokenQuery);
		setAssetType(queryPos, kaleoTaskInstanceTokenQuery);
		setDueDateGT(queryPos, kaleoTaskInstanceTokenQuery);
		setDueDateLT(queryPos, kaleoTaskInstanceTokenQuery);
		setTaskNames(queryPos, kaleoTaskInstanceTokenQuery);

		setAssetTitle(queryPos, kaleoTaskInstanceTokenQuery);

		return sqlQuery;
	}

	protected String getAssetPrimaryKey(
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		Long[] assetPrimaryKeys =
			kaleoTaskInstanceTokenQuery.getAssetPrimaryKeys();

		if (ArrayUtil.isEmpty(assetPrimaryKeys)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(assetPrimaryKeys.length + 1);

		sb.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 0; i < (assetPrimaryKeys.length - 1); i++) {
			sb.append("(KaleoTaskInstanceToken.classPK = ?) OR ");
		}

		sb.append("(KaleoTaskInstanceToken.classPK = ?))");

		return sb.toString();
	}

	protected String getAssetTitle(
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery,
		boolean firstCriteria) {

		String assetTitle = kaleoTaskInstanceTokenQuery.getAssetTitle();

		if (Validator.isNull(assetTitle)) {
			return StringPool.BLANK;
		}

		String[] assetTitles = _customSQL.keywords(assetTitle, false);

		if (ArrayUtil.isEmpty(assetTitles)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler((assetTitles.length * 2) + 1);

		if (!firstCriteria) {
			sb.append("[$AND_OR_CONNECTOR$] (");
		}
		else {
			sb.append(StringPool.OPEN_PARENTHESIS);
		}

		for (int i = 0; i < assetTitles.length; i++) {
			sb.append("(LOWER(AssetEntry.title) LIKE LOWER(?)) ");
			sb.append("OR ");
		}

		sb.setIndex(sb.index() - 1);

		sb.append(")");

		return sb.toString();
	}

	protected String getAssetTypes(
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery,
		boolean firstCriteria) {

		String[] assetTypes = kaleoTaskInstanceTokenQuery.getAssetTypes();

		if (ArrayUtil.isEmpty(assetTypes)) {
			return StringPool.BLANK;
		}

		assetTypes = _customSQL.keywords(
			kaleoTaskInstanceTokenQuery.getAssetTypes());

		if (ArrayUtil.isEmpty(assetTypes)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(assetTypes.length + 1);

		if (!firstCriteria) {
			sb.append(" AND (");
		}
		else {
			sb.append(StringPool.OPEN_PARENTHESIS);
		}

		for (int i = 0; i < (assetTypes.length - 1); i++) {
			sb.append("(LOWER(KaleoTaskInstanceToken.className) LIKE ?) OR ");
		}

		sb.append("(LOWER(KaleoTaskInstanceToken.className) LIKE ?))");

		return sb.toString();
	}

	protected String getAssigneeClassName(
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		if (Validator.isNull(
				kaleoTaskInstanceTokenQuery.getAssigneeClassName())) {

			return StringPool.BLANK;
		}

		return "AND (KaleoTaskAssignmentInstance.assigneeClassName = ?)";
	}

	protected String getAssigneeClassPKs(
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		Long[] assigneeClassPKs =
			kaleoTaskInstanceTokenQuery.getAssigneeClassPKs();

		if (ArrayUtil.isEmpty(assigneeClassPKs)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler((assigneeClassPKs.length * 2) + 1);

		sb.append("AND (KaleoTaskAssignmentInstance.assigneeClassPK IN (");

		for (Long assigneeClassPK : assigneeClassPKs) {
			sb.append(String.valueOf(assigneeClassPK));
			sb.append(StringPool.COMMA_AND_SPACE);
		}

		sb.setIndex(sb.index() - 1);
		sb.append("))");

		return sb.toString();
	}

	protected String getCompleted(
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		Boolean completed = kaleoTaskInstanceTokenQuery.isCompleted();

		if (completed == null) {
			return StringPool.BLANK;
		}

		return "AND (KaleoTaskInstanceToken.completed = ?)";
	}

	protected String getDueDateGT(
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery,
		boolean firstCriteria) {

		Date dueDateGT = kaleoTaskInstanceTokenQuery.getDueDateGT();

		if (dueDateGT == null) {
			return StringPool.BLANK;
		}

		if (firstCriteria) {
			return FIRST_DUE_DATE_GT;
		}

		return NOT_FIRST_DUE_DATE_GT;
	}

	protected String getDueDateLT(
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery,
		boolean firstCriteria) {

		Date dueDateLT = kaleoTaskInstanceTokenQuery.getDueDateLT();

		if (dueDateLT == null) {
			return StringPool.BLANK;
		}

		if (firstCriteria) {
			return FIRST_DUE_DATE_LT;
		}

		return NOT_FIRST_DUE_DATE_LT;
	}

	protected String getKaleoInstanceIds(
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		Long[] kaleoInstanceIds =
			kaleoTaskInstanceTokenQuery.getKaleoInstanceIds();

		if (ArrayUtil.isEmpty(kaleoInstanceIds)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler((kaleoInstanceIds.length * 2) + 1);

		sb.append("AND (KaleoTaskInstanceToken.kaleoInstanceId IN (");

		for (Long kaleoInstanceId : kaleoInstanceIds) {
			sb.append(String.valueOf(kaleoInstanceId));
			sb.append(StringPool.COMMA_AND_SPACE);
		}

		sb.setIndex(sb.index() - 1);
		sb.append("))");

		return sb.toString();
	}

	protected String getRoleIds(
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		Boolean searchByUserRoles =
			kaleoTaskInstanceTokenQuery.isSearchByUserRoles();

		if (searchByUserRoles != null) {
			return StringPool.BLANK;
		}

		List<Long> roleIds = kaleoTaskInstanceTokenQuery.getRoleIds();

		if (ListUtil.isEmpty(roleIds)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler((roleIds.size() * 2) + 1);

		sb.append("AND (KaleoTaskAssignmentInstance.assigneeClassPK IN (");

		Iterator<Long> iterator = roleIds.iterator();

		while (iterator.hasNext()) {
			sb.append(iterator.next());

			if (!iterator.hasNext()) {
				continue;
			}

			sb.append(", ");
		}

		sb.append("))");

		return sb.toString();
	}

	protected List<Long> getSearchByUserRoleIds(
			KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery)
		throws Exception {

		List<Long> roleIds = RoleUtil.getRoleIds(
			kaleoTaskInstanceTokenQuery.getServiceContext());

		User user = _userLocalService.getUserById(
			kaleoTaskInstanceTokenQuery.getUserId());

		List<Group> groups = new ArrayList<>();

		groups.addAll(user.getGroups());
		groups.addAll(
			_groupLocalService.getOrganizationsGroups(user.getOrganizations()));
		groups.addAll(
			_groupLocalService.getOrganizationsRelatedGroups(
				user.getOrganizations()));
		groups.addAll(
			_groupLocalService.getUserGroupsGroups(user.getUserGroups()));
		groups.addAll(
			_groupLocalService.getUserGroupsRelatedGroups(
				user.getUserGroups()));

		for (Group group : groups) {
			Collections.addAll(
				roleIds,
				ArrayUtil.toArray(
					_groupLocalService.getRolePrimaryKeys(group.getGroupId())));
		}

		return roleIds;
	}

	protected String getSearchByUserRoles(
			KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery)
		throws Exception {

		Boolean searchByUserRoles =
			kaleoTaskInstanceTokenQuery.isSearchByUserRoles();

		if (searchByUserRoles == null) {
			return StringPool.BLANK;
		}

		if (searchByUserRoles) {
			List<Long> roleIds = getSearchByUserRoleIds(
				kaleoTaskInstanceTokenQuery);

			Map<Long, Set<Long>> roleIdGroupIdsMap = new HashMap<>();

			List<UserGroupRole> userGroupRoles =
				_userGroupRoleLocalService.getUserGroupRoles(
					kaleoTaskInstanceTokenQuery.getUserId());

			for (UserGroupRole userGroupRole : userGroupRoles) {
				mapRoleIdGroupId(
					userGroupRole.getRoleId(), userGroupRole.getGroupId(),
					roleIdGroupIdsMap);
			}

			List<UserGroupGroupRole> userGroupGroupRoles =
				getUserGroupGroupRoles(kaleoTaskInstanceTokenQuery.getUserId());

			for (UserGroupGroupRole userGroupGroupRole : userGroupGroupRoles) {
				mapRoleIdGroupId(
					userGroupGroupRole.getRoleId(),
					userGroupGroupRole.getGroupId(), roleIdGroupIdsMap);
			}

			if (roleIds.isEmpty() && roleIdGroupIdsMap.isEmpty()) {
				return StringPool.BLANK;
			}

			StringBundler sb = new StringBundler();

			sb.append("AND (");
			sb.append("KaleoTaskAssignmentInstance.assigneeClassName = '");
			sb.append(Role.class.getName());
			sb.append("' ");

			if (!roleIds.isEmpty()) {
				sb.append("AND (");

				sb.append("KaleoTaskAssignmentInstance.assigneeClassPK IN (");

				for (int i = 0; i < roleIds.size(); i++) {
					sb.append(roleIds.get(i));

					if (i >= (roleIds.size() - 1)) {
						continue;
					}

					sb.append(", ");
				}
			}

			if (roleIdGroupIdsMap.isEmpty()) {
				sb.append(")))");
			}
			else {
				if (!roleIds.isEmpty()) {
					sb.append(") OR ");
				}
				else {
					sb.append("AND ");
				}

				for (Map.Entry<Long, Set<Long>> entry :
						roleIdGroupIdsMap.entrySet()) {

					sb.append(
						"((KaleoTaskAssignmentInstance.assigneeClassPK = ");
					sb.append(entry.getKey());
					sb.append(") AND ");
					sb.append("(KaleoTaskAssignmentInstance.groupId IN (");

					Set<Long> groupIds = entry.getValue();

					Iterator<Long> iterator = groupIds.iterator();

					while (iterator.hasNext()) {
						sb.append(iterator.next());

						if (!iterator.hasNext()) {
							continue;
						}

						sb.append(", ");
					}

					sb.append("))) ");
					sb.append("OR ");
				}

				sb.setIndex(sb.index() - 1);

				if (!roleIds.isEmpty()) {
					sb.append("))");
				}
				else {
					sb.append(")");
				}
			}

			return sb.toString();
		}

		return StringBundler.concat(
			"AND ((KaleoTaskAssignmentInstance.assigneeClassName = '",
			User.class.getName(),
			"') AND (KaleoTaskAssignmentInstance.assigneeClassPK = ",
			kaleoTaskInstanceTokenQuery.getUserId(), "))");
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return KaleoTaskInstanceTokenModelImpl.TABLE_COLUMNS_MAP;
	}

	protected String getTaskNames(
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery,
		boolean firstCriteria) {

		String[] taskNames = kaleoTaskInstanceTokenQuery.getTaskNames();

		if (ArrayUtil.isEmpty(taskNames)) {
			return StringPool.BLANK;
		}

		List<String> taskNamesList = new ArrayList<>();

		for (String taskName : taskNames) {
			for (String keyword : _customSQL.keywords(taskName, false)) {
				taskNamesList.add(keyword);
			}
		}

		taskNames = taskNamesList.toArray(new String[0]);

		if (ArrayUtil.isEmpty(taskNames)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler((taskNames.length * 2) + 1);

		if (!firstCriteria) {
			sb.append("[$AND_OR_CONNECTOR$] (");
		}
		else {
			sb.append(StringPool.OPEN_PARENTHESIS);
		}

		for (int i = 0; i < taskNames.length; i++) {
			sb.append(
				"(LOWER(KaleoTaskInstanceToken.kaleoTaskName) LIKE LOWER(?))");
			sb.append(" OR ");
		}

		sb.setIndex(sb.index() - 1);

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	protected List<UserGroupGroupRole> getUserGroupGroupRoles(long userId)
		throws Exception {

		List<UserGroupGroupRole> userGroupGroupRoles = new ArrayList<>();

		List<UserGroup> userGroups = _userGroupLocalService.getUserUserGroups(
			userId);

		for (UserGroup userGroup : userGroups) {
			userGroupGroupRoles.addAll(
				_userGroupGroupRoleLocalService.getUserGroupGroupRoles(
					userGroup.getUserGroupId()));
		}

		return userGroupGroupRoles;
	}

	protected void mapRoleIdGroupId(
		long roleId, long groupId, Map<Long, Set<Long>> roleIdGroupIdsMap) {

		Set<Long> groupIds = roleIdGroupIdsMap.get(roleId);

		if (groupIds == null) {
			groupIds = new TreeSet<>();

			roleIdGroupIdsMap.put(roleId, groupIds);
		}

		groupIds.add(groupId);
	}

	protected void setAssetPrimaryKey(
		QueryPos queryPos,
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		Long[] assetPrimaryKeys =
			kaleoTaskInstanceTokenQuery.getAssetPrimaryKeys();

		if (ArrayUtil.isEmpty(assetPrimaryKeys)) {
			return;
		}

		queryPos.add(assetPrimaryKeys);
	}

	protected void setAssetTitle(
		QueryPos queryPos,
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		String assetTitle = kaleoTaskInstanceTokenQuery.getAssetTitle();

		if (Validator.isNull(assetTitle)) {
			return;
		}

		String[] assetTitles = _customSQL.keywords(assetTitle, false);

		queryPos.add(assetTitles);
	}

	protected void setAssetType(
		QueryPos queryPos,
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		String[] assetTypes = kaleoTaskInstanceTokenQuery.getAssetTypes();

		if (ArrayUtil.isEmpty(assetTypes)) {
			return;
		}

		assetTypes = _customSQL.keywords(assetTypes, false);

		queryPos.add(assetTypes);
	}

	protected void setAssigneeClassName(
		QueryPos queryPos,
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		String assigneeClassName =
			kaleoTaskInstanceTokenQuery.getAssigneeClassName();

		if (Validator.isNull(assigneeClassName)) {
			return;
		}

		queryPos.add(assigneeClassName);
	}

	protected void setCompleted(
		QueryPos queryPos,
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		Boolean completed = kaleoTaskInstanceTokenQuery.isCompleted();

		if (completed == null) {
			return;
		}

		queryPos.add(completed);
	}

	protected void setDueDateGT(
		QueryPos queryPos,
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		Date dueDateGT = kaleoTaskInstanceTokenQuery.getDueDateGT();

		if (dueDateGT == null) {
			return;
		}

		Timestamp dueDateGT_TS = CalendarUtil.getTimestamp(dueDateGT);

		queryPos.add(dueDateGT_TS);
	}

	protected void setDueDateLT(
		QueryPos queryPos,
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		Date dueDateLT = kaleoTaskInstanceTokenQuery.getDueDateLT();

		if (dueDateLT == null) {
			return;
		}

		Timestamp dueDateLT_TS = CalendarUtil.getTimestamp(dueDateLT);

		queryPos.add(dueDateLT_TS);
	}

	protected void setTaskNames(
		QueryPos queryPos,
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		String[] taskNames = kaleoTaskInstanceTokenQuery.getTaskNames();

		if (ArrayUtil.isEmpty(taskNames)) {
			return;
		}

		List<String> taskNamesList = new ArrayList<>();

		for (String taskName : taskNames) {
			for (String keyword : _customSQL.keywords(taskName, false)) {
				taskNamesList.add(keyword);
			}
		}

		taskNames = taskNamesList.toArray(new String[0]);

		if (ArrayUtil.isEmpty(taskNames)) {
			return;
		}

		queryPos.add(taskNames);
	}

	protected static final String FIRST_DUE_DATE_GT =
		"(KaleoTaskInstanceToken.dueDate >= ? [$AND_OR_NULL_CHECK$])";

	protected static final String FIRST_DUE_DATE_LT =
		"(KaleoTaskInstanceToken.dueDate <= ? [$AND_OR_NULL_CHECK$])";

	protected static final String NOT_FIRST_DUE_DATE_GT =
		"[$AND_OR_CONNECTOR$] (KaleoTaskInstanceToken.dueDate >= ? " +
			"[$AND_OR_NULL_CHECK$])";

	protected static final String NOT_FIRST_DUE_DATE_LT =
		"[$AND_OR_CONNECTOR$] (KaleoTaskInstanceToken.dueDate <= ? " +
			"[$AND_OR_NULL_CHECK$])";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"KaleoTaskInstanceToken.";

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UserGroupGroupRoleLocalService _userGroupGroupRoleLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}