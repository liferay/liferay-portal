/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.indexer.helper;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.generic.StringQuery;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.constants.SearchContextAttributes;
import com.liferay.portal.search.internal.indexer.IncludeExcludeUtil;
import com.liferay.portal.search.internal.indexer.IndexerProvidedClausesUtil;
import com.liferay.portal.search.internal.util.SearchStringUtil;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author André de Oliveira
 */
@Component(service = AddSearchKeywordsQueryContributorHelper.class)
public class AddSearchKeywordsQueryContributorHelperImpl
	implements AddSearchKeywordsQueryContributorHelper {

	@Override
	public void contribute(
		BooleanQuery booleanQuery, SearchContext searchContext) {

		if (IndexerProvidedClausesUtil.shouldSuppress(searchContext)) {
			return;
		}

		_addKeywordQueryContributorClauses(booleanQuery, searchContext);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, KeywordQueryContributor.class,
			"(!(indexer.class.name=*))");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	protected Collection<String> getStrings(
		String string, SearchContext searchContext) {

		return Arrays.asList(
			SearchStringUtil.splitAndUnquote(
				(String)searchContext.getAttribute(string)));
	}

	private void _addKeywordQueryContributorClauses(
		BooleanQuery booleanQuery, SearchContext searchContext) {

		boolean luceneSyntax = GetterUtil.getBoolean(
			searchContext.getAttribute(
				SearchContextAttributes.ATTRIBUTE_KEY_LUCENE_SYNTAX));

		String keywords = searchContext.getKeywords();

		if (luceneSyntax) {
			_addStringQuery(booleanQuery, keywords);

			return;
		}

		List<KeywordQueryContributor> filteredKeywordQueryContributors =
			IncludeExcludeUtil.filter(
				_serviceTrackerList.toList(),
				getStrings(
					"search.full.query.clause.contributors.includes",
					searchContext),
				getStrings(
					"search.full.query.clause.contributors.excludes",
					searchContext),
				this::_getClassName);

		for (KeywordQueryContributor keywordQueryContributor :
				filteredKeywordQueryContributors) {

			keywordQueryContributor.contribute(
				keywords, booleanQuery,
				new KeywordQueryContributorHelper() {

					@Override
					public String getClassName() {
						return null;
					}

					@Override
					public String[] getSearchClassNames() {
						throw new UnsupportedOperationException();
					}

					@Override
					public SearchContext getSearchContext() {
						return searchContext;
					}

				});
		}
	}

	private void _addStringQuery(BooleanQuery booleanQuery, String keywords) {
		if (Validator.isBlank(keywords)) {
			return;
		}

		try {
			booleanQuery.add(
				new StringQuery(keywords), BooleanClauseOccur.MUST);
		}
		catch (ParseException parseException) {
			throw new RuntimeException(parseException);
		}
	}

	private String _getClassName(Object object) {
		Class<?> clazz = object.getClass();

		return clazz.getName();
	}

	private ServiceTrackerList<KeywordQueryContributor> _serviceTrackerList;

}