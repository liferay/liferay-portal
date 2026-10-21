/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.batch.engine.client.dto.v1_0.ExportTask;
import com.liferay.headless.batch.engine.client.resource.v1_0.ExportTaskResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.tools.rest.builder.test.client.http.HttpInvoker;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.FeatureFlagMethodTestEntityResource;

import jakarta.ws.rs.NotFoundException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class FeatureFlagMethodTestEntityResourceTest
	extends BaseFeatureFlagMethodTestEntityResourceTestCase {

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		for (String featureFlagKey :
				new String[] {
					_FEATURE_FLAG_KEY,
					_FEATURE_FLAG_KEY_GET_BY_EXTERNAL_REFERENCE_CODE,
					_FEATURE_FLAG_KEY_PUT_BY_EXTERNAL_REFERENCE_CODE,
					_FEATURE_FLAG_KEY_SITE
				}) {

			FeatureFlagTestUtil.invokeFeatureFlagListeners(
				TestPropsValues.getCompanyId(), false, featureFlagKey);
		}
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY)
	@Override
	@Test
	public void testDeleteFeatureFlagMethodTestEntity() throws Exception {
		_testDeleteFeatureFlagMethodTestEntityWhenFeatureFlagIsDisabled();
		_testDeleteFeatureFlagMethodTestEntityWhenFeatureFlagIsDisabledUsingFactory();
		_testDeleteFeatureFlagMethodTestEntityWhenFeatureFlagIsEnabled();
		_testDeleteFeatureFlagMethodTestEntityWhenFeatureFlagIsEnabledUsingFactory();
	}

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY)
	@Override
	@Test
	public void testDeleteFeatureFlagMethodTestEntityBatch() throws Exception {
		_testDeleteFeatureFlagMethodTestEntityBatchWhenFeatureFlagIsDisabled();
		_testDeleteFeatureFlagMethodTestEntityBatchWhenFeatureFlagIsEnabled();
	}

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY_SITE)
	@Override
	@Test
	public void testDeleteSiteFeatureFlagMethodTestEntityByExternalReferenceCode()
		throws Exception {

		_testDeleteSiteFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsDisabled();
		_testDeleteSiteFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsEnabled();
	}

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY)
	@Override
	@Test
	public void testGetFeatureFlagMethodTestEntitiesPage() throws Exception {
		_testGetFeatureFlagMethodTestEntitiesPageWhenFeatureFlagIsDisabled();
		_testGetFeatureFlagMethodTestEntitiesPageWhenFeatureFlagIsEnabled();
		_testGetFeatureFlagMethodTestEntitiesPageWhenOperationIdIsExcluded();
	}

	@FeatureFlag(
		enable = false, value = _FEATURE_FLAG_KEY_GET_BY_EXTERNAL_REFERENCE_CODE
	)
	@Override
	@Test
	public void testGetFeatureFlagMethodTestEntityByExternalReferenceCode()
		throws Exception {

		_testGetFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsDisabled();
		_testGetFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsEnabled();
	}

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY_SITE)
	@Override
	@Test
	public void testGetSiteFeatureFlagMethodTestEntitiesPage()
		throws Exception {

		_testGetSiteFeatureFlagMethodTestEntitiesPageWhenFeatureFlagIsDisabled();
		_testGetSiteFeatureFlagMethodTestEntitiesPageWhenFeatureFlagIsEnabled();
	}

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY)
	@Override
	@Test
	public void testGraphQLDeleteFeatureFlagMethodTestEntity()
		throws Exception {

		_testGraphQLDeleteFeatureFlagMethodTestEntityWhenFeatureFlagIsDisabled();
		_testGraphQLDeleteFeatureFlagMethodTestEntityWhenFeatureFlagIsEnabled();
	}

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY_SITE)
	@Override
	@Test
	public void testGraphQLDeleteSiteFeatureFlagMethodTestEntityByExternalReferenceCode()
		throws Exception {

		_testGraphQLDeleteSiteFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsDisabled();
		_testGraphQLDeleteSiteFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsEnabled();
	}

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY)
	@Override
	@Test
	public void testGraphQLGetFeatureFlagMethodTestEntitiesPage()
		throws Exception {

		_testGraphQLGetFeatureFlagMethodTestEntitiesPageWhenFeatureFlagIsDisabled();
		_testGraphQLGetFeatureFlagMethodTestEntitiesPageWhenFeatureFlagIsEnabled();
	}

	@FeatureFlag(
		enable = false, value = _FEATURE_FLAG_KEY_GET_BY_EXTERNAL_REFERENCE_CODE
	)
	@Override
	@Test
	public void testGraphQLGetFeatureFlagMethodTestEntityByExternalReferenceCode()
		throws Exception {

		_testGraphQLGetFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsDisabled();
		_testGraphQLGetFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsEnabled();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetFeatureFlagMethodTestEntityByExternalReferenceCodeNotFound()
		throws Exception {

		super.
			testGraphQLGetFeatureFlagMethodTestEntityByExternalReferenceCodeNotFound();
	}

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY_SITE)
	@Override
	@Test
	public void testGraphQLGetSiteFeatureFlagMethodTestEntitiesPage()
		throws Exception {

		_testGraphQLGetSiteFeatureFlagMethodTestEntitiesPageWhenFeatureFlagIsDisabled();
		_testGraphQLGetSiteFeatureFlagMethodTestEntitiesPageWhenFeatureFlagIsEnabled();
	}

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY)
	@Override
	@Test
	public void testGraphQLPostFeatureFlagMethodTestEntity() throws Exception {
		_testGraphQLPostFeatureFlagMethodTestEntityWhenFeatureFlagIsDisabled();
	}

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY_SITE)
	@Override
	@Test
	public void testGraphQLPostSiteFeatureFlagMethodTestEntity()
		throws Exception {

		_testGraphQLPostSiteFeatureFlagMethodTestEntityWhenFeatureFlagIsDisabled();
		_testGraphQLPostSiteFeatureFlagMethodTestEntityWhenFeatureFlagIsEnabled();
	}

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY)
	@Override
	@Test
	public void testPatchFeatureFlagMethodTestEntity() throws Exception {
		_testPatchFeatureFlagMethodTestEntityWhenFeatureFlagIsDisabled();
		_testPatchFeatureFlagMethodTestEntityWhenFeatureFlagIsEnabled();
	}

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY)
	@Override
	@Test
	public void testPostFeatureFlagMethodTestEntity() throws Exception {
		_testPostFeatureFlagMethodTestEntityWhenFeatureFlagIsDisabled();
	}

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY_SITE)
	@Override
	@Test
	public void testPostSiteFeatureFlagMethodTestEntity() throws Exception {
		_testPostSiteFeatureFlagMethodTestEntityWhenFeatureFlagIsDisabled();
		_testPostSiteFeatureFlagMethodTestEntityWhenFeatureFlagIsEnabled();
	}

	@FeatureFlag(
		enable = false, value = _FEATURE_FLAG_KEY_PUT_BY_EXTERNAL_REFERENCE_CODE
	)
	@Override
	@Test
	public void testPutFeatureFlagMethodTestEntityByExternalReferenceCode()
		throws Exception {

		_testPutFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsDisabled();
		_testPutFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsEnabled();
	}

	private void _assertFieldUndefined(
			String fieldName, JSONObject jsonObject, String typeName)
		throws Exception {

		JSONAssert.assertEquals(
			JSONUtil.put(
				"errors",
				JSONUtil.putAll(
					JSONUtil.put(
						"extensions",
						JSONUtil.put("exception", JSONUtil.put("errno", 400))
					).put(
						"message",
						StringBundler.concat(
							"Validation error (FieldUndefined@[", fieldName,
							"]) : Field '", fieldName, "' in type '", typeName,
							"' is undefined")
					))
			).toString(),
			jsonObject.toString(), JSONCompareMode.LENIENT);
	}

	private FeatureFlagMethodTestEntityResource
			_buildFeatureFlagMethodTestEntityResource()
		throws Exception {

		return _featureFlagMethodTestEntityResourceFactory.create(
		).user(
			TestPropsValues.getUser()
		).build();
	}

	private boolean _hasOperation(String httpMethod, String path)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, "portal-tools-rest-builder-test/v1.0/openapi.json",
			Http.Method.GET);

		JSONObject pathsJSONObject = jsonObject.getJSONObject("paths");

		String versionedPath = "/v1.0/" + path;

		if (!pathsJSONObject.has(versionedPath)) {
			return false;
		}

		JSONObject pathJSONObject = pathsJSONObject.getJSONObject(
			versionedPath);

		return pathJSONObject.has(httpMethod);
	}

	private void _setFeatureFlagEnabled(boolean enabled, String featureFlagKey)
		throws Exception {

		PropsUtil.set(
			FeatureFlagConstants.getKey(featureFlagKey),
			String.valueOf(enabled));

		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), enabled, featureFlagKey);
	}

	private void _testDeleteFeatureFlagMethodTestEntityBatchWhenFeatureFlagIsDisabled()
		throws Exception {

		_setFeatureFlagEnabled(false, _FEATURE_FLAG_KEY);

		assertHttpResponseStatusCode(
			404,
			featureFlagMethodTestEntityResource.
				deleteFeatureFlagMethodTestEntityBatchHttpResponse(
					null,
					JSONUtil.putAll(
						JSONUtil.put("id", RandomTestUtil.randomLong()))));

		Assert.assertFalse(_hasOperation("delete", _PATH + "/batch"));
	}

	private void _testDeleteFeatureFlagMethodTestEntityBatchWhenFeatureFlagIsEnabled()
		throws Exception {

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY);

		testDeleteFeatureFlagMethodTestEntityBatch_deleteFeatureFlagMethodTestEntity(
			202, null, RandomTestUtil.randomLong());

		Assert.assertTrue(_hasOperation("delete", _PATH + "/batch"));
	}

	private void _testDeleteFeatureFlagMethodTestEntityWhenFeatureFlagIsDisabled()
		throws Exception {

		_setFeatureFlagEnabled(false, _FEATURE_FLAG_KEY);

		assertHttpResponseStatusCode(
			404,
			featureFlagMethodTestEntityResource.
				deleteFeatureFlagMethodTestEntityHttpResponse(
					RandomTestUtil.randomLong()));

		Assert.assertFalse(
			_hasOperation(
				"delete", _PATH + "/{featureFlagMethodTestEntityId}"));
	}

	private void _testDeleteFeatureFlagMethodTestEntityWhenFeatureFlagIsDisabledUsingFactory()
		throws Exception {

		_setFeatureFlagEnabled(false, _FEATURE_FLAG_KEY);

		FeatureFlagMethodTestEntityResource
			factoryFeatureFlagMethodTestEntityResource =
				_buildFeatureFlagMethodTestEntityResource();

		AssertUtils.assertFailure(
			NotFoundException.class, "HTTP 404 Not Found",
			() ->
				factoryFeatureFlagMethodTestEntityResource.
					deleteFeatureFlagMethodTestEntity(
						RandomTestUtil.randomLong()));
	}

	private void _testDeleteFeatureFlagMethodTestEntityWhenFeatureFlagIsEnabled()
		throws Exception {

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY);

		assertHttpResponseStatusCode(
			204,
			featureFlagMethodTestEntityResource.
				deleteFeatureFlagMethodTestEntityHttpResponse(
					RandomTestUtil.randomLong()));

		Assert.assertTrue(
			_hasOperation(
				"delete", _PATH + "/{featureFlagMethodTestEntityId}"));
	}

	private void _testDeleteFeatureFlagMethodTestEntityWhenFeatureFlagIsEnabledUsingFactory()
		throws Exception {

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY);

		FeatureFlagMethodTestEntityResource
			factoryFeatureFlagMethodTestEntityResource =
				_buildFeatureFlagMethodTestEntityResource();

		factoryFeatureFlagMethodTestEntityResource.
			deleteFeatureFlagMethodTestEntity(RandomTestUtil.randomLong());
	}

	private void _testDeleteSiteFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsDisabled()
		throws Exception {

		_setFeatureFlagEnabled(false, _FEATURE_FLAG_KEY_SITE);

		assertHttpResponseStatusCode(
			404,
			featureFlagMethodTestEntityResource.
				deleteSiteFeatureFlagMethodTestEntityByExternalReferenceCodeHttpResponse(
					testGroup.getGroupId(), RandomTestUtil.randomString()));

		Assert.assertFalse(
			_hasOperation(
				"delete",
				"sites/{siteId}/" + _PATH +
					"/by-external-reference-code/{externalReferenceCode}"));
	}

	private void _testDeleteSiteFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsEnabled()
		throws Exception {

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY_SITE);

		assertHttpResponseStatusCode(
			204,
			featureFlagMethodTestEntityResource.
				deleteSiteFeatureFlagMethodTestEntityByExternalReferenceCodeHttpResponse(
					testGroup.getGroupId(), RandomTestUtil.randomString()));

		Assert.assertTrue(
			_hasOperation(
				"delete",
				"sites/{siteId}/" + _PATH +
					"/by-external-reference-code/{externalReferenceCode}"));
	}

	private void _testGetFeatureFlagMethodTestEntitiesPageWhenFeatureFlagIsDisabled()
		throws Exception {

		_setFeatureFlagEnabled(false, _FEATURE_FLAG_KEY);

		HttpInvoker.HttpResponse httpResponse =
			featureFlagMethodTestEntityResource.
				getFeatureFlagMethodTestEntitiesPageHttpResponse();

		assertHttpResponseStatusCode(404, httpResponse);

		Assert.assertEquals(
			"NOT_FOUND",
			JSONUtil.getValueAsString(
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()),
				"Object/status"));

		assertHttpResponseStatusCode(
			404,
			featureFlagMethodTestEntityResource.
				postFeatureFlagMethodTestEntitiesPageExportBatchHttpResponse(
					null, null, null));
		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null, "portal-tools-rest-builder-test/v1.0/test-entities",
				Http.Method.GET));

		Assert.assertFalse(_hasOperation("get", _PATH));
		Assert.assertFalse(_hasOperation("post", _PATH + "/export-batch"));
		Assert.assertTrue(_hasOperation("get", "test-entities"));
	}

	private void _testGetFeatureFlagMethodTestEntitiesPageWhenFeatureFlagIsEnabled()
		throws Exception {

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY);

		assertHttpResponseStatusCode(
			200,
			featureFlagMethodTestEntityResource.
				getFeatureFlagMethodTestEntitiesPageHttpResponse());

		HttpInvoker.HttpResponse httpResponse =
			featureFlagMethodTestEntityResource.
				postFeatureFlagMethodTestEntitiesPageExportBatchHttpResponse(
					null, null, null);

		assertHttpResponseStatusCode(202, httpResponse);

		_waitForFinish(ExportTask.toDTO(httpResponse.getContent()));

		Assert.assertTrue(_hasOperation("get", _PATH));
		Assert.assertTrue(_hasOperation("post", _PATH + "/export-batch"));
	}

	private void _testGetFeatureFlagMethodTestEntitiesPageWhenOperationIdIsExcluded()
		throws Exception {

		String pid = ConfigurationTestUtil.createFactoryConfiguration(
			"com.liferay.portal.vulcan.internal.configuration." +
				"VulcanConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				"excludedOperationIds", "getFeatureFlagMethodTestEntitiesPage"
			).put(
				"graphQLEnabled", true
			).put(
				"path", "/portal-tools-rest-builder-test"
			).put(
				"restEnabled", true
			).build());

		try {
			_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY);

			assertHttpResponseStatusCode(
				409,
				featureFlagMethodTestEntityResource.
					getFeatureFlagMethodTestEntitiesPageHttpResponse());

			Assert.assertFalse(_hasOperation("get", _PATH));
			Assert.assertTrue(_hasOperation("post", _PATH));

			_setFeatureFlagEnabled(false, _FEATURE_FLAG_KEY);

			assertHttpResponseStatusCode(
				404,
				featureFlagMethodTestEntityResource.
					getFeatureFlagMethodTestEntitiesPageHttpResponse());
		}
		finally {
			ConfigurationTestUtil.deleteConfiguration(pid);
		}
	}

	private void _testGetFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsDisabled()
		throws Exception {

		_setFeatureFlagEnabled(
			false, _FEATURE_FLAG_KEY_GET_BY_EXTERNAL_REFERENCE_CODE);

		assertHttpResponseStatusCode(
			404,
			featureFlagMethodTestEntityResource.
				getFeatureFlagMethodTestEntityByExternalReferenceCodeHttpResponse(
					RandomTestUtil.randomString()));

		Assert.assertFalse(
			_hasOperation(
				"get",
				_PATH + "/by-external-reference-code/{externalReferenceCode}"));
	}

	private void _testGetFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsEnabled()
		throws Exception {

		_setFeatureFlagEnabled(
			true, _FEATURE_FLAG_KEY_GET_BY_EXTERNAL_REFERENCE_CODE);

		assertHttpResponseStatusCode(
			200,
			featureFlagMethodTestEntityResource.
				getFeatureFlagMethodTestEntityByExternalReferenceCodeHttpResponse(
					RandomTestUtil.randomString()));

		Assert.assertTrue(
			_hasOperation(
				"get",
				_PATH + "/by-external-reference-code/{externalReferenceCode}"));
	}

	private void _testGetSiteFeatureFlagMethodTestEntitiesPageWhenFeatureFlagIsDisabled()
		throws Exception {

		_setFeatureFlagEnabled(false, _FEATURE_FLAG_KEY_SITE);

		assertHttpResponseStatusCode(
			404,
			featureFlagMethodTestEntityResource.
				getSiteFeatureFlagMethodTestEntitiesPageHttpResponse(
					testGroup.getGroupId()));

		Assert.assertFalse(_hasOperation("get", "sites/{siteId}/" + _PATH));
	}

	private void _testGetSiteFeatureFlagMethodTestEntitiesPageWhenFeatureFlagIsEnabled()
		throws Exception {

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY_SITE);

		assertHttpResponseStatusCode(
			200,
			featureFlagMethodTestEntityResource.
				getSiteFeatureFlagMethodTestEntitiesPageHttpResponse(
					testGroup.getGroupId()));

		Assert.assertTrue(_hasOperation("get", "sites/{siteId}/" + _PATH));
	}

	private void _testGraphQLDeleteFeatureFlagMethodTestEntityWhenFeatureFlagIsDisabled()
		throws Exception {

		_setFeatureFlagEnabled(false, _FEATURE_FLAG_KEY);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"notprivacysafe.graphql.GraphQL", LoggerTestUtil.ERROR)) {

			_assertFieldUndefined(
				"deleteFeatureFlagMethodTestEntity",
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteFeatureFlagMethodTestEntity",
						HashMapBuilder.<String, Object>put(
							"featureFlagMethodTestEntityId",
							RandomTestUtil.randomLong()
						).build())),
				"mutation");
		}
	}

	private void _testGraphQLDeleteFeatureFlagMethodTestEntityWhenFeatureFlagIsEnabled()
		throws Exception {

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY);

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteFeatureFlagMethodTestEntity",
						HashMapBuilder.<String, Object>put(
							"featureFlagMethodTestEntityId",
							RandomTestUtil.randomLong()
						).build())),
				"JSONObject/data", "Object/deleteFeatureFlagMethodTestEntity"));
	}

	private void _testGraphQLDeleteSiteFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsDisabled()
		throws Exception {

		_setFeatureFlagEnabled(false, _FEATURE_FLAG_KEY_SITE);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"notprivacysafe.graphql.GraphQL", LoggerTestUtil.ERROR)) {

			_assertFieldUndefined(
				"deleteSiteFeatureFlagMethodTestEntityByExternalReferenceCode",
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteFeatureFlagMethodTestEntity" +
							"ByExternalReferenceCode",
						HashMapBuilder.<String, Object>put(
							"externalReferenceCode",
							"\"" + RandomTestUtil.randomString() + "\""
						).put(
							"siteKey", "\"" + testGroup.getGroupId() + "\""
						).build())),
				"mutation");
		}
	}

	private void _testGraphQLDeleteSiteFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsEnabled()
		throws Exception {

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY_SITE);

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteFeatureFlagMethodTestEntity" +
							"ByExternalReferenceCode",
						HashMapBuilder.<String, Object>put(
							"externalReferenceCode",
							"\"" + RandomTestUtil.randomString() + "\""
						).put(
							"siteKey", "\"" + testGroup.getGroupId() + "\""
						).build())),
				"JSONObject/data",
				"Object/deleteSiteFeatureFlagMethodTestEntity" +
					"ByExternalReferenceCode"));
	}

	private void _testGraphQLGetFeatureFlagMethodTestEntitiesPageWhenFeatureFlagIsDisabled()
		throws Exception {

		_setFeatureFlagEnabled(false, _FEATURE_FLAG_KEY);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"notprivacysafe.graphql.GraphQL", LoggerTestUtil.ERROR)) {

			_assertFieldUndefined(
				"featureFlagMethodTestEntities",
				invokeGraphQLQuery(
					new GraphQLField(
						"featureFlagMethodTestEntities",
						new GraphQLField("totalCount"))),
				"query");
		}
	}

	private void _testGraphQLGetFeatureFlagMethodTestEntitiesPageWhenFeatureFlagIsEnabled()
		throws Exception {

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY);

		Assert.assertEquals(
			0,
			JSONUtil.getValueAsLong(
				invokeGraphQLQuery(
					new GraphQLField(
						"featureFlagMethodTestEntities",
						new GraphQLField("totalCount"))),
				"JSONObject/data", "JSONObject/featureFlagMethodTestEntities",
				"Object/totalCount"));
	}

	private void _testGraphQLGetFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsDisabled()
		throws Exception {

		_setFeatureFlagEnabled(
			false, _FEATURE_FLAG_KEY_GET_BY_EXTERNAL_REFERENCE_CODE);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"notprivacysafe.graphql.GraphQL", LoggerTestUtil.ERROR)) {

			_assertFieldUndefined(
				"featureFlagMethodTestEntityByExternalReferenceCode",
				invokeGraphQLQuery(
					new GraphQLField(
						"featureFlagMethodTestEntityByExternalReferenceCode",
						HashMapBuilder.<String, Object>put(
							"externalReferenceCode",
							"\"" + RandomTestUtil.randomString() + "\""
						).build(),
						new GraphQLField("id"))),
				"query");
		}
	}

	private void _testGraphQLGetFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsEnabled()
		throws Exception {

		_setFeatureFlagEnabled(
			true, _FEATURE_FLAG_KEY_GET_BY_EXTERNAL_REFERENCE_CODE);

		JSONObject dataJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"featureFlagMethodTestEntityByExternalReferenceCode",
					HashMapBuilder.<String, Object>put(
						"externalReferenceCode",
						"\"" + RandomTestUtil.randomString() + "\""
					).build(),
					new GraphQLField("id"))),
			"JSONObject/data");

		Assert.assertFalse(
			dataJSONObject.isNull(
				"featureFlagMethodTestEntityByExternalReferenceCode"));
	}

	private void _testGraphQLGetSiteFeatureFlagMethodTestEntitiesPageWhenFeatureFlagIsDisabled()
		throws Exception {

		_setFeatureFlagEnabled(false, _FEATURE_FLAG_KEY_SITE);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"notprivacysafe.graphql.GraphQL", LoggerTestUtil.ERROR)) {

			_assertFieldUndefined(
				"siteFeatureFlagMethodTestEntities",
				invokeGraphQLQuery(
					new GraphQLField(
						"siteFeatureFlagMethodTestEntities",
						HashMapBuilder.<String, Object>put(
							"siteKey", "\"" + testGroup.getGroupId() + "\""
						).build(),
						new GraphQLField("totalCount"))),
				"query");
		}
	}

	private void _testGraphQLGetSiteFeatureFlagMethodTestEntitiesPageWhenFeatureFlagIsEnabled()
		throws Exception {

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY_SITE);

		Assert.assertEquals(
			0,
			JSONUtil.getValueAsLong(
				invokeGraphQLQuery(
					new GraphQLField(
						"siteFeatureFlagMethodTestEntities",
						HashMapBuilder.<String, Object>put(
							"siteKey", "\"" + testGroup.getGroupId() + "\""
						).build(),
						new GraphQLField("totalCount"))),
				"JSONObject/data",
				"JSONObject/siteFeatureFlagMethodTestEntities",
				"Object/totalCount"));
	}

	private void _testGraphQLPostFeatureFlagMethodTestEntityWhenFeatureFlagIsDisabled()
		throws Exception {

		_setFeatureFlagEnabled(false, _FEATURE_FLAG_KEY);

		JSONObject dataJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLMutation(
				new GraphQLField(
					"createFeatureFlagMethodTestEntity",
					HashMapBuilder.<String, Object>put(
						"featureFlagMethodTestEntity", "{}"
					).build(),
					new GraphQLField("id"))),
			"JSONObject/data");

		Assert.assertFalse(
			dataJSONObject.isNull("createFeatureFlagMethodTestEntity"));
	}

	private void _testGraphQLPostSiteFeatureFlagMethodTestEntityWhenFeatureFlagIsDisabled()
		throws Exception {

		_setFeatureFlagEnabled(false, _FEATURE_FLAG_KEY_SITE);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"notprivacysafe.graphql.GraphQL", LoggerTestUtil.ERROR)) {

			_assertFieldUndefined(
				"createSiteFeatureFlagMethodTestEntity",
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteFeatureFlagMethodTestEntity",
						HashMapBuilder.<String, Object>put(
							"featureFlagMethodTestEntity", "{}"
						).put(
							"siteKey", "\"" + testGroup.getGroupId() + "\""
						).build(),
						new GraphQLField("id"))),
				"mutation");
		}
	}

	private void _testGraphQLPostSiteFeatureFlagMethodTestEntityWhenFeatureFlagIsEnabled()
		throws Exception {

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY_SITE);

		JSONObject dataJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLMutation(
				new GraphQLField(
					"createSiteFeatureFlagMethodTestEntity",
					HashMapBuilder.<String, Object>put(
						"featureFlagMethodTestEntity", "{}"
					).put(
						"siteKey", "\"" + testGroup.getGroupId() + "\""
					).build(),
					new GraphQLField("id"))),
			"JSONObject/data");

		Assert.assertFalse(
			dataJSONObject.isNull("createSiteFeatureFlagMethodTestEntity"));
	}

	private void _testPatchFeatureFlagMethodTestEntityWhenFeatureFlagIsDisabled()
		throws Exception {

		_setFeatureFlagEnabled(false, _FEATURE_FLAG_KEY);

		assertHttpResponseStatusCode(
			404,
			featureFlagMethodTestEntityResource.
				patchFeatureFlagMethodTestEntityHttpResponse(
					RandomTestUtil.randomLong(),
					randomFeatureFlagMethodTestEntity()));

		Assert.assertFalse(
			_hasOperation("patch", _PATH + "/{featureFlagMethodTestEntityId}"));
	}

	private void _testPatchFeatureFlagMethodTestEntityWhenFeatureFlagIsEnabled()
		throws Exception {

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY);

		assertHttpResponseStatusCode(
			200,
			featureFlagMethodTestEntityResource.
				patchFeatureFlagMethodTestEntityHttpResponse(
					RandomTestUtil.randomLong(),
					randomFeatureFlagMethodTestEntity()));

		Assert.assertTrue(
			_hasOperation("patch", _PATH + "/{featureFlagMethodTestEntityId}"));
	}

	private void _testPostFeatureFlagMethodTestEntityWhenFeatureFlagIsDisabled()
		throws Exception {

		_setFeatureFlagEnabled(false, _FEATURE_FLAG_KEY);

		assertHttpResponseStatusCode(
			200,
			featureFlagMethodTestEntityResource.
				postFeatureFlagMethodTestEntityHttpResponse(
					randomFeatureFlagMethodTestEntity()));

		Assert.assertTrue(_hasOperation("post", _PATH));
		Assert.assertTrue(_hasOperation("post", _PATH + "/batch"));
	}

	private void _testPostSiteFeatureFlagMethodTestEntityWhenFeatureFlagIsDisabled()
		throws Exception {

		_setFeatureFlagEnabled(false, _FEATURE_FLAG_KEY_SITE);

		assertHttpResponseStatusCode(
			404,
			featureFlagMethodTestEntityResource.
				postSiteFeatureFlagMethodTestEntityHttpResponse(
					testGroup.getGroupId(),
					randomFeatureFlagMethodTestEntity()));

		Assert.assertFalse(_hasOperation("post", "sites/{siteId}/" + _PATH));
	}

	private void _testPostSiteFeatureFlagMethodTestEntityWhenFeatureFlagIsEnabled()
		throws Exception {

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY_SITE);

		assertHttpResponseStatusCode(
			200,
			featureFlagMethodTestEntityResource.
				postSiteFeatureFlagMethodTestEntityHttpResponse(
					testGroup.getGroupId(),
					randomFeatureFlagMethodTestEntity()));

		Assert.assertTrue(_hasOperation("post", "sites/{siteId}/" + _PATH));
	}

	private void _testPutFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsDisabled()
		throws Exception {

		_setFeatureFlagEnabled(
			false, _FEATURE_FLAG_KEY_PUT_BY_EXTERNAL_REFERENCE_CODE);

		assertHttpResponseStatusCode(
			404,
			featureFlagMethodTestEntityResource.
				putFeatureFlagMethodTestEntityByExternalReferenceCodeHttpResponse(
					RandomTestUtil.randomString(),
					randomFeatureFlagMethodTestEntity()));

		Assert.assertFalse(
			_hasOperation(
				"put",
				_PATH + "/by-external-reference-code/{externalReferenceCode}"));
	}

	private void _testPutFeatureFlagMethodTestEntityByExternalReferenceCodeWhenFeatureFlagIsEnabled()
		throws Exception {

		_setFeatureFlagEnabled(
			true, _FEATURE_FLAG_KEY_PUT_BY_EXTERNAL_REFERENCE_CODE);

		assertHttpResponseStatusCode(
			200,
			featureFlagMethodTestEntityResource.
				putFeatureFlagMethodTestEntityByExternalReferenceCodeHttpResponse(
					RandomTestUtil.randomString(),
					randomFeatureFlagMethodTestEntity()));

		Assert.assertTrue(
			_hasOperation(
				"put",
				_PATH + "/by-external-reference-code/{externalReferenceCode}"));
	}

	private void _waitForFinish(ExportTask exportTask) throws Exception {
		User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		ExportTaskResource exportTaskResource = ExportTaskResource.builder(
		).authentication(
			user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		while (true) {
			exportTask = exportTaskResource.getExportTask(exportTask.getId());

			ExportTask.ExecuteStatus executeStatus =
				exportTask.getExecuteStatus();

			if ((executeStatus == ExportTask.ExecuteStatus.COMPLETED) ||
				(executeStatus == ExportTask.ExecuteStatus.FAILED)) {

				Assert.assertEquals(
					exportTask.getErrorMessage(),
					ExportTask.ExecuteStatus.COMPLETED, executeStatus);

				return;
			}
		}
	}

	private static final String _FEATURE_FLAG_KEY = "METHOD-123";

	private static final String
		_FEATURE_FLAG_KEY_GET_BY_EXTERNAL_REFERENCE_CODE = "METHOD-124";

	private static final String
		_FEATURE_FLAG_KEY_PUT_BY_EXTERNAL_REFERENCE_CODE = "METHOD-125";

	private static final String _FEATURE_FLAG_KEY_SITE = "METHOD-126";

	private static final String _PATH = "feature-flag-method-test-entities";

	@Inject
	private FeatureFlagMethodTestEntityResource.Factory
		_featureFlagMethodTestEntityResourceFactory;

}