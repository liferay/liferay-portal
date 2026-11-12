/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.web.internal.data.provider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;

import com.liferay.analytics.reports.web.internal.client.AsahFaroBackendClient;
import com.liferay.analytics.reports.web.internal.model.AcquisitionChannel;
import com.liferay.analytics.reports.web.internal.model.HistoricalMetric;
import com.liferay.analytics.reports.web.internal.model.PageExperience;
import com.liferay.analytics.reports.web.internal.model.ReferringSocialMedia;
import com.liferay.analytics.reports.web.internal.model.ReferringURL;
import com.liferay.analytics.reports.web.internal.model.TimeRange;
import com.liferay.analytics.reports.web.internal.model.TimeSpan;
import com.liferay.analytics.reports.web.internal.model.TrafficChannel;
import com.liferay.analytics.reports.web.internal.model.TrafficSource;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Validator;

import java.time.format.DateTimeFormatter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author David Arques
 */
public class AnalyticsReportsDataProvider {

	public AnalyticsReportsDataProvider(
		AnalyticsSettingsManager analyticsSettingsManager, Http http) {

		if (http == null) {
			throw new IllegalArgumentException("Http is null");
		}

		_asahFaroBackendClient = new AsahFaroBackendClient(
			analyticsSettingsManager, http);
	}

	public Map<String, AcquisitionChannel> getAcquisitionChannels(
			long companyId, String experienceId, TimeRange timeRange,
			String url)
		throws PortalException {

		try {
			Map<String, AcquisitionChannel> acquisitionChannels =
				new HashMap<>();

			String response = _asahFaroBackendClient.doGet(
				companyId,
				_getPagesEndpoint(
					experienceId, "acquisition-channels", timeRange, url));

			TypeFactory typeFactory = _objectMapper.getTypeFactory();

			Map<String, Long> acquisitionChannelValues =
				_objectMapper.readValue(
					response,
					typeFactory.constructMapType(
						Map.class, typeFactory.constructType(String.class),
						typeFactory.constructType(Long.class)));

			Double total = 0.0;

			Collection<Long> values = acquisitionChannelValues.values();

			for (Long value : values) {
				total += value;
			}

			for (Map.Entry<String, Long> entry :
					acquisitionChannelValues.entrySet()) {

				acquisitionChannels.put(
					entry.getKey(),
					new AcquisitionChannel(
						entry.getKey(), entry.getValue(),
						(entry.getValue() / total) * 100));
			}

			return acquisitionChannels;
		}
		catch (Exception exception) {
			throw new PortalException(
				"Unable to get acquisition channels", exception);
		}
	}

	public List<ReferringURL> getDomainReferringURLs(
			long companyId, String experienceId, TimeRange timeRange,
			String url)
		throws PortalException {

		try {
			String response = _asahFaroBackendClient.doGet(
				companyId,
				_getPagesEndpoint(
					experienceId, "page-referrer-hosts", timeRange, url));

			TypeFactory typeFactory = _objectMapper.getTypeFactory();

			Map<String, Long> pageReferrerHosts = _objectMapper.readValue(
				response,
				typeFactory.constructMapType(
					Map.class, typeFactory.constructType(String.class),
					typeFactory.constructType(Long.class)));

			return TransformUtil.transform(
				pageReferrerHosts.entrySet(),
				entry -> new ReferringURL(
					Math.toIntExact(entry.getValue()), entry.getKey()));
		}
		catch (Exception exception) {
			throw new PortalException(
				"Unable to get referring domains", exception);
		}
	}

	public HistoricalMetric getHistoricalReadsHistoricalMetric(
			long companyId, String experienceId, TimeRange timeRange,
			String url)
		throws PortalException {

		try {
			String response = _asahFaroBackendClient.doGet(
				companyId,
				_getPagesEndpoint(experienceId, "read-counts", timeRange, url));

			return _objectMapper.readValue(response, HistoricalMetric.class);
		}
		catch (Exception exception) {
			throw new PortalException(
				"Unable to get historical views", exception);
		}
	}

	public HistoricalMetric getHistoricalViewsHistoricalMetric(
			long companyId, String experienceId, TimeRange timeRange,
			String url)
		throws PortalException {

		try {
			String response = _asahFaroBackendClient.doGet(
				companyId,
				_getPagesEndpoint(experienceId, "view-counts", timeRange, url));

			return _objectMapper.readValue(response, HistoricalMetric.class);
		}
		catch (Exception exception) {
			throw new PortalException(
				"Unable to get historical views", exception);
		}
	}

	public List<PageExperience> getPageExperiences(long companyId, String url)
		throws PortalException {

		try {
			String response = _asahFaroBackendClient.doGet(
				companyId,
				"api/1.0/pages/page-experiences?canonicalUrl=" +
					HtmlUtil.escapeURL(url));

			TypeFactory typeFactory = _objectMapper.getTypeFactory();

			return _objectMapper.readValue(
				response,
				typeFactory.constructCollectionType(
					List.class, PageExperience.class));
		}
		catch (Exception exception) {
			throw new PortalException(
				"Unable to get page experiences", exception);
		}
	}

	public List<ReferringURL> getPageReferringURLs(
			long companyId, String experienceId, TimeRange timeRange,
			String url)
		throws PortalException {

		try {
			String response = _asahFaroBackendClient.doGet(
				companyId,
				_getPagesEndpoint(
					experienceId, "page-referrers", timeRange, url));

			TypeFactory typeFactory = _objectMapper.getTypeFactory();

			Map<String, Long> pageReferrers = _objectMapper.readValue(
				response,
				typeFactory.constructMapType(
					Map.class, typeFactory.constructType(String.class),
					typeFactory.constructType(Long.class)));

			return TransformUtil.transform(
				pageReferrers.entrySet(),
				entry -> new ReferringURL(
					Math.toIntExact(entry.getValue()), entry.getKey()));
		}
		catch (Exception exception) {
			throw new PortalException(
				"Unable to get referring pages", exception);
		}
	}

	public List<ReferringSocialMedia> getReferringSocialMediaList(
			long companyId, String experienceId, TimeRange timeRange,
			String url)
		throws PortalException {

		try {
			String response = _asahFaroBackendClient.doGet(
				companyId,
				_getPagesEndpoint(
					experienceId, "social-page-referrers", timeRange, url));

			TypeFactory typeFactory = _objectMapper.getTypeFactory();

			Map<String, Long> socialPageReferrers = _objectMapper.readValue(
				response,
				typeFactory.constructMapType(
					Map.class, typeFactory.constructType(String.class),
					typeFactory.constructType(Long.class)));

			return TransformUtil.transform(
				socialPageReferrers.entrySet(),
				entry -> new ReferringSocialMedia(
					entry.getKey(), Math.toIntExact(entry.getValue())));
		}
		catch (Exception exception) {
			throw new PortalException(
				"Unable to get referring social media", exception);
		}
	}

	public Long getTotalReads(long companyId, String experienceId, String url)
		throws PortalException {

		try {
			long totalReads = GetterUtil.getLong(
				_asahFaroBackendClient.doGet(
					companyId,
					_getPagesEndpoint(experienceId, "read-count", null, url)));

			return Math.max(
				0, totalReads - _getTodayReads(companyId, experienceId, url));
		}
		catch (Exception exception) {
			throw new PortalException("Unable to get total reads", exception);
		}
	}

	public Long getTotalViews(long companyId, String experienceId, String url)
		throws PortalException {

		try {
			long totalViews = GetterUtil.getLong(
				_asahFaroBackendClient.doGet(
					companyId,
					_getPagesEndpoint(experienceId, "view-count", null, url)));

			return Math.max(
				0, totalViews - _getTodayViews(companyId, experienceId, url));
		}
		catch (Exception exception) {
			throw new PortalException("Unable to get total views", exception);
		}
	}

	public Map<TrafficChannel.Type, TrafficChannel> getTrafficChannels(
			long companyId, String experienceId, TimeRange timeRange,
			String url)
		throws PortalException {

		try {
			Map<TrafficChannel.Type, TrafficChannel> trafficChannels =
				new HashMap<>();

			Map<String, AcquisitionChannel> acquisitionChannels =
				getAcquisitionChannels(companyId, experienceId, timeRange, url);

			for (AcquisitionChannel acquisitionChannel :
					acquisitionChannels.values()) {

				try {
					TrafficChannel trafficChannel = TrafficChannel.newInstance(
						acquisitionChannel);

					trafficChannels.put(
						trafficChannel.getType(), trafficChannel);
				}
				catch (IllegalArgumentException illegalArgumentException) {
					if (_log.isDebugEnabled()) {
						_log.debug(illegalArgumentException);
					}
				}
			}

			return trafficChannels;
		}
		catch (Exception exception) {
			throw new PortalException(
				"Unable to get acquisition channels", exception);
		}
	}

	public Map<String, TrafficSource> getTrafficSources(
		long companyId, String url) {

		try {
			Map<String, TrafficSource> trafficSources = new HashMap<>();

			String response = _asahFaroBackendClient.doGet(
				companyId, "api/seo/1.0/traffic-sources?url=" + url);

			TypeFactory typeFactory = _objectMapper.getTypeFactory();

			for (TrafficSource trafficSource :
					(List<TrafficSource>)_objectMapper.readValue(
						response,
						typeFactory.constructCollectionType(
							List.class, TrafficSource.class))) {

				trafficSources.put(trafficSource.getName(), trafficSource);
			}

			return trafficSources;
		}
		catch (Exception exception) {
			_log.error("Unable to get traffic sources", exception);

			return Collections.emptyMap();
		}
	}

	public boolean isValidAnalyticsConnection(long companyId) throws Exception {
		return _asahFaroBackendClient.isValidConnection(companyId);
	}

	private String _getPagesEndpoint(
		String experienceId, String path, TimeRange timeRange, String url) {

		String endpoint = String.format(
			"api/1.0/pages/%s?canonicalURL=%s", path, HtmlUtil.escapeURL(url));

		if (timeRange != null) {
			endpoint += String.format(
				"&endDate=%s",
				DateTimeFormatter.ISO_DATE.format(timeRange.getEndLocalDate()));
		}

		if (Validator.isNotNull(experienceId)) {
			endpoint += "&experienceId=" + experienceId;
		}

		if (timeRange != null) {
			endpoint += String.format(
				"&interval=D&startDate=%s",
				DateTimeFormatter.ISO_DATE.format(
					timeRange.getStartLocalDate()));
		}

		return endpoint;
	}

	private long _getTodayReads(long companyId, String experienceId, String url)
		throws PortalException {

		HistoricalMetric historicalMetric = getHistoricalReadsHistoricalMetric(
			companyId, experienceId, TimeRange.of(TimeSpan.TODAY, 0), url);

		Double value = historicalMetric.getValue();

		return value.longValue();
	}

	private long _getTodayViews(long companyId, String experienceId, String url)
		throws PortalException {

		HistoricalMetric historicalMetric = getHistoricalViewsHistoricalMetric(
			companyId, experienceId, TimeRange.of(TimeSpan.TODAY, 0), url);

		Double value = historicalMetric.getValue();

		return value.longValue();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsReportsDataProvider.class);

	private static final ObjectMapper _objectMapper = new ObjectMapper() {
		{
			enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
			enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
			configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		}
	};

	private final AsahFaroBackendClient _asahFaroBackendClient;

}