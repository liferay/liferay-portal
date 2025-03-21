/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v1_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v1_0.AdvancedConfigurationSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class AdvancedConfiguration implements Cloneable, Serializable {

	public static AdvancedConfiguration toDTO(String json) {
		return AdvancedConfigurationSerDes.toDTO(json);
	}

	public Collapse getCollapse() {
		return collapse;
	}

	public void setCollapse(Collapse collapse) {
		this.collapse = collapse;
	}

	public void setCollapse(
		UnsafeSupplier<Collapse, Exception> collapseUnsafeSupplier) {

		try {
			collapse = collapseUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Collapse collapse;

	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public void setFields(
		UnsafeSupplier<String[], Exception> fieldsUnsafeSupplier) {

		try {
			fields = fieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] fields;

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public void setSource(
		UnsafeSupplier<Source, Exception> sourceUnsafeSupplier) {

		try {
			source = sourceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Source source;

	public String[] getStored_fields() {
		return stored_fields;
	}

	public void setStored_fields(String[] stored_fields) {
		this.stored_fields = stored_fields;
	}

	public void setStored_fields(
		UnsafeSupplier<String[], Exception> stored_fieldsUnsafeSupplier) {

		try {
			stored_fields = stored_fieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] stored_fields;

	@Override
	public AdvancedConfiguration clone() throws CloneNotSupportedException {
		return (AdvancedConfiguration)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AdvancedConfiguration)) {
			return false;
		}

		AdvancedConfiguration advancedConfiguration =
			(AdvancedConfiguration)object;

		return Objects.equals(toString(), advancedConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AdvancedConfigurationSerDes.toJSON(this);
	}

}