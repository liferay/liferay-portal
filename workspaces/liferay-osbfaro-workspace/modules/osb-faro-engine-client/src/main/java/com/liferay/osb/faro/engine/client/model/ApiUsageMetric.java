/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nilton Vieira
 */
public class ApiUsageMetric {

	@JsonProperty("calls")
	public long getCallsCount() {
		return _callsCount;
	}

	@JsonProperty("_embedded")
	public Map<String, Object> getEmbeddedResources() {
		return _embeddedResources;
	}

	public String getProjectId() {
		return _projectId;
	}

	public void setCallsCount(long callsCount) {
		_callsCount = callsCount;
	}

	public void setEmbeddedResources(Map<String, Object> embeddedResources) {
		_embeddedResources = embeddedResources;
	}

	public void setProjectId(String projectId) {
		_projectId = projectId;
	}

	private long _callsCount;
	private Map<String, Object> _embeddedResources = new HashMap<>();
	private String _projectId;

}