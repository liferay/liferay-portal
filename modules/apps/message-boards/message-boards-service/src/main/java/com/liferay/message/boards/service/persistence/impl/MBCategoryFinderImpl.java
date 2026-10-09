/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.impl;

import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.impl.MBCategoryImpl;
import com.liferay.message.boards.model.impl.MBThreadImpl;
import com.liferay.message.boards.service.persistence.MBCategoryFinder;
import com.liferay.message.boards.service.persistence.MBCategoryUtil;
import com.liferay.message.boards.service.persistence.MBThreadUtil;
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
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.subscription.model.Subscription;
import com.liferay.subscription.service.SubscriptionLocalService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 * @author Sergio González
 */
@Component(service = MBCategoryFinder.class)
public class MBCategoryFinderImpl
	extends MBCategoryFinderBaseImpl implements MBCategoryFinder {

	public static final String COUNT_C_BY_G_P =
		MBCategoryFinder.class.getName() + ".countC_ByG_P";

	public static final String COUNT_C_BY_S_G_U_P =
		MBCategoryFinder.class.getName() + ".countC_ByS_G_U_P";

	public static final String COUNT_T_BY_G_C =
		MBCategoryFinder.class.getName() + ".countT_ByG_C";

	public static final String FIND_C_BY_G_P =
		MBCategoryFinder.class.getName() + ".findC_ByG_P";

	public static final String FIND_C_BY_S_G_U_P =
		MBCategoryFinder.class.getName() + ".findC_ByS_G_U_P";

	public static final String FIND_T_BY_G_C =
		MBCategoryFinder.class.getName() + ".findT_ByG_C";

	@Override
	public int countC_ByG_P(
		long groupId, long parentCategoryId,
		QueryDefinition<?> queryDefinition) {

		return doCountC_ByG_P(
			groupId, parentCategoryId, queryDefinition, false);
	}

	@Override
	public int countC_ByS_G_U_P(
		long groupId, long userId, long[] parentCategoryIds,
		QueryDefinition<MBCategory> queryDefinition) {

		return doCountC_ByS_G_U_P(
			groupId, userId, parentCategoryIds, queryDefinition, false);
	}

	@Override
	public int countC_T_ByG_C(
		long groupId, long categoryId, QueryDefinition<?> queryDefinition) {

		return doCountC_T_ByG_C(groupId, categoryId, queryDefinition, false);
	}

	@Override
	public int filterCountC_ByG_P(
		long groupId, long parentCategoryId,
		QueryDefinition<?> queryDefinition) {

		return doCountC_ByG_P(groupId, parentCategoryId, queryDefinition, true);
	}

	@Override
	public int filterCountC_ByS_G_U_P(
		long groupId, long userId, long[] parentCategoryIds,
		QueryDefinition<MBCategory> queryDefinition) {

		return doCountC_ByS_G_U_P(
			groupId, userId, parentCategoryIds, queryDefinition, true);
	}

	@Override
	public int filterCountC_T_ByG_C(
		long groupId, long categoryId, QueryDefinition<?> queryDefinition) {

		return doCountC_T_ByG_C(groupId, categoryId, queryDefinition, true);
	}

	@Override
	public List<MBCategory> filterFindC_ByG_P(
		long groupId, long parentCategoryId,
		QueryDefinition<MBCategory> queryDefinition) {

		return doFindC_ByG_P(groupId, parentCategoryId, queryDefinition, true);
	}

	@Override
	public List<MBCategory> filterFindC_ByS_G_U_P(
		long groupId, long userId, long[] parentCategoryIds,
		QueryDefinition<MBCategory> queryDefinition) {

		return doFindC_ByS_G_U_P(
			groupId, userId, parentCategoryIds, queryDefinition, true);
	}

	@Override
	public List<Object> filterFindC_T_ByG_C(
		long groupId, long categoryId, QueryDefinition<?> queryDefinition) {

		return doFindC_T_ByG_C(groupId, categoryId, queryDefinition, true);
	}

	@Override
	public List<MBCategory> findC_ByG_P(
		long groupId, long parentCategoryId,
		QueryDefinition<MBCategory> queryDefinition) {

		return doFindC_ByG_P(groupId, parentCategoryId, queryDefinition, false);
	}

	@Override
	public List<MBCategory> findC_ByS_G_U_P(
		long groupId, long userId, long[] parentCategoryIds,
		QueryDefinition<MBCategory> queryDefinition) {

		return doFindC_ByS_G_U_P(
			groupId, userId, parentCategoryIds, queryDefinition, false);
	}

	@Override
	public List<Object> findC_T_ByG_C(
		long groupId, long categoryId, QueryDefinition<?> queryDefinition) {

		return doFindC_T_ByG_C(groupId, categoryId, queryDefinition, false);
	}

	protected int doCountC_ByG_P(
		long groupId, long parentCategoryId, QueryDefinition<?> queryDefinition,
		boolean inlineSQLHelper) {

		if (!inlineSQLHelper || !InlineSQLHelperUtil.isEnabled(groupId)) {
			if (queryDefinition.isExcludeStatus()) {
				return MBCategoryUtil.countByG_P_NotS(
					groupId, parentCategoryId, queryDefinition.getStatus());
			}

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				return MBCategoryUtil.countByG_P_S(
					groupId, parentCategoryId, queryDefinition.getStatus());
			}

			return MBCategoryUtil.countByG_P(groupId, parentCategoryId);
		}

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), COUNT_C_BY_G_P, queryDefinition,
				MBCategoryImpl.TABLE_NAME);

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, MBCategory.class.getName(), "MBCategory.categoryId",
				groupId);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(parentCategoryId);
			queryPos.add(queryDefinition.getStatus());

			if (queryDefinition.getOwnerUserId() > 0) {
				queryPos.add(queryDefinition.getOwnerUserId());

				if (queryDefinition.isIncludeOwner()) {
					queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
				}
			}

			Iterator<Long> iterator = sqlQuery.iterate();

			while (iterator.hasNext()) {
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

	protected int doCountC_ByS_G_U_P(
		long groupId, long userId, long[] parentCategoryIds,
		QueryDefinition<MBCategory> queryDefinition, boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), COUNT_C_BY_S_G_U_P);

			if (ArrayUtil.isEmpty(parentCategoryIds)) {
				sql = StringUtil.removeSubstring(
					sql, "(MBCategory.parentCategoryId = ?) AND");
			}
			else {
				String mergedParentCategoryIds = StringUtil.merge(
					parentCategoryIds, " OR MBCategory.parentCategoryId = ");

				sql = StringUtil.replace(
					sql, "MBCategory.parentCategoryId = ?",
					"MBCategory.parentCategoryId = " + mergedParentCategoryIds);
			}

			sql = updateSQL(sql, queryDefinition);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, MBCategory.class.getName(), "MBCategory.categoryId",
					groupId);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(_portal.getClassNameId(MBCategory.class.getName()));
			queryPos.add(groupId);
			queryPos.add(userId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				queryPos.add(queryDefinition.getStatus());
			}

			int count = 0;

			Iterator<Long> iterator = sqlQuery.iterate();

			if (iterator.hasNext()) {
				Long l = iterator.next();

				if (l != null) {
					count = l.intValue();
				}
			}

			Group group = _groupLocalService.getGroup(groupId);

			Subscription subscription =
				_subscriptionLocalService.fetchSubscription(
					group.getCompanyId(), userId, MBCategory.class.getName(),
					groupId);

			if (subscription != null) {
				count++;
			}

			return count;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected int doCountC_T_ByG_C(
		long groupId, long categoryId, QueryDefinition<?> queryDefinition,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			StringBundler sb = new StringBundler(5);

			sb.append(StringPool.OPEN_PARENTHESIS);

			String sql = _customSQL.get(
				getClass(), COUNT_T_BY_G_C, queryDefinition,
				MBThreadImpl.TABLE_NAME);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, MBMessage.class.getName(), "MBThread.rootMessageId",
					groupId);
			}

			sb.append(sql);
			sb.append(") UNION ALL (");

			sql = _customSQL.get(
				getClass(), COUNT_C_BY_G_P, queryDefinition,
				MBCategoryImpl.TABLE_NAME);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, MBCategory.class.getName(), "MBCategory.categoryId",
					groupId);
			}

			sb.append(sql);
			sb.append(StringPool.CLOSE_PARENTHESIS);

			sql = sb.toString();

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

			queryPos.add(groupId);
			queryPos.add(categoryId);
			queryPos.add(queryDefinition.getStatus());

			if (queryDefinition.getOwnerUserId() > 0) {
				queryPos.add(queryDefinition.getOwnerUserId());

				if (queryDefinition.isIncludeOwner()) {
					queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
				}
			}

			int count = 0;

			Iterator<Long> iterator = sqlQuery.iterate();

			while (iterator.hasNext()) {
				Long l = iterator.next();

				if (l == null) {
					continue;
				}

				count += l.intValue();
			}

			return count;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected List<MBCategory> doFindC_ByG_P(
		long groupId, long parentCategoryId,
		QueryDefinition<MBCategory> queryDefinition, boolean inlineSQLHelper) {

		if (!inlineSQLHelper || !InlineSQLHelperUtil.isEnabled(groupId)) {
			if (queryDefinition.isExcludeStatus()) {
				return MBCategoryUtil.findByG_P_NotS(
					groupId, parentCategoryId, queryDefinition.getStatus(),
					queryDefinition.getStart(), queryDefinition.getEnd(),
					queryDefinition.getOrderByComparator());
			}

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				return MBCategoryUtil.findByG_P_S(
					groupId, parentCategoryId, queryDefinition.getStatus(),
					queryDefinition.getStart(), queryDefinition.getEnd(),
					queryDefinition.getOrderByComparator());
			}

			return MBCategoryUtil.findByG_P(
				groupId, parentCategoryId, queryDefinition.getStart(),
				queryDefinition.getEnd(),
				queryDefinition.getOrderByComparator());
		}

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), FIND_C_BY_G_P, queryDefinition,
				MBCategoryImpl.TABLE_NAME);

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, MBCategory.class.getName(), "MBCategory.categoryId",
				groupId);

			sql = _customSQL.replaceOrderBy(
				sql, queryDefinition.getOrderByComparator());

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("modelId", Type.LONG);
			sqlQuery.addScalar("modelCategory", Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(parentCategoryId);
			queryPos.add(queryDefinition.getStatus());

			if (queryDefinition.getOwnerUserId() > 0) {
				queryPos.add(queryDefinition.getOwnerUserId());

				if (queryDefinition.isIncludeOwner()) {
					queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
				}
			}

			List<MBCategory> categories = new ArrayList<>();

			Iterator<Object[]> iterator = (Iterator<Object[]>)QueryUtil.iterate(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());

			while (iterator.hasNext()) {
				Object[] array = iterator.next();

				long modelId = (Long)array[0];

				MBCategory category = MBCategoryUtil.findByPrimaryKey(modelId);

				categories.add(category);
			}

			return categories;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected List<MBCategory> doFindC_ByS_G_U_P(
		long groupId, long userId, long[] parentCategoryIds,
		QueryDefinition<MBCategory> queryDefinition, boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_C_BY_S_G_U_P);

			if (ArrayUtil.isEmpty(parentCategoryIds)) {
				sql = StringUtil.removeSubstring(
					sql, "(MBCategory.parentCategoryId = ?) AND");
			}
			else {
				String mergedParentCategoryIds = StringUtil.merge(
					parentCategoryIds, " OR MBCategory.parentCategoryId = ");

				sql = StringUtil.replace(
					sql, "MBCategory.parentCategoryId = ?",
					"MBCategory.parentCategoryId = " + mergedParentCategoryIds);
			}

			sql = updateSQL(sql, queryDefinition);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, MBCategory.class.getName(), "MBCategory.categoryId",
					groupId);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("MBCategory", MBCategoryImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(_portal.getClassNameId(MBCategory.class.getName()));
			queryPos.add(groupId);
			queryPos.add(userId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				queryPos.add(queryDefinition.getStatus());
			}

			List<MBCategory> list = (List<MBCategory>)QueryUtil.list(
				sqlQuery, getDialect(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				false);

			Group group = _groupLocalService.getGroup(groupId);

			Subscription subscription =
				_subscriptionLocalService.fetchSubscription(
					group.getCompanyId(), userId, MBCategory.class.getName(),
					groupId);

			if (subscription != null) {
				MBCategory category = new MBCategoryImpl();

				category.setGroupId(group.getGroupId());
				category.setCompanyId(group.getCompanyId());
				category.setName(group.getDescriptiveName());
				category.setDescription(
					group.getDescription(LocaleUtil.getMostRelevantLocale()));

				list.add(category);
			}

			return Collections.unmodifiableList(
				ListUtil.subList(
					list, queryDefinition.getStart(),
					queryDefinition.getEnd()));
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected List<Object> doFindC_T_ByG_C(
		long groupId, long categoryId, QueryDefinition<?> queryDefinition,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			StringBundler sb = new StringBundler(6);

			sb.append("SELECT * FROM (");

			String sql = _customSQL.get(
				getClass(), FIND_T_BY_G_C, queryDefinition,
				MBThreadImpl.TABLE_NAME);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, MBMessage.class.getName(), "MBThread.rootMessageId",
					groupId);
			}

			sb.append(sql);
			sb.append(" UNION ALL ");

			sql = _customSQL.get(
				getClass(), FIND_C_BY_G_P, queryDefinition,
				MBCategoryImpl.TABLE_NAME);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, MBCategory.class.getName(), "MBCategory.categoryId",
					groupId);
			}

			sb.append(sql);
			sb.append(") TEMP_TABLE ORDER BY modelCategory ASC, priority ");
			sb.append("DESC, modelId ASC");

			sql = sb.toString();

			sql = _customSQL.replaceOrderBy(
				sql, queryDefinition.getOrderByComparator());

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("modelId", Type.LONG);
			sqlQuery.addScalar("modelCategory", Type.LONG);

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

			queryPos.add(groupId);
			queryPos.add(categoryId);
			queryPos.add(queryDefinition.getStatus());

			if (queryDefinition.getOwnerUserId() > 0) {
				queryPos.add(queryDefinition.getOwnerUserId());

				if (queryDefinition.isIncludeOwner()) {
					queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
				}
			}

			List<Object> models = new ArrayList<>();

			Iterator<Object[]> iterator = (Iterator<Object[]>)QueryUtil.iterate(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());

			while (iterator.hasNext()) {
				Object[] array = iterator.next();

				long modelId = (Long)array[0];
				long modelCategory = (Long)array[1];

				Object object = null;

				if (modelCategory == 1) {
					object = MBThreadUtil.findByPrimaryKey(modelId);
				}
				else {
					object = MBCategoryUtil.findByPrimaryKey(modelId);
				}

				models.add(object);
			}

			return models;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected String updateSQL(
		String sql, QueryDefinition<MBCategory> queryDefinition) {

		if (queryDefinition.getStatus() == WorkflowConstants.STATUS_ANY) {
			return sql;
		}

		if (queryDefinition.isExcludeStatus()) {
			return _customSQL.appendCriteria(
				sql, "AND (MBCategory.status != ?)");
		}

		return _customSQL.appendCriteria(sql, "AND (MBCategory.status = ?)");
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

}