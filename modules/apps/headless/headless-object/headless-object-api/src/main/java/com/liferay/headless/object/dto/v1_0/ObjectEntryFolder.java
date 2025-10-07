/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.headless.delivery.dto.v1_0.Creator;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

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
 * @author Alicia García
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents a object entry folder that contains objects entries and other object entry folders.",
	value = "ObjectEntryFolder"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Represents a object entry folder that contains objects entries and other object entry folders.",
	requiredProperties = {"title"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ObjectEntryFolder")
public class ObjectEntryFolder implements Serializable {

	public static ObjectEntryFolder toDTO(String json) {
		return ObjectMapperUtil.readValue(ObjectEntryFolder.class, json);
	}

	public static ObjectEntryFolder unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ObjectEntryFolder.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Block of actions allowed by the user making the request."
	)
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

	@GraphQLField(
		description = "Block of actions allowed by the user making the request."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The object entry folder's creator."
	)
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

	@GraphQLField(description = "The object entry folder's creator.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The object entry folder's creation date."
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

	@GraphQLField(description = "The object entry folder's creation date.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The last time a field of the folder changed."
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

	@GraphQLField(description = "The last time a field of the folder changed.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The object entry folder's description."
	)
	public String getDescription() {
		if (_descriptionSupplier != null) {
			description = _descriptionSupplier.get();

			_descriptionSupplier = null;
		}

		return description;
	}

	public void setDescription(String description) {
		this.description = description;

		_descriptionSupplier = null;
	}

	@JsonIgnore
	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		_descriptionSupplier = () -> {
			try {
				return descriptionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The object entry folder's description.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The object entry folder's external reference code."
	)
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
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
		description = "The object entry folder's external reference code."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The object entry folder's ID."
	)
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

	@GraphQLField(description = "The object entry folder's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The object entry folder's label."
	)
	public String getLabel() {
		if (_labelSupplier != null) {
			label = _labelSupplier.get();

			_labelSupplier = null;
		}

		return label;
	}

	public void setLabel(String label) {
		this.label = label;

		_labelSupplier = null;
	}

	@JsonIgnore
	public void setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		_labelSupplier = () -> {
			try {
				return labelUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The object entry folder's label.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String label;

	@JsonIgnore
	private Supplier<String> _labelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized object entry folder's label."
	)
	@Valid
	public Map<String, String> getLabel_i18n() {
		if (_label_i18nSupplier != null) {
			label_i18n = _label_i18nSupplier.get();

			_label_i18nSupplier = null;
		}

		return label_i18n;
	}

	public void setLabel_i18n(Map<String, String> label_i18n) {
		this.label_i18n = label_i18n;

		_label_i18nSupplier = null;
	}

	@JsonIgnore
	public void setLabel_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			label_i18nUnsafeSupplier) {

		_label_i18nSupplier = () -> {
			try {
				return label_i18nUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The localized object entry folder's label.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> label_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _label_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The number of object entries in this object entry folder."
	)
	public Integer getNumberOfObjectEntries() {
		if (_numberOfObjectEntriesSupplier != null) {
			numberOfObjectEntries = _numberOfObjectEntriesSupplier.get();

			_numberOfObjectEntriesSupplier = null;
		}

		return numberOfObjectEntries;
	}

	public void setNumberOfObjectEntries(Integer numberOfObjectEntries) {
		this.numberOfObjectEntries = numberOfObjectEntries;

		_numberOfObjectEntriesSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfObjectEntries(
		UnsafeSupplier<Integer, Exception>
			numberOfObjectEntriesUnsafeSupplier) {

		_numberOfObjectEntriesSupplier = () -> {
			try {
				return numberOfObjectEntriesUnsafeSupplier.get();
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
		description = "The number of object entries in this object entry folder."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer numberOfObjectEntries;

	@JsonIgnore
	private Supplier<Integer> _numberOfObjectEntriesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The number of this object entry folder's child object entry folders."
	)
	public Integer getNumberOfObjectEntryFolders() {
		if (_numberOfObjectEntryFoldersSupplier != null) {
			numberOfObjectEntryFolders =
				_numberOfObjectEntryFoldersSupplier.get();

			_numberOfObjectEntryFoldersSupplier = null;
		}

		return numberOfObjectEntryFolders;
	}

	public void setNumberOfObjectEntryFolders(
		Integer numberOfObjectEntryFolders) {

		this.numberOfObjectEntryFolders = numberOfObjectEntryFolders;

		_numberOfObjectEntryFoldersSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfObjectEntryFolders(
		UnsafeSupplier<Integer, Exception>
			numberOfObjectEntryFoldersUnsafeSupplier) {

		_numberOfObjectEntryFoldersSupplier = () -> {
			try {
				return numberOfObjectEntryFoldersUnsafeSupplier.get();
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
		description = "The number of this object entry folder's child object entry folders."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer numberOfObjectEntryFolders;

	@JsonIgnore
	private Supplier<Integer> _numberOfObjectEntryFoldersSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The object entry folder's parent, if it exists."
	)
	@Valid
	public ParentObjectEntryFolderBrief getParentObjectEntryFolderBrief() {
		if (_parentObjectEntryFolderBriefSupplier != null) {
			parentObjectEntryFolderBrief =
				_parentObjectEntryFolderBriefSupplier.get();

			_parentObjectEntryFolderBriefSupplier = null;
		}

		return parentObjectEntryFolderBrief;
	}

	public void setParentObjectEntryFolderBrief(
		ParentObjectEntryFolderBrief parentObjectEntryFolderBrief) {

		this.parentObjectEntryFolderBrief = parentObjectEntryFolderBrief;

		_parentObjectEntryFolderBriefSupplier = null;
	}

	@JsonIgnore
	public void setParentObjectEntryFolderBrief(
		UnsafeSupplier<ParentObjectEntryFolderBrief, Exception>
			parentObjectEntryFolderBriefUnsafeSupplier) {

		_parentObjectEntryFolderBriefSupplier = () -> {
			try {
				return parentObjectEntryFolderBriefUnsafeSupplier.get();
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
		description = "The object entry folder's parent, if it exists."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected ParentObjectEntryFolderBrief parentObjectEntryFolderBrief;

	@JsonIgnore
	private Supplier<ParentObjectEntryFolderBrief>
		_parentObjectEntryFolderBriefSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The parent entry folder's external reference code, if it exists."
	)
	public String getParentObjectEntryFolderExternalReferenceCode() {
		if (_parentObjectEntryFolderExternalReferenceCodeSupplier != null) {
			parentObjectEntryFolderExternalReferenceCode =
				_parentObjectEntryFolderExternalReferenceCodeSupplier.get();

			_parentObjectEntryFolderExternalReferenceCodeSupplier = null;
		}

		return parentObjectEntryFolderExternalReferenceCode;
	}

	public void setParentObjectEntryFolderExternalReferenceCode(
		String parentObjectEntryFolderExternalReferenceCode) {

		this.parentObjectEntryFolderExternalReferenceCode =
			parentObjectEntryFolderExternalReferenceCode;

		_parentObjectEntryFolderExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setParentObjectEntryFolderExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			parentObjectEntryFolderExternalReferenceCodeUnsafeSupplier) {

		_parentObjectEntryFolderExternalReferenceCodeSupplier = () -> {
			try {
				return parentObjectEntryFolderExternalReferenceCodeUnsafeSupplier.
					get();
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
		description = "The parent entry folder's external reference code, if it exists."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String parentObjectEntryFolderExternalReferenceCode;

	@JsonIgnore
	private Supplier<String>
		_parentObjectEntryFolderExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The ID of the object entry folder's parent, if it exists."
	)
	public Long getParentObjectEntryFolderId() {
		if (_parentObjectEntryFolderIdSupplier != null) {
			parentObjectEntryFolderId =
				_parentObjectEntryFolderIdSupplier.get();

			_parentObjectEntryFolderIdSupplier = null;
		}

		return parentObjectEntryFolderId;
	}

	public void setParentObjectEntryFolderId(Long parentObjectEntryFolderId) {
		this.parentObjectEntryFolderId = parentObjectEntryFolderId;

		_parentObjectEntryFolderIdSupplier = null;
	}

	@JsonIgnore
	public void setParentObjectEntryFolderId(
		UnsafeSupplier<Long, Exception>
			parentObjectEntryFolderIdUnsafeSupplier) {

		_parentObjectEntryFolderIdSupplier = () -> {
			try {
				return parentObjectEntryFolderIdUnsafeSupplier.get();
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
		description = "The ID of the object entry folder's parent, if it exists."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long parentObjectEntryFolderId;

	@JsonIgnore
	private Supplier<Long> _parentObjectEntryFolderIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public com.liferay.portal.vulcan.permission.Permission[] getPermissions() {
		if (_permissionsSupplier != null) {
			permissions = _permissionsSupplier.get();

			_permissionsSupplier = null;
		}

		return permissions;
	}

	public void setPermissions(
		com.liferay.portal.vulcan.permission.Permission[] permissions) {

		this.permissions = permissions;

		_permissionsSupplier = null;
	}

	@JsonIgnore
	public void setPermissions(
		UnsafeSupplier
			<com.liferay.portal.vulcan.permission.Permission[], Exception>
				permissionsUnsafeSupplier) {

		_permissionsSupplier = () -> {
			try {
				return permissionsUnsafeSupplier.get();
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
	protected com.liferay.portal.vulcan.permission.Permission[] permissions;

	@JsonIgnore
	private Supplier<com.liferay.portal.vulcan.permission.Permission[]>
		_permissionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Creator getRemovedBy() {
		if (_removedBySupplier != null) {
			removedBy = _removedBySupplier.get();

			_removedBySupplier = null;
		}

		return removedBy;
	}

	public void setRemovedBy(Creator removedBy) {
		this.removedBy = removedBy;

		_removedBySupplier = null;
	}

	@JsonIgnore
	public void setRemovedBy(
		UnsafeSupplier<Creator, Exception> removedByUnsafeSupplier) {

		_removedBySupplier = () -> {
			try {
				return removedByUnsafeSupplier.get();
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
	protected Creator removedBy;

	@JsonIgnore
	private Supplier<Creator> _removedBySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getRemovedDate() {
		if (_removedDateSupplier != null) {
			removedDate = _removedDateSupplier.get();

			_removedDateSupplier = null;
		}

		return removedDate;
	}

	public void setRemovedDate(Date removedDate) {
		this.removedDate = removedDate;

		_removedDateSupplier = null;
	}

	@JsonIgnore
	public void setRemovedDate(
		UnsafeSupplier<Date, Exception> removedDateUnsafeSupplier) {

		_removedDateSupplier = () -> {
			try {
				return removedDateUnsafeSupplier.get();
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
	protected Date removedDate;

	@JsonIgnore
	private Supplier<Date> _removedDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The scope id of the object entry folder."
	)
	public Long getScopeId() {
		if (_scopeIdSupplier != null) {
			scopeId = _scopeIdSupplier.get();

			_scopeIdSupplier = null;
		}

		return scopeId;
	}

	public void setScopeId(Long scopeId) {
		this.scopeId = scopeId;

		_scopeIdSupplier = null;
	}

	@JsonIgnore
	public void setScopeId(
		UnsafeSupplier<Long, Exception> scopeIdUnsafeSupplier) {

		_scopeIdSupplier = () -> {
			try {
				return scopeIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The scope id of the object entry folder.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long scopeId;

	@JsonIgnore
	private Supplier<Long> _scopeIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The scope key of the object entry folder."
	)
	public String getScopeKey() {
		if (_scopeKeySupplier != null) {
			scopeKey = _scopeKeySupplier.get();

			_scopeKeySupplier = null;
		}

		return scopeKey;
	}

	public void setScopeKey(String scopeKey) {
		this.scopeKey = scopeKey;

		_scopeKeySupplier = null;
	}

	@JsonIgnore
	public void setScopeKey(
		UnsafeSupplier<String, Exception> scopeKeyUnsafeSupplier) {

		_scopeKeySupplier = () -> {
			try {
				return scopeKeyUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The scope key of the object entry folder.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String scopeKey;

	@JsonIgnore
	private Supplier<String> _scopeKeySupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Status status;

	@JsonIgnore
	private Supplier<Status> _statusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The object entry folder's main title/name."
	)
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

	@GraphQLField(description = "The object entry folder's main title/name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String title;

	@JsonIgnore
	private Supplier<String> _titleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A write-only property that specifies the object entry folder's default permissions."
	)
	@JsonGetter("viewableBy")
	@Valid
	public ViewableBy getViewableBy() {
		if (_viewableBySupplier != null) {
			viewableBy = _viewableBySupplier.get();

			_viewableBySupplier = null;
		}

		return viewableBy;
	}

	@JsonIgnore
	public String getViewableByAsString() {
		ViewableBy viewableBy = getViewableBy();

		if (viewableBy == null) {
			return null;
		}

		return viewableBy.toString();
	}

	public void setViewableBy(ViewableBy viewableBy) {
		this.viewableBy = viewableBy;

		_viewableBySupplier = null;
	}

	@JsonIgnore
	public void setViewableBy(
		UnsafeSupplier<ViewableBy, Exception> viewableByUnsafeSupplier) {

		_viewableBySupplier = () -> {
			try {
				return viewableByUnsafeSupplier.get();
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
		description = "A write-only property that specifies the object entry folder's default permissions."
	)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected ViewableBy viewableBy;

	@JsonIgnore
	private Supplier<ViewableBy> _viewableBySupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectEntryFolder)) {
			return false;
		}

		ObjectEntryFolder objectEntryFolder = (ObjectEntryFolder)object;

		return Objects.equals(toString(), objectEntryFolder.toString());
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

		Creator creator = getCreator();

		if (creator != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(creator);
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

		String description = getDescription();

		if (description != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(description));

			sb.append("\"");
		}

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

			sb.append("\"");
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		String label = getLabel();

		if (label != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(label));

			sb.append("\"");
		}

		Map<String, String> label_i18n = getLabel_i18n();

		if (label_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label_i18n\": ");

			sb.append(_toJSON(label_i18n));
		}

		Integer numberOfObjectEntries = getNumberOfObjectEntries();

		if (numberOfObjectEntries != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfObjectEntries\": ");

			sb.append(numberOfObjectEntries);
		}

		Integer numberOfObjectEntryFolders = getNumberOfObjectEntryFolders();

		if (numberOfObjectEntryFolders != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfObjectEntryFolders\": ");

			sb.append(numberOfObjectEntryFolders);
		}

		ParentObjectEntryFolderBrief parentObjectEntryFolderBrief =
			getParentObjectEntryFolderBrief();

		if (parentObjectEntryFolderBrief != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentObjectEntryFolderBrief\": ");

			sb.append(String.valueOf(parentObjectEntryFolderBrief));
		}

		String parentObjectEntryFolderExternalReferenceCode =
			getParentObjectEntryFolderExternalReferenceCode();

		if (parentObjectEntryFolderExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentObjectEntryFolderExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(parentObjectEntryFolderExternalReferenceCode));

			sb.append("\"");
		}

		Long parentObjectEntryFolderId = getParentObjectEntryFolderId();

		if (parentObjectEntryFolderId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentObjectEntryFolderId\": ");

			sb.append(parentObjectEntryFolderId);
		}

		com.liferay.portal.vulcan.permission.Permission[] permissions =
			getPermissions();

		if (permissions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < permissions.length; i++) {
				sb.append(permissions[i]);

				if ((i + 1) < permissions.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Creator removedBy = getRemovedBy();

		if (removedBy != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"removedBy\": ");

			sb.append(removedBy);
		}

		Date removedDate = getRemovedDate();

		if (removedDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"removedDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(removedDate));

			sb.append("\"");
		}

		Long scopeId = getScopeId();

		if (scopeId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scopeId\": ");

			sb.append(scopeId);
		}

		String scopeKey = getScopeKey();

		if (scopeKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scopeKey\": ");

			sb.append("\"");

			sb.append(_escape(scopeKey));

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

		ViewableBy viewableBy = getViewableBy();

		if (viewableBy != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewableBy\": ");

			sb.append("\"");

			sb.append(viewableBy);

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.object.dto.v1_0.ObjectEntryFolder",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("ViewableBy")
	public static enum ViewableBy {

		ANYONE("Anyone"), MEMBERS("Members"), OWNER("Owner");

		@JsonCreator
		public static ViewableBy create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (ViewableBy viewableBy : values()) {
				if (Objects.equals(viewableBy.getValue(), value)) {
					return viewableBy;
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

		private ViewableBy(String value) {
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