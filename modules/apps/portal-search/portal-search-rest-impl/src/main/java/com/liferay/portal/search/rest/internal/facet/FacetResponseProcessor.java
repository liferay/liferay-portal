/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.internal.facet;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.Field;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.rest.dto.v1_0.FacetConfiguration;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portlet.asset.service.permission.AssetCategoryPermission;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(service = FacetResponseProcessor.class)
public class FacetResponseProcessor {

	public Map<String, Object> getTermsMap(
		long companyId, FacetConfiguration[] facetConfigurations, Locale locale,
		SearchResponse searchResponse, long userId) {

		return _toTermsMap(
			companyId, facetConfigurations, locale, searchResponse, userId);
	}

	private String _getAssetCategoryDisplayName(
		Locale locale, long assetCategoryId) {

		if (!_hasViewPermissionToAssetCategory(assetCategoryId)) {
			return null;
		}

		AssetCategory assetCategory = _assetCategoryLocalService.fetchCategory(
			assetCategoryId);

		if (assetCategory != null) {
			return assetCategory.getTitle(locale);
		}

		return null;
	}

	private String _getDateRangeDisplayName(
		FacetConfiguration facetConfiguration, Locale locale, String term) {

		Map<String, Object> attributes = facetConfiguration.getAttributes();

		JSONArray rangesJSONArray = _jsonFactory.createJSONArray(
			(List<Map<String, Object>>)attributes.get("ranges"));

		for (int i = 0; i < rangesJSONArray.length(); i++) {
			JSONObject jsonObject = rangesJSONArray.getJSONObject(i);

			if (StringUtil.equals(jsonObject.getString("range"), term)) {
				return _language.get(locale, jsonObject.getString("label"));
			}
		}

		return term;
	}

	private String _getDisplayName(
		long companyId, FacetConfiguration facetConfiguration, Locale locale,
		String term, long userId) {

		if (StringUtil.equals("category", facetConfiguration.getName()) ||
			StringUtil.equals("vocabulary", facetConfiguration.getName())) {

			if (term.contains("-")) {
				String[] termParts = term.split("-");

				return _getAssetCategoryDisplayName(
					locale, GetterUtil.getLong(termParts[1]));
			}

			return _getAssetCategoryDisplayName(
				locale, GetterUtil.getLong(term));
		}
		else if (StringUtil.equals(
					"date-range", facetConfiguration.getName())) {

			return _getDateRangeDisplayName(facetConfiguration, locale, term);
		}
		else if (StringUtil.equals("folder", facetConfiguration.getName())) {
			return _getFolderDisplayName(
				companyId, locale, GetterUtil.getLong(term), userId);
		}
		else if (StringUtil.equals("site", facetConfiguration.getName())) {
			return _getSiteDisplayName(GetterUtil.getLong(term), locale);
		}
		else if (StringUtil.equals("type", facetConfiguration.getName())) {
			return _getTypeDisplayName(term, companyId, locale);
		}

		return term;
	}

	private String _getFolderDisplayName(
		long companyId, Locale locale, long folderId, long userId) {

		if (folderId == 0) {
			return null;
		}

		SearchResponse searchResponse = _searchFolder(
			companyId, folderId, locale, userId);

		SearchHits searchHits = searchResponse.getSearchHits();

		if (searchHits.getTotalHits() == 0) {
			return null;
		}

		List<SearchHit> searchHitsList = searchHits.getSearchHits();

		SearchHit searchHit = searchHitsList.get(0);

		Document document = searchHit.getDocument();

		Map<String, Field> fieldsMap = document.getFields();

		for (Map.Entry<String, Field> entry : fieldsMap.entrySet()) {
			if (StringUtil.startsWith(
					entry.getKey(),
					com.liferay.portal.kernel.search.Field.TITLE)) {

				Field field = entry.getValue();

				return (String)field.getValue();
			}
		}

		return null;
	}

	private String _getSiteDisplayName(long groupId, Locale locale) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return null;
		}

		try {
			String name = group.getDescriptiveName(locale);

			if (group.isStagingGroup()) {
				name = StringBundler.concat(
					name, StringPool.SPACE, StringPool.OPEN_PARENTHESIS,
					_language.get(locale, "staged"),
					StringPool.CLOSE_PARENTHESIS);
			}

			return name;
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return null;
	}

	private String _getTerm(
		FacetConfiguration facetConfiguration, String term) {

		if ((StringUtil.equals("category", facetConfiguration.getName()) ||
			 StringUtil.equals("vocabulary", facetConfiguration.getName())) &&
			term.contains("-")) {

			String[] termParts = term.split("-");

			return termParts[1];
		}

		return term;
	}

	private String _getTypeDisplayName(
		String className, long companyId, Locale locale) {

		if (className.startsWith(
				ObjectDefinitionConstants.
					CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION)) {

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
					companyId, className);

			if (objectDefinition != null) {
				return objectDefinition.getLabel(locale);
			}
		}
		else {
			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(className);

			if (assetRendererFactory != null) {
				return assetRendererFactory.getTypeName(locale);
			}
		}

		return null;
	}

	private boolean _hasViewPermissionToAssetCategory(long assetCategoryId) {
		try {
			return AssetCategoryPermission.contains(
				PermissionThreadLocal.getPermissionChecker(), assetCategoryId,
				ActionKeys.VIEW);
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	private SearchResponse _searchFolder(
		long companyId, long folderId, Locale locale, long userId) {

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder();

		searchRequestBuilder.modelIndexerClassNames(
			"com.liferay.bookmarks.model.BookmarksFolder",
			"com.liferay.document.library.kernel.model.DLFolder",
			"com.liferay.journal.model.JournalFolder"
		).emptySearchEnabled(
			true
		).size(
			1
		).fields(
			_localization.getLocalizedName(
				com.liferay.portal.kernel.search.Field.TITLE,
				_language.getLanguageId(locale))
		).withSearchContext(
			searchContext -> {
				searchContext.setCompanyId(companyId);
				searchContext.setFolderIds(new long[] {folderId});
				searchContext.setUserId(userId);
			}
		);

		return _searcher.search(searchRequestBuilder.build());
	}

	private Collection<AssetCategoryTree> _toAssetCategoryTrees(
		FacetConfiguration facetConfiguration, Locale locale,
		List<TermCollector> termCollectors) {

		Map<Long, AssetCategoryTree> assetCategoryTrees = new HashMap<>();

		for (int i = 0; i < termCollectors.size(); i++) {
			TermCollector termCollector = termCollectors.get(i);

			if ((facetConfiguration.getFrequencyThreshold() >
					termCollector.getFrequency()) ||
				(i >= facetConfiguration.getMaxTerms())) {

				continue;
			}

			try {
				String[] termParts = StringUtil.split(
					termCollector.getTerm(), "-");

				long assetCategoryId = GetterUtil.getLong(termParts[1]);

				long assetVocabularyId = GetterUtil.getLong(termParts[0]);

				AssetVocabulary assetVocabulary =
					_assetVocabularyLocalService.getVocabulary(
						assetVocabularyId);

				AssetCategoryTree assetCategoryTree = assetCategoryTrees.get(
					assetVocabulary.getVocabularyId());

				if (assetCategoryTree == null) {
					assetCategoryTree = new AssetCategoryTree(
						assetVocabulary, locale);

					assetCategoryTrees.put(
						assetVocabularyId, assetCategoryTree);
				}

				assetCategoryTree.addAssetCategory(
					assetCategoryId, termCollector.getFrequency());
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		return assetCategoryTrees.values();
	}

	private JSONArray _toTermJSONArray(
		long companyId, FacetConfiguration facetConfiguration, Locale locale,
		List<TermCollector> termCollectors, long userId) {

		JSONArray termJSONArray = _jsonFactory.createJSONArray();

		for (int i = 0; i < termCollectors.size(); i++) {
			TermCollector termCollector = termCollectors.get(i);

			if ((facetConfiguration.getFrequencyThreshold() >
					termCollector.getFrequency()) ||
				(i >= facetConfiguration.getMaxTerms())) {

				continue;
			}

			String displayName = _getDisplayName(
				companyId, facetConfiguration, locale, termCollector.getTerm(),
				userId);

			if (Validator.isBlank(displayName)) {
				continue;
			}

			termJSONArray.put(
				JSONUtil.put(
					"displayName", displayName
				).put(
					"frequency", termCollector.getFrequency()
				).put(
					"term",
					_getTerm(facetConfiguration, termCollector.getTerm())
				));
		}

		return termJSONArray;
	}

	private Map<String, Object> _toTermsMap(
		long companyId, FacetConfiguration[] facetConfigurations, Locale locale,
		SearchResponse searchResponse, long userId) {

		if (ArrayUtil.isEmpty(facetConfigurations)) {
			return null;
		}

		Map<String, Object> termsMap = new HashMap<>();

		for (FacetConfiguration facetConfiguration : facetConfigurations) {
			Facet facet = searchResponse.withFacetContextGet(
				facetContext -> {
					if (Validator.isNotNull(
							facetConfiguration.getAggregationName())) {

						return facetContext.getFacet(
							facetConfiguration.getAggregationName());
					}

					return facetContext.getFacet(facetConfiguration.getName());
				});

			if (facet == null) {
				continue;
			}

			FacetCollector facetCollector = facet.getFacetCollector();

			List<TermCollector> termCollectors =
				facetCollector.getTermCollectors();

			if (ListUtil.isEmpty(termCollectors)) {
				continue;
			}

			if (StringUtil.equals("vocabulary", facetConfiguration.getName()) &&
				StringUtil.equals(
					facet.getFieldName(), "assetVocabularyCategoryIds")) {

				Collection<AssetCategoryTree> assetCategoryTrees =
					_toAssetCategoryTrees(
						facetConfiguration, locale, termCollectors);

				if (!assetCategoryTrees.isEmpty()) {
					termsMap.put(
						facetConfiguration.getAggregationName(),
						_toAssetCategoryTrees(
							facetConfiguration, locale, termCollectors));
				}
			}
			else {
				JSONArray termJSONArray = _toTermJSONArray(
					companyId, facetConfiguration, locale, termCollectors,
					userId);

				if (termJSONArray.length() > 0) {
					termsMap.put(
						facetConfiguration.getAggregationName(), termJSONArray);
				}
			}
		}

		return termsMap;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FacetResponseProcessor.class);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private Searcher _searcher;

}