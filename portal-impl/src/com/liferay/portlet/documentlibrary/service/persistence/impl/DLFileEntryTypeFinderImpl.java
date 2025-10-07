/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.persistence.impl;

import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryTypeFinder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryTypeImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.util.Iterator;
import java.util.List;

/**
 * @author Sergio González
 * @author Connor McKay
 * @author Alexander Chow
 */
public class DLFileEntryTypeFinderImpl
	extends DLFileEntryTypeFinderBaseImpl implements DLFileEntryTypeFinder {

	public static final String COUNT_BY_C_G_N_D_S =
		DLFileEntryTypeFinder.class.getName() + ".countByC_G_N_D_S";

	public static final String COUNT_BY_C_F_G_N_D_S =
		DLFileEntryTypeFinder.class.getName() + ".countByC_F_G_N_D_S";

	public static final String FIND_BY_C_G_N_D_S =
		DLFileEntryTypeFinder.class.getName() + ".findByC_G_N_D_S";

	public static final String FIND_BY_C_F_G_N_D_S =
		DLFileEntryTypeFinder.class.getName() + ".findByC_F_G_N_D_S";

	@Override
	public int countByKeywords(
		long companyId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType) {

		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = CustomSQLUtil.keywords(keywords);
			descriptions = CustomSQLUtil.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return doCountByC_G_N_D_S(
			companyId, groupIds, names, descriptions, andOperator,
			includeBasicFileEntryType, false);
	}

	@Override
	public int filterCountByKeywords(
		long companyId, long folderId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType, boolean inherited) {

		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = CustomSQLUtil.keywords(keywords);
			descriptions = CustomSQLUtil.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return doCountByC_F_G_N_D_S(
			companyId, folderId, groupIds, names, descriptions, andOperator,
			includeBasicFileEntryType, inherited, true);
	}

	@Override
	public int filterCountByKeywords(
		long companyId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType) {

		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = CustomSQLUtil.keywords(keywords);
			descriptions = CustomSQLUtil.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return doCountByC_G_N_D_S(
			companyId, groupIds, names, descriptions, andOperator,
			includeBasicFileEntryType, true);
	}

	@Override
	public List<DLFileEntryType> filterFindByKeywords(
		long companyId, long folderId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType, boolean inherited, int start,
		int end) {

		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = CustomSQLUtil.keywords(keywords);
			descriptions = CustomSQLUtil.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return doFindByC_F_G_N_D_S(
			companyId, folderId, groupIds, names, descriptions, andOperator,
			includeBasicFileEntryType, inherited, start, end, true);
	}

	@Override
	public List<DLFileEntryType> filterFindByKeywords(
		long companyId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType, int start, int end,
		OrderByComparator<DLFileEntryType> orderByComparator) {

		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = CustomSQLUtil.keywords(keywords);
			descriptions = CustomSQLUtil.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return doFindByC_G_N_D_S(
			companyId, groupIds, names, descriptions, andOperator,
			includeBasicFileEntryType, start, end, orderByComparator, true);
	}

	@Override
	public List<DLFileEntryType> findByKeywords(
		long companyId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType, int start, int end,
		OrderByComparator<DLFileEntryType> orderByComparator) {

		String[] names = null;
		String[] descriptions = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = CustomSQLUtil.keywords(keywords);
			descriptions = CustomSQLUtil.keywords(keywords, false);
		}
		else {
			andOperator = true;
		}

		return doFindByC_G_N_D_S(
			companyId, groupIds, names, descriptions, andOperator,
			includeBasicFileEntryType, start, end, orderByComparator, false);
	}

	protected int doCountByC_G_N_D_S(
		long companyId, long[] groupIds, String[] names, String[] descriptions,
		boolean andOperator, boolean includeBasicFileEntryType,
		boolean inlineSQLHelper) {

		names = CustomSQLUtil.keywords(names);
		descriptions = CustomSQLUtil.keywords(descriptions, false);

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_C_G_N_D_S);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, DLFileEntryType.class.getName(),
					"DLFileEntryType.fileEntryTypeId", groupIds);
			}

			sql = StringUtil.replace(
				sql, "[$BASIC_DOCUMENT$]",
				getBasicDocumentCount(includeBasicFileEntryType));
			sql = StringUtil.replace(
				sql, "[$GROUP_ID$]", getGroupIds(groupIds.length));
			sql = CustomSQLUtil.replaceKeywords(
				sql, "LOWER(DLFileEntryType.name)", StringPool.LIKE, false,
				names);
			sql = CustomSQLUtil.replaceKeywords(
				sql, "DLFileEntryType.description", StringPool.LIKE, true,
				descriptions);
			sql = CustomSQLUtil.replaceAndOperator(sql, andOperator);

			if (includeBasicFileEntryType) {
				sql = sql.concat(StringPool.CLOSE_PARENTHESIS);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (includeBasicFileEntryType) {
				queryPos.add(names, 2);
				queryPos.add(descriptions, 2);
			}

			queryPos.add(companyId);
			queryPos.add(groupIds);
			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);

			int countValue = 0;

			Iterator<Long> iterator = sqlQuery.iterate();

			while (iterator.hasNext()) {
				Long count = iterator.next();

				if (count != null) {
					countValue += count.intValue();
				}
			}

			return countValue;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected int doCountByC_F_G_N_D_S(
		long companyId, long folderId, long[] groupIds, String[] names,
		String[] descriptions, boolean andOperator,
		boolean includeBasicFileEntryType, boolean inherited,
		boolean inlineSQLHelper) {

		names = CustomSQLUtil.keywords(names);
		descriptions = CustomSQLUtil.keywords(descriptions, false);

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_C_F_G_N_D_S);

			if (inherited) {
				sql = StringUtil.removeSubstring(sql, _INNER_JOIN_SQL);

				sql = StringUtil.removeSubstring(sql, _WHERE_SQL);
			}

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, DLFileEntryType.class.getName(),
					"DLFileEntryType.fileEntryTypeId", groupIds);
			}

			sql = StringUtil.replace(
				sql, "[$BASIC_DOCUMENT$]",
				getBasicDocumentCount(includeBasicFileEntryType));
			sql = StringUtil.replace(
				sql, "[$GROUP_ID$]", getGroupIds(groupIds.length));
			sql = CustomSQLUtil.replaceKeywords(
				sql, "LOWER(DLFileEntryType.name)", StringPool.LIKE, false,
				names);
			sql = CustomSQLUtil.replaceKeywords(
				sql, "DLFileEntryType.description", StringPool.LIKE, true,
				descriptions);
			sql = CustomSQLUtil.replaceAndOperator(sql, andOperator);

			if (includeBasicFileEntryType) {
				sql = sql.concat(StringPool.CLOSE_PARENTHESIS);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (includeBasicFileEntryType) {
				queryPos.add(names, 2);
				queryPos.add(descriptions, 2);
			}

			queryPos.add(companyId);

			if (!inherited) {
				queryPos.add(folderId);
			}

			queryPos.add(groupIds);
			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);

			int countValue = 0;

			Iterator<Long> iterator = sqlQuery.iterate();

			while (iterator.hasNext()) {
				Long count = iterator.next();

				if (count != null) {
					countValue += count.intValue();
				}
			}

			return countValue;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected List<DLFileEntryType> doFindByC_G_N_D_S(
		long companyId, long[] groupIds, String[] names, String[] descriptions,
		boolean andOperator, boolean includeBasicFileEntryType, int start,
		int end, OrderByComparator<DLFileEntryType> orderByComparator,
		boolean inlineSQLHelper) {

		names = CustomSQLUtil.keywords(names);
		descriptions = CustomSQLUtil.keywords(descriptions, false);

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_C_G_N_D_S);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, DLFileEntryType.class.getName(),
					"DLFileEntryType.fileEntryTypeId", groupIds);
			}

			sql = StringUtil.replace(
				sql, "[$BASIC_DOCUMENT$]",
				getBasicDocument(includeBasicFileEntryType));
			sql = StringUtil.replace(
				sql, "[$GROUP_ID$]", getGroupIds(groupIds.length));
			sql = CustomSQLUtil.replaceKeywords(
				sql, "LOWER(DLFileEntryType.name)", StringPool.LIKE, false,
				names);
			sql = CustomSQLUtil.replaceKeywords(
				sql, "DLFileEntryType.description", StringPool.LIKE, true,
				descriptions);
			sql = CustomSQLUtil.replaceAndOperator(sql, andOperator);

			if (includeBasicFileEntryType) {
				sql = sql.concat(StringPool.CLOSE_PARENTHESIS);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("DLFileEntryType", DLFileEntryTypeImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (includeBasicFileEntryType) {
				queryPos.add(names, 2);
				queryPos.add(descriptions, 2);
			}

			queryPos.add(companyId);
			queryPos.add(groupIds);
			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);

			return (List<DLFileEntryType>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected List<DLFileEntryType> doFindByC_F_G_N_D_S(
		long companyId, long folderId, long[] groupIds, String[] names,
		String[] descriptions, boolean andOperator,
		boolean includeBasicFileEntryType, boolean inherited, int start,
		int end, boolean inlineSQLHelper) {

		names = CustomSQLUtil.keywords(names);
		descriptions = CustomSQLUtil.keywords(descriptions, false);

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_C_F_G_N_D_S);

			if (inherited) {
				sql = StringUtil.removeSubstring(sql, _INNER_JOIN_SQL);

				sql = StringUtil.removeSubstring(sql, _WHERE_SQL);
			}

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, DLFileEntryType.class.getName(),
					"DLFileEntryType.fileEntryTypeId", groupIds);
			}

			sql = StringUtil.replace(
				sql, "[$BASIC_DOCUMENT$]",
				getBasicDocument(includeBasicFileEntryType));
			sql = StringUtil.replace(
				sql, "[$GROUP_ID$]", getGroupIds(groupIds.length));
			sql = CustomSQLUtil.replaceKeywords(
				sql, "LOWER(DLFileEntryType.name)", StringPool.LIKE, false,
				names);
			sql = CustomSQLUtil.replaceKeywords(
				sql, "DLFileEntryType.description", StringPool.LIKE, true,
				descriptions);
			sql = CustomSQLUtil.replaceAndOperator(sql, andOperator);

			if (includeBasicFileEntryType) {
				sql = sql.concat(StringPool.CLOSE_PARENTHESIS);
			}

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addEntity("DLFileEntryType", DLFileEntryTypeImpl.class);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (includeBasicFileEntryType) {
				queryPos.add(names, 2);
				queryPos.add(descriptions, 2);
			}

			queryPos.add(companyId);

			if (!inherited) {
				queryPos.add(folderId);
			}

			queryPos.add(groupIds);
			queryPos.add(names, 2);
			queryPos.add(descriptions, 2);

			return (List<DLFileEntryType>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected String getBasicDocument(boolean includeBasicFileEntryType) {
		if (!includeBasicFileEntryType) {
			return StringPool.BLANK;
		}

		return getBasicDocument(
			"(SELECT {DLFileEntryType.*} From DLFileEntryType WHERE ");
	}

	protected String getBasicDocument(String prefix) {
		return StringBundler.concat(
			prefix, "((DLFileEntryType.companyId = 0) AND (DLFileEntryType.",
			"groupId = 0) AND ((LOWER(DLFileEntryType.name) LIKE ? ",
			"[$AND_OR_NULL_CHECK$]) [$AND_OR_CONNECTOR$] (DLFileEntryType.",
			"description LIKE ? [$AND_OR_NULL_CHECK$]) ))) UNION ALL (");
	}

	protected String getBasicDocumentCount(boolean includeBasicFileEntryType) {
		if (!includeBasicFileEntryType) {
			return StringPool.BLANK;
		}

		return getBasicDocument(
			"(SELECT COUNT(*) AS COUNT_VALUE From DLFileEntryType WHERE ");
	}

	protected String getGroupIds(int size) {
		if (size == 0) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(size + 1);

		sb.append(StringPool.OPEN_PARENTHESIS);

		for (int i = 0; i < (size - 1); i++) {
			sb.append("DLFileEntryType.groupId = ? OR ");
		}

		sb.append("DLFileEntryType.groupId = ?) AND");

		return sb.toString();
	}

	private static final String _INNER_JOIN_SQL =
		"INNER JOIN DLFileEntryTypes_DLFolders ON " +
			"DLFileEntryTypes_DLFolders.fileEntryTypeId = " +
				"DLFileEntryType.fileEntryTypeId";

	private static final String _WHERE_SQL =
		"(DLFileEntryTypes_DLFolders.folderId = ?) AND";

}