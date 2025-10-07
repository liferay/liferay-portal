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
import com.liferay.headless.delivery.client.dto.v1_0.MessageBoardSection;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.permission.Permission;
import com.liferay.headless.delivery.client.resource.v1_0.MessageBoardSectionResource;
import com.liferay.headless.delivery.client.serdes.v1_0.MessageBoardSectionSerDes;
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
public abstract class BaseMessageBoardSectionResourceTestCase {

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

		_messageBoardSectionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		messageBoardSectionResource = MessageBoardSectionResource.builder(
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

		MessageBoardSection messageBoardSection1 = randomMessageBoardSection();

		String json = objectMapper.writeValueAsString(messageBoardSection1);

		MessageBoardSection messageBoardSection2 =
			MessageBoardSectionSerDes.toDTO(json);

		Assert.assertTrue(equals(messageBoardSection1, messageBoardSection2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		MessageBoardSection messageBoardSection = randomMessageBoardSection();

		String json1 = objectMapper.writeValueAsString(messageBoardSection);
		String json2 = MessageBoardSectionSerDes.toJSON(messageBoardSection);

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

		MessageBoardSection messageBoardSection = randomMessageBoardSection();

		messageBoardSection.setDescription(regex);
		messageBoardSection.setFriendlyUrlPath(regex);
		messageBoardSection.setTitle(regex);

		String json = MessageBoardSectionSerDes.toJSON(messageBoardSection);

		Assert.assertFalse(json.contains(regex));

		messageBoardSection = MessageBoardSectionSerDes.toDTO(json);

		Assert.assertEquals(regex, messageBoardSection.getDescription());
		Assert.assertEquals(regex, messageBoardSection.getFriendlyUrlPath());
		Assert.assertEquals(regex, messageBoardSection.getTitle());
	}

	@Test
	public void testDeleteMessageBoardSection() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardSection messageBoardSection =
			testDeleteMessageBoardSection_addMessageBoardSection();

		assertHttpResponseStatusCode(
			204,
			messageBoardSectionResource.deleteMessageBoardSectionHttpResponse(
				messageBoardSection.getId()));

		assertHttpResponseStatusCode(
			404,
			messageBoardSectionResource.getMessageBoardSectionHttpResponse(
				messageBoardSection.getId()));
		assertHttpResponseStatusCode(
			404,
			messageBoardSectionResource.getMessageBoardSectionHttpResponse(0L));
	}

	protected MessageBoardSection
			testDeleteMessageBoardSection_addMessageBoardSection()
		throws Exception {

		return messageBoardSectionResource.postSiteMessageBoardSection(
			testGroup.getGroupId(), randomMessageBoardSection());
	}

	@Test
	public void testGraphQLDeleteMessageBoardSection() throws Exception {

		// No namespace

		MessageBoardSection messageBoardSection1 =
			testGraphQLDeleteMessageBoardSection_addMessageBoardSection();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteMessageBoardSection",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardSectionId",
									messageBoardSection1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteMessageBoardSection"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"messageBoardSection",
					new HashMap<String, Object>() {
						{
							put(
								"messageBoardSectionId",
								messageBoardSection1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		MessageBoardSection messageBoardSection2 =
			testGraphQLDeleteMessageBoardSection_addMessageBoardSection();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteMessageBoardSection",
							new HashMap<String, Object>() {
								{
									put(
										"messageBoardSectionId",
										messageBoardSection2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteMessageBoardSection"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"messageBoardSection",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardSectionId",
									messageBoardSection2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected MessageBoardSection
			testGraphQLDeleteMessageBoardSection_addMessageBoardSection()
		throws Exception {

		return testGraphQLMessageBoardSection_addMessageBoardSection();
	}

	@Test
	public void testDeleteMessageBoardSectionBatch() throws Exception {
		MessageBoardSection messageBoardSection1 =
			testDeleteMessageBoardSectionBatch_addMessageBoardSection();

		testDeleteMessageBoardSectionBatch_deleteMessageBoardSection(
			202, null, messageBoardSection1.getId());

		assertHttpResponseStatusCode(
			404,
			messageBoardSectionResource.getMessageBoardSectionHttpResponse(
				messageBoardSection1.getId()));
	}

	protected MessageBoardSection
			testDeleteMessageBoardSectionBatch_addMessageBoardSection()
		throws Exception {

		return testDeleteMessageBoardSection_addMessageBoardSection();
	}

	protected void testDeleteMessageBoardSectionBatch_deleteMessageBoardSection(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			messageBoardSectionResource.
				deleteMessageBoardSectionBatchHttpResponse(
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
	public void testGetMessageBoardSection() throws Exception {
		MessageBoardSection postMessageBoardSection =
			testGetMessageBoardSection_addMessageBoardSection();

		MessageBoardSection getMessageBoardSection =
			messageBoardSectionResource.getMessageBoardSection(
				postMessageBoardSection.getId());

		assertEquals(postMessageBoardSection, getMessageBoardSection);
		assertValid(getMessageBoardSection);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		MessageBoardSection postMessageBoardSection =
			testGetMessageBoardSection_addMessageBoardSection();

		MessageBoardSection getMessageBoardSection =
			messageBoardSectionResource.getMessageBoardSection(
				postMessageBoardSection.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.delivery.dto.v1_0.MessageBoardSection"
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
			postMessageBoardSection.getId());

		assertEquals(
			getMessageBoardSection,
			MessageBoardSectionSerDes.toDTO(item.toString()));
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

	protected MessageBoardSection
			testGetMessageBoardSection_addMessageBoardSection()
		throws Exception {

		return messageBoardSectionResource.postSiteMessageBoardSection(
			testGroup.getGroupId(), randomMessageBoardSection());
	}

	@Test
	public void testGraphQLGetMessageBoardSection() throws Exception {
		MessageBoardSection messageBoardSection =
			testGraphQLGetMessageBoardSection_addMessageBoardSection();

		// No namespace

		Assert.assertTrue(
			equals(
				messageBoardSection,
				MessageBoardSectionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"messageBoardSection",
								new HashMap<String, Object>() {
									{
										put(
											"messageBoardSectionId",
											messageBoardSection.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/messageBoardSection"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				messageBoardSection,
				MessageBoardSectionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"messageBoardSection",
									new HashMap<String, Object>() {
										{
											put(
												"messageBoardSectionId",
												messageBoardSection.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/messageBoardSection"))));
	}

	@Test
	public void testGraphQLGetMessageBoardSectionNotFound() throws Exception {
		Long irrelevantMessageBoardSectionId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"messageBoardSection",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardSectionId",
									irrelevantMessageBoardSectionId);
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
							"messageBoardSection",
							new HashMap<String, Object>() {
								{
									put(
										"messageBoardSectionId",
										irrelevantMessageBoardSectionId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected MessageBoardSection
			testGraphQLGetMessageBoardSection_addMessageBoardSection()
		throws Exception {

		return testGraphQLMessageBoardSection_addMessageBoardSection();
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardSectionsPage()
		throws Exception {

		Long parentMessageBoardSectionId =
			testGetMessageBoardSectionMessageBoardSectionsPage_getParentMessageBoardSectionId();
		Long irrelevantParentMessageBoardSectionId =
			testGetMessageBoardSectionMessageBoardSectionsPage_getIrrelevantParentMessageBoardSectionId();

		Page<MessageBoardSection> page =
			messageBoardSectionResource.
				getMessageBoardSectionMessageBoardSectionsPage(
					parentMessageBoardSectionId, null, null, null,
					Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantParentMessageBoardSectionId != null) {
			MessageBoardSection irrelevantMessageBoardSection =
				testGetMessageBoardSectionMessageBoardSectionsPage_addMessageBoardSection(
					irrelevantParentMessageBoardSectionId,
					randomIrrelevantMessageBoardSection());

			page =
				messageBoardSectionResource.
					getMessageBoardSectionMessageBoardSectionsPage(
						irrelevantParentMessageBoardSectionId, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantMessageBoardSection,
				(List<MessageBoardSection>)page.getItems());
			assertValid(
				page,
				testGetMessageBoardSectionMessageBoardSectionsPage_getExpectedActions(
					irrelevantParentMessageBoardSectionId));
		}

		MessageBoardSection messageBoardSection1 =
			testGetMessageBoardSectionMessageBoardSectionsPage_addMessageBoardSection(
				parentMessageBoardSectionId, randomMessageBoardSection());

		MessageBoardSection messageBoardSection2 =
			testGetMessageBoardSectionMessageBoardSectionsPage_addMessageBoardSection(
				parentMessageBoardSectionId, randomMessageBoardSection());

		page =
			messageBoardSectionResource.
				getMessageBoardSectionMessageBoardSectionsPage(
					parentMessageBoardSectionId, null, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			messageBoardSection1, (List<MessageBoardSection>)page.getItems());
		assertContains(
			messageBoardSection2, (List<MessageBoardSection>)page.getItems());
		assertValid(
			page,
			testGetMessageBoardSectionMessageBoardSectionsPage_getExpectedActions(
				parentMessageBoardSectionId));

		messageBoardSectionResource.deleteMessageBoardSection(
			messageBoardSection1.getId());

		messageBoardSectionResource.deleteMessageBoardSection(
			messageBoardSection2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetMessageBoardSectionMessageBoardSectionsPage_getExpectedActions(
				Long parentMessageBoardSectionId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardSectionsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long parentMessageBoardSectionId =
			testGetMessageBoardSectionMessageBoardSectionsPage_getParentMessageBoardSectionId();

		MessageBoardSection messageBoardSection1 = randomMessageBoardSection();

		messageBoardSection1 =
			testGetMessageBoardSectionMessageBoardSectionsPage_addMessageBoardSection(
				parentMessageBoardSectionId, messageBoardSection1);

		for (EntityField entityField : entityFields) {
			Page<MessageBoardSection> page =
				messageBoardSectionResource.
					getMessageBoardSectionMessageBoardSectionsPage(
						parentMessageBoardSectionId, null, null,
						getFilterString(
							entityField, "between", messageBoardSection1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(messageBoardSection1),
				(List<MessageBoardSection>)page.getItems());
		}
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardSectionsPageWithFilterDoubleEquals()
		throws Exception {

		testGetMessageBoardSectionMessageBoardSectionsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardSectionsPageWithFilterStringContains()
		throws Exception {

		testGetMessageBoardSectionMessageBoardSectionsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardSectionsPageWithFilterStringEquals()
		throws Exception {

		testGetMessageBoardSectionMessageBoardSectionsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardSectionsPageWithFilterStringStartsWith()
		throws Exception {

		testGetMessageBoardSectionMessageBoardSectionsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetMessageBoardSectionMessageBoardSectionsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long parentMessageBoardSectionId =
			testGetMessageBoardSectionMessageBoardSectionsPage_getParentMessageBoardSectionId();

		MessageBoardSection messageBoardSection1 =
			testGetMessageBoardSectionMessageBoardSectionsPage_addMessageBoardSection(
				parentMessageBoardSectionId, randomMessageBoardSection());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardSection messageBoardSection2 =
			testGetMessageBoardSectionMessageBoardSectionsPage_addMessageBoardSection(
				parentMessageBoardSectionId, randomMessageBoardSection());

		for (EntityField entityField : entityFields) {
			Page<MessageBoardSection> page =
				messageBoardSectionResource.
					getMessageBoardSectionMessageBoardSectionsPage(
						parentMessageBoardSectionId, null, null,
						getFilterString(
							entityField, operator, messageBoardSection1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(messageBoardSection1),
				(List<MessageBoardSection>)page.getItems());
		}
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardSectionsPageWithPagination()
		throws Exception {

		Long parentMessageBoardSectionId =
			testGetMessageBoardSectionMessageBoardSectionsPage_getParentMessageBoardSectionId();

		Page<MessageBoardSection> messageBoardSectionsPage =
			messageBoardSectionResource.
				getMessageBoardSectionMessageBoardSectionsPage(
					parentMessageBoardSectionId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			messageBoardSectionsPage.getTotalCount());

		MessageBoardSection messageBoardSection1 =
			testGetMessageBoardSectionMessageBoardSectionsPage_addMessageBoardSection(
				parentMessageBoardSectionId, randomMessageBoardSection());

		MessageBoardSection messageBoardSection2 =
			testGetMessageBoardSectionMessageBoardSectionsPage_addMessageBoardSection(
				parentMessageBoardSectionId, randomMessageBoardSection());

		MessageBoardSection messageBoardSection3 =
			testGetMessageBoardSectionMessageBoardSectionsPage_addMessageBoardSection(
				parentMessageBoardSectionId, randomMessageBoardSection());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<MessageBoardSection> page1 =
				messageBoardSectionResource.
					getMessageBoardSectionMessageBoardSectionsPage(
						parentMessageBoardSectionId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				messageBoardSection1,
				(List<MessageBoardSection>)page1.getItems());

			Page<MessageBoardSection> page2 =
				messageBoardSectionResource.
					getMessageBoardSectionMessageBoardSectionsPage(
						parentMessageBoardSectionId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				messageBoardSection2,
				(List<MessageBoardSection>)page2.getItems());

			Page<MessageBoardSection> page3 =
				messageBoardSectionResource.
					getMessageBoardSectionMessageBoardSectionsPage(
						parentMessageBoardSectionId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				messageBoardSection3,
				(List<MessageBoardSection>)page3.getItems());
		}
		else {
			Page<MessageBoardSection> page1 =
				messageBoardSectionResource.
					getMessageBoardSectionMessageBoardSectionsPage(
						parentMessageBoardSectionId, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<MessageBoardSection> messageBoardSections1 =
				(List<MessageBoardSection>)page1.getItems();

			Assert.assertEquals(
				messageBoardSections1.toString(), totalCount + 2,
				messageBoardSections1.size());

			Page<MessageBoardSection> page2 =
				messageBoardSectionResource.
					getMessageBoardSectionMessageBoardSectionsPage(
						parentMessageBoardSectionId, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<MessageBoardSection> messageBoardSections2 =
				(List<MessageBoardSection>)page2.getItems();

			Assert.assertEquals(
				messageBoardSections2.toString(), 1,
				messageBoardSections2.size());

			Page<MessageBoardSection> page3 =
				messageBoardSectionResource.
					getMessageBoardSectionMessageBoardSectionsPage(
						parentMessageBoardSectionId, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				messageBoardSection1,
				(List<MessageBoardSection>)page3.getItems());
			assertContains(
				messageBoardSection2,
				(List<MessageBoardSection>)page3.getItems());
			assertContains(
				messageBoardSection3,
				(List<MessageBoardSection>)page3.getItems());
		}
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardSectionsPageWithSortDateTime()
		throws Exception {

		testGetMessageBoardSectionMessageBoardSectionsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, messageBoardSection1, messageBoardSection2) -> {
				BeanTestUtil.setProperty(
					messageBoardSection1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardSectionsPageWithSortDouble()
		throws Exception {

		testGetMessageBoardSectionMessageBoardSectionsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, messageBoardSection1, messageBoardSection2) -> {
				BeanTestUtil.setProperty(
					messageBoardSection1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					messageBoardSection2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardSectionsPageWithSortInteger()
		throws Exception {

		testGetMessageBoardSectionMessageBoardSectionsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, messageBoardSection1, messageBoardSection2) -> {
				BeanTestUtil.setProperty(
					messageBoardSection1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					messageBoardSection2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardSectionsPageWithSortString()
		throws Exception {

		testGetMessageBoardSectionMessageBoardSectionsPageWithSort(
			EntityField.Type.STRING,
			(entityField, messageBoardSection1, messageBoardSection2) -> {
				Class<?> clazz = messageBoardSection1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						messageBoardSection1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						messageBoardSection2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						messageBoardSection1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						messageBoardSection2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						messageBoardSection1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						messageBoardSection2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetMessageBoardSectionMessageBoardSectionsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, MessageBoardSection, MessageBoardSection,
				 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long parentMessageBoardSectionId =
			testGetMessageBoardSectionMessageBoardSectionsPage_getParentMessageBoardSectionId();

		MessageBoardSection messageBoardSection1 = randomMessageBoardSection();
		MessageBoardSection messageBoardSection2 = randomMessageBoardSection();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, messageBoardSection1, messageBoardSection2);
		}

		messageBoardSection1 =
			testGetMessageBoardSectionMessageBoardSectionsPage_addMessageBoardSection(
				parentMessageBoardSectionId, messageBoardSection1);

		messageBoardSection2 =
			testGetMessageBoardSectionMessageBoardSectionsPage_addMessageBoardSection(
				parentMessageBoardSectionId, messageBoardSection2);

		Page<MessageBoardSection> page =
			messageBoardSectionResource.
				getMessageBoardSectionMessageBoardSectionsPage(
					parentMessageBoardSectionId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<MessageBoardSection> ascPage =
				messageBoardSectionResource.
					getMessageBoardSectionMessageBoardSectionsPage(
						parentMessageBoardSectionId, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				messageBoardSection1,
				(List<MessageBoardSection>)ascPage.getItems());
			assertContains(
				messageBoardSection2,
				(List<MessageBoardSection>)ascPage.getItems());

			Page<MessageBoardSection> descPage =
				messageBoardSectionResource.
					getMessageBoardSectionMessageBoardSectionsPage(
						parentMessageBoardSectionId, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				messageBoardSection2,
				(List<MessageBoardSection>)descPage.getItems());
			assertContains(
				messageBoardSection1,
				(List<MessageBoardSection>)descPage.getItems());
		}
	}

	protected MessageBoardSection
			testGetMessageBoardSectionMessageBoardSectionsPage_addMessageBoardSection(
				Long parentMessageBoardSectionId,
				MessageBoardSection messageBoardSection)
		throws Exception {

		return messageBoardSectionResource.
			postMessageBoardSectionMessageBoardSection(
				parentMessageBoardSectionId, messageBoardSection);
	}

	protected Long
			testGetMessageBoardSectionMessageBoardSectionsPage_getParentMessageBoardSectionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetMessageBoardSectionMessageBoardSectionsPage_getIrrelevantParentMessageBoardSectionId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetMessageBoardSectionMessageBoardSectionsPage()
		throws Exception {

		Long parentMessageBoardSectionId =
			testGetMessageBoardSectionMessageBoardSectionsPage_getParentMessageBoardSectionId();

		GraphQLField graphQLField = new GraphQLField(
			"messageBoardSectionMessageBoardSections",
			new HashMap<String, Object>() {
				{
					put(
						"parentMessageBoardSectionId",
						parentMessageBoardSectionId);
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject messageBoardSectionMessageBoardSectionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/messageBoardSectionMessageBoardSections");

		long totalCount =
			messageBoardSectionMessageBoardSectionsJSONObject.getLong(
				"totalCount");

		MessageBoardSection messageBoardSection1 =
			testGraphQLGetMessageBoardSectionMessageBoardSectionsPageMessageBoardSection_addMessageBoardSection(
				parentMessageBoardSectionId, randomMessageBoardSection());

		MessageBoardSection messageBoardSection2 =
			testGraphQLGetMessageBoardSectionMessageBoardSectionsPageMessageBoardSection_addMessageBoardSection(
				parentMessageBoardSectionId, randomMessageBoardSection());

		messageBoardSectionMessageBoardSectionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/messageBoardSectionMessageBoardSections");

		Assert.assertEquals(
			totalCount + 2,
			messageBoardSectionMessageBoardSectionsJSONObject.getLong(
				"totalCount"));

		assertContains(
			messageBoardSection1,
			Arrays.asList(
				MessageBoardSectionSerDes.toDTOs(
					messageBoardSectionMessageBoardSectionsJSONObject.getString(
						"items"))));
		assertContains(
			messageBoardSection2,
			Arrays.asList(
				MessageBoardSectionSerDes.toDTOs(
					messageBoardSectionMessageBoardSectionsJSONObject.getString(
						"items"))));

		// Using the namespace headlessDelivery_v1_0

		messageBoardSectionMessageBoardSectionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("headlessDelivery_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"JSONObject/messageBoardSectionMessageBoardSections");

		Assert.assertEquals(
			totalCount + 2,
			messageBoardSectionMessageBoardSectionsJSONObject.getLong(
				"totalCount"));

		assertContains(
			messageBoardSection1,
			Arrays.asList(
				MessageBoardSectionSerDes.toDTOs(
					messageBoardSectionMessageBoardSectionsJSONObject.getString(
						"items"))));
		assertContains(
			messageBoardSection2,
			Arrays.asList(
				MessageBoardSectionSerDes.toDTOs(
					messageBoardSectionMessageBoardSectionsJSONObject.getString(
						"items"))));
	}

	protected MessageBoardSection
			testGraphQLGetMessageBoardSectionMessageBoardSectionsPageMessageBoardSection_addMessageBoardSection(
				Long parentMessageBoardSectionId,
				MessageBoardSection messageBoardSection)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetMessageBoardSectionPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardSection postMessageBoardSection =
			testGetMessageBoardSectionPermissionsPage_addMessageBoardSection();

		Page<Permission> page =
			messageBoardSectionResource.getMessageBoardSectionPermissionsPage(
				postMessageBoardSection.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected MessageBoardSection
			testGetMessageBoardSectionPermissionsPage_addMessageBoardSection()
		throws Exception {

		return messageBoardSectionResource.postSiteMessageBoardSection(
			testGroup.getGroupId(), randomMessageBoardSection());
	}

	@Test
	public void testGraphQLGetMessageBoardSectionPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardSection postMessageBoardSection =
			testGraphQLGetMessageBoardSectionPermissionsPage_addMessageBoardSection();

		GraphQLField graphQLField = new GraphQLField(
			"messageBoardSectionPermissions",
			new HashMap<String, Object>() {
				{
					put(
						"messageBoardSectionId",
						postMessageBoardSection.getId());
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject messageBoardSectionPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/messageBoardSectionPermissions");

		Assert.assertNotNull(messageBoardSectionPermissionsJSONObject);
	}

	protected MessageBoardSection
			testGraphQLGetMessageBoardSectionPermissionsPage_addMessageBoardSection()
		throws Exception {

		return testGraphQLMessageBoardSection_addMessageBoardSection();
	}

	@Test
	public void testGetSiteMessageBoardSectionByFriendlyUrlPath()
		throws Exception {

		MessageBoardSection postMessageBoardSection =
			testGetSiteMessageBoardSectionByFriendlyUrlPath_addMessageBoardSection();

		MessageBoardSection getMessageBoardSection =
			messageBoardSectionResource.
				getSiteMessageBoardSectionByFriendlyUrlPath(
					postMessageBoardSection.getSiteId(),
					postMessageBoardSection.getFriendlyUrlPath());

		assertEquals(postMessageBoardSection, getMessageBoardSection);
		assertValid(getMessageBoardSection);
	}

	protected MessageBoardSection
			testGetSiteMessageBoardSectionByFriendlyUrlPath_addMessageBoardSection()
		throws Exception {

		return messageBoardSectionResource.postSiteMessageBoardSection(
			testGroup.getGroupId(), randomMessageBoardSection());
	}

	@Test
	public void testGraphQLGetSiteMessageBoardSectionByFriendlyUrlPath()
		throws Exception {

		MessageBoardSection messageBoardSection =
			testGraphQLGetSiteMessageBoardSectionByFriendlyUrlPath_addMessageBoardSection();

		// No namespace

		Assert.assertTrue(
			equals(
				messageBoardSection,
				MessageBoardSectionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"messageBoardSectionByFriendlyUrlPath",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												messageBoardSection.
													getSiteId() + "\"");
										put(
											"friendlyUrlPath",
											"\"" +
												messageBoardSection.
													getFriendlyUrlPath() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/messageBoardSectionByFriendlyUrlPath"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				messageBoardSection,
				MessageBoardSectionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"messageBoardSectionByFriendlyUrlPath",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													messageBoardSection.
														getSiteId() + "\"");
											put(
												"friendlyUrlPath",
												"\"" +
													messageBoardSection.
														getFriendlyUrlPath() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/messageBoardSectionByFriendlyUrlPath"))));
	}

	@Test
	public void testGraphQLGetSiteMessageBoardSectionByFriendlyUrlPathNotFound()
		throws Exception {

		String irrelevantFriendlyUrlPath =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"messageBoardSectionByFriendlyUrlPath",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put(
									"friendlyUrlPath",
									irrelevantFriendlyUrlPath);
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
							"messageBoardSectionByFriendlyUrlPath",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put(
										"friendlyUrlPath",
										irrelevantFriendlyUrlPath);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected MessageBoardSection
			testGraphQLGetSiteMessageBoardSectionByFriendlyUrlPath_addMessageBoardSection()
		throws Exception {

		return testGraphQLSiteMessageBoardSection_addMessageBoardSection();
	}

	@Test
	public void testGetSiteMessageBoardSectionPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardSection postMessageBoardSection =
			testGetSiteMessageBoardSectionPermissionsPage_addMessageBoardSection();

		Page<Permission> page =
			messageBoardSectionResource.
				getSiteMessageBoardSectionPermissionsPage(
					testGroup.getGroupId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected MessageBoardSection
			testGetSiteMessageBoardSectionPermissionsPage_addMessageBoardSection()
		throws Exception {

		return messageBoardSectionResource.postSiteMessageBoardSection(
			testGroup.getGroupId(), randomMessageBoardSection());
	}

	@Test
	public void testGraphQLGetSiteMessageBoardSectionPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardSection postMessageBoardSection =
			testGraphQLGetSiteMessageBoardSectionPermissionsPage_addMessageBoardSection();

		GraphQLField graphQLField = new GraphQLField(
			"siteMessageBoardSectionPermissions",
			new HashMap<String, Object>() {
				{
					put(
						"siteKey",
						"\"" + postMessageBoardSection.getSiteId() + "\"");
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject siteMessageBoardSectionPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/siteMessageBoardSectionPermissions");

		Assert.assertNotNull(siteMessageBoardSectionPermissionsJSONObject);
	}

	protected MessageBoardSection
			testGraphQLGetSiteMessageBoardSectionPermissionsPage_addMessageBoardSection()
		throws Exception {

		return testGraphQLSiteMessageBoardSection_addMessageBoardSection();
	}

	@Test
	public void testGetSiteMessageBoardSectionsPage() throws Exception {
		Long siteId = testGetSiteMessageBoardSectionsPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteMessageBoardSectionsPage_getIrrelevantSiteId();

		Page<MessageBoardSection> page =
			messageBoardSectionResource.getSiteMessageBoardSectionsPage(
				siteId, null, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			MessageBoardSection irrelevantMessageBoardSection =
				testGetSiteMessageBoardSectionsPage_addMessageBoardSection(
					irrelevantSiteId, randomIrrelevantMessageBoardSection());

			page = messageBoardSectionResource.getSiteMessageBoardSectionsPage(
				irrelevantSiteId, null, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantMessageBoardSection,
				(List<MessageBoardSection>)page.getItems());
			assertValid(
				page,
				testGetSiteMessageBoardSectionsPage_getExpectedActions(
					irrelevantSiteId));
		}

		MessageBoardSection messageBoardSection1 =
			testGetSiteMessageBoardSectionsPage_addMessageBoardSection(
				siteId, randomMessageBoardSection());

		MessageBoardSection messageBoardSection2 =
			testGetSiteMessageBoardSectionsPage_addMessageBoardSection(
				siteId, randomMessageBoardSection());

		page = messageBoardSectionResource.getSiteMessageBoardSectionsPage(
			siteId, null, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			messageBoardSection1, (List<MessageBoardSection>)page.getItems());
		assertContains(
			messageBoardSection2, (List<MessageBoardSection>)page.getItems());
		assertValid(
			page,
			testGetSiteMessageBoardSectionsPage_getExpectedActions(siteId));

		messageBoardSectionResource.deleteMessageBoardSection(
			messageBoardSection1.getId());

		messageBoardSectionResource.deleteMessageBoardSection(
			messageBoardSection2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteMessageBoardSectionsPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/sites/{siteId}/message-board-sections/batch".
				replace("{siteId}", String.valueOf(siteId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteMessageBoardSectionsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteMessageBoardSectionsPage_getSiteId();

		MessageBoardSection messageBoardSection1 = randomMessageBoardSection();

		messageBoardSection1 =
			testGetSiteMessageBoardSectionsPage_addMessageBoardSection(
				siteId, messageBoardSection1);

		for (EntityField entityField : entityFields) {
			Page<MessageBoardSection> page =
				messageBoardSectionResource.getSiteMessageBoardSectionsPage(
					siteId, null, null, null,
					getFilterString(
						entityField, "between", messageBoardSection1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(messageBoardSection1),
				(List<MessageBoardSection>)page.getItems());
		}
	}

	@Test
	public void testGetSiteMessageBoardSectionsPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteMessageBoardSectionsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteMessageBoardSectionsPageWithFilterStringContains()
		throws Exception {

		testGetSiteMessageBoardSectionsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteMessageBoardSectionsPageWithFilterStringEquals()
		throws Exception {

		testGetSiteMessageBoardSectionsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteMessageBoardSectionsPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteMessageBoardSectionsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteMessageBoardSectionsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteMessageBoardSectionsPage_getSiteId();

		MessageBoardSection messageBoardSection1 =
			testGetSiteMessageBoardSectionsPage_addMessageBoardSection(
				siteId, randomMessageBoardSection());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardSection messageBoardSection2 =
			testGetSiteMessageBoardSectionsPage_addMessageBoardSection(
				siteId, randomMessageBoardSection());

		for (EntityField entityField : entityFields) {
			Page<MessageBoardSection> page =
				messageBoardSectionResource.getSiteMessageBoardSectionsPage(
					siteId, null, null, null,
					getFilterString(
						entityField, operator, messageBoardSection1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(messageBoardSection1),
				(List<MessageBoardSection>)page.getItems());
		}
	}

	@Test
	public void testGetSiteMessageBoardSectionsPageWithPagination()
		throws Exception {

		Long siteId = testGetSiteMessageBoardSectionsPage_getSiteId();

		Page<MessageBoardSection> messageBoardSectionsPage =
			messageBoardSectionResource.getSiteMessageBoardSectionsPage(
				siteId, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			messageBoardSectionsPage.getTotalCount());

		MessageBoardSection messageBoardSection1 =
			testGetSiteMessageBoardSectionsPage_addMessageBoardSection(
				siteId, randomMessageBoardSection());

		MessageBoardSection messageBoardSection2 =
			testGetSiteMessageBoardSectionsPage_addMessageBoardSection(
				siteId, randomMessageBoardSection());

		MessageBoardSection messageBoardSection3 =
			testGetSiteMessageBoardSectionsPage_addMessageBoardSection(
				siteId, randomMessageBoardSection());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<MessageBoardSection> page1 =
				messageBoardSectionResource.getSiteMessageBoardSectionsPage(
					siteId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				messageBoardSection1,
				(List<MessageBoardSection>)page1.getItems());

			Page<MessageBoardSection> page2 =
				messageBoardSectionResource.getSiteMessageBoardSectionsPage(
					siteId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				messageBoardSection2,
				(List<MessageBoardSection>)page2.getItems());

			Page<MessageBoardSection> page3 =
				messageBoardSectionResource.getSiteMessageBoardSectionsPage(
					siteId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				messageBoardSection3,
				(List<MessageBoardSection>)page3.getItems());
		}
		else {
			Page<MessageBoardSection> page1 =
				messageBoardSectionResource.getSiteMessageBoardSectionsPage(
					siteId, null, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<MessageBoardSection> messageBoardSections1 =
				(List<MessageBoardSection>)page1.getItems();

			Assert.assertEquals(
				messageBoardSections1.toString(), totalCount + 2,
				messageBoardSections1.size());

			Page<MessageBoardSection> page2 =
				messageBoardSectionResource.getSiteMessageBoardSectionsPage(
					siteId, null, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<MessageBoardSection> messageBoardSections2 =
				(List<MessageBoardSection>)page2.getItems();

			Assert.assertEquals(
				messageBoardSections2.toString(), 1,
				messageBoardSections2.size());

			Page<MessageBoardSection> page3 =
				messageBoardSectionResource.getSiteMessageBoardSectionsPage(
					siteId, null, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				messageBoardSection1,
				(List<MessageBoardSection>)page3.getItems());
			assertContains(
				messageBoardSection2,
				(List<MessageBoardSection>)page3.getItems());
			assertContains(
				messageBoardSection3,
				(List<MessageBoardSection>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteMessageBoardSectionsPageWithSortDateTime()
		throws Exception {

		testGetSiteMessageBoardSectionsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, messageBoardSection1, messageBoardSection2) -> {
				BeanTestUtil.setProperty(
					messageBoardSection1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteMessageBoardSectionsPageWithSortDouble()
		throws Exception {

		testGetSiteMessageBoardSectionsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, messageBoardSection1, messageBoardSection2) -> {
				BeanTestUtil.setProperty(
					messageBoardSection1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					messageBoardSection2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteMessageBoardSectionsPageWithSortInteger()
		throws Exception {

		testGetSiteMessageBoardSectionsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, messageBoardSection1, messageBoardSection2) -> {
				BeanTestUtil.setProperty(
					messageBoardSection1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					messageBoardSection2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteMessageBoardSectionsPageWithSortString()
		throws Exception {

		testGetSiteMessageBoardSectionsPageWithSort(
			EntityField.Type.STRING,
			(entityField, messageBoardSection1, messageBoardSection2) -> {
				Class<?> clazz = messageBoardSection1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						messageBoardSection1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						messageBoardSection2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						messageBoardSection1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						messageBoardSection2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						messageBoardSection1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						messageBoardSection2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteMessageBoardSectionsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, MessageBoardSection, MessageBoardSection,
				 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteMessageBoardSectionsPage_getSiteId();

		MessageBoardSection messageBoardSection1 = randomMessageBoardSection();
		MessageBoardSection messageBoardSection2 = randomMessageBoardSection();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, messageBoardSection1, messageBoardSection2);
		}

		messageBoardSection1 =
			testGetSiteMessageBoardSectionsPage_addMessageBoardSection(
				siteId, messageBoardSection1);

		messageBoardSection2 =
			testGetSiteMessageBoardSectionsPage_addMessageBoardSection(
				siteId, messageBoardSection2);

		Page<MessageBoardSection> page =
			messageBoardSectionResource.getSiteMessageBoardSectionsPage(
				siteId, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<MessageBoardSection> ascPage =
				messageBoardSectionResource.getSiteMessageBoardSectionsPage(
					siteId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				messageBoardSection1,
				(List<MessageBoardSection>)ascPage.getItems());
			assertContains(
				messageBoardSection2,
				(List<MessageBoardSection>)ascPage.getItems());

			Page<MessageBoardSection> descPage =
				messageBoardSectionResource.getSiteMessageBoardSectionsPage(
					siteId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				messageBoardSection2,
				(List<MessageBoardSection>)descPage.getItems());
			assertContains(
				messageBoardSection1,
				(List<MessageBoardSection>)descPage.getItems());
		}
	}

	protected MessageBoardSection
			testGetSiteMessageBoardSectionsPage_addMessageBoardSection(
				Long siteId, MessageBoardSection messageBoardSection)
		throws Exception {

		return messageBoardSectionResource.postSiteMessageBoardSection(
			siteId, messageBoardSection);
	}

	protected Long testGetSiteMessageBoardSectionsPage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long testGetSiteMessageBoardSectionsPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteMessageBoardSectionsPage() throws Exception {
		Long siteId = testGetSiteMessageBoardSectionsPage_getSiteId();

		GraphQLField graphQLField = new GraphQLField(
			"messageBoardSections",
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

		JSONObject messageBoardSectionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/messageBoardSections");

		long totalCount = messageBoardSectionsJSONObject.getLong("totalCount");

		MessageBoardSection messageBoardSection1 =
			testGraphQLSiteMessageBoardSection_addMessageBoardSection(
				siteId, randomMessageBoardSection());

		MessageBoardSection messageBoardSection2 =
			testGraphQLSiteMessageBoardSection_addMessageBoardSection(
				siteId, randomMessageBoardSection());

		messageBoardSectionsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/messageBoardSections");

		Assert.assertEquals(
			totalCount + 2,
			messageBoardSectionsJSONObject.getLong("totalCount"));

		assertContains(
			messageBoardSection1,
			Arrays.asList(
				MessageBoardSectionSerDes.toDTOs(
					messageBoardSectionsJSONObject.getString("items"))));
		assertContains(
			messageBoardSection2,
			Arrays.asList(
				MessageBoardSectionSerDes.toDTOs(
					messageBoardSectionsJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		messageBoardSectionsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/messageBoardSections");

		Assert.assertEquals(
			totalCount + 2,
			messageBoardSectionsJSONObject.getLong("totalCount"));

		assertContains(
			messageBoardSection1,
			Arrays.asList(
				MessageBoardSectionSerDes.toDTOs(
					messageBoardSectionsJSONObject.getString("items"))));
		assertContains(
			messageBoardSection2,
			Arrays.asList(
				MessageBoardSectionSerDes.toDTOs(
					messageBoardSectionsJSONObject.getString("items"))));
	}

	@Test
	public void testPatchMessageBoardSection() throws Exception {
		MessageBoardSection postMessageBoardSection =
			testPatchMessageBoardSection_addMessageBoardSection();

		MessageBoardSection randomPatchMessageBoardSection =
			randomPatchMessageBoardSection();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardSection patchMessageBoardSection =
			messageBoardSectionResource.patchMessageBoardSection(
				postMessageBoardSection.getId(),
				randomPatchMessageBoardSection);

		MessageBoardSection expectedPatchMessageBoardSection =
			postMessageBoardSection.clone();

		BeanTestUtil.copyProperties(
			randomPatchMessageBoardSection, expectedPatchMessageBoardSection);

		MessageBoardSection getMessageBoardSection =
			messageBoardSectionResource.getMessageBoardSection(
				patchMessageBoardSection.getId());

		assertEquals(expectedPatchMessageBoardSection, getMessageBoardSection);
		assertValid(getMessageBoardSection);
	}

	protected MessageBoardSection
			testPatchMessageBoardSection_addMessageBoardSection()
		throws Exception {

		return messageBoardSectionResource.postSiteMessageBoardSection(
			testGroup.getGroupId(), randomMessageBoardSection());
	}

	@Test
	public void testPostMessageBoardSectionMessageBoardSection()
		throws Exception {

		MessageBoardSection randomMessageBoardSection =
			randomMessageBoardSection();

		MessageBoardSection postMessageBoardSection =
			testPostMessageBoardSectionMessageBoardSection_addMessageBoardSection(
				randomMessageBoardSection);

		assertEquals(randomMessageBoardSection, postMessageBoardSection);
		assertValid(postMessageBoardSection);
	}

	protected MessageBoardSection
			testPostMessageBoardSectionMessageBoardSection_addMessageBoardSection(
				MessageBoardSection messageBoardSection)
		throws Exception {

		return messageBoardSectionResource.
			postMessageBoardSectionMessageBoardSection(
				testGetMessageBoardSectionMessageBoardSectionsPage_getParentMessageBoardSectionId(),
				messageBoardSection);
	}

	@Test
	public void testGraphQLPostMessageBoardSectionMessageBoardSection()
		throws Exception {

		MessageBoardSection randomMessageBoardSection =
			randomMessageBoardSection();

		MessageBoardSection messageBoardSection =
			testGraphQLMessageBoardSection_addMessageBoardSection(
				testGroup.getGroupId(), randomMessageBoardSection);

		Assert.assertTrue(
			equals(randomMessageBoardSection, messageBoardSection));
	}

	@Test
	public void testPostSiteMessageBoardSection() throws Exception {
		MessageBoardSection randomMessageBoardSection =
			randomMessageBoardSection();

		MessageBoardSection postMessageBoardSection =
			testPostSiteMessageBoardSection_addMessageBoardSection(
				randomMessageBoardSection);

		assertEquals(randomMessageBoardSection, postMessageBoardSection);
		assertValid(postMessageBoardSection);
	}

	protected MessageBoardSection
			testPostSiteMessageBoardSection_addMessageBoardSection(
				MessageBoardSection messageBoardSection)
		throws Exception {

		return messageBoardSectionResource.postSiteMessageBoardSection(
			testGetSiteMessageBoardSectionsPage_getSiteId(),
			messageBoardSection);
	}

	@Test
	public void testGraphQLPostSiteMessageBoardSection() throws Exception {
		MessageBoardSection randomMessageBoardSection =
			randomMessageBoardSection();

		MessageBoardSection messageBoardSection =
			testGraphQLSiteMessageBoardSection_addMessageBoardSection(
				testGroup.getGroupId(), randomMessageBoardSection);

		Assert.assertTrue(
			equals(randomMessageBoardSection, messageBoardSection));
	}

	@Test
	public void testPutMessageBoardSection() throws Exception {
		MessageBoardSection postMessageBoardSection =
			testPutMessageBoardSection_addMessageBoardSection();

		MessageBoardSection randomMessageBoardSection =
			randomMessageBoardSection();

		MessageBoardSection putMessageBoardSection =
			messageBoardSectionResource.putMessageBoardSection(
				postMessageBoardSection.getId(), randomMessageBoardSection);

		assertEquals(randomMessageBoardSection, putMessageBoardSection);
		assertValid(putMessageBoardSection);

		MessageBoardSection getMessageBoardSection =
			messageBoardSectionResource.getMessageBoardSection(
				putMessageBoardSection.getId());

		assertEquals(randomMessageBoardSection, getMessageBoardSection);
		assertValid(getMessageBoardSection);
	}

	protected MessageBoardSection
			testPutMessageBoardSection_addMessageBoardSection()
		throws Exception {

		return messageBoardSectionResource.postSiteMessageBoardSection(
			testGroup.getGroupId(), randomMessageBoardSection());
	}

	@Test
	public void testPutMessageBoardSectionPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardSection messageBoardSection =
			testPutMessageBoardSectionPermissionsPage_addMessageBoardSection();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			messageBoardSectionResource.
				putMessageBoardSectionPermissionsPageHttpResponse(
					messageBoardSection.getId(),
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
			messageBoardSectionResource.
				putMessageBoardSectionPermissionsPageHttpResponse(
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

	protected MessageBoardSection
			testPutMessageBoardSectionPermissionsPage_addMessageBoardSection()
		throws Exception {

		return messageBoardSectionResource.postSiteMessageBoardSection(
			testGroup.getGroupId(), randomMessageBoardSection());
	}

	@Test
	public void testPutMessageBoardSectionSubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardSection messageBoardSection =
			testPutMessageBoardSectionSubscribe_addMessageBoardSection();

		assertHttpResponseStatusCode(
			204,
			messageBoardSectionResource.
				putMessageBoardSectionSubscribeHttpResponse(
					messageBoardSection.getId()));

		assertHttpResponseStatusCode(
			404,
			messageBoardSectionResource.
				putMessageBoardSectionSubscribeHttpResponse(0L));
	}

	protected MessageBoardSection
			testPutMessageBoardSectionSubscribe_addMessageBoardSection()
		throws Exception {

		return messageBoardSectionResource.postSiteMessageBoardSection(
			testGroup.getGroupId(), randomMessageBoardSection());
	}

	@Test
	public void testPutMessageBoardSectionUnsubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardSection messageBoardSection =
			testPutMessageBoardSectionUnsubscribe_addMessageBoardSection();

		assertHttpResponseStatusCode(
			204,
			messageBoardSectionResource.
				putMessageBoardSectionUnsubscribeHttpResponse(
					messageBoardSection.getId()));

		assertHttpResponseStatusCode(
			404,
			messageBoardSectionResource.
				putMessageBoardSectionUnsubscribeHttpResponse(0L));
	}

	protected MessageBoardSection
			testPutMessageBoardSectionUnsubscribe_addMessageBoardSection()
		throws Exception {

		return messageBoardSectionResource.postSiteMessageBoardSection(
			testGroup.getGroupId(), randomMessageBoardSection());
	}

	@Test
	public void testPutSiteMessageBoardSectionPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardSection messageBoardSection =
			testPutSiteMessageBoardSectionPermissionsPage_addMessageBoardSection();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			messageBoardSectionResource.
				putSiteMessageBoardSectionPermissionsPageHttpResponse(
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
			messageBoardSectionResource.
				putSiteMessageBoardSectionPermissionsPageHttpResponse(
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

	protected MessageBoardSection
			testPutSiteMessageBoardSectionPermissionsPage_addMessageBoardSection()
		throws Exception {

		return messageBoardSectionResource.postSiteMessageBoardSection(
			testGroup.getGroupId(), randomMessageBoardSection());
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		MessageBoardSection messageBoardSection1 =
			testBatchEngineDeleteImportTask_addMessageBoardSection();

		testBatchEngineDeleteImportTask_deleteMessageBoardSection(
			200, null, messageBoardSection1.getId());

		assertHttpResponseStatusCode(
			404,
			messageBoardSectionResource.getMessageBoardSectionHttpResponse(
				messageBoardSection1.getId()));
	}

	protected MessageBoardSection
			testBatchEngineDeleteImportTask_addMessageBoardSection()
		throws Exception {

		return testDeleteMessageBoardSection_addMessageBoardSection();
	}

	protected void testBatchEngineDeleteImportTask_deleteMessageBoardSection(
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
				"com.liferay.headless.delivery.dto.v1_0.MessageBoardSection",
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

	protected MessageBoardSection
			testGraphQLMessageBoardSection_addMessageBoardSection()
		throws Exception {

		return testGraphQLMessageBoardSection_addMessageBoardSection(
			testGroup.getGroupId(), randomMessageBoardSection());
	}

	protected MessageBoardSection
			testGraphQLMessageBoardSection_addMessageBoardSection(
				Long siteId, MessageBoardSection messageBoardSection)
		throws Exception {

		JSONDeserializer<MessageBoardSection> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(MessageBoardSection.class)) {

			if (getGraphQLValue(field.get(messageBoardSection)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(messageBoardSection)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteMessageBoardSection",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("messageBoardSection", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteMessageBoardSection"),
			MessageBoardSection.class);
	}

	protected MessageBoardSection
			testGraphQLSiteMessageBoardSection_addMessageBoardSection()
		throws Exception {

		return testGraphQLSiteMessageBoardSection_addMessageBoardSection(
			testGroup.getGroupId(), randomMessageBoardSection());
	}

	protected MessageBoardSection
			testGraphQLSiteMessageBoardSection_addMessageBoardSection(
				Long siteId, MessageBoardSection messageBoardSection)
		throws Exception {

		JSONDeserializer<MessageBoardSection> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(MessageBoardSection.class)) {

			if (getGraphQLValue(field.get(messageBoardSection)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(messageBoardSection)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteMessageBoardSection",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("messageBoardSection", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteMessageBoardSection"),
			MessageBoardSection.class);
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
		MessageBoardSection messageBoardSection,
		List<MessageBoardSection> messageBoardSections) {

		boolean contains = false;

		for (MessageBoardSection item : messageBoardSections) {
			if (equals(messageBoardSection, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			messageBoardSections + " does not contain " + messageBoardSection,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		MessageBoardSection messageBoardSection1,
		MessageBoardSection messageBoardSection2) {

		Assert.assertTrue(
			messageBoardSection1 + " does not equal " + messageBoardSection2,
			equals(messageBoardSection1, messageBoardSection2));
	}

	protected void assertEquals(
		List<MessageBoardSection> messageBoardSections1,
		List<MessageBoardSection> messageBoardSections2) {

		Assert.assertEquals(
			messageBoardSections1.size(), messageBoardSections2.size());

		for (int i = 0; i < messageBoardSections1.size(); i++) {
			MessageBoardSection messageBoardSection1 =
				messageBoardSections1.get(i);
			MessageBoardSection messageBoardSection2 =
				messageBoardSections2.get(i);

			assertEquals(messageBoardSection1, messageBoardSection2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<MessageBoardSection> messageBoardSections1,
		List<MessageBoardSection> messageBoardSections2) {

		Assert.assertEquals(
			messageBoardSections1.size(), messageBoardSections2.size());

		for (MessageBoardSection messageBoardSection1 : messageBoardSections1) {
			boolean contains = false;

			for (MessageBoardSection messageBoardSection2 :
					messageBoardSections2) {

				if (equals(messageBoardSection1, messageBoardSection2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				messageBoardSections2 + " does not contain " +
					messageBoardSection1,
				contains);
		}
	}

	protected void assertValid(MessageBoardSection messageBoardSection)
		throws Exception {

		boolean valid = true;

		if (messageBoardSection.getDateCreated() == null) {
			valid = false;
		}

		if (messageBoardSection.getDateModified() == null) {
			valid = false;
		}

		if (messageBoardSection.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				messageBoardSection.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (messageBoardSection.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (messageBoardSection.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (messageBoardSection.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (messageBoardSection.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (messageBoardSection.getFriendlyUrlPath() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfMessageBoardSections",
					additionalAssertFieldName)) {

				if (messageBoardSection.getNumberOfMessageBoardSections() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfMessageBoardThreads", additionalAssertFieldName)) {

				if (messageBoardSection.getNumberOfMessageBoardThreads() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parentMessageBoardSectionId", additionalAssertFieldName)) {

				if (messageBoardSection.getParentMessageBoardSectionId() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("subscribed", additionalAssertFieldName)) {
				if (messageBoardSection.getSubscribed() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (messageBoardSection.getTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (messageBoardSection.getViewableBy() == null) {
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

	protected void assertValid(Page<MessageBoardSection> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<MessageBoardSection> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<MessageBoardSection> messageBoardSections =
			page.getItems();

		int size = messageBoardSections.size();

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

		graphQLFields.add(new GraphQLField("id"));

		graphQLFields.add(new GraphQLField("siteId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.delivery.dto.v1_0.MessageBoardSection.
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
		MessageBoardSection messageBoardSection1,
		MessageBoardSection messageBoardSection2) {

		if (messageBoardSection1 == messageBoardSection2) {
			return true;
		}

		if (!Objects.equals(
				messageBoardSection1.getSiteId(),
				messageBoardSection2.getSiteId())) {

			return false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)messageBoardSection1.getActions(),
						(Map)messageBoardSection2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardSection1.getCreator(),
						messageBoardSection2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardSection1.getCustomFields(),
						messageBoardSection2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardSection1.getDateCreated(),
						messageBoardSection2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardSection1.getDateModified(),
						messageBoardSection2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardSection1.getDescription(),
						messageBoardSection2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardSection1.getFriendlyUrlPath(),
						messageBoardSection2.getFriendlyUrlPath())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardSection1.getId(),
						messageBoardSection2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfMessageBoardSections",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						messageBoardSection1.getNumberOfMessageBoardSections(),
						messageBoardSection2.
							getNumberOfMessageBoardSections())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfMessageBoardThreads", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						messageBoardSection1.getNumberOfMessageBoardThreads(),
						messageBoardSection2.
							getNumberOfMessageBoardThreads())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parentMessageBoardSectionId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						messageBoardSection1.getParentMessageBoardSectionId(),
						messageBoardSection2.
							getParentMessageBoardSectionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("subscribed", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardSection1.getSubscribed(),
						messageBoardSection2.getSubscribed())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardSection1.getTitle(),
						messageBoardSection2.getTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardSection1.getViewableBy(),
						messageBoardSection2.getViewableBy())) {

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

		if (!(_messageBoardSectionResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_messageBoardSectionResource;

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
		MessageBoardSection messageBoardSection) {

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

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = messageBoardSection.getDateCreated();

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

				sb.append(_format.format(messageBoardSection.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = messageBoardSection.getDateModified();

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
					_format.format(messageBoardSection.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = messageBoardSection.getDescription();

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

		if (entityFieldName.equals("friendlyUrlPath")) {
			Object object = messageBoardSection.getFriendlyUrlPath();

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

		if (entityFieldName.equals("numberOfMessageBoardSections")) {
			sb.append(
				String.valueOf(
					messageBoardSection.getNumberOfMessageBoardSections()));

			return sb.toString();
		}

		if (entityFieldName.equals("numberOfMessageBoardThreads")) {
			sb.append(
				String.valueOf(
					messageBoardSection.getNumberOfMessageBoardThreads()));

			return sb.toString();
		}

		if (entityFieldName.equals("parentMessageBoardSectionId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("subscribed")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("title")) {
			Object object = messageBoardSection.getTitle();

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

	protected MessageBoardSection randomMessageBoardSection() throws Exception {
		return new MessageBoardSection() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				friendlyUrlPath = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				numberOfMessageBoardSections = RandomTestUtil.randomInt();
				numberOfMessageBoardThreads = RandomTestUtil.randomInt();
				parentMessageBoardSectionId = RandomTestUtil.randomLong();
				siteId = testGroup.getGroupId();
				subscribed = RandomTestUtil.randomBoolean();
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected MessageBoardSection randomIrrelevantMessageBoardSection()
		throws Exception {

		MessageBoardSection randomIrrelevantMessageBoardSection =
			randomMessageBoardSection();

		randomIrrelevantMessageBoardSection.setSiteId(
			irrelevantGroup.getGroupId());

		return randomIrrelevantMessageBoardSection;
	}

	protected MessageBoardSection randomPatchMessageBoardSection()
		throws Exception {

		return randomMessageBoardSection();
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

	protected MessageBoardSectionResource messageBoardSectionResource;
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
		LogFactoryUtil.getLog(BaseMessageBoardSectionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.delivery.resource.v1_0.MessageBoardSectionResource
			_messageBoardSectionResource;

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