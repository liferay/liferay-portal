/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.client.dto.v2_0;

import com.liferay.data.engine.rest.client.function.UnsafeSupplier;
import com.liferay.data.engine.rest.client.serdes.v2_0.DataRuleSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Jeyvison Nascimento
 * @generated
 */
@Generated("")
public class DataRule implements Cloneable, Serializable {

	public static DataRule toDTO(String json) {
		return DataRuleSerDes.toDTO(json);
	}

	public Map[] getActions() {
		return actions;
	}

	public void setActions(Map[] actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Map[], Exception> actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map[] actions;

	public Map[] getConditions() {
		return conditions;
	}

	public void setConditions(Map[] conditions) {
		this.conditions = conditions;
	}

	public void setConditions(
		UnsafeSupplier<Map[], Exception> conditionsUnsafeSupplier) {

		try {
			conditions = conditionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map[] conditions;

	public String getLogicalOperator() {
		return logicalOperator;
	}

	public void setLogicalOperator(String logicalOperator) {
		this.logicalOperator = logicalOperator;
	}

	public void setLogicalOperator(
		UnsafeSupplier<String, Exception> logicalOperatorUnsafeSupplier) {

		try {
			logicalOperator = logicalOperatorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String logicalOperator;

	public Map<String, Object> getName() {
		return name;
	}

	public void setName(Map<String, Object> name) {
		this.name = name;
	}

	public void setName(
		UnsafeSupplier<Map<String, Object>, Exception> nameUnsafeSupplier) {

		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Object> name;

	@Override
	public DataRule clone() throws CloneNotSupportedException {
		return (DataRule)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DataRule)) {
			return false;
		}

		DataRule dataRule = (DataRule)object;

		return Objects.equals(toString(), dataRule.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DataRuleSerDes.toJSON(this);
	}

}