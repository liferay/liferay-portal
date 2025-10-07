/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.headless.delivery.client.dto.v1_0.Field;
import com.liferay.headless.delivery.client.dto.v1_0.WikiNode;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.permission.Permission;
import com.liferay.headless.delivery.client.resource.v1_0.WikiNodeResource;
import com.liferay.headless.delivery.client.serdes.v1_0.WikiNodeSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegate;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegateBuilderRegistry;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.Method;

import java.net.URI;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseWikiNodeResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_wikiNodeResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		wikiNodeResource = WikiNodeResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(irrelevantGroup);
		GroupTestUtil.deleteGroup(testGroup);
	}

	@Test
	public void testClientSerDesToDTO() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		WikiNode wikiNode1 = randomWikiNode();

		String json = objectMapper.writeValueAsString(wikiNode1);

		WikiNode wikiNode2 = WikiNodeSerDes.toDTO(json);

		Assert.assertTrue(equals(wikiNode1, wikiNode2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		WikiNode wikiNode = randomWikiNode();

		String json1 = objectMapper.writeValueAsString(wikiNode);
		String json2 = WikiNodeSerDes.toJSON(wikiNode);

		Assert.assertEquals(
			objectMapper.readTree(json1), objectMapper.readTree(json2));
	}

	protected ObjectMapper getClientSerDesObjectMapper() {
		return new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				enable(SerializationFeature.INDENT_OUTPUT);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};
	}

	@Test
	public void testEscapeRegexInStringFields() throws Exception {
		String regex = "^[0-9]+(\\.[0-9]{1,2})\"?";

		WikiNode wikiNode = randomWikiNode();

		wikiNode.setDescription(regex);
		wikiNode.setExternalReferenceCode(regex);
		wikiNode.setName(regex);

		String json = WikiNodeSerDes.toJSON(wikiNode);

		Assert.assertFalse(json.contains(regex));

		wikiNode = WikiNodeSerDes.toDTO(json);

		Assert.assertEquals(regex, wikiNode.getDescription());
		Assert.assertEquals(regex, wikiNode.getExternalReferenceCode());
		Assert.assertEquals(regex, wikiNode.getName());
	}

	@Test
	public void testDeleteSiteWikiNodeByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiNode wikiNode =
			testDeleteSiteWikiNodeByExternalReferenceCode_addWikiNode();

		assertHttpResponseStatusCode(
			204,
			wikiNodeResource.
				deleteSiteWikiNodeByExternalReferenceCodeHttpResponse(
					wikiNode.getSiteId(), wikiNode.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			wikiNodeResource.getSiteWikiNodeByExternalReferenceCodeHttpResponse(
				wikiNode.getSiteId(), wikiNode.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			wikiNodeResource.getSiteWikiNodeByExternalReferenceCodeHttpResponse(
				wikiNode.getSiteId(), "-"));
	}

	protected WikiNode
			testDeleteSiteWikiNodeByExternalReferenceCode_addWikiNode()
		throws Exception {

		return wikiNodeResource.postSiteWikiNode(
			testGroup.getGroupId(), randomWikiNode());
	}

	@Test
	public void testGraphQLDeleteSiteWikiNodeByExternalReferenceCode()
		throws Exception {

		// No namespace

		WikiNode wikiNode1 =
			testGraphQLDeleteSiteWikiNodeByExternalReferenceCode_addWikiNode();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteWikiNodeByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + wikiNode1.getSiteId() + "\"");
								put(
									"externalReferenceCode",
									"\"" +
										wikiNode1.getExternalReferenceCode() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteWikiNodeByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"wikiNodeByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put("siteKey", "\"" + wikiNode1.getSiteId() + "\"");
							put(
								"externalReferenceCode",
								"\"" + wikiNode1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		WikiNode wikiNode2 =
			testGraphQLDeleteSiteWikiNodeByExternalReferenceCode_addWikiNode();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteSiteWikiNodeByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + wikiNode2.getSiteId() + "\"");
									put(
										"externalReferenceCode",
										"\"" +
											wikiNode2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteSiteWikiNodeByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"wikiNodeByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + wikiNode2.getSiteId() + "\"");
								put(
									"externalReferenceCode",
									"\"" +
										wikiNode2.getExternalReferenceCode() +
											"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected WikiNode
			testGraphQLDeleteSiteWikiNodeByExternalReferenceCode_addWikiNode()
		throws Exception {

		return testGraphQLSiteWikiNode_addWikiNode();
	}

	@Test
	public void testDeleteWikiNode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiNode wikiNode = testDeleteWikiNode_addWikiNode();

		assertHttpResponseStatusCode(
			204, wikiNodeResource.deleteWikiNodeHttpResponse(wikiNode.getId()));

		assertHttpResponseStatusCode(
			404, wikiNodeResource.getWikiNodeHttpResponse(wikiNode.getId()));
		assertHttpResponseStatusCode(
			404, wikiNodeResource.getWikiNodeHttpResponse(0L));
	}

	protected WikiNode testDeleteWikiNode_addWikiNode() throws Exception {
		return wikiNodeResource.postSiteWikiNode(
			testGroup.getGroupId(), randomWikiNode());
	}

	@Test
	public void testGraphQLDeleteWikiNode() throws Exception {

		// No namespace

		WikiNode wikiNode1 = testGraphQLDeleteWikiNode_addWikiNode();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteWikiNode",
						new HashMap<String, Object>() {
							{
								put("wikiNodeId", wikiNode1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteWikiNode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"wikiNode",
					new HashMap<String, Object>() {
						{
							put("wikiNodeId", wikiNode1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		WikiNode wikiNode2 = testGraphQLDeleteWikiNode_addWikiNode();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteWikiNode",
							new HashMap<String, Object>() {
								{
									put("wikiNodeId", wikiNode2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteWikiNode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"wikiNode",
						new HashMap<String, Object>() {
							{
								put("wikiNodeId", wikiNode2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected WikiNode testGraphQLDeleteWikiNode_addWikiNode()
		throws Exception {

		return testGraphQLWikiNode_addWikiNode();
	}

	@Test
	public void testDeleteWikiNodeBatch() throws Exception {
		WikiNode wikiNode1 = testDeleteWikiNodeBatch_addWikiNode();

		testDeleteWikiNodeBatch_deleteWikiNode(202, null, wikiNode1.getId());

		assertHttpResponseStatusCode(
			404, wikiNodeResource.getWikiNodeHttpResponse(wikiNode1.getId()));
	}

	protected WikiNode testDeleteWikiNodeBatch_addWikiNode() throws Exception {
		return testDeleteWikiNode_addWikiNode();
	}

	protected void testDeleteWikiNodeBatch_deleteWikiNode(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			wikiNodeResource.deleteWikiNodeBatchHttpResponse(
				null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"id", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetSiteWikiNodeByExternalReferenceCode() throws Exception {
		WikiNode postWikiNode =
			testGetSiteWikiNodeByExternalReferenceCode_addWikiNode();

		WikiNode getWikiNode =
			wikiNodeResource.getSiteWikiNodeByExternalReferenceCode(
				postWikiNode.getSiteId(),
				postWikiNode.getExternalReferenceCode());

		assertEquals(postWikiNode, getWikiNode);
		assertValid(getWikiNode);
	}

	protected WikiNode testGetSiteWikiNodeByExternalReferenceCode_addWikiNode()
		throws Exception {

		return wikiNodeResource.postSiteWikiNode(
			testGroup.getGroupId(), randomWikiNode());
	}

	@Test
	public void testGraphQLGetSiteWikiNodeByExternalReferenceCode()
		throws Exception {

		WikiNode wikiNode =
			testGraphQLGetSiteWikiNodeByExternalReferenceCode_addWikiNode();

		// No namespace

		Assert.assertTrue(
			equals(
				wikiNode,
				WikiNodeSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"wikiNodeByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" + wikiNode.getSiteId() + "\"");
										put(
											"externalReferenceCode",
											"\"" +
												wikiNode.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/wikiNodeByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				wikiNode,
				WikiNodeSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"wikiNodeByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" + wikiNode.getSiteId() +
													"\"");
											put(
												"externalReferenceCode",
												"\"" +
													wikiNode.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/wikiNodeByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSiteWikiNodeByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"wikiNodeByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"wikiNodeByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected WikiNode
			testGraphQLGetSiteWikiNodeByExternalReferenceCode_addWikiNode()
		throws Exception {

		return testGraphQLSiteWikiNode_addWikiNode();
	}

	@Test
	public void testGetSiteWikiNodePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiNode postWikiNode =
			testGetSiteWikiNodePermissionsPage_addWikiNode();

		Page<Permission> page = wikiNodeResource.getSiteWikiNodePermissionsPage(
			testGroup.getGroupId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected WikiNode testGetSiteWikiNodePermissionsPage_addWikiNode()
		throws Exception {

		return wikiNodeResource.postSiteWikiNode(
			testGroup.getGroupId(), randomWikiNode());
	}

	@Test
	public void testGraphQLGetSiteWikiNodePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiNode postWikiNode =
			testGraphQLGetSiteWikiNodePermissionsPage_addWikiNode();

		GraphQLField graphQLField = new GraphQLField(
			"siteWikiNodePermissions",
			new HashMap<String, Object>() {
				{
					put("siteKey", "\"" + postWikiNode.getSiteId() + "\"");
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject siteWikiNodePermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/siteWikiNodePermissions");

		Assert.assertNotNull(siteWikiNodePermissionsJSONObject);
	}

	protected WikiNode testGraphQLGetSiteWikiNodePermissionsPage_addWikiNode()
		throws Exception {

		return testGraphQLSiteWikiNode_addWikiNode();
	}

	@Test
	public void testGetSiteWikiNodesPage() throws Exception {
		Long siteId = testGetSiteWikiNodesPage_getSiteId();
		Long irrelevantSiteId = testGetSiteWikiNodesPage_getIrrelevantSiteId();

		Page<WikiNode> page = wikiNodeResource.getSiteWikiNodesPage(
			siteId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			WikiNode irrelevantWikiNode = testGetSiteWikiNodesPage_addWikiNode(
				irrelevantSiteId, randomIrrelevantWikiNode());

			page = wikiNodeResource.getSiteWikiNodesPage(
				irrelevantSiteId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantWikiNode, (List<WikiNode>)page.getItems());
			assertValid(
				page,
				testGetSiteWikiNodesPage_getExpectedActions(irrelevantSiteId));
		}

		WikiNode wikiNode1 = testGetSiteWikiNodesPage_addWikiNode(
			siteId, randomWikiNode());

		WikiNode wikiNode2 = testGetSiteWikiNodesPage_addWikiNode(
			siteId, randomWikiNode());

		page = wikiNodeResource.getSiteWikiNodesPage(
			siteId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(wikiNode1, (List<WikiNode>)page.getItems());
		assertContains(wikiNode2, (List<WikiNode>)page.getItems());
		assertValid(page, testGetSiteWikiNodesPage_getExpectedActions(siteId));

		wikiNodeResource.deleteWikiNode(wikiNode1.getId());

		wikiNodeResource.deleteWikiNode(wikiNode2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteWikiNodesPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/sites/{siteId}/wiki-nodes/batch".
				replace("{siteId}", String.valueOf(siteId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteWikiNodesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteWikiNodesPage_getSiteId();

		WikiNode wikiNode1 = randomWikiNode();

		wikiNode1 = testGetSiteWikiNodesPage_addWikiNode(siteId, wikiNode1);

		for (EntityField entityField : entityFields) {
			Page<WikiNode> page = wikiNodeResource.getSiteWikiNodesPage(
				siteId, null, null,
				getFilterString(entityField, "between", wikiNode1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(wikiNode1),
				(List<WikiNode>)page.getItems());
		}
	}

	@Test
	public void testGetSiteWikiNodesPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteWikiNodesPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteWikiNodesPageWithFilterStringContains()
		throws Exception {

		testGetSiteWikiNodesPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteWikiNodesPageWithFilterStringEquals()
		throws Exception {

		testGetSiteWikiNodesPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteWikiNodesPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteWikiNodesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteWikiNodesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteWikiNodesPage_getSiteId();

		WikiNode wikiNode1 = testGetSiteWikiNodesPage_addWikiNode(
			siteId, randomWikiNode());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiNode wikiNode2 = testGetSiteWikiNodesPage_addWikiNode(
			siteId, randomWikiNode());

		for (EntityField entityField : entityFields) {
			Page<WikiNode> page = wikiNodeResource.getSiteWikiNodesPage(
				siteId, null, null,
				getFilterString(entityField, operator, wikiNode1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(wikiNode1),
				(List<WikiNode>)page.getItems());
		}
	}

	@Test
	public void testGetSiteWikiNodesPageWithPagination() throws Exception {
		Long siteId = testGetSiteWikiNodesPage_getSiteId();

		Page<WikiNode> wikiNodesPage = wikiNodeResource.getSiteWikiNodesPage(
			siteId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(wikiNodesPage.getTotalCount());

		WikiNode wikiNode1 = testGetSiteWikiNodesPage_addWikiNode(
			siteId, randomWikiNode());

		WikiNode wikiNode2 = testGetSiteWikiNodesPage_addWikiNode(
			siteId, randomWikiNode());

		WikiNode wikiNode3 = testGetSiteWikiNodesPage_addWikiNode(
			siteId, randomWikiNode());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WikiNode> page1 = wikiNodeResource.getSiteWikiNodesPage(
				siteId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(wikiNode1, (List<WikiNode>)page1.getItems());

			Page<WikiNode> page2 = wikiNodeResource.getSiteWikiNodesPage(
				siteId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(wikiNode2, (List<WikiNode>)page2.getItems());

			Page<WikiNode> page3 = wikiNodeResource.getSiteWikiNodesPage(
				siteId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(wikiNode3, (List<WikiNode>)page3.getItems());
		}
		else {
			Page<WikiNode> page1 = wikiNodeResource.getSiteWikiNodesPage(
				siteId, null, null, null, Pagination.of(1, totalCount + 2),
				null);

			List<WikiNode> wikiNodes1 = (List<WikiNode>)page1.getItems();

			Assert.assertEquals(
				wikiNodes1.toString(), totalCount + 2, wikiNodes1.size());

			Page<WikiNode> page2 = wikiNodeResource.getSiteWikiNodesPage(
				siteId, null, null, null, Pagination.of(2, totalCount + 2),
				null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WikiNode> wikiNodes2 = (List<WikiNode>)page2.getItems();

			Assert.assertEquals(wikiNodes2.toString(), 1, wikiNodes2.size());

			Page<WikiNode> page3 = wikiNodeResource.getSiteWikiNodesPage(
				siteId, null, null, null, Pagination.of(1, (int)totalCount + 3),
				null);

			assertContains(wikiNode1, (List<WikiNode>)page3.getItems());
			assertContains(wikiNode2, (List<WikiNode>)page3.getItems());
			assertContains(wikiNode3, (List<WikiNode>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteWikiNodesPageWithSortDateTime() throws Exception {
		testGetSiteWikiNodesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, wikiNode1, wikiNode2) -> {
				BeanTestUtil.setProperty(
					wikiNode1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteWikiNodesPageWithSortDouble() throws Exception {
		testGetSiteWikiNodesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, wikiNode1, wikiNode2) -> {
				BeanTestUtil.setProperty(wikiNode1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(wikiNode2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteWikiNodesPageWithSortInteger() throws Exception {
		testGetSiteWikiNodesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, wikiNode1, wikiNode2) -> {
				BeanTestUtil.setProperty(wikiNode1, entityField.getName(), 0);
				BeanTestUtil.setProperty(wikiNode2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteWikiNodesPageWithSortString() throws Exception {
		testGetSiteWikiNodesPageWithSort(
			EntityField.Type.STRING,
			(entityField, wikiNode1, wikiNode2) -> {
				Class<?> clazz = wikiNode1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						wikiNode1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						wikiNode2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						wikiNode1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						wikiNode2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						wikiNode1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						wikiNode2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteWikiNodesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, WikiNode, WikiNode, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteWikiNodesPage_getSiteId();

		WikiNode wikiNode1 = randomWikiNode();
		WikiNode wikiNode2 = randomWikiNode();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, wikiNode1, wikiNode2);
		}

		wikiNode1 = testGetSiteWikiNodesPage_addWikiNode(siteId, wikiNode1);

		wikiNode2 = testGetSiteWikiNodesPage_addWikiNode(siteId, wikiNode2);

		Page<WikiNode> page = wikiNodeResource.getSiteWikiNodesPage(
			siteId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<WikiNode> ascPage = wikiNodeResource.getSiteWikiNodesPage(
				siteId, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(wikiNode1, (List<WikiNode>)ascPage.getItems());
			assertContains(wikiNode2, (List<WikiNode>)ascPage.getItems());

			Page<WikiNode> descPage = wikiNodeResource.getSiteWikiNodesPage(
				siteId, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(wikiNode2, (List<WikiNode>)descPage.getItems());
			assertContains(wikiNode1, (List<WikiNode>)descPage.getItems());
		}
	}

	protected WikiNode testGetSiteWikiNodesPage_addWikiNode(
			Long siteId, WikiNode wikiNode)
		throws Exception {

		return wikiNodeResource.postSiteWikiNode(siteId, wikiNode);
	}

	protected Long testGetSiteWikiNodesPage_getSiteId() throws Exception {
		return testGroup.getGroupId();
	}

	protected Long testGetSiteWikiNodesPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteWikiNodesPage() throws Exception {
		Long siteId = testGetSiteWikiNodesPage_getSiteId();

		GraphQLField graphQLField = new GraphQLField(
			"wikiNodes",
			new HashMap<String, Object>() {
				{
					put("siteKey", "\"" + siteId + "\"");
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject wikiNodesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/wikiNodes");

		long totalCount = wikiNodesJSONObject.getLong("totalCount");

		WikiNode wikiNode1 = testGraphQLSiteWikiNode_addWikiNode(
			siteId, randomWikiNode());

		WikiNode wikiNode2 = testGraphQLSiteWikiNode_addWikiNode(
			siteId, randomWikiNode());

		wikiNodesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/wikiNodes");

		Assert.assertEquals(
			totalCount + 2, wikiNodesJSONObject.getLong("totalCount"));

		assertContains(
			wikiNode1,
			Arrays.asList(
				WikiNodeSerDes.toDTOs(wikiNodesJSONObject.getString("items"))));
		assertContains(
			wikiNode2,
			Arrays.asList(
				WikiNodeSerDes.toDTOs(wikiNodesJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		wikiNodesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/wikiNodes");

		Assert.assertEquals(
			totalCount + 2, wikiNodesJSONObject.getLong("totalCount"));

		assertContains(
			wikiNode1,
			Arrays.asList(
				WikiNodeSerDes.toDTOs(wikiNodesJSONObject.getString("items"))));
		assertContains(
			wikiNode2,
			Arrays.asList(
				WikiNodeSerDes.toDTOs(wikiNodesJSONObject.getString("items"))));
	}

	@Test
	public void testGetWikiNode() throws Exception {
		WikiNode postWikiNode = testGetWikiNode_addWikiNode();

		WikiNode getWikiNode = wikiNodeResource.getWikiNode(
			postWikiNode.getId());

		assertEquals(postWikiNode, getWikiNode);
		assertValid(getWikiNode);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		WikiNode postWikiNode = testGetWikiNode_addWikiNode();

		WikiNode getWikiNode = wikiNodeResource.getWikiNode(
			postWikiNode.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany, "com.liferay.headless.delivery.dto.v1_0.WikiNode"
			).acceptLanguage(
				new AcceptLanguage() {

					@Override
					public List<Locale> getLocales() {
						return Arrays.asList(LocaleUtil.getDefault());
					}

					@Override
					public String getPreferredLanguageId() {
						return LocaleUtil.toLanguageId(LocaleUtil.getDefault());
					}

					@Override
					public Locale getPreferredLocale() {
						return LocaleUtil.getDefault();
					}

				}
			).groupLocalService(
				_groupLocalService
			).httpServletRequest(
				testVulcanCRUDItemDelegate_getHttpServletRequest()
			).httpServletResponse(
				new MockHttpServletResponse()
			).resourceActionLocalService(
				_resourceActionLocalService
			).resourcePermissionLocalService(
				_resourcePermissionLocalService
			).roleLocalService(
				_roleLocalService
			).scopeChecker(
				_scopeChecker
			).uriInfo(
				testVulcanCRUDItemDelegate_getUriInfo()
			).user(
				testVulcanCRUDItemDelegate_getUser()
			).build();

		Object item = vulcanCRUDItemDelegate.getItem(postWikiNode.getId());

		assertEquals(getWikiNode, WikiNodeSerDes.toDTO(item.toString()));
	}

	protected HttpServletRequest
		testVulcanCRUDItemDelegate_getHttpServletRequest() {

		return new MockHttpServletRequest() {

			@Override
			public StringBuffer getRequestURL() {
				return new StringBuffer(
					StringBundler.concat(
						"http://localhost:8080/o/v1.0/",
						RandomTestUtil.randomString(), "/",
						RandomTestUtil.randomString()));
			}

		};
	}

	protected UriInfo testVulcanCRUDItemDelegate_getUriInfo() {
		String applicationPath = RandomTestUtil.randomString() + "/";
		String resourcePath = RandomTestUtil.randomString();

		return new UriInfo() {

			@Override
			public String getPath() {
				return resourcePath;
			}

			@Override
			public String getPath(boolean decode) {
				return getPath();
			}

			@Override
			public List<PathSegment> getPathSegments() {
				return Collections.emptyList();
			}

			@Override
			public List<PathSegment> getPathSegments(boolean decode) {
				return getPathSegments();
			}

			@Override
			public URI getRequestUri() {
				return URI.create(
					"http://localhost:8080/o/" + applicationPath +
						resourcePath);
			}

			@Override
			public UriBuilder getRequestUriBuilder() {
				return UriBuilder.fromUri(getRequestUri());
			}

			@Override
			public URI getAbsolutePath() {
				return getRequestUri();
			}

			@Override
			public UriBuilder getAbsolutePathBuilder() {
				return getRequestUriBuilder();
			}

			@Override
			public URI getBaseUri() {
				return URI.create("http://localhost:8080/o/" + applicationPath);
			}

			@Override
			public UriBuilder getBaseUriBuilder() {
				return UriBuilder.fromUri(getBaseUri());
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters() {
				return new MultivaluedHashMap<>();
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters(
				boolean decode) {

				return getPathParameters();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters() {
				return new MultivaluedHashMap<>();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters(
				boolean decode) {

				return getQueryParameters();
			}

			@Override
			public List<String> getMatchedURIs() {
				return Collections.emptyList();
			}

			@Override
			public List<String> getMatchedURIs(boolean decode) {
				return getMatchedURIs();
			}

			@Override
			public List<Object> getMatchedResources() {
				return Collections.emptyList();
			}

			@Override
			public URI resolve(URI requestUri) {
				return getBaseUri().resolve(requestUri);
			}

			@Override
			public URI relativize(URI uri) {
				return getBaseUri().relativize(uri);
			}

		};
	}

	protected com.liferay.portal.kernel.model.User
		testVulcanCRUDItemDelegate_getUser() {

		return _testCompanyAdminUser;
	}

	protected WikiNode testGetWikiNode_addWikiNode() throws Exception {
		return wikiNodeResource.postSiteWikiNode(
			testGroup.getGroupId(), randomWikiNode());
	}

	@Test
	public void testGraphQLGetWikiNode() throws Exception {
		WikiNode wikiNode = testGraphQLGetWikiNode_addWikiNode();

		// No namespace

		Assert.assertTrue(
			equals(
				wikiNode,
				WikiNodeSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"wikiNode",
								new HashMap<String, Object>() {
									{
										put("wikiNodeId", wikiNode.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/wikiNode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				wikiNode,
				WikiNodeSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"wikiNode",
									new HashMap<String, Object>() {
										{
											put("wikiNodeId", wikiNode.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/wikiNode"))));
	}

	@Test
	public void testGraphQLGetWikiNodeNotFound() throws Exception {
		Long irrelevantWikiNodeId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"wikiNode",
						new HashMap<String, Object>() {
							{
								put("wikiNodeId", irrelevantWikiNodeId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"wikiNode",
							new HashMap<String, Object>() {
								{
									put("wikiNodeId", irrelevantWikiNodeId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected WikiNode testGraphQLGetWikiNode_addWikiNode() throws Exception {
		return testGraphQLWikiNode_addWikiNode();
	}

	@Test
	public void testGetWikiNodePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiNode postWikiNode = testGetWikiNodePermissionsPage_addWikiNode();

		Page<Permission> page = wikiNodeResource.getWikiNodePermissionsPage(
			postWikiNode.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected WikiNode testGetWikiNodePermissionsPage_addWikiNode()
		throws Exception {

		return wikiNodeResource.postSiteWikiNode(
			testGroup.getGroupId(), randomWikiNode());
	}

	@Test
	public void testGraphQLGetWikiNodePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiNode postWikiNode =
			testGraphQLGetWikiNodePermissionsPage_addWikiNode();

		GraphQLField graphQLField = new GraphQLField(
			"wikiNodePermissions",
			new HashMap<String, Object>() {
				{
					put("wikiNodeId", postWikiNode.getId());
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject wikiNodePermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/wikiNodePermissions");

		Assert.assertNotNull(wikiNodePermissionsJSONObject);
	}

	protected WikiNode testGraphQLGetWikiNodePermissionsPage_addWikiNode()
		throws Exception {

		return testGraphQLWikiNode_addWikiNode();
	}

	@Test
	public void testPostSiteWikiNode() throws Exception {
		WikiNode randomWikiNode = randomWikiNode();

		WikiNode postWikiNode = testPostSiteWikiNode_addWikiNode(
			randomWikiNode);

		assertEquals(randomWikiNode, postWikiNode);
		assertValid(postWikiNode);
	}

	protected WikiNode testPostSiteWikiNode_addWikiNode(WikiNode wikiNode)
		throws Exception {

		return wikiNodeResource.postSiteWikiNode(
			testGetSiteWikiNodesPage_getSiteId(), wikiNode);
	}

	@Test
	public void testGraphQLPostSiteWikiNode() throws Exception {
		WikiNode randomWikiNode = randomWikiNode();

		WikiNode wikiNode = testGraphQLSiteWikiNode_addWikiNode(
			testGroup.getGroupId(), randomWikiNode);

		Assert.assertTrue(equals(randomWikiNode, wikiNode));
	}

	@Test
	public void testPutSiteWikiNodeByExternalReferenceCode() throws Exception {
		WikiNode postWikiNode =
			testPutSiteWikiNodeByExternalReferenceCode_addWikiNode();

		WikiNode randomWikiNode = randomWikiNode();

		WikiNode putWikiNode =
			wikiNodeResource.putSiteWikiNodeByExternalReferenceCode(
				postWikiNode.getSiteId(),
				postWikiNode.getExternalReferenceCode(), randomWikiNode);

		assertEquals(randomWikiNode, putWikiNode);
		assertValid(putWikiNode);

		WikiNode getWikiNode =
			wikiNodeResource.getSiteWikiNodeByExternalReferenceCode(
				putWikiNode.getSiteId(),
				putWikiNode.getExternalReferenceCode());

		assertEquals(randomWikiNode, getWikiNode);
		assertValid(getWikiNode);

		WikiNode newWikiNode =
			testPutSiteWikiNodeByExternalReferenceCode_createWikiNode();

		putWikiNode = wikiNodeResource.putSiteWikiNodeByExternalReferenceCode(
			newWikiNode.getSiteId(), newWikiNode.getExternalReferenceCode(),
			newWikiNode);

		assertEquals(newWikiNode, putWikiNode);
		assertValid(putWikiNode);

		getWikiNode = wikiNodeResource.getSiteWikiNodeByExternalReferenceCode(
			putWikiNode.getSiteId(), putWikiNode.getExternalReferenceCode());

		assertEquals(newWikiNode, getWikiNode);

		Assert.assertEquals(
			newWikiNode.getExternalReferenceCode(),
			putWikiNode.getExternalReferenceCode());
	}

	protected WikiNode testPutSiteWikiNodeByExternalReferenceCode_addWikiNode()
		throws Exception {

		return wikiNodeResource.postSiteWikiNode(
			testGroup.getGroupId(), randomWikiNode());
	}

	protected WikiNode
			testPutSiteWikiNodeByExternalReferenceCode_createWikiNode()
		throws Exception {

		return randomWikiNode();
	}

	@Test
	public void testPutSiteWikiNodePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiNode wikiNode = testPutSiteWikiNodePermissionsPage_addWikiNode();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			wikiNodeResource.putSiteWikiNodePermissionsPageHttpResponse(
				testGroup.getGroupId(),
				new Permission[] {
					new Permission() {
						{
							setActionIds(new String[] {"PERMISSIONS"});
							setRoleName(role.getName());
						}
					}
				}));

		assertHttpResponseStatusCode(
			404,
			wikiNodeResource.putSiteWikiNodePermissionsPageHttpResponse(
				testGroup.getGroupId(),
				new Permission[] {
					new Permission() {
						{
							setActionIds(new String[] {"-"});
							setRoleName("-");
						}
					}
				}));
	}

	protected WikiNode testPutSiteWikiNodePermissionsPage_addWikiNode()
		throws Exception {

		return wikiNodeResource.postSiteWikiNode(
			testGroup.getGroupId(), randomWikiNode());
	}

	@Test
	public void testPutWikiNode() throws Exception {
		WikiNode postWikiNode = testPutWikiNode_addWikiNode();

		WikiNode randomWikiNode = randomWikiNode();

		WikiNode putWikiNode = wikiNodeResource.putWikiNode(
			postWikiNode.getId(), randomWikiNode);

		assertEquals(randomWikiNode, putWikiNode);
		assertValid(putWikiNode);

		WikiNode getWikiNode = wikiNodeResource.getWikiNode(
			putWikiNode.getId());

		assertEquals(randomWikiNode, getWikiNode);
		assertValid(getWikiNode);
	}

	protected WikiNode testPutWikiNode_addWikiNode() throws Exception {
		return wikiNodeResource.postSiteWikiNode(
			testGroup.getGroupId(), randomWikiNode());
	}

	@Test
	public void testPutWikiNodePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiNode wikiNode = testPutWikiNodePermissionsPage_addWikiNode();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			wikiNodeResource.putWikiNodePermissionsPageHttpResponse(
				wikiNode.getId(),
				new Permission[] {
					new Permission() {
						{
							setActionIds(new String[] {"VIEW"});
							setRoleName(role.getName());
						}
					}
				}));

		assertHttpResponseStatusCode(
			404,
			wikiNodeResource.putWikiNodePermissionsPageHttpResponse(
				0L,
				new Permission[] {
					new Permission() {
						{
							setActionIds(new String[] {"-"});
							setRoleName("-");
						}
					}
				}));
	}

	protected WikiNode testPutWikiNodePermissionsPage_addWikiNode()
		throws Exception {

		return wikiNodeResource.postSiteWikiNode(
			testGroup.getGroupId(), randomWikiNode());
	}

	@Test
	public void testPutWikiNodeSubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiNode wikiNode = testPutWikiNodeSubscribe_addWikiNode();

		assertHttpResponseStatusCode(
			204,
			wikiNodeResource.putWikiNodeSubscribeHttpResponse(
				wikiNode.getId()));

		assertHttpResponseStatusCode(
			404, wikiNodeResource.putWikiNodeSubscribeHttpResponse(0L));
	}

	protected WikiNode testPutWikiNodeSubscribe_addWikiNode() throws Exception {
		return wikiNodeResource.postSiteWikiNode(
			testGroup.getGroupId(), randomWikiNode());
	}

	@Test
	public void testPutWikiNodeUnsubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiNode wikiNode = testPutWikiNodeUnsubscribe_addWikiNode();

		assertHttpResponseStatusCode(
			204,
			wikiNodeResource.putWikiNodeUnsubscribeHttpResponse(
				wikiNode.getId()));

		assertHttpResponseStatusCode(
			404, wikiNodeResource.putWikiNodeUnsubscribeHttpResponse(0L));
	}

	protected WikiNode testPutWikiNodeUnsubscribe_addWikiNode()
		throws Exception {

		return wikiNodeResource.postSiteWikiNode(
			testGroup.getGroupId(), randomWikiNode());
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		WikiNode wikiNode1 = testBatchEngineDeleteImportTask_addWikiNode();

		testBatchEngineDeleteImportTask_deleteWikiNode(
			200, null, wikiNode1.getId());

		assertHttpResponseStatusCode(
			404, wikiNodeResource.getWikiNodeHttpResponse(wikiNode1.getId()));
	}

	protected WikiNode testBatchEngineDeleteImportTask_addWikiNode()
		throws Exception {

		return testDeleteWikiNode_addWikiNode();
	}

	protected void testBatchEngineDeleteImportTask_deleteWikiNode(
			int expectedStatusCode, String externalReferenceCode, Long id,
			String... parameters)
		throws Exception {

		ImportTaskResource importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).parameters(
			parameters
		).build();

		HttpResponse httpResponse =
			importTaskResource.deleteImportTaskHttpResponse(
				"com.liferay.headless.delivery.dto.v1_0.WikiNode", null, null,
				null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"id", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected WikiNode testGraphQLSiteWikiNode_addWikiNode() throws Exception {
		return testGraphQLSiteWikiNode_addWikiNode(
			testGroup.getGroupId(), randomWikiNode());
	}

	protected WikiNode testGraphQLSiteWikiNode_addWikiNode(
			Long siteId, WikiNode wikiNode)
		throws Exception {

		JSONDeserializer<WikiNode> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(WikiNode.class)) {

			if (getGraphQLValue(field.get(wikiNode)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(wikiNode)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteWikiNode",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("wikiNode", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteWikiNode"),
			WikiNode.class);
	}

	protected WikiNode testGraphQLWikiNode_addWikiNode() throws Exception {
		return testGraphQLWikiNode_addWikiNode(
			testGroup.getGroupId(), randomWikiNode());
	}

	protected WikiNode testGraphQLWikiNode_addWikiNode(
			Long siteId, WikiNode wikiNode)
		throws Exception {

		JSONDeserializer<WikiNode> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(WikiNode.class)) {

			if (getGraphQLValue(field.get(wikiNode)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(wikiNode)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteWikiNode",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("wikiNode", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteWikiNode"),
			WikiNode.class);
	}

	protected String getGraphQLValue(Object value) throws Exception {
		if (value == null) {
			return null;
		}
		else if (value instanceof Boolean || value instanceof Number) {
			return value.toString();
		}
		else if (value instanceof Date date) {
			return "\"" +
				DateUtil.getDate(
					date, "yyyy-MM-dd'T'HH:mm:ss'Z'", LocaleUtil.getDefault(),
					TimeZone.getTimeZone("UTC")) + "\"";
		}
		else if (value instanceof Enum<?> enm) {
			return enm.name();
		}
		else if (value instanceof Map<?, ?> map) {
			List<String> entries = new ArrayList<>();

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				String graphQLValue = getGraphQLValue(entry.getValue());

				if (graphQLValue != null) {
					entries.add(entry.getKey() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
		else if (value instanceof Object[] array) {
			List<String> entries = new ArrayList<>();

			for (Object entry : array) {
				String graphQLValue = getGraphQLValue(entry);

				if (graphQLValue != null) {
					entries.add(graphQLValue);
				}
			}

			return "[" + String.join(", ", entries) + "]";
		}
		else if (value instanceof String) {
			return "\"" + value + "\"";
		}
		else {
			List<String> entries = new ArrayList<>();

			Class<?> clazz = value.getClass();
			java.lang.reflect.Field[] declaredFields = getDeclaredFields(clazz);

			if (declaredFields.length == 0) {
				declaredFields = getDeclaredFields(clazz.getSuperclass());
			}

			for (java.lang.reflect.Field field : declaredFields) {
				String graphQLValue = getGraphQLValue(field.get(value));

				if (graphQLValue != null) {
					entries.add(field.getName() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
	}

	protected void assertContains(WikiNode wikiNode, List<WikiNode> wikiNodes) {
		boolean contains = false;

		for (WikiNode item : wikiNodes) {
			if (equals(wikiNode, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			wikiNodes + " does not contain " + wikiNode, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(WikiNode wikiNode1, WikiNode wikiNode2) {
		Assert.assertTrue(
			wikiNode1 + " does not equal " + wikiNode2,
			equals(wikiNode1, wikiNode2));
	}

	protected void assertEquals(
		List<WikiNode> wikiNodes1, List<WikiNode> wikiNodes2) {

		Assert.assertEquals(wikiNodes1.size(), wikiNodes2.size());

		for (int i = 0; i < wikiNodes1.size(); i++) {
			WikiNode wikiNode1 = wikiNodes1.get(i);
			WikiNode wikiNode2 = wikiNodes2.get(i);

			assertEquals(wikiNode1, wikiNode2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<WikiNode> wikiNodes1, List<WikiNode> wikiNodes2) {

		Assert.assertEquals(wikiNodes1.size(), wikiNodes2.size());

		for (WikiNode wikiNode1 : wikiNodes1) {
			boolean contains = false;

			for (WikiNode wikiNode2 : wikiNodes2) {
				if (equals(wikiNode1, wikiNode2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				wikiNodes2 + " does not contain " + wikiNode1, contains);
		}
	}

	protected void assertValid(WikiNode wikiNode) throws Exception {
		boolean valid = true;

		if (wikiNode.getDateCreated() == null) {
			valid = false;
		}

		if (wikiNode.getDateModified() == null) {
			valid = false;
		}

		if (wikiNode.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(wikiNode.getSiteId(), testGroup.getGroupId())) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (wikiNode.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (wikiNode.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (wikiNode.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (wikiNode.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (wikiNode.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfWikiPages", additionalAssertFieldName)) {

				if (wikiNode.getNumberOfWikiPages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("subscribed", additionalAssertFieldName)) {
				if (wikiNode.getSubscribed() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (wikiNode.getViewableBy() == null) {
					valid = false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	protected void assertValid(Page<WikiNode> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<WikiNode> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<WikiNode> wikiNodes = page.getItems();

		int size = wikiNodes.size();

		if ((page.getLastPage() > 0) && (page.getPage() > 0) &&
			(page.getPageSize() > 0) && (page.getTotalCount() > 0) &&
			(size > 0)) {

			valid = true;
		}

		Assert.assertTrue(valid);

		assertValid(page.getActions(), expectedActions);
	}

	protected void assertValid(
		Map<String, Map<String, String>> actions1,
		Map<String, Map<String, String>> actions2) {

		for (String key : actions2.keySet()) {
			Map action = actions1.get(key);

			Assert.assertNotNull(key + " does not contain an action", action);

			Map<String, String> expectedAction = actions2.get(key);

			Assert.assertEquals(
				expectedAction.get("method"), action.get("method"));
			Assert.assertEquals(expectedAction.get("href"), action.get("href"));
		}
	}

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		graphQLFields.add(new GraphQLField("id"));

		graphQLFields.add(new GraphQLField("siteId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.delivery.dto.v1_0.WikiNode.class)) {

			if (!ArrayUtil.contains(
					getAdditionalAssertFieldNames(), field.getName())) {

				continue;
			}

			graphQLFields.addAll(getGraphQLFields(field));
		}

		return graphQLFields;
	}

	protected List<GraphQLField> getGraphQLFields(
			java.lang.reflect.Field... fields)
		throws Exception {

		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (java.lang.reflect.Field field : fields) {
			com.liferay.portal.vulcan.graphql.annotation.GraphQLField
				vulcanGraphQLField = field.getAnnotation(
					com.liferay.portal.vulcan.graphql.annotation.GraphQLField.
						class);

			if (vulcanGraphQLField != null) {
				Class<?> clazz = field.getType();

				if (clazz.isArray()) {
					clazz = clazz.getComponentType();
				}

				List<GraphQLField> childrenGraphQLFields = getGraphQLFields(
					getDeclaredFields(clazz));

				graphQLFields.add(
					new GraphQLField(field.getName(), childrenGraphQLFields));
			}
		}

		return graphQLFields;
	}

	protected String[] getIgnoredEntityFieldNames() {
		return new String[0];
	}

	protected boolean equals(WikiNode wikiNode1, WikiNode wikiNode2) {
		if (wikiNode1 == wikiNode2) {
			return true;
		}

		if (!Objects.equals(wikiNode1.getSiteId(), wikiNode2.getSiteId())) {
			return false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)wikiNode1.getActions(),
						(Map)wikiNode2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiNode1.getCreator(), wikiNode2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiNode1.getDateCreated(),
						wikiNode2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiNode1.getDateModified(),
						wikiNode2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiNode1.getDescription(),
						wikiNode2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						wikiNode1.getExternalReferenceCode(),
						wikiNode2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(wikiNode1.getId(), wikiNode2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiNode1.getName(), wikiNode2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfWikiPages", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						wikiNode1.getNumberOfWikiPages(),
						wikiNode2.getNumberOfWikiPages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("subscribed", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiNode1.getSubscribed(), wikiNode2.getSubscribed())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiNode1.getViewableBy(), wikiNode2.getViewableBy())) {

					return false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		return true;
	}

	protected boolean equals(
		Map<String, Object> map1, Map<String, Object> map2) {

		if (Objects.equals(map1.keySet(), map2.keySet())) {
			for (Map.Entry<String, Object> entry : map1.entrySet()) {
				if (entry.getValue() instanceof Map) {
					if (!equals(
							(Map)entry.getValue(),
							(Map)map2.get(entry.getKey()))) {

						return false;
					}
				}
				else if (!Objects.deepEquals(
							entry.getValue(), map2.get(entry.getKey()))) {

					return false;
				}
			}

			return true;
		}

		return false;
	}

	protected java.lang.reflect.Field[] getDeclaredFields(Class clazz)
		throws Exception {

		if (clazz.getClassLoader() == null) {
			return new java.lang.reflect.Field[0];
		}

		return TransformUtil.transform(
			ReflectionUtil.getDeclaredFields(clazz),
			field -> {
				if (field.isSynthetic()) {
					return null;
				}

				return field;
			},
			java.lang.reflect.Field.class);
	}

	protected java.util.Collection<EntityField> getEntityFields()
		throws Exception {

		if (!(_wikiNodeResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_wikiNodeResource;

		EntityModel entityModel = entityModelResource.getEntityModel(
			new MultivaluedHashMap());

		if (entityModel == null) {
			return Collections.emptyList();
		}

		Map<String, EntityField> entityFieldsMap =
			entityModel.getEntityFieldsMap();

		return entityFieldsMap.values();
	}

	protected List<EntityField> getEntityFields(EntityField.Type type)
		throws Exception {

		return TransformUtil.transform(
			getEntityFields(),
			entityField -> {
				if (!Objects.equals(entityField.getType(), type) ||
					ArrayUtil.contains(
						getIgnoredEntityFieldNames(), entityField.getName())) {

					return null;
				}

				return entityField;
			});
	}

	protected String getFilterString(
		EntityField entityField, String operator, WikiNode wikiNode) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = wikiNode.getDateCreated();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_format.format(wikiNode.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = wikiNode.getDateModified();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_format.format(wikiNode.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = wikiNode.getDescription();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = wikiNode.getExternalReferenceCode();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = wikiNode.getName();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("numberOfWikiPages")) {
			sb.append(String.valueOf(wikiNode.getNumberOfWikiPages()));

			return sb.toString();
		}

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("subscribed")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("viewableBy")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		throw new IllegalArgumentException(
			"Invalid entity field " + entityFieldName);
	}

	protected String invoke(String query) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(
			JSONUtil.put(
				"query", query
			).toString(),
			"application/json");
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
		httpInvoker.path("http://localhost:8080/o/graphql");
		httpInvoker.userNameAndPassword(
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	protected JSONObject invokeGraphQLMutation(GraphQLField graphQLField)
		throws Exception {

		GraphQLField mutationGraphQLField = new GraphQLField(
			"mutation", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(mutationGraphQLField.toString()));
	}

	protected JSONObject invokeGraphQLQuery(GraphQLField graphQLField)
		throws Exception {

		GraphQLField queryGraphQLField = new GraphQLField(
			"query", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(queryGraphQLField.toString()));
	}

	protected WikiNode randomWikiNode() throws Exception {
		return new WikiNode() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				numberOfWikiPages = RandomTestUtil.randomInt();
				siteId = testGroup.getGroupId();
				subscribed = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected WikiNode randomIrrelevantWikiNode() throws Exception {
		WikiNode randomIrrelevantWikiNode = randomWikiNode();

		randomIrrelevantWikiNode.setSiteId(irrelevantGroup.getGroupId());

		return randomIrrelevantWikiNode;
	}

	protected WikiNode randomPatchWikiNode() throws Exception {
		return randomWikiNode();
	}

	protected final JSONObject waitForFinish(
			String expectedExecuteStatus, JSONObject jsonObject)
		throws Exception {

		while (true) {
			ImportTask importTask = importTaskResource.getImportTask(
				jsonObject.getLong("id"));

			ImportTask.ExecuteStatus executeStatus =
				importTask.getExecuteStatus();

			if (StringUtil.equals(executeStatus.getValue(), "COMPLETED") ||
				StringUtil.equals(executeStatus.getValue(), "FAILED")) {

				Assert.assertEquals(
					expectedExecuteStatus, executeStatus.getValue());

				return jsonObject;
			}
		}
	}

	protected WikiNodeResource wikiNodeResource;
	protected ImportTaskResource importTaskResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
	protected com.liferay.portal.kernel.model.Group testGroup;

	protected static class BeanTestUtil {

		public static void copyProperties(Object source, Object target)
			throws Exception {

			Class<?> sourceClass = source.getClass();

			Class<?> targetClass = target.getClass();

			for (java.lang.reflect.Field field :
					_getAllDeclaredFields(sourceClass)) {

				if (field.isSynthetic()) {
					continue;
				}

				Method getMethod = _getMethod(
					sourceClass, field.getName(), "get");

				try {
					Method setMethod = _getMethod(
						targetClass, field.getName(), "set",
						getMethod.getReturnType());

					setMethod.invoke(target, getMethod.invoke(source));
				}
				catch (Exception e) {
					continue;
				}
			}
		}

		public static boolean hasProperty(Object bean, String name) {
			Method setMethod = _getMethod(
				bean.getClass(), "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod != null) {
				return true;
			}

			return false;
		}

		public static void setProperty(Object bean, String name, Object value)
			throws Exception {

			Class<?> clazz = bean.getClass();

			Method setMethod = _getMethod(
				clazz, "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod == null) {
				throw new NoSuchMethodException();
			}

			Class<?>[] parameterTypes = setMethod.getParameterTypes();

			setMethod.invoke(bean, _translateValue(parameterTypes[0], value));
		}

		private static List<java.lang.reflect.Field> _getAllDeclaredFields(
			Class<?> clazz) {

			List<java.lang.reflect.Field> fields = new ArrayList<>();

			while ((clazz != null) && (clazz != Object.class)) {
				for (java.lang.reflect.Field field :
						clazz.getDeclaredFields()) {

					fields.add(field);
				}

				clazz = clazz.getSuperclass();
			}

			return fields;
		}

		private static Method _getMethod(Class<?> clazz, String name) {
			for (Method method : clazz.getMethods()) {
				if (name.equals(method.getName()) &&
					(method.getParameterCount() == 1) &&
					_parameterTypes.contains(method.getParameterTypes()[0])) {

					return method;
				}
			}

			return null;
		}

		private static Method _getMethod(
				Class<?> clazz, String fieldName, String prefix,
				Class<?>... parameterTypes)
			throws Exception {

			return clazz.getMethod(
				prefix + StringUtil.upperCaseFirstLetter(fieldName),
				parameterTypes);
		}

		private static Object _translateValue(
			Class<?> parameterType, Object value) {

			if ((value instanceof Integer) &&
				parameterType.equals(Long.class)) {

				Integer intValue = (Integer)value;

				return intValue.longValue();
			}

			return value;
		}

		private static final Set<Class<?>> _parameterTypes = new HashSet<>(
			Arrays.asList(
				Boolean.class, Date.class, Double.class, Integer.class,
				Long.class, Map.class, String.class));

	}

	protected class GraphQLField {

		public GraphQLField(String key, GraphQLField... graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(String key, List<GraphQLField> graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			GraphQLField... graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = Arrays.asList(graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			List<GraphQLField> graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = graphQLFields;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(_key);

			if (!_parameterMap.isEmpty()) {
				sb.append("(");

				for (Map.Entry<String, Object> entry :
						_parameterMap.entrySet()) {

					sb.append(entry.getKey());
					sb.append(": ");
					sb.append(entry.getValue());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append(")");
			}

			if (!_graphQLFields.isEmpty()) {
				sb.append("{");

				for (GraphQLField graphQLField : _graphQLFields) {
					sb.append(graphQLField.toString());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append("}");
			}

			return sb.toString();
		}

		private final List<GraphQLField> _graphQLFields;
		private final String _key;
		private final Map<String, Object> _parameterMap;

	}

	private static final com.liferay.portal.kernel.log.Log _log =
		LogFactoryUtil.getLog(BaseWikiNodeResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.delivery.resource.v1_0.WikiNodeResource
		_wikiNodeResource;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private ScopeChecker _scopeChecker;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private VulcanCRUDItemDelegateBuilderRegistry
		_vulcanCRUDItemDelegateBuilderRegistry;

}