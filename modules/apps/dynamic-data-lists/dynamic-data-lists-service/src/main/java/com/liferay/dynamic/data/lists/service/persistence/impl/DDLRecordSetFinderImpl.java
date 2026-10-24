/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.service.persistence.impl;

import com.liferay.dynamic.data.lists.constants.DDLRecordSetConstants;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordSetImpl;
import com.liferay.dynamic.data.lists.service.persistence.DDLRecordSetFinder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 * @author Connor McKay
 */
@Component(service = DDLRecordSetFinder.class)
public class DDLRecordSetFinderImpl
	extends DDLRecordSetFinderBaseImpl implements DDLRecordSetFinder {

	public static final String COUNT_BY_C_G_N_D_S =
		DDLRecordSetFinder.class.getName() + ".countByC_G_N_D_S";

	public static final String FIND_BY_C_G_N_D_S =
		DDLRecordSetFinder.class.getName() + ".findByC_G_N_D_S";

	@Override
	public int countByKeywords(
		long companyId, long groupId, String keywords, int scope) {

		return doCountByKeywords(companyId, groupId, keywords, scope, false);
	}

	@Override
	public int filterCountByKeywords(
		long companyId, long groupId, String keywords, int scope) {

		return doCountByKeywords(companyId, groupId, keywords, scope, true);
	}

	@Override
	public int filterCountByC_G_N_D_S(
		long companyId, long groupId, String name, String description,
		int scope, boolean andOperator) {

		String[] names = _customSQL.keywords(name);
		String[] descriptions = _customSQL.keywords(description, false);

		return doCountByC_G_N_D_S(
			companyId, groupId, names, descriptions, scope, andOperator, true);
	}

	@Override
	public List<DDLRecordSet> filterFindByKeywords(
		long companyId, long groupId, String keywords, int scope, int start,
		int end, OrderByComparator<DDLRecordSet> orderByComparator) {

		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = _customSQL.keywords(keywords);
			descriptions = _customSQL.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return filterFindByC_G_N_D_S(
			companyId, groupId, names, descriptions, scope, andOperator, start,
			end, orderByComparator);
	}

	@Override
	public List<DDLRecordSet> filterFindByC_G_N_D_S(
		long companyId, long groupId, String name, String description,
		int scope, boolean andOperator, int start, int end,
		OrderByComparator<DDLRecordSet> orderByComparator) {

		String[] names = _customSQL.keywords(name);
		String[] descriptions = _customSQL.keywords(description, false);

		return filterFindByC_G_N_D_S(
			companyId, groupId, names, descriptions, scope, andOperator, start,
			end, orderByComparator);
	}

	@Override
	public List<DDLRecordSet> filterFindByC_G_N_D_S(
		long companyId, long groupId, String[] names, String[] descriptions,
		int scope, boolean andOperator, int start, int end,
		OrderByComparator<DDLRecordSet> orderByComparator) {

		return doFindByC_G_N_D_S(
			companyId, groupId, names, descriptions, scope, andOperator, start,
			end, orderByComparator, true);
	}

	@Override
	public List<DDLRecordSet> findByKeywords(
		long companyId, long groupId, String keywords, int scope, int start,
		int end, OrderByComparator<DDLRecordSet> orderByComparator) {

		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = _customSQL.keywords(keywords);
			descriptions = _customSQL.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return findByC_G_N_D_S(
			companyId, groupId, names, descriptions, scope, andOperator, start,
			end, orderByComparator);
	}

	@Override
	public List<DDLRecordSet> findByC_G_N_D_S(
		long companyId, long groupId, String[] names, String[] descriptions,
		int scope, boolean andOperator, int start, int end,
		OrderByComparator<DDLRecordSet> orderByComparator) {

		return doFindByC_G_N_D_S(
			companyId, groupId, names, descriptions, scope, andOperator, start,
			end, orderByComparator, false);
	}

	protected int doCountByKeywords(
		long companyId, long groupId, String keywords, int scope,
		boolean inlineSQLHelper) {

		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = _customSQL.keywords(keywords);
			descriptions = _customSQL.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return doCountByC_G_N_D_S(
			companyId, groupId, names, descriptions, scope, andOperator,
			inlineSQLHelper);
	}

	protected int doCountByC_G_N_D_S(
		long companyId, long groupId, String[] names, String[] descriptions,
		int scope, boolean andOperator, boolean inlineSQLHelper) {

		names = _customSQL.keywords(names);
		descriptions = _customSQL.keywords(descriptions, false);

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_C_G_N_D_S);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, DDLRecordSet.class.getName(),
					"DDLRecordSet.recordSetId", groupId);
			}

			if (groupId <= 0) {
				sql = StringUtil.removeSubstring(
					sql, "(DDLRecordSet.groupId = ?) AND");
			}

			if (scope == DDLRecordSetConstants.SCOPE_ANY) {
				sql = StringUtil.removeSubstring(
					sql, "(DDLRecordSet.scope = ?) AND");
			}

			sql = _customSQL.replaceKeywords(
				sql, "LOWER(DDLRecordSet.name)", StringPool.LIKE, false, names);
			sql = _customSQL.replaceKeywords(
				sql, "DDLRecordSet.description", StringPool.LIKE, true,
				descriptions);
			sql = _customSQL.replaceAndOperator(sql, andOperator);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (groupId > 0) {
				queryPos.add(groupId);
			}

			if (scope != DDLRecordSetConstants.SCOPE_ANY) {
				queryPos.add(scope);
			}

			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);

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

	protected List<DDLRecordSet> doFindByC_G_N_D_S(
		long companyId, long groupId, String[] names, String[] descriptions,
		int scope, boolean andOperator, int start, int end,
		OrderByComparator<DDLRecordSet> orderByComparator,
		boolean inlineSQLHelper) {

		names = _customSQL.keywords(names);
		descriptions = _customSQL.keywords(descriptions, false);

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_C_G_N_D_S);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, DDLRecordSet.class.getName(),
					"DDLRecordSet.recordSetId", groupId);
			}

			if (groupId <= 0) {
				sql = StringUtil.removeSubstring(
					sql, "(DDLRecordSet.groupId = ?) AND");
			}

			if (scope == DDLRecordSetConstants.SCOPE_ANY) {
				sql = StringUtil.removeSubstring(
					sql, "(DDLRecordSet.scope = ?) AND");
			}

			sql = _customSQL.replaceKeywords(
				sql, "LOWER(DDLRecordSet.name)", StringPool.LIKE, false, names);
			sql = _customSQL.replaceKeywords(
				sql, "LOWER(DDLRecordSet.description)", StringPool.LIKE, true,
				descriptions);
			sql = _customSQL.replaceAndOperator(sql, andOperator);
			sql = _customSQL.replaceOrderBy(sql, orderByComparator);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("DDLRecordSet", DDLRecordSetImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (groupId > 0) {
				queryPos.add(groupId);
			}

			if (scope != DDLRecordSetConstants.SCOPE_ANY) {
				queryPos.add(scope);
			}

			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);

			return (List<DDLRecordSet>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Reference
	private CustomSQL _customSQL;

}