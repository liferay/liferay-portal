/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.persistence.impl;

import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.persistence.KBArticlePersistence;
import com.liferay.knowledge.base.service.persistence.KBFolderFinder;
import com.liferay.knowledge.base.service.persistence.KBFolderPersistence;
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
import com.liferay.portal.kernel.security.permission.InlineSQLHelper;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(service = KBFolderFinder.class)
public class KBFolderFinderImpl
	extends KBFolderFinderBaseImpl implements KBFolderFinder {

	public static final String COUNT_A_BY_G_P =
		KBFolderFinder.class.getName() + ".countA_ByG_P";

	public static final String COUNT_F_BY_G_P =
		KBFolderFinder.class.getName() + ".countF_ByG_P";

	public static final String FIND_A_BY_G_P =
		KBFolderFinder.class.getName() + ".findA_ByG_P";

	public static final String FIND_A_BY_G_P_VC =
		KBFolderFinder.class.getName() + ".findA_ByG_P_VC";

	public static final String FIND_F_BY_G_P =
		KBFolderFinder.class.getName() + ".findF_ByG_P";

	@Override
	public int countF_A_ByG_P(
		long groupId, long parentResourcePrimKey,
		QueryDefinition<?> queryDefinition) {

		return doCountF_A_ByG_P(
			groupId, parentResourcePrimKey, queryDefinition, false);
	}

	@Override
	public int filterCountF_A_ByG_P(
		long groupId, long parentResourcePrimKey,
		QueryDefinition<?> queryDefinition) {

		return doCountF_A_ByG_P(
			groupId, parentResourcePrimKey, queryDefinition, true);
	}

	@Override
	public List<Object> filterFindF_A_ByG_P(
		long groupId, long parentResourcePrimKey,
		QueryDefinition<?> queryDefinition) {

		return doFindF_A_ByG_P(
			groupId, parentResourcePrimKey, queryDefinition, true);
	}

	@Override
	public List<Object> findF_A_ByG_P(
		long groupId, long parentResourcePrimKey,
		QueryDefinition<?> queryDefinition) {

		return doFindF_A_ByG_P(
			groupId, parentResourcePrimKey, queryDefinition, false);
	}

	protected int doCountF_A_ByG_P(
		long groupId, long parentResourcePrimKey,
		QueryDefinition<?> queryDefinition, boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			StringBundler sb = new StringBundler(5);

			sb.append(StringPool.OPEN_PARENTHESIS);

			String sql = _customSQL.get(
				getClass(), COUNT_A_BY_G_P, queryDefinition);

			if (inlineSQLHelper) {
				sql = _inlineSQLHelper.replacePermissionCheck(
					sql, KBArticle.class.getName(), "KBArticle.kbArticleId",
					groupId);
			}

			sb.append(sql);
			sb.append(") UNION ALL (");

			QueryDefinition<?> kbFolderQueryDefinition =
				_getKBFolderQueryDefinition(queryDefinition);

			sql = _customSQL.get(
				getClass(), COUNT_F_BY_G_P, kbFolderQueryDefinition);

			if (inlineSQLHelper) {
				sql = _inlineSQLHelper.replacePermissionCheck(
					sql, KBFolder.class.getName(), "KBFolder.kbFolderId",
					groupId);
			}

			sb.append(sql);
			sb.append(StringPool.CLOSE_PARENTHESIS);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(
				sb.toString());

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(parentResourcePrimKey);
			queryPos.add(true);
			queryPos.add(queryDefinition.getStatus());
			queryPos.add(groupId);
			queryPos.add(parentResourcePrimKey);
			queryPos.add(kbFolderQueryDefinition.getStatus());

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

	protected List<Object> doFindF_A_ByG_P(
		long groupId, long parentResourcePrimKey,
		QueryDefinition<?> queryDefinition, boolean inlineSQLHelper) {

		boolean orderByViewCount = false;

		OrderByComparator<?> orderByComparator =
			queryDefinition.getOrderByComparator();

		if ((orderByComparator != null) &&
			ArrayUtil.contains(
				orderByComparator.getOrderByFields(), "viewCount")) {

			orderByViewCount = true;
		}

		Session session = null;

		try {
			session = openSession();

			StringBundler sb = new StringBundler(5);

			sb.append("SELECT * FROM (");

			String sql = null;

			if (orderByViewCount) {
				sql = _customSQL.get(
					getClass(), FIND_A_BY_G_P_VC, queryDefinition);
			}
			else {
				sql = _customSQL.get(
					getClass(), FIND_A_BY_G_P, queryDefinition);
			}

			if (inlineSQLHelper) {
				sql = _inlineSQLHelper.replacePermissionCheck(
					sql, KBArticle.class.getName(), "KBArticle.kbArticleId",
					groupId);
			}

			sb.append(sql);
			sb.append(" UNION ALL ");

			QueryDefinition<?> kbFolderQueryDefinition =
				_getKBFolderQueryDefinition(queryDefinition);

			sql = _customSQL.get(
				getClass(), FIND_F_BY_G_P, kbFolderQueryDefinition);

			if (inlineSQLHelper) {
				sql = _inlineSQLHelper.replacePermissionCheck(
					sql, KBFolder.class.getName(), "KBFolder.kbFolderId",
					groupId);
			}

			sb.append(sql);
			sb.append(") TEMP_TABLE ORDER BY modelFolder DESC");

			sql = sb.toString();

			sql = _customSQL.replaceOrderBy(sql, orderByComparator);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("modelId", Type.LONG);
			sqlQuery.addScalar("modelFolder", Type.LONG);
			sqlQuery.addScalar("modifiedDate", Type.DATE);
			sqlQuery.addScalar("priority", Type.DOUBLE);
			sqlQuery.addScalar("title", Type.STRING);
			sqlQuery.addScalar("viewCount", Type.INTEGER);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (orderByViewCount) {
				long classNameId = _classNameLocalService.getClassNameId(
					KBArticle.class);

				queryPos.add(classNameId);

				queryPos.add(groupId);
				queryPos.add(parentResourcePrimKey);
				queryPos.add(true);
				queryPos.add(queryDefinition.getStatus());
				queryPos.add(classNameId);
			}

			queryPos.add(groupId);
			queryPos.add(parentResourcePrimKey);
			queryPos.add(true);
			queryPos.add(queryDefinition.getStatus());
			queryPos.add(groupId);
			queryPos.add(parentResourcePrimKey);
			queryPos.add(kbFolderQueryDefinition.getStatus());

			List<Object> models = new ArrayList<>();

			Iterator<Object[]> iterator = (Iterator<Object[]>)QueryUtil.iterate(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());

			while (iterator.hasNext()) {
				Object[] array = iterator.next();

				long modelId = (Long)array[0];
				long modelFolder = (Long)array[1];

				Object object = null;

				if (modelFolder == 1) {
					object = _kBFolderPersistence.findByPrimaryKey(modelId);
				}
				else {
					object = _kBArticlePersistence.findByPrimaryKey(modelId);
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

	private QueryDefinition<?> _getKBFolderQueryDefinition(
		QueryDefinition<?> queryDefinition) {

		QueryDefinition<?> kbFolderQueryDefinition = new QueryDefinition<>();

		kbFolderQueryDefinition.setStatus(
			queryDefinition.getStatus(), queryDefinition.isExcludeStatus());

		if (!queryDefinition.isExcludeStatus() &&
			(queryDefinition.getStatus() !=
				WorkflowConstants.STATUS_APPROVED) &&
			(queryDefinition.getStatus() !=
				WorkflowConstants.STATUS_IN_TRASH)) {

			kbFolderQueryDefinition.setStatus(
				WorkflowConstants.STATUS_APPROVED);
		}

		return kbFolderQueryDefinition;
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private InlineSQLHelper _inlineSQLHelper;

	@Reference
	private KBArticlePersistence _kBArticlePersistence;

	@Reference
	private KBFolderPersistence _kBFolderPersistence;

}