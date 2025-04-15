/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v1_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v1_0.ClauseSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class Clause implements Cloneable, Serializable {

	public static Clause toDTO(String json) {
		return ClauseSerDes.toDTO(json);
	}

	public Boolean getAdditive() {
		return additive;
	}

	public void setAdditive(Boolean additive) {
		this.additive = additive;
	}

	public void setAdditive(
		UnsafeSupplier<Boolean, Exception> additiveUnsafeSupplier) {

		try {
			additive = additiveUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean additive;

	public Float getBoost() {
		return boost;
	}

	public void setBoost(Float boost) {
		this.boost = boost;
	}

	public void setBoost(UnsafeSupplier<Float, Exception> boostUnsafeSupplier) {
		try {
			boost = boostUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Float boost;

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public void setContext(
		UnsafeSupplier<String, Exception> contextUnsafeSupplier) {

		try {
			context = contextUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String context;

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public void setDisabled(
		UnsafeSupplier<Boolean, Exception> disabledUnsafeSupplier) {

		try {
			disabled = disabledUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean disabled;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setField(
		UnsafeSupplier<String, Exception> fieldUnsafeSupplier) {

		try {
			field = fieldUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String field;

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

	public String getOccur() {
		return occur;
	}

	public void setOccur(String occur) {
		this.occur = occur;
	}

	public void setOccur(
		UnsafeSupplier<String, Exception> occurUnsafeSupplier) {

		try {
			occur = occurUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String occur;

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public void setParent(
		UnsafeSupplier<String, Exception> parentUnsafeSupplier) {

		try {
			parent = parentUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String parent;

	public Object getQuery() {
		return query;
	}

	public void setQuery(Object query) {
		this.query = query;
	}

	public void setQuery(
		UnsafeSupplier<Object, Exception> queryUnsafeSupplier) {

		try {
			query = queryUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object query;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<String, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String type;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setValue(
		UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

		try {
			value = valueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String value;

	@Override
	public Clause clone() throws CloneNotSupportedException {
		return (Clause)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Clause)) {
			return false;
		}

		Clause clause = (Clause)object;

		return Objects.equals(toString(), clause.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ClauseSerDes.toJSON(this);
	}

}