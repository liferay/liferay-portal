/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.dto.v1_0;

import com.liferay.exportimport.rest.client.function.UnsafeSupplier;
import com.liferay.exportimport.rest.client.serdes.v1_0.ExportPreviewSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class ExportPreview implements Cloneable, Serializable {

	public static ExportPreview toDTO(String json) {
		return ExportPreviewSerDes.toDTO(json);
	}

	public Long getAdditionCount() {
		return additionCount;
	}

	public void setAdditionCount(Long additionCount) {
		this.additionCount = additionCount;
	}

	public void setAdditionCount(
		UnsafeSupplier<Long, Exception> additionCountUnsafeSupplier) {

		try {
			additionCount = additionCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long additionCount;

	public Long getDeletionCount() {
		return deletionCount;
	}

	public void setDeletionCount(Long deletionCount) {
		this.deletionCount = deletionCount;
	}

	public void setDeletionCount(
		UnsafeSupplier<Long, Exception> deletionCountUnsafeSupplier) {

		try {
			deletionCount = deletionCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long deletionCount;

	public PortletDataHandler[] getPortletDataHandlers() {
		return portletDataHandlers;
	}

	public void setPortletDataHandlers(
		PortletDataHandler[] portletDataHandlers) {

		this.portletDataHandlers = portletDataHandlers;
	}

	public void setPortletDataHandlers(
		UnsafeSupplier<PortletDataHandler[], Exception>
			portletDataHandlersUnsafeSupplier) {

		try {
			portletDataHandlers = portletDataHandlersUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PortletDataHandler[] portletDataHandlers;

	@Override
	public ExportPreview clone() throws CloneNotSupportedException {
		return (ExportPreview)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ExportPreview)) {
			return false;
		}

		ExportPreview exportPreview = (ExportPreview)object;

		return Objects.equals(toString(), exportPreview.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ExportPreviewSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1803643627