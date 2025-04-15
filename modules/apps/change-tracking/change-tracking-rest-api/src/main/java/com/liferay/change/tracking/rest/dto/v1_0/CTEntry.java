/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author David Truong
 * @generated
 */
@Generated("")
@GraphQLName("CTEntry")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "CTEntry")
public class CTEntry implements Serializable {

	public static CTEntry toDTO(String json) {
		return ObjectMapperUtil.readValue(CTEntry.class, json);
	}

	public static CTEntry unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(CTEntry.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Map<String, String>> getActions() {
		if (_actionsSupplier != null) {
			actions = _actionsSupplier.get();

			_actionsSupplier = null;
		}

		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;

		_actionsSupplier = null;
	}

	@JsonIgnore
	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		_actionsSupplier = () -> {
			try {
				return actionsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getChangeType() {
		if (_changeTypeSupplier != null) {
			changeType = _changeTypeSupplier.get();

			_changeTypeSupplier = null;
		}

		return changeType;
	}

	public void setChangeType(String changeType) {
		this.changeType = changeType;

		_changeTypeSupplier = null;
	}

	@JsonIgnore
	public void setChangeType(
		UnsafeSupplier<String, Exception> changeTypeUnsafeSupplier) {

		_changeTypeSupplier = () -> {
			try {
				return changeTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String changeType;

	@JsonIgnore
	private Supplier<String> _changeTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getCtCollectionId() {
		if (_ctCollectionIdSupplier != null) {
			ctCollectionId = _ctCollectionIdSupplier.get();

			_ctCollectionIdSupplier = null;
		}

		return ctCollectionId;
	}

	public void setCtCollectionId(Long ctCollectionId) {
		this.ctCollectionId = ctCollectionId;

		_ctCollectionIdSupplier = null;
	}

	@JsonIgnore
	public void setCtCollectionId(
		UnsafeSupplier<Long, Exception> ctCollectionIdUnsafeSupplier) {

		_ctCollectionIdSupplier = () -> {
			try {
				return ctCollectionIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long ctCollectionId;

	@JsonIgnore
	private Supplier<Long> _ctCollectionIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getCtCollectionName() {
		if (_ctCollectionNameSupplier != null) {
			ctCollectionName = _ctCollectionNameSupplier.get();

			_ctCollectionNameSupplier = null;
		}

		return ctCollectionName;
	}

	public void setCtCollectionName(String ctCollectionName) {
		this.ctCollectionName = ctCollectionName;

		_ctCollectionNameSupplier = null;
	}

	@JsonIgnore
	public void setCtCollectionName(
		UnsafeSupplier<String, Exception> ctCollectionNameUnsafeSupplier) {

		_ctCollectionNameSupplier = () -> {
			try {
				return ctCollectionNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String ctCollectionName;

	@JsonIgnore
	private Supplier<String> _ctCollectionNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Status getCtCollectionStatus() {
		if (_ctCollectionStatusSupplier != null) {
			ctCollectionStatus = _ctCollectionStatusSupplier.get();

			_ctCollectionStatusSupplier = null;
		}

		return ctCollectionStatus;
	}

	public void setCtCollectionStatus(Status ctCollectionStatus) {
		this.ctCollectionStatus = ctCollectionStatus;

		_ctCollectionStatusSupplier = null;
	}

	@JsonIgnore
	public void setCtCollectionStatus(
		UnsafeSupplier<Status, Exception> ctCollectionStatusUnsafeSupplier) {

		_ctCollectionStatusSupplier = () -> {
			try {
				return ctCollectionStatusUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Status ctCollectionStatus;

	@JsonIgnore
	private Supplier<Status> _ctCollectionStatusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getCtCollectionStatusDate() {
		if (_ctCollectionStatusDateSupplier != null) {
			ctCollectionStatusDate = _ctCollectionStatusDateSupplier.get();

			_ctCollectionStatusDateSupplier = null;
		}

		return ctCollectionStatusDate;
	}

	public void setCtCollectionStatusDate(Date ctCollectionStatusDate) {
		this.ctCollectionStatusDate = ctCollectionStatusDate;

		_ctCollectionStatusDateSupplier = null;
	}

	@JsonIgnore
	public void setCtCollectionStatusDate(
		UnsafeSupplier<Date, Exception> ctCollectionStatusDateUnsafeSupplier) {

		_ctCollectionStatusDateSupplier = () -> {
			try {
				return ctCollectionStatusDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date ctCollectionStatusDate;

	@JsonIgnore
	private Supplier<Date> _ctCollectionStatusDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getCtCollectionStatusUserName() {
		if (_ctCollectionStatusUserNameSupplier != null) {
			ctCollectionStatusUserName =
				_ctCollectionStatusUserNameSupplier.get();

			_ctCollectionStatusUserNameSupplier = null;
		}

		return ctCollectionStatusUserName;
	}

	public void setCtCollectionStatusUserName(
		String ctCollectionStatusUserName) {

		this.ctCollectionStatusUserName = ctCollectionStatusUserName;

		_ctCollectionStatusUserNameSupplier = null;
	}

	@JsonIgnore
	public void setCtCollectionStatusUserName(
		UnsafeSupplier<String, Exception>
			ctCollectionStatusUserNameUnsafeSupplier) {

		_ctCollectionStatusUserNameSupplier = () -> {
			try {
				return ctCollectionStatusUserNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String ctCollectionStatusUserName;

	@JsonIgnore
	private Supplier<String> _ctCollectionStatusUserNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getHideable() {
		if (_hideableSupplier != null) {
			hideable = _hideableSupplier.get();

			_hideableSupplier = null;
		}

		return hideable;
	}

	public void setHideable(Boolean hideable) {
		this.hideable = hideable;

		_hideableSupplier = null;
	}

	@JsonIgnore
	public void setHideable(
		UnsafeSupplier<Boolean, Exception> hideableUnsafeSupplier) {

		_hideableSupplier = () -> {
			try {
				return hideableUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean hideable;

	@JsonIgnore
	private Supplier<Boolean> _hideableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getModelClassNameId() {
		if (_modelClassNameIdSupplier != null) {
			modelClassNameId = _modelClassNameIdSupplier.get();

			_modelClassNameIdSupplier = null;
		}

		return modelClassNameId;
	}

	public void setModelClassNameId(Long modelClassNameId) {
		this.modelClassNameId = modelClassNameId;

		_modelClassNameIdSupplier = null;
	}

	@JsonIgnore
	public void setModelClassNameId(
		UnsafeSupplier<Long, Exception> modelClassNameIdUnsafeSupplier) {

		_modelClassNameIdSupplier = () -> {
			try {
				return modelClassNameIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long modelClassNameId;

	@JsonIgnore
	private Supplier<Long> _modelClassNameIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getModelClassPK() {
		if (_modelClassPKSupplier != null) {
			modelClassPK = _modelClassPKSupplier.get();

			_modelClassPKSupplier = null;
		}

		return modelClassPK;
	}

	public void setModelClassPK(Long modelClassPK) {
		this.modelClassPK = modelClassPK;

		_modelClassPKSupplier = null;
	}

	@JsonIgnore
	public void setModelClassPK(
		UnsafeSupplier<Long, Exception> modelClassPKUnsafeSupplier) {

		_modelClassPKSupplier = () -> {
			try {
				return modelClassPKUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long modelClassPK;

	@JsonIgnore
	private Supplier<Long> _modelClassPKSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getOwnerId() {
		if (_ownerIdSupplier != null) {
			ownerId = _ownerIdSupplier.get();

			_ownerIdSupplier = null;
		}

		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;

		_ownerIdSupplier = null;
	}

	@JsonIgnore
	public void setOwnerId(
		UnsafeSupplier<Long, Exception> ownerIdUnsafeSupplier) {

		_ownerIdSupplier = () -> {
			try {
				return ownerIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long ownerId;

	@JsonIgnore
	private Supplier<Long> _ownerIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getOwnerName() {
		if (_ownerNameSupplier != null) {
			ownerName = _ownerNameSupplier.get();

			_ownerNameSupplier = null;
		}

		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;

		_ownerNameSupplier = null;
	}

	@JsonIgnore
	public void setOwnerName(
		UnsafeSupplier<String, Exception> ownerNameUnsafeSupplier) {

		_ownerNameSupplier = () -> {
			try {
				return ownerNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String ownerName;

	@JsonIgnore
	private Supplier<String> _ownerNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getSiteId() {
		if (_siteIdSupplier != null) {
			siteId = _siteIdSupplier.get();

			_siteIdSupplier = null;
		}

		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;

		_siteIdSupplier = null;
	}

	@JsonIgnore
	public void setSiteId(
		UnsafeSupplier<Long, Exception> siteIdUnsafeSupplier) {

		_siteIdSupplier = () -> {
			try {
				return siteIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long siteId;

	@JsonIgnore
	private Supplier<Long> _siteIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSiteName() {
		if (_siteNameSupplier != null) {
			siteName = _siteNameSupplier.get();

			_siteNameSupplier = null;
		}

		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;

		_siteNameSupplier = null;
	}

	@JsonIgnore
	public void setSiteName(
		UnsafeSupplier<String, Exception> siteNameUnsafeSupplier) {

		_siteNameSupplier = () -> {
			try {
				return siteNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String siteName;

	@JsonIgnore
	private Supplier<String> _siteNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Status getStatus() {
		if (_statusSupplier != null) {
			status = _statusSupplier.get();

			_statusSupplier = null;
		}

		return status;
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Status status;

	@JsonIgnore
	private Supplier<Status> _statusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Info on when a change was made."
	)
	public String getStatusMessage() {
		if (_statusMessageSupplier != null) {
			statusMessage = _statusMessageSupplier.get();

			_statusMessageSupplier = null;
		}

		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;

		_statusMessageSupplier = null;
	}

	@JsonIgnore
	public void setStatusMessage(
		UnsafeSupplier<String, Exception> statusMessageUnsafeSupplier) {

		_statusMessageSupplier = () -> {
			try {
				return statusMessageUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Info on when a change was made.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String statusMessage;

	@JsonIgnore
	private Supplier<String> _statusMessageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTitle() {
		if (_titleSupplier != null) {
			title = _titleSupplier.get();

			_titleSupplier = null;
		}

		return title;
	}

	public void setTitle(String title) {
		this.title = title;

		_titleSupplier = null;
	}

	@JsonIgnore
	public void setTitle(
		UnsafeSupplier<String, Exception> titleUnsafeSupplier) {

		_titleSupplier = () -> {
			try {
				return titleUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String title;

	@JsonIgnore
	private Supplier<String> _titleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTypeName() {
		if (_typeNameSupplier != null) {
			typeName = _typeNameSupplier.get();

			_typeNameSupplier = null;
		}

		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;

		_typeNameSupplier = null;
	}

	@JsonIgnore
	public void setTypeName(
		UnsafeSupplier<String, Exception> typeNameUnsafeSupplier) {

		_typeNameSupplier = () -> {
			try {
				return typeNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String typeName;

	@JsonIgnore
	private Supplier<String> _typeNameSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CTEntry)) {
			return false;
		}

		CTEntry ctEntry = (CTEntry)object;

		return Objects.equals(toString(), ctEntry.toString());
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

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		String changeType = getChangeType();

		if (changeType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"changeType\": ");

			sb.append("\"");

			sb.append(_escape(changeType));

			sb.append("\"");
		}

		Long ctCollectionId = getCtCollectionId();

		if (ctCollectionId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ctCollectionId\": ");

			sb.append(ctCollectionId);
		}

		String ctCollectionName = getCtCollectionName();

		if (ctCollectionName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ctCollectionName\": ");

			sb.append("\"");

			sb.append(_escape(ctCollectionName));

			sb.append("\"");
		}

		Status ctCollectionStatus = getCtCollectionStatus();

		if (ctCollectionStatus != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ctCollectionStatus\": ");

			sb.append(String.valueOf(ctCollectionStatus));
		}

		Date ctCollectionStatusDate = getCtCollectionStatusDate();

		if (ctCollectionStatusDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ctCollectionStatusDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(ctCollectionStatusDate));

			sb.append("\"");
		}

		String ctCollectionStatusUserName = getCtCollectionStatusUserName();

		if (ctCollectionStatusUserName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ctCollectionStatusUserName\": ");

			sb.append("\"");

			sb.append(_escape(ctCollectionStatusUserName));

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

		Boolean hideable = getHideable();

		if (hideable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hideable\": ");

			sb.append(hideable);
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Long modelClassNameId = getModelClassNameId();

		if (modelClassNameId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modelClassNameId\": ");

			sb.append(modelClassNameId);
		}

		Long modelClassPK = getModelClassPK();

		if (modelClassPK != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modelClassPK\": ");

			sb.append(modelClassPK);
		}

		Long ownerId = getOwnerId();

		if (ownerId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ownerId\": ");

			sb.append(ownerId);
		}

		String ownerName = getOwnerName();

		if (ownerName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ownerName\": ");

			sb.append("\"");

			sb.append(_escape(ownerName));

			sb.append("\"");
		}

		Long siteId = getSiteId();

		if (siteId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(siteId);
		}

		String siteName = getSiteName();

		if (siteName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteName\": ");

			sb.append("\"");

			sb.append(_escape(siteName));

			sb.append("\"");
		}

		Status status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(String.valueOf(status));
		}

		String statusMessage = getStatusMessage();

		if (statusMessage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"statusMessage\": ");

			sb.append("\"");

			sb.append(_escape(statusMessage));

			sb.append("\"");
		}

		String title = getTitle();

		if (title != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(title));

			sb.append("\"");
		}

		String typeName = getTypeName();

		if (typeName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"typeName\": ");

			sb.append("\"");

			sb.append(_escape(typeName));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.change.tracking.rest.dto.v1_0.CTEntry",
		name = "x-class-name"
	)
	public String xClassName;

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