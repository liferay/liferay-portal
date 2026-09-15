/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.engine.client.model.Field;
import com.liferay.osb.faro.rest.dto.v1_0.Individual;
import com.liferay.osb.faro.rest.dto.v1_0.IndividualDemographicField;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leslie Wong
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.engine.client.model.Individual",
	service = DTOConverter.class
)
public class IndividualDTOConverter
	implements DTOConverter
		<com.liferay.osb.faro.engine.client.model.Individual, Individual> {

	@Override
	public String getContentType() {
		return Individual.class.getSimpleName();
	}

	@Override
	public Individual toDTO(
		DTOConverterContext dtoConverterContext,
		com.liferay.osb.faro.engine.client.model.Individual individual) {

		if (individual == null) {
			return null;
		}

		return new Individual() {
			{
				setAccountName(individual::getAccountName);
				setActivitiesCount(individual::getActivitiesCount);
				setActivityStatus(individual::getActivityStatus);
				setAverageSessionDuration(
					individual::getAverageSessionDuration);
				setDateCreated(individual::getDateCreated);
				setDateModified(individual::getDateModified);
				setDemographics(
					() -> _toIndividualDemographicFieldsMap(
						individual.getDemographics(), dtoConverterContext));
				setEmailAddress(
					() -> _getDemographicValue(individual, "email"));
				setFirstActivityDate(individual::getFirstActivityDate);
				setId(individual::getId);
				setKnownSinceDate(individual::getKnownSinceDate);
				setLastActivityDate(individual::getLastActivityDate);
				setLastSessionCountry(individual::getLastSessionCountry);
				setName(() -> _getName(individual));
				setProfileType(
					() -> ProfileType.create(
						StringUtil.toUpperCase(individual.getProfileType())));
				setSessionsCount(individual::getSessionsCount);
			}
		};
	}

	private String _getDemographicValue(
		com.liferay.osb.faro.engine.client.model.Individual individual,
		String name) {

		Map<String, List<Field>> demographics = individual.getDemographics();

		if (demographics == null) {
			return null;
		}

		List<Field> fields = demographics.get(name);

		if (ListUtil.isEmpty(fields)) {
			return null;
		}

		Field field = fields.get(0);

		return field.getValue();
	}

	private String _getName(
		com.liferay.osb.faro.engine.client.model.Individual individual) {

		String name = StringUtil.trim(
			StringBundler.concat(
				GetterUtil.getString(
					_getDemographicValue(individual, "givenName")),
				StringPool.SPACE,
				GetterUtil.getString(
					_getDemographicValue(individual, "familyName"))));

		if (Validator.isBlank(name)) {
			return null;
		}

		return name;
	}

	private Map<String, List<IndividualDemographicField>>
			_toIndividualDemographicFieldsMap(
				Map<String, List<Field>> demographicFields,
				DTOConverterContext dtoConverterContext)
		throws Exception {

		if ((demographicFields == null) || demographicFields.isEmpty()) {
			return null;
		}

		Map<String, List<IndividualDemographicField>>
			individualDemographicFieldsMap = new HashMap<>();

		for (Map.Entry<String, List<Field>> entry :
				demographicFields.entrySet()) {

			List<Field> fields = entry.getValue();

			if (fields == null) {
				continue;
			}

			List<IndividualDemographicField> individualDemographicFields =
				new ArrayList<>(fields.size());

			for (Field field : fields) {
				individualDemographicFields.add(
					_individualDemographicFieldDTOConverter.toDTO(
						dtoConverterContext, field));
			}

			individualDemographicFieldsMap.put(
				entry.getKey(), individualDemographicFields);
		}

		return individualDemographicFieldsMap;
	}

	@Reference(
		target = "(component.name=com.liferay.osb.faro.rest.internal.dto.v1_0.converter.IndividualDemographicFieldDTOConverter)"
	)
	private DTOConverter<Field, IndividualDemographicField>
		_individualDemographicFieldDTOConverter;

}