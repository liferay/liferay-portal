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

	public Boolean getAcceptAllLanguages() {
		return acceptAllLanguages;
	}

	public void setAcceptAllLanguages(Boolean acceptAllLanguages) {
		this.acceptAllLanguages = acceptAllLanguages;
	}

	public void setAcceptAllLanguages(
		UnsafeSupplier<Boolean, Exception> acceptAllLanguagesUnsafeSupplier) {

		try {
			acceptAllLanguages = acceptAllLanguagesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean acceptAllLanguages;

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

	public Object getEmbeddedNestedField() {
		return embeddedNestedField;
	}

	public void setEmbeddedNestedField(Object embeddedNestedField) {
		this.embeddedNestedField = embeddedNestedField;
	}

	public void setEmbeddedNestedField(
		UnsafeSupplier<Object, Exception> embeddedNestedFieldUnsafeSupplier) {

		try {
			embeddedNestedField = embeddedNestedFieldUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object embeddedNestedField;

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

	public String getNestedField1() {
		return nestedField1;
	}

	public void setNestedField1(String nestedField1) {
		this.nestedField1 = nestedField1;
	}

	public void setNestedField1(
		UnsafeSupplier<String, Exception> nestedField1UnsafeSupplier) {

		try {
			nestedField1 = nestedField1UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String nestedField1;

	public String getNestedField2() {
		return nestedField2;
	}

	public void setNestedField2(String nestedField2) {
		this.nestedField2 = nestedField2;
	}

	public void setNestedField2(
		UnsafeSupplier<String, Exception> nestedField2UnsafeSupplier) {

		try {
			nestedField2 = nestedField2UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String nestedField2;

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
// LIFERAY-REST-BUILDER-HASH:-1455668970