/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sort;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.GeoDistanceSort;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.geolocation.GeoLocationPoint;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.NestedSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

/**
 * @author Michael C. Han
 */
public class SortTranslator {

	public void translate(
		SearchSourceBuilder searchSourceBuilder, Sort[] sorts) {

		if (ArrayUtil.isEmpty(sorts)) {
			return;
		}

		Set<String> sortFieldNames = new HashSet<>();

		for (Sort sort : sorts) {
			if (sort == null) {
				continue;
			}

			String sortFieldName = _getSortFieldName(sort, "_score");

			if (sortFieldNames.contains(sortFieldName)) {
				continue;
			}

			sortFieldNames.add(sortFieldName);

			searchSourceBuilder.sort(_getSortBuilder(sort, sortFieldName));
		}
	}

	private SortBuilder<?> _getFieldSortBuilder(Sort sort, String fieldName) {
		FieldSortBuilder fieldSortBuilder = SortBuilders.fieldSort(fieldName);

		fieldSortBuilder.unmappedType("keyword");

		if (sort.isReverse()) {
			fieldSortBuilder.order(SortOrder.DESC);
		}

		return fieldSortBuilder;
	}

	private SortBuilder<?> _getGeoDistanceSortBuilder(
		Sort sort, String fieldName) {

		GeoDistanceSort geoDistanceSort = (GeoDistanceSort)sort;

		List<GeoPoint> geoPoints = new ArrayList<>();

		for (GeoLocationPoint geoLocationPoint :
				geoDistanceSort.getGeoLocationPoints()) {

			geoPoints.add(
				new GeoPoint(
					geoLocationPoint.getLatitude(),
					geoLocationPoint.getLongitude()));
		}

		GeoDistanceSortBuilder geoDistanceSortBuilder =
			SortBuilders.geoDistanceSort(
				fieldName, geoPoints.toArray(new GeoPoint[0]));

		geoDistanceSortBuilder.geoDistance(GeoDistance.ARC);

		Collection<String> geoHashes = geoDistanceSort.getGeoHashes();

		if (!geoHashes.isEmpty()) {
			geoDistanceSort.addGeoHash(geoHashes.toArray(new String[0]));
		}

		if (sort.isReverse()) {
			geoDistanceSortBuilder.order(SortOrder.DESC);
		}

		return geoDistanceSortBuilder;
	}

	private SortBuilder<?> _getNestedFieldSortBuilder(
		Sort sort, String sortFieldName) {

		String[] parts = StringUtil.split(sortFieldName, StringPool.POUND);

		String sortField = parts[0];

		FieldSortBuilder fieldSortBuilder = SortBuilders.fieldSort(sortField);

		if (sort.isReverse()) {
			fieldSortBuilder.order(SortOrder.DESC);
		}

		NestedSortBuilder nestedSortBuilder = new NestedSortBuilder(
			"nestedFieldArray");

		String fieldName = parts[1];

		nestedSortBuilder.setFilter(
			QueryBuilders.termQuery("nestedFieldArray.fieldName", fieldName));

		fieldSortBuilder.setNestedSort(nestedSortBuilder);

		return fieldSortBuilder;
	}

	private SortBuilder<?> _getScoreSortBuilder(Sort sort) {
		SortBuilder<?> sortBuilder = SortBuilders.scoreSort();

		if (sort.isReverse()) {
			sortBuilder.order(SortOrder.ASC);
		}

		return sortBuilder;
	}

	private SortBuilder<?> _getSortBuilder(Sort sort, String fieldName) {
		if (fieldName.equals("_score")) {
			return _getScoreSortBuilder(sort);
		}

		if (sort.getType() == Sort.GEO_DISTANCE_TYPE) {
			return _getGeoDistanceSortBuilder(sort, fieldName);
		}

		if (fieldName.startsWith("nestedFieldArray.")) {
			return _getNestedFieldSortBuilder(sort, fieldName);
		}

		return _getFieldSortBuilder(sort, fieldName);
	}

	private String _getSortFieldName(Sort sort, String scoreFieldName) {
		String sortFieldName = sort.getFieldName();

		if (Objects.equals(sortFieldName, Field.PRIORITY) ||
			Objects.equals(sortFieldName, "_score")) {

			return sortFieldName;
		}

		if ((sortFieldName != null) &&
			sortFieldName.startsWith("nestedFieldArray.")) {

			return sortFieldName;
		}

		return Field.getSortFieldName(sort, scoreFieldName);
	}

}