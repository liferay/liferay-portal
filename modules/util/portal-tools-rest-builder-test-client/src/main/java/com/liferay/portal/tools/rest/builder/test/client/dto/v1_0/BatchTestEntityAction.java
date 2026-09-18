/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.dto.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.function.UnsafeSupplier;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.BatchTestEntityActionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class BatchTestEntityAction implements Cloneable, Serializable {

	public static BatchTestEntityAction toDTO(String json) {
		return BatchTestEntityActionSerDes.toDTO(json);
	}

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

	public Long getSourceBatchTestEntityId() {
		return sourceBatchTestEntityId;
	}

	public void setSourceBatchTestEntityId(Long sourceBatchTestEntityId) {
		this.sourceBatchTestEntityId = sourceBatchTestEntityId;
	}

	public void setSourceBatchTestEntityId(
		UnsafeSupplier<Long, Exception> sourceBatchTestEntityIdUnsafeSupplier) {

		try {
			sourceBatchTestEntityId =
				sourceBatchTestEntityIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long sourceBatchTestEntityId;

	@Override
	public BatchTestEntityAction clone() throws CloneNotSupportedException {
		return (BatchTestEntityAction)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BatchTestEntityAction)) {
			return false;
		}

		BatchTestEntityAction batchTestEntityAction =
			(BatchTestEntityAction)object;

		return Objects.equals(toString(), batchTestEntityAction.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return BatchTestEntityActionSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:34803470