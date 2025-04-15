/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v1_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v1_0.ParameterConfigurationSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class ParameterConfiguration implements Cloneable, Serializable {

	public static ParameterConfiguration toDTO(String json) {
		return ParameterConfigurationSerDes.toDTO(json);
	}

	public Map<String, Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Parameter> parameters) {
		this.parameters = parameters;
	}

	public void setParameters(
		UnsafeSupplier<Map<String, Parameter>, Exception>
			parametersUnsafeSupplier) {

		try {
			parameters = parametersUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Parameter> parameters;

	@Override
	public ParameterConfiguration clone() throws CloneNotSupportedException {
		return (ParameterConfiguration)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ParameterConfiguration)) {
			return false;
		}

		ParameterConfiguration parameterConfiguration =
			(ParameterConfiguration)object;

		return Objects.equals(toString(), parameterConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ParameterConfigurationSerDes.toJSON(this);
	}

}