/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.suggestions.spi;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.asset.AssetURLViewProvider;
import com.liferay.portal.search.constants.SearchContextAttributes;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.rest.dto.v1_0.SuggestionsContributorConfiguration;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.spi.suggestions.SuggestionsContributor;
import com.liferay.portal.search.suggestions.Suggestion;
import com.liferay.portal.search.suggestions.SuggestionBuilder;
import com.liferay.portal.search.suggestions.SuggestionBuilderFactory;
import com.liferay.portal.search.suggestions.SuggestionsContributorResults;
import com.liferay.portal.search.suggestions.SuggestionsContributorResultsBuilderFactory;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	enabled = false,
	property = "search.suggestions.contributor.name=sxpBlueprint",
	service = SuggestionsContributor.class
)
public class SXPBlueprintSuggestionsContributor
	implements SuggestionsContributor {

	@Override
	public SuggestionsContributorResults getSuggestionsContributorResults(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContext searchContext,
		SuggestionsContributorConfiguration
			suggestionsContributorConfiguration) {

		Map<String, Object> attributes =
			(Map<String, Object>)
				suggestionsContributorConfiguration.getAttributes();

		if ((attributes == null) ||
			(!attributes.containsKey("sxpBlueprintExternalReferenceCode") &&
			 !attributes.containsKey("sxpBlueprintId"))) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Attributes do not contain search experiences blueprint " +
						"External Reference Code or ID");
			}

			return null;
		}

		if (!_exceedsCharacterThreshold(
				attributes, searchContext.getKeywords())) {

			return null;
		}

		SearchResponse searchResponse = _searcher.search(
			_getSearchRequest(
				attributes, searchContext,
				GetterUtil.getInteger(
					suggestionsContributorConfiguration.getSize(), 5)));

		SearchHits searchHits = searchResponse.getSearchHits();

		if (searchHits.getTotalHits() == 0) {
			return null;
		}

		return _suggestionsContributorResultsBuilderFactory.builder(
		).displayGroupName(
			suggestionsContributorConfiguration.getDisplayGroupName()
		).suggestions(
			_getSuggestions(
				attributes, liferayPortletRequest, liferayPortletResponse,
				searchContext, searchHits.getSearchHits())
		).build();
	}

	private boolean _exceedsCharacterThreshold(
		Map<String, Object> attributes, String keywords) {

		int characterThreshold = _getCharacterThreshold(attributes);

		if (Validator.isBlank(keywords)) {
			if (characterThreshold == 0) {
				return true;
			}
		}
		else if (keywords.length() >= characterThreshold) {
			return true;
		}

		return false;
	}

	private AssetRenderer<?> _getAssetRenderer(
		AssetRendererFactory<?> assetRendererFactory, long entryClassPK) {

		try {
			return assetRendererFactory.getAssetRenderer(entryClassPK);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	private int _getCharacterThreshold(Map<String, Object> attributes) {
		if (attributes == null) {
			return _CHARACTER_THRESHOLD;
		}

		return MapUtil.getInteger(
			attributes, "characterThreshold", _CHARACTER_THRESHOLD);
	}

	private Map<String, Object> _getFieldValues(
		Document document, List<String> fieldNames, Locale locale) {

		Map<String, Object> fieldValues = new HashMap<>();

		for (String fieldName : fieldNames) {
			fieldName = _replaceLanguageId(locale, fieldName);

			fieldValues.put(fieldName, document.getValue(fieldName));
		}

		return fieldValues;
	}

	private String[] _getNestedFieldValue(Document document, String fieldName) {
		String[] parts = StringUtil.split(fieldName, "\\.");

		Map<String, Field> nestedFields = (Map<String, Field>)document.getValue(
			parts[0]);

		if (nestedFields == null) {
			return null;
		}

		return GetterUtil.getStringValues(nestedFields.get(parts[1]));
	}

	private SearchRequest _getSearchRequest(
		Map<String, Object> attributes, SearchContext searchContext1,
		int size) {

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder();

		searchRequestBuilder.from(
			0
		).queryString(
			searchContext1.getKeywords()
		).size(
			size
		).withSearchContext(
			searchContext2 -> {
				_setSearchExperiencesSearchContextAttributes(
					attributes, searchContext1, searchContext2);

				searchContext2.setAttribute(
					SearchContextAttributes.
						ATTRIBUTE_KEY_CONTRIBUTE_TUNING_RANKINGS,
					Boolean.TRUE);
				searchContext2.setAttribute(
					SearchContextAttributes.ATTRIBUTE_KEY_EMPTY_SEARCH,
					searchContext1.getAttribute(
						SearchContextAttributes.ATTRIBUTE_KEY_EMPTY_SEARCH));
				searchContext2.setCompanyId(searchContext1.getCompanyId());
				searchContext2.setGroupIds(searchContext1.getGroupIds());

				if (FeatureFlagManagerUtil.isEnabled("LPD-35128")) {
					searchContext2.setIncludeAttachments(
						MapUtil.getBoolean(attributes, "includeAttachments"));
				}

				searchContext2.setKeywords(searchContext1.getKeywords());
				searchContext2.setLocale(searchContext1.getLocale());
				searchContext2.setTimeZone(searchContext1.getTimeZone());
				searchContext2.setUserId(searchContext1.getUserId());
			}
		);

		return searchRequestBuilder.build();
	}

	private Suggestion _getSuggestion(
		List<String> fieldNames, boolean includeAssetSearchSummary,
		boolean includeAssetURL, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContext searchContext, SearchHit searchHit, String text,
		boolean useAssetTitle) {

		SuggestionBuilder suggestionBuilder = _suggestionBuilderFactory.builder(
		).score(
			searchHit.getScore()
		).text(
			text
		);

		Document document = searchHit.getDocument();

		if (ListUtil.isNotEmpty(fieldNames)) {
			suggestionBuilder.attribute(
				"fields",
				_getFieldValues(
					document, fieldNames, searchContext.getLocale()));
		}

		if (!includeAssetSearchSummary && !includeAssetURL && !useAssetTitle) {
			return suggestionBuilder.build();
		}

		try {
			_setAssetFields(
				document, includeAssetSearchSummary, includeAssetURL,
				liferayPortletRequest, liferayPortletResponse,
				searchContext.getLocale(), suggestionBuilder, useAssetTitle);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return suggestionBuilder.build();
	}

	private List<Suggestion> _getSuggestions(
		Map<String, Object> attributes,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContext searchContext, List<SearchHit> searchHits) {

		List<Suggestion> suggestions = new ArrayList<>();

		List<String> fieldNames = (List<String>)attributes.get("fields");
		String fieldValueSeparator = MapUtil.getString(
			attributes, "fieldValueSeparator");
		boolean includeAssetSearchSummary = MapUtil.getBoolean(
			attributes, "includeAssetSearchSummary", true);
		boolean includeAssetURL = MapUtil.getBoolean(
			attributes, "includeAssetURL", true);

		String textFieldName = MapUtil.getString(attributes, "textField");

		for (SearchHit searchHit : searchHits) {
			Document document = searchHit.getDocument();

			if (Validator.isBlank(textFieldName)) {
				suggestions.add(
					_getSuggestion(
						fieldNames, includeAssetSearchSummary, includeAssetURL,
						liferayPortletRequest, liferayPortletResponse,
						searchContext, searchHit, StringPool.BLANK, true));

				continue;
			}

			List<String> texts = _getTexts(
				document,
				_replaceLanguageId(searchContext.getLocale(), textFieldName));

			for (String text : texts) {
				if (!Validator.isBlank(fieldValueSeparator)) {
					String[] parts = StringUtil.split(
						text, fieldValueSeparator);

					for (String part : parts) {
						suggestions.add(
							_getSuggestion(
								fieldNames, includeAssetSearchSummary,
								includeAssetURL, liferayPortletRequest,
								liferayPortletResponse, searchContext,
								searchHit, part, false));
					}
				}
				else {
					suggestions.add(
						_getSuggestion(
							fieldNames, includeAssetSearchSummary,
							includeAssetURL, liferayPortletRequest,
							liferayPortletResponse, searchContext, searchHit,
							text, false));
				}
			}
		}

		return suggestions;
	}

	private List<String> _getTexts(Document document, String fieldName) {
		if (StringUtil.contains(fieldName, ".")) {
			return Arrays.asList(_getNestedFieldValue(document, fieldName));
		}

		return document.getStrings(fieldName);
	}

	private String _replaceLanguageId(Locale locale, String fieldName) {
		return StringUtil.replace(
			fieldName, "${language_id}", LocaleUtil.toLanguageId(locale));
	}

	private void _setAssetFields(
			Document document, boolean includeAssetSearchSummary,
			boolean includeAssetURL,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, Locale locale,
			SuggestionBuilder suggestionBuilder, boolean useAssetTitle)
		throws Exception {

		String entryClassName = document.getString(Field.ENTRY_CLASS_NAME);

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				entryClassName);

		if (assetRendererFactory == null) {
			return;
		}

		long entryClassPK = document.getLong(Field.ENTRY_CLASS_PK);

		AssetRenderer<?> assetRenderer = _getAssetRenderer(
			assetRendererFactory, entryClassPK);

		if (assetRenderer == null) {
			return;
		}

		if (includeAssetSearchSummary) {
			suggestionBuilder.attribute(
				"assetSearchSummary",
				assetRenderer.getSummary(
					liferayPortletRequest, liferayPortletResponse));
		}

		if (includeAssetURL) {
			String assetClassName = entryClassName;
			long assetClassPK = entryClassPK;

			long classNameId = GetterUtil.getLong(
				document.getValue(Field.CLASS_NAME_ID));
			long classPK = GetterUtil.getLong(
				document.getValue(Field.CLASS_PK));

			if ((classNameId > 0) && (classPK > 0)) {
				ClassName className = _classNameLocalService.getClassName(
					classNameId);

				AssetRendererFactory<?> classNameAssetRendererFactory =
					AssetRendererFactoryRegistryUtil.
						getAssetRendererFactoryByClassName(
							className.getClassName());

				if (classNameAssetRendererFactory != null) {
					assetClassName = className.getClassName();
					assetClassPK = classPK;
				}
			}

			suggestionBuilder.attribute(
				"assetURL",
				_assetURLViewProvider.getAssetURLView(
					assetRenderer, assetRendererFactory, assetClassName,
					assetClassPK, liferayPortletRequest,
					liferayPortletResponse));
		}

		if (useAssetTitle) {
			suggestionBuilder.text(assetRenderer.getTitle(locale));
		}
	}

	private void _setSearchExperiencesSearchContextAttributes(
		Map<String, Object> attributes, SearchContext sourceSearchContext,
		SearchContext targetSearchContext) {

		MapUtil.isNotEmptyForEach(
			attributes,
			(key, value) -> {
				if (key.startsWith("search.experiences.") && (value != null) &&
					(value instanceof Serializable)) {

					targetSearchContext.setAttribute(key, (Serializable)value);
				}
			});

		MapUtil.isNotEmptyForEach(
			sourceSearchContext.getAttributes(),
			(key, value) -> {
				if (key.startsWith("search.experiences.") && (value != null)) {
					targetSearchContext.setAttribute(key, value);
				}
			});

		targetSearchContext.setAttribute(
			"search.experiences.blueprint.external.reference.code",
			MapUtil.getString(attributes, "sxpBlueprintExternalReferenceCode"));
		targetSearchContext.setAttribute(
			"search.experiences.blueprint.id",
			MapUtil.getLong(attributes, "sxpBlueprintId"));
	}

	private static final int _CHARACTER_THRESHOLD = 2;

	private static final Log _log = LogFactoryUtil.getLog(
		SXPBlueprintSuggestionsContributor.class);

	@Reference
	private AssetURLViewProvider _assetURLViewProvider;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private Searcher _searcher;

	@Reference
	private SuggestionBuilderFactory _suggestionBuilderFactory;

	@Reference
	private SuggestionsContributorResultsBuilderFactory
		_suggestionsContributorResultsBuilderFactory;

}