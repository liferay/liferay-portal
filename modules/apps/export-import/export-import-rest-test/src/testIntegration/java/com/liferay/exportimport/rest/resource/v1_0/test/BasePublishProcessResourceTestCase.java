/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.exportimport.rest.client.dto.v1_0.ProcessProgress;
import com.liferay.exportimport.rest.client.dto.v1_0.PublishProcess;
import com.liferay.exportimport.rest.client.dto.v1_0.Type;
import com.liferay.exportimport.rest.client.http.HttpInvoker;
import com.liferay.exportimport.rest.client.pagination.Page;
import com.liferay.exportimport.rest.client.pagination.Pagination;
import com.liferay.exportimport.rest.client.resource.v1_0.PublishProcessResource;
import com.liferay.exportimport.rest.client.serdes.v1_0.PublishProcessSerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.test.util.JAXRSWhiteboardTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
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
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public abstract class BasePublishProcessResourceTestCase {

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

		JAXRSWhiteboardTestUtil.ensureReady();
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_publishProcessResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		publishProcessResource = PublishProcessResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
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

		PublishProcess publishProcess1 = randomPublishProcess();

		String json = objectMapper.writeValueAsString(publishProcess1);

		PublishProcess publishProcess2 = PublishProcessSerDes.toDTO(json);

		Assert.assertTrue(equals(publishProcess1, publishProcess2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PublishProcess publishProcess = randomPublishProcess();

		String json1 = objectMapper.writeValueAsString(publishProcess);
		String json2 = PublishProcessSerDes.toJSON(publishProcess);

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

		PublishProcess publishProcess = randomPublishProcess();

		publishProcess.setErrorMessage(regex);
		publishProcess.setName(regex);

		String json = PublishProcessSerDes.toJSON(publishProcess);

		Assert.assertFalse(json.contains(regex));

		publishProcess = PublishProcessSerDes.toDTO(json);

		Assert.assertEquals(regex, publishProcess.getErrorMessage());
		Assert.assertEquals(regex, publishProcess.getName());
	}

	@Test
	public void testDeletePublishProcess() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		PublishProcess publishProcess =
			testDeletePublishProcess_addPublishProcess();

		assertHttpResponseStatusCode(
			204,
			publishProcessResource.deletePublishProcessHttpResponse(
				publishProcess.getId()));

		assertHttpResponseStatusCode(
			404,
			publishProcessResource.getPublishProcessHttpResponse(
				publishProcess.getId()));
		assertHttpResponseStatusCode(
			404, publishProcessResource.getPublishProcessHttpResponse(0L));
	}

	protected PublishProcess testDeletePublishProcess_addPublishProcess()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testDeletePublishProcessBatch() throws Exception {
		PublishProcess publishProcess1 =
			testDeletePublishProcessBatch_addPublishProcess();

		testDeletePublishProcessBatch_deletePublishProcess(
			202, null, publishProcess1.getId());

		assertHttpResponseStatusCode(
			404,
			publishProcessResource.getPublishProcessHttpResponse(
				publishProcess1.getId()));
	}

	protected PublishProcess testDeletePublishProcessBatch_addPublishProcess()
		throws Exception {

		return testDeletePublishProcess_addPublishProcess();
	}

	protected void testDeletePublishProcessBatch_deletePublishProcess(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			publishProcessResource.deletePublishProcessBatchHttpResponse(
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
	public void testGetPublishProcess() throws Exception {
		PublishProcess postPublishProcess =
			testGetPublishProcess_addPublishProcess();

		PublishProcess getPublishProcess =
			publishProcessResource.getPublishProcess(
				postPublishProcess.getId());

		assertEquals(postPublishProcess, getPublishProcess);
		assertValid(getPublishProcess);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		PublishProcess postPublishProcess =
			testGetPublishProcess_addPublishProcess();

		PublishProcess getPublishProcess =
			publishProcessResource.getPublishProcess(
				postPublishProcess.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.exportimport.rest.dto.v1_0.PublishProcess"
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
			postPublishProcess.getId());

		assertEquals(
			getPublishProcess, PublishProcessSerDes.toDTO(item.toString()));
	}

	protected HttpServletRequest
		testVulcanCRUDItemDelegate_getHttpServletRequest() {

		return new MockHttpServletRequest() {

			@Override
			public StringBuffer getRequestURL() {
				return new StringBuffer(
					StringBundler.concat(
						"http://localhost:",
						String.valueOf(PortalUtil.getPortalServerPort(false)),
						"/o/v1.0/", RandomTestUtil.randomString(), "/",
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
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false), "/o/",
						applicationPath, resourcePath));
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
				return URI.create(
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false), "/o/",
						applicationPath));
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

	protected PublishProcess testGetPublishProcess_addPublishProcess()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSitePublishProcessesPage() throws Exception {
		String siteExternalReferenceCode =
			testGetSitePublishProcessesPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSitePublishProcessesPage_getIrrelevantSiteExternalReferenceCode();

		Page<PublishProcess> page =
			publishProcessResource.getSitePublishProcessesPage(
				siteExternalReferenceCode, null, null, null,
				Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteExternalReferenceCode != null) {
			PublishProcess irrelevantPublishProcess =
				testGetSitePublishProcessesPage_addPublishProcess(
					irrelevantSiteExternalReferenceCode,
					randomIrrelevantPublishProcess());

			page = publishProcessResource.getSitePublishProcessesPage(
				irrelevantSiteExternalReferenceCode, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPublishProcess,
				(List<PublishProcess>)page.getItems());
			assertValid(
				page,
				testGetSitePublishProcessesPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode));
		}

		PublishProcess publishProcess1 =
			testGetSitePublishProcessesPage_addPublishProcess(
				siteExternalReferenceCode, randomPublishProcess());

		PublishProcess publishProcess2 =
			testGetSitePublishProcessesPage_addPublishProcess(
				siteExternalReferenceCode, randomPublishProcess());

		page = publishProcessResource.getSitePublishProcessesPage(
			siteExternalReferenceCode, null, null, null,
			Pagination.of(1, (int)totalCount + 2), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(publishProcess1, (List<PublishProcess>)page.getItems());
		assertContains(publishProcess2, (List<PublishProcess>)page.getItems());
		assertValid(
			page,
			testGetSitePublishProcessesPage_getExpectedActions(
				siteExternalReferenceCode));

		publishProcessResource.deletePublishProcess(publishProcess1.getId());

		publishProcessResource.deletePublishProcess(publishProcess2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSitePublishProcessesPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			("http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/export-import/v1.0/sites/{siteExternalReferenceCode}/publish-processes/batch").
					replace(
						"{siteExternalReferenceCode}",
						String.valueOf(siteExternalReferenceCode)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSitePublishProcessesPageWithPagination()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSitePublishProcessesPage_getSiteExternalReferenceCode();

		Page<PublishProcess> publishProcessesPage =
			publishProcessResource.getSitePublishProcessesPage(
				siteExternalReferenceCode, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			publishProcessesPage.getTotalCount());

		PublishProcess publishProcess1 =
			testGetSitePublishProcessesPage_addPublishProcess(
				siteExternalReferenceCode, randomPublishProcess());

		PublishProcess publishProcess2 =
			testGetSitePublishProcessesPage_addPublishProcess(
				siteExternalReferenceCode, randomPublishProcess());

		PublishProcess publishProcess3 =
			testGetSitePublishProcessesPage_addPublishProcess(
				siteExternalReferenceCode, randomPublishProcess());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PublishProcess> page1 =
				publishProcessResource.getSitePublishProcessesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				publishProcess1, (List<PublishProcess>)page1.getItems());

			Page<PublishProcess> page2 =
				publishProcessResource.getSitePublishProcessesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				publishProcess2, (List<PublishProcess>)page2.getItems());

			Page<PublishProcess> page3 =
				publishProcessResource.getSitePublishProcessesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				publishProcess3, (List<PublishProcess>)page3.getItems());
		}
		else {
			Page<PublishProcess> page1 =
				publishProcessResource.getSitePublishProcessesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<PublishProcess> publishProcesses1 =
				(List<PublishProcess>)page1.getItems();

			Assert.assertEquals(
				publishProcesses1.toString(), totalCount + 2,
				publishProcesses1.size());

			Page<PublishProcess> page2 =
				publishProcessResource.getSitePublishProcessesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PublishProcess> publishProcesses2 =
				(List<PublishProcess>)page2.getItems();

			Assert.assertEquals(
				publishProcesses2.toString(), 1, publishProcesses2.size());

			Page<PublishProcess> page3 =
				publishProcessResource.getSitePublishProcessesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				publishProcess1, (List<PublishProcess>)page3.getItems());
			assertContains(
				publishProcess2, (List<PublishProcess>)page3.getItems());
			assertContains(
				publishProcess3, (List<PublishProcess>)page3.getItems());
		}
	}

	@Test
	public void testGetSitePublishProcessesPageWithSortDateTime()
		throws Exception {

		testGetSitePublishProcessesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, publishProcess1, publishProcess2) -> {
				BeanTestUtil.setProperty(
					publishProcess1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSitePublishProcessesPageWithSortDouble()
		throws Exception {

		testGetSitePublishProcessesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, publishProcess1, publishProcess2) -> {
				BeanTestUtil.setProperty(
					publishProcess1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					publishProcess2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSitePublishProcessesPageWithSortInteger()
		throws Exception {

		testGetSitePublishProcessesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, publishProcess1, publishProcess2) -> {
				BeanTestUtil.setProperty(
					publishProcess1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					publishProcess2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSitePublishProcessesPageWithSortString()
		throws Exception {

		testGetSitePublishProcessesPageWithSort(
			EntityField.Type.STRING,
			(entityField, publishProcess1, publishProcess2) -> {
				Class<?> clazz = publishProcess1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						publishProcess1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						publishProcess2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						publishProcess1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						publishProcess2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						publishProcess1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						publishProcess2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSitePublishProcessesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, PublishProcess, PublishProcess, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSitePublishProcessesPage_getSiteExternalReferenceCode();

		PublishProcess publishProcess1 = randomPublishProcess();
		PublishProcess publishProcess2 = randomPublishProcess();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, publishProcess1, publishProcess2);
		}

		publishProcess1 = testGetSitePublishProcessesPage_addPublishProcess(
			siteExternalReferenceCode, publishProcess1);

		publishProcess2 = testGetSitePublishProcessesPage_addPublishProcess(
			siteExternalReferenceCode, publishProcess2);

		Page<PublishProcess> page =
			publishProcessResource.getSitePublishProcessesPage(
				siteExternalReferenceCode, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PublishProcess> ascPage =
				publishProcessResource.getSitePublishProcessesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				publishProcess1, (List<PublishProcess>)ascPage.getItems());
			assertContains(
				publishProcess2, (List<PublishProcess>)ascPage.getItems());

			Page<PublishProcess> descPage =
				publishProcessResource.getSitePublishProcessesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				publishProcess2, (List<PublishProcess>)descPage.getItems());
			assertContains(
				publishProcess1, (List<PublishProcess>)descPage.getItems());
		}
	}

	protected PublishProcess testGetSitePublishProcessesPage_addPublishProcess(
			String siteExternalReferenceCode, PublishProcess publishProcess)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSitePublishProcessesPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSitePublishProcessesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Test
	public void testPostPublishProcessRelaunch() throws Exception {
		PublishProcess randomPublishProcess = randomPublishProcess();

		PublishProcess postPublishProcess =
			testPostPublishProcessRelaunch_addPublishProcess(
				randomPublishProcess);

		assertEquals(randomPublishProcess, postPublishProcess);
		assertValid(postPublishProcess);
	}

	protected PublishProcess testPostPublishProcessRelaunch_addPublishProcess(
			PublishProcess publishProcess)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSitePublishProcess() throws Exception {
		PublishProcess randomPublishProcess = randomPublishProcess();

		PublishProcess postPublishProcess =
			testPostSitePublishProcess_addPublishProcess(randomPublishProcess);

		assertEquals(randomPublishProcess, postPublishProcess);
		assertValid(postPublishProcess);
	}

	protected PublishProcess testPostSitePublishProcess_addPublishProcess(
			PublishProcess publishProcess)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		PublishProcess publishProcess1 =
			testBatchEngineDeleteImportTask_addPublishProcess();

		testBatchEngineDeleteImportTask_deletePublishProcess(
			200, null, publishProcess1.getId());

		assertHttpResponseStatusCode(
			404,
			publishProcessResource.getPublishProcessHttpResponse(
				publishProcess1.getId()));
	}

	protected PublishProcess testBatchEngineDeleteImportTask_addPublishProcess()
		throws Exception {

		return testDeletePublishProcess_addPublishProcess();
	}

	protected void testBatchEngineDeleteImportTask_deletePublishProcess(
			int expectedStatusCode, String externalReferenceCode, Long id,
			String... parameters)
		throws Exception {

		ImportTaskResource importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).parameters(
			parameters
		).build();

		HttpResponse httpResponse =
			importTaskResource.deleteImportTaskHttpResponse(
				"com.liferay.exportimport.rest.dto.v1_0.PublishProcess", null,
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

	@Test
	public void testGetPublishProcessProgress() throws Exception {
		PublishProcess postPublishProcess =
			testGetPublishProcess_addPublishProcess();

		ProcessProgress postProcessProgress =
			testGetPublishProcessProgress_addProcessProgress(
				postPublishProcess.getId(), randomProcessProgress());

		ProcessProgress getProcessProgress =
			publishProcessResource.getPublishProcessProgress(
				postPublishProcess.getId());

		assertEquals(postProcessProgress, getProcessProgress);
		assertValid(getProcessProgress);
	}

	protected ProcessProgress testGetPublishProcessProgress_addProcessProgress(
			long publishProcessId, ProcessProgress processProgress)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		PublishProcess publishProcess, List<PublishProcess> publishProcesses) {

		boolean contains = false;

		for (PublishProcess item : publishProcesses) {
			if (equals(publishProcess, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			publishProcesses + " does not contain " + publishProcess, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PublishProcess publishProcess1, PublishProcess publishProcess2) {

		Assert.assertTrue(
			publishProcess1 + " does not equal " + publishProcess2,
			equals(publishProcess1, publishProcess2));
	}

	protected void assertEquals(
		List<PublishProcess> publishProcesses1,
		List<PublishProcess> publishProcesses2) {

		Assert.assertEquals(publishProcesses1.size(), publishProcesses2.size());

		for (int i = 0; i < publishProcesses1.size(); i++) {
			PublishProcess publishProcess1 = publishProcesses1.get(i);
			PublishProcess publishProcess2 = publishProcesses2.get(i);

			assertEquals(publishProcess1, publishProcess2);
		}
	}

	protected void assertEquals(
		ProcessProgress processProgress1, ProcessProgress processProgress2) {

		Assert.assertTrue(
			processProgress1 + " does not equal " + processProgress2,
			equals(processProgress1, processProgress2));
	}

	protected void assertEqualsIgnoringOrder(
		List<PublishProcess> publishProcesses1,
		List<PublishProcess> publishProcesses2) {

		Assert.assertEquals(publishProcesses1.size(), publishProcesses2.size());

		for (PublishProcess publishProcess1 : publishProcesses1) {
			boolean contains = false;

			for (PublishProcess publishProcess2 : publishProcesses2) {
				if (equals(publishProcess1, publishProcess2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				publishProcesses2 + " does not contain " + publishProcess1,
				contains);
		}
	}

	protected void assertValid(PublishProcess publishProcess) throws Exception {
		boolean valid = true;

		if (publishProcess.getDateCreated() == null) {
			valid = false;
		}

		if (publishProcess.getDateModified() == null) {
			valid = false;
		}

		if (publishProcess.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (publishProcess.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dateCompleted", additionalAssertFieldName)) {
				if (publishProcess.getDateCompleted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("errorMessage", additionalAssertFieldName)) {
				if (publishProcess.getErrorMessage() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (publishProcess.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (publishProcess.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (publishProcess.getType() == null) {
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

	protected void assertValid(Page<PublishProcess> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PublishProcess> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PublishProcess> publishProcesses = page.getItems();

		int size = publishProcesses.size();

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

	protected void assertValid(ProcessProgress processProgress) {
		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalProcessProgressAssertFieldNames()) {

			if (Objects.equals("percentage", additionalAssertFieldName)) {
				if (processProgress.getPercentage() == null) {
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

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected String[] getAdditionalProcessProgressAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		graphQLFields.add(new GraphQLField("id"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.exportimport.rest.dto.v1_0.PublishProcess.
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
		PublishProcess publishProcess1, PublishProcess publishProcess2) {

		if (publishProcess1 == publishProcess2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						publishProcess1.getCreator(),
						publishProcess2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCompleted", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						publishProcess1.getDateCompleted(),
						publishProcess2.getDateCompleted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						publishProcess1.getDateCreated(),
						publishProcess2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						publishProcess1.getDateModified(),
						publishProcess2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("errorMessage", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						publishProcess1.getErrorMessage(),
						publishProcess2.getErrorMessage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						publishProcess1.getId(), publishProcess2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						publishProcess1.getName(), publishProcess2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						publishProcess1.getStatus(),
						publishProcess2.getStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						publishProcess1.getType(), publishProcess2.getType())) {

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

	protected boolean equals(
		ProcessProgress processProgress1, ProcessProgress processProgress2) {

		if (processProgress1 == processProgress2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalProcessProgressAssertFieldNames()) {

			if (Objects.equals("percentage", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						processProgress1.getPercentage(),
						processProgress2.getPercentage())) {

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

		if (!(_publishProcessResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_publishProcessResource;

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
		PublishProcess publishProcess) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCompleted")) {
			if (operator.equals("between")) {
				Date date = publishProcess.getDateCompleted();

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

				sb.append(_format.format(publishProcess.getDateCompleted()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = publishProcess.getDateCreated();

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

				sb.append(_format.format(publishProcess.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = publishProcess.getDateModified();

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

				sb.append(_format.format(publishProcess.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("errorMessage")) {
			Object object = publishProcess.getErrorMessage();

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
			Object object = publishProcess.getName();

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

		if (entityFieldName.equals("type")) {
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
		httpInvoker.path(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/graphql");
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

	protected PublishProcess randomPublishProcess() throws Exception {
		return new PublishProcess() {
			{
				dateCompleted = RandomTestUtil.nextDate();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				errorMessage = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected PublishProcess randomIrrelevantPublishProcess() throws Exception {
		PublishProcess randomIrrelevantPublishProcess = randomPublishProcess();

		return randomIrrelevantPublishProcess;
	}

	protected PublishProcess randomPatchPublishProcess() throws Exception {
		return randomPublishProcess();
	}

	protected ProcessProgress randomProcessProgress() throws Exception {
		return new ProcessProgress() {
			{
				percentage = RandomTestUtil.randomInt();
			}
		};
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

	protected PublishProcessResource publishProcessResource;
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
		LogFactoryUtil.getLog(BasePublishProcessResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.exportimport.rest.resource.v1_0.PublishProcessResource
		_publishProcessResource;

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
// LIFERAY-REST-BUILDER-HASH:559692138