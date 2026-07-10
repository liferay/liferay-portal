/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.ResourceFolderSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class ResourceFolder implements Cloneable, Serializable {

	public static ResourceFolder toDTO(String json) {
		return ResourceFolderSerDes.toDTO(json);
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

	public FragmentSet getFragmentSet() {
		return fragmentSet;
	}

	public void setFragmentSet(FragmentSet fragmentSet) {
		this.fragmentSet = fragmentSet;
	}

	public void setFragmentSet(
		UnsafeSupplier<FragmentSet, Exception> fragmentSetUnsafeSupplier) {

		try {
			fragmentSet = fragmentSetUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FragmentSet fragmentSet;

	public String getFragmentSetExternalReferenceCode() {
		return fragmentSetExternalReferenceCode;
	}

	public void setFragmentSetExternalReferenceCode(
		String fragmentSetExternalReferenceCode) {

		this.fragmentSetExternalReferenceCode =
			fragmentSetExternalReferenceCode;
	}

	public void setFragmentSetExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			fragmentSetExternalReferenceCodeUnsafeSupplier) {

		try {
			fragmentSetExternalReferenceCode =
				fragmentSetExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String fragmentSetExternalReferenceCode;

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

	public ResourceFolder getParentResourceFolder() {
		return parentResourceFolder;
	}

	public void setParentResourceFolder(ResourceFolder parentResourceFolder) {
		this.parentResourceFolder = parentResourceFolder;
	}

	public void setParentResourceFolder(
		UnsafeSupplier<ResourceFolder, Exception>
			parentResourceFolderUnsafeSupplier) {

		try {
			parentResourceFolder = parentResourceFolderUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ResourceFolder parentResourceFolder;

	public String getParentResourceFolderExternalReferenceCode() {
		return parentResourceFolderExternalReferenceCode;
	}

	public void setParentResourceFolderExternalReferenceCode(
		String parentResourceFolderExternalReferenceCode) {

		this.parentResourceFolderExternalReferenceCode =
			parentResourceFolderExternalReferenceCode;
	}

	public void setParentResourceFolderExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			parentResourceFolderExternalReferenceCodeUnsafeSupplier) {

		try {
			parentResourceFolderExternalReferenceCode =
				parentResourceFolderExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String parentResourceFolderExternalReferenceCode;

	@Override
	public ResourceFolder clone() throws CloneNotSupportedException {
		return (ResourceFolder)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ResourceFolder)) {
			return false;
		}

		ResourceFolder resourceFolder = (ResourceFolder)object;

		return Objects.equals(toString(), resourceFolder.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ResourceFolderSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-241184010