/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.dto.v1_0;

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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents a category, which is a hierarchical classification that can be associated with particular asset types. Properties follow the [category](https://schema.org/category) specification.",
	value = "TaxonomyCategory"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Represents a category, which is a hierarchical classification that can be associated with particular asset types. Properties follow the [category](https://schema.org/category) specification.",
	requiredProperties = {"name"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "TaxonomyCategory")
public class TaxonomyCategory implements Serializable {

	public static TaxonomyCategory toDTO(String json) {
		return ObjectMapperUtil.readValue(TaxonomyCategory.class, json);
	}

	public static TaxonomyCategory unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(TaxonomyCategory.class, json);
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

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String assetLibraryKey;

	@JsonIgnore
	private Supplier<String> _assetLibraryKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of languages the category has a translation for."
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
		description = "A list of languages the category has a translation for."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String[] availableLanguages;

	@JsonIgnore
	private Supplier<String[]> _availableLanguagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The category's creator."
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

	@GraphQLField(description = "The category's creator.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The category's creation date."
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

	@GraphQLField(description = "The category's creation date.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The category's most recent modification date."
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

	@GraphQLField(description = "The category's most recent modification date.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The category's text description."
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

	@GraphQLField(description = "The category's text description.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> description_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _description_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The category's external reference code"
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

	@GraphQLField(description = "The category's external reference code")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The category's ID."
	)
	public String getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(String id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
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

	@GraphQLField(description = "The category's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String id;

	@JsonIgnore
	private Supplier<String> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The category's name."
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

	@GraphQLField(description = "The category's name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> name_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _name_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The number of times this category has been used in other assets."
	)
	public Integer getNumberOfTaxonomyCategories() {
		if (_numberOfTaxonomyCategoriesSupplier != null) {
			numberOfTaxonomyCategories =
				_numberOfTaxonomyCategoriesSupplier.get();

			_numberOfTaxonomyCategoriesSupplier = null;
		}

		return numberOfTaxonomyCategories;
	}

	public void setNumberOfTaxonomyCategories(
		Integer numberOfTaxonomyCategories) {

		this.numberOfTaxonomyCategories = numberOfTaxonomyCategories;

		_numberOfTaxonomyCategoriesSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfTaxonomyCategories(
		UnsafeSupplier<Integer, Exception>
			numberOfTaxonomyCategoriesUnsafeSupplier) {

		_numberOfTaxonomyCategoriesSupplier = () -> {
			try {
				return numberOfTaxonomyCategoriesUnsafeSupplier.get();
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
		description = "The number of times this category has been used in other assets."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer numberOfTaxonomyCategories;

	@JsonIgnore
	private Supplier<Integer> _numberOfTaxonomyCategoriesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The category's parent category, if it exists."
	)
	@Valid
	public ParentTaxonomyCategory getParentTaxonomyCategory() {
		if (_parentTaxonomyCategorySupplier != null) {
			parentTaxonomyCategory = _parentTaxonomyCategorySupplier.get();

			_parentTaxonomyCategorySupplier = null;
		}

		return parentTaxonomyCategory;
	}

	public void setParentTaxonomyCategory(
		ParentTaxonomyCategory parentTaxonomyCategory) {

		this.parentTaxonomyCategory = parentTaxonomyCategory;

		_parentTaxonomyCategorySupplier = null;
	}

	@JsonIgnore
	public void setParentTaxonomyCategory(
		UnsafeSupplier<ParentTaxonomyCategory, Exception>
			parentTaxonomyCategoryUnsafeSupplier) {

		_parentTaxonomyCategorySupplier = () -> {
			try {
				return parentTaxonomyCategoryUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The category's parent category, if it exists.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ParentTaxonomyCategory parentTaxonomyCategory;

	@JsonIgnore
	private Supplier<ParentTaxonomyCategory> _parentTaxonomyCategorySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The parent category's `TaxonomyVocabulary`, if such a parent category exists."
	)
	@Valid
	public ParentTaxonomyVocabulary getParentTaxonomyVocabulary() {
		if (_parentTaxonomyVocabularySupplier != null) {
			parentTaxonomyVocabulary = _parentTaxonomyVocabularySupplier.get();

			_parentTaxonomyVocabularySupplier = null;
		}

		return parentTaxonomyVocabulary;
	}

	public void setParentTaxonomyVocabulary(
		ParentTaxonomyVocabulary parentTaxonomyVocabulary) {

		this.parentTaxonomyVocabulary = parentTaxonomyVocabulary;

		_parentTaxonomyVocabularySupplier = null;
	}

	@JsonIgnore
	public void setParentTaxonomyVocabulary(
		UnsafeSupplier<ParentTaxonomyVocabulary, Exception>
			parentTaxonomyVocabularyUnsafeSupplier) {

		_parentTaxonomyVocabularySupplier = () -> {
			try {
				return parentTaxonomyVocabularyUnsafeSupplier.get();
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
		description = "The parent category's `TaxonomyVocabulary`, if such a parent category exists."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ParentTaxonomyVocabulary parentTaxonomyVocabulary;

	@JsonIgnore
	private Supplier<ParentTaxonomyVocabulary>
		_parentTaxonomyVocabularySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The path of the category."
	)
	public String getPath() {
		if (_pathSupplier != null) {
			path = _pathSupplier.get();

			_pathSupplier = null;
		}

		return path;
	}

	public void setPath(String path) {
		this.path = path;

		_pathSupplier = null;
	}

	@JsonIgnore
	public void setPath(UnsafeSupplier<String, Exception> pathUnsafeSupplier) {
		_pathSupplier = () -> {
			try {
				return pathUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The path of the category.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String path;

	@JsonIgnore
	private Supplier<String> _pathSupplier;

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
		description = "The external reference code of the site to which this category is scoped."
	)
	public String getSiteExternalReferenceCode() {
		if (_siteExternalReferenceCodeSupplier != null) {
			siteExternalReferenceCode =
				_siteExternalReferenceCodeSupplier.get();

			_siteExternalReferenceCodeSupplier = null;
		}

		return siteExternalReferenceCode;
	}

	public void setSiteExternalReferenceCode(String siteExternalReferenceCode) {
		this.siteExternalReferenceCode = siteExternalReferenceCode;

		_siteExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setSiteExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			siteExternalReferenceCodeUnsafeSupplier) {

		_siteExternalReferenceCodeSupplier = () -> {
			try {
				return siteExternalReferenceCodeUnsafeSupplier.get();
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
		description = "The external reference code of the site to which this category is scoped."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String siteExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _siteExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The ID of the site to which this category is scoped."
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

	@GraphQLField(
		description = "The ID of the site to which this category is scoped."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long siteId;

	@JsonIgnore
	private Supplier<Long> _siteIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The category's properties."
	)
	@Valid
	public TaxonomyCategoryProperty[] getTaxonomyCategoryProperties() {
		if (_taxonomyCategoryPropertiesSupplier != null) {
			taxonomyCategoryProperties =
				_taxonomyCategoryPropertiesSupplier.get();

			_taxonomyCategoryPropertiesSupplier = null;
		}

		return taxonomyCategoryProperties;
	}

	public void setTaxonomyCategoryProperties(
		TaxonomyCategoryProperty[] taxonomyCategoryProperties) {

		this.taxonomyCategoryProperties = taxonomyCategoryProperties;

		_taxonomyCategoryPropertiesSupplier = null;
	}

	@JsonIgnore
	public void setTaxonomyCategoryProperties(
		UnsafeSupplier<TaxonomyCategoryProperty[], Exception>
			taxonomyCategoryPropertiesUnsafeSupplier) {

		_taxonomyCategoryPropertiesSupplier = () -> {
			try {
				return taxonomyCategoryPropertiesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The category's properties.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected TaxonomyCategoryProperty[] taxonomyCategoryProperties;

	@JsonIgnore
	private Supplier<TaxonomyCategoryProperty[]>
		_taxonomyCategoryPropertiesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getTaxonomyCategoryUsageCount() {
		if (_taxonomyCategoryUsageCountSupplier != null) {
			taxonomyCategoryUsageCount =
				_taxonomyCategoryUsageCountSupplier.get();

			_taxonomyCategoryUsageCountSupplier = null;
		}

		return taxonomyCategoryUsageCount;
	}

	public void setTaxonomyCategoryUsageCount(
		Integer taxonomyCategoryUsageCount) {

		this.taxonomyCategoryUsageCount = taxonomyCategoryUsageCount;

		_taxonomyCategoryUsageCountSupplier = null;
	}

	@JsonIgnore
	public void setTaxonomyCategoryUsageCount(
		UnsafeSupplier<Integer, Exception>
			taxonomyCategoryUsageCountUnsafeSupplier) {

		_taxonomyCategoryUsageCountSupplier = () -> {
			try {
				return taxonomyCategoryUsageCountUnsafeSupplier.get();
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
	protected Integer taxonomyCategoryUsageCount;

	@JsonIgnore
	private Supplier<Integer> _taxonomyCategoryUsageCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The `TaxonomyVocabulary` id, only if the category does not have a parent category."
	)
	public Long getTaxonomyVocabularyId() {
		if (_taxonomyVocabularyIdSupplier != null) {
			taxonomyVocabularyId = _taxonomyVocabularyIdSupplier.get();

			_taxonomyVocabularyIdSupplier = null;
		}

		return taxonomyVocabularyId;
	}

	public void setTaxonomyVocabularyId(Long taxonomyVocabularyId) {
		this.taxonomyVocabularyId = taxonomyVocabularyId;

		_taxonomyVocabularyIdSupplier = null;
	}

	@JsonIgnore
	public void setTaxonomyVocabularyId(
		UnsafeSupplier<Long, Exception> taxonomyVocabularyIdUnsafeSupplier) {

		_taxonomyVocabularyIdSupplier = () -> {
			try {
				return taxonomyVocabularyIdUnsafeSupplier.get();
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
		description = "The `TaxonomyVocabulary` id, only if the category does not have a parent category."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long taxonomyVocabularyId;

	@JsonIgnore
	private Supplier<Long> _taxonomyVocabularyIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A write-only property that specifies the category's default permissions."
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
		description = "A write-only property that specifies the category's default permissions."
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

		if (!(object instanceof TaxonomyCategory)) {
			return false;
		}

		TaxonomyCategory taxonomyCategory = (TaxonomyCategory)object;

		return Objects.equals(toString(), taxonomyCategory.toString());
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

		Creator creator = getCreator();

		if (creator != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(creator));
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

		String id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(id));

			sb.append("\"");
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

		Integer numberOfTaxonomyCategories = getNumberOfTaxonomyCategories();

		if (numberOfTaxonomyCategories != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfTaxonomyCategories\": ");

			sb.append(numberOfTaxonomyCategories);
		}

		ParentTaxonomyCategory parentTaxonomyCategory =
			getParentTaxonomyCategory();

		if (parentTaxonomyCategory != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentTaxonomyCategory\": ");

			sb.append(String.valueOf(parentTaxonomyCategory));
		}

		ParentTaxonomyVocabulary parentTaxonomyVocabulary =
			getParentTaxonomyVocabulary();

		if (parentTaxonomyVocabulary != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentTaxonomyVocabulary\": ");

			sb.append(String.valueOf(parentTaxonomyVocabulary));
		}

		String path = getPath();

		if (path != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"path\": ");

			sb.append("\"");

			sb.append(_escape(path));

			sb.append("\"");
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

		String siteExternalReferenceCode = getSiteExternalReferenceCode();

		if (siteExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(siteExternalReferenceCode));

			sb.append("\"");
		}

		Long siteId = getSiteId();

		if (siteId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(siteId);
		}

		TaxonomyCategoryProperty[] taxonomyCategoryProperties =
			getTaxonomyCategoryProperties();

		if (taxonomyCategoryProperties != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryProperties\": ");

			sb.append("[");

			for (int i = 0; i < taxonomyCategoryProperties.length; i++) {
				sb.append(String.valueOf(taxonomyCategoryProperties[i]));

				if ((i + 1) < taxonomyCategoryProperties.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Integer taxonomyCategoryUsageCount = getTaxonomyCategoryUsageCount();

		if (taxonomyCategoryUsageCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryUsageCount\": ");

			sb.append(taxonomyCategoryUsageCount);
		}

		Long taxonomyVocabularyId = getTaxonomyVocabularyId();

		if (taxonomyVocabularyId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyVocabularyId\": ");

			sb.append(taxonomyVocabularyId);
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
		defaultValue = "com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyCategory",
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