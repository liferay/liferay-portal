/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.persistence.impl;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileShortcutConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryUtil;
import com.liferay.document.library.kernel.service.persistence.DLFileShortcutUtil;
import com.liferay.document.library.kernel.service.persistence.DLFolderFinder;
import com.liferay.document.library.kernel.service.persistence.DLFolderUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.documentlibrary.DLGroupServiceSettings;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileShortcutImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileVersionImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFolderImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class DLFolderFinderImpl
	extends DLFolderFinderBaseImpl implements DLFolderFinder {

	public static final String COUNT_F_BY_G_M_F =
		DLFolderFinder.class.getName() + ".countF_ByG_M_F";

	public static final String COUNT_FE_BY_G_F =
		DLFolderFinder.class.getName() + ".countFE_ByG_F";

	public static final String COUNT_FE_BY_G_F_FETI =
		DLFolderFinder.class.getName() + ".countFE_ByG_F_FETI";

	public static final String COUNT_FS_BY_G_F_A =
		DLFolderFinder.class.getName() + ".countFS_ByG_F_A";

	public static final String FIND_F_BY_NO_ASSETS =
		DLFolderFinder.class.getName() + ".findF_ByNoAssets";

	public static final String FIND_F_BY_C_T =
		DLFolderFinder.class.getName() + ".findF_ByC_T";

	public static final String FIND_F_BY_G_M_F =
		DLFolderFinder.class.getName() + ".findF_ByG_M_F";

	public static final String FIND_FE_BY_G_F =
		DLFolderFinder.class.getName() + ".findFE_ByG_F";

	public static final String FIND_FE_BY_G_F_RC =
		DLFolderFinder.class.getName() + ".findFE_ByG_F_RC";

	public static final String FIND_FE_BY_G_F_FETI =
		DLFolderFinder.class.getName() + ".findFE_ByG_F_FETI";

	public static final String FIND_FE_BY_G_F_FETI_RC =
		DLFolderFinder.class.getName() + ".findFE_ByG_F_FETI_RC";

	public static final String FIND_FS_BY_G_F_A =
		DLFolderFinder.class.getName() + ".findFS_ByG_F_A";

	public static final String FIND_FS_BY_G_F_A_RC =
		DLFolderFinder.class.getName() + ".findFS_ByG_F_A_RC";

	public static final String JOIN_FE_BY_DL_FILE_VERSION =
		DLFolderFinder.class.getName() + ".joinFE_ByDLFileVersion";

	public static final String JOIN_FS_BY_DL_FILE_ENTRY =
		DLFolderFinder.class.getName() + ".joinFS_ByDLFileEntry";

	public static final String JOIN_VC_BY_DL_FILE_VERSION =
		DLFolderFinder.class.getName() + ".joinVC_ByDLFileVersion";

	@Override
	public int countF_FE_FS_ByG_F_M_M(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders, QueryDefinition<?> queryDefinition) {

		return doCountF_FE_FS_ByG_F_M_M(
			groupId, folderId, mimeTypes, includeMountFolders, queryDefinition,
			false);
	}

	@Override
	public int countFE_FS_ByG_F(
		long groupId, long folderId, QueryDefinition<?> queryDefinition) {

		return doCountFE_FS_ByG_F_M(
			groupId, folderId, null, queryDefinition, false);
	}

	@Override
	public int filterCountF_FE_FS_ByG_F_M_M(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders, QueryDefinition<?> queryDefinition) {

		return doCountF_FE_FS_ByG_F_M_M(
			groupId, folderId, mimeTypes, includeMountFolders, queryDefinition,
			true);
	}

	@Override
	public int filterCountF_FE_FS_ByG_F_M_FETI_M(
		long groupId, long folderId, String[] mimeTypes, long fileEntryTypeId,
		boolean includeMountFolders, QueryDefinition<?> queryDefinition) {

		return doCountF_FE_FS_ByG_F_M_FETI_M(
			groupId, folderId, mimeTypes, fileEntryTypeId, includeMountFolders,
			queryDefinition, true);
	}

	@Override
	public int filterCountFE_FS_ByG_F(
		long groupId, long folderId, QueryDefinition<?> queryDefinition) {

		return doCountFE_FS_ByG_F_M(
			groupId, folderId, null, queryDefinition, true);
	}

	@Override
	public int filterCountFE_FS_ByG_F_M(
		long groupId, long folderId, String[] mimeTypes,
		QueryDefinition<?> queryDefinition) {

		return doCountFE_FS_ByG_F_M(
			groupId, folderId, mimeTypes, queryDefinition, true);
	}

	@Override
	public List<Object> filterFindF_FE_FS_ByG_F_M_M(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders, QueryDefinition<?> queryDefinition) {

		return doFindF_FE_FS_ByG_F_M_M(
			groupId, folderId, mimeTypes, includeMountFolders, queryDefinition,
			true);
	}

	@Override
	public List<Object> filterFindF_FE_FS_ByG_F_M_FETI_M(
		long groupId, long folderId, String[] mimeTypes, long fileEntryTypeId,
		boolean includeMountFolders, QueryDefinition<?> queryDefinition) {

		return doFindF_FE_FS_ByG_F_M_FETI_M(
			groupId, folderId, mimeTypes, fileEntryTypeId, includeMountFolders,
			queryDefinition, true);
	}

	@Override
	public List<Object> filterFindFE_FS_ByG_F(
		long groupId, long folderId, QueryDefinition<?> queryDefinition) {

		return doFindFE_FS_ByG_F(groupId, folderId, queryDefinition, true);
	}

	@Override
	public List<DLFolder> findF_ByNoAssets() {
		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_F_BY_NO_ASSETS);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(
				PortalUtil.getClassNameId(DLFolderConstants.getClassName()));

			sqlQuery.addEntity("DLFolder", DLFolderImpl.class);

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
	public List<DLFolder> findF_ByC_T(long classNameId, String treePath) {
		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_F_BY_C_T);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(
				CustomSQLUtil.keywords(treePath, WildcardMode.TRAILING)[0]);
			queryPos.add(classNameId);

			sqlQuery.addEntity("DLFolder", DLFolderImpl.class);

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
	public List<Object> findF_FE_FS_ByG_F_M_M(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders, QueryDefinition<?> queryDefinition) {

		return doFindF_FE_FS_ByG_F_M_M(
			groupId, folderId, mimeTypes, includeMountFolders, queryDefinition,
			false);
	}

	@Override
	public List<Object> findFE_FS_ByG_F(
		long groupId, long folderId, QueryDefinition<?> queryDefinition) {

		return doFindFE_FS_ByG_F(groupId, folderId, queryDefinition, false);
	}

	protected int doCountF_FE_FS_ByG_F_M_M(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders, QueryDefinition<?> queryDefinition,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			StringBundler sb = new StringBundler(7);

			sb.append(StringPool.OPEN_PARENTHESIS);

			String sql = CustomSQLUtil.get(
				COUNT_F_BY_G_M_F, queryDefinition, DLFolderImpl.TABLE_NAME);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, DLFolder.class.getName(), "DLFolder.folderId",
					groupId);
			}

			sb.append(sql);
			sb.append(") UNION ALL (");
			sb.append(
				getFileVersionsSQL(
					COUNT_FE_BY_G_F, groupId, mimeTypes, queryDefinition,
					inlineSQLHelper));
			sb.append(") UNION ALL (");
			sb.append(
				getFileShortcutsSQL(
					COUNT_FS_BY_G_F_A, groupId, mimeTypes, queryDefinition,
					inlineSQLHelper));
			sb.append(StringPool.CLOSE_PARENTHESIS);

			sql = sb.toString();

			boolean showHiddenMountFolders = isShowHiddenMountFolders(groupId);

			sql = updateSQL(
				sql, folderId, includeMountFolders, showHiddenMountFolders);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (!showHiddenMountFolders || !includeMountFolders) {
				queryPos.add(false);
			}

			if (!showHiddenMountFolders && !includeMountFolders) {
				queryPos.add(false);
			}

			queryPos.add(queryDefinition.getStatus());
			queryPos.add(folderId);
			queryPos.add(groupId);
			queryPos.add(queryDefinition.getStatus());

			if ((queryDefinition.getOwnerUserId() > 0) &&
				queryDefinition.isIncludeOwner()) {

				queryPos.add(queryDefinition.getOwnerUserId());
				queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
			}

			queryPos.add(folderId);

			if (mimeTypes != null) {
				queryPos.add(mimeTypes);
			}

			queryPos.add(groupId);
			queryPos.add(true);
			queryPos.add(queryDefinition.getStatus());
			queryPos.add(folderId);

			if (mimeTypes != null) {
				queryPos.add(mimeTypes);
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

	protected int doCountF_FE_FS_ByG_F_M_FETI_M(
		long groupId, long folderId, String[] mimeTypes, long fileEntryTypeId,
		boolean includeMountFolders, QueryDefinition<?> queryDefinition,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			StringBundler sb = new StringBundler(7);

			sb.append(StringPool.OPEN_PARENTHESIS);

			String sql = CustomSQLUtil.get(
				COUNT_F_BY_G_M_F, queryDefinition, DLFolderImpl.TABLE_NAME);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, DLFolder.class.getName(), "DLFolder.folderId",
					groupId);
			}

			sb.append(sql);
			sb.append(") UNION ALL (");
			sb.append(
				getFileVersionsSQL(
					COUNT_FE_BY_G_F_FETI, groupId, mimeTypes, queryDefinition,
					inlineSQLHelper));
			sb.append(") UNION ALL (");
			sb.append(
				getFileShortcutsSQL(
					COUNT_FS_BY_G_F_A, groupId, mimeTypes, queryDefinition,
					inlineSQLHelper));
			sb.append(StringPool.CLOSE_PARENTHESIS);

			sql = sb.toString();

			boolean showHiddenMountFolders = isShowHiddenMountFolders(groupId);

			sql = updateSQL(
				sql, folderId, includeMountFolders, showHiddenMountFolders);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (!showHiddenMountFolders || !includeMountFolders) {
				queryPos.add(false);
			}

			if (!showHiddenMountFolders && !includeMountFolders) {
				queryPos.add(false);
			}

			queryPos.add(queryDefinition.getStatus());
			queryPos.add(folderId);
			queryPos.add(groupId);
			queryPos.add(queryDefinition.getStatus());

			if ((queryDefinition.getOwnerUserId() > 0) &&
				queryDefinition.isIncludeOwner()) {

				queryPos.add(queryDefinition.getOwnerUserId());
				queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
			}

			queryPos.add(folderId);

			if (mimeTypes != null) {
				queryPos.add(mimeTypes);
			}

			queryPos.add(fileEntryTypeId);
			queryPos.add(groupId);
			queryPos.add(true);
			queryPos.add(queryDefinition.getStatus());
			queryPos.add(folderId);

			if (mimeTypes != null) {
				queryPos.add(mimeTypes);
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

	protected int doCountFE_FS_ByG_F_M(
		long groupId, long folderId, String[] mimeTypes,
		QueryDefinition<?> queryDefinition, boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			StringBundler sb = new StringBundler(5);

			sb.append(StringPool.OPEN_PARENTHESIS);

			String sql = getFileVersionsSQL(
				COUNT_FE_BY_G_F, groupId, mimeTypes, queryDefinition,
				inlineSQLHelper);

			sb.append(sql);

			sb.append(") UNION ALL (");
			sb.append(
				getFileShortcutsSQL(
					COUNT_FS_BY_G_F_A, groupId, mimeTypes, queryDefinition,
					inlineSQLHelper));
			sb.append(StringPool.CLOSE_PARENTHESIS);

			sql = sb.toString();

			sql = updateSQL(sql, folderId, false, false);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(queryDefinition.getStatus());
			queryPos.add(folderId);

			if (mimeTypes != null) {
				queryPos.add(mimeTypes);
			}

			queryPos.add(groupId);
			queryPos.add(true);
			queryPos.add(queryDefinition.getStatus());
			queryPos.add(folderId);

			if (mimeTypes != null) {
				queryPos.add(mimeTypes);
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

	protected List<Object> doFindF_FE_FS_ByG_F_M_M(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders, QueryDefinition<?> queryDefinition,
		boolean inlineSQLHelper) {

		OrderByComparator<?> orderByComparator =
			queryDefinition.getOrderByComparator();

		if ((orderByComparator != null) &&
			ArrayUtil.contains(
				orderByComparator.getOrderByFields(), "readCount")) {

			return doFindF_FE_FS_ByG_F_M_M_RC(
				groupId, folderId, mimeTypes, includeMountFolders,
				queryDefinition, inlineSQLHelper);
		}

		Session session = null;

		try {
			session = openSession();

			StringBundler sb = new StringBundler(7);

			sb.append("SELECT * FROM (");

			String sql = CustomSQLUtil.get(
				FIND_F_BY_G_M_F, queryDefinition, DLFolderImpl.TABLE_NAME);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, DLFolder.class.getName(), "DLFolder.folderId",
					groupId);
			}

			sb.append(sql);
			sb.append(" UNION ALL ");

			sql = getFileEntriesSQL(
				FIND_FE_BY_G_F, groupId, mimeTypes, queryDefinition,
				inlineSQLHelper);

			sb.append(sql);

			sb.append(" UNION ALL ");

			sql = getFileShortcutsSQL(
				FIND_FS_BY_G_F_A, groupId, mimeTypes, queryDefinition,
				inlineSQLHelper);

			sb.append(sql);

			sb.append(") TEMP_TABLE ORDER BY modelFolder DESC, title ASC");

			sql = sb.toString();

			boolean showHiddenMountFolders = isShowHiddenMountFolders(groupId);

			sql = updateSQL(
				sql, folderId, includeMountFolders, showHiddenMountFolders);

			sql = CustomSQLUtil.replaceOrderBy(sql, orderByComparator);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("modelFolderId", Type.LONG);
			sqlQuery.addScalar("name", Type.STRING);
			sqlQuery.addScalar("title", Type.STRING);
			sqlQuery.addScalar("fileShortcutId", Type.LONG);
			sqlQuery.addScalar("modelFolder", Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (!showHiddenMountFolders || !includeMountFolders) {
				queryPos.add(false);
			}

			if (!showHiddenMountFolders && !includeMountFolders) {
				queryPos.add(false);
			}

			queryPos.add(queryDefinition.getStatus());
			queryPos.add(folderId);
			queryPos.add(groupId);
			queryPos.add(queryDefinition.getStatus());

			if ((queryDefinition.getOwnerUserId() > 0) &&
				queryDefinition.isIncludeOwner()) {

				queryPos.add(queryDefinition.getOwnerUserId());
				queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
			}

			queryPos.add(folderId);

			if (mimeTypes != null) {
				queryPos.add(mimeTypes);
			}

			queryPos.add(groupId);
			queryPos.add(true);
			queryPos.add(queryDefinition.getStatus());
			queryPos.add(folderId);

			if (mimeTypes != null) {
				queryPos.add(mimeTypes);
			}

			List<Object> models = new ArrayList<>();

			Iterator<Object[]> iterator = (Iterator<Object[]>)QueryUtil.iterate(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());

			while (iterator.hasNext()) {
				Object[] array = iterator.next();

				long curFolderId = (Long)array[0];
				//String title = (String)array[2];
				long fileShortcutId = (Long)array[3];
				long modelFolder = (Long)array[4];

				Object object = null;

				if (modelFolder == 1) {
					object = DLFolderUtil.findByPrimaryKey(curFolderId);
				}
				else if (fileShortcutId > 0) {
					object = DLFileShortcutUtil.findByPrimaryKey(
						fileShortcutId);
				}
				else {
					String name = (String)array[1];

					object = DLFileEntryUtil.findByG_F_N(
						groupId, curFolderId, name);
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

	protected List<Object> doFindF_FE_FS_ByG_F_M_FETI_M(
		long groupId, long folderId, String[] mimeTypes, long fileEntryTypeId,
		boolean includeMountFolders, QueryDefinition<?> queryDefinition,
		boolean inlineSQLHelper) {

		OrderByComparator<?> orderByComparator =
			queryDefinition.getOrderByComparator();

		if ((orderByComparator != null) &&
			ArrayUtil.contains(
				orderByComparator.getOrderByFields(), "readCount")) {

			return doFindF_FE_FS_ByG_F_M_FETI_M_RC(
				groupId, folderId, mimeTypes, fileEntryTypeId,
				includeMountFolders, queryDefinition, inlineSQLHelper);
		}

		Session session = null;

		try {
			session = openSession();

			StringBundler sb = new StringBundler(7);

			sb.append("SELECT * FROM (");

			String sql = CustomSQLUtil.get(
				FIND_F_BY_G_M_F, queryDefinition, DLFolderImpl.TABLE_NAME);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, DLFolder.class.getName(), "DLFolder.folderId",
					groupId);
			}

			sb.append(sql);
			sb.append(" UNION ALL ");

			sql = getFileEntriesSQL(
				FIND_FE_BY_G_F_FETI, groupId, mimeTypes, queryDefinition,
				inlineSQLHelper);

			sb.append(sql);

			sb.append(" UNION ALL ");

			sql = getFileShortcutsSQL(
				FIND_FS_BY_G_F_A, groupId, mimeTypes, queryDefinition,
				inlineSQLHelper);

			sb.append(sql);

			sb.append(") TEMP_TABLE ORDER BY modelFolder DESC, title ASC");

			sql = sb.toString();

			boolean showHiddenMountFolders = isShowHiddenMountFolders(groupId);

			sql = updateSQL(
				sql, folderId, includeMountFolders, showHiddenMountFolders);

			sql = CustomSQLUtil.replaceOrderBy(sql, orderByComparator);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("modelFolderId", Type.LONG);
			sqlQuery.addScalar("name", Type.STRING);
			sqlQuery.addScalar("title", Type.STRING);
			sqlQuery.addScalar("fileShortcutId", Type.LONG);
			sqlQuery.addScalar("modelFolder", Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (!showHiddenMountFolders || !includeMountFolders) {
				queryPos.add(false);
			}

			if (!showHiddenMountFolders && !includeMountFolders) {
				queryPos.add(false);
			}

			queryPos.add(queryDefinition.getStatus());
			queryPos.add(folderId);
			queryPos.add(groupId);
			queryPos.add(queryDefinition.getStatus());

			if ((queryDefinition.getOwnerUserId() > 0) &&
				queryDefinition.isIncludeOwner()) {

				queryPos.add(queryDefinition.getOwnerUserId());
				queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
			}

			queryPos.add(folderId);
			queryPos.add(fileEntryTypeId);

			if (mimeTypes != null) {
				queryPos.add(mimeTypes);
			}

			queryPos.add(groupId);
			queryPos.add(true);
			queryPos.add(queryDefinition.getStatus());
			queryPos.add(folderId);

			if (mimeTypes != null) {
				queryPos.add(mimeTypes);
			}

			List<Object> models = new ArrayList<>();

			Iterator<Object[]> iterator = (Iterator<Object[]>)QueryUtil.iterate(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());

			while (iterator.hasNext()) {
				Object[] array = iterator.next();

				long curFolderId = (Long)array[0];
				//String title = (String)array[2];
				long fileShortcutId = (Long)array[3];
				long modelFolder = (Long)array[4];

				Object object = null;

				if (modelFolder == 1) {
					object = DLFolderUtil.findByPrimaryKey(curFolderId);
				}
				else if (fileShortcutId > 0) {
					object = DLFileShortcutUtil.findByPrimaryKey(
						fileShortcutId);
				}
				else {
					String name = (String)array[1];

					object = DLFileEntryUtil.findByG_F_N(
						groupId, curFolderId, name);
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

	protected List<Object> doFindF_FE_FS_ByG_F_M_M_RC(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders, QueryDefinition<?> queryDefinition,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			StringBundler sb = new StringBundler(7);

			sb.append("SELECT * FROM (");

			String sql = CustomSQLUtil.get(
				FIND_F_BY_G_M_F, queryDefinition, DLFolderImpl.TABLE_NAME);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, DLFolder.class.getName(), "DLFolder.folderId",
					groupId);
			}

			sb.append(sql);
			sb.append(" UNION ");

			sql = getFileEntriesSQL(
				FIND_FE_BY_G_F_RC, groupId, mimeTypes, queryDefinition,
				inlineSQLHelper);

			sb.append(sql);

			sb.append(" UNION ");

			sql = getFileShortcutsSQL(
				FIND_FS_BY_G_F_A_RC, groupId, mimeTypes, queryDefinition,
				inlineSQLHelper);

			sb.append(sql);

			sb.append(") TEMP_TABLE ORDER BY modelFolder DESC, title ASC");

			sql = sb.toString();

			boolean showHiddenMountFolders = isShowHiddenMountFolders(groupId);

			sql = updateSQL(
				sql, folderId, includeMountFolders, showHiddenMountFolders);

			sql = CustomSQLUtil.replaceOrderBy(
				sql, queryDefinition.getOrderByComparator());

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("modelFolderId", Type.LONG);
			sqlQuery.addScalar("name", Type.STRING);
			sqlQuery.addScalar("title", Type.STRING);
			sqlQuery.addScalar("fileShortcutId", Type.LONG);
			sqlQuery.addScalar("modelFolder", Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (!showHiddenMountFolders || !includeMountFolders) {
				queryPos.add(false);
			}

			if (!showHiddenMountFolders && !includeMountFolders) {
				queryPos.add(false);
			}

			queryPos.add(queryDefinition.getStatus());
			queryPos.add(folderId);

			for (int i = 0; i < 2; i++) {
				queryPos.add(groupId);
				queryPos.add(queryDefinition.getStatus());

				if ((queryDefinition.getOwnerUserId() > 0) &&
					queryDefinition.isIncludeOwner()) {

					queryPos.add(queryDefinition.getOwnerUserId());
					queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
				}

				queryPos.add(folderId);

				if (mimeTypes == null) {
					continue;
				}

				queryPos.add(mimeTypes);
			}

			for (int i = 0; i < 2; i++) {
				queryPos.add(groupId);
				queryPos.add(true);
				queryPos.add(queryDefinition.getStatus());
				queryPos.add(folderId);

				if (mimeTypes == null) {
					continue;
				}

				queryPos.add(mimeTypes);
			}

			List<Object> models = new ArrayList<>();

			Iterator<Object[]> iterator = (Iterator<Object[]>)QueryUtil.iterate(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());

			while (iterator.hasNext()) {
				Object[] array = iterator.next();

				long curFolderId = (Long)array[0];
				//String title = (String)array[2];
				long fileShortcutId = (Long)array[3];
				long modelFolder = (Long)array[4];

				Object object = null;

				if (modelFolder == 1) {
					object = DLFolderUtil.findByPrimaryKey(curFolderId);
				}
				else if (fileShortcutId > 0) {
					object = DLFileShortcutUtil.findByPrimaryKey(
						fileShortcutId);
				}
				else {
					String name = (String)array[1];

					object = DLFileEntryUtil.findByG_F_N(
						groupId, curFolderId, name);
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

	protected List<Object> doFindF_FE_FS_ByG_F_M_FETI_M_RC(
		long groupId, long folderId, String[] mimeTypes, long fileEntryTypeId,
		boolean includeMountFolders, QueryDefinition<?> queryDefinition,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			StringBundler sb = new StringBundler(7);

			sb.append("SELECT * FROM (");

			String sql = CustomSQLUtil.get(
				FIND_F_BY_G_M_F, queryDefinition, DLFolderImpl.TABLE_NAME);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, DLFolder.class.getName(), "DLFolder.folderId",
					groupId);
			}

			sb.append(sql);
			sb.append(" UNION ALL ");

			sql = getFileEntriesSQL(
				FIND_FE_BY_G_F_FETI_RC, groupId, mimeTypes, queryDefinition,
				inlineSQLHelper);

			sb.append(sql);

			sb.append(" UNION ALL ");

			sql = getFileShortcutsSQL(
				FIND_FS_BY_G_F_A_RC, groupId, mimeTypes, queryDefinition,
				inlineSQLHelper);

			sb.append(sql);

			sb.append(") TEMP_TABLE ORDER BY modelFolder DESC, title ASC");

			sql = sb.toString();

			boolean showHiddenMountFolders = isShowHiddenMountFolders(groupId);

			sql = updateSQL(
				sql, folderId, includeMountFolders, showHiddenMountFolders);

			sql = CustomSQLUtil.replaceOrderBy(
				sql, queryDefinition.getOrderByComparator());

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("modelFolderId", Type.LONG);
			sqlQuery.addScalar("name", Type.STRING);
			sqlQuery.addScalar("title", Type.STRING);
			sqlQuery.addScalar("fileShortcutId", Type.LONG);
			sqlQuery.addScalar("modelFolder", Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (!showHiddenMountFolders || !includeMountFolders) {
				queryPos.add(false);
			}

			if (!showHiddenMountFolders && !includeMountFolders) {
				queryPos.add(false);
			}

			queryPos.add(queryDefinition.getStatus());
			queryPos.add(folderId);

			for (int i = 0; i < 2; i++) {
				queryPos.add(groupId);
				queryPos.add(queryDefinition.getStatus());

				if ((queryDefinition.getOwnerUserId() > 0) &&
					queryDefinition.isIncludeOwner()) {

					queryPos.add(queryDefinition.getOwnerUserId());
					queryPos.add(WorkflowConstants.STATUS_IN_TRASH);
				}

				queryPos.add(folderId);

				if (mimeTypes != null) {
					queryPos.add(mimeTypes);
				}

				queryPos.add(fileEntryTypeId);
			}

			for (int i = 0; i < 2; i++) {
				queryPos.add(groupId);
				queryPos.add(true);
				queryPos.add(queryDefinition.getStatus());
				queryPos.add(folderId);

				if (mimeTypes == null) {
					continue;
				}

				queryPos.add(mimeTypes);
			}

			List<Object> models = new ArrayList<>();

			Iterator<Object[]> iterator = (Iterator<Object[]>)QueryUtil.iterate(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());

			while (iterator.hasNext()) {
				Object[] array = iterator.next();

				long curFolderId = (Long)array[0];
				//String title = (String)array[2];
				long fileShortcutId = (Long)array[3];
				long modelFolder = (Long)array[4];

				Object object = null;

				if (modelFolder == 1) {
					object = DLFolderUtil.findByPrimaryKey(curFolderId);
				}
				else if (fileShortcutId > 0) {
					object = DLFileShortcutUtil.findByPrimaryKey(
						fileShortcutId);
				}
				else {
					String name = (String)array[1];

					object = DLFileEntryUtil.findByG_F_N(
						groupId, curFolderId, name);
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

	protected List<Object> doFindFE_FS_ByG_F(
		long groupId, long folderId, QueryDefinition<?> queryDefinition,
		boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			StringBundler sb = new StringBundler(5);

			sb.append("SELECT * FROM (");

			String sql = getFileEntriesSQL(
				FIND_FE_BY_G_F, groupId, null, queryDefinition,
				inlineSQLHelper);

			sb.append(sql);

			sb.append(" UNION ALL ");

			sql = getFileShortcutsSQL(
				FIND_FS_BY_G_F_A, groupId, null, queryDefinition,
				inlineSQLHelper);

			sb.append(sql);

			sb.append(") TEMP_TABLE ORDER BY modelFolder DESC, title ASC");

			sql = sb.toString();

			sql = updateSQL(sql, folderId, false, false);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("modelFolderId", Type.LONG);
			sqlQuery.addScalar("name", Type.STRING);
			sqlQuery.addScalar("title", Type.STRING);
			sqlQuery.addScalar("fileShortcutId", Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);
			queryPos.add(queryDefinition.getStatus());
			queryPos.add(folderId);
			queryPos.add(groupId);
			queryPos.add(true);
			queryPos.add(queryDefinition.getStatus());
			queryPos.add(folderId);

			List<Object> models = new ArrayList<>();

			Iterator<Object[]> iterator = (Iterator<Object[]>)QueryUtil.iterate(
				sqlQuery, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());

			while (iterator.hasNext()) {
				Object[] array = iterator.next();

				long fileShortcutId = (Long)array[3];

				Object object = null;

				if (fileShortcutId > 0) {
					object = DLFileShortcutUtil.findByPrimaryKey(
						fileShortcutId);
				}
				else {
					long folderId2 = (Long)array[0];
					String name = (String)array[1];
					//String title = (String)array[2];

					object = DLFileEntryUtil.findByG_F_N(
						groupId, folderId2, name);
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

	protected String getFileEntriesSQL(
		String id, long groupId, String[] mimeTypes,
		QueryDefinition<?> queryDefinition, boolean inlineSQLHelper) {

		String sql = CustomSQLUtil.get(
			id, queryDefinition, DLFileVersionImpl.TABLE_NAME);

		if (inlineSQLHelper) {
			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, DLFileEntry.class.getName(), "DLFileEntry.fileEntryId",
				groupId);
		}

		if (queryDefinition.getStatus() == WorkflowConstants.STATUS_ANY) {
			sql = StringUtil.removeSubstring(sql, "[$JOIN$]");
		}
		else {
			sql = StringUtil.replace(
				sql, "[$JOIN$]",
				CustomSQLUtil.get(
					DLFolderFinderImpl.JOIN_FE_BY_DL_FILE_VERSION));
		}

		OrderByComparator<?> orderByComparator =
			queryDefinition.getOrderByComparator();

		if ((orderByComparator != null) &&
			ArrayUtil.contains(
				orderByComparator.getOrderByFields(), "readCount")) {

			sql = StringUtil.replace(
				sql, "[$VC_JOIN$]",
				CustomSQLUtil.get(
					DLFolderFinderImpl.JOIN_VC_BY_DL_FILE_VERSION));
		}

		if (ArrayUtil.isNotEmpty(mimeTypes)) {
			sql = StringBundler.concat(
				sql, WHERE_AND, StringPool.OPEN_PARENTHESIS,
				getMimeTypes(mimeTypes, DLFileEntryImpl.TABLE_NAME),
				StringPool.CLOSE_PARENTHESIS);
		}

		return sql;
	}

	protected String getFileShortcutsSQL(
		String id, long groupId, String[] mimeTypes,
		QueryDefinition<?> queryDefinition, boolean inlineSQLHelper) {

		String sql = CustomSQLUtil.get(
			id, queryDefinition, DLFileShortcutImpl.TABLE_NAME);

		if (inlineSQLHelper) {
			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, DLFileShortcutConstants.getClassName(),
				"DLFileShortcut.fileShortcutId", groupId);
		}

		if (ArrayUtil.isNotEmpty(mimeTypes)) {
			sql = StringBundler.concat(
				StringUtil.replace(
					sql, "[$JOIN$]",
					CustomSQLUtil.get(JOIN_FS_BY_DL_FILE_ENTRY)),
				WHERE_AND, StringPool.OPEN_PARENTHESIS,
				getMimeTypes(mimeTypes, DLFileEntryImpl.TABLE_NAME),
				StringPool.CLOSE_PARENTHESIS);
		}
		else {
			sql = StringUtil.removeSubstring(sql, "[$JOIN$]");
		}

		return sql;
	}

	protected String getFileVersionsSQL(
		String id, long groupId, String[] mimeTypes,
		QueryDefinition<?> queryDefinition, boolean inlineSQLHelper) {

		String sql = CustomSQLUtil.get(
			id, queryDefinition, DLFileVersionImpl.TABLE_NAME);

		if (inlineSQLHelper) {
			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, DLFileEntry.class.getName(), "DLFileVersion.fileEntryId",
				groupId);
		}

		if (ArrayUtil.isNotEmpty(mimeTypes)) {
			sql = StringBundler.concat(
				sql, WHERE_AND, StringPool.OPEN_PARENTHESIS,
				getMimeTypes(mimeTypes, DLFileVersionImpl.TABLE_NAME),
				StringPool.CLOSE_PARENTHESIS);
		}

		return sql;
	}

	protected String getFolderId(long folderId, String tableName) {
		StringBundler sb = new StringBundler(4);

		sb.append(tableName);
		sb.append(".");

		if (tableName.equals(DLFolderImpl.TABLE_NAME)) {
			sb.append("parentFolderId");
		}
		else {
			sb.append("folderId");
		}

		sb.append("= ? ");

		return sb.toString();
	}

	protected String getMimeTypes(String[] mimeTypes, String tableName) {
		if (mimeTypes.length == 0) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler((mimeTypes.length * 3) - 1);

		for (int i = 0; i < mimeTypes.length; i++) {
			sb.append(tableName);
			sb.append(".mimeType = ?");

			if ((i + 1) == mimeTypes.length) {
				continue;
			}

			sb.append(WHERE_OR);
		}

		return sb.toString();
	}

	protected boolean isShowHiddenMountFolders(long groupId) {
		try {
			DLGroupServiceSettings dlGroupServiceSettings =
				DLGroupServiceSettings.getInstance(groupId);

			return dlGroupServiceSettings.isShowHiddenMountFolders();
		}
		catch (PortalException portalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return false;
	}

	protected String updateSQL(
		String sql, long folderId, boolean includeMountFolders,
		boolean showHiddenMountFolders) {

		sql = StringUtil.replace(
			sql,
			new String[] {
				"[$FILE_ENTRY_FOLDER_ID$]", "[$FILE_SHORTCUT_FOLDER_ID$]",
				"[$FILE_VERSION_FOLDER_ID$]", "[$FOLDER_PARENT_FOLDER_ID$]"
			},
			new String[] {
				getFolderId(folderId, DLFileEntryImpl.TABLE_NAME),
				getFolderId(folderId, DLFileShortcutImpl.TABLE_NAME),
				getFolderId(folderId, DLFileVersionImpl.TABLE_NAME),
				getFolderId(folderId, DLFolderImpl.TABLE_NAME)
			});

		if (showHiddenMountFolders) {
			if (includeMountFolders) {
				sql = StringUtil.removeSubstring(sql, "([$HIDDEN$]) AND");
			}
			else {
				sql = StringUtil.replace(
					sql, "([$HIDDEN$]) AND", "(DLFolder.mountPoint = ?) AND");
			}
		}
		else {
			if (includeMountFolders) {
				sql = StringUtil.replace(
					sql, "([$HIDDEN$]) AND", "(DLFolder.hidden_ = ?) AND");
			}
			else {
				sql = StringUtil.replace(
					sql, "([$HIDDEN$]) AND",
					"(DLFolder.hidden_ = ?) AND (DLFolder.mountPoint = ?) AND");
			}
		}

		return sql;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFolderFinderImpl.class);

}