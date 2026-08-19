/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.client.dto.v1_0;

import com.liferay.headless.cms.client.function.UnsafeSupplier;
import com.liferay.headless.cms.client.serdes.v1_0.BrokenLinkAssetSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Crescenzo Rega
 * @generated
 */
@Generated("")
public class BrokenLinkAsset implements Cloneable, Serializable {

	public static BrokenLinkAsset toDTO(String json) {
		return BrokenLinkAssetSerDes.toDTO(json);
	}

	public String getBrokenLinkTitle() {
		return brokenLinkTitle;
	}

	public void setBrokenLinkTitle(String brokenLinkTitle) {
		this.brokenLinkTitle = brokenLinkTitle;
	}

	public void setBrokenLinkTitle(
		UnsafeSupplier<String, Exception> brokenLinkTitleUnsafeSupplier) {

		try {
			brokenLinkTitle = brokenLinkTitleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String brokenLinkTitle;

	public Long getBrokenLinksCount() {
		return brokenLinksCount;
	}

	public void setBrokenLinksCount(Long brokenLinksCount) {
		this.brokenLinksCount = brokenLinksCount;
	}

	public void setBrokenLinksCount(
		UnsafeSupplier<Long, Exception> brokenLinksCountUnsafeSupplier) {

		try {
			brokenLinksCount = brokenLinksCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long brokenLinksCount;

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public void setHref(UnsafeSupplier<String, Exception> hrefUnsafeSupplier) {
		try {
			href = hrefUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String href;

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

	public String getObjectDefinitionExternalReferenceCode() {
		return objectDefinitionExternalReferenceCode;
	}

	public void setObjectDefinitionExternalReferenceCode(
		String objectDefinitionExternalReferenceCode) {

		this.objectDefinitionExternalReferenceCode =
			objectDefinitionExternalReferenceCode;
	}

	public void setObjectDefinitionExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			objectDefinitionExternalReferenceCodeUnsafeSupplier) {

		try {
			objectDefinitionExternalReferenceCode =
				objectDefinitionExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String objectDefinitionExternalReferenceCode;

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
	public BrokenLinkAsset clone() throws CloneNotSupportedException {
		return (BrokenLinkAsset)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BrokenLinkAsset)) {
			return false;
		}

		BrokenLinkAsset brokenLinkAsset = (BrokenLinkAsset)object;

		return Objects.equals(toString(), brokenLinkAsset.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return BrokenLinkAssetSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:834820098