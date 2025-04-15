/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.client.dto.v1_0;

import com.liferay.headless.admin.taxonomy.client.function.UnsafeSupplier;
import com.liferay.headless.admin.taxonomy.client.serdes.v1_0.TaxonomyCategorySerDes;

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
public class TaxonomyCategory implements Cloneable, Serializable {

	public static TaxonomyCategory toDTO(String json) {
		return TaxonomyCategorySerDes.toDTO(json);
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String id;

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

	public Integer getNumberOfTaxonomyCategories() {
		return numberOfTaxonomyCategories;
	}

	public void setNumberOfTaxonomyCategories(
		Integer numberOfTaxonomyCategories) {

		this.numberOfTaxonomyCategories = numberOfTaxonomyCategories;
	}

	public void setNumberOfTaxonomyCategories(
		UnsafeSupplier<Integer, Exception>
			numberOfTaxonomyCategoriesUnsafeSupplier) {

		try {
			numberOfTaxonomyCategories =
				numberOfTaxonomyCategoriesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer numberOfTaxonomyCategories;

	public ParentTaxonomyCategory getParentTaxonomyCategory() {
		return parentTaxonomyCategory;
	}

	public void setParentTaxonomyCategory(
		ParentTaxonomyCategory parentTaxonomyCategory) {

		this.parentTaxonomyCategory = parentTaxonomyCategory;
	}

	public void setParentTaxonomyCategory(
		UnsafeSupplier<ParentTaxonomyCategory, Exception>
			parentTaxonomyCategoryUnsafeSupplier) {

		try {
			parentTaxonomyCategory = parentTaxonomyCategoryUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ParentTaxonomyCategory parentTaxonomyCategory;

	public ParentTaxonomyVocabulary getParentTaxonomyVocabulary() {
		return parentTaxonomyVocabulary;
	}

	public void setParentTaxonomyVocabulary(
		ParentTaxonomyVocabulary parentTaxonomyVocabulary) {

		this.parentTaxonomyVocabulary = parentTaxonomyVocabulary;
	}

	public void setParentTaxonomyVocabulary(
		UnsafeSupplier<ParentTaxonomyVocabulary, Exception>
			parentTaxonomyVocabularyUnsafeSupplier) {

		try {
			parentTaxonomyVocabulary =
				parentTaxonomyVocabularyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ParentTaxonomyVocabulary parentTaxonomyVocabulary;

	public com.liferay.headless.admin.taxonomy.client.permission.Permission[]
		getPermissions() {

		return permissions;
	}

	public void setPermissions(
		com.liferay.headless.admin.taxonomy.client.permission.Permission[]
			permissions) {

		this.permissions = permissions;
	}

	public void setPermissions(
		UnsafeSupplier
			<com.liferay.headless.admin.taxonomy.client.permission.Permission[],
			 Exception> permissionsUnsafeSupplier) {

		try {
			permissions = permissionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected com.liferay.headless.admin.taxonomy.client.permission.Permission[]
		permissions;

	public String getSiteExternalReferenceCode() {
		return siteExternalReferenceCode;
	}

	public void setSiteExternalReferenceCode(String siteExternalReferenceCode) {
		this.siteExternalReferenceCode = siteExternalReferenceCode;
	}

	public void setSiteExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			siteExternalReferenceCodeUnsafeSupplier) {

		try {
			siteExternalReferenceCode =
				siteExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String siteExternalReferenceCode;

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

	public TaxonomyCategoryProperty[] getTaxonomyCategoryProperties() {
		return taxonomyCategoryProperties;
	}

	public void setTaxonomyCategoryProperties(
		TaxonomyCategoryProperty[] taxonomyCategoryProperties) {

		this.taxonomyCategoryProperties = taxonomyCategoryProperties;
	}

	public void setTaxonomyCategoryProperties(
		UnsafeSupplier<TaxonomyCategoryProperty[], Exception>
			taxonomyCategoryPropertiesUnsafeSupplier) {

		try {
			taxonomyCategoryProperties =
				taxonomyCategoryPropertiesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected TaxonomyCategoryProperty[] taxonomyCategoryProperties;

	public Integer getTaxonomyCategoryUsageCount() {
		return taxonomyCategoryUsageCount;
	}

	public void setTaxonomyCategoryUsageCount(
		Integer taxonomyCategoryUsageCount) {

		this.taxonomyCategoryUsageCount = taxonomyCategoryUsageCount;
	}

	public void setTaxonomyCategoryUsageCount(
		UnsafeSupplier<Integer, Exception>
			taxonomyCategoryUsageCountUnsafeSupplier) {

		try {
			taxonomyCategoryUsageCount =
				taxonomyCategoryUsageCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer taxonomyCategoryUsageCount;

	public Long getTaxonomyVocabularyId() {
		return taxonomyVocabularyId;
	}

	public void setTaxonomyVocabularyId(Long taxonomyVocabularyId) {
		this.taxonomyVocabularyId = taxonomyVocabularyId;
	}

	public void setTaxonomyVocabularyId(
		UnsafeSupplier<Long, Exception> taxonomyVocabularyIdUnsafeSupplier) {

		try {
			taxonomyVocabularyId = taxonomyVocabularyIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long taxonomyVocabularyId;

	public ViewableBy getViewableBy() {
		return viewableBy;
	}

	public String getViewableByAsString() {
		if (viewableBy == null) {
			return null;
		}

		return viewableBy.toString();
	}

	public void setViewableBy(ViewableBy viewableBy) {
		this.viewableBy = viewableBy;
	}

	public void setViewableBy(
		UnsafeSupplier<ViewableBy, Exception> viewableByUnsafeSupplier) {

		try {
			viewableBy = viewableByUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ViewableBy viewableBy;

	@Override
	public TaxonomyCategory clone() throws CloneNotSupportedException {
		return (TaxonomyCategory)super.clone();
	}

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
		return TaxonomyCategorySerDes.toJSON(this);
	}

	public static enum ViewableBy {

		ANYONE("Anyone"), MEMBERS("Members"), OWNER("Owner");

		public static ViewableBy create(String value) {
			for (ViewableBy viewableBy : values()) {
				if (Objects.equals(viewableBy.getValue(), value) ||
					Objects.equals(viewableBy.name(), value)) {

					return viewableBy;
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

		private ViewableBy(String value) {
			_value = value;
		}

		private final String _value;

	}

}