/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.dto.v1_0;

import com.liferay.headless.admin.user.client.function.UnsafeSupplier;
import com.liferay.headless.admin.user.client.serdes.v1_0.TaxonomyCategoryBriefSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class TaxonomyCategoryBrief implements Cloneable, Serializable {

	public static TaxonomyCategoryBrief toDTO(String json) {
		return TaxonomyCategoryBriefSerDes.toDTO(json);
	}

	public Object getEmbeddedTaxonomyCategory() {
		return embeddedTaxonomyCategory;
	}

	public void setEmbeddedTaxonomyCategory(Object embeddedTaxonomyCategory) {
		this.embeddedTaxonomyCategory = embeddedTaxonomyCategory;
	}

	public void setEmbeddedTaxonomyCategory(
		UnsafeSupplier<Object, Exception>
			embeddedTaxonomyCategoryUnsafeSupplier) {

		try {
			embeddedTaxonomyCategory =
				embeddedTaxonomyCategoryUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object embeddedTaxonomyCategory;

	public Long getTaxonomyCategoryId() {
		return taxonomyCategoryId;
	}

	public void setTaxonomyCategoryId(Long taxonomyCategoryId) {
		this.taxonomyCategoryId = taxonomyCategoryId;
	}

	public void setTaxonomyCategoryId(
		UnsafeSupplier<Long, Exception> taxonomyCategoryIdUnsafeSupplier) {

		try {
			taxonomyCategoryId = taxonomyCategoryIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long taxonomyCategoryId;

	public String getTaxonomyCategoryName() {
		return taxonomyCategoryName;
	}

	public void setTaxonomyCategoryName(String taxonomyCategoryName) {
		this.taxonomyCategoryName = taxonomyCategoryName;
	}

	public void setTaxonomyCategoryName(
		UnsafeSupplier<String, Exception> taxonomyCategoryNameUnsafeSupplier) {

		try {
			taxonomyCategoryName = taxonomyCategoryNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String taxonomyCategoryName;

	public Map<String, String> getTaxonomyCategoryName_i18n() {
		return taxonomyCategoryName_i18n;
	}

	public void setTaxonomyCategoryName_i18n(
		Map<String, String> taxonomyCategoryName_i18n) {

		this.taxonomyCategoryName_i18n = taxonomyCategoryName_i18n;
	}

	public void setTaxonomyCategoryName_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			taxonomyCategoryName_i18nUnsafeSupplier) {

		try {
			taxonomyCategoryName_i18n =
				taxonomyCategoryName_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> taxonomyCategoryName_i18n;

	public TaxonomyCategoryReference getTaxonomyCategoryReference() {
		return taxonomyCategoryReference;
	}

	public void setTaxonomyCategoryReference(
		TaxonomyCategoryReference taxonomyCategoryReference) {

		this.taxonomyCategoryReference = taxonomyCategoryReference;
	}

	public void setTaxonomyCategoryReference(
		UnsafeSupplier<TaxonomyCategoryReference, Exception>
			taxonomyCategoryReferenceUnsafeSupplier) {

		try {
			taxonomyCategoryReference =
				taxonomyCategoryReferenceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected TaxonomyCategoryReference taxonomyCategoryReference;

	@Override
	public TaxonomyCategoryBrief clone() throws CloneNotSupportedException {
		return (TaxonomyCategoryBrief)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TaxonomyCategoryBrief)) {
			return false;
		}

		TaxonomyCategoryBrief taxonomyCategoryBrief =
			(TaxonomyCategoryBrief)object;

		return Objects.equals(toString(), taxonomyCategoryBrief.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return TaxonomyCategoryBriefSerDes.toJSON(this);
	}

}