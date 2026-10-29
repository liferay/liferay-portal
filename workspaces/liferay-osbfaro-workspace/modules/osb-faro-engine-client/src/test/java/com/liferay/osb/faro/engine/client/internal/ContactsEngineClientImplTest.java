/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.internal;

import com.fasterxml.jackson.core.type.TypeReference;

import com.liferay.osb.faro.engine.client.constants.FieldMappingConstants;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.http.HttpMethod;

/**
 * @author Eudaldo Alonso
 */
public class ContactsEngineClientImplTest {

	@Test
	public void testGetFieldNamesList() {
		Map<String, String> liferayFieldNames =
			FieldMappingConstants.getLiferayFieldNames();

		List<String> labels = new ArrayList<>(liferayFieldNames.keySet());

		String fieldName = RandomTestUtil.randomString();

		_fieldNamesList = Arrays.asList(
			new ArrayList<>(Collections.singletonList(fieldName)),
			new ArrayList<>(
				Arrays.asList(fieldName, liferayFieldNames.get(labels.get(1)))),
			new ArrayList<>(Collections.singletonList(fieldName)));

		Assert.assertEquals(
			Arrays.asList(
				Arrays.asList(liferayFieldNames.get(labels.get(0)), fieldName),
				Arrays.asList(fieldName, liferayFieldNames.get(labels.get(1))),
				Collections.singletonList(fieldName)),
			_contactsEngineClientImpl.getFieldNamesList(
				Mockito.mock(FaroProject.class),
				Arrays.asList(
					labels.get(0), labels.get(1),
					RandomTestUtil.randomString()),
				FieldMappingConstants.OWNER_TYPE_INDIVIDUAL,
				Arrays.asList(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString())));
	}

	private final ContactsEngineClientImpl _contactsEngineClientImpl =
		new ContactsEngineClientImpl() {

			@Override
			protected <T> List<T> bulk(
				FaroProject faroProject, String type, HttpMethod httpMethod,
				TypeReference<T> typeReference,
				List<Map<String, Object>> uriVariablesList) {

				return (List<T>)_fieldNamesList;
			}

		};

	private List<List<String>> _fieldNamesList;

}