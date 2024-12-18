/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v2_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v2_0.ConditionConfigurationSerDes;

import java.io.Serializable;

import java.util.Objects;

import javax.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class ConditionConfiguration implements Cloneable, Serializable {

	public static ConditionConfiguration toDTO(String json) {
		return ConditionConfigurationSerDes.toDTO(json);
	}

	public Condition[] getConditions() {
		return conditions;
	}

	public void setConditions(Condition[] conditions) {
		this.conditions = conditions;
	}

	public void setConditions(
		UnsafeSupplier<Condition[], Exception> conditionsUnsafeSupplier) {

		try {
			conditions = conditionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Condition[] conditions;

	@Override
	public ConditionConfiguration clone() throws CloneNotSupportedException {
		return (ConditionConfiguration)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ConditionConfiguration)) {
			return false;
		}

		ConditionConfiguration conditionConfiguration =
			(ConditionConfiguration)object;

		return Objects.equals(toString(), conditionConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ConditionConfigurationSerDes.toJSON(this);
	}

}