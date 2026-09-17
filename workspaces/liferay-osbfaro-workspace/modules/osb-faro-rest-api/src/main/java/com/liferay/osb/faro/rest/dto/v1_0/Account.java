/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "An account synced to Analytics Cloud. Accounts aggregate individuals (or contacts) employed by or associated with a single organization.",
	value = "Account"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "An account synced to Analytics Cloud. Accounts aggregate individuals (or contacts) employed by or associated with a single organization."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Account")
public class Account implements Serializable {

	public static Account toDTO(String json) {
		return ObjectMapperUtil.readValue(Account.class, json);
	}

	public static Account unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Account.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Name of the account."
	)
	public String getAccountName() {
		if (_accountNameSupplier != null) {
			accountName = _accountNameSupplier.get();

			_accountNameSupplier = null;
		}

		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;

		_accountNameSupplier = null;
	}

	@JsonIgnore
	public void setAccountName(
		UnsafeSupplier<String, Exception> accountNameUnsafeSupplier) {

		_accountNameSupplier = () -> {
			try {
				return accountNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Name of the account.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String accountName;

	@JsonIgnore
	private Supplier<String> _accountNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Account classification supplied by the data source (e.g. 'Customer', 'Partner')."
	)
	public String getAccountType() {
		if (_accountTypeSupplier != null) {
			accountType = _accountTypeSupplier.get();

			_accountTypeSupplier = null;
		}

		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;

		_accountTypeSupplier = null;
	}

	@JsonIgnore
	public void setAccountType(
		UnsafeSupplier<String, Exception> accountTypeUnsafeSupplier) {

		_accountTypeSupplier = () -> {
			try {
				return accountTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Account classification supplied by the data source (e.g. 'Customer', 'Partner')."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String accountType;

	@JsonIgnore
	private Supplier<String> _accountTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Total number of tracked activities attributed to individuals associated with this account, over the account's whole history. Not a windowed count."
	)
	public Long getActivitiesCount() {
		if (_activitiesCountSupplier != null) {
			activitiesCount = _activitiesCountSupplier.get();

			_activitiesCountSupplier = null;
		}

		return activitiesCount;
	}

	public void setActivitiesCount(Long activitiesCount) {
		this.activitiesCount = activitiesCount;

		_activitiesCountSupplier = null;
	}

	@JsonIgnore
	public void setActivitiesCount(
		UnsafeSupplier<Long, Exception> activitiesCountUnsafeSupplier) {

		_activitiesCountSupplier = () -> {
			try {
				return activitiesCountUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Total number of tracked activities attributed to individuals associated with this account, over the account's whole history. Not a windowed count."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long activitiesCount;

	@JsonIgnore
	private Supplier<Long> _activitiesCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Annual revenue of the account."
	)
	public Double getAnnualRevenue() {
		if (_annualRevenueSupplier != null) {
			annualRevenue = _annualRevenueSupplier.get();

			_annualRevenueSupplier = null;
		}

		return annualRevenue;
	}

	public void setAnnualRevenue(Double annualRevenue) {
		this.annualRevenue = annualRevenue;

		_annualRevenueSupplier = null;
	}

	@JsonIgnore
	public void setAnnualRevenue(
		UnsafeSupplier<Double, Exception> annualRevenueUnsafeSupplier) {

		_annualRevenueSupplier = () -> {
			try {
				return annualRevenueUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Annual revenue of the account.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double annualRevenue;

	@JsonIgnore
	private Supplier<Double> _annualRevenueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Country name where the account is headquartered."
	)
	public String getCountry() {
		if (_countrySupplier != null) {
			country = _countrySupplier.get();

			_countrySupplier = null;
		}

		return country;
	}

	public void setCountry(String country) {
		this.country = country;

		_countrySupplier = null;
	}

	@JsonIgnore
	public void setCountry(
		UnsafeSupplier<String, Exception> countryUnsafeSupplier) {

		_countrySupplier = () -> {
			try {
				return countryUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Country name where the account is headquartered."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String country;

	@JsonIgnore
	private Supplier<String> _countrySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Last time the account record was modified within Analytics Cloud."
	)
	public Date getDateModified() {
		if (_dateModifiedSupplier != null) {
			dateModified = _dateModifiedSupplier.get();

			_dateModifiedSupplier = null;
		}

		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;

		_dateModifiedSupplier = null;
	}

	@JsonIgnore
	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		_dateModifiedSupplier = () -> {
			try {
				return dateModifiedUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Last time the account record was modified within Analytics Cloud."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Date of the earliest tracked activity from any individual associated with this account."
	)
	public Date getFirstActivityDate() {
		if (_firstActivityDateSupplier != null) {
			firstActivityDate = _firstActivityDateSupplier.get();

			_firstActivityDateSupplier = null;
		}

		return firstActivityDate;
	}

	public void setFirstActivityDate(Date firstActivityDate) {
		this.firstActivityDate = firstActivityDate;

		_firstActivityDateSupplier = null;
	}

	@JsonIgnore
	public void setFirstActivityDate(
		UnsafeSupplier<Date, Exception> firstActivityDateUnsafeSupplier) {

		_firstActivityDateSupplier = () -> {
			try {
				return firstActivityDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Date of the earliest tracked activity from any individual associated with this account."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date firstActivityDate;

	@JsonIgnore
	private Supplier<Date> _firstActivityDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Account ID. Use this with `getWorkspaceGroupAccount` to fetch the account directly."
	)
	public String getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(String id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Account ID. Use this with `getWorkspaceGroupAccount` to fetch the account directly."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String id;

	@JsonIgnore
	private Supplier<String> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Industry classification of the account (e.g. 'Software', 'Manufacturing')."
	)
	public String getIndustry() {
		if (_industrySupplier != null) {
			industry = _industrySupplier.get();

			_industrySupplier = null;
		}

		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;

		_industrySupplier = null;
	}

	@JsonIgnore
	public void setIndustry(
		UnsafeSupplier<String, Exception> industryUnsafeSupplier) {

		_industrySupplier = () -> {
			try {
				return industryUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Industry classification of the account (e.g. 'Software', 'Manufacturing')."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String industry;

	@JsonIgnore
	private Supplier<String> _industrySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Date of most recent activity from any individual associated with this account."
	)
	public Date getLastActivityDate() {
		if (_lastActivityDateSupplier != null) {
			lastActivityDate = _lastActivityDateSupplier.get();

			_lastActivityDateSupplier = null;
		}

		return lastActivityDate;
	}

	public void setLastActivityDate(Date lastActivityDate) {
		this.lastActivityDate = lastActivityDate;

		_lastActivityDateSupplier = null;
	}

	@JsonIgnore
	public void setLastActivityDate(
		UnsafeSupplier<Date, Exception> lastActivityDateUnsafeSupplier) {

		_lastActivityDateSupplier = () -> {
			try {
				return lastActivityDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Date of most recent activity from any individual associated with this account."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date lastActivityDate;

	@JsonIgnore
	private Supplier<Date> _lastActivityDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Sales/marketing lifecycle stage of the account (e.g. 'Prospect', 'Customer')."
	)
	public String getLifecycleStage() {
		if (_lifecycleStageSupplier != null) {
			lifecycleStage = _lifecycleStageSupplier.get();

			_lifecycleStageSupplier = null;
		}

		return lifecycleStage;
	}

	public void setLifecycleStage(String lifecycleStage) {
		this.lifecycleStage = lifecycleStage;

		_lifecycleStageSupplier = null;
	}

	@JsonIgnore
	public void setLifecycleStage(
		UnsafeSupplier<String, Exception> lifecycleStageUnsafeSupplier) {

		_lifecycleStageSupplier = () -> {
			try {
				return lifecycleStageUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Sales/marketing lifecycle stage of the account (e.g. 'Prospect', 'Customer')."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String lifecycleStage;

	@JsonIgnore
	private Supplier<String> _lifecycleStageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Number of employees reported for the account."
	)
	public Integer getNumberOfEmployees() {
		if (_numberOfEmployeesSupplier != null) {
			numberOfEmployees = _numberOfEmployeesSupplier.get();

			_numberOfEmployeesSupplier = null;
		}

		return numberOfEmployees;
	}

	public void setNumberOfEmployees(Integer numberOfEmployees) {
		this.numberOfEmployees = numberOfEmployees;

		_numberOfEmployeesSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfEmployees(
		UnsafeSupplier<Integer, Exception> numberOfEmployeesUnsafeSupplier) {

		_numberOfEmployeesSupplier = () -> {
			try {
				return numberOfEmployeesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Number of employees reported for the account.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer numberOfEmployees;

	@JsonIgnore
	private Supplier<Integer> _numberOfEmployeesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Website of the account."
	)
	public String getWebsite() {
		if (_websiteSupplier != null) {
			website = _websiteSupplier.get();

			_websiteSupplier = null;
		}

		return website;
	}

	public void setWebsite(String website) {
		this.website = website;

		_websiteSupplier = null;
	}

	@JsonIgnore
	public void setWebsite(
		UnsafeSupplier<String, Exception> websiteUnsafeSupplier) {

		_websiteSupplier = () -> {
			try {
				return websiteUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Website of the account.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String website;

	@JsonIgnore
	private Supplier<String> _websiteSupplier;

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
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		String accountName = getAccountName();

		if (accountName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountName\": ");

			sb.append("\"");

			sb.append(_escape(accountName));

			sb.append("\"");
		}

		String accountType = getAccountType();

		if (accountType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountType\": ");

			sb.append("\"");

			sb.append(_escape(accountType));

			sb.append("\"");
		}

		Long activitiesCount = getActivitiesCount();

		if (activitiesCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"activitiesCount\": ");

			sb.append(activitiesCount);
		}

		Double annualRevenue = getAnnualRevenue();

		if (annualRevenue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"annualRevenue\": ");

			sb.append(annualRevenue);
		}

		String country = getCountry();

		if (country != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"country\": ");

			sb.append("\"");

			sb.append(_escape(country));

			sb.append("\"");
		}

		Date dateModified = getDateModified();

		if (dateModified != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateModified));

			sb.append("\"");
		}

		Date firstActivityDate = getFirstActivityDate();

		if (firstActivityDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"firstActivityDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(firstActivityDate));

			sb.append("\"");
		}

		String id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(id));

			sb.append("\"");
		}

		String industry = getIndustry();

		if (industry != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"industry\": ");

			sb.append("\"");

			sb.append(_escape(industry));

			sb.append("\"");
		}

		Date lastActivityDate = getLastActivityDate();

		if (lastActivityDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastActivityDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(lastActivityDate));

			sb.append("\"");
		}

		String lifecycleStage = getLifecycleStage();

		if (lifecycleStage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lifecycleStage\": ");

			sb.append("\"");

			sb.append(_escape(lifecycleStage));

			sb.append("\"");
		}

		Integer numberOfEmployees = getNumberOfEmployees();

		if (numberOfEmployees != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfEmployees\": ");

			sb.append(numberOfEmployees);
		}

		String website = getWebsite();

		if (website != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"website\": ");

			sb.append("\"");

			sb.append(_escape(website));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.Account",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:1374552283