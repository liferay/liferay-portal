/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.search;

import com.liferay.portal.search.engine.adapter.search.ClearScrollRequest;
import com.liferay.portal.search.engine.adapter.search.ClearScrollResponse;
import com.liferay.portal.search.engine.adapter.search.ClosePointInTimeRequest;
import com.liferay.portal.search.engine.adapter.search.ClosePointInTimeResponse;
import com.liferay.portal.search.engine.adapter.search.CountSearchRequest;
import com.liferay.portal.search.engine.adapter.search.CountSearchResponse;
import com.liferay.portal.search.engine.adapter.search.MultisearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.MultisearchSearchResponse;
import com.liferay.portal.search.engine.adapter.search.OpenPointInTimeRequest;
import com.liferay.portal.search.engine.adapter.search.OpenPointInTimeResponse;
import com.liferay.portal.search.engine.adapter.search.SearchRequestExecutor;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.engine.adapter.search.SuggestSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SuggestSearchResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = "search.engine.impl=OpenSearch",
	service = SearchRequestExecutor.class
)
public class OpenSearchSearchRequestExecutor implements SearchRequestExecutor {

	@Override
	public ClearScrollResponse executeSearchRequest(
		ClearScrollRequest clearScrollRequest) {

		return _clearScrollRequestExecutor.execute(clearScrollRequest);
	}

	@Override
	public ClosePointInTimeResponse executeSearchRequest(
		ClosePointInTimeRequest closePointInTimeRequest) {

		return _closePointInTimeRequestExecutor.execute(
			closePointInTimeRequest);
	}

	@Override
	public CountSearchResponse executeSearchRequest(
		CountSearchRequest countSearchRequest) {

		return _countSearchRequestExecutor.execute(countSearchRequest);
	}

	@Override
	public MultisearchSearchResponse executeSearchRequest(
		MultisearchSearchRequest multisearchSearchRequest) {

		return _multisearchSearchRequestExecutor.execute(
			multisearchSearchRequest);
	}

	@Override
	public OpenPointInTimeResponse executeSearchRequest(
		OpenPointInTimeRequest openPointInTimeRequest) {

		return _openPointInTimeRequestExecutor.execute(openPointInTimeRequest);
	}

	@Override
	public SearchSearchResponse executeSearchRequest(
		SearchSearchRequest searchSearchRequest) {

		return _searchSearchRequestExecutor.execute(searchSearchRequest);
	}

	@Override
	public SuggestSearchResponse executeSearchRequest(
		SuggestSearchRequest suggestSearchRequest) {

		return _suggestSearchRequestExecutor.execute(suggestSearchRequest);
	}

	@Activate
	protected void activate() {
		_clearScrollRequestExecutor = new ClearScrollRequestExecutor(
			_openSearchConnectionManager);
	}

	private ClearScrollRequestExecutor _clearScrollRequestExecutor;

	@Reference
	private ClosePointInTimeRequestExecutor _closePointInTimeRequestExecutor;

	@Reference
	private CountSearchRequestExecutor _countSearchRequestExecutor;

	@Reference
	private MultisearchSearchRequestExecutor _multisearchSearchRequestExecutor;

	@Reference
	private OpenPointInTimeRequestExecutor _openPointInTimeRequestExecutor;

	@Reference
	private OpenSearchConnectionManager _openSearchConnectionManager;

	@Reference
	private SearchSearchRequestExecutor _searchSearchRequestExecutor;

	@Reference
	private SuggestSearchRequestExecutor _suggestSearchRequestExecutor;

}