/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.web.internal.data.provider;

import com.liferay.analytics.reports.web.internal.model.AcquisitionChannel;
import com.liferay.analytics.reports.web.internal.model.HistogramMetric;
import com.liferay.analytics.reports.web.internal.model.HistoricalMetric;
import com.liferay.analytics.reports.web.internal.model.PageExperience;
import com.liferay.analytics.reports.web.internal.model.ReferringSocialMedia;
import com.liferay.analytics.reports.web.internal.model.ReferringURL;
import com.liferay.analytics.reports.web.internal.model.TimeRange;
import com.liferay.analytics.reports.web.internal.model.TimeSpan;
import com.liferay.analytics.reports.web.internal.model.TrafficChannel;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author David Arques
 */
public class AnalyticsReportsDataProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetAcquisitionChannels() throws Exception {
		AnalyticsReportsDataProvider analyticsReportsDataProvider =
			new AnalyticsReportsDataProvider(
				_getAnalyticsSettingsManager(),
				_getHttp(
					Collections.singletonMap(
						"/acquisition-channels",
						JSONUtil.put(
							"direct", 5847L
						).put(
							"organic", 1732L
						).put(
							"paid", 1235L
						).put(
							"referrer", 3849L
						).put(
							"social", 735L
						).toString())));

		TimeSpan timeSpan = TimeSpan.of("last-7-days");

		TimeRange timeRange = timeSpan.toTimeRange(0);

		Map<String, AcquisitionChannel> acquisitionChannels =
			analyticsReportsDataProvider.getAcquisitionChannels(
				RandomTestUtil.randomLong(), null, timeRange,
				RandomTestUtil.randomString());

		Assert.assertEquals(
			acquisitionChannels.toString(), 5, acquisitionChannels.size());
		Assert.assertEquals(
			String.valueOf(new AcquisitionChannel("direct", 5847L, 43.64D)),
			String.valueOf(acquisitionChannels.get("direct")));
		Assert.assertEquals(
			String.valueOf(new AcquisitionChannel("organic", 1732L, 12.93D)),
			String.valueOf(acquisitionChannels.get("organic")));
		Assert.assertEquals(
			String.valueOf(new AcquisitionChannel("paid", 1235L, 9.22)),
			String.valueOf(acquisitionChannels.get("paid")));
		Assert.assertEquals(
			String.valueOf(new AcquisitionChannel("referrer", 3849L, 28.73D)),
			String.valueOf(acquisitionChannels.get("referrer")));
		Assert.assertEquals(
			String.valueOf(new AcquisitionChannel("social", 735L, 5.49D)),
			String.valueOf(acquisitionChannels.get("social")));
	}

	@Test
	public void testGetDomainReferringURLs() throws Exception {
		AnalyticsReportsDataProvider analyticsReportsDataProvider =
			new AnalyticsReportsDataProvider(
				_getAnalyticsSettingsManager(),
				_getHttp(
					Collections.singletonMap(
						"/page-referrer-hosts",
						JSONUtil.put(
							"abc.com", 3.0
						).put(
							"google.com", 6.0
						).put(
							"liferay.com", 1.0
						).toString())));

		TimeSpan timeSpan = TimeSpan.of("last-7-days");

		TimeRange timeRange = timeSpan.toTimeRange(0);

		List<ReferringURL> referringURLS =
			analyticsReportsDataProvider.getDomainReferringURLs(
				RandomTestUtil.randomLong(), null, timeRange,
				RandomTestUtil.randomString());

		Assert.assertEquals(referringURLS.toString(), 3, referringURLS.size());
		Assert.assertEquals(
			String.valueOf(new ReferringURL(1, "liferay.com")),
			String.valueOf(referringURLS.get(0)));
		Assert.assertEquals(
			String.valueOf(new ReferringURL(6, "google.com")),
			String.valueOf(referringURLS.get(1)));
		Assert.assertEquals(
			String.valueOf(new ReferringURL(3, "abc.com")),
			String.valueOf(referringURLS.get(2)));
	}

	@Test
	public void testGetHistoricalReadsHistoricalMetric() throws Exception {
		LocalDate localDate = LocalDate.now();

		AnalyticsReportsDataProvider analyticsReportsDataProvider =
			new AnalyticsReportsDataProvider(
				_getAnalyticsSettingsManager(),
				_getHttp(
					Collections.singletonMap(
						"/read-counts",
						JSONUtil.put(
							"histogram",
							JSONUtil.put(
								JSONUtil.put(
									"key",
									localDate.format(
										DateTimeFormatter.ISO_LOCAL_DATE)
								).put(
									"value", 5
								))
						).put(
							"value", 5
						).toString())));

		HistoricalMetric historicalMetric =
			analyticsReportsDataProvider.getHistoricalReadsHistoricalMetric(
				RandomTestUtil.randomLong(), null,
				TimeRange.of(TimeSpan.LAST_7_DAYS, 0),
				RandomTestUtil.randomString());

		Assert.assertEquals(5.0, historicalMetric.getValue(), 0.0);

		List<HistogramMetric> histogramMetrics =
			historicalMetric.getHistogramMetrics();

		Assert.assertEquals(
			histogramMetrics.toString(), 1, histogramMetrics.size());

		HistogramMetric histogramMetric = histogramMetrics.get(0);

		Assert.assertEquals(5.0, histogramMetric.getValue(), 0.0);

		Date date = histogramMetric.getKey();

		Instant instant = date.toInstant();

		ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

		Assert.assertEquals(localDate, zonedDateTime.toLocalDate());
	}

	@Test
	public void testGetPageExperiences() throws Exception {
		String json = JSONUtil.putAll(
			JSONUtil.put(
				"id", "exp1"
			).put(
				"name", "Experience 1"
			)
		).put(
			JSONUtil.put(
				"id", "exp2"
			).put(
				"name", "Experience 2"
			)
		).toString();

		AnalyticsReportsDataProvider analyticsReportsDataProvider =
			new AnalyticsReportsDataProvider(
				_getAnalyticsSettingsManager(),
				_getHttp(Collections.singletonMap("/page-experiences", json)));

		List<PageExperience> pageExperiences =
			analyticsReportsDataProvider.getPageExperiences(
				RandomTestUtil.randomLong(), RandomTestUtil.randomString());

		Assert.assertEquals(
			pageExperiences.toString(), 2, pageExperiences.size());

		PageExperience pageExperience1 = pageExperiences.get(0);

		Assert.assertEquals("exp1", pageExperience1.getId());
		Assert.assertEquals("Experience 1", pageExperience1.getName());

		PageExperience pageExperience2 = pageExperiences.get(1);

		Assert.assertEquals("exp2", pageExperience2.getId());
		Assert.assertEquals("Experience 2", pageExperience2.getName());
	}

	@Test
	public void testGetPageReferringURLs() throws Exception {
		AnalyticsReportsDataProvider analyticsReportsDataProvider =
			new AnalyticsReportsDataProvider(
				_getAnalyticsSettingsManager(),
				_getHttp(
					Collections.singletonMap(
						"/page-referrers",
						JSONUtil.put(
							"https://slickdeals.net/computer-deals", 6.0
						).put(
							"https://slickdeals.net/credit-card-offers/", 1.0
						).put(
							"https://www.tomshardware.com/news/toms-hardware-" +
								"live-events-ces-2021",
							3.0
						).toString())));

		TimeSpan timeSpan = TimeSpan.of("last-7-days");

		TimeRange timeRange = timeSpan.toTimeRange(0);

		List<ReferringURL> referringURLS =
			analyticsReportsDataProvider.getPageReferringURLs(
				RandomTestUtil.randomLong(), null, timeRange,
				RandomTestUtil.randomString());

		Assert.assertEquals(referringURLS.toString(), 3, referringURLS.size());
		Assert.assertEquals(
			String.valueOf(
				new ReferringURL(
					1, "https://slickdeals.net/credit-card-offers/")),
			String.valueOf(referringURLS.get(0)));
		Assert.assertEquals(
			String.valueOf(
				new ReferringURL(
					3,
					"https://www.tomshardware.com/news/toms-hardware-live-" +
						"events-ces-2021")),
			String.valueOf(referringURLS.get(1)));
		Assert.assertEquals(
			String.valueOf(
				new ReferringURL(6, "https://slickdeals.net/computer-deals")),
			String.valueOf(referringURLS.get(2)));
	}

	@Test
	public void testGetReferringSocialMediaList() throws Exception {
		AnalyticsReportsDataProvider analyticsReportsDataProvider =
			new AnalyticsReportsDataProvider(
				_getAnalyticsSettingsManager(),
				_getHttp(
					Collections.singletonMap(
						"/social-page-referrers",
						JSONUtil.put(
							"facebook", 6.0
						).put(
							"instagram", 3.0
						).put(
							"linkedin", 1.0
						).toString())));

		TimeSpan timeSpan = TimeSpan.of("last-7-days");

		TimeRange timeRange = timeSpan.toTimeRange(0);

		List<ReferringSocialMedia> referringSocialMediaList =
			analyticsReportsDataProvider.getReferringSocialMediaList(
				RandomTestUtil.randomLong(), null, timeRange,
				RandomTestUtil.randomString());

		Assert.assertEquals(
			referringSocialMediaList.toString(), 3,
			referringSocialMediaList.size());
		Assert.assertEquals(
			String.valueOf(new ReferringSocialMedia("facebook", 6)),
			String.valueOf(referringSocialMediaList.get(0)));
		Assert.assertEquals(
			String.valueOf(new ReferringSocialMedia("instagram", 3)),
			String.valueOf(referringSocialMediaList.get(1)));
		Assert.assertEquals(
			String.valueOf(new ReferringSocialMedia("linkedin", 1)),
			String.valueOf(referringSocialMediaList.get(2)));
	}

	@Test
	public void testGetTotalReads() throws Exception {
		AnalyticsReportsDataProvider analyticsReportsDataProvider =
			new AnalyticsReportsDataProvider(
				_getAnalyticsSettingsManager(),
				_getHttp(
					HashMapBuilder.put(
						"/read-count", "12345"
					).put(
						"/read-counts",
						JSONUtil.put(
							"histogram",
							JSONUtil.put(
								JSONUtil.put(
									"key",
									() -> {
										LocalDate localDate = LocalDate.now();

										return localDate.format(
											DateTimeFormatter.ISO_LOCAL_DATE);
									}
								).put(
									"value", 5
								))
						).put(
							"value", 5
						).toString()
					).build()));

		Assert.assertEquals(
			Long.valueOf(12340),
			analyticsReportsDataProvider.getTotalReads(
				RandomTestUtil.randomLong(), null,
				RandomTestUtil.randomString()));
	}

	@Test(expected = PortalException.class)
	public void testGetTotalReadsWithAsahFaroBackendError() throws Exception {
		AnalyticsReportsDataProvider analyticsReportsDataProvider =
			new AnalyticsReportsDataProvider(
				_getAnalyticsSettingsManager(), _getHttp(new IOException()));

		analyticsReportsDataProvider.getTotalReads(
			RandomTestUtil.randomLong(), null, RandomTestUtil.randomString());
	}

	@Test
	public void testGetTotalViews() throws Exception {
		AnalyticsReportsDataProvider analyticsReportsDataProvider =
			new AnalyticsReportsDataProvider(
				_getAnalyticsSettingsManager(),
				_getHttp(
					HashMapBuilder.put(
						"/view-count", "12345"
					).put(
						"/view-counts",
						JSONUtil.put(
							"histogram",
							JSONUtil.put(
								JSONUtil.put(
									"key",
									() -> {
										LocalDate localDate = LocalDate.now();

										return localDate.format(
											DateTimeFormatter.ISO_LOCAL_DATE);
									}
								).put(
									"value", 5
								))
						).put(
							"value", 5
						).toString()
					).build()));

		Assert.assertEquals(
			Long.valueOf(12340),
			analyticsReportsDataProvider.getTotalViews(
				RandomTestUtil.randomLong(), null,
				RandomTestUtil.randomString()));
	}

	@Test(expected = PortalException.class)
	public void testGetTotalViewsWithAsahFaroBackendError() throws Exception {
		AnalyticsReportsDataProvider analyticsReportsDataProvider =
			new AnalyticsReportsDataProvider(
				_getAnalyticsSettingsManager(), _getHttp(new IOException()));

		analyticsReportsDataProvider.getTotalViews(
			RandomTestUtil.randomLong(), null, RandomTestUtil.randomString());
	}

	@Test
	public void testGetTrafficChannels() throws Exception {
		AnalyticsReportsDataProvider analyticsReportsDataProvider =
			new AnalyticsReportsDataProvider(
				_getAnalyticsSettingsManager(),
				_getHttp(
					HashMapBuilder.put(
						"/acquisition-channels",
						JSONUtil.put(
							"organic", 3849L
						).put(
							"paid", 235L
						).put(
							"social", 389L
						).toString()
					).build()));

		TimeSpan timeSpan = TimeSpan.of("last-7-days");

		TimeRange timeRange = timeSpan.toTimeRange(0);

		Map<TrafficChannel.Type, TrafficChannel> trafficChannels =
			analyticsReportsDataProvider.getTrafficChannels(
				RandomTestUtil.randomLong(), null, timeRange,
				RandomTestUtil.randomString());

		Assert.assertEquals(
			trafficChannels.toString(), 3, trafficChannels.size());
		Assert.assertEquals(
			String.valueOf(
				TrafficChannel.newInstance(
					3849L, 86.0D, TrafficChannel.Type.ORGANIC)),
			String.valueOf(trafficChannels.get(TrafficChannel.Type.ORGANIC)));
		Assert.assertEquals(
			String.valueOf(
				TrafficChannel.newInstance(
					235L, 5.3D, TrafficChannel.Type.PAID)),
			String.valueOf(trafficChannels.get(TrafficChannel.Type.PAID)));
		Assert.assertEquals(
			String.valueOf(
				TrafficChannel.newInstance(
					389L, 8.7D, TrafficChannel.Type.SOCIAL)),
			String.valueOf(trafficChannels.get(TrafficChannel.Type.SOCIAL)));
	}

	@Test(expected = PortalException.class)
	public void testGetTrafficChannelsWithAsahFaroBackendError()
		throws Exception {

		AnalyticsReportsDataProvider analyticsReportsDataProvider =
			new AnalyticsReportsDataProvider(
				_getAnalyticsSettingsManager(), _getHttp(new IOException()));

		TimeSpan timeSpan = TimeSpan.of("last-7-days");

		TimeRange timeRange = timeSpan.toTimeRange(0);

		analyticsReportsDataProvider.getTrafficChannels(
			RandomTestUtil.randomLong(), null, timeRange,
			RandomTestUtil.randomString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNewAnalyticsReportsDataProviderWithNullHttp()
		throws Exception {

		new AnalyticsReportsDataProvider(_getAnalyticsSettingsManager(), null);
	}

	private AnalyticsSettingsManager _getAnalyticsSettingsManager()
		throws Exception {

		AnalyticsSettingsManager analyticsSettingsManager = Mockito.mock(
			AnalyticsSettingsManager.class);

		Mockito.when(
			analyticsSettingsManager.getAnalyticsConfiguration(
				Mockito.anyLong())
		).thenReturn(
			Mockito.mock(AnalyticsConfiguration.class)
		);

		return analyticsSettingsManager;
	}

	private Http _getHttp(Exception exception) throws Exception {
		Http http = Mockito.mock(Http.class);

		Mockito.when(
			http.URLtoString(Mockito.any(Http.Options.class))
		).thenThrow(
			exception
		);

		return http;
	}

	private Http _getHttp(Map<String, String> mockRequest) throws Exception {
		Http http = Mockito.mock(Http.class);

		Mockito.when(
			http.URLtoString(Mockito.any(Http.Options.class))
		).then(
			answer -> {
				Http.Options options = (Http.Options)answer.getArguments()[0];

				String location = options.getLocation();

				String endpoint = location.substring(
					location.lastIndexOf("/"), location.indexOf("?"));

				if (mockRequest.containsKey(endpoint)) {
					Http.Response httpResponse = new Http.Response();

					httpResponse.setResponseCode(200);

					options.setResponse(httpResponse);

					return mockRequest.get(endpoint);
				}

				Http.Response httpResponse = new Http.Response();

				httpResponse.setResponseCode(400);

				options.setResponse(httpResponse);

				return "error, endpoint not found";
			}
		);

		return http;
	}

}