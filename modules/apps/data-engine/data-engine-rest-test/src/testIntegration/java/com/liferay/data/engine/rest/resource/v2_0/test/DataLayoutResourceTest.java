/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.resource.v2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.data.engine.rest.client.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.client.dto.v2_0.DataDefinitionField;
import com.liferay.data.engine.rest.client.dto.v2_0.DataLayout;
import com.liferay.data.engine.rest.client.dto.v2_0.DataLayoutColumn;
import com.liferay.data.engine.rest.client.dto.v2_0.DataLayoutPage;
import com.liferay.data.engine.rest.client.dto.v2_0.DataLayoutRenderingContext;
import com.liferay.data.engine.rest.client.dto.v2_0.DataLayoutRow;
import com.liferay.data.engine.rest.client.http.HttpInvoker;
import com.liferay.data.engine.rest.client.pagination.Page;
import com.liferay.data.engine.rest.client.pagination.Pagination;
import com.liferay.data.engine.rest.client.problem.Problem;
import com.liferay.data.engine.rest.client.resource.v2_0.DataDefinitionResource;
import com.liferay.data.engine.rest.client.resource.v2_0.DataLayoutResource;
import com.liferay.data.engine.rest.resource.v2_0.test.util.DataDefinitionTestUtil;
import com.liferay.data.engine.rest.resource.v2_0.test.util.DataLayoutTestUtil;
import com.liferay.data.engine.rest.resource.v2_0.test.util.content.type.test.util.ModelResourceActionTestUtil;
import com.liferay.data.engine.rest.strategy.util.DataRecordValueKeyUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marcelo Mello
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class DataLayoutResourceTest extends BaseDataLayoutResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseDataLayoutResourceTestCase.setUpClass();

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
	public void testGetDataDefinitionDataLayoutsPage() throws Exception {
		super.testGetDataDefinitionDataLayoutsPage();

		Page<DataLayout> page =
			dataLayoutResource.getDataDefinitionDataLayoutsPage(
				testGetDataDefinitionDataLayoutsPage_getDataDefinitionId(),
				"layout", Pagination.of(1, 2), null);

		Assert.assertEquals(0, page.getTotalCount());

		_testGetDataDefinitionDataLayoutsPage("FORM", "FoRmSLaYoUt");
		_testGetDataDefinitionDataLayoutsPage(
			"abcdefghijklmnopqrstuvwxyz0123456789",
			"abcdefghijklmnopqrstuvwxyz0123456789");
		_testGetDataDefinitionDataLayoutsPage("form layout", "form layout");
		_testGetDataDefinitionDataLayoutsPage("layo", "form layout");
	}

	@Override
	@Test
	public void testGraphQLGetDataLayout() throws Exception {
		DataLayout dataLayout = testGraphQLDataLayout_addDataLayout();

		JSONObject dataLayoutJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"dataLayout",
					HashMapBuilder.<String, Object>put(
						"dataLayoutId", dataLayout.getId()
					).build(),
					getGraphQLFields())),
			"JSONObject/data", "JSONObject/dataLayout");

		Assert.assertEquals(
			GetterUtil.getLong(dataLayout.getDataDefinitionId()),
			dataLayoutJSONObject.getLong("dataDefinitionId"));
		Assert.assertEquals(
			MapUtil.getString(dataLayout.getName(), "en_US"),
			JSONUtil.getValue(
				dataLayoutJSONObject, "JSONObject/name", "Object/en_US"));
	}

	@Override
	@Test
	public void testGraphQLGetSiteDataLayoutByContentTypeByDataLayoutKey()
		throws Exception {

		DataLayout dataLayout = testGraphQLDataLayout_addDataLayout();

		JSONObject dataLayoutJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"dataLayoutByContentTypeByDataLayoutKey",
					HashMapBuilder.<String, Object>put(
						"contentType", "\"test\""
					).put(
						"dataLayoutKey",
						StringBundler.concat(
							StringPool.QUOTE, dataLayout.getDataLayoutKey(),
							StringPool.QUOTE)
					).put(
						"siteKey",
						StringBundler.concat(
							StringPool.QUOTE, dataLayout.getSiteId(),
							StringPool.QUOTE)
					).build(),
					getGraphQLFields())),
			"JSONObject/data",
			"JSONObject/dataLayoutByContentTypeByDataLayoutKey");

		Assert.assertEquals(
			GetterUtil.getLong(dataLayout.getDataDefinitionId()),
			dataLayoutJSONObject.getLong("dataDefinitionId"));
		Assert.assertEquals(
			MapUtil.getString(dataLayout.getName(), "en_US"),
			JSONUtil.getValue(
				dataLayoutJSONObject, "JSONObject/name", "Object/en_US"));
	}

	@Override
	@Test
	public void testGraphQLGetSiteDataLayoutByContentTypeByDataLayoutKeyNotFound()
		throws Exception {

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"dataLayoutByContentTypeByDataLayoutKey",
						HashMapBuilder.<String, Object>put(
							"contentType", "\"test\""
						).put(
							"dataLayoutKey",
							"\"" + RandomTestUtil.randomString() + "\""
						).put(
							"siteKey",
							"\"" + irrelevantGroup.getGroupId() + "\""
						).build(),
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	@Override
	@Test
	public void testPostDataDefinitionDataLayout() throws Exception {
		super.testPostDataDefinitionDataLayout();

		// Data layout with data layout fields (visual property)

		DataLayout randomDataLayout = _randomDataLayout(true);

		DataLayout postDataLayout =
			testPostDataDefinitionDataLayout_addDataLayout(randomDataLayout);

		assertEquals(randomDataLayout, postDataLayout);
		assertValid(postDataLayout);
		Assert.assertTrue(
			equals(
				randomDataLayout.getDataLayoutFields(),
				postDataLayout.getDataLayoutFields()));

		// Multiple data layouts with the same data definition

		for (int i = 0; i < 3; i++) {
			randomDataLayout = randomDataLayout();

			postDataLayout = testPostDataDefinitionDataLayout_addDataLayout(
				randomDataLayout);

			assertEquals(randomDataLayout, postDataLayout);
			assertValid(postDataLayout);
		}

		// MustNotDuplicateFieldName

		DataDefinitionResource.Builder builder =
			DataDefinitionResource.builder();

		DataDefinitionResource dataDefinitionResource = builder.authentication(
			"test@liferay.com", TestPropsValues.USER_PASSWORD
		).build();

		DataDefinition dataDefinition =
			dataDefinitionResource.postSiteDataDefinitionByContentType(
				testGroup.getGroupId(), "test",
				new DataDefinition() {
					{
						availableLanguageIds = new String[] {"en_US", "pt_BR"};
						dataDefinitionFields = new DataDefinitionField[] {
							new DataDefinitionField() {
								{
									customProperties = Collections.singletonMap(
										"fieldReference", "fieldReference1");
									fieldType = "text";
									label = HashMapBuilder.<String, Object>put(
										"en_US", RandomTestUtil.randomString()
									).put(
										"pt_BR", RandomTestUtil.randomString()
									).build();
									name = "text1";
								}
							},
							new DataDefinitionField() {
								{
									customProperties = Collections.singletonMap(
										"fieldReference", "fieldReference2");
									fieldType = "text";
									label = HashMapBuilder.<String, Object>put(
										"en_US", RandomTestUtil.randomString()
									).put(
										"pt_BR", RandomTestUtil.randomString()
									).build();
									name = "text2";
								}
							}
						};
						dataDefinitionKey = RandomTestUtil.randomString();
						defaultLanguageId = "en_US";
						name = HashMapBuilder.<String, Object>put(
							"en_US", RandomTestUtil.randomString()
						).build();
					}
				});

		try {
			DataLayoutRow dataLayoutRow = new DataLayoutRow() {
				{
					dataLayoutColumns = new DataLayoutColumn[] {
						new DataLayoutColumn() {
							{
								columnSize = 12;
								fieldNames = new String[] {
									"text1", "text2", "text1"
								};
							}
						}
					};
				}
			};

			dataLayoutResource.postDataDefinitionDataLayout(
				dataDefinition.getId(),
				new DataLayout() {
					{
						dataDefinitionId = dataDefinition.getId();
						dataLayoutKey = RandomTestUtil.randomString();
						dataLayoutPages = new DataLayoutPage[] {
							new DataLayoutPage() {
								{
									dataLayoutRows = new DataLayoutRow[] {
										dataLayoutRow
									};
									description =
										HashMapBuilder.<String, Object>put(
											"en_US", "Page Description"
										).build();
									title = HashMapBuilder.<String, Object>put(
										"en_US", "Page Title"
									).build();
								}
							}
						};
						name = HashMapBuilder.<String, Object>put(
							"en_US", RandomTestUtil.randomString()
						).build();
						paginationMode = "wizard";
					}
				});

			Assert.fail("An exception must be thrown");
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("text1", problem.getDetail());
			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"DataLayoutValidationException.MustNotDuplicateFieldName",
				problem.getType());
		}
		finally {
			dataDefinitionResource.deleteDataDefinition(dataDefinition.getId());
		}
	}

	@Override
	@Test
	public void testPostDataLayoutContext() throws Exception {
		DataLayoutResource.Builder builder = DataLayoutResource.builder();

		dataLayoutResource = builder.authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.ENGLISH
		).build();

		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinitionWithDataLayout(
				testGroup.getGroupId());

		DataLayout dataLayout = dataDefinition.getDefaultDataLayout();

		HttpInvoker.HttpResponse httpResponse =
			dataLayoutResource.postDataLayoutContextHttpResponse(
				dataLayout.getId(),
				new DataLayoutRenderingContext() {
					{
						containerId = "testContainer";
						dataRecordValues = HashMapBuilder.<String, Object>put(
							DataRecordValueKeyUtil.createDataRecordValueKey(
								"Text", RandomTestUtil.randomString(),
								StringPool.BLANK, 0),
							HashMapBuilder.<String, Object>put(
								"en_US", "value"
							).put(
								"pt_BR", "valor"
							).build()
						).build();
						namespace = "myNamespace";
						pathThemeImages = StringUtil.randomString();
						readOnly = false;
						scopeGroupId = testGroup.getGroupId();
						siteGroupId = testGroup.getGroupId();
					}
				});

		String content = httpResponse.getContent();

		Assert.assertThat(content, CoreMatchers.containsString("myNamespace"));
		Assert.assertThat(
			content, CoreMatchers.containsString("testContainer"));
		Assert.assertThat(content, CoreMatchers.containsString("valor"));
		Assert.assertThat(content, CoreMatchers.containsString("value"));

		JSONObject contentJSONObject = JSONFactoryUtil.createJSONObject(
			content);

		Assert.assertEquals(
			LocaleUtil.ENGLISH.getLanguage(),
			contentJSONObject.getString("editingLanguageId"));

		Assert.assertEquals(200, httpResponse.getStatusCode());
	}

	@Override
	@Test
	public void testPutDataLayout() throws Exception {
		super.testPutDataLayout();

		DataLayout dataLayout = testPutDataLayout_addDataLayout();
		DataLayout randomDataLayout = _randomDataLayout(true);

		DataLayout putDataLayout = dataLayoutResource.putDataLayout(
			dataLayout.getId(), randomDataLayout);

		assertEquals(randomDataLayout, putDataLayout);
		assertValid(putDataLayout);
		Assert.assertTrue(
			equals(
				randomDataLayout.getDataLayoutFields(),
				putDataLayout.getDataLayoutFields()));

		dataLayout = dataLayoutResource.getDataLayout(dataLayout.getId());

		assertEquals(randomDataLayout, dataLayout);
		assertValid(dataLayout);
		Assert.assertTrue(
			equals(
				randomDataLayout.getDataLayoutFields(),
				dataLayout.getDataLayoutFields()));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"dataDefinitionId", "name", "paginationMode"};
	}

	@Override
	protected DataLayout randomDataLayout() {
		return DataLayoutTestUtil.createDataLayout(
			_dataDefinition.getId(), RandomTestUtil.randomString(),
			testGroup.getGroupId());
	}

	@Override
	protected DataLayout randomIrrelevantDataLayout() throws Exception {
		DataLayout randomIrrelevantDataLayout =
			super.randomIrrelevantDataLayout();

		randomIrrelevantDataLayout.setDataDefinitionId(
			_irrelevantDataDefinition.getId());

		return randomIrrelevantDataLayout;
	}

	@Override
	protected DataLayout testDeleteDataDefinitionDataLayout_addDataLayout()
		throws Exception {

		return dataLayoutResource.postDataDefinitionDataLayout(
			_dataDefinition.getId(), randomDataLayout());
	}

	@Override
	protected Long testDeleteDataDefinitionDataLayout_getDataDefinitionId(
		DataLayout dataLayout) {

		return dataLayout.getDataDefinitionId();
	}

	@Override
	protected DataLayout testDeleteDataLayout_addDataLayout() throws Exception {
		return dataLayoutResource.postDataDefinitionDataLayout(
			_dataDefinition.getId(), randomDataLayout());
	}

	@Override
	protected Long testGetDataDefinitionDataLayoutsPage_getDataDefinitionId()
		throws Exception {

		return _dataDefinition.getId();
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetDataDefinitionDataLayoutsPage_getExpectedActions(
				Long dataDefinitionId)
		throws Exception {

		return Collections.emptyMap();
	}

	@Override
	protected DataLayout testGetDataLayout_addDataLayout() throws Exception {
		return dataLayoutResource.postDataDefinitionDataLayout(
			_dataDefinition.getId(), randomDataLayout());
	}

	@Override
	protected DataLayout
			testGetSiteDataLayoutByContentTypeByDataLayoutKey_addDataLayout()
		throws Exception {

		DataLayout dataLayout = dataLayoutResource.postDataDefinitionDataLayout(
			_dataDefinition.getId(), randomDataLayout());

		dataLayout.setContentType("test");

		return dataLayout;
	}

	@Override
	protected Long testGraphQLDataDefinitionDataLayout_getDataDefinitionId()
		throws Exception {

		return _dataDefinition.getId();
	}

	@Override
	protected DataLayout testGraphQLDataLayout_addDataLayout()
		throws Exception {

		return dataLayoutResource.postDataDefinitionDataLayout(
			_dataDefinition.getId(), randomDataLayout());
	}

	@Override
	protected Long
			testGraphQLDeleteDataDefinitionDataLayout_getDataDefinitionId(
				DataLayout dataLayout)
		throws Exception {

		return _dataDefinition.getId();
	}

	@Override
	protected Long testGraphQLPostDataDefinitionDataLayout_getDataDefinitionId(
			DataLayout dataLayout)
		throws Exception {

		return _dataDefinition.getId();
	}

	@Override
	protected DataLayout testPutDataLayout_addDataLayout() throws Exception {
		return dataLayoutResource.postDataDefinitionDataLayout(
			_dataDefinition.getId(), randomDataLayout());
	}

	private DataLayout _randomDataLayout(boolean withVisualProperties) {
		DataLayout dataLayout = randomDataLayout();

		if (!withVisualProperties) {
			return dataLayout;
		}

		DataDefinitionField dataDefinitionField =
			_dataDefinition.getDataDefinitionFields()[0];

		dataLayout.setDataLayoutFields(
			HashMapBuilder.<String, Object>put(
				dataDefinitionField.getName(),
				HashMapBuilder.<String, Object>put(
					"label",
					HashMapBuilder.<String, Object>put(
						_dataDefinition.getDefaultLanguageId(),
						RandomTestUtil.randomString()
					).build()
				).put(
					"placeholder",
					HashMapBuilder.<String, Object>put(
						_dataDefinition.getDefaultLanguageId(),
						RandomTestUtil.randomString()
					).build()
				).put(
					"predefinedValue",
					HashMapBuilder.<String, Object>put(
						_dataDefinition.getDefaultLanguageId(),
						RandomTestUtil.randomString()
					).build()
				).put(
					"required", RandomTestUtil.randomBoolean()
				).put(
					"requiredErrorMessage",
					HashMapBuilder.<String, Object>put(
						_dataDefinition.getDefaultLanguageId(),
						RandomTestUtil.randomString()
					).build()
				).put(
					"showLabel", RandomTestUtil.randomBoolean()
				).put(
					"tip",
					HashMapBuilder.<String, Object>put(
						_dataDefinition.getDefaultLanguageId(),
						RandomTestUtil.randomString()
					).build()
				).build()
			).build());

		return dataLayout;
	}

	private void _testGetDataDefinitionDataLayoutsPage(
			String keywords, String name)
		throws Exception {

		Long dataDefinitionId =
			testGetDataDefinitionDataLayoutsPage_getDataDefinitionId();

		DataLayout dataLayout =
			testGetDataDefinitionDataLayoutsPage_addDataLayout(
				dataDefinitionId,
				DataLayoutTestUtil.createDataLayout(
					_dataDefinition.getId(), name, testGroup.getGroupId()));

		Page<DataLayout> page =
			dataLayoutResource.getDataDefinitionDataLayoutsPage(
				dataDefinitionId, keywords, Pagination.of(1, 2), null);

		Assert.assertEquals(1, page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(dataLayout), (List<DataLayout>)page.getItems());
		assertValid(page);

		dataLayoutResource.deleteDataLayout(dataLayout.getId());
	}

	@Inject
	private static ResourceActions _resourceActions;

	private DataDefinition _dataDefinition;
	private DataDefinition _irrelevantDataDefinition;

}