/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service.persistence.impl;

import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.model.impl.CalendarResourceImpl;
import com.liferay.calendar.model.impl.CalendarResourceModelImpl;
import com.liferay.calendar.service.persistence.CalendarResourceFinder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo Lundgren
 * @author Fabio Pezzutto
 */
@Component(service = CalendarResourceFinder.class)
public class CalendarResourceFinderImpl
	extends CalendarResourceFinderBaseImpl implements CalendarResourceFinder {

	public static final String COUNT_BY_C_G_C_C_N_D_A =
		CalendarResourceFinder.class.getName() + ".countByC_G_C_C_N_D_A";

	public static final String FIND_BY_C_G_C_C_N_D_A =
		CalendarResourceFinder.class.getName() + ".findByC_G_C_C_N_D_A";

	@Override
	public int countByKeywords(
		long companyId, long[] groupIds, long[] classNameIds, String keywords,
		boolean active) {

		String[] codes = null;
		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			codes = _customSQL.keywords(keywords);
			names = _customSQL.keywords(keywords);
			descriptions = _customSQL.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return countByC_G_C_C_N_D_A(
			companyId, groupIds, classNameIds, codes, names, descriptions,
			active, andOperator);
	}

	@Override
	public int countByC_G_C_C_N_D_A(
		long companyId, long[] groupIds, long[] classNameIds, String[] codes,
		String[] names, String[] descriptions, boolean active,
		boolean andOperator) {

		return doCountByC_G_C_C_N_D_A(
			companyId, groupIds, classNameIds, codes, names, descriptions,
			active, andOperator, false);
	}

	@Override
	public int filterCountByKeywords(
		long companyId, long[] groupIds, long[] classNameIds, String keywords,
		boolean active) {

		String[] codes = null;
		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			codes = _customSQL.keywords(keywords);
			names = _customSQL.keywords(keywords);
			descriptions = _customSQL.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return filterCountByC_G_C_C_N_D_A(
			companyId, groupIds, classNameIds, codes, names, descriptions,
			active, andOperator);
	}

	@Override
	public int filterCountByC_G_C_C_N_D_A(
		long companyId, long[] groupIds, long[] classNameIds, String code,
		String name, String description, boolean active, boolean andOperator) {

		String[] codes = _customSQL.keywords(code);
		String[] names = _customSQL.keywords(name);
		String[] descriptions = _customSQL.keywords(description, false);

		return filterCountByC_G_C_C_N_D_A(
			companyId, groupIds, classNameIds, codes, names, descriptions,
			active, andOperator);
	}

	@Override
	public int filterCountByC_G_C_C_N_D_A(
		long companyId, long[] groupIds, long[] classNameIds, String[] codes,
		String[] names, String[] descriptions, boolean active,
		boolean andOperator) {

		return doCountByC_G_C_C_N_D_A(
			companyId, groupIds, classNameIds, codes, names, descriptions,
			active, andOperator, true);
	}

	@Override
	public List<CalendarResource> filterFindByKeywords(
		long companyId, long[] groupIds, long[] classNameIds, String keywords,
		boolean active, int start, int end,
		OrderByComparator<CalendarResource> orderByComparator) {

		String[] codes = null;
		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			codes = _customSQL.keywords(keywords);
			names = _customSQL.keywords(keywords);
			descriptions = _customSQL.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return filterFindByC_G_C_C_N_D_A(
			companyId, groupIds, classNameIds, codes, names, descriptions,
			active, andOperator, start, end, orderByComparator);
	}

	@Override
	public List<CalendarResource> filterFindByC_G_C_C_N_D_A(
		long companyId, long[] groupIds, long[] classNameIds, String code,
		String name, String description, boolean active, boolean andOperator,
		int start, int end,
		OrderByComparator<CalendarResource> orderByComparator) {

		String[] codes = _customSQL.keywords(code);
		String[] names = _customSQL.keywords(name);
		String[] descriptions = _customSQL.keywords(description, false);

		return filterFindByC_G_C_C_N_D_A(
			companyId, groupIds, classNameIds, codes, names, descriptions,
			active, andOperator, start, end, orderByComparator);
	}

	@Override
	public List<CalendarResource> filterFindByC_G_C_C_N_D_A(
		long companyId, long[] groupIds, long[] classNameIds, String[] codes,
		String[] names, String[] descriptions, boolean active,
		boolean andOperator, int start, int end,
		OrderByComparator<CalendarResource> orderByComparator) {

		return doFindByC_G_C_C_N_D_A(
			companyId, groupIds, classNameIds, codes, names, descriptions,
			active, andOperator, start, end, orderByComparator, true);
	}

	@Override
	public List<CalendarResource> findByKeywords(
		long companyId, long[] groupIds, long[] classNameIds, String keywords,
		boolean active, int start, int end,
		OrderByComparator<CalendarResource> orderByComparator) {

		String[] codes = null;
		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			codes = _customSQL.keywords(keywords);
			names = _customSQL.keywords(keywords);
			descriptions = _customSQL.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return findByC_G_C_C_N_D_A(
			companyId, groupIds, classNameIds, codes, names, descriptions,
			active, andOperator, start, end, orderByComparator);
	}

	@Override
	public List<CalendarResource> findByC_G_C_C_N_D_A(
		long companyId, long[] groupIds, long[] classNameIds, String[] codes,
		String[] names, String[] descriptions, boolean active,
		boolean andOperator, int start, int end,
		OrderByComparator<CalendarResource> orderByComparator) {

		return doFindByC_G_C_C_N_D_A(
			companyId, groupIds, classNameIds, codes, names, descriptions,
			active, andOperator, start, end, orderByComparator, false);
	}

	protected int doCountByC_G_C_C_N_D_A(
		long companyId, long[] groupIds, long[] classNameIds, String[] codes,
		String[] names, String[] descriptions, boolean active,
		boolean andOperator, boolean inlineSQLHelper) {

		codes = _customSQL.keywords(codes);
		names = _customSQL.keywords(names);
		descriptions = _customSQL.keywords(descriptions, false);

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_C_G_C_C_N_D_A);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, CalendarResource.class.getName(),
					"CalendarResource.calendarResourceId", groupIds);
			}

			sql = StringUtil.replace(
				sql, "[$GROUP_ID$]", getGroupIds(groupIds));
			sql = StringUtil.replace(
				sql, "[$CLASS_NAME_ID$]", getClassNameIds(classNameIds));
			sql = _customSQL.replaceKeywords(
				sql, "LOWER(code_)", StringPool.LIKE, false, codes);
			sql = _customSQL.replaceKeywords(
				sql, "LOWER(name)", StringPool.LIKE, false, names);
			sql = _customSQL.replaceKeywords(
				sql, "description", StringPool.LIKE, true, descriptions);
			sql = _customSQL.replaceAndOperator(sql, andOperator);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);
			queryPos.add(groupIds);

			if (ArrayUtil.isNotEmpty(classNameIds)) {
				queryPos.add(classNameIds);
			}

			queryPos.add(codes, 2);
			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);
			queryPos.add(active);

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

	protected List<CalendarResource> doFindByC_G_C_C_N_D_A(
		long companyId, long[] groupIds, long[] classNameIds, String[] codes,
		String[] names, String[] descriptions, boolean active,
		boolean andOperator, int start, int end,
		OrderByComparator<CalendarResource> orderByComparator,
		boolean inlineSQLHelper) {

		codes = _customSQL.keywords(codes);
		names = _customSQL.keywords(names);
		descriptions = _customSQL.keywords(descriptions, false);

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_C_G_C_C_N_D_A);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, CalendarResource.class.getName(),
					"CalendarResource.calendarResourceId", groupIds);
			}

			sql = StringUtil.replace(
				sql, "[$GROUP_ID$]", getGroupIds(groupIds));
			sql = StringUtil.replace(
				sql, "[$CLASS_NAME_ID$]", getClassNameIds(classNameIds));
			sql = _customSQL.replaceKeywords(
				sql, "LOWER(code_)", StringPool.LIKE, false, codes);
			sql = _customSQL.replaceKeywords(
				sql, "LOWER(name)", StringPool.LIKE, false, names);
			sql = _customSQL.replaceKeywords(
				sql, "description", StringPool.LIKE, true, descriptions);
			sql = _customSQL.replaceAndOperator(sql, andOperator);

			StringBundler sb = new StringBundler();

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, "CalendarResource.", orderByComparator, true);
			}

			sql = StringUtil.replace(sql, "[$ORDER_BY$]", sb.toString());

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("CalendarResource", CalendarResourceImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);
			queryPos.add(groupIds);

			if (ArrayUtil.isNotEmpty(classNameIds)) {
				queryPos.add(classNameIds);
			}

			queryPos.add(codes, 2);
			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);
			queryPos.add(active);

			return (List<CalendarResource>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected String getClassNameIds(long[] classNameIds) {
		if (ArrayUtil.isEmpty(classNameIds)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(classNameIds.length + 1);

		sb.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 0; i < (classNameIds.length - 1); i++) {
			sb.append("classNameId = ? OR ");
		}

		sb.append("classNameId = ?) AND");

		return sb.toString();
	}

	protected String getGroupIds(long[] groupIds) {
		if (ArrayUtil.isEmpty(groupIds)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(groupIds.length + 1);

		sb.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 0; i < (groupIds.length - 1); i++) {
			sb.append("groupId = ? OR ");
		}

		sb.append("groupId = ?) AND");

		return sb.toString();
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CalendarResourceModelImpl.TABLE_COLUMNS_MAP;
	}

	@Reference
	private CustomSQL _customSQL;

}