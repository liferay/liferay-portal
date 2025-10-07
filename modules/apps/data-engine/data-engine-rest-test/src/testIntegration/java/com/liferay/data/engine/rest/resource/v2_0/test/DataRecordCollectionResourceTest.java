/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.resource.v2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.data.engine.rest.client.dto.v2_0.DataRecordCollection;
import com.liferay.data.engine.rest.client.pagination.Page;
import com.liferay.data.engine.rest.client.pagination.Pagination;
import com.liferay.data.engine.rest.client.permission.Permission;
import com.liferay.data.engine.rest.resource.v2_0.test.util.DataDefinitionTestUtil;
import com.liferay.data.engine.rest.resource.v2_0.test.util.content.type.test.util.ModelResourceActionTestUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gabriel Albuquerque
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class DataRecordCollectionResourceTest
	extends BaseDataRecordCollectionResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseDataRecordCollectionResourceTestCase.setUpClass();

		ModelResourceActionTestUtil.populateModelResourceAction(
			_resourceActions);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ModelResourceActionTestUtil.deleteModelResourceAction(_resourceActions);
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_ddmStructure = DataDefinitionTestUtil.addDDMStructure(testGroup);
		_irrelevantDDMStructure = DataDefinitionTestUtil.addDDMStructure(
			irrelevantGroup);
	}

	@Override
	@Test
	public void testGetDataDefinitionDataRecordCollectionsPage()
		throws Exception {

		super.testGetDataDefinitionDataRecordCollectionsPage();

		_testGetDataDefinitionDataRecordCollectionsPage(
			"CoLLeCTion dEsCrIpTiOn", "COLLECTION", "name");
		_testGetDataDefinitionDataRecordCollectionsPage(
			"definition", "abcdefghijklmnopqrstuvwxyz0123456789",
			"abcdefghijklmnopqrstuvwxyz0123456789");
	}

	@Override
	@Test
	public void testGetDataRecordCollectionPermissionByCurrentUser()
		throws Exception {

		DataRecordCollection dataRecordCollection =
			dataRecordCollectionResource.postDataDefinitionDataRecordCollection(
				_ddmStructure.getStructureId(), randomDataRecordCollection());

		assertHttpResponseStatusCode(
			200,
			dataRecordCollectionResource.
				getDataRecordCollectionPermissionByCurrentUserHttpResponse(
					dataRecordCollection.getId()));
	}

	@Override
	@Test
	public void testGetDataRecordCollectionPermissionsPage() throws Exception {
		DataRecordCollection dataRecordCollection =
			testGetDataRecordCollection_addDataRecordCollection();

		Page<Permission> page =
			dataRecordCollectionResource.getDataRecordCollectionPermissionsPage(
				dataRecordCollection.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	@Override
	@Test
	public void testGraphQLGetDataDefinitionDataRecordCollection()
		throws Exception {

		DataRecordCollection dataRecordCollection =
			dataRecordCollectionResource.postDataDefinitionDataRecordCollection(
				_ddmStructure.getStructureId(),
				_randomDataRecordCollection(_ddmStructure.getStructureKey()));

		JSONObject dataRecordCollectionJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField(
						"dataDefinitionDataRecordCollection",
						HashMapBuilder.<String, Object>put(
							"dataDefinitionId",
							dataRecordCollection.getDataDefinitionId()
						).build(),
						getGraphQLFields())),
				"JSONObject/data",
				"JSONObject/dataDefinitionDataRecordCollection");

		Assert.assertEquals(
			MapUtil.getString(dataRecordCollection.getName(), "en_US"),
			JSONUtil.getValue(
				dataRecordCollectionJSONObject, "JSONObject/name",
				"Object/en_US"));
	}

	@Override
	@Test
	public void testGraphQLGetDataRecordCollection() throws Exception {
		DataRecordCollection dataRecordCollection =
			testGraphQLDataRecordCollection_addDataRecordCollection();

		JSONObject dataRecordCollectionJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField(
						"dataRecordCollection",
						HashMapBuilder.<String, Object>put(
							"dataRecordCollectionId",
							dataRecordCollection.getId()
						).build(),
						getGraphQLFields())),
				"JSONObject/data", "JSONObject/dataRecordCollection");

		Assert.assertEquals(
			GetterUtil.getLong(dataRecordCollection.getDataDefinitionId()),
			dataRecordCollectionJSONObject.getLong("dataDefinitionId"));
		Assert.assertEquals(
			MapUtil.getString(dataRecordCollection.getName(), "en_US"),
			JSONUtil.getValue(
				dataRecordCollectionJSONObject, "JSONObject/name",
				"Object/en_US"));
	}

	@Override
	@Test
	public void testGraphQLGetSiteDataRecordCollectionByDataRecordCollectionKey()
		throws Exception {

		DataRecordCollection dataRecordCollection =
			testGraphQLDataRecordCollection_addDataRecordCollection();

		JSONObject dataRecordCollectionJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField(
						"dataRecordCollectionByDataRecordCollectionKey",
						HashMapBuilder.<String, Object>put(
							"dataRecordCollectionKey",
							StringBundler.concat(
								StringPool.QUOTE,
								dataRecordCollection.
									getDataRecordCollectionKey(),
								StringPool.QUOTE)
						).put(
							"siteKey",
							StringBundler.concat(
								StringPool.QUOTE,
								dataRecordCollection.getSiteId(),
								StringPool.QUOTE)
						).build(),
						getGraphQLFields())),
				"JSONObject/data",
				"JSONObject/dataRecordCollectionByDataRecordCollectionKey");

		Assert.assertEquals(
			GetterUtil.getLong(dataRecordCollection.getDataDefinitionId()),
			dataRecordCollectionJSONObject.getLong("dataDefinitionId"));
		Assert.assertEquals(
			MapUtil.getString(dataRecordCollection.getName(), "en_US"),
			JSONUtil.getValue(
				dataRecordCollectionJSONObject, "JSONObject/name",
				"Object/en_US"));
	}

	@Override
	@Test
	public void testPostDataDefinitionDataRecordCollection() throws Exception {
		super.testPostDataDefinitionDataRecordCollection();

		assertHttpResponseStatusCode(
			404,
			dataRecordCollectionResource.
				postDataDefinitionDataRecordCollectionHttpResponse(
					0L, randomDataRecordCollection()));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"dataDefinitionId", "name"};
	}

	@Override
	protected DataRecordCollection randomDataRecordCollection() {
		return _createDataRecordCollection(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	@Override
	protected DataRecordCollection randomIrrelevantDataRecordCollection()
		throws Exception {

		DataRecordCollection randomIrrelevantDataRecordCollection =
			super.randomIrrelevantDataRecordCollection();

		randomIrrelevantDataRecordCollection.setDataDefinitionId(
			_irrelevantDDMStructure.getStructureId());

		return randomIrrelevantDataRecordCollection;
	}

	@Override
	protected DataRecordCollection
			testDeleteDataRecordCollection_addDataRecordCollection()
		throws Exception {

		return dataRecordCollectionResource.
			postDataDefinitionDataRecordCollection(
				_ddmStructure.getStructureId(), randomDataRecordCollection());
	}

	@Override
	protected DataRecordCollection
			testGetDataDefinitionDataRecordCollection_addDataRecordCollection()
		throws Exception {

		return dataRecordCollectionResource.
			postDataDefinitionDataRecordCollection(
				_ddmStructure.getStructureId(),
				_randomDataRecordCollection(_ddmStructure.getStructureKey()));
	}

	@Override
	protected Long
			testGetDataDefinitionDataRecordCollection_getDataDefinitionId(
				DataRecordCollection dataRecordCollection)
		throws Exception {

		return dataRecordCollection.getDataDefinitionId();
	}

	@Override
	protected Long
			testGetDataDefinitionDataRecordCollectionsPage_getDataDefinitionId()
		throws Exception {

		return _ddmStructure.getStructureId();
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetDataDefinitionDataRecordCollectionsPage_getExpectedActions(
				Long dataDefinitionId)
		throws Exception {

		return Collections.emptyMap();
	}

	@Override
	protected DataRecordCollection
			testGetDataRecordCollection_addDataRecordCollection()
		throws Exception {

		return dataRecordCollectionResource.
			postDataDefinitionDataRecordCollection(
				_ddmStructure.getStructureId(), randomDataRecordCollection());
	}

	@Override
	protected DataRecordCollection
			testGetSiteDataRecordCollectionByDataRecordCollectionKey_addDataRecordCollection()
		throws Exception {

		return dataRecordCollectionResource.
			postDataDefinitionDataRecordCollection(
				_ddmStructure.getStructureId(), randomDataRecordCollection());
	}

	@Override
	protected DataRecordCollection
			testGraphQLDataRecordCollection_addDataRecordCollection()
		throws Exception {

		return dataRecordCollectionResource.
			postDataDefinitionDataRecordCollection(
				_ddmStructure.getStructureId(), randomDataRecordCollection());
	}

	@Override
	protected Long
			testGraphQLPostDataDefinitionDataRecordCollection_getDataDefinitionId(
				DataRecordCollection dataRecordCollection)
		throws Exception {

		return _ddmStructure.getStructureId();
	}

	@Override
	protected DataRecordCollection
			testPostDataDefinitionDataRecordCollection_addDataRecordCollection(
				DataRecordCollection dataRecordCollection)
		throws Exception {

		return dataRecordCollectionResource.
			postDataDefinitionDataRecordCollection(
				dataRecordCollection.getDataDefinitionId(),
				dataRecordCollection);
	}

	@Override
	protected DataRecordCollection
			testPutDataRecordCollection_addDataRecordCollection()
		throws Exception {

		return dataRecordCollectionResource.
			postDataDefinitionDataRecordCollection(
				_ddmStructure.getStructureId(), randomDataRecordCollection());
	}

	@Override
	protected DataRecordCollection
			testPutDataRecordCollectionPermissionsPage_addDataRecordCollection()
		throws Exception {

		return dataRecordCollectionResource.
			postDataDefinitionDataRecordCollection(
				_ddmStructure.getStructureId(), randomDataRecordCollection());
	}

	private DataRecordCollection _createDataRecordCollection(
		String description, String name) {

		return _createDataRecordCollection(
			RandomTestUtil.randomString(), description, name);
	}

	private DataRecordCollection _createDataRecordCollection(
		String dataRecordCollectionKey, String description, String name) {

		DataRecordCollection dataRecordCollection = new DataRecordCollection() {
			{
				dataDefinitionId = _ddmStructure.getStructureId();
				siteId = testGroup.getGroupId();
			}
		};

		dataRecordCollection.setDataRecordCollectionKey(
			dataRecordCollectionKey);
		dataRecordCollection.setDescription(
			HashMapBuilder.<String, Object>put(
				"en_US", description
			).build());
		dataRecordCollection.setName(
			HashMapBuilder.<String, Object>put(
				"en_US", name
			).build());

		return dataRecordCollection;
	}

	private DataRecordCollection _randomDataRecordCollection(
		String dataRecordCollectionKey) {

		return _createDataRecordCollection(
			dataRecordCollectionKey, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
	}

	private void _testGetDataDefinitionDataRecordCollectionsPage(
			String description, String keywords, String name)
		throws Exception {

		Long dataDefinitionId =
			testGetDataDefinitionDataRecordCollectionsPage_getDataDefinitionId();

		DataRecordCollection dataRecordCollection =
			testGetDataDefinitionDataRecordCollectionsPage_addDataRecordCollection(
				dataDefinitionId,
				_createDataRecordCollection(description, name));

		Page<DataRecordCollection> page =
			dataRecordCollectionResource.
				getDataDefinitionDataRecordCollectionsPage(
					dataDefinitionId, keywords, Pagination.of(1, 2));

		Assert.assertEquals(1, page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(dataRecordCollection),
			(List<DataRecordCollection>)page.getItems());
		assertValid(page);

		dataRecordCollectionResource.deleteDataRecordCollection(
			dataRecordCollection.getId());
	}

	@Inject
	private static ResourceActions _resourceActions;

	private DDMStructure _ddmStructure;
	private DDMStructure _irrelevantDDMStructure;

}