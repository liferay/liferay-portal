/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portlet.social.model.impl.SocialActivityCounterImpl;
import com.liferay.social.kernel.model.SocialActivityCounter;
import com.liferay.social.kernel.service.persistence.SocialActivityCounterFinder;
import com.liferay.social.kernel.util.SocialCounterPeriodUtil;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Zsolt Berentey
 */
public class SocialActivityCounterFinderImpl
	extends SocialActivityCounterFinderBaseImpl
	implements SocialActivityCounterFinder {

	public static final String COUNT_U_BY_G_C_N_S_E =
		SocialActivityCounterFinder.class.getName() + ".countU_ByG_C_N_S_E";

	public static final String FIND_AC_BY_G_N_S_E_1 =
		SocialActivityCounterFinder.class.getName() + ".findAC_ByG_N_S_E_1";

	public static final String FIND_AC_BY_G_N_S_E_2 =
		SocialActivityCounterFinder.class.getName() + ".findAC_ByG_N_S_E_2";

	public static final String FIND_AC_BY_G_C_C_N_S_E =
		SocialActivityCounterFinder.class.getName() + ".findAC_By_G_C_C_N_S_E";

	public static final String FIND_U_BY_G_C_N_S_E =
		SocialActivityCounterFinder.class.getName() + ".findU_ByG_C_N_S_E";

	@Override
	public int countU_ByG_N(long groupId, String[] names) {
		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_U_BY_G_C_N_S_E);

			sql = StringUtil.replace(sql, "[$NAME$]", getNames(names));

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(PortalUtil.getClassNameId(User.class.getName()));

			setNames(queryPos, names);

			queryPos.add(SocialCounterPeriodUtil.getPeriodLength());
			queryPos.add(SocialCounterPeriodUtil.getActivityDay());

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
	public List<SocialActivityCounter> findAC_ByG_N_S_E_1(
		long groupId, String name, int startPeriod, int endPeriod,
		int periodLength) {

		String key = StringBundler.concat(
			groupId, StringPool.POUND, name, StringPool.POUND, startPeriod,
			StringPool.POUND, endPeriod, StringPool.POUND, periodLength);

		List<SocialActivityCounter> activityCounters = null;

		if (endPeriod < SocialCounterPeriodUtil.getActivityDay()) {
			activityCounters =
				(List<SocialActivityCounter>)_activityCountersPortalCache.get(
					key);
		}

		if (activityCounters != null) {
			return activityCounters;
		}

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_AC_BY_G_N_S_E_1);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(name);
			queryPos.add(startPeriod);
			queryPos.add(endPeriod);
			queryPos.add(periodLength);
			queryPos.add(endPeriod);

			activityCounters = new ArrayList<>();

			Iterator<Object[]> iterator = sqlQuery.iterate();

			while (iterator.hasNext()) {
				Object[] array = iterator.next();

				SocialActivityCounter activityCounter =
					new SocialActivityCounterImpl();

				activityCounter.setName(GetterUtil.getString(array[0]));
				activityCounter.setCurrentValue(
					GetterUtil.getInteger(array[1]));
				activityCounter.setStartPeriod(GetterUtil.getInteger(array[2]));
				activityCounter.setEndPeriod(GetterUtil.getInteger(array[3]));

				activityCounters.add(activityCounter);
			}
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			if (activityCounters == null) {
				_activityCountersPortalCache.remove(key);
			}
			else {
				if (endPeriod < SocialCounterPeriodUtil.getActivityDay()) {
					_activityCountersPortalCache.put(
						key, (Serializable)activityCounters);
				}
			}

			closeSession(session);
		}

		return activityCounters;
	}

	@Override
	public List<SocialActivityCounter> findAC_ByG_N_S_E_2(
		long groupId, String counterName, int startPeriod, int endPeriod,
		int periodLength) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_AC_BY_G_N_S_E_2);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(counterName);
			queryPos.add(startPeriod);
			queryPos.add(endPeriod);
			queryPos.add(periodLength);
			queryPos.add(endPeriod);

			List<SocialActivityCounter> activityCounters = new ArrayList<>();

			Iterator<Object[]> iterator = sqlQuery.iterate();

			while (iterator.hasNext()) {
				Object[] array = iterator.next();

				SocialActivityCounter activityCounter =
					new SocialActivityCounterImpl();

				activityCounter.setClassNameId(GetterUtil.getLong(array[0]));
				activityCounter.setName(GetterUtil.getString(array[1]));
				activityCounter.setCurrentValue(
					GetterUtil.getInteger(array[2]));

				activityCounters.add(activityCounter);
			}

			return activityCounters;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<SocialActivityCounter> findAC_By_G_C_C_N_S_E(
		long groupId, List<Long> userIds, String[] names, int start, int end) {

		if (names.length == 0) {
			return null;
		}

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_AC_BY_G_C_C_N_S_E);

			sql = StringUtil.replace(
				sql, new String[] {"[$CLASS_PK$]", "[$NAME$]"},
				new String[] {StringUtil.merge(userIds), getNames(names)});

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				"SocialActivityCounter", SocialActivityCounterImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(PortalUtil.getClassNameId(User.class.getName()));

			setNames(queryPos, names);

			return (List<SocialActivityCounter>)QueryUtil.list(
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
	public List<Long> findU_ByG_N(
		long groupId, String[] names, int start, int end) {

		if (names.length == 0) {
			return null;
		}

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_U_BY_G_C_N_S_E);

			sql = StringUtil.replace(sql, "[$NAME$]", getNames(names));

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("classPK", Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(PortalUtil.getClassNameId(User.class.getName()));

			setNames(queryPos, names);

			queryPos.add(SocialCounterPeriodUtil.getStartPeriod());

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

	protected String getNames(String[] names) {
		if (names.length == 0) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler((names.length * 2) - 1);

		for (int i = 0; i < names.length; i++) {
			sb.append(StringPool.QUESTION);

			if ((i + 1) >= names.length) {
				continue;
			}

			sb.append(StringPool.COMMA);
		}

		return sb.toString();
	}

	protected void setNames(QueryPos queryPos, String[] names) {
		if (ArrayUtil.isNotEmpty(names)) {
			for (String name : names) {
				queryPos.add(name);
			}
		}
	}

	private static final PortalCache<String, Serializable>
		_activityCountersPortalCache = PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.MULTI_VM,
			SocialActivityCounterFinder.class.getName());

}