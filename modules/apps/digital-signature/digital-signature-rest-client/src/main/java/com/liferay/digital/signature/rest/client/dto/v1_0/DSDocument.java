/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.client.dto.v1_0;

import com.liferay.digital.signature.rest.client.function.UnsafeSupplier;
import com.liferay.digital.signature.rest.client.serdes.v1_0.DSDocumentSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author José Abelenda
 * @generated
 */
@Generated("")
public class DSDocument implements Cloneable, Serializable {

	public static DSDocument toDTO(String json) {
		return DSDocumentSerDes.toDTO(json);
	}

	public String getAssignTabsToDSRecipientId() {
		return assignTabsToDSRecipientId;
	}

	public void setAssignTabsToDSRecipientId(String assignTabsToDSRecipientId) {
		this.assignTabsToDSRecipientId = assignTabsToDSRecipientId;
	}

	public void setAssignTabsToDSRecipientId(
		UnsafeSupplier<String, Exception>
			assignTabsToDSRecipientIdUnsafeSupplier) {

		try {
			assignTabsToDSRecipientId =
				assignTabsToDSRecipientIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String assignTabsToDSRecipientId;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setData(UnsafeSupplier<String, Exception> dataUnsafeSupplier) {
		try {
			data = dataUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String data;

	public String getFileEntryExternalReferenceCode() {
		return fileEntryExternalReferenceCode;
	}

	public void setFileEntryExternalReferenceCode(
		String fileEntryExternalReferenceCode) {

		this.fileEntryExternalReferenceCode = fileEntryExternalReferenceCode;
	}

	public void setFileEntryExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			fileEntryExternalReferenceCodeUnsafeSupplier) {

		try {
			fileEntryExternalReferenceCode =
				fileEntryExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String fileEntryExternalReferenceCode;

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public void setFileExtension(
		UnsafeSupplier<String, Exception> fileExtensionUnsafeSupplier) {

		try {
			fileExtension = fileExtensionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String fileExtension;

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

	public Boolean getTransformPDFFields() {
		return transformPDFFields;
	}

	public void setTransformPDFFields(Boolean transformPDFFields) {
		this.transformPDFFields = transformPDFFields;
	}

	public void setTransformPDFFields(
		UnsafeSupplier<Boolean, Exception> transformPDFFieldsUnsafeSupplier) {

		try {
			transformPDFFields = transformPDFFieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean transformPDFFields;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setUri(UnsafeSupplier<String, Exception> uriUnsafeSupplier) {
		try {
			uri = uriUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String uri;

	@Override
	public DSDocument clone() throws CloneNotSupportedException {
		return (DSDocument)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DSDocument)) {
			return false;
		}

		DSDocument dsDocument = (DSDocument)object;

		return Objects.equals(toString(), dsDocument.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DSDocumentSerDes.toJSON(this);
	}

}