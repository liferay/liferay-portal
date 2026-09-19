/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.searcher;

import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.search.asset.SearchableAssetClassNamesProvider;
import com.liferay.portal.search.internal.expando.helper.ExpandoQueryContributorHelper;
import com.liferay.portal.search.internal.indexer.helper.AddSearchKeywordsQueryContributorHelper;
import com.liferay.portal.search.internal.indexer.helper.PostProcessSearchQueryContributorHelper;
import com.liferay.portal.search.internal.indexer.helper.PreFilterContributorHelper;
import com.liferay.portal.search.internal.searcher.helper.IndexSearcherHelper;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(service = FacetedSearcherManager.class)
public class FacetedSearcherManagerImpl implements FacetedSearcherManager {

	@Override
	public FacetedSearcher createFacetedSearcher() {
		return new FacetedSearcherImpl(
			addSearchKeywordsQueryContributorHelper,
			expandoQueryContributorHelper, indexerRegistry, indexSearcherHelper,
			postProcessSearchQueryContributorHelper, preFilterContributorHelper,
			searchableAssetClassNamesProvider, searchRequestBuilderFactory);
	}

	@Reference
	protected AddSearchKeywordsQueryContributorHelper
		addSearchKeywordsQueryContributorHelper;

	@Reference
	protected ExpandoQueryContributorHelper expandoQueryContributorHelper;

	@Reference
	protected IndexSearcherHelper indexSearcherHelper;

	@Reference
	protected IndexerRegistry indexerRegistry;

	protected Localization localization;

	@Reference
	protected PostProcessSearchQueryContributorHelper
		postProcessSearchQueryContributorHelper;

	@Reference
	protected PreFilterContributorHelper preFilterContributorHelper;

	@Reference
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Reference
	protected SearchableAssetClassNamesProvider
		searchableAssetClassNamesProvider;

}