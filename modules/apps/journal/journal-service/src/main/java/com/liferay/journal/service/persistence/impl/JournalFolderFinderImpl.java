/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.persistence.impl;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.model.impl.JournalArticleImpl;
import com.liferay.journal.model.impl.JournalFolderImpl;
import com.liferay.journal.service.persistence.JournalArticleUtil;
import com.liferay.journal.service.persistence.JournalFolderFinder;
import com.liferay.journal.service.persistence.JournalFolderUtil;
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
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Juan Fernández
 * @author Zsolt Berentey
 */
@Component(service = JournalFolderFinder.class)
public class JournalFolderFinderImpl
	extends JournalFolderFinderBaseImpl implements JournalFolderFinder {

	public static final String COUNT_A_BY_G_U_F_DDMSI_NOT_S =
		JournalFolderFinder.class.getName() + ".countA_ByG_U_F_DDMSI_NotS";

	public static final String COUNT_F_BY_G_F =
		JournalFolderFinder.class.getName() + ".countF_ByG_F";

	public static final String FIND_A_BY_G_U_F_DDMSI =
		JournalFolderFinder.class.getName() + ".findA_ByG_U_F_DDMSI";

	public static final String FIND_A_BY_G_U_F_DDMSI_L_NOT_S =
		JournalFolderFinder.class.getName() + ".findA_ByG_U_F_DDMSI_L_NotS";

	public static final String FIND_F_BY_G_F =
		JournalFolderFinder.class.getName() + ".findF_ByG_F";

	public static final String FIND_F_BY_G_F_L =
		JournalFolderFinder.class.getName() + ".findF_ByG_F_L";

	@Override
	public int countF_A_ByG_F_DDMSI(
		long groupId, long folderId, long ddmStructureId,
		QueryDefinition<?> queryDefinition) {

		return doCountF_A_ByG_F_DDMSI_NotS(
			groupId, folderId, ddmStructureId, null, queryDefinition, false);
	}

	@Override
	public int filterCountF_A_ByG_F_DDMSI(
		long groupId, long folderId, long ddmStructureId,
		QueryDefinition<?> queryDefinition) {

		return doCountF_A_ByG_F_DDMSI_NotS(
			groupId, folderId, ddmStructureId, null, queryDefinition, true);
	}

	@Override
	public int filterCountF_A_ByG_F_DDMSI_NotS(
		long groupId, long folderId, long ddmStructureId,
		int[] excludedStatuses, QueryDefinition<?> queryDefinition) {

		return doCountF_A_ByG_F_DDMSI_NotS(
			groupId, folderId, ddmStructureId, excludedStatuses,
			queryDefinition, true);
	}

	@Override
	public List<Object> filterFindF_A_ByG_F_DDMSI(
		long groupId, long folderId, long ddmStructureId,
		QueryDefinition<?> queryDefinition) {

		return doFindF_A_ByG_F_DDMSI(
			groupId, folderId, ddmStructureId, queryDefinition, true);
	}

	@Override
	public List<Object> filterFindF_A_ByG_F_DDMSI_L(
		long groupId, long folderId, long ddmStructureId, Locale locale,
		QueryDefinition<?> queryDefinition) {

		return doFindF_A_ByG_F_DDMSI_L_NotS(
			groupId, folderId, ddmStructureId, locale, null, queryDefinition,
			true);
	}

	public List<Object> filterFindF_A_ByG_F_DDMSI_L_NotS(
		long groupId, long folderId, long ddmStructureId, Locale locale,
		int[] excludedStatuses, QueryDefinition<?> queryDefinition) {

		return doFindF_A_ByG_F_DDMSI_L_NotS(
			groupId, folderId, ddmStructureId, locale, excludedStatuses,
			queryDefinition, true);
	}

	@Override
	public List<Object> findF_A_ByG_F_DDMSI(
		long groupId, long folderId, long ddmStructureId,
		QueryDefinition<?> queryDefinition) {

		return doFindF_A_ByG_F_DDMSI(
			groupId, folderId, ddmStructureId, queryDefinition, false);
	}

	protected int doCountF_A_ByG_F_DDMSI_NotS(
		long groupId, long folderId, long ddmStructureId,
		int[] excludedStatuses, QueryDefinition<?> queryDefinition,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = updateSQL(
				StringBundler.concat(
					StringPool.OPEN_PARENTHESIS,
					getFoldersSQL(
						COUNT_F_BY_G_F, groupId, queryDefinition,
						inlineSQLHelper),
					") UNION ALL (",
					getArticlesSQL(
						COUNT_A_BY_G_U_F_DDMSI_NOT_S, groupId, queryDefinition,
						inlineSQLHelper),
					StringPool.CLOSE_PARENTHESIS),
				folderId, ddmStructureId, excludedStatuses);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(queryDefinition.getStatus());

			if (folderId >= 0) {
				queryPos.add(folderId);
			}

			queryPos.add(groupId);

			if (queryDefinition.getOwnerUserId() > 0) {
				queryPos.add(queryDefinition.getOwnerUserId());
				queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
			}

			queryPos.add(queryDefinition.getStatus());

			if (folderId >= 0) {
				queryPos.add(folderId);
			}

			if (ddmStructureId > 0) {
				queryPos.add(ddmStructureId);
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

	protected List<Object> doFindF_A_ByG_F_DDMSI(
		long groupId, long folderId, long ddmStructureId,
		QueryDefinition<?> queryDefinition, boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = updateSQL(
				StringBundler.concat(
					StringPool.OPEN_PARENTHESIS,
					getFoldersSQL(
						FIND_F_BY_G_F, groupId, queryDefinition,
						inlineSQLHelper),
					") UNION ALL (",
					getArticlesSQL(
						FIND_A_BY_G_U_F_DDMSI, groupId, queryDefinition,
						inlineSQLHelper),
					StringPool.CLOSE_PARENTHESIS),
				folderId, ddmStructureId, null);

			sql = _customSQL.replaceOrderBy(
				sql, queryDefinition.getOrderByComparator());

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("modelFolderId", Type.LONG);
			sqlQuery.addScalar("modelFolder", Type.LONG);
			sqlQuery.addScalar("articleId", Type.STRING);
			sqlQuery.addScalar("version", Type.DOUBLE);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(queryDefinition.getStatus());

			if (folderId >= 0) {
				queryPos.add(folderId);
			}

			queryPos.add(groupId);

			if (queryDefinition.getOwnerUserId() > 0) {
				queryPos.add(queryDefinition.getOwnerUserId());
				queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
			}

			queryPos.add(queryDefinition.getStatus());

			if (folderId >= 0) {
				queryPos.add(folderId);
			}

			if (ddmStructureId > 0) {
				queryPos.add(ddmStructureId);
			}

			List<Object> models = new ArrayList<>();

			Iterator<Object[]> iterator = (Iterator<Object[]>)QueryUtil.iterate(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());

			while (iterator.hasNext()) {
				Object[] array = iterator.next();

				long modelFolder = (Long)array[1];

				Object object = null;

				if (modelFolder == 1) {
					long curFolderId = (Long)array[0];

					object = JournalFolderUtil.findByPrimaryKey(curFolderId);
				}
				else {
					String articleId = (String)array[2];
					double version = (Double)array[3];

					object = JournalArticleUtil.findByG_A_V(
						groupId, articleId, version);
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

	protected List<Object> doFindF_A_ByG_F_DDMSI_L_NotS(
		long groupId, long folderId, long ddmStructureId, Locale locale,
		int[] excludedStatuses, QueryDefinition<?> queryDefinition,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			String sql = updateSQL(
				StringBundler.concat(
					StringPool.OPEN_PARENTHESIS,
					getFoldersSQL(
						FIND_F_BY_G_F_L, groupId, queryDefinition,
						inlineSQLHelper),
					") UNION ALL (",
					getArticlesSQL(
						FIND_A_BY_G_U_F_DDMSI_L_NOT_S, groupId, queryDefinition,
						inlineSQLHelper),
					StringPool.CLOSE_PARENTHESIS),
				folderId, ddmStructureId, excludedStatuses);

			sql = _customSQL.replaceOrderBy(
				sql, queryDefinition.getOrderByComparator());

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("modelFolderId", Type.LONG);
			sqlQuery.addScalar("modelFolder", Type.LONG);
			sqlQuery.addScalar("articleId", Type.STRING);
			sqlQuery.addScalar("version", Type.DOUBLE);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(queryDefinition.getStatus());

			if (folderId >= 0) {
				queryPos.add(folderId);
			}

			queryPos.add(groupId);

			if (queryDefinition.getOwnerUserId() > 0) {
				queryPos.add(queryDefinition.getOwnerUserId());
				queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
			}

			queryPos.add(queryDefinition.getStatus());

			if (folderId >= 0) {
				queryPos.add(folderId);
			}

			if (ddmStructureId > 0) {
				queryPos.add(ddmStructureId);
			}

			queryPos.add(LocaleUtil.toLanguageId(locale));

			List<Object> models = new ArrayList<>();

			Iterator<Object[]> iterator = (Iterator<Object[]>)QueryUtil.iterate(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());

			while (iterator.hasNext()) {
				Object[] array = iterator.next();

				long modelFolder = (Long)array[1];

				Object object = null;

				if (modelFolder == 1) {
					long curFolderId = (Long)array[0];

					object = JournalFolderUtil.findByPrimaryKey(curFolderId);
				}
				else {
					String articleId = (String)array[2];
					double version = (Double)array[3];

					object = JournalArticleUtil.findByG_A_V(
						groupId, articleId, version);
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

	protected String getArticlesSQL(
		String id, long groupId, QueryDefinition<?> queryDefinition,
		boolean inlineSQLHelper) {

		String sql = _customSQL.get(
			getClass(), id, queryDefinition, JournalArticleImpl.TABLE_NAME);

		if (inlineSQLHelper) {
			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, JournalArticle.class.getName(),
				"JournalArticle.resourcePrimKey", groupId);
		}

		return sql;
	}

	protected String getDDMStructureId(long ddmStructureId) {
		if (ddmStructureId <= 0) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(3);

		sb.append(" AND ");
		sb.append(JournalArticleImpl.TABLE_NAME);
		sb.append(".DDMStructureId = ? ");

		return sb.toString();
	}

	protected String getExcludedStatuses(
		int[] excludedStatuses, String tableName) {

		if (ArrayUtil.isEmpty(excludedStatuses)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(5);

		sb.append(" and ");
		sb.append(tableName);
		sb.append(".status not in (");

		for (int i = 0; i < excludedStatuses.length; i++) {
			sb.append(excludedStatuses[i]);

			if (i == (excludedStatuses.length - 1)) {
				continue;
			}

			sb.append(", ");
		}

		sb.append(")");

		return sb.toString();
	}

	protected String getFolderId(long folderId, String tableName) {
		if (folderId < 0) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(5);

		sb.append(" AND ");
		sb.append(tableName);
		sb.append(".");

		if (tableName.equals(JournalFolderImpl.TABLE_NAME)) {
			sb.append("parentFolderId");
		}
		else {
			sb.append("folderId");
		}

		sb.append(" = ? ");

		return sb.toString();
	}

	protected String getFoldersSQL(
		String id, long groupId, QueryDefinition<?> queryDefinition,
		boolean inlineSQLHelper) {

		String sql = _customSQL.get(
			getClass(), id, queryDefinition, JournalFolderImpl.TABLE_NAME);

		if (inlineSQLHelper) {
			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, JournalFolder.class.getName(), "JournalFolder.folderId",
				groupId);
		}

		return sql;
	}

	protected String updateSQL(
		String sql, long folderId, long ddmStructureId,
		int[] excludedStatuses) {

		return StringUtil.replace(
			sql,
			new String[] {
				"[$ARTICLE_FOLDER_ID$]", "[$DDM_STRUCTURE_ID$]",
				"[$EXCLUDED_STATUSES$]", "[$FOLDER_PARENT_FOLDER_ID$]"
			},
			new String[] {
				getFolderId(folderId, JournalArticleImpl.TABLE_NAME),
				getDDMStructureId(ddmStructureId),
				getExcludedStatuses(
					excludedStatuses, JournalArticleImpl.TABLE_NAME),
				getFolderId(folderId, JournalFolderImpl.TABLE_NAME)
			});
	}

	@Reference
	private CustomSQL _customSQL;

}