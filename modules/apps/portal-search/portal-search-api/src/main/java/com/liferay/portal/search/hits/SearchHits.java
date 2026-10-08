/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.hits;

import java.io.Serializable;

import java.util.Collection;
import java.util.List;

/**
 * @author Michael C. Han
 * @author André de Oliveira
 */
public class SearchHits implements Serializable {

	public void addSearchHits(Collection<SearchHit> searchHits) {
		_searchHits.addAll(searchHits);
	}

	public float getMaxScore() {
		return _maxScore;
	}

	public List<SearchHit> getSearchHits() {
		return _searchHits;
	}

	public long getSearchTime() {
		return _searchTime;
	}

	public long getTotalHits() {
		return _totalHits;
	}

	public TotalHitsRelation getTotalHitsRelation() {
		return _totalHitsRelation;
	}

	protected SearchHits(
		float maxScore, List<SearchHit> searchHits, long searchTime,
		long totalHits) {

		this(maxScore, searchHits, searchTime, totalHits, TotalHitsRelation.EQ);
	}

	protected SearchHits(
		float maxScore, List<SearchHit> searchHits, long searchTime,
		long totalHits, TotalHitsRelation totalHitsRelation) {

		_maxScore = maxScore;
		_searchHits = searchHits;
		_searchTime = searchTime;
		_totalHits = totalHits;
		_totalHitsRelation = totalHitsRelation;
	}

	private final float _maxScore;
	private final List<SearchHit> _searchHits;
	private final long _searchTime;
	private final long _totalHits;
	private final TotalHitsRelation _totalHitsRelation;

}