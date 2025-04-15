/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.client.dto.v1_0;

import com.liferay.headless.admin.content.client.function.UnsafeSupplier;
import com.liferay.headless.admin.content.client.serdes.v1_0.StructuredContentLinkSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class StructuredContentLink implements Cloneable, Serializable {

	public static StructuredContentLink toDTO(String json) {
		return StructuredContentLinkSerDes.toDTO(json);
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setContentType(
		UnsafeSupplier<String, Exception> contentTypeUnsafeSupplier) {

		try {
			contentType = contentTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String contentType;

	public StructuredContent getEmbeddedStructuredContent() {
		return embeddedStructuredContent;
	}

	public void setEmbeddedStructuredContent(
		StructuredContent embeddedStructuredContent) {

		this.embeddedStructuredContent = embeddedStructuredContent;
	}

	public void setEmbeddedStructuredContent(
		UnsafeSupplier<StructuredContent, Exception>
			embeddedStructuredContentUnsafeSupplier) {

		try {
			embeddedStructuredContent =
				embeddedStructuredContentUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected StructuredContent embeddedStructuredContent;

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitle(
		UnsafeSupplier<String, Exception> titleUnsafeSupplier) {

		try {
			title = titleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String title;

	@Override
	public StructuredContentLink clone() throws CloneNotSupportedException {
		return (StructuredContentLink)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof StructuredContentLink)) {
			return false;
		}

		StructuredContentLink structuredContentLink =
			(StructuredContentLink)object;

		return Objects.equals(toString(), structuredContentLink.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return StructuredContentLinkSerDes.toJSON(this);
	}

}