/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.metadata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.mule.internal.oas.constants.OASConstants;

import java.io.InputStream;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.MetadataKey;
import org.mule.runtime.api.metadata.MetadataKeyBuilder;
import org.mule.runtime.api.metadata.MetadataResolvingException;

/**
 * @author Matija Petanjek
 */
public class MetadataKeysBuilderTest {

	@Before
	public void setUp() throws Exception {
		metadataKeysBuilder = Mockito.spy(MetadataKeysBuilder.class);

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"com/liferay/mule/internal/metadata/openapi.json");

		ObjectMapper objectMapper = new ObjectMapper();

		openAPISpecJsonNode = objectMapper.readTree(inputStream);

		Mockito.doReturn(
			openAPISpecJsonNode
		).when(
			metadataKeysBuilder
		).getOASJsonNode(
			Mockito.any()
		);
	}

	@Test
	public void testBuildClassNameMetadataKeys()
		throws ConnectionException, MetadataResolvingException {

		Set<MetadataKey> metadataKeys =
			metadataKeysBuilder.buildClassNameMetadataKeys(null);

		Assert.assertEquals(metadataKeys.toString(), 2, metadataKeys.size());

		MetadataKey metadataKey = MetadataKeyBuilder.newKey(
			"com.liferay.headless.v1_0.Entity"
		).build();

		Assert.assertTrue(metadataKeys.contains(metadataKey));

		metadataKey = MetadataKeyBuilder.newKey(
			"com.liferay.headless.v1_0.NestedEntity"
		).build();

		Assert.assertTrue(metadataKeys.contains(metadataKey));
	}

	@Test
	public void testBuildDELETEEndpointMetadataKeys()
		throws ConnectionException, MetadataResolvingException {

		Set<String> metadataKeyIds = toMetadataKeyIdSet(
			metadataKeysBuilder.buildEndpointMetadataKeys(
				null, OASConstants.OPERATION_DELETE));

		Assert.assertTrue(
			metadataKeyIds.contains("/endpoint/with/delete/and/get/operation"));
	}

	@Test
	public void testBuildEmptyMetadataKeys()
		throws ConnectionException, MetadataResolvingException {

		Set<MetadataKey> metadataKeys =
			metadataKeysBuilder.buildEndpointMetadataKeys(null, OPERATION_HEAD);

		Assert.assertTrue(metadataKeys.isEmpty());
	}

	@Test
	public void testBuildGETEndpointMetadataKeys()
		throws ConnectionException, MetadataResolvingException {

		Set<String> metadataKeyIds = toMetadataKeyIdSet(
			metadataKeysBuilder.buildEndpointMetadataKeys(
				null, OASConstants.OPERATION_GET));

		Assert.assertTrue(
			metadataKeyIds.contains("/endpoint/with/delete/and/get/operation"));
		Assert.assertTrue(
			metadataKeyIds.contains("/endpoint/with/get/and/patch/operation"));
		Assert.assertTrue(
			metadataKeyIds.contains("/endpoint/with/get/and/post/operation"));
	}

	@Test
	public void testBuildPATCHEndpointMetadataKeys()
		throws ConnectionException, MetadataResolvingException {

		Set<String> metadataKeyIds = toMetadataKeyIdSet(
			metadataKeysBuilder.buildEndpointMetadataKeys(
				null, OASConstants.OPERATION_PATCH));

		Assert.assertTrue(
			metadataKeyIds.contains("/endpoint/with/get/and/patch/operation"));
	}

	@Test
	public void testBuildPOSTEndpointMetadataKeys()
		throws ConnectionException, MetadataResolvingException {

		Set<String> metadataKeyIds = toMetadataKeyIdSet(
			metadataKeysBuilder.buildEndpointMetadataKeys(
				null, OASConstants.OPERATION_POST));

		Assert.assertTrue(
			metadataKeyIds.contains("/endpoint/with/get/and/post/operation"));
	}

	private Set<String> toMetadataKeyIdSet(Set<MetadataKey> metadataKeys) {
		Set<String> metadataKeyIds = new HashSet<>();

		for (MetadataKey metadataKey : metadataKeys) {
			metadataKeyIds.add(metadataKey.getId());
		}

		return metadataKeyIds;
	}

	private static final String OPERATION_HEAD = "head";

	private MetadataKeysBuilder metadataKeysBuilder;
	private JsonNode openAPISpecJsonNode;

}