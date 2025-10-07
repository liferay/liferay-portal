/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.UtilityPageSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class UtilityPage implements Cloneable, Serializable {

	public static UtilityPage toDTO(String json) {
		return UtilityPageSerDes.toDTO(json);
	}

	public Creator getCreator() {
		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;
	}

	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		try {
			creator = creatorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Creator creator;

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

	public Date getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(Date datePublished) {
		this.datePublished = datePublished;
	}

	public void setDatePublished(
		UnsafeSupplier<Date, Exception> datePublishedUnsafeSupplier) {

		try {
			datePublished = datePublishedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date datePublished;

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

	public FriendlyUrlHistory getFriendlyUrlHistory() {
		return friendlyUrlHistory;
	}

	public void setFriendlyUrlHistory(FriendlyUrlHistory friendlyUrlHistory) {
		this.friendlyUrlHistory = friendlyUrlHistory;
	}

	public void setFriendlyUrlHistory(
		UnsafeSupplier<FriendlyUrlHistory, Exception>
			friendlyUrlHistoryUnsafeSupplier) {

		try {
			friendlyUrlHistory = friendlyUrlHistoryUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FriendlyUrlHistory friendlyUrlHistory;

	public Map<String, String> getFriendlyUrlPath_i18n() {
		return friendlyUrlPath_i18n;
	}

	public void setFriendlyUrlPath_i18n(
		Map<String, String> friendlyUrlPath_i18n) {

		this.friendlyUrlPath_i18n = friendlyUrlPath_i18n;
	}

	public void setFriendlyUrlPath_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			friendlyUrlPath_i18nUnsafeSupplier) {

		try {
			friendlyUrlPath_i18n = friendlyUrlPath_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> friendlyUrlPath_i18n;

	public Boolean getMarkedAsDefault() {
		return markedAsDefault;
	}

	public void setMarkedAsDefault(Boolean markedAsDefault) {
		this.markedAsDefault = markedAsDefault;
	}

	public void setMarkedAsDefault(
		UnsafeSupplier<Boolean, Exception> markedAsDefaultUnsafeSupplier) {

		try {
			markedAsDefault = markedAsDefaultUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean markedAsDefault;

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

	public PageSpecification[] getPageSpecifications() {
		return pageSpecifications;
	}

	public void setPageSpecifications(PageSpecification[] pageSpecifications) {
		this.pageSpecifications = pageSpecifications;
	}

	public void setPageSpecifications(
		UnsafeSupplier<PageSpecification[], Exception>
			pageSpecificationsUnsafeSupplier) {

		try {
			pageSpecifications = pageSpecificationsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PageSpecification[] pageSpecifications;

	public com.liferay.headless.admin.site.client.permission.Permission[]
		getPermissions() {

		return permissions;
	}

	public void setPermissions(
		com.liferay.headless.admin.site.client.permission.Permission[]
			permissions) {

		this.permissions = permissions;
	}

	public void setPermissions(
		UnsafeSupplier
			<com.liferay.headless.admin.site.client.permission.Permission[],
			 Exception> permissionsUnsafeSupplier) {

		try {
			permissions = permissionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected com.liferay.headless.admin.site.client.permission.Permission[]
		permissions;

	public ItemExternalReference getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(ItemExternalReference thumbnail) {
		this.thumbnail = thumbnail;
	}

	public void setThumbnail(
		UnsafeSupplier<ItemExternalReference, Exception>
			thumbnailUnsafeSupplier) {

		try {
			thumbnail = thumbnailUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ItemExternalReference thumbnail;

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

	public UtilityPageSettings getUtilityPageSettings() {
		return utilityPageSettings;
	}

	public void setUtilityPageSettings(
		UtilityPageSettings utilityPageSettings) {

		this.utilityPageSettings = utilityPageSettings;
	}

	public void setUtilityPageSettings(
		UnsafeSupplier<UtilityPageSettings, Exception>
			utilityPageSettingsUnsafeSupplier) {

		try {
			utilityPageSettings = utilityPageSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected UtilityPageSettings utilityPageSettings;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setUuid(UnsafeSupplier<String, Exception> uuidUnsafeSupplier) {
		try {
			uuid = uuidUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String uuid;

	@Override
	public UtilityPage clone() throws CloneNotSupportedException {
		return (UtilityPage)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UtilityPage)) {
			return false;
		}

		UtilityPage utilityPage = (UtilityPage)object;

		return Objects.equals(toString(), utilityPage.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return UtilityPageSerDes.toJSON(this);
	}

	public static enum Type {

		COOKIE_POLICY("CookiePolicy"), CREATE_ACCOUNT("CreateAccount"),
		ERROR("Error"), ERROR_CODE404("ErrorCode404"),
		ERROR_CODE500("ErrorCode500"), FORGOT_PASSWORD("ForgotPassword"),
		LOGIN("Login"), TERMS_OF_USE("TermsOfUse");

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