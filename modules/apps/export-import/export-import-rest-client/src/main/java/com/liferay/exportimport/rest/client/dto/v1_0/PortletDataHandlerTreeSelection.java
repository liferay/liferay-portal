/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.dto.v1_0;

import com.liferay.exportimport.rest.client.function.UnsafeSupplier;
import com.liferay.exportimport.rest.client.serdes.v1_0.PortletDataHandlerTreeSelectionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class PortletDataHandlerTreeSelection
	extends PortletDataHandlerControl implements Cloneable, Serializable {

	public static PortletDataHandlerTreeSelection toDTO(String json) {
		return PortletDataHandlerTreeSelectionSerDes.toDTO(json);
	}

	public Long getAdditionCount() {
		return additionCount;
	}

	public void setAdditionCount(Long additionCount) {
		this.additionCount = additionCount;
	}

	public void setAdditionCount(
		UnsafeSupplier<Long, Exception> additionCountUnsafeSupplier) {

		try {
			additionCount = additionCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long additionCount;

	public Long getDeletionCount() {
		return deletionCount;
	}

	public void setDeletionCount(Long deletionCount) {
		this.deletionCount = deletionCount;
	}

	public void setDeletionCount(
		UnsafeSupplier<Long, Exception> deletionCountUnsafeSupplier) {

		try {
			deletionCount = deletionCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long deletionCount;

	public String[] getExcludedItemExternalReferenceCodes() {
		return excludedItemExternalReferenceCodes;
	}

	public void setExcludedItemExternalReferenceCodes(
		String[] excludedItemExternalReferenceCodes) {

		this.excludedItemExternalReferenceCodes =
			excludedItemExternalReferenceCodes;
	}

	public void setExcludedItemExternalReferenceCodes(
		UnsafeSupplier<String[], Exception>
			excludedItemExternalReferenceCodesUnsafeSupplier) {

		try {
			excludedItemExternalReferenceCodes =
				excludedItemExternalReferenceCodesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] excludedItemExternalReferenceCodes;

	public String[] getExcludedSubtreeExternalReferenceCodes() {
		return excludedSubtreeExternalReferenceCodes;
	}

	public void setExcludedSubtreeExternalReferenceCodes(
		String[] excludedSubtreeExternalReferenceCodes) {

		this.excludedSubtreeExternalReferenceCodes =
			excludedSubtreeExternalReferenceCodes;
	}

	public void setExcludedSubtreeExternalReferenceCodes(
		UnsafeSupplier<String[], Exception>
			excludedSubtreeExternalReferenceCodesUnsafeSupplier) {

		try {
			excludedSubtreeExternalReferenceCodes =
				excludedSubtreeExternalReferenceCodesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] excludedSubtreeExternalReferenceCodes;

	public String[] getItemExternalReferenceCodes() {
		return itemExternalReferenceCodes;
	}

	public void setItemExternalReferenceCodes(
		String[] itemExternalReferenceCodes) {

		this.itemExternalReferenceCodes = itemExternalReferenceCodes;
	}

	public void setItemExternalReferenceCodes(
		UnsafeSupplier<String[], Exception>
			itemExternalReferenceCodesUnsafeSupplier) {

		try {
			itemExternalReferenceCodes =
				itemExternalReferenceCodesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] itemExternalReferenceCodes;

	public Boolean getSelectAll() {
		return selectAll;
	}

	public void setSelectAll(Boolean selectAll) {
		this.selectAll = selectAll;
	}

	public void setSelectAll(
		UnsafeSupplier<Boolean, Exception> selectAllUnsafeSupplier) {

		try {
			selectAll = selectAllUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean selectAll;

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public void setSelected(
		UnsafeSupplier<Boolean, Exception> selectedUnsafeSupplier) {

		try {
			selected = selectedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean selected;

	public String[] getSubtreeExternalReferenceCodes() {
		return subtreeExternalReferenceCodes;
	}

	public void setSubtreeExternalReferenceCodes(
		String[] subtreeExternalReferenceCodes) {

		this.subtreeExternalReferenceCodes = subtreeExternalReferenceCodes;
	}

	public void setSubtreeExternalReferenceCodes(
		UnsafeSupplier<String[], Exception>
			subtreeExternalReferenceCodesUnsafeSupplier) {

		try {
			subtreeExternalReferenceCodes =
				subtreeExternalReferenceCodesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] subtreeExternalReferenceCodes;

	@Override
	public PortletDataHandlerTreeSelection clone()
		throws CloneNotSupportedException {

		return (PortletDataHandlerTreeSelection)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PortletDataHandlerTreeSelection)) {
			return false;
		}

		PortletDataHandlerTreeSelection portletDataHandlerTreeSelection =
			(PortletDataHandlerTreeSelection)object;

		return Objects.equals(
			toString(), portletDataHandlerTreeSelection.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PortletDataHandlerTreeSelectionSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:991003868