/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.client.dto.v1_0;

import com.liferay.digital.signature.rest.client.function.UnsafeSupplier;
import com.liferay.digital.signature.rest.client.serdes.v1_0.SignatureRequestRecipientSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author José Abelenda
 * @generated
 */
@Generated("")
public class SignatureRequestRecipient implements Cloneable, Serializable {

	public static SignatureRequestRecipient toDTO(String json) {
		return SignatureRequestRecipientSerDes.toDTO(json);
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

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	public void setSentDate(
		UnsafeSupplier<Date, Exception> sentDateUnsafeSupplier) {

		try {
			sentDate = sentDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date sentDate;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStatus(
		UnsafeSupplier<String, Exception> statusUnsafeSupplier) {

		try {
			status = statusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String status;

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public void setStatusDate(
		UnsafeSupplier<Date, Exception> statusDateUnsafeSupplier) {

		try {
			statusDate = statusDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date statusDate;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setUserId(
		UnsafeSupplier<Long, Exception> userIdUnsafeSupplier) {

		try {
			userId = userIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long userId;

	@Override
	public SignatureRequestRecipient clone() throws CloneNotSupportedException {
		return (SignatureRequestRecipient)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SignatureRequestRecipient)) {
			return false;
		}

		SignatureRequestRecipient signatureRequestRecipient =
			(SignatureRequestRecipient)object;

		return Objects.equals(toString(), signatureRequestRecipient.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SignatureRequestRecipientSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-446863155