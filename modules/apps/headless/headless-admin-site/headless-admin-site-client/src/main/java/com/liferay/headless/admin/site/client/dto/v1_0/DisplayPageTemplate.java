/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.DisplayPageTemplateSerDes;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class DisplayPageTemplate implements Cloneable, Serializable {

	public static DisplayPageTemplate toDTO(String json) {
		return DisplayPageTemplateSerDes.toDTO(json);
	}

	public ClassSubtypeReference getContentTypeReference() {
		return contentTypeReference;
	}

	public void setContentTypeReference(
		ClassSubtypeReference contentTypeReference) {

		this.contentTypeReference = contentTypeReference;
	}

	public void setContentTypeReference(
		UnsafeSupplier<ClassSubtypeReference, Exception>
			contentTypeReferenceUnsafeSupplier) {

		try {
			contentTypeReference = contentTypeReferenceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ClassSubtypeReference contentTypeReference;

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

	public String getCreatorExternalReferenceCode() {
		return creatorExternalReferenceCode;
	}

	public void setCreatorExternalReferenceCode(
		String creatorExternalReferenceCode) {

		this.creatorExternalReferenceCode = creatorExternalReferenceCode;
	}

	public void setCreatorExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			creatorExternalReferenceCodeUnsafeSupplier) {

		try {
			creatorExternalReferenceCode =
				creatorExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String creatorExternalReferenceCode;

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

	public DisplayPageTemplateSettings getDisplayPageTemplateSettings() {
		return displayPageTemplateSettings;
	}

	public void setDisplayPageTemplateSettings(
		DisplayPageTemplateSettings displayPageTemplateSettings) {

		this.displayPageTemplateSettings = displayPageTemplateSettings;
	}

	public void setDisplayPageTemplateSettings(
		UnsafeSupplier<DisplayPageTemplateSettings, Exception>
			displayPageTemplateSettingsUnsafeSupplier) {

		try {
			displayPageTemplateSettings =
				displayPageTemplateSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DisplayPageTemplateSettings displayPageTemplateSettings;

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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setKey(UnsafeSupplier<String, Exception> keyUnsafeSupplier) {
		try {
			key = keyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String key;

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

	public DisplayPageTemplateFolder getParentFolder() {
		return parentFolder;
	}

	public void setParentFolder(DisplayPageTemplateFolder parentFolder) {
		this.parentFolder = parentFolder;
	}

	public void setParentFolder(
		UnsafeSupplier<DisplayPageTemplateFolder, Exception>
			parentFolderUnsafeSupplier) {

		try {
			parentFolder = parentFolderUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DisplayPageTemplateFolder parentFolder;

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
	public DisplayPageTemplate clone() throws CloneNotSupportedException {
		return (DisplayPageTemplate)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DisplayPageTemplate)) {
			return false;
		}

		DisplayPageTemplate displayPageTemplate = (DisplayPageTemplate)object;

		return Objects.equals(toString(), displayPageTemplate.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DisplayPageTemplateSerDes.toJSON(this);
	}

}