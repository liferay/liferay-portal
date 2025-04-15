/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.DisplayPageTemplateFolderSerDes;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class DisplayPageTemplateFolder implements Cloneable, Serializable {

	public static DisplayPageTemplateFolder toDTO(String json) {
		return DisplayPageTemplateFolderSerDes.toDTO(json);
	}

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

	public String getParentDisplayPageTemplateFolderExternalReferenceCode() {
		return parentDisplayPageTemplateFolderExternalReferenceCode;
	}

	public void setParentDisplayPageTemplateFolderExternalReferenceCode(
		String parentDisplayPageTemplateFolderExternalReferenceCode) {

		this.parentDisplayPageTemplateFolderExternalReferenceCode =
			parentDisplayPageTemplateFolderExternalReferenceCode;
	}

	public void setParentDisplayPageTemplateFolderExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			parentDisplayPageTemplateFolderExternalReferenceCodeUnsafeSupplier) {

		try {
			parentDisplayPageTemplateFolderExternalReferenceCode =
				parentDisplayPageTemplateFolderExternalReferenceCodeUnsafeSupplier.
					get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String parentDisplayPageTemplateFolderExternalReferenceCode;

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
	public DisplayPageTemplateFolder clone() throws CloneNotSupportedException {
		return (DisplayPageTemplateFolder)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DisplayPageTemplateFolder)) {
			return false;
		}

		DisplayPageTemplateFolder displayPageTemplateFolder =
			(DisplayPageTemplateFolder)object;

		return Objects.equals(toString(), displayPageTemplateFolder.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DisplayPageTemplateFolderSerDes.toJSON(this);
	}

}