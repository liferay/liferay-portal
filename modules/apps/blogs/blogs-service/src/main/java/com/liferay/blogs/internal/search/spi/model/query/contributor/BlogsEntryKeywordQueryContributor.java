/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.internal.search.spi.model.query.contributor;

import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.search.query.QueryHelper;
import com.liferay.portal.search.spi.model.query.contributor.HighlightFieldNamesQueryConfigContributor;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Gamarra
 */
@Component(
	property = "indexer.class.name=com.liferay.blogs.model.BlogsEntry",
	service = KeywordQueryContributor.class
)
public class BlogsEntryKeywordQueryContributor
	implements KeywordQueryContributor {

	@Override
	public void contribute(
		String keywords, BooleanQuery booleanQuery,
		KeywordQueryContributorHelper keywordQueryContributorHelper) {

		SearchContext searchContext =
			keywordQueryContributorHelper.getSearchContext();

		_queryHelper.addSearchLocalizedTerm(
			booleanQuery, searchContext, Field.CONTENT, false);
		_queryHelper.addSearchTerm(
			booleanQuery, searchContext, Field.SUBTITLE, false);
		_queryHelper.addSearchLocalizedTerm(
			booleanQuery, searchContext, Field.TITLE, false);

		_highlightFieldNamesQueryConfigContributor.
			contributeHighlightFieldNames(searchContext);
	}

	@Reference(
		target = "(indexer.class.name=com.liferay.blogs.model.BlogsEntry)"
	)
	private HighlightFieldNamesQueryConfigContributor
		_highlightFieldNamesQueryConfigContributor;

	@Reference
	private QueryHelper _queryHelper;

}