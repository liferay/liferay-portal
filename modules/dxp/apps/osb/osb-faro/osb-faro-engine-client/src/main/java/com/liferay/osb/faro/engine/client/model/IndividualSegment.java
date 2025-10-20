/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
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
public class IndividualSegment {

	public long getActiveIndividualCount() {
		return _activeIndividualCount;
	}

	public long getActivitiesCount() {
		return _activitiesCount;
	}

	public long getAnonymousIndividualCount() {
		return _anonymousIndividualCount;
	}

	public Author getAuthor() {
		return _author;
	}

	public String getChannelId() {
		return _channelId;
	}

	public Date getDateCreated() {
		if (_dateCreated == null) {
			return null;
		}

		return new Date(_dateCreated.getTime());
	}

	public Date getDateModified() {
		if (_dateModified == null) {
			return null;
		}

		return new Date(_dateModified.getTime());
	}

	@JsonProperty("_embedded")
	public Map<String, Object> getEmbeddedResources() {
		return _embeddedResources;
	}

	public String getFilter() {
		return _filter;
	}

	public String getFilterMetadata() {
		return _filterMetadata;
	}

	public String getId() {
		return _id;
	}

	public long getIndividualCount() {
		return _individualCount;
	}

	public long getKnownIndividualCount() {
		return _knownIndividualCount;
	}

	public Date getLastActivityDate() {
		if (_lastActivityDate == null) {
			return null;
		}

		return new Date(_lastActivityDate.getTime());
	}

	public String getName() {
		return _name;
	}

	public String getScope() {
		return _scope;
	}

	public String getSegmentType() {
		return _segmentType;
	}

	public String getState() {
		return _state;
	}

	public String getStatus() {
		return _status;
	}

	public boolean isIncludeAnonymousUsers() {
		return _includeAnonymousUsers;
	}

	public void setActiveIndividualCount(long activeIndividualCount) {
		_activeIndividualCount = activeIndividualCount;
	}

	public void setActivitiesCount(long activitiesCount) {
		_activitiesCount = activitiesCount;
	}

	public void setAnonymousIndividualCount(long anonymousIndividualCount) {
		_anonymousIndividualCount = anonymousIndividualCount;
	}

	public void setAuthor(Author author) {
		_author = author;
	}

	public void setChannelId(String channelId) {
		_channelId = channelId;
	}

	public void setDateCreated(Date dateCreated) {
		if (dateCreated != null) {
			_dateCreated = new Date(dateCreated.getTime());
		}
	}

	public void setDateModified(Date dateModified) {
		if (dateModified != null) {
			_dateModified = new Date(dateModified.getTime());
		}
	}

	public void setEmbeddedResources(Map<String, Object> embeddedResources) {
		_embeddedResources = embeddedResources;
	}

	public void setFilter(String filter) {
		_filter = filter;
	}

	public void setFilterMetadata(String filterMetadata) {
		_filterMetadata = filterMetadata;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setIncludeAnonymousUsers(boolean includeAnonymousUsers) {
		_includeAnonymousUsers = includeAnonymousUsers;
	}

	public void setIndividualCount(long individualCount) {
		_individualCount = individualCount;
	}

	public void setKnownIndividualCount(long knownIndividualCount) {
		_knownIndividualCount = knownIndividualCount;
	}

	public void setLastActivityDate(Date lastActivityDate) {
		if (lastActivityDate != null) {
			_lastActivityDate = new Date(lastActivityDate.getTime());
		}
	}

	public void setName(String name) {
		_name = name;
	}

	public void setScope(String scope) {
		_scope = scope;
	}

	public void setSegmentType(String segmentType) {
		_segmentType = segmentType;
	}

	public void setState(String state) {
		_state = state;
	}

	public void setStatus(String status) {
		_status = status;
	}

	public enum Scope {

		PROJECT, USER

	}

	public enum State {

		DISABLED, IN_PROGRESS, READY

	}

	public enum Status {

		ACTIVE, INACTIVE

	}

	public enum Type {

		BATCH, REAL_TIME

	}

	private long _activeIndividualCount;
	private long _activitiesCount;
	private long _anonymousIndividualCount;
	private Author _author;
	private String _channelId;
	private Date _dateCreated;
	private Date _dateModified;
	private Map<String, Object> _embeddedResources = new HashMap<>();
	private String _filter;
	private String _filterMetadata;
	private String _id;
	private boolean _includeAnonymousUsers;
	private long _individualCount;
	private long _knownIndividualCount;
	private Date _lastActivityDate;
	private String _name;
	private String _scope = Scope.PROJECT.name();
	private String _segmentType = Type.BATCH.name();
	private String _state = State.READY.name();
	private String _status = Status.ACTIVE.name();

}