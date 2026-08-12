/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.pim.client.dto.v1_0;

import com.liferay.headless.pim.client.function.UnsafeSupplier;
import com.liferay.headless.pim.client.serdes.v1_0.LinkSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Stefano Motta
 * @generated
 */
@Generated("")
public class Link implements Cloneable, Serializable {

	public static Link toDTO(String json) {
		return LinkSerDes.toDTO(json);
	}

	public LinkReference getSourceLinkReference() {
		return sourceLinkReference;
	}

	public void setSourceLinkReference(LinkReference sourceLinkReference) {
		this.sourceLinkReference = sourceLinkReference;
	}

	public void setSourceLinkReference(
		UnsafeSupplier<LinkReference, Exception>
			sourceLinkReferenceUnsafeSupplier) {

		try {
			sourceLinkReference = sourceLinkReferenceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected LinkReference sourceLinkReference;

	public LinkReference[] getTargetLinkReferences() {
		return targetLinkReferences;
	}

	public void setTargetLinkReferences(LinkReference[] targetLinkReferences) {
		this.targetLinkReferences = targetLinkReferences;
	}

	public void setTargetLinkReferences(
		UnsafeSupplier<LinkReference[], Exception>
			targetLinkReferencesUnsafeSupplier) {

		try {
			targetLinkReferences = targetLinkReferencesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected LinkReference[] targetLinkReferences;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<String, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String type;

	@Override
	public Link clone() throws CloneNotSupportedException {
		return (Link)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Link)) {
			return false;
		}

		Link link = (Link)object;

		return Objects.equals(toString(), link.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return LinkSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1573249940