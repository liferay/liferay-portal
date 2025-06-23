/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.admin.site.client.dto.v1_0.PageRuleAction;
import com.liferay.headless.admin.site.client.http.HttpInvoker;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.resource.v1_0.PageRuleActionResource;
import com.liferay.headless.admin.site.client.serdes.v1_0.PageRuleActionSerDes;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.MultivaluedHashMap;

import java.lang.reflect.Method;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public abstract class BasePageRuleActionResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

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

		_pageRuleActionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		pageRuleActionResource = PageRuleActionResource.builder(
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

		PageRuleAction pageRuleAction1 = randomPageRuleAction();

		String json = objectMapper.writeValueAsString(pageRuleAction1);

		PageRuleAction pageRuleAction2 = PageRuleActionSerDes.toDTO(json);

		Assert.assertTrue(equals(pageRuleAction1, pageRuleAction2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PageRuleAction pageRuleAction = randomPageRuleAction();

		String json1 = objectMapper.writeValueAsString(pageRuleAction);
		String json2 = PageRuleActionSerDes.toJSON(pageRuleAction);

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

		PageRuleAction pageRuleAction = randomPageRuleAction();

		pageRuleAction.setAction(regex);
		pageRuleAction.setExternalReferenceCode(regex);
		pageRuleAction.setItemId(regex);
		pageRuleAction.setType(regex);

		String json = PageRuleActionSerDes.toJSON(pageRuleAction);

		Assert.assertFalse(json.contains(regex));

		pageRuleAction = PageRuleActionSerDes.toDTO(json);

		Assert.assertEquals(regex, pageRuleAction.getAction());
		Assert.assertEquals(regex, pageRuleAction.getExternalReferenceCode());
		Assert.assertEquals(regex, pageRuleAction.getItemId());
		Assert.assertEquals(regex, pageRuleAction.getType());
	}

	@Test
	public void testDeleteSiteSiteByExternalReferenceCodePageRuleAction()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PageRuleAction pageRuleAction =
			testDeleteSiteSiteByExternalReferenceCodePageRuleAction_addPageRuleAction();

		assertHttpResponseStatusCode(
			204,
			pageRuleActionResource.
				deleteSiteSiteByExternalReferenceCodePageRuleActionHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodePageRuleAction_getSiteExternalReferenceCode(),
					pageRuleAction.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			pageRuleActionResource.
				getSiteSiteByExternalReferenceCodePageRuleActionHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodePageRuleAction_getSiteExternalReferenceCode(),
					pageRuleAction.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			pageRuleActionResource.
				getSiteSiteByExternalReferenceCodePageRuleActionHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodePageRuleAction_getSiteExternalReferenceCode(),
					"-"));
	}

	protected PageRuleAction
			testDeleteSiteSiteByExternalReferenceCodePageRuleAction_addPageRuleAction()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteSiteSiteByExternalReferenceCodePageRuleAction_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodePageRuleAction()
		throws Exception {

		PageRuleAction postPageRuleAction =
			testGetSiteSiteByExternalReferenceCodePageRuleAction_addPageRuleAction();

		PageRuleAction getPageRuleAction =
			pageRuleActionResource.
				getSiteSiteByExternalReferenceCodePageRuleAction(
					testGetSiteSiteByExternalReferenceCodePageRuleAction_getSiteExternalReferenceCode(),
					postPageRuleAction.getExternalReferenceCode());

		assertEquals(postPageRuleAction, getPageRuleAction);
		assertValid(getPageRuleAction);
	}

	protected PageRuleAction
			testGetSiteSiteByExternalReferenceCodePageRuleAction_addPageRuleAction()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodePageRuleAction_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodePageRuleAction()
		throws Exception {

		PageRuleAction pageRuleAction =
			testGraphQLGetSiteSiteByExternalReferenceCodePageRuleAction_addPageRuleAction();

		// No namespace

		Assert.assertTrue(
			equals(
				pageRuleAction,
				PageRuleActionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"siteByExternalReferenceCodePageRuleAction",
								new HashMap<String, Object>() {
									{
										put(
											"siteExternalReferenceCode",
											"\"" +
												testGraphQLGetSiteSiteByExternalReferenceCodePageRuleAction_getSiteExternalReferenceCode() +
													"\"");
										put(
											"pageRuleActionExternalReferenceCode",
											"\"" +
												pageRuleAction.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/siteByExternalReferenceCodePageRuleAction"))));

		// Using the namespace headlessAdminSite_v1_0

		Assert.assertTrue(
			equals(
				pageRuleAction,
				PageRuleActionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminSite_v1_0",
								new GraphQLField(
									"siteByExternalReferenceCodePageRuleAction",
									new HashMap<String, Object>() {
										{
											put(
												"siteExternalReferenceCode",
												"\"" +
													testGraphQLGetSiteSiteByExternalReferenceCodePageRuleAction_getSiteExternalReferenceCode() +
														"\"");
											put(
												"pageRuleActionExternalReferenceCode",
												"\"" +
													pageRuleAction.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminSite_v1_0",
						"Object/siteByExternalReferenceCodePageRuleAction"))));
	}

	protected String
			testGraphQLGetSiteSiteByExternalReferenceCodePageRuleAction_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodePageRuleActionNotFound()
		throws Exception {

		String irrelevantPageRuleActionExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"siteByExternalReferenceCodePageRuleAction",
						new HashMap<String, Object>() {
							{
								put(
									"siteExternalReferenceCode",
									"\"" +
										irrelevantGroup.
											getExternalReferenceCode() + "\"");
								put(
									"pageRuleActionExternalReferenceCode",
									irrelevantPageRuleActionExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminSite_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminSite_v1_0",
						new GraphQLField(
							"siteByExternalReferenceCodePageRuleAction",
							new HashMap<String, Object>() {
								{
									put(
										"siteExternalReferenceCode",
										"\"" +
											irrelevantGroup.
												getExternalReferenceCode() +
													"\"");
									put(
										"pageRuleActionExternalReferenceCode",
										irrelevantPageRuleActionExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected PageRuleAction
			testGraphQLGetSiteSiteByExternalReferenceCodePageRuleAction_addPageRuleAction()
		throws Exception {

		return testGraphQLPageRuleAction_addPageRuleAction();
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodePageRulePageRuleActionsPage()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodePageRulePageRuleActionsPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodePageRulePageRuleActionsPage_getIrrelevantSiteExternalReferenceCode();
		String pageRuleExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodePageRulePageRuleActionsPage_getPageRuleExternalReferenceCode();
		String irrelevantPageRuleExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodePageRulePageRuleActionsPage_getIrrelevantPageRuleExternalReferenceCode();

		Page<PageRuleAction> page =
			pageRuleActionResource.
				getSiteSiteByExternalReferenceCodePageRulePageRuleActionsPage(
					siteExternalReferenceCode, pageRuleExternalReferenceCode,
					null);

		long totalCount = page.getTotalCount();

		if ((irrelevantSiteExternalReferenceCode != null) &&
			(irrelevantPageRuleExternalReferenceCode != null)) {

			PageRuleAction irrelevantPageRuleAction =
				testGetSiteSiteByExternalReferenceCodePageRulePageRuleActionsPage_addPageRuleAction(
					irrelevantSiteExternalReferenceCode,
					irrelevantPageRuleExternalReferenceCode,
					randomIrrelevantPageRuleAction());

			page =
				pageRuleActionResource.
					getSiteSiteByExternalReferenceCodePageRulePageRuleActionsPage(
						irrelevantSiteExternalReferenceCode,
						irrelevantPageRuleExternalReferenceCode, null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPageRuleAction,
				(List<PageRuleAction>)page.getItems());
			assertValid(
				page,
				testGetSiteSiteByExternalReferenceCodePageRulePageRuleActionsPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode,
					irrelevantPageRuleExternalReferenceCode));
		}

		PageRuleAction pageRuleAction1 =
			testGetSiteSiteByExternalReferenceCodePageRulePageRuleActionsPage_addPageRuleAction(
				siteExternalReferenceCode, pageRuleExternalReferenceCode,
				randomPageRuleAction());

		PageRuleAction pageRuleAction2 =
			testGetSiteSiteByExternalReferenceCodePageRulePageRuleActionsPage_addPageRuleAction(
				siteExternalReferenceCode, pageRuleExternalReferenceCode,
				randomPageRuleAction());

		page =
			pageRuleActionResource.
				getSiteSiteByExternalReferenceCodePageRulePageRuleActionsPage(
					siteExternalReferenceCode, pageRuleExternalReferenceCode,
					null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(pageRuleAction1, (List<PageRuleAction>)page.getItems());
		assertContains(pageRuleAction2, (List<PageRuleAction>)page.getItems());
		assertValid(
			page,
			testGetSiteSiteByExternalReferenceCodePageRulePageRuleActionsPage_getExpectedActions(
				siteExternalReferenceCode, pageRuleExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetSiteSiteByExternalReferenceCodePageRulePageRuleActionsPage_getExpectedActions(
				String siteExternalReferenceCode,
				String pageRuleExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected PageRuleAction
			testGetSiteSiteByExternalReferenceCodePageRulePageRuleActionsPage_addPageRuleAction(
				String siteExternalReferenceCode,
				String pageRuleExternalReferenceCode,
				PageRuleAction pageRuleAction)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodePageRulePageRuleActionsPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteSiteByExternalReferenceCodePageRulePageRuleActionsPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteSiteByExternalReferenceCodePageRulePageRuleActionsPage_getPageRuleExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodePageRulePageRuleActionsPage_getIrrelevantPageRuleExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testPatchSiteSiteByExternalReferenceCodePageRuleAction()
		throws Exception {

		PageRuleAction postPageRuleAction =
			testPatchSiteSiteByExternalReferenceCodePageRuleAction_addPageRuleAction();

		PageRuleAction randomPatchPageRuleAction = randomPatchPageRuleAction();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PageRuleAction patchPageRuleAction =
			pageRuleActionResource.
				patchSiteSiteByExternalReferenceCodePageRuleAction(
					null, postPageRuleAction.getExternalReferenceCode(),
					randomPatchPageRuleAction);

		PageRuleAction expectedPatchPageRuleAction = postPageRuleAction.clone();

		BeanTestUtil.copyProperties(
			randomPatchPageRuleAction, expectedPatchPageRuleAction);

		PageRuleAction getPageRuleAction =
			pageRuleActionResource.
				getSiteSiteByExternalReferenceCodePageRuleAction(
					null, patchPageRuleAction.getExternalReferenceCode());

		assertEquals(expectedPatchPageRuleAction, getPageRuleAction);
		assertValid(getPageRuleAction);
	}

	protected PageRuleAction
			testPatchSiteSiteByExternalReferenceCodePageRuleAction_addPageRuleAction()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSiteSiteByExternalReferenceCodePageRulePageRuleAction()
		throws Exception {

		PageRuleAction randomPageRuleAction = randomPageRuleAction();

		PageRuleAction postPageRuleAction =
			testPostSiteSiteByExternalReferenceCodePageRulePageRuleAction_addPageRuleAction(
				randomPageRuleAction);

		assertEquals(randomPageRuleAction, postPageRuleAction);
		assertValid(postPageRuleAction);
	}

	protected PageRuleAction
			testPostSiteSiteByExternalReferenceCodePageRulePageRuleAction_addPageRuleAction(
				PageRuleAction pageRuleAction)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteSiteByExternalReferenceCodePageRuleAction()
		throws Exception {

		PageRuleAction postPageRuleAction =
			testPutSiteSiteByExternalReferenceCodePageRuleAction_addPageRuleAction();

		PageRuleAction randomPageRuleAction = randomPageRuleAction();

		PageRuleAction putPageRuleAction =
			pageRuleActionResource.
				putSiteSiteByExternalReferenceCodePageRuleAction(
					testPutSiteSiteByExternalReferenceCodePageRuleAction_getSiteExternalReferenceCode(),
					postPageRuleAction.getExternalReferenceCode(),
					randomPageRuleAction);

		assertEquals(randomPageRuleAction, putPageRuleAction);
		assertValid(putPageRuleAction);

		PageRuleAction getPageRuleAction =
			pageRuleActionResource.
				getSiteSiteByExternalReferenceCodePageRuleAction(
					testPutSiteSiteByExternalReferenceCodePageRuleAction_getSiteExternalReferenceCode(),
					putPageRuleAction.getExternalReferenceCode());

		assertEquals(randomPageRuleAction, getPageRuleAction);
		assertValid(getPageRuleAction);
	}

	protected PageRuleAction
			testPutSiteSiteByExternalReferenceCodePageRuleAction_addPageRuleAction()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPutSiteSiteByExternalReferenceCodePageRuleAction_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected PageRuleAction testGraphQLPageRuleAction_addPageRuleAction()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		PageRuleAction pageRuleAction, List<PageRuleAction> pageRuleActions) {

		boolean contains = false;

		for (PageRuleAction item : pageRuleActions) {
			if (equals(pageRuleAction, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			pageRuleActions + " does not contain " + pageRuleAction, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PageRuleAction pageRuleAction1, PageRuleAction pageRuleAction2) {

		Assert.assertTrue(
			pageRuleAction1 + " does not equal " + pageRuleAction2,
			equals(pageRuleAction1, pageRuleAction2));
	}

	protected void assertEquals(
		List<PageRuleAction> pageRuleActions1,
		List<PageRuleAction> pageRuleActions2) {

		Assert.assertEquals(pageRuleActions1.size(), pageRuleActions2.size());

		for (int i = 0; i < pageRuleActions1.size(); i++) {
			PageRuleAction pageRuleAction1 = pageRuleActions1.get(i);
			PageRuleAction pageRuleAction2 = pageRuleActions2.get(i);

			assertEquals(pageRuleAction1, pageRuleAction2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<PageRuleAction> pageRuleActions1,
		List<PageRuleAction> pageRuleActions2) {

		Assert.assertEquals(pageRuleActions1.size(), pageRuleActions2.size());

		for (PageRuleAction pageRuleAction1 : pageRuleActions1) {
			boolean contains = false;

			for (PageRuleAction pageRuleAction2 : pageRuleActions2) {
				if (equals(pageRuleAction1, pageRuleAction2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				pageRuleActions2 + " does not contain " + pageRuleAction1,
				contains);
		}
	}

	protected void assertValid(PageRuleAction pageRuleAction) throws Exception {
		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("action", additionalAssertFieldName)) {
				if (pageRuleAction.getAction() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (pageRuleAction.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("itemId", additionalAssertFieldName)) {
				if (pageRuleAction.getItemId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (pageRuleAction.getType() == null) {
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

	protected void assertValid(Page<PageRuleAction> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PageRuleAction> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PageRuleAction> pageRuleActions = page.getItems();

		int size = pageRuleActions.size();

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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.admin.site.dto.v1_0.PageRuleAction.
						class)) {

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

	protected boolean equals(
		PageRuleAction pageRuleAction1, PageRuleAction pageRuleAction2) {

		if (pageRuleAction1 == pageRuleAction2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("action", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageRuleAction1.getAction(),
						pageRuleAction2.getAction())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						pageRuleAction1.getExternalReferenceCode(),
						pageRuleAction2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("itemId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageRuleAction1.getItemId(),
						pageRuleAction2.getItemId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageRuleAction1.getType(), pageRuleAction2.getType())) {

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

		if (!(_pageRuleActionResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_pageRuleActionResource;

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
		EntityField entityField, String operator,
		PageRuleAction pageRuleAction) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("action")) {
			Object object = pageRuleAction.getAction();

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
			Object object = pageRuleAction.getExternalReferenceCode();

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

		if (entityFieldName.equals("itemId")) {
			Object object = pageRuleAction.getItemId();

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

		if (entityFieldName.equals("type")) {
			Object object = pageRuleAction.getType();

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

	protected PageRuleAction randomPageRuleAction() throws Exception {
		return new PageRuleAction() {
			{
				action = StringUtil.toLowerCase(RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				itemId = StringUtil.toLowerCase(RandomTestUtil.randomString());
				type = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected PageRuleAction randomIrrelevantPageRuleAction() throws Exception {
		PageRuleAction randomIrrelevantPageRuleAction = randomPageRuleAction();

		return randomIrrelevantPageRuleAction;
	}

	protected PageRuleAction randomPatchPageRuleAction() throws Exception {
		return randomPageRuleAction();
	}

	protected PageRuleActionResource pageRuleActionResource;
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
		LogFactoryUtil.getLog(BasePageRuleActionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.site.resource.v1_0.PageRuleActionResource
		_pageRuleActionResource;

}