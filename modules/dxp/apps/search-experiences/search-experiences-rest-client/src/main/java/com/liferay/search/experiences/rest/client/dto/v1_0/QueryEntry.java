/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v1_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v1_0.QueryEntrySerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class QueryEntry implements Cloneable, Serializable {

	public static QueryEntry toDTO(String json) {
		return QueryEntrySerDes.toDTO(json);
	}

	public Clause[] getClauses() {
		return clauses;
	}

	public void setClauses(Clause[] clauses) {
		this.clauses = clauses;
	}

	public void setClauses(
		UnsafeSupplier<Clause[], Exception> clausesUnsafeSupplier) {

		try {
			clauses = clausesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Clause[] clauses;

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public void setCondition(
		UnsafeSupplier<Condition, Exception> conditionUnsafeSupplier) {

		try {
			condition = conditionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Condition condition;

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public void setEnabled(
		UnsafeSupplier<Boolean, Exception> enabledUnsafeSupplier) {

		try {
			enabled = enabledUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean enabled;

	public Clause[] getPostFilterClauses() {
		return postFilterClauses;
	}

	public void setPostFilterClauses(Clause[] postFilterClauses) {
		this.postFilterClauses = postFilterClauses;
	}

	public void setPostFilterClauses(
		UnsafeSupplier<Clause[], Exception> postFilterClausesUnsafeSupplier) {

		try {
			postFilterClauses = postFilterClausesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Clause[] postFilterClauses;

	public Rescore[] getRescores() {
		return rescores;
	}

	public void setRescores(Rescore[] rescores) {
		this.rescores = rescores;
	}

	public void setRescores(
		UnsafeSupplier<Rescore[], Exception> rescoresUnsafeSupplier) {

		try {
			rescores = rescoresUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Rescore[] rescores;

	@Override
	public QueryEntry clone() throws CloneNotSupportedException {
		return (QueryEntry)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof QueryEntry)) {
			return false;
		}

		QueryEntry queryEntry = (QueryEntry)object;

		return Objects.equals(toString(), queryEntry.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return QueryEntrySerDes.toJSON(this);
	}

}