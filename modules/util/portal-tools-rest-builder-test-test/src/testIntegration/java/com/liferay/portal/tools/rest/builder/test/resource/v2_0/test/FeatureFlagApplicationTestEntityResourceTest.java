/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.tools.rest.builder.test.resource.v2_0.FeatureFlagApplicationTestEntityResource;
import com.liferay.portal.vulcan.application.HeadlessApplicationProvider;

import jakarta.ws.rs.NotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class FeatureFlagApplicationTestEntityResourceTest
	extends BaseFeatureFlagApplicationTestEntityResourceTestCase {

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), false, _FEATURE_FLAG_KEY);
	}

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY)
	@Override
	@Test
	public void testGetFeatureFlagApplicationTestEntitiesPage()
		throws Exception {

		_testGetFeatureFlagApplicationTestEntitiesPageWhenFeatureFlagIsDisabled();
		_testGetFeatureFlagApplicationTestEntitiesPageWhenFeatureFlagIsEnabled();
	}

	private FeatureFlagApplicationTestEntityResource
			_buildFeatureFlagApplicationTestEntityResource()
		throws Exception {

		return _featureFlagApplicationTestEntityResourceFactory.create(
		).user(
			TestPropsValues.getUser()
		).build();
	}

	private HeadlessApplicationProvider.Application _getApplication()
		throws Exception {

		for (HeadlessApplicationProvider.Application application :
				_headlessApplicationProvider.getApplications(
					TestPropsValues.getCompanyId())) {

			if (Objects.equals(
					application.getBasePath(),
					"/portal-tools-rest-builder-test")) {

				return application;
			}
		}

		return null;
	}

	private int _getHttpCode(String path) throws Exception {
		return HTTPTestUtil.invokeToHttpCode(
			null, "portal-tools-rest-builder-test/" + path, Http.Method.GET);
	}

	private List<String> _getOpenAPIDocumentVersions() throws Exception {
		HeadlessApplicationProvider.Application application = _getApplication();

		return TransformUtil.transform(
			application.getOpenAPIDocuments(),
			HeadlessApplicationProvider.OpenAPIDocument::getVersion);
	}

	private boolean _hasResourceMethods(String version) throws Exception {
		HeadlessApplicationProvider.Application application = _getApplication();

		for (HeadlessApplicationProvider.ResourceMethod resourceMethod :
				application.getResourceMethods()) {

			String path = resourceMethod.getPath();

			if (path.startsWith(
					"/portal-tools-rest-builder-test/" + version + "/")) {

				return true;
			}
		}

		return false;
	}

	private void _setFeatureFlagEnabled(boolean enabled) throws Exception {
		PropsUtil.set(
			FeatureFlagConstants.getKey(_FEATURE_FLAG_KEY),
			String.valueOf(enabled));

		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), enabled, _FEATURE_FLAG_KEY);
	}

	private void _testGetFeatureFlagApplicationTestEntitiesPageWhenFeatureFlagIsDisabled()
		throws Exception {

		_setFeatureFlagEnabled(false);

		assertHttpResponseStatusCode(
			404,
			featureFlagApplicationTestEntityResource.
				getFeatureFlagApplicationTestEntitiesPageHttpResponse());
		Assert.assertEquals(200, _getHttpCode("v1.0/openapi.json"));
		Assert.assertEquals(404, _getHttpCode("v2.0/openapi.json"));

		Assert.assertEquals(
			Collections.singletonList("v1.0"), _getOpenAPIDocumentVersions());
		Assert.assertFalse(_hasResourceMethods("v2.0"));

		FeatureFlagApplicationTestEntityResource
			factoryFeatureFlagApplicationTestEntityResource =
				_buildFeatureFlagApplicationTestEntityResource();

		AssertUtils.assertFailure(
			NotFoundException.class, "HTTP 404 Not Found",
			() ->
				factoryFeatureFlagApplicationTestEntityResource.
					getFeatureFlagApplicationTestEntitiesPage());

		String fieldName = "featureFlagApplicationTestEntities";

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"notprivacysafe.graphql.GraphQL", LoggerTestUtil.ERROR)) {

			JSONAssert.assertEquals(
				JSONUtil.put(
					"errors",
					JSONUtil.putAll(
						JSONUtil.put(
							"extensions",
							JSONUtil.put(
								"exception", JSONUtil.put("errno", 400))
						).put(
							"message",
							StringBundler.concat(
								"Validation error (FieldUndefined@[", fieldName,
								"]) : Field '", fieldName,
								"' in type 'query' is undefined")
						))
				).toString(),
				invokeGraphQLQuery(
					new GraphQLField(fieldName, new GraphQLField("totalCount"))
				).toString(),
				JSONCompareMode.LENIENT);
		}
	}

	private void _testGetFeatureFlagApplicationTestEntitiesPageWhenFeatureFlagIsEnabled()
		throws Exception {

		_setFeatureFlagEnabled(true);

		assertHttpResponseStatusCode(
			200,
			featureFlagApplicationTestEntityResource.
				getFeatureFlagApplicationTestEntitiesPageHttpResponse());
		Assert.assertEquals(200, _getHttpCode("v2.0/openapi.json"));

		Assert.assertEquals(
			Arrays.asList("v1.0", "v2.0"), _getOpenAPIDocumentVersions());
		Assert.assertTrue(_hasResourceMethods("v2.0"));

		FeatureFlagApplicationTestEntityResource
			factoryFeatureFlagApplicationTestEntityResource =
				_buildFeatureFlagApplicationTestEntityResource();

		Assert.assertNotNull(
			factoryFeatureFlagApplicationTestEntityResource.
				getFeatureFlagApplicationTestEntitiesPage());

		Assert.assertEquals(
			0,
			JSONUtil.getValueAsLong(
				invokeGraphQLQuery(
					new GraphQLField(
						"featureFlagApplicationTestEntities",
						new GraphQLField("totalCount"))),
				"JSONObject/data",
				"JSONObject/featureFlagApplicationTestEntities",
				"Object/totalCount"));
	}

	private static final String _FEATURE_FLAG_KEY = "APPLICATION-123";

	@Inject
	private FeatureFlagApplicationTestEntityResource.Factory
		_featureFlagApplicationTestEntityResourceFactory;

	@Inject
	private HeadlessApplicationProvider _headlessApplicationProvider;

}