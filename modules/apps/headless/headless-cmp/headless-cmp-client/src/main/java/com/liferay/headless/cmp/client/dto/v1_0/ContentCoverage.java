/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.client.dto.v1_0;

import com.liferay.headless.cmp.client.function.UnsafeSupplier;
import com.liferay.headless.cmp.client.serdes.v1_0.ContentCoverageSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Carolina Barbosa
 * @generated
 */
@Generated("")
public class ContentCoverage implements Cloneable, Serializable {

	public static ContentCoverage toDTO(String json) {
		return ContentCoverageSerDes.toDTO(json);
	}

	public Long getAssetCount() {
		return assetCount;
	}

	public void setAssetCount(Long assetCount) {
		this.assetCount = assetCount;
	}

	public void setAssetCount(
		UnsafeSupplier<Long, Exception> assetCountUnsafeSupplier) {

		try {
			assetCount = assetCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long assetCount;

	public ContentCoverageEntry[] getContentCoverageEntries() {
		return contentCoverageEntries;
	}

	public void setContentCoverageEntries(
		ContentCoverageEntry[] contentCoverageEntries) {

		this.contentCoverageEntries = contentCoverageEntries;
	}

	public void setContentCoverageEntries(
		UnsafeSupplier<ContentCoverageEntry[], Exception>
			contentCoverageEntriesUnsafeSupplier) {

		try {
			contentCoverageEntries = contentCoverageEntriesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ContentCoverageEntry[] contentCoverageEntries;

	public FunnelStage[] getFunnelStages() {
		return funnelStages;
	}

	public void setFunnelStages(FunnelStage[] funnelStages) {
		this.funnelStages = funnelStages;
	}

	public void setFunnelStages(
		UnsafeSupplier<FunnelStage[], Exception> funnelStagesUnsafeSupplier) {

		try {
			funnelStages = funnelStagesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FunnelStage[] funnelStages;

	public Persona[] getPersonas() {
		return personas;
	}

	public void setPersonas(Persona[] personas) {
		this.personas = personas;
	}

	public void setPersonas(
		UnsafeSupplier<Persona[], Exception> personasUnsafeSupplier) {

		try {
			personas = personasUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Persona[] personas;

	@Override
	public ContentCoverage clone() throws CloneNotSupportedException {
		return (ContentCoverage)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ContentCoverage)) {
			return false;
		}

		ContentCoverage contentCoverage = (ContentCoverage)object;

		return Objects.equals(toString(), contentCoverage.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ContentCoverageSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:575390081