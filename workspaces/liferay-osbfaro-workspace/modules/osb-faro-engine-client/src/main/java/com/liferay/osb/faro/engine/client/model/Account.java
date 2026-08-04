/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

import java.util.Date;
import java.util.List;

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

	public Long getActivitiesCount() {
		return _activitiesCount;
	}

	public Double getAnnualRevenue() {
		return _annualRevenue;
	}

	public List<CalculatedField> getCalculatedFields() {
		return _calculatedFields;
	}

	public String getCountry() {
		return _country;
	}

	public List<Field> getFields() {
		return _fields;
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

	public String getIndustry() {
		return _industry;
	}

	public Date getLastActivityDate() {
		if (_lastActivityDate == null) {
			return null;
		}

		return new Date(_lastActivityDate.getTime());
	}

	public List<LifecycleStage> getLifecycleStages() {
		return _lifecycleStages;
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

	public void setActivitiesCount(Long activitiesCount) {
		_activitiesCount = activitiesCount;
	}

	public void setAnnualRevenue(Double annualRevenue) {
		_annualRevenue = annualRevenue;
	}

	public void setCalculatedFields(List<CalculatedField> calculatedFields) {
		_calculatedFields = calculatedFields;
	}

	public void setCountry(String country) {
		_country = country;
	}

	public void setFields(List<Field> fields) {
		_fields = fields;
	}

	public void setFirstActivityDate(Date firstActivityDate) {
		if (firstActivityDate != null) {
			_firstActivityDate = new Date(firstActivityDate.getTime());
		}
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

	public void setLifecycleStages(List<LifecycleStage> lifecycleStages) {
		_lifecycleStages = lifecycleStages;
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

	public static class CalculatedField {

		public Date getModifiedDate() {
			if (_modifiedDate == null) {
				return null;
			}

			return new Date(_modifiedDate.getTime());
		}

		public String getName() {
			return _name;
		}

		public String getNamespace() {
			return _namespace;
		}

		public String getValue() {
			return _value;
		}

		public void setModifiedDate(Date modifiedDate) {
			if (modifiedDate != null) {
				_modifiedDate = new Date(modifiedDate.getTime());
			}
		}

		public void setName(String name) {
			_name = name;
		}

		public void setNamespace(String namespace) {
			_namespace = namespace;
		}

		public void setValue(String value) {
			_value = value;
		}

		private Date _modifiedDate;
		private String _name;
		private String _namespace;
		private String _value;

	}

	public static class Field {

		public String getDataSourceId() {
			return _dataSourceId;
		}

		public String getDataSourceName() {
			return _dataSourceName;
		}

		public Date getModifiedDate() {
			if (_modifiedDate == null) {
				return null;
			}

			return new Date(_modifiedDate.getTime());
		}

		public String getName() {
			return _name;
		}

		public String getSourceName() {
			return _sourceName;
		}

		public String getValue() {
			return _value;
		}

		public void setDataSourceId(String dataSourceId) {
			_dataSourceId = dataSourceId;
		}

		public void setDataSourceName(String dataSourceName) {
			_dataSourceName = dataSourceName;
		}

		public void setModifiedDate(Date modifiedDate) {
			if (modifiedDate != null) {
				_modifiedDate = new Date(modifiedDate.getTime());
			}
		}

		public void setName(String name) {
			_name = name;
		}

		public void setSourceName(String sourceName) {
			_sourceName = sourceName;
		}

		public void setValue(String value) {
			_value = value;
		}

		private String _dataSourceId;
		private String _dataSourceName;
		private Date _modifiedDate;
		private String _name;
		private String _sourceName;
		private String _value;

	}

	public static class LifecycleStage {

		public String getLifecycleName() {
			return _lifecycleName;
		}

		public String getStageType() {
			return _stageType;
		}

		public void setLifecycleName(String lifecycleName) {
			_lifecycleName = lifecycleName;
		}

		public void setStageType(String stageType) {
			_stageType = stageType;
		}

		private String _lifecycleName;
		private String _stageType;

	}

	private String _accountName;
	private String _accountType;
	private Long _activitiesCount;
	private Double _annualRevenue;
	private List<CalculatedField> _calculatedFields;
	private String _country;
	private List<Field> _fields;
	private Date _firstActivityDate;
	private String _id;
	private String _industry;
	private Date _lastActivityDate;
	private List<LifecycleStage> _lifecycleStages;
	private Date _modifiedDate;
	private Integer _numberOfEmployees;
	private String _website;

}