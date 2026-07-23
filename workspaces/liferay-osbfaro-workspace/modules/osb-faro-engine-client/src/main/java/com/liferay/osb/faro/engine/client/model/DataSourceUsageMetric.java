/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Caio Pinheiro
 */
public class DataSourceUsageMetric {

	@JsonProperty("billableEvents")
	public long getBillableEventsCount() {
		return _billableEventsCount;
	}

	public long getDataSourceId() {
		return _dataSourceId;
	}

	public String getDataSourceName() {
		return _dataSourceName;
	}

	public String getDataSourceStatus() {
		return _dataSourceStatus;
	}

	public Date getDate() {
		return _date;
	}

	@JsonProperty("_embedded")
	public Map<String, Object> getEmbeddedResources() {
		return _embeddedResources;
	}

	@JsonProperty("knownIndividuals")
	public long getKnownIndividualsCount() {
		return _knownIndividualsCount;
	}

	public String getProjectId() {
		return _projectId;
	}

	public void setBillableEventsCount(long billableEventsCount) {
		_billableEventsCount = billableEventsCount;
	}

	public void setDataSourceId(long dataSourceId) {
		_dataSourceId = dataSourceId;
	}

	public void setDataSourceName(String dataSourceName) {
		_dataSourceName = dataSourceName;
	}

	public void setDataSourceStatus(String dataSourceStatus) {
		_dataSourceStatus = dataSourceStatus;
	}

	public void setDate(Date date) {
		_date = date;
	}

	public void setEmbeddedResources(Map<String, Object> embeddedResources) {
		_embeddedResources = embeddedResources;
	}

	public void setKnownIndividualsCount(long knownIndividualsCount) {
		_knownIndividualsCount = knownIndividualsCount;
	}

	public void setProjectId(String projectId) {
		_projectId = projectId;
	}

	private long _billableEventsCount;
	private long _dataSourceId;
	private String _dataSourceName;
	private String _dataSourceStatus;
	private Date _date;
	private Map<String, Object> _embeddedResources = new HashMap<>();
	private long _knownIndividualsCount;
	private String _projectId;

}