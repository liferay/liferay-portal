/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.SpecificationSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class Specification implements Cloneable, Serializable {

	public static Specification toDTO(String json) {
		return SpecificationSerDes.toDTO(json);
	}

	public Map<String, String> getDescription() {
		return description;
	}

	public void setDescription(Map<String, String> description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<Map<String, String>, Exception>
			descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> description;

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

	public Boolean getFacetable() {
		return facetable;
	}

	public void setFacetable(Boolean facetable) {
		this.facetable = facetable;
	}

	public void setFacetable(
		UnsafeSupplier<Boolean, Exception> facetableUnsafeSupplier) {

		try {
			facetable = facetableUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean facetable;

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

	public Long getListTypeDefinitionId() {
		return listTypeDefinitionId;
	}

	public void setListTypeDefinitionId(Long listTypeDefinitionId) {
		this.listTypeDefinitionId = listTypeDefinitionId;
	}

	public void setListTypeDefinitionId(
		UnsafeSupplier<Long, Exception> listTypeDefinitionIdUnsafeSupplier) {

		try {
			listTypeDefinitionId = listTypeDefinitionIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long listTypeDefinitionId;

	public Long[] getListTypeDefinitionIds() {
		return listTypeDefinitionIds;
	}

	public void setListTypeDefinitionIds(Long[] listTypeDefinitionIds) {
		this.listTypeDefinitionIds = listTypeDefinitionIds;
	}

	public void setListTypeDefinitionIds(
		UnsafeSupplier<Long[], Exception> listTypeDefinitionIdsUnsafeSupplier) {

		try {
			listTypeDefinitionIds = listTypeDefinitionIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long[] listTypeDefinitionIds;

	public OptionCategory getOptionCategory() {
		return optionCategory;
	}

	public void setOptionCategory(OptionCategory optionCategory) {
		this.optionCategory = optionCategory;
	}

	public void setOptionCategory(
		UnsafeSupplier<OptionCategory, Exception>
			optionCategoryUnsafeSupplier) {

		try {
			optionCategory = optionCategoryUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected OptionCategory optionCategory;

	public Double getPriority() {
		return priority;
	}

	public void setPriority(Double priority) {
		this.priority = priority;
	}

	public void setPriority(
		UnsafeSupplier<Double, Exception> priorityUnsafeSupplier) {

		try {
			priority = priorityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double priority;

	public Map<String, String> getTitle() {
		return title;
	}

	public void setTitle(Map<String, String> title) {
		this.title = title;
	}

	public void setTitle(
		UnsafeSupplier<Map<String, String>, Exception> titleUnsafeSupplier) {

		try {
			title = titleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> title;

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public void setVisible(
		UnsafeSupplier<Boolean, Exception> visibleUnsafeSupplier) {

		try {
			visible = visibleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean visible;

	@Override
	public Specification clone() throws CloneNotSupportedException {
		return (Specification)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Specification)) {
			return false;
		}

		Specification specification = (Specification)object;

		return Objects.equals(toString(), specification.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SpecificationSerDes.toJSON(this);
	}

}