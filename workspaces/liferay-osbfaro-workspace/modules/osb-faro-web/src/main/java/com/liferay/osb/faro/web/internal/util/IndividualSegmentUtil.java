/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.util;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.constants.FieldMappingConstants;
import com.liferay.osb.faro.engine.client.constants.FilterConstants;
import com.liferay.osb.faro.engine.client.model.Field;
import com.liferay.osb.faro.engine.client.model.FieldMapping;
import com.liferay.osb.faro.engine.client.model.Individual;
import com.liferay.osb.faro.engine.client.model.IndividualSegment;
import com.liferay.osb.faro.engine.client.model.IndividualTransformation;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.util.FilterBuilder;
import com.liferay.osb.faro.engine.client.util.FilterUtil;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.web.internal.constants.FaroPaginationConstants;
import com.liferay.osb.faro.web.internal.exception.FaroException;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Matthew Kong
 */
public class IndividualSegmentUtil {

	public static double getDefaultBinSize(
		int numberOfBins, double minValue, double maxValue) {

		if ((numberOfBins > _MAX_BINS) || (numberOfBins <= 0)) {
			throw new FaroException("Invalid number of bins: " + numberOfBins);
		}

		return (long)Math.ceil((maxValue - minValue) / numberOfBins);
	}

	public static List<Map<String, Object>> getFieldDistribution(
			FaroProject faroProject, String id, String fieldMappingFieldName,
			double binSize, int numberOfBins, int cur, int delta,
			ContactsEngineClient contactsEngineClient)
		throws Exception {

		IndividualSegment individualSegment =
			contactsEngineClient.getIndividualSegment(faroProject, id, false);

		FieldMapping fieldMapping = contactsEngineClient.getFieldMapping(
			faroProject, fieldMappingFieldName);

		if (SchemaOrgUtil.isSubtype(
				fieldMapping.getFieldType(),
				FieldMappingConstants.TYPE_NUMBER)) {

			return getBinnedIndividualFieldDistribution(
				faroProject, individualSegment, fieldMapping, binSize,
				numberOfBins, contactsEngineClient);
		}

		return getIndividualFieldDistribution(
			faroProject, individualSegment, fieldMapping, cur, delta,
			contactsEngineClient);
	}

	public static Object getFieldValue(
		FaroProject faroProject, String faroEntityId, String fieldName,
		String orderByType, ContactsEngineClient contactsEngineClient) {

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addNullFilter(
			fieldName, FilterConstants.COMPARISON_OPERATOR_NOT_EQUALS,
			FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL);

		Results<Individual> results =
			contactsEngineClient.getIndividualsByIndividualSegment(
				faroProject, faroEntityId, null, null, filterBuilder, false, 1,
				1,
				Collections.singletonList(
					new OrderByField(fieldName, orderByType)));

		if (results.getTotal() == 0) {
			return null;
		}

		List<Individual> individuals = results.getItems();

		Individual individual = individuals.get(0);

		Map<String, List<Field>> demographics = individual.getDemographics();

		List<Field> fields = demographics.get(fieldName);

		Field field = fields.get(0);

		return field.getValue();
	}

	public static double getMinBinSize(double minValue, double maxValue) {
		return (long)Math.ceil((maxValue - minValue) / _MAX_BINS);
	}

	protected static List<Double[]> getBinRanges(
			double binSize, int numberOfBins, double minValue, double maxValue)
		throws Exception {

		if (binSize <= 0) {
			binSize = getDefaultBinSize(numberOfBins, minValue, maxValue);
		}
		else if (binSize < getMinBinSize(minValue, maxValue)) {
			throw new FaroException(
				"Exceeded the maximum number of bins: " + _MAX_BINS);
		}

		List<Double[]> binRanges = new ArrayList<>();

		double curMin = minValue;

		while (curMin < maxValue) {
			double curMax = Math.min(curMin + binSize, maxValue);

			binRanges.add(new Double[] {curMin, curMax});

			curMin = curMax;
		}

		return binRanges;
	}

	protected static List<Map<String, Object>>
			getBinnedIndividualFieldDistribution(
				FaroProject faroProject, IndividualSegment individualSegment,
				FieldMapping fieldMapping, double binSize, int numberOfBins,
				ContactsEngineClient contactsEngineClient)
		throws Exception {

		if (binSize == 0) {
			return getIndividualFieldDistribution(
				faroProject, individualSegment, fieldMapping, 1, numberOfBins,
				contactsEngineClient);
		}

		Object maxValueObject = getFieldValue(
			faroProject, individualSegment.getId(), fieldMapping.getFieldName(),
			FaroPaginationConstants.ORDER_BY_TYPE_DESC, contactsEngineClient);

		if (maxValueObject == null) {
			return Collections.emptyList();
		}

		double maxValue = GetterUtil.getDouble(maxValueObject);

		double minValue = GetterUtil.getDouble(
			getFieldValue(
				faroProject, individualSegment.getId(),
				fieldMapping.getFieldName(),
				FaroPaginationConstants.ORDER_BY_TYPE_ASC,
				contactsEngineClient));

		if (maxValue == minValue) {
			return getIndividualFieldDistribution(
				faroProject, individualSegment, fieldMapping, 1, 1,
				contactsEngineClient);
		}

		List<Map<String, Object>> individualFieldDistribution =
			new ArrayList<>();

		String fieldName = FilterUtil.getFieldName(
			fieldMapping.getFieldName(),
			FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL);

		List<Double[]> binRanges = getBinRanges(
			binSize, numberOfBins, minValue, maxValue);

		for (int i = 0; i < binRanges.size(); i++) {
			Double[] binRange = binRanges.get(i);

			FilterBuilder curFilterBuilder = new FilterBuilder();

			curFilterBuilder.addFilter(
				fieldName,
				FilterConstants.COMPARISON_OPERATOR_GREATER_THAN_OR_EQUAL,
				binRange[0]);

			if (i == (binRanges.size() - 1)) {
				curFilterBuilder.addFilter(
					fieldName,
					FilterConstants.COMPARISON_OPERATOR_LESS_THAN_OR_EQUAL,
					binRange[1]);
			}
			else {
				curFilterBuilder.addFilter(
					fieldName, FilterConstants.COMPARISON_OPERATOR_LESS_THAN,
					binRange[1]);
			}

			individualFieldDistribution.add(
				HashMapBuilder.<String, Object>put(
					"count",
					() -> {
						Results<Individual> curResults =
							contactsEngineClient.
								getIndividualsByIndividualSegment(
									faroProject, individualSegment.getId(),
									null, null, curFilterBuilder,
									individualSegment.isIncludeAnonymousUsers(),
									1, 1, null);

						return curResults.getTotal();
					}
				).put(
					"values", binRange
				).build());
		}

		return individualFieldDistribution;
	}

	protected static List<Map<String, Object>> getIndividualFieldDistribution(
		FaroProject faroProject, IndividualSegment individualSegment,
		FieldMapping fieldMapping, int cur, int delta,
		ContactsEngineClient contactsEngineClient) {

		List<Map<String, Object>> individualFieldDistribution =
			new ArrayList<>();

		if (delta == QueryUtil.ALL_POS) {
			delta = _MAX_BINS;
		}

		Results<IndividualTransformation> individualTransformations =
			contactsEngineClient.getIndividualTransformations(
				faroProject, individualSegment.getId(), null, null,
				fieldMapping.getFieldName(), cur, delta, null);

		for (IndividualTransformation individualTransformation :
				individualTransformations.getItems()) {

			Map<String, Object> terms = individualTransformation.getTerms();

			individualFieldDistribution.add(
				HashMapBuilder.<String, Object>put(
					"count", individualTransformation.getTotalElements()
				).put(
					"values",
					() -> {
						List<Object> values = new ArrayList<>(terms.values());

						return new Object[] {values.get(0)};
					}
				).build());
		}

		return individualFieldDistribution;
	}

	private static final int _MAX_BINS = 100;

}