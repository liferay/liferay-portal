/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.dto.v1_0;

import com.liferay.exportimport.rest.client.function.UnsafeSupplier;
import com.liferay.exportimport.rest.client.serdes.v1_0.SyncProcessSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class SyncProcess implements Cloneable, Serializable {

	public static SyncProcess toDTO(String json) {
		return SyncProcessSerDes.toDTO(json);
	}

	public Long getSiteTemplateId() {
		return siteTemplateId;
	}

	public void setSiteTemplateId(Long siteTemplateId) {
		this.siteTemplateId = siteTemplateId;
	}

	public void setSiteTemplateId(
		UnsafeSupplier<Long, Exception> siteTemplateIdUnsafeSupplier) {

		try {
			siteTemplateId = siteTemplateIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long siteTemplateId;

	public String getSiteTemplateName() {
		return siteTemplateName;
	}

	public void setSiteTemplateName(String siteTemplateName) {
		this.siteTemplateName = siteTemplateName;
	}

	public void setSiteTemplateName(
		UnsafeSupplier<String, Exception> siteTemplateNameUnsafeSupplier) {

		try {
			siteTemplateName = siteTemplateNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String siteTemplateName;

	@Override
	public SyncProcess clone() throws CloneNotSupportedException {
		return (SyncProcess)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SyncProcess)) {
			return false;
		}

		SyncProcess syncProcess = (SyncProcess)object;

		return Objects.equals(toString(), syncProcess.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SyncProcessSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1087434144