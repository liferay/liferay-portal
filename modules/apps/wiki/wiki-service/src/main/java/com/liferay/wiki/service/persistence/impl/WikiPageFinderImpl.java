/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.service.persistence.impl;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.permission.InlineSQLHelper;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.impl.WikiPageImpl;
import com.liferay.wiki.service.persistence.WikiPageFinder;

import java.sql.Timestamp;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = WikiPageFinder.class)
public class WikiPageFinderImpl
	extends WikiPageFinderBaseImpl implements WikiPageFinder {

	public static final String COUNT_BY_MODIFIED_DATE =
		WikiPageFinder.class.getName() + ".countByModifiedDate";

	public static final String COUNT_BY_G_N_H_S =
		WikiPageFinder.class.getName() + ".countByG_N_H_S";

	public static final String FIND_BY_MODIFIED_DATE =
		WikiPageFinder.class.getName() + ".findByModifiedDate";

	public static final String FIND_BY_G_N_H_S =
		WikiPageFinder.class.getName() + ".findByG_N_H_S";

	@Override
	public int countByModifiedDate(
		long groupId, long nodeId, Date modifiedDate, boolean before) {

		return doCountByModifiedDate(
			groupId, nodeId, new Timestamp(modifiedDate.getTime()), before,
			false);
	}

	@Override
	public int filterCountByG_N_H_S(
		long groupId, long nodeId, boolean head,
		QueryDefinition<WikiPage> queryDefinition) {

		return doCountByG_N_H_S(groupId, nodeId, head, queryDefinition, true);
	}

	@Override
	public List<WikiPage> filterFindByG_N_H_S(
		long groupId, long nodeId, boolean head,
		QueryDefinition<WikiPage> queryDefinition) {

		return doFindByG_N_H_S(groupId, nodeId, head, queryDefinition, true);
	}

	@Override
	public List<WikiPage> findByModifiedDate(
		long groupId, long nodeId, Timestamp modifiedDate, boolean before,
		int start, int end) {

		return doFindByModifiedDate(
			groupId, nodeId, modifiedDate, before, start, end, false);
	}

	protected int doCountByModifiedDate(
		long groupId, long nodeId, Timestamp modifiedDate, boolean before,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_MODIFIED_DATE);

			String modifiedDateComparator = StringPool.GREATER_THAN;

			if (before) {
				modifiedDateComparator = StringPool.LESS_THAN;
			}

			sql = StringUtil.replace(
				sql, "[$MODIFIED_DATE_COMPARATOR$]", modifiedDateComparator);

			if (inlineSQLHelper) {
				sql = _inlineSQLHelper.replacePermissionCheck(
					sql, WikiPage.class.getName(), "WikiPage.resourcePrimKey",
					groupId);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(nodeId);
			queryPos.add(modifiedDate);
			queryPos.add(true);
			queryPos.add(WorkflowConstants.STATUS_APPROVED);

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

	protected int doCountByG_N_H_S(
		long groupId, long nodeId, boolean head,
		QueryDefinition<WikiPage> queryDefinition, boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), COUNT_BY_G_N_H_S, queryDefinition, "WikiPage");

			if (inlineSQLHelper) {
				sql = _inlineSQLHelper.replacePermissionCheck(
					sql, WikiPage.class.getName(), "WikiPage.resourcePrimKey",
					groupId);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(nodeId);
			queryPos.add(head);

			if (queryDefinition.getOwnerUserId() > 0) {
				queryPos.add(queryDefinition.getOwnerUserId());

				if (queryDefinition.isIncludeOwner()) {
					queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
				}
			}

			queryPos.add(queryDefinition.getStatus());

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

	protected List<WikiPage> doFindByModifiedDate(
		long groupId, long nodeId, Timestamp modifiedDate, boolean before,
		int start, int end, boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_MODIFIED_DATE);

			String modifiedDateComparator = StringPool.GREATER_THAN;

			if (before) {
				modifiedDateComparator = StringPool.LESS_THAN;
			}

			sql = StringUtil.replace(
				sql, "[$MODIFIED_DATE_COMPARATOR$]", modifiedDateComparator);

			if (inlineSQLHelper) {
				sql = _inlineSQLHelper.replacePermissionCheck(
					sql, WikiPage.class.getName(), "WikiPage.resourcePrimKey",
					groupId);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("WikiPage", WikiPageImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(nodeId);
			queryPos.add(modifiedDate);
			queryPos.add(true);
			queryPos.add(WorkflowConstants.STATUS_APPROVED);

			return (List<WikiPage>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected List<WikiPage> doFindByG_N_H_S(
		long groupId, long nodeId, boolean head,
		QueryDefinition<WikiPage> queryDefinition, boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), FIND_BY_G_N_H_S, queryDefinition, "WikiPage");

			if (inlineSQLHelper) {
				sql = _inlineSQLHelper.replacePermissionCheck(
					sql, WikiPage.class.getName(), "WikiPage.resourcePrimKey",
					groupId);
			}

			sql = _customSQL.replaceOrderBy(
				sql, queryDefinition.getOrderByComparator("WikiPage"));

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("WikiPage", WikiPageImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(nodeId);
			queryPos.add(head);

			if (queryDefinition.getOwnerUserId() > 0) {
				queryPos.add(queryDefinition.getOwnerUserId());

				if (queryDefinition.isIncludeOwner()) {
					queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
				}
			}

			queryPos.add(queryDefinition.getStatus());

			return (List<WikiPage>)QueryUtil.list(
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

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private InlineSQLHelper _inlineSQLHelper;

}