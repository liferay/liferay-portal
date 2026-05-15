/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

import java.util.Date;

/**
 * @author Matthew Kong
 */
public class Account {

	public String getAccountName() {
		return _accountName;
	}

	public String getAccountType() {
		return _accountType;
	}

	public Double getAnnualRevenue() {
		return _annualRevenue;
	}

	public String getCountry() {
		return _country;
	}

	public String getId() {
		return _id;
	}

	public String getIndustry() {
		return _industry;
	}

	public Date getLastActivityDate() {
		if (_lastActivityDate == null) {
			return null;
		}

		return new Date(_lastActivityDate.getTime());
	}

	public String getLifecycleStage() {
		return _lifecycleStage;
	}

	public Date getModifiedDate() {
		if (_modifiedDate == null) {
			return null;
		}

		return new Date(_modifiedDate.getTime());
	}

	public Integer getNumberOfEmployees() {
		return _numberOfEmployees;
	}

	public String getWebsite() {
		return _website;
	}

	public void setAccountName(String accountName) {
		_accountName = accountName;
	}

	public void setAccountType(String accountType) {
		_accountType = accountType;
	}

	public void setAnnualRevenue(Double annualRevenue) {
		_annualRevenue = annualRevenue;
	}

	public void setCountry(String country) {
		_country = country;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setIndustry(String industry) {
		_industry = industry;
	}

	public void setLastActivityDate(Date lastActivityDate) {
		if (lastActivityDate != null) {
			_lastActivityDate = new Date(lastActivityDate.getTime());
		}
	}

	public void setLifecycleStage(String lifecycleStage) {
		_lifecycleStage = lifecycleStage;
	}

	public void setModifiedDate(Date modifiedDate) {
		if (modifiedDate != null) {
			_modifiedDate = new Date(modifiedDate.getTime());
		}
	}

	public void setNumberOfEmployees(Integer numberOfEmployees) {
		_numberOfEmployees = numberOfEmployees;
	}

	public void setWebsite(String website) {
		_website = website;
	}

	private String _accountName;
	private String _accountType;
	private Double _annualRevenue;
	private String _country;
	private String _id;
	private String _industry;
	private Date _lastActivityDate;
	private String _lifecycleStage;
	private Date _modifiedDate;
	private Integer _numberOfEmployees;
	private String _website;

}