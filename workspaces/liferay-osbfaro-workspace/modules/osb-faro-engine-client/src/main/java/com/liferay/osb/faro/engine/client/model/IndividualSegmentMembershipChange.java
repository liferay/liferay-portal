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
 * @author Matthew Kong
 */
public class IndividualSegmentMembershipChange {

	public Date getDateChanged() {
		if (_dateChanged == null) {
			return null;
		}

		return new Date(_dateChanged.getTime());
	}

	public Date getDateFirst() {
		if (_dateFirst == null) {
			return null;
		}

		return new Date(_dateFirst.getTime());
	}

	@JsonProperty("_embedded")
	public Map<String, Object> getEmbeddedResources() {
		return _embeddedResources;
	}

	public String getId() {
		return _id;
	}

	public String getIndividualEmail() {
		return _individualEmail;
	}

	public String getIndividualId() {
		return _individualId;
	}

	public String getIndividualName() {
		return _individualName;
	}

	public String getIndividualSegmentId() {
		return _individualSegmentId;
	}

	public Long getIndividualsCount() {
		return _individualsCount;
	}

	public String getOperation() {
		return _operation;
	}

	public boolean isIndividualDeleted() {
		return _individualDeleted;
	}

	public void setDateChanged(Date dateChanged) {
		if (dateChanged != null) {
			_dateChanged = new Date(dateChanged.getTime());
		}
	}

	public void setDateFirst(Date dateFirst) {
		if (dateFirst != null) {
			_dateFirst = new Date(dateFirst.getTime());
		}
	}

	public void setEmbeddedResources(Map<String, Object> embeddedResources) {
		_embeddedResources = embeddedResources;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setIndividualDeleted(boolean individualDeleted) {
		_individualDeleted = individualDeleted;
	}

	public void setIndividualEmail(String individualEmail) {
		_individualEmail = individualEmail;
	}

	public void setIndividualId(String individualId) {
		_individualId = individualId;
	}

	public void setIndividualName(String individualName) {
		_individualName = individualName;
	}

	public void setIndividualSegmentId(String individualSegmentId) {
		_individualSegmentId = individualSegmentId;
	}

	public void setIndividualsCount(Long individualsCount) {
		_individualsCount = individualsCount;
	}

	public void setOperation(String operation) {
		_operation = operation;
	}

	public enum Operation {

		ADDED, REMOVED

	}

	private Date _dateChanged;
	private Date _dateFirst;
	private Map<String, Object> _embeddedResources = new HashMap<>();
	private String _id;
	private boolean _individualDeleted;
	private String _individualEmail;
	private String _individualId;
	private String _individualName;
	private String _individualSegmentId;
	private Long _individualsCount;
	private String _operation = Operation.ADDED.name();

}