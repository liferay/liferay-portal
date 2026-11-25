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
import com.liferay.portal.kernel.util.StringUtil;

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
	public void testToDTO() {
		_testToDTOWithDemographics();
		_testToDTOWithoutDemographics();
	}

	private Field _createField(String name, String value) {
		Field field = new Field();

		field.setName(name);
		field.setValue(value);

		return field;
	}

	private void _testToDTOWithDemographics() {
		String emailAddress = RandomTestUtil.randomString();
		String familyName = RandomTestUtil.randomString();
		String givenName = RandomTestUtil.randomString();

		Individual individual = _toDTO(
			HashMapBuilder.<String, List<Field>>put(
				"email",
				Collections.singletonList(_createField("email", emailAddress))
			).put(
				"familyName",
				Collections.singletonList(
					_createField("familyName", familyName))
			).put(
				"givenName",
				Collections.singletonList(_createField("givenName", givenName))
			).build());

		Map<?, ?> demographics = (Map<?, ?>)individual.getDemographics();

		Assert.assertEquals(demographics.toString(), 3, demographics.size());

		Assert.assertEquals(emailAddress, individual.getEmailAddress());
		Assert.assertEquals(
			givenName + StringPool.SPACE + familyName, individual.getName());

		individual = _toDTO(
			HashMapBuilder.<String, List<Field>>put(
				"familyName",
				Collections.singletonList(
					_createField("familyName", familyName))
			).build());

		Assert.assertEquals(familyName, individual.getName());
	}

	private void _testToDTOWithoutDemographics() {
		String activityStatus = RandomTestUtil.randomString();
		Long averageSessionDuration = RandomTestUtil.randomLong();
		Date knownSinceDate = RandomTestUtil.nextDate();
		Individual.ProfileType profileType = Individual.ProfileType.KNOWN;
		Long sessionsCount = RandomTestUtil.randomLong();

		Individual individual = _toDTO(
			activityStatus, averageSessionDuration, null, knownSinceDate,
			StringUtil.toLowerCase(profileType.getValue()), sessionsCount);

		Assert.assertEquals(activityStatus, individual.getActivityStatus());
		Assert.assertEquals(
			averageSessionDuration, individual.getAverageSessionDuration());
		Assert.assertNull(individual.getDemographics());
		Assert.assertNull(individual.getEmailAddress());
		Assert.assertEquals(knownSinceDate, individual.getKnownSinceDate());
		Assert.assertNull(individual.getName());
		Assert.assertEquals(profileType, individual.getProfileType());
		Assert.assertEquals(sessionsCount, individual.getSessionsCount());
	}

	private Individual _toDTO(Map<String, List<Field>> demographics) {
		return _toDTO(
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
			demographics, RandomTestUtil.nextDate(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong());
	}

	private Individual _toDTO(
		String activityStatus, Long averageSessionDuration,
		Map<String, List<Field>> demographics, Date knownSinceDate,
		String profileType, Long sessionsCount) {

		com.liferay.osb.faro.engine.client.model.Individual individual =
			new com.liferay.osb.faro.engine.client.model.Individual();

		individual.setActivityStatus(activityStatus);
		individual.setAverageSessionDuration(averageSessionDuration);
		individual.setDemographics(demographics);
		individual.setId(RandomTestUtil.randomString());
		individual.setKnownSinceDate(knownSinceDate);
		individual.setProfileType(profileType);
		individual.setSessionsCount(sessionsCount);

		return _individualDTOConverter.toDTO(
			new FaroDTOConverterContext(false, individual.getId(), null),
			individual);
	}

	private final IndividualDTOConverter _individualDTOConverter =
		new IndividualDTOConverter();

}