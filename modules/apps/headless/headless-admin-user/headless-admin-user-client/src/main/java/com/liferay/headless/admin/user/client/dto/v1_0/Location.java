/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.dto.v1_0;

import com.liferay.headless.admin.user.client.function.UnsafeSupplier;
import com.liferay.headless.admin.user.client.serdes.v1_0.LocationSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class Location implements Cloneable, Serializable {

	public static Location toDTO(String json) {
		return LocationSerDes.toDTO(json);
	}

	public String getAddressCountry() {
		return addressCountry;
	}

	public void setAddressCountry(String addressCountry) {
		this.addressCountry = addressCountry;
	}

	public void setAddressCountry(
		UnsafeSupplier<String, Exception> addressCountryUnsafeSupplier) {

		try {
			addressCountry = addressCountryUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String addressCountry;

	public String getAddressCountryCode() {
		return addressCountryCode;
	}

	public void setAddressCountryCode(String addressCountryCode) {
		this.addressCountryCode = addressCountryCode;
	}

	public void setAddressCountryCode(
		UnsafeSupplier<String, Exception> addressCountryCodeUnsafeSupplier) {

		try {
			addressCountryCode = addressCountryCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String addressCountryCode;

	public Map<String, String> getAddressCountry_i18n() {
		return addressCountry_i18n;
	}

	public void setAddressCountry_i18n(
		Map<String, String> addressCountry_i18n) {

		this.addressCountry_i18n = addressCountry_i18n;
	}

	public void setAddressCountry_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			addressCountry_i18nUnsafeSupplier) {

		try {
			addressCountry_i18n = addressCountry_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> addressCountry_i18n;

	public String getAddressRegion() {
		return addressRegion;
	}

	public void setAddressRegion(String addressRegion) {
		this.addressRegion = addressRegion;
	}

	public void setAddressRegion(
		UnsafeSupplier<String, Exception> addressRegionUnsafeSupplier) {

		try {
			addressRegion = addressRegionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String addressRegion;

	public String getAddressRegionCode() {
		return addressRegionCode;
	}

	public void setAddressRegionCode(String addressRegionCode) {
		this.addressRegionCode = addressRegionCode;
	}

	public void setAddressRegionCode(
		UnsafeSupplier<String, Exception> addressRegionCodeUnsafeSupplier) {

		try {
			addressRegionCode = addressRegionCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String addressRegionCode;

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

	@Override
	public Location clone() throws CloneNotSupportedException {
		return (Location)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Location)) {
			return false;
		}

		Location location = (Location)object;

		return Objects.equals(toString(), location.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return LocationSerDes.toJSON(this);
	}

}