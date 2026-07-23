/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

import java.util.List;

/**
 * @author Caio Pinheiro
 */
public class DataSourceUsage {

	public DataSourceUsage(
		String dataSourceId, String dataSourceName, String dataSourceStatus,
		List<DataSourceUsageMetric> usageMetrics) {

		_dataSourceId = dataSourceId;
		_dataSourceName = dataSourceName;
		_dataSourceStatus = dataSourceStatus;
		_usageMetrics = usageMetrics;
	}

	public String getDataSourceId() {
		return _dataSourceId;
	}

	public String getDataSourceName() {
		return _dataSourceName;
	}

	public String getDataSourceStatus() {
		return _dataSourceStatus;
	}

	public List<DataSourceUsageMetric> getUsageMetrics() {
		return _usageMetrics;
	}

	private final String _dataSourceId;
	private final String _dataSourceName;
	private final String _dataSourceStatus;
	private final List<DataSourceUsageMetric> _usageMetrics;

}