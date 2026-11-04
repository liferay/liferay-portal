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
 * @author Marcos Martins
 */
public class ProjectUsageMetric {

	public long getActiveLogicalBytes() {
		return _activeLogicalBytes;
	}

	@JsonProperty("anonymousIndividuals")
	public long getAnonymousIndividualsCount() {
		return _anonymousIndividualsCount;
	}

	public Date getCreateDate() {
		return _createDate;
	}

	@JsonProperty("_embedded")
	public Map<String, Object> getEmbeddedResources() {
		return _embeddedResources;
	}

	@JsonProperty("events")
	public long getEventsCount() {
		return _eventsCount;
	}

	@JsonProperty("knownIndividuals")
	public long getKnownIndividualsCount() {
		return _knownIndividualsCount;
	}

	public long getLongTermLogicalBytes() {
		return _longTermLogicalBytes;
	}

	public long getOnDemandBytesBilled() {
		return _onDemandBytesBilled;
	}

	@JsonProperty("pageViews")
	public long getPageViewsCount() {
		return _pageViewsCount;
	}

	public String getProjectId() {
		return _projectId;
	}

	public long getReservationSlotMs() {
		return _reservationSlotMs;
	}

	public void setActiveLogicalBytes(long activeLogicalBytes) {
		_activeLogicalBytes = activeLogicalBytes;
	}

	public void setAnonymousIndividualsCount(long anonymousIndividualsCount) {
		_anonymousIndividualsCount = anonymousIndividualsCount;
	}

	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	public void setEmbeddedResources(Map<String, Object> embeddedResources) {
		_embeddedResources = embeddedResources;
	}

	public void setEventsCount(long eventsCount) {
		_eventsCount = eventsCount;
	}

	public void setKnownIndividualsCount(long knownIndividualsCount) {
		_knownIndividualsCount = knownIndividualsCount;
	}

	public void setLongTermLogicalBytes(long longTermLogicalBytes) {
		_longTermLogicalBytes = longTermLogicalBytes;
	}

	public void setOnDemandBytesBilled(long onDemandBytesBilled) {
		_onDemandBytesBilled = onDemandBytesBilled;
	}

	public void setPageViewsCount(long pageViewsCount) {
		_pageViewsCount = pageViewsCount;
	}

	public void setProjectId(String projectId) {
		_projectId = projectId;
	}

	public void setReservationSlotMs(long reservationSlotMs) {
		_reservationSlotMs = reservationSlotMs;
	}

	private long _activeLogicalBytes;
	private long _anonymousIndividualsCount;
	private Date _createDate;
	private Map<String, Object> _embeddedResources = new HashMap<>();
	private long _eventsCount;
	private long _knownIndividualsCount;
	private long _longTermLogicalBytes;
	private long _onDemandBytesBilled;
	private long _pageViewsCount;
	private String _projectId;
	private long _reservationSlotMs;

}