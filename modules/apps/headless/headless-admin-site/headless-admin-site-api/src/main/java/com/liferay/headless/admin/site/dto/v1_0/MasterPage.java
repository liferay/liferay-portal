/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.headless.admin.taxonomy.dto.v1_0.Keyword;
import com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyCategory;
import com.liferay.headless.admin.user.dto.v1_0.Creator;
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
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A page with common elements (header, footer, ...) used for all or several pages of a site.",
	value = "MasterPage"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "MasterPage")
public class MasterPage implements Serializable {

	public static MasterPage toDTO(String json) {
		return ObjectMapperUtil.readValue(MasterPage.class, json);
	}

	public static MasterPage unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(MasterPage.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The master page's creator. It is not returned by default. It can be embedded via nestedFields."
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

	@GraphQLField(
		description = "The master page's creator. It is not returned by default. It can be embedded via nestedFields."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page's creator external reference code."
	)
	public String getCreatorExternalReferenceCode() {
		if (_creatorExternalReferenceCodeSupplier != null) {
			creatorExternalReferenceCode =
				_creatorExternalReferenceCodeSupplier.get();

			_creatorExternalReferenceCodeSupplier = null;
		}

		return creatorExternalReferenceCode;
	}

	public void setCreatorExternalReferenceCode(
		String creatorExternalReferenceCode) {

		this.creatorExternalReferenceCode = creatorExternalReferenceCode;

		_creatorExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setCreatorExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			creatorExternalReferenceCodeUnsafeSupplier) {

		_creatorExternalReferenceCodeSupplier = () -> {
			try {
				return creatorExternalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page's creator external reference code.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String creatorExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _creatorExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The master page's creation date."
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

	@GraphQLField(description = "The master page's creation date.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The last time any field of the master page was changed."
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
		description = "The last time any field of the master page was changed."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The master page's most recent publication date."
	)
	public Date getDatePublished() {
		if (_datePublishedSupplier != null) {
			datePublished = _datePublishedSupplier.get();

			_datePublishedSupplier = null;
		}

		return datePublished;
	}

	public void setDatePublished(Date datePublished) {
		this.datePublished = datePublished;

		_datePublishedSupplier = null;
	}

	@JsonIgnore
	public void setDatePublished(
		UnsafeSupplier<Date, Exception> datePublishedUnsafeSupplier) {

		_datePublishedSupplier = () -> {
			try {
				return datePublishedUnsafeSupplier.get();
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
		description = "The master page's most recent publication date."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date datePublished;

	@JsonIgnore
	private Supplier<Date> _datePublishedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The master page's external reference code, unique per site."
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
		description = "The master page's external reference code, unique per site."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The master page's key."
	)
	public String getKey() {
		if (_keySupplier != null) {
			key = _keySupplier.get();

			_keySupplier = null;
		}

		return key;
	}

	public void setKey(String key) {
		this.key = key;

		_keySupplier = null;
	}

	@JsonIgnore
	public void setKey(UnsafeSupplier<String, Exception> keyUnsafeSupplier) {
		_keySupplier = () -> {
			try {
				return keyUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The master page's key.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String key;

	@JsonIgnore
	private Supplier<String> _keySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The external references to the associated keywords."
	)
	@Valid
	public ItemExternalReference[] getKeywordItemExternalReferences() {
		if (_keywordItemExternalReferencesSupplier != null) {
			keywordItemExternalReferences =
				_keywordItemExternalReferencesSupplier.get();

			_keywordItemExternalReferencesSupplier = null;
		}

		return keywordItemExternalReferences;
	}

	public void setKeywordItemExternalReferences(
		ItemExternalReference[] keywordItemExternalReferences) {

		this.keywordItemExternalReferences = keywordItemExternalReferences;

		_keywordItemExternalReferencesSupplier = null;
	}

	@JsonIgnore
	public void setKeywordItemExternalReferences(
		UnsafeSupplier<ItemExternalReference[], Exception>
			keywordItemExternalReferencesUnsafeSupplier) {

		_keywordItemExternalReferencesSupplier = () -> {
			try {
				return keywordItemExternalReferencesUnsafeSupplier.get();
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
		description = "The external references to the associated keywords."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ItemExternalReference[] keywordItemExternalReferences;

	@JsonIgnore
	private Supplier<ItemExternalReference[]>
		_keywordItemExternalReferencesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The associated keywords. They are not returned by default. They can be embedded via nestedFields."
	)
	@Valid
	public Keyword[] getKeywords() {
		if (_keywordsSupplier != null) {
			keywords = _keywordsSupplier.get();

			_keywordsSupplier = null;
		}

		return keywords;
	}

	public void setKeywords(Keyword[] keywords) {
		this.keywords = keywords;

		_keywordsSupplier = null;
	}

	@JsonIgnore
	public void setKeywords(
		UnsafeSupplier<Keyword[], Exception> keywordsUnsafeSupplier) {

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
		description = "The associated keywords. They are not returned by default. They can be embedded via nestedFields."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Keyword[] keywords;

	@JsonIgnore
	private Supplier<Keyword[]> _keywordsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether the master page is the default one."
	)
	public Boolean getMarkedAsDefault() {
		if (_markedAsDefaultSupplier != null) {
			markedAsDefault = _markedAsDefaultSupplier.get();

			_markedAsDefaultSupplier = null;
		}

		return markedAsDefault;
	}

	public void setMarkedAsDefault(Boolean markedAsDefault) {
		this.markedAsDefault = markedAsDefault;

		_markedAsDefaultSupplier = null;
	}

	@JsonIgnore
	public void setMarkedAsDefault(
		UnsafeSupplier<Boolean, Exception> markedAsDefaultUnsafeSupplier) {

		_markedAsDefaultSupplier = () -> {
			try {
				return markedAsDefaultUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Whether the master page is the default one.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean markedAsDefault;

	@JsonIgnore
	private Supplier<Boolean> _markedAsDefaultSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The master page's name."
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

	@GraphQLField(description = "The master page's name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The master page's specifications. A master page will contain 1 page specifications for its draft layout and 1 page specifications for its published layout. This field is not returned by default. It can be requested via nestedFields."
	)
	@Valid
	public PageSpecification[] getPageSpecifications() {
		if (_pageSpecificationsSupplier != null) {
			pageSpecifications = _pageSpecificationsSupplier.get();

			_pageSpecificationsSupplier = null;
		}

		return pageSpecifications;
	}

	public void setPageSpecifications(PageSpecification[] pageSpecifications) {
		this.pageSpecifications = pageSpecifications;

		_pageSpecificationsSupplier = null;
	}

	@JsonIgnore
	public void setPageSpecifications(
		UnsafeSupplier<PageSpecification[], Exception>
			pageSpecificationsUnsafeSupplier) {

		_pageSpecificationsSupplier = () -> {
			try {
				return pageSpecificationsUnsafeSupplier.get();
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
		description = "The master page's specifications. A master page will contain 1 page specifications for its draft layout and 1 page specifications for its published layout. This field is not returned by default. It can be requested via nestedFields."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PageSpecification[] pageSpecifications;

	@JsonIgnore
	private Supplier<PageSpecification[]> _pageSpecificationsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The associated categories. They are not returned by default. They can be embedded via nestedFields."
	)
	@Valid
	public TaxonomyCategory[] getTaxonomyCategories() {
		if (_taxonomyCategoriesSupplier != null) {
			taxonomyCategories = _taxonomyCategoriesSupplier.get();

			_taxonomyCategoriesSupplier = null;
		}

		return taxonomyCategories;
	}

	public void setTaxonomyCategories(TaxonomyCategory[] taxonomyCategories) {
		this.taxonomyCategories = taxonomyCategories;

		_taxonomyCategoriesSupplier = null;
	}

	@JsonIgnore
	public void setTaxonomyCategories(
		UnsafeSupplier<TaxonomyCategory[], Exception>
			taxonomyCategoriesUnsafeSupplier) {

		_taxonomyCategoriesSupplier = () -> {
			try {
				return taxonomyCategoriesUnsafeSupplier.get();
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
		description = "The associated categories. They are not returned by default. They can be embedded via nestedFields."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected TaxonomyCategory[] taxonomyCategories;

	@JsonIgnore
	private Supplier<TaxonomyCategory[]> _taxonomyCategoriesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The external references to the associated categories."
	)
	@Valid
	public ItemExternalReference[] getTaxonomyCategoryItemExternalReferences() {
		if (_taxonomyCategoryItemExternalReferencesSupplier != null) {
			taxonomyCategoryItemExternalReferences =
				_taxonomyCategoryItemExternalReferencesSupplier.get();

			_taxonomyCategoryItemExternalReferencesSupplier = null;
		}

		return taxonomyCategoryItemExternalReferences;
	}

	public void setTaxonomyCategoryItemExternalReferences(
		ItemExternalReference[] taxonomyCategoryItemExternalReferences) {

		this.taxonomyCategoryItemExternalReferences =
			taxonomyCategoryItemExternalReferences;

		_taxonomyCategoryItemExternalReferencesSupplier = null;
	}

	@JsonIgnore
	public void setTaxonomyCategoryItemExternalReferences(
		UnsafeSupplier<ItemExternalReference[], Exception>
			taxonomyCategoryItemExternalReferencesUnsafeSupplier) {

		_taxonomyCategoryItemExternalReferencesSupplier = () -> {
			try {
				return taxonomyCategoryItemExternalReferencesUnsafeSupplier.
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
		description = "The external references to the associated categories."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ItemExternalReference[] taxonomyCategoryItemExternalReferences;

	@JsonIgnore
	private Supplier<ItemExternalReference[]>
		_taxonomyCategoryItemExternalReferencesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The master page's thumbnail."
	)
	@Valid
	public ItemExternalReference getThumbnail() {
		if (_thumbnailSupplier != null) {
			thumbnail = _thumbnailSupplier.get();

			_thumbnailSupplier = null;
		}

		return thumbnail;
	}

	public void setThumbnail(ItemExternalReference thumbnail) {
		this.thumbnail = thumbnail;

		_thumbnailSupplier = null;
	}

	@JsonIgnore
	public void setThumbnail(
		UnsafeSupplier<ItemExternalReference, Exception>
			thumbnailUnsafeSupplier) {

		_thumbnailSupplier = () -> {
			try {
				return thumbnailUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The master page's thumbnail.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ItemExternalReference thumbnail;

	@JsonIgnore
	private Supplier<ItemExternalReference> _thumbnailSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A valid external identifier to reference this page."
	)
	public String getUuid() {
		if (_uuidSupplier != null) {
			uuid = _uuidSupplier.get();

			_uuidSupplier = null;
		}

		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;

		_uuidSupplier = null;
	}

	@JsonIgnore
	public void setUuid(UnsafeSupplier<String, Exception> uuidUnsafeSupplier) {
		_uuidSupplier = () -> {
			try {
				return uuidUnsafeSupplier.get();
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
		description = "A valid external identifier to reference this page."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String uuid;

	@JsonIgnore
	private Supplier<String> _uuidSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof MasterPage)) {
			return false;
		}

		MasterPage masterPage = (MasterPage)object;

		return Objects.equals(toString(), masterPage.toString());
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

		Creator creator = getCreator();

		if (creator != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(creator);
		}

		String creatorExternalReferenceCode = getCreatorExternalReferenceCode();

		if (creatorExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creatorExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(creatorExternalReferenceCode));

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

		Date datePublished = getDatePublished();

		if (datePublished != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"datePublished\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(datePublished));

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

		String key = getKey();

		if (key != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(key));

			sb.append("\"");
		}

		ItemExternalReference[] keywordItemExternalReferences =
			getKeywordItemExternalReferences();

		if (keywordItemExternalReferences != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywordItemExternalReferences\": ");

			sb.append("[");

			for (int i = 0; i < keywordItemExternalReferences.length; i++) {
				sb.append(String.valueOf(keywordItemExternalReferences[i]));

				if ((i + 1) < keywordItemExternalReferences.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Keyword[] keywords = getKeywords();

		if (keywords != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywords\": ");

			sb.append("[");

			for (int i = 0; i < keywords.length; i++) {
				sb.append(keywords[i]);

				if ((i + 1) < keywords.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean markedAsDefault = getMarkedAsDefault();

		if (markedAsDefault != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"markedAsDefault\": ");

			sb.append(markedAsDefault);
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

		PageSpecification[] pageSpecifications = getPageSpecifications();

		if (pageSpecifications != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageSpecifications\": ");

			sb.append("[");

			for (int i = 0; i < pageSpecifications.length; i++) {
				sb.append(String.valueOf(pageSpecifications[i]));

				if ((i + 1) < pageSpecifications.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		TaxonomyCategory[] taxonomyCategories = getTaxonomyCategories();

		if (taxonomyCategories != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategories\": ");

			sb.append("[");

			for (int i = 0; i < taxonomyCategories.length; i++) {
				sb.append(taxonomyCategories[i]);

				if ((i + 1) < taxonomyCategories.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		ItemExternalReference[] taxonomyCategoryItemExternalReferences =
			getTaxonomyCategoryItemExternalReferences();

		if (taxonomyCategoryItemExternalReferences != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryItemExternalReferences\": ");

			sb.append("[");

			for (int i = 0; i < taxonomyCategoryItemExternalReferences.length;
				 i++) {

				sb.append(
					String.valueOf(taxonomyCategoryItemExternalReferences[i]));

				if ((i + 1) < taxonomyCategoryItemExternalReferences.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		ItemExternalReference thumbnail = getThumbnail();

		if (thumbnail != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"thumbnail\": ");

			sb.append(String.valueOf(thumbnail));
		}

		String uuid = getUuid();

		if (uuid != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uuid\": ");

			sb.append("\"");

			sb.append(_escape(uuid));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.MasterPage",
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