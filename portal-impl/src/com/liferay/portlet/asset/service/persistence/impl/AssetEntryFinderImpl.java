/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.persistence.impl;

import com.liferay.asset.kernel.configuration.provider.AssetCategoryConfigurationProviderUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.persistence.AssetEntryFinder;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.asset.model.impl.AssetEntryImpl;
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

	@Override
	public int countEntries(AssetEntryQuery entryQuery) {
		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = buildAssetQuerySQL(entryQuery, true, session);

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
	public List<AssetEntry> findEntries(AssetEntryQuery entryQuery) {
		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = buildAssetQuerySQL(entryQuery, false, session);

			return (List<AssetEntry>)QueryUtil.list(
				sqlQuery, getDialect(), entryQuery.getStart(),
				entryQuery.getEnd());
		}
		catch (Exception exception) {
			throw new SystemException(exception);
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

			if (AssetCategoryConfigurationProviderUtil.isSearchHierarchical(
					CompanyThreadLocal.getCompanyId())) {

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
		String categoryIdsString = null;

		if (AssetCategoryConfigurationProviderUtil.isSearchHierarchical(
				CompanyThreadLocal.getCompanyId())) {

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

		String sql = CustomSQLUtil.get(FIND_BY_AND_CATEGORY_IDS);

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

		StringBundler sb = new StringBundler(77);

		if (count) {
			sb.append("SELECT COUNT(DISTINCT AssetEntry.entryId) AS ");
			sb.append("COUNT_VALUE ");
		}
		else {
			sb.append("SELECT {AssetEntry.*} ");

			boolean selectRatings = false;
			boolean selectViewCount = false;

			String orderByCol1 = entryQuery.getOrderByCol1();
			String orderByCol2 = entryQuery.getOrderByCol2();

			if (orderByCol1.equals("ratings") ||
				orderByCol2.equals("ratings") ||
				orderByCol1.equals("ratingsTotalScore") ||
				orderByCol2.equals("ratingsTotalScore")) {

				selectRatings = true;

				sb.append(", TEMP_TABLE_ASSET_ENTRY.averageScore, ");
				sb.append("TEMP_TABLE_ASSET_ENTRY.totalScore ");
			}

			if (orderByCol1.equals("viewCount") ||
				orderByCol2.equals("viewCount")) {

				selectViewCount = true;

				sb.append(", TEMP_TABLE_ASSET_ENTRY.viewCount ");
			}

			sb.append("FROM (SELECT DISTINCT AssetEntry.entryId ");

			if (selectRatings) {
				sb.append(", RatingsStats.averageScore, ");
				sb.append(" RatingsStats.totalScore ");
			}

			if (selectViewCount) {
				sb.append(", ViewCountEntry.viewCount ");
			}
		}

		sb.append("FROM AssetEntry ");

		if (ArrayUtil.isNotEmpty(entryQuery.getAnyTagIds())) {
			sb.append("INNER JOIN AssetEntries_AssetTags ON ");
			sb.append("(AssetEntries_AssetTags.entryId = AssetEntry.entryId) ");
			sb.append("INNER JOIN AssetTag ON (AssetTag.tagId = ");
			sb.append("AssetEntries_AssetTags.tagId) ");
		}

		String linkedAssetEntryIdsSQL = _getLinkedAssetEntryIdsSQL(entryQuery);

		if (Validator.isNotNull(linkedAssetEntryIdsSQL)) {
			sb.append("INNER JOIN (SELECT AssetLink.entryId1 AS entryId ");
			sb.append("FROM AssetLink WHERE AssetLink.entryId2 IN ");
			sb.append(linkedAssetEntryIdsSQL);
			sb.append("AND AssetLink.entryId1 NOT IN ");
			sb.append(linkedAssetEntryIdsSQL);
			sb.append("UNION SELECT AssetLink.entryId2 AS entryId FROM ");
			sb.append("AssetLink WHERE AssetLink.entryId1 IN ");
			sb.append(linkedAssetEntryIdsSQL);
			sb.append(" AND AssetLink.entryId2 NOT IN ");
			sb.append(linkedAssetEntryIdsSQL);
			sb.append(" ) TEMP_TABLE_ASSET_LINK ON ");
			sb.append("(TEMP_TABLE_ASSET_LINK.entryId = AssetEntry.entryId) ");
		}

		if (entryQuery.isExcludeZeroViewCount()) {
			sb.append("INNER JOIN ViewCountEntry ON (");
			sb.append("ViewCountEntry.companyId = AssetEntry.companyId) AND ");
			sb.append("(ViewCountEntry.classNameId = ");
			sb.append(
				ClassNameLocalServiceUtil.getClassNameId(AssetEntry.class));
			sb.append(") AND (ViewCountEntry.classPK = AssetEntry.entryId) ");
			sb.append("AND (ViewCountEntry.viewCount > 0) ");
		}

		String orderByCol1 = AssetEntryQuery.ORDER_BY_COLUMN_DEFAULT;
		String orderByCol2 = AssetEntryQuery.ORDER_BY_COLUMN_DEFAULT;

		for (String orderByColumn : AssetEntryQuery.ORDER_BY_COLUMNS) {
			if (orderByColumn.equals(entryQuery.getOrderByCol1())) {
				orderByCol1 = orderByColumn;
			}

			if (orderByColumn.equals(entryQuery.getOrderByCol2())) {
				orderByCol2 = orderByColumn;
			}
		}

		if (orderByCol1.equals("ratings") || orderByCol2.equals("ratings") ||
			orderByCol1.equals("ratingsTotalScore") ||
			orderByCol2.equals("ratingsTotalScore")) {

			sb.append(" LEFT JOIN RatingsStats ON (RatingsStats.classNameId ");
			sb.append("= AssetEntry.classNameId) AND (RatingsStats.classPK = ");
			sb.append("AssetEntry.classPK)");
		}

		if (!entryQuery.isExcludeZeroViewCount() &&
			(orderByCol1.equals("viewCount") ||
			 orderByCol2.equals("viewCount"))) {

			sb.append(" LEFT JOIN ViewCountEntry ON ");
			sb.append("(ViewCountEntry.companyId = AssetEntry.companyId) AND ");
			sb.append("(ViewCountEntry.classNameId = ");
			sb.append(
				ClassNameLocalServiceUtil.getClassNameId(AssetEntry.class));
			sb.append(") AND (ViewCountEntry.classPK = AssetEntry.entryId) ");
		}

		sb.append("WHERE ");

		int whereIndex = sb.index();

		if (Validator.isNotNull(linkedAssetEntryIdsSQL)) {
			sb.append(" AND (AssetEntry.entryId NOT IN ");
			sb.append(linkedAssetEntryIdsSQL);
			sb.append(" )");
		}

		if (entryQuery.isListable() != null) {
			sb.append(" AND (listable = ?)");
		}

		if (entryQuery.isVisible() != null) {
			sb.append(" AND (visible = ?)");
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
			sb.append(") TEMP_TABLE_ASSET_ENTRY INNER JOIN ");
			sb.append("AssetEntry AssetEntry ON ");
			sb.append("TEMP_TABLE_ASSET_ENTRY.entryId = ");
			sb.append("AssetEntry.entryId ORDER BY ");

			if (orderByCol1.equals("ratings")) {
				sb.append("CASE WHEN TEMP_TABLE_ASSET_ENTRY.averageScore ");
				sb.append("IS NULL THEN 0 ");
				sb.append("ELSE TEMP_TABLE_ASSET_ENTRY.averageScore END ");
				sb.append(entryQuery.getOrderByType1());
				sb.append(", CASE WHEN TEMP_TABLE_ASSET_ENTRY.totalScore ");
				sb.append("IS NULL THEN 0 ");
				sb.append("ELSE TEMP_TABLE_ASSET_ENTRY.totalScore END");
			}
			else if (orderByCol1.equals("ratingsTotalScore")) {
				sb.append("CASE WHEN TEMP_TABLE_ASSET_ENTRY.totalScore ");
				sb.append("IS NULL THEN 0 ");
				sb.append("ELSE TEMP_TABLE_ASSET_ENTRY.totalScore END ");
				sb.append(entryQuery.getOrderByType1());
				sb.append(", CASE WHEN TEMP_TABLE_ASSET_ENTRY.averageScore ");
				sb.append("IS NULL THEN 0 ");
				sb.append("ELSE TEMP_TABLE_ASSET_ENTRY.averageScore END");
			}
			else if (orderByCol1.equals("title")) {
				sb.append("CAST_CLOB_TEXT(AssetEntry.title)");
			}
			else if (orderByCol1.equals("viewCount")) {
				sb.append("CASE WHEN TEMP_TABLE_ASSET_ENTRY.viewCount ");
				sb.append("IS NULL THEN 0 ");
				sb.append("ELSE TEMP_TABLE_ASSET_ENTRY.viewCount END");
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
					sb.append(", CASE WHEN ");
					sb.append("TEMP_TABLE_ASSET_ENTRY.averageScore IS NULL ");
					sb.append("THEN 0 ELSE ");
					sb.append("TEMP_TABLE_ASSET_ENTRY.averageScore END ");
					sb.append(entryQuery.getOrderByType2());
					sb.append(", CASE WHEN TEMP_TABLE_ASSET_ENTRY.totalScore ");
					sb.append("IS NULL THEN 0 ");
					sb.append("ELSE TEMP_TABLE_ASSET_ENTRY.totalScore END");
				}
				else if (orderByCol2.equals("ratingsTotalScore")) {
					sb.append(", CASE WHEN TEMP_TABLE_ASSET_ENTRY.totalScore ");
					sb.append("IS NULL THEN 0 ");
					sb.append("ELSE TEMP_TABLE_ASSET_ENTRY.totalScore END ");
					sb.append(entryQuery.getOrderByType2());
					sb.append(", CASE WHEN ");
					sb.append("TEMP_TABLE_ASSET_ENTRY.averageScore IS NULL ");
					sb.append("THEN 0 ELSE ");
					sb.append("TEMP_TABLE_ASSET_ENTRY.averageScore END");
				}
				else if (orderByCol2.equals("title")) {
					sb.append(", CAST_CLOB_TEXT(AssetEntry.title)");
				}
				else if (orderByCol2.equals("viewCount")) {
					sb.append(", CASE WHEN ");
					sb.append("TEMP_TABLE_ASSET_ENTRY.viewCount IS NULL ");
					sb.append("THEN 0 ELSE ");
					sb.append("TEMP_TABLE_ASSET_ENTRY.viewCount END");
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

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		if (count) {
			sqlQuery.addScalar(COUNT_COLUMN_NAME, Type.LONG);
		}
		else {
			sqlQuery.addEntity("AssetEntry", AssetEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (entryQuery.isListable() != null) {
			queryPos.add(entryQuery.isListable());
		}

		if (entryQuery.isVisible() != null) {
			queryPos.add(entryQuery.isVisible());
		}

		if (Validator.isNotNull(entryQuery.getKeywords())) {
			queryPos.add(
				StringUtil.quote(entryQuery.getKeywords(), StringPool.PERCENT));
			queryPos.add(
				StringUtil.quote(entryQuery.getKeywords(), StringPool.PERCENT));
			queryPos.add(
				StringUtil.quote(entryQuery.getKeywords(), StringPool.PERCENT));
		}
		else {
			if (Validator.isNotNull(entryQuery.getUserName())) {
				queryPos.add(
					StringUtil.quote(
						entryQuery.getUserName(), StringPool.PERCENT));
			}

			if (Validator.isNotNull(entryQuery.getTitle())) {
				queryPos.add(
					StringUtil.quote(
						entryQuery.getTitle(), StringPool.PERCENT));
			}

			if (Validator.isNotNull(entryQuery.getDescription())) {
				queryPos.add(
					StringUtil.quote(
						entryQuery.getDescription(), StringPool.PERCENT));
			}
		}

		if (layout != null) {
			queryPos.add(layout.getUuid());
		}

		setDates(
			queryPos, entryQuery.getPublishDate(),
			entryQuery.getExpirationDate());

		queryPos.add(entryQuery.getGroupIds());
		queryPos.add(entryQuery.getClassNameIds());

		return sqlQuery;
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

			if (AssetCategoryConfigurationProviderUtil.isSearchHierarchical(
					CompanyThreadLocal.getCompanyId())) {

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

		if (AssetCategoryConfigurationProviderUtil.isSearchHierarchical(
				CompanyThreadLocal.getCompanyId())) {

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
		AssetCategory parentAssetCategory =
			AssetCategoryLocalServiceUtil.fetchAssetCategory(parentCategoryId);

		if (parentAssetCategory == null) {
			return Collections.emptyList();
		}

		return ListUtil.toList(
			AssetCategoryLocalServiceUtil.getDescendantCategories(
				parentAssetCategory),
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
		QueryPos queryPos, Date publishDate, Date expirationDate) {

		if (publishDate != null) {
			Timestamp publishDate_TS = CalendarUtil.getTimestamp(publishDate);

			queryPos.add(publishDate_TS);
		}

		if (expirationDate != null) {
			Timestamp expirationDate_TS = CalendarUtil.getTimestamp(
				expirationDate);

			queryPos.add(expirationDate_TS);
		}
	}

	private String _getLinkedAssetEntryIdsSQL(AssetEntryQuery entryQuery) {
		if (ArrayUtil.isEmpty(entryQuery.getLinkedAssetEntryIds())) {
			return null;
		}

		StringBundler sb = new StringBundler();

		sb.append(" (");

		long[] linkedAssetEntryIds = entryQuery.getLinkedAssetEntryIds();

		for (int i = 0; i < linkedAssetEntryIds.length; i++) {
			if (i > 0) {
				sb.append(StringPool.COMMA);
			}

			sb.append(linkedAssetEntryIds[i]);
		}

		sb.append(") ");

		return sb.toString();
	}

}