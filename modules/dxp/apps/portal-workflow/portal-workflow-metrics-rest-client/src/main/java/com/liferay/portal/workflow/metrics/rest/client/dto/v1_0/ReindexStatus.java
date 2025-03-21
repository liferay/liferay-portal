/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.dto.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.function.UnsafeSupplier;
import com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0.ReindexStatusSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class ReindexStatus implements Cloneable, Serializable {

	public static ReindexStatus toDTO(String json) {
		return ReindexStatusSerDes.toDTO(json);
	}

	public Long getCompletionPercentage() {
		return completionPercentage;
	}

	public void setCompletionPercentage(Long completionPercentage) {
		this.completionPercentage = completionPercentage;
	}

	public void setCompletionPercentage(
		UnsafeSupplier<Long, Exception> completionPercentageUnsafeSupplier) {

		try {
			completionPercentage = completionPercentageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long completionPercentage;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setKey(UnsafeSupplier<String, Exception> keyUnsafeSupplier) {
		try {
			key = keyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String key;

	@Override
	public ReindexStatus clone() throws CloneNotSupportedException {
		return (ReindexStatus)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ReindexStatus)) {
			return false;
		}

		ReindexStatus reindexStatus = (ReindexStatus)object;

		return Objects.equals(toString(), reindexStatus.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ReindexStatusSerDes.toJSON(this);
	}

}