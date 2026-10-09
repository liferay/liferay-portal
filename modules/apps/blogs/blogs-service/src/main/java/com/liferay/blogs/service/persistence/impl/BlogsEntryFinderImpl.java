/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.service.persistence.impl;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.model.impl.BlogsEntryImpl;
import com.liferay.blogs.service.persistence.BlogsEntryFinder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.CalendarUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.sql.Timestamp;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = BlogsEntryFinder.class)
public class BlogsEntryFinderImpl
	extends BlogsEntryFinderBaseImpl implements BlogsEntryFinder {

	public static final String COUNT_BY_ORGANIZATION_IDS =
		BlogsEntryFinder.class.getName() + ".countByOrganizationIds";

	public static final String FIND_BY_GROUP_IDS =
		BlogsEntryFinder.class.getName() + ".findByGroupIds";

	public static final String FIND_BY_ORGANIZATION_IDS =
		BlogsEntryFinder.class.getName() + ".findByOrganizationIds";

	public static final String FIND_BY_NO_ASSETS =
		BlogsEntryFinder.class.getName() + ".findByNoAssets";

	@Override
	public int countByOrganizationId(
		long organizationId, Date displayDate,
		QueryDefinition<BlogsEntry> queryDefinition) {

		return countByOrganizationIds(
			ListUtil.fromArray(organizationId), displayDate, queryDefinition);
	}

	@Override
	public int countByOrganizationIds(
		List<Long> organizationIds, Date displayDate,
		QueryDefinition<BlogsEntry> queryDefinition) {

		Timestamp displayDate_TS = CalendarUtil.getTimestamp(displayDate);

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_BY_ORGANIZATION_IDS);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				if (queryDefinition.isExcludeStatus()) {
					sql = _customSQL.appendCriteria(
						sql, "AND (BlogsEntry.status != ?)");
				}
				else {
					sql = _customSQL.appendCriteria(
						sql, "AND (BlogsEntry.status = ?)");
				}
			}

			sql = StringUtil.replace(
				sql, "[$ORGANIZATION_ID$]",
				getOrganizationIds(organizationIds));

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			for (Long organizationId : organizationIds) {
				queryPos.add(organizationId);
			}

			queryPos.add(displayDate_TS);

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
	public List<BlogsEntry> findByGroupIds(
		long companyId, long groupId, Date displayDate,
		QueryDefinition<BlogsEntry> queryDefinition) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_GROUP_IDS);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				if (queryDefinition.isExcludeStatus()) {
					sql = _customSQL.appendCriteria(
						sql, "AND (BlogsEntry.status != ?)");
				}
				else {
					sql = _customSQL.appendCriteria(
						sql, "AND (BlogsEntry.status = ?)");
				}
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("BlogsEntry", BlogsEntryImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);
			queryPos.add(groupId);
			queryPos.add(groupId);
			queryPos.add(groupId);
			queryPos.add(displayDate);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				queryPos.add(queryDefinition.getStatus());
			}

			return (List<BlogsEntry>)QueryUtil.list(
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
	public List<BlogsEntry> findByOrganizationId(
		long organizationId, Date displayDate,
		QueryDefinition<BlogsEntry> queryDefinition) {

		return findByOrganizationIds(
			ListUtil.fromArray(organizationId), displayDate, queryDefinition);
	}

	@Override
	public List<BlogsEntry> findByOrganizationIds(
		List<Long> organizationIds, Date displayDate,
		QueryDefinition<BlogsEntry> queryDefinition) {

		Timestamp displayDate_TS = CalendarUtil.getTimestamp(displayDate);

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_ORGANIZATION_IDS);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				if (queryDefinition.isExcludeStatus()) {
					sql = _customSQL.appendCriteria(
						sql, "AND (BlogsEntry.status != ?)");
				}
				else {
					sql = _customSQL.appendCriteria(
						sql, "AND (BlogsEntry.status = ?)");
				}
			}

			sql = StringUtil.replace(
				sql, "[$ORGANIZATION_ID$]",
				getOrganizationIds(organizationIds));
			sql = _customSQL.replaceOrderBy(
				sql, queryDefinition.getOrderByComparator());

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("BlogsEntry", BlogsEntryImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			for (Long organizationId : organizationIds) {
				queryPos.add(organizationId);
			}

			queryPos.add(displayDate_TS);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				queryPos.add(queryDefinition.getStatus());
			}

			return (List<BlogsEntry>)QueryUtil.list(
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

	protected String getOrganizationIds(List<Long> organizationIds) {
		if (organizationIds.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler((organizationIds.size() * 2) - 1);

		for (int i = 0; i < organizationIds.size(); i++) {
			sb.append("Users_Orgs.organizationId = ? ");

			if ((i + 1) == organizationIds.size()) {
				continue;
			}

			sb.append("OR ");
		}

		return sb.toString();
	}

	@Reference
	private CustomSQL _customSQL;

}