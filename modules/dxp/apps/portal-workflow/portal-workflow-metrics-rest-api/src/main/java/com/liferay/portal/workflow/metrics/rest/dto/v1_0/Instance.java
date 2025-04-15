/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.dto.v1_0;

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
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "https://www.schema.org/Instance", value = "Instance"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Instance")
public class Instance implements Serializable {

	public static Instance toDTO(String json) {
		return ObjectMapperUtil.readValue(Instance.class, json);
	}

	public static Instance unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Instance.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getActive() {
		if (_activeSupplier != null) {
			active = _activeSupplier.get();

			_activeSupplier = null;
		}

		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;

		_activeSupplier = null;
	}

	@JsonIgnore
	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		_activeSupplier = () -> {
			try {
				return activeUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean active;

	@JsonIgnore
	private Supplier<Boolean> _activeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAssetTitle() {
		if (_assetTitleSupplier != null) {
			assetTitle = _assetTitleSupplier.get();

			_assetTitleSupplier = null;
		}

		return assetTitle;
	}

	public void setAssetTitle(String assetTitle) {
		this.assetTitle = assetTitle;

		_assetTitleSupplier = null;
	}

	@JsonIgnore
	public void setAssetTitle(
		UnsafeSupplier<String, Exception> assetTitleUnsafeSupplier) {

		_assetTitleSupplier = () -> {
			try {
				return assetTitleUnsafeSupplier.get();
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
	protected String assetTitle;

	@JsonIgnore
	private Supplier<String> _assetTitleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, String> getAssetTitle_i18n() {
		if (_assetTitle_i18nSupplier != null) {
			assetTitle_i18n = _assetTitle_i18nSupplier.get();

			_assetTitle_i18nSupplier = null;
		}

		return assetTitle_i18n;
	}

	public void setAssetTitle_i18n(Map<String, String> assetTitle_i18n) {
		this.assetTitle_i18n = assetTitle_i18n;

		_assetTitle_i18nSupplier = null;
	}

	@JsonIgnore
	public void setAssetTitle_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			assetTitle_i18nUnsafeSupplier) {

		_assetTitle_i18nSupplier = () -> {
			try {
				return assetTitle_i18nUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> assetTitle_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _assetTitle_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAssetType() {
		if (_assetTypeSupplier != null) {
			assetType = _assetTypeSupplier.get();

			_assetTypeSupplier = null;
		}

		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;

		_assetTypeSupplier = null;
	}

	@JsonIgnore
	public void setAssetType(
		UnsafeSupplier<String, Exception> assetTypeUnsafeSupplier) {

		_assetTypeSupplier = () -> {
			try {
				return assetTypeUnsafeSupplier.get();
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
	protected String assetType;

	@JsonIgnore
	private Supplier<String> _assetTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, String> getAssetType_i18n() {
		if (_assetType_i18nSupplier != null) {
			assetType_i18n = _assetType_i18nSupplier.get();

			_assetType_i18nSupplier = null;
		}

		return assetType_i18n;
	}

	public void setAssetType_i18n(Map<String, String> assetType_i18n) {
		this.assetType_i18n = assetType_i18n;

		_assetType_i18nSupplier = null;
	}

	@JsonIgnore
	public void setAssetType_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			assetType_i18nUnsafeSupplier) {

		_assetType_i18nSupplier = () -> {
			try {
				return assetType_i18nUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> assetType_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _assetType_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Assignee[] getAssignees() {
		if (_assigneesSupplier != null) {
			assignees = _assigneesSupplier.get();

			_assigneesSupplier = null;
		}

		return assignees;
	}

	public void setAssignees(Assignee[] assignees) {
		this.assignees = assignees;

		_assigneesSupplier = null;
	}

	@JsonIgnore
	public void setAssignees(
		UnsafeSupplier<Assignee[], Exception> assigneesUnsafeSupplier) {

		_assigneesSupplier = () -> {
			try {
				return assigneesUnsafeSupplier.get();
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
	protected Assignee[] assignees;

	@JsonIgnore
	private Supplier<Assignee[]> _assigneesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getClassName() {
		if (_classNameSupplier != null) {
			className = _classNameSupplier.get();

			_classNameSupplier = null;
		}

		return className;
	}

	public void setClassName(String className) {
		this.className = className;

		_classNameSupplier = null;
	}

	@JsonIgnore
	public void setClassName(
		UnsafeSupplier<String, Exception> classNameUnsafeSupplier) {

		_classNameSupplier = () -> {
			try {
				return classNameUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String className;

	@JsonIgnore
	private Supplier<String> _classNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getClassPK() {
		if (_classPKSupplier != null) {
			classPK = _classPKSupplier.get();

			_classPKSupplier = null;
		}

		return classPK;
	}

	public void setClassPK(Long classPK) {
		this.classPK = classPK;

		_classPKSupplier = null;
	}

	@JsonIgnore
	public void setClassPK(
		UnsafeSupplier<Long, Exception> classPKUnsafeSupplier) {

		_classPKSupplier = () -> {
			try {
				return classPKUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long classPK;

	@JsonIgnore
	private Supplier<Long> _classPKSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getCompleted() {
		if (_completedSupplier != null) {
			completed = _completedSupplier.get();

			_completedSupplier = null;
		}

		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;

		_completedSupplier = null;
	}

	@JsonIgnore
	public void setCompleted(
		UnsafeSupplier<Boolean, Exception> completedUnsafeSupplier) {

		_completedSupplier = () -> {
			try {
				return completedUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean completed;

	@JsonIgnore
	private Supplier<Boolean> _completedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Creator getCreator() {
		if (_creatorSupplier != null) {
			creator = _creatorSupplier.get();

			_creatorSupplier = null;
		}

		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;

		_creatorSupplier = null;
	}

	@JsonIgnore
	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		_creatorSupplier = () -> {
			try {
				return creatorUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getDateCompletion() {
		if (_dateCompletionSupplier != null) {
			dateCompletion = _dateCompletionSupplier.get();

			_dateCompletionSupplier = null;
		}

		return dateCompletion;
	}

	public void setDateCompletion(Date dateCompletion) {
		this.dateCompletion = dateCompletion;

		_dateCompletionSupplier = null;
	}

	@JsonIgnore
	public void setDateCompletion(
		UnsafeSupplier<Date, Exception> dateCompletionUnsafeSupplier) {

		_dateCompletionSupplier = () -> {
			try {
				return dateCompletionUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date dateCompletion;

	@JsonIgnore
	private Supplier<Date> _dateCompletionSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getDuration() {
		if (_durationSupplier != null) {
			duration = _durationSupplier.get();

			_durationSupplier = null;
		}

		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;

		_durationSupplier = null;
	}

	@JsonIgnore
	public void setDuration(
		UnsafeSupplier<Long, Exception> durationUnsafeSupplier) {

		_durationSupplier = () -> {
			try {
				return durationUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long duration;

	@JsonIgnore
	private Supplier<Long> _durationSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getProcessId() {
		if (_processIdSupplier != null) {
			processId = _processIdSupplier.get();

			_processIdSupplier = null;
		}

		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;

		_processIdSupplier = null;
	}

	@JsonIgnore
	public void setProcessId(
		UnsafeSupplier<Long, Exception> processIdUnsafeSupplier) {

		_processIdSupplier = () -> {
			try {
				return processIdUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long processId;

	@JsonIgnore
	private Supplier<Long> _processIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getProcessVersion() {
		if (_processVersionSupplier != null) {
			processVersion = _processVersionSupplier.get();

			_processVersionSupplier = null;
		}

		return processVersion;
	}

	public void setProcessVersion(String processVersion) {
		this.processVersion = processVersion;

		_processVersionSupplier = null;
	}

	@JsonIgnore
	public void setProcessVersion(
		UnsafeSupplier<String, Exception> processVersionUnsafeSupplier) {

		_processVersionSupplier = () -> {
			try {
				return processVersionUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String processVersion;

	@JsonIgnore
	private Supplier<String> _processVersionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public SLAResult[] getSlaResults() {
		if (_slaResultsSupplier != null) {
			slaResults = _slaResultsSupplier.get();

			_slaResultsSupplier = null;
		}

		return slaResults;
	}

	public void setSlaResults(SLAResult[] slaResults) {
		this.slaResults = slaResults;

		_slaResultsSupplier = null;
	}

	@JsonIgnore
	public void setSlaResults(
		UnsafeSupplier<SLAResult[], Exception> slaResultsUnsafeSupplier) {

		_slaResultsSupplier = () -> {
			try {
				return slaResultsUnsafeSupplier.get();
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
	protected SLAResult[] slaResults;

	@JsonIgnore
	private Supplier<SLAResult[]> _slaResultsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("slaStatus")
	@Valid
	public SLAStatus getSLAStatus() {
		if (_slaStatusSupplier != null) {
			slaStatus = _slaStatusSupplier.get();

			_slaStatusSupplier = null;
		}

		return slaStatus;
	}

	@JsonIgnore
	public String getSLAStatusAsString() {
		SLAStatus slaStatus = getSLAStatus();

		if (slaStatus == null) {
			return null;
		}

		return slaStatus.toString();
	}

	public void setSLAStatus(SLAStatus slaStatus) {
		this.slaStatus = slaStatus;

		_slaStatusSupplier = null;
	}

	@JsonIgnore
	public void setSLAStatus(
		UnsafeSupplier<SLAStatus, Exception> slaStatusUnsafeSupplier) {

		_slaStatusSupplier = () -> {
			try {
				return slaStatusUnsafeSupplier.get();
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
	protected SLAStatus slaStatus;

	@JsonIgnore
	private Supplier<SLAStatus> _slaStatusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getTaskNames() {
		if (_taskNamesSupplier != null) {
			taskNames = _taskNamesSupplier.get();

			_taskNamesSupplier = null;
		}

		return taskNames;
	}

	public void setTaskNames(String[] taskNames) {
		this.taskNames = taskNames;

		_taskNamesSupplier = null;
	}

	@JsonIgnore
	public void setTaskNames(
		UnsafeSupplier<String[], Exception> taskNamesUnsafeSupplier) {

		_taskNamesSupplier = () -> {
			try {
				return taskNamesUnsafeSupplier.get();
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
	protected String[] taskNames;

	@JsonIgnore
	private Supplier<String[]> _taskNamesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Transition[] getTransitions() {
		if (_transitionsSupplier != null) {
			transitions = _transitionsSupplier.get();

			_transitionsSupplier = null;
		}

		return transitions;
	}

	public void setTransitions(Transition[] transitions) {
		this.transitions = transitions;

		_transitionsSupplier = null;
	}

	@JsonIgnore
	public void setTransitions(
		UnsafeSupplier<Transition[], Exception> transitionsUnsafeSupplier) {

		_transitionsSupplier = () -> {
			try {
				return transitionsUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Transition[] transitions;

	@JsonIgnore
	private Supplier<Transition[]> _transitionsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Instance)) {
			return false;
		}

		Instance instance = (Instance)object;

		return Objects.equals(toString(), instance.toString());
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

		Boolean active = getActive();

		if (active != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(active);
		}

		String assetTitle = getAssetTitle();

		if (assetTitle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTitle\": ");

			sb.append("\"");

			sb.append(_escape(assetTitle));

			sb.append("\"");
		}

		Map<String, String> assetTitle_i18n = getAssetTitle_i18n();

		if (assetTitle_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTitle_i18n\": ");

			sb.append(_toJSON(assetTitle_i18n));
		}

		String assetType = getAssetType();

		if (assetType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetType\": ");

			sb.append("\"");

			sb.append(_escape(assetType));

			sb.append("\"");
		}

		Map<String, String> assetType_i18n = getAssetType_i18n();

		if (assetType_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetType_i18n\": ");

			sb.append(_toJSON(assetType_i18n));
		}

		Assignee[] assignees = getAssignees();

		if (assignees != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assignees\": ");

			sb.append("[");

			for (int i = 0; i < assignees.length; i++) {
				sb.append(String.valueOf(assignees[i]));

				if ((i + 1) < assignees.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String className = getClassName();

		if (className != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(className));

			sb.append("\"");
		}

		Long classPK = getClassPK();

		if (classPK != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"classPK\": ");

			sb.append(classPK);
		}

		Boolean completed = getCompleted();

		if (completed != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"completed\": ");

			sb.append(completed);
		}

		Creator creator = getCreator();

		if (creator != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(creator));
		}

		Date dateCompletion = getDateCompletion();

		if (dateCompletion != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCompletion\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateCompletion));

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

		Long duration = getDuration();

		if (duration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"duration\": ");

			sb.append(duration);
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Long processId = getProcessId();

		if (processId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"processId\": ");

			sb.append(processId);
		}

		String processVersion = getProcessVersion();

		if (processVersion != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"processVersion\": ");

			sb.append("\"");

			sb.append(_escape(processVersion));

			sb.append("\"");
		}

		SLAResult[] slaResults = getSlaResults();

		if (slaResults != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"slaResults\": ");

			sb.append("[");

			for (int i = 0; i < slaResults.length; i++) {
				sb.append(String.valueOf(slaResults[i]));

				if ((i + 1) < slaResults.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		SLAStatus slaStatus = getSLAStatus();

		if (slaStatus != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"slaStatus\": ");

			sb.append("\"");

			sb.append(slaStatus);

			sb.append("\"");
		}

		String[] taskNames = getTaskNames();

		if (taskNames != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taskNames\": ");

			sb.append("[");

			for (int i = 0; i < taskNames.length; i++) {
				sb.append("\"");

				sb.append(_escape(taskNames[i]));

				sb.append("\"");

				if ((i + 1) < taskNames.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Transition[] transitions = getTransitions();

		if (transitions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"transitions\": ");

			sb.append("[");

			for (int i = 0; i < transitions.length; i++) {
				sb.append(String.valueOf(transitions[i]));

				if ((i + 1) < transitions.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.portal.workflow.metrics.rest.dto.v1_0.Instance",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("SLAStatus")
	public static enum SLAStatus {

		ON_TIME("OnTime"), OVERDUE("Overdue"), UNTRACKED("Untracked");

		@JsonCreator
		public static SLAStatus create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (SLAStatus slaStatus : values()) {
				if (Objects.equals(slaStatus.getValue(), value)) {
					return slaStatus;
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

		private SLAStatus(String value) {
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