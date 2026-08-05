/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.helper;

import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.ApiUsage;
import com.liferay.osb.faro.engine.client.model.ApiUsageMetric;
import com.liferay.osb.faro.engine.client.model.ProjectMetric;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroDataSourceUsage;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroDataSourceUsageLocalService;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.util.DateUtil;
import com.liferay.osb.faro.web.internal.model.display.contacts.ApiUsageMetricDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.DataSourceUsage;
import com.liferay.osb.faro.web.internal.model.display.contacts.DataSourceUsageMetric;
import com.liferay.osb.faro.web.internal.model.display.contacts.DataSourceUsageMetricDisplay;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Caio Pinheiro
 */
@Component(service = ProjectUsageHelper.class)
public class ProjectUsageHelper {

	public Page<DataSourceUsageMetricDisplay> getDataSourceUsageMetricDisplays(
			String endDateString, int page, int pageSize,
			String startDateString)
		throws Exception {

		List<DataSourceUsageMetricDisplay> dataSourceUsageMetricDisplays =
			new ArrayList<>();

		List<FaroProject> faroProjects =
			_faroProjectLocalService.getFaroProjects(
				(page - 1) * pageSize, page * pageSize);

		Map<String, Map<String, ApiUsageMetric>> apiUsageMetricsMap =
			_getApiUsageMetricsMap(
				endDateString, faroProjects, startDateString);

		Date endDate = _parseUTCDate(endDateString);

		Map<String, Map<String, ProjectMetric>> projectMetricsMap =
			_getProjectMetricsMap(faroProjects);

		Date startDate = _parseUTCDate(startDateString);

		for (FaroProject faroProject : faroProjects) {
			try {
				dataSourceUsageMetricDisplays.add(
					_getDataSourceUsageMetricDisplay(
						apiUsageMetricsMap, endDate, faroProject,
						projectMetricsMap, startDate));
			}
			catch (Exception exception) {
				_log.error(
					"Unable to build data source usage metric display for " +
						"Faro project " + faroProject.getFaroProjectId(),
					exception);
			}
		}

		return Page.of(
			dataSourceUsageMetricDisplays, Pagination.of(page, pageSize),
			_faroProjectLocalService.getFaroProjectsCount());
	}

	private String _formatUTCDate(long time) {
		Instant instant = Instant.ofEpochMilli(time);

		ZonedDateTime zonedDateTime = instant.atZone(ZoneOffset.UTC);

		LocalDate localDate = zonedDateTime.toLocalDate();

		return localDate.format(_dateTimeFormatter);
	}

	private Map<String, Map<String, ApiUsageMetric>> _getApiUsageMetricsMap(
			String endDateString, List<FaroProject> faroProjects,
			String startDateString)
		throws Exception {

		Map<String, Map<String, ApiUsageMetric>> apiUsageMetricsMap =
			new HashMap<>();

		for (FaroProject faroProject : faroProjects) {
			String serverLocation = faroProject.getServerLocation();

			if (apiUsageMetricsMap.containsKey(serverLocation)) {
				continue;
			}

			Map<String, ApiUsageMetric> apiUsageMetricMap = new HashMap<>();

			try {
				Results<ApiUsageMetric> results =
					_contactsEngineClient.getApiUsageMetrics(
						faroProject, endDateString, startDateString);

				for (ApiUsageMetric apiUsageMetric : results.getItems()) {
					apiUsageMetricMap.put(
						apiUsageMetric.getProjectId(), apiUsageMetric);
				}
			}
			catch (Exception exception) {
				_log.error(
					"Unable to fetch API usage metrics for project " +
						faroProject.getProjectId(),
					exception);
			}

			apiUsageMetricsMap.put(serverLocation, apiUsageMetricMap);
		}

		return apiUsageMetricsMap;
	}

	private DataSourceUsageMetricDisplay _getDataSourceUsageMetricDisplay(
			Map<String, Map<String, ApiUsageMetric>> apiUsageMetricsMap,
			Date endDate, FaroProject faroProject,
			Map<String, Map<String, ProjectMetric>> projectMetricsMap,
			Date startDate)
		throws Exception {

		Map<String, ApiUsageMetric> apiUsageMetricMap = apiUsageMetricsMap.get(
			faroProject.getServerLocation());

		ApiUsageMetric apiUsageMetric = apiUsageMetricMap.get(
			faroProject.getProjectId());

		List<ApiUsageMetricDisplay> apiUsageMetricDisplays = new ArrayList<>();

		if (apiUsageMetric != null) {
			for (ApiUsage apiUsage : apiUsageMetric.getApiUsages()) {
				apiUsageMetricDisplays.add(
					new ApiUsageMetricDisplay(
						apiUsage.getCallsCount(), apiUsage.getDateString()));
			}
		}

		long batchSegmentsCount = 0;
		long connectorsCount = 0;
		long dataSourcesCount = 0;
		long realTimeSegmentsCount = 0;

		Map<String, ProjectMetric> projectMetricMap = projectMetricsMap.get(
			faroProject.getServerLocation());

		ProjectMetric projectMetric = projectMetricMap.get(
			faroProject.getProjectId());

		if (projectMetric != null) {
			batchSegmentsCount = projectMetric.getBatchSegmentsCount();
			connectorsCount = projectMetric.getConnectorsConnected();
			dataSourcesCount = projectMetric.getDataSourcesConnected();
			realTimeSegmentsCount = projectMetric.getRealTimeSegmentsCount();
		}

		List<DataSourceUsage> dataSourceUsages = new ArrayList<>();

		Map<Long, List<FaroDataSourceUsage>> faroDataSourceUsagesMap =
			new LinkedHashMap<>();

		for (FaroDataSourceUsage faroDataSourceUsage :
				_faroDataSourceUsageLocalService.getFaroDataSourceUsages(
					faroProject.getFaroProjectId(), startDate, endDate)) {

			List<FaroDataSourceUsage> faroDataSourceUsages =
				faroDataSourceUsagesMap.computeIfAbsent(
					faroDataSourceUsage.getDataSourceId(),
					dataSourceId -> new ArrayList<>());

			faroDataSourceUsages.add(faroDataSourceUsage);
		}

		for (Map.Entry<Long, List<FaroDataSourceUsage>> entry :
				faroDataSourceUsagesMap.entrySet()) {

			List<FaroDataSourceUsage> faroDataSourceUsages = entry.getValue();

			FaroDataSourceUsage latestFaroDataSourceUsage =
				faroDataSourceUsages.get(faroDataSourceUsages.size() - 1);

			dataSourceUsages.add(
				new DataSourceUsage(
					String.valueOf(entry.getKey()),
					GetterUtil.getString(
						latestFaroDataSourceUsage.getDataSourceName(),
						String.valueOf(entry.getKey())),
					latestFaroDataSourceUsage.getDataSourceStatus(),
					TransformUtil.transform(
						faroDataSourceUsages,
						faroDataSourceUsage -> new DataSourceUsageMetric(
							_formatUTCDate(faroDataSourceUsage.getUsageTime()),
							faroDataSourceUsage.getBillableEventsCount(),
							faroDataSourceUsage.getKnownIndividualsCount()))));
		}

		return new DataSourceUsageMetricDisplay(
			apiUsageMetricDisplays, batchSegmentsCount, dataSourcesCount,
			connectorsCount, faroProject.getCorpProjectName(),
			faroProject.getCorpProjectUuid(), dataSourceUsages,
			DateUtil.formatDate(
				new Date(faroProject.getLastAccessTime()),
				DateUtil.PATTERN_DATE),
			DateUtil.formatDate(
				faroProject.getLastAnniversaryDate(), DateUtil.PATTERN_DATE),
			!StringUtil.equals(
				faroProject.getState(), FaroProjectConstants.STATE_READY),
			realTimeSegmentsCount, faroProject.getWeDeployKey());
	}

	private Map<String, Map<String, ProjectMetric>> _getProjectMetricsMap(
		List<FaroProject> faroProjects) {

		Map<String, Map<String, ProjectMetric>> projectMetricsMap =
			new HashMap<>();

		for (FaroProject faroProject : faroProjects) {
			String serverLocation = faroProject.getServerLocation();

			if (projectMetricsMap.containsKey(serverLocation)) {
				continue;
			}

			Map<String, ProjectMetric> projectMetricMap = new HashMap<>();

			try {
				Results<ProjectMetric> results =
					_contactsEngineClient.getProjectMetrics(faroProject);

				for (ProjectMetric projectMetric : results.getItems()) {
					projectMetricMap.put(
						projectMetric.getProjectId(), projectMetric);
				}
			}
			catch (Exception exception) {
				_log.error(
					"Unable to fetch project metrics for project " +
						faroProject.getProjectId(),
					exception);
			}

			projectMetricsMap.put(serverLocation, projectMetricMap);
		}

		return projectMetricsMap;
	}

	private Date _parseUTCDate(String dateString) {
		LocalDate localDate = LocalDate.now(ZoneOffset.UTC);

		localDate = localDate.minusDays(1);

		if (dateString != null) {
			localDate = LocalDate.parse(dateString, _dateTimeFormatter);
		}

		ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneOffset.UTC);

		return Date.from(zonedDateTime.toInstant());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProjectUsageHelper.class);

	private static final DateTimeFormatter _dateTimeFormatter =
		DateTimeFormatter.ofPattern(DateUtil.PATTERN_DATE);

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	@Reference
	private FaroDataSourceUsageLocalService _faroDataSourceUsageLocalService;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

}