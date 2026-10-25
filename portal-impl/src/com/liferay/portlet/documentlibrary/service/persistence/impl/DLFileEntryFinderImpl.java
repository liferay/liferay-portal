/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.persistence.impl;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryTable;
import com.liferay.document.library.kernel.model.DLFileVersionTable;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryFinder;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.sql.dsl.query.sort.OrderByExpression;
import com.liferay.petra.sql.dsl.spi.expression.Scalar;
import com.liferay.petra.sql.dsl.spi.expression.TableStar;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.view.count.ViewCountManagerUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileVersionImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class DLFileEntryFinderImpl
	extends DLFileEntryFinderBaseImpl implements DLFileEntryFinder {

	public static final String COUNT_BY_EXTRA_SETTINGS =
		DLFileEntryFinder.class.getName() + ".countByExtraSettings";

	public static final String COUNT_BY_G_F_S =
		DLFileEntryFinder.class.getName() + ".countByG_F_S";

	public static final String FIND_BY_DDM_STRUCTURE_IDS =
		DLFileEntryFinder.class.getName() + ".findByDDMStructureIds";

	public static final String FIND_BY_NO_ASSETS =
		DLFileEntryFinder.class.getName() + ".findByNoAssets";

	public static final String FIND_BY_EXTRA_SETTINGS =
		DLFileEntryFinder.class.getName() + ".findByExtraSettings";

	public static final String FIND_BY_C_T =
		DLFileEntryFinder.class.getName() + ".findByC_T";

	@Override
	public int countByExtraSettings() {
		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_EXTRA_SETTINGS);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

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
	public int countByG_F(
		long groupId, List<Long> folderIds,
		QueryDefinition<DLFileEntry> queryDefinition) {

		return doCountByG_F(groupId, folderIds, queryDefinition, false);
	}

	@Override
	public int countByG_U_R_F_M(
		long groupId, long userId, List<Long> repositoryIds,
		List<Long> folderIds, String[] mimeTypes,
		QueryDefinition<DLFileEntry> queryDefinition) {

		return doCountByG_U_R_F_M(
			groupId, userId, repositoryIds, folderIds, mimeTypes,
			queryDefinition, false);
	}

	@Override
	public int filterCountByG_F(
		long groupId, List<Long> folderIds,
		QueryDefinition<DLFileEntry> queryDefinition) {

		return doCountByG_F(groupId, folderIds, queryDefinition, true);
	}

	@Override
	public int filterCountByG_U_F_M(
		long groupId, long userId, List<Long> folderIds, String[] mimeTypes,
		QueryDefinition<DLFileEntry> queryDefinition) {

		List<Long> repositoryIds = Collections.emptyList();

		return doCountByG_U_R_F_M(
			groupId, userId, repositoryIds, folderIds, mimeTypes,
			queryDefinition, true);
	}

	@Override
	public int filterCountByG_U_R_F_M(
		long groupId, long userId, List<Long> repositoryIds,
		List<Long> folderIds, String[] mimeTypes,
		QueryDefinition<DLFileEntry> queryDefinition) {

		return doCountByG_U_R_F_M(
			groupId, userId, repositoryIds, folderIds, mimeTypes,
			queryDefinition, true);
	}

	@Override
	public List<DLFileEntry> filterFindByG_F(
		long groupId, List<Long> folderIds,
		QueryDefinition<DLFileEntry> queryDefinition) {

		List<Long> repositoryIds = Collections.emptyList();

		return doFindByG_U_R_F_M(
			groupId, 0, repositoryIds, folderIds, null, queryDefinition, true);
	}

	@Override
	public List<DLFileEntry> filterFindByG_U_F_M(
		long groupId, long userId, List<Long> folderIds, String[] mimeTypes,
		QueryDefinition<DLFileEntry> queryDefinition) {

		List<Long> repositoryIds = Collections.emptyList();

		return doFindByG_U_R_F_M(
			groupId, userId, repositoryIds, folderIds, mimeTypes,
			queryDefinition, true);
	}

	@Override
	public List<DLFileEntry> filterFindByG_U_R_F_M(
		long groupId, long userId, List<Long> repositoryIds,
		List<Long> folderIds, String[] mimeTypes,
		QueryDefinition<DLFileEntry> queryDefinition) {

		return doFindByG_U_R_F_M(
			groupId, userId, repositoryIds, folderIds, mimeTypes,
			queryDefinition, true);
	}

	@Override
	public List<DLFileEntry> findByDDMStructureIds(
		long groupId, long[] ddmStructureIds, int start, int end) {

		Session session = null;

		try {
			session = openSession();

			if ((ddmStructureIds == null) || (ddmStructureIds.length <= 0)) {
				return Collections.emptyList();
			}

			String sql = CustomSQLUtil.get(FIND_BY_DDM_STRUCTURE_IDS);

			if (groupId <= 0) {
				sql = StringUtil.removeSubstring(
					sql, "(DLFileEntry.groupId = ?) AND");
			}

			sql = StringUtil.replace(
				sql, "[$DDM_STRUCTURE_ID$]",
				getDDMStructureIds(ddmStructureIds));

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				DLFileEntryImpl.TABLE_NAME, DLFileEntryImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (groupId > 0) {
				queryPos.add(groupId);
			}

			queryPos.add(ddmStructureIds);

			return (List<DLFileEntry>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<DLFileEntry> findByDDMStructureIds(
		long[] ddmStructureIds, int start, int end) {

		return findByDDMStructureIds(0, ddmStructureIds, start, end);
	}

	@Override
	public List<DLFileEntry> findByNoAssets() {
		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_NO_ASSETS);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity(
				DLFileEntryImpl.TABLE_NAME, DLFileEntryImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(
				PortalUtil.getClassNameId(DLFileEntryConstants.getClassName()));

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
	public List<DLFileEntry> findByExtraSettings(int start, int end) {
		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_EXTRA_SETTINGS);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar("fileEntryId", Type.LONG);

			List<Long> fileEntryIds = (List<Long>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);

			List<DLFileEntry> dlFileEntries = new ArrayList<>(
				fileEntryIds.size());

			for (long fileEntryId : fileEntryIds) {
				dlFileEntries.add(
					dlFileEntryPersistence.findByPrimaryKey(fileEntryId));
			}

			return Collections.unmodifiableList(dlFileEntries);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<DLFileEntry> findByC_T(long classNameId, String treePath) {
		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_C_T);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(
				CustomSQLUtil.keywords(treePath, WildcardMode.TRAILING)[0]);
			queryPos.add(classNameId);

			sqlQuery.addEntity(
				DLFileEntryImpl.TABLE_NAME, DLFileEntryImpl.class);

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
	public List<DLFileEntry> findByG_F(
		long groupId, List<Long> folderIds,
		QueryDefinition<DLFileEntry> queryDefinition) {

		List<Long> repositoryIds = Collections.emptyList();

		return doFindByG_U_R_F_M(
			groupId, 0, repositoryIds, folderIds, null, queryDefinition, false);
	}

	@Override
	public List<DLFileEntry> findByG_R_F(
		long groupId, List<Long> repositoryIds, List<Long> folderIds,
		QueryDefinition<DLFileEntry> queryDefinition) {

		return doFindByG_U_R_F_M(
			groupId, 0, repositoryIds, folderIds, null, queryDefinition, false);
	}

	@Override
	public List<DLFileEntry> findByG_U_F(
		long groupId, long userId, List<Long> folderIds,
		QueryDefinition<DLFileEntry> queryDefinition) {

		List<Long> repositoryIds = Collections.emptyList();

		return doFindByG_U_R_F_M(
			groupId, userId, repositoryIds, folderIds, null, queryDefinition,
			false);
	}

	@Override
	public List<DLFileEntry> findByG_U_F_M(
		long groupId, long userId, List<Long> folderIds, String[] mimeTypes,
		QueryDefinition<DLFileEntry> queryDefinition) {

		List<Long> repositoryIds = Collections.emptyList();

		return doFindByG_U_R_F_M(
			groupId, userId, repositoryIds, folderIds, mimeTypes,
			queryDefinition, false);
	}

	@Override
	public List<DLFileEntry> findByG_U_R_F(
		long groupId, long userId, List<Long> repositoryIds,
		List<Long> folderIds, QueryDefinition<DLFileEntry> queryDefinition) {

		return doFindByG_U_R_F_M(
			groupId, userId, repositoryIds, folderIds, null, queryDefinition,
			false);
	}

	@Override
	public List<DLFileEntry> findByG_U_R_F_M(
		long groupId, long userId, List<Long> repositoryIds,
		List<Long> folderIds, String[] mimeTypes,
		QueryDefinition<DLFileEntry> queryDefinition) {

		return doFindByG_U_R_F_M(
			groupId, userId, repositoryIds, folderIds, mimeTypes,
			queryDefinition, false);
	}

	protected int doCountByG_F(
		long groupId, List<Long> folderIds,
		QueryDefinition<DLFileEntry> queryDefinition, boolean inlineSQLHelper) {

		Session session = null;

		try {
			session = openSession();

			List<Long> repositoryIds = Collections.emptyList();

			String sql = getFileEntriesSQL(
				COUNT_BY_G_F_S, groupId, repositoryIds, folderIds, null,
				queryDefinition, inlineSQLHelper);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

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

	protected int doCountByG_U_R_F_M(
		long groupId, long userId, List<Long> repositoryIds,
		List<Long> folderIds, String[] mimeTypes,
		QueryDefinition<DLFileEntry> queryDefinition, boolean inlineSQLHelper) {

		FromStep fromStep = DSLQueryFactoryUtil.select(
			DSLFunctionFactoryUtil.count(
				DLFileEntryTable.INSTANCE.fileEntryId
			).as(
				COUNT_COLUMN_NAME
			));

		JoinStep joinStep = _getJoinStep(
			fromStep, folderIds, groupId, inlineSQLHelper, mimeTypes,
			queryDefinition, repositoryIds, userId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(joinStep);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

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

	protected List<DLFileEntry> doFindByG_U_R_F_M(
		long groupId, long userId, List<Long> repositoryIds,
		List<Long> folderIds, String[] mimeTypes,
		QueryDefinition<DLFileEntry> queryDefinition, boolean inlineSQLHelper) {

		FromStep fromStep = DSLQueryFactoryUtil.select(
			DLFileEntryTable.INSTANCE);

		Table<?> viewCountEntryTable =
			ViewCountManagerUtil.getViewCountEntryTable();

		Column<?, ?> viewCountColumn = viewCountEntryTable.getColumn(
			"viewCount");

		OrderByComparator<DLFileEntry> orderByComparator =
			queryDefinition.getOrderByComparator();

		if (_isOrderByReadCount(orderByComparator)) {
			fromStep = DSLQueryFactoryUtil.select(
				new TableStar(DLFileEntryTable.INSTANCE),
				DSLFunctionFactoryUtil.caseWhenThen(
					viewCountColumn.isNull(),
					DSLFunctionFactoryUtil.castText(new Scalar<>(0))
				).elseEnd(
					DSLFunctionFactoryUtil.castText(
						new Scalar<>(viewCountColumn.toString()))
				).as(
					"viewCount"
				));
		}

		JoinStep joinStep = _getJoinStep(
			fromStep, folderIds, groupId, inlineSQLHelper, mimeTypes,
			queryDefinition, repositoryIds, userId);

		DSLQuery dslQuery = null;

		if (orderByComparator == null) {
			dslQuery = joinStep.orderBy(
				DLFileEntryTable.INSTANCE.fileEntryId.ascending());
		}
		else if (_isOrderByReadCount(orderByComparator)) {
			OrderByExpression orderByExpression = viewCountColumn.descending();

			if (orderByComparator.isAscending()) {
				orderByExpression = viewCountColumn.ascending();
			}

			dslQuery = joinStep.leftJoinOn(
				viewCountEntryTable,
				viewCountEntryTable.getColumn(
					"classNameId", Long.class
				).eq(
					ClassNameLocalServiceUtil.getClassNameId(DLFileEntry.class)
				).and(
					viewCountEntryTable.getColumn(
						"classPK", Long.class
					).eq(
						DLFileEntryTable.INSTANCE.fileEntryId
					)
				).and(
					viewCountEntryTable.getColumn(
						"companyId", Long.class
					).eq(
						DLFileEntryTable.INSTANCE.companyId
					)
				)
			).orderBy(
				orderByExpression
			);
		}
		else {
			dslQuery = joinStep.orderBy(
				DLFileEntryTable.INSTANCE, orderByComparator);
		}

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(dslQuery);

			sqlQuery.addEntity(
				DLFileEntryImpl.TABLE_NAME, DLFileEntryImpl.class);

			return (List<DLFileEntry>)QueryUtil.list(
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

	protected String getDDMStructureIds(long[] ddmStructureIds) {
		StringBundler sb = new StringBundler(
			((ddmStructureIds.length * 2) - 1) + 2);

		sb.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 0; i < ddmStructureIds.length; i++) {
			sb.append("DDMStructureLink.structureId = ?");

			if ((i + 1) == ddmStructureIds.length) {
				continue;
			}

			sb.append(WHERE_OR);
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	protected String getFileEntriesSQL(
		String id, long groupId, List<Long> repositoryIds, List<Long> folderIds,
		String[] mimeTypes, QueryDefinition<DLFileEntry> queryDefinition,
		boolean inlineSQLHelper) {

		String tableName = DLFileVersionImpl.TABLE_NAME;

		String sql = CustomSQLUtil.get(id, queryDefinition, tableName);

		if (queryDefinition.getStatus() == WorkflowConstants.STATUS_ANY) {
			sql = StringUtil.removeSubstring(sql, "[$JOIN$]");

			tableName = DLFileEntryImpl.TABLE_NAME;
		}
		else {
			sql = StringUtil.replace(
				sql, "[$JOIN$]",
				CustomSQLUtil.get(
					DLFolderFinderImpl.JOIN_FE_BY_DL_FILE_VERSION));
		}

		if (inlineSQLHelper && InlineSQLHelperUtil.isEnabled()) {
			if (queryDefinition.getStatus() == WorkflowConstants.STATUS_ANY) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, DLFileEntry.class.getName(), "DLFileEntry.fileEntryId",
					groupId);
			}
			else {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, DLFileEntry.class.getName(),
					"DLFileVersion.fileEntryId", groupId);
			}
		}

		if (ListUtil.isNotEmpty(repositoryIds) ||
			ListUtil.isNotEmpty(folderIds) || ArrayUtil.isNotEmpty(mimeTypes)) {

			StringBundler sb = new StringBundler(12);

			if (ListUtil.isNotEmpty(repositoryIds)) {
				sb.append(WHERE_AND);
				sb.append(StringPool.OPEN_PARENTHESIS);
				sb.append(getRepositoryIds(repositoryIds, tableName));
				sb.append(StringPool.CLOSE_PARENTHESIS);
			}

			if (ListUtil.isNotEmpty(folderIds)) {
				sb.append(WHERE_AND);
				sb.append(StringPool.OPEN_PARENTHESIS);
				sb.append(getFolderIds(folderIds, tableName));
				sb.append(StringPool.CLOSE_PARENTHESIS);
			}

			if (ArrayUtil.isNotEmpty(mimeTypes)) {
				sb.append(WHERE_AND);
				sb.append(StringPool.OPEN_PARENTHESIS);
				sb.append(getMimeTypes(mimeTypes, tableName));
				sb.append(StringPool.CLOSE_PARENTHESIS);
			}

			return StringUtil.replace(sql, "[$FOLDER_ID$]", sb.toString());
		}

		return StringUtil.removeSubstring(sql, "[$FOLDER_ID$]");
	}

	protected String getFolderIds(List<Long> folderIds, String tableName) {
		if (folderIds.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler((folderIds.size() * 3) + 1);

		sb.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 0; i < folderIds.size(); i++) {
			sb.append(tableName);
			sb.append(".folderId = ");
			sb.append(folderIds.get(i));

			if ((i + 1) == folderIds.size()) {
				continue;
			}

			sb.append(WHERE_OR);
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

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

	protected String getRepositoryIds(
		List<Long> repositoryIds, String tableName) {

		if (repositoryIds.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler((repositoryIds.size() * 3) + 1);

		sb.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 0; i < repositoryIds.size(); i++) {
			sb.append(tableName);
			sb.append(".repositoryId = ? ");

			if ((i + 1) == repositoryIds.size()) {
				continue;
			}

			sb.append(WHERE_OR);
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	private JoinStep _getJoinStep(
		FromStep fromStep, List<Long> folderIds, long groupId,
		boolean inlineSQLHelper, String[] mimeTypes,
		QueryDefinition<DLFileEntry> queryDefinition, List<Long> repositoryIds,
		long userId) {

		JoinStep joinStep = DSLQueryFactoryUtil.selectDistinct(
			DLFileEntryTable.INSTANCE.fileEntryId
		).from(
			DLFileEntryTable.INSTANCE
		);

		if ((userId > 0) ||
			(queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY)) {

			joinStep = joinStep.innerJoinON(
				DLFileVersionTable.INSTANCE,
				DLFileEntryTable.INSTANCE.fileEntryId.eq(
					DLFileVersionTable.INSTANCE.fileEntryId));
		}

		Predicate wherePredicate = DLFileEntryTable.INSTANCE.groupId.eq(
			groupId);

		if (userId > 0) {
			wherePredicate = wherePredicate.and(
				Predicate.withParentheses(
					DLFileEntryTable.INSTANCE.userId.eq(
						userId
					).or(
						DLFileVersionTable.INSTANCE.userId.eq(userId)
					)));
		}

		if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
			if (queryDefinition.isExcludeStatus()) {
				wherePredicate = wherePredicate.and(
					DLFileVersionTable.INSTANCE.status.neq(
						queryDefinition.getStatus()));
			}
			else {
				wherePredicate = wherePredicate.and(
					DLFileVersionTable.INSTANCE.status.eq(
						queryDefinition.getStatus()));
			}
		}

		if (ListUtil.isNotEmpty(repositoryIds)) {
			Predicate repositoriesPredicate =
				DLFileEntryTable.INSTANCE.repositoryId.eq(repositoryIds.get(0));

			for (int i = 1; i < repositoryIds.size(); i++) {
				repositoriesPredicate = repositoriesPredicate.or(
					DLFileEntryTable.INSTANCE.repositoryId.eq(
						repositoryIds.get(i)));
			}

			wherePredicate = wherePredicate.and(
				Predicate.withParentheses(repositoriesPredicate));
		}

		if (ListUtil.isNotEmpty(folderIds)) {
			Predicate foldersPredicate = DLFileEntryTable.INSTANCE.folderId.eq(
				folderIds.get(0));

			for (int i = 1; i < folderIds.size(); i++) {
				foldersPredicate = foldersPredicate.or(
					DLFileEntryTable.INSTANCE.folderId.eq(folderIds.get(i)));
			}

			wherePredicate = wherePredicate.and(
				Predicate.withParentheses(foldersPredicate));
		}

		if (ArrayUtil.isNotEmpty(mimeTypes)) {
			Predicate mimeTypesPredicate =
				DLFileEntryTable.INSTANCE.mimeType.eq(mimeTypes[0]);

			for (int i = 1; i < mimeTypes.length; i++) {
				mimeTypesPredicate = mimeTypesPredicate.or(
					DLFileEntryTable.INSTANCE.mimeType.eq(mimeTypes[i]));
			}

			wherePredicate = wherePredicate.and(
				Predicate.withParentheses(mimeTypesPredicate));
		}

		return fromStep.from(
			joinStep.where(
				wherePredicate.and(
					() -> {
						if (inlineSQLHelper) {
							if (queryDefinition.getStatus() ==
									WorkflowConstants.STATUS_ANY) {

								return InlineSQLHelperUtil.
									getPermissionWherePredicate(
										DLFileEntry.class,
										DLFileEntryTable.INSTANCE.fileEntryId,
										groupId);
							}

							return InlineSQLHelperUtil.
								getPermissionWherePredicate(
									DLFileEntry.class,
									DLFileVersionTable.INSTANCE.fileEntryId,
									groupId);
						}

						return null;
					})
			).as(
				"tempDLFileEntry"
			)
		).innerJoinON(
			DLFileEntryTable.INSTANCE,
			DLFileEntryTable.INSTANCE.as(
				"tempDLFileEntry"
			).fileEntryId.eq(
				DLFileEntryTable.INSTANCE.fileEntryId
			)
		);
	}

	private boolean _isOrderByReadCount(
		OrderByComparator<DLFileEntry> orderByComparator) {

		if ((orderByComparator != null) &&
			(StringUtil.containsIgnoreCase(
				orderByComparator.getOrderBy(), "readCount",
				StringPool.COMMA) ||
			 StringUtil.containsIgnoreCase(
				 orderByComparator.getOrderBy(), "readCount ASC",
				 StringPool.COMMA) ||
			 StringUtil.containsIgnoreCase(
				 orderByComparator.getOrderBy(), "readCount DESC",
				 StringPool.COMMA))) {

			return true;
		}

		return false;
	}

}