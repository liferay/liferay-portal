/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Caio Pinheiro
 */
public class ProjectDataSourceCount {

	@JsonProperty("connectorsConnected")
	public long getConnectorsConnected() {
		return _connectorsConnected;
	}

	@JsonProperty("dataSourcesConnected")
	public long getDataSourcesConnected() {
		return _dataSourcesConnected;
	}

	@JsonProperty("_embedded")
	public Map<String, Object> getEmbeddedResources() {
		return _embeddedResources;
	}

	public String getProjectId() {
		return _projectId;
	}

	public void setConnectorsConnected(long connectorsConnected) {
		_connectorsConnected = connectorsConnected;
	}

	public void setDataSourcesConnected(long dataSourcesConnected) {
		_dataSourcesConnected = dataSourcesConnected;
	}

	public void setEmbeddedResources(Map<String, Object> embeddedResources) {
		_embeddedResources = embeddedResources;
	}

	public void setProjectId(String projectId) {
		_projectId = projectId;
	}

	private long _connectorsConnected;
	private long _dataSourcesConnected;
	private Map<String, Object> _embeddedResources = new HashMap<>();
	private String _projectId;

}