/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microblogs.service.persistence.impl;

import com.liferay.microblogs.constants.MicroblogsEntryConstants;
import com.liferay.microblogs.model.MicroblogsEntry;
import com.liferay.microblogs.model.impl.MicroblogsEntryImpl;
import com.liferay.microblogs.service.persistence.MicroblogsEntryFinder;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.social.kernel.model.SocialRelationConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jonathan Lee
 */
@Component(service = MicroblogsEntryFinder.class)
public class MicroblogsEntryFinderImpl
	extends MicroblogsEntryFinderBaseImpl implements MicroblogsEntryFinder {

	public static final String COUNT_BY_C_U =
		MicroblogsEntryFinder.class.getName() + ".countByC_U";

	public static final String COUNT_BY_U_MU =
		MicroblogsEntryFinder.class.getName() + ".countByU_MU";

	public static final String COUNT_BY_C_U_ATN =
		MicroblogsEntryFinder.class.getName() + ".countByC_U_ATN";

	public static final String COUNT_BY_U_T_MU =
		MicroblogsEntryFinder.class.getName() + ".countByU_T_MU";

	public static final String FIND_BY_C_U =
		MicroblogsEntryFinder.class.getName() + ".findByC_U";

	public static final String FIND_BY_U_MU =
		MicroblogsEntryFinder.class.getName() + ".findByU_MU";

	public static final String FIND_BY_C_U_ATN =
		MicroblogsEntryFinder.class.getName() + ".findByC_U_ATN";

	public static final String FIND_BY_U_T_MU =
		MicroblogsEntryFinder.class.getName() + ".findByU_T_MU";

	@Override
	public int countByC_U(long companyId, long userId) {
		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_C_U);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);
			queryPos.add(MicroblogsEntryConstants.TYPE_EVERYONE);
			queryPos.add(userId);
			queryPos.add(SocialRelationConstants.TYPE_UNI_ENEMY);
			queryPos.add(userId);
			queryPos.add(userId);
			queryPos.add(MicroblogsEntryConstants.TYPE_REPLY);

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
	public int countByU_MU(long userId, long microblogsEntryUserId) {
		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_U_MU);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(MicroblogsEntryConstants.TYPE_EVERYONE);
			queryPos.add(userId);
			queryPos.add(microblogsEntryUserId);
			queryPos.add(MicroblogsEntryConstants.TYPE_REPLY);

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
	public int countByC_U_ATN(
		long companyId, long userId, String assetTagName) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_C_U_ATN);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);
			queryPos.add(MicroblogsEntryConstants.TYPE_EVERYONE);
			queryPos.add(userId);
			queryPos.add(assetTagName);
			queryPos.add(userId);
			queryPos.add(assetTagName);

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
	public int countByU_T_MU(
		long userId, int type, long microblogsEntryUserId) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_U_T_MU);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(MicroblogsEntryConstants.TYPE_EVERYONE);
			queryPos.add(userId);
			queryPos.add(type);
			queryPos.add(microblogsEntryUserId);

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
	public List<MicroblogsEntry> findByC_U(
		long companyId, long userId, int start, int end) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_C_U);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("microblogsEntryId", Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);
			queryPos.add(MicroblogsEntryConstants.TYPE_EVERYONE);
			queryPos.add(userId);
			queryPos.add(SocialRelationConstants.TYPE_UNI_ENEMY);
			queryPos.add(userId);
			queryPos.add(userId);
			queryPos.add(MicroblogsEntryConstants.TYPE_REPLY);

			Iterator<Long> iterator = (Iterator<Long>)QueryUtil.iterate(
				sqlQuery, getDialect(), start, end);

			List<MicroblogsEntry> microblogsEntries = new ArrayList<>();

			while (iterator.hasNext()) {
				microblogsEntries.add(
					microblogsEntryPersistence.fetchByPrimaryKey(
						(Long)iterator.next()));
			}

			return microblogsEntries;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<MicroblogsEntry> findByU_MU(
		long userId, long microblogsEntryUserId, int start, int end) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_U_MU);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("MicroblogsEntry", MicroblogsEntryImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(MicroblogsEntryConstants.TYPE_EVERYONE);
			queryPos.add(userId);
			queryPos.add(microblogsEntryUserId);
			queryPos.add(MicroblogsEntryConstants.TYPE_REPLY);

			return (List<MicroblogsEntry>)QueryUtil.list(
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
	public List<MicroblogsEntry> findByC_U_ATN(
		long companyId, long userId, String assetTagName, int start, int end) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_C_U_ATN);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("MicroblogsEntry", MicroblogsEntryImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);
			queryPos.add(MicroblogsEntryConstants.TYPE_EVERYONE);
			queryPos.add(userId);
			queryPos.add(assetTagName);
			queryPos.add(userId);
			queryPos.add(assetTagName);

			return (List<MicroblogsEntry>)QueryUtil.list(
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
	public List<MicroblogsEntry> findByU_T_MU(
		long userId, int type, long microblogsEntryUserId, int start, int end) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_U_T_MU);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("MicroblogsEntry", MicroblogsEntryImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(MicroblogsEntryConstants.TYPE_EVERYONE);
			queryPos.add(userId);
			queryPos.add(type);
			queryPos.add(microblogsEntryUserId);

			return (List<MicroblogsEntry>)QueryUtil.list(
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