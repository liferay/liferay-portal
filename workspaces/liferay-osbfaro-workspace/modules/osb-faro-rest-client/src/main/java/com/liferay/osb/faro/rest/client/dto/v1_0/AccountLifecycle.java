/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.dto.v1_0;

import com.liferay.osb.faro.rest.client.function.UnsafeSupplier;
import com.liferay.osb.faro.rest.client.serdes.v1_0.AccountLifecycleSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class AccountLifecycle implements Cloneable, Serializable {

	public static AccountLifecycle toDTO(String json) {
		return AccountLifecycleSerDes.toDTO(json);
	}

	public AccountLifecycleStage[] getAccountLifecycleStages() {
		return accountLifecycleStages;
	}

	public void setAccountLifecycleStages(
		AccountLifecycleStage[] accountLifecycleStages) {

		this.accountLifecycleStages = accountLifecycleStages;
	}

	public void setAccountLifecycleStages(
		UnsafeSupplier<AccountLifecycleStage[], Exception>
			accountLifecycleStagesUnsafeSupplier) {

		try {
			accountLifecycleStages = accountLifecycleStagesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected AccountLifecycleStage[] accountLifecycleStages;

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		try {
			dateModified = dateModifiedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateModified;

	public Boolean getDefaultAccountLifecycle() {
		return defaultAccountLifecycle;
	}

	public void setDefaultAccountLifecycle(Boolean defaultAccountLifecycle) {
		this.defaultAccountLifecycle = defaultAccountLifecycle;
	}

	public void setDefaultAccountLifecycle(
		UnsafeSupplier<Boolean, Exception>
			defaultAccountLifecycleUnsafeSupplier) {

		try {
			defaultAccountLifecycle =
				defaultAccountLifecycleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean defaultAccountLifecycle;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String description;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String id;

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

	@Override
	public AccountLifecycle clone() throws CloneNotSupportedException {
		return (AccountLifecycle)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AccountLifecycle)) {
			return false;
		}

		AccountLifecycle accountLifecycle = (AccountLifecycle)object;

		return Objects.equals(toString(), accountLifecycle.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AccountLifecycleSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1781668517