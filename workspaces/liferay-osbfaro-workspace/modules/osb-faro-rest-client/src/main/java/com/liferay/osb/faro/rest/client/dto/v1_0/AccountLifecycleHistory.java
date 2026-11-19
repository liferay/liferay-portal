/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.dto.v1_0;

import com.liferay.osb.faro.rest.client.function.UnsafeSupplier;
import com.liferay.osb.faro.rest.client.serdes.v1_0.AccountLifecycleHistorySerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class AccountLifecycleHistory implements Cloneable, Serializable {

	public static AccountLifecycleHistory toDTO(String json) {
		return AccountLifecycleHistorySerDes.toDTO(json);
	}

	public AccountLifecycleStageHistory[] getAccountLifecycleStageHistories() {
		return accountLifecycleStageHistories;
	}

	public void setAccountLifecycleStageHistories(
		AccountLifecycleStageHistory[] accountLifecycleStageHistories) {

		this.accountLifecycleStageHistories = accountLifecycleStageHistories;
	}

	public void setAccountLifecycleStageHistories(
		UnsafeSupplier<AccountLifecycleStageHistory[], Exception>
			accountLifecycleStageHistoriesUnsafeSupplier) {

		try {
			accountLifecycleStageHistories =
				accountLifecycleStageHistoriesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected AccountLifecycleStageHistory[] accountLifecycleStageHistories;

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
	public AccountLifecycleHistory clone() throws CloneNotSupportedException {
		return (AccountLifecycleHistory)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AccountLifecycleHistory)) {
			return false;
		}

		AccountLifecycleHistory accountLifecycleHistory =
			(AccountLifecycleHistory)object;

		return Objects.equals(toString(), accountLifecycleHistory.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AccountLifecycleHistorySerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:943443408