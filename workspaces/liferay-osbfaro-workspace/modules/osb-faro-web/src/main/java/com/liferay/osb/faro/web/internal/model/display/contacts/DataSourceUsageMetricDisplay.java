/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

import java.util.List;

/**
 * @author Caio Pinheiro
 */
public class DataSourceUsageMetricDisplay {

	public DataSourceUsageMetricDisplay(
		long apiCallsCount, long batchSegmentsCount,
		long connectedDataSourcesCount, String corpProjectName,
		String corpProjectUuid, List<DataSourceUsage> dataSourceUsages,
		String lastAccessDateString, String lastAnniversaryDateString,
		boolean offline, long realTimeSegmentsCount, String weDeployKey) {

		_apiCallsCount = apiCallsCount;
		_batchSegmentsCount = batchSegmentsCount;
		_connectedDataSourcesCount = connectedDataSourcesCount;
		_corpProjectName = corpProjectName;
		_corpProjectUuid = corpProjectUuid;
		_dataSourceUsages = dataSourceUsages;
		_lastAccessDateString = lastAccessDateString;
		_lastAnniversaryDateString = lastAnniversaryDateString;
		_offline = offline;
		_realTimeSegmentsCount = realTimeSegmentsCount;
		_weDeployKey = weDeployKey;
	}

	public long getApiCallsCount() {
		return _apiCallsCount;
	}

	public long getBatchSegmentsCount() {
		return _batchSegmentsCount;
	}

	public long getConnectedDataSourcesCount() {
		return _connectedDataSourcesCount;
	}

	public String getCorpProjectName() {
		return _corpProjectName;
	}

	public String getCorpProjectUuid() {
		return _corpProjectUuid;
	}

	public List<DataSourceUsage> getDataSourceUsages() {
		return _dataSourceUsages;
	}

	public String getLastAccessDateString() {
		return _lastAccessDateString;
	}

	public String getLastAnniversaryDateString() {
		return _lastAnniversaryDateString;
	}

	public long getRealTimeSegmentsCount() {
		return _realTimeSegmentsCount;
	}

	public String getWeDeployKey() {
		return _weDeployKey;
	}

	public boolean isOffline() {
		return _offline;
	}

	private final long _apiCallsCount;
	private final long _batchSegmentsCount;
	private final long _connectedDataSourcesCount;
	private final String _corpProjectName;
	private final String _corpProjectUuid;
	private final List<DataSourceUsage> _dataSourceUsages;
	private final String _lastAccessDateString;
	private final String _lastAnniversaryDateString;
	private final boolean _offline;
	private final long _realTimeSegmentsCount;
	private final String _weDeployKey;

}