/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v1_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v1_0.InnerHitSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class InnerHit implements Cloneable, Serializable {

	public static InnerHit toDTO(String json) {
		return InnerHitSerDes.toDTO(json);
	}

	public InnerCollapse getInnerCollapse() {
		return innerCollapse;
	}

	public void setInnerCollapse(InnerCollapse innerCollapse) {
		this.innerCollapse = innerCollapse;
	}

	public void setInnerCollapse(
		UnsafeSupplier<InnerCollapse, Exception> innerCollapseUnsafeSupplier) {

		try {
			innerCollapse = innerCollapseUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected InnerCollapse innerCollapse;

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

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public void setSize(UnsafeSupplier<Integer, Exception> sizeUnsafeSupplier) {
		try {
			size = sizeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer size;

	public Object[] getSorts() {
		return sorts;
	}

	public void setSorts(Object[] sorts) {
		this.sorts = sorts;
	}

	public void setSorts(
		UnsafeSupplier<Object[], Exception> sortsUnsafeSupplier) {

		try {
			sorts = sortsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object[] sorts;

	@Override
	public InnerHit clone() throws CloneNotSupportedException {
		return (InnerHit)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof InnerHit)) {
			return false;
		}

		InnerHit innerHit = (InnerHit)object;

		return Objects.equals(toString(), innerHit.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return InnerHitSerDes.toJSON(this);
	}

}