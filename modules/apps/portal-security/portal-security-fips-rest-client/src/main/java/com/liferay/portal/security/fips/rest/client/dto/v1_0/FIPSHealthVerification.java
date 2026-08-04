/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.client.dto.v1_0;

import com.liferay.portal.security.fips.rest.client.function.UnsafeSupplier;
import com.liferay.portal.security.fips.rest.client.serdes.v1_0.FIPSHealthVerificationSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Lucas Miranda
 * @generated
 */
@Generated("")
public class FIPSHealthVerification implements Cloneable, Serializable {

	public static FIPSHealthVerification toDTO(String json) {
		return FIPSHealthVerificationSerDes.toDTO(json);
	}

	public Date getDateVerified() {
		return dateVerified;
	}

	public void setDateVerified(Date dateVerified) {
		this.dateVerified = dateVerified;
	}

	public void setDateVerified(
		UnsafeSupplier<Date, Exception> dateVerifiedUnsafeSupplier) {

		try {
			dateVerified = dateVerifiedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateVerified;

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setErrorMessage(
		UnsafeSupplier<String, Exception> errorMessageUnsafeSupplier) {

		try {
			errorMessage = errorMessageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String errorMessage;

	public Status getStatus() {
		return status;
	}

	public String getStatusAsString() {
		if (status == null) {
			return null;
		}

		return status.toString();
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setStatus(
		UnsafeSupplier<Status, Exception> statusUnsafeSupplier) {

		try {
			status = statusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Status status;

	@Override
	public FIPSHealthVerification clone() throws CloneNotSupportedException {
		return (FIPSHealthVerification)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FIPSHealthVerification)) {
			return false;
		}

		FIPSHealthVerification fipsHealthVerification =
			(FIPSHealthVerification)object;

		return Objects.equals(toString(), fipsHealthVerification.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FIPSHealthVerificationSerDes.toJSON(this);
	}

	public static enum Status {

		ERROR("ERROR"), INITIALIZING("INITIALIZING"),
		OPERATIONAL("OPERATIONAL"), POWER_OFF("POWER_OFF"),
		SELF_TEST("SELF_TEST");

		public static Status create(String value) {
			for (Status status : values()) {
				if (Objects.equals(status.getValue(), value) ||
					Objects.equals(status.name(), value)) {

					return status;
				}
			}

			return null;
		}

		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private Status(String value) {
			_value = value;
		}

		private final String _value;

	}

}
// LIFERAY-REST-BUILDER-HASH:627698597