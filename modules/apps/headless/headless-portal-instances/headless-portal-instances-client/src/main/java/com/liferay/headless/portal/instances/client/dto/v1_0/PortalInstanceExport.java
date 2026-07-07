/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.client.dto.v1_0;

import com.liferay.headless.portal.instances.client.function.UnsafeSupplier;
import com.liferay.headless.portal.instances.client.serdes.v1_0.PortalInstanceExportSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alberto Chaparro
 * @generated
 */
@Generated("")
public class PortalInstanceExport implements Cloneable, Serializable {

	public static PortalInstanceExport toDTO(String json) {
		return PortalInstanceExportSerDes.toDTO(json);
	}

	public String getExportedPartitionName() {
		return exportedPartitionName;
	}

	public void setExportedPartitionName(String exportedPartitionName) {
		this.exportedPartitionName = exportedPartitionName;
	}

	public void setExportedPartitionName(
		UnsafeSupplier<String, Exception> exportedPartitionNameUnsafeSupplier) {

		try {
			exportedPartitionName = exportedPartitionNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String exportedPartitionName;

	public String getSourcePartitionName() {
		return sourcePartitionName;
	}

	public void setSourcePartitionName(String sourcePartitionName) {
		this.sourcePartitionName = sourcePartitionName;
	}

	public void setSourcePartitionName(
		UnsafeSupplier<String, Exception> sourcePartitionNameUnsafeSupplier) {

		try {
			sourcePartitionName = sourcePartitionNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String sourcePartitionName;

	@Override
	public PortalInstanceExport clone() throws CloneNotSupportedException {
		return (PortalInstanceExport)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PortalInstanceExport)) {
			return false;
		}

		PortalInstanceExport portalInstanceExport =
			(PortalInstanceExport)object;

		return Objects.equals(toString(), portalInstanceExport.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PortalInstanceExportSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-48228477