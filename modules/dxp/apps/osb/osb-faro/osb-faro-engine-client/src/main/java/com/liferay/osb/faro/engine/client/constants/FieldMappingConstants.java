/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.constants;

import com.liferay.osb.faro.engine.client.model.FieldMappingMap;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shinn Lok
 */
public class FieldMappingConstants {

	public static final String CONTEXT_CUSTOM = "custom";

	public static final String CONTEXT_DEMOGRAPHICS = "demographics";

	public static final String CONTEXT_FIELDS = "fields";

	public static final String CONTEXT_INTERESTS = "interests";

	public static final String CONTEXT_ORGANIZATION = "organization";

	public static final String OWNER_TYPE_ACCOUNT = "account";

	public static final String OWNER_TYPE_ACCOUNT_SEGMENT = "account-segment";

	public static final String OWNER_TYPE_INDIVIDUAL = "individual";

	public static final String OWNER_TYPE_INDIVIDUAL_SEGMENT =
		"individual-segment";

	public static final String OWNER_TYPE_ORGANIZATION = "organization";

	public static final String TYPE_BOOLEAN = "Boolean";

	public static final String TYPE_DATE = "Date";

	public static final String TYPE_DECIMAL = "Decimal";

	public static final String TYPE_INTEGER = "Integer";

	public static final String TYPE_NUMBER = "Number";

	public static final String TYPE_TEXT = "Text";

	public static Map<String, String> getContexts() {
		return _contexts;
	}

	public static List<FieldMappingMap> getDefaultFieldMappingMaps() {
		return _defaultFieldMappingMaps;
	}

	public static String getDemographicsFieldMappingLanguageKey(
		String fieldName) {

		return _demographicsFieldMappingLanguageKeys.get(fieldName);
	}

	public static Map<String, String> getFieldTypes() {
		return _fieldTypes;
	}

	public static List<FieldMappingMap> getLiferayFieldMappingMaps() {
		return _liferayFieldMappingMaps;
	}

	public static Map<String, String> getLiferayFieldNames() {
		if (MapUtil.isNotEmpty(_liferayFieldNames)) {
			return _liferayFieldNames;
		}

		for (FieldMappingMap fieldMappingMap : _liferayFieldMappingMaps) {
			_liferayFieldNames.put(
				fieldMappingMap.getDataSourceFieldName(),
				fieldMappingMap.getName());
		}

		return _liferayFieldNames;
	}

	public static Map<String, String> getOwnerTypes() {
		return _ownerTypes;
	}

	public static List<FieldMappingMap> getSalesforceAccountFieldMappingMaps() {
		return _salesforceAccountFieldMappingMaps;
	}

	public static List<FieldMappingMap>
		getSalesforceIndividualFieldMappingMaps() {

		return _salesforceIndividualFieldMappingMaps;
	}

	public static List<String> getSearchFieldMappingNames() {
		return _searchFieldMappingNames;
	}

	private static final Map<String, String> _contexts = HashMapBuilder.put(
		CONTEXT_CUSTOM, CONTEXT_CUSTOM
	).put(
		CONTEXT_DEMOGRAPHICS, CONTEXT_DEMOGRAPHICS
	).put(
		CONTEXT_INTERESTS, CONTEXT_INTERESTS
	).put(
		CONTEXT_ORGANIZATION, CONTEXT_ORGANIZATION
	).build();
	private static final List<FieldMappingMap> _defaultFieldMappingMaps =
		Arrays.asList(
			new FieldMappingMap(null, "email", TYPE_TEXT),
			new FieldMappingMap(null, "familyName", TYPE_TEXT),
			new FieldMappingMap(null, "givenName", TYPE_TEXT),
			new FieldMappingMap(null, "image", TYPE_TEXT),
			new FieldMappingMap(null, "jobTitle", TYPE_TEXT),
			new FieldMappingMap(null, "worksFor", TYPE_TEXT));
	private static final Map<String, String>
		_demographicsFieldMappingLanguageKeys = HashMapBuilder.put(
			"birthday", "field.birth-date"
		).put(
			"emailAddress", "field.email-address"
		).put(
			"firstName", "field.first-name"
		).put(
			"jobTitle", "field.job-title"
		).put(
			"languageId", "selected-language"
		).put(
			"lastName", "field.last-name"
		).put(
			"middleName", "middle-name"
		).put(
			"modifiedDate", "field.date-modified"
		).put(
			"screenName", "field.screen-name"
		).build();
	private static final Map<String, String> _fieldTypes = HashMapBuilder.put(
		"boolean", TYPE_BOOLEAN
	).put(
		"date", TYPE_DATE
	).put(
		"number", TYPE_NUMBER
	).put(
		"string", TYPE_TEXT
	).build();
	private static final List<FieldMappingMap> _liferayFieldMappingMaps =
		Arrays.asList(
			new FieldMappingMap("addresses", "address", TYPE_TEXT),
			new FieldMappingMap("birthday", "birthDate", TYPE_DATE),
			new FieldMappingMap("emailAddress", "email", TYPE_TEXT),
			new FieldMappingMap("firstName", "givenName", TYPE_TEXT),
			new FieldMappingMap("gender", "gender", TYPE_TEXT),
			new FieldMappingMap("jobTitle", "jobTitle", TYPE_TEXT),
			new FieldMappingMap("lastName", "familyName", TYPE_TEXT),
			new FieldMappingMap("middleName", "additionalName", TYPE_TEXT),
			new FieldMappingMap("phones", "telephone", TYPE_TEXT));
	private static final Map<String, String> _liferayFieldNames =
		new HashMap<>();
	private static final Map<String, String> _ownerTypes = HashMapBuilder.put(
		OWNER_TYPE_ACCOUNT, OWNER_TYPE_ACCOUNT
	).put(
		OWNER_TYPE_INDIVIDUAL, OWNER_TYPE_INDIVIDUAL
	).put(
		OWNER_TYPE_ORGANIZATION, OWNER_TYPE_ORGANIZATION
	).build();
	private static final List<FieldMappingMap>
		_salesforceAccountFieldMappingMaps = Arrays.asList(
			new FieldMappingMap("id", "accountId", TYPE_TEXT),
			new FieldMappingMap("AnnualRevenue", "annualRevenue", TYPE_NUMBER),
			new FieldMappingMap("BillingCity", "billingCity", TYPE_TEXT),
			new FieldMappingMap("BillingCountry", "billingCountry", TYPE_TEXT),
			new FieldMappingMap(
				"BillingPostalCode", "billingPostalCode", TYPE_TEXT),
			new FieldMappingMap("BillingState", "billingState", TYPE_TEXT),
			new FieldMappingMap("BillingStreet", "billingStreet", TYPE_TEXT),
			new FieldMappingMap(
				"CurrencyIsoCode", "currencyIsoCode", TYPE_TEXT),
			new FieldMappingMap("Description", "description", TYPE_TEXT),
			new FieldMappingMap("Fax", "fax", TYPE_TEXT),
			new FieldMappingMap("Industry", "industry", TYPE_TEXT),
			new FieldMappingMap("Name", "accountName", TYPE_TEXT),
			new FieldMappingMap(
				"NumberOfEmployees", "numberOfEmployees", TYPE_NUMBER),
			new FieldMappingMap("Ownership", "ownership", TYPE_TEXT),
			new FieldMappingMap("Phone", "phone", TYPE_TEXT),
			new FieldMappingMap("Rating", "rating", TYPE_TEXT),
			new FieldMappingMap("ShippingCity", "shippingCity", TYPE_TEXT),
			new FieldMappingMap(
				"ShippingCountry", "shippingCountry", TYPE_TEXT),
			new FieldMappingMap(
				"ShippingPostalCode", "shippingPostalCode", TYPE_TEXT),
			new FieldMappingMap("ShippingState", "shippingState", TYPE_TEXT),
			new FieldMappingMap("ShippingStreet", "shippingStreet", TYPE_TEXT),
			new FieldMappingMap("Type", "accountType", TYPE_TEXT),
			new FieldMappingMap("Website", "website", TYPE_TEXT),
			new FieldMappingMap("YearStarted", "yearStarted", TYPE_NUMBER));
	private static final List<FieldMappingMap>
		_salesforceIndividualFieldMappingMaps = Arrays.asList(
			new FieldMappingMap("birthDate", "birthDate", TYPE_DATE),
			new FieldMappingMap("city", "city", TYPE_TEXT),
			new FieldMappingMap("company", "worksFor", TYPE_TEXT),
			new FieldMappingMap("country", "country", TYPE_TEXT),
			new FieldMappingMap("department", "department", TYPE_TEXT),
			new FieldMappingMap("description", "description", TYPE_TEXT),
			new FieldMappingMap("doNotCall", "doNotCall", TYPE_BOOLEAN),
			new FieldMappingMap("email", "email", TYPE_TEXT),
			new FieldMappingMap("fax", "fax", TYPE_TEXT),
			new FieldMappingMap("firstName", "givenName", TYPE_TEXT),
			new FieldMappingMap("fullName", "fullName", TYPE_TEXT),
			new FieldMappingMap("industry", "industry", TYPE_TEXT),
			new FieldMappingMap("lastName", "familyName", TYPE_TEXT),
			new FieldMappingMap("middleName", "additionalName", TYPE_TEXT),
			new FieldMappingMap("mobilePhone", "mobilePhone", TYPE_TEXT),
			new FieldMappingMap("phone", "telephone", TYPE_TEXT),
			new FieldMappingMap("postalCode", "postalCode", TYPE_TEXT),
			new FieldMappingMap("salutation", "salutation", TYPE_TEXT),
			new FieldMappingMap("state", "state", TYPE_TEXT),
			new FieldMappingMap("street", "street", TYPE_TEXT),
			new FieldMappingMap("suffix", "suffix", TYPE_TEXT),
			new FieldMappingMap("title", "jobTitle", TYPE_TEXT));
	private static final List<String> _searchFieldMappingNames = Arrays.asList(
		"email", "familyName", "givenName", "image", "jobTitle", "worksFor");

}