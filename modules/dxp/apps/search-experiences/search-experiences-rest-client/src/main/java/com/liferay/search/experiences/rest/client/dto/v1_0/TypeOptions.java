/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v1_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v1_0.TypeOptionsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class TypeOptions implements Cloneable, Serializable {

	public static TypeOptions toDTO(String json) {
		return TypeOptionsSerDes.toDTO(json);
	}

	public Boolean getBoost() {
		return boost;
	}

	public void setBoost(Boolean boost) {
		this.boost = boost;
	}

	public void setBoost(
		UnsafeSupplier<Boolean, Exception> boostUnsafeSupplier) {

		try {
			boost = boostUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean boost;

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setFormat(
		UnsafeSupplier<String, Exception> formatUnsafeSupplier) {

		try {
			format = formatUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String format;

	public Object getMax() {
		return max;
	}

	public void setMax(Object max) {
		this.max = max;
	}

	public void setMax(UnsafeSupplier<Object, Exception> maxUnsafeSupplier) {
		try {
			max = maxUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object max;

	public Object getMin() {
		return min;
	}

	public void setMin(Object min) {
		this.min = min;
	}

	public void setMin(UnsafeSupplier<Object, Exception> minUnsafeSupplier) {
		try {
			min = minUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object min;

	public Boolean getNullable() {
		return nullable;
	}

	public void setNullable(Boolean nullable) {
		this.nullable = nullable;
	}

	public void setNullable(
		UnsafeSupplier<Boolean, Exception> nullableUnsafeSupplier) {

		try {
			nullable = nullableUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean nullable;

	public Option[] getOptions() {
		return options;
	}

	public void setOptions(Option[] options) {
		this.options = options;
	}

	public void setOptions(
		UnsafeSupplier<Option[], Exception> optionsUnsafeSupplier) {

		try {
			options = optionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Option[] options;

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public void setRequired(
		UnsafeSupplier<Boolean, Exception> requiredUnsafeSupplier) {

		try {
			required = requiredUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean required;

	public Object getStep() {
		return step;
	}

	public void setStep(Object step) {
		this.step = step;
	}

	public void setStep(UnsafeSupplier<Object, Exception> stepUnsafeSupplier) {
		try {
			step = stepUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object step;

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setUnit(UnsafeSupplier<String, Exception> unitUnsafeSupplier) {
		try {
			unit = unitUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String unit;

	public String getUnitSuffix() {
		return unitSuffix;
	}

	public void setUnitSuffix(String unitSuffix) {
		this.unitSuffix = unitSuffix;
	}

	public void setUnitSuffix(
		UnsafeSupplier<String, Exception> unitSuffixUnsafeSupplier) {

		try {
			unitSuffix = unitSuffixUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String unitSuffix;

	@Override
	public TypeOptions clone() throws CloneNotSupportedException {
		return (TypeOptions)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TypeOptions)) {
			return false;
		}

		TypeOptions typeOptions = (TypeOptions)object;

		return Objects.equals(toString(), typeOptions.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return TypeOptionsSerDes.toJSON(this);
	}

}