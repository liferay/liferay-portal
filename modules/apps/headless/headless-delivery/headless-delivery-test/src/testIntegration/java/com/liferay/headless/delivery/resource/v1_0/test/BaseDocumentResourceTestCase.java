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

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.headless.delivery.client.dto.v1_0.Document;
import com.liferay.headless.delivery.client.dto.v1_0.Field;
import com.liferay.headless.delivery.client.dto.v1_0.Rating;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.permission.Permission;
import com.liferay.headless.delivery.client.resource.v1_0.DocumentResource;
import com.liferay.headless.delivery.client.serdes.v1_0.DocumentSerDes;
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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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

import java.io.File;

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
public abstract class BaseDocumentResourceTestCase {

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

		irrelevantDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			new ServiceContext() {
				{
					setCompanyId(testCompany.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
		irrelevantDepotEntryGroup = irrelevantDepotEntry.getGroup();
		testDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			new ServiceContext() {
				{
					setCompanyId(testCompany.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
		testDepotEntryGroup = testDepotEntry.getGroup();

		_documentResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		documentResource = DocumentResource.builder(
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

		Document document1 = randomDocument();

		String json = objectMapper.writeValueAsString(document1);

		Document document2 = DocumentSerDes.toDTO(json);

		Assert.assertTrue(equals(document1, document2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Document document = randomDocument();

		String json1 = objectMapper.writeValueAsString(document);
		String json2 = DocumentSerDes.toJSON(document);

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

		Document document = randomDocument();

		document.setAssetLibraryKey(regex);
		document.setContentUrl(regex);
		document.setContentValue(regex);
		document.setDescription(regex);
		document.setDocumentFolderExternalReferenceCode(regex);
		document.setEncodingFormat(regex);
		document.setExternalReferenceCode(regex);
		document.setFileExtension(regex);
		document.setFileName(regex);
		document.setFriendlyUrlPath(regex);
		document.setTitle(regex);

		String json = DocumentSerDes.toJSON(document);

		Assert.assertFalse(json.contains(regex));

		document = DocumentSerDes.toDTO(json);

		Assert.assertEquals(regex, document.getAssetLibraryKey());
		Assert.assertEquals(regex, document.getContentUrl());
		Assert.assertEquals(regex, document.getContentValue());
		Assert.assertEquals(regex, document.getDescription());
		Assert.assertEquals(
			regex, document.getDocumentFolderExternalReferenceCode());
		Assert.assertEquals(regex, document.getEncodingFormat());
		Assert.assertEquals(regex, document.getExternalReferenceCode());
		Assert.assertEquals(regex, document.getFileExtension());
		Assert.assertEquals(regex, document.getFileName());
		Assert.assertEquals(regex, document.getFriendlyUrlPath());
		Assert.assertEquals(regex, document.getTitle());
	}

	@Test
	public void testDeleteAssetLibraryDocumentByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Document document =
			testDeleteAssetLibraryDocumentByExternalReferenceCode_addDocument();

		assertHttpResponseStatusCode(
			204,
			documentResource.
				deleteAssetLibraryDocumentByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId(),
					document.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			documentResource.
				getAssetLibraryDocumentByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId(),
					document.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			documentResource.
				getAssetLibraryDocumentByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId(),
					"-"));
	}

	protected Document
			testDeleteAssetLibraryDocumentByExternalReferenceCode_addDocument()
		throws Exception {

		return documentResource.postAssetLibraryDocument(
			testDepotEntry.getDepotEntryId(), randomDocument(),
			getMultipartFiles());
	}

	protected Long
			testDeleteAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLDeleteAssetLibraryDocumentByExternalReferenceCode()
		throws Exception {

		// No namespace

		Document document1 =
			testGraphQLDeleteAssetLibraryDocumentByExternalReferenceCode_addDocument();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAssetLibraryDocumentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryId",
									"\"" +
										testGraphQLDeleteAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" +
										document1.getExternalReferenceCode() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteAssetLibraryDocumentByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"assetLibraryDocumentByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"assetLibraryId",
								"\"" +
									testGraphQLDeleteAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId() +
										"\"");
							put(
								"externalReferenceCode",
								"\"" + document1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		Document document2 =
			testGraphQLDeleteAssetLibraryDocumentByExternalReferenceCode_addDocument();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteAssetLibraryDocumentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"assetLibraryId",
										"\"" +
											testGraphQLDeleteAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId() +
												"\"");
									put(
										"externalReferenceCode",
										"\"" +
											document2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteAssetLibraryDocumentByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"assetLibraryDocumentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryId",
									"\"" +
										testGraphQLDeleteAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" +
										document2.getExternalReferenceCode() +
											"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Long
			testGraphQLDeleteAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Document
			testGraphQLDeleteAssetLibraryDocumentByExternalReferenceCode_addDocument()
		throws Exception {

		return testGraphQLAssetLibraryDocument_addDocument();
	}

	@Test
	public void testDeleteDocument() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Document document = testDeleteDocument_addDocument();

		assertHttpResponseStatusCode(
			204, documentResource.deleteDocumentHttpResponse(document.getId()));

		assertHttpResponseStatusCode(
			404, documentResource.getDocumentHttpResponse(document.getId()));
		assertHttpResponseStatusCode(
			404, documentResource.getDocumentHttpResponse(0L));
	}

	protected Document testDeleteDocument_addDocument() throws Exception {
		return documentResource.postSiteDocument(
			testGroup.getGroupId(), randomDocument(), getMultipartFiles());
	}

	@Test
	public void testGraphQLDeleteDocument() throws Exception {

		// No namespace

		Document document1 = testGraphQLDeleteDocument_addDocument();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteDocument",
						new HashMap<String, Object>() {
							{
								put("documentId", document1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteDocument"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"document",
					new HashMap<String, Object>() {
						{
							put("documentId", document1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		Document document2 = testGraphQLDeleteDocument_addDocument();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteDocument",
							new HashMap<String, Object>() {
								{
									put("documentId", document2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteDocument"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"document",
						new HashMap<String, Object>() {
							{
								put("documentId", document2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Document testGraphQLDeleteDocument_addDocument()
		throws Exception {

		return testGraphQLDocument_addDocument();
	}

	@Test
	public void testDeleteDocumentBatch() throws Exception {
		Document document1 = testDeleteDocumentBatch_addDocument();

		testDeleteDocumentBatch_deleteDocument(202, null, document1.getId());

		assertHttpResponseStatusCode(
			404, documentResource.getDocumentHttpResponse(document1.getId()));
	}

	protected Document testDeleteDocumentBatch_addDocument() throws Exception {
		return testDeleteDocument_addDocument();
	}

	protected void testDeleteDocumentBatch_deleteDocument(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			documentResource.deleteDocumentBatchHttpResponse(
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
	public void testDeleteDocumentMyRating() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Document document = testDeleteDocumentMyRating_addDocument();

		assertHttpResponseStatusCode(
			204,
			documentResource.deleteDocumentMyRatingHttpResponse(
				document.getId()));

		assertHttpResponseStatusCode(
			404,
			documentResource.getDocumentMyRatingHttpResponse(document.getId()));
		assertHttpResponseStatusCode(
			404, documentResource.getDocumentMyRatingHttpResponse(0L));
	}

	protected Document testDeleteDocumentMyRating_addDocument()
		throws Exception {

		return documentResource.postSiteDocument(
			testGroup.getGroupId(), randomDocument(), getMultipartFiles());
	}

	@Test
	public void testGraphQLDeleteDocumentMyRating() throws Exception {

		// No namespace

		Document document1 = testGraphQLDeleteDocumentMyRating_addDocument();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteDocumentMyRating",
						new HashMap<String, Object>() {
							{
								put("documentId", document1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteDocumentMyRating"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"documentMyRating",
					new HashMap<String, Object>() {
						{
							put("documentId", document1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		Document document2 = testGraphQLDeleteDocumentMyRating_addDocument();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteDocumentMyRating",
							new HashMap<String, Object>() {
								{
									put("documentId", document2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteDocumentMyRating"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"documentMyRating",
						new HashMap<String, Object>() {
							{
								put("documentId", document2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Document testGraphQLDeleteDocumentMyRating_addDocument()
		throws Exception {

		return testGraphQLDocument_addDocument();
	}

	@Test
	public void testDeleteSiteDocumentByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Document document =
			testDeleteSiteDocumentByExternalReferenceCode_addDocument();

		assertHttpResponseStatusCode(
			204,
			documentResource.
				deleteSiteDocumentByExternalReferenceCodeHttpResponse(
					document.getSiteId(), document.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			documentResource.getSiteDocumentByExternalReferenceCodeHttpResponse(
				document.getSiteId(), document.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			documentResource.getSiteDocumentByExternalReferenceCodeHttpResponse(
				document.getSiteId(), "-"));
	}

	protected Document
			testDeleteSiteDocumentByExternalReferenceCode_addDocument()
		throws Exception {

		return documentResource.postSiteDocument(
			testGroup.getGroupId(), randomDocument(), getMultipartFiles());
	}

	@Test
	public void testGraphQLDeleteSiteDocumentByExternalReferenceCode()
		throws Exception {

		// No namespace

		Document document1 =
			testGraphQLDeleteSiteDocumentByExternalReferenceCode_addDocument();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteDocumentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + document1.getSiteId() + "\"");
								put(
									"externalReferenceCode",
									"\"" +
										document1.getExternalReferenceCode() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteDocumentByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"documentByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put("siteKey", "\"" + document1.getSiteId() + "\"");
							put(
								"externalReferenceCode",
								"\"" + document1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		Document document2 =
			testGraphQLDeleteSiteDocumentByExternalReferenceCode_addDocument();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteSiteDocumentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + document2.getSiteId() + "\"");
									put(
										"externalReferenceCode",
										"\"" +
											document2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteSiteDocumentByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"documentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + document2.getSiteId() + "\"");
								put(
									"externalReferenceCode",
									"\"" +
										document2.getExternalReferenceCode() +
											"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Document
			testGraphQLDeleteSiteDocumentByExternalReferenceCode_addDocument()
		throws Exception {

		return testGraphQLSiteDocument_addDocument();
	}

	@Test
	public void testGetAssetLibraryDocumentByExternalReferenceCode()
		throws Exception {

		Document postDocument =
			testGetAssetLibraryDocumentByExternalReferenceCode_addDocument();

		Document getDocument =
			documentResource.getAssetLibraryDocumentByExternalReferenceCode(
				testGetAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId(),
				postDocument.getExternalReferenceCode());

		assertEquals(postDocument, getDocument);
		assertValid(getDocument);
	}

	protected Document
			testGetAssetLibraryDocumentByExternalReferenceCode_addDocument()
		throws Exception {

		return documentResource.postAssetLibraryDocument(
			testDepotEntry.getDepotEntryId(), randomDocument(),
			getMultipartFiles());
	}

	protected Long
			testGetAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLGetAssetLibraryDocumentByExternalReferenceCode()
		throws Exception {

		Document document =
			testGraphQLGetAssetLibraryDocumentByExternalReferenceCode_addDocument();

		// No namespace

		Assert.assertTrue(
			equals(
				document,
				DocumentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"assetLibraryDocumentByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"assetLibraryId",
											"\"" +
												testGraphQLGetAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId() +
													"\"");
										put(
											"externalReferenceCode",
											"\"" +
												document.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/assetLibraryDocumentByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				document,
				DocumentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"assetLibraryDocumentByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"assetLibraryId",
												"\"" +
													testGraphQLGetAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId() +
														"\"");
											put(
												"externalReferenceCode",
												"\"" +
													document.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/assetLibraryDocumentByExternalReferenceCode"))));
	}

	protected Long
			testGraphQLGetAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLGetAssetLibraryDocumentByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"assetLibraryDocumentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryId",
									"\"" +
										irrelevantDepotEntry.getDepotEntryId() +
											"\"");
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
							"assetLibraryDocumentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"assetLibraryId",
										"\"" +
											irrelevantDepotEntry.
												getDepotEntryId() + "\"");
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Document
			testGraphQLGetAssetLibraryDocumentByExternalReferenceCode_addDocument()
		throws Exception {

		return testGraphQLAssetLibraryDocument_addDocument();
	}

	@Test
	public void testGetAssetLibraryDocumentPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Document postDocument =
			testGetAssetLibraryDocumentPermissionsPage_addDocument();

		Page<Permission> page =
			documentResource.getAssetLibraryDocumentPermissionsPage(
				testDepotEntry.getDepotEntryId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected Document testGetAssetLibraryDocumentPermissionsPage_addDocument()
		throws Exception {

		return documentResource.postAssetLibraryDocument(
			testDepotEntry.getDepotEntryId(), randomDocument(),
			getMultipartFiles());
	}

	@Test
	public void testGraphQLGetAssetLibraryDocumentPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Document postDocument =
			testGraphQLGetAssetLibraryDocumentPermissionsPage_addDocument();

		GraphQLField graphQLField = new GraphQLField(
			"assetLibraryDocumentPermissions",
			new HashMap<String, Object>() {
				{
					put(
						"assetLibraryId",
						"\"" +
							testGraphQLGetAssetLibraryDocumentPermissionsPage_getAssetLibraryId() +
								"\"");
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject assetLibraryDocumentPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryDocumentPermissions");

		Assert.assertNotNull(assetLibraryDocumentPermissionsJSONObject);
	}

	protected Long
			testGraphQLGetAssetLibraryDocumentPermissionsPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Document
			testGraphQLGetAssetLibraryDocumentPermissionsPage_addDocument()
		throws Exception {

		return testGraphQLAssetLibraryDocument_addDocument();
	}

	@Test
	public void testGetAssetLibraryDocumentsPage() throws Exception {
		Long assetLibraryId =
			testGetAssetLibraryDocumentsPage_getAssetLibraryId();
		Long irrelevantAssetLibraryId =
			testGetAssetLibraryDocumentsPage_getIrrelevantAssetLibraryId();

		Page<Document> page = documentResource.getAssetLibraryDocumentsPage(
			assetLibraryId, null, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryId != null) {
			Document irrelevantDocument =
				testGetAssetLibraryDocumentsPage_addDocument(
					irrelevantAssetLibraryId, randomIrrelevantDocument());

			page = documentResource.getAssetLibraryDocumentsPage(
				irrelevantAssetLibraryId, null, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantDocument, (List<Document>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryDocumentsPage_getExpectedActions(
					irrelevantAssetLibraryId));
		}

		Document document1 = testGetAssetLibraryDocumentsPage_addDocument(
			assetLibraryId, randomDocument());

		Document document2 = testGetAssetLibraryDocumentsPage_addDocument(
			assetLibraryId, randomDocument());

		page = documentResource.getAssetLibraryDocumentsPage(
			assetLibraryId, null, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(document1, (List<Document>)page.getItems());
		assertContains(document2, (List<Document>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryDocumentsPage_getExpectedActions(
				assetLibraryId));

		documentResource.deleteDocument(document1.getId());

		documentResource.deleteDocument(document2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryDocumentsPage_getExpectedActions(
				Long assetLibraryId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/asset-libraries/{assetLibraryId}/documents/batch".
				replace("{assetLibraryId}", String.valueOf(assetLibraryId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryDocumentsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryDocumentsPage_getAssetLibraryId();

		Document document1 = randomDocument();

		document1 = testGetAssetLibraryDocumentsPage_addDocument(
			assetLibraryId, document1);

		for (EntityField entityField : entityFields) {
			Page<Document> page = documentResource.getAssetLibraryDocumentsPage(
				assetLibraryId, null, null, null,
				getFilterString(entityField, "between", document1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(document1),
				(List<Document>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryDocumentsPageWithFilterDoubleEquals()
		throws Exception {

		testGetAssetLibraryDocumentsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAssetLibraryDocumentsPageWithFilterStringContains()
		throws Exception {

		testGetAssetLibraryDocumentsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryDocumentsPageWithFilterStringEquals()
		throws Exception {

		testGetAssetLibraryDocumentsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryDocumentsPageWithFilterStringStartsWith()
		throws Exception {

		testGetAssetLibraryDocumentsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetAssetLibraryDocumentsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryDocumentsPage_getAssetLibraryId();

		Document document1 = testGetAssetLibraryDocumentsPage_addDocument(
			assetLibraryId, randomDocument());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Document document2 = testGetAssetLibraryDocumentsPage_addDocument(
			assetLibraryId, randomDocument());

		for (EntityField entityField : entityFields) {
			Page<Document> page = documentResource.getAssetLibraryDocumentsPage(
				assetLibraryId, null, null, null,
				getFilterString(entityField, operator, document1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(document1),
				(List<Document>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryDocumentsPageWithPagination()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryDocumentsPage_getAssetLibraryId();

		Page<Document> documentsPage =
			documentResource.getAssetLibraryDocumentsPage(
				assetLibraryId, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(documentsPage.getTotalCount());

		Document document1 = testGetAssetLibraryDocumentsPage_addDocument(
			assetLibraryId, randomDocument());

		Document document2 = testGetAssetLibraryDocumentsPage_addDocument(
			assetLibraryId, randomDocument());

		Document document3 = testGetAssetLibraryDocumentsPage_addDocument(
			assetLibraryId, randomDocument());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Document> page1 =
				documentResource.getAssetLibraryDocumentsPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(document1, (List<Document>)page1.getItems());

			Page<Document> page2 =
				documentResource.getAssetLibraryDocumentsPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(document2, (List<Document>)page2.getItems());

			Page<Document> page3 =
				documentResource.getAssetLibraryDocumentsPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(document3, (List<Document>)page3.getItems());
		}
		else {
			Page<Document> page1 =
				documentResource.getAssetLibraryDocumentsPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<Document> documents1 = (List<Document>)page1.getItems();

			Assert.assertEquals(
				documents1.toString(), totalCount + 2, documents1.size());

			Page<Document> page2 =
				documentResource.getAssetLibraryDocumentsPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Document> documents2 = (List<Document>)page2.getItems();

			Assert.assertEquals(documents2.toString(), 1, documents2.size());

			Page<Document> page3 =
				documentResource.getAssetLibraryDocumentsPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(document1, (List<Document>)page3.getItems());
			assertContains(document2, (List<Document>)page3.getItems());
			assertContains(document3, (List<Document>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryDocumentsPageWithSortDateTime()
		throws Exception {

		testGetAssetLibraryDocumentsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, document1, document2) -> {
				BeanTestUtil.setProperty(
					document1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetLibraryDocumentsPageWithSortDouble()
		throws Exception {

		testGetAssetLibraryDocumentsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, document1, document2) -> {
				BeanTestUtil.setProperty(document1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(document2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetLibraryDocumentsPageWithSortInteger()
		throws Exception {

		testGetAssetLibraryDocumentsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, document1, document2) -> {
				BeanTestUtil.setProperty(document1, entityField.getName(), 0);
				BeanTestUtil.setProperty(document2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetLibraryDocumentsPageWithSortString()
		throws Exception {

		testGetAssetLibraryDocumentsPageWithSort(
			EntityField.Type.STRING,
			(entityField, document1, document2) -> {
				Class<?> clazz = document1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						document1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						document2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						document1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						document2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						document1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						document2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAssetLibraryDocumentsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Document, Document, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryDocumentsPage_getAssetLibraryId();

		Document document1 = randomDocument();
		Document document2 = randomDocument();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, document1, document2);
		}

		document1 = testGetAssetLibraryDocumentsPage_addDocument(
			assetLibraryId, document1);

		document2 = testGetAssetLibraryDocumentsPage_addDocument(
			assetLibraryId, document2);

		Page<Document> page = documentResource.getAssetLibraryDocumentsPage(
			assetLibraryId, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Document> ascPage =
				documentResource.getAssetLibraryDocumentsPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(document1, (List<Document>)ascPage.getItems());
			assertContains(document2, (List<Document>)ascPage.getItems());

			Page<Document> descPage =
				documentResource.getAssetLibraryDocumentsPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(document2, (List<Document>)descPage.getItems());
			assertContains(document1, (List<Document>)descPage.getItems());
		}
	}

	protected Document testGetAssetLibraryDocumentsPage_addDocument(
			Long assetLibraryId, Document document)
		throws Exception {

		return documentResource.postAssetLibraryDocument(
			assetLibraryId, document, getMultipartFiles());
	}

	protected Long testGetAssetLibraryDocumentsPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Long
			testGetAssetLibraryDocumentsPage_getIrrelevantAssetLibraryId()
		throws Exception {

		return irrelevantDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLGetAssetLibraryDocumentsPage() throws Exception {
		Long assetLibraryId =
			testGetAssetLibraryDocumentsPage_getAssetLibraryId();

		GraphQLField graphQLField = new GraphQLField(
			"assetLibraryDocuments",
			new HashMap<String, Object>() {
				{
					put("assetLibraryId", "\"" + assetLibraryId + "\"");
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject assetLibraryDocumentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryDocuments");

		long totalCount = assetLibraryDocumentsJSONObject.getLong("totalCount");

		Document document1 = testGraphQLAssetLibraryDocument_addDocument(
			assetLibraryId, randomDocument());

		Document document2 = testGraphQLAssetLibraryDocument_addDocument(
			assetLibraryId, randomDocument());

		assetLibraryDocumentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/assetLibraryDocuments");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryDocumentsJSONObject.getLong("totalCount"));

		assertContains(
			document1,
			Arrays.asList(
				DocumentSerDes.toDTOs(
					assetLibraryDocumentsJSONObject.getString("items"))));
		assertContains(
			document2,
			Arrays.asList(
				DocumentSerDes.toDTOs(
					assetLibraryDocumentsJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		assetLibraryDocumentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/assetLibraryDocuments");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryDocumentsJSONObject.getLong("totalCount"));

		assertContains(
			document1,
			Arrays.asList(
				DocumentSerDes.toDTOs(
					assetLibraryDocumentsJSONObject.getString("items"))));
		assertContains(
			document2,
			Arrays.asList(
				DocumentSerDes.toDTOs(
					assetLibraryDocumentsJSONObject.getString("items"))));
	}

	@Test
	public void testGetAssetLibraryDocumentsRatedByMePage() throws Exception {
		Long assetLibraryId =
			testGetAssetLibraryDocumentsRatedByMePage_getAssetLibraryId();
		Long irrelevantAssetLibraryId =
			testGetAssetLibraryDocumentsRatedByMePage_getIrrelevantAssetLibraryId();

		Page<Document> page =
			documentResource.getAssetLibraryDocumentsRatedByMePage(
				assetLibraryId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryId != null) {
			Document irrelevantDocument =
				testGetAssetLibraryDocumentsRatedByMePage_addDocument(
					irrelevantAssetLibraryId, randomIrrelevantDocument());

			page = documentResource.getAssetLibraryDocumentsRatedByMePage(
				irrelevantAssetLibraryId,
				Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantDocument, (List<Document>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryDocumentsRatedByMePage_getExpectedActions(
					irrelevantAssetLibraryId));
		}

		Document document1 =
			testGetAssetLibraryDocumentsRatedByMePage_addDocument(
				assetLibraryId, randomDocument());

		Document document2 =
			testGetAssetLibraryDocumentsRatedByMePage_addDocument(
				assetLibraryId, randomDocument());

		page = documentResource.getAssetLibraryDocumentsRatedByMePage(
			assetLibraryId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(document1, (List<Document>)page.getItems());
		assertContains(document2, (List<Document>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryDocumentsRatedByMePage_getExpectedActions(
				assetLibraryId));

		documentResource.deleteDocument(document1.getId());

		documentResource.deleteDocument(document2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryDocumentsRatedByMePage_getExpectedActions(
				Long assetLibraryId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryDocumentsRatedByMePageWithPagination()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryDocumentsRatedByMePage_getAssetLibraryId();

		Page<Document> documentsPage =
			documentResource.getAssetLibraryDocumentsRatedByMePage(
				assetLibraryId, null);

		int totalCount = GetterUtil.getInteger(documentsPage.getTotalCount());

		Document document1 =
			testGetAssetLibraryDocumentsRatedByMePage_addDocument(
				assetLibraryId, randomDocument());

		Document document2 =
			testGetAssetLibraryDocumentsRatedByMePage_addDocument(
				assetLibraryId, randomDocument());

		Document document3 =
			testGetAssetLibraryDocumentsRatedByMePage_addDocument(
				assetLibraryId, randomDocument());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Document> page1 =
				documentResource.getAssetLibraryDocumentsRatedByMePage(
					assetLibraryId,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(document1, (List<Document>)page1.getItems());

			Page<Document> page2 =
				documentResource.getAssetLibraryDocumentsRatedByMePage(
					assetLibraryId,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(document2, (List<Document>)page2.getItems());

			Page<Document> page3 =
				documentResource.getAssetLibraryDocumentsRatedByMePage(
					assetLibraryId,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(document3, (List<Document>)page3.getItems());
		}
		else {
			Page<Document> page1 =
				documentResource.getAssetLibraryDocumentsRatedByMePage(
					assetLibraryId, Pagination.of(1, totalCount + 2));

			List<Document> documents1 = (List<Document>)page1.getItems();

			Assert.assertEquals(
				documents1.toString(), totalCount + 2, documents1.size());

			Page<Document> page2 =
				documentResource.getAssetLibraryDocumentsRatedByMePage(
					assetLibraryId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Document> documents2 = (List<Document>)page2.getItems();

			Assert.assertEquals(documents2.toString(), 1, documents2.size());

			Page<Document> page3 =
				documentResource.getAssetLibraryDocumentsRatedByMePage(
					assetLibraryId, Pagination.of(1, (int)totalCount + 3));

			assertContains(document1, (List<Document>)page3.getItems());
			assertContains(document2, (List<Document>)page3.getItems());
			assertContains(document3, (List<Document>)page3.getItems());
		}
	}

	protected Document testGetAssetLibraryDocumentsRatedByMePage_addDocument(
			Long assetLibraryId, Document document)
		throws Exception {

		return documentResource.postAssetLibraryDocument(
			assetLibraryId, document, getMultipartFiles());
	}

	protected Long testGetAssetLibraryDocumentsRatedByMePage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Long
			testGetAssetLibraryDocumentsRatedByMePage_getIrrelevantAssetLibraryId()
		throws Exception {

		return irrelevantDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLGetAssetLibraryDocumentsRatedByMePage()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryDocumentsRatedByMePage_getAssetLibraryId();

		GraphQLField graphQLField = new GraphQLField(
			"assetLibraryDocumentsRatedByMe",
			new HashMap<String, Object>() {
				{
					put("assetLibraryId", "\"" + assetLibraryId + "\"");
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject assetLibraryDocumentsRatedByMeJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryDocumentsRatedByMe");

		long totalCount = assetLibraryDocumentsRatedByMeJSONObject.getLong(
			"totalCount");

		Document document1 = testGraphQLAssetLibraryDocument_addDocument(
			assetLibraryId, randomDocument());

		Document document2 = testGraphQLAssetLibraryDocument_addDocument(
			assetLibraryId, randomDocument());

		assetLibraryDocumentsRatedByMeJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryDocumentsRatedByMe");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryDocumentsRatedByMeJSONObject.getLong("totalCount"));

		assertContains(
			document1,
			Arrays.asList(
				DocumentSerDes.toDTOs(
					assetLibraryDocumentsRatedByMeJSONObject.getString(
						"items"))));
		assertContains(
			document2,
			Arrays.asList(
				DocumentSerDes.toDTOs(
					assetLibraryDocumentsRatedByMeJSONObject.getString(
						"items"))));

		// Using the namespace headlessDelivery_v1_0

		assetLibraryDocumentsRatedByMeJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("headlessDelivery_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"JSONObject/assetLibraryDocumentsRatedByMe");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryDocumentsRatedByMeJSONObject.getLong("totalCount"));

		assertContains(
			document1,
			Arrays.asList(
				DocumentSerDes.toDTOs(
					assetLibraryDocumentsRatedByMeJSONObject.getString(
						"items"))));
		assertContains(
			document2,
			Arrays.asList(
				DocumentSerDes.toDTOs(
					assetLibraryDocumentsRatedByMeJSONObject.getString(
						"items"))));
	}

	@Test
	public void testGetDocument() throws Exception {
		Document postDocument = testGetDocument_addDocument();

		Document getDocument = documentResource.getDocument(
			postDocument.getId());

		assertEquals(postDocument, getDocument);
		assertValid(getDocument);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		Document postDocument = testGetDocument_addDocument();

		Document getDocument = documentResource.getDocument(
			postDocument.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany, "com.liferay.headless.delivery.dto.v1_0.Document"
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

		Object item = vulcanCRUDItemDelegate.getItem(postDocument.getId());

		assertEquals(getDocument, DocumentSerDes.toDTO(item.toString()));
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

	protected Document testGetDocument_addDocument() throws Exception {
		return documentResource.postSiteDocument(
			testGroup.getGroupId(), randomDocument(), getMultipartFiles());
	}

	@Test
	public void testGraphQLGetDocument() throws Exception {
		Document document = testGraphQLGetDocument_addDocument();

		// No namespace

		Assert.assertTrue(
			equals(
				document,
				DocumentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"document",
								new HashMap<String, Object>() {
									{
										put("documentId", document.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/document"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				document,
				DocumentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"document",
									new HashMap<String, Object>() {
										{
											put("documentId", document.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/document"))));
	}

	@Test
	public void testGraphQLGetDocumentNotFound() throws Exception {
		Long irrelevantDocumentId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"document",
						new HashMap<String, Object>() {
							{
								put("documentId", irrelevantDocumentId);
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
							"document",
							new HashMap<String, Object>() {
								{
									put("documentId", irrelevantDocumentId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Document testGraphQLGetDocument_addDocument() throws Exception {
		return testGraphQLDocument_addDocument();
	}

	@Test
	public void testGetDocumentFolderDocumentsPage() throws Exception {
		Long documentFolderId =
			testGetDocumentFolderDocumentsPage_getDocumentFolderId();
		Long irrelevantDocumentFolderId =
			testGetDocumentFolderDocumentsPage_getIrrelevantDocumentFolderId();

		Page<Document> page = documentResource.getDocumentFolderDocumentsPage(
			documentFolderId, null, null, null, null, Pagination.of(1, 10),
			null);

		long totalCount = page.getTotalCount();

		if (irrelevantDocumentFolderId != null) {
			Document irrelevantDocument =
				testGetDocumentFolderDocumentsPage_addDocument(
					irrelevantDocumentFolderId, randomIrrelevantDocument());

			page = documentResource.getDocumentFolderDocumentsPage(
				irrelevantDocumentFolderId, null, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantDocument, (List<Document>)page.getItems());
			assertValid(
				page,
				testGetDocumentFolderDocumentsPage_getExpectedActions(
					irrelevantDocumentFolderId));
		}

		Document document1 = testGetDocumentFolderDocumentsPage_addDocument(
			documentFolderId, randomDocument());

		Document document2 = testGetDocumentFolderDocumentsPage_addDocument(
			documentFolderId, randomDocument());

		page = documentResource.getDocumentFolderDocumentsPage(
			documentFolderId, null, null, null, null, Pagination.of(1, 10),
			null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(document1, (List<Document>)page.getItems());
		assertContains(document2, (List<Document>)page.getItems());
		assertValid(
			page,
			testGetDocumentFolderDocumentsPage_getExpectedActions(
				documentFolderId));

		documentResource.deleteDocument(document1.getId());

		documentResource.deleteDocument(document2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetDocumentFolderDocumentsPage_getExpectedActions(
				Long documentFolderId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/document-folders/{documentFolderId}/documents/batch".
				replace(
					"{documentFolderId}", String.valueOf(documentFolderId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetDocumentFolderDocumentsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long documentFolderId =
			testGetDocumentFolderDocumentsPage_getDocumentFolderId();

		Document document1 = randomDocument();

		document1 = testGetDocumentFolderDocumentsPage_addDocument(
			documentFolderId, document1);

		for (EntityField entityField : entityFields) {
			Page<Document> page =
				documentResource.getDocumentFolderDocumentsPage(
					documentFolderId, null, null, null,
					getFilterString(entityField, "between", document1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(document1),
				(List<Document>)page.getItems());
		}
	}

	@Test
	public void testGetDocumentFolderDocumentsPageWithFilterDoubleEquals()
		throws Exception {

		testGetDocumentFolderDocumentsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetDocumentFolderDocumentsPageWithFilterStringContains()
		throws Exception {

		testGetDocumentFolderDocumentsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetDocumentFolderDocumentsPageWithFilterStringEquals()
		throws Exception {

		testGetDocumentFolderDocumentsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetDocumentFolderDocumentsPageWithFilterStringStartsWith()
		throws Exception {

		testGetDocumentFolderDocumentsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetDocumentFolderDocumentsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long documentFolderId =
			testGetDocumentFolderDocumentsPage_getDocumentFolderId();

		Document document1 = testGetDocumentFolderDocumentsPage_addDocument(
			documentFolderId, randomDocument());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Document document2 = testGetDocumentFolderDocumentsPage_addDocument(
			documentFolderId, randomDocument());

		for (EntityField entityField : entityFields) {
			Page<Document> page =
				documentResource.getDocumentFolderDocumentsPage(
					documentFolderId, null, null, null,
					getFilterString(entityField, operator, document1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(document1),
				(List<Document>)page.getItems());
		}
	}

	@Test
	public void testGetDocumentFolderDocumentsPageWithPagination()
		throws Exception {

		Long documentFolderId =
			testGetDocumentFolderDocumentsPage_getDocumentFolderId();

		Page<Document> documentsPage =
			documentResource.getDocumentFolderDocumentsPage(
				documentFolderId, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(documentsPage.getTotalCount());

		Document document1 = testGetDocumentFolderDocumentsPage_addDocument(
			documentFolderId, randomDocument());

		Document document2 = testGetDocumentFolderDocumentsPage_addDocument(
			documentFolderId, randomDocument());

		Document document3 = testGetDocumentFolderDocumentsPage_addDocument(
			documentFolderId, randomDocument());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Document> page1 =
				documentResource.getDocumentFolderDocumentsPage(
					documentFolderId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(document1, (List<Document>)page1.getItems());

			Page<Document> page2 =
				documentResource.getDocumentFolderDocumentsPage(
					documentFolderId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(document2, (List<Document>)page2.getItems());

			Page<Document> page3 =
				documentResource.getDocumentFolderDocumentsPage(
					documentFolderId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(document3, (List<Document>)page3.getItems());
		}
		else {
			Page<Document> page1 =
				documentResource.getDocumentFolderDocumentsPage(
					documentFolderId, null, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<Document> documents1 = (List<Document>)page1.getItems();

			Assert.assertEquals(
				documents1.toString(), totalCount + 2, documents1.size());

			Page<Document> page2 =
				documentResource.getDocumentFolderDocumentsPage(
					documentFolderId, null, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Document> documents2 = (List<Document>)page2.getItems();

			Assert.assertEquals(documents2.toString(), 1, documents2.size());

			Page<Document> page3 =
				documentResource.getDocumentFolderDocumentsPage(
					documentFolderId, null, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(document1, (List<Document>)page3.getItems());
			assertContains(document2, (List<Document>)page3.getItems());
			assertContains(document3, (List<Document>)page3.getItems());
		}
	}

	@Test
	public void testGetDocumentFolderDocumentsPageWithSortDateTime()
		throws Exception {

		testGetDocumentFolderDocumentsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, document1, document2) -> {
				BeanTestUtil.setProperty(
					document1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetDocumentFolderDocumentsPageWithSortDouble()
		throws Exception {

		testGetDocumentFolderDocumentsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, document1, document2) -> {
				BeanTestUtil.setProperty(document1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(document2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetDocumentFolderDocumentsPageWithSortInteger()
		throws Exception {

		testGetDocumentFolderDocumentsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, document1, document2) -> {
				BeanTestUtil.setProperty(document1, entityField.getName(), 0);
				BeanTestUtil.setProperty(document2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetDocumentFolderDocumentsPageWithSortString()
		throws Exception {

		testGetDocumentFolderDocumentsPageWithSort(
			EntityField.Type.STRING,
			(entityField, document1, document2) -> {
				Class<?> clazz = document1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						document1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						document2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						document1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						document2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						document1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						document2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetDocumentFolderDocumentsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Document, Document, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long documentFolderId =
			testGetDocumentFolderDocumentsPage_getDocumentFolderId();

		Document document1 = randomDocument();
		Document document2 = randomDocument();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, document1, document2);
		}

		document1 = testGetDocumentFolderDocumentsPage_addDocument(
			documentFolderId, document1);

		document2 = testGetDocumentFolderDocumentsPage_addDocument(
			documentFolderId, document2);

		Page<Document> page = documentResource.getDocumentFolderDocumentsPage(
			documentFolderId, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Document> ascPage =
				documentResource.getDocumentFolderDocumentsPage(
					documentFolderId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(document1, (List<Document>)ascPage.getItems());
			assertContains(document2, (List<Document>)ascPage.getItems());

			Page<Document> descPage =
				documentResource.getDocumentFolderDocumentsPage(
					documentFolderId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(document2, (List<Document>)descPage.getItems());
			assertContains(document1, (List<Document>)descPage.getItems());
		}
	}

	protected Document testGetDocumentFolderDocumentsPage_addDocument(
			Long documentFolderId, Document document)
		throws Exception {

		return documentResource.postDocumentFolderDocument(
			documentFolderId, document, getMultipartFiles());
	}

	protected Long testGetDocumentFolderDocumentsPage_getDocumentFolderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetDocumentFolderDocumentsPage_getIrrelevantDocumentFolderId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetDocumentFolderDocumentsPage() throws Exception {
		Long documentFolderId =
			testGetDocumentFolderDocumentsPage_getDocumentFolderId();

		GraphQLField graphQLField = new GraphQLField(
			"documentFolderDocuments",
			new HashMap<String, Object>() {
				{
					put("documentFolderId", documentFolderId);
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject documentFolderDocumentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/documentFolderDocuments");

		long totalCount = documentFolderDocumentsJSONObject.getLong(
			"totalCount");

		Document document1 = testGraphQLDocumentFolderDocument_addDocument(
			documentFolderId, randomDocument());

		Document document2 = testGraphQLDocumentFolderDocument_addDocument(
			documentFolderId, randomDocument());

		documentFolderDocumentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/documentFolderDocuments");

		Assert.assertEquals(
			totalCount + 2,
			documentFolderDocumentsJSONObject.getLong("totalCount"));

		assertContains(
			document1,
			Arrays.asList(
				DocumentSerDes.toDTOs(
					documentFolderDocumentsJSONObject.getString("items"))));
		assertContains(
			document2,
			Arrays.asList(
				DocumentSerDes.toDTOs(
					documentFolderDocumentsJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		documentFolderDocumentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/documentFolderDocuments");

		Assert.assertEquals(
			totalCount + 2,
			documentFolderDocumentsJSONObject.getLong("totalCount"));

		assertContains(
			document1,
			Arrays.asList(
				DocumentSerDes.toDTOs(
					documentFolderDocumentsJSONObject.getString("items"))));
		assertContains(
			document2,
			Arrays.asList(
				DocumentSerDes.toDTOs(
					documentFolderDocumentsJSONObject.getString("items"))));
	}

	@Test
	public void testGetDocumentPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Document postDocument = testGetDocumentPermissionsPage_addDocument();

		Page<Permission> page = documentResource.getDocumentPermissionsPage(
			postDocument.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected Document testGetDocumentPermissionsPage_addDocument()
		throws Exception {

		return documentResource.postSiteDocument(
			testGroup.getGroupId(), randomDocument(), getMultipartFiles());
	}

	@Test
	public void testGraphQLGetDocumentPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Document postDocument =
			testGraphQLGetDocumentPermissionsPage_addDocument();

		GraphQLField graphQLField = new GraphQLField(
			"documentPermissions",
			new HashMap<String, Object>() {
				{
					put("documentId", postDocument.getId());
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject documentPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/documentPermissions");

		Assert.assertNotNull(documentPermissionsJSONObject);
	}

	protected Document testGraphQLGetDocumentPermissionsPage_addDocument()
		throws Exception {

		return testGraphQLDocument_addDocument();
	}

	@Test
	public void testGetDocumentRenderedContentByDisplayPageDisplayPageKey()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testGetSiteDocumentByExternalReferenceCode() throws Exception {
		Document postDocument =
			testGetSiteDocumentByExternalReferenceCode_addDocument();

		Document getDocument =
			documentResource.getSiteDocumentByExternalReferenceCode(
				postDocument.getSiteId(),
				postDocument.getExternalReferenceCode());

		assertEquals(postDocument, getDocument);
		assertValid(getDocument);
	}

	protected Document testGetSiteDocumentByExternalReferenceCode_addDocument()
		throws Exception {

		return documentResource.postSiteDocument(
			testGroup.getGroupId(), randomDocument(), getMultipartFiles());
	}

	@Test
	public void testGraphQLGetSiteDocumentByExternalReferenceCode()
		throws Exception {

		Document document =
			testGraphQLGetSiteDocumentByExternalReferenceCode_addDocument();

		// No namespace

		Assert.assertTrue(
			equals(
				document,
				DocumentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"documentByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" + document.getSiteId() + "\"");
										put(
											"externalReferenceCode",
											"\"" +
												document.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/documentByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				document,
				DocumentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"documentByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" + document.getSiteId() +
													"\"");
											put(
												"externalReferenceCode",
												"\"" +
													document.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/documentByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSiteDocumentByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"documentByExternalReferenceCode",
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
							"documentByExternalReferenceCode",
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

	protected Document
			testGraphQLGetSiteDocumentByExternalReferenceCode_addDocument()
		throws Exception {

		return testGraphQLSiteDocument_addDocument();
	}

	@Test
	public void testGetSiteDocumentPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Document postDocument =
			testGetSiteDocumentPermissionsPage_addDocument();

		Page<Permission> page = documentResource.getSiteDocumentPermissionsPage(
			testGroup.getGroupId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected Document testGetSiteDocumentPermissionsPage_addDocument()
		throws Exception {

		return documentResource.postSiteDocument(
			testGroup.getGroupId(), randomDocument(), getMultipartFiles());
	}

	@Test
	public void testGraphQLGetSiteDocumentPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Document postDocument =
			testGraphQLGetSiteDocumentPermissionsPage_addDocument();

		GraphQLField graphQLField = new GraphQLField(
			"siteDocumentPermissions",
			new HashMap<String, Object>() {
				{
					put("siteKey", "\"" + postDocument.getSiteId() + "\"");
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject siteDocumentPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/siteDocumentPermissions");

		Assert.assertNotNull(siteDocumentPermissionsJSONObject);
	}

	protected Document testGraphQLGetSiteDocumentPermissionsPage_addDocument()
		throws Exception {

		return testGraphQLSiteDocument_addDocument();
	}

	@Test
	public void testGetSiteDocumentsPage() throws Exception {
		Long siteId = testGetSiteDocumentsPage_getSiteId();
		Long irrelevantSiteId = testGetSiteDocumentsPage_getIrrelevantSiteId();

		Page<Document> page = documentResource.getSiteDocumentsPage(
			siteId, null, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			Document irrelevantDocument = testGetSiteDocumentsPage_addDocument(
				irrelevantSiteId, randomIrrelevantDocument());

			page = documentResource.getSiteDocumentsPage(
				irrelevantSiteId, null, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantDocument, (List<Document>)page.getItems());
			assertValid(
				page,
				testGetSiteDocumentsPage_getExpectedActions(irrelevantSiteId));
		}

		Document document1 = testGetSiteDocumentsPage_addDocument(
			siteId, randomDocument());

		Document document2 = testGetSiteDocumentsPage_addDocument(
			siteId, randomDocument());

		page = documentResource.getSiteDocumentsPage(
			siteId, null, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(document1, (List<Document>)page.getItems());
		assertContains(document2, (List<Document>)page.getItems());
		assertValid(page, testGetSiteDocumentsPage_getExpectedActions(siteId));

		documentResource.deleteDocument(document1.getId());

		documentResource.deleteDocument(document2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteDocumentsPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/sites/{siteId}/documents/batch".
				replace("{siteId}", String.valueOf(siteId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteDocumentsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteDocumentsPage_getSiteId();

		Document document1 = randomDocument();

		document1 = testGetSiteDocumentsPage_addDocument(siteId, document1);

		for (EntityField entityField : entityFields) {
			Page<Document> page = documentResource.getSiteDocumentsPage(
				siteId, null, null, null,
				getFilterString(entityField, "between", document1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(document1),
				(List<Document>)page.getItems());
		}
	}

	@Test
	public void testGetSiteDocumentsPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteDocumentsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteDocumentsPageWithFilterStringContains()
		throws Exception {

		testGetSiteDocumentsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteDocumentsPageWithFilterStringEquals()
		throws Exception {

		testGetSiteDocumentsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteDocumentsPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteDocumentsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteDocumentsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteDocumentsPage_getSiteId();

		Document document1 = testGetSiteDocumentsPage_addDocument(
			siteId, randomDocument());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Document document2 = testGetSiteDocumentsPage_addDocument(
			siteId, randomDocument());

		for (EntityField entityField : entityFields) {
			Page<Document> page = documentResource.getSiteDocumentsPage(
				siteId, null, null, null,
				getFilterString(entityField, operator, document1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(document1),
				(List<Document>)page.getItems());
		}
	}

	@Test
	public void testGetSiteDocumentsPageWithPagination() throws Exception {
		Long siteId = testGetSiteDocumentsPage_getSiteId();

		Page<Document> documentsPage = documentResource.getSiteDocumentsPage(
			siteId, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(documentsPage.getTotalCount());

		Document document1 = testGetSiteDocumentsPage_addDocument(
			siteId, randomDocument());

		Document document2 = testGetSiteDocumentsPage_addDocument(
			siteId, randomDocument());

		Document document3 = testGetSiteDocumentsPage_addDocument(
			siteId, randomDocument());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Document> page1 = documentResource.getSiteDocumentsPage(
				siteId, null, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(document1, (List<Document>)page1.getItems());

			Page<Document> page2 = documentResource.getSiteDocumentsPage(
				siteId, null, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(document2, (List<Document>)page2.getItems());

			Page<Document> page3 = documentResource.getSiteDocumentsPage(
				siteId, null, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(document3, (List<Document>)page3.getItems());
		}
		else {
			Page<Document> page1 = documentResource.getSiteDocumentsPage(
				siteId, null, null, null, null,
				Pagination.of(1, totalCount + 2), null);

			List<Document> documents1 = (List<Document>)page1.getItems();

			Assert.assertEquals(
				documents1.toString(), totalCount + 2, documents1.size());

			Page<Document> page2 = documentResource.getSiteDocumentsPage(
				siteId, null, null, null, null,
				Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Document> documents2 = (List<Document>)page2.getItems();

			Assert.assertEquals(documents2.toString(), 1, documents2.size());

			Page<Document> page3 = documentResource.getSiteDocumentsPage(
				siteId, null, null, null, null,
				Pagination.of(1, (int)totalCount + 3), null);

			assertContains(document1, (List<Document>)page3.getItems());
			assertContains(document2, (List<Document>)page3.getItems());
			assertContains(document3, (List<Document>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteDocumentsPageWithSortDateTime() throws Exception {
		testGetSiteDocumentsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, document1, document2) -> {
				BeanTestUtil.setProperty(
					document1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteDocumentsPageWithSortDouble() throws Exception {
		testGetSiteDocumentsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, document1, document2) -> {
				BeanTestUtil.setProperty(document1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(document2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteDocumentsPageWithSortInteger() throws Exception {
		testGetSiteDocumentsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, document1, document2) -> {
				BeanTestUtil.setProperty(document1, entityField.getName(), 0);
				BeanTestUtil.setProperty(document2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteDocumentsPageWithSortString() throws Exception {
		testGetSiteDocumentsPageWithSort(
			EntityField.Type.STRING,
			(entityField, document1, document2) -> {
				Class<?> clazz = document1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						document1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						document2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						document1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						document2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						document1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						document2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteDocumentsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Document, Document, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteDocumentsPage_getSiteId();

		Document document1 = randomDocument();
		Document document2 = randomDocument();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, document1, document2);
		}

		document1 = testGetSiteDocumentsPage_addDocument(siteId, document1);

		document2 = testGetSiteDocumentsPage_addDocument(siteId, document2);

		Page<Document> page = documentResource.getSiteDocumentsPage(
			siteId, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Document> ascPage = documentResource.getSiteDocumentsPage(
				siteId, null, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(document1, (List<Document>)ascPage.getItems());
			assertContains(document2, (List<Document>)ascPage.getItems());

			Page<Document> descPage = documentResource.getSiteDocumentsPage(
				siteId, null, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(document2, (List<Document>)descPage.getItems());
			assertContains(document1, (List<Document>)descPage.getItems());
		}
	}

	protected Document testGetSiteDocumentsPage_addDocument(
			Long siteId, Document document)
		throws Exception {

		return documentResource.postSiteDocument(
			siteId, document, getMultipartFiles());
	}

	protected Long testGetSiteDocumentsPage_getSiteId() throws Exception {
		return testGroup.getGroupId();
	}

	protected Long testGetSiteDocumentsPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteDocumentsPage() throws Exception {
		Long siteId = testGetSiteDocumentsPage_getSiteId();

		GraphQLField graphQLField = new GraphQLField(
			"documents",
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

		JSONObject documentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/documents");

		long totalCount = documentsJSONObject.getLong("totalCount");

		Document document1 = testGraphQLSiteDocument_addDocument(
			siteId, randomDocument());

		Document document2 = testGraphQLSiteDocument_addDocument(
			siteId, randomDocument());

		documentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/documents");

		Assert.assertEquals(
			totalCount + 2, documentsJSONObject.getLong("totalCount"));

		assertContains(
			document1,
			Arrays.asList(
				DocumentSerDes.toDTOs(documentsJSONObject.getString("items"))));
		assertContains(
			document2,
			Arrays.asList(
				DocumentSerDes.toDTOs(documentsJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		documentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/documents");

		Assert.assertEquals(
			totalCount + 2, documentsJSONObject.getLong("totalCount"));

		assertContains(
			document1,
			Arrays.asList(
				DocumentSerDes.toDTOs(documentsJSONObject.getString("items"))));
		assertContains(
			document2,
			Arrays.asList(
				DocumentSerDes.toDTOs(documentsJSONObject.getString("items"))));
	}

	@Test
	public void testGetSiteDocumentsRatedByMePage() throws Exception {
		Long siteId = testGetSiteDocumentsRatedByMePage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteDocumentsRatedByMePage_getIrrelevantSiteId();

		Page<Document> page = documentResource.getSiteDocumentsRatedByMePage(
			siteId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			Document irrelevantDocument =
				testGetSiteDocumentsRatedByMePage_addDocument(
					irrelevantSiteId, randomIrrelevantDocument());

			page = documentResource.getSiteDocumentsRatedByMePage(
				irrelevantSiteId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantDocument, (List<Document>)page.getItems());
			assertValid(
				page,
				testGetSiteDocumentsRatedByMePage_getExpectedActions(
					irrelevantSiteId));
		}

		Document document1 = testGetSiteDocumentsRatedByMePage_addDocument(
			siteId, randomDocument());

		Document document2 = testGetSiteDocumentsRatedByMePage_addDocument(
			siteId, randomDocument());

		page = documentResource.getSiteDocumentsRatedByMePage(
			siteId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(document1, (List<Document>)page.getItems());
		assertContains(document2, (List<Document>)page.getItems());
		assertValid(
			page, testGetSiteDocumentsRatedByMePage_getExpectedActions(siteId));

		documentResource.deleteDocument(document1.getId());

		documentResource.deleteDocument(document2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteDocumentsRatedByMePage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteDocumentsRatedByMePageWithPagination()
		throws Exception {

		Long siteId = testGetSiteDocumentsRatedByMePage_getSiteId();

		Page<Document> documentsPage =
			documentResource.getSiteDocumentsRatedByMePage(siteId, null);

		int totalCount = GetterUtil.getInteger(documentsPage.getTotalCount());

		Document document1 = testGetSiteDocumentsRatedByMePage_addDocument(
			siteId, randomDocument());

		Document document2 = testGetSiteDocumentsRatedByMePage_addDocument(
			siteId, randomDocument());

		Document document3 = testGetSiteDocumentsRatedByMePage_addDocument(
			siteId, randomDocument());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Document> page1 =
				documentResource.getSiteDocumentsRatedByMePage(
					siteId,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(document1, (List<Document>)page1.getItems());

			Page<Document> page2 =
				documentResource.getSiteDocumentsRatedByMePage(
					siteId,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(document2, (List<Document>)page2.getItems());

			Page<Document> page3 =
				documentResource.getSiteDocumentsRatedByMePage(
					siteId,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(document3, (List<Document>)page3.getItems());
		}
		else {
			Page<Document> page1 =
				documentResource.getSiteDocumentsRatedByMePage(
					siteId, Pagination.of(1, totalCount + 2));

			List<Document> documents1 = (List<Document>)page1.getItems();

			Assert.assertEquals(
				documents1.toString(), totalCount + 2, documents1.size());

			Page<Document> page2 =
				documentResource.getSiteDocumentsRatedByMePage(
					siteId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Document> documents2 = (List<Document>)page2.getItems();

			Assert.assertEquals(documents2.toString(), 1, documents2.size());

			Page<Document> page3 =
				documentResource.getSiteDocumentsRatedByMePage(
					siteId, Pagination.of(1, (int)totalCount + 3));

			assertContains(document1, (List<Document>)page3.getItems());
			assertContains(document2, (List<Document>)page3.getItems());
			assertContains(document3, (List<Document>)page3.getItems());
		}
	}

	protected Document testGetSiteDocumentsRatedByMePage_addDocument(
			Long siteId, Document document)
		throws Exception {

		return documentResource.postSiteDocument(
			siteId, document, getMultipartFiles());
	}

	protected Long testGetSiteDocumentsRatedByMePage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long testGetSiteDocumentsRatedByMePage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteDocumentsRatedByMePage() throws Exception {
		Long siteId = testGetSiteDocumentsRatedByMePage_getSiteId();

		GraphQLField graphQLField = new GraphQLField(
			"documentsRatedByMe",
			new HashMap<String, Object>() {
				{
					put("siteKey", "\"" + siteId + "\"");
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject documentsRatedByMeJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/documentsRatedByMe");

		long totalCount = documentsRatedByMeJSONObject.getLong("totalCount");

		Document document1 = testGraphQLSiteDocument_addDocument(
			siteId, randomDocument());

		Document document2 = testGraphQLSiteDocument_addDocument(
			siteId, randomDocument());

		documentsRatedByMeJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/documentsRatedByMe");

		Assert.assertEquals(
			totalCount + 2, documentsRatedByMeJSONObject.getLong("totalCount"));

		assertContains(
			document1,
			Arrays.asList(
				DocumentSerDes.toDTOs(
					documentsRatedByMeJSONObject.getString("items"))));
		assertContains(
			document2,
			Arrays.asList(
				DocumentSerDes.toDTOs(
					documentsRatedByMeJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		documentsRatedByMeJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/documentsRatedByMe");

		Assert.assertEquals(
			totalCount + 2, documentsRatedByMeJSONObject.getLong("totalCount"));

		assertContains(
			document1,
			Arrays.asList(
				DocumentSerDes.toDTOs(
					documentsRatedByMeJSONObject.getString("items"))));
		assertContains(
			document2,
			Arrays.asList(
				DocumentSerDes.toDTOs(
					documentsRatedByMeJSONObject.getString("items"))));
	}

	@Test
	public void testPatchDocument() throws Exception {
		Document postDocument = testPatchDocument_addDocument();

		Document randomPatchDocument = randomPatchDocument();

		Map<String, File> multipartFiles = getMultipartFiles();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Document patchDocument = documentResource.patchDocument(
			postDocument.getId(), randomPatchDocument, multipartFiles);

		Document expectedPatchDocument = postDocument.clone();

		BeanTestUtil.copyProperties(randomPatchDocument, expectedPatchDocument);

		Document getDocument = documentResource.getDocument(
			patchDocument.getId());

		assertEquals(expectedPatchDocument, getDocument);
		assertValid(getDocument);

		assertValid(getDocument, multipartFiles);
	}

	protected Document testPatchDocument_addDocument() throws Exception {
		return documentResource.postSiteDocument(
			testGroup.getGroupId(), randomDocument(), getMultipartFiles());
	}

	@Test
	public void testPostAssetLibraryDocument() throws Exception {
		Document randomDocument = randomDocument();

		Map<String, File> multipartFiles = getMultipartFiles();

		Document postDocument = testPostAssetLibraryDocument_addDocument(
			randomDocument, multipartFiles);

		assertEquals(randomDocument, postDocument);
		assertValid(postDocument);

		assertValid(postDocument, multipartFiles);
	}

	protected Document testPostAssetLibraryDocument_addDocument(
			Document document, Map<String, File> multipartFiles)
		throws Exception {

		return documentResource.postAssetLibraryDocument(
			testGetAssetLibraryDocumentsPage_getAssetLibraryId(), document,
			multipartFiles);
	}

	@Test
	public void testPostDocumentFolderDocument() throws Exception {
		Document randomDocument = randomDocument();

		Map<String, File> multipartFiles = getMultipartFiles();

		Document postDocument = testPostDocumentFolderDocument_addDocument(
			randomDocument, multipartFiles);

		assertEquals(randomDocument, postDocument);
		assertValid(postDocument);

		assertValid(postDocument, multipartFiles);
	}

	protected Document testPostDocumentFolderDocument_addDocument(
			Document document, Map<String, File> multipartFiles)
		throws Exception {

		return documentResource.postDocumentFolderDocument(
			testGetDocumentFolderDocumentsPage_getDocumentFolderId(), document,
			multipartFiles);
	}

	@Test
	public void testPostSiteDocument() throws Exception {
		Document randomDocument = randomDocument();

		Map<String, File> multipartFiles = getMultipartFiles();

		Document postDocument = testPostSiteDocument_addDocument(
			randomDocument, multipartFiles);

		assertEquals(randomDocument, postDocument);
		assertValid(postDocument);

		assertValid(postDocument, multipartFiles);
	}

	protected Document testPostSiteDocument_addDocument(
			Document document, Map<String, File> multipartFiles)
		throws Exception {

		return documentResource.postSiteDocument(
			testGetSiteDocumentsPage_getSiteId(), document, multipartFiles);
	}

	@Test
	public void testPutAssetLibraryDocumentByExternalReferenceCode()
		throws Exception {

		Document postDocument =
			testPutAssetLibraryDocumentByExternalReferenceCode_addDocument();

		Document randomDocument = randomDocument();

		Map<String, File> multipartFiles = getMultipartFiles();

		Document putDocument =
			documentResource.putAssetLibraryDocumentByExternalReferenceCode(
				testPutAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId(),
				postDocument.getExternalReferenceCode(), randomDocument,
				multipartFiles);

		assertEquals(randomDocument, putDocument);
		assertValid(putDocument);

		Document getDocument =
			documentResource.getAssetLibraryDocumentByExternalReferenceCode(
				testPutAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId(),
				putDocument.getExternalReferenceCode());

		assertEquals(randomDocument, getDocument);
		assertValid(getDocument);

		assertValid(getDocument, multipartFiles);

		Document newDocument =
			testPutAssetLibraryDocumentByExternalReferenceCode_createDocument();

		putDocument =
			documentResource.putAssetLibraryDocumentByExternalReferenceCode(
				testPutAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId(),
				newDocument.getExternalReferenceCode(), newDocument,
				getMultipartFiles());

		assertEquals(newDocument, putDocument);
		assertValid(putDocument);

		getDocument =
			documentResource.getAssetLibraryDocumentByExternalReferenceCode(
				testPutAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId(),
				putDocument.getExternalReferenceCode());

		assertEquals(newDocument, getDocument);

		Assert.assertEquals(
			newDocument.getExternalReferenceCode(),
			putDocument.getExternalReferenceCode());
	}

	protected Document
			testPutAssetLibraryDocumentByExternalReferenceCode_addDocument()
		throws Exception {

		return documentResource.postAssetLibraryDocument(
			testDepotEntry.getDepotEntryId(), randomDocument(),
			getMultipartFiles());
	}

	protected Long
			testPutAssetLibraryDocumentByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Document
			testPutAssetLibraryDocumentByExternalReferenceCode_createDocument()
		throws Exception {

		return randomDocument();
	}

	@Test
	public void testPutAssetLibraryDocumentPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Document document =
			testPutAssetLibraryDocumentPermissionsPage_addDocument();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			documentResource.putAssetLibraryDocumentPermissionsPageHttpResponse(
				testDepotEntry.getDepotEntryId(),
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
			documentResource.putAssetLibraryDocumentPermissionsPageHttpResponse(
				testDepotEntry.getDepotEntryId(),
				new Permission[] {
					new Permission() {
						{
							setActionIds(new String[] {"-"});
							setRoleName("-");
						}
					}
				}));
	}

	protected Document testPutAssetLibraryDocumentPermissionsPage_addDocument()
		throws Exception {

		return documentResource.postAssetLibraryDocument(
			testDepotEntry.getDepotEntryId(), randomDocument(),
			getMultipartFiles());
	}

	@Test
	public void testPutDocument() throws Exception {
		Document postDocument = testPutDocument_addDocument();

		Document randomDocument = randomDocument();

		Map<String, File> multipartFiles = getMultipartFiles();

		Document putDocument = documentResource.putDocument(
			postDocument.getId(), randomDocument, multipartFiles);

		assertEquals(randomDocument, putDocument);
		assertValid(putDocument);

		Document getDocument = documentResource.getDocument(
			putDocument.getId());

		assertEquals(randomDocument, getDocument);
		assertValid(getDocument);

		assertValid(getDocument, multipartFiles);
	}

	protected Document testPutDocument_addDocument() throws Exception {
		return documentResource.postSiteDocument(
			testGroup.getGroupId(), randomDocument(), getMultipartFiles());
	}

	@Test
	public void testPutDocumentPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Document document = testPutDocumentPermissionsPage_addDocument();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			documentResource.putDocumentPermissionsPageHttpResponse(
				document.getId(),
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
			documentResource.putDocumentPermissionsPageHttpResponse(
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

	protected Document testPutDocumentPermissionsPage_addDocument()
		throws Exception {

		return documentResource.postSiteDocument(
			testGroup.getGroupId(), randomDocument(), getMultipartFiles());
	}

	@Test
	public void testPutSiteDocumentByExternalReferenceCode() throws Exception {
		Document postDocument =
			testPutSiteDocumentByExternalReferenceCode_addDocument();

		Document randomDocument = randomDocument();

		Map<String, File> multipartFiles = getMultipartFiles();

		Document putDocument =
			documentResource.putSiteDocumentByExternalReferenceCode(
				postDocument.getSiteId(),
				postDocument.getExternalReferenceCode(), randomDocument,
				multipartFiles);

		assertEquals(randomDocument, putDocument);
		assertValid(putDocument);

		Document getDocument =
			documentResource.getSiteDocumentByExternalReferenceCode(
				putDocument.getSiteId(),
				putDocument.getExternalReferenceCode());

		assertEquals(randomDocument, getDocument);
		assertValid(getDocument);

		assertValid(getDocument, multipartFiles);

		Document newDocument =
			testPutSiteDocumentByExternalReferenceCode_createDocument();

		putDocument = documentResource.putSiteDocumentByExternalReferenceCode(
			newDocument.getSiteId(), newDocument.getExternalReferenceCode(),
			newDocument, getMultipartFiles());

		assertEquals(newDocument, putDocument);
		assertValid(putDocument);

		getDocument = documentResource.getSiteDocumentByExternalReferenceCode(
			putDocument.getSiteId(), putDocument.getExternalReferenceCode());

		assertEquals(newDocument, getDocument);

		Assert.assertEquals(
			newDocument.getExternalReferenceCode(),
			putDocument.getExternalReferenceCode());
	}

	protected Document testPutSiteDocumentByExternalReferenceCode_addDocument()
		throws Exception {

		return documentResource.postSiteDocument(
			testGroup.getGroupId(), randomDocument(), getMultipartFiles());
	}

	protected Document
			testPutSiteDocumentByExternalReferenceCode_createDocument()
		throws Exception {

		return randomDocument();
	}

	@Test
	public void testPutSiteDocumentPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Document document = testPutSiteDocumentPermissionsPage_addDocument();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			documentResource.putSiteDocumentPermissionsPageHttpResponse(
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
			documentResource.putSiteDocumentPermissionsPageHttpResponse(
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

	protected Document testPutSiteDocumentPermissionsPage_addDocument()
		throws Exception {

		return documentResource.postSiteDocument(
			testGroup.getGroupId(), randomDocument(), getMultipartFiles());
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Document document1 = testBatchEngineDeleteImportTask_addDocument();

		testBatchEngineDeleteImportTask_deleteDocument(
			200, null, document1.getId());

		assertHttpResponseStatusCode(
			404, documentResource.getDocumentHttpResponse(document1.getId()));
	}

	protected Document testBatchEngineDeleteImportTask_addDocument()
		throws Exception {

		return testDeleteDocument_addDocument();
	}

	protected void testBatchEngineDeleteImportTask_deleteDocument(
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
				"com.liferay.headless.delivery.dto.v1_0.Document", null, null,
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

	@Test
	public void testGetDocumentMyRating() throws Exception {
		Document postDocument = testGetDocument_addDocument();

		Rating postRating = testGetDocumentMyRating_addRating(
			postDocument.getId(), randomRating());

		Rating getRating = documentResource.getDocumentMyRating(
			postDocument.getId());

		assertEquals(postRating, getRating);
		assertValid(getRating);
	}

	protected Rating testGetDocumentMyRating_addRating(
			long documentId, Rating rating)
		throws Exception {

		return documentResource.postDocumentMyRating(documentId, rating);
	}

	@Test
	public void testPostDocumentMyRating() throws Exception {
		Assert.assertTrue(true);
	}

	@Test
	public void testPutDocumentMyRating() throws Exception {
		Document postDocument = testPutDocument_addDocument();

		testPutDocumentMyRating_addRating(postDocument.getId(), randomRating());

		Rating randomRating = randomRating();

		Rating putRating = documentResource.putDocumentMyRating(
			postDocument.getId(), randomRating);

		assertEquals(randomRating, putRating);
		assertValid(putRating);
	}

	protected Rating testPutDocumentMyRating_addRating(
			long documentId, Rating rating)
		throws Exception {

		return documentResource.postDocumentMyRating(documentId, rating);
	}

	protected Document testGraphQLAssetLibraryDocument_addDocument()
		throws Exception {

		return testGraphQLAssetLibraryDocument_addDocument(
			testDepotEntry.getDepotEntryId(), randomDocument());
	}

	protected Document testGraphQLAssetLibraryDocument_addDocument(
			Long assetLibraryId, Document document)
		throws Exception {

		JSONDeserializer<Document> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(Document.class)) {

			if (getGraphQLValue(field.get(document)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(document)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createAssetLibraryDocument",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryId",
									"\"" + assetLibraryId + "\"");
								put("document", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createAssetLibraryDocument"),
			Document.class);
	}

	protected Document testGraphQLDocument_addDocument() throws Exception {
		return testGraphQLDocument_addDocument(
			testGroup.getGroupId(), randomDocument());
	}

	protected Document testGraphQLDocument_addDocument(
			Long siteId, Document document)
		throws Exception {

		JSONDeserializer<Document> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(Document.class)) {

			if (getGraphQLValue(field.get(document)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(document)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteDocument",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("document", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteDocument"),
			Document.class);
	}

	protected Document testGraphQLSiteDocument_addDocument() throws Exception {
		return testGraphQLSiteDocument_addDocument(
			testGroup.getGroupId(), randomDocument());
	}

	protected Document testGraphQLSiteDocument_addDocument(
			Long siteId, Document document)
		throws Exception {

		JSONDeserializer<Document> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(Document.class)) {

			if (getGraphQLValue(field.get(document)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(document)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteDocument",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("document", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteDocument"),
			Document.class);
	}

	protected Document testGraphQLDocumentFolderDocument_addDocument()
		throws Exception {

		return testGraphQLDocumentFolderDocument_addDocument(
			testGraphQLDocumentFolderDocument_getDocumentFolderId(),
			randomDocument());
	}

	protected Long testGraphQLDocumentFolderDocument_getDocumentFolderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Document testGraphQLDocumentFolderDocument_addDocument(
			Long documentFolderId, Document document)
		throws Exception {

		JSONDeserializer<Document> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(Document.class)) {

			if (getGraphQLValue(field.get(document)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(document)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createDocumentFolderDocument",
						new HashMap<String, Object>() {
							{
								put("documentFolderId", documentFolderId);
								put("document", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createDocumentFolderDocument"),
			Document.class);
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

	protected void assertContains(Document document, List<Document> documents) {
		boolean contains = false;

		for (Document item : documents) {
			if (equals(document, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			documents + " does not contain " + document, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Document document1, Document document2) {
		Assert.assertTrue(
			document1 + " does not equal " + document2,
			equals(document1, document2));
	}

	protected void assertEquals(
		List<Document> documents1, List<Document> documents2) {

		Assert.assertEquals(documents1.size(), documents2.size());

		for (int i = 0; i < documents1.size(); i++) {
			Document document1 = documents1.get(i);
			Document document2 = documents2.get(i);

			assertEquals(document1, document2);
		}
	}

	protected void assertEquals(Rating rating1, Rating rating2) {
		Assert.assertTrue(
			rating1 + " does not equal " + rating2, equals(rating1, rating2));
	}

	protected void assertEqualsIgnoringOrder(
		List<Document> documents1, List<Document> documents2) {

		Assert.assertEquals(documents1.size(), documents2.size());

		for (Document document1 : documents1) {
			boolean contains = false;

			for (Document document2 : documents2) {
				if (equals(document1, document2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				documents2 + " does not contain " + document1, contains);
		}
	}

	protected void assertValid(Document document) throws Exception {
		boolean valid = true;

		if (document.getDateCreated() == null) {
			valid = false;
		}

		if (document.getDateModified() == null) {
			valid = false;
		}

		if (document.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				document.getAssetLibraryKey(),
				testDepotEntryGroup.getGroupKey()) &&
			!Objects.equals(document.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (document.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("adaptedImages", additionalAssertFieldName)) {
				if (document.getAdaptedImages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("aggregateRating", additionalAssertFieldName)) {
				if (document.getAggregateRating() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetLibraryKey", additionalAssertFieldName)) {
				if (document.getAssetLibraryKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("contentUrl", additionalAssertFieldName)) {
				if (document.getContentUrl() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("contentValue", additionalAssertFieldName)) {
				if (document.getContentValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (document.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (document.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dateExpired", additionalAssertFieldName)) {
				if (document.getDateExpired() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (document.getDatePublished() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (document.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"documentFolderExternalReferenceCode",
					additionalAssertFieldName)) {

				if (document.getDocumentFolderExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("documentFolderId", additionalAssertFieldName)) {
				if (document.getDocumentFolderId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("documentType", additionalAssertFieldName)) {
				if (document.getDocumentType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (document.getEncodingFormat() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (document.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("fileExtension", additionalAssertFieldName)) {
				if (document.getFileExtension() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("fileName", additionalAssertFieldName)) {
				if (document.getFileName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (document.getFriendlyUrlPath() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (document.getKeywords() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("numberOfComments", additionalAssertFieldName)) {
				if (document.getNumberOfComments() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("relatedContents", additionalAssertFieldName)) {
				if (document.getRelatedContents() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("renderedContents", additionalAssertFieldName)) {
				if (document.getRenderedContents() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sizeInBytes", additionalAssertFieldName)) {
				if (document.getSizeInBytes() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryBriefs", additionalAssertFieldName)) {

				if (document.getTaxonomyCategoryBriefs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryIds", additionalAssertFieldName)) {

				if (document.getTaxonomyCategoryIds() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (document.getTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (document.getViewableBy() == null) {
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

	protected void assertValid(
			Document document, Map<String, File> multipartFiles)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertValid(Page<Document> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Document> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Document> documents = page.getItems();

		int size = documents.size();

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

	protected void assertValid(Rating rating) {
		boolean valid = true;

		if (rating.getDateCreated() == null) {
			valid = false;
		}

		if (rating.getDateModified() == null) {
			valid = false;
		}

		if (rating.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalRatingAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (rating.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("bestRating", additionalAssertFieldName)) {
				if (rating.getBestRating() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (rating.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("ratingValue", additionalAssertFieldName)) {
				if (rating.getRatingValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("worstRating", additionalAssertFieldName)) {
				if (rating.getWorstRating() == null) {
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

	protected String[] getAdditionalRatingAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		graphQLFields.add(new GraphQLField("id"));

		graphQLFields.add(new GraphQLField("siteId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.delivery.dto.v1_0.Document.class)) {

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

	protected boolean equals(Document document1, Document document2) {
		if (document1 == document2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)document1.getActions(),
						(Map)document2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("adaptedImages", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getAdaptedImages(),
						document2.getAdaptedImages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("aggregateRating", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getAggregateRating(),
						document2.getAggregateRating())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("contentUrl", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getContentUrl(), document2.getContentUrl())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("contentValue", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getContentValue(),
						document2.getContentValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getCreator(), document2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getCustomFields(),
						document2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getDateCreated(),
						document2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateExpired", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getDateExpired(),
						document2.getDateExpired())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getDateModified(),
						document2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getDatePublished(),
						document2.getDatePublished())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getDescription(),
						document2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"documentFolderExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						document1.getDocumentFolderExternalReferenceCode(),
						document2.getDocumentFolderExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("documentFolderId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getDocumentFolderId(),
						document2.getDocumentFolderId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("documentType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getDocumentType(),
						document2.getDocumentType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getEncodingFormat(),
						document2.getEncodingFormat())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						document1.getExternalReferenceCode(),
						document2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fileExtension", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getFileExtension(),
						document2.getFileExtension())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fileName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getFileName(), document2.getFileName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getFriendlyUrlPath(),
						document2.getFriendlyUrlPath())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(document1.getId(), document2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getKeywords(), document2.getKeywords())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("numberOfComments", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getNumberOfComments(),
						document2.getNumberOfComments())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("relatedContents", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getRelatedContents(),
						document2.getRelatedContents())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("renderedContents", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getRenderedContents(),
						document2.getRenderedContents())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sizeInBytes", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getSizeInBytes(),
						document2.getSizeInBytes())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryBriefs", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						document1.getTaxonomyCategoryBriefs(),
						document2.getTaxonomyCategoryBriefs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryIds", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						document1.getTaxonomyCategoryIds(),
						document2.getTaxonomyCategoryIds())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getTitle(), document2.getTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						document1.getViewableBy(), document2.getViewableBy())) {

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

	protected boolean equals(Rating rating1, Rating rating2) {
		if (rating1 == rating2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalRatingAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getActions(), rating2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("bestRating", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getBestRating(), rating2.getBestRating())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getCreator(), rating2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getDateCreated(), rating2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getDateModified(), rating2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(rating1.getId(), rating2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("ratingValue", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getRatingValue(), rating2.getRatingValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("worstRating", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getWorstRating(), rating2.getWorstRating())) {

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

		if (!(_documentResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_documentResource;

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
		EntityField entityField, String operator, Document document) {

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

		if (entityFieldName.equals("adaptedImages")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("aggregateRating")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("assetLibraryKey")) {
			Object object = document.getAssetLibraryKey();

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

		if (entityFieldName.equals("contentUrl")) {
			Object object = document.getContentUrl();

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

		if (entityFieldName.equals("contentValue")) {
			Object object = document.getContentValue();

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
				Date date = document.getDateCreated();

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

				sb.append(_format.format(document.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateExpired")) {
			if (operator.equals("between")) {
				Date date = document.getDateExpired();

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

				sb.append(_format.format(document.getDateExpired()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = document.getDateModified();

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

				sb.append(_format.format(document.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("datePublished")) {
			if (operator.equals("between")) {
				Date date = document.getDatePublished();

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

				sb.append(_format.format(document.getDatePublished()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = document.getDescription();

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

		if (entityFieldName.equals("documentFolderExternalReferenceCode")) {
			Object object = document.getDocumentFolderExternalReferenceCode();

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

		if (entityFieldName.equals("documentFolderId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("documentType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("encodingFormat")) {
			Object object = document.getEncodingFormat();

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
			Object object = document.getExternalReferenceCode();

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

		if (entityFieldName.equals("fileExtension")) {
			Object object = document.getFileExtension();

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

		if (entityFieldName.equals("fileName")) {
			Object object = document.getFileName();

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
			Object object = document.getFriendlyUrlPath();

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

		if (entityFieldName.equals("keywords")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("numberOfComments")) {
			sb.append(String.valueOf(document.getNumberOfComments()));

			return sb.toString();
		}

		if (entityFieldName.equals("relatedContents")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("renderedContents")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("sizeInBytes")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("taxonomyCategoryBriefs")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("taxonomyCategoryIds")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("title")) {
			Object object = document.getTitle();

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

	protected Map<String, File> getMultipartFiles() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
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

	protected Document randomDocument() throws Exception {
		return new Document() {
			{
				assetLibraryKey = String.valueOf(
					testDepotEntry.getDepotEntryId());
				contentUrl = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				contentValue = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateExpired = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				datePublished = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				documentFolderExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				documentFolderId = RandomTestUtil.randomLong();
				encodingFormat = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				fileExtension = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				fileName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				friendlyUrlPath = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				numberOfComments = RandomTestUtil.randomInt();
				siteId = testGroup.getGroupId();
				sizeInBytes = RandomTestUtil.randomLong();
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected Document randomIrrelevantDocument() throws Exception {
		Document randomIrrelevantDocument = randomDocument();

		randomIrrelevantDocument.setAssetLibraryKey(
			String.valueOf(irrelevantDepotEntry.getDepotEntryId()));

		randomIrrelevantDocument.setSiteId(irrelevantGroup.getGroupId());

		return randomIrrelevantDocument;
	}

	protected Document randomPatchDocument() throws Exception {
		return randomDocument();
	}

	protected Rating randomRating() throws Exception {
		return new Rating() {
			{
				bestRating = RandomTestUtil.randomDouble();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				id = RandomTestUtil.randomLong();
				ratingValue = RandomTestUtil.randomDouble();
				worstRating = RandomTestUtil.randomDouble();
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

	protected DocumentResource documentResource;
	protected ImportTaskResource importTaskResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
	protected DepotEntry irrelevantDepotEntry;
	protected com.liferay.portal.kernel.model.Group irrelevantDepotEntryGroup;
	protected DepotEntry testDepotEntry;
	protected com.liferay.portal.kernel.model.Group testDepotEntryGroup;
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
		LogFactoryUtil.getLog(BaseDocumentResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.delivery.resource.v1_0.DocumentResource
		_documentResource;

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