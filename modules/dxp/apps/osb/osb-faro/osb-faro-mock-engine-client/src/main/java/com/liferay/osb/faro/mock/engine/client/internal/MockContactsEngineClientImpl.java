/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.mock.engine.client.internal;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.constants.FieldMappingConstants;
import com.liferay.osb.faro.engine.client.constants.FilterConstants;
import com.liferay.osb.faro.engine.client.model.Field;
import com.liferay.osb.faro.engine.client.model.Individual;
import com.liferay.osb.faro.engine.client.model.ProjectUsageMetric;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.util.FilterBuilder;
import com.liferay.osb.faro.engine.client.util.FilterUtil;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.portal.kernel.util.ListUtil;

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
	public Results<Individual> getCoworkerIndividuals(
		FaroProject faroProject, String individualId, String query,
		List<String> fields, int cur, int delta,
		List<OrderByField> orderByFields) {

		// PULPO-304

		Individual individual = getIndividual(faroProject, individualId, null);

		Map<String, List<Field>> demographics = individual.getDemographics();

		List<Field> worksFor = demographics.get("worksFor");

		if (ListUtil.isNull(worksFor)) {
			return new Results<>();
		}

		Field field = worksFor.get(0);

		Results<Individual> results = getIndividuals(
			faroProject, field.getName(), field.getValue(), query, fields, cur,
			delta, orderByFields);

		List<Individual> individuals = ListUtil.filter(
			results.getItems(),
			curIndividual -> {
				String id = curIndividual.getId();

				return !id.equals(individual.getId());
			});

		return new Results<>(individuals, individuals.size());
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

			if ((fieldName != null) && !fieldNames.contains(fieldName)) {
				fieldNames.add(0, fieldName);
			}
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
	public Results<ProjectUsageMetric> getProjectUsageMetrics(
		FaroProject faroProject, Date sinceDate) {

		return contactsEngineClient.getProjectUsageMetrics(
			faroProject, sinceDate);
	}

	@Override
	public Results<Individual> getSimilarIndividuals(
		FaroProject faroProject, String individualId, String query,
		List<String> fields, int cur, int delta,
		List<OrderByField> orderByFields) {

		// PULPO-305

		Individual individual = getIndividual(faroProject, individualId, null);

		Map<String, List<Field>> demographics = individual.getDemographics();

		List<Field> jobTitle = demographics.get("jobTitle");

		if (ListUtil.isNull(jobTitle)) {
			return new Results<>();
		}

		Field field = jobTitle.get(0);

		Results<Individual> results = getIndividuals(
			faroProject, field.getName(), field.getValue(), query, fields, cur,
			delta, orderByFields);

		List<Individual> individuals = ListUtil.filter(
			results.getItems(),
			curIndividual -> {
				String id = curIndividual.getId();

				return !id.equals(individual.getId());
			});

		return new Results<>(individuals, individuals.size());
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

	protected Results<Individual> getIndividuals(
		FaroProject faroProject, String fieldName, String fieldValue,
		String query, List<String> fields, int cur, int delta,
		List<OrderByField> orderByFields) {

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(
			FilterUtil.getFieldName(
				fieldName, FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL),
			FilterConstants.COMPARISON_OPERATOR_EQUALS, fieldValue);
		filterBuilder.addSearchFilter(
			query, fields, FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL);

		return getIndividuals(
			faroProject, filterBuilder, false, cur, delta, orderByFields);
	}

	private static final Map<String, String> _liferayFieldNames =
		FieldMappingConstants.getLiferayFieldNames();

}