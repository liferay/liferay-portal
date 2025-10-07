/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.SiteTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.pagination.Page;
import com.liferay.portal.tools.rest.builder.test.client.resource.v1_0.SiteTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.SiteTestEntitySerDes;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class SiteTestEntityResourceTest
	extends BaseSiteTestEntityResourceTestCase {

	@Override
	@Test
	@TestInfo("LPD-53838")
	public void testGetSiteSiteTestEntitiesPage() throws Exception {
		super.testGetSiteSiteTestEntitiesPage();

		Page<SiteTestEntity> page =
			siteTestEntityResource.getSiteSiteTestEntitiesPage(
				testGroup.getGroupId());

		long totalCount = page.getTotalCount();

		testGetSiteSiteTestEntitiesPage_addSiteTestEntity(
			testGroup.getGroupId(), randomSiteTestEntity());

		_assertSiteSiteTestEntitiesCount(
			testGroup.getExternalReferenceCode(), totalCount + 1);

		testGetSiteSiteTestEntitiesPage_addSiteTestEntity(
			testGroup.getGroupId(), randomSiteTestEntity());

		_assertSiteSiteTestEntitiesCount(
			testGroup.getGroupKey(), totalCount + 2);
	}

	@Override
	@Test
	@TestInfo("LPD-54012")
	public void testGraphQLGetSiteSiteTestEntitiesPage() throws Exception {
		super.testGraphQLGetSiteSiteTestEntitiesPage();

		JSONObject siteTestEntitiesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"siteTestEntities",
					HashMapBuilder.<String, Object>put(
						"siteKey",
						"\"" + testGroup.getExternalReferenceCode() + "\""
					).build(),
					new GraphQLField("items", getGraphQLFields()),
					new GraphQLField("page"), new GraphQLField("totalCount"))),
			"JSONObject/data", "JSONObject/siteTestEntities");

		long totalCount = siteTestEntitiesJSONObject.getLong("totalCount");

		testGraphQLSiteSiteTestEntity_addSiteTestEntity(
			testGroup.getGroupId(), randomSiteTestEntity());

		siteTestEntitiesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"siteTestEntities",
					HashMapBuilder.<String, Object>put(
						"siteKey", "\"" + testGroup.getGroupId() + "\""
					).build(),
					new GraphQLField("items", getGraphQLFields()),
					new GraphQLField("page"), new GraphQLField("totalCount"))),
			"JSONObject/data", "JSONObject/siteTestEntities");

		Assert.assertEquals(
			totalCount + 1, siteTestEntitiesJSONObject.getLong("totalCount"));

		testGraphQLSiteSiteTestEntity_addSiteTestEntity(
			testGroup.getGroupId(), randomSiteTestEntity());

		siteTestEntitiesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"siteTestEntities",
					HashMapBuilder.<String, Object>put(
						"siteKey", "\"" + testGroup.getGroupKey() + "\""
					).build(),
					new GraphQLField("items", getGraphQLFields()),
					new GraphQLField("page"), new GraphQLField("totalCount"))),
			"JSONObject/data", "JSONObject/siteTestEntities");

		Assert.assertEquals(
			totalCount + 2, siteTestEntitiesJSONObject.getLong("totalCount"));
	}

	@Override
	@Test
	public void testGraphQLGetSiteTestEntity() throws Exception {
		SiteTestEntity siteTestEntity =
			testGraphQLGetSiteTestEntity_addSiteTestEntity();

		// No namespace

		Assert.assertTrue(
			equals(
				siteTestEntity,
				SiteTestEntitySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"siteTestEntity",
								HashMapBuilder.<String, Object>put(
									"siteTestEntityId", siteTestEntity.getId()
								).build(),
								getGraphQLFields())),
						"JSONObject/data", "Object/siteTestEntity"))));

		// Using the namespace test_v1_0

		Assert.assertTrue(
			equals(
				siteTestEntity,
				SiteTestEntitySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"test_v1_0",
								new GraphQLField(
									"siteTestEntity",
									HashMapBuilder.<String, Object>put(
										"siteTestEntityId",
										siteTestEntity.getId()
									).build(),
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/test_v1_0",
						"Object/siteTestEntity"))));
	}

	@Override
	@Test
	public void testGraphQLGetSiteTestEntityNotFound() throws Exception {
		Long irrelevantSiteTestEntityId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"siteTestEntity",
						HashMapBuilder.<String, Object>put(
							"siteTestEntityId", irrelevantSiteTestEntityId
						).build(),
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace test_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"test_v1_0",
						new GraphQLField(
							"siteTestEntity",
							HashMapBuilder.<String, Object>put(
								"siteTestEntityId", irrelevantSiteTestEntityId
							).build(),
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	@Override
	@Test
	public void testPostSiteSiteTestEntity() throws Exception {
		super.testPostSiteSiteTestEntity();

		_testPostSiteTestEntityBatch();
	}

	@Override
	@Test
	public void testPutSiteTestEntity() throws Exception {
		super.testPutSiteTestEntity();

		_testPutSiteTestEntityBatch();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"description"};
	}

	private void _assertSiteSiteTestEntitiesCount(
			String siteId, long totalCount)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, "test/v1.0/sites/" + siteId + "/site-test-entities",
			Http.Method.GET);

		Assert.assertEquals(totalCount, jsonObject.getInt("totalCount"));
	}

	private SiteTestEntityResource _createSiteTestEntityResource(
			String importStrategy, String updateStrategy)
		throws Exception {

		User testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		return SiteTestEntityResource.builder(
		).authentication(
			testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).parameter(
			"importStrategy", importStrategy
		).parameter(
			"updateStrategy", updateStrategy
		).build();
	}

	private void _testPostSiteTestEntityBatch() throws Exception {
		SiteTestEntity siteTestEntity =
			testPostSiteSiteTestEntity_addSiteTestEntity(
				randomSiteTestEntity());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineImportTaskExecutorImpl",
				LoggerTestUtil.ERROR)) {

			_waitForFinish(
				"FAILED", true,
				JSONFactoryUtil.createJSONObject(
					siteTestEntityResource.
						postSiteSiteTestEntityBatchHttpResponse(
							testGroup.getGroupId(), null,
							JSONUtil.putAll(
								JSONFactoryUtil.createJSONObject(
									siteTestEntity.toString()))
						).getContent()));
		}
	}

	private void _testPutSiteTestEntityBatch() throws Exception {
		SiteTestEntity postSiteTestEntity =
			siteTestEntityResource.postSiteSiteTestEntity(
				testGroup.getGroupId(), randomSiteTestEntity());

		String description = RandomTestUtil.randomString();

		postSiteTestEntity.setDescription(description);

		SiteTestEntity randomSiteTestEntity = randomSiteTestEntity();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal.strategy." +
					"OnErrorContinueBatchEngineImportStrategy",
				LoggerTestUtil.ERROR)) {

			SiteTestEntityResource siteTestEntityResource =
				_createSiteTestEntityResource("ON_ERROR_CONTINUE", "UPDATE");

			_waitForFinish(
				"COMPLETED", true,
				JSONFactoryUtil.createJSONObject(
					siteTestEntityResource.putSiteTestEntityBatchHttpResponse(
						null,
						JSONUtil.putAll(
							JSONFactoryUtil.createJSONObject(
								postSiteTestEntity.toString()),
							JSONFactoryUtil.createJSONObject(
								randomSiteTestEntity.toString()))
					).getContent()));
		}

		SiteTestEntity actualSiteTestEntity =
			siteTestEntityResource.getSiteTestEntity(
				postSiteTestEntity.getId());

		Assert.assertEquals(description, actualSiteTestEntity.getDescription());

		assertHttpResponseStatusCode(
			404,
			siteTestEntityResource.
				getSiteSiteTestEntityByExternalReferenceCodeHttpResponse(
					testGroup.getGroupId(),
					randomSiteTestEntity.getExternalReferenceCode()));
	}

	private JSONObject _waitForFinish(
			String expectedExecuteStatus, boolean importTask,
			JSONObject jsonObject)
		throws Exception {

		String endpoint = StringBundler.concat(
			"headless-batch-engine/v1.0/",
			importTask ? "import-task" : "export-task",
			"/by-external-reference-code/");

		while (true) {
			jsonObject = HTTPTestUtil.invokeToJSONObject(
				null, endpoint + jsonObject.getString("externalReferenceCode"),
				Http.Method.GET);

			String executeStatus = jsonObject.getString("executeStatus");

			if (StringUtil.equals(executeStatus, "COMPLETED") ||
				StringUtil.equals(executeStatus, "FAILED")) {

				Assert.assertEquals(expectedExecuteStatus, executeStatus);

				return jsonObject;
			}
		}
	}

}