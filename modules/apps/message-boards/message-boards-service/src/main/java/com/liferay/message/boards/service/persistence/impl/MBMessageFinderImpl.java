/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.impl;

import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.impl.MBMessageImpl;
import com.liferay.message.boards.service.persistence.MBMessageFinder;
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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = MBMessageFinder.class)
public class MBMessageFinderImpl
	extends MBMessageFinderBaseImpl implements MBMessageFinder {

	public static final String COUNT_BY_PARENT_MESSAGE_ID =
		MBMessageFinder.class.getName() + ".countByParentMessageId";

	public static final String COUNT_BY_G_U_C_S =
		MBMessageFinder.class.getName() + ".countByG_U_C_S";

	public static final String COUNT_BY_G_U_C_A_S =
		MBMessageFinder.class.getName() + ".countByG_U_C_A_S";

	public static final String COUNT_BY_G_U_MD_C_A_S =
		MBMessageFinder.class.getName() + ".countByG_U_MD_C_A_S";

	public static final String FIND_BY_PARENT_MESSAGE_ID =
		MBMessageFinder.class.getName() + ".findByParentMessageId";

	public static final String FIND_BY_THREAD_ID =
		MBMessageFinder.class.getName() + ".findByThreadId";

	public static final String FIND_BY_G_U_C_S =
		MBMessageFinder.class.getName() + ".findByG_U_C_S";

	public static final String FIND_BY_G_U_C_A_S =
		MBMessageFinder.class.getName() + ".findByG_U_C_A_S";

	public static final String FIND_BY_G_U_MD_C_A_S =
		MBMessageFinder.class.getName() + ".findByG_U_MD_C_A_S";

	@Override
	public int countByParentMessageId(
		long parentMessageId, boolean flatten,
		QueryDefinition<MBMessage> queryDefinition) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), COUNT_BY_PARENT_MESSAGE_ID, queryDefinition,
				MBMessageImpl.TABLE_NAME);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(flatten);
			queryPos.add(parentMessageId);
			queryPos.add(flatten);
			queryPos.add("%/" + parentMessageId + "/_%");
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
		finally {
			closeSession(session);
		}
	}

	@Override
	public int filterCountByG_U_C_S(
		long groupId, long userId, long[] categoryIds, int status) {

		return _countByG_U_C_S(groupId, userId, categoryIds, status);
	}

	@Override
	public int filterCountByG_U_C_A_S(
		long groupId, long userId, long[] categoryIds, boolean anonymous,
		int status) {

		return _countByG_U_C_A_S(
			groupId, userId, categoryIds, anonymous, status);
	}

	@Override
	public int filterCountByG_U_MD_C_S(
		long groupId, long userId, Date modifiedDate, long[] categoryIds,
		int status) {

		return _countByG_U_MD_C_A_S(
			groupId, userId, modifiedDate, categoryIds, true, status);
	}

	@Override
	public int filterCountByG_U_MD_C_A_S(
		long groupId, long userId, Date modifiedDate, long[] categoryIds,
		boolean anonymous, int status) {

		return _countByG_U_MD_C_A_S(
			groupId, userId, modifiedDate, categoryIds, anonymous, status);
	}

	@Override
	public List<Long> filterFindByG_U_C_S(
		long groupId, long userId, long[] categoryIds, int status, int start,
		int end) {

		return _findByG_U_C_S(groupId, userId, categoryIds, status, start, end);
	}

	@Override
	public List<Long> filterFindByG_U_C_A_S(
		long groupId, long userId, long[] categoryIds, boolean anonymous,
		int status, int start, int end) {

		return _findByG_U_C_A_S(
			groupId, userId, categoryIds, anonymous, status, start, end);
	}

	@Override
	public List<Long> filterFindByG_U_MD_C_S(
		long groupId, long userId, Date modifiedDate, long[] categoryIds,
		int status, int start, int end) {

		return _findByG_U_MD_C_A_S(
			groupId, userId, modifiedDate, categoryIds, true, status, start,
			end);
	}

	@Override
	public List<Long> filterFindByG_U_MD_C_A_S(
		long groupId, long userId, Date modifiedDate, long[] categoryIds,
		boolean anonymous, int status, int start, int end) {

		return _findByG_U_MD_C_A_S(
			groupId, userId, modifiedDate, categoryIds, anonymous, status,
			start, end);
	}

	@Override
	public List<MBMessage> findByParentMessageId(
		long parentMessageId, boolean flatten,
		QueryDefinition<MBMessage> queryDefinition) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), FIND_BY_PARENT_MESSAGE_ID, queryDefinition,
				MBMessageImpl.TABLE_NAME);

			sql = _customSQL.replaceOrderBy(
				sql, queryDefinition.getOrderByComparator());

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(MBMessageImpl.TABLE_NAME, MBMessageImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(flatten);
			queryPos.add(parentMessageId);
			queryPos.add(flatten);
			queryPos.add("%/" + parentMessageId + "/_%");
			queryPos.add(queryDefinition.getStatus());

			if (queryDefinition.getOwnerUserId() > 0) {
				queryPos.add(queryDefinition.getOwnerUserId());

				if (queryDefinition.isIncludeOwner()) {
					queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
				}
			}

			return (List<MBMessage>)QueryUtil.list(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<MBMessage> findByThreadId(
		long threadId, QueryDefinition<MBMessage> queryDefinition) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), FIND_BY_THREAD_ID, queryDefinition,
				MBMessageImpl.TABLE_NAME);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(MBMessageImpl.TABLE_NAME, MBMessageImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(threadId);
			queryPos.add(queryDefinition.getStatus());

			if (queryDefinition.getOwnerUserId() > 0) {
				queryPos.add(queryDefinition.getOwnerUserId());

				if (queryDefinition.isIncludeOwner()) {
					queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
				}
			}

			return (List<MBMessage>)QueryUtil.list(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());
		}
		finally {
			closeSession(session);
		}
	}

	private int _countByG_U_C_S(
		long groupId, long userId, long[] categoryIds, int status) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_G_U_C_S);

			if (userId <= 0) {
				sql = StringUtil.removeSubstring(sql, _USER_ID_SQL);
			}

			if (ArrayUtil.isEmpty(categoryIds)) {
				sql = StringUtil.removeSubstring(
					sql, "(currentMessage.categoryId = ?) AND");
			}
			else {
				String mergedCategoryIds = StringUtil.merge(
					categoryIds, " OR currentMessage.categoryId = ");

				sql = StringUtil.replace(
					sql, "currentMessage.categoryId = ?",
					"currentMessage.categoryId = " + mergedCategoryIds);
			}

			if (status != WorkflowConstants.STATUS_ANY) {
				sql = _customSQL.appendCriteria(
					sql, "AND (currentMessage.status = ?)");
			}

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, MBMessage.class.getName(), "currentMessage.rootMessageId",
				groupId);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (userId > 0) {
				queryPos.add(userId);
			}

			if (status != WorkflowConstants.STATUS_ANY) {
				queryPos.add(status);
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

	private int _countByG_U_C_A_S(
		long groupId, long userId, long[] categoryIds, boolean anonymous,
		int status) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_G_U_C_A_S);

			if (ArrayUtil.isEmpty(categoryIds)) {
				sql = StringUtil.removeSubstring(
					sql, "(currentMessage.categoryId = ?) AND");
			}
			else {
				String mergedCategoryIds = StringUtil.merge(
					categoryIds, " OR currentMessage.categoryId = ");

				sql = StringUtil.replace(
					sql, "currentMessage.categoryId = ?",
					"currentMessage.categoryId = " + mergedCategoryIds);
			}

			if (status != WorkflowConstants.STATUS_ANY) {
				sql = _customSQL.appendCriteria(
					sql, "AND (currentMessage.status = ?)");
			}

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, MBMessage.class.getName(), "currentMessage.rootMessageId",
				groupId);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(userId);
			queryPos.add(anonymous);

			if (status != WorkflowConstants.STATUS_ANY) {
				queryPos.add(status);
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

	private int _countByG_U_MD_C_A_S(
		long groupId, long userId, Date modifiedDate, long[] categoryIds,
		boolean anonymous, int status) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_G_U_MD_C_A_S);

			if (userId <= 0) {
				sql = StringUtil.removeSubstring(sql, _USER_ID_SQL);
			}

			if (ArrayUtil.isEmpty(categoryIds)) {
				sql = StringUtil.removeSubstring(
					sql, "(currentMessage.categoryId = ?) AND");
			}
			else {
				String mergedCategoryIds = StringUtil.merge(
					categoryIds, " OR currentMessage.categoryId = ");

				sql = StringUtil.replace(
					sql, "currentMessage.categoryId = ?",
					"currentMessage.categoryId = " + mergedCategoryIds);
			}

			if (status != WorkflowConstants.STATUS_ANY) {
				sql = _customSQL.appendCriteria(
					sql, "AND (currentMessage.status = ?)");
			}

			if (!anonymous) {
				sql = _customSQL.appendCriteria(
					sql, "AND (currentMessage.anonymous = [$FALSE$])");
			}

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, MBMessage.class.getName(), "currentMessage.rootMessageId",
				groupId);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (userId > 0) {
				queryPos.add(userId);
			}

			queryPos.add(modifiedDate);

			if (status != WorkflowConstants.STATUS_ANY) {
				queryPos.add(status);
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

	private List<Long> _findByG_U_C_S(
		long groupId, long userId, long[] categoryIds, int status, int start,
		int end) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_G_U_C_S);

			if (userId <= 0) {
				sql = StringUtil.removeSubstring(sql, _USER_ID_SQL);
			}

			if (ArrayUtil.isEmpty(categoryIds)) {
				sql = StringUtil.removeSubstring(
					sql, "(currentMessage.categoryId = ?) AND");
			}
			else {
				String mergedCategoryIds = StringUtil.merge(
					categoryIds, " OR currentMessage.categoryId = ");

				sql = StringUtil.replace(
					sql, "currentMessage.categoryId = ?",
					"currentMessage.categoryId = " + mergedCategoryIds);
			}

			if (status != WorkflowConstants.STATUS_ANY) {
				sql = _customSQL.appendCriteria(
					sql, "AND (currentMessage.status = ?)");
			}

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, MBMessage.class.getName(), "currentMessage.rootMessageId",
				groupId);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("threadId", Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (userId > 0) {
				queryPos.add(userId);
			}

			if (status != WorkflowConstants.STATUS_ANY) {
				queryPos.add(status);
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

	private List<Long> _findByG_U_C_A_S(
		long groupId, long userId, long[] categoryIds, boolean anonymous,
		int status, int start, int end) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_G_U_C_A_S);

			if (ArrayUtil.isEmpty(categoryIds)) {
				sql = StringUtil.removeSubstring(
					sql, "(currentMessage.categoryId = ?) AND");
			}
			else {
				String mergedCategoryIds = StringUtil.merge(
					categoryIds, " OR currentMessage.categoryId = ");

				sql = StringUtil.replace(
					sql, "currentMessage.categoryId = ?",
					"currentMessage.categoryId = " + mergedCategoryIds);
			}

			if (status != WorkflowConstants.STATUS_ANY) {
				sql = _customSQL.appendCriteria(
					sql, "AND (currentMessage.status = ?)");
			}

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, MBMessage.class.getName(), "currentMessage.rootMessageId",
				groupId);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("threadId", Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(userId);
			queryPos.add(anonymous);

			if (status != WorkflowConstants.STATUS_ANY) {
				queryPos.add(status);
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

	private List<Long> _findByG_U_MD_C_A_S(
		long groupId, long userId, Date modifiedDate, long[] categoryIds,
		boolean anonymous, int status, int start, int end) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_G_U_MD_C_A_S);

			if (userId <= 0) {
				sql = StringUtil.removeSubstring(sql, _USER_ID_SQL);
			}

			if (ArrayUtil.isEmpty(categoryIds)) {
				sql = StringUtil.removeSubstring(
					sql, "(currentMessage.categoryId = ?) AND");
			}
			else {
				String mergedCategoryIds = StringUtil.merge(
					categoryIds, " OR currentMessage.categoryId = ");

				sql = StringUtil.replace(
					sql, "currentMessage.categoryId = ?",
					"currentMessage.categoryId = " + mergedCategoryIds);
			}

			if (status != WorkflowConstants.STATUS_ANY) {
				sql = _customSQL.appendCriteria(
					sql, "AND (currentMessage.status = ?)");
			}

			if (!anonymous) {
				sql = _customSQL.appendCriteria(
					sql, "AND (currentMessage.anonymous = [$FALSE$])");
			}

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, MBMessage.class.getName(), "currentMessage.rootMessageId",
				groupId);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("threadId", Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (userId > 0) {
				queryPos.add(userId);
			}

			queryPos.add(modifiedDate);

			if (status != WorkflowConstants.STATUS_ANY) {
				queryPos.add(status);
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

	private static final String _USER_ID_SQL =
		"AND (currentMessage.userId = ?)";

	@Reference
	private CustomSQL _customSQL;

}