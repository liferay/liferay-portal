/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v1_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v1_0.HitSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class Hit implements Cloneable, Serializable {

	public static Hit toDTO(String json) {
		return HitSerDes.toDTO(json);
	}

	public Map<String, DocumentField> getDocumentFields() {
		return documentFields;
	}

	public void setDocumentFields(Map<String, DocumentField> documentFields) {
		this.documentFields = documentFields;
	}

	public void setDocumentFields(
		UnsafeSupplier<Map<String, DocumentField>, Exception>
			documentFieldsUnsafeSupplier) {

		try {
			documentFields = documentFieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, DocumentField> documentFields;

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public void setExplanation(
		UnsafeSupplier<String, Exception> explanationUnsafeSupplier) {

		try {
			explanation = explanationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String explanation;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String id;

	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	public void setScore(UnsafeSupplier<Float, Exception> scoreUnsafeSupplier) {
		try {
			score = scoreUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Float score;

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public void setVersion(
		UnsafeSupplier<Long, Exception> versionUnsafeSupplier) {

		try {
			version = versionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long version;

	@Override
	public Hit clone() throws CloneNotSupportedException {
		return (Hit)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Hit)) {
			return false;
		}

		Hit hit = (Hit)object;

		return Objects.equals(toString(), hit.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return HitSerDes.toJSON(this);
	}

}