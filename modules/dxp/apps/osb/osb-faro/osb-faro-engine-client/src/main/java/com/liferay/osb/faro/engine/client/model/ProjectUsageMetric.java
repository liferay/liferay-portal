/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Marcos Martins
 */
public class ProjectUsageMetric {

	public Date getCreateDate() {
		return _createDate;
	}

	@JsonProperty("_embedded")
	public Map<String, Object> getEmbeddedResources() {
		return _embeddedResources;
	}

	@JsonProperty("knownIndividuals")
	public long getKnownIndividualsCount() {
		return _knownIndividualsCount;
	}

	@JsonProperty("pageViews")
	public long getPageViewsCount() {
		return _pageViewsCount;
	}

	public String getProjectId() {
		return _projectId;
	}

	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	public void setEmbeddedResources(Map<String, Object> embeddedResources) {
		_embeddedResources = embeddedResources;
	}

	public void setKnownIndividualsCount(long knownIndividualsCount) {
		_knownIndividualsCount = knownIndividualsCount;
	}

	public void setPageViewsCount(long pageViewsCount) {
		_pageViewsCount = pageViewsCount;
	}

	public void setProjectId(String projectId) {
		_projectId = projectId;
	}

	private Date _createDate;
	private Map<String, Object> _embeddedResources = new HashMap<>();
	private long _knownIndividualsCount;
	private long _pageViewsCount;
	private String _projectId;

}