/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service.persistence.impl;

import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.impl.CalendarImpl;
import com.liferay.calendar.model.impl.CalendarModelImpl;
import com.liferay.calendar.service.persistence.CalendarFinder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo Lundgren
 * @author Fabio Pezzutto
 */
@Component(service = CalendarFinder.class)
public class CalendarFinderImpl
	extends CalendarFinderBaseImpl implements CalendarFinder {

	public static final String FIND_BY_C_G_C_N_D =
		CalendarFinder.class.getName() + ".findByC_G_C_N_D";

	@Override
	public List<Calendar> findByKeywords(
		long companyId, long[] groupIds, long[] calendarResourceIds,
		String keywords, int start, int end,
		OrderByComparator<Calendar> orderByComparator) {

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

		return findByC_G_C_N_D(
			companyId, groupIds, calendarResourceIds, names, descriptions,
			andOperator, start, end, orderByComparator);
	}

	@Override
	public List<Calendar> findByC_G_C_N_D(
		long companyId, long[] groupIds, long[] calendarResourceIds,
		String name, String description, boolean andOperator, int start,
		int end, OrderByComparator<Calendar> orderByComparator) {

		String[] names = _customSQL.keywords(name);
		String[] descriptions = _customSQL.keywords(description, false);

		return findByC_G_C_N_D(
			companyId, groupIds, calendarResourceIds, names, descriptions,
			andOperator, start, end, orderByComparator);
	}

	@Override
	public List<Calendar> findByC_G_C_N_D(
		long companyId, long[] groupIds, long[] calendarResourceIds,
		String[] names, String[] descriptions, boolean andOperator, int start,
		int end, OrderByComparator<Calendar> orderByComparator) {

		if (groupIds == null) {
			groupIds = new long[0];
		}

		names = _customSQL.keywords(names);
		descriptions = _customSQL.keywords(descriptions, false);

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_C_G_C_N_D);

			sql = StringUtil.replace(
				sql, "[$GROUP_ID$]", getGroupIds(groupIds));
			sql = StringUtil.replace(
				sql, "[$CALENDAR_RESOURCE_ID$]",
				getCalendarResourceIds(calendarResourceIds));
			sql = _customSQL.replaceKeywords(
				sql, "LOWER(name)", StringPool.LIKE, false, names);
			sql = _customSQL.replaceKeywords(
				sql, "description", StringPool.LIKE, false, descriptions);
			sql = _customSQL.replaceAndOperator(sql, andOperator);

			StringBundler sb = new StringBundler();

			if (orderByComparator != null) {
				appendOrderByComparator(sb, "Calendar.", orderByComparator);
			}

			sql = StringUtil.replace(sql, "[$ORDER_BY$]", sb.toString());

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("Calendar", CalendarImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);
			queryPos.add(groupIds);

			if (ArrayUtil.isNotEmpty(calendarResourceIds)) {
				queryPos.add(calendarResourceIds);
			}

			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);

			return (List<Calendar>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected String getCalendarResourceIds(long[] calendarResourceIds) {
		if (ArrayUtil.isEmpty(calendarResourceIds)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(calendarResourceIds.length + 1);

		sb.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 0; i < (calendarResourceIds.length - 1); i++) {
			sb.append("calendarResourceId = ? OR ");
		}

		sb.append("calendarResourceId = ?) AND");

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
		return CalendarModelImpl.TABLE_COLUMNS_MAP;
	}

	@Reference
	private CustomSQL _customSQL;

}