/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.client.dto.v1_0;

import com.liferay.portal.security.fips.rest.client.function.UnsafeSupplier;
import com.liferay.portal.security.fips.rest.client.serdes.v1_0.HealthVerificationSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Lucas Miranda
 * @generated
 */
@Generated("")
public class HealthVerification implements Cloneable, Serializable {

	public static HealthVerification toDTO(String json) {
		return HealthVerificationSerDes.toDTO(json);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setDate(UnsafeSupplier<Date, Exception> dateUnsafeSupplier) {
		try {
			date = dateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date date;

	public String getFailedTest() {
		return failedTest;
	}

	public void setFailedTest(String failedTest) {
		this.failedTest = failedTest;
	}

	public void setFailedTest(
		UnsafeSupplier<String, Exception> failedTestUnsafeSupplier) {

		try {
			failedTest = failedTestUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String failedTest;

	public String getFipsState() {
		return fipsState;
	}

	public void setFipsState(String fipsState) {
		this.fipsState = fipsState;
	}

	public void setFipsState(
		UnsafeSupplier<String, Exception> fipsStateUnsafeSupplier) {

		try {
			fipsState = fipsStateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String fipsState;

	public String getProviderMessage() {
		return providerMessage;
	}

	public void setProviderMessage(String providerMessage) {
		this.providerMessage = providerMessage;
	}

	public void setProviderMessage(
		UnsafeSupplier<String, Exception> providerMessageUnsafeSupplier) {

		try {
			providerMessage = providerMessageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String providerMessage;

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public void setProviderName(
		UnsafeSupplier<String, Exception> providerNameUnsafeSupplier) {

		try {
			providerName = providerNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String providerName;

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
	public HealthVerification clone() throws CloneNotSupportedException {
		return (HealthVerification)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof HealthVerification)) {
			return false;
		}

		HealthVerification healthVerification = (HealthVerification)object;

		return Objects.equals(toString(), healthVerification.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return HealthVerificationSerDes.toJSON(this);
	}

	public static enum Status {

		HEALTHY("HEALTHY"), FAILED("FAILED"), NOT_APPLICABLE("NOT_APPLICABLE");

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
// LIFERAY-REST-BUILDER-HASH:-1628254707