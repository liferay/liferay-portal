/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.machine.learning.internal.search.index;

import com.liferay.analytics.machine.learning.internal.recommendation.constants.RecommendationIndexNames;
import com.liferay.analytics.machine.learning.internal.recommendation.search.RecommendationIndexer;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.spi.reindexer.IndexReindexer;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "search.index.category=content-recommendation",
	service = IndexReindexer.class
)
public class MostViewedContentRecommendationIndexReindexer
	implements IndexReindexer {

	@Override
	public void reindex(long companyId, ExecutionMode executionMode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LRAC-14771") ||
			(companyId == CompanyConstants.SYSTEM)) {

			return;
		}

		if (executionMode != ExecutionMode.SYNC) {
			_recommendationIndexer.dropIndex(companyId);
		}

		_recommendationIndexer.createIndex(companyId);
	}

	@Activate
	protected void activate() {
		_recommendationIndexer = new RecommendationIndexer(
			RecommendationIndexNames.MOST_VIEWED_CONTENT_RECOMMENDATION,
			_indexNameBuilder, _searchCapabilities, _searchEngineAdapter);
	}

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	private RecommendationIndexer _recommendationIndexer;

	@Reference
	private SearchCapabilities _searchCapabilities;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

}