/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.client.dto.v1_0;

import com.liferay.analytics.settings.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.settings.rest.client.serdes.v1_0.ContactConfigurationSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class ContactConfiguration implements Cloneable, Serializable {

	public static ContactConfiguration toDTO(String json) {
		return ContactConfigurationSerDes.toDTO(json);
	}

	public Boolean getSyncAllAccounts() {
		return syncAllAccounts;
	}

	public void setSyncAllAccounts(Boolean syncAllAccounts) {
		this.syncAllAccounts = syncAllAccounts;
	}

	public void setSyncAllAccounts(
		UnsafeSupplier<Boolean, Exception> syncAllAccountsUnsafeSupplier) {

		try {
			syncAllAccounts = syncAllAccountsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean syncAllAccounts;

	public Boolean getSyncAllContacts() {
		return syncAllContacts;
	}

	public void setSyncAllContacts(Boolean syncAllContacts) {
		this.syncAllContacts = syncAllContacts;
	}

	public void setSyncAllContacts(
		UnsafeSupplier<Boolean, Exception> syncAllContactsUnsafeSupplier) {

		try {
			syncAllContacts = syncAllContactsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean syncAllContacts;

	public String[] getSyncedAccountGroupIds() {
		return syncedAccountGroupIds;
	}

	public void setSyncedAccountGroupIds(String[] syncedAccountGroupIds) {
		this.syncedAccountGroupIds = syncedAccountGroupIds;
	}

	public void setSyncedAccountGroupIds(
		UnsafeSupplier<String[], Exception>
			syncedAccountGroupIdsUnsafeSupplier) {

		try {
			syncedAccountGroupIds = syncedAccountGroupIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] syncedAccountGroupIds;

	public String[] getSyncedOrganizationIds() {
		return syncedOrganizationIds;
	}

	public void setSyncedOrganizationIds(String[] syncedOrganizationIds) {
		this.syncedOrganizationIds = syncedOrganizationIds;
	}

	public void setSyncedOrganizationIds(
		UnsafeSupplier<String[], Exception>
			syncedOrganizationIdsUnsafeSupplier) {

		try {
			syncedOrganizationIds = syncedOrganizationIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] syncedOrganizationIds;

	public String[] getSyncedUserGroupIds() {
		return syncedUserGroupIds;
	}

	public void setSyncedUserGroupIds(String[] syncedUserGroupIds) {
		this.syncedUserGroupIds = syncedUserGroupIds;
	}

	public void setSyncedUserGroupIds(
		UnsafeSupplier<String[], Exception> syncedUserGroupIdsUnsafeSupplier) {

		try {
			syncedUserGroupIds = syncedUserGroupIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] syncedUserGroupIds;

	@Override
	public ContactConfiguration clone() throws CloneNotSupportedException {
		return (ContactConfiguration)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ContactConfiguration)) {
			return false;
		}

		ContactConfiguration contactConfiguration =
			(ContactConfiguration)object;

		return Objects.equals(toString(), contactConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ContactConfigurationSerDes.toJSON(this);
	}

}