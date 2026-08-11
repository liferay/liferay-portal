/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.dto.v1_0;

import com.liferay.exportimport.rest.client.function.UnsafeSupplier;
import com.liferay.exportimport.rest.client.serdes.v1_0.PublishPreviewSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class PublishPreview implements Cloneable, Serializable {

	public static PublishPreview toDTO(String json) {
		return PublishPreviewSerDes.toDTO(json);
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

	public PreviewPortletDataHandlerSection[]
		getPreviewPortletDataHandlerSections() {

		return previewPortletDataHandlerSections;
	}

	public void setPreviewPortletDataHandlerSections(
		PreviewPortletDataHandlerSection[] previewPortletDataHandlerSections) {

		this.previewPortletDataHandlerSections =
			previewPortletDataHandlerSections;
	}

	public void setPreviewPortletDataHandlerSections(
		UnsafeSupplier<PreviewPortletDataHandlerSection[], Exception>
			previewPortletDataHandlerSectionsUnsafeSupplier) {

		try {
			previewPortletDataHandlerSections =
				previewPortletDataHandlerSectionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PreviewPortletDataHandlerSection[]
		previewPortletDataHandlerSections;

	@Override
	public PublishPreview clone() throws CloneNotSupportedException {
		return (PublishPreview)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PublishPreview)) {
			return false;
		}

		PublishPreview publishPreview = (PublishPreview)object;

		return Objects.equals(toString(), publishPreview.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PublishPreviewSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-630006356