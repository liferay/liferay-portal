/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A saved audience definition that selects individuals matching a filter expression.",
	value = "IndividualSegment"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A saved audience definition that selects individuals matching a filter expression."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "IndividualSegment")
public class IndividualSegment implements Serializable {

	public static IndividualSegment toDTO(String json) {
		return ObjectMapperUtil.readValue(IndividualSegment.class, json);
	}

	public static IndividualSegment unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(IndividualSegment.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Number of individuals currently in the segment who have been active."
	)
	public Long getActiveIndividualCount() {
		if (_activeIndividualCountSupplier != null) {
			activeIndividualCount = _activeIndividualCountSupplier.get();

			_activeIndividualCountSupplier = null;
		}

		return activeIndividualCount;
	}

	public void setActiveIndividualCount(Long activeIndividualCount) {
		this.activeIndividualCount = activeIndividualCount;

		_activeIndividualCountSupplier = null;
	}

	@JsonIgnore
	public void setActiveIndividualCount(
		UnsafeSupplier<Long, Exception> activeIndividualCountUnsafeSupplier) {

		_activeIndividualCountSupplier = () -> {
			try {
				return activeIndividualCountUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Number of individuals currently in the segment who have been active."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long activeIndividualCount;

	@JsonIgnore
	private Supplier<Long> _activeIndividualCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Number of anonymous individuals in this segment."
	)
	public Long getAnonymousIndividualCount() {
		if (_anonymousIndividualCountSupplier != null) {
			anonymousIndividualCount = _anonymousIndividualCountSupplier.get();

			_anonymousIndividualCountSupplier = null;
		}

		return anonymousIndividualCount;
	}

	public void setAnonymousIndividualCount(Long anonymousIndividualCount) {
		this.anonymousIndividualCount = anonymousIndividualCount;

		_anonymousIndividualCountSupplier = null;
	}

	@JsonIgnore
	public void setAnonymousIndividualCount(
		UnsafeSupplier<Long, Exception>
			anonymousIndividualCountUnsafeSupplier) {

		_anonymousIndividualCountSupplier = () -> {
			try {
				return anonymousIndividualCountUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Number of anonymous individuals in this segment."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long anonymousIndividualCount;

	@JsonIgnore
	private Supplier<Long> _anonymousIndividualCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "ID of the channel this segment is scoped to."
	)
	public String getChannelId() {
		if (_channelIdSupplier != null) {
			channelId = _channelIdSupplier.get();

			_channelIdSupplier = null;
		}

		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;

		_channelIdSupplier = null;
	}

	@JsonIgnore
	public void setChannelId(
		UnsafeSupplier<String, Exception> channelIdUnsafeSupplier) {

		_channelIdSupplier = () -> {
			try {
				return channelIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "ID of the channel this segment is scoped to.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String channelId;

	@JsonIgnore
	private Supplier<String> _channelIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When the segment was created."
	)
	public Date getDateCreated() {
		if (_dateCreatedSupplier != null) {
			dateCreated = _dateCreatedSupplier.get();

			_dateCreatedSupplier = null;
		}

		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;

		_dateCreatedSupplier = null;
	}

	@JsonIgnore
	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		_dateCreatedSupplier = () -> {
			try {
				return dateCreatedUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "When the segment was created.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Last time the segment definition was modified."
	)
	public Date getDateModified() {
		if (_dateModifiedSupplier != null) {
			dateModified = _dateModifiedSupplier.get();

			_dateModifiedSupplier = null;
		}

		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;

		_dateModifiedSupplier = null;
	}

	@JsonIgnore
	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		_dateModifiedSupplier = () -> {
			try {
				return dateModifiedUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Last time the segment definition was modified."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "OData-style filter expression that defines segment membership."
	)
	public String getFilter() {
		if (_filterSupplier != null) {
			filter = _filterSupplier.get();

			_filterSupplier = null;
		}

		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;

		_filterSupplier = null;
	}

	@JsonIgnore
	public void setFilter(
		UnsafeSupplier<String, Exception> filterUnsafeSupplier) {

		_filterSupplier = () -> {
			try {
				return filterUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "OData-style filter expression that defines segment membership."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String filter;

	@JsonIgnore
	private Supplier<String> _filterSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Segment ID. Use this with `getWorkspaceGroupIndividualSegment` to fetch a single segment."
	)
	public String getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(String id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Segment ID. Use this with `getWorkspaceGroupIndividualSegment` to fetch a single segment."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String id;

	@JsonIgnore
	private Supplier<String> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "True if anonymous individuals are eligible for membership in this segment."
	)
	public Boolean getIncludeAnonymousUsers() {
		if (_includeAnonymousUsersSupplier != null) {
			includeAnonymousUsers = _includeAnonymousUsersSupplier.get();

			_includeAnonymousUsersSupplier = null;
		}

		return includeAnonymousUsers;
	}

	public void setIncludeAnonymousUsers(Boolean includeAnonymousUsers) {
		this.includeAnonymousUsers = includeAnonymousUsers;

		_includeAnonymousUsersSupplier = null;
	}

	@JsonIgnore
	public void setIncludeAnonymousUsers(
		UnsafeSupplier<Boolean, Exception>
			includeAnonymousUsersUnsafeSupplier) {

		_includeAnonymousUsersSupplier = () -> {
			try {
				return includeAnonymousUsersUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "True if anonymous individuals are eligible for membership in this segment."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean includeAnonymousUsers;

	@JsonIgnore
	private Supplier<Boolean> _includeAnonymousUsersSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Total individuals in the segment."
	)
	public Long getIndividualCount() {
		if (_individualCountSupplier != null) {
			individualCount = _individualCountSupplier.get();

			_individualCountSupplier = null;
		}

		return individualCount;
	}

	public void setIndividualCount(Long individualCount) {
		this.individualCount = individualCount;

		_individualCountSupplier = null;
	}

	@JsonIgnore
	public void setIndividualCount(
		UnsafeSupplier<Long, Exception> individualCountUnsafeSupplier) {

		_individualCountSupplier = () -> {
			try {
				return individualCountUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Total individuals in the segment.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long individualCount;

	@JsonIgnore
	private Supplier<Long> _individualCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Number of known individuals in this segment."
	)
	public Long getKnownIndividualCount() {
		if (_knownIndividualCountSupplier != null) {
			knownIndividualCount = _knownIndividualCountSupplier.get();

			_knownIndividualCountSupplier = null;
		}

		return knownIndividualCount;
	}

	public void setKnownIndividualCount(Long knownIndividualCount) {
		this.knownIndividualCount = knownIndividualCount;

		_knownIndividualCountSupplier = null;
	}

	@JsonIgnore
	public void setKnownIndividualCount(
		UnsafeSupplier<Long, Exception> knownIndividualCountUnsafeSupplier) {

		_knownIndividualCountSupplier = () -> {
			try {
				return knownIndividualCountUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Number of known individuals in this segment.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long knownIndividualCount;

	@JsonIgnore
	private Supplier<Long> _knownIndividualCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Most recent activity by any individual currently in the segment."
	)
	public Date getLastActivityDate() {
		if (_lastActivityDateSupplier != null) {
			lastActivityDate = _lastActivityDateSupplier.get();

			_lastActivityDateSupplier = null;
		}

		return lastActivityDate;
	}

	public void setLastActivityDate(Date lastActivityDate) {
		this.lastActivityDate = lastActivityDate;

		_lastActivityDateSupplier = null;
	}

	@JsonIgnore
	public void setLastActivityDate(
		UnsafeSupplier<Date, Exception> lastActivityDateUnsafeSupplier) {

		_lastActivityDateSupplier = () -> {
			try {
				return lastActivityDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Most recent activity by any individual currently in the segment."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date lastActivityDate;

	@JsonIgnore
	private Supplier<Date> _lastActivityDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display name of the segment."
	)
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Display name of the segment.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Segment evaluation cadence."
	)
	@JsonGetter("segmentType")
	@Valid
	public SegmentType getSegmentType() {
		if (_segmentTypeSupplier != null) {
			segmentType = _segmentTypeSupplier.get();

			_segmentTypeSupplier = null;
		}

		return segmentType;
	}

	@JsonIgnore
	public String getSegmentTypeAsString() {
		SegmentType segmentType = getSegmentType();

		if (segmentType == null) {
			return null;
		}

		return segmentType.toString();
	}

	public void setSegmentType(SegmentType segmentType) {
		this.segmentType = segmentType;

		_segmentTypeSupplier = null;
	}

	@JsonIgnore
	public void setSegmentType(
		UnsafeSupplier<SegmentType, Exception> segmentTypeUnsafeSupplier) {

		_segmentTypeSupplier = () -> {
			try {
				return segmentTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Segment evaluation cadence.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected SegmentType segmentType;

	@JsonIgnore
	private Supplier<SegmentType> _segmentTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Lifecycle state of the segment."
	)
	@JsonGetter("state")
	@Valid
	public State getState() {
		if (_stateSupplier != null) {
			state = _stateSupplier.get();

			_stateSupplier = null;
		}

		return state;
	}

	@JsonIgnore
	public String getStateAsString() {
		State state = getState();

		if (state == null) {
			return null;
		}

		return state.toString();
	}

	public void setState(State state) {
		this.state = state;

		_stateSupplier = null;
	}

	@JsonIgnore
	public void setState(UnsafeSupplier<State, Exception> stateUnsafeSupplier) {
		_stateSupplier = () -> {
			try {
				return stateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Lifecycle state of the segment.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected State state;

	@JsonIgnore
	private Supplier<State> _stateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Operational status of the segment."
	)
	@JsonGetter("status")
	@Valid
	public Status getStatus() {
		if (_statusSupplier != null) {
			status = _statusSupplier.get();

			_statusSupplier = null;
		}

		return status;
	}

	@JsonIgnore
	public String getStatusAsString() {
		Status status = getStatus();

		if (status == null) {
			return null;
		}

		return status.toString();
	}

	public void setStatus(Status status) {
		this.status = status;

		_statusSupplier = null;
	}

	@JsonIgnore
	public void setStatus(
		UnsafeSupplier<Status, Exception> statusUnsafeSupplier) {

		_statusSupplier = () -> {
			try {
				return statusUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Operational status of the segment.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Status status;

	@JsonIgnore
	private Supplier<Status> _statusSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof IndividualSegment)) {
			return false;
		}

		IndividualSegment individualSegment = (IndividualSegment)object;

		return Objects.equals(toString(), individualSegment.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		Long activeIndividualCount = getActiveIndividualCount();

		if (activeIndividualCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"activeIndividualCount\": ");

			sb.append(activeIndividualCount);
		}

		Long anonymousIndividualCount = getAnonymousIndividualCount();

		if (anonymousIndividualCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"anonymousIndividualCount\": ");

			sb.append(anonymousIndividualCount);
		}

		String channelId = getChannelId();

		if (channelId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelId\": ");

			sb.append("\"");

			sb.append(_escape(channelId));

			sb.append("\"");
		}

		Date dateCreated = getDateCreated();

		if (dateCreated != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateCreated));

			sb.append("\"");
		}

		Date dateModified = getDateModified();

		if (dateModified != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateModified));

			sb.append("\"");
		}

		String filter = getFilter();

		if (filter != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"filter\": ");

			sb.append("\"");

			sb.append(_escape(filter));

			sb.append("\"");
		}

		String id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(id));

			sb.append("\"");
		}

		Boolean includeAnonymousUsers = getIncludeAnonymousUsers();

		if (includeAnonymousUsers != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"includeAnonymousUsers\": ");

			sb.append(includeAnonymousUsers);
		}

		Long individualCount = getIndividualCount();

		if (individualCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"individualCount\": ");

			sb.append(individualCount);
		}

		Long knownIndividualCount = getKnownIndividualCount();

		if (knownIndividualCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"knownIndividualCount\": ");

			sb.append(knownIndividualCount);
		}

		Date lastActivityDate = getLastActivityDate();

		if (lastActivityDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastActivityDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(lastActivityDate));

			sb.append("\"");
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		SegmentType segmentType = getSegmentType();

		if (segmentType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"segmentType\": ");

			sb.append("\"");
			sb.append(segmentType);
			sb.append("\"");
		}

		State state = getState();

		if (state != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"state\": ");

			sb.append("\"");
			sb.append(state);
			sb.append("\"");
		}

		Status status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");
			sb.append(status);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.IndividualSegment",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("SegmentType")
	public static enum SegmentType {

		BATCH("BATCH"), REAL_TIME("REAL_TIME");

		@JsonCreator
		public static SegmentType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (SegmentType segmentType : values()) {
				if (Objects.equals(segmentType.getValue(), value)) {
					return segmentType;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private SegmentType(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("State")
	public static enum State {

		DISABLED("DISABLED"), IN_PROGRESS("IN_PROGRESS"), READY("READY");

		@JsonCreator
		public static State create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (State state : values()) {
				if (Objects.equals(state.getValue(), value)) {
					return state;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private State(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("Status")
	public static enum Status {

		ACTIVE("ACTIVE"), INACTIVE("INACTIVE");

		@JsonCreator
		public static Status create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Status status : values()) {
				if (Objects.equals(status.getValue(), value)) {
					return status;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private Status(String value) {
			_value = value;
		}

		private final String _value;

	}

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:-1793603065