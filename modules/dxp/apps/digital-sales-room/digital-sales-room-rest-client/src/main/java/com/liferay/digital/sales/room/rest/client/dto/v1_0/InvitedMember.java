/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.rest.client.dto.v1_0;

import com.liferay.digital.sales.room.rest.client.function.UnsafeSupplier;
import com.liferay.digital.sales.room.rest.client.serdes.v1_0.InvitedMemberSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Stefano Motta
 * @generated
 */
@Generated("")
public class InvitedMember implements Cloneable, Serializable {

	public static InvitedMember toDTO(String json) {
		return InvitedMemberSerDes.toDTO(json);
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public void setEmailAddress(
		UnsafeSupplier<String, Exception> emailAddressUnsafeSupplier) {

		try {
			emailAddress = emailAddressUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String emailAddress;

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

	public Date getMembershipExpirationDate() {
		return membershipExpirationDate;
	}

	public void setMembershipExpirationDate(Date membershipExpirationDate) {
		this.membershipExpirationDate = membershipExpirationDate;
	}

	public void setMembershipExpirationDate(
		UnsafeSupplier<Date, Exception>
			membershipExpirationDateUnsafeSupplier) {

		try {
			membershipExpirationDate =
				membershipExpirationDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date membershipExpirationDate;

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public void setOwnerId(
		UnsafeSupplier<Long, Exception> ownerIdUnsafeSupplier) {

		try {
			ownerId = ownerIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long ownerId;

	public String getRoleKey() {
		return roleKey;
	}

	public void setRoleKey(String roleKey) {
		this.roleKey = roleKey;
	}

	public void setRoleKey(
		UnsafeSupplier<String, Exception> roleKeyUnsafeSupplier) {

		try {
			roleKey = roleKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String roleKey;

	@Override
	public InvitedMember clone() throws CloneNotSupportedException {
		return (InvitedMember)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof InvitedMember)) {
			return false;
		}

		InvitedMember invitedMember = (InvitedMember)object;

		return Objects.equals(toString(), invitedMember.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return InvitedMemberSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-700883315