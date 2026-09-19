/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.suggestions.spi;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
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
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	enabled = true, property = "search.suggestions.contributor.name=basic",
	service = SuggestionsContributor.class
)
public class BasicSuggestionsContributor implements SuggestionsContributor {

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

		return _toSuggestionsContributorResults(
			suggestionsContributorConfiguration.getDisplayGroupName(),
			liferayPortletRequest, liferayPortletResponse, searchContext,
			searchHits.getSearchHits());
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

	private int _getCharacterThreshold(Map<String, Object> attributes) {
		if (attributes == null) {
			return _CHARACTER_THRESHOLD;
		}

		return MapUtil.getInteger(
			attributes, "characterThreshold", _CHARACTER_THRESHOLD);
	}

	private SearchRequest _getSearchRequest(
		Map<String, Object> attributes, SearchContext searchContext1,
		int size) {

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder();

		searchRequestBuilder.withSearchContext(
			searchContext2 -> {
				_setIncludeAttachments(attributes, searchContext2);

				searchContext2.setAttribute(
					SearchContextAttributes.
						ATTRIBUTE_KEY_CONTRIBUTE_TUNING_RANKINGS,
					Boolean.TRUE);
				searchContext2.setCompanyId(searchContext1.getCompanyId());
				searchContext2.setGroupIds(searchContext1.getGroupIds());
				searchContext2.setKeywords(searchContext1.getKeywords());
				searchContext2.setLocale(searchContext1.getLocale());
				searchContext2.setTimeZone(searchContext1.getTimeZone());
				searchContext2.setUserId(searchContext1.getUserId());
			});

		searchRequestBuilder.size(
			size
		).queryString(
			searchContext1.getKeywords()
		).from(
			0
		);

		return searchRequestBuilder.build();
	}

	private Suggestion _getSuggestion(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, Locale locale,
		SearchHit searchHit) {

		Document document = searchHit.getDocument();

		String entryClassName = document.getString(Field.ENTRY_CLASS_NAME);

		SuggestionBuilder suggestionBuilder = _suggestionBuilderFactory.builder(
		).attribute(
			"fields",
			HashMapBuilder.<String, Object>put(
				Field.ENTRY_CLASS_NAME, entryClassName
			).build()
		).score(
			searchHit.getScore()
		);

		String text = null;

		try {
			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(entryClassName);

			if (assetRendererFactory == null) {
				return null;
			}

			long entryClassPK = document.getLong(Field.ENTRY_CLASS_PK);

			AssetRenderer<?> assetRenderer =
				assetRendererFactory.getAssetRenderer(entryClassPK);

			if (assetRenderer != null) {
				suggestionBuilder.attribute(
					"assetSearchSummary",
					assetRenderer.getSummary(
						liferayPortletRequest, liferayPortletResponse));

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

				text = assetRenderer.getTitle(locale);
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		if (text == null) {
			text = _getText(searchHit.getDocument(), locale);
		}

		return suggestionBuilder.text(
			text
		).build();
	}

	private String _getText(Document document, Locale locale) {
		String languageId = LocaleUtil.toLanguageId(locale);

		String text = document.getString(
			StringBundler.concat(
				Field.TITLE, StringPool.UNDERLINE, languageId));

		if (Validator.isBlank(text)) {
			text = document.getString("localized_title_" + languageId);
		}

		if (Validator.isBlank(text)) {
			text = document.getString(
				StringBundler.concat(
					Field.NAME, StringPool.UNDERLINE, languageId));
		}

		if (Validator.isBlank(text)) {
			text = document.getString(Field.TITLE);
		}

		return text;
	}

	private void _setIncludeAttachments(
		Map<String, Object> attributes, SearchContext searchContext) {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35128") ||
			MapUtil.isEmpty(attributes)) {

			return;
		}

		searchContext.setIncludeAttachments(
			MapUtil.getBoolean(attributes, "includeAttachments"));
	}

	private SuggestionsContributorResults _toSuggestionsContributorResults(
		String displayGroupName, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContext searchContext, List<SearchHit> searchHits) {

		return _suggestionsContributorResultsBuilderFactory.builder(
		).displayGroupName(
			displayGroupName
		).suggestions(
			TransformUtil.transform(
				searchHits,
				searchHit -> _getSuggestion(
					liferayPortletRequest, liferayPortletResponse,
					searchContext.getLocale(), searchHit))
		).build();
	}

	private static final int _CHARACTER_THRESHOLD = 2;

	private static final Log _log = LogFactoryUtil.getLog(
		BasicSuggestionsContributor.class);

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