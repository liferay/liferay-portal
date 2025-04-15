/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.dto.v1_0;

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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents a navigation menu item.",
	value = "NavigationMenuItem"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "NavigationMenuItem")
public class NavigationMenuItem implements Serializable {

	public static NavigationMenuItem toDTO(String json) {
		return ObjectMapperUtil.readValue(NavigationMenuItem.class, json);
	}

	public static NavigationMenuItem unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(NavigationMenuItem.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The list of languages the navigation menu item has a translation for."
	)
	public String[] getAvailableLanguages() {
		if (_availableLanguagesSupplier != null) {
			availableLanguages = _availableLanguagesSupplier.get();

			_availableLanguagesSupplier = null;
		}

		return availableLanguages;
	}

	public void setAvailableLanguages(String[] availableLanguages) {
		this.availableLanguages = availableLanguages;

		_availableLanguagesSupplier = null;
	}

	@JsonIgnore
	public void setAvailableLanguages(
		UnsafeSupplier<String[], Exception> availableLanguagesUnsafeSupplier) {

		_availableLanguagesSupplier = () -> {
			try {
				return availableLanguagesUnsafeSupplier.get();
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
		description = "The list of languages the navigation menu item has a translation for."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String[] availableLanguages;

	@JsonIgnore
	private Supplier<String[]> _availableLanguagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The navigation menu item's content API REST URL."
	)
	public String getContentURL() {
		if (_contentURLSupplier != null) {
			contentURL = _contentURLSupplier.get();

			_contentURLSupplier = null;
		}

		return contentURL;
	}

	public void setContentURL(String contentURL) {
		this.contentURL = contentURL;

		_contentURLSupplier = null;
	}

	@JsonIgnore
	public void setContentURL(
		UnsafeSupplier<String, Exception> contentURLUnsafeSupplier) {

		_contentURLSupplier = () -> {
			try {
				return contentURLUnsafeSupplier.get();
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
		description = "The navigation menu item's content API REST URL."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String contentURL;

	@JsonIgnore
	private Supplier<String> _contentURLSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The navigation menu item's creator."
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

	@GraphQLField(description = "The navigation menu item's creator.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of the custom fields associated with the navigation menu item."
	)
	@Valid
	public com.liferay.portal.vulcan.custom.field.CustomField[]
		getCustomFields() {

		if (_customFieldsSupplier != null) {
			customFields = _customFieldsSupplier.get();

			_customFieldsSupplier = null;
		}

		return customFields;
	}

	public void setCustomFields(
		com.liferay.portal.vulcan.custom.field.CustomField[] customFields) {

		this.customFields = customFields;

		_customFieldsSupplier = null;
	}

	@JsonIgnore
	public void setCustomFields(
		UnsafeSupplier
			<com.liferay.portal.vulcan.custom.field.CustomField[], Exception>
				customFieldsUnsafeSupplier) {

		_customFieldsSupplier = () -> {
			try {
				return customFieldsUnsafeSupplier.get();
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
		description = "A list of the custom fields associated with the navigation menu item."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected com.liferay.portal.vulcan.custom.field.CustomField[] customFields;

	@JsonIgnore
	private Supplier<com.liferay.portal.vulcan.custom.field.CustomField[]>
		_customFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The navigation menu item's creation date."
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

	@GraphQLField(description = "The navigation menu item's creation date.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The last time the navigation menu item changed."
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
		description = "The last time the navigation menu item changed."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The navigation menu item's external reference code."
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
		description = "The navigation menu item's external reference code."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The navigation menu item's ID."
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

	@GraphQLField(description = "The navigation menu item's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The link to a page on the server."
	)
	public String getLink() {
		if (_linkSupplier != null) {
			link = _linkSupplier.get();

			_linkSupplier = null;
		}

		return link;
	}

	public void setLink(String link) {
		this.link = link;

		_linkSupplier = null;
	}

	@JsonIgnore
	public void setLink(UnsafeSupplier<String, Exception> linkUnsafeSupplier) {
		_linkSupplier = () -> {
			try {
				return linkUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The link to a page on the server.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String link;

	@JsonIgnore
	private Supplier<String> _linkSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized links to a page on the server."
	)
	@Valid
	public Map<String, String> getLink_i18n() {
		if (_link_i18nSupplier != null) {
			link_i18n = _link_i18nSupplier.get();

			_link_i18nSupplier = null;
		}

		return link_i18n;
	}

	public void setLink_i18n(Map<String, String> link_i18n) {
		this.link_i18n = link_i18n;

		_link_i18nSupplier = null;
	}

	@JsonIgnore
	public void setLink_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			link_i18nUnsafeSupplier) {

		_link_i18nSupplier = () -> {
			try {
				return link_i18nUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The localized links to a page on the server.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> link_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _link_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The navigation menu item's name."
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

	@GraphQLField(description = "The navigation menu item's name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized navigation menu item's names."
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

	@GraphQLField(description = "The localized navigation menu item's names.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> name_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _name_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The navigation menu items this navigation menu item has."
	)
	@Valid
	public NavigationMenuItem[] getNavigationMenuItems() {
		if (_navigationMenuItemsSupplier != null) {
			navigationMenuItems = _navigationMenuItemsSupplier.get();

			_navigationMenuItemsSupplier = null;
		}

		return navigationMenuItems;
	}

	public void setNavigationMenuItems(
		NavigationMenuItem[] navigationMenuItems) {

		this.navigationMenuItems = navigationMenuItems;

		_navigationMenuItemsSupplier = null;
	}

	@JsonIgnore
	public void setNavigationMenuItems(
		UnsafeSupplier<NavigationMenuItem[], Exception>
			navigationMenuItemsUnsafeSupplier) {

		_navigationMenuItemsSupplier = () -> {
			try {
				return navigationMenuItemsUnsafeSupplier.get();
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
		description = "The navigation menu items this navigation menu item has."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected NavigationMenuItem[] navigationMenuItems;

	@JsonIgnore
	private Supplier<NavigationMenuItem[]> _navigationMenuItemsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The ID of the navigation menu item's parent."
	)
	public Long getParentNavigationMenuId() {
		if (_parentNavigationMenuIdSupplier != null) {
			parentNavigationMenuId = _parentNavigationMenuIdSupplier.get();

			_parentNavigationMenuIdSupplier = null;
		}

		return parentNavigationMenuId;
	}

	public void setParentNavigationMenuId(Long parentNavigationMenuId) {
		this.parentNavigationMenuId = parentNavigationMenuId;

		_parentNavigationMenuIdSupplier = null;
	}

	@JsonIgnore
	public void setParentNavigationMenuId(
		UnsafeSupplier<Long, Exception> parentNavigationMenuIdUnsafeSupplier) {

		_parentNavigationMenuIdSupplier = () -> {
			try {
				return parentNavigationMenuIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The ID of the navigation menu item's parent.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long parentNavigationMenuId;

	@JsonIgnore
	private Supplier<Long> _parentNavigationMenuIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The navigation menu item's linked site page URL."
	)
	public String getSitePageURL() {
		if (_sitePageURLSupplier != null) {
			sitePageURL = _sitePageURLSupplier.get();

			_sitePageURLSupplier = null;
		}

		return sitePageURL;
	}

	public void setSitePageURL(String sitePageURL) {
		this.sitePageURL = sitePageURL;

		_sitePageURLSupplier = null;
	}

	@JsonIgnore
	public void setSitePageURL(
		UnsafeSupplier<String, Exception> sitePageURLUnsafeSupplier) {

		_sitePageURLSupplier = () -> {
			try {
				return sitePageURLUnsafeSupplier.get();
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
		description = "The navigation menu item's linked site page URL."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String sitePageURL;

	@JsonIgnore
	private Supplier<String> _sitePageURLSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The navigation menu item's type."
	)
	public String getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	public void setType(String type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<String, Exception> typeUnsafeSupplier) {
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

	@GraphQLField(description = "The navigation menu item's type.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String type;

	@JsonIgnore
	private Supplier<String> _typeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The navigation menu item's linked URL."
	)
	public String getUrl() {
		if (_urlSupplier != null) {
			url = _urlSupplier.get();

			_urlSupplier = null;
		}

		return url;
	}

	public void setUrl(String url) {
		this.url = url;

		_urlSupplier = null;
	}

	@JsonIgnore
	public void setUrl(UnsafeSupplier<String, Exception> urlUnsafeSupplier) {
		_urlSupplier = () -> {
			try {
				return urlUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The navigation menu item's linked URL.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String url;

	@JsonIgnore
	private Supplier<String> _urlSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getUseCustomName() {
		if (_useCustomNameSupplier != null) {
			useCustomName = _useCustomNameSupplier.get();

			_useCustomNameSupplier = null;
		}

		return useCustomName;
	}

	public void setUseCustomName(Boolean useCustomName) {
		this.useCustomName = useCustomName;

		_useCustomNameSupplier = null;
	}

	@JsonIgnore
	public void setUseCustomName(
		UnsafeSupplier<Boolean, Exception> useCustomNameUnsafeSupplier) {

		_useCustomNameSupplier = () -> {
			try {
				return useCustomNameUnsafeSupplier.get();
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
	protected Boolean useCustomName;

	@JsonIgnore
	private Supplier<Boolean> _useCustomNameSupplier;

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
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		String[] availableLanguages = getAvailableLanguages();

		if (availableLanguages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availableLanguages\": ");

			sb.append("[");

			for (int i = 0; i < availableLanguages.length; i++) {
				sb.append("\"");

				sb.append(_escape(availableLanguages[i]));

				sb.append("\"");

				if ((i + 1) < availableLanguages.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String contentURL = getContentURL();

		if (contentURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentURL\": ");

			sb.append("\"");

			sb.append(_escape(contentURL));

			sb.append("\"");
		}

		Creator creator = getCreator();

		if (creator != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(creator));
		}

		com.liferay.portal.vulcan.custom.field.CustomField[] customFields =
			getCustomFields();

		if (customFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < customFields.length; i++) {
				sb.append(customFields[i]);

				if ((i + 1) < customFields.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		String link = getLink();

		if (link != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"link\": ");

			sb.append("\"");

			sb.append(_escape(link));

			sb.append("\"");
		}

		Map<String, String> link_i18n = getLink_i18n();

		if (link_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"link_i18n\": ");

			sb.append(_toJSON(link_i18n));
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

		NavigationMenuItem[] navigationMenuItems = getNavigationMenuItems();

		if (navigationMenuItems != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"navigationMenuItems\": ");

			sb.append("[");

			for (int i = 0; i < navigationMenuItems.length; i++) {
				sb.append(String.valueOf(navigationMenuItems[i]));

				if ((i + 1) < navigationMenuItems.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Long parentNavigationMenuId = getParentNavigationMenuId();

		if (parentNavigationMenuId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentNavigationMenuId\": ");

			sb.append(parentNavigationMenuId);
		}

		String sitePageURL = getSitePageURL();

		if (sitePageURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sitePageURL\": ");

			sb.append("\"");

			sb.append(_escape(sitePageURL));

			sb.append("\"");
		}

		String type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(type));

			sb.append("\"");
		}

		String url = getUrl();

		if (url != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"url\": ");

			sb.append("\"");

			sb.append(_escape(url));

			sb.append("\"");
		}

		Boolean useCustomName = getUseCustomName();

		if (useCustomName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"useCustomName\": ");

			sb.append(useCustomName);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.NavigationMenuItem",
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