/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.service.persistence.impl;

import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.model.impl.BookmarksFolderImpl;
import com.liferay.bookmarks.service.persistence.BookmarksEntryUtil;
import com.liferay.bookmarks.service.persistence.BookmarksFolderFinder;
import com.liferay.bookmarks.service.persistence.BookmarksFolderUtil;
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
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 * @author Alexander Chow
 */
@Component(service = BookmarksFolderFinder.class)
public class BookmarksFolderFinderImpl
	extends BookmarksFolderFinderBaseImpl implements BookmarksFolderFinder {

	public static final String COUNT_E_BY_G_F =
		BookmarksFolderFinder.class.getName() + ".countE_ByG_F";

	public static final String COUNT_E_BY_G_F_S =
		BookmarksFolderFinder.class.getName() + ".countE_ByG_F_S";

	public static final String COUNT_F_BY_G_P =
		BookmarksFolderFinder.class.getName() + ".countF_ByG_P";

	public static final String COUNT_F_BY_G_P_S =
		BookmarksFolderFinder.class.getName() + ".countF_ByG_P_S";

	public static final String FIND_BY_NO_ASSETS =
		BookmarksFolderFinder.class.getName() + ".findByNoAssets";

	public static final String FIND_E_BY_G_F =
		BookmarksFolderFinder.class.getName() + ".findE_ByG_F";

	public static final String FIND_E_BY_G_F_S =
		BookmarksFolderFinder.class.getName() + ".findE_ByG_F_S";

	public static final String FIND_F_BY_G_P =
		BookmarksFolderFinder.class.getName() + ".findF_ByG_P";

	public static final String FIND_F_BY_G_P_S =
		BookmarksFolderFinder.class.getName() + ".findF_ByG_P_S";

	@Override
	public int countF_E_ByG_F(
		long groupId, long folderId, QueryDefinition<?> queryDefinition) {

		return doCountF_E_ByG_F(groupId, folderId, queryDefinition, false);
	}

	@Override
	public int filterCountF_E_ByG_F(
		long groupId, long folderId, QueryDefinition<?> queryDefinition) {

		return doCountF_E_ByG_F(groupId, folderId, queryDefinition, true);
	}

	@Override
	public List<Object> filterFindBF_E_ByG_F(
		long groupId, long folderId, QueryDefinition<?> queryDefinition) {

		return doFindF_E_ByG_F(groupId, folderId, queryDefinition, true);
	}

	@Override
	public List<BookmarksFolder> findByNoAssets() {
		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_NO_ASSETS);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("BookmarksFolder", BookmarksFolderImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(_portal.getClassNameId(BookmarksFolder.class));

			return sqlQuery.list(true);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<Object> findF_E_ByG_F(
		long groupId, long folderId, QueryDefinition<?> queryDefinition) {

		return doFindF_E_ByG_F(groupId, folderId, queryDefinition, false);
	}

	protected int doCountF_E_ByG_F(
		long groupId, long folderId, QueryDefinition<?> queryDefinition,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			StringBundler sb = new StringBundler(5);

			sb.append(StringPool.OPEN_PARENTHESIS);

			String sql = null;

			if (queryDefinition.getStatus() == WorkflowConstants.STATUS_ANY) {
				sql = _customSQL.get(getClass(), COUNT_F_BY_G_P);
			}
			else {
				sql = _customSQL.get(getClass(), COUNT_F_BY_G_P_S);

				sql = replaceExcludeStatus(sql, queryDefinition);
			}

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, BookmarksFolder.class.getName(),
					"BookmarksFolder.folderId", groupId);
			}

			sb.append(sql);
			sb.append(") UNION ALL (");

			if (queryDefinition.getStatus() == WorkflowConstants.STATUS_ANY) {
				sql = _customSQL.get(getClass(), COUNT_E_BY_G_F);
			}
			else {
				sql = _customSQL.get(getClass(), COUNT_E_BY_G_F_S);

				sql = replaceExcludeStatus(sql, queryDefinition);
			}

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, BookmarksEntry.class.getName(),
					"BookmarksEntry.entryId", groupId);
			}

			sb.append(sql);
			sb.append(StringPool.CLOSE_PARENTHESIS);

			sql = sb.toString();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(folderId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				queryPos.add(queryDefinition.getStatus());
			}

			queryPos.add(groupId);
			queryPos.add(folderId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				queryPos.add(queryDefinition.getStatus());
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

	protected List<Object> doFindF_E_ByG_F(
		long groupId, long folderId, QueryDefinition<?> queryDefinition,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			StringBundler sb = new StringBundler(5);

			sb.append("SELECT * FROM (");

			String sql = null;

			if (queryDefinition.getStatus() == WorkflowConstants.STATUS_ANY) {
				sql = _customSQL.get(getClass(), FIND_F_BY_G_P);
			}
			else {
				sql = _customSQL.get(getClass(), FIND_F_BY_G_P_S);

				sql = replaceExcludeStatus(sql, queryDefinition);
			}

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, BookmarksFolder.class.getName(),
					"BookmarksFolder.folderId", groupId);
			}

			sb.append(sql);
			sb.append(" UNION ALL ");

			if (queryDefinition.getStatus() == WorkflowConstants.STATUS_ANY) {
				sql = _customSQL.get(getClass(), FIND_E_BY_G_F);
			}
			else {
				sql = _customSQL.get(getClass(), FIND_E_BY_G_F_S);

				sql = replaceExcludeStatus(sql, queryDefinition);
			}

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, BookmarksEntry.class.getName(),
					"BookmarksEntry.entryId", groupId);
			}

			sb.append(sql);
			sb.append(") TEMP_TABLE ORDER BY modelName ASC");

			sql = sb.toString();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("modelId", Type.LONG);
			sqlQuery.addScalar("modelName", Type.STRING);
			sqlQuery.addScalar("modelFolder", Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(folderId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				queryPos.add(queryDefinition.getStatus());
			}

			queryPos.add(groupId);
			queryPos.add(folderId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				queryPos.add(queryDefinition.getStatus());
			}

			List<Object> models = new ArrayList<>();

			Iterator<Object[]> iterator = (Iterator<Object[]>)QueryUtil.iterate(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());

			while (iterator.hasNext()) {
				Object[] array = iterator.next();

				long modelId = (Long)array[0];
				//String name = (String)array[1];
				long modelFolder = (Long)array[2];

				Object object = null;

				if (modelFolder == 0) {
					object = BookmarksFolderUtil.findByPrimaryKey(modelId);
				}
				else {
					object = BookmarksEntryUtil.findByPrimaryKey(modelId);
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

	protected String replaceExcludeStatus(
		String sql, QueryDefinition<?> queryDefinition) {

		if (queryDefinition.isExcludeStatus()) {
			sql = StringUtil.replace(sql, ".status = ?)", ".status != ?)");
		}

		return sql;
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private Portal _portal;

}