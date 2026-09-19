/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.display.context;

import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.spi.display.CTDisplayRendererRegistry;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserTable;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.ExistsFilter;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Samuel Trong Tran
 */
public class DisplayContextUtil {

	public static Map<Long, Document> getCTEntryDocuments(
		long ctCollectionId, ThemeDisplay themeDisplay) {

		Searcher searcher = _searcherSnapshot.get();

		SearchRequestBuilderFactory searchRequestBuilderFactory =
			_searchRequestBuilderFactorySnapshot.get();

		SearchRequestBuilder searchRequestBuilder =
			searchRequestBuilderFactory.builder(
			).companyId(
				themeDisplay.getCompanyId()
			).emptySearchEnabled(
				true
			).fields(
				"changeType", Field.ENTRY_CLASS_PK, Field.GROUP_ID,
				"modelClassNameId", "modelClassPK", Field.MODIFIED_DATE,
				Field.STATUS,
				Field.getLocalizedName(themeDisplay.getLocale(), Field.TITLE)
			).modelIndexerClasses(
				CTEntry.class
			).withSearchContext(
				searchContext -> {
					searchContext.setAttribute(
						"ctCollectionId", ctCollectionId);
					searchContext.setAttribute("showHideable", Boolean.TRUE);
				}
			);

		SearchResponse searchResponse = searcher.search(
			searchRequestBuilder.build());

		List<Document> documents = searchResponse.getDocuments();

		Map<Long, Document> ctEntryDocuments = new HashMap<>(documents.size());

		for (Document document : documents) {
			ctEntryDocuments.put(
				document.getLong(Field.ENTRY_CLASS_PK), document);
		}

		return ctEntryDocuments;
	}

	public static Map<Long, String> getSiteNames(
		long ctCollectionId, boolean showHideable, ThemeDisplay themeDisplay) {

		Map<Long, String> siteNames = new LinkedHashMap<>();

		Searcher searcher = _searcherSnapshot.get();

		SearchRequestBuilderFactory searchRequestBuilderFactory =
			_searchRequestBuilderFactorySnapshot.get();

		Sorts sorts = _sortsSnapshot.get();

		SearchRequestBuilder searchRequestBuilder =
			searchRequestBuilderFactory.builder(
			).companyId(
				themeDisplay.getCompanyId()
			).emptySearchEnabled(
				true
			).fields(
				Field.GROUP_ID, "groupName"
			).modelIndexerClasses(
				CTEntry.class
			).sorts(
				sorts.field(
					Field.getSortableFieldName(
						"groupName_".concat(
							LocaleUtil.toLanguageId(themeDisplay.getLocale()))),
					SortOrder.ASC)
			).withSearchContext(
				searchContext -> {
					searchContext.setAttribute(
						"ctCollectionId", ctCollectionId);
					searchContext.setAttribute("showHideable", showHideable);

					BooleanQuery booleanQuery = new BooleanQuery();

					BooleanFilter booleanFilter = new BooleanFilter();

					booleanFilter.add(
						new ExistsFilter(Field.GROUP_ID),
						BooleanClauseOccur.MUST);

					booleanQuery.setPreBooleanFilter(booleanFilter);

					searchContext.setBooleanClauses(
						new BooleanClause[] {
							new BooleanClause<>(
								booleanQuery, BooleanClauseOccur.MUST)
						});
				}
			);

		SearchResponse searchResponse = searcher.search(
			searchRequestBuilder.build());

		for (Document document : searchResponse.getDocuments()) {
			siteNames.put(
				document.getLong(Field.GROUP_ID),
				document.getString("groupName"));
		}

		return siteNames;
	}

	public static Map<Long, String> getTypeNames(
		long ctCollectionId, boolean showHideable, ThemeDisplay themeDisplay) {

		Map<Long, String> typeNames = new LinkedHashMap<>();

		Searcher searcher = _searcherSnapshot.get();

		SearchRequestBuilderFactory searchRequestBuilderFactory =
			_searchRequestBuilderFactorySnapshot.get();

		Sorts sorts = _sortsSnapshot.get();

		SearchRequestBuilder searchRequestBuilder =
			searchRequestBuilderFactory.builder(
			).companyId(
				themeDisplay.getCompanyId()
			).entryClassNames(
				CTEntry.class.getName()
			).emptySearchEnabled(
				true
			).fields(
				"modelClassNameId", "typeName"
			).sorts(
				sorts.field(
					Field.getSortableFieldName(
						"typeName_".concat(
							LocaleUtil.toLanguageId(themeDisplay.getLocale()))),
					SortOrder.ASC)
			).withSearchContext(
				searchContext -> {
					searchContext.setAttribute(
						"ctCollectionId", ctCollectionId);
					searchContext.setAttribute("showHideable", showHideable);
				}
			);

		SearchResponse searchResponse = searcher.search(
			searchRequestBuilder.build());

		for (Document document : searchResponse.getDocuments()) {
			typeNames.put(
				document.getLong("modelClassNameId"),
				document.getString("typeName"));
		}

		return typeNames;
	}

	public static JSONObject getTypeNamesJSONObject(
		Set<Long> classNameIds,
		CTDisplayRendererRegistry ctDisplayRendererRegistry,
		ThemeDisplay themeDisplay) {

		JSONObject typeNamesJSONObject = JSONFactoryUtil.createJSONObject();

		for (long classNameId : classNameIds) {
			String typeName = ctDisplayRendererRegistry.getTypeName(
				themeDisplay.getLocale(), classNameId);

			typeNamesJSONObject.put(String.valueOf(classNameId), typeName);
		}

		return typeNamesJSONObject;
	}

	public static JSONObject getUserInfoJSONObject(
		Predicate innerJoinPredicate, Table<?> innerJoinTable,
		ThemeDisplay themeDisplay, UserLocalService userLocalService,
		Predicate wherePredicate) {

		JSONObject userInfoJSONObject = JSONFactoryUtil.createJSONObject();

		List<User> users = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			users = userLocalService.dslQuery(
				DSLQueryFactoryUtil.selectDistinct(
					UserTable.INSTANCE
				).from(
					UserTable.INSTANCE
				).innerJoinON(
					innerJoinTable, innerJoinPredicate
				).where(
					wherePredicate
				));
		}

		for (User user : users) {
			String portraitURL = StringPool.BLANK;

			if (user.getPortraitId() > 0) {
				try {
					portraitURL = user.getPortraitURL(themeDisplay);
				}
				catch (PortalException portalException) {
					_log.error(portalException);
				}
			}

			userInfoJSONObject.put(
				String.valueOf(user.getUserId()),
				JSONUtil.put(
					"portraitURL", portraitURL
				).put(
					"userName", user.getFullName()
				));
		}

		return userInfoJSONObject;
	}

	private DisplayContextUtil() {
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DisplayContextUtil.class);

	private static final Snapshot<SearchRequestBuilderFactory>
		_searchRequestBuilderFactorySnapshot = new Snapshot<>(
			DisplayContextUtil.class, SearchRequestBuilderFactory.class);
	private static final Snapshot<Searcher> _searcherSnapshot = new Snapshot<>(
		DisplayContextUtil.class, Searcher.class);
	private static final Snapshot<Sorts> _sortsSnapshot = new Snapshot<>(
		DisplayContextUtil.class, Sorts.class);

}