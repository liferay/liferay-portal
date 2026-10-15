/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.dto.v1_0;

import com.liferay.exportimport.rest.client.function.UnsafeSupplier;
import com.liferay.exportimport.rest.client.serdes.v1_0.PortletDataHandlerSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class PortletDataHandler implements Cloneable, Serializable {

	public static PortletDataHandler toDTO(String json) {
		return PortletDataHandlerSerDes.toDTO(json);
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String description;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		try {
			label = labelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String label;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public PortletDataHandlerControl[] getPortletDataHandlerControls() {
		return portletDataHandlerControls;
	}

	public void setPortletDataHandlerControls(
		PortletDataHandlerControl[] portletDataHandlerControls) {

		this.portletDataHandlerControls = portletDataHandlerControls;
	}

	public void setPortletDataHandlerControls(
		UnsafeSupplier<PortletDataHandlerControl[], Exception>
			portletDataHandlerControlsUnsafeSupplier) {

		try {
			portletDataHandlerControls =
				portletDataHandlerControlsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PortletDataHandlerControl[] portletDataHandlerControls;

	public String getSectionKey() {
		return sectionKey;
	}

	public void setSectionKey(String sectionKey) {
		this.sectionKey = sectionKey;
	}

	public void setSectionKey(
		UnsafeSupplier<String, Exception> sectionKeyUnsafeSupplier) {

		try {
			sectionKey = sectionKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String sectionKey;

	public String getSectionLabel() {
		return sectionLabel;
	}

	public void setSectionLabel(String sectionLabel) {
		this.sectionLabel = sectionLabel;
	}

	public void setSectionLabel(
		UnsafeSupplier<String, Exception> sectionLabelUnsafeSupplier) {

		try {
			sectionLabel = sectionLabelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String sectionLabel;

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

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setTag(UnsafeSupplier<String, Exception> tagUnsafeSupplier) {
		try {
			tag = tagUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String tag;

	@Override
	public PortletDataHandler clone() throws CloneNotSupportedException {
		return (PortletDataHandler)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PortletDataHandler)) {
			return false;
		}

		PortletDataHandler portletDataHandler = (PortletDataHandler)object;

		return Objects.equals(toString(), portletDataHandler.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PortletDataHandlerSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:83091401