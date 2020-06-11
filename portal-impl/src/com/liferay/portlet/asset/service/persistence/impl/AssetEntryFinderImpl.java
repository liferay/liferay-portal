/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portlet.asset.service.persistence.impl;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.persistence.AssetCategoryUtil;
import com.liferay.asset.kernel.service.persistence.AssetEntryFinder;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.asset.model.impl.AssetEntryImpl;
import com.liferay.portlet.documentlibrary.service.persistence.impl.DLFileEntryFinderImpl;
import com.liferay.portlet.documentlibrary.service.persistence.impl.DLFolderFinderImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Shuyang Zhou
 */
public class AssetEntryFinderImpl
	extends AssetEntryFinderBaseImpl implements AssetEntryFinder {

	public static final String FIND_BY_AND_CATEGORY_IDS =
		AssetEntryFinder.class.getName() + ".findByAndCategoryIds";

	public static final String FIND_BY_AND_TAG_IDS =
		AssetEntryFinder.class.getName() + ".findByAndTagIds";

	public static final String FIND_BY_CLASS_NAME_ID =
		AssetEntryFinder.class.getName() + ".findByClassNameId";

	@Override
	public int countEntries(AssetEntryQuery entryQuery) {
		Session session = null;

		try {
			session = openSession();

			SQLQuery q = buildAssetQuerySQL(entryQuery, true, session);

			Iterator<Long> itr = q.iterate();

			if (itr.hasNext()) {
				Long count = itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<AssetEntry> findByDLFileEntryC_T(
		long classNameId, String treePath) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_CLASS_NAME_ID);

			sql = StringUtil.replace(
				sql, "[$JOIN$]",
				CustomSQLUtil.get(
					DLFileEntryFinderImpl.JOIN_AE_BY_DL_FILE_ENTRY));
			sql = StringUtil.replace(
				sql, "[$WHERE$]", "DLFileEntry.treePath LIKE ? AND");

			SQLQuery q = session.createSynchronizedSQLQuery(sql);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(
				CustomSQLUtil.keywords(treePath, WildcardMode.TRAILING)[0]);
			qPos.add(classNameId);

			q.addEntity(AssetEntryImpl.TABLE_NAME, AssetEntryImpl.class);

			return q.list(true);
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<AssetEntry> findByDLFolderC_T(
		long classNameId, String treePath) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_CLASS_NAME_ID);

			sql = StringUtil.replace(
				sql, "[$JOIN$]",
				CustomSQLUtil.get(DLFolderFinderImpl.JOIN_AE_BY_DL_FOLDER));
			sql = StringUtil.replace(
				sql, "[$WHERE$]", "DLFolder.treePath LIKE ? AND");

			SQLQuery q = session.createSynchronizedSQLQuery(sql);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(
				CustomSQLUtil.keywords(treePath, WildcardMode.TRAILING)[0]);
			qPos.add(classNameId);

			q.addEntity(AssetEntryImpl.TABLE_NAME, AssetEntryImpl.class);

			return q.list(true);
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	public List<AssetEntry> findEntries(AssetEntryQuery entryQuery) {
		Session session = null;

		try {
			session = openSession();

			SQLQuery q = buildAssetQuerySQL(entryQuery, false, session);

			return (List<AssetEntry>)QueryUtil.list(
				q, getDialect(), entryQuery.getStart(), entryQuery.getEnd());
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	protected void buildAllCategoriesSQL(long[] categoryIds, StringBundler sb) {
		String findByAndCategoryIdsSQL = CustomSQLUtil.get(
			FIND_BY_AND_CATEGORY_IDS);

		sb.append(" AND (");

		for (int i = 0; i < categoryIds.length; i++) {
			String sql = null;

			if (PropsValues.ASSET_CATEGORIES_SEARCH_HIERARCHICAL) {
				List<Long> treeCategoryIds = getSubcategoryIds(categoryIds[i]);

				if (treeCategoryIds.size() > 1) {
					sql = StringUtil.replace(
						findByAndCategoryIdsSQL, "[$CATEGORY_ID$]",
						StringUtil.merge(treeCategoryIds));
				}
			}

			if (sql == null) {
				sql = StringUtil.replace(
					findByAndCategoryIdsSQL, " IN ([$CATEGORY_ID$])",
					" = " + categoryIds[i]);
			}

			sb.append(sql);

			if ((i + 1) < categoryIds.length) {
				sb.append(" AND ");
			}
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);
	}

	protected void buildAllTagsSQL(long[][] tagIds, StringBundler sb) {
		sb.append(" AND AssetEntry.entryId IN (");

		for (int i = 0; i < tagIds.length; i++) {
			String sql = CustomSQLUtil.get(FIND_BY_AND_TAG_IDS);

			sql = StringUtil.replace(sql, "[$TAG_ID$]", getTagIds(tagIds[i]));

			sb.append(sql);

			if ((i + 1) < tagIds.length) {
				sb.append(" AND AssetEntry.entryId IN (");
			}
		}

		for (int i = 0; i < tagIds.length; i++) {
			if ((i + 1) < tagIds.length) {
				sb.append(StringPool.CLOSE_PARENTHESIS);
			}
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);
	}

	protected void buildAnyCategoriesSQL(long[] categoryIds, StringBundler sb) {
		String sql = CustomSQLUtil.get(FIND_BY_AND_CATEGORY_IDS);

		String categoryIdsString = null;

		if (PropsValues.ASSET_CATEGORIES_SEARCH_HIERARCHICAL) {
			List<Long> categoryIdsList = new ArrayList<>();

			for (long categoryId : categoryIds) {
				categoryIdsList.addAll(getSubcategoryIds(categoryId));
			}

			if (categoryIdsList.isEmpty()) {
				return;
			}

			categoryIdsString = StringUtil.merge(categoryIdsList);
		}
		else {
			categoryIdsString = StringUtil.merge(categoryIds);
		}

		sb.append(" AND (");
		sb.append(
			StringUtil.replace(sql, "[$CATEGORY_ID$]", categoryIdsString));
		sb.append(StringPool.CLOSE_PARENTHESIS);
	}

	protected String buildAnyTagsSQL(long[] tagIds, StringBundler sb) {
		sb.append(" AND (");

		for (int i = 0; i < tagIds.length; i++) {
			sb.append("AssetTag.tagId = ");
			sb.append(tagIds[i]);

			if ((i + 1) != tagIds.length) {
				sb.append(" OR ");
			}
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	protected SQLQuery buildAssetQuerySQL(
		AssetEntryQuery entryQuery, boolean count, Session session) {

		StringBundler sb = new StringBundler(52);

		if (count) {
			sb.append(
				"SELECT COUNT(DISTINCT AssetEntry.entryId) AS COUNT_VALUE ");
		}
		else {
			sb.append("SELECT {AssetEntry.*} ");

			boolean selectRatings = false;

			String orderByCol1 = entryQuery.getOrderByCol1();
			String orderByCol2 = entryQuery.getOrderByCol2();

			if (orderByCol1.equals("ratings") ||
				orderByCol2.equals("ratings")) {

				selectRatings = true;

				sb.append(", TEMP_TABLE.averageScore ");
			}

			sb.append("FROM (SELECT DISTINCT AssetEntry.entryId ");

			if (selectRatings) {
				sb.append(", RatingsStats.averageScore ");
			}
		}

		sb.append("FROM AssetEntry ");

		if (ArrayUtil.isNotEmpty(entryQuery.getAnyTagIds())) {
			sb.append("INNER JOIN AssetEntries_AssetTags ON ");
			sb.append("(AssetEntries_AssetTags.entryId = AssetEntry.entryId) ");
			sb.append("INNER JOIN AssetTag ON (AssetTag.tagId = ");
			sb.append("AssetEntries_AssetTags.tagId) ");
		}

		if (entryQuery.getLinkedAssetEntryId() > 0) {
			sb.append("INNER JOIN AssetLink ON (AssetEntry.entryId = ");
			sb.append("AssetLink.entryId1) OR (AssetEntry.entryId = ");
			sb.append("AssetLink.entryId2)");
		}

		String orderByCol1 = AssetEntryQuery.ORDER_BY_COLUMNS[2];
		String orderByCol2 = AssetEntryQuery.ORDER_BY_COLUMNS[2];

		for (String orderByColumn : AssetEntryQuery.ORDER_BY_COLUMNS) {
			if (orderByColumn.equals(entryQuery.getOrderByCol1())) {
				orderByCol1 = orderByColumn;
			}

			if (orderByColumn.equals(entryQuery.getOrderByCol2())) {
				orderByCol2 = orderByColumn;
			}
		}

		if (orderByCol1.equals("ratings") || orderByCol2.equals("ratings")) {
			sb.append(" LEFT JOIN RatingsStats ON (RatingsStats.classNameId ");
			sb.append("= AssetEntry.classNameId) AND (RatingsStats.classPK = ");
			sb.append("AssetEntry.classPK)");
		}

		sb.append("WHERE ");

		int whereIndex = sb.index();

		if (entryQuery.getLinkedAssetEntryId() > 0) {
			sb.append(" AND ((AssetLink.entryId1 = ?) OR (AssetLink.entryId2 ");
			sb.append("= ?)) AND (AssetEntry.entryId != ?)");
		}

		if (entryQuery.isListable() != null) {
			sb.append(" AND (listable = ?)");
		}

		if (entryQuery.isVisible() != null) {
			sb.append(" AND (visible = ?)");
		}

		if (entryQuery.isExcludeZeroViewCount()) {
			sb.append(" AND (AssetEntry.viewCount > 0)");
		}

		// Keywords

		if (Validator.isNotNull(entryQuery.getKeywords())) {
			sb.append(" AND ((AssetEntry.userName LIKE ?) OR ");
			sb.append("(AssetEntry.title LIKE ?) OR (AssetEntry.description ");
			sb.append("LIKE ?))");
		}
		else if (Validator.isNotNull(entryQuery.getUserName()) ||
				 Validator.isNotNull(entryQuery.getTitle()) ||
				 Validator.isNotNull(entryQuery.getDescription())) {

			sb.append(" AND (");

			boolean requiresOperator = false;

			if (Validator.isNotNull(entryQuery.getUserName())) {
				sb.append("(AssetEntry.userName LIKE ?)");

				requiresOperator = true;
			}

			if (Validator.isNotNull(entryQuery.getTitle())) {
				if (requiresOperator) {
					sb.append(entryQuery.isAndOperator() ? " AND " : " OR ");
				}

				sb.append("(AssetEntry.title LIKE ?)");

				requiresOperator = true;
			}

			if (Validator.isNotNull(entryQuery.getDescription())) {
				if (requiresOperator) {
					sb.append(entryQuery.isAndOperator() ? " AND " : " OR ");
				}

				sb.append("(AssetEntry.description LIKE ?)");
			}

			sb.append(")");
		}

		// Layout

		Layout layout = entryQuery.getLayout();

		if (layout != null) {
			sb.append(" AND (AssetEntry.layoutUuid = ?)");
		}

		// Category conditions

		if (ArrayUtil.isNotEmpty(entryQuery.getAllCategoryIds())) {
			buildAllCategoriesSQL(entryQuery.getAllCategoryIds(), sb);
		}

		if (ArrayUtil.isNotEmpty(entryQuery.getAnyCategoryIds())) {
			buildAnyCategoriesSQL(entryQuery.getAnyCategoryIds(), sb);
		}

		if (ArrayUtil.isNotEmpty(entryQuery.getNotAllCategoryIds())) {
			buildNotAllCategoriesSQL(entryQuery.getNotAllCategoryIds(), sb);
		}

		if (ArrayUtil.isNotEmpty(entryQuery.getNotAnyCategoryIds())) {
			buildNotAnyCategoriesSQL(entryQuery.getNotAnyCategoryIds(), sb);
		}

		// Asset entry subtypes

		if (ArrayUtil.isNotEmpty(entryQuery.getClassTypeIds())) {
			buildClassTypeIdsSQL(entryQuery.getClassTypeIds(), sb);
		}

		// Tag conditions

		if (ArrayUtil.isNotEmpty(entryQuery.getAllTagIds())) {
			buildAllTagsSQL(entryQuery.getAllTagIdsArray(), sb);
		}

		if (ArrayUtil.isNotEmpty(entryQuery.getAnyTagIds())) {
			buildAnyTagsSQL(entryQuery.getAnyTagIds(), sb);
		}

		if (ArrayUtil.isNotEmpty(entryQuery.getNotAllTagIds())) {
			buildNotAllTagsSQL(entryQuery.getNotAllTagIdsArray(), sb);
		}

		if (ArrayUtil.isNotEmpty(entryQuery.getNotAnyTagIds())) {
			buildNotAnyTagsSQL(entryQuery.getNotAnyTagIds(), sb);
		}

		// Other conditions

		sb.append(
			getDates(
				entryQuery.getPublishDate(), entryQuery.getExpirationDate()));
		sb.append(getGroupIds(entryQuery.getGroupIds()));
		sb.append(getClassNameIds(entryQuery.getClassNameIds()));

		if (!count) {
			sb.append(") TEMP_TABLE INNER JOIN AssetEntry AssetEntry ON ");
			sb.append("TEMP_TABLE.entryId = AssetEntry.entryId ORDER BY ");

			if (orderByCol1.equals("ratings")) {
				sb.append("TEMP_TABLE.averageScore");
			}
			else {
				sb.append("AssetEntry.");
				sb.append(orderByCol1);
			}

			sb.append(StringPool.SPACE);
			sb.append(entryQuery.getOrderByType1());

			if (Validator.isNotNull(orderByCol2) &&
				!orderByCol1.equals(orderByCol2)) {

				if (orderByCol2.equals("ratings")) {
					sb.append(", TEMP_TABLE.averageScore");
				}
				else {
					sb.append(", AssetEntry.");
					sb.append(orderByCol2);
				}

				sb.append(StringPool.SPACE);
				sb.append(entryQuery.getOrderByType2());
			}
		}

		if (sb.index() > whereIndex) {
			String where = sb.stringAt(whereIndex);

			if (where.startsWith(" AND")) {
				sb.setStringAt(where.substring(4), whereIndex);
			}
		}

		String sql = sb.toString();

		SQLQuery q = session.createSynchronizedSQLQuery(sql);

		if (count) {
			q.addScalar(COUNT_COLUMN_NAME, Type.LONG);
		}
		else {
			q.addEntity("AssetEntry", AssetEntryImpl.class);
		}

		QueryPos qPos = QueryPos.getInstance(q);

		if (entryQuery.getLinkedAssetEntryId() > 0) {
			qPos.add(entryQuery.getLinkedAssetEntryId());
			qPos.add(entryQuery.getLinkedAssetEntryId());
			qPos.add(entryQuery.getLinkedAssetEntryId());
		}

		if (entryQuery.isListable() != null) {
			qPos.add(entryQuery.isListable());
		}

		if (entryQuery.isVisible() != null) {
			qPos.add(entryQuery.isVisible());
		}

		if (Validator.isNotNull(entryQuery.getKeywords())) {
			qPos.add(
				StringUtil.quote(entryQuery.getKeywords(), StringPool.PERCENT));
			qPos.add(
				StringUtil.quote(entryQuery.getKeywords(), StringPool.PERCENT));
			qPos.add(
				StringUtil.quote(entryQuery.getKeywords(), StringPool.PERCENT));
		}
		else {
			if (Validator.isNotNull(entryQuery.getUserName())) {
				qPos.add(
					StringUtil.quote(
						entryQuery.getUserName(), StringPool.PERCENT));
			}

			if (Validator.isNotNull(entryQuery.getTitle())) {
				qPos.add(
					StringUtil.quote(
						entryQuery.getTitle(), StringPool.PERCENT));
			}

			if (Validator.isNotNull(entryQuery.getDescription())) {
				qPos.add(
					StringUtil.quote(
						entryQuery.getDescription(), StringPool.PERCENT));
			}
		}

		if (layout != null) {
			qPos.add(layout.getUuid());
		}

		setDates(
			qPos, entryQuery.getPublishDate(), entryQuery.getExpirationDate());

		qPos.add(entryQuery.getGroupIds());
		qPos.add(entryQuery.getClassNameIds());

		return q;
	}

	protected void buildClassTypeIdsSQL(long[] classTypeIds, StringBundler sb) {
		sb.append(" AND (");

		for (int i = 0; i < classTypeIds.length; i++) {
			sb.append(" AssetEntry.classTypeId = ");
			sb.append(classTypeIds[i]);

			if ((i + 1) < classTypeIds.length) {
				sb.append(" OR ");
			}
			else {
				sb.append(StringPool.CLOSE_PARENTHESIS);
			}
		}
	}

	protected void buildNotAllCategoriesSQL(
		long[] categoryIds, StringBundler sb) {

		String findByAndCategoryIdsSQL = CustomSQLUtil.get(
			FIND_BY_AND_CATEGORY_IDS);

		sb.append(" AND (");

		for (int i = 0; i < categoryIds.length; i++) {
			sb.append("NOT ");

			String sql = null;

			if (PropsValues.ASSET_CATEGORIES_SEARCH_HIERARCHICAL) {
				List<Long> treeCategoryIds = getSubcategoryIds(categoryIds[i]);

				if (treeCategoryIds.size() > 1) {
					sql = StringUtil.replace(
						findByAndCategoryIdsSQL, "[$CATEGORY_ID$]",
						StringUtil.merge(treeCategoryIds));
				}
			}

			if (sql == null) {
				sql = StringUtil.replace(
					findByAndCategoryIdsSQL, " IN ([$CATEGORY_ID$])",
					" = " + categoryIds[i]);
			}

			sb.append(sql);

			if ((i + 1) < categoryIds.length) {
				sb.append(" OR ");
			}
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);
	}

	protected void buildNotAllTagsSQL(long[][] tagIds, StringBundler sb) {
		sb.append(" AND (");

		for (int i = 0; i < tagIds.length; i++) {
			sb.append("AssetEntry.entryId NOT IN (");

			String sql = CustomSQLUtil.get(FIND_BY_AND_TAG_IDS);

			sql = StringUtil.replace(sql, "[$TAG_ID$]", getTagIds(tagIds[i]));

			sb.append(sql);

			sb.append(StringPool.CLOSE_PARENTHESIS);

			if (((i + 1) < tagIds.length) && (tagIds[i + 1].length > 0)) {
				sb.append(" OR ");
			}
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);
	}

	protected void buildNotAnyCategoriesSQL(
		long[] notCategoryIds, StringBundler sb) {

		sb.append(" AND (NOT ");

		String sql = CustomSQLUtil.get(FIND_BY_AND_CATEGORY_IDS);

		String notCategoryIdsString = null;

		if (PropsValues.ASSET_CATEGORIES_SEARCH_HIERARCHICAL) {
			List<Long> notCategoryIdsList = new ArrayList<>();

			for (long notCategoryId : notCategoryIds) {
				notCategoryIdsList.addAll(getSubcategoryIds(notCategoryId));
			}

			notCategoryIdsString = StringUtil.merge(notCategoryIdsList);
		}
		else {
			notCategoryIdsString = StringUtil.merge(notCategoryIds);
		}

		sb.append(
			StringUtil.replace(sql, "[$CATEGORY_ID$]", notCategoryIdsString));
		sb.append(StringPool.CLOSE_PARENTHESIS);
	}

	protected String buildNotAnyTagsSQL(long[] notTagIds, StringBundler sb) {
		sb.append(" AND (");

		for (int i = 0; i < notTagIds.length; i++) {
			sb.append("AssetEntry.entryId NOT IN (");

			String sql = CustomSQLUtil.get(FIND_BY_AND_TAG_IDS);

			sql = StringUtil.replace(sql, "[$TAG_ID$]", getTagIds(notTagIds));

			sb.append(sql);

			sb.append(StringPool.CLOSE_PARENTHESIS);

			if ((i + 1) < notTagIds.length) {
				sb.append(" AND ");
			}
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	protected String getClassNameIds(long[] classNameIds) {
		if (classNameIds.length == 0) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(classNameIds.length + 1);

		sb.append(" AND (AssetEntry.classNameId = ?");

		for (int i = 0; i < (classNameIds.length - 1); i++) {
			sb.append(" OR AssetEntry.classNameId = ?");
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	protected String getDates(Date publishDate, Date expirationDate) {
		StringBundler sb = new StringBundler(4);

		if (publishDate != null) {
			sb.append(" AND (AssetEntry.publishDate IS NULL OR ");
			sb.append("AssetEntry.publishDate < ?)");
		}

		if (expirationDate != null) {
			sb.append(" AND (AssetEntry.expirationDate IS NULL OR ");
			sb.append("AssetEntry.expirationDate > ?)");
		}

		return sb.toString();
	}

	protected String getGroupIds(long[] groupIds) {
		if (groupIds.length == 0) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(groupIds.length + 1);

		sb.append(" AND (AssetEntry.groupId = ?");

		for (int i = 0; i < (groupIds.length - 1); i++) {
			sb.append(" OR AssetEntry.groupId = ?");
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	protected List<Long> getSubcategoryIds(long parentCategoryId) {
		AssetCategory parentAssetCategory = AssetCategoryUtil.fetchByPrimaryKey(
			parentCategoryId);

		if (parentAssetCategory == null) {
			return Collections.emptyList();
		}

		return ListUtil.toList(
			AssetCategoryUtil.getDescendants(parentAssetCategory),
			AssetCategory.CATEGORY_ID_ACCESSOR);
	}

	protected String getTagIds(long[] tagIds) {
		StringBundler sb = new StringBundler((tagIds.length * 3) - 1);

		for (int i = 0; i < tagIds.length; i++) {
			sb.append("tagId = ");
			sb.append(tagIds[i]);

			if ((i + 1) != tagIds.length) {
				sb.append(" OR ");
			}
		}

		return sb.toString();
	}

	protected void setDates(
		QueryPos qPos, Date publishDate, Date expirationDate) {

		if (publishDate != null) {
			Timestamp publishDate_TS = CalendarUtil.getTimestamp(publishDate);

			qPos.add(publishDate_TS);
		}

		if (expirationDate != null) {
			Timestamp expirationDate_TS = CalendarUtil.getTimestamp(
				expirationDate);

			qPos.add(expirationDate_TS);
		}
	}

}