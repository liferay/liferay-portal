/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ChildTestEntity1;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ChildTestEntity2;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ChildTestEntity3;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.TestEntity;
import com.liferay.portal.tools.rest.builder.test.client.pagination.Page;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.ChildTestEntity1SerDes;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.ChildTestEntity2SerDes;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.ChildTestEntity3SerDes;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class TestEntityResourceTest extends BaseTestEntityResourceTestCase {

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Ignore
	@Override
	@Test
	public void testDeleteTestEntityBatch() throws Exception {
		super.testDeleteTestEntityBatch();
	}

	@Ignore
	@Override
	@Test
	public void testGetTestEntitiesPageWithFilterDateTimeEquals()
		throws Exception {

		super.testGetTestEntitiesPageWithFilterDateTimeEquals();
	}

	@Ignore
	@Override
	@Test
	public void testGetTestEntitiesPageWithFilterStringContains()
		throws Exception {

		super.testGetTestEntitiesPageWithFilterStringContains();
	}

	@Ignore
	@Override
	@Test
	public void testGetTestEntitiesPageWithFilterStringEquals()
		throws Exception {

		super.testGetTestEntitiesPageWithFilterStringEquals();
	}

	@Ignore
	@Override
	@Test
	public void testGetTestEntitiesPageWithFilterStringStartsWith()
		throws Exception {

		super.testGetTestEntitiesPageWithFilterStringStartsWith();
	}

	@Override
	@Test
	public void testGetTestEntityCount() throws Exception {
		int initialCount = testEntityResource.getTestEntityCount();

		testEntityResource.postTestEntity(randomTestEntity());

		Assert.assertEquals(
			Integer.valueOf(initialCount + 1),
			testEntityResource.getTestEntityCount());
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteTestEntity() throws Exception {
		super.testGraphQLDeleteTestEntity();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetTestEntitiesPage() throws Exception {
		super.testGraphQLGetTestEntitiesPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetTestEntity() throws Exception {
		super.testGraphQLGetTestEntity();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetTestEntityNotFound() throws Exception {
		super.testGraphQLGetTestEntityNotFound();
	}

	@Override
	@Test
	public void testPatchTestEntity() throws Exception {
		super.testPatchTestEntity();

		ChildTestEntity1 postChildTestEntity1 = new ChildTestEntity1();

		postChildTestEntity1.setProperty1(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		postChildTestEntity1.setType(
			TestEntity.Type.create("ChildTestEntity1"));

		postChildTestEntity1 =
			(ChildTestEntity1)testEntityResource.postTestEntity(
				postChildTestEntity1);

		// Patch child test entity 1

		ChildTestEntity1 randomPatchChildTestEntity1 = new ChildTestEntity1();

		randomPatchChildTestEntity1.setProperty1(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		randomPatchChildTestEntity1.setType(
			TestEntity.Type.create("ChildTestEntity1"));

		ChildTestEntity1 patchChildTestEntity1 =
			(ChildTestEntity1)testEntityResource.patchTestEntity(
				postChildTestEntity1.getId(),
				testPatchTestEntity_getOptionalParameter(),
				randomPatchChildTestEntity1);

		ChildTestEntity1 expectedPatchChildTestEntity1 =
			postChildTestEntity1.clone();

		BeanTestUtil.copyProperties(
			randomPatchChildTestEntity1, expectedPatchChildTestEntity1);

		ChildTestEntity1 getChildTestEntity1 =
			(ChildTestEntity1)testEntityResource.getTestEntity(
				patchChildTestEntity1.getId());

		assertEquals(expectedPatchChildTestEntity1, getChildTestEntity1);
		assertValid(getChildTestEntity1);

		// Patch child test entity 2

		ChildTestEntity2 randomPatchChildTestEntity2 = new ChildTestEntity2();

		randomPatchChildTestEntity2.setProperty2(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		randomPatchChildTestEntity2.setType(
			TestEntity.Type.create("ChildTestEntity2"));

		ChildTestEntity2 patchChildTestEntity2 =
			(ChildTestEntity2)testEntityResource.patchTestEntity(
				postChildTestEntity1.getId(),
				testPatchTestEntity_getOptionalParameter(),
				randomPatchChildTestEntity2);

		ChildTestEntity2 expectedPatchChildTestEntity2 = new ChildTestEntity2();

		BeanTestUtil.copyProperties(
			postChildTestEntity1, expectedPatchChildTestEntity2);

		BeanTestUtil.copyProperties(
			randomPatchChildTestEntity2, expectedPatchChildTestEntity2);

		ChildTestEntity2 getChildTestEntity2 =
			(ChildTestEntity2)testEntityResource.getTestEntity(
				patchChildTestEntity2.getId());

		assertEquals(expectedPatchChildTestEntity2, getChildTestEntity2);
		assertValid(getChildTestEntity2);
	}

	@Override
	@Test
	public void testPostReservedWord() throws Exception {
		testEntityResource.postReservedWord(true);
	}

	@Override
	@Test
	public void testPostTestEntity() throws Exception {
		super.testPostTestEntity();

		String invalidTypeId = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"name", StringUtil.toLowerCase(RandomTestUtil.randomString())
			).put(
				"type", invalidTypeId
			).toString(),
			"test/v1.0/test-entities", Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject.getString("status"));
		Assert.assertTrue(
			jsonObject.getString(
				"title"
			).contains(
				"Could not resolve type id '" + invalidTypeId + "' as a subtype"
			));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"name", StringUtil.toLowerCase(RandomTestUtil.randomString())
			).toString(),
			"test/v1.0/test-entities", Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject.getString("status"));
		Assert.assertTrue(
			jsonObject.getString(
				"title"
			).contains(
				"missing type id property 'type'"
			));

		// TODO Split after LPD-60141

		_testPostTestEntityImportTask();
	}

	@Ignore
	@Test
	public void testPostTestEntityMultipartBulk() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testPutTestEntityStatus() throws Exception {
		super.testPutTestEntityStatus();
	}

	@Ignore
	@Override
	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		super.testVulcanCRUDItemDelegateGetItem();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"property1", "property2"};
	}

	@Override
	protected TestEntity testDeleteTestEntity_addTestEntity() throws Exception {
		return testGetTestEntitiesPage_addTestEntity(randomTestEntity());
	}

	@Override
	protected TestEntity testGetTestEntitiesPage_addTestEntity(
			TestEntity testEntity)
		throws Exception {

		return testEntityResource.postTestEntity(testEntity);
	}

	@Override
	protected TestEntity testGetTestEntity_addTestEntity() throws Exception {
		return testGetTestEntitiesPage_addTestEntity(randomTestEntity());
	}

	@Override
	protected TestEntity testGraphQLTestEntity_addTestEntity()
		throws Exception {

		return testGetTestEntitiesPage_addTestEntity(randomTestEntity());
	}

	@Override
	protected TestEntity testGraphQLTestEntity_addTestEntity(
			TestEntity testEntity)
		throws Exception {

		return testEntityResource.postTestEntity(testEntity);
	}

	@Override
	protected TestEntity testPatchTestEntity_addTestEntity() throws Exception {
		return testGetTestEntitiesPage_addTestEntity(randomTestEntity());
	}

	@Override
	protected Long testPatchTestEntity_getOptionalParameter() {
		return RandomTestUtil.nextLong();
	}

	@Override
	protected TestEntity testPostTestEntity_addTestEntity(TestEntity testEntity)
		throws Exception {

		return testGetTestEntitiesPage_addTestEntity(testEntity);
	}

	@Override
	protected TestEntity testPutTestEntity_addTestEntity() throws Exception {
		return testGetTestEntitiesPage_addTestEntity(randomTestEntity());
	}

	@Override
	protected Long testPutTestEntity_getOptionalParameter() {
		return RandomTestUtil.nextLong();
	}

	private void _testPostTestEntityImportTask() throws Exception {
		ChildTestEntity1 childTestEntity1 = new ChildTestEntity1();

		childTestEntity1.setName(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		childTestEntity1.setProperty1(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		childTestEntity1.setType(TestEntity.Type.create("ChildTestEntity1"));

		ChildTestEntity2 childTestEntity2 = new ChildTestEntity2();

		childTestEntity2.setName(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		childTestEntity2.setProperty2(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		childTestEntity2.setType(TestEntity.Type.create("ChildTestEntity2"));

		ChildTestEntity3 childTestEntity3 = new ChildTestEntity3();

		childTestEntity3.setName(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		childTestEntity3.setType(TestEntity.Type.create("ChildTestEntity3"));

		Page<TestEntity> page = testEntityResource.getTestEntitiesPage(null);

		long totalCount = page.getTotalCount();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineImportTaskExecutorImpl",
				LoggerTestUtil.ERROR)) {

			String invalidTypeId = StringUtil.toLowerCase(
				RandomTestUtil.randomString());

			JSONObject jsonObject = waitForFinish(
				"COMPLETED",
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.putAll(
						JSONFactoryUtil.createJSONObject(
							ChildTestEntity1SerDes.toJSON(childTestEntity1)),
						JSONUtil.put(
							"name",
							StringUtil.toLowerCase(
								RandomTestUtil.randomString())
						).put(
							"type", invalidTypeId
						),
						JSONFactoryUtil.createJSONObject(
							ChildTestEntity2SerDes.toJSON(childTestEntity2)),
						JSONUtil.put(
							"name",
							StringUtil.toLowerCase(
								RandomTestUtil.randomString())),
						JSONFactoryUtil.createJSONObject(
							ChildTestEntity3SerDes.toJSON(childTestEntity3))
					).toString(),
					StringBundler.concat(
						"headless-batch-engine/v1.0/import-task",
						"/com.liferay.portal.tools.rest.builder.test.dto.v1_0.",
						"TestEntity?importStrategy=ON_ERROR_CONTINUE"),
					Http.Method.POST));

			page = testEntityResource.getTestEntitiesPage(null);

			Assert.assertEquals(totalCount + 3, page.getTotalCount());

			assertContains(childTestEntity1, (List<TestEntity>)page.getItems());
			assertContains(childTestEntity2, (List<TestEntity>)page.getItems());
			assertContains(childTestEntity3, (List<TestEntity>)page.getItems());

			jsonObject = HTTPTestUtil.invokeToJSONObject(
				null,
				"headless-batch-engine/v1.0/import-task/" +
					jsonObject.getLong("id"),
				Http.Method.GET);

			JSONArray failedItemsJSONArray = jsonObject.getJSONArray(
				"failedItems");

			Assert.assertEquals(2, failedItemsJSONArray.length());
			Assert.assertTrue(
				failedItemsJSONArray.getJSONObject(
					0
				).getString(
					"message"
				).contains(
					"Could not resolve type id '" + invalidTypeId +
						"' as a subtype"
				));
			Assert.assertTrue(
				failedItemsJSONArray.getJSONObject(
					1
				).getString(
					"message"
				).contains(
					"missing type id property 'type'"
				));
		}
	}

}