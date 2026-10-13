/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.digital.signature.rest.client.dto.v1_0.SignatureRequest;
import com.liferay.digital.signature.rest.client.http.HttpInvoker;
import com.liferay.digital.signature.rest.client.pagination.Page;
import com.liferay.digital.signature.rest.client.pagination.Pagination;
import com.liferay.digital.signature.rest.client.resource.v1_0.SignatureRequestResource;
import com.liferay.digital.signature.rest.client.serdes.v1_0.SignatureRequestSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.test.util.JAXRSWhiteboardTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
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
 * @author José Abelenda
 * @generated
 */
@Generated("")
public abstract class BaseSignatureRequestResourceTestCase {

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

		_signatureRequestResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		signatureRequestResource = SignatureRequestResource.builder(
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

		SignatureRequest signatureRequest1 = randomSignatureRequest();

		String json = objectMapper.writeValueAsString(signatureRequest1);

		SignatureRequest signatureRequest2 = SignatureRequestSerDes.toDTO(json);

		Assert.assertTrue(equals(signatureRequest1, signatureRequest2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		SignatureRequest signatureRequest = randomSignatureRequest();

		String json1 = objectMapper.writeValueAsString(signatureRequest);
		String json2 = SignatureRequestSerDes.toJSON(signatureRequest);

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

		SignatureRequest signatureRequest = randomSignatureRequest();

		signatureRequest.setEmailMessage(regex);
		signatureRequest.setEmailSubject(regex);
		signatureRequest.setName(regex);
		signatureRequest.setStatus(regex);

		String json = SignatureRequestSerDes.toJSON(signatureRequest);

		Assert.assertFalse(json.contains(regex));

		signatureRequest = SignatureRequestSerDes.toDTO(json);

		Assert.assertEquals(regex, signatureRequest.getEmailMessage());
		Assert.assertEquals(regex, signatureRequest.getEmailSubject());
		Assert.assertEquals(regex, signatureRequest.getName());
		Assert.assertEquals(regex, signatureRequest.getStatus());
	}

	@Test
	public void testGetSignatureRequest() throws Exception {
		SignatureRequest postSignatureRequest =
			testGetSignatureRequest_addSignatureRequest();

		SignatureRequest getSignatureRequest =
			signatureRequestResource.getSignatureRequest(
				postSignatureRequest.getId());

		assertEquals(postSignatureRequest, getSignatureRequest);
		assertValid(getSignatureRequest);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		SignatureRequest postSignatureRequest =
			testGetSignatureRequest_addSignatureRequest();

		SignatureRequest getSignatureRequest =
			signatureRequestResource.getSignatureRequest(
				postSignatureRequest.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.digital.signature.rest.dto.v1_0.SignatureRequest"
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
			postSignatureRequest.getId());

		assertEquals(
			getSignatureRequest, SignatureRequestSerDes.toDTO(item.toString()));
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

	protected SignatureRequest testGetSignatureRequest_addSignatureRequest()
		throws Exception {

		return signatureRequestResource.postSiteSignatureRequest(
			testGroup.getGroupId(), randomSignatureRequest());
	}

	@Test
	public void testGraphQLGetSignatureRequest() throws Exception {
		SignatureRequest signatureRequest =
			testGraphQLGetSignatureRequest_addSignatureRequest();

		// No namespace

		Assert.assertTrue(
			equals(
				signatureRequest,
				SignatureRequestSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"signatureRequest",
								new HashMap<String, Object>() {
									{
										put(
											"signatureRequestId",
											signatureRequest.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/signatureRequest"))));

		// Using the namespace digitalSignature_v1_0

		Assert.assertTrue(
			equals(
				signatureRequest,
				SignatureRequestSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"digitalSignature_v1_0",
								new GraphQLField(
									"signatureRequest",
									new HashMap<String, Object>() {
										{
											put(
												"signatureRequestId",
												signatureRequest.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/digitalSignature_v1_0",
						"Object/signatureRequest"))));
	}

	@Test
	public void testGraphQLGetSignatureRequestNotFound() throws Exception {
		Long irrelevantSignatureRequestId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"signatureRequest",
						new HashMap<String, Object>() {
							{
								put(
									"signatureRequestId",
									irrelevantSignatureRequestId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace digitalSignature_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"digitalSignature_v1_0",
						new GraphQLField(
							"signatureRequest",
							new HashMap<String, Object>() {
								{
									put(
										"signatureRequestId",
										irrelevantSignatureRequestId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected SignatureRequest
			testGraphQLGetSignatureRequest_addSignatureRequest()
		throws Exception {

		return testGraphQLSignatureRequest_addSignatureRequest();
	}

	@Test
	public void testGetSiteSignatureRequestsPage() throws Exception {
		Long siteId = testGetSiteSignatureRequestsPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteSignatureRequestsPage_getIrrelevantSiteId();

		Page<SignatureRequest> page =
			signatureRequestResource.getSiteSignatureRequestsPage(
				siteId, null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			SignatureRequest irrelevantSignatureRequest =
				testGetSiteSignatureRequestsPage_addSignatureRequest(
					irrelevantSiteId, randomIrrelevantSignatureRequest());

			page = signatureRequestResource.getSiteSignatureRequestsPage(
				irrelevantSiteId, null, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantSignatureRequest,
				(List<SignatureRequest>)page.getItems());
			assertValid(
				page,
				testGetSiteSignatureRequestsPage_getExpectedActions(
					irrelevantSiteId));
		}

		SignatureRequest signatureRequest1 =
			testGetSiteSignatureRequestsPage_addSignatureRequest(
				siteId, randomSignatureRequest());

		SignatureRequest signatureRequest2 =
			testGetSiteSignatureRequestsPage_addSignatureRequest(
				siteId, randomSignatureRequest());

		page = signatureRequestResource.getSiteSignatureRequestsPage(
			siteId, null, Pagination.of(1, (int)totalCount + 2));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			signatureRequest1, (List<SignatureRequest>)page.getItems());
		assertContains(
			signatureRequest2, (List<SignatureRequest>)page.getItems());
		assertValid(
			page, testGetSiteSignatureRequestsPage_getExpectedActions(siteId));
	}

	protected Map<String, Map<String, String>>
			testGetSiteSignatureRequestsPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			("http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/digital-signature-rest/v1.0/sites/{siteId}/signature-requests/batch").
					replace("{siteId}", String.valueOf(siteId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteSignatureRequestsPageWithPagination()
		throws Exception {

		Long siteId = testGetSiteSignatureRequestsPage_getSiteId();

		Page<SignatureRequest> signatureRequestsPage =
			signatureRequestResource.getSiteSignatureRequestsPage(
				siteId, null, null);

		int totalCount = GetterUtil.getInteger(
			signatureRequestsPage.getTotalCount());

		SignatureRequest signatureRequest1 =
			testGetSiteSignatureRequestsPage_addSignatureRequest(
				siteId, randomSignatureRequest());

		SignatureRequest signatureRequest2 =
			testGetSiteSignatureRequestsPage_addSignatureRequest(
				siteId, randomSignatureRequest());

		SignatureRequest signatureRequest3 =
			testGetSiteSignatureRequestsPage_addSignatureRequest(
				siteId, randomSignatureRequest());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<SignatureRequest> page1 =
				signatureRequestResource.getSiteSignatureRequestsPage(
					siteId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				signatureRequest1, (List<SignatureRequest>)page1.getItems());

			Page<SignatureRequest> page2 =
				signatureRequestResource.getSiteSignatureRequestsPage(
					siteId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				signatureRequest2, (List<SignatureRequest>)page2.getItems());

			Page<SignatureRequest> page3 =
				signatureRequestResource.getSiteSignatureRequestsPage(
					siteId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				signatureRequest3, (List<SignatureRequest>)page3.getItems());
		}
		else {
			Page<SignatureRequest> page1 =
				signatureRequestResource.getSiteSignatureRequestsPage(
					siteId, null, Pagination.of(1, totalCount + 2));

			List<SignatureRequest> signatureRequests1 =
				(List<SignatureRequest>)page1.getItems();

			Assert.assertEquals(
				signatureRequests1.toString(), totalCount + 2,
				signatureRequests1.size());

			Page<SignatureRequest> page2 =
				signatureRequestResource.getSiteSignatureRequestsPage(
					siteId, null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<SignatureRequest> signatureRequests2 =
				(List<SignatureRequest>)page2.getItems();

			Assert.assertEquals(
				signatureRequests2.toString(), 1, signatureRequests2.size());

			Page<SignatureRequest> page3 =
				signatureRequestResource.getSiteSignatureRequestsPage(
					siteId, null, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				signatureRequest1, (List<SignatureRequest>)page3.getItems());
			assertContains(
				signatureRequest2, (List<SignatureRequest>)page3.getItems());
			assertContains(
				signatureRequest3, (List<SignatureRequest>)page3.getItems());
		}
	}

	protected SignatureRequest
			testGetSiteSignatureRequestsPage_addSignatureRequest(
				Long siteId, SignatureRequest signatureRequest)
		throws Exception {

		return signatureRequestResource.postSiteSignatureRequest(
			siteId, signatureRequest);
	}

	protected Long testGetSiteSignatureRequestsPage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long testGetSiteSignatureRequestsPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteSignatureRequestsPage() throws Exception {
		Long siteId = testGetSiteSignatureRequestsPage_getSiteId();

		GraphQLField graphQLField =
			testGraphQLGetSiteSignatureRequestsPageSiteSignatureRequest_getGraphQLField(
				siteId);

		// No namespace

		JSONObject signatureRequestsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/signatureRequests");

		long totalCount = signatureRequestsJSONObject.getLong("totalCount");

		SignatureRequest signatureRequest1 =
			testGraphQLSiteSignatureRequest_addSignatureRequest(
				siteId, randomSignatureRequest());

		SignatureRequest signatureRequest2 =
			testGraphQLSiteSignatureRequest_addSignatureRequest(
				siteId, randomSignatureRequest());

		signatureRequestsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/signatureRequests");

		Assert.assertEquals(
			totalCount + 2, signatureRequestsJSONObject.getLong("totalCount"));

		assertContains(
			signatureRequest1,
			Arrays.asList(
				SignatureRequestSerDes.toDTOs(
					signatureRequestsJSONObject.getString("items"))));
		assertContains(
			signatureRequest2,
			Arrays.asList(
				SignatureRequestSerDes.toDTOs(
					signatureRequestsJSONObject.getString("items"))));

		// Using the namespace digitalSignature_v1_0

		signatureRequestsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("digitalSignature_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/digitalSignature_v1_0",
			"JSONObject/signatureRequests");

		Assert.assertEquals(
			totalCount + 2, signatureRequestsJSONObject.getLong("totalCount"));

		assertContains(
			signatureRequest1,
			Arrays.asList(
				SignatureRequestSerDes.toDTOs(
					signatureRequestsJSONObject.getString("items"))));
		assertContains(
			signatureRequest2,
			Arrays.asList(
				SignatureRequestSerDes.toDTOs(
					signatureRequestsJSONObject.getString("items"))));
	}

	protected GraphQLField
			testGraphQLGetSiteSignatureRequestsPageSiteSignatureRequest_getGraphQLField(
				Long siteId)
		throws Exception {

		return new GraphQLField(
			"signatureRequests",
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
	}

	@Test
	public void testPostSignatureRequestNotification() throws Exception {
		SignatureRequest randomSignatureRequest = randomSignatureRequest();

		SignatureRequest postSignatureRequest =
			testPostSignatureRequestNotification_addSignatureRequest(
				randomSignatureRequest);

		assertEquals(randomSignatureRequest, postSignatureRequest);
		assertValid(postSignatureRequest);
	}

	protected SignatureRequest
			testPostSignatureRequestNotification_addSignatureRequest(
				SignatureRequest signatureRequest)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostSignatureRequestNotification() throws Exception {
		SignatureRequest randomSignatureRequest = randomSignatureRequest();

		SignatureRequest signatureRequest =
			testGraphQLSignatureRequest_addSignatureRequest(
				testGroup.getGroupId(), randomSignatureRequest);

		Assert.assertTrue(equals(randomSignatureRequest, signatureRequest));
	}

	@Test
	public void testPostSignatureRequestVoid() throws Exception {
		SignatureRequest randomSignatureRequest = randomSignatureRequest();

		SignatureRequest postSignatureRequest =
			testPostSignatureRequestVoid_addSignatureRequest(
				randomSignatureRequest);

		assertEquals(randomSignatureRequest, postSignatureRequest);
		assertValid(postSignatureRequest);
	}

	protected SignatureRequest testPostSignatureRequestVoid_addSignatureRequest(
			SignatureRequest signatureRequest)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostSignatureRequestVoid() throws Exception {
		SignatureRequest randomSignatureRequest = randomSignatureRequest();

		SignatureRequest signatureRequest =
			testGraphQLSignatureRequest_addSignatureRequest(
				testGroup.getGroupId(), randomSignatureRequest);

		Assert.assertTrue(equals(randomSignatureRequest, signatureRequest));
	}

	@Test
	public void testPostSiteSignatureRequest() throws Exception {
		SignatureRequest randomSignatureRequest = randomSignatureRequest();

		SignatureRequest postSignatureRequest =
			testPostSiteSignatureRequest_addSignatureRequest(
				randomSignatureRequest);

		assertEquals(randomSignatureRequest, postSignatureRequest);
		assertValid(postSignatureRequest);
	}

	protected SignatureRequest testPostSiteSignatureRequest_addSignatureRequest(
			SignatureRequest signatureRequest)
		throws Exception {

		return signatureRequestResource.postSiteSignatureRequest(
			testGetSiteSignatureRequestsPage_getSiteId(), signatureRequest);
	}

	@Test
	public void testGraphQLPostSiteSignatureRequest() throws Exception {
		SignatureRequest randomSignatureRequest = randomSignatureRequest();

		SignatureRequest signatureRequest =
			testGraphQLSiteSignatureRequest_addSignatureRequest(
				testGroup.getGroupId(), randomSignatureRequest);

		Assert.assertTrue(equals(randomSignatureRequest, signatureRequest));
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected SignatureRequest testGraphQLSignatureRequest_addSignatureRequest()
		throws Exception {

		return testGraphQLSignatureRequest_addSignatureRequest(
			testGroup.getGroupId(), randomSignatureRequest());
	}

	protected SignatureRequest testGraphQLSignatureRequest_addSignatureRequest(
			Long siteId, SignatureRequest signatureRequest)
		throws Exception {

		JSONDeserializer<SignatureRequest> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(SignatureRequest.class)) {

			if (getGraphQLValue(field.get(signatureRequest)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(signatureRequest)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteSignatureRequest",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("signatureRequest", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteSignatureRequest"),
			SignatureRequest.class);
	}

	protected SignatureRequest
			testGraphQLSiteSignatureRequest_addSignatureRequest()
		throws Exception {

		return testGraphQLSiteSignatureRequest_addSignatureRequest(
			testGroup.getGroupId(), randomSignatureRequest());
	}

	protected SignatureRequest
			testGraphQLSiteSignatureRequest_addSignatureRequest(
				Long siteId, SignatureRequest signatureRequest)
		throws Exception {

		JSONDeserializer<SignatureRequest> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(SignatureRequest.class)) {

			if (getGraphQLValue(field.get(signatureRequest)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(signatureRequest)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteSignatureRequest",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("signatureRequest", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteSignatureRequest"),
			SignatureRequest.class);
	}

	protected String getGraphQLValue(Object value) throws Exception {
		if (value == null) {
			return null;
		}
		else if (value instanceof Boolean || value instanceof Number) {
			return value.toString();
		}
		else if (value instanceof Date) {
			Date date = (Date)value;

			return "\"" +
				DateUtil.getDate(
					date, "yyyy-MM-dd'T'HH:mm:ss'Z'", LocaleUtil.getDefault(),
					TimeZone.getTimeZone("UTC")) + "\"";
		}
		else if (value instanceof Enum) {
			Enum<?> enm = (Enum<?>)value;

			return enm.name();
		}
		else if (value instanceof Map) {
			Map<?, ?> map = (Map<?, ?>)value;

			List<String> entries = new ArrayList<>();

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				String graphQLValue = getGraphQLValue(entry.getValue());

				if (graphQLValue != null) {
					entries.add(entry.getKey() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
		else if (value instanceof Object[]) {
			Object[] array = (Object[])value;

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
		SignatureRequest signatureRequest,
		List<SignatureRequest> signatureRequests) {

		boolean contains = false;

		for (SignatureRequest item : signatureRequests) {
			if (equals(signatureRequest, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			signatureRequests + " does not contain " + signatureRequest,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		SignatureRequest signatureRequest1,
		SignatureRequest signatureRequest2) {

		Assert.assertTrue(
			signatureRequest1 + " does not equal " + signatureRequest2,
			equals(signatureRequest1, signatureRequest2));
	}

	protected void assertEquals(
		List<SignatureRequest> signatureRequests1,
		List<SignatureRequest> signatureRequests2) {

		Assert.assertEquals(
			signatureRequests1.size(), signatureRequests2.size());

		for (int i = 0; i < signatureRequests1.size(); i++) {
			SignatureRequest signatureRequest1 = signatureRequests1.get(i);
			SignatureRequest signatureRequest2 = signatureRequests2.get(i);

			assertEquals(signatureRequest1, signatureRequest2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<SignatureRequest> signatureRequests1,
		List<SignatureRequest> signatureRequests2) {

		Assert.assertEquals(
			signatureRequests1.size(), signatureRequests2.size());

		for (SignatureRequest signatureRequest1 : signatureRequests1) {
			boolean contains = false;

			for (SignatureRequest signatureRequest2 : signatureRequests2) {
				if (equals(signatureRequest1, signatureRequest2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				signatureRequests2 + " does not contain " + signatureRequest1,
				contains);
		}
	}

	protected void assertValid(SignatureRequest signatureRequest)
		throws Exception {

		boolean valid = true;

		if (signatureRequest.getDateCreated() == null) {
			valid = false;
		}

		if (signatureRequest.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (signatureRequest.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (signatureRequest.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("emailMessage", additionalAssertFieldName)) {
				if (signatureRequest.getEmailMessage() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("emailSubject", additionalAssertFieldName)) {
				if (signatureRequest.getEmailSubject() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (signatureRequest.getExpirationDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("expireAfter", additionalAssertFieldName)) {
				if (signatureRequest.getExpireAfter() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("fileEntryBriefs", additionalAssertFieldName)) {
				if (signatureRequest.getFileEntryBriefs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("fileEntryIds", additionalAssertFieldName)) {
				if (signatureRequest.getFileEntryIds() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (signatureRequest.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"sendNotifications", additionalAssertFieldName)) {

				if (signatureRequest.getSendNotifications() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"signatureRequestRecipients", additionalAssertFieldName)) {

				if (signatureRequest.getSignatureRequestRecipients() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (signatureRequest.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("statusDate", additionalAssertFieldName)) {
				if (signatureRequest.getStatusDate() == null) {
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

	protected void assertValid(Page<SignatureRequest> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<SignatureRequest> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<SignatureRequest> signatureRequests =
			page.getItems();

		int size = signatureRequests.size();

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
					com.liferay.digital.signature.rest.dto.v1_0.
						SignatureRequest.class)) {

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
		SignatureRequest signatureRequest1,
		SignatureRequest signatureRequest2) {

		if (signatureRequest1 == signatureRequest2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)signatureRequest1.getActions(),
						(Map)signatureRequest2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						signatureRequest1.getCreator(),
						signatureRequest2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						signatureRequest1.getDateCreated(),
						signatureRequest2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("emailMessage", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						signatureRequest1.getEmailMessage(),
						signatureRequest2.getEmailMessage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("emailSubject", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						signatureRequest1.getEmailSubject(),
						signatureRequest2.getEmailSubject())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						signatureRequest1.getExpirationDate(),
						signatureRequest2.getExpirationDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("expireAfter", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						signatureRequest1.getExpireAfter(),
						signatureRequest2.getExpireAfter())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fileEntryBriefs", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						signatureRequest1.getFileEntryBriefs(),
						signatureRequest2.getFileEntryBriefs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fileEntryIds", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						signatureRequest1.getFileEntryIds(),
						signatureRequest2.getFileEntryIds())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						signatureRequest1.getId(), signatureRequest2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						signatureRequest1.getName(),
						signatureRequest2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"sendNotifications", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						signatureRequest1.getSendNotifications(),
						signatureRequest2.getSendNotifications())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"signatureRequestRecipients", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						signatureRequest1.getSignatureRequestRecipients(),
						signatureRequest2.getSignatureRequestRecipients())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						signatureRequest1.getStatus(),
						signatureRequest2.getStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("statusDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						signatureRequest1.getStatusDate(),
						signatureRequest2.getStatusDate())) {

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

		if (!(_signatureRequestResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_signatureRequestResource;

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
		SignatureRequest signatureRequest) {

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
				Date date = signatureRequest.getDateCreated();

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

				sb.append(_format.format(signatureRequest.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("emailMessage")) {
			Object object = signatureRequest.getEmailMessage();

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

		if (entityFieldName.equals("emailSubject")) {
			Object object = signatureRequest.getEmailSubject();

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

		if (entityFieldName.equals("expirationDate")) {
			if (operator.equals("between")) {
				Date date = signatureRequest.getExpirationDate();

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

				sb.append(_format.format(signatureRequest.getExpirationDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("expireAfter")) {
			sb.append(String.valueOf(signatureRequest.getExpireAfter()));

			return sb.toString();
		}

		if (entityFieldName.equals("fileEntryBriefs")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("fileEntryIds")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = signatureRequest.getName();

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

		if (entityFieldName.equals("sendNotifications")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("signatureRequestRecipients")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("status")) {
			Object object = signatureRequest.getStatus();

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

		if (entityFieldName.equals("statusDate")) {
			if (operator.equals("between")) {
				Date date = signatureRequest.getStatusDate();

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

				sb.append(_format.format(signatureRequest.getStatusDate()));
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

	protected SignatureRequest randomSignatureRequest() throws Exception {
		return new SignatureRequest() {
			{
				dateCreated = RandomTestUtil.nextDate();
				emailMessage =
					StringUtil.toLowerCase(RandomTestUtil.randomString()) +
						"@liferay.com";
				emailSubject =
					StringUtil.toLowerCase(RandomTestUtil.randomString()) +
						"@liferay.com";
				expirationDate = RandomTestUtil.nextDate();
				expireAfter = RandomTestUtil.randomInt();
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				sendNotifications = RandomTestUtil.randomBoolean();
				status = StringUtil.toLowerCase(RandomTestUtil.randomString());
				statusDate = RandomTestUtil.nextDate();
			}
		};
	}

	protected SignatureRequest randomIrrelevantSignatureRequest()
		throws Exception {

		SignatureRequest randomIrrelevantSignatureRequest =
			randomSignatureRequest();

		return randomIrrelevantSignatureRequest;
	}

	protected SignatureRequest randomPatchSignatureRequest() throws Exception {
		return randomSignatureRequest();
	}

	protected SignatureRequestResource signatureRequestResource;
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
		LogFactoryUtil.getLog(BaseSignatureRequestResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.digital.signature.rest.resource.v1_0.
			SignatureRequestResource _signatureRequestResource;

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
// LIFERAY-REST-BUILDER-HASH:1988794787