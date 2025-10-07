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
import com.liferay.petra.sql.dsl.query.OrderByStep;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleTable;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.TeamTable;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.RoleFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.impl.RoleImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 * @author Marcellus Tavares
 * @author Connor McKay
 */
public class RoleFinderImpl extends RoleFinderBaseImpl implements RoleFinder {

	public static final String COUNT_BY_C_N_D_T =
		RoleFinder.class.getName() + ".countByC_N_D_T";

	public static final String COUNT_BY_C_N_T_D_T =
		RoleFinder.class.getName() + ".countByC_N_T_D_T";

	public static final String FIND_BY_C_N_D_T =
		RoleFinder.class.getName() + ".findByC_N_D_T";

	public static final String FIND_BY_C_N_T_D_T =
		RoleFinder.class.getName() + ".findByC_N_T_D_T";

	public static final String JOIN_BY_USERS_ROLES =
		RoleFinder.class.getName() + ".joinByUsersRoles";

	@Override
	public int countByGroupRoleAndTeamRole(
		long companyId, String name, List<String> excludedNames, String title,
		String description, int[] types, long excludedTeamRoleId,
		long teamGroupId) {

		return doCountByGroupRoleAndTeamRole(
			companyId, name, excludedNames, title, description, types,
			excludedTeamRoleId, teamGroupId, false);
	}

	@Override
	public int countByKeywords(
		long companyId, String keywords, Integer[] types) {

		return countByKeywords(
			companyId, keywords, types, new LinkedHashMap<String, Object>());
	}

	@Override
	public int countByKeywords(
		long companyId, String keywords, Integer[] types,
		LinkedHashMap<String, Object> params) {

		String[] names = null;
		String[] titles = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = CustomSQLUtil.keywords(keywords);
			titles = CustomSQLUtil.keywords(keywords);
			descriptions = CustomSQLUtil.keywords(keywords);
		}
		else {
			andOperator = true;
		}

		return countByC_N_T_D_T(
			companyId, names, titles, descriptions, types, params, andOperator);
	}

	@Override
	public int countByC_N_D_T(
		long companyId, String name, String description, Integer[] types,
		LinkedHashMap<String, Object> params, boolean andOperator) {

		String[] names = CustomSQLUtil.keywords(name);
		String[] descriptions = CustomSQLUtil.keywords(description);

		return countByC_N_D_T(
			companyId, names, descriptions, types, params, andOperator);
	}

	@Override
	public int countByC_N_D_T(
		long companyId, String[] names, String[] descriptions, Integer[] types,
		LinkedHashMap<String, Object> params, boolean andOperator) {

		return doCountByC_N_D_T(
			companyId, names, descriptions, types, params, andOperator, false);
	}

	@Override
	public int countByC_N_T_D_T(
		long companyId, String name, String title, String description,
		Integer[] types, LinkedHashMap<String, Object> params,
		boolean andOperator) {

		String[] names = CustomSQLUtil.keywords(name);
		String[] titles = CustomSQLUtil.keywords(name);
		String[] descriptions = CustomSQLUtil.keywords(description);

		return countByC_N_T_D_T(
			companyId, names, titles, descriptions, types, params, andOperator);
	}

	@Override
	public int countByC_N_T_D_T(
		long companyId, String[] names, String[] titles, String[] descriptions,
		Integer[] types, LinkedHashMap<String, Object> params,
		boolean andOperator) {

		return doCountByC_N_T_D_T(
			companyId, names, titles, descriptions, types, params, andOperator,
			false);
	}

	@Override
	public int filterCountByGroupRoleAndTeamRole(
		long companyId, String name, List<String> excludedNames, String title,
		String description, int[] types, long excludedTeamRoleId,
		long teamGroupId) {

		return doCountByGroupRoleAndTeamRole(
			companyId, name, excludedNames, title, description, types,
			excludedTeamRoleId, teamGroupId, true);
	}

	@Override
	public int filterCountByKeywords(
		long companyId, String keywords, Integer[] types,
		LinkedHashMap<String, Object> params) {

		String[] names = null;
		String[] titles = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = CustomSQLUtil.keywords(keywords);
			titles = CustomSQLUtil.keywords(keywords);
			descriptions = CustomSQLUtil.keywords(keywords);
		}
		else {
			andOperator = true;
		}

		return filterCountByC_N_T_D_T(
			companyId, names, titles, descriptions, types, params, andOperator);
	}

	@Override
	public int filterCountByC_N_D_T(
		long companyId, String name, String description, Integer[] types,
		LinkedHashMap<String, Object> params, boolean andOperator) {

		String[] names = CustomSQLUtil.keywords(name);
		String[] descriptions = CustomSQLUtil.keywords(description);

		return filterCountByC_N_D_T(
			companyId, names, descriptions, types, params, andOperator);
	}

	@Override
	public int filterCountByC_N_D_T(
		long companyId, String[] names, String[] descriptions, Integer[] types,
		LinkedHashMap<String, Object> params, boolean andOperator) {

		return doCountByC_N_D_T(
			companyId, names, descriptions, types, params, andOperator, true);
	}

	@Override
	public int filterCountByC_N_T_D_T(
		long companyId, String name, String title, String description,
		Integer[] types, LinkedHashMap<String, Object> params,
		boolean andOperator) {

		String[] names = CustomSQLUtil.keywords(name);
		String[] titles = CustomSQLUtil.keywords(title);
		String[] descriptions = CustomSQLUtil.keywords(description);

		return filterCountByC_N_T_D_T(
			companyId, names, titles, descriptions, types, params, andOperator);
	}

	@Override
	public int filterCountByC_N_T_D_T(
		long companyId, String[] names, String[] titles, String[] descriptions,
		Integer[] types, LinkedHashMap<String, Object> params,
		boolean andOperator) {

		return doCountByC_N_T_D_T(
			companyId, names, titles, descriptions, types, params, andOperator,
			true);
	}

	@Override
	public List<Role> filterFindByGroupRoleAndTeamRole(
		long companyId, String name, List<String> excludedNames, String title,
		String description, int[] types, long excludedTeamRoleId,
		long teamGroupId, int start, int end) {

		return doFindByGroupRoleAndTeamRole(
			companyId, name, excludedNames, title, description, types,
			excludedTeamRoleId, teamGroupId, start, end, true);
	}

	@Override
	public List<Role> filterFindByKeywords(
		long companyId, String keywords, Integer[] types,
		LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		String[] names = null;
		String[] titles = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = CustomSQLUtil.keywords(keywords);
			titles = CustomSQLUtil.keywords(keywords);
			descriptions = CustomSQLUtil.keywords(keywords);
		}
		else {
			andOperator = true;
		}

		return filterFindByC_N_T_D_T(
			companyId, names, titles, descriptions, types, params, andOperator,
			start, end, orderByComparator);
	}

	@Override
	public List<Role> filterFindByC_N_D_T(
		long companyId, String name, String description, Integer[] types,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end, OrderByComparator<Role> orderByComparator) {

		String[] names = CustomSQLUtil.keywords(name);
		String[] descriptions = CustomSQLUtil.keywords(description);

		return filterFindByC_N_D_T(
			companyId, names, descriptions, types, params, andOperator, start,
			end, orderByComparator);
	}

	@Override
	public List<Role> filterFindByC_N_D_T(
		long companyId, String[] names, String[] descriptions, Integer[] types,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end, OrderByComparator<Role> orderByComparator) {

		return doFindByC_N_D_T(
			companyId, names, descriptions, types, params, andOperator, start,
			end, orderByComparator, true);
	}

	@Override
	public List<Role> filterFindByC_N_T_D_T(
		long companyId, String name, String title, String description,
		Integer[] types, LinkedHashMap<String, Object> params,
		boolean andOperator, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		String[] names = CustomSQLUtil.keywords(name);
		String[] titles = CustomSQLUtil.keywords(title);
		String[] descriptions = CustomSQLUtil.keywords(description);

		return filterFindByC_N_T_D_T(
			companyId, names, titles, descriptions, types, params, andOperator,
			start, end, orderByComparator);
	}

	@Override
	public List<Role> filterFindByC_N_T_D_T(
		long companyId, String[] names, String[] titles, String[] descriptions,
		Integer[] types, LinkedHashMap<String, Object> params,
		boolean andOperator, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return doFindByC_N_T_D_T(
			companyId, names, titles, descriptions, types, params, andOperator,
			start, end, orderByComparator, true);
	}

	@Override
	public List<Role> findByGroupRoleAndTeamRole(
		long companyId, String name, List<String> excludedNames, String title,
		String description, int[] types, long excludedTeamRoleId,
		long teamGroupId, int start, int end) {

		return doFindByGroupRoleAndTeamRole(
			companyId, name, excludedNames, title, description, types,
			excludedTeamRoleId, teamGroupId, start, end, false);
	}

	@Override
	public List<Role> findByKeywords(
		long companyId, String keywords, Integer[] types, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return findByKeywords(
			companyId, keywords, types, new LinkedHashMap<String, Object>(),
			start, end, orderByComparator);
	}

	@Override
	public List<Role> findByKeywords(
		long companyId, String keywords, Integer[] types,
		LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		String[] names = null;
		String[] titles = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = CustomSQLUtil.keywords(keywords);
			titles = CustomSQLUtil.keywords(keywords);
			descriptions = CustomSQLUtil.keywords(keywords);
		}
		else {
			andOperator = true;
		}

		return findByC_N_T_D_T(
			companyId, names, titles, descriptions, types, params, andOperator,
			start, end, orderByComparator);
	}

	@Override
	public List<Role> findByC_N_D_T(
		long companyId, String name, String description, Integer[] types,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end, OrderByComparator<Role> orderByComparator) {

		String[] names = CustomSQLUtil.keywords(name);
		String[] descriptions = CustomSQLUtil.keywords(description);

		return findByC_N_D_T(
			companyId, names, descriptions, types, params, andOperator, start,
			end, orderByComparator);
	}

	@Override
	public List<Role> findByC_N_D_T(
		long companyId, String[] names, String[] descriptions, Integer[] types,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end, OrderByComparator<Role> orderByComparator) {

		return doFindByC_N_D_T(
			companyId, names, descriptions, types, params, andOperator, start,
			end, orderByComparator, false);
	}

	@Override
	public List<Role> findByC_N_T_D_T(
		long companyId, String name, String title, String description,
		Integer[] types, LinkedHashMap<String, Object> params,
		boolean andOperator, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		String[] names = CustomSQLUtil.keywords(name);
		String[] titles = CustomSQLUtil.keywords(title);
		String[] descriptions = CustomSQLUtil.keywords(description);

		return findByC_N_T_D_T(
			companyId, names, titles, descriptions, types, params, andOperator,
			start, end, orderByComparator);
	}

	@Override
	public List<Role> findByC_N_T_D_T(
		long companyId, String[] names, String[] titles, String[] descriptions,
		Integer[] types, LinkedHashMap<String, Object> params,
		boolean andOperator, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return doFindByC_N_T_D_T(
			companyId, names, titles, descriptions, types, params, andOperator,
			start, end, orderByComparator, false);
	}

	protected int doCountByGroupRoleAndTeamRole(
		long companyId, String name, List<String> excludedNames, String title,
		String description, int[] types, long excludedTeamRoleId,
		long teamGroupId, boolean inlineSQLHelper) {

		if (ArrayUtil.isEmpty(types)) {
			return 0;
		}

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(
				_getOrderByStep(
					DSLQueryFactoryUtil.select(
						DSLFunctionFactoryUtil.count(
							RoleTable.INSTANCE.roleId
						).as(
							COUNT_COLUMN_NAME
						)),
					companyId, name, excludedNames, title, description, types,
					excludedTeamRoleId, teamGroupId, inlineSQLHelper));

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

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

	protected int doCountByC_N_D_T(
		long companyId, String[] names, String[] descriptions, Integer[] types,
		LinkedHashMap<String, Object> params, boolean andOperator,
		boolean inlineSQLHelper) {

		names = CustomSQLUtil.keywords(names, true);
		descriptions = CustomSQLUtil.keywords(descriptions, true);

		if (types == null) {
			types = new Integer[0];
		}

		if (params == null) {
			params = new LinkedHashMap<>();
		}

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_C_N_D_T);

			long classNameId = GetterUtil.getLong(
				params.get("classNameId"),
				ClassNameLocalServiceUtil.getClassNameId(Role.class));

			sql = StringUtil.replace(
				sql, "[$CLASS_NAME_ID$]", String.valueOf(classNameId));

			sql = CustomSQLUtil.replaceKeywords(
				sql, "LOWER(Role_.name)", StringPool.LIKE, false, names);
			sql = CustomSQLUtil.replaceKeywords(
				sql, "LOWER(Role_.description)", StringPool.LIKE, true,
				descriptions);
			sql = StringUtil.replace(sql, "[$TYPE$]", getTypes(types.length));
			sql = StringUtil.replace(sql, "[$JOIN$]", getJoin(params));
			sql = StringUtil.replace(sql, "[$WHERE$]", getWhere(params));
			sql = CustomSQLUtil.replaceAndOperator(sql, andOperator);

			if (inlineSQLHelper &&
				InlineSQLHelperUtil.isEnabled(companyId, 0)) {

				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, Role.class.getName(), "Role_.roleId");
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			setJoin(queryPos, params);

			queryPos.add(companyId);
			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);
			queryPos.add(types);

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

	protected int doCountByC_N_T_D_T(
		long companyId, String[] names, String[] titles, String[] descriptions,
		Integer[] types, LinkedHashMap<String, Object> params,
		boolean andOperator, boolean inlineSQLHelper) {

		names = CustomSQLUtil.keywords(names, true);
		titles = CustomSQLUtil.keywords(titles, true);
		descriptions = CustomSQLUtil.keywords(descriptions, true);

		if (types == null) {
			types = new Integer[0];
		}

		if (params == null) {
			params = new LinkedHashMap<>();
		}

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_C_N_T_D_T);

			long classNameId = GetterUtil.getLong(
				params.get("classNameId"),
				ClassNameLocalServiceUtil.getClassNameId(Role.class));

			sql = StringUtil.replace(
				sql, "[$CLASS_NAME_ID$]", String.valueOf(classNameId));

			sql = CustomSQLUtil.replaceKeywords(
				sql, "LOWER(Role_.name)", StringPool.LIKE, false, names);
			sql = CustomSQLUtil.replaceKeywords(
				sql, "LOWER(Role_.title)", StringPool.LIKE, false, titles);
			sql = CustomSQLUtil.replaceKeywords(
				sql, "LOWER(Role_.description)", StringPool.LIKE, true,
				descriptions);
			sql = StringUtil.replace(sql, "[$TYPE$]", getTypes(types.length));
			sql = StringUtil.replace(sql, "[$JOIN$]", getJoin(params));
			sql = StringUtil.replace(sql, "[$WHERE$]", getWhere(params));
			sql = CustomSQLUtil.replaceAndOperator(sql, andOperator);

			if (inlineSQLHelper &&
				InlineSQLHelperUtil.isEnabled(companyId, 0)) {

				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, Role.class.getName(), "Role_.roleId");
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			setJoin(queryPos, params);

			queryPos.add(companyId);
			queryPos.add(names, 2);
			queryPos.add(titles, 2);
			queryPos.add(descriptions, 2);
			queryPos.add(types);

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

	protected List<Role> doFindByGroupRoleAndTeamRole(
		long companyId, String name, List<String> excludedNames, String title,
		String description, int[] types, long excludedTeamRoleId,
		long teamGroupId, int start, int end, boolean inlineSQLHelper) {

		if (ArrayUtil.isEmpty(types)) {
			return Collections.emptyList();
		}

		Session session = null;

		try {
			session = openSession();

			OrderByStep orderByStep = _getOrderByStep(
				DSLQueryFactoryUtil.select(RoleTable.INSTANCE), companyId, name,
				excludedNames, title, description, types, excludedTeamRoleId,
				teamGroupId, inlineSQLHelper);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(
				orderByStep.orderBy(RoleTable.INSTANCE.name.ascending()));

			sqlQuery.addEntity("Role_", RoleImpl.class);

			return (List<Role>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected List<Role> doFindByC_N_D_T(
		long companyId, String[] names, String[] descriptions, Integer[] types,
		LinkedHashMap<String, Object> params, boolean andOperator, int start,
		int end, OrderByComparator<Role> orderByComparator,
		boolean inlineSQLHelper) {

		names = CustomSQLUtil.keywords(names, true);
		descriptions = CustomSQLUtil.keywords(descriptions, true);

		if (types == null) {
			types = new Integer[0];
		}

		if (params == null) {
			params = new LinkedHashMap<>();
		}

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_C_N_D_T);

			long classNameId = GetterUtil.getLong(
				params.get("classNameId"),
				ClassNameLocalServiceUtil.getClassNameId(Role.class));

			sql = StringUtil.replace(
				sql, "[$CLASS_NAME_ID$]", String.valueOf(classNameId));

			sql = CustomSQLUtil.replaceKeywords(
				sql, "LOWER(Role_.name)", StringPool.LIKE, false, names);
			sql = CustomSQLUtil.replaceKeywords(
				sql, "LOWER(Role_.description)", StringPool.LIKE, true,
				descriptions);
			sql = StringUtil.replace(sql, "[$TYPE$]", getTypes(types.length));
			sql = StringUtil.replace(sql, "[$JOIN$]", getJoin(params));
			sql = StringUtil.replace(sql, "[$WHERE$]", getWhere(params));
			sql = CustomSQLUtil.replaceAndOperator(sql, andOperator);
			sql = CustomSQLUtil.replaceOrderBy(sql, orderByComparator);

			if (inlineSQLHelper &&
				InlineSQLHelperUtil.isEnabled(companyId, 0)) {

				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, Role.class.getName(), "Role_.roleId");
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("Role_", RoleImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			setJoin(queryPos, params);

			queryPos.add(companyId);
			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);
			queryPos.add(types);

			return (List<Role>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected List<Role> doFindByC_N_T_D_T(
		long companyId, String[] names, String[] titles, String[] descriptions,
		Integer[] types, LinkedHashMap<String, Object> params,
		boolean andOperator, int start, int end,
		OrderByComparator<Role> orderByComparator, boolean inlineSQLHelper) {

		names = CustomSQLUtil.keywords(names, true);
		titles = CustomSQLUtil.keywords(titles, true);
		descriptions = CustomSQLUtil.keywords(descriptions, true);

		if (types == null) {
			types = new Integer[0];
		}

		if (params == null) {
			params = new LinkedHashMap<>();
		}

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_C_N_T_D_T);

			long classNameId = GetterUtil.getLong(
				params.get("classNameId"),
				ClassNameLocalServiceUtil.getClassNameId(Role.class));

			sql = StringUtil.replace(
				sql, "[$CLASS_NAME_ID$]", String.valueOf(classNameId));

			sql = CustomSQLUtil.replaceKeywords(
				sql, "LOWER(Role_.name)", StringPool.LIKE, false, names);
			sql = CustomSQLUtil.replaceKeywords(
				sql, "LOWER(Role_.title)", StringPool.LIKE, false, titles);
			sql = CustomSQLUtil.replaceKeywords(
				sql, "LOWER(Role_.description)", StringPool.LIKE, true,
				descriptions);
			sql = StringUtil.replace(sql, "[$TYPE$]", getTypes(types.length));
			sql = StringUtil.replace(sql, "[$JOIN$]", getJoin(params));
			sql = StringUtil.replace(sql, "[$WHERE$]", getWhere(params));
			sql = CustomSQLUtil.replaceAndOperator(sql, andOperator);
			sql = CustomSQLUtil.replaceOrderBy(sql, orderByComparator);

			if (inlineSQLHelper &&
				InlineSQLHelperUtil.isEnabled(companyId, 0)) {

				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, Role.class.getName(), "Role_.roleId");
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("Role_", RoleImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			setJoin(queryPos, params);

			queryPos.add(companyId);
			queryPos.add(names, 2);
			queryPos.add(titles, 2);
			queryPos.add(descriptions, 2);
			queryPos.add(types);

			return (List<Role>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected String getExcludedNames(List<String> excludedNames) {
		if (ListUtil.isEmpty(excludedNames)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(excludedNames.size() + 1);

		sb.append(" AND (");

		for (int i = 0; i < (excludedNames.size() - 1); i++) {
			sb.append("Role_.name != ? AND ");
		}

		sb.append("Role_.name != ?)");

		return sb.toString();
	}

	protected String getJoin(LinkedHashMap<String, Object> params) {
		if ((params == null) || params.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(params.size());

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			if (Validator.isNotNull(entry.getValue())) {
				sb.append(getJoin(entry.getKey()));
			}
		}

		return sb.toString();
	}

	protected String getJoin(String key) {
		String join = StringPool.BLANK;

		if (key.equals("usersRoles")) {
			join = CustomSQLUtil.get(JOIN_BY_USERS_ROLES);
		}

		if (Validator.isNotNull(join)) {
			int pos = join.indexOf("WHERE");

			if (pos != -1) {
				join = join.substring(0, pos);
			}
		}

		return join;
	}

	protected String getTypes(int size) {
		if (size == 0) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(size + 1);

		sb.append(" AND (");

		for (int i = 0; i < (size - 1); i++) {
			sb.append("Role_.type_ = ? OR ");
		}

		sb.append("Role_.type_ = ?)");

		return sb.toString();
	}

	protected String getWhere(LinkedHashMap<String, Object> params) {
		if ((params == null) || params.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(params.size());

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			if (Validator.isNotNull(entry.getValue())) {
				sb.append(getWhere(entry.getKey()));
			}
		}

		return sb.toString();
	}

	protected String getWhere(String key) {
		String join = StringPool.BLANK;

		if (key.equals("usersRoles")) {
			join = CustomSQLUtil.get(JOIN_BY_USERS_ROLES);
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

	protected void setJoin(
		QueryPos queryPos, LinkedHashMap<String, Object> params) {

		if (params == null) {
			return;
		}

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			if (Objects.equals(entry.getKey(), "classNameId")) {
				continue;
			}

			Object value = entry.getValue();

			if (value instanceof Long) {
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

	private Predicate _getKeywordsPredicate(
		Expression<String> expression, String[] keywords) {

		Predicate keywordsPredicate = null;

		expression = DSLFunctionFactoryUtil.lower(expression);

		for (String keyword : keywords) {
			if (keyword == null) {
				continue;
			}

			Predicate keywordPredicate = expression.like(keyword);

			if (keywordsPredicate == null) {
				keywordsPredicate = keywordPredicate;
			}
			else {
				keywordsPredicate = keywordsPredicate.or(keywordPredicate);
			}
		}

		return keywordsPredicate;
	}

	private OrderByStep _getOrderByStep(
		FromStep fromStep, long companyId, String name,
		List<String> excludedNames, String title, String description,
		int[] types, long excludedTeamRoleId, long teamGroupId,
		boolean inlineSQLHelper) {

		Predicate wherePredicate = RoleTable.INSTANCE.companyId.eq(companyId);

		if (inlineSQLHelper && InlineSQLHelperUtil.isEnabled()) {
			wherePredicate = Predicate.withParentheses(
				wherePredicate.and(
					InlineSQLHelperUtil.getPermissionWherePredicate(
						Role.class, RoleTable.INSTANCE.roleId,
						new long[] {0})));
		}

		Predicate rolesWherePredicate = null;
		Predicate teamsSubqueryPredicate = null;

		if (Validator.isNotNull(name)) {
			String[] names = CustomSQLUtil.keywords(name, true);

			rolesWherePredicate = _getKeywordsPredicate(
				RoleTable.INSTANCE.name, names);

			teamsSubqueryPredicate = _getKeywordsPredicate(
				TeamTable.INSTANCE.name, names);
		}

		if (Validator.isNotNull(title)) {
			rolesWherePredicate = Predicate.or(
				rolesWherePredicate,
				_getKeywordsPredicate(
					RoleTable.INSTANCE.title,
					CustomSQLUtil.keywords(title, true)));
		}

		if (Validator.isNotNull(description)) {
			String[] descriptions = CustomSQLUtil.keywords(description, true);

			rolesWherePredicate = Predicate.or(
				rolesWherePredicate,
				_getKeywordsPredicate(
					DSLFunctionFactoryUtil.castClobText(
						RoleTable.INSTANCE.description),
					descriptions));

			teamsSubqueryPredicate = Predicate.or(
				teamsSubqueryPredicate,
				_getKeywordsPredicate(
					TeamTable.INSTANCE.description, descriptions));
		}

		rolesWherePredicate = Predicate.withParentheses(rolesWherePredicate);

		teamsSubqueryPredicate = TeamTable.INSTANCE.groupId.eq(
			teamGroupId
		).and(
			Predicate.withParentheses(teamsSubqueryPredicate)
		);

		if (ListUtil.isNotEmpty(excludedNames)) {
			Predicate excludedNamesWherePredicate = RoleTable.INSTANCE.name.neq(
				excludedNames.get(0));

			for (int i = 1; i < excludedNames.size(); i++) {
				excludedNamesWherePredicate = excludedNamesWherePredicate.and(
					RoleTable.INSTANCE.name.neq(excludedNames.get(i)));
			}

			if (rolesWherePredicate == null) {
				rolesWherePredicate = Predicate.withParentheses(
					excludedNamesWherePredicate);
			}
			else {
				rolesWherePredicate = rolesWherePredicate.and(
					Predicate.withParentheses(excludedNamesWherePredicate));
			}
		}

		Predicate typesWherePredicate = RoleTable.INSTANCE.type.eq(types[0]);

		for (int i = 1; i < types.length; i++) {
			typesWherePredicate = typesWherePredicate.or(
				RoleTable.INSTANCE.type.eq(types[i]));
		}

		if (rolesWherePredicate == null) {
			rolesWherePredicate = Predicate.withParentheses(
				typesWherePredicate);
		}
		else {
			rolesWherePredicate = rolesWherePredicate.and(
				Predicate.withParentheses(typesWherePredicate));
		}

		if (rolesWherePredicate != null) {
			wherePredicate = wherePredicate.and(
				Predicate.withParentheses(rolesWherePredicate));
		}

		DSLQuery teamsSubqueryDSLQuery = DSLQueryFactoryUtil.select(
			TeamTable.INSTANCE.teamId
		).from(
			TeamTable.INSTANCE
		).where(
			teamsSubqueryPredicate
		);

		Predicate teamsWherePredicate = RoleTable.INSTANCE.classNameId.eq(
			ClassNameLocalServiceUtil.getClassNameId(Team.class)
		).and(
			RoleTable.INSTANCE.roleId.neq(excludedTeamRoleId)
		).and(
			RoleTable.INSTANCE.classPK.in(teamsSubqueryDSLQuery)
		);

		return fromStep.from(
			RoleTable.INSTANCE
		).where(
			wherePredicate.or(Predicate.withParentheses(teamsWherePredicate))
		);
	}

}