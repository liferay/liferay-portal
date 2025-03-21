/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v1_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v1_0.ConditionSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class Condition implements Cloneable, Serializable {

	public static Condition toDTO(String json) {
		return ConditionSerDes.toDTO(json);
	}

	public Condition[] getAllConditions() {
		return allConditions;
	}

	public void setAllConditions(Condition[] allConditions) {
		this.allConditions = allConditions;
	}

	public void setAllConditions(
		UnsafeSupplier<Condition[], Exception> allConditionsUnsafeSupplier) {

		try {
			allConditions = allConditionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Condition[] allConditions;

	public Condition[] getAnyConditions() {
		return anyConditions;
	}

	public void setAnyConditions(Condition[] anyConditions) {
		this.anyConditions = anyConditions;
	}

	public void setAnyConditions(
		UnsafeSupplier<Condition[], Exception> anyConditionsUnsafeSupplier) {

		try {
			anyConditions = anyConditionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Condition[] anyConditions;

	public Contains getContains() {
		return contains;
	}

	public void setContains(Contains contains) {
		this.contains = contains;
	}

	public void setContains(
		UnsafeSupplier<Contains, Exception> containsUnsafeSupplier) {

		try {
			contains = containsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Contains contains;

	public Equals getEquals() {
		return equals;
	}

	public void setEquals(Equals equals) {
		this.equals = equals;
	}

	public void setEquals(
		UnsafeSupplier<Equals, Exception> equalsUnsafeSupplier) {

		try {
			equals = equalsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Equals equals;

	public Exists getExists() {
		return exists;
	}

	public void setExists(Exists exists) {
		this.exists = exists;
	}

	public void setExists(
		UnsafeSupplier<Exists, Exception> existsUnsafeSupplier) {

		try {
			exists = existsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Exists exists;

	public In getIn() {
		return in;
	}

	public void setIn(In in) {
		this.in = in;
	}

	public void setIn(UnsafeSupplier<In, Exception> inUnsafeSupplier) {
		try {
			in = inUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected In in;

	public Condition getNot() {
		return not;
	}

	public void setNot(Condition not) {
		this.not = not;
	}

	public void setNot(UnsafeSupplier<Condition, Exception> notUnsafeSupplier) {
		try {
			not = notUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Condition not;

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public void setRange(UnsafeSupplier<Range, Exception> rangeUnsafeSupplier) {
		try {
			range = rangeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Range range;

	@Override
	public Condition clone() throws CloneNotSupportedException {
		return (Condition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Condition)) {
			return false;
		}

		Condition condition = (Condition)object;

		return Objects.equals(toString(), condition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ConditionSerDes.toJSON(this);
	}

}