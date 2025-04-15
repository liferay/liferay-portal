/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.dto.v1_0;

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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName("ObjectDefinition")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ObjectDefinition")
public class ObjectDefinition implements Serializable {

	public static ObjectDefinition toDTO(String json) {
		return ObjectMapperUtil.readValue(ObjectDefinition.class, json);
	}

	public static ObjectDefinition unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ObjectDefinition.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getAccountEntryRestricted() {
		if (_accountEntryRestrictedSupplier != null) {
			accountEntryRestricted = _accountEntryRestrictedSupplier.get();

			_accountEntryRestrictedSupplier = null;
		}

		return accountEntryRestricted;
	}

	public void setAccountEntryRestricted(Boolean accountEntryRestricted) {
		this.accountEntryRestricted = accountEntryRestricted;

		_accountEntryRestrictedSupplier = null;
	}

	@JsonIgnore
	public void setAccountEntryRestricted(
		UnsafeSupplier<Boolean, Exception>
			accountEntryRestrictedUnsafeSupplier) {

		_accountEntryRestrictedSupplier = () -> {
			try {
				return accountEntryRestrictedUnsafeSupplier.get();
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
	protected Boolean accountEntryRestricted;

	@JsonIgnore
	private Supplier<Boolean> _accountEntryRestrictedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAccountEntryRestrictedObjectFieldName() {
		if (_accountEntryRestrictedObjectFieldNameSupplier != null) {
			accountEntryRestrictedObjectFieldName =
				_accountEntryRestrictedObjectFieldNameSupplier.get();

			_accountEntryRestrictedObjectFieldNameSupplier = null;
		}

		return accountEntryRestrictedObjectFieldName;
	}

	public void setAccountEntryRestrictedObjectFieldName(
		String accountEntryRestrictedObjectFieldName) {

		this.accountEntryRestrictedObjectFieldName =
			accountEntryRestrictedObjectFieldName;

		_accountEntryRestrictedObjectFieldNameSupplier = null;
	}

	@JsonIgnore
	public void setAccountEntryRestrictedObjectFieldName(
		UnsafeSupplier<String, Exception>
			accountEntryRestrictedObjectFieldNameUnsafeSupplier) {

		_accountEntryRestrictedObjectFieldNameSupplier = () -> {
			try {
				return accountEntryRestrictedObjectFieldNameUnsafeSupplier.
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
	protected String accountEntryRestrictedObjectFieldName;

	@JsonIgnore
	private Supplier<String> _accountEntryRestrictedObjectFieldNameSupplier;

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
	public Boolean getEnableCategorization() {
		if (_enableCategorizationSupplier != null) {
			enableCategorization = _enableCategorizationSupplier.get();

			_enableCategorizationSupplier = null;
		}

		return enableCategorization;
	}

	public void setEnableCategorization(Boolean enableCategorization) {
		this.enableCategorization = enableCategorization;

		_enableCategorizationSupplier = null;
	}

	@JsonIgnore
	public void setEnableCategorization(
		UnsafeSupplier<Boolean, Exception> enableCategorizationUnsafeSupplier) {

		_enableCategorizationSupplier = () -> {
			try {
				return enableCategorizationUnsafeSupplier.get();
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
	protected Boolean enableCategorization;

	@JsonIgnore
	private Supplier<Boolean> _enableCategorizationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getEnableComments() {
		if (_enableCommentsSupplier != null) {
			enableComments = _enableCommentsSupplier.get();

			_enableCommentsSupplier = null;
		}

		return enableComments;
	}

	public void setEnableComments(Boolean enableComments) {
		this.enableComments = enableComments;

		_enableCommentsSupplier = null;
	}

	@JsonIgnore
	public void setEnableComments(
		UnsafeSupplier<Boolean, Exception> enableCommentsUnsafeSupplier) {

		_enableCommentsSupplier = () -> {
			try {
				return enableCommentsUnsafeSupplier.get();
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
	protected Boolean enableComments;

	@JsonIgnore
	private Supplier<Boolean> _enableCommentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getEnableFriendlyURLCustomization() {
		if (_enableFriendlyURLCustomizationSupplier != null) {
			enableFriendlyURLCustomization =
				_enableFriendlyURLCustomizationSupplier.get();

			_enableFriendlyURLCustomizationSupplier = null;
		}

		return enableFriendlyURLCustomization;
	}

	public void setEnableFriendlyURLCustomization(
		Boolean enableFriendlyURLCustomization) {

		this.enableFriendlyURLCustomization = enableFriendlyURLCustomization;

		_enableFriendlyURLCustomizationSupplier = null;
	}

	@JsonIgnore
	public void setEnableFriendlyURLCustomization(
		UnsafeSupplier<Boolean, Exception>
			enableFriendlyURLCustomizationUnsafeSupplier) {

		_enableFriendlyURLCustomizationSupplier = () -> {
			try {
				return enableFriendlyURLCustomizationUnsafeSupplier.get();
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
	protected Boolean enableFriendlyURLCustomization;

	@JsonIgnore
	private Supplier<Boolean> _enableFriendlyURLCustomizationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getEnableIndexSearch() {
		if (_enableIndexSearchSupplier != null) {
			enableIndexSearch = _enableIndexSearchSupplier.get();

			_enableIndexSearchSupplier = null;
		}

		return enableIndexSearch;
	}

	public void setEnableIndexSearch(Boolean enableIndexSearch) {
		this.enableIndexSearch = enableIndexSearch;

		_enableIndexSearchSupplier = null;
	}

	@JsonIgnore
	public void setEnableIndexSearch(
		UnsafeSupplier<Boolean, Exception> enableIndexSearchUnsafeSupplier) {

		_enableIndexSearchSupplier = () -> {
			try {
				return enableIndexSearchUnsafeSupplier.get();
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
	protected Boolean enableIndexSearch;

	@JsonIgnore
	private Supplier<Boolean> _enableIndexSearchSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getEnableLocalization() {
		if (_enableLocalizationSupplier != null) {
			enableLocalization = _enableLocalizationSupplier.get();

			_enableLocalizationSupplier = null;
		}

		return enableLocalization;
	}

	public void setEnableLocalization(Boolean enableLocalization) {
		this.enableLocalization = enableLocalization;

		_enableLocalizationSupplier = null;
	}

	@JsonIgnore
	public void setEnableLocalization(
		UnsafeSupplier<Boolean, Exception> enableLocalizationUnsafeSupplier) {

		_enableLocalizationSupplier = () -> {
			try {
				return enableLocalizationUnsafeSupplier.get();
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
	protected Boolean enableLocalization;

	@JsonIgnore
	private Supplier<Boolean> _enableLocalizationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getEnableObjectEntryDraft() {
		if (_enableObjectEntryDraftSupplier != null) {
			enableObjectEntryDraft = _enableObjectEntryDraftSupplier.get();

			_enableObjectEntryDraftSupplier = null;
		}

		return enableObjectEntryDraft;
	}

	public void setEnableObjectEntryDraft(Boolean enableObjectEntryDraft) {
		this.enableObjectEntryDraft = enableObjectEntryDraft;

		_enableObjectEntryDraftSupplier = null;
	}

	@JsonIgnore
	public void setEnableObjectEntryDraft(
		UnsafeSupplier<Boolean, Exception>
			enableObjectEntryDraftUnsafeSupplier) {

		_enableObjectEntryDraftSupplier = () -> {
			try {
				return enableObjectEntryDraftUnsafeSupplier.get();
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
	protected Boolean enableObjectEntryDraft;

	@JsonIgnore
	private Supplier<Boolean> _enableObjectEntryDraftSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getEnableObjectEntryHistory() {
		if (_enableObjectEntryHistorySupplier != null) {
			enableObjectEntryHistory = _enableObjectEntryHistorySupplier.get();

			_enableObjectEntryHistorySupplier = null;
		}

		return enableObjectEntryHistory;
	}

	public void setEnableObjectEntryHistory(Boolean enableObjectEntryHistory) {
		this.enableObjectEntryHistory = enableObjectEntryHistory;

		_enableObjectEntryHistorySupplier = null;
	}

	@JsonIgnore
	public void setEnableObjectEntryHistory(
		UnsafeSupplier<Boolean, Exception>
			enableObjectEntryHistoryUnsafeSupplier) {

		_enableObjectEntryHistorySupplier = () -> {
			try {
				return enableObjectEntryHistoryUnsafeSupplier.get();
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
	protected Boolean enableObjectEntryHistory;

	@JsonIgnore
	private Supplier<Boolean> _enableObjectEntryHistorySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getEnableObjectEntryVersioning() {
		if (_enableObjectEntryVersioningSupplier != null) {
			enableObjectEntryVersioning =
				_enableObjectEntryVersioningSupplier.get();

			_enableObjectEntryVersioningSupplier = null;
		}

		return enableObjectEntryVersioning;
	}

	public void setEnableObjectEntryVersioning(
		Boolean enableObjectEntryVersioning) {

		this.enableObjectEntryVersioning = enableObjectEntryVersioning;

		_enableObjectEntryVersioningSupplier = null;
	}

	@JsonIgnore
	public void setEnableObjectEntryVersioning(
		UnsafeSupplier<Boolean, Exception>
			enableObjectEntryVersioningUnsafeSupplier) {

		_enableObjectEntryVersioningSupplier = () -> {
			try {
				return enableObjectEntryVersioningUnsafeSupplier.get();
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
	protected Boolean enableObjectEntryVersioning;

	@JsonIgnore
	private Supplier<Boolean> _enableObjectEntryVersioningSupplier;

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
	@Valid
	public Map<String, String> getLabel() {
		if (_labelSupplier != null) {
			label = _labelSupplier.get();

			_labelSupplier = null;
		}

		return label;
	}

	public void setLabel(Map<String, String> label) {
		this.label = label;

		_labelSupplier = null;
	}

	@JsonIgnore
	public void setLabel(
		UnsafeSupplier<Map<String, String>, Exception> labelUnsafeSupplier) {

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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> label;

	@JsonIgnore
	private Supplier<Map<String, String>> _labelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getModifiable() {
		if (_modifiableSupplier != null) {
			modifiable = _modifiableSupplier.get();

			_modifiableSupplier = null;
		}

		return modifiable;
	}

	public void setModifiable(Boolean modifiable) {
		this.modifiable = modifiable;

		_modifiableSupplier = null;
	}

	@JsonIgnore
	public void setModifiable(
		UnsafeSupplier<Boolean, Exception> modifiableUnsafeSupplier) {

		_modifiableSupplier = () -> {
			try {
				return modifiableUnsafeSupplier.get();
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
	protected Boolean modifiable;

	@JsonIgnore
	private Supplier<Boolean> _modifiableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ObjectAction[] getObjectActions() {
		if (_objectActionsSupplier != null) {
			objectActions = _objectActionsSupplier.get();

			_objectActionsSupplier = null;
		}

		return objectActions;
	}

	public void setObjectActions(ObjectAction[] objectActions) {
		this.objectActions = objectActions;

		_objectActionsSupplier = null;
	}

	@JsonIgnore
	public void setObjectActions(
		UnsafeSupplier<ObjectAction[], Exception> objectActionsUnsafeSupplier) {

		_objectActionsSupplier = () -> {
			try {
				return objectActionsUnsafeSupplier.get();
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
	protected ObjectAction[] objectActions;

	@JsonIgnore
	private Supplier<ObjectAction[]> _objectActionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ObjectDefinitionSetting[] getObjectDefinitionSettings() {
		if (_objectDefinitionSettingsSupplier != null) {
			objectDefinitionSettings = _objectDefinitionSettingsSupplier.get();

			_objectDefinitionSettingsSupplier = null;
		}

		return objectDefinitionSettings;
	}

	public void setObjectDefinitionSettings(
		ObjectDefinitionSetting[] objectDefinitionSettings) {

		this.objectDefinitionSettings = objectDefinitionSettings;

		_objectDefinitionSettingsSupplier = null;
	}

	@JsonIgnore
	public void setObjectDefinitionSettings(
		UnsafeSupplier<ObjectDefinitionSetting[], Exception>
			objectDefinitionSettingsUnsafeSupplier) {

		_objectDefinitionSettingsSupplier = () -> {
			try {
				return objectDefinitionSettingsUnsafeSupplier.get();
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
	protected ObjectDefinitionSetting[] objectDefinitionSettings;

	@JsonIgnore
	private Supplier<ObjectDefinitionSetting[]>
		_objectDefinitionSettingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ObjectField[] getObjectFields() {
		if (_objectFieldsSupplier != null) {
			objectFields = _objectFieldsSupplier.get();

			_objectFieldsSupplier = null;
		}

		return objectFields;
	}

	public void setObjectFields(ObjectField[] objectFields) {
		this.objectFields = objectFields;

		_objectFieldsSupplier = null;
	}

	@JsonIgnore
	public void setObjectFields(
		UnsafeSupplier<ObjectField[], Exception> objectFieldsUnsafeSupplier) {

		_objectFieldsSupplier = () -> {
			try {
				return objectFieldsUnsafeSupplier.get();
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
	protected ObjectField[] objectFields;

	@JsonIgnore
	private Supplier<ObjectField[]> _objectFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getObjectFolderExternalReferenceCode() {
		if (_objectFolderExternalReferenceCodeSupplier != null) {
			objectFolderExternalReferenceCode =
				_objectFolderExternalReferenceCodeSupplier.get();

			_objectFolderExternalReferenceCodeSupplier = null;
		}

		return objectFolderExternalReferenceCode;
	}

	public void setObjectFolderExternalReferenceCode(
		String objectFolderExternalReferenceCode) {

		this.objectFolderExternalReferenceCode =
			objectFolderExternalReferenceCode;

		_objectFolderExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setObjectFolderExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			objectFolderExternalReferenceCodeUnsafeSupplier) {

		_objectFolderExternalReferenceCodeSupplier = () -> {
			try {
				return objectFolderExternalReferenceCodeUnsafeSupplier.get();
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
	protected String objectFolderExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _objectFolderExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ObjectLayout[] getObjectLayouts() {
		if (_objectLayoutsSupplier != null) {
			objectLayouts = _objectLayoutsSupplier.get();

			_objectLayoutsSupplier = null;
		}

		return objectLayouts;
	}

	public void setObjectLayouts(ObjectLayout[] objectLayouts) {
		this.objectLayouts = objectLayouts;

		_objectLayoutsSupplier = null;
	}

	@JsonIgnore
	public void setObjectLayouts(
		UnsafeSupplier<ObjectLayout[], Exception> objectLayoutsUnsafeSupplier) {

		_objectLayoutsSupplier = () -> {
			try {
				return objectLayoutsUnsafeSupplier.get();
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
	protected ObjectLayout[] objectLayouts;

	@JsonIgnore
	private Supplier<ObjectLayout[]> _objectLayoutsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ObjectRelationship[] getObjectRelationships() {
		if (_objectRelationshipsSupplier != null) {
			objectRelationships = _objectRelationshipsSupplier.get();

			_objectRelationshipsSupplier = null;
		}

		return objectRelationships;
	}

	public void setObjectRelationships(
		ObjectRelationship[] objectRelationships) {

		this.objectRelationships = objectRelationships;

		_objectRelationshipsSupplier = null;
	}

	@JsonIgnore
	public void setObjectRelationships(
		UnsafeSupplier<ObjectRelationship[], Exception>
			objectRelationshipsUnsafeSupplier) {

		_objectRelationshipsSupplier = () -> {
			try {
				return objectRelationshipsUnsafeSupplier.get();
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
	protected ObjectRelationship[] objectRelationships;

	@JsonIgnore
	private Supplier<ObjectRelationship[]> _objectRelationshipsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ObjectValidationRule[] getObjectValidationRules() {
		if (_objectValidationRulesSupplier != null) {
			objectValidationRules = _objectValidationRulesSupplier.get();

			_objectValidationRulesSupplier = null;
		}

		return objectValidationRules;
	}

	public void setObjectValidationRules(
		ObjectValidationRule[] objectValidationRules) {

		this.objectValidationRules = objectValidationRules;

		_objectValidationRulesSupplier = null;
	}

	@JsonIgnore
	public void setObjectValidationRules(
		UnsafeSupplier<ObjectValidationRule[], Exception>
			objectValidationRulesUnsafeSupplier) {

		_objectValidationRulesSupplier = () -> {
			try {
				return objectValidationRulesUnsafeSupplier.get();
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
	protected ObjectValidationRule[] objectValidationRules;

	@JsonIgnore
	private Supplier<ObjectValidationRule[]> _objectValidationRulesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ObjectView[] getObjectViews() {
		if (_objectViewsSupplier != null) {
			objectViews = _objectViewsSupplier.get();

			_objectViewsSupplier = null;
		}

		return objectViews;
	}

	public void setObjectViews(ObjectView[] objectViews) {
		this.objectViews = objectViews;

		_objectViewsSupplier = null;
	}

	@JsonIgnore
	public void setObjectViews(
		UnsafeSupplier<ObjectView[], Exception> objectViewsUnsafeSupplier) {

		_objectViewsSupplier = () -> {
			try {
				return objectViewsUnsafeSupplier.get();
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
	protected ObjectView[] objectViews;

	@JsonIgnore
	private Supplier<ObjectView[]> _objectViewsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPanelAppOrder() {
		if (_panelAppOrderSupplier != null) {
			panelAppOrder = _panelAppOrderSupplier.get();

			_panelAppOrderSupplier = null;
		}

		return panelAppOrder;
	}

	public void setPanelAppOrder(String panelAppOrder) {
		this.panelAppOrder = panelAppOrder;

		_panelAppOrderSupplier = null;
	}

	@JsonIgnore
	public void setPanelAppOrder(
		UnsafeSupplier<String, Exception> panelAppOrderUnsafeSupplier) {

		_panelAppOrderSupplier = () -> {
			try {
				return panelAppOrderUnsafeSupplier.get();
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
	protected String panelAppOrder;

	@JsonIgnore
	private Supplier<String> _panelAppOrderSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPanelCategoryKey() {
		if (_panelCategoryKeySupplier != null) {
			panelCategoryKey = _panelCategoryKeySupplier.get();

			_panelCategoryKeySupplier = null;
		}

		return panelCategoryKey;
	}

	public void setPanelCategoryKey(String panelCategoryKey) {
		this.panelCategoryKey = panelCategoryKey;

		_panelCategoryKeySupplier = null;
	}

	@JsonIgnore
	public void setPanelCategoryKey(
		UnsafeSupplier<String, Exception> panelCategoryKeyUnsafeSupplier) {

		_panelCategoryKeySupplier = () -> {
			try {
				return panelCategoryKeyUnsafeSupplier.get();
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
	protected String panelCategoryKey;

	@JsonIgnore
	private Supplier<String> _panelCategoryKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getParameterRequired() {
		if (_parameterRequiredSupplier != null) {
			parameterRequired = _parameterRequiredSupplier.get();

			_parameterRequiredSupplier = null;
		}

		return parameterRequired;
	}

	public void setParameterRequired(Boolean parameterRequired) {
		this.parameterRequired = parameterRequired;

		_parameterRequiredSupplier = null;
	}

	@JsonIgnore
	public void setParameterRequired(
		UnsafeSupplier<Boolean, Exception> parameterRequiredUnsafeSupplier) {

		_parameterRequiredSupplier = () -> {
			try {
				return parameterRequiredUnsafeSupplier.get();
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
	protected Boolean parameterRequired;

	@JsonIgnore
	private Supplier<Boolean> _parameterRequiredSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, String> getPluralLabel() {
		if (_pluralLabelSupplier != null) {
			pluralLabel = _pluralLabelSupplier.get();

			_pluralLabelSupplier = null;
		}

		return pluralLabel;
	}

	public void setPluralLabel(Map<String, String> pluralLabel) {
		this.pluralLabel = pluralLabel;

		_pluralLabelSupplier = null;
	}

	@JsonIgnore
	public void setPluralLabel(
		UnsafeSupplier<Map<String, String>, Exception>
			pluralLabelUnsafeSupplier) {

		_pluralLabelSupplier = () -> {
			try {
				return pluralLabelUnsafeSupplier.get();
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
	protected Map<String, String> pluralLabel;

	@JsonIgnore
	private Supplier<Map<String, String>> _pluralLabelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getPortlet() {
		if (_portletSupplier != null) {
			portlet = _portletSupplier.get();

			_portletSupplier = null;
		}

		return portlet;
	}

	public void setPortlet(Boolean portlet) {
		this.portlet = portlet;

		_portletSupplier = null;
	}

	@JsonIgnore
	public void setPortlet(
		UnsafeSupplier<Boolean, Exception> portletUnsafeSupplier) {

		_portletSupplier = () -> {
			try {
				return portletUnsafeSupplier.get();
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
	protected Boolean portlet;

	@JsonIgnore
	private Supplier<Boolean> _portletSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getRestContextPath() {
		if (_restContextPathSupplier != null) {
			restContextPath = _restContextPathSupplier.get();

			_restContextPathSupplier = null;
		}

		return restContextPath;
	}

	public void setRestContextPath(String restContextPath) {
		this.restContextPath = restContextPath;

		_restContextPathSupplier = null;
	}

	@JsonIgnore
	public void setRestContextPath(
		UnsafeSupplier<String, Exception> restContextPathUnsafeSupplier) {

		_restContextPathSupplier = () -> {
			try {
				return restContextPathUnsafeSupplier.get();
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
	protected String restContextPath;

	@JsonIgnore
	private Supplier<String> _restContextPathSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getRootObjectDefinitionExternalReferenceCode() {
		if (_rootObjectDefinitionExternalReferenceCodeSupplier != null) {
			rootObjectDefinitionExternalReferenceCode =
				_rootObjectDefinitionExternalReferenceCodeSupplier.get();

			_rootObjectDefinitionExternalReferenceCodeSupplier = null;
		}

		return rootObjectDefinitionExternalReferenceCode;
	}

	public void setRootObjectDefinitionExternalReferenceCode(
		String rootObjectDefinitionExternalReferenceCode) {

		this.rootObjectDefinitionExternalReferenceCode =
			rootObjectDefinitionExternalReferenceCode;

		_rootObjectDefinitionExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setRootObjectDefinitionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			rootObjectDefinitionExternalReferenceCodeUnsafeSupplier) {

		_rootObjectDefinitionExternalReferenceCodeSupplier = () -> {
			try {
				return rootObjectDefinitionExternalReferenceCodeUnsafeSupplier.
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String rootObjectDefinitionExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _rootObjectDefinitionExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getScope() {
		if (_scopeSupplier != null) {
			scope = _scopeSupplier.get();

			_scopeSupplier = null;
		}

		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;

		_scopeSupplier = null;
	}

	@JsonIgnore
	public void setScope(
		UnsafeSupplier<String, Exception> scopeUnsafeSupplier) {

		_scopeSupplier = () -> {
			try {
				return scopeUnsafeSupplier.get();
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
	protected String scope;

	@JsonIgnore
	private Supplier<String> _scopeSupplier;

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
	public String getStorageType() {
		if (_storageTypeSupplier != null) {
			storageType = _storageTypeSupplier.get();

			_storageTypeSupplier = null;
		}

		return storageType;
	}

	public void setStorageType(String storageType) {
		this.storageType = storageType;

		_storageTypeSupplier = null;
	}

	@JsonIgnore
	public void setStorageType(
		UnsafeSupplier<String, Exception> storageTypeUnsafeSupplier) {

		_storageTypeSupplier = () -> {
			try {
				return storageTypeUnsafeSupplier.get();
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
	protected String storageType;

	@JsonIgnore
	private Supplier<String> _storageTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSystem() {
		if (_systemSupplier != null) {
			system = _systemSupplier.get();

			_systemSupplier = null;
		}

		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;

		_systemSupplier = null;
	}

	@JsonIgnore
	public void setSystem(
		UnsafeSupplier<Boolean, Exception> systemUnsafeSupplier) {

		_systemSupplier = () -> {
			try {
				return systemUnsafeSupplier.get();
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
	protected Boolean system;

	@JsonIgnore
	private Supplier<Boolean> _systemSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTitleObjectFieldName() {
		if (_titleObjectFieldNameSupplier != null) {
			titleObjectFieldName = _titleObjectFieldNameSupplier.get();

			_titleObjectFieldNameSupplier = null;
		}

		return titleObjectFieldName;
	}

	public void setTitleObjectFieldName(String titleObjectFieldName) {
		this.titleObjectFieldName = titleObjectFieldName;

		_titleObjectFieldNameSupplier = null;
	}

	@JsonIgnore
	public void setTitleObjectFieldName(
		UnsafeSupplier<String, Exception> titleObjectFieldNameUnsafeSupplier) {

		_titleObjectFieldNameSupplier = () -> {
			try {
				return titleObjectFieldNameUnsafeSupplier.get();
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
	protected String titleObjectFieldName;

	@JsonIgnore
	private Supplier<String> _titleObjectFieldNameSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectDefinition)) {
			return false;
		}

		ObjectDefinition objectDefinition = (ObjectDefinition)object;

		return Objects.equals(toString(), objectDefinition.toString());
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

		Boolean accountEntryRestricted = getAccountEntryRestricted();

		if (accountEntryRestricted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountEntryRestricted\": ");

			sb.append(accountEntryRestricted);
		}

		String accountEntryRestrictedObjectFieldName =
			getAccountEntryRestrictedObjectFieldName();

		if (accountEntryRestrictedObjectFieldName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountEntryRestrictedObjectFieldName\": ");

			sb.append("\"");

			sb.append(_escape(accountEntryRestrictedObjectFieldName));

			sb.append("\"");
		}

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		Boolean active = getActive();

		if (active != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(active);
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

		Boolean enableCategorization = getEnableCategorization();

		if (enableCategorization != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enableCategorization\": ");

			sb.append(enableCategorization);
		}

		Boolean enableComments = getEnableComments();

		if (enableComments != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enableComments\": ");

			sb.append(enableComments);
		}

		Boolean enableFriendlyURLCustomization =
			getEnableFriendlyURLCustomization();

		if (enableFriendlyURLCustomization != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enableFriendlyURLCustomization\": ");

			sb.append(enableFriendlyURLCustomization);
		}

		Boolean enableIndexSearch = getEnableIndexSearch();

		if (enableIndexSearch != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enableIndexSearch\": ");

			sb.append(enableIndexSearch);
		}

		Boolean enableLocalization = getEnableLocalization();

		if (enableLocalization != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enableLocalization\": ");

			sb.append(enableLocalization);
		}

		Boolean enableObjectEntryDraft = getEnableObjectEntryDraft();

		if (enableObjectEntryDraft != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enableObjectEntryDraft\": ");

			sb.append(enableObjectEntryDraft);
		}

		Boolean enableObjectEntryHistory = getEnableObjectEntryHistory();

		if (enableObjectEntryHistory != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enableObjectEntryHistory\": ");

			sb.append(enableObjectEntryHistory);
		}

		Boolean enableObjectEntryVersioning = getEnableObjectEntryVersioning();

		if (enableObjectEntryVersioning != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enableObjectEntryVersioning\": ");

			sb.append(enableObjectEntryVersioning);
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

		Map<String, String> label = getLabel();

		if (label != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append(_toJSON(label));
		}

		Boolean modifiable = getModifiable();

		if (modifiable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modifiable\": ");

			sb.append(modifiable);
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

		ObjectAction[] objectActions = getObjectActions();

		if (objectActions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectActions\": ");

			sb.append("[");

			for (int i = 0; i < objectActions.length; i++) {
				sb.append(String.valueOf(objectActions[i]));

				if ((i + 1) < objectActions.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		ObjectDefinitionSetting[] objectDefinitionSettings =
			getObjectDefinitionSettings();

		if (objectDefinitionSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionSettings\": ");

			sb.append("[");

			for (int i = 0; i < objectDefinitionSettings.length; i++) {
				sb.append(String.valueOf(objectDefinitionSettings[i]));

				if ((i + 1) < objectDefinitionSettings.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		ObjectField[] objectFields = getObjectFields();

		if (objectFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectFields\": ");

			sb.append("[");

			for (int i = 0; i < objectFields.length; i++) {
				sb.append(String.valueOf(objectFields[i]));

				if ((i + 1) < objectFields.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String objectFolderExternalReferenceCode =
			getObjectFolderExternalReferenceCode();

		if (objectFolderExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectFolderExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(objectFolderExternalReferenceCode));

			sb.append("\"");
		}

		ObjectLayout[] objectLayouts = getObjectLayouts();

		if (objectLayouts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectLayouts\": ");

			sb.append("[");

			for (int i = 0; i < objectLayouts.length; i++) {
				sb.append(String.valueOf(objectLayouts[i]));

				if ((i + 1) < objectLayouts.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		ObjectRelationship[] objectRelationships = getObjectRelationships();

		if (objectRelationships != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectRelationships\": ");

			sb.append("[");

			for (int i = 0; i < objectRelationships.length; i++) {
				sb.append(String.valueOf(objectRelationships[i]));

				if ((i + 1) < objectRelationships.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		ObjectValidationRule[] objectValidationRules =
			getObjectValidationRules();

		if (objectValidationRules != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectValidationRules\": ");

			sb.append("[");

			for (int i = 0; i < objectValidationRules.length; i++) {
				sb.append(String.valueOf(objectValidationRules[i]));

				if ((i + 1) < objectValidationRules.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		ObjectView[] objectViews = getObjectViews();

		if (objectViews != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectViews\": ");

			sb.append("[");

			for (int i = 0; i < objectViews.length; i++) {
				sb.append(String.valueOf(objectViews[i]));

				if ((i + 1) < objectViews.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String panelAppOrder = getPanelAppOrder();

		if (panelAppOrder != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"panelAppOrder\": ");

			sb.append("\"");

			sb.append(_escape(panelAppOrder));

			sb.append("\"");
		}

		String panelCategoryKey = getPanelCategoryKey();

		if (panelCategoryKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"panelCategoryKey\": ");

			sb.append("\"");

			sb.append(_escape(panelCategoryKey));

			sb.append("\"");
		}

		Boolean parameterRequired = getParameterRequired();

		if (parameterRequired != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parameterRequired\": ");

			sb.append(parameterRequired);
		}

		Map<String, String> pluralLabel = getPluralLabel();

		if (pluralLabel != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pluralLabel\": ");

			sb.append(_toJSON(pluralLabel));
		}

		Boolean portlet = getPortlet();

		if (portlet != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"portlet\": ");

			sb.append(portlet);
		}

		String restContextPath = getRestContextPath();

		if (restContextPath != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"restContextPath\": ");

			sb.append("\"");

			sb.append(_escape(restContextPath));

			sb.append("\"");
		}

		String rootObjectDefinitionExternalReferenceCode =
			getRootObjectDefinitionExternalReferenceCode();

		if (rootObjectDefinitionExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rootObjectDefinitionExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(rootObjectDefinitionExternalReferenceCode));

			sb.append("\"");
		}

		String scope = getScope();

		if (scope != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scope\": ");

			sb.append("\"");

			sb.append(_escape(scope));

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

		String storageType = getStorageType();

		if (storageType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"storageType\": ");

			sb.append("\"");

			sb.append(_escape(storageType));

			sb.append("\"");
		}

		Boolean system = getSystem();

		if (system != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"system\": ");

			sb.append(system);
		}

		String titleObjectFieldName = getTitleObjectFieldName();

		if (titleObjectFieldName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"titleObjectFieldName\": ");

			sb.append("\"");

			sb.append(_escape(titleObjectFieldName));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition",
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