/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v1_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v1_0.SourceSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class Source implements Cloneable, Serializable {

	public static Source toDTO(String json) {
		return SourceSerDes.toDTO(json);
	}

	public String[] getExcludes() {
		return excludes;
	}

	public void setExcludes(String[] excludes) {
		this.excludes = excludes;
	}

	public void setExcludes(
		UnsafeSupplier<String[], Exception> excludesUnsafeSupplier) {

		try {
			excludes = excludesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] excludes;

	public Boolean getFetchSource() {
		return fetchSource;
	}

	public void setFetchSource(Boolean fetchSource) {
		this.fetchSource = fetchSource;
	}

	public void setFetchSource(
		UnsafeSupplier<Boolean, Exception> fetchSourceUnsafeSupplier) {

		try {
			fetchSource = fetchSourceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean fetchSource;

	public String[] getIncludes() {
		return includes;
	}

	public void setIncludes(String[] includes) {
		this.includes = includes;
	}

	public void setIncludes(
		UnsafeSupplier<String[], Exception> includesUnsafeSupplier) {

		try {
			includes = includesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] includes;

	@Override
	public Source clone() throws CloneNotSupportedException {
		return (Source)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Source)) {
			return false;
		}

		Source source = (Source)object;

		return Objects.equals(toString(), source.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SourceSerDes.toJSON(this);
	}

}