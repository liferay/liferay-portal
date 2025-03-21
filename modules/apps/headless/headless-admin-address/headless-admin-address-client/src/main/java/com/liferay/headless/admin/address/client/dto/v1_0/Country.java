/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.address.client.dto.v1_0;

import com.liferay.headless.admin.address.client.function.UnsafeSupplier;
import com.liferay.headless.admin.address.client.serdes.v1_0.CountrySerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Drew Brokke
 * @generated
 */
@Generated("")
public class Country implements Cloneable, Serializable {

	public static Country toDTO(String json) {
		return CountrySerDes.toDTO(json);
	}

	public String getA2() {
		return a2;
	}

	public void setA2(String a2) {
		this.a2 = a2;
	}

	public void setA2(UnsafeSupplier<String, Exception> a2UnsafeSupplier) {
		try {
			a2 = a2UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String a2;

	public String getA3() {
		return a3;
	}

	public void setA3(String a3) {
		this.a3 = a3;
	}

	public void setA3(UnsafeSupplier<String, Exception> a3UnsafeSupplier) {
		try {
			a3 = a3UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String a3;

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		try {
			active = activeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean active;

	public Boolean getBillingAllowed() {
		return billingAllowed;
	}

	public void setBillingAllowed(Boolean billingAllowed) {
		this.billingAllowed = billingAllowed;
	}

	public void setBillingAllowed(
		UnsafeSupplier<Boolean, Exception> billingAllowedUnsafeSupplier) {

		try {
			billingAllowed = billingAllowedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean billingAllowed;

	public Boolean getGroupFilterEnabled() {
		return groupFilterEnabled;
	}

	public void setGroupFilterEnabled(Boolean groupFilterEnabled) {
		this.groupFilterEnabled = groupFilterEnabled;
	}

	public void setGroupFilterEnabled(
		UnsafeSupplier<Boolean, Exception> groupFilterEnabledUnsafeSupplier) {

		try {
			groupFilterEnabled = groupFilterEnabledUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean groupFilterEnabled;

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

	public Integer getIdd() {
		return idd;
	}

	public void setIdd(Integer idd) {
		this.idd = idd;
	}

	public void setIdd(UnsafeSupplier<Integer, Exception> iddUnsafeSupplier) {
		try {
			idd = iddUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer idd;

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

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public void setNumber(
		UnsafeSupplier<Integer, Exception> numberUnsafeSupplier) {

		try {
			number = numberUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer number;

	public Double getPosition() {
		return position;
	}

	public void setPosition(Double position) {
		this.position = position;
	}

	public void setPosition(
		UnsafeSupplier<Double, Exception> positionUnsafeSupplier) {

		try {
			position = positionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double position;

	public Region[] getRegions() {
		return regions;
	}

	public void setRegions(Region[] regions) {
		this.regions = regions;
	}

	public void setRegions(
		UnsafeSupplier<Region[], Exception> regionsUnsafeSupplier) {

		try {
			regions = regionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Region[] regions;

	public Boolean getShippingAllowed() {
		return shippingAllowed;
	}

	public void setShippingAllowed(Boolean shippingAllowed) {
		this.shippingAllowed = shippingAllowed;
	}

	public void setShippingAllowed(
		UnsafeSupplier<Boolean, Exception> shippingAllowedUnsafeSupplier) {

		try {
			shippingAllowed = shippingAllowedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean shippingAllowed;

	public Boolean getSubjectToVAT() {
		return subjectToVAT;
	}

	public void setSubjectToVAT(Boolean subjectToVAT) {
		this.subjectToVAT = subjectToVAT;
	}

	public void setSubjectToVAT(
		UnsafeSupplier<Boolean, Exception> subjectToVATUnsafeSupplier) {

		try {
			subjectToVAT = subjectToVATUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean subjectToVAT;

	public Map<String, String> getTitle_i18n() {
		return title_i18n;
	}

	public void setTitle_i18n(Map<String, String> title_i18n) {
		this.title_i18n = title_i18n;
	}

	public void setTitle_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			title_i18nUnsafeSupplier) {

		try {
			title_i18n = title_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> title_i18n;

	public Boolean getZipRequired() {
		return zipRequired;
	}

	public void setZipRequired(Boolean zipRequired) {
		this.zipRequired = zipRequired;
	}

	public void setZipRequired(
		UnsafeSupplier<Boolean, Exception> zipRequiredUnsafeSupplier) {

		try {
			zipRequired = zipRequiredUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean zipRequired;

	@Override
	public Country clone() throws CloneNotSupportedException {
		return (Country)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Country)) {
			return false;
		}

		Country country = (Country)object;

		return Objects.equals(toString(), country.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CountrySerDes.toJSON(this);
	}

}