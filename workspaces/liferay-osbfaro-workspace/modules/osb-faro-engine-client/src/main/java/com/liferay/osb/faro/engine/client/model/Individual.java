/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Matthew Kong
 */
public class Individual {

	public String getAccountName() {
		return _accountName;
	}

	public List<Account> getAccounts() {
		return _accounts;
	}

	public Long getActivitiesCount() {
		return _activitiesCount;
	}

	public String getActivityStatus() {
		return _activityStatus;
	}

	public Long getAverageSessionDuration() {
		return _averageSessionDuration;
	}

	public Map<String, String> getContext() {
		return _context;
	}

	public Map<String, List<Field>> getCustom() {
		return _custom;
	}

	public List<DataSourceIndividualPK> getDataSourceIndividualPKs() {
		return _dataSourceIndividualPKs;
	}

	public Date getDateCreated() {
		if (_dateCreated == null) {
			return null;
		}

		return new Date(_dateCreated.getTime());
	}

	public Date getDateModified() {
		if (_dateModified == null) {
			return null;
		}

		return new Date(_dateModified.getTime());
	}

	public Map<String, List<Field>> getDemographics() {
		return _demographics;
	}

	@JsonProperty("_embedded")
	public Map<String, Object> getEmbeddedResources() {
		return _embeddedResources;
	}

	public Date getFirstActivityDate() {
		if (_firstActivityDate == null) {
			return null;
		}

		return new Date(_firstActivityDate.getTime());
	}

	public String getId() {
		return _id;
	}

	public Date getLastActivityDate() {
		if (_lastActivityDate == null) {
			return null;
		}

		return new Date(_lastActivityDate.getTime());
	}

	public String getLastSessionCountry() {
		return _lastSessionCountry;
	}

	public String getProfileType() {
		return _profileType;
	}

	public Long getSessionsCount() {
		return _sessionsCount;
	}

	public void setAccountName(String accountName) {
		_accountName = accountName;
	}

	public void setAccounts(List<Account> accounts) {
		_accounts = accounts;
	}

	public void setActivitiesCount(long activitiesCount) {
		_activitiesCount = activitiesCount;
	}

	public void setActivityStatus(String activityStatus) {
		_activityStatus = activityStatus;
	}

	public void setAverageSessionDuration(Long averageSessionDuration) {
		_averageSessionDuration = averageSessionDuration;
	}

	public void setContext(Map<String, String> context) {
		_context = context;
	}

	public void setCustom(Map<String, List<Field>> custom) {
		_custom = custom;
	}

	public void setDataSourceIndividualPKs(
		List<DataSourceIndividualPK> dataSourceIndividualPKs) {

		_dataSourceIndividualPKs = dataSourceIndividualPKs;
	}

	public void setDateCreated(Date dateCreated) {
		if (dateCreated != null) {
			_dateCreated = new Date(dateCreated.getTime());
		}
	}

	public void setDateModified(Date dateModified) {
		if (dateModified != null) {
			_dateModified = new Date(dateModified.getTime());
		}
	}

	public void setDemographics(Map<String, List<Field>> demographics) {
		_demographics = demographics;
	}

	public void setEmbeddedResources(Map<String, Object> embeddedResources) {
		_embeddedResources = embeddedResources;
	}

	public void setFirstActivityDate(Date firstActivityDate) {
		if (firstActivityDate != null) {
			_firstActivityDate = new Date(firstActivityDate.getTime());
		}
	}

	public void setId(String id) {
		_id = id;
	}

	public void setLastActivityDate(Date lastActivityDate) {
		if (lastActivityDate != null) {
			_lastActivityDate = new Date(lastActivityDate.getTime());
		}
	}

	public void setLastSessionCountry(String lastSessionCountry) {
		_lastSessionCountry = lastSessionCountry;
	}

	public void setProfileType(String profileType) {
		_profileType = profileType;
	}

	public void setSessionsCount(Long sessionsCount) {
		_sessionsCount = sessionsCount;
	}

	public static class Account {

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

		@JsonProperty("createDate")
		public Date getCreatedDate() {
			if (_createdDate == null) {
				return null;
			}

			return new Date(_createdDate.getTime());
		}

		public String getCurrencyCode() {
			return _currencyCode;
		}

		public int getCustomerSince() {
			return _customerSince;
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

		public long getNumberOfEmployees() {
			return _numberOfEmployees;
		}

		public String getState() {
			return _state;
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

		public void setCreatedLocalDate(Date createdDate) {
			if (createdDate != null) {
				_createdDate = new Date(createdDate.getTime());
			}
		}

		public void setCurrencyCode(String currencyCode) {
			_currencyCode = currencyCode;
		}

		public void setCustomerSince(int customerSince) {
			_customerSince = customerSince;
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

		public void setNumberOfEmployees(long numberOfEmployees) {
			_numberOfEmployees = numberOfEmployees;
		}

		public void setState(String state) {
			_state = state;
		}

		private String _accountName;
		private String _accountType;
		private Double _annualRevenue;
		private String _country;
		private Date _createdDate;
		private String _currencyCode;
		private int _customerSince;
		private String _id;
		private String _industry;
		private Date _lastActivityDate;
		private long _numberOfEmployees;
		private String _state;

	}

	public static class DataSourceIndividualPK {

		public String getDataSourceId() {
			return _dataSourceId;
		}

		public String getDataSourceType() {
			return _dataSourceType;
		}

		public List<String> getIndividualPKs() {
			return _individualPKs;
		}

		public void setDataSourceId(String dataSourceId) {
			_dataSourceId = dataSourceId;
		}

		public void setDataSourceType(String dataSourceType) {
			_dataSourceType = dataSourceType;
		}

		public void setIndividualPKs(List<String> individualPKs) {
			_individualPKs = individualPKs;
		}

		private String _dataSourceId;
		private String _dataSourceType;
		private List<String> _individualPKs;

	}

	private String _accountName;
	private List<Account> _accounts;
	private Long _activitiesCount;
	private String _activityStatus;
	private Long _averageSessionDuration;
	private Map<String, String> _context = new HashMap<>();
	private Map<String, List<Field>> _custom = new HashMap<>();
	private List<DataSourceIndividualPK> _dataSourceIndividualPKs =
		new ArrayList<>();
	private Date _dateCreated;
	private Date _dateModified;
	private Map<String, List<Field>> _demographics = new HashMap<>();
	private Map<String, Object> _embeddedResources = new HashMap<>();
	private Date _firstActivityDate;
	private String _id;
	private Date _lastActivityDate;
	private String _lastSessionCountry;
	private String _profileType;
	private Long _sessionsCount;

}