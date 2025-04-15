/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.NavigationMenuItemSerDes;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class NavigationMenuItem implements Cloneable, Serializable {

	public static NavigationMenuItem toDTO(String json) {
		return NavigationMenuItemSerDes.toDTO(json);
	}

	public String[] getAvailableLanguages() {
		return availableLanguages;
	}

	public void setAvailableLanguages(String[] availableLanguages) {
		this.availableLanguages = availableLanguages;
	}

	public void setAvailableLanguages(
		UnsafeSupplier<String[], Exception> availableLanguagesUnsafeSupplier) {

		try {
			availableLanguages = availableLanguagesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] availableLanguages;

	public String getContentURL() {
		return contentURL;
	}

	public void setContentURL(String contentURL) {
		this.contentURL = contentURL;
	}

	public void setContentURL(
		UnsafeSupplier<String, Exception> contentURLUnsafeSupplier) {

		try {
			contentURL = contentURLUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String contentURL;

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

	public com.liferay.headless.delivery.client.custom.field.CustomField[]
		getCustomFields() {

		return customFields;
	}

	public void setCustomFields(
		com.liferay.headless.delivery.client.custom.field.CustomField[]
			customFields) {

		this.customFields = customFields;
	}

	public void setCustomFields(
		UnsafeSupplier
			<com.liferay.headless.delivery.client.custom.field.CustomField[],
			 Exception> customFieldsUnsafeSupplier) {

		try {
			customFields = customFieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected com.liferay.headless.delivery.client.custom.field.CustomField[]
		customFields;

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

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setLink(UnsafeSupplier<String, Exception> linkUnsafeSupplier) {
		try {
			link = linkUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String link;

	public Map<String, String> getLink_i18n() {
		return link_i18n;
	}

	public void setLink_i18n(Map<String, String> link_i18n) {
		this.link_i18n = link_i18n;
	}

	public void setLink_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			link_i18nUnsafeSupplier) {

		try {
			link_i18n = link_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> link_i18n;

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

	public NavigationMenuItem[] getNavigationMenuItems() {
		return navigationMenuItems;
	}

	public void setNavigationMenuItems(
		NavigationMenuItem[] navigationMenuItems) {

		this.navigationMenuItems = navigationMenuItems;
	}

	public void setNavigationMenuItems(
		UnsafeSupplier<NavigationMenuItem[], Exception>
			navigationMenuItemsUnsafeSupplier) {

		try {
			navigationMenuItems = navigationMenuItemsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected NavigationMenuItem[] navigationMenuItems;

	public Long getParentNavigationMenuId() {
		return parentNavigationMenuId;
	}

	public void setParentNavigationMenuId(Long parentNavigationMenuId) {
		this.parentNavigationMenuId = parentNavigationMenuId;
	}

	public void setParentNavigationMenuId(
		UnsafeSupplier<Long, Exception> parentNavigationMenuIdUnsafeSupplier) {

		try {
			parentNavigationMenuId = parentNavigationMenuIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long parentNavigationMenuId;

	public String getSitePageURL() {
		return sitePageURL;
	}

	public void setSitePageURL(String sitePageURL) {
		this.sitePageURL = sitePageURL;
	}

	public void setSitePageURL(
		UnsafeSupplier<String, Exception> sitePageURLUnsafeSupplier) {

		try {
			sitePageURL = sitePageURLUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String sitePageURL;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<String, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String type;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUrl(UnsafeSupplier<String, Exception> urlUnsafeSupplier) {
		try {
			url = urlUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String url;

	public Boolean getUseCustomName() {
		return useCustomName;
	}

	public void setUseCustomName(Boolean useCustomName) {
		this.useCustomName = useCustomName;
	}

	public void setUseCustomName(
		UnsafeSupplier<Boolean, Exception> useCustomNameUnsafeSupplier) {

		try {
			useCustomName = useCustomNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean useCustomName;

	@Override
	public NavigationMenuItem clone() throws CloneNotSupportedException {
		return (NavigationMenuItem)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof NavigationMenuItem)) {
			return false;
		}

		NavigationMenuItem navigationMenuItem = (NavigationMenuItem)object;

		return Objects.equals(toString(), navigationMenuItem.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return NavigationMenuItemSerDes.toJSON(this);
	}

}