/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.search.spi.model.query.contributor;

import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.ExpandoQueryContributor;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.localization.SearchLocalizationHelper;
import com.liferay.portal.search.query.QueryHelper;
import com.liferay.portal.search.spi.model.query.contributor.HighlightFieldNamesQueryConfigContributor;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;

import java.util.LinkedHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "indexer.class.name=com.liferay.journal.model.JournalArticle",
	service = KeywordQueryContributor.class
)
public class JournalArticleKeywordQueryContributor
	implements KeywordQueryContributor {

	@Override
	public void contribute(
		String keywords, BooleanQuery booleanQuery,
		KeywordQueryContributorHelper keywordQueryContributorHelper) {

		SearchContext searchContext =
			keywordQueryContributorHelper.getSearchContext();

		_queryHelper.addSearchTerm(
			booleanQuery, searchContext, Field.ARTICLE_ID, false);
		_queryHelper.addSearchTerm(
			booleanQuery, searchContext, Field.CLASS_PK, false);
		_addSearchLocalizedTerm(booleanQuery, searchContext, Field.CONTENT);
		_addSearchLocalizedTerm(booleanQuery, searchContext, Field.DESCRIPTION);
		_queryHelper.addSearchTerm(
			booleanQuery, searchContext, Field.ENTRY_CLASS_PK, false);
		_addSearchLocalizedTerm(booleanQuery, searchContext, Field.TITLE);
		_queryHelper.addSearchTerm(
			booleanQuery, searchContext, Field.USER_NAME, false);

		LinkedHashMap<String, Object> params =
			(LinkedHashMap<String, Object>)searchContext.getAttribute("params");

		if (params != null) {
			String expandoAttributes = (String)params.get("expandoAttributes");

			if (Validator.isNotNull(expandoAttributes)) {
				_expandoQueryContributor.contribute(
					expandoAttributes, booleanQuery,
					new String[] {JournalArticle.class.getName()},
					searchContext);
			}
		}

		_highlightFieldNamesQueryConfigContributor.
			contributeHighlightFieldNames(searchContext);
	}

	private void _addLocalizedFields(
		BooleanQuery booleanQuery, String fieldName, String value,
		SearchContext searchContext) {

		String[] localizedFieldNames =
			_searchLocalizationHelper.getLocalizedFieldNames(
				new String[] {fieldName}, searchContext);

		for (String localizedFieldName : localizedFieldNames) {
			_addTerm(booleanQuery, localizedFieldName, value);
		}
	}

	private void _addLocalizedQuery(
		BooleanQuery booleanQuery, BooleanQuery localizedQuery,
		SearchContext searchContext) {

		BooleanClauseOccur booleanClauseOccur = BooleanClauseOccur.SHOULD;

		if (searchContext.isAndSearch()) {
			booleanClauseOccur = BooleanClauseOccur.MUST;
		}

		try {
			booleanQuery.add(localizedQuery, booleanClauseOccur);
		}
		catch (ParseException parseException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to add localized localized query ",
						localizedQuery, " with boolean clause occur ",
						booleanClauseOccur),
					parseException);
			}
		}
	}

	private void _addSearchLocalizedTerm(
		BooleanQuery booleanQuery, SearchContext searchContext,
		String fieldName) {

		if (Validator.isBlank(fieldName)) {
			return;
		}

		String value = GetterUtil.getString(
			searchContext.getAttribute(fieldName));

		if (Validator.isBlank(value)) {
			value = searchContext.getKeywords();
		}

		if (Validator.isBlank(value)) {
			return;
		}

		if (Validator.isBlank(searchContext.getKeywords())) {
			BooleanQuery localizedQuery = new BooleanQueryImpl();

			_addLocalizedFields(
				localizedQuery, fieldName, value, searchContext);

			_addLocalizedQuery(booleanQuery, localizedQuery, searchContext);
		}
		else {
			_addLocalizedFields(booleanQuery, fieldName, value, searchContext);
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
						"Unable to add search term to query field:", field,
						" value:", value, " like:", false),
					parseException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleKeywordQueryContributor.class);

	@Reference
	private ExpandoQueryContributor _expandoQueryContributor;

	@Reference(
		target = "(indexer.class.name=com.liferay.journal.model.JournalArticle)"
	)
	private HighlightFieldNamesQueryConfigContributor
		_highlightFieldNamesQueryConfigContributor;

	@Reference
	private QueryHelper _queryHelper;

	@Reference
	private SearchLocalizationHelper _searchLocalizationHelper;

}