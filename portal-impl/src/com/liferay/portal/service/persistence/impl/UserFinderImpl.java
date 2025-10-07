/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.CustomSQLParam;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.OrganizationUtil;
import com.liferay.portal.kernel.service.persistence.RoleUtil;
import com.liferay.portal.kernel.service.persistence.UserFinder;
import com.liferay.portal.kernel.service.persistence.UserUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TableNameOrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.impl.UserImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Jon Steer
 * @author Raymond Augé
 * @author Connor McKay
 * @author Shuyang Zhou
 */
public class UserFinderImpl extends UserFinderBaseImpl implements UserFinder {

	public static final String COUNT_BY_ORGANIZATIONS_AND_USER_GROUPS =
		UserFinder.class.getName() + ".countByOrganizationsAndUserGroups";

	public static final String COUNT_BY_SOCIAL_USERS =
		UserFinder.class.getName() + ".countBySocialUsers";

	public static final String COUNT_BY_USER =
		UserFinder.class.getName() + ".countByUser";

	public static final String FIND_BY_NO_ANNOUNCEMENTS_DELIVERIES =
		UserFinder.class.getName() + ".findByNoAnnouncementsDeliveries";

	public static final String FIND_BY_NO_GROUPS =
		UserFinder.class.getName() + ".findByNoGroups";

	public static final String FIND_BY_SOCIAL_USERS =
		UserFinder.class.getName() + ".findBySocialUsers";

	public static final String FIND_BY_USERS_GROUPS =
		UserFinder.class.getName() + ".findByUsersGroups";

	public static final String FIND_BY_USERS_ORGS =
		UserFinder.class.getName() + ".findByUsersOrgs";

	public static final String FIND_BY_USERS_ORGS_GROUP =
		UserFinder.class.getName() + ".findByUsersOrgsGroup";

	public static final String FIND_BY_USERS_ORGS_GT_USER_ID =
		UserFinder.class.getName() + ".findByUsersOrgsGtUserId";

	public static final String FIND_BY_USERS_USER_GROUPS =
		UserFinder.class.getName() + ".findByUsersUserGroups";

	public static final String FIND_BY_USERS_USER_GROUPS_GT_USER_ID =
		UserFinder.class.getName() + ".findByUsersUserGroupsGtUserId";

	public static final String FIND_BY_C_FN_MN_LN_SN_EA_S =
		UserFinder.class.getName() + ".findByC_FN_MN_LN_SN_EA_S";

	public static final String JOIN_BY_CONTACT_TWITTER_SN =
		UserFinder.class.getName() + ".joinByContactTwitterSN";

	public static final String JOIN_BY_GROUPS_ORGS =
		UserFinder.class.getName() + ".joinByGroupsOrgs";

	public static final String JOIN_BY_GROUPS_USER_GROUPS =
		UserFinder.class.getName() + ".joinByGroupsUserGroups";

	public static final String JOIN_BY_NO_ACCOUNT_ENTRIES_AND_NO_ORGANIZATIONS =
		UserFinder.class.getName() +
			".joinByNoAccountEntriesAndNoOrganizations";

	public static final String JOIN_BY_NO_ORGANIZATIONS =
		UserFinder.class.getName() + ".joinByNoOrganizations";

	public static final String JOIN_BY_USER_GROUP_ROLE =
		UserFinder.class.getName() + ".joinByUserGroupRole";

	public static final String JOIN_BY_USER_GROUPS_TEAMS =
		UserFinder.class.getName() + ".joinByUserGroupsTeams";

	public static final String JOIN_BY_USERS_GROUPS =
		UserFinder.class.getName() + ".joinByUsersGroups";

	public static final String JOIN_BY_USERS_ORGS =
		UserFinder.class.getName() + ".joinByUsersOrgs";

	public static final String JOIN_BY_USERS_ORGS_TREE =
		UserFinder.class.getName() + ".joinByUsersOrgsTree";

	public static final String JOIN_BY_USERS_PASSWORD_POLICIES =
		UserFinder.class.getName() + ".joinByUsersPasswordPolicies";

	public static final String JOIN_BY_USERS_ROLES =
		UserFinder.class.getName() + ".joinByUsersRoles";

	public static final String JOIN_BY_USERS_TEAMS =
		UserFinder.class.getName() + ".joinByUsersTeams";

	public static final String JOIN_BY_USERS_USER_GROUPS =
		UserFinder.class.getName() + ".joinByUsersUserGroups";

	public static final String JOIN_BY_ANNOUNCEMENTS_DELIVERY_EMAIL_OR_SMS =
		UserFinder.class.getName() + ".joinByAnnouncementsDeliveryEmailOrSms";

	public static final String JOIN_BY_SOCIAL_MUTUAL_RELATION =
		UserFinder.class.getName() + ".joinBySocialMutualRelation";

	public static final String JOIN_BY_SOCIAL_MUTUAL_RELATION_TYPE =
		UserFinder.class.getName() + ".joinBySocialMutualRelationType";

	public static final String JOIN_BY_SOCIAL_RELATION =
		UserFinder.class.getName() + ".joinBySocialRelation";

	public static final String JOIN_BY_SOCIAL_RELATION_TYPE =
		UserFinder.class.getName() + ".joinBySocialRelationType";

	@Override
	public int countByKeywords(
		long companyId, String keywords, int status,
		LinkedHashMap<String, Object> params) {

		String[] firstNames = null;
		String[] middleNames = null;
		String[] lastNames = null;
		String[] screenNames = null;
		String[] emailAddresses = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			firstNames = CustomSQLUtil.keywords(keywords);
			middleNames = CustomSQLUtil.keywords(keywords);
			lastNames = CustomSQLUtil.keywords(keywords);
			screenNames = CustomSQLUtil.keywords(keywords);
			emailAddresses = CustomSQLUtil.keywords(keywords);
		}
		else {
			andOperator = true;
		}

		return countByC_FN_MN_LN_SN_EA_S(
			companyId, firstNames, middleNames, lastNames, screenNames,
			emailAddresses, status, params, andOperator);
	}

	@Override
	public int countByOrganizationsAndUserGroups(
		long[] organizationIds, long[] userGroupIds) {

		if (ArrayUtil.isEmpty(organizationIds) &&
			ArrayUtil.isEmpty(userGroupIds)) {

			return 0;
		}

		if (ArrayUtil.isEmpty(organizationIds)) {
			organizationIds = new long[] {0L};
		}

		if (ArrayUtil.isEmpty(userGroupIds)) {
			userGroupIds = new long[] {0L};
		}

		Long count = null;

		Session session = openSession();

		try {
			String sql = CustomSQLUtil.get(
				COUNT_BY_ORGANIZATIONS_AND_USER_GROUPS);

			sql = StringUtil.replace(
				sql, new String[] {"[$ORGANIZATION_ID$]", "[$USER_GROUP_ID$]"},
				new String[] {
					StringUtil.merge(organizationIds),
					StringUtil.merge(userGroupIds)
				});

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			count = (Long)sqlQuery.uniqueResult();
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			if (count == null) {
				count = Long.valueOf(0);
			}

			closeSession(session);
		}

		return count.intValue();
	}

	@Override
	public int countBySocialUsers(
		long companyId, long userId, int socialRelationType,
		String socialRelationTypeComparator, int status) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_SOCIAL_USERS);

			if (socialRelationTypeComparator.equals(StringPool.EQUAL)) {
				sql = StringUtil.replace(
					sql, "[$SOCIAL_RELATION_TYPE_COMPARATOR$]",
					StringPool.EQUAL);
			}
			else {
				sql = StringUtil.replace(
					sql, "[$SOCIAL_RELATION_TYPE_COMPARATOR$]",
					StringPool.NOT_EQUAL);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(userId);
			queryPos.add(socialRelationType);
			queryPos.add(companyId);
			queryPos.add(status);

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
	public int countByUser(long userId, LinkedHashMap<String, Object> params) {
		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_USER);

			sql = replaceJoinAndWhere(sql, params);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			setJoin(queryPos, params);

			queryPos.add(userId);

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
	public int countByC_FN_MN_LN_SN_EA_S(
		long companyId, String firstName, String middleName, String lastName,
		String screenName, String emailAddress, int status,
		LinkedHashMap<String, Object> params, boolean andOperator) {

		String[] firstNames = null;
		String[] middleNames = null;
		String[] lastNames = null;
		String[] screenNames = null;
		String[] emailAddresses = null;

		if (Validator.isNotNull(firstName) || Validator.isNotNull(middleName) ||
			Validator.isNotNull(lastName) || Validator.isNotNull(screenName) ||
			Validator.isNotNull(emailAddress)) {

			firstNames = CustomSQLUtil.keywords(firstName);
			middleNames = CustomSQLUtil.keywords(middleName);
			lastNames = CustomSQLUtil.keywords(lastName);
			screenNames = CustomSQLUtil.keywords(screenName);
			emailAddresses = CustomSQLUtil.keywords(emailAddress);
		}
		else {
			andOperator = true;
		}

		return countByC_FN_MN_LN_SN_EA_S(
			companyId, firstNames, middleNames, lastNames, screenNames,
			emailAddresses, status, params, andOperator);
	}

	@Override
	public int countByC_FN_MN_LN_SN_EA_S(
		long companyId, String[] firstNames, String[] middleNames,
		String[] lastNames, String[] screenNames, String[] emailAddresses,
		int status, LinkedHashMap<String, Object> params, boolean andOperator) {

		firstNames = CustomSQLUtil.keywords(firstNames);
		middleNames = CustomSQLUtil.keywords(middleNames);
		lastNames = CustomSQLUtil.keywords(lastNames);
		screenNames = CustomSQLUtil.keywords(screenNames);
		emailAddresses = CustomSQLUtil.keywords(emailAddresses);

		List<LinkedHashMap<String, Object>> paramsList = getParamsList(params);

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_C_FN_MN_LN_SN_EA_S);

			sql = StringUtil.replace(
				sql, "[$COLUMN_NAMES$]", getColumnNames(null));

			sql = replaceKeywords(
				sql, firstNames, middleNames, lastNames, screenNames,
				emailAddresses);

			if (status == WorkflowConstants.STATUS_ANY) {
				sql = StringUtil.removeSubstring(sql, _STATUS_SQL);
			}

			StringBundler sb = new StringBundler((paramsList.size() * 4) + 1);

			sb.append("SELECT COUNT(userId) AS COUNT_VALUE FROM (");

			for (int i = 0; i < paramsList.size(); i++) {
				if (i != 0) {
					sb.append(" UNION ");
				}

				sb.append(StringPool.OPEN_PARENTHESIS);
				sb.append(replaceJoinAndWhere(sql, paramsList.get(i)));
				sb.append(StringPool.CLOSE_PARENTHESIS);
			}

			sb.append(") userId");

			sql = sb.toString();

			sql = CustomSQLUtil.replaceAndOperator(sql, andOperator);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			for (LinkedHashMap<String, Object> paramsMap : paramsList) {
				setJoin(queryPos, paramsMap);

				queryPos.add(companyId);
				queryPos.add(firstNames, 2);
				queryPos.add(middleNames, 2);
				queryPos.add(lastNames, 2);
				queryPos.add(screenNames, 2);
				queryPos.add(emailAddresses, 2);

				if (status != WorkflowConstants.STATUS_ANY) {
					queryPos.add(status);
				}
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
	public List<User> findByKeywords(
		long companyId, String keywords, int status,
		LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<User> orderByComparator) {

		String[] firstNames = null;
		String[] middleNames = null;
		String[] lastNames = null;
		String[] screenNames = null;
		String[] emailAddresses = null;
		boolean andOperator = false;

		if (params == null) {
			params = _emptyLinkedHashMap;
		}

		if (Validator.isNotNull(keywords)) {
			WildcardMode wildcardMode = (WildcardMode)GetterUtil.getObject(
				params.get("wildcardMode"), WildcardMode.SURROUND);

			firstNames = CustomSQLUtil.keywords(keywords, wildcardMode);
			middleNames = CustomSQLUtil.keywords(keywords, wildcardMode);
			lastNames = CustomSQLUtil.keywords(keywords, wildcardMode);
			screenNames = CustomSQLUtil.keywords(keywords, wildcardMode);
			emailAddresses = CustomSQLUtil.keywords(keywords, wildcardMode);
		}
		else {
			andOperator = true;
		}

		return findByC_FN_MN_LN_SN_EA_S(
			companyId, firstNames, middleNames, lastNames, screenNames,
			emailAddresses, status, params, andOperator, start, end,
			orderByComparator);
	}

	@Override
	public List<User> findByNoAnnouncementsDeliveries(String type) {
		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_NO_ANNOUNCEMENTS_DELIVERIES);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("User_", UserImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(type);

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
	public List<User> findByNoGroups() {
		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_NO_GROUPS);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("User_", UserImpl.class);

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
	public List<User> findBySocialUsers(
		long companyId, long userId, int socialRelationType,
		String socialRelationTypeComparator, int status, int start, int end,
		OrderByComparator<User> orderByComparator) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_SOCIAL_USERS);

			if (socialRelationTypeComparator.equals(StringPool.EQUAL)) {
				sql = StringUtil.replace(
					sql, "[$SOCIAL_RELATION_TYPE_COMPARATOR$]",
					StringPool.EQUAL);
			}
			else {
				sql = StringUtil.replace(
					sql, "[$SOCIAL_RELATION_TYPE_COMPARATOR$]",
					StringPool.NOT_EQUAL);
			}

			if (orderByComparator != null) {
				sql = CustomSQLUtil.replaceOrderBy(
					sql,
					new TableNameOrderByComparator<>(
						orderByComparator, "User_"));
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("User_", UserImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(userId);
			queryPos.add(socialRelationType);
			queryPos.add(companyId);
			queryPos.add(status);

			return (List<User>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<User> findByUsersOrgsGtUserId(
		long companyId, long organizationId, long gtUserId, int size) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_USERS_ORGS_GT_USER_ID);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("User_", UserImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(organizationId);
			queryPos.add(companyId);
			queryPos.add(gtUserId);

			return (List<User>)QueryUtil.list(sqlQuery, getDialect(), 0, size);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<User> findByUsersUserGroupsGtUserId(
		long companyId, long userGroupId, long gtUserId, int size) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(
				FIND_BY_USERS_USER_GROUPS_GT_USER_ID);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("User_", UserImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(userGroupId);
			queryPos.add(companyId);
			queryPos.add(gtUserId);

			return (List<User>)QueryUtil.list(sqlQuery, getDialect(), 0, size);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<User> findByC_FN_MN_LN_SN_EA_S(
		long companyId, String firstName, String middleName, String lastName,
		String screenName, String emailAddress, int status,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end, OrderByComparator<User> orderByComparator) {

		String[] firstNames = null;
		String[] middleNames = null;
		String[] lastNames = null;
		String[] screenNames = null;
		String[] emailAddresses = null;

		if (Validator.isNotNull(firstName) || Validator.isNotNull(middleName) ||
			Validator.isNotNull(lastName) || Validator.isNotNull(screenName) ||
			Validator.isNotNull(emailAddress)) {

			firstNames = CustomSQLUtil.keywords(firstName);
			middleNames = CustomSQLUtil.keywords(middleName);
			lastNames = CustomSQLUtil.keywords(lastName);
			screenNames = CustomSQLUtil.keywords(screenName);
			emailAddresses = CustomSQLUtil.keywords(emailAddress);
		}
		else {
			andOperator = true;
		}

		return findByC_FN_MN_LN_SN_EA_S(
			companyId, firstNames, middleNames, lastNames, screenNames,
			emailAddresses, status, params, andOperator, start, end,
			orderByComparator);
	}

	@Override
	public List<User> findByC_FN_MN_LN_SN_EA_S(
		long companyId, String[] firstNames, String[] middleNames,
		String[] lastNames, String[] screenNames, String[] emailAddresses,
		int status, LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end, OrderByComparator<User> orderByComparator) {

		try {
			List<Long> userIds = doFindByC_FN_MN_LN_SN_EA_S(
				companyId, firstNames, middleNames, lastNames, screenNames,
				emailAddresses, status, params, andOperator, start, end,
				orderByComparator);

			List<User> users = new ArrayList<>(userIds.size());

			for (Long userId : userIds) {
				User user = UserUtil.findByPrimaryKey(userId);

				users.add(user);
			}

			return users;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	protected List<Long> doFindByC_FN_MN_LN_SN_EA_S(
		long companyId, String[] firstNames, String[] middleNames,
		String[] lastNames, String[] screenNames, String[] emailAddresses,
		int status, LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end, OrderByComparator<User> orderByComparator) {

		firstNames = CustomSQLUtil.keywords(firstNames);
		middleNames = CustomSQLUtil.keywords(middleNames);
		lastNames = CustomSQLUtil.keywords(lastNames);
		screenNames = CustomSQLUtil.keywords(screenNames);
		emailAddresses = CustomSQLUtil.keywords(emailAddresses);

		List<LinkedHashMap<String, Object>> paramsList = getParamsList(params);

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_C_FN_MN_LN_SN_EA_S);

			sql = StringUtil.replace(
				sql, "[$COLUMN_NAMES$]", getColumnNames(orderByComparator));

			sql = replaceKeywords(
				sql, firstNames, middleNames, lastNames, screenNames,
				emailAddresses);

			if (status == WorkflowConstants.STATUS_ANY) {
				sql = StringUtil.removeSubstring(sql, _STATUS_SQL);
			}

			int initialCapacity = (paramsList.size() * 3) - 2;

			if (orderByComparator != null) {
				initialCapacity += 2;
			}

			if (initialCapacity > 0) {
				StringBundler sb = new StringBundler(initialCapacity);

				for (int i = 0; i < paramsList.size(); i++) {
					if (i == 0) {
						sb.append(replaceJoinAndWhere(sql, paramsList.get(i)));
					}
					else {
						sb.append(" UNION (");
						sb.append(replaceJoinAndWhere(sql, paramsList.get(i)));
						sb.append(StringPool.CLOSE_PARENTHESIS);
					}
				}

				if (orderByComparator != null) {
					sb.append(" ORDER BY ");
					sb.append(orderByComparator.toString());
				}

				sql = sb.toString();
			}

			sql = CustomSQLUtil.replaceAndOperator(sql, andOperator);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("userId", Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			for (LinkedHashMap<String, Object> paramsMap : paramsList) {
				setJoin(queryPos, paramsMap);

				queryPos.add(companyId);
				queryPos.add(firstNames, 2);
				queryPos.add(middleNames, 2);
				queryPos.add(lastNames, 2);
				queryPos.add(screenNames, 2);
				queryPos.add(emailAddresses, 2);

				if (status != WorkflowConstants.STATUS_ANY) {
					queryPos.add(status);
				}
			}

			return (List<Long>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected String getColumnNames(OrderByComparator<User> orderByComparator) {
		if (orderByComparator == null) {
			return "DISTINCT User_.userId AS userId";
		}

		String[] orderByFields = orderByComparator.getOrderByFields();

		StringBundler sb = new StringBundler((orderByFields.length * 4) + 1);

		sb.append("DISTINCT User_.userId AS userId");

		for (String field : orderByFields) {
			sb.append(", User_.");
			sb.append(field);
			sb.append(" AS ");
			sb.append(field);
		}

		return sb.toString();
	}

	protected String getJoin(LinkedHashMap<String, Object> params) {
		if ((params == null) || params.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(params.size());

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();

			if (key.equals("expandoAttributes") || key.equals("noLDAPUsers")) {
				continue;
			}

			Object value = entry.getValue();

			if (value != null) {
				sb.append(getJoin(key, value));
			}
		}

		return sb.toString();
	}

	protected String getJoin(String key, Object value) {
		String join = StringPool.BLANK;

		if (key.equals("contactTwitterSn")) {
			join = CustomSQLUtil.get(JOIN_BY_CONTACT_TWITTER_SN);
		}
		else if (key.equals("groupsOrgs")) {
			join = CustomSQLUtil.get(JOIN_BY_GROUPS_ORGS);
		}
		else if (key.equals("groupsUserGroups")) {
			join = CustomSQLUtil.get(JOIN_BY_GROUPS_USER_GROUPS);
		}
		else if (key.equals("noAccountEntriesAndNoOrganizations")) {
			join = CustomSQLUtil.get(
				JOIN_BY_NO_ACCOUNT_ENTRIES_AND_NO_ORGANIZATIONS);
		}
		else if (key.equals("noOrganizations")) {
			join = CustomSQLUtil.get(JOIN_BY_NO_ORGANIZATIONS);
		}
		else if (key.equals("userGroupRole")) {
			join = CustomSQLUtil.get(JOIN_BY_USER_GROUP_ROLE);
		}
		else if (key.equals("userGroupsTeams")) {
			join = CustomSQLUtil.get(JOIN_BY_USER_GROUPS_TEAMS);
		}
		else if (key.equals("usersGroups")) {
			join = CustomSQLUtil.get(JOIN_BY_USERS_GROUPS);
		}
		else if (key.equals("usersOrgs")) {
			join = CustomSQLUtil.get(JOIN_BY_USERS_ORGS);
		}
		else if (key.equals("usersOrgsTree")) {
			join = CustomSQLUtil.get(JOIN_BY_USERS_ORGS_TREE);
		}
		else if (key.equals("usersPasswordPolicies")) {
			join = CustomSQLUtil.get(JOIN_BY_USERS_PASSWORD_POLICIES);
		}
		else if (key.equals("usersRoles")) {
			join = CustomSQLUtil.get(JOIN_BY_USERS_ROLES);
		}
		else if (key.equals("usersTeams")) {
			join = CustomSQLUtil.get(JOIN_BY_USERS_TEAMS);
		}
		else if (key.equals("usersUserGroups")) {
			join = CustomSQLUtil.get(JOIN_BY_USERS_USER_GROUPS);
		}
		else if (key.equals("announcementsDeliveryEmailOrSms")) {
			join = CustomSQLUtil.get(
				JOIN_BY_ANNOUNCEMENTS_DELIVERY_EMAIL_OR_SMS);
		}
		else if (key.equals("socialMutualRelation")) {
			join = CustomSQLUtil.get(JOIN_BY_SOCIAL_MUTUAL_RELATION);
		}
		else if (key.equals("socialMutualRelationType")) {
			join = CustomSQLUtil.get(JOIN_BY_SOCIAL_MUTUAL_RELATION_TYPE);
		}
		else if (key.equals("socialRelation")) {
			join = CustomSQLUtil.get(JOIN_BY_SOCIAL_RELATION);
		}
		else if (key.equals("socialRelationType")) {
			join = CustomSQLUtil.get(JOIN_BY_SOCIAL_RELATION_TYPE);
		}
		else if (value instanceof CustomSQLParam) {
			CustomSQLParam customSQLParam = (CustomSQLParam)value;

			join = customSQLParam.getSQL();
		}

		if (Validator.isNotNull(join)) {
			int pos = join.indexOf("WHERE");

			if (pos != -1) {
				join = join.substring(0, pos);
			}
		}

		return join;
	}

	protected List<LinkedHashMap<String, Object>> getParamsList(
		LinkedHashMap<String, Object> params) {

		List<LinkedHashMap<String, Object>> paramsList = new ArrayList<>();

		if (params == null) {
			params = _emptyLinkedHashMap;
		}

		params.remove(Field.GROUP_ID);

		LinkedHashMap<String, Object> params1 = params;

		LinkedHashMap<String, Object> params2 = null;

		LinkedHashMap<String, Object> params3 = null;

		LinkedHashMap<String, Object> params4 = null;

		LinkedHashMap<String, Object> params5 = null;

		LinkedHashMap<String, Object> params6 = null;

		LinkedHashMap<String, Object> params7 = null;

		Long[] groupIds = null;

		if (params.get("usersGroups") instanceof Long) {
			Long groupId = (Long)params.get("usersGroups");

			if (groupId > 0) {
				groupIds = new Long[] {groupId};
			}
		}
		else {
			groupIds = (Long[])params.get("usersGroups");
		}

		Long[] roleIds = null;

		if (params.get("usersRoles") instanceof Long) {
			Long roleId = (Long)params.get("usersRoles");

			if (roleId > 0) {
				roleIds = new Long[] {roleId};
			}
		}
		else {
			roleIds = (Long[])params.get("usersRoles");
		}

		Long[] teamIds = null;

		if (params.get("usersTeams") instanceof Long) {
			Long teamId = (Long)params.get("usersTeams");

			if (teamId > 0) {
				teamIds = new Long[] {teamId};
			}
		}
		else {
			teamIds = (Long[])params.get("usersTeams");
		}

		boolean inherit = GetterUtil.getBoolean(params.get("inherit"));
		boolean socialRelationTypeUnionUserGroups = GetterUtil.getBoolean(
			params.get("socialRelationTypeUnionUserGroups"));

		if (ArrayUtil.isNotEmpty(groupIds) && inherit &&
			!socialRelationTypeUnionUserGroups) {

			List<Long> organizationIds = new ArrayList<>();
			List<Long> siteGroupIds = new ArrayList<>();
			List<Long> userGroupIds = new ArrayList<>();

			for (long groupId : groupIds) {
				Group group = GroupLocalServiceUtil.fetchGroup(groupId);

				if (group == null) {
					continue;
				}

				if (group.isOrganization()) {
					organizationIds.add(group.getOrganizationId());
				}
				else if (group.isUserGroup()) {
					userGroupIds.add(group.getClassPK());
				}

				if (group.isSite()) {
					siteGroupIds.add(groupId);
				}
			}

			if (!organizationIds.isEmpty()) {
				params2 = new LinkedHashMap<>(params1);

				params2.remove("usersGroups");

				if (PropsValues.ORGANIZATIONS_MEMBERSHIP_STRICT) {
					params2.put(
						"usersOrgs", organizationIds.toArray(new Long[0]));
				}
				else {
					Map<Serializable, Organization> organizations =
						OrganizationUtil.fetchByPrimaryKeys(
							new HashSet<Serializable>(organizationIds));

					params2.put(
						"usersOrgsTree",
						new ArrayList<Organization>(organizations.values()));
				}
			}

			if (!siteGroupIds.isEmpty()) {
				Long[] siteGroupIdsArray = siteGroupIds.toArray(new Long[0]);

				params3 = new LinkedHashMap<>(params1);

				params3.remove("usersGroups");

				params3.put("groupsOrgs", siteGroupIdsArray);

				params4 = new LinkedHashMap<>(params1);

				params4.remove("usersGroups");

				params4.put("groupsUserGroups", siteGroupIdsArray);
			}

			if (!userGroupIds.isEmpty()) {
				params5 = new LinkedHashMap<>(params1);

				params5.remove("usersGroups");

				params5.put(
					"usersUserGroups", userGroupIds.toArray(new Long[0]));
			}
		}

		if (ArrayUtil.isNotEmpty(roleIds) && inherit &&
			!socialRelationTypeUnionUserGroups) {

			List<Long> organizationIds = new ArrayList<>();
			List<Long> siteGroupIds = new ArrayList<>();
			List<Long> userGroupIds = new ArrayList<>();

			for (long roleId : roleIds) {
				List<Group> groups = RoleUtil.getGroups(roleId);

				for (Group group : groups) {
					if (group.isOrganization()) {
						organizationIds.add(group.getOrganizationId());
					}
					else if (group.isUserGroup()) {
						userGroupIds.add(group.getClassPK());
					}
					else {
						siteGroupIds.add(group.getGroupId());
					}
				}
			}

			if (!organizationIds.isEmpty()) {
				params2 = new LinkedHashMap<>(params1);

				params2.remove("usersRoles");

				if (PropsValues.ORGANIZATIONS_MEMBERSHIP_STRICT) {
					params2.put(
						"usersOrgs", organizationIds.toArray(new Long[0]));
				}
				else {
					Map<Serializable, Organization> organizations =
						OrganizationUtil.fetchByPrimaryKeys(
							new HashSet<Serializable>(organizationIds));

					params2.put(
						"usersOrgsTree",
						new ArrayList<Organization>(organizations.values()));
				}
			}

			if (!siteGroupIds.isEmpty()) {
				Long[] siteGroupIdsArray = siteGroupIds.toArray(new Long[0]);

				params3 = new LinkedHashMap<>(params1);

				params3.remove("usersRoles");

				params3.put("usersGroups", siteGroupIdsArray);

				params4 = new LinkedHashMap<>(params1);

				params4.remove("usersRoles");

				params4.put("groupsOrgs", siteGroupIdsArray);

				params5 = new LinkedHashMap<>(params1);

				params5.remove("usersRoles");

				params5.put("groupsUserGroups", siteGroupIdsArray);
			}

			if (!userGroupIds.isEmpty()) {
				params6 = new LinkedHashMap<>(params1);

				params6.remove("usersRoles");

				params6.put(
					"usersUserGroups", userGroupIds.toArray(new Long[0]));
			}
		}

		if (ArrayUtil.isNotEmpty(teamIds) && inherit &&
			!socialRelationTypeUnionUserGroups) {

			params7 = new LinkedHashMap<>(params1);

			params7.remove("usersTeams");

			params7.put("userGroupsTeams", teamIds);
		}

		if (socialRelationTypeUnionUserGroups) {
			boolean hasSocialRelationTypes = Validator.isNotNull(
				params.get("socialRelationType"));

			if (hasSocialRelationTypes && ArrayUtil.isNotEmpty(groupIds)) {
				params2 = new LinkedHashMap<>(params1);

				params1.remove("socialRelationType");

				params2.remove("usersGroups");

				params3 = new LinkedHashMap<>(params1);

				params3.remove("socialRelationType");
				params3.remove("usersGroups");

				params3.put("groupsUserGroups", groupIds);
			}
		}

		paramsList.add(params1);

		if (params2 != null) {
			paramsList.add(params2);
		}

		if (params3 != null) {
			paramsList.add(params3);
		}

		if (params4 != null) {
			paramsList.add(params4);
		}

		if (params5 != null) {
			paramsList.add(params5);
		}

		if (params6 != null) {
			paramsList.add(params6);
		}

		if (params7 != null) {
			paramsList.add(params7);
		}

		return paramsList;
	}

	protected String getWhere(LinkedHashMap<String, Object> params) {
		if ((params == null) || params.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(params.size());

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();

			if (key.equals("expandoAttributes")) {
				continue;
			}

			Object value = entry.getValue();

			if (value != null) {
				sb.append(getWhere(key, value));
			}
		}

		return sb.toString();
	}

	protected String getWhere(String key, Object value) {
		String join = StringPool.BLANK;

		if (key.equals("contactTwitterSn")) {
			join = CustomSQLUtil.get(JOIN_BY_CONTACT_TWITTER_SN);
		}
		else if (key.equals("groupsOrgs")) {
			Long[] groupIds = (Long[])value;

			join = CustomSQLUtil.get(JOIN_BY_GROUPS_ORGS);

			if (groupIds.length > 1) {
				StringBundler sb = new StringBundler((groupIds.length * 2) + 1);

				sb.append("Groups_Orgs.groupId IN (");

				for (long groupId : groupIds) {
					sb.append(groupId);
					sb.append(StringPool.COMMA);
				}

				sb.setIndex(sb.index() - 1);

				sb.append(StringPool.CLOSE_PARENTHESIS);

				join = StringUtil.replace(
					join, "Groups_Orgs.groupId = ?", sb.toString());
			}
			else {
				join = StringUtil.replace(
					join, "Groups_Orgs.groupId = ?",
					"Groups_Orgs.groupId = " + groupIds[0]);
			}
		}
		else if (key.equals("groupsUserGroups")) {
			Long[] groupIds = (Long[])value;

			join = CustomSQLUtil.get(JOIN_BY_GROUPS_USER_GROUPS);

			if (groupIds.length > 1) {
				StringBundler sb = new StringBundler((groupIds.length * 2) + 1);

				sb.append("Groups_UserGroups.groupId IN (");

				for (long groupId : groupIds) {
					sb.append(groupId);
					sb.append(StringPool.COMMA);
				}

				sb.setIndex(sb.index() - 1);

				sb.append(StringPool.CLOSE_PARENTHESIS);

				join = StringUtil.replace(
					join, "Groups_UserGroups.groupId = ?", sb.toString());
			}
			else {
				join = StringUtil.replace(
					join, "Groups_UserGroups.groupId = ?",
					"Groups_UserGroups.groupId = " + groupIds[0]);
			}
		}
		else if (key.equals("noAccountEntriesAndNoOrganizations")) {
			join = CustomSQLUtil.get(
				JOIN_BY_NO_ACCOUNT_ENTRIES_AND_NO_ORGANIZATIONS);
		}
		else if (key.equals("noLDAPUsers")) {
			join = "WHERE User_.ldapServerId = -1";
		}
		else if (key.equals("noOrganizations")) {
			join = CustomSQLUtil.get(JOIN_BY_NO_ORGANIZATIONS);
		}
		else if (key.equals("userGroupRole")) {
			join = CustomSQLUtil.get(JOIN_BY_USER_GROUP_ROLE);

			Long[] valueArray = (Long[])value;

			Long groupId = valueArray[0];

			if (Validator.isNull(groupId)) {
				join = StringUtil.removeSubstring(
					join, "(UserGroupRole.groupId = ?) AND");
			}
		}
		else if (key.equals("userGroupsTeams")) {
			Long[] teamIds = (Long[])value;

			join = CustomSQLUtil.get(JOIN_BY_USER_GROUPS_TEAMS);

			if (teamIds.length > 1) {
				StringBundler sb = new StringBundler((teamIds.length * 2) + 1);

				sb.append("UserGroups_Teams.teamId IN (");

				for (long teamId : teamIds) {
					sb.append(teamId);
					sb.append(StringPool.COMMA);
				}

				sb.setIndex(sb.index() - 1);

				sb.append(StringPool.CLOSE_PARENTHESIS);

				join = StringUtil.replace(
					join, "UserGroups_Teams.teamId = ?", sb.toString());
			}
			else {
				join = StringUtil.replace(
					join, "UserGroups_Teams.teamId = ?",
					"UserGroups_Teams.teamId = " + teamIds[0]);
			}
		}
		else if (key.equals("usersGroups")) {
			if (value instanceof Long) {
				join = CustomSQLUtil.get(JOIN_BY_USERS_GROUPS);
			}
			else if (value instanceof Long[]) {
				Long[] groupIds = (Long[])value;

				if (groupIds.length > 1) {
					StringBundler sb = new StringBundler(
						(groupIds.length * 2) + 1);

					sb.append("WHERE (Users_Groups.groupId IN (");

					for (long groupId : groupIds) {
						sb.append(groupId);
						sb.append(StringPool.COMMA);
					}

					sb.setIndex(sb.index() - 1);

					sb.append("))");

					join = sb.toString();
				}
				else {
					join = "WHERE (Users_Groups.groupId = " + groupIds[0] + ")";
				}
			}
		}
		else if (key.equals("usersOrgs")) {
			if (value instanceof Long) {
				join = CustomSQLUtil.get(JOIN_BY_USERS_ORGS);
			}
			else if (value instanceof Long[]) {
				Long[] organizationIds = (Long[])value;

				if (organizationIds.length > 1) {
					StringBundler sb = new StringBundler(
						(organizationIds.length * 2) + 1);

					sb.append("WHERE (Users_Orgs.organizationId IN (");

					for (long organizationId : organizationIds) {
						sb.append(organizationId);
						sb.append(StringPool.COMMA);
					}

					sb.setIndex(sb.index() - 1);

					sb.append("))");

					join = sb.toString();
				}
				else {
					join =
						"WHERE (Users_Orgs.organizationId = " +
							organizationIds[0] + ")";
				}
			}
		}
		else if (key.equals("usersOrgsTree")) {
			List<Organization> organizationsTree = (List<Organization>)value;

			int size = organizationsTree.size();

			if (size > 0) {
				StringBundler sb = new StringBundler((size * 4) + 1);

				sb.append("WHERE (");

				for (Organization organization : organizationsTree) {
					sb.append("(Organization_.treePath LIKE '%/");
					sb.append(organization.getOrganizationId());
					sb.append("/%') ");
					sb.append("OR ");
				}

				sb.setIndex(sb.index() - 1);

				sb.append(StringPool.CLOSE_PARENTHESIS);

				join = sb.toString();
			}
			else {
				join = "WHERE (Organization_.treePath LIKE '%/ /%')";
			}
		}
		else if (key.equals("usersPasswordPolicies")) {
			join = CustomSQLUtil.get(JOIN_BY_USERS_PASSWORD_POLICIES);
		}
		else if (key.equals("usersRoles")) {
			join = CustomSQLUtil.get(JOIN_BY_USERS_ROLES);
		}
		else if (key.equals("usersTeams")) {
			join = CustomSQLUtil.get(JOIN_BY_USERS_TEAMS);
		}
		else if (key.equals("usersUserGroups")) {
			if (value instanceof Long) {
				join = CustomSQLUtil.get(JOIN_BY_USERS_USER_GROUPS);
			}
			else if (value instanceof Long[]) {
				Long[] userGroupIds = (Long[])value;

				if (userGroupIds.length > 1) {
					StringBundler sb = new StringBundler(
						(userGroupIds.length * 2) + 1);

					sb.append("WHERE (Users_UserGroups.userGroupId IN (");

					for (long userGroupId : userGroupIds) {
						sb.append(userGroupId);
						sb.append(StringPool.COMMA);
					}

					sb.setIndex(sb.index() - 1);

					sb.append("))");

					join = sb.toString();
				}
				else {
					join =
						"WHERE (Users_UserGroups.userGroupId = " +
							userGroupIds[0] + ")";
				}
			}
		}
		else if (key.equals("announcementsDeliveryEmailOrSms")) {
			join = CustomSQLUtil.get(
				JOIN_BY_ANNOUNCEMENTS_DELIVERY_EMAIL_OR_SMS);
		}
		else if (key.equals("socialMutualRelation")) {
			join = CustomSQLUtil.get(JOIN_BY_SOCIAL_MUTUAL_RELATION);
		}
		else if (key.equals("socialMutualRelationType")) {
			join = CustomSQLUtil.get(JOIN_BY_SOCIAL_MUTUAL_RELATION_TYPE);
		}
		else if (key.equals("socialRelation")) {
			join = CustomSQLUtil.get(JOIN_BY_SOCIAL_RELATION);
		}
		else if (key.equals("socialRelationType")) {
			if (value instanceof Long[]) {
				join = CustomSQLUtil.get(JOIN_BY_SOCIAL_RELATION_TYPE);
			}
			else if (value instanceof Long[][]) {
				StringBundler sb = new StringBundler();

				sb.append("WHERE (SocialRelation.userId1 = ?) AND ");
				sb.append("(SocialRelation.type_ IN (");

				Long[][] valueDoubleArray = (Long[][])value;

				Long[] socialRelationTypes = valueDoubleArray[1];

				for (int i = 0; i < socialRelationTypes.length; i++) {
					sb.append(StringPool.QUESTION);

					if ((i + 1) < socialRelationTypes.length) {
						sb.append(StringPool.COMMA);
					}
				}

				sb.append("))");

				join = sb.toString();
			}
		}
		else if (value instanceof CustomSQLParam) {
			CustomSQLParam customSQLParam = (CustomSQLParam)value;

			join = customSQLParam.getSQL();
		}

		if (Validator.isNotNull(join)) {
			int pos = join.indexOf("WHERE");

			if (pos != -1) {
				join = join.substring(pos + 5);

				join = join.concat(" AND ");
			}
			else {
				join = StringPool.BLANK;
			}
		}

		return join;
	}

	protected String replaceJoinAndWhere(
		String sql, LinkedHashMap<String, Object> params) {

		sql = StringUtil.replace(sql, "[$JOIN$]", getJoin(params));
		sql = StringUtil.replace(sql, "[$WHERE$]", getWhere(params));

		return sql;
	}

	protected String replaceKeywords(
		String sql, String[] firstNames, String[] middleNames,
		String[] lastNames, String[] screenNames, String[] emailAddresses) {

		sql = CustomSQLUtil.replaceKeywords(
			sql, "LOWER(User_.firstName)", StringPool.LIKE, false, firstNames);
		sql = CustomSQLUtil.replaceKeywords(
			sql, "LOWER(User_.middleName)", StringPool.LIKE, false,
			middleNames);
		sql = CustomSQLUtil.replaceKeywords(
			sql, "LOWER(User_.lastName)", StringPool.LIKE, false, lastNames);
		sql = CustomSQLUtil.replaceKeywords(
			sql, "LOWER(User_.screenName)", StringPool.LIKE, false,
			screenNames);
		sql = CustomSQLUtil.replaceKeywords(
			sql, "LOWER(User_.emailAddress)", StringPool.LIKE, true,
			emailAddresses);

		return sql;
	}

	protected void setJoin(
		QueryPos queryPos, LinkedHashMap<String, Object> params) {

		if (params == null) {
			return;
		}

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();

			if (key.equals("expandoAttributes")) {
				continue;
			}

			Object value = entry.getValue();

			if (value instanceof Long) {
				Long valueLong = (Long)value;

				if (Validator.isNotNull(valueLong)) {
					queryPos.add(valueLong);
				}
			}
			else if (value instanceof Long[]) {
				if (key.equals("groupsOrgs") ||
					key.equals("groupsUserGroups") ||
					key.equals("userGroupsTeams") ||
					key.equals("usersGroups") || key.equals("usersOrgs") ||
					key.equals("usersUserGroups")) {

					continue;
				}

				Long[] valueArray = (Long[])value;

				for (Long element : valueArray) {
					if (Validator.isNotNull(element)) {
						queryPos.add(element);
					}
				}
			}
			else if (value instanceof Long[][]) {
				Long[][] valueDoubleArray = (Long[][])value;

				for (Long[] valueArray : valueDoubleArray) {
					for (Long valueLong : valueArray) {
						queryPos.add(valueLong);
					}
				}
			}
			else if (value instanceof String) {
				String valueString = (String)value;

				if (Validator.isNotNull(valueString)) {
					queryPos.add(valueString);
				}
			}
			else if (value instanceof String[]) {
				String[] valueArray = (String[])value;

				for (String element : valueArray) {
					if (Validator.isNotNull(element)) {
						queryPos.add(element);
					}
				}
			}
			else if (value instanceof CustomSQLParam) {
				CustomSQLParam customSQLParam = (CustomSQLParam)value;

				customSQLParam.process(queryPos);
			}
		}
	}

	private static final String _STATUS_SQL = "AND (User_.status = ?)";

	private final LinkedHashMap<String, Object> _emptyLinkedHashMap =
		new LinkedHashMap<>();

}