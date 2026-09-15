/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.dto.v1_0;

import com.liferay.osb.faro.rest.client.function.UnsafeSupplier;
import com.liferay.osb.faro.rest.client.serdes.v1_0.AccountSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class Account implements Cloneable, Serializable {

	public static Account toDTO(String json) {
		return AccountSerDes.toDTO(json);
	}

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

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public void setAccountType(
		UnsafeSupplier<String, Exception> accountTypeUnsafeSupplier) {

		try {
			accountType = accountTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String accountType;

	public Long getActivitiesCount() {
		return activitiesCount;
	}

	public void setActivitiesCount(Long activitiesCount) {
		this.activitiesCount = activitiesCount;
	}

	public void setActivitiesCount(
		UnsafeSupplier<Long, Exception> activitiesCountUnsafeSupplier) {

		try {
			activitiesCount = activitiesCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long activitiesCount;

	public Double getAnnualRevenue() {
		return annualRevenue;
	}

	public void setAnnualRevenue(Double annualRevenue) {
		this.annualRevenue = annualRevenue;
	}

	public void setAnnualRevenue(
		UnsafeSupplier<Double, Exception> annualRevenueUnsafeSupplier) {

		try {
			annualRevenue = annualRevenueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double annualRevenue;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setCountry(
		UnsafeSupplier<String, Exception> countryUnsafeSupplier) {

		try {
			country = countryUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String country;

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

	public Date getFirstActivityDate() {
		return firstActivityDate;
	}

	public void setFirstActivityDate(Date firstActivityDate) {
		this.firstActivityDate = firstActivityDate;
	}

	public void setFirstActivityDate(
		UnsafeSupplier<Date, Exception> firstActivityDateUnsafeSupplier) {

		try {
			firstActivityDate = firstActivityDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date firstActivityDate;

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

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public void setIndustry(
		UnsafeSupplier<String, Exception> industryUnsafeSupplier) {

		try {
			industry = industryUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String industry;

	public Date getLastActivityDate() {
		return lastActivityDate;
	}

	public void setLastActivityDate(Date lastActivityDate) {
		this.lastActivityDate = lastActivityDate;
	}

	public void setLastActivityDate(
		UnsafeSupplier<Date, Exception> lastActivityDateUnsafeSupplier) {

		try {
			lastActivityDate = lastActivityDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date lastActivityDate;

	public String getLifecycleStage() {
		return lifecycleStage;
	}

	public void setLifecycleStage(String lifecycleStage) {
		this.lifecycleStage = lifecycleStage;
	}

	public void setLifecycleStage(
		UnsafeSupplier<String, Exception> lifecycleStageUnsafeSupplier) {

		try {
			lifecycleStage = lifecycleStageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String lifecycleStage;

	public Integer getNumberOfEmployees() {
		return numberOfEmployees;
	}

	public void setNumberOfEmployees(Integer numberOfEmployees) {
		this.numberOfEmployees = numberOfEmployees;
	}

	public void setNumberOfEmployees(
		UnsafeSupplier<Integer, Exception> numberOfEmployeesUnsafeSupplier) {

		try {
			numberOfEmployees = numberOfEmployeesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer numberOfEmployees;

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public void setWebsite(
		UnsafeSupplier<String, Exception> websiteUnsafeSupplier) {

		try {
			website = websiteUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String website;

	@Override
	public Account clone() throws CloneNotSupportedException {
		return (Account)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Account)) {
			return false;
		}

		Account account = (Account)object;

		return Objects.equals(toString(), account.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AccountSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1858872924