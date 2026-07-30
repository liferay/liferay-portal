/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.osb.faro.engine.client.model.Field;
import com.liferay.osb.faro.engine.client.model.Individual;
import com.liferay.osb.faro.web.internal.constants.FaroConstants;
import com.liferay.osb.faro.web.internal.model.display.main.FaroEntityDisplay;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
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
@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
public class IndividualDisplay implements FaroEntityDisplay {

	public IndividualDisplay() {
	}

	public IndividualDisplay(Individual individual) {
		List<Individual.Account> accounts = individual.getAccounts();

		if (accounts != null) {
			_accounts = accounts;
		}

		_individual = individual;

		_accountName = individual.getAccountName();
		_activitiesCount = individual.getActivitiesCount();
		_averageSessionDuration = individual.getAverageSessionDuration();
		_activityStatus = individual.getActivityStatus();
		_context = individual.getContext();
		_dataSourceIndividualPKs = individual.getDataSourceIndividualPKs();
		_dateCreated = individual.getDateCreated();
		_firstActivityDate = individual.getFirstActivityDate();
		_id = individual.getId();
		_lastActivityDate = individual.getLastActivityDate();
		_lastSessionCountry = individual.getLastSessionCountry();

		StringBundler sb = new StringBundler(3);

		sb.append(GetterUtil.get(getValue("givenName"), StringPool.BLANK));

		if (sb.index() > 0) {
			sb.append(StringPool.SPACE);
		}

		sb.append(GetterUtil.get(getValue("familyName"), StringPool.BLANK));

		_name = sb.toString();

		if (Validator.isBlank(_name)) {
			_name = _id;
		}

		_profileType = individual.getProfileType();
		_sessionsCount = individual.getSessionsCount();
		_type = FaroConstants.TYPE_INDIVIDUAL;

		addProperties(_propertyNames);
	}

	@Override
	public void addProperties(List<String> propertyNames) {
		for (String propertyName : propertyNames) {
			_propertiesMap.put(propertyName, getValue(propertyName));
		}
	}

	@Override
	public String getId() {
		return _id;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public Map<String, Object> getProperties() {
		return _propertiesMap;
	}

	@Override
	public int getType() {
		return _type;
	}

	protected Object getValue(String key) {
		Map<String, List<Field>> fieldsMap = _individual.getDemographics();

		List<Field> properties = fieldsMap.get(key);

		if (ListUtil.isNotEmpty(properties)) {
			Field field = properties.get(0);

			return field.getValue();
		}

		return null;
	}

	private static final List<String> _propertyNames = Arrays.asList(
		"additionalName", "birthDate", "country", "email", "familyName",
		"givenName", "image", "jobTitle", "languageId", "prefix", "screenName",
		"suffix", "userId", "uuid", "worksFor");

	private String _accountName;
	private List<Individual.Account> _accounts;
	private Long _activitiesCount;
	private String _activityStatus;
	private Long _averageSessionDuration;

	@JsonProperty("context")
	private Map<String, String> _context = new HashMap<>();

	private List<Individual.DataSourceIndividualPK> _dataSourceIndividualPKs;
	private Date _dateCreated;
	private Date _firstActivityDate;
	private String _id;

	@JsonIgnore
	private Individual _individual;

	private Date _lastActivityDate;
	private String _lastSessionCountry;
	private String _name;
	private String _profileType;

	@JsonProperty("properties")
	private Map<String, Object> _propertiesMap = new HashMap<>();

	private Long _sessionsCount;
	private int _type;

}