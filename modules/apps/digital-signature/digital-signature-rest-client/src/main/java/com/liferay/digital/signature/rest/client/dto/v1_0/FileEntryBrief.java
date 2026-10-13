/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.client.dto.v1_0;

import com.liferay.digital.signature.rest.client.function.UnsafeSupplier;
import com.liferay.digital.signature.rest.client.serdes.v1_0.FileEntryBriefSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author José Abelenda
 * @generated
 */
@Generated("")
public class FileEntryBrief implements Cloneable, Serializable {

	public static FileEntryBrief toDTO(String json) {
		return FileEntryBriefSerDes.toDTO(json);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitle(
		UnsafeSupplier<String, Exception> titleUnsafeSupplier) {

		try {
			title = titleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String title;

	@Override
	public FileEntryBrief clone() throws CloneNotSupportedException {
		return (FileEntryBrief)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FileEntryBrief)) {
			return false;
		}

		FileEntryBrief fileEntryBrief = (FileEntryBrief)object;

		return Objects.equals(toString(), fileEntryBrief.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FileEntryBriefSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1861679047