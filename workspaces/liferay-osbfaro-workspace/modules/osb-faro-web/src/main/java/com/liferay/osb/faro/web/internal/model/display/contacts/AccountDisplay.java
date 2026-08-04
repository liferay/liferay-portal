/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.osb.faro.engine.client.model.Account;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shinn Lok
 */
public class AccountDisplay {

	public AccountDisplay() {
	}

	public AccountDisplay(Account account) {
		_account = account;

		_accountName = account.getAccountName();
		_accountType = account.getAccountType();
		_activitiesCount = account.getActivitiesCount();
		_annualRevenue = account.getAnnualRevenue();
		_country = account.getCountry();
		_firstActivityDate = account.getFirstActivityDate();
		_id = account.getId();
		_industry = account.getIndustry();
		_lastActivityDate = account.getLastActivityDate();

		List<Account.LifecycleStage> lifecycleStages =
			account.getLifecycleStages();

		if (ListUtil.isNotEmpty(lifecycleStages)) {
			Account.LifecycleStage lifecycleStage = lifecycleStages.get(0);

			_lifecycleStage = lifecycleStage.getStageType();
		}

		_modifiedDate = account.getModifiedDate();
		_numberOfEmployees = account.getNumberOfEmployees();
		_website = account.getWebsite();
	}

	@JsonAnyGetter
	public Map<String, Object> getFieldValues() {
		Map<String, Object> fieldValues = new HashMap<>();

		if (_account == null) {
			return fieldValues;
		}

		List<Account.CalculatedField> calculatedFields =
			_account.getCalculatedFields();

		if (ListUtil.isNotEmpty(calculatedFields)) {
			for (Account.CalculatedField calculatedField : calculatedFields) {
				String name = calculatedField.getName();
				String namespace = calculatedField.getNamespace();

				if (Validator.isNull(name) || Validator.isNull(namespace)) {
					continue;
				}

				fieldValues.put(
					namespace + StringPool.SLASH + name,
					calculatedField.getValue());
			}
		}

		List<Account.Field> fields = _account.getFields();

		if (ListUtil.isEmpty(fields)) {
			return fieldValues;
		}

		for (Account.Field field : fields) {
			String name = field.getName();

			if (Validator.isNull(name) || _defaultFieldNames.contains(name)) {
				continue;
			}

			fieldValues.put(name, field.getValue());
		}

		return fieldValues;
	}

	private static final List<String> _defaultFieldNames = Arrays.asList(
		"accountName", "accountType", "activitiesCount", "annualRevenue",
		"country", "id", "industry", "lifecycleStage", "numberOfEmployees",
		"website");

	@JsonIgnore
	private Account _account;

	private String _accountName;
	private String _accountType;
	private Long _activitiesCount;
	private Double _annualRevenue;
	private String _country;

	@JsonProperty("firstActive")
	private Date _firstActivityDate;

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