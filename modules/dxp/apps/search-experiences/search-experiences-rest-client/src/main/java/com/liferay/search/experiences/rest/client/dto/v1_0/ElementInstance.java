/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v1_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v1_0.ElementInstanceSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class ElementInstance implements Cloneable, Serializable {

	public static ElementInstance toDTO(String json) {
		return ElementInstanceSerDes.toDTO(json);
	}

	public Configuration getConfigurationEntry() {
		return configurationEntry;
	}

	public void setConfigurationEntry(Configuration configurationEntry) {
		this.configurationEntry = configurationEntry;
	}

	public void setConfigurationEntry(
		UnsafeSupplier<Configuration, Exception>
			configurationEntryUnsafeSupplier) {

		try {
			configurationEntry = configurationEntryUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Configuration configurationEntry;

	public SXPElement getSxpElement() {
		return sxpElement;
	}

	public void setSxpElement(SXPElement sxpElement) {
		this.sxpElement = sxpElement;
	}

	public void setSxpElement(
		UnsafeSupplier<SXPElement, Exception> sxpElementUnsafeSupplier) {

		try {
			sxpElement = sxpElementUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected SXPElement sxpElement;

	public Long getSxpElementId() {
		return sxpElementId;
	}

	public void setSxpElementId(Long sxpElementId) {
		this.sxpElementId = sxpElementId;
	}

	public void setSxpElementId(
		UnsafeSupplier<Long, Exception> sxpElementIdUnsafeSupplier) {

		try {
			sxpElementId = sxpElementIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long sxpElementId;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<Integer, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer type;

	public Map<String, Object> getUiConfigurationValues() {
		return uiConfigurationValues;
	}

	public void setUiConfigurationValues(
		Map<String, Object> uiConfigurationValues) {

		this.uiConfigurationValues = uiConfigurationValues;
	}

	public void setUiConfigurationValues(
		UnsafeSupplier<Map<String, Object>, Exception>
			uiConfigurationValuesUnsafeSupplier) {

		try {
			uiConfigurationValues = uiConfigurationValuesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Object> uiConfigurationValues;

	@Override
	public ElementInstance clone() throws CloneNotSupportedException {
		return (ElementInstance)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ElementInstance)) {
			return false;
		}

		ElementInstance elementInstance = (ElementInstance)object;

		return Objects.equals(toString(), elementInstance.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ElementInstanceSerDes.toJSON(this);
	}

}