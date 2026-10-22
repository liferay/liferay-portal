/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.impl;

import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.model.impl.MBThreadImpl;
import com.liferay.message.boards.service.persistence.MBThreadFinder;
import com.liferay.message.boards.service.persistence.MBThreadUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
@Component(service = MBThreadFinder.class)
public class MBThreadFinderImpl
	extends MBThreadFinderBaseImpl implements MBThreadFinder {

	public static final String COUNT_BY_G_U =
		MBThreadFinder.class.getName() + ".countByG_U";

	public static final String COUNT_BY_G_C =
		MBThreadFinder.class.getName() + ".countByG_C";

	public static final String COUNT_BY_G_U_LPD =
		MBThreadFinder.class.getName() + ".countByG_U_LPD";

	public static final String COUNT_BY_G_U_A =
		MBThreadFinder.class.getName() + ".countByG_U_A";

	public static final String COUNT_BY_S_G_U =
		MBThreadFinder.class.getName() + ".countByS_G_U";

	public static final String COUNT_BY_S_G_U_C =
		MBThreadFinder.class.getName() + ".countByS_G_U_C";

	public static final String FIND_BY_G_U =
		MBThreadFinder.class.getName() + ".findByG_U";

	public static final String FIND_BY_G_C =
		MBThreadFinder.class.getName() + ".findByG_C";

	public static final String FIND_BY_G_U_LPD =
		MBThreadFinder.class.getName() + ".findByG_U_LPD";

	public static final String FIND_BY_G_U_A =
		MBThreadFinder.class.getName() + ".findByG_U_A";

	public static final String FIND_BY_S_G_U =
		MBThreadFinder.class.getName() + ".findByS_G_U";

	public static final String FIND_BY_S_G_U_C =
		MBThreadFinder.class.getName() + ".findByS_G_U_C";

	@Override
	public int countByG_U(
		long groupId, long userId, QueryDefinition<MBThread> queryDefinition) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_G_U);

			sql = _updateSQL(sql, queryDefinition);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(userId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				queryPos.add(queryDefinition.getStatus());
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
	public int countByG_U_LPD(
		long groupId, long userId, Date lastPostDate,
		QueryDefinition<MBThread> queryDefinition) {

		return countByG_U_LPD_A(
			groupId, userId, lastPostDate, true, queryDefinition);
	}

	@Override
	public int countByG_U_A(
		long groupId, long userId, boolean anonymous,
		QueryDefinition<MBThread> queryDefinition) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_G_U_A);

			sql = _updateSQL(sql, queryDefinition);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(userId);
			queryPos.add(anonymous);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				queryPos.add(queryDefinition.getStatus());
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
	public int countByS_G_U(
		long groupId, long userId, QueryDefinition<MBThread> queryDefinition) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_S_G_U);

			sql = _updateSQL(sql, queryDefinition);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(_portal.getClassNameId(MBThread.class.getName()));
			queryPos.add(groupId);
			queryPos.add(userId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				queryPos.add(queryDefinition.getStatus());
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
	public int countByG_U_LPD_A(
		long groupId, long userId, Date lastPostDate, boolean includeAnonymous,
		QueryDefinition<MBThread> queryDefinition) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_G_U_LPD);

			if (userId <= 0) {
				sql = StringUtil.replace(
					sql, "DISTINCT MBThread.threadId", StringPool.STAR);
				sql = StringUtil.removeSubstring(sql, _INNER_JOIN_SQL);
				sql = StringUtil.removeSubstring(sql, _USER_ID_SQL);
			}

			sql = _updateSQL(sql, queryDefinition);

			if (!includeAnonymous && (userId > 0)) {
				sql = _customSQL.appendCriteria(
					sql, "AND (MBMessage.anonymous = [$FALSE$])");
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(lastPostDate);

			if (userId > 0) {
				queryPos.add(userId);
			}

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				queryPos.add(queryDefinition.getStatus());
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
	public int filterCountByG_C(long groupId, long categoryId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return MBThreadUtil.countByG_C(groupId, categoryId);
		}

		Session session = null;

		try {
			session = openSession();

			QueryDefinition<?> queryDefinition = new QueryDefinition(
				WorkflowConstants.STATUS_ANY);

			String sql = _customSQL.get(
				getClass(), COUNT_BY_G_C, queryDefinition,
				MBThreadImpl.TABLE_NAME);

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, MBMessage.class.getName(), "MBThread.rootMessageId",
				groupId);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(categoryId);
			queryPos.add(WorkflowConstants.STATUS_ANY);

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
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public int filterCountByG_C(
		long groupId, long categoryId,
		QueryDefinition<MBThread> queryDefinition) {

		return _countByG_C(groupId, categoryId, queryDefinition);
	}

	@Override
	public int filterCountByS_G_U_C(
		long groupId, long userId, long[] categoryIds,
		QueryDefinition<MBThread> queryDefinition) {

		return _countByS_G_U_C(groupId, userId, categoryIds, queryDefinition);
	}

	@Override
	public List<MBThread> filterFindByG_C(
		long groupId, long categoryId,
		QueryDefinition<MBThread> queryDefinition) {

		return _findByG_C(groupId, categoryId, queryDefinition);
	}

	@Override
	public List<MBThread> filterFindByS_G_U_C(
		long groupId, long userId, long[] categoryIds,
		QueryDefinition<MBThread> queryDefinition) {

		return _findByS_G_U_C(groupId, userId, categoryIds, queryDefinition);
	}

	@Override
	public List<MBThread> findByG_U(
		long groupId, long userId, QueryDefinition<MBThread> queryDefinition) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_G_U);

			sql = _updateSQL(sql, queryDefinition);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("MBThread", MBThreadImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(userId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				queryPos.add(queryDefinition.getStatus());
			}

			return (List<MBThread>)QueryUtil.list(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<MBThread> findByG_U_LPD(
		long groupId, long userId, Date lastPostDate,
		QueryDefinition<MBThread> queryDefinition) {

		return findByG_U_LPD_A(
			groupId, userId, lastPostDate, true, queryDefinition);
	}

	@Override
	public List<MBThread> findByG_U_A(
		long groupId, long userId, boolean anonymous,
		QueryDefinition<MBThread> queryDefinition) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_G_U_A);

			sql = _updateSQL(sql, queryDefinition);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("MBThread", MBThreadImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(userId);
			queryPos.add(anonymous);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				queryPos.add(queryDefinition.getStatus());
			}

			return (List<MBThread>)QueryUtil.list(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<MBThread> findByS_G_U(
		long groupId, long userId, QueryDefinition<MBThread> queryDefinition) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_S_G_U);

			sql = _updateSQL(sql, queryDefinition);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("MBThread", MBThreadImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(_portal.getClassNameId(MBThread.class.getName()));
			queryPos.add(groupId);
			queryPos.add(userId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				queryPos.add(queryDefinition.getStatus());
			}

			return (List<MBThread>)QueryUtil.list(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<MBThread> findByG_U_LPD_A(
		long groupId, long userId, Date lastPostDate, boolean includeAnonymous,
		QueryDefinition<MBThread> queryDefinition) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_G_U_LPD);

			if (userId <= 0) {
				sql = StringUtil.removeSubstring(sql, "DISTINCT ");
				sql = StringUtil.removeSubstring(sql, _INNER_JOIN_SQL);
				sql = StringUtil.removeSubstring(sql, _USER_ID_SQL);
			}

			sql = _updateSQL(sql, queryDefinition);

			if (!includeAnonymous && (userId > 0)) {
				sql = _customSQL.appendCriteria(
					sql, "AND (MBMessage.anonymous = [$FALSE$])");
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("MBThread", MBThreadImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(lastPostDate);

			if (userId > 0) {
				queryPos.add(userId);
			}

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				queryPos.add(queryDefinition.getStatus());
			}

			return (List<MBThread>)QueryUtil.list(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private int _countByG_C(
		long groupId, long categoryId,
		QueryDefinition<MBThread> queryDefinition) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			if (queryDefinition.isExcludeStatus()) {
				return MBThreadUtil.countByG_C_NotS(
					groupId, categoryId, queryDefinition.getStatus());
			}

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				return MBThreadUtil.countByG_C_S(
					groupId, categoryId, queryDefinition.getStatus());
			}

			return MBThreadUtil.countByG_C(groupId, categoryId);
		}

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), COUNT_BY_G_C, queryDefinition,
				MBThreadImpl.TABLE_NAME);

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, MBMessage.class.getName(), "MBThread.rootMessageId",
				groupId);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(categoryId);
			queryPos.add(queryDefinition.getStatus());

			if (queryDefinition.getOwnerUserId() > 0) {
				queryPos.add(queryDefinition.getOwnerUserId());

				if (queryDefinition.isIncludeOwner()) {
					queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
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
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private int _countByS_G_U_C(
		long groupId, long userId, long[] categoryIds,
		QueryDefinition<MBThread> queryDefinition) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_S_G_U_C);

			if (ArrayUtil.isEmpty(categoryIds)) {
				sql = StringUtil.removeSubstring(
					sql, "(MBThread.categoryId = ?) AND");
			}
			else {
				String mergedCategoryIds = StringUtil.merge(
					categoryIds, " OR MBThread.categoryId = ");

				sql = StringUtil.replace(
					sql, "MBThread.categoryId = ?",
					"MBThread.categoryId = " + mergedCategoryIds);
			}

			sql = _updateSQL(sql, queryDefinition);

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, MBMessage.class.getName(), "MBThread.rootMessageId",
				groupId);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(_portal.getClassNameId(MBThread.class.getName()));
			queryPos.add(groupId);
			queryPos.add(userId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				queryPos.add(queryDefinition.getStatus());
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

	private List<MBThread> _findByG_C(
		long groupId, long categoryId,
		QueryDefinition<MBThread> queryDefinition) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			if (queryDefinition.isExcludeStatus()) {
				return MBThreadUtil.findByG_C_NotS(
					groupId, categoryId, queryDefinition.getStatus(),
					queryDefinition.getStart(), queryDefinition.getEnd(),
					queryDefinition.getOrderByComparator());
			}

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				return MBThreadUtil.findByG_C_S(
					groupId, categoryId, queryDefinition.getStatus(),
					queryDefinition.getStart(), queryDefinition.getEnd(),
					queryDefinition.getOrderByComparator());
			}

			return MBThreadUtil.findByG_C(
				groupId, categoryId, queryDefinition.getStart(),
				queryDefinition.getEnd(),
				queryDefinition.getOrderByComparator());
		}

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), FIND_BY_G_C, queryDefinition,
				MBThreadImpl.TABLE_NAME);

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, MBMessage.class.getName(), "MBThread.rootMessageId",
				groupId);

			sql = _customSQL.replaceOrderBy(
				sql, queryDefinition.getOrderByComparator());

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("MBThread", MBThreadImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(categoryId);
			queryPos.add(queryDefinition.getStatus());

			if (queryDefinition.getOwnerUserId() > 0) {
				queryPos.add(queryDefinition.getOwnerUserId());

				if (queryDefinition.isIncludeOwner()) {
					queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
				}
			}

			return (List<MBThread>)QueryUtil.list(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private List<MBThread> _findByS_G_U_C(
		long groupId, long userId, long[] categoryIds,
		QueryDefinition<MBThread> queryDefinition) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_S_G_U_C);

			if (ArrayUtil.isEmpty(categoryIds)) {
				sql = StringUtil.removeSubstring(
					sql, "(MBThread.categoryId = ?) AND");
			}
			else {
				String mergedCategoryIds = StringUtil.merge(
					categoryIds, " OR MBThread.categoryId = ");

				sql = StringUtil.replace(
					sql, "MBThread.categoryId = ?",
					"MBThread.categoryId = " + mergedCategoryIds);
			}

			sql = _updateSQL(sql, queryDefinition);

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, MBMessage.class.getName(), "MBThread.rootMessageId",
				groupId);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("MBThread", MBThreadImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(_portal.getClassNameId(MBThread.class.getName()));
			queryPos.add(groupId);
			queryPos.add(userId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				queryPos.add(queryDefinition.getStatus());
			}

			return (List<MBThread>)QueryUtil.list(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private String _updateSQL(
		String sql, QueryDefinition<MBThread> queryDefinition) {

		if (queryDefinition.getStatus() == WorkflowConstants.STATUS_ANY) {
			return sql;
		}

		if (queryDefinition.isExcludeStatus()) {
			return _customSQL.appendCriteria(sql, "AND (MBThread.status != ?)");
		}

		return _customSQL.appendCriteria(sql, "AND (MBThread.status = ?)");
	}

	private static final String _INNER_JOIN_SQL =
		"INNER JOIN MBMessage ON MBThread.threadId = MBMessage.threadId";

	private static final String _USER_ID_SQL = "AND (MBMessage.userId = ?)";

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private Portal _portal;

}