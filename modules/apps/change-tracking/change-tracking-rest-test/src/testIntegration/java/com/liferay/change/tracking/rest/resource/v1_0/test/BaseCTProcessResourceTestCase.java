/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.change.tracking.rest.client.dto.v1_0.CTProcess;
import com.liferay.change.tracking.rest.client.http.HttpInvoker;
import com.liferay.change.tracking.rest.client.pagination.Page;
import com.liferay.change.tracking.rest.client.pagination.Pagination;
import com.liferay.change.tracking.rest.client.resource.v1_0.CTProcessResource;
import com.liferay.change.tracking.rest.client.serdes.v1_0.CTProcessSerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
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
 * @author David Truong
 * @generated
 */
@Generated("")
public abstract class BaseCTProcessResourceTestCase {

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

		_ctProcessResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		ctProcessResource = CTProcessResource.builder(
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

		CTProcess ctProcess1 = randomCTProcess();

		String json = objectMapper.writeValueAsString(ctProcess1);

		CTProcess ctProcess2 = CTProcessSerDes.toDTO(json);

		Assert.assertTrue(equals(ctProcess1, ctProcess2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		CTProcess ctProcess = randomCTProcess();

		String json1 = objectMapper.writeValueAsString(ctProcess);
		String json2 = CTProcessSerDes.toJSON(ctProcess);

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

		CTProcess ctProcess = randomCTProcess();

		ctProcess.setDescription(regex);
		ctProcess.setName(regex);
		ctProcess.setOwnerName(regex);

		String json = CTProcessSerDes.toJSON(ctProcess);

		Assert.assertFalse(json.contains(regex));

		ctProcess = CTProcessSerDes.toDTO(json);

		Assert.assertEquals(regex, ctProcess.getDescription());
		Assert.assertEquals(regex, ctProcess.getName());
		Assert.assertEquals(regex, ctProcess.getOwnerName());
	}

	@Test
	public void testDeleteCTProcess() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		CTProcess ctProcess = testDeleteCTProcess_addCTProcess();

		assertHttpResponseStatusCode(
			204,
			ctProcessResource.deleteCTProcessHttpResponse(ctProcess.getId()));

		assertHttpResponseStatusCode(
			404, ctProcessResource.getCTProcessHttpResponse(ctProcess.getId()));
		assertHttpResponseStatusCode(
			404, ctProcessResource.getCTProcessHttpResponse(0L));
	}

	protected CTProcess testDeleteCTProcess_addCTProcess() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteCTProcess() throws Exception {

		// No namespace

		CTProcess ctProcess1 = testGraphQLDeleteCTProcess_addCTProcess();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteCTProcess",
						new HashMap<String, Object>() {
							{
								put("ctProcessId", ctProcess1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteCTProcess"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"cTProcess",
					new HashMap<String, Object>() {
						{
							put("ctProcessId", ctProcess1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace changeTracking_v1_0

		CTProcess ctProcess2 = testGraphQLDeleteCTProcess_addCTProcess();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"changeTracking_v1_0",
						new GraphQLField(
							"deleteCTProcess",
							new HashMap<String, Object>() {
								{
									put("ctProcessId", ctProcess2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/changeTracking_v1_0",
				"Object/deleteCTProcess"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"changeTracking_v1_0",
					new GraphQLField(
						"cTProcess",
						new HashMap<String, Object>() {
							{
								put("ctProcessId", ctProcess2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected CTProcess testGraphQLDeleteCTProcess_addCTProcess()
		throws Exception {

		return testGraphQLCTProcess_addCTProcess();
	}

	@Test
	public void testDeleteCTProcessBatch() throws Exception {
		CTProcess ctProcess1 = testDeleteCTProcessBatch_addCTProcess();

		testDeleteCTProcessBatch_deleteCTProcess(202, null, ctProcess1.getId());

		assertHttpResponseStatusCode(
			404,
			ctProcessResource.getCTProcessHttpResponse(ctProcess1.getId()));
	}

	protected CTProcess testDeleteCTProcessBatch_addCTProcess()
		throws Exception {

		return testDeleteCTProcess_addCTProcess();
	}

	protected void testDeleteCTProcessBatch_deleteCTProcess(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			ctProcessResource.deleteCTProcessBatchHttpResponse(
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
	public void testGetCTProcess() throws Exception {
		CTProcess postCTProcess = testGetCTProcess_addCTProcess();

		CTProcess getCTProcess = ctProcessResource.getCTProcess(
			postCTProcess.getId());

		assertEquals(postCTProcess, getCTProcess);
		assertValid(getCTProcess);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		CTProcess postCTProcess = testGetCTProcess_addCTProcess();

		CTProcess getCTProcess = ctProcessResource.getCTProcess(
			postCTProcess.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.change.tracking.rest.dto.v1_0.CTProcess"
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

		Object item = vulcanCRUDItemDelegate.getItem(postCTProcess.getId());

		assertEquals(getCTProcess, CTProcessSerDes.toDTO(item.toString()));
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

	protected CTProcess testGetCTProcess_addCTProcess() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCTProcess() throws Exception {
		CTProcess ctProcess = testGraphQLGetCTProcess_addCTProcess();

		// No namespace

		Assert.assertTrue(
			equals(
				ctProcess,
				CTProcessSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"cTProcess",
								new HashMap<String, Object>() {
									{
										put("ctProcessId", ctProcess.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/cTProcess"))));

		// Using the namespace changeTracking_v1_0

		Assert.assertTrue(
			equals(
				ctProcess,
				CTProcessSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"changeTracking_v1_0",
								new GraphQLField(
									"cTProcess",
									new HashMap<String, Object>() {
										{
											put(
												"ctProcessId",
												ctProcess.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/changeTracking_v1_0",
						"Object/cTProcess"))));
	}

	@Test
	public void testGraphQLGetCTProcessNotFound() throws Exception {
		Long irrelevantCtProcessId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"cTProcess",
						new HashMap<String, Object>() {
							{
								put("ctProcessId", irrelevantCtProcessId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace changeTracking_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"changeTracking_v1_0",
						new GraphQLField(
							"cTProcess",
							new HashMap<String, Object>() {
								{
									put("ctProcessId", irrelevantCtProcessId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected CTProcess testGraphQLGetCTProcess_addCTProcess()
		throws Exception {

		return testGraphQLCTProcess_addCTProcess();
	}

	@Test
	public void testGetCTProcessesPage() throws Exception {
		Page<CTProcess> page = ctProcessResource.getCTProcessesPage(
			null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		CTProcess ctProcess1 = testGetCTProcessesPage_addCTProcess(
			randomCTProcess());

		CTProcess ctProcess2 = testGetCTProcessesPage_addCTProcess(
			randomCTProcess());

		page = ctProcessResource.getCTProcessesPage(
			null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(ctProcess1, (List<CTProcess>)page.getItems());
		assertContains(ctProcess2, (List<CTProcess>)page.getItems());
		assertValid(page, testGetCTProcessesPage_getExpectedActions());

		ctProcessResource.deleteCTProcess(ctProcess1.getId());

		ctProcessResource.deleteCTProcess(ctProcess2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetCTProcessesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetCTProcessesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		CTProcess ctProcess1 = randomCTProcess();

		ctProcess1 = testGetCTProcessesPage_addCTProcess(ctProcess1);

		for (EntityField entityField : entityFields) {
			Page<CTProcess> page = ctProcessResource.getCTProcessesPage(
				null, null, getFilterString(entityField, "between", ctProcess1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(ctProcess1),
				(List<CTProcess>)page.getItems());
		}
	}

	@Test
	public void testGetCTProcessesPageWithFilterDoubleEquals()
		throws Exception {

		testGetCTProcessesPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetCTProcessesPageWithFilterStringContains()
		throws Exception {

		testGetCTProcessesPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetCTProcessesPageWithFilterStringEquals()
		throws Exception {

		testGetCTProcessesPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetCTProcessesPageWithFilterStringStartsWith()
		throws Exception {

		testGetCTProcessesPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetCTProcessesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		CTProcess ctProcess1 = testGetCTProcessesPage_addCTProcess(
			randomCTProcess());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		CTProcess ctProcess2 = testGetCTProcessesPage_addCTProcess(
			randomCTProcess());

		for (EntityField entityField : entityFields) {
			Page<CTProcess> page = ctProcessResource.getCTProcessesPage(
				null, null, getFilterString(entityField, operator, ctProcess1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(ctProcess1),
				(List<CTProcess>)page.getItems());
		}
	}

	@Test
	public void testGetCTProcessesPageWithPagination() throws Exception {
		Page<CTProcess> ctProcessesPage = ctProcessResource.getCTProcessesPage(
			null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(ctProcessesPage.getTotalCount());

		CTProcess ctProcess1 = testGetCTProcessesPage_addCTProcess(
			randomCTProcess());

		CTProcess ctProcess2 = testGetCTProcessesPage_addCTProcess(
			randomCTProcess());

		CTProcess ctProcess3 = testGetCTProcessesPage_addCTProcess(
			randomCTProcess());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<CTProcess> page1 = ctProcessResource.getCTProcessesPage(
				null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(ctProcess1, (List<CTProcess>)page1.getItems());

			Page<CTProcess> page2 = ctProcessResource.getCTProcessesPage(
				null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(ctProcess2, (List<CTProcess>)page2.getItems());

			Page<CTProcess> page3 = ctProcessResource.getCTProcessesPage(
				null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(ctProcess3, (List<CTProcess>)page3.getItems());
		}
		else {
			Page<CTProcess> page1 = ctProcessResource.getCTProcessesPage(
				null, null, null, Pagination.of(1, totalCount + 2), null);

			List<CTProcess> ctProcesses1 = (List<CTProcess>)page1.getItems();

			Assert.assertEquals(
				ctProcesses1.toString(), totalCount + 2, ctProcesses1.size());

			Page<CTProcess> page2 = ctProcessResource.getCTProcessesPage(
				null, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<CTProcess> ctProcesses2 = (List<CTProcess>)page2.getItems();

			Assert.assertEquals(
				ctProcesses2.toString(), 1, ctProcesses2.size());

			Page<CTProcess> page3 = ctProcessResource.getCTProcessesPage(
				null, null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(ctProcess1, (List<CTProcess>)page3.getItems());
			assertContains(ctProcess2, (List<CTProcess>)page3.getItems());
			assertContains(ctProcess3, (List<CTProcess>)page3.getItems());
		}
	}

	@Test
	public void testGetCTProcessesPageWithSortDateTime() throws Exception {
		testGetCTProcessesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, ctProcess1, ctProcess2) -> {
				BeanTestUtil.setProperty(
					ctProcess1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetCTProcessesPageWithSortDouble() throws Exception {
		testGetCTProcessesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, ctProcess1, ctProcess2) -> {
				BeanTestUtil.setProperty(
					ctProcess1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					ctProcess2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetCTProcessesPageWithSortInteger() throws Exception {
		testGetCTProcessesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, ctProcess1, ctProcess2) -> {
				BeanTestUtil.setProperty(ctProcess1, entityField.getName(), 0);
				BeanTestUtil.setProperty(ctProcess2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetCTProcessesPageWithSortString() throws Exception {
		testGetCTProcessesPageWithSort(
			EntityField.Type.STRING,
			(entityField, ctProcess1, ctProcess2) -> {
				Class<?> clazz = ctProcess1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						ctProcess1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						ctProcess2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						ctProcess1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						ctProcess2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						ctProcess1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						ctProcess2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetCTProcessesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, CTProcess, CTProcess, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		CTProcess ctProcess1 = randomCTProcess();
		CTProcess ctProcess2 = randomCTProcess();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, ctProcess1, ctProcess2);
		}

		ctProcess1 = testGetCTProcessesPage_addCTProcess(ctProcess1);

		ctProcess2 = testGetCTProcessesPage_addCTProcess(ctProcess2);

		Page<CTProcess> page = ctProcessResource.getCTProcessesPage(
			null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<CTProcess> ascPage = ctProcessResource.getCTProcessesPage(
				null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(ctProcess1, (List<CTProcess>)ascPage.getItems());
			assertContains(ctProcess2, (List<CTProcess>)ascPage.getItems());

			Page<CTProcess> descPage = ctProcessResource.getCTProcessesPage(
				null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(ctProcess2, (List<CTProcess>)descPage.getItems());
			assertContains(ctProcess1, (List<CTProcess>)descPage.getItems());
		}
	}

	protected CTProcess testGetCTProcessesPage_addCTProcess(CTProcess ctProcess)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostCTProcessRevert() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		CTProcess ctProcess = testPostCTProcessRevert_addCTProcess();

		assertHttpResponseStatusCode(
			204,
			ctProcessResource.postCTProcessRevertHttpResponse(
				ctProcess.getId(), null, null));

		assertHttpResponseStatusCode(
			404,
			ctProcessResource.postCTProcessRevertHttpResponse(0L, null, null));
	}

	protected CTProcess testPostCTProcessRevert_addCTProcess()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		CTProcess ctProcess1 = testBatchEngineDeleteImportTask_addCTProcess();

		testBatchEngineDeleteImportTask_deleteCTProcess(
			200, null, ctProcess1.getId());

		assertHttpResponseStatusCode(
			404,
			ctProcessResource.getCTProcessHttpResponse(ctProcess1.getId()));
	}

	protected CTProcess testBatchEngineDeleteImportTask_addCTProcess()
		throws Exception {

		return testDeleteCTProcess_addCTProcess();
	}

	protected void testBatchEngineDeleteImportTask_deleteCTProcess(
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
				"com.liferay.change.tracking.rest.dto.v1_0.CTProcess", null,
				null, null, null,
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

	protected CTProcess testGraphQLCTProcess_addCTProcess() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		CTProcess ctProcess, List<CTProcess> ctProcesses) {

		boolean contains = false;

		for (CTProcess item : ctProcesses) {
			if (equals(ctProcess, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			ctProcesses + " does not contain " + ctProcess, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(CTProcess ctProcess1, CTProcess ctProcess2) {
		Assert.assertTrue(
			ctProcess1 + " does not equal " + ctProcess2,
			equals(ctProcess1, ctProcess2));
	}

	protected void assertEquals(
		List<CTProcess> ctProcesses1, List<CTProcess> ctProcesses2) {

		Assert.assertEquals(ctProcesses1.size(), ctProcesses2.size());

		for (int i = 0; i < ctProcesses1.size(); i++) {
			CTProcess ctProcess1 = ctProcesses1.get(i);
			CTProcess ctProcess2 = ctProcesses2.get(i);

			assertEquals(ctProcess1, ctProcess2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<CTProcess> ctProcesses1, List<CTProcess> ctProcesses2) {

		Assert.assertEquals(ctProcesses1.size(), ctProcesses2.size());

		for (CTProcess ctProcess1 : ctProcesses1) {
			boolean contains = false;

			for (CTProcess ctProcess2 : ctProcesses2) {
				if (equals(ctProcess1, ctProcess2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				ctProcesses2 + " does not contain " + ctProcess1, contains);
		}
	}

	protected void assertValid(CTProcess ctProcess) throws Exception {
		boolean valid = true;

		if (ctProcess.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (ctProcess.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("ctCollectionId", additionalAssertFieldName)) {
				if (ctProcess.getCtCollectionId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (ctProcess.getDatePublished() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (ctProcess.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (ctProcess.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("ownerName", additionalAssertFieldName)) {
				if (ctProcess.getOwnerName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (ctProcess.getStatus() == null) {
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

	protected void assertValid(Page<CTProcess> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<CTProcess> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<CTProcess> ctProcesses = page.getItems();

		int size = ctProcesses.size();

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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.change.tracking.rest.dto.v1_0.CTProcess.
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

	protected boolean equals(CTProcess ctProcess1, CTProcess ctProcess2) {
		if (ctProcess1 == ctProcess2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)ctProcess1.getActions(),
						(Map)ctProcess2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("ctCollectionId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						ctProcess1.getCtCollectionId(),
						ctProcess2.getCtCollectionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						ctProcess1.getDatePublished(),
						ctProcess2.getDatePublished())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						ctProcess1.getDescription(),
						ctProcess2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						ctProcess1.getId(), ctProcess2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						ctProcess1.getName(), ctProcess2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("ownerName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						ctProcess1.getOwnerName(), ctProcess2.getOwnerName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						ctProcess1.getStatus(), ctProcess2.getStatus())) {

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

		if (!(_ctProcessResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_ctProcessResource;

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
		EntityField entityField, String operator, CTProcess ctProcess) {

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

		if (entityFieldName.equals("ctCollectionId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("datePublished")) {
			if (operator.equals("between")) {
				Date date = ctProcess.getDatePublished();

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

				sb.append(_format.format(ctProcess.getDatePublished()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = ctProcess.getDescription();

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
			Object object = ctProcess.getName();

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

		if (entityFieldName.equals("ownerName")) {
			Object object = ctProcess.getOwnerName();

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

		if (entityFieldName.equals("status")) {
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

	protected CTProcess randomCTProcess() throws Exception {
		return new CTProcess() {
			{
				ctCollectionId = RandomTestUtil.randomLong();
				datePublished = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				ownerName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected CTProcess randomIrrelevantCTProcess() throws Exception {
		CTProcess randomIrrelevantCTProcess = randomCTProcess();

		return randomIrrelevantCTProcess;
	}

	protected CTProcess randomPatchCTProcess() throws Exception {
		return randomCTProcess();
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

	protected CTProcessResource ctProcessResource;
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
		LogFactoryUtil.getLog(BaseCTProcessResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.change.tracking.rest.resource.v1_0.CTProcessResource
		_ctProcessResource;

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