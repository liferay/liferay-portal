/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.engine.client.model.Field;
import com.liferay.osb.faro.rest.dto.v1_0.Individual;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Leslie Wong
 */
public class IndividualDTOConverterTest {

	@Before
	public void setUp() throws Exception {
		java.lang.reflect.Field field =
			IndividualDTOConverter.class.getDeclaredField(
				"_individualDemographicFieldDTOConverter");

		field.setAccessible(true);

		field.set(
			_individualDTOConverter,
			new IndividualDemographicFieldDTOConverter());
	}

	@Test
	public void testToDTODerivesNameAndEmailAddressFromDemographics() {
		Individual individualDTO = _toDTO(
			_createIndividual(
				HashMapBuilder.<String, List<Field>>put(
					"email",
					Collections.singletonList(
						_createField("email", "jane.doe@acme.example"))
				).put(
					"familyName",
					Collections.singletonList(_createField("familyName", "Doe"))
				).put(
					"givenName",
					Collections.singletonList(_createField("givenName", "Jane"))
				).build()));

		Map<?, ?> demographics = (Map<?, ?>)individualDTO.getDemographics();

		Assert.assertEquals(demographics.toString(), 3, demographics.size());

		Assert.assertEquals(
			"jane.doe@acme.example", individualDTO.getEmailAddress());
		Assert.assertEquals("Jane Doe", individualDTO.getName());
	}

	@Test
	public void testToDTOLeavesNameAndEmailAddressNullWithoutDemographics() {
		Individual individualDTO = _toDTO(_createIndividual(null));

		Assert.assertNull(individualDTO.getDemographics());
		Assert.assertNull(individualDTO.getEmailAddress());
		Assert.assertNull(individualDTO.getName());
	}

	@Test
	public void testToDTOMapsEngagementFields() {
		Individual individualDTO = _toDTO(_createIndividual(null));

		Assert.assertEquals("inactive", individualDTO.getActivityStatus());
		Assert.assertEquals(
			Long.valueOf(90000), individualDTO.getAverageSessionDuration());
		Assert.assertEquals(_knownSinceDate, individualDTO.getKnownSinceDate());
		Assert.assertEquals(
			Individual.ProfileType.KNOWN, individualDTO.getProfileType());
		Assert.assertEquals(Long.valueOf(7), individualDTO.getSessionsCount());
	}

	@Test
	public void testToDTOUsesTheOnlyAvailableNamePart() {
		Individual individualDTO = _toDTO(
			_createIndividual(
				HashMapBuilder.<String, List<Field>>put(
					"familyName",
					Collections.singletonList(_createField("familyName", "Doe"))
				).build()));

		Assert.assertEquals("Doe", individualDTO.getName());
	}

	private Field _createField(String name, String value) {
		Field field = new Field();

		field.setName(name);
		field.setValue(value);

		return field;
	}

	private com.liferay.osb.faro.engine.client.model.Individual
		_createIndividual(Map<String, List<Field>> demographics) {

		com.liferay.osb.faro.engine.client.model.Individual individual =
			new com.liferay.osb.faro.engine.client.model.Individual();

		individual.setActivityStatus("inactive");
		individual.setAverageSessionDuration(90000L);
		individual.setDemographics(demographics);
		individual.setId("individual-1");
		individual.setKnownSinceDate(_knownSinceDate);
		individual.setProfileType("known");
		individual.setSessionsCount(7L);

		return individual;
	}

	private Individual _toDTO(
		com.liferay.osb.faro.engine.client.model.Individual individual) {

		return _individualDTOConverter.toDTO(
			new FaroDTOConverterContext(false, individual.getId(), null),
			individual);
	}

	private final IndividualDTOConverter _individualDTOConverter =
		new IndividualDTOConverter();
	private final Date _knownSinceDate = new Date(1720000000000L);

}