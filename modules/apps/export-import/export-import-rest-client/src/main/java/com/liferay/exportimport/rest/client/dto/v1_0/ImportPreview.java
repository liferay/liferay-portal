/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.dto.v1_0;

import com.liferay.exportimport.rest.client.function.UnsafeSupplier;
import com.liferay.exportimport.rest.client.serdes.v1_0.ImportPreviewSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class ImportPreview implements Cloneable, Serializable {

	public static ImportPreview toDTO(String json) {
		return ImportPreviewSerDes.toDTO(json);
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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setAuthor(
		UnsafeSupplier<String, Exception> authorUnsafeSupplier) {

		try {
			author = authorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String author;

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

	public Date getExportDate() {
		return exportDate;
	}

	public void setExportDate(Date exportDate) {
		this.exportDate = exportDate;
	}

	public void setExportDate(
		UnsafeSupplier<Date, Exception> exportDateUnsafeSupplier) {

		try {
			exportDate = exportDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date exportDate;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFileName(
		UnsafeSupplier<String, Exception> fileNameUnsafeSupplier) {

		try {
			fileName = fileNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String fileName;

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public void setFileSize(
		UnsafeSupplier<Long, Exception> fileSizeUnsafeSupplier) {

		try {
			fileSize = fileSizeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long fileSize;

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

	public PreviewSite[] getPreviewSites() {
		return previewSites;
	}

	public void setPreviewSites(PreviewSite[] previewSites) {
		this.previewSites = previewSites;
	}

	public void setPreviewSites(
		UnsafeSupplier<PreviewSite[], Exception> previewSitesUnsafeSupplier) {

		try {
			previewSites = previewSitesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PreviewSite[] previewSites;

	@Override
	public ImportPreview clone() throws CloneNotSupportedException {
		return (ImportPreview)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ImportPreview)) {
			return false;
		}

		ImportPreview importPreview = (ImportPreview)object;

		return Objects.equals(toString(), importPreview.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ImportPreviewSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-831052032