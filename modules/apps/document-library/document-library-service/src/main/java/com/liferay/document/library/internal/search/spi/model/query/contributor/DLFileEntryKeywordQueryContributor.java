/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.search.spi.model.query.contributor;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.MatchQuery;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.localization.SearchLocalizationHelper;
import com.liferay.portal.search.query.QueryHelper;
import com.liferay.portal.search.spi.model.query.contributor.HighlightFieldNamesQueryConfigContributor;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(
	property = "indexer.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
	service = KeywordQueryContributor.class
)
public class DLFileEntryKeywordQueryContributor
	implements KeywordQueryContributor {

	@Override
	public void contribute(
		String keywords, BooleanQuery booleanQuery,
		KeywordQueryContributorHelper keywordQueryContributorHelper) {

		SearchContext searchContext =
			keywordQueryContributorHelper.getSearchContext();

		if (Validator.isNull(keywords)) {
			_queryHelper.addSearchLocalizedTerm(
				booleanQuery, searchContext, Field.DESCRIPTION, false);
			_queryHelper.addSearchTerm(
				booleanQuery, searchContext, Field.USER_NAME, false);
		}

		_queryHelper.addSearchTerm(
			booleanQuery, searchContext, "ddmContent", false);
		_queryHelper.addSearchTerm(
			booleanQuery, searchContext, "extension", false);
		_queryHelper.addSearchTerm(
			booleanQuery, searchContext, "fileEntryTypeId", false);

		_addSearchLocalizedTerm(booleanQuery, Field.CONTENT, searchContext);
		_addSearchLocalizedTerm(booleanQuery, Field.TITLE, searchContext);

		if (Validator.isNotNull(keywords)) {
			try {
				BooleanQuery fileNameBooleanQuery = new BooleanQueryImpl();

				_addKeywordsToFileNameBooleanQuery(
					fileNameBooleanQuery, keywords);

				booleanQuery.add(
					_getMatchQuery(
						"fileExtension", keywords,
						MatchQuery.Type.PHRASE_PREFIX),
					BooleanClauseOccur.SHOULD);
				fileNameBooleanQuery.add(
					_getMatchQuery(
						"fileName", keywords, MatchQuery.Type.PHRASE),
					BooleanClauseOccur.SHOULD);

				booleanQuery.add(
					fileNameBooleanQuery, BooleanClauseOccur.SHOULD);
			}
			catch (ParseException parseException) {
				throw new SystemException(parseException);
			}
		}

		_highlightFieldNamesQueryConfigContributor.
			contributeHighlightFieldNames(searchContext);
	}

	private void _addKeywordsToFileNameBooleanQuery(
			BooleanQuery fileNameBooleanQuery, String keywords)
		throws ParseException {

		String exactMatch = StringUtils.substringBetween(
			keywords, StringPool.QUOTE);

		if (Validator.isNull(exactMatch)) {
			fileNameBooleanQuery.add(
				_getShouldBooleanQuery(StringUtil.trim(keywords)),
				BooleanClauseOccur.MUST);
		}
		else {
			fileNameBooleanQuery.add(
				_getMatchQuery("fileName", exactMatch, MatchQuery.Type.PHRASE),
				BooleanClauseOccur.MUST);

			String remainingKeywords = keywords.replaceFirst(
				Pattern.quote(StringPool.QUOTE + exactMatch + StringPool.QUOTE),
				"");

			if (Validator.isNotNull(remainingKeywords)) {
				_addKeywordsToFileNameBooleanQuery(
					fileNameBooleanQuery, remainingKeywords);
			}
		}
	}

	private void _addSearchLocalizedTerm(
		BooleanQuery booleanQuery, String fieldName,
		SearchContext searchContext) {

		String value = searchContext.getKeywords();

		if (Validator.isBlank(value)) {
			return;
		}

		String[] localizedFieldNames =
			_searchLocalizationHelper.getLocalizedFieldNames(
				new String[] {fieldName}, searchContext);

		for (String localizedFieldName : localizedFieldNames) {
			_addTerm(booleanQuery, localizedFieldName, value);
		}
	}

	private void _addTerm(
		BooleanQuery booleanQuery, String field, String value) {

		try {
			booleanQuery.addTerm(field, value, false);
		}
		catch (ParseException parseException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to add term field \"", field,
						"\" with value \"", value, "\""),
					parseException);
			}
		}
	}

	private MatchQuery _getMatchQuery(
		String field, String keywords, MatchQuery.Type phrase) {

		MatchQuery matchPhraseQuery = new MatchQuery(field, keywords);

		matchPhraseQuery.setType(phrase);

		return matchPhraseQuery;
	}

	private BooleanQuery _getShouldBooleanQuery(String keyword)
		throws ParseException {

		BooleanQuery booleanQuery = new BooleanQueryImpl();

		booleanQuery.add(
			new MatchQuery("fileName", keyword), BooleanClauseOccur.SHOULD);
		booleanQuery.add(
			_getMatchQuery("fileName", keyword, MatchQuery.Type.PHRASE_PREFIX),
			BooleanClauseOccur.SHOULD);

		return booleanQuery;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileEntryKeywordQueryContributor.class);

	@Reference(
		target = "(indexer.class.name=com.liferay.document.library.kernel.model.DLFileEntry)"
	)
	private HighlightFieldNamesQueryConfigContributor
		_highlightFieldNamesQueryConfigContributor;

	@Reference
	private QueryHelper _queryHelper;

	@Reference
	private SearchLocalizationHelper _searchLocalizationHelper;

}