/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.filter;

import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.DateRangeTermFilter;
import com.liferay.portal.kernel.search.filter.ExistsFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.FilterTranslator;
import com.liferay.portal.kernel.search.filter.GeoBoundingBoxFilter;
import com.liferay.portal.kernel.search.filter.GeoDistanceFilter;
import com.liferay.portal.kernel.search.filter.GeoDistanceRangeFilter;
import com.liferay.portal.kernel.search.filter.GeoPolygonFilter;
import com.liferay.portal.kernel.search.filter.MissingFilter;
import com.liferay.portal.kernel.search.filter.PrefixFilter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.search.filter.RangeTermFilter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.search.geolocation.GeoDistance;
import com.liferay.portal.kernel.search.geolocation.GeoLocationPoint;
import com.liferay.portal.kernel.search.query.QueryTranslator;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch7.internal.legacy.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.elasticsearch7.internal.util.QueryUtil;
import com.liferay.portal.search.filter.DateRangeFilter;
import com.liferay.portal.search.filter.FilterVisitor;
import com.liferay.portal.search.filter.RangeFilter;
import com.liferay.portal.search.filter.TermsSetFilter;
import com.liferay.portal.search.index.IndexNameBuilder;

import java.text.Format;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.GeoBoundingBoxQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermsSetQueryBuilder;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Marco Leo
 */
@Component(
	property = "search.engine.impl=Elasticsearch",
	service = FilterTranslator.class
)
public class ElasticsearchFilterTranslator
	implements FilterTranslator<QueryBuilder>, FilterVisitor<QueryBuilder> {

	@Override
	public QueryBuilder translate(Filter filter, SearchContext searchContext) {
		return filter.accept(this);
	}

	@Override
	public QueryBuilder visit(BooleanFilter booleanFilter) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		for (BooleanClause<Filter> booleanClause :
				booleanFilter.getMustBooleanClauses()) {

			QueryBuilder queryBuilder = translate(booleanClause);

			boolQueryBuilder.must(queryBuilder);
		}

		for (BooleanClause<Filter> booleanClause :
				booleanFilter.getMustNotBooleanClauses()) {

			QueryBuilder queryBuilder = translate(booleanClause);

			boolQueryBuilder.mustNot(queryBuilder);
		}

		for (BooleanClause<Filter> booleanClause :
				booleanFilter.getShouldBooleanClauses()) {

			QueryBuilder queryBuilder = translate(booleanClause);

			boolQueryBuilder.should(queryBuilder);
		}

		return boolQueryBuilder;
	}

	@Override
	public QueryBuilder visit(DateRangeFilter dateRangeFilter) {
		RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(
			dateRangeFilter.getFieldName());

		if (dateRangeFilter.getFormat() != null) {
			rangeQueryBuilder.format(dateRangeFilter.getFormat());
		}

		rangeQueryBuilder.from(dateRangeFilter.getFrom());
		rangeQueryBuilder.includeLower(dateRangeFilter.isIncludeLower());
		rangeQueryBuilder.includeUpper(dateRangeFilter.isIncludeUpper());

		if (dateRangeFilter.getTimeZoneId() != null) {
			rangeQueryBuilder.timeZone(dateRangeFilter.getTimeZoneId());
		}

		rangeQueryBuilder.to(dateRangeFilter.getTo());

		return rangeQueryBuilder;
	}

	@Override
	public QueryBuilder visit(DateRangeTermFilter dateRangeTermFilter) {
		RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(
			dateRangeTermFilter.getField());

		Format format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			dateRangeTermFilter.getDateFormat(),
			dateRangeTermFilter.getTimeZone());

		try {
			rangeQueryBuilder.from(
				format.parseObject(dateRangeTermFilter.getLowerBound()));
			rangeQueryBuilder.includeLower(
				dateRangeTermFilter.isIncludesLower());
			rangeQueryBuilder.includeUpper(
				dateRangeTermFilter.isIncludesUpper());
			rangeQueryBuilder.to(
				format.parseObject(dateRangeTermFilter.getUpperBound()));
		}
		catch (ParseException parseException) {
			throw new IllegalArgumentException(
				"Invalid date range " + dateRangeTermFilter, parseException);
		}

		return rangeQueryBuilder;
	}

	@Override
	public QueryBuilder visit(ExistsFilter existsFilter) {
		return QueryBuilders.existsQuery(existsFilter.getField());
	}

	@Override
	public QueryBuilder visit(GeoBoundingBoxFilter geoBoundingBoxFilter) {
		GeoBoundingBoxQueryBuilder geoBoundingBoxQueryBuilder =
			QueryBuilders.geoBoundingBoxQuery(geoBoundingBoxFilter.getField());

		GeoLocationPoint bottomRightGeoLocationPoint =
			geoBoundingBoxFilter.getBottomRightGeoLocationPoint();

		GeoPoint bottomRightGeoPoint = new GeoPoint(
			bottomRightGeoLocationPoint.getLatitude(),
			bottomRightGeoLocationPoint.getLongitude());

		GeoLocationPoint topLeftGeoLocationPoint =
			geoBoundingBoxFilter.getTopLeftGeoLocationPoint();

		GeoPoint topLeftGeoPoint = new GeoPoint(
			topLeftGeoLocationPoint.getLatitude(),
			topLeftGeoLocationPoint.getLongitude());

		geoBoundingBoxQueryBuilder.setCorners(
			topLeftGeoPoint, bottomRightGeoPoint);

		return geoBoundingBoxQueryBuilder;
	}

	@Override
	public QueryBuilder visit(GeoDistanceFilter geoDistanceFilter) {
		GeoDistanceQueryBuilder geoDistanceQueryBuilder =
			QueryBuilders.geoDistanceQuery(geoDistanceFilter.getField());

		geoDistanceQueryBuilder.distance(
			String.valueOf(geoDistanceFilter.getGeoDistance()));

		GeoLocationPoint pinGeoLocationPoint =
			geoDistanceFilter.getPinGeoLocationPoint();

		geoDistanceQueryBuilder.point(
			pinGeoLocationPoint.getLatitude(),
			pinGeoLocationPoint.getLongitude());

		return geoDistanceQueryBuilder;
	}

	@Override
	public QueryBuilder visit(GeoDistanceRangeFilter geoDistanceRangeFilter) {
		GeoDistanceQueryBuilder geoDistanceQueryBuilder =
			new GeoDistanceQueryBuilder(geoDistanceRangeFilter.getField());

		GeoDistance geoDistance =
			geoDistanceRangeFilter.getUpperBoundGeoDistance();

		geoDistanceQueryBuilder.distance(
			String.valueOf(geoDistance.getDistance()),
			DistanceUnit.fromString(
				String.valueOf(geoDistance.getDistanceUnit())));

		GeoLocationPoint geoLocationPoint =
			geoDistanceRangeFilter.getPinGeoLocationPoint();

		geoDistanceQueryBuilder.point(
			new GeoPoint(
				geoLocationPoint.getLatitude(),
				geoLocationPoint.getLongitude()));

		return geoDistanceQueryBuilder;
	}

	@Override
	public QueryBuilder visit(GeoPolygonFilter geoPolygonFilter) {
		List<GeoPoint> geoPoints = new ArrayList<>();

		for (GeoLocationPoint geoLocationPoint :
				geoPolygonFilter.getGeoLocationPoints()) {

			geoPoints.add(
				new GeoPoint(
					geoLocationPoint.getLatitude(),
					geoLocationPoint.getLongitude()));
		}

		return QueryBuilders.geoPolygonQuery(
			geoPolygonFilter.getField(), geoPoints);
	}

	@Override
	public QueryBuilder visit(MissingFilter missingFilter) {
		BoolQueryBuilder missingQueryBuilder = new BoolQueryBuilder(
		).mustNot(
			new ExistsQueryBuilder(missingFilter.getField())
		);

		if (missingFilter.isExists() != null) {
			missingFilter.setExists(missingFilter.isExists());
		}

		if (missingFilter.isNullValue() != null) {
			missingFilter.setNullValue(missingFilter.isNullValue());
		}

		return missingQueryBuilder;
	}

	@Override
	public QueryBuilder visit(PrefixFilter prefixFilter) {
		return QueryBuilders.prefixQuery(
			prefixFilter.getField(), prefixFilter.getPrefix());
	}

	@Override
	public QueryBuilder visit(QueryFilter queryFilter) {
		return _queryTranslator.translate(queryFilter.getQuery(), null);
	}

	@Override
	public QueryBuilder visit(RangeFilter rangeFilter) {
		RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(
			rangeFilter.getFieldName());

		if (rangeFilter.getFormat() != null) {
			rangeQueryBuilder.format(rangeFilter.getFormat());
		}

		rangeQueryBuilder.from(rangeFilter.getFrom());
		rangeQueryBuilder.includeLower(rangeFilter.isIncludeLower());
		rangeQueryBuilder.includeUpper(rangeFilter.isIncludeUpper());

		if (rangeFilter.getTimeZoneId() != null) {
			rangeQueryBuilder.timeZone(rangeFilter.getTimeZoneId());
		}

		rangeQueryBuilder.to(rangeFilter.getTo());

		return rangeQueryBuilder;
	}

	@Override
	public QueryBuilder visit(RangeTermFilter rangeTermFilter) {
		RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(
			rangeTermFilter.getField());

		rangeQueryBuilder.from(rangeTermFilter.getLowerBound());
		rangeQueryBuilder.includeLower(rangeTermFilter.isIncludesLower());
		rangeQueryBuilder.includeUpper(rangeTermFilter.isIncludesUpper());
		rangeQueryBuilder.to(rangeTermFilter.getUpperBound());

		return rangeQueryBuilder;
	}

	@Override
	public QueryBuilder visit(TermFilter termFilter) {
		return QueryBuilders.termQuery(
			termFilter.getField(), termFilter.getValue());
	}

	@Override
	public QueryBuilder visit(TermsFilter termsFilter) {
		return QueryUtil.translateTerms(
			termsFilter.getField(), termsFilter.getValues());
	}

	@Override
	public QueryBuilder visit(TermsSetFilter termsSetFilter) {
		TermsSetQueryBuilder termsSetQueryBuilder = new TermsSetQueryBuilder(
			termsSetFilter.getFieldName(),
			ListUtil.toList(termsSetFilter.getValues()));

		if (!Validator.isBlank(termsSetFilter.getMinimumShouldMatchField())) {
			termsSetQueryBuilder.setMinimumShouldMatchField(
				termsSetFilter.getMinimumShouldMatchField());
		}

		return termsSetQueryBuilder;
	}

	@Activate
	protected void activate() {
		_queryTranslator = new ElasticsearchQueryTranslator(indexNameBuilder);
	}

	protected QueryBuilder translate(BooleanClause<Filter> booleanClause) {
		Filter filter = booleanClause.getClause();

		return filter.accept(this);
	}

	@Reference
	protected IndexNameBuilder indexNameBuilder;

	private QueryTranslator<QueryBuilder> _queryTranslator;

}