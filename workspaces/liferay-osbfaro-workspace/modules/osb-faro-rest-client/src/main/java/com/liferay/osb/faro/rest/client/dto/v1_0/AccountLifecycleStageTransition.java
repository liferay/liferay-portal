/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.dto.v1_0;

import com.liferay.osb.faro.rest.client.function.UnsafeSupplier;
import com.liferay.osb.faro.rest.client.serdes.v1_0.AccountLifecycleStageTransitionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class AccountLifecycleStageTransition
	implements Cloneable, Serializable {

	public static AccountLifecycleStageTransition toDTO(String json) {
		return AccountLifecycleStageTransitionSerDes.toDTO(json);
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public void setAccountId(
		UnsafeSupplier<String, Exception> accountIdUnsafeSupplier) {

		try {
			accountId = accountIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String accountId;

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public void setAccountName(
		UnsafeSupplier<String, Exception> accountNameUnsafeSupplier) {

		try {
			accountName = accountNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String accountName;

	public AccountLifecycleStage getFromAccountLifecycleStage() {
		return fromAccountLifecycleStage;
	}

	public void setFromAccountLifecycleStage(
		AccountLifecycleStage fromAccountLifecycleStage) {

		this.fromAccountLifecycleStage = fromAccountLifecycleStage;
	}

	public void setFromAccountLifecycleStage(
		UnsafeSupplier<AccountLifecycleStage, Exception>
			fromAccountLifecycleStageUnsafeSupplier) {

		try {
			fromAccountLifecycleStage =
				fromAccountLifecycleStageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected AccountLifecycleStage fromAccountLifecycleStage;

	public AccountLifecycleStage getToAccountLifecycleStage() {
		return toAccountLifecycleStage;
	}

	public void setToAccountLifecycleStage(
		AccountLifecycleStage toAccountLifecycleStage) {

		this.toAccountLifecycleStage = toAccountLifecycleStage;
	}

	public void setToAccountLifecycleStage(
		UnsafeSupplier<AccountLifecycleStage, Exception>
			toAccountLifecycleStageUnsafeSupplier) {

		try {
			toAccountLifecycleStage =
				toAccountLifecycleStageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected AccountLifecycleStage toAccountLifecycleStage;

	public Date getTransitionDate() {
		return transitionDate;
	}

	public void setTransitionDate(Date transitionDate) {
		this.transitionDate = transitionDate;
	}

	public void setTransitionDate(
		UnsafeSupplier<Date, Exception> transitionDateUnsafeSupplier) {

		try {
			transitionDate = transitionDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date transitionDate;

	@Override
	public AccountLifecycleStageTransition clone()
		throws CloneNotSupportedException {

		return (AccountLifecycleStageTransition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AccountLifecycleStageTransition)) {
			return false;
		}

		AccountLifecycleStageTransition accountLifecycleStageTransition =
			(AccountLifecycleStageTransition)object;

		return Objects.equals(
			toString(), accountLifecycleStageTransition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AccountLifecycleStageTransitionSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:974601048