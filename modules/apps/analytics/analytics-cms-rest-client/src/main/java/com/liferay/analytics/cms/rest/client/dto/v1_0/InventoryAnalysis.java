/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.dto.v1_0;

import com.liferay.analytics.cms.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.cms.rest.client.serdes.v1_0.InventoryAnalysisSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rachael Koestartyo
 * @generated
 */
@Generated("")
public class InventoryAnalysis implements Cloneable, Serializable {

	public static InventoryAnalysis toDTO(String json) {
		return InventoryAnalysisSerDes.toDTO(json);
	}

	public InventoryAnalysisItem[] getInventoryAnalysisItems() {
		return inventoryAnalysisItems;
	}

	public void setInventoryAnalysisItems(
		InventoryAnalysisItem[] inventoryAnalysisItems) {

		this.inventoryAnalysisItems = inventoryAnalysisItems;
	}

	public void setInventoryAnalysisItems(
		UnsafeSupplier<InventoryAnalysisItem[], Exception>
			inventoryAnalysisItemsUnsafeSupplier) {

		try {
			inventoryAnalysisItems = inventoryAnalysisItemsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected InventoryAnalysisItem[] inventoryAnalysisItems;

	public Long getInventoryAnalysisItemsCount() {
		return inventoryAnalysisItemsCount;
	}

	public void setInventoryAnalysisItemsCount(
		Long inventoryAnalysisItemsCount) {

		this.inventoryAnalysisItemsCount = inventoryAnalysisItemsCount;
	}

	public void setInventoryAnalysisItemsCount(
		UnsafeSupplier<Long, Exception>
			inventoryAnalysisItemsCountUnsafeSupplier) {

		try {
			inventoryAnalysisItemsCount =
				inventoryAnalysisItemsCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long inventoryAnalysisItemsCount;

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public void setTotalCount(
		UnsafeSupplier<Long, Exception> totalCountUnsafeSupplier) {

		try {
			totalCount = totalCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long totalCount;

	@Override
	public InventoryAnalysis clone() throws CloneNotSupportedException {
		return (InventoryAnalysis)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof InventoryAnalysis)) {
			return false;
		}

		InventoryAnalysis inventoryAnalysis = (InventoryAnalysis)object;

		return Objects.equals(toString(), inventoryAnalysis.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return InventoryAnalysisSerDes.toJSON(this);
	}

}