/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.osb.faro.engine.client.model.Account;

import java.util.Date;

/**
 * @author Shinn Lok
 */
public class AccountDisplay {

	public AccountDisplay() {
	}

	public AccountDisplay(Account account) {
		_accountName = account.getAccountName();
		_accountType = account.getAccountType();
		_annualRevenue = account.getAnnualRevenue();
		_country = account.getCountry();
		_id = account.getId();
		_industry = account.getIndustry();
		_lastActivityDate = account.getLastActivityDate();
		_lifecycleStage = account.getLifecycleStage();
		_modifiedDate = account.getModifiedDate();
		_numberOfEmployees = account.getNumberOfEmployees();
		_website = account.getWebsite();
	}

	private String _accountName;
	private String _accountType;
	private Double _annualRevenue;
	private String _country;
	private String _id;
	private String _industry;

	@JsonProperty("lastActive")
	private Date _lastActivityDate;

	private String _lifecycleStage;

	@JsonProperty("lastEnriched")
	private Date _modifiedDate;

	private Integer _numberOfEmployees;
	private String _website;

}