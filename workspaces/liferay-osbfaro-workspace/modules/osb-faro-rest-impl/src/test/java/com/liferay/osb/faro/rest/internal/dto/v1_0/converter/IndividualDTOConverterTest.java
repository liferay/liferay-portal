/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.engine.client.model.Field;
import com.liferay.osb.faro.rest.dto.v1_0.Individual;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Collections;
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
	public void testToDTO() {
		com.liferay.osb.faro.engine.client.model.Individual
			engineClientIndividual = _createEngineClientIndividual(null);

		Individual individual = _toDTO(engineClientIndividual);

		Assert.assertEquals(
			engineClientIndividual.getActivityStatus(),
			individual.getActivityStatus());
		Assert.assertEquals(
			engineClientIndividual.getAverageSessionDuration(),
			individual.getAverageSessionDuration());
		Assert.assertEquals(
			engineClientIndividual.getKnownSinceDate(),
			individual.getKnownSinceDate());
		Assert.assertEquals(
			Individual.ProfileType.KNOWN, individual.getProfileType());
		Assert.assertEquals(
			engineClientIndividual.getSessionsCount(),
			individual.getSessionsCount());

		Assert.assertNull(individual.getDemographics());
		Assert.assertNull(individual.getEmailAddress());
		Assert.assertNull(individual.getName());

		String emailAddress = RandomTestUtil.randomString();
		String familyName = RandomTestUtil.randomString();
		String givenName = RandomTestUtil.randomString();

		individual = _toDTO(
			_createEngineClientIndividual(
				HashMapBuilder.<String, List<Field>>put(
					"email",
					Collections.singletonList(
						_createField("email", emailAddress))
				).put(
					"familyName",
					Collections.singletonList(
						_createField("familyName", familyName))
				).put(
					"givenName",
					Collections.singletonList(
						_createField("givenName", givenName))
				).build()));

		Map<?, ?> demographics = (Map<?, ?>)individual.getDemographics();

		Assert.assertEquals(demographics.toString(), 3, demographics.size());

		Assert.assertEquals(emailAddress, individual.getEmailAddress());
		Assert.assertEquals(
			givenName + StringPool.SPACE + familyName, individual.getName());

		individual = _toDTO(
			_createEngineClientIndividual(
				HashMapBuilder.<String, List<Field>>put(
					"familyName",
					Collections.singletonList(
						_createField("familyName", familyName))
				).build()));

		Assert.assertEquals(familyName, individual.getName());
	}

	private com.liferay.osb.faro.engine.client.model.Individual
		_createEngineClientIndividual(Map<String, List<Field>> demographics) {

		com.liferay.osb.faro.engine.client.model.Individual
			engineClientIndividual =
				new com.liferay.osb.faro.engine.client.model.Individual();

		engineClientIndividual.setActivityStatus(RandomTestUtil.randomString());
		engineClientIndividual.setAverageSessionDuration(
			RandomTestUtil.randomLong());
		engineClientIndividual.setDemographics(demographics);
		engineClientIndividual.setId(RandomTestUtil.randomString());
		engineClientIndividual.setKnownSinceDate(RandomTestUtil.nextDate());
		engineClientIndividual.setProfileType("known");
		engineClientIndividual.setSessionsCount(RandomTestUtil.randomLong());

		return engineClientIndividual;
	}

	private Field _createField(String name, String value) {
		Field field = new Field();

		field.setName(name);
		field.setValue(value);

		return field;
	}

	private Individual _toDTO(
		com.liferay.osb.faro.engine.client.model.Individual
			engineClientIndividual) {

		return _individualDTOConverter.toDTO(
			new FaroDTOConverterContext(
				false, engineClientIndividual.getId(), null),
			engineClientIndividual);
	}

	private final IndividualDTOConverter _individualDTOConverter =
		new IndividualDTOConverter();

}