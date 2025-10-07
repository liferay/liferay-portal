/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.dto.v1_0;

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
 * @author Roberto Díaz
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents an Asset Library", value = "AssetLibrary"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AssetLibrary")
public class AssetLibrary implements Serializable {

	public static AssetLibrary toDTO(String json) {
		return ObjectMapperUtil.readValue(AssetLibrary.class, json);
	}

	public static AssetLibrary unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(AssetLibrary.class, json);
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
		description = "The asset library's key."
	)
	public String getAssetLibraryKey() {
		if (_assetLibraryKeySupplier != null) {
			assetLibraryKey = _assetLibraryKeySupplier.get();

			_assetLibraryKeySupplier = null;
		}

		return assetLibraryKey;
	}

	public void setAssetLibraryKey(String assetLibraryKey) {
		this.assetLibraryKey = assetLibraryKey;

		_assetLibraryKeySupplier = null;
	}

	@JsonIgnore
	public void setAssetLibraryKey(
		UnsafeSupplier<String, Exception> assetLibraryKeyUnsafeSupplier) {

		_assetLibraryKeySupplier = () -> {
			try {
				return assetLibraryKeyUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The asset library's key.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String assetLibraryKey;

	@JsonIgnore
	private Supplier<String> _assetLibraryKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The asset library's connected sites."
	)
	@Valid
	public ConnectedSite[] getConnectedSites() {
		if (_connectedSitesSupplier != null) {
			connectedSites = _connectedSitesSupplier.get();

			_connectedSitesSupplier = null;
		}

		return connectedSites;
	}

	public void setConnectedSites(ConnectedSite[] connectedSites) {
		this.connectedSites = connectedSites;

		_connectedSitesSupplier = null;
	}

	@JsonIgnore
	public void setConnectedSites(
		UnsafeSupplier<ConnectedSite[], Exception>
			connectedSitesUnsafeSupplier) {

		_connectedSitesSupplier = () -> {
			try {
				return connectedSitesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The asset library's connected sites.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected ConnectedSite[] connectedSites;

	@JsonIgnore
	private Supplier<ConnectedSite[]> _connectedSitesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The asset library's creator user ID."
	)
	public Long getCreatorUserId() {
		if (_creatorUserIdSupplier != null) {
			creatorUserId = _creatorUserIdSupplier.get();

			_creatorUserIdSupplier = null;
		}

		return creatorUserId;
	}

	public void setCreatorUserId(Long creatorUserId) {
		this.creatorUserId = creatorUserId;

		_creatorUserIdSupplier = null;
	}

	@JsonIgnore
	public void setCreatorUserId(
		UnsafeSupplier<Long, Exception> creatorUserIdUnsafeSupplier) {

		_creatorUserIdSupplier = () -> {
			try {
				return creatorUserIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The asset library's creator user ID.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long creatorUserId;

	@JsonIgnore
	private Supplier<Long> _creatorUserIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The asset library's creation date."
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

	@GraphQLField(description = "The asset library's creation date.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The last time a field of the asset library changed."
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
		description = "The last time a field of the asset library changed."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The asset library's description."
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

	@GraphQLField(description = "The asset library's description.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized asset library's description."
	)
	@Valid
	public Map<String, String> getDescription_i18n() {
		if (_description_i18nSupplier != null) {
			description_i18n = _description_i18nSupplier.get();

			_description_i18nSupplier = null;
		}

		return description_i18n;
	}

	public void setDescription_i18n(Map<String, String> description_i18n) {
		this.description_i18n = description_i18n;

		_description_i18nSupplier = null;
	}

	@JsonIgnore
	public void setDescription_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			description_i18nUnsafeSupplier) {

		_description_i18nSupplier = () -> {
			try {
				return description_i18nUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The localized asset library's description.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> description_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _description_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The asset library's external reference code."
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

	@GraphQLField(description = "The asset library's external reference code.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The asset library's ID."
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

	@GraphQLField(description = "The asset library's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The asset library's name."
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

	@GraphQLField(description = "The asset library's name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized asset library's name."
	)
	@Valid
	public Map<String, String> getName_i18n() {
		if (_name_i18nSupplier != null) {
			name_i18n = _name_i18nSupplier.get();

			_name_i18nSupplier = null;
		}

		return name_i18n;
	}

	public void setName_i18n(Map<String, String> name_i18n) {
		this.name_i18n = name_i18n;

		_name_i18nSupplier = null;
	}

	@JsonIgnore
	public void setName_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			name_i18nUnsafeSupplier) {

		_name_i18nSupplier = () -> {
			try {
				return name_i18nUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The localized asset library's name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> name_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _name_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The number of this asset library's connected sites."
	)
	public Integer getNumberOfConnectedSites() {
		if (_numberOfConnectedSitesSupplier != null) {
			numberOfConnectedSites = _numberOfConnectedSitesSupplier.get();

			_numberOfConnectedSitesSupplier = null;
		}

		return numberOfConnectedSites;
	}

	public void setNumberOfConnectedSites(Integer numberOfConnectedSites) {
		this.numberOfConnectedSites = numberOfConnectedSites;

		_numberOfConnectedSitesSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfConnectedSites(
		UnsafeSupplier<Integer, Exception>
			numberOfConnectedSitesUnsafeSupplier) {

		_numberOfConnectedSitesSupplier = () -> {
			try {
				return numberOfConnectedSitesUnsafeSupplier.get();
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
		description = "The number of this asset library's connected sites."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer numberOfConnectedSites;

	@JsonIgnore
	private Supplier<Integer> _numberOfConnectedSitesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The number of this asset library's associated users."
	)
	public Integer getNumberOfUserAccounts() {
		if (_numberOfUserAccountsSupplier != null) {
			numberOfUserAccounts = _numberOfUserAccountsSupplier.get();

			_numberOfUserAccountsSupplier = null;
		}

		return numberOfUserAccounts;
	}

	public void setNumberOfUserAccounts(Integer numberOfUserAccounts) {
		this.numberOfUserAccounts = numberOfUserAccounts;

		_numberOfUserAccountsSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfUserAccounts(
		UnsafeSupplier<Integer, Exception> numberOfUserAccountsUnsafeSupplier) {

		_numberOfUserAccountsSupplier = () -> {
			try {
				return numberOfUserAccountsUnsafeSupplier.get();
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
		description = "The number of this asset library's associated users."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer numberOfUserAccounts;

	@JsonIgnore
	private Supplier<Integer> _numberOfUserAccountsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The number of this asset library's associated user groups."
	)
	public Integer getNumberOfUserGroups() {
		if (_numberOfUserGroupsSupplier != null) {
			numberOfUserGroups = _numberOfUserGroupsSupplier.get();

			_numberOfUserGroupsSupplier = null;
		}

		return numberOfUserGroups;
	}

	public void setNumberOfUserGroups(Integer numberOfUserGroups) {
		this.numberOfUserGroups = numberOfUserGroups;

		_numberOfUserGroupsSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfUserGroups(
		UnsafeSupplier<Integer, Exception> numberOfUserGroupsUnsafeSupplier) {

		_numberOfUserGroupsSupplier = () -> {
			try {
				return numberOfUserGroupsUnsafeSupplier.get();
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
		description = "The number of this asset library's associated user groups."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer numberOfUserGroups;

	@JsonIgnore
	private Supplier<Integer> _numberOfUserGroupsSupplier;

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

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The asset library's settings."
	)
	@Valid
	public Settings getSettings() {
		if (_settingsSupplier != null) {
			settings = _settingsSupplier.get();

			_settingsSupplier = null;
		}

		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;

		_settingsSupplier = null;
	}

	@JsonIgnore
	public void setSettings(
		UnsafeSupplier<Settings, Exception> settingsUnsafeSupplier) {

		_settingsSupplier = () -> {
			try {
				return settingsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The asset library's settings.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Settings settings;

	@JsonIgnore
	private Supplier<Settings> _settingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The asset library's site ID."
	)
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

	@GraphQLField(description = "The asset library's site ID.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long siteId;

	@JsonIgnore
	private Supplier<Long> _siteIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("type")
	@Valid
	public Type getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	@JsonIgnore
	public String getTypeAsString() {
		Type type = getType();

		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		_typeSupplier = () -> {
			try {
				return typeUnsafeSupplier.get();
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
	protected Type type;

	@JsonIgnore
	private Supplier<Type> _typeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The asset library's associated users."
	)
	@Valid
	public UserAccount[] getUserAccounts() {
		if (_userAccountsSupplier != null) {
			userAccounts = _userAccountsSupplier.get();

			_userAccountsSupplier = null;
		}

		return userAccounts;
	}

	public void setUserAccounts(UserAccount[] userAccounts) {
		this.userAccounts = userAccounts;

		_userAccountsSupplier = null;
	}

	@JsonIgnore
	public void setUserAccounts(
		UnsafeSupplier<UserAccount[], Exception> userAccountsUnsafeSupplier) {

		_userAccountsSupplier = () -> {
			try {
				return userAccountsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The asset library's associated users.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected UserAccount[] userAccounts;

	@JsonIgnore
	private Supplier<UserAccount[]> _userAccountsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The asset library's associated user groups."
	)
	@Valid
	public UserGroup[] getUserGroups() {
		if (_userGroupsSupplier != null) {
			userGroups = _userGroupsSupplier.get();

			_userGroupsSupplier = null;
		}

		return userGroups;
	}

	public void setUserGroups(UserGroup[] userGroups) {
		this.userGroups = userGroups;

		_userGroupsSupplier = null;
	}

	@JsonIgnore
	public void setUserGroups(
		UnsafeSupplier<UserGroup[], Exception> userGroupsUnsafeSupplier) {

		_userGroupsSupplier = () -> {
			try {
				return userGroupsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The asset library's associated user groups.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected UserGroup[] userGroups;

	@JsonIgnore
	private Supplier<UserGroup[]> _userGroupsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AssetLibrary)) {
			return false;
		}

		AssetLibrary assetLibrary = (AssetLibrary)object;

		return Objects.equals(toString(), assetLibrary.toString());
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

		String assetLibraryKey = getAssetLibraryKey();

		if (assetLibraryKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetLibraryKey\": ");

			sb.append("\"");

			sb.append(_escape(assetLibraryKey));

			sb.append("\"");
		}

		ConnectedSite[] connectedSites = getConnectedSites();

		if (connectedSites != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"connectedSites\": ");

			sb.append("[");

			for (int i = 0; i < connectedSites.length; i++) {
				sb.append(String.valueOf(connectedSites[i]));

				if ((i + 1) < connectedSites.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Long creatorUserId = getCreatorUserId();

		if (creatorUserId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creatorUserId\": ");

			sb.append(creatorUserId);
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

		Map<String, String> description_i18n = getDescription_i18n();

		if (description_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(description_i18n));
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

		Map<String, String> name_i18n = getName_i18n();

		if (name_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(name_i18n));
		}

		Integer numberOfConnectedSites = getNumberOfConnectedSites();

		if (numberOfConnectedSites != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfConnectedSites\": ");

			sb.append(numberOfConnectedSites);
		}

		Integer numberOfUserAccounts = getNumberOfUserAccounts();

		if (numberOfUserAccounts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfUserAccounts\": ");

			sb.append(numberOfUserAccounts);
		}

		Integer numberOfUserGroups = getNumberOfUserGroups();

		if (numberOfUserGroups != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfUserGroups\": ");

			sb.append(numberOfUserGroups);
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

		Settings settings = getSettings();

		if (settings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"settings\": ");

			sb.append(String.valueOf(settings));
		}

		Long siteId = getSiteId();

		if (siteId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(siteId);
		}

		Type type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(type);

			sb.append("\"");
		}

		UserAccount[] userAccounts = getUserAccounts();

		if (userAccounts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userAccounts\": ");

			sb.append("[");

			for (int i = 0; i < userAccounts.length; i++) {
				sb.append(String.valueOf(userAccounts[i]));

				if ((i + 1) < userAccounts.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		UserGroup[] userGroups = getUserGroups();

		if (userGroups != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userGroups\": ");

			sb.append("[");

			for (int i = 0; i < userGroups.length; i++) {
				sb.append(String.valueOf(userGroups[i]));

				if ((i + 1) < userGroups.length) {
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
		defaultValue = "com.liferay.headless.asset.library.dto.v1_0.AssetLibrary",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Type")
	public static enum Type {

		ASSET_LIBRARY("AssetLibrary"), SPACE("Space");

		@JsonCreator
		public static Type create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value)) {
					return type;
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

		private Type(String value) {
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