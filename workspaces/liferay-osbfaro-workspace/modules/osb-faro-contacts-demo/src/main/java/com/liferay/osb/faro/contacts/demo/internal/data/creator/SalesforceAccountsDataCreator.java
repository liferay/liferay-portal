/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.contacts.demo.internal.data.creator;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Matthew Kong
 */
public class SalesforceAccountsDataCreator extends DataCreator {

	public SalesforceAccountsDataCreator(
		ContactsEngineClient contactsEngineClient, FaroProject faroProject,
		String dataSourceId) {

		super(
			contactsEngineClient, faroProject, "osbasahsalesforceraw",
			"accounts");

		_dataSourceId = dataSourceId;

		_salesforceAuditEventsDataCreator =
			new SalesforceAuditEventsDataCreator(
				contactsEngineClient, faroProject, "Account");
	}

	@Override
	public void execute() {
		super.execute();

		_salesforceAuditEventsDataCreator.execute();
	}

	@Override
	protected Map<String, Object> doCreate(Object[] params) {
		String uuid = internet.uuid();

		Map<String, Object> salesforceAccount =
			HashMapBuilder.<String, Object>put(
				"accountName", company.name()
			).put(
				"dataSourceId", _dataSourceId
			).put(
				"fields",
				toFields(
					HashMapBuilder.<String, Object>put(
						"accountType", "Customer"
					).put(
						"annualRevenue", number.numberBetween(0, 1000) * 1000
					).put(
						"billingCity", address.city()
					).put(
						"billingPostalCode", address.zipCode()
					).put(
						"billingStreet", address.streetAddress()
					).put(
						"country", address.country()
					).put(
						"currencyCode",
						_currencyIsoCodes.get(
							random.nextInt(_currencyIsoCodes.size()))
					).put(
						"description", company.catchPhrase()
					).put(
						"fax", phoneNumber.phoneNumber()
					).put(
						"industry", company.industry()
					).put(
						"numberOfEmployees", number.numberBetween(1, 100000)
					).put(
						"ownership", "Private"
					).put(
						"phone", phoneNumber.phoneNumber()
					).put(
						"shippingCity", address.city()
					).put(
						"shippingCountry", address.country()
					).put(
						"shippingPostalCode", address.zipCode()
					).put(
						"shippingState", address.state()
					).put(
						"shippingStreet", address.streetAddress()
					).put(
						"state", address.state()
					).put(
						"website", "https://" + internet.url()
					).put(
						"yearStarted", number.numberBetween(1900, 2019)
					).build())
			).put(
				"id", uuid
			).put(
				"modifiedDate", formatDate(new Date())
			).put(
				"salesforceId", uuid
			).build();

		_salesforceAuditEventsDataCreator.create(
			new Object[] {salesforceAccount});

		return salesforceAccount;
	}

	private static final List<String> _currencyIsoCodes = Arrays.asList(
		"CNY", "EUR", "GBP", "USD");

	private final String _dataSourceId;
	private final SalesforceAuditEventsDataCreator
		_salesforceAuditEventsDataCreator;

}