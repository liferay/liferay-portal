/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.resource.v2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.data.engine.rest.client.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.client.dto.v2_0.DataListView;
import com.liferay.data.engine.rest.resource.v2_0.test.util.DataDefinitionTestUtil;
import com.liferay.data.engine.rest.resource.v2_0.test.util.content.type.test.util.ModelResourceActionTestUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;

import java.util.Collections;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jeyvison Nascimento
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class DataListViewResourceTest extends BaseDataListViewResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseDataListViewResourceTestCase.setUpClass();

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

		_dataDefinition = DataDefinitionTestUtil.addDataDefinition(
			testGroup.getGroupId());
		_irrelevantDataDefinition = DataDefinitionTestUtil.addDataDefinition(
			irrelevantGroup.getGroupId());
	}

	@Override
	@Test
	public void testGraphQLGetDataListView() throws Exception {
		DataListView dataListView = testGraphQLDataListView_addDataListView();

		JSONObject dataListViewJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"dataListView",
					HashMapBuilder.<String, Object>put(
						"dataListViewId", dataListView.getId()
					).build(),
					getGraphQLFields())),
			"JSONObject/data", "JSONObject/dataListView");

		Assert.assertEquals(
			GetterUtil.getLong(dataListView.getDataDefinitionId()),
			dataListViewJSONObject.getLong("dataDefinitionId"));
		Assert.assertEquals(
			GetterUtil.getString(
				ArrayUtil.getValue(dataListView.getFieldNames(), 0)),
			JSONUtil.getValueAsString(
				dataListViewJSONObject, "JSONArray/fieldNames", "Object/0"));
		Assert.assertEquals(
			GetterUtil.getString(dataListView.getSortField()),
			dataListViewJSONObject.getString("sortField"));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"dataDefinitionId", "fieldNames", "sortField"};
	}

	@Override
	protected DataListView randomDataListView() throws Exception {
		DataListView dataListView = super.randomDataListView();

		dataListView.setDataDefinitionId(_dataDefinition.getId());
		dataListView.setFieldNames(
			new String[] {RandomTestUtil.randomString()});

		return dataListView;
	}

	@Override
	protected DataListView
			testDeleteDataDefinitionDataListView_addDataListView()
		throws Exception {

		return dataListViewResource.postDataDefinitionDataListView(
			_dataDefinition.getId(), randomDataListView());
	}

	@Override
	protected Long testDeleteDataDefinitionDataListView_getDataDefinitionId(
		DataListView dataListView) {

		return dataListView.getDataDefinitionId();
	}

	@Override
	protected DataListView testDeleteDataListView_addDataListView()
		throws Exception {

		return dataListViewResource.postDataDefinitionDataListView(
			_dataDefinition.getId(), randomDataListView());
	}

	@Override
	protected Long testGetDataDefinitionDataListViewsPage_getDataDefinitionId()
		throws Exception {

		return _dataDefinition.getId();
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetDataDefinitionDataListViewsPage_getExpectedActions(
				Long dataDefinitionId)
		throws Exception {

		return Collections.emptyMap();
	}

	@Override
	protected Long
			testGetDataDefinitionDataListViewsPage_getIrrelevantDataDefinitionId()
		throws Exception {

		return _irrelevantDataDefinition.getId();
	}

	@Override
	protected DataListView testGetDataListView_addDataListView()
		throws Exception {

		return dataListViewResource.postDataDefinitionDataListView(
			_dataDefinition.getId(), randomDataListView());
	}

	@Override
	protected Long testGraphQLDataDefinitionDataListView_getDataDefinitionId()
		throws Exception {

		return _dataDefinition.getId();
	}

	@Override
	protected DataListView testGraphQLDataListView_addDataListView()
		throws Exception {

		return dataListViewResource.postDataDefinitionDataListView(
			_dataDefinition.getId(), randomDataListView());
	}

	@Override
	protected Long
		testGraphQLDeleteDataDefinitionDataListView_getDataDefinitionId(
			DataListView dataListView) {

		return dataListView.getDataDefinitionId();
	}

	@Override
	protected Long
			testGraphQLPostDataDefinitionDataListView_getDataDefinitionId(
				DataListView dataListView)
		throws Exception {

		return _dataDefinition.getId();
	}

	@Override
	protected DataListView testPutDataListView_addDataListView()
		throws Exception {

		return dataListViewResource.postDataDefinitionDataListView(
			_dataDefinition.getId(), randomDataListView());
	}

	@Inject
	private static ResourceActions _resourceActions;

	private DataDefinition _dataDefinition;
	private DataDefinition _irrelevantDataDefinition;

}