/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.model.listener.test;

import com.liferay.headless.builder.application.APIApplication;
import com.liferay.headless.builder.test.BaseTestCase;
import com.liferay.headless.builder.test.util.APIApplicationTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Sergio Jiménez del Coso
 */
@FeatureFlag("LPS-178642")
public class APIApplicationPublisherObjectEntryModelListenerTest
	extends BaseTestCase {

	@Test
	public void testPublishAPIApplicationOnPatch() throws Exception {
		String baseURL = StringUtil.toLowerCase(RandomTestUtil.randomString());
		String externalReferenceCode = RandomTestUtil.randomString();

		assertSuccessfulJSONObject(
			_getAPIApplicationJSONString(
				"unpublished", baseURL, externalReferenceCode),
			"headless-builder/applications", Http.Method.POST);

		APIApplicationTestUtil.assertNotDeployedAPIApplication(baseURL);

		assertSuccessfulJSONObject(
			JSONUtil.put(
				"applicationStatus", "published"
			).toString(),
			"headless-builder/applications/by-external-reference-code/" +
				externalReferenceCode,
			Http.Method.PATCH);

		APIApplicationTestUtil.assertDeployedAPIApplication(baseURL);
	}

	@Test
	public void testPublishAPIApplicationOnPost() throws Exception {
		String baseURL = StringUtil.toLowerCase(RandomTestUtil.randomString());

		assertSuccessfulJSONObject(
			_getAPIApplicationJSONString(
				"published", baseURL, RandomTestUtil.randomString()),
			"headless-builder/applications", Http.Method.POST);

		APIApplicationTestUtil.assertDeployedAPIApplication(baseURL);
	}

	@Test
	public void testPublishAPIApplicationOnPut() throws Exception {
		String baseURL1 = StringUtil.toLowerCase(RandomTestUtil.randomString());
		String externalReferenceCode = RandomTestUtil.randomString();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			_getAPIApplicationJSONString(
				"published", baseURL1, externalReferenceCode),
			"headless-builder/applications", Http.Method.POST);

		APIApplicationTestUtil.assertDeployedAPIApplication(baseURL1);

		String baseURL2 = StringUtil.toLowerCase(RandomTestUtil.randomString());

		assertSuccessfulJSONObject(
			JSONUtil.put(
				"applicationStatus", "published"
			).put(
				"baseURL", baseURL2
			).put(
				"title", "test"
			).toString(),
			"headless-builder/applications/" + jsonObject.getLong("id"),
			Http.Method.PUT);

		APIApplicationTestUtil.assertNotDeployedAPIApplication(baseURL1);
		APIApplicationTestUtil.assertDeployedAPIApplication(baseURL2);
	}

	@Test
	public void testPublishAPIApplicationOnPutByExternalReferenceCode()
		throws Exception {

		String baseURL1 = StringUtil.toLowerCase(RandomTestUtil.randomString());
		String externalReferenceCode = RandomTestUtil.randomString();

		HTTPTestUtil.invokeToJSONObject(
			_getAPIApplicationJSONString(
				"published", baseURL1, externalReferenceCode),
			"headless-builder/applications", Http.Method.POST);

		String baseURL2 = StringUtil.toLowerCase(RandomTestUtil.randomString());

		assertSuccessfulJSONObject(
			JSONUtil.put(
				"applicationStatus", "published"
			).put(
				"baseURL", baseURL2
			).put(
				"title", "title"
			).toString(),
			"headless-builder/applications/by-external-reference-code/" +
				externalReferenceCode,
			Http.Method.PUT);

		APIApplicationTestUtil.assertNotDeployedAPIApplication(baseURL1);
		APIApplicationTestUtil.assertDeployedAPIApplication(baseURL2);
	}

	@Test
	public void testUnpublishAPIApplicationOnDelete() throws Exception {
		String baseURL = StringUtil.toLowerCase(RandomTestUtil.randomString());
		String externalReferenceCode = RandomTestUtil.randomString();

		assertSuccessfulJSONObject(
			_getAPIApplicationJSONString(
				"published", baseURL, externalReferenceCode),
			"headless-builder/applications", Http.Method.POST);

		APIApplicationTestUtil.assertDeployedAPIApplication(baseURL);

		String apiEndpointExternalReferenceCode = RandomTestUtil.randomString();

		assertSuccessfulJSONObject(
			JSONUtil.put(
				"externalReferenceCode", apiEndpointExternalReferenceCode
			).put(
				"httpMethod", "get"
			).put(
				"name", "name"
			).put(
				"path",
				StringPool.FORWARD_SLASH +
					StringUtil.toLowerCase(RandomTestUtil.randomString())
			).put(
				"r_apiApplicationToAPIEndpoints_l_apiApplicationERC",
				externalReferenceCode
			).put(
				"retrieveType",
				APIApplication.Endpoint.RetrieveType.COLLECTION.getValue()
			).put(
				"scope", APIApplication.Endpoint.Scope.COMPANY.getValue()
			).toString(),
			"headless-builder/endpoints", Http.Method.POST);

		String apiSchemaExternalReferenceCode = RandomTestUtil.randomString();

		assertSuccessfulJSONObject(
			JSONUtil.put(
				"externalReferenceCode", apiSchemaExternalReferenceCode
			).put(
				"mainObjectDefinitionERC", "L_API_APPLICATION"
			).put(
				"name", "name"
			).put(
				"r_apiApplicationToAPISchemas_l_apiApplicationERC",
				externalReferenceCode
			).toString(),
			"headless-builder/schemas", Http.Method.POST);

		assertSuccessfulJSONObject(
			null,
			"headless-builder/applications/by-external-reference-code/" +
				externalReferenceCode,
			Http.Method.DELETE);

		APIApplicationTestUtil.assertNotDeployedAPIApplication(baseURL);

		Assert.assertEquals(
			404,
			HTTPTestUtil.invokeToHttpCode(
				null,
				"headless-builder/endpoints/by-external-reference-code/" +
					apiEndpointExternalReferenceCode,
				Http.Method.GET));
		Assert.assertEquals(
			404,
			HTTPTestUtil.invokeToHttpCode(
				null,
				"headless-builder/schemas/by-external-reference-code/" +
					apiSchemaExternalReferenceCode,
				Http.Method.GET));
	}

	@Test
	public void testUnpublishAPIApplicationOnPatch() throws Exception {
		String baseURL = StringUtil.toLowerCase(RandomTestUtil.randomString());
		String externalReferenceCode = RandomTestUtil.randomString();

		assertSuccessfulJSONObject(
			_getAPIApplicationJSONString(
				"published", baseURL, externalReferenceCode),
			"headless-builder/applications", Http.Method.POST);

		APIApplicationTestUtil.assertDeployedAPIApplication(baseURL);

		assertSuccessfulJSONObject(
			JSONUtil.put(
				"applicationStatus", "unpublished"
			).toString(),
			"headless-builder/applications/by-external-reference-code/" +
				externalReferenceCode,
			Http.Method.PATCH);

		APIApplicationTestUtil.assertNotDeployedAPIApplication(baseURL);

		Assert.assertFalse(
			HTTPTestUtil.invokeToJSONObject(
				null, "openapi", Http.Method.GET
			).has(
				"/c/" + baseURL
			));
	}

	private String _getAPIApplicationJSONString(
		String applicationStatus, String baseURL,
		String externalReferenceCode) {

		return JSONUtil.put(
			"applicationStatus", applicationStatus
		).put(
			"baseURL", baseURL
		).put(
			"externalReferenceCode", externalReferenceCode
		).put(
			"title", RandomTestUtil.randomString()
		).toString();
	}

}