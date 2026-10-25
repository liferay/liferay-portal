/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.sql.dsl.query.sort.OrderByExpression;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupTable;
import com.liferay.portal.kernel.model.Groups_OrgsTable;
import com.liferay.portal.kernel.model.Groups_RolesTable;
import com.liferay.portal.kernel.model.Groups_UserGroupsTable;
import com.liferay.portal.kernel.model.LayoutTable;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourcePermissionTable;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroupRoleTable;
import com.liferay.portal.kernel.model.Users_GroupsTable;
import com.liferay.portal.kernel.model.Users_OrgsTable;
import com.liferay.portal.kernel.model.Users_RolesTable;
import com.liferay.portal.kernel.model.Users_UserGroupsTable;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.RolePermissions;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourceActionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.GroupFinder;
import com.liferay.portal.kernel.service.persistence.GroupUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.comparator.GroupNameComparator;
import com.liferay.portal.model.impl.GroupImpl;
import com.liferay.portal.service.impl.GroupLocalServiceImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class GroupFinderImpl
	extends GroupFinderBaseImpl implements GroupFinder {

	public static final String COUNT_BY_LAYOUTS =
		GroupFinder.class.getName() + ".countByLayouts";

	public static final String COUNT_BY_GROUP_ID =
		GroupFinder.class.getName() + ".countByGroupId";

	public static final String FIND_BY_COMPANY_ID =
		GroupFinder.class.getName() + ".findByCompanyId";

	public static final String FIND_BY_LAYOUTS =
		GroupFinder.class.getName() + ".findByLayouts";

	public static final String FIND_BY_LIVE_GROUPS =
		GroupFinder.class.getName() + ".findByLiveGroups";

	public static final String FIND_BY_C_GK =
		GroupFinder.class.getName() + ".findByC_GK";

	public static final String FIND_BY_C_A =
		GroupFinder.class.getName() + ".findByC_A";

	public static final String FIND_BY_L_TS_S_RSGC =
		GroupFinder.class.getName() + ".findByL_TS_S_RSGC";

	public static final FinderPath FINDER_PATH_FIND_BY_C_A = new FinderPath(
		GroupPersistenceImpl.FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
		"GroupFinderImpl_findByC_A",
		new String[] {Long.class.getName(), Boolean.class.getName()},
		new String[] {"companyId", "active_"}, false);

	public static final String JOIN_BY_ACTION_ID =
		GroupFinder.class.getName() + ".joinByActionId";

	public static final String JOIN_BY_ACTIVE =
		GroupFinder.class.getName() + ".joinByActive";

	public static final String JOIN_BY_CREATOR_USER_ID =
		GroupFinder.class.getName() + ".joinByCreatorUserId";

	public static final String JOIN_BY_GROUP_ORG =
		GroupFinder.class.getName() + ".joinByGroupOrg";

	public static final String JOIN_BY_GROUPS_ORGS =
		GroupFinder.class.getName() + ".joinByGroupsOrgs";

	public static final String JOIN_BY_GROUPS_ROLES =
		GroupFinder.class.getName() + ".joinByGroupsRoles";

	public static final String JOIN_BY_GROUPS_USER_GROUPS =
		GroupFinder.class.getName() + ".joinByGroupsUserGroups";

	public static final String JOIN_BY_LAYOUT =
		GroupFinder.class.getName() + ".joinByLayout";

	public static final String JOIN_BY_MANUAL_MEMBERSHIP =
		GroupFinder.class.getName() + ".joinByManualMembership";

	public static final String JOIN_BY_MEMBERSHIP_RESTRICTION =
		GroupFinder.class.getName() + ".joinByMembershipRestriction";

	public static final String JOIN_BY_PAGE_COUNT =
		GroupFinder.class.getName() + ".joinByPageCount";

	public static final String JOIN_BY_ROLE_RESOURCE_PERMISSIONS =
		GroupFinder.class.getName() + ".joinByRoleResourcePermissions";

	public static final String JOIN_BY_SITE =
		GroupFinder.class.getName() + ".joinBySite";

	public static final String JOIN_BY_TYPE =
		GroupFinder.class.getName() + ".joinByType";

	public static final String JOIN_BY_USER_GROUP_ROLE =
		GroupFinder.class.getName() + ".joinByUserGroupRole";

	public static final String JOIN_BY_USERS_GROUPS =
		GroupFinder.class.getName() + ".joinByUsersGroups";

	@Override
	public int countByLayouts(
		long companyId, long parentGroupId, boolean site) {

		return countByLayouts(companyId, parentGroupId, site, null);
	}

	@Override
	public int countByLayouts(
		long companyId, long parentGroupId, boolean site, Boolean active) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_LAYOUTS);

			if (active != null) {
				sql = StringUtil.replace(
					sql, "[$ACTIVE$]", "AND (Group_.active_ = ?)");
			}
			else {
				sql = StringUtil.removeSubstring(sql, "[$ACTIVE$]");
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);
			queryPos.add(parentGroupId);
			queryPos.add(site);

			if (active != null) {
				queryPos.add(active);
			}

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
	public int countByG_U(long groupId, long userId, boolean inherit) {
		LinkedHashMap<String, Object> params1 =
			LinkedHashMapBuilder.<String, Object>put(
				"usersGroups", userId
			).build();

		LinkedHashMap<String, Object> params2 =
			LinkedHashMapBuilder.<String, Object>put(
				"groupOrg", userId
			).build();

		LinkedHashMap<String, Object> params3 =
			LinkedHashMapBuilder.<String, Object>put(
				"groupsOrgs", userId
			).build();

		LinkedHashMap<String, Object> params4 =
			LinkedHashMapBuilder.<String, Object>put(
				"groupsUserGroups", userId
			).build();

		Session session = null;

		try {
			session = openSession();

			int count = countByGroupId(session, groupId, params1);

			if (inherit) {
				count += countByGroupId(session, groupId, params2);
				count += countByGroupId(session, groupId, params3);
				count += countByGroupId(session, groupId, params4);
			}

			return count;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public int countByC_C_PG_N_D(
		long companyId, long[] classNameIds, long parentGroupId, String[] names,
		String[] descriptions, LinkedHashMap<String, Object> params,
		boolean andOperator) {

		names = CustomSQLUtil.keywords(names);
		descriptions = CustomSQLUtil.keywords(descriptions);

		if (params == null) {
			params = _emptyLinkedHashMap;
		}

		LinkedHashMap<String, Object> params1 = params;

		LinkedHashMap<String, Object> params2 = null;

		LinkedHashMap<String, Object> params3 = null;

		LinkedHashMap<String, Object> params4 = null;

		Long userId = (Long)params.get("usersGroups");

		boolean doUnion = Validator.isNotNull(userId);

		if (doUnion) {
			params2 = new LinkedHashMap<>(params1);
			params3 = new LinkedHashMap<>(params1);
			params4 = new LinkedHashMap<>(params1);

			_populateUnionParams(
				userId, classNameIds, params1, params2, params3, params4);
		}
		else if (classNameIds != null) {
			params1.put("classNameIds", classNameIds);
		}

		DSLQuery dslQuery = _buildGroupDSLQuery(
			companyId, parentGroupId, names, descriptions, params1, andOperator,
			false);

		if (doUnion) {
			if (params2.containsKey("classNameIds")) {
				dslQuery = dslQuery.union(
					_buildGroupDSLQuery(
						companyId, parentGroupId, names, descriptions, params2,
						andOperator, false));
			}

			if (params3.containsKey("classNameIds")) {
				dslQuery = dslQuery.union(
					_buildGroupDSLQuery(
						companyId, parentGroupId, names, descriptions, params3,
						andOperator, false));
			}

			if (params4.containsKey("classNameIds")) {
				dslQuery = dslQuery.union(
					_buildGroupDSLQuery(
						companyId, parentGroupId, names, descriptions, params4,
						andOperator, false));
			}
		}

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(dslQuery);

			sqlQuery.addScalar("groupId", Type.LONG);

			Set<Long> groupIds = new HashSet<>(sqlQuery.list(true));

			return groupIds.size();
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public Group fetchByC_GK(long companyId, String groupKey)
		throws NoSuchGroupException {

		groupKey = StringUtil.lowerCase(groupKey);

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_C_GK);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("Group_", GroupImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);
			queryPos.add(groupKey);

			List<Group> groups = sqlQuery.list();

			if (groups.isEmpty()) {
				return null;
			}

			return groups.get(0);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<Group> findByCompanyId(
		long companyId, LinkedHashMap<String, Object> params, int start,
		int end, OrderByComparator<Group> orderByComparator) {

		if (params == null) {
			params = _emptyLinkedHashMap;
		}

		LinkedHashMap<String, Object> params1 = params;

		LinkedHashMap<String, Object> params2 = null;

		LinkedHashMap<String, Object> params3 = null;

		LinkedHashMap<String, Object> params4 = null;

		Long userId = (Long)params.get("usersGroups");
		boolean inherit = GetterUtil.getBoolean(params.get("inherit"), true);

		boolean doUnion = false;

		if (Validator.isNotNull(userId) && inherit) {
			doUnion = true;
		}

		if (doUnion) {
			params2 = new LinkedHashMap<>(params1);
			params3 = new LinkedHashMap<>(params1);
			params4 = new LinkedHashMap<>(params1);

			_populateUnionParams(
				userId, null, params1, params2, params3, params4);
		}
		else {
			params1.put("classNameIds", _getGroupOrganizationClassNameIds());
		}

		String sqlKey = _buildSQLCacheKey(
			orderByComparator, params1, params2, params3, params4);

		String sql = _findByCompanyIdSQLCache.get(sqlKey);

		if (sql == null) {
			String findByCompanyIdSQL = CustomSQLUtil.get(FIND_BY_COMPANY_ID);

			if (params.get("active") == Boolean.TRUE) {
				findByCompanyIdSQL = StringUtil.removeSubstring(
					findByCompanyIdSQL, "(Group_.liveGroupId = 0) AND");
			}

			findByCompanyIdSQL = replaceOrderBy(
				findByCompanyIdSQL, orderByComparator);

			StringBundler sb = new StringBundler(11);

			sb.append(StringPool.OPEN_PARENTHESIS);
			sb.append(replaceJoinAndWhere(findByCompanyIdSQL, params1));

			if (doUnion) {
				sb.append(") UNION (");
				sb.append(replaceJoinAndWhere(findByCompanyIdSQL, params2));
				sb.append(") UNION (");
				sb.append(replaceJoinAndWhere(findByCompanyIdSQL, params3));
				sb.append(") UNION (");
				sb.append(replaceJoinAndWhere(findByCompanyIdSQL, params4));
			}

			sb.append(StringPool.CLOSE_PARENTHESIS);

			if (orderByComparator != null) {
				sb.append(" ORDER BY ");
				sb.append(orderByComparator.toString());
			}

			sql = sb.toString();

			_findByCompanyIdSQLCache.put(sqlKey, sql);
		}

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("groupId", Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			setJoin(queryPos, params1);

			queryPos.add(companyId);

			if (doUnion) {
				setJoin(queryPos, params2);

				queryPos.add(companyId);

				setJoin(queryPos, params3);

				queryPos.add(companyId);

				setJoin(queryPos, params4);

				queryPos.add(companyId);
			}

			List<Long> groupIds = (List<Long>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);

			List<Group> groups = new ArrayList<>(groupIds.size());

			for (Long groupId : groupIds) {
				Group group = GroupUtil.findByPrimaryKey(groupId);

				groups.add(group);
			}

			return groups;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<Group> findByLayouts(
		long companyId, long parentGroupId, boolean site, Boolean active,
		int start, int end, OrderByComparator<Group> orderByComparator) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_LAYOUTS);

			sql = CustomSQLUtil.replaceOrderBy(sql, orderByComparator);

			if (active != null) {
				sql = StringUtil.replace(
					sql, "[$ACTIVE$]", "AND (Group_.active_ = ?)");
			}
			else {
				sql = StringUtil.removeSubstring(sql, "[$ACTIVE$]");
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("Group_", GroupImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);
			queryPos.add(parentGroupId);
			queryPos.add(site);

			if (active != null) {
				queryPos.add(active);
			}

			return sqlQuery.list(true);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<Group> findByLiveGroups() {
		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_LIVE_GROUPS);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("Group_", GroupImpl.class);

			return sqlQuery.list(true);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<Long> findByC_A(long companyId, boolean active) {
		Object[] finderArgs = {companyId, active};

		List<Long> list = (List<Long>)FinderCacheUtil.getResult(
			FINDER_PATH_FIND_BY_C_A, finderArgs, GroupUtil.getPersistence());

		if (list != null) {
			return list;
		}

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_C_A);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("groupId", Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);
			queryPos.add(active);

			list = sqlQuery.list(true);

			FinderCacheUtil.putResult(
				FINDER_PATH_FIND_BY_C_A, finderArgs, list);

			return list;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<Group> findByL_TS_S_RSGC(
		long liveGroupId, String typeSettings, boolean site,
		int remoteStagingGroupCount) {

		String sql = CustomSQLUtil.get(FIND_BY_L_TS_S_RSGC);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("Group_", GroupImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(liveGroupId);
			queryPos.add(StringUtil.quote(typeSettings, StringPool.PERCENT));
			queryPos.add(site);
			queryPos.add(remoteStagingGroupCount);

			return (List<Group>)QueryUtil.list(
				sqlQuery, getDialect(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<Group> findByC_C_PG_N_D(
		long companyId, long[] classNameIds, long parentGroupId, String[] names,
		String[] descriptions, LinkedHashMap<String, Object> params,
		boolean andOperator, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		names = CustomSQLUtil.keywords(names);
		descriptions = CustomSQLUtil.keywords(descriptions);

		if (params == null) {
			params = _emptyLinkedHashMap;
		}

		LinkedHashMap<String, Object> params1 = params;

		LinkedHashMap<String, Object> params2 = null;

		LinkedHashMap<String, Object> params3 = null;

		LinkedHashMap<String, Object> params4 = null;

		Long userId = (Long)params.get("usersGroups");
		boolean inherit = GetterUtil.getBoolean(params.get("inherit"), true);

		boolean doUnion = false;

		if (Validator.isNotNull(userId) && inherit) {
			doUnion = true;
		}

		if (doUnion) {
			params2 = new LinkedHashMap<>(params1);
			params3 = new LinkedHashMap<>(params1);
			params4 = new LinkedHashMap<>(params1);

			_populateUnionParams(
				userId, classNameIds, params1, params2, params3, params4);
		}
		else if (classNameIds != null) {
			params1.put("classNameIds", classNameIds);
		}

		if (orderByComparator == null) {
			orderByComparator = new GroupNameComparator(true);
		}

		DSLQuery dslQuery = _buildGroupDSLQuery(
			companyId, parentGroupId, names, descriptions, params1, andOperator,
			true);

		if (doUnion) {
			if (params2.containsKey("classNameIds")) {
				dslQuery = dslQuery.union(
					_buildGroupDSLQuery(
						companyId, parentGroupId, names, descriptions, params2,
						andOperator, true));
			}

			if (params3.containsKey("classNameIds")) {
				dslQuery = dslQuery.union(
					_buildGroupDSLQuery(
						companyId, parentGroupId, names, descriptions, params3,
						andOperator, true));
			}

			if (params4.containsKey("classNameIds")) {
				dslQuery = dslQuery.union(
					_buildGroupDSLQuery(
						companyId, parentGroupId, names, descriptions, params4,
						andOperator, true));
			}
		}

		if (orderByComparator != null) {
			GroupTable tempGroupTable = GroupTable.INSTANCE.as("tempGroup");

			dslQuery = DSLQueryFactoryUtil.select(
				tempGroupTable.groupId
			).from(
				dslQuery.as("tempGroup")
			).orderBy(
				_getOrderByExpressions(orderByComparator, tempGroupTable)
			);
		}

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(dslQuery);

			sqlQuery.addScalar("groupId", Type.LONG);

			List<Long> groupIds = (List<Long>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);

			List<Group> groups = new ArrayList<>(groupIds.size());

			for (Long groupId : groupIds) {
				Group group = GroupUtil.findByPrimaryKey(groupId);

				groups.add(group);
			}

			return groups;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public Map<Long, long[]> findByC_C_S_A_UserInheritedGroupIds(
		long companyId) {

		long[] groupOrganizationClassNameIds =
			_getGroupOrganizationClassNameIds();

		long groupClassNameId = groupOrganizationClassNameIds[0];
		long organizationClassNameId = groupOrganizationClassNameIds[1];

		Predicate activeSitePredicate = _getActiveSitePredicate(companyId);

		Map<Long, Set<Long>> groupIdSetsMap = new HashMap<>();

		// Branch 1: Direct user group membership

		_addBulkUserGroupIds(
			groupIdSetsMap,
			_getJoinStep(
				DSLQueryFactoryUtil.selectDistinct(
					Users_GroupsTable.INSTANCE.userId,
					GroupTable.INSTANCE.groupId
				).from(
					GroupTable.INSTANCE
				),
				LinkedHashMapBuilder.<String, Object>put(
					"usersGroups", Boolean.TRUE
				).build()),
			activeSitePredicate.and(
				_getClassNameIdPredicate(groupOrganizationClassNameIds)));

		// Branch 2: Groups for user's organizations (via classPK)

		_addBulkUserGroupIds(
			groupIdSetsMap,
			_getJoinStep(
				DSLQueryFactoryUtil.selectDistinct(
					Users_OrgsTable.INSTANCE.userId, GroupTable.INSTANCE.groupId
				).from(
					GroupTable.INSTANCE
				),
				LinkedHashMapBuilder.<String, Object>put(
					"groupOrg", Boolean.TRUE
				).build()),
			activeSitePredicate.and(
				GroupTable.INSTANCE.classNameId.eq(organizationClassNameId)));

		// Branch 3: Groups related to user's organizations

		_addBulkUserGroupIds(
			groupIdSetsMap,
			_getJoinStep(
				DSLQueryFactoryUtil.selectDistinct(
					Users_OrgsTable.INSTANCE.userId, GroupTable.INSTANCE.groupId
				).from(
					GroupTable.INSTANCE
				),
				LinkedHashMapBuilder.<String, Object>put(
					"groupsOrgs", Boolean.TRUE
				).build()),
			activeSitePredicate.and(
				GroupTable.INSTANCE.classNameId.eq(groupClassNameId)));

		// Branch 4: Groups related to user's user groups

		_addBulkUserGroupIds(
			groupIdSetsMap,
			_getJoinStep(
				DSLQueryFactoryUtil.selectDistinct(
					Users_UserGroupsTable.INSTANCE.userId,
					GroupTable.INSTANCE.groupId
				).from(
					GroupTable.INSTANCE
				),
				LinkedHashMapBuilder.<String, Object>put(
					"groupsUserGroups", Boolean.TRUE
				).build()),
			activeSitePredicate.and(
				_getClassNameIdPredicate(groupOrganizationClassNameIds)));

		Map<Long, long[]> result = new HashMap<>();

		for (Map.Entry<Long, Set<Long>> entry : groupIdSetsMap.entrySet()) {
			result.put(entry.getKey(), ArrayUtil.toLongArray(entry.getValue()));
		}

		return result;
	}

	protected int countByGroupId(
			Session session, long groupId, Map<String, Object> params)
		throws Exception {

		String sql = CustomSQLUtil.get(COUNT_BY_GROUP_ID);

		sql = replaceJoinAndWhere(sql, params);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		setJoin(queryPos, params);

		queryPos.add(groupId);

		Iterator<Long> iterator = sqlQuery.iterate();

		if (iterator.hasNext()) {
			Long count = iterator.next();

			if (count != null) {
				return count.intValue();
			}
		}

		return 0;
	}

	protected String getJoin(Map<String, Object> params) {
		if ((params == null) || params.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(params.size());

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			if (Validator.isNull(entry.getValue())) {
				continue;
			}

			String key = entry.getKey();

			if (key.equals("rolePermissions")) {
				key = "rolePermissions_6";
			}

			Map<String, String> joinMap = _getJoinMap();

			String joinValue = joinMap.get(key);

			if (Validator.isNull(joinValue)) {
				continue;
			}

			sb.append(joinValue);
		}

		return sb.toString();
	}

	protected String getWhere(Map<String, Object> params) {
		if ((params == null) || params.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(params.size());

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();

			if (key.equals("classNameIds")) {
				if (entry.getValue() instanceof Long) {
					sb.append("(Group_.classNameId = ?) AND ");
				}
				else {
					sb.append(StringPool.OPEN_PARENTHESIS);

					long[] classNameIds = (long[])entry.getValue();

					for (int i = 0; i < classNameIds.length; i++) {
						sb.append("(Group_.classNameId = ?) OR ");
					}

					sb.setStringAt(
						"(Group_.classNameId = ?)) AND ", sb.index() - 1);
				}
			}
			else if (key.equals("excludedGroupIds")) {
				List<Long> excludedGroupIds = (List<Long>)entry.getValue();

				if (!excludedGroupIds.isEmpty()) {
					sb.append(StringPool.OPEN_PARENTHESIS);

					for (int i = 0; i < excludedGroupIds.size(); i++) {
						sb.append("(Group_.groupId != ?) AND ");
					}

					sb.setStringAt(
						"(Group_.groupId != ?)) AND ", sb.index() - 1);
				}
			}
			else if (key.equals("groupsTree")) {
				List<Group> groupsTree = (List<Group>)entry.getValue();

				if (!groupsTree.isEmpty()) {
					sb.append(StringPool.OPEN_PARENTHESIS);

					for (int i = 0; i < groupsTree.size(); i++) {
						sb.append("(Group_.treePath LIKE ?) OR ");
					}

					sb.setStringAt(
						"(Group_.treePath LIKE ?)) AND ", sb.index() - 1);
				}
			}
			else if (key.equals("types")) {
				List<Integer> types = (List<Integer>)entry.getValue();

				if (!types.isEmpty()) {
					sb.append(StringPool.OPEN_PARENTHESIS);

					for (int i = 0; i < types.size(); i++) {
						sb.append("(Group_.type_ = ?) OR ");
					}

					sb.setStringAt("(Group_.type_ = ?)) AND ", sb.index() - 1);
				}
			}
			else {
				if (key.equals("rolePermissions")) {
					key = "rolePermissions_6";
				}

				Map<String, String> whereMap = _getWhereMap();

				String whereValue = whereMap.get(key);

				if (Validator.isNotNull(whereValue)) {
					sb.append(whereValue);
				}
			}
		}

		return sb.toString();
	}

	protected String replaceJoinAndWhere(
		String sql, Map<String, Object> params) {

		if (params.isEmpty()) {
			return StringUtil.removeSubstrings(sql, "[$JOIN$]", "[$WHERE$]");
		}

		String cacheKey = _buildSQLCacheKey(sql, params);

		String resultSQL = _replaceJoinAndWhereSQLCache.get(cacheKey);

		if (resultSQL == null) {
			sql = StringUtil.replace(sql, "[$JOIN$]", getJoin(params));

			resultSQL = StringUtil.replace(sql, "[$WHERE$]", getWhere(params));

			_replaceJoinAndWhereSQLCache.put(cacheKey, resultSQL);
		}

		return resultSQL;
	}

	protected String replaceOrderBy(
		String sql, OrderByComparator<Group> orderByComparator) {

		if (orderByComparator instanceof GroupNameComparator) {
			sql = StringUtil.replace(
				sql, "Group_.name AS groupName",
				"REPLACE(Group_.name, '" +
					GroupLocalServiceImpl.ORGANIZATION_NAME_SUFFIX +
						"', '') AS groupName");
		}

		return sql;
	}

	protected void setJoin(QueryPos queryPos, Map<String, Object> params)
		throws Exception {

		if (params == null) {
			return;
		}

		if (params.containsKey("actionId")) {
			Long userId = _getUserId(params);

			queryPos.add(userId);
			queryPos.add(userId);
		}

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();

			if (key.equals("actionId")) {
				Long userId = _getUserId(params);

				int hasUserRole = 0;

				Long companyId = CompanyThreadLocal.getCompanyId();

				Role adminRole = RoleLocalServiceUtil.fetchRole(
					companyId, RoleConstants.ADMINISTRATOR);

				if (RoleLocalServiceUtil.hasUserRole(
						userId, adminRole.getRoleId())) {

					hasUserRole = 1;
				}

				queryPos.add(hasUserRole);

				Role siteAdminRole = RoleLocalServiceUtil.fetchRole(
					companyId, RoleConstants.SITE_ADMINISTRATOR);

				queryPos.add(siteAdminRole.getRoleId());

				Role siteOwnerRole = RoleLocalServiceUtil.fetchRole(
					companyId, RoleConstants.SITE_OWNER);

				queryPos.add(siteOwnerRole.getRoleId());

				ResourceAction resourceAction =
					ResourceActionLocalServiceUtil.getResourceAction(
						Group.class.getName(), (String)entry.getValue());

				queryPos.add(resourceAction.getBitwiseValue());
			}
			else if (key.equals("active") || key.equals("layout") ||
					 key.equals("manualMembership") || key.equals("site")) {

				Boolean value = (Boolean)entry.getValue();

				queryPos.add(value);
			}
			else if (key.equals("classNameIds")) {
				if (entry.getValue() instanceof Long) {
					queryPos.add((long)entry.getValue());
				}
				else {
					for (long classNameId : (long[])entry.getValue()) {
						queryPos.add(classNameId);
					}
				}
			}
			else if (key.equals("excludedGroupIds")) {
				List<Long> excludedGroupIds = (List<Long>)entry.getValue();

				if (!excludedGroupIds.isEmpty()) {
					for (long excludedGroupId : excludedGroupIds) {
						queryPos.add(excludedGroupId);
					}
				}
			}
			else if (key.equals("groupsTree")) {
				List<Group> groupsTree = (List<Group>)entry.getValue();

				if (!groupsTree.isEmpty()) {
					for (Group group : groupsTree) {
						queryPos.add(
							StringBundler.concat(
								StringPool.PERCENT, StringPool.SLASH,
								group.getGroupId(), StringPool.SLASH,
								StringPool.PERCENT));
					}
				}
			}
			else if (key.equals("pageCount")) {
			}
			else if (key.equals("rolePermissions")) {
				RolePermissions rolePermissions =
					(RolePermissions)entry.getValue();

				ResourceAction resourceAction =
					ResourceActionLocalServiceUtil.getResourceAction(
						rolePermissions.getName(),
						rolePermissions.getActionId());

				queryPos.add(rolePermissions.getName());
				queryPos.add(rolePermissions.getScope());
				queryPos.add(rolePermissions.getRoleId());

				queryPos.add(resourceAction.getBitwiseValue());
			}
			else if (key.equals("types")) {
				List<Integer> values = (List<Integer>)entry.getValue();

				for (Integer value : values) {
					queryPos.add(value);
				}
			}
			else if (key.equals("userGroupRole")) {
				List<Long> values = (List<Long>)entry.getValue();

				Long userId = values.get(0);
				Long roleId = values.get(1);

				queryPos.add(userId);
				queryPos.add(roleId);
			}
			else if (key.equals("userId")) {
			}
			else {
				Object value = entry.getValue();

				if (value instanceof Integer) {
					Integer valueInteger = (Integer)value;

					if (valueInteger != null) {
						queryPos.add(valueInteger);
					}
				}
				else if (value instanceof Long) {
					Long valueLong = (Long)value;

					if (Validator.isNotNull(valueLong)) {
						queryPos.add(valueLong);
					}
				}
				else if (value instanceof String) {
					String valueString = (String)value;

					if (Validator.isNotNull(valueString)) {
						queryPos.add(valueString);
					}
				}
			}
		}
	}

	private void _addBulkUserGroupIds(
		Map<Long, Set<Long>> groupIdSetsMap, JoinStep joinStep,
		Predicate predicate) {

		for (Object[] values :
				groupPersistence.<List<Object[]>>dslQuery(
					joinStep.where(predicate), false)) {

			Set<Long> groupIds = groupIdSetsMap.computeIfAbsent(
				(Long)values[0], key -> new HashSet<>());

			groupIds.add((Long)values[1]);
		}
	}

	private DSLQuery _buildGroupDSLQuery(
		long companyId, long parentGroupId, String[] names,
		String[] descriptions, Map<String, Object> params, boolean andOperator,
		boolean includeOrderByColumns) {

		FromStep fromStep;

		if (includeOrderByColumns) {
			fromStep = DSLQueryFactoryUtil.selectDistinct(
				GroupTable.INSTANCE.groupId, GroupTable.INSTANCE.name,
				GroupTable.INSTANCE.type, GroupTable.INSTANCE.friendlyURL);
		}
		else {
			fromStep = DSLQueryFactoryUtil.selectDistinct(
				GroupTable.INSTANCE.groupId);
		}

		JoinStep joinStep = fromStep.from(GroupTable.INSTANCE);

		joinStep = _getJoinStep(joinStep, params);

		Predicate predicate = _getBasePredicate(
			companyId, parentGroupId, names, descriptions, andOperator);

		predicate = Predicate.and(predicate, _getParamsPredicate(params));

		return joinStep.where(predicate);
	}

	@SafeVarargs
	private final String _buildSQLCacheKey(
		OrderByComparator<Group> orderByComparator,
		Map<String, Object>... params) {

		if (orderByComparator == null) {
			return _buildSQLCacheKey(StringPool.BLANK, params);
		}

		return _buildSQLCacheKey(orderByComparator.getOrderBy(), params);
	}

	@SafeVarargs
	private final String _buildSQLCacheKey(
		String sql, Map<String, Object>... params) {

		int size = 1;

		for (Map<String, Object> param : params) {
			if (param == null) {
				continue;
			}

			size += param.size() * 5;
		}

		StringBundler sb = new StringBundler(size);

		sb.append(sql);

		for (Map<String, Object> param : params) {
			if (param == null) {
				continue;
			}

			for (Map.Entry<String, Object> entry : param.entrySet()) {
				sb.append(StringPool.COMMA);

				String key = entry.getKey();

				if (key.equals("rolePermissions")) {
					key = "rolePermissions_6";
				}

				sb.append(key);
				sb.append(StringPool.DASH);

				Object value = entry.getValue();

				if (value instanceof long[]) {
					long[] values = (long[])value;

					sb.append(values.length);
				}
				else if (value instanceof Collection<?>) {
					Collection<?> values = (Collection<?>)value;

					sb.append(values.size());
				}

				sb.append(StringPool.COMMA);
			}
		}

		return sb.toString();
	}

	private Predicate _getActionIdPredicate(
		String actionId, Map<String, Object> params) {

		Long userId = _getUserId(params);

		Long companyId = CompanyThreadLocal.getCompanyId();

		Role adminRole = RoleLocalServiceUtil.fetchRole(
			companyId, RoleConstants.ADMINISTRATOR);

		if (RoleLocalServiceUtil.hasUserRole(userId, adminRole.getRoleId())) {
			return null;
		}

		ResourceAction resourceAction =
			ResourceActionLocalServiceUtil.fetchResourceAction(
				Group.class.getName(), actionId);

		if (resourceAction == null) {
			return GroupTable.INSTANCE.groupId.eq(0L);
		}

		Role siteAdminRole = RoleLocalServiceUtil.fetchRole(
			companyId, RoleConstants.SITE_ADMINISTRATOR);

		Role siteOwnerRole = RoleLocalServiceUtil.fetchRole(
			companyId, RoleConstants.SITE_OWNER);

		// Groups where user has site admin or site owner role

		Predicate siteRolePredicate = GroupTable.INSTANCE.groupId.in(
			DSLQueryFactoryUtil.select(
				UserGroupRoleTable.INSTANCE.groupId
			).from(
				UserGroupRoleTable.INSTANCE
			).innerJoinON(
				Users_GroupsTable.INSTANCE,
				Users_GroupsTable.INSTANCE.groupId.eq(
					UserGroupRoleTable.INSTANCE.groupId
				).and(
					Users_GroupsTable.INSTANCE.userId.eq(userId)
				)
			).where(
				UserGroupRoleTable.INSTANCE.userId.eq(
					userId
				).and(
					UserGroupRoleTable.INSTANCE.roleId.eq(
						siteAdminRole.getRoleId()
					).or(
						UserGroupRoleTable.INSTANCE.roleId.eq(
							siteOwnerRole.getRoleId())
					).withParentheses()
				)
			));

		// Groups where user has a site role with matching resource permission

		Predicate siteRolePermissionPredicate = GroupTable.INSTANCE.groupId.in(
			DSLQueryFactoryUtil.select(
				Users_GroupsTable.INSTANCE.groupId
			).from(
				Users_GroupsTable.INSTANCE
			).innerJoinON(
				UserGroupRoleTable.INSTANCE,
				UserGroupRoleTable.INSTANCE.userId.eq(
					Users_GroupsTable.INSTANCE.userId
				).and(
					UserGroupRoleTable.INSTANCE.groupId.eq(
						Users_GroupsTable.INSTANCE.groupId)
				)
			).innerJoinON(
				ResourcePermissionTable.INSTANCE,
				ResourcePermissionTable.INSTANCE.roleId.eq(
					UserGroupRoleTable.INSTANCE.roleId)
			).where(
				Users_GroupsTable.INSTANCE.userId.eq(
					userId
				).and(
					DSLFunctionFactoryUtil.bitAnd(
						ResourcePermissionTable.INSTANCE.actionIds,
						resourceAction.getBitwiseValue()
					).neq(
						0L
					)
				)
			));

		// Groups where user has a global role with matching resource permission

		Predicate globalRolePermissionPredicate =
			GroupTable.INSTANCE.groupId.in(
				DSLQueryFactoryUtil.select(
					Users_GroupsTable.INSTANCE.groupId
				).from(
					Users_GroupsTable.INSTANCE
				).innerJoinON(
					Users_RolesTable.INSTANCE,
					Users_RolesTable.INSTANCE.userId.eq(
						Users_GroupsTable.INSTANCE.userId)
				).innerJoinON(
					ResourcePermissionTable.INSTANCE,
					ResourcePermissionTable.INSTANCE.roleId.eq(
						Users_RolesTable.INSTANCE.roleId)
				).where(
					Users_GroupsTable.INSTANCE.userId.eq(
						userId
					).and(
						DSLFunctionFactoryUtil.bitAnd(
							ResourcePermissionTable.INSTANCE.actionIds,
							resourceAction.getBitwiseValue()
						).neq(
							0L
						)
					)
				));

		return siteRolePredicate.or(
			siteRolePermissionPredicate
		).or(
			globalRolePermissionPredicate
		).withParentheses();
	}

	private Predicate _getActiveSitePredicate(long companyId) {
		return GroupTable.INSTANCE.companyId.eq(
			companyId
		).and(
			GroupTable.INSTANCE.parentGroupId.eq(0L)
		).and(
			GroupTable.INSTANCE.liveGroupId.eq(0L)
		).and(
			GroupTable.INSTANCE.active.eq(true)
		).and(
			GroupTable.INSTANCE.site.eq(true)
		);
	}

	private Predicate _getBasePredicate(
		long companyId, long parentGroupId, String[] names,
		String[] descriptions, boolean andOperator) {

		Predicate predicate = GroupTable.INSTANCE.companyId.eq(companyId);

		if (parentGroupId == GroupConstants.ANY_PARENT_GROUP_ID) {
			predicate = predicate.and(
				GroupTable.INSTANCE.parentGroupId.neq(parentGroupId));
		}
		else {
			predicate = predicate.and(
				GroupTable.INSTANCE.parentGroupId.eq(parentGroupId));
		}

		predicate = predicate.and(
			GroupTable.INSTANCE.liveGroupId.eq(0L)
		).and(
			GroupTable.INSTANCE.groupKey.neq("Control Panel")
		);

		Predicate keywordsPredicate = _getKeywordsPredicate(
			names, descriptions, andOperator);

		if (keywordsPredicate != null) {
			predicate = predicate.and(keywordsPredicate);
		}

		return predicate;
	}

	private Predicate _getClassNameIdPredicate(long[] classNameIds) {
		Predicate predicate = null;

		for (long classNameId : classNameIds) {
			predicate = Predicate.or(
				predicate, GroupTable.INSTANCE.classNameId.eq(classNameId));
		}

		if (predicate != null) {
			return predicate.withParentheses();
		}

		return null;
	}

	private String _getCondition(String join) {
		if (Validator.isNotNull(join)) {
			int pos = join.lastIndexOf("WHERE");

			if (pos != -1) {
				join = StringPool.OPEN_PARENTHESIS + join.substring(pos + 5);

				join = join.concat(") AND ");
			}
			else {
				join = StringPool.BLANK;
			}
		}

		return join;
	}

	private long[] _getGroupOrganizationClassNameIds() {
		if (_groupOrganizationClassNameIds == null) {
			_groupOrganizationClassNameIds = new long[] {
				ClassNameLocalServiceUtil.getClassNameId(Group.class),
				ClassNameLocalServiceUtil.getClassNameId(Organization.class)
			};
		}

		return _groupOrganizationClassNameIds;
	}

	private Map<String, String> _getJoinMap() {
		if (_joinMap != null) {
			return _joinMap;
		}

		Map<String, String> joinMap = HashMapBuilder.put(
			"actionId", _removeWhere(CustomSQLUtil.get(JOIN_BY_ACTION_ID))
		).put(
			"active", _removeWhere(CustomSQLUtil.get(JOIN_BY_ACTIVE))
		).put(
			"groupOrg", _removeWhere(CustomSQLUtil.get(JOIN_BY_GROUP_ORG))
		).put(
			"groupsOrgs", _removeWhere(CustomSQLUtil.get(JOIN_BY_GROUPS_ORGS))
		).put(
			"groupsRoles", _removeWhere(CustomSQLUtil.get(JOIN_BY_GROUPS_ROLES))
		).put(
			"groupsUserGroups",
			_removeWhere(CustomSQLUtil.get(JOIN_BY_GROUPS_USER_GROUPS))
		).put(
			"layout", _removeWhere(CustomSQLUtil.get(JOIN_BY_LAYOUT))
		).put(
			"membershipRestriction",
			_removeWhere(CustomSQLUtil.get(JOIN_BY_MEMBERSHIP_RESTRICTION))
		).put(
			"pageCount", _removeWhere(CustomSQLUtil.get(JOIN_BY_PAGE_COUNT))
		).put(
			"rolePermissions_6",
			_removeWhere(CustomSQLUtil.get(JOIN_BY_ROLE_RESOURCE_PERMISSIONS))
		).put(
			"site", _removeWhere(CustomSQLUtil.get(JOIN_BY_SITE))
		).put(
			"type", _removeWhere(CustomSQLUtil.get(JOIN_BY_TYPE))
		).put(
			"userGroupRole",
			_removeWhere(CustomSQLUtil.get(JOIN_BY_USER_GROUP_ROLE))
		).put(
			"usersGroups", _removeWhere(CustomSQLUtil.get(JOIN_BY_USERS_GROUPS))
		).build();

		_joinMap = joinMap;

		return _joinMap;
	}

	private JoinStep _getJoinStep(
		JoinStep joinStep, Map<String, Object> params) {

		if ((params == null) || params.isEmpty()) {
			return joinStep;
		}

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			if (Validator.isNull(entry.getValue())) {
				continue;
			}

			String key = entry.getKey();

			if (key.equals("groupOrg")) {
				joinStep = joinStep.innerJoinON(
					Users_OrgsTable.INSTANCE,
					Users_OrgsTable.INSTANCE.organizationId.eq(
						GroupTable.INSTANCE.classPK));
			}
			else if (key.equals("groupsOrgs")) {
				joinStep = joinStep.innerJoinON(
					Groups_OrgsTable.INSTANCE,
					Groups_OrgsTable.INSTANCE.groupId.eq(
						GroupTable.INSTANCE.groupId)
				).innerJoinON(
					Users_OrgsTable.INSTANCE,
					Users_OrgsTable.INSTANCE.organizationId.eq(
						Groups_OrgsTable.INSTANCE.organizationId)
				);
			}
			else if (key.equals("groupsRoles")) {
				joinStep = joinStep.innerJoinON(
					Groups_RolesTable.INSTANCE,
					Groups_RolesTable.INSTANCE.groupId.eq(
						GroupTable.INSTANCE.groupId));
			}
			else if (key.equals("groupsUserGroups")) {
				joinStep = joinStep.innerJoinON(
					Groups_UserGroupsTable.INSTANCE,
					Groups_UserGroupsTable.INSTANCE.groupId.eq(
						GroupTable.INSTANCE.groupId)
				).innerJoinON(
					Users_UserGroupsTable.INSTANCE,
					Users_UserGroupsTable.INSTANCE.userGroupId.eq(
						Groups_UserGroupsTable.INSTANCE.userGroupId)
				);
			}
			else if (key.equals("layout") || key.equals("pageCount")) {
				joinStep = joinStep.innerJoinON(
					LayoutTable.INSTANCE,
					LayoutTable.INSTANCE.groupId.eq(
						GroupTable.INSTANCE.groupId));
			}
			else if (key.equals("rolePermissions")) {
				joinStep = joinStep.innerJoinON(
					ResourcePermissionTable.INSTANCE,
					ResourcePermissionTable.INSTANCE.primKeyId.eq(
						GroupTable.INSTANCE.groupId));
			}
			else if (key.equals("userGroupRole")) {
				joinStep = joinStep.innerJoinON(
					UserGroupRoleTable.INSTANCE,
					UserGroupRoleTable.INSTANCE.groupId.eq(
						GroupTable.INSTANCE.groupId));
			}
			else if (key.equals("usersGroups")) {
				joinStep = joinStep.innerJoinON(
					Users_GroupsTable.INSTANCE,
					Users_GroupsTable.INSTANCE.groupId.eq(
						GroupTable.INSTANCE.groupId));
			}
		}

		return joinStep;
	}

	private Predicate _getKeywordsPredicate(
		String[] names, String[] descriptions, boolean andOperator) {

		Predicate namePredicate = null;

		for (String name : names) {
			if (Validator.isNull(name)) {
				continue;
			}

			Predicate likePredicate = DSLFunctionFactoryUtil.lower(
				GroupTable.INSTANCE.name
			).like(
				StringUtil.toLowerCase(name)
			);

			if (namePredicate == null) {
				namePredicate = likePredicate;
			}
			else {
				namePredicate = namePredicate.or(likePredicate);
			}
		}

		Predicate descriptionPredicate = null;

		for (String description : descriptions) {
			if (Validator.isNull(description)) {
				continue;
			}

			Predicate likePredicate = DSLFunctionFactoryUtil.lower(
				GroupTable.INSTANCE.description
			).like(
				StringUtil.toLowerCase(description)
			);

			if (descriptionPredicate == null) {
				descriptionPredicate = likePredicate;
			}
			else {
				descriptionPredicate = descriptionPredicate.or(likePredicate);
			}
		}

		if ((namePredicate == null) && (descriptionPredicate == null)) {
			return null;
		}

		if (namePredicate != null) {
			namePredicate = namePredicate.withParentheses();
		}

		if (descriptionPredicate != null) {
			descriptionPredicate = descriptionPredicate.withParentheses();
		}

		if (andOperator) {
			return Predicate.and(namePredicate, descriptionPredicate);
		}

		return Predicate.or(
			namePredicate, descriptionPredicate
		).withParentheses();
	}

	private OrderByExpression[] _getOrderByExpressions(
		OrderByComparator<Group> orderByComparator, GroupTable groupTable) {

		if (orderByComparator == null) {
			return new OrderByExpression[0];
		}

		String[] orderByFields = orderByComparator.getOrderByFields();

		OrderByExpression[] orderByExpressions =
			new OrderByExpression[orderByFields.length];

		for (int i = 0; i < orderByFields.length; i++) {
			Expression<?> expression = _getOrderByFieldExpression(
				orderByFields[i], groupTable);

			if (orderByComparator.isAscending(orderByFields[i])) {
				orderByExpressions[i] = expression.ascending();
			}
			else {
				orderByExpressions[i] = expression.descending();
			}
		}

		return orderByExpressions;
	}

	private Expression<?> _getOrderByFieldExpression(
		String field, GroupTable groupTable) {

		if (field.equals("groupName")) {
			return DSLFunctionFactoryUtil.replace(
				groupTable.name, GroupLocalServiceImpl.ORGANIZATION_NAME_SUFFIX,
				"");
		}
		else if (field.equals("name")) {
			return groupTable.name;
		}
		else if (field.equals("groupId")) {
			return groupTable.groupId;
		}
		else if (field.equals("groupType") || field.equals("type")) {
			return groupTable.type;
		}
		else if (field.equals("groupFriendlyURL") ||
				 field.equals("friendlyURL")) {

			return groupTable.friendlyURL;
		}
		else if (field.equals("description")) {
			return groupTable.description;
		}

		return groupTable.groupId;
	}

	private Predicate _getParamsPredicate(Map<String, Object> params) {
		if ((params == null) || params.isEmpty()) {
			return null;
		}

		Predicate predicate = null;

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			if (Validator.isNull(entry.getValue())) {
				continue;
			}

			String key = entry.getKey();

			Object value = entry.getValue();

			Predicate paramPredicate = null;

			if (key.equals("actionId")) {
				paramPredicate = _getActionIdPredicate((String)value, params);
			}
			else if (key.equals("active")) {
				paramPredicate = GroupTable.INSTANCE.liveGroupId.eq(
					0L
				).and(
					GroupTable.INSTANCE.active.eq((Boolean)value)
				).withParentheses();
			}
			else if (key.equals("classNameIds")) {
				if (value instanceof Long) {
					paramPredicate = GroupTable.INSTANCE.classNameId.eq(
						(Long)value);
				}
				else {
					long[] classNameIds = (long[])value;

					Predicate classNamePredicate = null;

					for (long classNameId : classNameIds) {
						classNamePredicate = Predicate.or(
							classNamePredicate,
							GroupTable.INSTANCE.classNameId.eq(classNameId));
					}

					if (classNamePredicate != null) {
						paramPredicate = classNamePredicate.withParentheses();
					}
				}
			}
			else if (key.equals("creatorUserId")) {
				paramPredicate = GroupTable.INSTANCE.creatorUserId.eq(
					(Long)value
				).and(
					GroupTable.INSTANCE.liveGroupId.eq(0L)
				).withParentheses();
			}
			else if (key.equals("excludedGroupIds")) {
				List<Long> excludedGroupIds = (List<Long>)value;

				if (!excludedGroupIds.isEmpty()) {
					Predicate excludePredicate = null;

					for (long excludedGroupId : excludedGroupIds) {
						excludePredicate = Predicate.and(
							excludePredicate,
							GroupTable.INSTANCE.groupId.neq(excludedGroupId));
					}

					if (excludePredicate != null) {
						paramPredicate = excludePredicate.withParentheses();
					}
				}
			}
			else if (key.equals("groupOrg")) {
				paramPredicate = Users_OrgsTable.INSTANCE.userId.eq(
					(Long)value);
			}
			else if (key.equals("groupsOrgs")) {
				paramPredicate = GroupTable.INSTANCE.liveGroupId.eq(
					0L
				).and(
					Users_OrgsTable.INSTANCE.userId.eq((Long)value)
				).withParentheses();
			}
			else if (key.equals("groupsRoles")) {
				paramPredicate = GroupTable.INSTANCE.liveGroupId.eq(
					0L
				).and(
					Groups_RolesTable.INSTANCE.roleId.eq((Long)value)
				).withParentheses();
			}
			else if (key.equals("groupsTree")) {
				List<Group> groupsTree = (List<Group>)value;

				if (!groupsTree.isEmpty()) {
					Predicate treePredicate = null;

					for (Group group : groupsTree) {
						treePredicate = Predicate.or(
							treePredicate,
							GroupTable.INSTANCE.treePath.like(
								StringBundler.concat(
									StringPool.PERCENT, StringPool.SLASH,
									group.getGroupId(), StringPool.SLASH,
									StringPool.PERCENT)));
					}

					if (treePredicate != null) {
						paramPredicate = treePredicate.withParentheses();
					}
				}
			}
			else if (key.equals("groupsUserGroups")) {
				paramPredicate = GroupTable.INSTANCE.liveGroupId.eq(
					0L
				).and(
					Users_UserGroupsTable.INSTANCE.userId.eq((Long)value)
				).withParentheses();
			}
			else if (key.equals("layout")) {
				paramPredicate = GroupTable.INSTANCE.liveGroupId.eq(
					0L
				).and(
					LayoutTable.INSTANCE.privateLayout.eq((Boolean)value)
				).withParentheses();
			}
			else if (key.equals("manualMembership")) {
				paramPredicate = GroupTable.INSTANCE.manualMembership.eq(
					(Boolean)value);
			}
			else if (key.equals("membershipRestriction")) {
				paramPredicate = GroupTable.INSTANCE.membershipRestriction.eq(
					(Integer)value);
			}
			else if (key.equals("pageCount")) {
				paramPredicate = GroupTable.INSTANCE.liveGroupId.eq(0L);
			}
			else if (key.equals("rolePermissions")) {
				RolePermissions rolePermissions = (RolePermissions)value;

				ResourceAction resourceAction =
					ResourceActionLocalServiceUtil.fetchResourceAction(
						rolePermissions.getName(),
						rolePermissions.getActionId());

				if (resourceAction != null) {
					paramPredicate = GroupTable.INSTANCE.liveGroupId.eq(
						0L
					).and(
						ResourcePermissionTable.INSTANCE.name.eq(
							rolePermissions.getName())
					).and(
						ResourcePermissionTable.INSTANCE.scope.eq(
							rolePermissions.getScope())
					).and(
						ResourcePermissionTable.INSTANCE.roleId.eq(
							rolePermissions.getRoleId())
					).and(
						DSLFunctionFactoryUtil.bitAnd(
							ResourcePermissionTable.INSTANCE.actionIds,
							resourceAction.getBitwiseValue()
						).neq(
							0L
						)
					).withParentheses();
				}
			}
			else if (key.equals("site")) {
				paramPredicate = GroupTable.INSTANCE.site.eq((Boolean)value);
			}
			else if (key.equals("type")) {
				paramPredicate = GroupTable.INSTANCE.type.eq((Integer)value);
			}
			else if (key.equals("types")) {
				List<Integer> types = (List<Integer>)value;

				if (!types.isEmpty()) {
					Predicate typesPredicate = null;

					for (Integer type : types) {
						typesPredicate = Predicate.or(
							typesPredicate, GroupTable.INSTANCE.type.eq(type));
					}

					if (typesPredicate != null) {
						paramPredicate = typesPredicate.withParentheses();
					}
				}
			}
			else if (key.equals("userGroupRole")) {
				List<Long> values = (List<Long>)value;

				paramPredicate = GroupTable.INSTANCE.liveGroupId.eq(
					0L
				).and(
					UserGroupRoleTable.INSTANCE.userId.eq(values.get(0))
				).and(
					UserGroupRoleTable.INSTANCE.roleId.eq(values.get(1))
				).withParentheses();
			}
			else if (key.equals("usersGroups")) {
				paramPredicate = GroupTable.INSTANCE.liveGroupId.eq(
					0L
				).and(
					Users_GroupsTable.INSTANCE.userId.eq((Long)value)
				).withParentheses();
			}

			if (paramPredicate == null) {
				continue;
			}

			predicate = Predicate.and(predicate, paramPredicate);
		}

		return predicate;
	}

	private Long _getUserId(Map<String, Object> params) {
		Long currentUserId = (Long)params.get("userId");

		if (Validator.isNull(currentUserId)) {
			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			currentUserId = permissionChecker.getUserId();
		}

		return currentUserId;
	}

	private Map<String, String> _getWhereMap() {
		if (_whereMap != null) {
			return _whereMap;
		}

		Map<String, String> whereMap = HashMapBuilder.put(
			"actionId", _getCondition(CustomSQLUtil.get(JOIN_BY_ACTION_ID))
		).put(
			"active", _getCondition(CustomSQLUtil.get(JOIN_BY_ACTIVE))
		).put(
			"creatorUserId",
			_getCondition(CustomSQLUtil.get(JOIN_BY_CREATOR_USER_ID))
		).put(
			"groupOrg", _getCondition(CustomSQLUtil.get(JOIN_BY_GROUP_ORG))
		).put(
			"groupsOrgs", _getCondition(CustomSQLUtil.get(JOIN_BY_GROUPS_ORGS))
		).put(
			"groupsRoles",
			_getCondition(CustomSQLUtil.get(JOIN_BY_GROUPS_ROLES))
		).put(
			"groupsUserGroups",
			_getCondition(CustomSQLUtil.get(JOIN_BY_GROUPS_USER_GROUPS))
		).put(
			"layout", _getCondition(CustomSQLUtil.get(JOIN_BY_LAYOUT))
		).put(
			"manualMembership",
			_getCondition(CustomSQLUtil.get(JOIN_BY_MANUAL_MEMBERSHIP))
		).put(
			"membershipRestriction",
			_getCondition(CustomSQLUtil.get(JOIN_BY_MEMBERSHIP_RESTRICTION))
		).put(
			"pageCount", _getCondition(CustomSQLUtil.get(JOIN_BY_PAGE_COUNT))
		).put(
			"rolePermissions_6",
			_getCondition(CustomSQLUtil.get(JOIN_BY_ROLE_RESOURCE_PERMISSIONS))
		).put(
			"site", _getCondition(CustomSQLUtil.get(JOIN_BY_SITE))
		).put(
			"type", _getCondition(CustomSQLUtil.get(JOIN_BY_TYPE))
		).put(
			"userGroupRole",
			_getCondition(CustomSQLUtil.get(JOIN_BY_USER_GROUP_ROLE))
		).put(
			"usersGroups",
			_getCondition(CustomSQLUtil.get(JOIN_BY_USERS_GROUPS))
		).build();

		_whereMap = whereMap;

		return _whereMap;
	}

	private void _populateUnionParams(
		long userId, long[] classNameIds, Map<String, Object> params1,
		Map<String, Object> params2, Map<String, Object> params3,
		Map<String, Object> params4) {

		params2.remove("usersGroups");
		params2.put("groupOrg", userId);

		params3.remove("usersGroups");
		params3.put("groupsOrgs", userId);

		params4.remove("usersGroups");
		params4.put("groupsUserGroups", userId);

		long[] groupOrganizationClassNameIds =
			_getGroupOrganizationClassNameIds();

		long groupClassNameId = groupOrganizationClassNameIds[0];
		long organizationClassNameId = groupOrganizationClassNameIds[1];

		if (classNameIds == null) {
			params1.put("classNameIds", groupOrganizationClassNameIds);
			params2.put("classNameIds", organizationClassNameId);
			params3.put("classNameIds", groupOrganizationClassNameIds);
			params4.put("classNameIds", groupOrganizationClassNameIds);
		}
		else {
			params1.put("classNameIds", classNameIds);

			if (ArrayUtil.contains(classNameIds, organizationClassNameId)) {
				params2.put("classNameIds", organizationClassNameId);

				if (ArrayUtil.contains(classNameIds, groupClassNameId)) {
					params3.put("classNameIds", groupClassNameId);
					params4.put("classNameIds", groupOrganizationClassNameIds);
				}
				else {
					params4.put("classNameIds", organizationClassNameId);
				}
			}
			else if (ArrayUtil.contains(classNameIds, groupClassNameId)) {
				params3.put("classNameIds", groupClassNameId);
				params4.put("classNameIds", groupClassNameId);
			}
		}
	}

	private String _removeWhere(String join) {
		if (Validator.isNotNull(join)) {
			int pos = join.lastIndexOf("WHERE");

			if (pos != -1) {
				join = join.substring(0, pos);
			}
		}

		return join;
	}

	private final LinkedHashMap<String, Object> _emptyLinkedHashMap =
		new LinkedHashMap<>();
	private final Map<String, String> _findByCompanyIdSQLCache =
		new ConcurrentHashMap<>();
	private volatile long[] _groupOrganizationClassNameIds;
	private volatile Map<String, String> _joinMap;
	private final Map<String, String> _replaceJoinAndWhereSQLCache =
		new ConcurrentHashMap<>();
	private volatile Map<String, String> _whereMap;

}