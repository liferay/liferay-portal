/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Option;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.OptionResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.OptionSerDes;
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
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
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
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public abstract class BaseOptionResourceTestCase {

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

		_optionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		optionResource = OptionResource.builder(
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

		Option option1 = randomOption();

		String json = objectMapper.writeValueAsString(option1);

		Option option2 = OptionSerDes.toDTO(json);

		Assert.assertTrue(equals(option1, option2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Option option = randomOption();

		String json1 = objectMapper.writeValueAsString(option);
		String json2 = OptionSerDes.toJSON(option);

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

		Option option = randomOption();

		option.setExternalReferenceCode(regex);
		option.setKey(regex);

		String json = OptionSerDes.toJSON(option);

		Assert.assertFalse(json.contains(regex));

		option = OptionSerDes.toDTO(json);

		Assert.assertEquals(regex, option.getExternalReferenceCode());
		Assert.assertEquals(regex, option.getKey());
	}

	@Test
	public void testDeleteOption() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Option option = testDeleteOption_addOption();

		assertHttpResponseStatusCode(
			204, optionResource.deleteOptionHttpResponse(option.getId()));

		assertHttpResponseStatusCode(
			404, optionResource.getOptionHttpResponse(option.getId()));
		assertHttpResponseStatusCode(
			404, optionResource.getOptionHttpResponse(0L));
	}

	protected Option testDeleteOption_addOption() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOption() throws Exception {

		// No namespace

		Option option1 = testGraphQLDeleteOption_addOption();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOption",
						new HashMap<String, Object>() {
							{
								put("id", option1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteOption"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"option",
					new HashMap<String, Object>() {
						{
							put("id", option1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Option option2 = testGraphQLDeleteOption_addOption();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteOption",
							new HashMap<String, Object>() {
								{
									put("id", option2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteOption"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"option",
						new HashMap<String, Object>() {
							{
								put("id", option2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Option testGraphQLDeleteOption_addOption() throws Exception {
		return testGraphQLOption_addOption();
	}

	@Test
	public void testDeleteOptionBatch() throws Exception {
		Option option1 = testDeleteOptionBatch_addOption();

		testDeleteOptionBatch_deleteOption(
			202, option1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, optionResource.getOptionHttpResponse(option1.getId()));

		option1 = testDeleteOptionBatch_addOption();

		testDeleteOptionBatch_deleteOption(202, null, option1.getId());

		assertHttpResponseStatusCode(
			404, optionResource.getOptionHttpResponse(option1.getId()));

		option1 = testDeleteOptionBatch_addOption();
		Option option2 = testDeleteOptionBatch_addOption();

		testDeleteOptionBatch_deleteOption(
			202, option2.getExternalReferenceCode(), option1.getId());

		assertHttpResponseStatusCode(
			404, optionResource.getOptionHttpResponse(option1.getId()));
		assertHttpResponseStatusCode(
			200, optionResource.getOptionHttpResponse(option2.getId()));

		testDeleteOptionBatch_deleteOption(
			202, option2.getExternalReferenceCode(), option1.getId());

		assertHttpResponseStatusCode(
			404, optionResource.getOptionHttpResponse(option2.getId()));
	}

	protected Option testDeleteOptionBatch_addOption() throws Exception {
		return testDeleteOption_addOption();
	}

	protected void testDeleteOptionBatch_deleteOption(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			optionResource.deleteOptionBatchHttpResponse(
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
	public void testDeleteOptionByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Option option = testDeleteOptionByExternalReferenceCode_addOption();

		assertHttpResponseStatusCode(
			204,
			optionResource.deleteOptionByExternalReferenceCodeHttpResponse(
				option.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			optionResource.getOptionByExternalReferenceCodeHttpResponse(
				option.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			optionResource.getOptionByExternalReferenceCodeHttpResponse("-"));
	}

	protected Option testDeleteOptionByExternalReferenceCode_addOption()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOptionByExternalReferenceCode()
		throws Exception {

		// No namespace

		Option option1 =
			testGraphQLDeleteOptionByExternalReferenceCode_addOption();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOptionByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + option1.getExternalReferenceCode() +
										"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteOptionByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"optionByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + option1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Option option2 =
			testGraphQLDeleteOptionByExternalReferenceCode_addOption();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteOptionByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											option2.getExternalReferenceCode() +
												"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteOptionByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"optionByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + option2.getExternalReferenceCode() +
										"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Option testGraphQLDeleteOptionByExternalReferenceCode_addOption()
		throws Exception {

		return testGraphQLOption_addOption();
	}

	@Test
	public void testGetOption() throws Exception {
		Option postOption = testGetOption_addOption();

		Option getOption = optionResource.getOption(postOption.getId());

		assertEquals(postOption, getOption);
		assertValid(getOption);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		Option postOption = testGetOption_addOption();

		Option getOption = optionResource.getOption(postOption.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.Option"
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

		Object item = vulcanCRUDItemDelegate.getItem(postOption.getId());

		assertEquals(getOption, OptionSerDes.toDTO(item.toString()));
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

	protected Option testGetOption_addOption() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOption() throws Exception {
		Option option = testGraphQLGetOption_addOption();

		// No namespace

		Assert.assertTrue(
			equals(
				option,
				OptionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"option",
								new HashMap<String, Object>() {
									{
										put("id", option.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/option"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				option,
				OptionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"option",
									new HashMap<String, Object>() {
										{
											put("id", option.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/option"))));
	}

	@Test
	public void testGraphQLGetOptionNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"option",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"option",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Option testGraphQLGetOption_addOption() throws Exception {
		return testGraphQLOption_addOption();
	}

	@Test
	public void testGetOptionByExternalReferenceCode() throws Exception {
		Option postOption = testGetOptionByExternalReferenceCode_addOption();

		Option getOption = optionResource.getOptionByExternalReferenceCode(
			postOption.getExternalReferenceCode());

		assertEquals(postOption, getOption);
		assertValid(getOption);
	}

	protected Option testGetOptionByExternalReferenceCode_addOption()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOptionByExternalReferenceCode() throws Exception {
		Option option = testGraphQLGetOptionByExternalReferenceCode_addOption();

		// No namespace

		Assert.assertTrue(
			equals(
				option,
				OptionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"optionByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												option.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/optionByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				option,
				OptionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"optionByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													option.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/optionByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetOptionByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"optionByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"optionByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Option testGraphQLGetOptionByExternalReferenceCode_addOption()
		throws Exception {

		return testGraphQLOption_addOption();
	}

	@Test
	public void testGetOptionsPage() throws Exception {
		Page<Option> page = optionResource.getOptionsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		Option option1 = testGetOptionsPage_addOption(randomOption());

		Option option2 = testGetOptionsPage_addOption(randomOption());

		page = optionResource.getOptionsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(option1, (List<Option>)page.getItems());
		assertContains(option2, (List<Option>)page.getItems());
		assertValid(page, testGetOptionsPage_getExpectedActions());

		optionResource.deleteOption(option1.getId());

		optionResource.deleteOption(option2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOptionsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOptionsPageWithFilterDateTimeEquals() throws Exception {
		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Option option1 = randomOption();

		option1 = testGetOptionsPage_addOption(option1);

		for (EntityField entityField : entityFields) {
			Page<Option> page = optionResource.getOptionsPage(
				null, getFilterString(entityField, "between", option1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(option1),
				(List<Option>)page.getItems());
		}
	}

	@Test
	public void testGetOptionsPageWithFilterDoubleEquals() throws Exception {
		testGetOptionsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetOptionsPageWithFilterStringContains() throws Exception {
		testGetOptionsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetOptionsPageWithFilterStringEquals() throws Exception {
		testGetOptionsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetOptionsPageWithFilterStringStartsWith()
		throws Exception {

		testGetOptionsPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetOptionsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Option option1 = testGetOptionsPage_addOption(randomOption());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Option option2 = testGetOptionsPage_addOption(randomOption());

		for (EntityField entityField : entityFields) {
			Page<Option> page = optionResource.getOptionsPage(
				null, getFilterString(entityField, operator, option1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(option1),
				(List<Option>)page.getItems());
		}
	}

	@Test
	public void testGetOptionsPageWithPagination() throws Exception {
		Page<Option> optionsPage = optionResource.getOptionsPage(
			null, null, null, null);

		int totalCount = GetterUtil.getInteger(optionsPage.getTotalCount());

		Option option1 = testGetOptionsPage_addOption(randomOption());

		Option option2 = testGetOptionsPage_addOption(randomOption());

		Option option3 = testGetOptionsPage_addOption(randomOption());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Option> page1 = optionResource.getOptionsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(option1, (List<Option>)page1.getItems());

			Page<Option> page2 = optionResource.getOptionsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(option2, (List<Option>)page2.getItems());

			Page<Option> page3 = optionResource.getOptionsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(option3, (List<Option>)page3.getItems());
		}
		else {
			Page<Option> page1 = optionResource.getOptionsPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<Option> options1 = (List<Option>)page1.getItems();

			Assert.assertEquals(
				options1.toString(), totalCount + 2, options1.size());

			Page<Option> page2 = optionResource.getOptionsPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Option> options2 = (List<Option>)page2.getItems();

			Assert.assertEquals(options2.toString(), 1, options2.size());

			Page<Option> page3 = optionResource.getOptionsPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(option1, (List<Option>)page3.getItems());
			assertContains(option2, (List<Option>)page3.getItems());
			assertContains(option3, (List<Option>)page3.getItems());
		}
	}

	@Test
	public void testGetOptionsPageWithSortDateTime() throws Exception {
		testGetOptionsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, option1, option2) -> {
				BeanTestUtil.setProperty(
					option1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetOptionsPageWithSortDouble() throws Exception {
		testGetOptionsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, option1, option2) -> {
				BeanTestUtil.setProperty(option1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(option2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetOptionsPageWithSortInteger() throws Exception {
		testGetOptionsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, option1, option2) -> {
				BeanTestUtil.setProperty(option1, entityField.getName(), 0);
				BeanTestUtil.setProperty(option2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetOptionsPageWithSortString() throws Exception {
		testGetOptionsPageWithSort(
			EntityField.Type.STRING,
			(entityField, option1, option2) -> {
				Class<?> clazz = option1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						option1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						option2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						option1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						option2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						option1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						option2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetOptionsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Option, Option, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Option option1 = randomOption();
		Option option2 = randomOption();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, option1, option2);
		}

		option1 = testGetOptionsPage_addOption(option1);

		option2 = testGetOptionsPage_addOption(option2);

		Page<Option> page = optionResource.getOptionsPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Option> ascPage = optionResource.getOptionsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(option1, (List<Option>)ascPage.getItems());
			assertContains(option2, (List<Option>)ascPage.getItems());

			Page<Option> descPage = optionResource.getOptionsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(option2, (List<Option>)descPage.getItems());
			assertContains(option1, (List<Option>)descPage.getItems());
		}
	}

	protected Option testGetOptionsPage_addOption(Option option)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOptionsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"options",
			new HashMap<String, Object>() {
				{
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject optionsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/options");

		long totalCount = optionsJSONObject.getLong("totalCount");

		Option option1 = testGraphQLOption_addOption(randomOption());

		Option option2 = testGraphQLOption_addOption(randomOption());

		optionsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/options");

		Assert.assertEquals(
			totalCount + 2, optionsJSONObject.getLong("totalCount"));

		assertContains(
			option1,
			Arrays.asList(
				OptionSerDes.toDTOs(optionsJSONObject.getString("items"))));
		assertContains(
			option2,
			Arrays.asList(
				OptionSerDes.toDTOs(optionsJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		optionsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminCatalog_v1_0",
			"JSONObject/options");

		Assert.assertEquals(
			totalCount + 2, optionsJSONObject.getLong("totalCount"));

		assertContains(
			option1,
			Arrays.asList(
				OptionSerDes.toDTOs(optionsJSONObject.getString("items"))));
		assertContains(
			option2,
			Arrays.asList(
				OptionSerDes.toDTOs(optionsJSONObject.getString("items"))));
	}

	@Test
	public void testPatchOption() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPatchOptionByExternalReferenceCode() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPostOption() throws Exception {
		Option randomOption = randomOption();

		Option postOption = testPostOption_addOption(randomOption);

		assertEquals(randomOption, postOption);
		assertValid(postOption);
	}

	protected Option testPostOption_addOption(Option option) throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostOption() throws Exception {
		Option randomOption = randomOption();

		Option option = testGraphQLOption_addOption(randomOption);

		Assert.assertTrue(equals(randomOption, option));
	}

	@Test
	public void testPutOptionByExternalReferenceCode() throws Exception {
		Option postOption = testPutOptionByExternalReferenceCode_addOption();

		Option randomOption = randomOption();

		Option putOption = optionResource.putOptionByExternalReferenceCode(
			postOption.getExternalReferenceCode(), randomOption);

		assertEquals(randomOption, putOption);
		assertValid(putOption);

		Option getOption = optionResource.getOptionByExternalReferenceCode(
			putOption.getExternalReferenceCode());

		assertEquals(randomOption, getOption);
		assertValid(getOption);

		Option newOption = testPutOptionByExternalReferenceCode_createOption();

		putOption = optionResource.putOptionByExternalReferenceCode(
			newOption.getExternalReferenceCode(), newOption);

		assertEquals(newOption, putOption);
		assertValid(putOption);

		getOption = optionResource.getOptionByExternalReferenceCode(
			putOption.getExternalReferenceCode());

		assertEquals(newOption, getOption);

		Assert.assertEquals(
			newOption.getExternalReferenceCode(),
			putOption.getExternalReferenceCode());
	}

	protected Option testPutOptionByExternalReferenceCode_addOption()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Option testPutOptionByExternalReferenceCode_createOption()
		throws Exception {

		return randomOption();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Option option1 = testBatchEngineDeleteImportTask_addOption();

		testBatchEngineDeleteImportTask_deleteOption(
			200, option1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, optionResource.getOptionHttpResponse(option1.getId()));

		option1 = testBatchEngineDeleteImportTask_addOption();

		testBatchEngineDeleteImportTask_deleteOption(
			200, null, option1.getId());

		assertHttpResponseStatusCode(
			404, optionResource.getOptionHttpResponse(option1.getId()));

		option1 = testBatchEngineDeleteImportTask_addOption();
		Option option2 = testBatchEngineDeleteImportTask_addOption();

		testBatchEngineDeleteImportTask_deleteOption(
			200, option2.getExternalReferenceCode(), option1.getId());

		assertHttpResponseStatusCode(
			404, optionResource.getOptionHttpResponse(option1.getId()));
		assertHttpResponseStatusCode(
			200, optionResource.getOptionHttpResponse(option2.getId()));

		testBatchEngineDeleteImportTask_deleteOption(
			200, option2.getExternalReferenceCode(), option1.getId());

		assertHttpResponseStatusCode(
			404, optionResource.getOptionHttpResponse(option2.getId()));
	}

	protected Option testBatchEngineDeleteImportTask_addOption()
		throws Exception {

		return testDeleteOption_addOption();
	}

	protected void testBatchEngineDeleteImportTask_deleteOption(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.Option",
				null, null, null, null,
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

	protected Option testGraphQLOption_addOption() throws Exception {
		return testGraphQLOption_addOption(randomOption());
	}

	protected Option testGraphQLOption_addOption(Option option)
		throws Exception {

		JSONDeserializer<Option> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field : getDeclaredFields(Option.class)) {
			if (getGraphQLValue(field.get(option)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(option)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createOption",
						new HashMap<String, Object>() {
							{
								put("option", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createOption"),
			Option.class);
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

	protected void assertContains(Option option, List<Option> options) {
		boolean contains = false;

		for (Option item : options) {
			if (equals(option, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(options + " does not contain " + option, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Option option1, Option option2) {
		Assert.assertTrue(
			option1 + " does not equal " + option2, equals(option1, option2));
	}

	protected void assertEquals(List<Option> options1, List<Option> options2) {
		Assert.assertEquals(options1.size(), options2.size());

		for (int i = 0; i < options1.size(); i++) {
			Option option1 = options1.get(i);
			Option option2 = options2.get(i);

			assertEquals(option1, option2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Option> options1, List<Option> options2) {

		Assert.assertEquals(options1.size(), options2.size());

		for (Option option1 : options1) {
			boolean contains = false;

			for (Option option2 : options2) {
				if (equals(option1, option2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				options2 + " does not contain " + option1, contains);
		}
	}

	protected void assertValid(Option option) throws Exception {
		boolean valid = true;

		if (option.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (option.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("catalogId", additionalAssertFieldName)) {
				if (option.getCatalogId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (option.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (option.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (option.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("facetable", additionalAssertFieldName)) {
				if (option.getFacetable() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("fieldType", additionalAssertFieldName)) {
				if (option.getFieldType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (option.getKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (option.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("optionValues", additionalAssertFieldName)) {
				if (option.getOptionValues() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (option.getPriority() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("required", additionalAssertFieldName)) {
				if (option.getRequired() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("skuContributor", additionalAssertFieldName)) {
				if (option.getSkuContributor() == null) {
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

	protected void assertValid(Page<Option> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Option> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Option> options = page.getItems();

		int size = options.size();

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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.catalog.dto.v1_0.Option.
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

	protected boolean equals(Option option1, Option option2) {
		if (option1 == option2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)option1.getActions(), (Map)option2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("catalogId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						option1.getCatalogId(), option2.getCatalogId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						option1.getCustomFields(), option2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!equals(
						(Map)option1.getDescription(),
						(Map)option2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						option1.getExternalReferenceCode(),
						option2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("facetable", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						option1.getFacetable(), option2.getFacetable())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fieldType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						option1.getFieldType(), option2.getFieldType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(option1.getId(), option2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(option1.getKey(), option2.getKey())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!equals((Map)option1.getName(), (Map)option2.getName())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("optionValues", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						option1.getOptionValues(), option2.getOptionValues())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						option1.getPriority(), option2.getPriority())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("required", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						option1.getRequired(), option2.getRequired())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("skuContributor", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						option1.getSkuContributor(),
						option2.getSkuContributor())) {

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

		if (!(_optionResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_optionResource;

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
		EntityField entityField, String operator, Option option) {

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

		if (entityFieldName.equals("catalogId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("description")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = option.getExternalReferenceCode();

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

		if (entityFieldName.equals("facetable")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("fieldType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("key")) {
			Object object = option.getKey();

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

		if (entityFieldName.equals("name")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("optionValues")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priority")) {
			sb.append(String.valueOf(option.getPriority()));

			return sb.toString();
		}

		if (entityFieldName.equals("required")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("skuContributor")) {
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

	protected Option randomOption() throws Exception {
		return new Option() {
			{
				catalogId = RandomTestUtil.randomLong();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				facetable = RandomTestUtil.randomBoolean();
				id = RandomTestUtil.randomLong();
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				priority = RandomTestUtil.randomDouble();
				required = RandomTestUtil.randomBoolean();
				skuContributor = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected Option randomIrrelevantOption() throws Exception {
		Option randomIrrelevantOption = randomOption();

		return randomIrrelevantOption;
	}

	protected Option randomPatchOption() throws Exception {
		return randomOption();
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

	protected OptionResource optionResource;
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
		LogFactoryUtil.getLog(BaseOptionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.admin.catalog.resource.v1_0.OptionResource
			_optionResource;

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