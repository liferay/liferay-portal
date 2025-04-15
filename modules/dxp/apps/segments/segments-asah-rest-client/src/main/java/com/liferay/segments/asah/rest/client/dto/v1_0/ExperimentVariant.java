/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.rest.client.dto.v1_0;

import com.liferay.segments.asah.rest.client.function.UnsafeSupplier;
import com.liferay.segments.asah.rest.client.serdes.v1_0.ExperimentVariantSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ExperimentVariant implements Cloneable, Serializable {

	public static ExperimentVariant toDTO(String json) {
		return ExperimentVariantSerDes.toDTO(json);
	}

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

	public Double getTrafficSplit() {
		return trafficSplit;
	}

	public void setTrafficSplit(Double trafficSplit) {
		this.trafficSplit = trafficSplit;
	}

	public void setTrafficSplit(
		UnsafeSupplier<Double, Exception> trafficSplitUnsafeSupplier) {

		try {
			trafficSplit = trafficSplitUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double trafficSplit;

	@Override
	public ExperimentVariant clone() throws CloneNotSupportedException {
		return (ExperimentVariant)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ExperimentVariant)) {
			return false;
		}

		ExperimentVariant experimentVariant = (ExperimentVariant)object;

		return Objects.equals(toString(), experimentVariant.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ExperimentVariantSerDes.toJSON(this);
	}

}