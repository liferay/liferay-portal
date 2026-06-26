/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.helper;

import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.util.DateUtil;
import com.liferay.osb.faro.web.internal.model.display.contacts.DataSourceUsage;
import com.liferay.osb.faro.web.internal.model.display.contacts.DataSourceUsageMetric;
import com.liferay.osb.faro.web.internal.model.display.contacts.DataSourceUsageMetricDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

		for (FaroProject faroProject :
				_faroProjectLocalService.getFaroProjects(
					(page - 1) * pageSize, page * pageSize)) {

			dataSourceUsageMetricDisplays.add(
				_getDataSourceUsageMetricDisplay(faroProject));
		}

		return Page.of(
			dataSourceUsageMetricDisplays, Pagination.of(page, pageSize),
			_faroProjectLocalService.getFaroProjectsCount());
	}

	private DataSourceUsageMetricDisplay _getDataSourceUsageMetricDisplay(
			FaroProject faroProject)
		throws Exception {

		return new DataSourceUsageMetricDisplay(
			5, _getDataSourceUsages().size(), faroProject.getCorpProjectName(),
			faroProject.getCorpProjectUuid(), _getDataSourceUsages(),
			DateUtil.formatDate(
				new Date(faroProject.getLastAccessTime()),
				DateUtil.PATTERN_DATE),
			DateUtil.formatDate(
				faroProject.getLastAnniversaryDate(), DateUtil.PATTERN_DATE),
			!StringUtil.equals(
				faroProject.getState(), FaroProjectConstants.STATE_READY),
			3, faroProject.getWeDeployKey());
	}

	private List<DataSourceUsageMetric> _getDataSourceUsageMetrics(
		int dataSourceIndex) {

		return ListUtil.fromArray(
			new DataSourceUsageMetric(
				"2026-06-17", 100000 - (dataSourceIndex * 12000),
				60 + (dataSourceIndex * 25)),
			new DataSourceUsageMetric(
				"2026-06-16", 98230 - (dataSourceIndex * 12000),
				63 + (dataSourceIndex * 25)));
	}

	private List<DataSourceUsage> _getDataSourceUsages() {
		return ListUtil.fromArray(
			new DataSourceUsage(
				"10001", "Liferay", _getDataSourceUsageMetrics(0)),
			new DataSourceUsage(
				"10002", "Salesforce", _getDataSourceUsageMetrics(1)));
	}

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

}