/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.rest.resource.v1_0.test;

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
import com.liferay.notification.rest.client.dto.v1_0.NotificationTemplate;
import com.liferay.notification.rest.client.http.HttpInvoker;
import com.liferay.notification.rest.client.pagination.Page;
import com.liferay.notification.rest.client.pagination.Pagination;
import com.liferay.notification.rest.client.resource.v1_0.NotificationTemplateResource;
import com.liferay.notification.rest.client.serdes.v1_0.NotificationTemplateSerDes;
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
 * @author Gabriel Albuquerque
 * @generated
 */
@Generated("")
public abstract class BaseNotificationTemplateResourceTestCase {

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

		_notificationTemplateResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		notificationTemplateResource = NotificationTemplateResource.builder(
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

		NotificationTemplate notificationTemplate1 =
			randomNotificationTemplate();

		String json = objectMapper.writeValueAsString(notificationTemplate1);

		NotificationTemplate notificationTemplate2 =
			NotificationTemplateSerDes.toDTO(json);

		Assert.assertTrue(equals(notificationTemplate1, notificationTemplate2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		NotificationTemplate notificationTemplate =
			randomNotificationTemplate();

		String json1 = objectMapper.writeValueAsString(notificationTemplate);
		String json2 = NotificationTemplateSerDes.toJSON(notificationTemplate);

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

		NotificationTemplate notificationTemplate =
			randomNotificationTemplate();

		notificationTemplate.setDescription(regex);
		notificationTemplate.setExternalReferenceCode(regex);
		notificationTemplate.setName(regex);
		notificationTemplate.setObjectDefinitionExternalReferenceCode(regex);
		notificationTemplate.setRecipientType(regex);
		notificationTemplate.setType(regex);
		notificationTemplate.setTypeLabel(regex);

		String json = NotificationTemplateSerDes.toJSON(notificationTemplate);

		Assert.assertFalse(json.contains(regex));

		notificationTemplate = NotificationTemplateSerDes.toDTO(json);

		Assert.assertEquals(regex, notificationTemplate.getDescription());
		Assert.assertEquals(
			regex, notificationTemplate.getExternalReferenceCode());
		Assert.assertEquals(regex, notificationTemplate.getName());
		Assert.assertEquals(
			regex,
			notificationTemplate.getObjectDefinitionExternalReferenceCode());
		Assert.assertEquals(regex, notificationTemplate.getRecipientType());
		Assert.assertEquals(regex, notificationTemplate.getType());
		Assert.assertEquals(regex, notificationTemplate.getTypeLabel());
	}

	@Test
	public void testDeleteNotificationTemplate() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		NotificationTemplate notificationTemplate =
			testDeleteNotificationTemplate_addNotificationTemplate();

		assertHttpResponseStatusCode(
			204,
			notificationTemplateResource.deleteNotificationTemplateHttpResponse(
				notificationTemplate.getId()));

		assertHttpResponseStatusCode(
			404,
			notificationTemplateResource.getNotificationTemplateHttpResponse(
				notificationTemplate.getId()));
		assertHttpResponseStatusCode(
			404,
			notificationTemplateResource.getNotificationTemplateHttpResponse(
				0L));
	}

	protected NotificationTemplate
			testDeleteNotificationTemplate_addNotificationTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteNotificationTemplate() throws Exception {

		// No namespace

		NotificationTemplate notificationTemplate1 =
			testGraphQLDeleteNotificationTemplate_addNotificationTemplate();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteNotificationTemplate",
						new HashMap<String, Object>() {
							{
								put(
									"notificationTemplateId",
									notificationTemplate1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteNotificationTemplate"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"notificationTemplate",
					new HashMap<String, Object>() {
						{
							put(
								"notificationTemplateId",
								notificationTemplate1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace notification_v1_0

		NotificationTemplate notificationTemplate2 =
			testGraphQLDeleteNotificationTemplate_addNotificationTemplate();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"notification_v1_0",
						new GraphQLField(
							"deleteNotificationTemplate",
							new HashMap<String, Object>() {
								{
									put(
										"notificationTemplateId",
										notificationTemplate2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/notification_v1_0",
				"Object/deleteNotificationTemplate"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"notification_v1_0",
					new GraphQLField(
						"notificationTemplate",
						new HashMap<String, Object>() {
							{
								put(
									"notificationTemplateId",
									notificationTemplate2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected NotificationTemplate
			testGraphQLDeleteNotificationTemplate_addNotificationTemplate()
		throws Exception {

		return testGraphQLNotificationTemplate_addNotificationTemplate();
	}

	@Test
	public void testDeleteNotificationTemplateBatch() throws Exception {
		NotificationTemplate notificationTemplate1 =
			testDeleteNotificationTemplateBatch_addNotificationTemplate();

		testDeleteNotificationTemplateBatch_deleteNotificationTemplate(
			202, null, notificationTemplate1.getId());

		assertHttpResponseStatusCode(
			404,
			notificationTemplateResource.getNotificationTemplateHttpResponse(
				notificationTemplate1.getId()));
	}

	protected NotificationTemplate
			testDeleteNotificationTemplateBatch_addNotificationTemplate()
		throws Exception {

		return testDeleteNotificationTemplate_addNotificationTemplate();
	}

	protected void
			testDeleteNotificationTemplateBatch_deleteNotificationTemplate(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			notificationTemplateResource.
				deleteNotificationTemplateBatchHttpResponse(
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
	public void testGetNotificationTemplate() throws Exception {
		NotificationTemplate postNotificationTemplate =
			testGetNotificationTemplate_addNotificationTemplate();

		NotificationTemplate getNotificationTemplate =
			notificationTemplateResource.getNotificationTemplate(
				postNotificationTemplate.getId());

		assertEquals(postNotificationTemplate, getNotificationTemplate);
		assertValid(getNotificationTemplate);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		NotificationTemplate postNotificationTemplate =
			testGetNotificationTemplate_addNotificationTemplate();

		NotificationTemplate getNotificationTemplate =
			notificationTemplateResource.getNotificationTemplate(
				postNotificationTemplate.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.notification.rest.dto.v1_0.NotificationTemplate"
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

		Object item = vulcanCRUDItemDelegate.getItem(
			postNotificationTemplate.getId());

		assertEquals(
			getNotificationTemplate,
			NotificationTemplateSerDes.toDTO(item.toString()));
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

	protected NotificationTemplate
			testGetNotificationTemplate_addNotificationTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetNotificationTemplate() throws Exception {
		NotificationTemplate notificationTemplate =
			testGraphQLGetNotificationTemplate_addNotificationTemplate();

		// No namespace

		Assert.assertTrue(
			equals(
				notificationTemplate,
				NotificationTemplateSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"notificationTemplate",
								new HashMap<String, Object>() {
									{
										put(
											"notificationTemplateId",
											notificationTemplate.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/notificationTemplate"))));

		// Using the namespace notification_v1_0

		Assert.assertTrue(
			equals(
				notificationTemplate,
				NotificationTemplateSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"notification_v1_0",
								new GraphQLField(
									"notificationTemplate",
									new HashMap<String, Object>() {
										{
											put(
												"notificationTemplateId",
												notificationTemplate.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/notification_v1_0",
						"Object/notificationTemplate"))));
	}

	@Test
	public void testGraphQLGetNotificationTemplateNotFound() throws Exception {
		Long irrelevantNotificationTemplateId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"notificationTemplate",
						new HashMap<String, Object>() {
							{
								put(
									"notificationTemplateId",
									irrelevantNotificationTemplateId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace notification_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"notification_v1_0",
						new GraphQLField(
							"notificationTemplate",
							new HashMap<String, Object>() {
								{
									put(
										"notificationTemplateId",
										irrelevantNotificationTemplateId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected NotificationTemplate
			testGraphQLGetNotificationTemplate_addNotificationTemplate()
		throws Exception {

		return testGraphQLNotificationTemplate_addNotificationTemplate();
	}

	@Test
	public void testGetNotificationTemplateByExternalReferenceCode()
		throws Exception {

		NotificationTemplate postNotificationTemplate =
			testGetNotificationTemplateByExternalReferenceCode_addNotificationTemplate();

		NotificationTemplate getNotificationTemplate =
			notificationTemplateResource.
				getNotificationTemplateByExternalReferenceCode(
					postNotificationTemplate.getExternalReferenceCode());

		assertEquals(postNotificationTemplate, getNotificationTemplate);
		assertValid(getNotificationTemplate);
	}

	protected NotificationTemplate
			testGetNotificationTemplateByExternalReferenceCode_addNotificationTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetNotificationTemplateByExternalReferenceCode()
		throws Exception {

		NotificationTemplate notificationTemplate =
			testGraphQLGetNotificationTemplateByExternalReferenceCode_addNotificationTemplate();

		// No namespace

		Assert.assertTrue(
			equals(
				notificationTemplate,
				NotificationTemplateSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"notificationTemplateByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												notificationTemplate.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/notificationTemplateByExternalReferenceCode"))));

		// Using the namespace notification_v1_0

		Assert.assertTrue(
			equals(
				notificationTemplate,
				NotificationTemplateSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"notification_v1_0",
								new GraphQLField(
									"notificationTemplateByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													notificationTemplate.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/notification_v1_0",
						"Object/notificationTemplateByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetNotificationTemplateByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"notificationTemplateByExternalReferenceCode",
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

		// Using the namespace notification_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"notification_v1_0",
						new GraphQLField(
							"notificationTemplateByExternalReferenceCode",
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

	protected NotificationTemplate
			testGraphQLGetNotificationTemplateByExternalReferenceCode_addNotificationTemplate()
		throws Exception {

		return testGraphQLNotificationTemplate_addNotificationTemplate();
	}

	@Test
	public void testGetNotificationTemplatesPage() throws Exception {
		Page<NotificationTemplate> page =
			notificationTemplateResource.getNotificationTemplatesPage(
				null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		NotificationTemplate notificationTemplate1 =
			testGetNotificationTemplatesPage_addNotificationTemplate(
				randomNotificationTemplate());

		NotificationTemplate notificationTemplate2 =
			testGetNotificationTemplatesPage_addNotificationTemplate(
				randomNotificationTemplate());

		page = notificationTemplateResource.getNotificationTemplatesPage(
			null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			notificationTemplate1, (List<NotificationTemplate>)page.getItems());
		assertContains(
			notificationTemplate2, (List<NotificationTemplate>)page.getItems());
		assertValid(
			page, testGetNotificationTemplatesPage_getExpectedActions());

		notificationTemplateResource.deleteNotificationTemplate(
			notificationTemplate1.getId());

		notificationTemplateResource.deleteNotificationTemplate(
			notificationTemplate2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetNotificationTemplatesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetNotificationTemplatesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		NotificationTemplate notificationTemplate1 =
			randomNotificationTemplate();

		notificationTemplate1 =
			testGetNotificationTemplatesPage_addNotificationTemplate(
				notificationTemplate1);

		for (EntityField entityField : entityFields) {
			Page<NotificationTemplate> page =
				notificationTemplateResource.getNotificationTemplatesPage(
					null, null,
					getFilterString(
						entityField, "between", notificationTemplate1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(notificationTemplate1),
				(List<NotificationTemplate>)page.getItems());
		}
	}

	@Test
	public void testGetNotificationTemplatesPageWithFilterDoubleEquals()
		throws Exception {

		testGetNotificationTemplatesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetNotificationTemplatesPageWithFilterStringContains()
		throws Exception {

		testGetNotificationTemplatesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetNotificationTemplatesPageWithFilterStringEquals()
		throws Exception {

		testGetNotificationTemplatesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetNotificationTemplatesPageWithFilterStringStartsWith()
		throws Exception {

		testGetNotificationTemplatesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetNotificationTemplatesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		NotificationTemplate notificationTemplate1 =
			testGetNotificationTemplatesPage_addNotificationTemplate(
				randomNotificationTemplate());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		NotificationTemplate notificationTemplate2 =
			testGetNotificationTemplatesPage_addNotificationTemplate(
				randomNotificationTemplate());

		for (EntityField entityField : entityFields) {
			Page<NotificationTemplate> page =
				notificationTemplateResource.getNotificationTemplatesPage(
					null, null,
					getFilterString(
						entityField, operator, notificationTemplate1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(notificationTemplate1),
				(List<NotificationTemplate>)page.getItems());
		}
	}

	@Test
	public void testGetNotificationTemplatesPageWithPagination()
		throws Exception {

		Page<NotificationTemplate> notificationTemplatesPage =
			notificationTemplateResource.getNotificationTemplatesPage(
				null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			notificationTemplatesPage.getTotalCount());

		NotificationTemplate notificationTemplate1 =
			testGetNotificationTemplatesPage_addNotificationTemplate(
				randomNotificationTemplate());

		NotificationTemplate notificationTemplate2 =
			testGetNotificationTemplatesPage_addNotificationTemplate(
				randomNotificationTemplate());

		NotificationTemplate notificationTemplate3 =
			testGetNotificationTemplatesPage_addNotificationTemplate(
				randomNotificationTemplate());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<NotificationTemplate> page1 =
				notificationTemplateResource.getNotificationTemplatesPage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				notificationTemplate1,
				(List<NotificationTemplate>)page1.getItems());

			Page<NotificationTemplate> page2 =
				notificationTemplateResource.getNotificationTemplatesPage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				notificationTemplate2,
				(List<NotificationTemplate>)page2.getItems());

			Page<NotificationTemplate> page3 =
				notificationTemplateResource.getNotificationTemplatesPage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				notificationTemplate3,
				(List<NotificationTemplate>)page3.getItems());
		}
		else {
			Page<NotificationTemplate> page1 =
				notificationTemplateResource.getNotificationTemplatesPage(
					null, null, null, Pagination.of(1, totalCount + 2), null);

			List<NotificationTemplate> notificationTemplates1 =
				(List<NotificationTemplate>)page1.getItems();

			Assert.assertEquals(
				notificationTemplates1.toString(), totalCount + 2,
				notificationTemplates1.size());

			Page<NotificationTemplate> page2 =
				notificationTemplateResource.getNotificationTemplatesPage(
					null, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<NotificationTemplate> notificationTemplates2 =
				(List<NotificationTemplate>)page2.getItems();

			Assert.assertEquals(
				notificationTemplates2.toString(), 1,
				notificationTemplates2.size());

			Page<NotificationTemplate> page3 =
				notificationTemplateResource.getNotificationTemplatesPage(
					null, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(
				notificationTemplate1,
				(List<NotificationTemplate>)page3.getItems());
			assertContains(
				notificationTemplate2,
				(List<NotificationTemplate>)page3.getItems());
			assertContains(
				notificationTemplate3,
				(List<NotificationTemplate>)page3.getItems());
		}
	}

	@Test
	public void testGetNotificationTemplatesPageWithSortDateTime()
		throws Exception {

		testGetNotificationTemplatesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, notificationTemplate1, notificationTemplate2) -> {
				BeanTestUtil.setProperty(
					notificationTemplate1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetNotificationTemplatesPageWithSortDouble()
		throws Exception {

		testGetNotificationTemplatesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, notificationTemplate1, notificationTemplate2) -> {
				BeanTestUtil.setProperty(
					notificationTemplate1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					notificationTemplate2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetNotificationTemplatesPageWithSortInteger()
		throws Exception {

		testGetNotificationTemplatesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, notificationTemplate1, notificationTemplate2) -> {
				BeanTestUtil.setProperty(
					notificationTemplate1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					notificationTemplate2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetNotificationTemplatesPageWithSortString()
		throws Exception {

		testGetNotificationTemplatesPageWithSort(
			EntityField.Type.STRING,
			(entityField, notificationTemplate1, notificationTemplate2) -> {
				Class<?> clazz = notificationTemplate1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						notificationTemplate1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						notificationTemplate2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						notificationTemplate1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						notificationTemplate2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						notificationTemplate1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						notificationTemplate2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetNotificationTemplatesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, NotificationTemplate, NotificationTemplate,
				 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		NotificationTemplate notificationTemplate1 =
			randomNotificationTemplate();
		NotificationTemplate notificationTemplate2 =
			randomNotificationTemplate();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, notificationTemplate1, notificationTemplate2);
		}

		notificationTemplate1 =
			testGetNotificationTemplatesPage_addNotificationTemplate(
				notificationTemplate1);

		notificationTemplate2 =
			testGetNotificationTemplatesPage_addNotificationTemplate(
				notificationTemplate2);

		Page<NotificationTemplate> page =
			notificationTemplateResource.getNotificationTemplatesPage(
				null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<NotificationTemplate> ascPage =
				notificationTemplateResource.getNotificationTemplatesPage(
					null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				notificationTemplate1,
				(List<NotificationTemplate>)ascPage.getItems());
			assertContains(
				notificationTemplate2,
				(List<NotificationTemplate>)ascPage.getItems());

			Page<NotificationTemplate> descPage =
				notificationTemplateResource.getNotificationTemplatesPage(
					null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				notificationTemplate2,
				(List<NotificationTemplate>)descPage.getItems());
			assertContains(
				notificationTemplate1,
				(List<NotificationTemplate>)descPage.getItems());
		}
	}

	protected NotificationTemplate
			testGetNotificationTemplatesPage_addNotificationTemplate(
				NotificationTemplate notificationTemplate)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetNotificationTemplatesPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"notificationTemplates",
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

		JSONObject notificationTemplatesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/notificationTemplates");

		long totalCount = notificationTemplatesJSONObject.getLong("totalCount");

		NotificationTemplate notificationTemplate1 =
			testGraphQLNotificationTemplate_addNotificationTemplate(
				randomNotificationTemplate());

		NotificationTemplate notificationTemplate2 =
			testGraphQLNotificationTemplate_addNotificationTemplate(
				randomNotificationTemplate());

		notificationTemplatesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/notificationTemplates");

		Assert.assertEquals(
			totalCount + 2,
			notificationTemplatesJSONObject.getLong("totalCount"));

		assertContains(
			notificationTemplate1,
			Arrays.asList(
				NotificationTemplateSerDes.toDTOs(
					notificationTemplatesJSONObject.getString("items"))));
		assertContains(
			notificationTemplate2,
			Arrays.asList(
				NotificationTemplateSerDes.toDTOs(
					notificationTemplatesJSONObject.getString("items"))));

		// Using the namespace notification_v1_0

		notificationTemplatesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("notification_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/notification_v1_0",
			"JSONObject/notificationTemplates");

		Assert.assertEquals(
			totalCount + 2,
			notificationTemplatesJSONObject.getLong("totalCount"));

		assertContains(
			notificationTemplate1,
			Arrays.asList(
				NotificationTemplateSerDes.toDTOs(
					notificationTemplatesJSONObject.getString("items"))));
		assertContains(
			notificationTemplate2,
			Arrays.asList(
				NotificationTemplateSerDes.toDTOs(
					notificationTemplatesJSONObject.getString("items"))));
	}

	@Test
	public void testPatchNotificationTemplate() throws Exception {
		NotificationTemplate postNotificationTemplate =
			testPatchNotificationTemplate_addNotificationTemplate();

		NotificationTemplate randomPatchNotificationTemplate =
			randomPatchNotificationTemplate();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		NotificationTemplate patchNotificationTemplate =
			notificationTemplateResource.patchNotificationTemplate(
				postNotificationTemplate.getId(),
				randomPatchNotificationTemplate);

		NotificationTemplate expectedPatchNotificationTemplate =
			postNotificationTemplate.clone();

		BeanTestUtil.copyProperties(
			randomPatchNotificationTemplate, expectedPatchNotificationTemplate);

		NotificationTemplate getNotificationTemplate =
			notificationTemplateResource.getNotificationTemplate(
				patchNotificationTemplate.getId());

		assertEquals(
			expectedPatchNotificationTemplate, getNotificationTemplate);
		assertValid(getNotificationTemplate);
	}

	protected NotificationTemplate
			testPatchNotificationTemplate_addNotificationTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostNotificationTemplate() throws Exception {
		NotificationTemplate randomNotificationTemplate =
			randomNotificationTemplate();

		NotificationTemplate postNotificationTemplate =
			testPostNotificationTemplate_addNotificationTemplate(
				randomNotificationTemplate);

		assertEquals(randomNotificationTemplate, postNotificationTemplate);
		assertValid(postNotificationTemplate);
	}

	protected NotificationTemplate
			testPostNotificationTemplate_addNotificationTemplate(
				NotificationTemplate notificationTemplate)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostNotificationTemplate() throws Exception {
		NotificationTemplate randomNotificationTemplate =
			randomNotificationTemplate();

		NotificationTemplate notificationTemplate =
			testGraphQLNotificationTemplate_addNotificationTemplate(
				randomNotificationTemplate);

		Assert.assertTrue(
			equals(randomNotificationTemplate, notificationTemplate));
	}

	@Test
	public void testPostNotificationTemplateCopy() throws Exception {
		NotificationTemplate randomNotificationTemplate =
			randomNotificationTemplate();

		NotificationTemplate postNotificationTemplate =
			testPostNotificationTemplateCopy_addNotificationTemplate(
				randomNotificationTemplate);

		assertEquals(randomNotificationTemplate, postNotificationTemplate);
		assertValid(postNotificationTemplate);
	}

	protected NotificationTemplate
			testPostNotificationTemplateCopy_addNotificationTemplate(
				NotificationTemplate notificationTemplate)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostNotificationTemplateCopy() throws Exception {
		NotificationTemplate randomNotificationTemplate =
			randomNotificationTemplate();

		NotificationTemplate notificationTemplate =
			testGraphQLNotificationTemplate_addNotificationTemplate(
				randomNotificationTemplate);

		Assert.assertTrue(
			equals(randomNotificationTemplate, notificationTemplate));
	}

	@Test
	public void testPutNotificationTemplate() throws Exception {
		NotificationTemplate postNotificationTemplate =
			testPutNotificationTemplate_addNotificationTemplate();

		NotificationTemplate randomNotificationTemplate =
			randomNotificationTemplate();

		NotificationTemplate putNotificationTemplate =
			notificationTemplateResource.putNotificationTemplate(
				postNotificationTemplate.getId(), randomNotificationTemplate);

		assertEquals(randomNotificationTemplate, putNotificationTemplate);
		assertValid(putNotificationTemplate);

		NotificationTemplate getNotificationTemplate =
			notificationTemplateResource.getNotificationTemplate(
				putNotificationTemplate.getId());

		assertEquals(randomNotificationTemplate, getNotificationTemplate);
		assertValid(getNotificationTemplate);
	}

	protected NotificationTemplate
			testPutNotificationTemplate_addNotificationTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutNotificationTemplateByExternalReferenceCode()
		throws Exception {

		NotificationTemplate postNotificationTemplate =
			testPutNotificationTemplateByExternalReferenceCode_addNotificationTemplate();

		NotificationTemplate randomNotificationTemplate =
			randomNotificationTemplate();

		NotificationTemplate putNotificationTemplate =
			notificationTemplateResource.
				putNotificationTemplateByExternalReferenceCode(
					postNotificationTemplate.getExternalReferenceCode(),
					randomNotificationTemplate);

		assertEquals(randomNotificationTemplate, putNotificationTemplate);
		assertValid(putNotificationTemplate);

		NotificationTemplate getNotificationTemplate =
			notificationTemplateResource.
				getNotificationTemplateByExternalReferenceCode(
					putNotificationTemplate.getExternalReferenceCode());

		assertEquals(randomNotificationTemplate, getNotificationTemplate);
		assertValid(getNotificationTemplate);

		NotificationTemplate newNotificationTemplate =
			testPutNotificationTemplateByExternalReferenceCode_createNotificationTemplate();

		putNotificationTemplate =
			notificationTemplateResource.
				putNotificationTemplateByExternalReferenceCode(
					newNotificationTemplate.getExternalReferenceCode(),
					newNotificationTemplate);

		assertEquals(newNotificationTemplate, putNotificationTemplate);
		assertValid(putNotificationTemplate);

		getNotificationTemplate =
			notificationTemplateResource.
				getNotificationTemplateByExternalReferenceCode(
					putNotificationTemplate.getExternalReferenceCode());

		assertEquals(newNotificationTemplate, getNotificationTemplate);

		Assert.assertEquals(
			newNotificationTemplate.getExternalReferenceCode(),
			putNotificationTemplate.getExternalReferenceCode());
	}

	protected NotificationTemplate
			testPutNotificationTemplateByExternalReferenceCode_addNotificationTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected NotificationTemplate
			testPutNotificationTemplateByExternalReferenceCode_createNotificationTemplate()
		throws Exception {

		return randomNotificationTemplate();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		NotificationTemplate notificationTemplate1 =
			testBatchEngineDeleteImportTask_addNotificationTemplate();

		testBatchEngineDeleteImportTask_deleteNotificationTemplate(
			200, null, notificationTemplate1.getId());

		assertHttpResponseStatusCode(
			404,
			notificationTemplateResource.getNotificationTemplateHttpResponse(
				notificationTemplate1.getId()));
	}

	protected NotificationTemplate
			testBatchEngineDeleteImportTask_addNotificationTemplate()
		throws Exception {

		return testDeleteNotificationTemplate_addNotificationTemplate();
	}

	protected void testBatchEngineDeleteImportTask_deleteNotificationTemplate(
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
				"com.liferay.notification.rest.dto.v1_0.NotificationTemplate",
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

	protected NotificationTemplate
			testGraphQLNotificationTemplate_addNotificationTemplate()
		throws Exception {

		return testGraphQLNotificationTemplate_addNotificationTemplate(
			randomNotificationTemplate());
	}

	protected NotificationTemplate
			testGraphQLNotificationTemplate_addNotificationTemplate(
				NotificationTemplate notificationTemplate)
		throws Exception {

		JSONDeserializer<NotificationTemplate> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(NotificationTemplate.class)) {

			if (getGraphQLValue(field.get(notificationTemplate)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(notificationTemplate)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createNotificationTemplate",
						new HashMap<String, Object>() {
							{
								put("notificationTemplate", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createNotificationTemplate"),
			NotificationTemplate.class);
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

	protected void assertContains(
		NotificationTemplate notificationTemplate,
		List<NotificationTemplate> notificationTemplates) {

		boolean contains = false;

		for (NotificationTemplate item : notificationTemplates) {
			if (equals(notificationTemplate, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			notificationTemplates + " does not contain " + notificationTemplate,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		NotificationTemplate notificationTemplate1,
		NotificationTemplate notificationTemplate2) {

		Assert.assertTrue(
			notificationTemplate1 + " does not equal " + notificationTemplate2,
			equals(notificationTemplate1, notificationTemplate2));
	}

	protected void assertEquals(
		List<NotificationTemplate> notificationTemplates1,
		List<NotificationTemplate> notificationTemplates2) {

		Assert.assertEquals(
			notificationTemplates1.size(), notificationTemplates2.size());

		for (int i = 0; i < notificationTemplates1.size(); i++) {
			NotificationTemplate notificationTemplate1 =
				notificationTemplates1.get(i);
			NotificationTemplate notificationTemplate2 =
				notificationTemplates2.get(i);

			assertEquals(notificationTemplate1, notificationTemplate2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<NotificationTemplate> notificationTemplates1,
		List<NotificationTemplate> notificationTemplates2) {

		Assert.assertEquals(
			notificationTemplates1.size(), notificationTemplates2.size());

		for (NotificationTemplate notificationTemplate1 :
				notificationTemplates1) {

			boolean contains = false;

			for (NotificationTemplate notificationTemplate2 :
					notificationTemplates2) {

				if (equals(notificationTemplate1, notificationTemplate2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				notificationTemplates2 + " does not contain " +
					notificationTemplate1,
				contains);
		}
	}

	protected void assertValid(NotificationTemplate notificationTemplate)
		throws Exception {

		boolean valid = true;

		if (notificationTemplate.getDateCreated() == null) {
			valid = false;
		}

		if (notificationTemplate.getDateModified() == null) {
			valid = false;
		}

		if (notificationTemplate.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (notificationTemplate.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"attachmentObjectFieldExternalReferenceCodes",
					additionalAssertFieldName)) {

				if (notificationTemplate.
						getAttachmentObjectFieldExternalReferenceCodes() ==
							null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"attachmentObjectFieldIds", additionalAssertFieldName)) {

				if (notificationTemplate.getAttachmentObjectFieldIds() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("body", additionalAssertFieldName)) {
				if (notificationTemplate.getBody() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (notificationTemplate.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("editorType", additionalAssertFieldName)) {
				if (notificationTemplate.getEditorType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (notificationTemplate.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (notificationTemplate.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (notificationTemplate.getName_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionExternalReferenceCode",
					additionalAssertFieldName)) {

				if (notificationTemplate.
						getObjectDefinitionExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionId", additionalAssertFieldName)) {

				if (notificationTemplate.getObjectDefinitionId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("recipientType", additionalAssertFieldName)) {
				if (notificationTemplate.getRecipientType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("recipients", additionalAssertFieldName)) {
				if (notificationTemplate.getRecipients() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("subject", additionalAssertFieldName)) {
				if (notificationTemplate.getSubject() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (notificationTemplate.getSystem() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (notificationTemplate.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("typeLabel", additionalAssertFieldName)) {
				if (notificationTemplate.getTypeLabel() == null) {
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

	protected void assertValid(Page<NotificationTemplate> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<NotificationTemplate> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<NotificationTemplate> notificationTemplates =
			page.getItems();

		int size = notificationTemplates.size();

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
					com.liferay.notification.rest.dto.v1_0.NotificationTemplate.
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
		NotificationTemplate notificationTemplate1,
		NotificationTemplate notificationTemplate2) {

		if (notificationTemplate1 == notificationTemplate2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)notificationTemplate1.getActions(),
						(Map)notificationTemplate2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"attachmentObjectFieldExternalReferenceCodes",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						notificationTemplate1.
							getAttachmentObjectFieldExternalReferenceCodes(),
						notificationTemplate2.
							getAttachmentObjectFieldExternalReferenceCodes())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"attachmentObjectFieldIds", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						notificationTemplate1.getAttachmentObjectFieldIds(),
						notificationTemplate2.getAttachmentObjectFieldIds())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("body", additionalAssertFieldName)) {
				if (!equals(
						(Map)notificationTemplate1.getBody(),
						(Map)notificationTemplate2.getBody())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationTemplate1.getDateCreated(),
						notificationTemplate2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationTemplate1.getDateModified(),
						notificationTemplate2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationTemplate1.getDescription(),
						notificationTemplate2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("editorType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationTemplate1.getEditorType(),
						notificationTemplate2.getEditorType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						notificationTemplate1.getExternalReferenceCode(),
						notificationTemplate2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationTemplate1.getId(),
						notificationTemplate2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationTemplate1.getName(),
						notificationTemplate2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)notificationTemplate1.getName_i18n(),
						(Map)notificationTemplate2.getName_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						notificationTemplate1.
							getObjectDefinitionExternalReferenceCode(),
						notificationTemplate2.
							getObjectDefinitionExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						notificationTemplate1.getObjectDefinitionId(),
						notificationTemplate2.getObjectDefinitionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("recipientType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationTemplate1.getRecipientType(),
						notificationTemplate2.getRecipientType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("recipients", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationTemplate1.getRecipients(),
						notificationTemplate2.getRecipients())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("subject", additionalAssertFieldName)) {
				if (!equals(
						(Map)notificationTemplate1.getSubject(),
						(Map)notificationTemplate2.getSubject())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationTemplate1.getSystem(),
						notificationTemplate2.getSystem())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationTemplate1.getType(),
						notificationTemplate2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("typeLabel", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationTemplate1.getTypeLabel(),
						notificationTemplate2.getTypeLabel())) {

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

		if (!(_notificationTemplateResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_notificationTemplateResource;

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
		NotificationTemplate notificationTemplate) {

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

		if (entityFieldName.equals(
				"attachmentObjectFieldExternalReferenceCodes")) {

			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("attachmentObjectFieldIds")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("body")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = notificationTemplate.getDateCreated();

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

				sb.append(
					_format.format(notificationTemplate.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = notificationTemplate.getDateModified();

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

				sb.append(
					_format.format(notificationTemplate.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = notificationTemplate.getDescription();

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

		if (entityFieldName.equals("editorType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = notificationTemplate.getExternalReferenceCode();

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
			Object object = notificationTemplate.getName();

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

		if (entityFieldName.equals("name_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("objectDefinitionExternalReferenceCode")) {
			Object object =
				notificationTemplate.getObjectDefinitionExternalReferenceCode();

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

		if (entityFieldName.equals("objectDefinitionId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("recipientType")) {
			Object object = notificationTemplate.getRecipientType();

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

		if (entityFieldName.equals("recipients")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("subject")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("system")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("type")) {
			Object object = notificationTemplate.getType();

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

		if (entityFieldName.equals("typeLabel")) {
			Object object = notificationTemplate.getTypeLabel();

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

	protected NotificationTemplate randomNotificationTemplate()
		throws Exception {

		return new NotificationTemplate() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				objectDefinitionExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				objectDefinitionId = RandomTestUtil.randomLong();
				recipientType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				system = RandomTestUtil.randomBoolean();
				type = StringUtil.toLowerCase(RandomTestUtil.randomString());
				typeLabel = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected NotificationTemplate randomIrrelevantNotificationTemplate()
		throws Exception {

		NotificationTemplate randomIrrelevantNotificationTemplate =
			randomNotificationTemplate();

		return randomIrrelevantNotificationTemplate;
	}

	protected NotificationTemplate randomPatchNotificationTemplate()
		throws Exception {

		return randomNotificationTemplate();
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

	protected NotificationTemplateResource notificationTemplateResource;
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
		LogFactoryUtil.getLog(BaseNotificationTemplateResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.notification.rest.resource.v1_0.NotificationTemplateResource
			_notificationTemplateResource;

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