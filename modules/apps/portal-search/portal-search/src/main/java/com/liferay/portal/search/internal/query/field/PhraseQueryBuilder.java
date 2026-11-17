/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.query.field;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.search.query.MatchQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.Query;

/**
 * @author André de Oliveira
 */
public class PhraseQueryBuilder {

	public Query build(String field, String value) {
		MatchQuery.Type type = MatchQuery.Type.PHRASE;

		if (_prefix) {
			type = MatchQuery.Type.PHRASE_PREFIX;
		}

		if (_trailingStarAware && value.endsWith(StringPool.STAR)) {
			value = value.substring(0, value.length() - 1);

			type = MatchQuery.Type.PHRASE_PREFIX;
		}

		MatchQuery matchQuery = QueriesUtil.match(field, value);

		if (_analyzer != null) {
			matchQuery.setAnalyzer(_analyzer);
		}

		if (_boost != null) {
			matchQuery.setBoost(_boost);
		}

		if (_prefix && (_maxExpansions != null)) {
			matchQuery.setMaxExpansions(_maxExpansions);
		}

		if (_slop != null) {
			matchQuery.setSlop(_slop);
		}

		matchQuery.setType(type);

		return matchQuery;
	}

	public void setAnalyzer(String analyzer) {
		_analyzer = analyzer;
	}

	public void setBoost(float boost) {
		_boost = boost;
	}

	public void setMaxExpansions(int maxExpansions) {
		_maxExpansions = maxExpansions;
	}

	public void setPrefix(boolean prefix) {
		_prefix = prefix;
	}

	public void setSlop(int slop) {
		_slop = slop;
	}

	public void setTrailingStarAware(boolean trailingStarAware) {
		_trailingStarAware = trailingStarAware;
	}

	private String _analyzer;
	private Float _boost;
	private Integer _maxExpansions;
	private boolean _prefix;
	private Integer _slop;
	private boolean _trailingStarAware;

}