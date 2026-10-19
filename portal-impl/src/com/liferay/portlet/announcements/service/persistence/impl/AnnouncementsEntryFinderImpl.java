/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.announcements.service.persistence.impl;

import com.liferay.announcements.kernel.model.AnnouncementsEntry;
import com.liferay.announcements.kernel.model.AnnouncementsFlagConstants;
import com.liferay.announcements.kernel.service.persistence.AnnouncementsEntryFinder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.CalendarUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portlet.announcements.model.impl.AnnouncementsEntryImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.sql.Timestamp;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thiago Moreira
 * @author Raymond Augé
 */
public class AnnouncementsEntryFinderImpl
	extends AnnouncementsEntryFinderBaseImpl
	implements AnnouncementsEntryFinder {

	public static final String COUNT_BY_HIDDEN =
		AnnouncementsEntryFinder.class.getName() + ".countByHidden";

	public static final String COUNT_BY_NOT_HIDDEN =
		AnnouncementsEntryFinder.class.getName() + ".countByNotHidden";

	public static final String FIND_BY_DISPLAY_DATE =
		AnnouncementsEntryFinder.class.getName() + ".findByDisplayDate";

	public static final String FIND_BY_HIDDEN =
		AnnouncementsEntryFinder.class.getName() + ".findByHidden";

	public static final String FIND_BY_NOT_HIDDEN =
		AnnouncementsEntryFinder.class.getName() + ".findByNotHidden";

	@Override
	public int countByScopes(
		long companyId, long userId, LinkedHashMap<Long, long[]> scopes,
		int displayDateMonth, int displayDateDay, int displayDateYear,
		int displayDateHour, int displayDateMinute, int expirationDateMonth,
		int expirationDateDay, int expirationDateYear, int expirationDateHour,
		int expirationDateMinute, boolean alert, int flagValue) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_HIDDEN);

			if (flagValue == AnnouncementsFlagConstants.NOT_HIDDEN) {
				sql = CustomSQLUtil.get(COUNT_BY_NOT_HIDDEN);
			}

			sql = StringUtil.replace(sql, "[$CLASS_PKS$]", getClassPKs(scopes));
			sql = CustomSQLUtil.replaceAndOperator(sql, true);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (flagValue == AnnouncementsFlagConstants.NOT_HIDDEN) {
				queryPos.add(userId);
				queryPos.add(AnnouncementsFlagConstants.HIDDEN);
			}

			queryPos.add(companyId);

			setClassPKs(queryPos, scopes);

			setDates(
				queryPos, displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute);

			queryPos.add(alert);

			if (flagValue != AnnouncementsFlagConstants.NOT_HIDDEN) {
				queryPos.add(userId);
				queryPos.add(AnnouncementsFlagConstants.HIDDEN);
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
	public List<AnnouncementsEntry> findByDisplayDate(
		Date displayDateLT, Date displayDateGT) {

		Timestamp displayDateLT_TS = CalendarUtil.getTimestamp(displayDateLT);
		Timestamp displayDateGT_TS = CalendarUtil.getTimestamp(displayDateGT);

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_DISPLAY_DATE);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				"AnnouncementsEntry", AnnouncementsEntryImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(displayDateGT_TS);
			queryPos.add(displayDateLT_TS);

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
	public List<AnnouncementsEntry> findByScopes(
		long companyId, long userId, LinkedHashMap<Long, long[]> scopes,
		int displayDateMonth, int displayDateDay, int displayDateYear,
		int displayDateHour, int displayDateMinute, int expirationDateMonth,
		int expirationDateDay, int expirationDateYear, int expirationDateHour,
		int expirationDateMinute, boolean alert, int flagValue, int start,
		int end) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_HIDDEN);

			if (flagValue == AnnouncementsFlagConstants.NOT_HIDDEN) {
				sql = CustomSQLUtil.get(FIND_BY_NOT_HIDDEN);
			}

			sql = StringUtil.replace(sql, "[$CLASS_PKS$]", getClassPKs(scopes));
			sql = CustomSQLUtil.replaceAndOperator(sql, true);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				"AnnouncementsEntry", AnnouncementsEntryImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (flagValue == AnnouncementsFlagConstants.NOT_HIDDEN) {
				queryPos.add(userId);
				queryPos.add(AnnouncementsFlagConstants.HIDDEN);
			}

			queryPos.add(companyId);

			setClassPKs(queryPos, scopes);

			setDates(
				queryPos, displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute);

			queryPos.add(alert);

			if (flagValue != AnnouncementsFlagConstants.NOT_HIDDEN) {
				queryPos.add(userId);
				queryPos.add(AnnouncementsFlagConstants.HIDDEN);
			}

			return (List<AnnouncementsEntry>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected String getClassPKs(LinkedHashMap<Long, long[]> scopes) {
		if ((scopes == null) || scopes.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(scopes.size() * 4);

		for (Map.Entry<Long, long[]> entry : scopes.entrySet()) {
			Long classNameId = entry.getKey();
			long[] classPKs = entry.getValue();

			sb.append(StringPool.OPEN_PARENTHESIS);
			sb.append(getClassPKs(classNameId.longValue(), classPKs));
			sb.append(StringPool.CLOSE_PARENTHESIS);
			sb.append(" OR ");
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	protected String getClassPKs(long classNameId, long[] classPKs) {
		if (classPKs.length == 0) {
			return "(AnnouncementsEntry.classNameId = ?) AND (";
		}

		StringBundler sb = new StringBundler((classPKs.length * 2) + 1);

		sb.append("(AnnouncementsEntry.classNameId = ?) AND (");

		for (int i = 0; i < classPKs.length; i++) {
			sb.append("(AnnouncementsEntry.classPK = ?)");

			if ((i + 1) < classPKs.length) {
				sb.append(" OR ");
			}
			else {
				sb.append(StringPool.CLOSE_PARENTHESIS);
			}
		}

		return sb.toString();
	}

	protected void setClassPKs(
		QueryPos queryPos, LinkedHashMap<Long, long[]> scopes) {

		if (scopes == null) {
			return;
		}

		for (Map.Entry<Long, long[]> entry : scopes.entrySet()) {
			Long classNameId = entry.getKey();
			long[] classPKs = entry.getValue();

			setClassPKs(queryPos, classNameId.longValue(), classPKs);
		}
	}

	protected void setClassPKs(
		QueryPos queryPos, long classNameId, long[] classPKs) {

		queryPos.add(classNameId);

		for (long classPK : classPKs) {
			queryPos.add(classPK);
		}
	}

	protected void setDates(
		QueryPos queryPos, int displayDateMonth, int displayDateDay,
		int displayDateYear, int displayDateHour, int displayDateMinute,
		int expirationDateMonth, int expirationDateDay, int expirationDateYear,
		int expirationDateHour, int expirationDateMinute) {

		Date displayDate = null;

		try {
			displayDate = PortalUtil.getDate(
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, null);
		}
		catch (PortalException portalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		if (displayDate == null) {
			displayDate = new Date();
		}

		Timestamp displayDateTS = CalendarUtil.getTimestamp(displayDate);

		Date expirationDate = null;

		try {
			expirationDate = PortalUtil.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, null);
		}
		catch (PortalException portalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		if (expirationDate == null) {
			expirationDate = new Date();
		}

		Timestamp expirationDateTS = CalendarUtil.getTimestamp(expirationDate);

		queryPos.add(displayDateTS);
		queryPos.add(displayDateTS);
		queryPos.add(expirationDateTS);
		queryPos.add(expirationDateTS);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnnouncementsEntryFinderImpl.class);

}