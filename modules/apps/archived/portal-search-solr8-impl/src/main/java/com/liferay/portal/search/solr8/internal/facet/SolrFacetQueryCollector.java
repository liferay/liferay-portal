/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal.facet;

import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.DefaultTermCollector;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.util.NamedList;

/**
 * @author Raymond Augé
 */
public class SolrFacetQueryCollector implements FacetCollector {

	public SolrFacetQueryCollector(Facet facet, NamedList<?> namedList) {
		String name = FacetUtil.getAggregationName(facet);

		_counts = _getCountsInSameOrder(namedList.asMap(0), name);
		_fieldName = name;
	}

	@Override
	public String getFieldName() {
		return _fieldName;
	}

	@Override
	public TermCollector getTermCollector(String term) {
		return new DefaultTermCollector(
			term, GetterUtil.getInteger(_counts.get(term)));
	}

	@Override
	public List<TermCollector> getTermCollectors() {
		if (_termCollectors != null) {
			return _termCollectors;
		}

		List<TermCollector> termCollectors = new ArrayList<>();

		for (Map.Entry<String, Integer> entry : _counts.entrySet()) {
			Integer count = entry.getValue();

			TermCollector termCollector = new DefaultTermCollector(
				entry.getKey(), count.intValue());

			termCollectors.add(termCollector);
		}

		_termCollectors = termCollectors;

		return _termCollectors;
	}

	private Map<String, Integer> _getCountsInSameOrder(
		Map<String, NamedList<?>> map1, String name) {

		Map<String, Integer> map2 = new LinkedHashMap<>();

		for (Map.Entry<String, NamedList<?>> entry : map1.entrySet()) {
			String bucket = entry.getKey();

			if (!bucket.startsWith(name)) {
				continue;
			}

			NamedList<?> namedList = entry.getValue();

			map2.put(
				bucket.substring(name.length() + 1),
				GetterUtil.getInteger(namedList.get("count")));
		}

		return map2;
	}

	private final Map<String, Integer> _counts;
	private final String _fieldName;
	private List<TermCollector> _termCollectors;

}