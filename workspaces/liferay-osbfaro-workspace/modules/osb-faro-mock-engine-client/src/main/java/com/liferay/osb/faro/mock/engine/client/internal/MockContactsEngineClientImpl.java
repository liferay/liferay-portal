/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.mock.engine.client.internal;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.constants.FieldMappingConstants;
import com.liferay.osb.faro.engine.client.model.ApiUsageMetric;
import com.liferay.osb.faro.engine.client.model.DataSourceUsageMetric;
import com.liferay.osb.faro.engine.client.model.Individual;
import com.liferay.osb.faro.engine.client.model.ProjectMetric;
import com.liferay.osb.faro.engine.client.model.ProjectUsageMetric;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Matthew Kong
 */
@Component(
	property = "service.ranking:Integer=100",
	service = ContactsEngineClient.class
)
public class MockContactsEngineClientImpl
	extends BaseMockContactsEngineClientImpl {

	@Override
	public Results<Individual> getAccountIndividuals(
		FaroProject faroProject, String accountId, String channelId,
		String query, String rangeEnd, Integer rangeKey, String rangeStart,
		int cur, int delta, String sortString) {

		return contactsEngineClient.getAccountIndividuals(
			faroProject, accountId, channelId, query, rangeEnd, rangeKey,
			rangeStart, cur, delta, sortString);
	}

	@Override
	public Results<ApiUsageMetric> getApiUsageMetrics(
		FaroProject faroProject, String endDateString, String startDateString) {

		return contactsEngineClient.getApiUsageMetrics(
			faroProject, endDateString, startDateString);
	}

	@Override
	public long getDXPUsersCount(FaroProject faroProject, String id) {
		return contactsEngineClient.getDXPUsersCount(faroProject, id);
	}

	@Override
	public Results<DataSourceUsageMetric> getDataSourceUsageMetrics(
		FaroProject faroProject, Date date) {

		return contactsEngineClient.getDataSourceUsageMetrics(
			faroProject, date);
	}

	@Override
	public List<String> getFieldNames(
		FaroProject faroProject, String label, String ownerType,
		Object values) {

		List<String> fieldNames = super.getFieldNames(
			faroProject, label, ownerType, values);

		String fieldName = _liferayFieldNames.get(label);

		if ((fieldName != null) && !fieldNames.contains(fieldName)) {
			fieldNames.add(0, fieldName);
		}

		return fieldNames;
	}

	@Override
	public List<List<String>> getFieldNamesList(
		FaroProject faroProject, List<String> labels, String ownerType,
		List<Object> valuesList) {

		List<List<String>> fieldNamesList = super.getFieldNamesList(
			faroProject, labels, ownerType, valuesList);

		for (int i = 0; i < fieldNamesList.size(); i++) {
			List<String> fieldNames = fieldNamesList.get(i);

			String fieldName = _liferayFieldNames.get(labels.get(i));

			if ((fieldName == null) || fieldNames.contains(fieldName)) {
				continue;
			}

			fieldNames.add(0, fieldName);
		}

		return fieldNamesList;
	}

	@Override
	public long getIdentitiesCount(FaroProject faroProject) {
		return contactsEngineClient.getIdentitiesCount(faroProject);
	}

	@Override
	public long getIndividualsCreatedBetweenCount(
		FaroProject faroProject, Date endDate, Date startDate) {

		return contactsEngineClient.getIndividualsCreatedBetweenCount(
			faroProject, endDate, startDate);
	}

	@Override
	public long getIndividualsCreatedSinceCount(
		FaroProject faroProject, Date startDate) {

		return contactsEngineClient.getIndividualsCreatedSinceCount(
			faroProject, startDate);
	}

	@Override
	public Date getLastSeenDate(FaroProject faroProject) {
		return contactsEngineClient.getLastSeenDate(faroProject);
	}

	@Override
	public Results<ProjectMetric> getProjectMetrics(FaroProject faroProject) {
		return contactsEngineClient.getProjectMetrics(faroProject);
	}

	@Override
	public Results<ProjectUsageMetric> getProjectUsageMetrics(
		FaroProject faroProject, Date sinceDate) {

		return contactsEngineClient.getProjectUsageMetrics(
			faroProject, sinceDate);
	}

	@Override
	public void insertBQProjects(List<FaroProject> faroProjects)
		throws Exception {

		contactsEngineClient.insertBQProjects(faroProjects);
	}

	@Override
	public void updateBQProject(FaroProject faroProject, Date startDate)
		throws Exception {

		contactsEngineClient.updateBQProject(faroProject, startDate);
	}

	private static final Map<String, String> _liferayFieldNames =
		FieldMappingConstants.getLiferayFieldNames();

}