/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.hits;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Wade Cao
 */
public class SearchHitsBuilder {

	public SearchHitsBuilder addSearchHit(SearchHit searchHit) {
		_searchHits.add(searchHit);

		return this;
	}

	public SearchHitsBuilder addSearchHits(Collection<SearchHit> searchHits) {
		_searchHits.addAll(searchHits);

		return this;
	}

	public SearchHits build() {
		return new SearchHits(
			_maxScore, _searchHits, _searchTime, _totalHits,
			_totalHitsRelation);
	}

	public SearchHitsBuilder maxScore(float maxScore) {
		_maxScore = maxScore;

		return this;
	}

	public SearchHitsBuilder searchTime(long searchTime) {
		_searchTime = searchTime;

		return this;
	}

	public SearchHitsBuilder totalHits(long totalHits) {
		_totalHits = totalHits;

		return this;
	}

	public SearchHitsBuilder totalHitsRelation(
		TotalHitsRelation totalHitsRelation) {

		_totalHitsRelation = totalHitsRelation;

		return this;
	}

	private float _maxScore;
	private final List<SearchHit> _searchHits = new ArrayList<>();
	private long _searchTime;
	private long _totalHits;
	private TotalHitsRelation _totalHitsRelation = TotalHitsRelation.EQ;

}