/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.client.dto.v1_0;

import com.liferay.headless.asset.library.client.function.UnsafeSupplier;
import com.liferay.headless.asset.library.client.serdes.v1_0.AssetLibrarySerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author Roberto Díaz
 * @generated
 */
@Generated("")
public class AssetLibrary implements Cloneable, Serializable {

	public static AssetLibrary toDTO(String json) {
		return AssetLibrarySerDes.toDTO(json);
	}

	public Map<String, Map<String, String>> getActions() {
		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Map<String, String>> actions;

	public String getAssetLibraryKey() {
		return assetLibraryKey;
	}

	public void setAssetLibraryKey(String assetLibraryKey) {
		this.assetLibraryKey = assetLibraryKey;
	}

	public void setAssetLibraryKey(
		UnsafeSupplier<String, Exception> assetLibraryKeyUnsafeSupplier) {

		try {
			assetLibraryKey = assetLibraryKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String assetLibraryKey;

	public ConnectedSite[] getConnectedSites() {
		return connectedSites;
	}

	public void setConnectedSites(ConnectedSite[] connectedSites) {
		this.connectedSites = connectedSites;
	}

	public void setConnectedSites(
		UnsafeSupplier<ConnectedSite[], Exception>
			connectedSitesUnsafeSupplier) {

		try {
			connectedSites = connectedSitesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ConnectedSite[] connectedSites;

	public Long getCreatorUserId() {
		return creatorUserId;
	}

	public void setCreatorUserId(Long creatorUserId) {
		this.creatorUserId = creatorUserId;
	}

	public void setCreatorUserId(
		UnsafeSupplier<Long, Exception> creatorUserIdUnsafeSupplier) {

		try {
			creatorUserId = creatorUserIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long creatorUserId;

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		try {
			dateCreated = dateCreatedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateCreated;

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		try {
			dateModified = dateModifiedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateModified;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String description;

	public Map<String, String> getDescription_i18n() {
		return description_i18n;
	}

	public void setDescription_i18n(Map<String, String> description_i18n) {
		this.description_i18n = description_i18n;
	}

	public void setDescription_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			description_i18nUnsafeSupplier) {

		try {
			description_i18n = description_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> description_i18n;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public Map<String, String> getName_i18n() {
		return name_i18n;
	}

	public void setName_i18n(Map<String, String> name_i18n) {
		this.name_i18n = name_i18n;
	}

	public void setName_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			name_i18nUnsafeSupplier) {

		try {
			name_i18n = name_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> name_i18n;

	public Integer getNumberOfConnectedSites() {
		return numberOfConnectedSites;
	}

	public void setNumberOfConnectedSites(Integer numberOfConnectedSites) {
		this.numberOfConnectedSites = numberOfConnectedSites;
	}

	public void setNumberOfConnectedSites(
		UnsafeSupplier<Integer, Exception>
			numberOfConnectedSitesUnsafeSupplier) {

		try {
			numberOfConnectedSites = numberOfConnectedSitesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer numberOfConnectedSites;

	public Integer getNumberOfUserAccounts() {
		return numberOfUserAccounts;
	}

	public void setNumberOfUserAccounts(Integer numberOfUserAccounts) {
		this.numberOfUserAccounts = numberOfUserAccounts;
	}

	public void setNumberOfUserAccounts(
		UnsafeSupplier<Integer, Exception> numberOfUserAccountsUnsafeSupplier) {

		try {
			numberOfUserAccounts = numberOfUserAccountsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer numberOfUserAccounts;

	public Integer getNumberOfUserGroups() {
		return numberOfUserGroups;
	}

	public void setNumberOfUserGroups(Integer numberOfUserGroups) {
		this.numberOfUserGroups = numberOfUserGroups;
	}

	public void setNumberOfUserGroups(
		UnsafeSupplier<Integer, Exception> numberOfUserGroupsUnsafeSupplier) {

		try {
			numberOfUserGroups = numberOfUserGroupsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer numberOfUserGroups;

	public com.liferay.headless.asset.library.client.permission.Permission[]
		getPermissions() {

		return permissions;
	}

	public void setPermissions(
		com.liferay.headless.asset.library.client.permission.Permission[]
			permissions) {

		this.permissions = permissions;
	}

	public void setPermissions(
		UnsafeSupplier
			<com.liferay.headless.asset.library.client.permission.Permission[],
			 Exception> permissionsUnsafeSupplier) {

		try {
			permissions = permissionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected com.liferay.headless.asset.library.client.permission.Permission[]
		permissions;

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public void setSettings(
		UnsafeSupplier<Settings, Exception> settingsUnsafeSupplier) {

		try {
			settings = settingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Settings settings;

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public void setSiteId(
		UnsafeSupplier<Long, Exception> siteIdUnsafeSupplier) {

		try {
			siteId = siteIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long siteId;

	public Type getType() {
		return type;
	}

	public String getTypeAsString() {
		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Type type;

	public UserAccount[] getUserAccounts() {
		return userAccounts;
	}

	public void setUserAccounts(UserAccount[] userAccounts) {
		this.userAccounts = userAccounts;
	}

	public void setUserAccounts(
		UnsafeSupplier<UserAccount[], Exception> userAccountsUnsafeSupplier) {

		try {
			userAccounts = userAccountsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected UserAccount[] userAccounts;

	public UserGroup[] getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(UserGroup[] userGroups) {
		this.userGroups = userGroups;
	}

	public void setUserGroups(
		UnsafeSupplier<UserGroup[], Exception> userGroupsUnsafeSupplier) {

		try {
			userGroups = userGroupsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected UserGroup[] userGroups;

	@Override
	public AssetLibrary clone() throws CloneNotSupportedException {
		return (AssetLibrary)super.clone();
	}

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
		return AssetLibrarySerDes.toJSON(this);
	}

	public static enum Type {

		ASSET_LIBRARY("AssetLibrary"), SPACE("Space");

		public static Type create(String value) {
			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value) ||
					Objects.equals(type.name(), value)) {

					return type;
				}
			}

			return null;
		}

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

}