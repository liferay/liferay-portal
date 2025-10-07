/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.sort;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.search.opensearch2.internal.geolocation.GeoTranslator;
import com.liferay.portal.search.opensearch2.internal.query.OpenSearchQueryTranslator;
import com.liferay.portal.search.opensearch2.internal.script.ScriptTranslator;
import com.liferay.portal.search.opensearch2.internal.util.ConversionUtil;
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

import org.opensearch.client.opensearch._types.NestedSortValue;
import org.opensearch.client.opensearch._types.ScriptSortType;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOptionsBuilders;
import org.opensearch.client.opensearch._types.mapping.FieldType;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.QueryVariant;

/**
 * @author Michael C. Han
 * @author Petteri Karttunen
 */
public class OpenSearchSortFieldTranslator
	implements SortFieldTranslator<SortOptions>, SortVisitor<SortOptions> {

	@Override
	public SortOptions translate(Sort sort) {
		return sort.accept(this);
	}

	@Override
	public SortOptions visit(FieldSort fieldSort) {
		org.opensearch.client.opensearch._types.FieldSort.Builder builder =
			SortOptionsBuilders.field();

		builder.field(fieldSort.getField());

		if (fieldSort.getMissing() != null) {
			builder.missing(
				ConversionUtil.toFieldValue(fieldSort.getMissing()));
		}

		if (fieldSort.getSortMode() != null) {
			builder.mode(translateSortMode(fieldSort.getSortMode()));
		}

		if (fieldSort.getNestedSort() != null) {
			builder.nested(translateNestedSort(fieldSort.getNestedSort()));
		}

		builder.order(translateSortOrder(fieldSort.getSortOrder()));
		builder.unmappedType(FieldType.Keyword);

		return SortOptions.of(
			sortOptions -> sortOptions.field(builder.build()));
	}

	@Override
	public SortOptions visit(GeoDistanceSort geoDistanceSort) {
		org.opensearch.client.opensearch._types.GeoDistanceSort.Builder
			builder = SortOptionsBuilders.geoDistance();

		if (geoDistanceSort.getGeoDistanceType() != null) {
			builder.distanceType(
				_geoTranslator.translateGeoDistanceType(
					geoDistanceSort.getGeoDistanceType()));
		}

		builder.field(geoDistanceSort.getField());
		builder.location(
			TransformUtil.transform(
				geoDistanceSort.getGeoLocationPoints(),
				_geoTranslator::translateGeoLocationPoint));

		if (geoDistanceSort.getSortMode() != null) {
			builder.mode(translateSortMode(geoDistanceSort.getSortMode()));
		}

		if (geoDistanceSort.getSortOrder() != null) {
			builder.order(translateSortOrder(geoDistanceSort.getSortOrder()));
		}

		if (geoDistanceSort.getDistanceUnit() != null) {
			builder.unit(
				_geoTranslator.translateDistanceUnit(
					geoDistanceSort.getDistanceUnit()));
		}

		return SortOptions.of(
			sortOptions -> sortOptions.geoDistance(builder.build()));
	}

	@Override
	public SortOptions visit(ScoreSort scoreSort) {
		org.opensearch.client.opensearch._types.ScoreSort.Builder builder =
			SortOptionsBuilders.score();

		if (scoreSort.getSortOrder() != null) {
			builder.order(translateSortOrder(scoreSort.getSortOrder()));
		}

		return SortOptions.of(
			sortOptions -> sortOptions.score(builder.build()));
	}

	@Override
	public SortOptions visit(ScriptSort scriptSort) {
		org.opensearch.client.opensearch._types.ScriptSort.Builder builder =
			SortOptionsBuilders.script();

		if (scriptSort.getSortMode() != null) {
			builder.mode(translateSortMode(scriptSort.getSortMode()));
		}

		if (scriptSort.getNestedSort() != null) {
			builder.nested(translateNestedSort(scriptSort.getNestedSort()));
		}

		builder.order(translateSortOrder(scriptSort.getSortOrder()));
		builder.script(_scriptTranslator.translate(scriptSort.getScript()));

		if (scriptSort.getScriptSortType() ==
				ScriptSort.ScriptSortType.NUMBER) {

			builder.type(ScriptSortType.Number);
		}
		else {
			builder.type(ScriptSortType.String);
		}

		return SortOptions.of(
			sortOptions -> sortOptions.script(builder.build()));
	}

	protected NestedSortValue translateNestedSort(NestedSort nestedSort) {
		NestedSortValue.Builder builder = new NestedSortValue.Builder();

		if (nestedSort.getFilterQuery() != null) {
			builder.filter(
				new Query(
					_queryTranslator.translate(nestedSort.getFilterQuery())));
		}

		builder.maxChildren(nestedSort.getMaxChildren());

		if (nestedSort.getNestedSort() != null) {
			builder.nested(translateNestedSort(nestedSort.getNestedSort()));
		}

		builder.path(nestedSort.getPath());

		return builder.build();
	}

	protected org.opensearch.client.opensearch._types.SortMode
		translateSortMode(SortMode sortMode) {

		if (sortMode == SortMode.AVG) {
			return org.opensearch.client.opensearch._types.SortMode.Avg;
		}
		else if (sortMode == SortMode.MAX) {
			return org.opensearch.client.opensearch._types.SortMode.Max;
		}
		else if (sortMode == SortMode.MEDIAN) {
			return org.opensearch.client.opensearch._types.SortMode.Median;
		}
		else if (sortMode == SortMode.MIN) {
			return org.opensearch.client.opensearch._types.SortMode.Min;
		}
		else if (sortMode == SortMode.SUM) {
			return org.opensearch.client.opensearch._types.SortMode.Sum;
		}

		throw new IllegalArgumentException("Invalid sort mode " + sortMode);
	}

	protected org.opensearch.client.opensearch._types.SortOrder
		translateSortOrder(SortOrder sortOrder) {

		if ((sortOrder == SortOrder.ASC) || (sortOrder == null)) {
			return org.opensearch.client.opensearch._types.SortOrder.Asc;
		}

		return org.opensearch.client.opensearch._types.SortOrder.Desc;
	}

	private final GeoTranslator _geoTranslator = new GeoTranslator();
	private final QueryTranslator<QueryVariant> _queryTranslator =
		new OpenSearchQueryTranslator();
	private final ScriptTranslator _scriptTranslator = new ScriptTranslator();

}