/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.info.collection.provider;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.util.AssetHelper;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.FilteredInfoCollectionProvider;
import com.liferay.info.collection.provider.SingleFormVariationInfoCollectionProvider;
import com.liferay.info.pagination.InfoPage;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.search.experiences.model.SXPBlueprint;

import java.util.Collections;

/**
 * @author Tibor Lipusz
 * @author Gustavo Lima
 */
public class AssetEntrySXPBlueprintInfoCollectionProvider
	extends SXPBlueprintInfoCollectionProvider<AssetEntry>
	implements FilteredInfoCollectionProvider<AssetEntry>,
			   SingleFormVariationInfoCollectionProvider<AssetEntry> {

	public AssetEntrySXPBlueprintInfoCollectionProvider(
		AssetHelper assetHelper, Searcher searcher,
		SearchRequestBuilderFactory searchRequestBuilderFactory,
		SXPBlueprint sxpBlueprint) {

		super(assetHelper, searcher, searchRequestBuilderFactory, sxpBlueprint);
	}

	@Override
	public InfoPage<AssetEntry> getCollectionInfoPage(
		CollectionQuery collectionQuery) {

		try {
			SearchResponse searchResponse = getCollectionQuerySearchResponse(
				collectionQuery);

			return InfoPage.of(
				assetHelper.getAssetEntries(searchResponse.getSearchHits()),
				collectionQuery.getPagination(), searchResponse.getTotalHits());
		}
		catch (Exception exception) {
			_log.error("Unable to get asset entries", exception);
		}

		return InfoPage.of(
			Collections.emptyList(), collectionQuery.getPagination(), 0);
	}

	@Override
	public String getKey() {
		return StringBundler.concat(
			AssetEntrySXPBlueprintInfoCollectionProvider.class.getName(),
			StringPool.UNDERLINE, sxpBlueprint.getCompanyId(),
			StringPool.UNDERLINE, sxpBlueprint.getExternalReferenceCode());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetEntrySXPBlueprintInfoCollectionProvider.class);

}