/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.DateHistogramAggregation;
import com.liferay.portal.search.aggregation.bucket.DateHistogramAggregationResult;
import com.liferay.portal.search.aggregation.bucket.DateRangeAggregation;
import com.liferay.portal.search.aggregation.bucket.RangeAggregationResult;
import com.liferay.portal.search.engine.adapter.search.SearchRequestExecutor;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Histogram;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.HistogramMetric;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.HistogramMetricResource;
import com.liferay.portal.workflow.metrics.search.index.constants.WorkflowMetricsIndexNameConstants;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rafael Praxedes
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/histogram-metric.properties",
	scope = ServiceScope.PROTOTYPE, service = HistogramMetricResource.class
)
public class HistogramMetricResourceImpl
	extends BaseHistogramMetricResourceImpl {

	@Override
	public HistogramMetric getProcessHistogramMetric(
			Long processId, Date dateEnd, Date dateStart, String unit)
		throws Exception {

		HistogramMetric histogramMetric = new HistogramMetric();

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		LocalDateTime endLocalDateTime = LocalDateTime.ofInstant(
			dateEnd.toInstant(), ZoneId.of(contextUser.getTimeZoneId()));
		LocalDateTime startLocalDateTime = LocalDateTime.ofInstant(
			dateStart.toInstant(), ZoneId.of(contextUser.getTimeZoneId()));

		DateRangeAggregation dateRangeAggregation = _createDateRangeAggregation(
			endLocalDateTime, startLocalDateTime);

		DateHistogramAggregation dateHistogramAggregation =
			_aggregations.dateHistogram("completionDate", "completionDate");

		dateHistogramAggregation.setDateHistogramInterval(
			_getDateHistogramInterval(unit));

		if (Objects.equals(unit, HistogramMetric.Unit.WEEKS.getValue())) {
			dateHistogramAggregation.setOffset(-86400000L);
		}

		dateRangeAggregation.addChildAggregation(dateHistogramAggregation);

		searchSearchRequest.addAggregation(dateRangeAggregation);

		searchSearchRequest.setIndexNames(
			_indexNameBuilder.getIndexName(contextCompany.getCompanyId()) +
				WorkflowMetricsIndexNameConstants.SUFFIX_INSTANCE);

		BooleanQuery booleanQuery = _queries.booleanQuery();

		searchSearchRequest.setQuery(
			booleanQuery.addMustQueryClauses(
				_queries.term("companyId", contextCompany.getCompanyId()),
				_queries.term("completed", Boolean.TRUE.toString()),
				_queries.term("deleted", Boolean.FALSE.toString()),
				_queries.term("processId", processId)));

		SearchSearchResponse searchSearchResponse =
			_searchRequestExecutor.executeSearchRequest(searchSearchRequest);

		Map<String, AggregationResult> aggregationResultsMap =
			searchSearchResponse.getAggregationResultsMap();

		RangeAggregationResult rangeAggregationResult =
			(RangeAggregationResult)aggregationResultsMap.get("completionDate");

		Bucket bucket = rangeAggregationResult.getBucket("current");

		Map<String, AggregationResult> childrenAggregationResults =
			bucket.getChildrenAggregationResults();

		DateHistogramAggregationResult dateHistogramAggregationResult =
			(DateHistogramAggregationResult)childrenAggregationResults.get(
				"completionDate");

		Collection<Histogram> histograms = _createHistograms(
			dateHistogramAggregationResult.getBuckets(), endLocalDateTime,
			startLocalDateTime, unit);

		histogramMetric.setHistograms(
			() -> histograms.toArray(new Histogram[0]));
		histogramMetric.setValue(
			() -> _getMetricValue(
				bucket, histograms,
				TimeUnit.DAYS.convert(
					dateEnd.getTime() - dateStart.getTime(),
					TimeUnit.MILLISECONDS),
				unit));

		return histogramMetric;
	}

	private DateRangeAggregation _createDateRangeAggregation(
		LocalDateTime endLocalDateTime, LocalDateTime startLocalDateTime) {

		DateRangeAggregation dateRangeAggregation = _aggregations.dateRange(
			"completionDate", "completionDate");

		dateRangeAggregation.addRange(
			"current", _dateTimeFormatter.format(startLocalDateTime),
			_dateTimeFormatter.format(endLocalDateTime));

		return dateRangeAggregation;
	}

	private Histogram _createHistogram(LocalDateTime localDateTime) {
		return new Histogram() {
			{
				setKey(localDateTime::toString);
				setValue(() -> 0.0);
			}
		};
	}

	private Collection<Histogram> _createHistograms(
		Collection<Bucket> buckets, LocalDateTime endLocalDateTime,
		LocalDateTime startLocalDateTime, String unit) {

		Map<String, Histogram> histograms = _createHistograms(
			endLocalDateTime, startLocalDateTime, unit);

		for (Bucket bucket : buckets) {
			LocalDateTime localDateTime = LocalDateTime.parse(
				bucket.getKey(), _dateTimeFormatter);

			if (Objects.equals(unit, HistogramMetric.Unit.MONTHS.getValue()) ||
				Objects.equals(unit, HistogramMetric.Unit.WEEKS.getValue()) ||
				Objects.equals(unit, HistogramMetric.Unit.YEARS.getValue())) {

				localDateTime = _getHistogramLocalDateTime(
					localDateTime, startLocalDateTime);
			}

			Histogram histogram = histograms.get(localDateTime.toString());

			histogram.setKey(localDateTime::toString);
			histogram.setValue(() -> (double)bucket.getDocCount());

			histograms.put(localDateTime.toString(), histogram);
		}

		return histograms.values();
	}

	private Map<String, Histogram> _createHistograms(
		LocalDateTime endLocalDateTime, LocalDateTime startLocalDateTime,
		String unit) {

		Map<String, Histogram> histograms = new LinkedHashMap<>();

		if (Objects.equals(unit, HistogramMetric.Unit.HOURS.getValue())) {
			startLocalDateTime = startLocalDateTime.withMinute(
				LocalTime.MIN.getMinute());
			startLocalDateTime = startLocalDateTime.withNano(
				LocalTime.MIN.getNano());
			startLocalDateTime = startLocalDateTime.withSecond(
				LocalTime.MIN.getSecond());
		}
		else {
			startLocalDateTime = startLocalDateTime.with(LocalTime.MIDNIGHT);
		}

		while (endLocalDateTime.isAfter(startLocalDateTime) ||
			   endLocalDateTime.equals(startLocalDateTime)) {

			histograms.put(
				String.valueOf(startLocalDateTime),
				_createHistogram(startLocalDateTime));

			startLocalDateTime = startLocalDateTime.plus(
				1, ChronoUnit.valueOf(StringUtil.toUpperCase(unit)));

			if (Objects.equals(unit, HistogramMetric.Unit.MONTHS.getValue()) &&
				(startLocalDateTime.getDayOfMonth() != 1)) {

				startLocalDateTime = startLocalDateTime.withDayOfMonth(1);
			}
			else if (Objects.equals(
						unit, HistogramMetric.Unit.WEEKS.getValue()) &&
					 (startLocalDateTime.getDayOfWeek() != DayOfWeek.SUNDAY)) {

				startLocalDateTime = startLocalDateTime.minusWeeks(1);
				startLocalDateTime = startLocalDateTime.with(DayOfWeek.SUNDAY);
			}
			else if (Objects.equals(
						unit, HistogramMetric.Unit.YEARS.getValue()) &&
					 (startLocalDateTime.getDayOfYear() != 1)) {

				startLocalDateTime = startLocalDateTime.withDayOfYear(1);
			}
		}

		return histograms;
	}

	private String _getDateHistogramInterval(String unit) {
		if (Objects.equals(unit, HistogramMetric.Unit.DAYS.getValue())) {
			return "1d";
		}
		else if (Objects.equals(unit, HistogramMetric.Unit.HOURS.getValue())) {
			return "1h";
		}
		else if (Objects.equals(unit, HistogramMetric.Unit.MONTHS.getValue())) {
			return "1M";
		}
		else if (Objects.equals(unit, HistogramMetric.Unit.WEEKS.getValue())) {
			return "1w";
		}

		return "1y";
	}

	private LocalDateTime _getHistogramLocalDateTime(
		LocalDateTime localDateTime, LocalDateTime startLocalDateTime) {

		if (startLocalDateTime.isAfter(localDateTime)) {
			return startLocalDateTime;
		}

		return localDateTime;
	}

	private double _getMetricValue(
		Bucket bucket, Collection<Histogram> histograms, long timeRange,
		String unit) {

		double timeAmount = histograms.size();

		if (timeRange == 0) {
			timeRange = 1;
		}

		if (Objects.equals(unit, HistogramMetric.Unit.MONTHS.getValue())) {
			timeAmount = timeRange / 30.0;
		}
		else if (Objects.equals(unit, HistogramMetric.Unit.WEEKS.getValue())) {
			timeAmount = timeRange / 7.0;
		}
		else if (Objects.equals(unit, HistogramMetric.Unit.YEARS.getValue())) {
			timeAmount = timeRange / 365.0;
		}

		return bucket.getDocCount() / timeAmount;
	}

	private static final DateTimeFormatter _dateTimeFormatter =
		DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	@Reference
	private Aggregations _aggregations;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private Queries _queries;

	@Reference
	private SearchRequestExecutor _searchRequestExecutor;

}