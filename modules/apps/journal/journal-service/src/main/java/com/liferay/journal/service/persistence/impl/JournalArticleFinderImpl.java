/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.persistence.impl;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.impl.JournalArticleImpl;
import com.liferay.journal.service.persistence.JournalArticleFinder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Connor McKay
 */
@Component(service = JournalArticleFinder.class)
public class JournalArticleFinderImpl
	extends JournalArticleFinderBaseImpl implements JournalArticleFinder {

	public static final String COUNT_BY_G_F =
		JournalArticleFinder.class.getName() + ".countByG_F";

	public static final String COUNT_BY_G_ST =
		JournalArticleFinder.class.getName() + ".countByG_ST";

	public static final String COUNT_BY_G_U_F_C =
		JournalArticleFinder.class.getName() + ".countByG_U_F_C";

	public static final String COUNT_BY_G_F_C_S =
		JournalArticleFinder.class.getName() + ".countByG_F_C_S";

	public static final String FIND_BY_G_ST_L =
		JournalArticleFinder.class.getName() + ".findByG_ST_L";

	public static final String FIND_BY_G_F_L =
		JournalArticleFinder.class.getName() + ".findByG_F_L";

	public static final String FIND_BY_G_U_F_C_L =
		JournalArticleFinder.class.getName() + ".findByG_U_F_C_L";

	public static final String FIND_BY_G_F_C_S_L =
		JournalArticleFinder.class.getName() + ".findByG_F_C_S_L";

	@Override
	public int countByG_F(
		long groupId, List<Long> folderIds,
		QueryDefinition<JournalArticle> queryDefinition) {

		return doCountByG_F(groupId, folderIds, queryDefinition, false);
	}

	@Override
	public int filterCountByG_F(
		long groupId, List<Long> folderIds,
		QueryDefinition<JournalArticle> queryDefinition) {

		return doCountByG_F(groupId, folderIds, queryDefinition, true);
	}

	@Override
	public int filterCountByG_ST(
		long groupId, int status,
		QueryDefinition<JournalArticle> queryDefinition) {

		return doCountByG_ST(groupId, status, queryDefinition, true);
	}

	@Override
	public int filterCountByG_F_C(
		long groupId, List<Long> folderIds, long classNameId,
		QueryDefinition<JournalArticle> queryDefinition) {

		return doCountByG_F_C(
			groupId, folderIds, classNameId, queryDefinition, true);
	}

	@Override
	public int filterCountByG_F_C_S(
		long groupId, List<Long> folderIds, long classNameId,
		long ddmStructureId, QueryDefinition<JournalArticle> queryDefinition) {

		return doCountByG_F_C_S(
			groupId, folderIds, classNameId, ddmStructureId, queryDefinition,
			true);
	}

	@Override
	public List<JournalArticle> filterFindByG_ST_L(
		long groupId, int status, Locale locale,
		QueryDefinition<JournalArticle> queryDefinition) {

		return doFindByG_ST_L(groupId, status, locale, queryDefinition, true);
	}

	@Override
	public List<JournalArticle> filterFindByG_F_L(
		long groupId, List<Long> folderIds, Locale locale,
		QueryDefinition<JournalArticle> queryDefinition) {

		return doFindByG_F_L(groupId, folderIds, locale, queryDefinition, true);
	}

	@Override
	public List<JournalArticle> filterFindByG_F_C_L(
		long groupId, List<Long> folderIds, long classNameId, Locale locale,
		QueryDefinition<JournalArticle> queryDefinition) {

		return doFindByG_F_C_L(
			groupId, folderIds, classNameId, locale, queryDefinition, true);
	}

	@Override
	public List<JournalArticle> filterFindByG_F_C_S_L(
		long groupId, List<Long> folderIds, long classNameId,
		long ddmStructureId, Locale locale,
		QueryDefinition<JournalArticle> queryDefinition) {

		return doFindByG_C_S_L(
			groupId, folderIds, classNameId, ddmStructureId, locale,
			queryDefinition, true);
	}

	@Override
	public List<JournalArticle> findByG_F_C_S_L(
		long groupId, List<Long> folderIds, long classNameId,
		long ddmStructureId, Locale locale,
		QueryDefinition<JournalArticle> queryDefinition) {

		return doFindByG_C_S_L(
			groupId, folderIds, classNameId, ddmStructureId, locale,
			queryDefinition, false);
	}

	protected int doCountByG_F(
		long groupId, List<Long> folderIds,
		QueryDefinition<JournalArticle> queryDefinition,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), COUNT_BY_G_F, queryDefinition, "JournalArticle");

			sql = replaceStatusJoin(sql, groupId, queryDefinition);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, JournalArticle.class.getName(),
					"JournalArticle.resourcePrimKey", groupId);
			}

			sql = StringUtil.replace(
				sql, "[$FOLDER_ID$]",
				getFolderIds(folderIds, JournalArticleImpl.TABLE_NAME));

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(queryDefinition.getStatus());

			for (Long folderId : folderIds) {
				queryPos.add(folderId);
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

	protected int doCountByG_ST(
		long groupId, int status,
		QueryDefinition<JournalArticle> queryDefinition,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), COUNT_BY_G_ST, queryDefinition, "JournalArticle");

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, JournalArticle.class.getName(),
					"JournalArticle.resourcePrimKey", groupId);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(status);

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

	protected int doCountByG_F_C(
		long groupId, List<Long> folderIds, long classNameId,
		QueryDefinition<JournalArticle> queryDefinition,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), COUNT_BY_G_U_F_C, queryDefinition,
				"JournalArticle");

			sql = replaceStatusJoin(sql, groupId, queryDefinition);

			if (folderIds.isEmpty()) {
				sql = StringUtil.removeSubstring(sql, "([$FOLDER_ID$]) AND");
			}
			else {
				sql = StringUtil.replace(
					sql, "[$FOLDER_ID$]",
					getFolderIds(folderIds, JournalArticleImpl.TABLE_NAME));
			}

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, JournalArticle.class.getName(),
					"JournalArticle.resourcePrimKey", groupId);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(classNameId);

			for (long folderId : folderIds) {
				queryPos.add(folderId);
			}

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

	protected int doCountByG_F_C_S(
		long groupId, List<Long> folderIds, long classNameId,
		long ddmStructureId, QueryDefinition<JournalArticle> queryDefinition,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), COUNT_BY_G_F_C_S, queryDefinition,
				"JournalArticle");

			sql = replaceStatusJoin(sql, groupId, queryDefinition);

			if (groupId <= 0) {
				sql = StringUtil.removeSubstring(
					sql, "(JournalArticle.groupId = ?) AND");
			}

			if (folderIds.isEmpty()) {
				sql = StringUtil.removeSubstring(sql, "([$FOLDER_ID$]) AND");
			}
			else {
				sql = StringUtil.replace(
					sql, "[$FOLDER_ID$]",
					getFolderIds(folderIds, JournalArticleImpl.TABLE_NAME));
			}

			if (ddmStructureId <= 0) {
				sql = StringUtil.removeSubstring(
					sql, "(JournalArticle.DDMStructureId = ?) AND");
			}

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, JournalArticle.class.getName(),
					"JournalArticle.resourcePrimKey", groupId);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (groupId > 0) {
				queryPos.add(groupId);
			}

			queryPos.add(classNameId);

			for (Long folderId : folderIds) {
				queryPos.add(folderId);
			}

			if (ddmStructureId > 0) {
				queryPos.add(ddmStructureId);
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

	protected List<JournalArticle> doFindByG_ST_L(
		long groupId, int status, Locale locale,
		QueryDefinition<JournalArticle> queryDefinition,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), FIND_BY_G_ST_L, queryDefinition, "JournalArticle");

			OrderByComparator<JournalArticle> orderByComparator =
				queryDefinition.getOrderByComparator();

			sql = _customSQL.replaceOrderBy(sql, orderByComparator);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, JournalArticle.class.getName(),
					"JournalArticle.resourcePrimKey", groupId);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				JournalArticleImpl.TABLE_NAME, JournalArticleImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (_isOrderByTitle(orderByComparator)) {
				queryPos.add(LocaleUtil.toLanguageId(locale));
			}
			else {
				queryPos.add(StringPool.UNDERLINE);
			}

			queryPos.add(groupId);
			queryPos.add(status);

			return (List<JournalArticle>)QueryUtil.list(
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

	protected List<JournalArticle> doFindByG_F_L(
		long groupId, List<Long> folderIds, Locale locale,
		QueryDefinition<JournalArticle> queryDefinition,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), FIND_BY_G_F_L, queryDefinition, "JournalArticle");

			sql = replaceStatusJoin(sql, groupId, queryDefinition);

			OrderByComparator<JournalArticle> orderByComparator =
				queryDefinition.getOrderByComparator();

			sql = _customSQL.replaceOrderBy(sql, orderByComparator);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, JournalArticle.class.getName(),
					"JournalArticle.resourcePrimKey", groupId);
			}

			sql = StringUtil.replace(
				sql, "[$FOLDER_ID$]",
				getFolderIds(folderIds, JournalArticleImpl.TABLE_NAME));

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				JournalArticleImpl.TABLE_NAME, JournalArticleImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (_isOrderByTitle(orderByComparator)) {
				queryPos.add(LocaleUtil.toLanguageId(locale));
			}
			else {
				queryPos.add(StringPool.UNDERLINE);
			}

			queryPos.add(groupId);
			queryPos.add(queryDefinition.getStatus());

			for (Long folderId : folderIds) {
				queryPos.add(folderId);
			}

			return (List<JournalArticle>)QueryUtil.list(
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

	protected List<JournalArticle> doFindByG_C_S_L(
		long groupId, List<Long> folderIds, long classNameId,
		long ddmStructureId, Locale locale,
		QueryDefinition<JournalArticle> queryDefinition,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), FIND_BY_G_F_C_S_L, queryDefinition,
				"JournalArticle");

			sql = replaceStatusJoin(sql, groupId, queryDefinition);

			OrderByComparator<JournalArticle> orderByComparator =
				queryDefinition.getOrderByComparator();

			sql = _customSQL.replaceOrderBy(sql, orderByComparator);

			if (groupId <= 0) {
				sql = StringUtil.removeSubstring(
					sql, "(JournalArticle.groupId = ?) AND");
			}

			if (folderIds.isEmpty()) {
				sql = StringUtil.removeSubstring(sql, "([$FOLDER_ID$]) AND");
			}
			else {
				sql = StringUtil.replace(
					sql, "[$FOLDER_ID$]",
					getFolderIds(folderIds, JournalArticleImpl.TABLE_NAME));
			}

			if (ddmStructureId <= 0) {
				sql = StringUtil.removeSubstring(
					sql, "(JournalArticle.DDMStructureId = ?) AND");
			}

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, JournalArticle.class.getName(),
					"JournalArticle.resourcePrimKey", groupId);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				JournalArticleImpl.TABLE_NAME, JournalArticleImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (_isOrderByTitle(orderByComparator)) {
				queryPos.add(LocaleUtil.toLanguageId(locale));
			}
			else {
				queryPos.add(StringPool.UNDERLINE);
			}

			if (groupId > 0) {
				queryPos.add(groupId);
			}

			queryPos.add(classNameId);

			for (Long folderId : folderIds) {
				queryPos.add(folderId);
			}

			if (ddmStructureId > 0) {
				queryPos.add(ddmStructureId);
			}

			queryPos.add(queryDefinition.getStatus());

			return (List<JournalArticle>)QueryUtil.list(
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

	protected List<JournalArticle> doFindByG_F_C_L(
		long groupId, List<Long> folderIds, long classNameId, Locale locale,
		QueryDefinition<JournalArticle> queryDefinition,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(
				getClass(), FIND_BY_G_U_F_C_L, queryDefinition,
				"JournalArticle");

			sql = replaceStatusJoin(sql, groupId, queryDefinition);

			OrderByComparator<JournalArticle> orderByComparator =
				queryDefinition.getOrderByComparator();

			sql = _customSQL.replaceOrderBy(sql, orderByComparator);

			if (folderIds.isEmpty()) {
				sql = StringUtil.removeSubstring(sql, "([$FOLDER_ID$]) AND");
			}
			else {
				sql = StringUtil.replace(
					sql, "[$FOLDER_ID$]",
					getFolderIds(folderIds, JournalArticleImpl.TABLE_NAME));
			}

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, JournalArticle.class.getName(),
					"JournalArticle.resourcePrimKey", groupId);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				JournalArticleImpl.TABLE_NAME, JournalArticleImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (_isOrderByTitle(orderByComparator)) {
				queryPos.add(LocaleUtil.toLanguageId(locale));
			}
			else {
				queryPos.add(StringPool.UNDERLINE);
			}

			queryPos.add(groupId);
			queryPos.add(classNameId);

			for (long folderId : folderIds) {
				queryPos.add(folderId);
			}

			if (queryDefinition.getOwnerUserId() > 0) {
				queryPos.add(queryDefinition.getOwnerUserId());

				if (queryDefinition.isIncludeOwner()) {
					queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
				}
			}

			queryPos.add(queryDefinition.getStatus());

			return (List<JournalArticle>)QueryUtil.list(
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

	protected String getFolderIds(List<Long> folderIds, String tableName) {
		if (folderIds.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler((folderIds.size() * 3) + 1);

		sb.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 0; i < folderIds.size(); i++) {
			sb.append(tableName);
			sb.append(".folderId = ? ");

			if ((i + 1) == folderIds.size()) {
				continue;
			}

			sb.append(WHERE_OR);
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	protected String replaceStatusJoin(
		String sql, long groupId,
		QueryDefinition<JournalArticle> queryDefinition) {

		if (queryDefinition.getStatus() == WorkflowConstants.STATUS_ANY) {
			return StringUtil.removeSubstring(sql, "[$STATUS_JOIN$] AND");
		}

		Group group = null;

		try {
			group = _groupLocalService.getGroup(groupId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		if (queryDefinition.isExcludeStatus()) {
			sql = StringUtil.replace(
				sql, "[$STATUS_JOIN$]",
				StringBundler.concat(
					"(JournalArticle.status != ", queryDefinition.getStatus(),
					" AND JournalArticle.companyId = ", group.getCompanyId(),
					") AND (tempJournalArticle.companyId = ",
					group.getCompanyId(), " AND tempJournalArticle.status != ",
					queryDefinition.getStatus(), ")"));
		}
		else {
			sql = StringUtil.replace(
				sql, "[$STATUS_JOIN$]",
				StringBundler.concat(
					"(JournalArticle.status = ", queryDefinition.getStatus(),
					" AND JournalArticle.companyId = ", group.getCompanyId(),
					") AND (tempJournalArticle.companyId = ",
					group.getCompanyId(), " AND tempJournalArticle.status = ",
					queryDefinition.getStatus(), ")"));
		}

		return sql;
	}

	private boolean _isOrderByTitle(
		OrderByComparator<JournalArticle> orderByComparator) {

		if ((orderByComparator != null) &&
			(StringUtil.containsIgnoreCase(
				orderByComparator.getOrderBy(), _TITLE_FIELD,
				StringPool.COMMA) ||
			 StringUtil.containsIgnoreCase(
				 orderByComparator.getOrderBy(), _TITLE_FIELD + " ASC",
				 StringPool.COMMA) ||
			 StringUtil.containsIgnoreCase(
				 orderByComparator.getOrderBy(), _TITLE_FIELD + " DESC",
				 StringPool.COMMA))) {

			return true;
		}

		return false;
	}

	private static final String _TITLE_FIELD =
		"JournalArticleLocalization.title";

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private GroupLocalService _groupLocalService;

}