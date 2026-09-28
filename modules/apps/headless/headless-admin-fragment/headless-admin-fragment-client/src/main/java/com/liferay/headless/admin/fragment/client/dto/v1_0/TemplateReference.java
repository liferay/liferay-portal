/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.TemplateReferenceSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class TemplateReference implements Cloneable, Serializable {

	public static TemplateReference toDTO(String json) {
		return TemplateReferenceSerDes.toDTO(json);
	}

	public String getRendererKey() {
		return rendererKey;
	}

	public void setRendererKey(String rendererKey) {
		this.rendererKey = rendererKey;
	}

	public void setRendererKey(
		UnsafeSupplier<String, Exception> rendererKeyUnsafeSupplier) {

		try {
			rendererKey = rendererKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String rendererKey;

	public String getTemplateKey() {
		return templateKey;
	}

	public void setTemplateKey(String templateKey) {
		this.templateKey = templateKey;
	}

	public void setTemplateKey(
		UnsafeSupplier<String, Exception> templateKeyUnsafeSupplier) {

		try {
			templateKey = templateKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String templateKey;

	@Override
	public TemplateReference clone() throws CloneNotSupportedException {
		return (TemplateReference)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TemplateReference)) {
			return false;
		}

		TemplateReference templateReference = (TemplateReference)object;

		return Objects.equals(toString(), templateReference.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return TemplateReferenceSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1730472199