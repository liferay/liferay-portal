/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sort;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.search.elasticsearch7.internal.geolocation.DistanceUnitTranslator;
import com.liferay.portal.search.elasticsearch7.internal.geolocation.GeoDistanceTypeTranslator;
import com.liferay.portal.search.elasticsearch7.internal.geolocation.GeoLocationPointTranslator;
import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.elasticsearch7.internal.script.ScriptTranslator;
import com.liferay.portal.search.query.QueryTranslator;
import com.liferay.portal.search.sort.FieldSort;
import com.liferay.portal.search.sort.GeoDistanceSort;
import com.liferay.portal.search.sort.NestedSort;
import com.liferay.portal.search.sort.ScoreSort;
import com.liferay.portal.search.sort.ScriptSort;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.search.sort.SortFieldTranslator;
import com.liferay.portal.search.sort.SortMode;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.SortVisitor;

import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.NestedSortBuilder;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;

/**
 * @author Michael C. Han
 */
public class ElasticsearchSortFieldTranslator
	implements SortFieldTranslator<SortBuilder<?>>,
			   SortVisitor<SortBuilder<?>> {

	@Override
	public SortBuilder<?> translate(Sort sort) {
		return sort.accept(this);
	}

	@Override
	public SortBuilder<?> visit(FieldSort fieldSort) {
		FieldSortBuilder fieldSortBuilder = SortBuilders.fieldSort(
			fieldSort.getField());

		fieldSortBuilder.order(translate(fieldSort.getSortOrder()));

		if (fieldSort.getMissing() != null) {
			fieldSortBuilder.missing(fieldSort.getMissing());
		}

		if (fieldSort.getNestedSort() != null) {
			fieldSortBuilder.setNestedSort(
				translate(fieldSort.getNestedSort()));
		}

		if (fieldSort.getSortMode() != null) {
			fieldSortBuilder.sortMode(translate(fieldSort.getSortMode()));
		}

		return fieldSortBuilder.unmappedType("keyword");
	}

	@Override
	public SortBuilder<?> visit(GeoDistanceSort geoDistanceSort) {
		GeoDistanceSortBuilder geoDistanceSortBuilder =
			SortBuilders.geoDistanceSort(
				geoDistanceSort.getField(),
				TransformUtil.transformToArray(
					geoDistanceSort.getGeoLocationPoints(),
					GeoLocationPointTranslator::translate, GeoPoint.class));

		geoDistanceSortBuilder.order(translate(geoDistanceSort.getSortOrder()));

		if (geoDistanceSort.getDistanceUnit() != null) {
			geoDistanceSortBuilder.unit(
				_distanceUnitTranslator.translate(
					geoDistanceSort.getDistanceUnit()));
		}

		if (geoDistanceSort.getGeoDistanceType() != null) {
			GeoDistance geoDistance = _geoDistanceTypeTranslator.translate(
				geoDistanceSort.getGeoDistanceType());

			geoDistanceSortBuilder.geoDistance(geoDistance);
		}

		if (geoDistanceSort.getNestedSort() != null) {
			geoDistanceSortBuilder.setNestedSort(
				translate(geoDistanceSort.getNestedSort()));
		}

		if (geoDistanceSort.getSortMode() != null) {
			geoDistanceSortBuilder.sortMode(
				translate(geoDistanceSort.getSortMode()));
		}

		return geoDistanceSortBuilder;
	}

	@Override
	public SortBuilder<?> visit(ScoreSort scoreSort) {
		return SortBuilders.scoreSort(
		).order(
			translate(scoreSort.getSortOrder())
		);
	}

	@Override
	public SortBuilder<?> visit(ScriptSort scriptSort) {
		Script script = _scriptTranslator.translate(scriptSort.getScript());

		ScriptSortBuilder.ScriptSortType scriptSortType =
			ScriptSortBuilder.ScriptSortType.NUMBER;

		if (scriptSort.getScriptSortType() ==
				ScriptSort.ScriptSortType.STRING) {

			scriptSortType = ScriptSortBuilder.ScriptSortType.STRING;
		}

		ScriptSortBuilder scriptSortBuilder = SortBuilders.scriptSort(
			script, scriptSortType);

		if (scriptSort.getNestedSort() != null) {
			scriptSortBuilder.setNestedSort(
				translate(scriptSort.getNestedSort()));
		}

		if (scriptSort.getSortMode() != null) {
			scriptSortBuilder.sortMode(translate(scriptSort.getSortMode()));
		}

		scriptSortBuilder.order(translate(scriptSort.getSortOrder()));

		return scriptSortBuilder;
	}

	protected NestedSortBuilder translate(NestedSort nestedSort) {
		NestedSortBuilder nestedSortBuilder = new NestedSortBuilder(
			nestedSort.getPath());

		if (nestedSort.getFilterQuery() != null) {
			QueryBuilder queryBuilder = _queryTranslator.translate(
				nestedSort.getFilterQuery());

			nestedSortBuilder.setFilter(queryBuilder);
		}

		if (nestedSort.getNestedSort() != null) {
			NestedSort childNestedSort = nestedSort.getNestedSort();

			nestedSortBuilder.setNestedSort(translate(childNestedSort));
		}

		nestedSortBuilder.setMaxChildren(nestedSort.getMaxChildren());

		return nestedSortBuilder;
	}

	protected org.elasticsearch.search.sort.SortMode translate(
		SortMode sortMode) {

		if (sortMode == SortMode.AVG) {
			return org.elasticsearch.search.sort.SortMode.AVG;
		}
		else if (sortMode == SortMode.MAX) {
			return org.elasticsearch.search.sort.SortMode.MAX;
		}
		else if (sortMode == SortMode.MEDIAN) {
			return org.elasticsearch.search.sort.SortMode.MEDIAN;
		}
		else if (sortMode == SortMode.MIN) {
			return org.elasticsearch.search.sort.SortMode.MIN;
		}
		else if (sortMode == SortMode.SUM) {
			return org.elasticsearch.search.sort.SortMode.SUM;
		}

		throw new IllegalArgumentException("Invalid sort mode: " + sortMode);
	}

	protected org.elasticsearch.search.sort.SortOrder translate(
		SortOrder sortOrder) {

		if ((sortOrder == SortOrder.ASC) || (sortOrder == null)) {
			return org.elasticsearch.search.sort.SortOrder.ASC;
		}
		else if (sortOrder == SortOrder.DESC) {
			return org.elasticsearch.search.sort.SortOrder.DESC;
		}

		throw new IllegalArgumentException("Invalid sort order: " + sortOrder);
	}

	private final DistanceUnitTranslator _distanceUnitTranslator =
		new DistanceUnitTranslator();
	private final GeoDistanceTypeTranslator _geoDistanceTypeTranslator =
		new GeoDistanceTypeTranslator();
	private final QueryTranslator<QueryBuilder> _queryTranslator =
		new ElasticsearchQueryTranslator();
	private final ScriptTranslator _scriptTranslator = new ScriptTranslator();

}