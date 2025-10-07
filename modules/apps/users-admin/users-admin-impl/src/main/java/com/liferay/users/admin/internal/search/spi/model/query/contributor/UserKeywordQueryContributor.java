/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.internal.search.spi.model.query.contributor;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.WildcardQuery;
import com.liferay.portal.kernel.search.generic.WildcardQueryImpl;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.query.QueryHelper;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luan Maoski
 */
@Component(
	property = "indexer.class.name=com.liferay.portal.kernel.model.User",
	service = KeywordQueryContributor.class
)
public class UserKeywordQueryContributor implements KeywordQueryContributor {

	@Override
	public void contribute(
		String keywords, BooleanQuery booleanQuery,
		KeywordQueryContributorHelper keywordQueryContributorHelper) {

		SearchContext searchContext =
			keywordQueryContributorHelper.getSearchContext();

		_addHighlightFieldNames(searchContext);

		queryHelper.addSearchTerm(booleanQuery, searchContext, "city", false);
		queryHelper.addSearchTerm(
			booleanQuery, searchContext, "country", false);
		queryHelper.addSearchTerm(
			booleanQuery, searchContext, "emailAddress.text", false);
		queryHelper.addSearchLocalizedTerm(
			booleanQuery, searchContext, "firstName", false);
		queryHelper.addSearchTerm(
			booleanQuery, searchContext, "firstName", false);
		queryHelper.addSearchLocalizedTerm(
			booleanQuery, searchContext, "fullName", false);
		queryHelper.addSearchTerm(
			booleanQuery, searchContext, "fullName", false);
		queryHelper.addSearchTerm(
			booleanQuery, searchContext, "jobTitle", false);
		queryHelper.addSearchLocalizedTerm(
			booleanQuery, searchContext, "lastName", false);
		queryHelper.addSearchTerm(
			booleanQuery, searchContext, "lastName", false);
		queryHelper.addSearchLocalizedTerm(
			booleanQuery, searchContext, "middleName", false);
		queryHelper.addSearchTerm(
			booleanQuery, searchContext, "middleName", false);
		queryHelper.addSearchTerm(booleanQuery, searchContext, "region", false);
		queryHelper.addSearchTerm(
			booleanQuery, searchContext, "screenName", false);
		queryHelper.addSearchTerm(
			booleanQuery, searchContext, "screenName.text", false);
		queryHelper.addSearchTerm(booleanQuery, searchContext, "street", false);
		queryHelper.addSearchTerm(booleanQuery, searchContext, "zip", false);

		if (Validator.isNotNull(keywords)) {
			try {
				keywords = StringUtil.toLowerCase(keywords);

				booleanQuery.add(
					_getTrailingWildcardQuery("emailAddress", keywords),
					BooleanClauseOccur.SHOULD);
				booleanQuery.add(
					_getTrailingWildcardQuery("emailAddressDomain", keywords),
					BooleanClauseOccur.SHOULD);
				booleanQuery.add(
					_getTrailingWildcardQuery("screenName.text", keywords),
					BooleanClauseOccur.SHOULD);
			}
			catch (ParseException parseException) {
				throw new SystemException(parseException);
			}
		}
	}

	@Reference
	protected QueryHelper queryHelper;

	private void _addHighlightFieldNames(SearchContext searchContext) {
		QueryConfig queryConfig = searchContext.getQueryConfig();

		if (!queryConfig.isHighlightEnabled()) {
			return;
		}

		queryConfig.addHighlightFieldNames("fullName");
	}

	private WildcardQuery _getTrailingWildcardQuery(
		String field, String value) {

		return new WildcardQueryImpl(field, value + StringPool.STAR);
	}

}