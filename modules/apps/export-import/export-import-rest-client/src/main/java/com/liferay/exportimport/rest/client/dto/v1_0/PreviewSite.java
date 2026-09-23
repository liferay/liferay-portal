/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.dto.v1_0;

import com.liferay.exportimport.rest.client.function.UnsafeSupplier;
import com.liferay.exportimport.rest.client.serdes.v1_0.PreviewSiteSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class PreviewSite implements Cloneable, Serializable {

	public static PreviewSite toDTO(String json) {
		return PreviewSiteSerDes.toDTO(json);
	}

	public Integer getChildSitesCount() {
		return childSitesCount;
	}

	public void setChildSitesCount(Integer childSitesCount) {
		this.childSitesCount = childSitesCount;
	}

	public void setChildSitesCount(
		UnsafeSupplier<Integer, Exception> childSitesCountUnsafeSupplier) {

		try {
			childSitesCount = childSitesCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer childSitesCount;

	public String getDescriptiveName() {
		return descriptiveName;
	}

	public void setDescriptiveName(String descriptiveName) {
		this.descriptiveName = descriptiveName;
	}

	public void setDescriptiveName(
		UnsafeSupplier<String, Exception> descriptiveNameUnsafeSupplier) {

		try {
			descriptiveName = descriptiveNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String descriptiveName;

	public Boolean getExistsInInstance() {
		return existsInInstance;
	}

	public void setExistsInInstance(Boolean existsInInstance) {
		this.existsInInstance = existsInInstance;
	}

	public void setExistsInInstance(
		UnsafeSupplier<Boolean, Exception> existsInInstanceUnsafeSupplier) {

		try {
			existsInInstance = existsInInstanceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean existsInInstance;

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setPath(UnsafeSupplier<String, Exception> pathUnsafeSupplier) {
		try {
			path = pathUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String path;

	@Override
	public PreviewSite clone() throws CloneNotSupportedException {
		return (PreviewSite)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PreviewSite)) {
			return false;
		}

		PreviewSite previewSite = (PreviewSite)object;

		return Objects.equals(toString(), previewSite.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PreviewSiteSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:793238128