/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.search.spi.model.query.contributor;

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
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "indexer.class.name=com.liferay.journal.model.JournalFolder",
	service = KeywordQueryContributor.class
)
public class JournalFolderKeywordQueryContributor
	implements KeywordQueryContributor {

	@Override
	public void contribute(
		String keywords, BooleanQuery booleanQuery,
		KeywordQueryContributorHelper keywordQueryContributorHelper) {

		SearchContext searchContext =
			keywordQueryContributorHelper.getSearchContext();

		_queryHelper.addSearchLocalizedTerm(
			booleanQuery, searchContext, Field.DESCRIPTION, false);
		_queryHelper.addSearchTerm(
			booleanQuery, searchContext, Field.DESCRIPTION, false);
		_queryHelper.addSearchLocalizedTerm(
			booleanQuery, searchContext, Field.TITLE, false);
		_queryHelper.addSearchTerm(
			booleanQuery, searchContext, Field.TITLE, false);

		_highlightFieldNamesQueryConfigContributor.
			contributeHighlightFieldNames(searchContext);
	}

	@Reference(
		target = "(indexer.class.name=com.liferay.journal.model.JournalFolder)"
	)
	private HighlightFieldNamesQueryConfigContributor
		_highlightFieldNamesQueryConfigContributor;

	@Reference
	private QueryHelper _queryHelper;

}