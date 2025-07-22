/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.headless.delivery.dto.v1_0.Creator;
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

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName("ObjectEntry")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ObjectEntry")
public class ObjectEntry implements Serializable {

	public static ObjectEntry toDTO(String json) {
		return ObjectMapperUtil.readValue(ObjectEntry.class, json);
	}

	public static ObjectEntry unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ObjectEntry.class, json);
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

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Optional field with the audit events associated with this object entry, can be embedded with nestedFields"
	)
	@Valid
	public AuditEvent[] getAuditEvents() {
		if (_auditEventsSupplier != null) {
			auditEvents = _auditEventsSupplier.get();

			_auditEventsSupplier = null;
		}

		return auditEvents;
	}

	public void setAuditEvents(AuditEvent[] auditEvents) {
		this.auditEvents = auditEvents;

		_auditEventsSupplier = null;
	}

	@JsonIgnore
	public void setAuditEvents(
		UnsafeSupplier<AuditEvent[], Exception> auditEventsUnsafeSupplier) {

		_auditEventsSupplier = () -> {
			try {
				return auditEventsUnsafeSupplier.get();
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
		description = "Optional field with the audit events associated with this object entry, can be embedded with nestedFields"
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected AuditEvent[] auditEvents;

	@JsonIgnore
	private Supplier<AuditEvent[]> _auditEventsSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

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
	public Date getDateDeleted() {
		if (_dateDeletedSupplier != null) {
			dateDeleted = _dateDeletedSupplier.get();

			_dateDeletedSupplier = null;
		}

		return dateDeleted;
	}

	public void setDateDeleted(Date dateDeleted) {
		this.dateDeleted = dateDeleted;

		_dateDeletedSupplier = null;
	}

	@JsonIgnore
	public void setDateDeleted(
		UnsafeSupplier<Date, Exception> dateDeletedUnsafeSupplier) {

		_dateDeletedSupplier = () -> {
			try {
				return dateDeletedUnsafeSupplier.get();
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
	protected Date dateDeleted;

	@JsonIgnore
	private Supplier<Date> _dateDeletedSupplier;

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
	public String getDefaultLanguageId() {
		if (_defaultLanguageIdSupplier != null) {
			defaultLanguageId = _defaultLanguageIdSupplier.get();

			_defaultLanguageIdSupplier = null;
		}

		return defaultLanguageId;
	}

	public void setDefaultLanguageId(String defaultLanguageId) {
		this.defaultLanguageId = defaultLanguageId;

		_defaultLanguageIdSupplier = null;
	}

	@JsonIgnore
	public void setDefaultLanguageId(
		UnsafeSupplier<String, Exception> defaultLanguageIdUnsafeSupplier) {

		_defaultLanguageIdSupplier = () -> {
			try {
				return defaultLanguageIdUnsafeSupplier.get();
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
	protected String defaultLanguageId;

	@JsonIgnore
	private Supplier<String> _defaultLanguageIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Creator getDeletedBy() {
		if (_deletedBySupplier != null) {
			deletedBy = _deletedBySupplier.get();

			_deletedBySupplier = null;
		}

		return deletedBy;
	}

	public void setDeletedBy(Creator deletedBy) {
		this.deletedBy = deletedBy;

		_deletedBySupplier = null;
	}

	@JsonIgnore
	public void setDeletedBy(
		UnsafeSupplier<Creator, Exception> deletedByUnsafeSupplier) {

		_deletedBySupplier = () -> {
			try {
				return deletedByUnsafeSupplier.get();
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
	protected Creator deletedBy;

	@JsonIgnore
	private Supplier<Creator> _deletedBySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getDisplayDate() {
		if (_displayDateSupplier != null) {
			displayDate = _displayDateSupplier.get();

			_displayDateSupplier = null;
		}

		return displayDate;
	}

	public void setDisplayDate(Date displayDate) {
		this.displayDate = displayDate;

		_displayDateSupplier = null;
	}

	@JsonIgnore
	public void setDisplayDate(
		UnsafeSupplier<Date, Exception> displayDateUnsafeSupplier) {

		_displayDateSupplier = () -> {
			try {
				return displayDateUnsafeSupplier.get();
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
	protected Date displayDate;

	@JsonIgnore
	private Supplier<Date> _displayDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getExpirationDate() {
		if (_expirationDateSupplier != null) {
			expirationDate = _expirationDateSupplier.get();

			_expirationDateSupplier = null;
		}

		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;

		_expirationDateSupplier = null;
	}

	@JsonIgnore
	public void setExpirationDate(
		UnsafeSupplier<Date, Exception> expirationDateUnsafeSupplier) {

		_expirationDateSupplier = () -> {
			try {
				return expirationDateUnsafeSupplier.get();
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
	protected Date expirationDate;

	@JsonIgnore
	private Supplier<Date> _expirationDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A relative URL to the page's rendered content."
	)
	public String getFriendlyUrlPath() {
		if (_friendlyUrlPathSupplier != null) {
			friendlyUrlPath = _friendlyUrlPathSupplier.get();

			_friendlyUrlPathSupplier = null;
		}

		return friendlyUrlPath;
	}

	public void setFriendlyUrlPath(String friendlyUrlPath) {
		this.friendlyUrlPath = friendlyUrlPath;

		_friendlyUrlPathSupplier = null;
	}

	@JsonIgnore
	public void setFriendlyUrlPath(
		UnsafeSupplier<String, Exception> friendlyUrlPathUnsafeSupplier) {

		_friendlyUrlPathSupplier = () -> {
			try {
				return friendlyUrlPathUnsafeSupplier.get();
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
		description = "A relative URL to the page's rendered content."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String friendlyUrlPath;

	@JsonIgnore
	private Supplier<String> _friendlyUrlPathSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized relative URLs to the page's rendered content."
	)
	@Valid
	public Map<String, String> getFriendlyUrlPath_i18n() {
		if (_friendlyUrlPath_i18nSupplier != null) {
			friendlyUrlPath_i18n = _friendlyUrlPath_i18nSupplier.get();

			_friendlyUrlPath_i18nSupplier = null;
		}

		return friendlyUrlPath_i18n;
	}

	public void setFriendlyUrlPath_i18n(
		Map<String, String> friendlyUrlPath_i18n) {

		this.friendlyUrlPath_i18n = friendlyUrlPath_i18n;

		_friendlyUrlPath_i18nSupplier = null;
	}

	@JsonIgnore
	public void setFriendlyUrlPath_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			friendlyUrlPath_i18nUnsafeSupplier) {

		_friendlyUrlPath_i18nSupplier = () -> {
			try {
				return friendlyUrlPath_i18nUnsafeSupplier.get();
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
		description = "The localized relative URLs to the page's rendered content."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> friendlyUrlPath_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _friendlyUrlPath_i18nSupplier;

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

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of keywords describing the object entry."
	)
	public String[] getKeywords() {
		if (_keywordsSupplier != null) {
			keywords = _keywordsSupplier.get();

			_keywordsSupplier = null;
		}

		return keywords;
	}

	public void setKeywords(String[] keywords) {
		this.keywords = keywords;

		_keywordsSupplier = null;
	}

	@JsonIgnore
	public void setKeywords(
		UnsafeSupplier<String[], Exception> keywordsUnsafeSupplier) {

		_keywordsSupplier = () -> {
			try {
				return keywordsUnsafeSupplier.get();
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
		description = "A list of keywords describing the object entry."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] keywords;

	@JsonIgnore
	private Supplier<String[]> _keywordsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getObjectEntryFolderExternalReferenceCode() {
		if (_objectEntryFolderExternalReferenceCodeSupplier != null) {
			objectEntryFolderExternalReferenceCode =
				_objectEntryFolderExternalReferenceCodeSupplier.get();

			_objectEntryFolderExternalReferenceCodeSupplier = null;
		}

		return objectEntryFolderExternalReferenceCode;
	}

	public void setObjectEntryFolderExternalReferenceCode(
		String objectEntryFolderExternalReferenceCode) {

		this.objectEntryFolderExternalReferenceCode =
			objectEntryFolderExternalReferenceCode;

		_objectEntryFolderExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setObjectEntryFolderExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			objectEntryFolderExternalReferenceCodeUnsafeSupplier) {

		_objectEntryFolderExternalReferenceCodeSupplier = () -> {
			try {
				return objectEntryFolderExternalReferenceCodeUnsafeSupplier.
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String objectEntryFolderExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _objectEntryFolderExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getObjectEntryFolderId() {
		if (_objectEntryFolderIdSupplier != null) {
			objectEntryFolderId = _objectEntryFolderIdSupplier.get();

			_objectEntryFolderIdSupplier = null;
		}

		return objectEntryFolderId;
	}

	public void setObjectEntryFolderId(Long objectEntryFolderId) {
		this.objectEntryFolderId = objectEntryFolderId;

		_objectEntryFolderIdSupplier = null;
	}

	@JsonIgnore
	public void setObjectEntryFolderId(
		UnsafeSupplier<Long, Exception> objectEntryFolderIdUnsafeSupplier) {

		_objectEntryFolderIdSupplier = () -> {
			try {
				return objectEntryFolderIdUnsafeSupplier.get();
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
	protected Long objectEntryFolderId;

	@JsonIgnore
	private Supplier<Long> _objectEntryFolderIdSupplier;

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
	public Map<String, Object> getProperties() {
		if (properties == null) {
			return null;
		}

		properties.replaceAll(
			(key, value) -> {
				if (!(value instanceof UnsafeSupplier<?, ?>)) {
					return value;
				}

				try {
					UnsafeSupplier<?, ?> unsafeSupplier =
						(UnsafeSupplier<?, ?>)value;

					return unsafeSupplier.get();
				}
				catch (Throwable throwable) {
					throw new RuntimeException(throwable);
				}
			});

		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		if (properties == null) {
			this.properties = null;

			return;
		}

		Map<String, Object> propertiesMap = new LinkedHashMap<>(properties);

		propertiesMap.replaceAll(
			(key, value) -> {
				if (!(value instanceof UnsafeSupplier<?, ?>)) {
					return value;
				}

				return new CachedUnsafeSupplier((UnsafeSupplier<?, ?>)value);
			});

		this.properties = Collections.synchronizedMap(propertiesMap);
	}

	@JsonIgnore
	public void setProperties(
		UnsafeSupplier<Map<String, Object>, Exception>
			propertiesUnsafeSupplier) {

		if (propertiesUnsafeSupplier == null) {
			setProperties((Map<String, Object>)null);

			return;
		}

		try {
			setProperties(propertiesUnsafeSupplier.get());
		}
		catch (RuntimeException runtimeException) {
			throw runtimeException;
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@GraphQLField
	@JsonAnyGetter
	@JsonAnySetter
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, Object> properties = Collections.synchronizedMap(
		new LinkedHashMap<>());

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getReviewDate() {
		if (_reviewDateSupplier != null) {
			reviewDate = _reviewDateSupplier.get();

			_reviewDateSupplier = null;
		}

		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;

		_reviewDateSupplier = null;
	}

	@JsonIgnore
	public void setReviewDate(
		UnsafeSupplier<Date, Exception> reviewDateUnsafeSupplier) {

		_reviewDateSupplier = () -> {
			try {
				return reviewDateUnsafeSupplier.get();
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
	protected Date reviewDate;

	@JsonIgnore
	private Supplier<Date> _reviewDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long scopeId;

	@JsonIgnore
	private Supplier<Long> _scopeIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
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

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public SystemProperties getSystemProperties() {
		if (_systemPropertiesSupplier != null) {
			systemProperties = _systemPropertiesSupplier.get();

			_systemPropertiesSupplier = null;
		}

		return systemProperties;
	}

	public void setSystemProperties(SystemProperties systemProperties) {
		this.systemProperties = systemProperties;

		_systemPropertiesSupplier = null;
	}

	@JsonIgnore
	public void setSystemProperties(
		UnsafeSupplier<SystemProperties, Exception>
			systemPropertiesUnsafeSupplier) {

		_systemPropertiesSupplier = () -> {
			try {
				return systemPropertiesUnsafeSupplier.get();
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
	protected SystemProperties systemProperties;

	@JsonIgnore
	private Supplier<SystemProperties> _systemPropertiesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The categories associated with this object entry."
	)
	@Valid
	public TaxonomyCategoryBrief[] getTaxonomyCategoryBriefs() {
		if (_taxonomyCategoryBriefsSupplier != null) {
			taxonomyCategoryBriefs = _taxonomyCategoryBriefsSupplier.get();

			_taxonomyCategoryBriefsSupplier = null;
		}

		return taxonomyCategoryBriefs;
	}

	public void setTaxonomyCategoryBriefs(
		TaxonomyCategoryBrief[] taxonomyCategoryBriefs) {

		this.taxonomyCategoryBriefs = taxonomyCategoryBriefs;

		_taxonomyCategoryBriefsSupplier = null;
	}

	@JsonIgnore
	public void setTaxonomyCategoryBriefs(
		UnsafeSupplier<TaxonomyCategoryBrief[], Exception>
			taxonomyCategoryBriefsUnsafeSupplier) {

		_taxonomyCategoryBriefsSupplier = () -> {
			try {
				return taxonomyCategoryBriefsUnsafeSupplier.get();
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
		description = "The categories associated with this object entry."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected TaxonomyCategoryBrief[] taxonomyCategoryBriefs;

	@JsonIgnore
	private Supplier<TaxonomyCategoryBrief[]> _taxonomyCategoryBriefsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A write-only field that adds `TaxonomyCategory` instances to the object entry."
	)
	public Long[] getTaxonomyCategoryIds() {
		if (_taxonomyCategoryIdsSupplier != null) {
			taxonomyCategoryIds = _taxonomyCategoryIdsSupplier.get();

			_taxonomyCategoryIdsSupplier = null;
		}

		return taxonomyCategoryIds;
	}

	public void setTaxonomyCategoryIds(Long[] taxonomyCategoryIds) {
		this.taxonomyCategoryIds = taxonomyCategoryIds;

		_taxonomyCategoryIdsSupplier = null;
	}

	@JsonIgnore
	public void setTaxonomyCategoryIds(
		UnsafeSupplier<Long[], Exception> taxonomyCategoryIdsUnsafeSupplier) {

		_taxonomyCategoryIdsSupplier = () -> {
			try {
				return taxonomyCategoryIdsUnsafeSupplier.get();
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
		description = "A write-only field that adds `TaxonomyCategory` instances to the object entry."
	)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected Long[] taxonomyCategoryIds;

	@JsonIgnore
	private Supplier<Long[]> _taxonomyCategoryIdsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectEntry)) {
			return false;
		}

		ObjectEntry objectEntry = (ObjectEntry)object;

		return Objects.equals(toString(), objectEntry.toString());
	}

	public Object getPropertyValue(String propertyName) {
		if (Objects.equals(propertyName, "actions")) {
			return getActions();
		}
		else if (Objects.equals(propertyName, "auditEvents")) {
			return getAuditEvents();
		}
		else if (Objects.equals(propertyName, "creator")) {
			return getCreator();
		}
		else if (Objects.equals(propertyName, "dateCreated")) {
			return getDateCreated();
		}
		else if (Objects.equals(propertyName, "dateDeleted")) {
			return getDateDeleted();
		}
		else if (Objects.equals(propertyName, "dateModified")) {
			return getDateModified();
		}
		else if (Objects.equals(propertyName, "defaultLanguageId")) {
			return getDefaultLanguageId();
		}
		else if (Objects.equals(propertyName, "deletedBy")) {
			return getDeletedBy();
		}
		else if (Objects.equals(propertyName, "displayDate")) {
			return getDisplayDate();
		}
		else if (Objects.equals(propertyName, "expirationDate")) {
			return getExpirationDate();
		}
		else if (Objects.equals(propertyName, "externalReferenceCode")) {
			return getExternalReferenceCode();
		}
		else if (Objects.equals(propertyName, "friendlyUrlPath")) {
			return getFriendlyUrlPath();
		}
		else if (Objects.equals(propertyName, "friendlyUrlPath_i18n")) {
			return getFriendlyUrlPath_i18n();
		}
		else if (Objects.equals(propertyName, "id")) {
			return getId();
		}
		else if (Objects.equals(propertyName, "keywords")) {
			return getKeywords();
		}
		else if (Objects.equals(
					propertyName, "objectEntryFolderExternalReferenceCode")) {

			return getObjectEntryFolderExternalReferenceCode();
		}
		else if (Objects.equals(propertyName, "objectEntryFolderId")) {
			return getObjectEntryFolderId();
		}
		else if (Objects.equals(propertyName, "permissions")) {
			return getPermissions();
		}
		else if (Objects.equals(propertyName, "reviewDate")) {
			return getReviewDate();
		}
		else if (Objects.equals(propertyName, "scopeId")) {
			return getScopeId();
		}
		else if (Objects.equals(propertyName, "scopeKey")) {
			return getScopeKey();
		}
		else if (Objects.equals(propertyName, "status")) {
			return getStatus();
		}
		else if (Objects.equals(propertyName, "systemProperties")) {
			return getSystemProperties();
		}
		else if (Objects.equals(propertyName, "taxonomyCategoryBriefs")) {
			return getTaxonomyCategoryBriefs();
		}
		else if (Objects.equals(propertyName, "taxonomyCategoryIds")) {
			return getTaxonomyCategoryIds();
		}
		else {
			if (properties.containsKey(propertyName)) {
				Object value = properties.get(propertyName);

				if (!(value instanceof UnsafeSupplier<?, ?>)) {
					return value;
				}

				UnsafeSupplier<?, ?> unsafeSupplier =
					(UnsafeSupplier<?, ?>)value;

				try {
					return unsafeSupplier.get();
				}
				catch (Throwable throwable) {
					throw new RuntimeException(throwable);
				}
			}
		}

		return null;
	}

	private final class CachedUnsafeSupplier<T, E extends Throwable>
		implements UnsafeSupplier<T, E> {

		public CachedUnsafeSupplier(UnsafeSupplier<T, E> unsafeSupplier) {
			_unsafeSupplier = unsafeSupplier;
		}

		public T get() throws E {
			if (_set) {
				return _value;
			}

			synchronized (_unsafeSupplier) {
				_value = _unsafeSupplier.get();

				_set = true;
			}

			return _value;
		}

		private boolean _set;
		private final UnsafeSupplier<T, E> _unsafeSupplier;
		private T _value;

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

		AuditEvent[] auditEvents = getAuditEvents();

		if (auditEvents != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"auditEvents\": ");

			sb.append("[");

			for (int i = 0; i < auditEvents.length; i++) {
				sb.append(String.valueOf(auditEvents[i]));

				if ((i + 1) < auditEvents.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		Date dateDeleted = getDateDeleted();

		if (dateDeleted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateDeleted\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateDeleted));

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

		String defaultLanguageId = getDefaultLanguageId();

		if (defaultLanguageId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultLanguageId\": ");

			sb.append("\"");

			sb.append(_escape(defaultLanguageId));

			sb.append("\"");
		}

		Creator deletedBy = getDeletedBy();

		if (deletedBy != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletedBy\": ");

			sb.append(deletedBy);
		}

		Date displayDate = getDisplayDate();

		if (displayDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(displayDate));

			sb.append("\"");
		}

		Date expirationDate = getExpirationDate();

		if (expirationDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expirationDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(expirationDate));

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

		String friendlyUrlPath = getFriendlyUrlPath();

		if (friendlyUrlPath != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlPath\": ");

			sb.append("\"");

			sb.append(_escape(friendlyUrlPath));

			sb.append("\"");
		}

		Map<String, String> friendlyUrlPath_i18n = getFriendlyUrlPath_i18n();

		if (friendlyUrlPath_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlPath_i18n\": ");

			sb.append(_toJSON(friendlyUrlPath_i18n));
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		String[] keywords = getKeywords();

		if (keywords != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywords\": ");

			sb.append("[");

			for (int i = 0; i < keywords.length; i++) {
				sb.append("\"");

				sb.append(_escape(keywords[i]));

				sb.append("\"");

				if ((i + 1) < keywords.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String objectEntryFolderExternalReferenceCode =
			getObjectEntryFolderExternalReferenceCode();

		if (objectEntryFolderExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectEntryFolderExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(objectEntryFolderExternalReferenceCode));

			sb.append("\"");
		}

		Long objectEntryFolderId = getObjectEntryFolderId();

		if (objectEntryFolderId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectEntryFolderId\": ");

			sb.append(objectEntryFolderId);
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

		Map<String, Object> properties = getProperties();

		if (properties != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"properties\": ");

			sb.append(_toJSON(properties));
		}

		Date reviewDate = getReviewDate();

		if (reviewDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reviewDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(reviewDate));

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

		SystemProperties systemProperties = getSystemProperties();

		if (systemProperties != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"systemProperties\": ");

			sb.append(String.valueOf(systemProperties));
		}

		TaxonomyCategoryBrief[] taxonomyCategoryBriefs =
			getTaxonomyCategoryBriefs();

		if (taxonomyCategoryBriefs != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryBriefs\": ");

			sb.append("[");

			for (int i = 0; i < taxonomyCategoryBriefs.length; i++) {
				sb.append(String.valueOf(taxonomyCategoryBriefs[i]));

				if ((i + 1) < taxonomyCategoryBriefs.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Long[] taxonomyCategoryIds = getTaxonomyCategoryIds();

		if (taxonomyCategoryIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryIds\": ");

			sb.append("[");

			for (int i = 0; i < taxonomyCategoryIds.length; i++) {
				sb.append(taxonomyCategoryIds[i]);

				if ((i + 1) < taxonomyCategoryIds.length) {
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
		defaultValue = "com.liferay.object.rest.dto.v1_0.ObjectEntry",
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