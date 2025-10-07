/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.dto.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.function.UnsafeSupplier;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.BatchTestEntitySerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class BatchTestEntity implements Cloneable, Serializable {

	public static BatchTestEntity toDTO(String json) {
		return BatchTestEntitySerDes.toDTO(json);
	}

	public
		com.liferay.portal.tools.rest.builder.test.client.custom.field.
			CustomField[] getCustomFields() {

		return customFields;
	}

	public void setCustomFields(
		com.liferay.portal.tools.rest.builder.test.client.custom.field.
			CustomField[] customFields) {

		this.customFields = customFields;
	}

	public void setCustomFields(
		UnsafeSupplier
			<com.liferay.portal.tools.rest.builder.test.client.custom.field.
				CustomField[],
			 Exception> customFieldsUnsafeSupplier) {

		try {
			customFields = customFieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected
		com.liferay.portal.tools.rest.builder.test.client.custom.field.
			CustomField[] customFields;

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

	public String getNestedField() {
		return nestedField;
	}

	public void setNestedField(String nestedField) {
		this.nestedField = nestedField;
	}

	public void setNestedField(
		UnsafeSupplier<String, Exception> nestedFieldUnsafeSupplier) {

		try {
			nestedField = nestedFieldUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String nestedField;

	public CompanyTestEntity getRelatedCompanyTestEntity() {
		return relatedCompanyTestEntity;
	}

	public void setRelatedCompanyTestEntity(
		CompanyTestEntity relatedCompanyTestEntity) {

		this.relatedCompanyTestEntity = relatedCompanyTestEntity;
	}

	public void setRelatedCompanyTestEntity(
		UnsafeSupplier<CompanyTestEntity, Exception>
			relatedCompanyTestEntityUnsafeSupplier) {

		try {
			relatedCompanyTestEntity =
				relatedCompanyTestEntityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected CompanyTestEntity relatedCompanyTestEntity;

	@Override
	public BatchTestEntity clone() throws CloneNotSupportedException {
		return (BatchTestEntity)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BatchTestEntity)) {
			return false;
		}

		BatchTestEntity batchTestEntity = (BatchTestEntity)object;

		return Objects.equals(toString(), batchTestEntity.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return BatchTestEntitySerDes.toJSON(this);
	}

}