/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.internal;

import com.fasterxml.jackson.core.type.TypeReference;

import com.liferay.osb.faro.engine.client.constants.FieldMappingConstants;
import com.liferay.osb.faro.engine.client.model.EntityModelPagedModel;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

/**
 * @author Eudaldo Alonso
 */
public class ContactsEngineClientImplTest {

	@Test
	public void testGetAccountMetrics() {
		String campaignId = RandomTestUtil.randomString();
		long channelId = RandomTestUtil.randomLong();

		_contactsEngineClientImpl.getAccountMetrics(
			_faroProject, campaignId, channelId);

		Assert.assertEquals(campaignId, _uriVariables.get("campaignId"));

		_contactsEngineClientImpl.getAccountMetrics(
			_faroProject, null, channelId);

		Assert.assertFalse(_uriVariables.containsKey("campaignId"));
	}

	@Test
	public void testGetAccounts() {
		String campaignId = RandomTestUtil.randomString();
		String channelId = RandomTestUtil.randomString();
		int cur = RandomTestUtil.randomInt();
		int delta = RandomTestUtil.randomInt();

		_contactsEngineClientImpl.getAccounts(
			_faroProject, null, campaignId, channelId, null, false, null, null,
			null, null, null, cur, delta, null);

		Assert.assertEquals(campaignId, _uriVariables.get("campaignId"));

		_contactsEngineClientImpl.getAccounts(
			_faroProject, null, null, channelId, null, false, null, null, null,
			null, null, cur, delta, null);

		Assert.assertFalse(_uriVariables.containsKey("campaignId"));
	}

	@Test
	public void testGetCampaignNames() {
		Long channelId = RandomTestUtil.randomLong();
		int cur = RandomTestUtil.randomInt();
		int delta = RandomTestUtil.randomInt();
		String query = RandomTestUtil.randomString();

		_contactsEngineClientImpl.getCampaignNames(
			_faroProject, channelId, query, cur, delta);

		Assert.assertEquals(channelId, _uriVariables.get("channelId"));
		Assert.assertEquals(query, _uriVariables.get("keywords"));
		Assert.assertEquals(cur - 1, _uriVariables.get("page"));
		Assert.assertEquals(delta, _uriVariables.get("size"));

		_contactsEngineClientImpl.getCampaignNames(
			_faroProject, channelId, null, cur, delta);

		Assert.assertFalse(_uriVariables.containsKey("keywords"));
	}

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

	@Test
	public void testGetIndividuals() {
		String campaignId = RandomTestUtil.randomString();
		String channelId = RandomTestUtil.randomString();
		int cur = RandomTestUtil.randomInt();
		int delta = RandomTestUtil.randomInt();

		_contactsEngineClientImpl.getIndividuals(
			_faroProject, null, null, null, campaignId, channelId, null, null,
			null, false, null, null, null, null, null, null, null, null, cur,
			delta, null);

		Assert.assertEquals(campaignId, _uriVariables.get("campaignId"));

		_contactsEngineClientImpl.getIndividuals(
			_faroProject, null, null, null, null, channelId, null, null, null,
			false, null, null, null, null, null, null, null, null, cur, delta,
			null);

		Assert.assertFalse(_uriVariables.containsKey("campaignId"));
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

			@Override
			protected <T> T get(
				FaroProject faroProject, String type,
				ParameterizedTypeReference<T> parameterizedTypeReference,
				Map<String, Object> uriVariables) {

				_uriVariables = uriVariables;

				Type responseType = parameterizedTypeReference.getType();

				String typeName = responseType.getTypeName();

				if (typeName.contains("PagedModel")) {
					return (T)Mockito.mock(EntityModelPagedModel.class);
				}

				return null;
			}

		};

	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private List<List<String>> _fieldNamesList;
	private Map<String, Object> _uriVariables;

}