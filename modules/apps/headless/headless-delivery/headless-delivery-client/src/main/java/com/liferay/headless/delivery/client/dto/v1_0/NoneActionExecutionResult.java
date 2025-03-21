/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.NoneActionExecutionResultSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class NoneActionExecutionResult implements Cloneable, Serializable {

	public static NoneActionExecutionResult toDTO(String json) {
		return NoneActionExecutionResultSerDes.toDTO(json);
	}

	public Boolean getReload() {
		return reload;
	}

	public void setReload(Boolean reload) {
		this.reload = reload;
	}

	public void setReload(
		UnsafeSupplier<Boolean, Exception> reloadUnsafeSupplier) {

		try {
			reload = reloadUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean reload;

	@Override
	public NoneActionExecutionResult clone() throws CloneNotSupportedException {
		return (NoneActionExecutionResult)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof NoneActionExecutionResult)) {
			return false;
		}

		NoneActionExecutionResult noneActionExecutionResult =
			(NoneActionExecutionResult)object;

		return Objects.equals(toString(), noneActionExecutionResult.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return NoneActionExecutionResultSerDes.toJSON(this);
	}

}