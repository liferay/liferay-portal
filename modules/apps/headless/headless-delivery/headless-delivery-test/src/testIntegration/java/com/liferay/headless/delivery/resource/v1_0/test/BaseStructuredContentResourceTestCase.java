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
import com.liferay.headless.delivery.client.dto.v1_0.Field;
import com.liferay.headless.delivery.client.dto.v1_0.Rating;
import com.liferay.headless.delivery.client.dto.v1_0.StructuredContent;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.permission.Permission;
import com.liferay.headless.delivery.client.resource.v1_0.StructuredContentResource;
import com.liferay.headless.delivery.client.serdes.v1_0.StructuredContentSerDes;
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
public abstract class BaseStructuredContentResourceTestCase {

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

		_structuredContentResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		structuredContentResource = StructuredContentResource.builder(
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

		StructuredContent structuredContent1 = randomStructuredContent();

		String json = objectMapper.writeValueAsString(structuredContent1);

		StructuredContent structuredContent2 = StructuredContentSerDes.toDTO(
			json);

		Assert.assertTrue(equals(structuredContent1, structuredContent2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		StructuredContent structuredContent = randomStructuredContent();

		String json1 = objectMapper.writeValueAsString(structuredContent);
		String json2 = StructuredContentSerDes.toJSON(structuredContent);

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

		StructuredContent structuredContent = randomStructuredContent();

		structuredContent.setAssetLibraryKey(regex);
		structuredContent.setDescription(regex);
		structuredContent.setExternalReferenceCode(regex);
		structuredContent.setFriendlyUrlPath(regex);
		structuredContent.setKey(regex);
		structuredContent.setTitle(regex);
		structuredContent.setUuid(regex);

		String json = StructuredContentSerDes.toJSON(structuredContent);

		Assert.assertFalse(json.contains(regex));

		structuredContent = StructuredContentSerDes.toDTO(json);

		Assert.assertEquals(regex, structuredContent.getAssetLibraryKey());
		Assert.assertEquals(regex, structuredContent.getDescription());
		Assert.assertEquals(
			regex, structuredContent.getExternalReferenceCode());
		Assert.assertEquals(regex, structuredContent.getFriendlyUrlPath());
		Assert.assertEquals(regex, structuredContent.getKey());
		Assert.assertEquals(regex, structuredContent.getTitle());
		Assert.assertEquals(regex, structuredContent.getUuid());
	}

	@Test
	public void testDeleteAssetLibraryStructuredContentByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent structuredContent =
			testDeleteAssetLibraryStructuredContentByExternalReferenceCode_addStructuredContent();

		assertHttpResponseStatusCode(
			204,
			structuredContentResource.
				deleteAssetLibraryStructuredContentByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId(),
					structuredContent.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			structuredContentResource.
				getAssetLibraryStructuredContentByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId(),
					structuredContent.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			structuredContentResource.
				getAssetLibraryStructuredContentByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId(),
					"-"));
	}

	protected StructuredContent
			testDeleteAssetLibraryStructuredContentByExternalReferenceCode_addStructuredContent()
		throws Exception {

		return structuredContentResource.postAssetLibraryStructuredContent(
			testDepotEntry.getDepotEntryId(), randomStructuredContent());
	}

	protected Long
			testDeleteAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLDeleteAssetLibraryStructuredContentByExternalReferenceCode()
		throws Exception {

		// No namespace

		StructuredContent structuredContent1 =
			testGraphQLDeleteAssetLibraryStructuredContentByExternalReferenceCode_addStructuredContent();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAssetLibraryStructuredContentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryId",
									"\"" +
										testGraphQLDeleteAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" +
										structuredContent1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteAssetLibraryStructuredContentByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"assetLibraryStructuredContentByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"assetLibraryId",
								"\"" +
									testGraphQLDeleteAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId() +
										"\"");
							put(
								"externalReferenceCode",
								"\"" +
									structuredContent1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		StructuredContent structuredContent2 =
			testGraphQLDeleteAssetLibraryStructuredContentByExternalReferenceCode_addStructuredContent();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteAssetLibraryStructuredContentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"assetLibraryId",
										"\"" +
											testGraphQLDeleteAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId() +
												"\"");
									put(
										"externalReferenceCode",
										"\"" +
											structuredContent2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteAssetLibraryStructuredContentByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"assetLibraryStructuredContentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryId",
									"\"" +
										testGraphQLDeleteAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" +
										structuredContent2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Long
			testGraphQLDeleteAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected StructuredContent
			testGraphQLDeleteAssetLibraryStructuredContentByExternalReferenceCode_addStructuredContent()
		throws Exception {

		return testGraphQLAssetLibraryStructuredContent_addStructuredContent();
	}

	@Test
	public void testDeleteSiteStructuredContentByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent structuredContent =
			testDeleteSiteStructuredContentByExternalReferenceCode_addStructuredContent();

		assertHttpResponseStatusCode(
			204,
			structuredContentResource.
				deleteSiteStructuredContentByExternalReferenceCodeHttpResponse(
					structuredContent.getSiteId(),
					structuredContent.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			structuredContentResource.
				getSiteStructuredContentByExternalReferenceCodeHttpResponse(
					structuredContent.getSiteId(),
					structuredContent.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			structuredContentResource.
				getSiteStructuredContentByExternalReferenceCodeHttpResponse(
					structuredContent.getSiteId(), "-"));
	}

	protected StructuredContent
			testDeleteSiteStructuredContentByExternalReferenceCode_addStructuredContent()
		throws Exception {

		return structuredContentResource.postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	@Test
	public void testGraphQLDeleteSiteStructuredContentByExternalReferenceCode()
		throws Exception {

		// No namespace

		StructuredContent structuredContent1 =
			testGraphQLDeleteSiteStructuredContentByExternalReferenceCode_addStructuredContent();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteStructuredContentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + structuredContent1.getSiteId() +
										"\"");
								put(
									"externalReferenceCode",
									"\"" +
										structuredContent1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteStructuredContentByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"structuredContentByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"siteKey",
								"\"" + structuredContent1.getSiteId() + "\"");
							put(
								"externalReferenceCode",
								"\"" +
									structuredContent1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		StructuredContent structuredContent2 =
			testGraphQLDeleteSiteStructuredContentByExternalReferenceCode_addStructuredContent();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteSiteStructuredContentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + structuredContent2.getSiteId() +
											"\"");
									put(
										"externalReferenceCode",
										"\"" +
											structuredContent2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteSiteStructuredContentByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"structuredContentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + structuredContent2.getSiteId() +
										"\"");
								put(
									"externalReferenceCode",
									"\"" +
										structuredContent2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected StructuredContent
			testGraphQLDeleteSiteStructuredContentByExternalReferenceCode_addStructuredContent()
		throws Exception {

		return testGraphQLSiteStructuredContent_addStructuredContent();
	}

	@Test
	public void testDeleteStructuredContent() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent structuredContent =
			testDeleteStructuredContent_addStructuredContent();

		assertHttpResponseStatusCode(
			204,
			structuredContentResource.deleteStructuredContentHttpResponse(
				structuredContent.getId()));

		assertHttpResponseStatusCode(
			404,
			structuredContentResource.getStructuredContentHttpResponse(
				structuredContent.getId()));
		assertHttpResponseStatusCode(
			404,
			structuredContentResource.getStructuredContentHttpResponse(0L));
	}

	protected StructuredContent
			testDeleteStructuredContent_addStructuredContent()
		throws Exception {

		return structuredContentResource.postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	@Test
	public void testGraphQLDeleteStructuredContent() throws Exception {

		// No namespace

		StructuredContent structuredContent1 =
			testGraphQLDeleteStructuredContent_addStructuredContent();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteStructuredContent",
						new HashMap<String, Object>() {
							{
								put(
									"structuredContentId",
									structuredContent1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteStructuredContent"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"structuredContent",
					new HashMap<String, Object>() {
						{
							put(
								"structuredContentId",
								structuredContent1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		StructuredContent structuredContent2 =
			testGraphQLDeleteStructuredContent_addStructuredContent();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteStructuredContent",
							new HashMap<String, Object>() {
								{
									put(
										"structuredContentId",
										structuredContent2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteStructuredContent"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"structuredContent",
						new HashMap<String, Object>() {
							{
								put(
									"structuredContentId",
									structuredContent2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected StructuredContent
			testGraphQLDeleteStructuredContent_addStructuredContent()
		throws Exception {

		return testGraphQLStructuredContent_addStructuredContent();
	}

	@Test
	public void testDeleteStructuredContentBatch() throws Exception {
		StructuredContent structuredContent1 =
			testDeleteStructuredContentBatch_addStructuredContent();

		testDeleteStructuredContentBatch_deleteStructuredContent(
			202, null, structuredContent1.getId());

		assertHttpResponseStatusCode(
			404,
			structuredContentResource.getStructuredContentHttpResponse(
				structuredContent1.getId()));
	}

	protected StructuredContent
			testDeleteStructuredContentBatch_addStructuredContent()
		throws Exception {

		return testDeleteStructuredContent_addStructuredContent();
	}

	protected void testDeleteStructuredContentBatch_deleteStructuredContent(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			structuredContentResource.deleteStructuredContentBatchHttpResponse(
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
	public void testDeleteStructuredContentMyRating() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent structuredContent =
			testDeleteStructuredContentMyRating_addStructuredContent();

		assertHttpResponseStatusCode(
			204,
			structuredContentResource.
				deleteStructuredContentMyRatingHttpResponse(
					structuredContent.getId()));

		assertHttpResponseStatusCode(
			404,
			structuredContentResource.getStructuredContentMyRatingHttpResponse(
				structuredContent.getId()));
		assertHttpResponseStatusCode(
			404,
			structuredContentResource.getStructuredContentMyRatingHttpResponse(
				0L));
	}

	protected StructuredContent
			testDeleteStructuredContentMyRating_addStructuredContent()
		throws Exception {

		return structuredContentResource.postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	@Test
	public void testGraphQLDeleteStructuredContentMyRating() throws Exception {

		// No namespace

		StructuredContent structuredContent1 =
			testGraphQLDeleteStructuredContentMyRating_addStructuredContent();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteStructuredContentMyRating",
						new HashMap<String, Object>() {
							{
								put(
									"structuredContentId",
									structuredContent1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteStructuredContentMyRating"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"structuredContentMyRating",
					new HashMap<String, Object>() {
						{
							put(
								"structuredContentId",
								structuredContent1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		StructuredContent structuredContent2 =
			testGraphQLDeleteStructuredContentMyRating_addStructuredContent();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteStructuredContentMyRating",
							new HashMap<String, Object>() {
								{
									put(
										"structuredContentId",
										structuredContent2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteStructuredContentMyRating"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"structuredContentMyRating",
						new HashMap<String, Object>() {
							{
								put(
									"structuredContentId",
									structuredContent2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected StructuredContent
			testGraphQLDeleteStructuredContentMyRating_addStructuredContent()
		throws Exception {

		return testGraphQLStructuredContent_addStructuredContent();
	}

	@Test
	public void testGetAssetLibraryStructuredContentByExternalReferenceCode()
		throws Exception {

		StructuredContent postStructuredContent =
			testGetAssetLibraryStructuredContentByExternalReferenceCode_addStructuredContent();

		StructuredContent getStructuredContent =
			structuredContentResource.
				getAssetLibraryStructuredContentByExternalReferenceCode(
					testGetAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId(),
					postStructuredContent.getExternalReferenceCode());

		assertEquals(postStructuredContent, getStructuredContent);
		assertValid(getStructuredContent);
	}

	protected StructuredContent
			testGetAssetLibraryStructuredContentByExternalReferenceCode_addStructuredContent()
		throws Exception {

		return structuredContentResource.postAssetLibraryStructuredContent(
			testDepotEntry.getDepotEntryId(), randomStructuredContent());
	}

	protected Long
			testGetAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLGetAssetLibraryStructuredContentByExternalReferenceCode()
		throws Exception {

		StructuredContent structuredContent =
			testGraphQLGetAssetLibraryStructuredContentByExternalReferenceCode_addStructuredContent();

		// No namespace

		Assert.assertTrue(
			equals(
				structuredContent,
				StructuredContentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"assetLibraryStructuredContentByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"assetLibraryId",
											"\"" +
												testGraphQLGetAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId() +
													"\"");
										put(
											"externalReferenceCode",
											"\"" +
												structuredContent.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/assetLibraryStructuredContentByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				structuredContent,
				StructuredContentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"assetLibraryStructuredContentByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"assetLibraryId",
												"\"" +
													testGraphQLGetAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId() +
														"\"");
											put(
												"externalReferenceCode",
												"\"" +
													structuredContent.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/assetLibraryStructuredContentByExternalReferenceCode"))));
	}

	protected Long
			testGraphQLGetAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLGetAssetLibraryStructuredContentByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"assetLibraryStructuredContentByExternalReferenceCode",
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
							"assetLibraryStructuredContentByExternalReferenceCode",
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

	protected StructuredContent
			testGraphQLGetAssetLibraryStructuredContentByExternalReferenceCode_addStructuredContent()
		throws Exception {

		return testGraphQLAssetLibraryStructuredContent_addStructuredContent();
	}

	@Test
	public void testGetAssetLibraryStructuredContentPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent postStructuredContent =
			testGetAssetLibraryStructuredContentPermissionsPage_addStructuredContent();

		Page<Permission> page =
			structuredContentResource.
				getAssetLibraryStructuredContentPermissionsPage(
					testDepotEntry.getDepotEntryId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected StructuredContent
			testGetAssetLibraryStructuredContentPermissionsPage_addStructuredContent()
		throws Exception {

		return structuredContentResource.postAssetLibraryStructuredContent(
			testDepotEntry.getDepotEntryId(), randomStructuredContent());
	}

	@Test
	public void testGraphQLGetAssetLibraryStructuredContentPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent postStructuredContent =
			testGraphQLGetAssetLibraryStructuredContentPermissionsPage_addStructuredContent();

		GraphQLField graphQLField = new GraphQLField(
			"assetLibraryStructuredContentPermissions",
			new HashMap<String, Object>() {
				{
					put(
						"assetLibraryId",
						"\"" +
							testGraphQLGetAssetLibraryStructuredContentPermissionsPage_getAssetLibraryId() +
								"\"");
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject assetLibraryStructuredContentPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryStructuredContentPermissions");

		Assert.assertNotNull(
			assetLibraryStructuredContentPermissionsJSONObject);
	}

	protected Long
			testGraphQLGetAssetLibraryStructuredContentPermissionsPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected StructuredContent
			testGraphQLGetAssetLibraryStructuredContentPermissionsPage_addStructuredContent()
		throws Exception {

		return testGraphQLAssetLibraryStructuredContent_addStructuredContent();
	}

	@Test
	public void testGetAssetLibraryStructuredContentsPage() throws Exception {
		Long assetLibraryId =
			testGetAssetLibraryStructuredContentsPage_getAssetLibraryId();
		Long irrelevantAssetLibraryId =
			testGetAssetLibraryStructuredContentsPage_getIrrelevantAssetLibraryId();

		Page<StructuredContent> page =
			structuredContentResource.getAssetLibraryStructuredContentsPage(
				assetLibraryId, null, null, null, null, Pagination.of(1, 10),
				null);

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryId != null) {
			StructuredContent irrelevantStructuredContent =
				testGetAssetLibraryStructuredContentsPage_addStructuredContent(
					irrelevantAssetLibraryId,
					randomIrrelevantStructuredContent());

			page =
				structuredContentResource.getAssetLibraryStructuredContentsPage(
					irrelevantAssetLibraryId, null, null, null, null,
					Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantStructuredContent,
				(List<StructuredContent>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryStructuredContentsPage_getExpectedActions(
					irrelevantAssetLibraryId));
		}

		StructuredContent structuredContent1 =
			testGetAssetLibraryStructuredContentsPage_addStructuredContent(
				assetLibraryId, randomStructuredContent());

		StructuredContent structuredContent2 =
			testGetAssetLibraryStructuredContentsPage_addStructuredContent(
				assetLibraryId, randomStructuredContent());

		page = structuredContentResource.getAssetLibraryStructuredContentsPage(
			assetLibraryId, null, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			structuredContent1, (List<StructuredContent>)page.getItems());
		assertContains(
			structuredContent2, (List<StructuredContent>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryStructuredContentsPage_getExpectedActions(
				assetLibraryId));

		structuredContentResource.deleteStructuredContent(
			structuredContent1.getId());

		structuredContentResource.deleteStructuredContent(
			structuredContent2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryStructuredContentsPage_getExpectedActions(
				Long assetLibraryId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/asset-libraries/{assetLibraryId}/structured-contents/batch".
				replace("{assetLibraryId}", String.valueOf(assetLibraryId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryStructuredContentsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryStructuredContentsPage_getAssetLibraryId();

		StructuredContent structuredContent1 = randomStructuredContent();

		structuredContent1 =
			testGetAssetLibraryStructuredContentsPage_addStructuredContent(
				assetLibraryId, structuredContent1);

		for (EntityField entityField : entityFields) {
			Page<StructuredContent> page =
				structuredContentResource.getAssetLibraryStructuredContentsPage(
					assetLibraryId, null, null, null,
					getFilterString(entityField, "between", structuredContent1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(structuredContent1),
				(List<StructuredContent>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryStructuredContentsPageWithFilterDoubleEquals()
		throws Exception {

		testGetAssetLibraryStructuredContentsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAssetLibraryStructuredContentsPageWithFilterStringContains()
		throws Exception {

		testGetAssetLibraryStructuredContentsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryStructuredContentsPageWithFilterStringEquals()
		throws Exception {

		testGetAssetLibraryStructuredContentsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryStructuredContentsPageWithFilterStringStartsWith()
		throws Exception {

		testGetAssetLibraryStructuredContentsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetAssetLibraryStructuredContentsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryStructuredContentsPage_getAssetLibraryId();

		StructuredContent structuredContent1 =
			testGetAssetLibraryStructuredContentsPage_addStructuredContent(
				assetLibraryId, randomStructuredContent());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent structuredContent2 =
			testGetAssetLibraryStructuredContentsPage_addStructuredContent(
				assetLibraryId, randomStructuredContent());

		for (EntityField entityField : entityFields) {
			Page<StructuredContent> page =
				structuredContentResource.getAssetLibraryStructuredContentsPage(
					assetLibraryId, null, null, null,
					getFilterString(entityField, operator, structuredContent1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(structuredContent1),
				(List<StructuredContent>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryStructuredContentsPageWithPagination()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryStructuredContentsPage_getAssetLibraryId();

		Page<StructuredContent> structuredContentsPage =
			structuredContentResource.getAssetLibraryStructuredContentsPage(
				assetLibraryId, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			structuredContentsPage.getTotalCount());

		StructuredContent structuredContent1 =
			testGetAssetLibraryStructuredContentsPage_addStructuredContent(
				assetLibraryId, randomStructuredContent());

		StructuredContent structuredContent2 =
			testGetAssetLibraryStructuredContentsPage_addStructuredContent(
				assetLibraryId, randomStructuredContent());

		StructuredContent structuredContent3 =
			testGetAssetLibraryStructuredContentsPage_addStructuredContent(
				assetLibraryId, randomStructuredContent());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<StructuredContent> page1 =
				structuredContentResource.getAssetLibraryStructuredContentsPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				structuredContent1, (List<StructuredContent>)page1.getItems());

			Page<StructuredContent> page2 =
				structuredContentResource.getAssetLibraryStructuredContentsPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				structuredContent2, (List<StructuredContent>)page2.getItems());

			Page<StructuredContent> page3 =
				structuredContentResource.getAssetLibraryStructuredContentsPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				structuredContent3, (List<StructuredContent>)page3.getItems());
		}
		else {
			Page<StructuredContent> page1 =
				structuredContentResource.getAssetLibraryStructuredContentsPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<StructuredContent> structuredContents1 =
				(List<StructuredContent>)page1.getItems();

			Assert.assertEquals(
				structuredContents1.toString(), totalCount + 2,
				structuredContents1.size());

			Page<StructuredContent> page2 =
				structuredContentResource.getAssetLibraryStructuredContentsPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<StructuredContent> structuredContents2 =
				(List<StructuredContent>)page2.getItems();

			Assert.assertEquals(
				structuredContents2.toString(), 1, structuredContents2.size());

			Page<StructuredContent> page3 =
				structuredContentResource.getAssetLibraryStructuredContentsPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				structuredContent1, (List<StructuredContent>)page3.getItems());
			assertContains(
				structuredContent2, (List<StructuredContent>)page3.getItems());
			assertContains(
				structuredContent3, (List<StructuredContent>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryStructuredContentsPageWithSortDateTime()
		throws Exception {

		testGetAssetLibraryStructuredContentsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, structuredContent1, structuredContent2) -> {
				BeanTestUtil.setProperty(
					structuredContent1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetLibraryStructuredContentsPageWithSortDouble()
		throws Exception {

		testGetAssetLibraryStructuredContentsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, structuredContent1, structuredContent2) -> {
				BeanTestUtil.setProperty(
					structuredContent1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					structuredContent2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetLibraryStructuredContentsPageWithSortInteger()
		throws Exception {

		testGetAssetLibraryStructuredContentsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, structuredContent1, structuredContent2) -> {
				BeanTestUtil.setProperty(
					structuredContent1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					structuredContent2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetLibraryStructuredContentsPageWithSortString()
		throws Exception {

		testGetAssetLibraryStructuredContentsPageWithSort(
			EntityField.Type.STRING,
			(entityField, structuredContent1, structuredContent2) -> {
				Class<?> clazz = structuredContent1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						structuredContent1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						structuredContent2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						structuredContent1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						structuredContent2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						structuredContent1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						structuredContent2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAssetLibraryStructuredContentsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, StructuredContent, StructuredContent, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryStructuredContentsPage_getAssetLibraryId();

		StructuredContent structuredContent1 = randomStructuredContent();
		StructuredContent structuredContent2 = randomStructuredContent();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, structuredContent1, structuredContent2);
		}

		structuredContent1 =
			testGetAssetLibraryStructuredContentsPage_addStructuredContent(
				assetLibraryId, structuredContent1);

		structuredContent2 =
			testGetAssetLibraryStructuredContentsPage_addStructuredContent(
				assetLibraryId, structuredContent2);

		Page<StructuredContent> page =
			structuredContentResource.getAssetLibraryStructuredContentsPage(
				assetLibraryId, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<StructuredContent> ascPage =
				structuredContentResource.getAssetLibraryStructuredContentsPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				structuredContent1,
				(List<StructuredContent>)ascPage.getItems());
			assertContains(
				structuredContent2,
				(List<StructuredContent>)ascPage.getItems());

			Page<StructuredContent> descPage =
				structuredContentResource.getAssetLibraryStructuredContentsPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				structuredContent2,
				(List<StructuredContent>)descPage.getItems());
			assertContains(
				structuredContent1,
				(List<StructuredContent>)descPage.getItems());
		}
	}

	protected StructuredContent
			testGetAssetLibraryStructuredContentsPage_addStructuredContent(
				Long assetLibraryId, StructuredContent structuredContent)
		throws Exception {

		return structuredContentResource.postAssetLibraryStructuredContent(
			assetLibraryId, structuredContent);
	}

	protected Long testGetAssetLibraryStructuredContentsPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Long
			testGetAssetLibraryStructuredContentsPage_getIrrelevantAssetLibraryId()
		throws Exception {

		return irrelevantDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLGetAssetLibraryStructuredContentsPage()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryStructuredContentsPage_getAssetLibraryId();

		GraphQLField graphQLField = new GraphQLField(
			"assetLibraryStructuredContents",
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

		JSONObject assetLibraryStructuredContentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryStructuredContents");

		long totalCount = assetLibraryStructuredContentsJSONObject.getLong(
			"totalCount");

		StructuredContent structuredContent1 =
			testGraphQLAssetLibraryStructuredContent_addStructuredContent(
				assetLibraryId, randomStructuredContent());

		StructuredContent structuredContent2 =
			testGraphQLAssetLibraryStructuredContent_addStructuredContent(
				assetLibraryId, randomStructuredContent());

		assetLibraryStructuredContentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryStructuredContents");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryStructuredContentsJSONObject.getLong("totalCount"));

		assertContains(
			structuredContent1,
			Arrays.asList(
				StructuredContentSerDes.toDTOs(
					assetLibraryStructuredContentsJSONObject.getString(
						"items"))));
		assertContains(
			structuredContent2,
			Arrays.asList(
				StructuredContentSerDes.toDTOs(
					assetLibraryStructuredContentsJSONObject.getString(
						"items"))));

		// Using the namespace headlessDelivery_v1_0

		assetLibraryStructuredContentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("headlessDelivery_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"JSONObject/assetLibraryStructuredContents");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryStructuredContentsJSONObject.getLong("totalCount"));

		assertContains(
			structuredContent1,
			Arrays.asList(
				StructuredContentSerDes.toDTOs(
					assetLibraryStructuredContentsJSONObject.getString(
						"items"))));
		assertContains(
			structuredContent2,
			Arrays.asList(
				StructuredContentSerDes.toDTOs(
					assetLibraryStructuredContentsJSONObject.getString(
						"items"))));
	}

	@Test
	public void testGetContentStructureStructuredContentsPage()
		throws Exception {

		Long contentStructureId =
			testGetContentStructureStructuredContentsPage_getContentStructureId();
		Long irrelevantContentStructureId =
			testGetContentStructureStructuredContentsPage_getIrrelevantContentStructureId();

		Page<StructuredContent> page =
			structuredContentResource.getContentStructureStructuredContentsPage(
				contentStructureId, null, null, null, Pagination.of(1, 10),
				null);

		long totalCount = page.getTotalCount();

		if (irrelevantContentStructureId != null) {
			StructuredContent irrelevantStructuredContent =
				testGetContentStructureStructuredContentsPage_addStructuredContent(
					irrelevantContentStructureId,
					randomIrrelevantStructuredContent());

			page =
				structuredContentResource.
					getContentStructureStructuredContentsPage(
						irrelevantContentStructureId, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantStructuredContent,
				(List<StructuredContent>)page.getItems());
			assertValid(
				page,
				testGetContentStructureStructuredContentsPage_getExpectedActions(
					irrelevantContentStructureId));
		}

		StructuredContent structuredContent1 =
			testGetContentStructureStructuredContentsPage_addStructuredContent(
				contentStructureId, randomStructuredContent());

		StructuredContent structuredContent2 =
			testGetContentStructureStructuredContentsPage_addStructuredContent(
				contentStructureId, randomStructuredContent());

		page =
			structuredContentResource.getContentStructureStructuredContentsPage(
				contentStructureId, null, null, null, Pagination.of(1, 10),
				null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			structuredContent1, (List<StructuredContent>)page.getItems());
		assertContains(
			structuredContent2, (List<StructuredContent>)page.getItems());
		assertValid(
			page,
			testGetContentStructureStructuredContentsPage_getExpectedActions(
				contentStructureId));

		structuredContentResource.deleteStructuredContent(
			structuredContent1.getId());

		structuredContentResource.deleteStructuredContent(
			structuredContent2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetContentStructureStructuredContentsPage_getExpectedActions(
				Long contentStructureId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetContentStructureStructuredContentsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long contentStructureId =
			testGetContentStructureStructuredContentsPage_getContentStructureId();

		StructuredContent structuredContent1 = randomStructuredContent();

		structuredContent1 =
			testGetContentStructureStructuredContentsPage_addStructuredContent(
				contentStructureId, structuredContent1);

		for (EntityField entityField : entityFields) {
			Page<StructuredContent> page =
				structuredContentResource.
					getContentStructureStructuredContentsPage(
						contentStructureId, null, null,
						getFilterString(
							entityField, "between", structuredContent1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(structuredContent1),
				(List<StructuredContent>)page.getItems());
		}
	}

	@Test
	public void testGetContentStructureStructuredContentsPageWithFilterDoubleEquals()
		throws Exception {

		testGetContentStructureStructuredContentsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetContentStructureStructuredContentsPageWithFilterStringContains()
		throws Exception {

		testGetContentStructureStructuredContentsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetContentStructureStructuredContentsPageWithFilterStringEquals()
		throws Exception {

		testGetContentStructureStructuredContentsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetContentStructureStructuredContentsPageWithFilterStringStartsWith()
		throws Exception {

		testGetContentStructureStructuredContentsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetContentStructureStructuredContentsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long contentStructureId =
			testGetContentStructureStructuredContentsPage_getContentStructureId();

		StructuredContent structuredContent1 =
			testGetContentStructureStructuredContentsPage_addStructuredContent(
				contentStructureId, randomStructuredContent());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent structuredContent2 =
			testGetContentStructureStructuredContentsPage_addStructuredContent(
				contentStructureId, randomStructuredContent());

		for (EntityField entityField : entityFields) {
			Page<StructuredContent> page =
				structuredContentResource.
					getContentStructureStructuredContentsPage(
						contentStructureId, null, null,
						getFilterString(
							entityField, operator, structuredContent1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(structuredContent1),
				(List<StructuredContent>)page.getItems());
		}
	}

	@Test
	public void testGetContentStructureStructuredContentsPageWithPagination()
		throws Exception {

		Long contentStructureId =
			testGetContentStructureStructuredContentsPage_getContentStructureId();

		Page<StructuredContent> structuredContentsPage =
			structuredContentResource.getContentStructureStructuredContentsPage(
				contentStructureId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			structuredContentsPage.getTotalCount());

		StructuredContent structuredContent1 =
			testGetContentStructureStructuredContentsPage_addStructuredContent(
				contentStructureId, randomStructuredContent());

		StructuredContent structuredContent2 =
			testGetContentStructureStructuredContentsPage_addStructuredContent(
				contentStructureId, randomStructuredContent());

		StructuredContent structuredContent3 =
			testGetContentStructureStructuredContentsPage_addStructuredContent(
				contentStructureId, randomStructuredContent());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<StructuredContent> page1 =
				structuredContentResource.
					getContentStructureStructuredContentsPage(
						contentStructureId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				structuredContent1, (List<StructuredContent>)page1.getItems());

			Page<StructuredContent> page2 =
				structuredContentResource.
					getContentStructureStructuredContentsPage(
						contentStructureId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				structuredContent2, (List<StructuredContent>)page2.getItems());

			Page<StructuredContent> page3 =
				structuredContentResource.
					getContentStructureStructuredContentsPage(
						contentStructureId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				structuredContent3, (List<StructuredContent>)page3.getItems());
		}
		else {
			Page<StructuredContent> page1 =
				structuredContentResource.
					getContentStructureStructuredContentsPage(
						contentStructureId, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<StructuredContent> structuredContents1 =
				(List<StructuredContent>)page1.getItems();

			Assert.assertEquals(
				structuredContents1.toString(), totalCount + 2,
				structuredContents1.size());

			Page<StructuredContent> page2 =
				structuredContentResource.
					getContentStructureStructuredContentsPage(
						contentStructureId, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<StructuredContent> structuredContents2 =
				(List<StructuredContent>)page2.getItems();

			Assert.assertEquals(
				structuredContents2.toString(), 1, structuredContents2.size());

			Page<StructuredContent> page3 =
				structuredContentResource.
					getContentStructureStructuredContentsPage(
						contentStructureId, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				structuredContent1, (List<StructuredContent>)page3.getItems());
			assertContains(
				structuredContent2, (List<StructuredContent>)page3.getItems());
			assertContains(
				structuredContent3, (List<StructuredContent>)page3.getItems());
		}
	}

	@Test
	public void testGetContentStructureStructuredContentsPageWithSortDateTime()
		throws Exception {

		testGetContentStructureStructuredContentsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, structuredContent1, structuredContent2) -> {
				BeanTestUtil.setProperty(
					structuredContent1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetContentStructureStructuredContentsPageWithSortDouble()
		throws Exception {

		testGetContentStructureStructuredContentsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, structuredContent1, structuredContent2) -> {
				BeanTestUtil.setProperty(
					structuredContent1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					structuredContent2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetContentStructureStructuredContentsPageWithSortInteger()
		throws Exception {

		testGetContentStructureStructuredContentsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, structuredContent1, structuredContent2) -> {
				BeanTestUtil.setProperty(
					structuredContent1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					structuredContent2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetContentStructureStructuredContentsPageWithSortString()
		throws Exception {

		testGetContentStructureStructuredContentsPageWithSort(
			EntityField.Type.STRING,
			(entityField, structuredContent1, structuredContent2) -> {
				Class<?> clazz = structuredContent1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						structuredContent1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						structuredContent2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						structuredContent1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						structuredContent2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						structuredContent1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						structuredContent2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetContentStructureStructuredContentsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, StructuredContent, StructuredContent, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long contentStructureId =
			testGetContentStructureStructuredContentsPage_getContentStructureId();

		StructuredContent structuredContent1 = randomStructuredContent();
		StructuredContent structuredContent2 = randomStructuredContent();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, structuredContent1, structuredContent2);
		}

		structuredContent1 =
			testGetContentStructureStructuredContentsPage_addStructuredContent(
				contentStructureId, structuredContent1);

		structuredContent2 =
			testGetContentStructureStructuredContentsPage_addStructuredContent(
				contentStructureId, structuredContent2);

		Page<StructuredContent> page =
			structuredContentResource.getContentStructureStructuredContentsPage(
				contentStructureId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<StructuredContent> ascPage =
				structuredContentResource.
					getContentStructureStructuredContentsPage(
						contentStructureId, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				structuredContent1,
				(List<StructuredContent>)ascPage.getItems());
			assertContains(
				structuredContent2,
				(List<StructuredContent>)ascPage.getItems());

			Page<StructuredContent> descPage =
				structuredContentResource.
					getContentStructureStructuredContentsPage(
						contentStructureId, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				structuredContent2,
				(List<StructuredContent>)descPage.getItems());
			assertContains(
				structuredContent1,
				(List<StructuredContent>)descPage.getItems());
		}
	}

	protected StructuredContent
			testGetContentStructureStructuredContentsPage_addStructuredContent(
				Long contentStructureId, StructuredContent structuredContent)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetContentStructureStructuredContentsPage_getContentStructureId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetContentStructureStructuredContentsPage_getIrrelevantContentStructureId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetSiteStructuredContentByExternalReferenceCode()
		throws Exception {

		StructuredContent postStructuredContent =
			testGetSiteStructuredContentByExternalReferenceCode_addStructuredContent();

		StructuredContent getStructuredContent =
			structuredContentResource.
				getSiteStructuredContentByExternalReferenceCode(
					postStructuredContent.getSiteId(),
					postStructuredContent.getExternalReferenceCode());

		assertEquals(postStructuredContent, getStructuredContent);
		assertValid(getStructuredContent);
	}

	protected StructuredContent
			testGetSiteStructuredContentByExternalReferenceCode_addStructuredContent()
		throws Exception {

		return structuredContentResource.postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	@Test
	public void testGraphQLGetSiteStructuredContentByExternalReferenceCode()
		throws Exception {

		StructuredContent structuredContent =
			testGraphQLGetSiteStructuredContentByExternalReferenceCode_addStructuredContent();

		// No namespace

		Assert.assertTrue(
			equals(
				structuredContent,
				StructuredContentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"structuredContentByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												structuredContent.getSiteId() +
													"\"");
										put(
											"externalReferenceCode",
											"\"" +
												structuredContent.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/structuredContentByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				structuredContent,
				StructuredContentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"structuredContentByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													structuredContent.
														getSiteId() + "\"");
											put(
												"externalReferenceCode",
												"\"" +
													structuredContent.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/structuredContentByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSiteStructuredContentByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"structuredContentByExternalReferenceCode",
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
							"structuredContentByExternalReferenceCode",
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

	protected StructuredContent
			testGraphQLGetSiteStructuredContentByExternalReferenceCode_addStructuredContent()
		throws Exception {

		return testGraphQLSiteStructuredContent_addStructuredContent();
	}

	@Test
	public void testGetSiteStructuredContentByKey() throws Exception {
		StructuredContent postStructuredContent =
			testGetSiteStructuredContentByKey_addStructuredContent();

		StructuredContent getStructuredContent =
			structuredContentResource.getSiteStructuredContentByKey(
				postStructuredContent.getSiteId(),
				postStructuredContent.getKey());

		assertEquals(postStructuredContent, getStructuredContent);
		assertValid(getStructuredContent);
	}

	protected StructuredContent
			testGetSiteStructuredContentByKey_addStructuredContent()
		throws Exception {

		return structuredContentResource.postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	@Test
	public void testGraphQLGetSiteStructuredContentByKey() throws Exception {
		StructuredContent structuredContent =
			testGraphQLGetSiteStructuredContentByKey_addStructuredContent();

		// No namespace

		Assert.assertTrue(
			equals(
				structuredContent,
				StructuredContentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"structuredContentByKey",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												structuredContent.getSiteId() +
													"\"");
										put(
											"key",
											"\"" + structuredContent.getKey() +
												"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/structuredContentByKey"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				structuredContent,
				StructuredContentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"structuredContentByKey",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													structuredContent.
														getSiteId() + "\"");
											put(
												"key",
												"\"" +
													structuredContent.getKey() +
														"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/structuredContentByKey"))));
	}

	@Test
	public void testGraphQLGetSiteStructuredContentByKeyNotFound()
		throws Exception {

		String irrelevantKey = "\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"structuredContentByKey",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put("key", irrelevantKey);
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
							"structuredContentByKey",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put("key", irrelevantKey);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected StructuredContent
			testGraphQLGetSiteStructuredContentByKey_addStructuredContent()
		throws Exception {

		return testGraphQLSiteStructuredContent_addStructuredContent();
	}

	@Test
	public void testGetSiteStructuredContentByUuid() throws Exception {
		StructuredContent postStructuredContent =
			testGetSiteStructuredContentByUuid_addStructuredContent();

		StructuredContent getStructuredContent =
			structuredContentResource.getSiteStructuredContentByUuid(
				postStructuredContent.getSiteId(),
				postStructuredContent.getUuid());

		assertEquals(postStructuredContent, getStructuredContent);
		assertValid(getStructuredContent);
	}

	protected StructuredContent
			testGetSiteStructuredContentByUuid_addStructuredContent()
		throws Exception {

		return structuredContentResource.postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	@Test
	public void testGraphQLGetSiteStructuredContentByUuid() throws Exception {
		StructuredContent structuredContent =
			testGraphQLGetSiteStructuredContentByUuid_addStructuredContent();

		// No namespace

		Assert.assertTrue(
			equals(
				structuredContent,
				StructuredContentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"structuredContentByUuid",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												structuredContent.getSiteId() +
													"\"");
										put(
											"uuid",
											"\"" + structuredContent.getUuid() +
												"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/structuredContentByUuid"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				structuredContent,
				StructuredContentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"structuredContentByUuid",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													structuredContent.
														getSiteId() + "\"");
											put(
												"uuid",
												"\"" +
													structuredContent.
														getUuid() + "\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/structuredContentByUuid"))));
	}

	@Test
	public void testGraphQLGetSiteStructuredContentByUuidNotFound()
		throws Exception {

		String irrelevantUuid = "\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"structuredContentByUuid",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put("uuid", irrelevantUuid);
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
							"structuredContentByUuid",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put("uuid", irrelevantUuid);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected StructuredContent
			testGraphQLGetSiteStructuredContentByUuid_addStructuredContent()
		throws Exception {

		return testGraphQLSiteStructuredContent_addStructuredContent();
	}

	@Test
	public void testGetSiteStructuredContentPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent postStructuredContent =
			testGetSiteStructuredContentPermissionsPage_addStructuredContent();

		Page<Permission> page =
			structuredContentResource.getSiteStructuredContentPermissionsPage(
				testGroup.getGroupId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected StructuredContent
			testGetSiteStructuredContentPermissionsPage_addStructuredContent()
		throws Exception {

		return structuredContentResource.postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	@Test
	public void testGraphQLGetSiteStructuredContentPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent postStructuredContent =
			testGraphQLGetSiteStructuredContentPermissionsPage_addStructuredContent();

		GraphQLField graphQLField = new GraphQLField(
			"siteStructuredContentPermissions",
			new HashMap<String, Object>() {
				{
					put(
						"siteKey",
						"\"" + postStructuredContent.getSiteId() + "\"");
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject siteStructuredContentPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/siteStructuredContentPermissions");

		Assert.assertNotNull(siteStructuredContentPermissionsJSONObject);
	}

	protected StructuredContent
			testGraphQLGetSiteStructuredContentPermissionsPage_addStructuredContent()
		throws Exception {

		return testGraphQLSiteStructuredContent_addStructuredContent();
	}

	@Test
	public void testGetSiteStructuredContentsPage() throws Exception {
		Long siteId = testGetSiteStructuredContentsPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteStructuredContentsPage_getIrrelevantSiteId();

		Page<StructuredContent> page =
			structuredContentResource.getSiteStructuredContentsPage(
				siteId, null, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			StructuredContent irrelevantStructuredContent =
				testGetSiteStructuredContentsPage_addStructuredContent(
					irrelevantSiteId, randomIrrelevantStructuredContent());

			page = structuredContentResource.getSiteStructuredContentsPage(
				irrelevantSiteId, null, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantStructuredContent,
				(List<StructuredContent>)page.getItems());
			assertValid(
				page,
				testGetSiteStructuredContentsPage_getExpectedActions(
					irrelevantSiteId));
		}

		StructuredContent structuredContent1 =
			testGetSiteStructuredContentsPage_addStructuredContent(
				siteId, randomStructuredContent());

		StructuredContent structuredContent2 =
			testGetSiteStructuredContentsPage_addStructuredContent(
				siteId, randomStructuredContent());

		page = structuredContentResource.getSiteStructuredContentsPage(
			siteId, null, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			structuredContent1, (List<StructuredContent>)page.getItems());
		assertContains(
			structuredContent2, (List<StructuredContent>)page.getItems());
		assertValid(
			page, testGetSiteStructuredContentsPage_getExpectedActions(siteId));

		structuredContentResource.deleteStructuredContent(
			structuredContent1.getId());

		structuredContentResource.deleteStructuredContent(
			structuredContent2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteStructuredContentsPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/sites/{siteId}/structured-contents/batch".
				replace("{siteId}", String.valueOf(siteId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteStructuredContentsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteStructuredContentsPage_getSiteId();

		StructuredContent structuredContent1 = randomStructuredContent();

		structuredContent1 =
			testGetSiteStructuredContentsPage_addStructuredContent(
				siteId, structuredContent1);

		for (EntityField entityField : entityFields) {
			Page<StructuredContent> page =
				structuredContentResource.getSiteStructuredContentsPage(
					siteId, null, null, null,
					getFilterString(entityField, "between", structuredContent1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(structuredContent1),
				(List<StructuredContent>)page.getItems());
		}
	}

	@Test
	public void testGetSiteStructuredContentsPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteStructuredContentsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteStructuredContentsPageWithFilterStringContains()
		throws Exception {

		testGetSiteStructuredContentsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteStructuredContentsPageWithFilterStringEquals()
		throws Exception {

		testGetSiteStructuredContentsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteStructuredContentsPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteStructuredContentsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteStructuredContentsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteStructuredContentsPage_getSiteId();

		StructuredContent structuredContent1 =
			testGetSiteStructuredContentsPage_addStructuredContent(
				siteId, randomStructuredContent());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent structuredContent2 =
			testGetSiteStructuredContentsPage_addStructuredContent(
				siteId, randomStructuredContent());

		for (EntityField entityField : entityFields) {
			Page<StructuredContent> page =
				structuredContentResource.getSiteStructuredContentsPage(
					siteId, null, null, null,
					getFilterString(entityField, operator, structuredContent1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(structuredContent1),
				(List<StructuredContent>)page.getItems());
		}
	}

	@Test
	public void testGetSiteStructuredContentsPageWithPagination()
		throws Exception {

		Long siteId = testGetSiteStructuredContentsPage_getSiteId();

		Page<StructuredContent> structuredContentsPage =
			structuredContentResource.getSiteStructuredContentsPage(
				siteId, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			structuredContentsPage.getTotalCount());

		StructuredContent structuredContent1 =
			testGetSiteStructuredContentsPage_addStructuredContent(
				siteId, randomStructuredContent());

		StructuredContent structuredContent2 =
			testGetSiteStructuredContentsPage_addStructuredContent(
				siteId, randomStructuredContent());

		StructuredContent structuredContent3 =
			testGetSiteStructuredContentsPage_addStructuredContent(
				siteId, randomStructuredContent());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<StructuredContent> page1 =
				structuredContentResource.getSiteStructuredContentsPage(
					siteId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				structuredContent1, (List<StructuredContent>)page1.getItems());

			Page<StructuredContent> page2 =
				structuredContentResource.getSiteStructuredContentsPage(
					siteId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				structuredContent2, (List<StructuredContent>)page2.getItems());

			Page<StructuredContent> page3 =
				structuredContentResource.getSiteStructuredContentsPage(
					siteId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				structuredContent3, (List<StructuredContent>)page3.getItems());
		}
		else {
			Page<StructuredContent> page1 =
				structuredContentResource.getSiteStructuredContentsPage(
					siteId, null, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<StructuredContent> structuredContents1 =
				(List<StructuredContent>)page1.getItems();

			Assert.assertEquals(
				structuredContents1.toString(), totalCount + 2,
				structuredContents1.size());

			Page<StructuredContent> page2 =
				structuredContentResource.getSiteStructuredContentsPage(
					siteId, null, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<StructuredContent> structuredContents2 =
				(List<StructuredContent>)page2.getItems();

			Assert.assertEquals(
				structuredContents2.toString(), 1, structuredContents2.size());

			Page<StructuredContent> page3 =
				structuredContentResource.getSiteStructuredContentsPage(
					siteId, null, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				structuredContent1, (List<StructuredContent>)page3.getItems());
			assertContains(
				structuredContent2, (List<StructuredContent>)page3.getItems());
			assertContains(
				structuredContent3, (List<StructuredContent>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteStructuredContentsPageWithSortDateTime()
		throws Exception {

		testGetSiteStructuredContentsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, structuredContent1, structuredContent2) -> {
				BeanTestUtil.setProperty(
					structuredContent1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteStructuredContentsPageWithSortDouble()
		throws Exception {

		testGetSiteStructuredContentsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, structuredContent1, structuredContent2) -> {
				BeanTestUtil.setProperty(
					structuredContent1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					structuredContent2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteStructuredContentsPageWithSortInteger()
		throws Exception {

		testGetSiteStructuredContentsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, structuredContent1, structuredContent2) -> {
				BeanTestUtil.setProperty(
					structuredContent1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					structuredContent2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteStructuredContentsPageWithSortString()
		throws Exception {

		testGetSiteStructuredContentsPageWithSort(
			EntityField.Type.STRING,
			(entityField, structuredContent1, structuredContent2) -> {
				Class<?> clazz = structuredContent1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						structuredContent1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						structuredContent2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						structuredContent1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						structuredContent2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						structuredContent1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						structuredContent2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteStructuredContentsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, StructuredContent, StructuredContent, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteStructuredContentsPage_getSiteId();

		StructuredContent structuredContent1 = randomStructuredContent();
		StructuredContent structuredContent2 = randomStructuredContent();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, structuredContent1, structuredContent2);
		}

		structuredContent1 =
			testGetSiteStructuredContentsPage_addStructuredContent(
				siteId, structuredContent1);

		structuredContent2 =
			testGetSiteStructuredContentsPage_addStructuredContent(
				siteId, structuredContent2);

		Page<StructuredContent> page =
			structuredContentResource.getSiteStructuredContentsPage(
				siteId, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<StructuredContent> ascPage =
				structuredContentResource.getSiteStructuredContentsPage(
					siteId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				structuredContent1,
				(List<StructuredContent>)ascPage.getItems());
			assertContains(
				structuredContent2,
				(List<StructuredContent>)ascPage.getItems());

			Page<StructuredContent> descPage =
				structuredContentResource.getSiteStructuredContentsPage(
					siteId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				structuredContent2,
				(List<StructuredContent>)descPage.getItems());
			assertContains(
				structuredContent1,
				(List<StructuredContent>)descPage.getItems());
		}
	}

	protected StructuredContent
			testGetSiteStructuredContentsPage_addStructuredContent(
				Long siteId, StructuredContent structuredContent)
		throws Exception {

		return structuredContentResource.postSiteStructuredContent(
			siteId, structuredContent);
	}

	protected Long testGetSiteStructuredContentsPage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long testGetSiteStructuredContentsPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteStructuredContentsPage() throws Exception {
		Long siteId = testGetSiteStructuredContentsPage_getSiteId();

		GraphQLField graphQLField = new GraphQLField(
			"structuredContents",
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

		JSONObject structuredContentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/structuredContents");

		long totalCount = structuredContentsJSONObject.getLong("totalCount");

		StructuredContent structuredContent1 =
			testGraphQLSiteStructuredContent_addStructuredContent(
				siteId, randomStructuredContent());

		StructuredContent structuredContent2 =
			testGraphQLSiteStructuredContent_addStructuredContent(
				siteId, randomStructuredContent());

		structuredContentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/structuredContents");

		Assert.assertEquals(
			totalCount + 2, structuredContentsJSONObject.getLong("totalCount"));

		assertContains(
			structuredContent1,
			Arrays.asList(
				StructuredContentSerDes.toDTOs(
					structuredContentsJSONObject.getString("items"))));
		assertContains(
			structuredContent2,
			Arrays.asList(
				StructuredContentSerDes.toDTOs(
					structuredContentsJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		structuredContentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/structuredContents");

		Assert.assertEquals(
			totalCount + 2, structuredContentsJSONObject.getLong("totalCount"));

		assertContains(
			structuredContent1,
			Arrays.asList(
				StructuredContentSerDes.toDTOs(
					structuredContentsJSONObject.getString("items"))));
		assertContains(
			structuredContent2,
			Arrays.asList(
				StructuredContentSerDes.toDTOs(
					structuredContentsJSONObject.getString("items"))));
	}

	@Test
	public void testGetStructuredContent() throws Exception {
		StructuredContent postStructuredContent =
			testGetStructuredContent_addStructuredContent();

		StructuredContent getStructuredContent =
			structuredContentResource.getStructuredContent(
				postStructuredContent.getId());

		assertEquals(postStructuredContent, getStructuredContent);
		assertValid(getStructuredContent);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		StructuredContent postStructuredContent =
			testGetStructuredContent_addStructuredContent();

		StructuredContent getStructuredContent =
			structuredContentResource.getStructuredContent(
				postStructuredContent.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.delivery.dto.v1_0.StructuredContent"
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
			postStructuredContent.getId());

		assertEquals(
			getStructuredContent,
			StructuredContentSerDes.toDTO(item.toString()));
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

	protected StructuredContent testGetStructuredContent_addStructuredContent()
		throws Exception {

		return structuredContentResource.postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	@Test
	public void testGraphQLGetStructuredContent() throws Exception {
		StructuredContent structuredContent =
			testGraphQLGetStructuredContent_addStructuredContent();

		// No namespace

		Assert.assertTrue(
			equals(
				structuredContent,
				StructuredContentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"structuredContent",
								new HashMap<String, Object>() {
									{
										put(
											"structuredContentId",
											structuredContent.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/structuredContent"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				structuredContent,
				StructuredContentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"structuredContent",
									new HashMap<String, Object>() {
										{
											put(
												"structuredContentId",
												structuredContent.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/structuredContent"))));
	}

	@Test
	public void testGraphQLGetStructuredContentNotFound() throws Exception {
		Long irrelevantStructuredContentId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"structuredContent",
						new HashMap<String, Object>() {
							{
								put(
									"structuredContentId",
									irrelevantStructuredContentId);
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
							"structuredContent",
							new HashMap<String, Object>() {
								{
									put(
										"structuredContentId",
										irrelevantStructuredContentId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected StructuredContent
			testGraphQLGetStructuredContent_addStructuredContent()
		throws Exception {

		return testGraphQLStructuredContent_addStructuredContent();
	}

	@Test
	public void testGetStructuredContentFolderStructuredContentsPage()
		throws Exception {

		Long structuredContentFolderId =
			testGetStructuredContentFolderStructuredContentsPage_getStructuredContentFolderId();
		Long irrelevantStructuredContentFolderId =
			testGetStructuredContentFolderStructuredContentsPage_getIrrelevantStructuredContentFolderId();

		Page<StructuredContent> page =
			structuredContentResource.
				getStructuredContentFolderStructuredContentsPage(
					structuredContentFolderId, null, null, null, null,
					Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantStructuredContentFolderId != null) {
			StructuredContent irrelevantStructuredContent =
				testGetStructuredContentFolderStructuredContentsPage_addStructuredContent(
					irrelevantStructuredContentFolderId,
					randomIrrelevantStructuredContent());

			page =
				structuredContentResource.
					getStructuredContentFolderStructuredContentsPage(
						irrelevantStructuredContentFolderId, null, null, null,
						null, Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantStructuredContent,
				(List<StructuredContent>)page.getItems());
			assertValid(
				page,
				testGetStructuredContentFolderStructuredContentsPage_getExpectedActions(
					irrelevantStructuredContentFolderId));
		}

		StructuredContent structuredContent1 =
			testGetStructuredContentFolderStructuredContentsPage_addStructuredContent(
				structuredContentFolderId, randomStructuredContent());

		StructuredContent structuredContent2 =
			testGetStructuredContentFolderStructuredContentsPage_addStructuredContent(
				structuredContentFolderId, randomStructuredContent());

		page =
			structuredContentResource.
				getStructuredContentFolderStructuredContentsPage(
					structuredContentFolderId, null, null, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			structuredContent1, (List<StructuredContent>)page.getItems());
		assertContains(
			structuredContent2, (List<StructuredContent>)page.getItems());
		assertValid(
			page,
			testGetStructuredContentFolderStructuredContentsPage_getExpectedActions(
				structuredContentFolderId));

		structuredContentResource.deleteStructuredContent(
			structuredContent1.getId());

		structuredContentResource.deleteStructuredContent(
			structuredContent2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetStructuredContentFolderStructuredContentsPage_getExpectedActions(
				Long structuredContentFolderId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/structured-content-folders/{structuredContentFolderId}/structured-contents/batch".
				replace(
					"{structuredContentFolderId}",
					String.valueOf(structuredContentFolderId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetStructuredContentFolderStructuredContentsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long structuredContentFolderId =
			testGetStructuredContentFolderStructuredContentsPage_getStructuredContentFolderId();

		StructuredContent structuredContent1 = randomStructuredContent();

		structuredContent1 =
			testGetStructuredContentFolderStructuredContentsPage_addStructuredContent(
				structuredContentFolderId, structuredContent1);

		for (EntityField entityField : entityFields) {
			Page<StructuredContent> page =
				structuredContentResource.
					getStructuredContentFolderStructuredContentsPage(
						structuredContentFolderId, null, null, null,
						getFilterString(
							entityField, "between", structuredContent1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(structuredContent1),
				(List<StructuredContent>)page.getItems());
		}
	}

	@Test
	public void testGetStructuredContentFolderStructuredContentsPageWithFilterDoubleEquals()
		throws Exception {

		testGetStructuredContentFolderStructuredContentsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetStructuredContentFolderStructuredContentsPageWithFilterStringContains()
		throws Exception {

		testGetStructuredContentFolderStructuredContentsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetStructuredContentFolderStructuredContentsPageWithFilterStringEquals()
		throws Exception {

		testGetStructuredContentFolderStructuredContentsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetStructuredContentFolderStructuredContentsPageWithFilterStringStartsWith()
		throws Exception {

		testGetStructuredContentFolderStructuredContentsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetStructuredContentFolderStructuredContentsPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long structuredContentFolderId =
			testGetStructuredContentFolderStructuredContentsPage_getStructuredContentFolderId();

		StructuredContent structuredContent1 =
			testGetStructuredContentFolderStructuredContentsPage_addStructuredContent(
				structuredContentFolderId, randomStructuredContent());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent structuredContent2 =
			testGetStructuredContentFolderStructuredContentsPage_addStructuredContent(
				structuredContentFolderId, randomStructuredContent());

		for (EntityField entityField : entityFields) {
			Page<StructuredContent> page =
				structuredContentResource.
					getStructuredContentFolderStructuredContentsPage(
						structuredContentFolderId, null, null, null,
						getFilterString(
							entityField, operator, structuredContent1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(structuredContent1),
				(List<StructuredContent>)page.getItems());
		}
	}

	@Test
	public void testGetStructuredContentFolderStructuredContentsPageWithPagination()
		throws Exception {

		Long structuredContentFolderId =
			testGetStructuredContentFolderStructuredContentsPage_getStructuredContentFolderId();

		Page<StructuredContent> structuredContentsPage =
			structuredContentResource.
				getStructuredContentFolderStructuredContentsPage(
					structuredContentFolderId, null, null, null, null, null,
					null);

		int totalCount = GetterUtil.getInteger(
			structuredContentsPage.getTotalCount());

		StructuredContent structuredContent1 =
			testGetStructuredContentFolderStructuredContentsPage_addStructuredContent(
				structuredContentFolderId, randomStructuredContent());

		StructuredContent structuredContent2 =
			testGetStructuredContentFolderStructuredContentsPage_addStructuredContent(
				structuredContentFolderId, randomStructuredContent());

		StructuredContent structuredContent3 =
			testGetStructuredContentFolderStructuredContentsPage_addStructuredContent(
				structuredContentFolderId, randomStructuredContent());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<StructuredContent> page1 =
				structuredContentResource.
					getStructuredContentFolderStructuredContentsPage(
						structuredContentFolderId, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				structuredContent1, (List<StructuredContent>)page1.getItems());

			Page<StructuredContent> page2 =
				structuredContentResource.
					getStructuredContentFolderStructuredContentsPage(
						structuredContentFolderId, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				structuredContent2, (List<StructuredContent>)page2.getItems());

			Page<StructuredContent> page3 =
				structuredContentResource.
					getStructuredContentFolderStructuredContentsPage(
						structuredContentFolderId, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				structuredContent3, (List<StructuredContent>)page3.getItems());
		}
		else {
			Page<StructuredContent> page1 =
				structuredContentResource.
					getStructuredContentFolderStructuredContentsPage(
						structuredContentFolderId, null, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<StructuredContent> structuredContents1 =
				(List<StructuredContent>)page1.getItems();

			Assert.assertEquals(
				structuredContents1.toString(), totalCount + 2,
				structuredContents1.size());

			Page<StructuredContent> page2 =
				structuredContentResource.
					getStructuredContentFolderStructuredContentsPage(
						structuredContentFolderId, null, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<StructuredContent> structuredContents2 =
				(List<StructuredContent>)page2.getItems();

			Assert.assertEquals(
				structuredContents2.toString(), 1, structuredContents2.size());

			Page<StructuredContent> page3 =
				structuredContentResource.
					getStructuredContentFolderStructuredContentsPage(
						structuredContentFolderId, null, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				structuredContent1, (List<StructuredContent>)page3.getItems());
			assertContains(
				structuredContent2, (List<StructuredContent>)page3.getItems());
			assertContains(
				structuredContent3, (List<StructuredContent>)page3.getItems());
		}
	}

	@Test
	public void testGetStructuredContentFolderStructuredContentsPageWithSortDateTime()
		throws Exception {

		testGetStructuredContentFolderStructuredContentsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, structuredContent1, structuredContent2) -> {
				BeanTestUtil.setProperty(
					structuredContent1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetStructuredContentFolderStructuredContentsPageWithSortDouble()
		throws Exception {

		testGetStructuredContentFolderStructuredContentsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, structuredContent1, structuredContent2) -> {
				BeanTestUtil.setProperty(
					structuredContent1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					structuredContent2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetStructuredContentFolderStructuredContentsPageWithSortInteger()
		throws Exception {

		testGetStructuredContentFolderStructuredContentsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, structuredContent1, structuredContent2) -> {
				BeanTestUtil.setProperty(
					structuredContent1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					structuredContent2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetStructuredContentFolderStructuredContentsPageWithSortString()
		throws Exception {

		testGetStructuredContentFolderStructuredContentsPageWithSort(
			EntityField.Type.STRING,
			(entityField, structuredContent1, structuredContent2) -> {
				Class<?> clazz = structuredContent1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						structuredContent1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						structuredContent2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						structuredContent1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						structuredContent2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						structuredContent1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						structuredContent2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetStructuredContentFolderStructuredContentsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, StructuredContent, StructuredContent, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long structuredContentFolderId =
			testGetStructuredContentFolderStructuredContentsPage_getStructuredContentFolderId();

		StructuredContent structuredContent1 = randomStructuredContent();
		StructuredContent structuredContent2 = randomStructuredContent();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, structuredContent1, structuredContent2);
		}

		structuredContent1 =
			testGetStructuredContentFolderStructuredContentsPage_addStructuredContent(
				structuredContentFolderId, structuredContent1);

		structuredContent2 =
			testGetStructuredContentFolderStructuredContentsPage_addStructuredContent(
				structuredContentFolderId, structuredContent2);

		Page<StructuredContent> page =
			structuredContentResource.
				getStructuredContentFolderStructuredContentsPage(
					structuredContentFolderId, null, null, null, null, null,
					null);

		for (EntityField entityField : entityFields) {
			Page<StructuredContent> ascPage =
				structuredContentResource.
					getStructuredContentFolderStructuredContentsPage(
						structuredContentFolderId, null, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				structuredContent1,
				(List<StructuredContent>)ascPage.getItems());
			assertContains(
				structuredContent2,
				(List<StructuredContent>)ascPage.getItems());

			Page<StructuredContent> descPage =
				structuredContentResource.
					getStructuredContentFolderStructuredContentsPage(
						structuredContentFolderId, null, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				structuredContent2,
				(List<StructuredContent>)descPage.getItems());
			assertContains(
				structuredContent1,
				(List<StructuredContent>)descPage.getItems());
		}
	}

	protected StructuredContent
			testGetStructuredContentFolderStructuredContentsPage_addStructuredContent(
				Long structuredContentFolderId,
				StructuredContent structuredContent)
		throws Exception {

		return structuredContentResource.
			postStructuredContentFolderStructuredContent(
				structuredContentFolderId, structuredContent);
	}

	protected Long
			testGetStructuredContentFolderStructuredContentsPage_getStructuredContentFolderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetStructuredContentFolderStructuredContentsPage_getIrrelevantStructuredContentFolderId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetStructuredContentFolderStructuredContentsPage()
		throws Exception {

		Long structuredContentFolderId =
			testGetStructuredContentFolderStructuredContentsPage_getStructuredContentFolderId();

		GraphQLField graphQLField = new GraphQLField(
			"structuredContentFolderStructuredContents",
			new HashMap<String, Object>() {
				{
					put("structuredContentFolderId", structuredContentFolderId);
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject structuredContentFolderStructuredContentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/structuredContentFolderStructuredContents");

		long totalCount =
			structuredContentFolderStructuredContentsJSONObject.getLong(
				"totalCount");

		StructuredContent structuredContent1 =
			testGraphQLStructuredContentFolderStructuredContent_addStructuredContent(
				structuredContentFolderId, randomStructuredContent());

		StructuredContent structuredContent2 =
			testGraphQLStructuredContentFolderStructuredContent_addStructuredContent(
				structuredContentFolderId, randomStructuredContent());

		structuredContentFolderStructuredContentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/structuredContentFolderStructuredContents");

		Assert.assertEquals(
			totalCount + 2,
			structuredContentFolderStructuredContentsJSONObject.getLong(
				"totalCount"));

		assertContains(
			structuredContent1,
			Arrays.asList(
				StructuredContentSerDes.toDTOs(
					structuredContentFolderStructuredContentsJSONObject.
						getString("items"))));
		assertContains(
			structuredContent2,
			Arrays.asList(
				StructuredContentSerDes.toDTOs(
					structuredContentFolderStructuredContentsJSONObject.
						getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		structuredContentFolderStructuredContentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("headlessDelivery_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"JSONObject/structuredContentFolderStructuredContents");

		Assert.assertEquals(
			totalCount + 2,
			structuredContentFolderStructuredContentsJSONObject.getLong(
				"totalCount"));

		assertContains(
			structuredContent1,
			Arrays.asList(
				StructuredContentSerDes.toDTOs(
					structuredContentFolderStructuredContentsJSONObject.
						getString("items"))));
		assertContains(
			structuredContent2,
			Arrays.asList(
				StructuredContentSerDes.toDTOs(
					structuredContentFolderStructuredContentsJSONObject.
						getString("items"))));
	}

	@Test
	public void testGetStructuredContentPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent postStructuredContent =
			testGetStructuredContentPermissionsPage_addStructuredContent();

		Page<Permission> page =
			structuredContentResource.getStructuredContentPermissionsPage(
				postStructuredContent.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected StructuredContent
			testGetStructuredContentPermissionsPage_addStructuredContent()
		throws Exception {

		return structuredContentResource.postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	@Test
	public void testGraphQLGetStructuredContentPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent postStructuredContent =
			testGraphQLGetStructuredContentPermissionsPage_addStructuredContent();

		GraphQLField graphQLField = new GraphQLField(
			"structuredContentPermissions",
			new HashMap<String, Object>() {
				{
					put("structuredContentId", postStructuredContent.getId());
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject structuredContentPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/structuredContentPermissions");

		Assert.assertNotNull(structuredContentPermissionsJSONObject);
	}

	protected StructuredContent
			testGraphQLGetStructuredContentPermissionsPage_addStructuredContent()
		throws Exception {

		return testGraphQLStructuredContent_addStructuredContent();
	}

	@Test
	public void testGetStructuredContentRenderedContentByDisplayPageDisplayPageKey()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testGetStructuredContentRenderedContentContentTemplate()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testPatchStructuredContent() throws Exception {
		StructuredContent postStructuredContent =
			testPatchStructuredContent_addStructuredContent();

		StructuredContent randomPatchStructuredContent =
			randomPatchStructuredContent();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent patchStructuredContent =
			structuredContentResource.patchStructuredContent(
				postStructuredContent.getId(), randomPatchStructuredContent);

		StructuredContent expectedPatchStructuredContent =
			postStructuredContent.clone();

		BeanTestUtil.copyProperties(
			randomPatchStructuredContent, expectedPatchStructuredContent);

		StructuredContent getStructuredContent =
			structuredContentResource.getStructuredContent(
				patchStructuredContent.getId());

		assertEquals(expectedPatchStructuredContent, getStructuredContent);
		assertValid(getStructuredContent);
	}

	protected StructuredContent
			testPatchStructuredContent_addStructuredContent()
		throws Exception {

		return structuredContentResource.postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	@Test
	public void testPostAssetLibraryStructuredContent() throws Exception {
		StructuredContent randomStructuredContent = randomStructuredContent();

		StructuredContent postStructuredContent =
			testPostAssetLibraryStructuredContent_addStructuredContent(
				randomStructuredContent);

		assertEquals(randomStructuredContent, postStructuredContent);
		assertValid(postStructuredContent);
	}

	protected StructuredContent
			testPostAssetLibraryStructuredContent_addStructuredContent(
				StructuredContent structuredContent)
		throws Exception {

		return structuredContentResource.postAssetLibraryStructuredContent(
			testGetAssetLibraryStructuredContentsPage_getAssetLibraryId(),
			structuredContent);
	}

	@Test
	public void testGraphQLPostAssetLibraryStructuredContent()
		throws Exception {

		StructuredContent randomStructuredContent = randomStructuredContent();

		StructuredContent structuredContent =
			testGraphQLAssetLibraryStructuredContent_addStructuredContent(
				testDepotEntry.getDepotEntryId(), randomStructuredContent);

		Assert.assertTrue(equals(randomStructuredContent, structuredContent));
	}

	@Test
	public void testPostSiteStructuredContent() throws Exception {
		StructuredContent randomStructuredContent = randomStructuredContent();

		StructuredContent postStructuredContent =
			testPostSiteStructuredContent_addStructuredContent(
				randomStructuredContent);

		assertEquals(randomStructuredContent, postStructuredContent);
		assertValid(postStructuredContent);
	}

	protected StructuredContent
			testPostSiteStructuredContent_addStructuredContent(
				StructuredContent structuredContent)
		throws Exception {

		return structuredContentResource.postSiteStructuredContent(
			testGetSiteStructuredContentsPage_getSiteId(), structuredContent);
	}

	@Test
	public void testGraphQLPostSiteStructuredContent() throws Exception {
		StructuredContent randomStructuredContent = randomStructuredContent();

		StructuredContent structuredContent =
			testGraphQLSiteStructuredContent_addStructuredContent(
				testGroup.getGroupId(), randomStructuredContent);

		Assert.assertTrue(equals(randomStructuredContent, structuredContent));
	}

	@Test
	public void testPostStructuredContentFolderStructuredContent()
		throws Exception {

		StructuredContent randomStructuredContent = randomStructuredContent();

		StructuredContent postStructuredContent =
			testPostStructuredContentFolderStructuredContent_addStructuredContent(
				randomStructuredContent);

		assertEquals(randomStructuredContent, postStructuredContent);
		assertValid(postStructuredContent);
	}

	protected StructuredContent
			testPostStructuredContentFolderStructuredContent_addStructuredContent(
				StructuredContent structuredContent)
		throws Exception {

		return structuredContentResource.
			postStructuredContentFolderStructuredContent(
				testGetStructuredContentFolderStructuredContentsPage_getStructuredContentFolderId(),
				structuredContent);
	}

	@Test
	public void testGraphQLPostStructuredContentFolderStructuredContent()
		throws Exception {

		StructuredContent randomStructuredContent = randomStructuredContent();

		StructuredContent structuredContent =
			testGraphQLStructuredContentFolderStructuredContent_addStructuredContent(
				testGraphQLPostStructuredContentFolderStructuredContent_getStructuredContentFolderId(
					randomStructuredContent),
				randomStructuredContent);

		Assert.assertTrue(equals(randomStructuredContent, structuredContent));
	}

	protected Long
			testGraphQLPostStructuredContentFolderStructuredContent_getStructuredContentFolderId(
				StructuredContent structuredContent)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutAssetLibraryStructuredContentByExternalReferenceCode()
		throws Exception {

		StructuredContent postStructuredContent =
			testPutAssetLibraryStructuredContentByExternalReferenceCode_addStructuredContent();

		StructuredContent randomStructuredContent = randomStructuredContent();

		StructuredContent putStructuredContent =
			structuredContentResource.
				putAssetLibraryStructuredContentByExternalReferenceCode(
					testPutAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId(),
					postStructuredContent.getExternalReferenceCode(),
					randomStructuredContent);

		assertEquals(randomStructuredContent, putStructuredContent);
		assertValid(putStructuredContent);

		StructuredContent getStructuredContent =
			structuredContentResource.
				getAssetLibraryStructuredContentByExternalReferenceCode(
					testPutAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId(),
					putStructuredContent.getExternalReferenceCode());

		assertEquals(randomStructuredContent, getStructuredContent);
		assertValid(getStructuredContent);

		StructuredContent newStructuredContent =
			testPutAssetLibraryStructuredContentByExternalReferenceCode_createStructuredContent();

		putStructuredContent =
			structuredContentResource.
				putAssetLibraryStructuredContentByExternalReferenceCode(
					testPutAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId(),
					newStructuredContent.getExternalReferenceCode(),
					newStructuredContent);

		assertEquals(newStructuredContent, putStructuredContent);
		assertValid(putStructuredContent);

		getStructuredContent =
			structuredContentResource.
				getAssetLibraryStructuredContentByExternalReferenceCode(
					testPutAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId(),
					putStructuredContent.getExternalReferenceCode());

		assertEquals(newStructuredContent, getStructuredContent);

		Assert.assertEquals(
			newStructuredContent.getExternalReferenceCode(),
			putStructuredContent.getExternalReferenceCode());
	}

	protected StructuredContent
			testPutAssetLibraryStructuredContentByExternalReferenceCode_addStructuredContent()
		throws Exception {

		return structuredContentResource.postAssetLibraryStructuredContent(
			testDepotEntry.getDepotEntryId(), randomStructuredContent());
	}

	protected Long
			testPutAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected StructuredContent
			testPutAssetLibraryStructuredContentByExternalReferenceCode_createStructuredContent()
		throws Exception {

		return randomStructuredContent();
	}

	@Test
	public void testPutAssetLibraryStructuredContentPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent structuredContent =
			testPutAssetLibraryStructuredContentPermissionsPage_addStructuredContent();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			structuredContentResource.
				putAssetLibraryStructuredContentPermissionsPageHttpResponse(
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
			structuredContentResource.
				putAssetLibraryStructuredContentPermissionsPageHttpResponse(
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

	protected StructuredContent
			testPutAssetLibraryStructuredContentPermissionsPage_addStructuredContent()
		throws Exception {

		return structuredContentResource.postAssetLibraryStructuredContent(
			testDepotEntry.getDepotEntryId(), randomStructuredContent());
	}

	@Test
	public void testPutSiteStructuredContentByExternalReferenceCode()
		throws Exception {

		StructuredContent postStructuredContent =
			testPutSiteStructuredContentByExternalReferenceCode_addStructuredContent();

		StructuredContent randomStructuredContent = randomStructuredContent();

		StructuredContent putStructuredContent =
			structuredContentResource.
				putSiteStructuredContentByExternalReferenceCode(
					postStructuredContent.getSiteId(),
					postStructuredContent.getExternalReferenceCode(),
					randomStructuredContent);

		assertEquals(randomStructuredContent, putStructuredContent);
		assertValid(putStructuredContent);

		StructuredContent getStructuredContent =
			structuredContentResource.
				getSiteStructuredContentByExternalReferenceCode(
					putStructuredContent.getSiteId(),
					putStructuredContent.getExternalReferenceCode());

		assertEquals(randomStructuredContent, getStructuredContent);
		assertValid(getStructuredContent);

		StructuredContent newStructuredContent =
			testPutSiteStructuredContentByExternalReferenceCode_createStructuredContent();

		putStructuredContent =
			structuredContentResource.
				putSiteStructuredContentByExternalReferenceCode(
					newStructuredContent.getSiteId(),
					newStructuredContent.getExternalReferenceCode(),
					newStructuredContent);

		assertEquals(newStructuredContent, putStructuredContent);
		assertValid(putStructuredContent);

		getStructuredContent =
			structuredContentResource.
				getSiteStructuredContentByExternalReferenceCode(
					putStructuredContent.getSiteId(),
					putStructuredContent.getExternalReferenceCode());

		assertEquals(newStructuredContent, getStructuredContent);

		Assert.assertEquals(
			newStructuredContent.getExternalReferenceCode(),
			putStructuredContent.getExternalReferenceCode());
	}

	protected StructuredContent
			testPutSiteStructuredContentByExternalReferenceCode_addStructuredContent()
		throws Exception {

		return structuredContentResource.postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	protected StructuredContent
			testPutSiteStructuredContentByExternalReferenceCode_createStructuredContent()
		throws Exception {

		return randomStructuredContent();
	}

	@Test
	public void testPutSiteStructuredContentPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent structuredContent =
			testPutSiteStructuredContentPermissionsPage_addStructuredContent();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			structuredContentResource.
				putSiteStructuredContentPermissionsPageHttpResponse(
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
			structuredContentResource.
				putSiteStructuredContentPermissionsPageHttpResponse(
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

	protected StructuredContent
			testPutSiteStructuredContentPermissionsPage_addStructuredContent()
		throws Exception {

		return structuredContentResource.postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	@Test
	public void testPutStructuredContent() throws Exception {
		StructuredContent postStructuredContent =
			testPutStructuredContent_addStructuredContent();

		StructuredContent randomStructuredContent = randomStructuredContent();

		StructuredContent putStructuredContent =
			structuredContentResource.putStructuredContent(
				postStructuredContent.getId(), randomStructuredContent);

		assertEquals(randomStructuredContent, putStructuredContent);
		assertValid(putStructuredContent);

		StructuredContent getStructuredContent =
			structuredContentResource.getStructuredContent(
				putStructuredContent.getId());

		assertEquals(randomStructuredContent, getStructuredContent);
		assertValid(getStructuredContent);
	}

	protected StructuredContent testPutStructuredContent_addStructuredContent()
		throws Exception {

		return structuredContentResource.postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	@Test
	public void testPutStructuredContentPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent structuredContent =
			testPutStructuredContentPermissionsPage_addStructuredContent();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			structuredContentResource.
				putStructuredContentPermissionsPageHttpResponse(
					structuredContent.getId(),
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
			structuredContentResource.
				putStructuredContentPermissionsPageHttpResponse(
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

	protected StructuredContent
			testPutStructuredContentPermissionsPage_addStructuredContent()
		throws Exception {

		return structuredContentResource.postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	@Test
	public void testPutStructuredContentSubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent structuredContent =
			testPutStructuredContentSubscribe_addStructuredContent();

		assertHttpResponseStatusCode(
			204,
			structuredContentResource.putStructuredContentSubscribeHttpResponse(
				structuredContent.getId()));

		assertHttpResponseStatusCode(
			404,
			structuredContentResource.putStructuredContentSubscribeHttpResponse(
				0L));
	}

	protected StructuredContent
			testPutStructuredContentSubscribe_addStructuredContent()
		throws Exception {

		return structuredContentResource.postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	@Test
	public void testPutStructuredContentUnsubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		StructuredContent structuredContent =
			testPutStructuredContentUnsubscribe_addStructuredContent();

		assertHttpResponseStatusCode(
			204,
			structuredContentResource.
				putStructuredContentUnsubscribeHttpResponse(
					structuredContent.getId()));

		assertHttpResponseStatusCode(
			404,
			structuredContentResource.
				putStructuredContentUnsubscribeHttpResponse(0L));
	}

	protected StructuredContent
			testPutStructuredContentUnsubscribe_addStructuredContent()
		throws Exception {

		return structuredContentResource.postSiteStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		StructuredContent structuredContent1 =
			testBatchEngineDeleteImportTask_addStructuredContent();

		testBatchEngineDeleteImportTask_deleteStructuredContent(
			200, null, structuredContent1.getId());

		assertHttpResponseStatusCode(
			404,
			structuredContentResource.getStructuredContentHttpResponse(
				structuredContent1.getId()));
	}

	protected StructuredContent
			testBatchEngineDeleteImportTask_addStructuredContent()
		throws Exception {

		return testDeleteStructuredContent_addStructuredContent();
	}

	protected void testBatchEngineDeleteImportTask_deleteStructuredContent(
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
				"com.liferay.headless.delivery.dto.v1_0.StructuredContent",
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

	@Test
	public void testGetStructuredContentMyRating() throws Exception {
		StructuredContent postStructuredContent =
			testGetStructuredContent_addStructuredContent();

		Rating postRating = testGetStructuredContentMyRating_addRating(
			postStructuredContent.getId(), randomRating());

		Rating getRating =
			structuredContentResource.getStructuredContentMyRating(
				postStructuredContent.getId());

		assertEquals(postRating, getRating);
		assertValid(getRating);
	}

	protected Rating testGetStructuredContentMyRating_addRating(
			long structuredContentId, Rating rating)
		throws Exception {

		return structuredContentResource.postStructuredContentMyRating(
			structuredContentId, rating);
	}

	@Test
	public void testPostStructuredContentMyRating() throws Exception {
		Assert.assertTrue(true);
	}

	@Test
	public void testPutStructuredContentMyRating() throws Exception {
		StructuredContent postStructuredContent =
			testPutStructuredContent_addStructuredContent();

		testPutStructuredContentMyRating_addRating(
			postStructuredContent.getId(), randomRating());

		Rating randomRating = randomRating();

		Rating putRating =
			structuredContentResource.putStructuredContentMyRating(
				postStructuredContent.getId(), randomRating);

		assertEquals(randomRating, putRating);
		assertValid(putRating);
	}

	protected Rating testPutStructuredContentMyRating_addRating(
			long structuredContentId, Rating rating)
		throws Exception {

		return structuredContentResource.postStructuredContentMyRating(
			structuredContentId, rating);
	}

	protected StructuredContent
			testGraphQLAssetLibraryStructuredContent_addStructuredContent()
		throws Exception {

		return testGraphQLAssetLibraryStructuredContent_addStructuredContent(
			testDepotEntry.getDepotEntryId(), randomStructuredContent());
	}

	protected StructuredContent
			testGraphQLAssetLibraryStructuredContent_addStructuredContent(
				Long assetLibraryId, StructuredContent structuredContent)
		throws Exception {

		JSONDeserializer<StructuredContent> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(StructuredContent.class)) {

			if (getGraphQLValue(field.get(structuredContent)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(structuredContent)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createAssetLibraryStructuredContent",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryId",
									"\"" + assetLibraryId + "\"");
								put("structuredContent", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createAssetLibraryStructuredContent"),
			StructuredContent.class);
	}

	protected StructuredContent
			testGraphQLSiteStructuredContent_addStructuredContent()
		throws Exception {

		return testGraphQLSiteStructuredContent_addStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	protected StructuredContent
			testGraphQLSiteStructuredContent_addStructuredContent(
				Long siteId, StructuredContent structuredContent)
		throws Exception {

		JSONDeserializer<StructuredContent> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(StructuredContent.class)) {

			if (getGraphQLValue(field.get(structuredContent)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(structuredContent)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteStructuredContent",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("structuredContent", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteStructuredContent"),
			StructuredContent.class);
	}

	protected StructuredContent
			testGraphQLStructuredContent_addStructuredContent()
		throws Exception {

		return testGraphQLStructuredContent_addStructuredContent(
			testGroup.getGroupId(), randomStructuredContent());
	}

	protected StructuredContent
			testGraphQLStructuredContent_addStructuredContent(
				Long siteId, StructuredContent structuredContent)
		throws Exception {

		JSONDeserializer<StructuredContent> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(StructuredContent.class)) {

			if (getGraphQLValue(field.get(structuredContent)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(structuredContent)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteStructuredContent",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("structuredContent", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteStructuredContent"),
			StructuredContent.class);
	}

	protected StructuredContent
			testGraphQLStructuredContentFolderStructuredContent_addStructuredContent()
		throws Exception {

		return testGraphQLStructuredContentFolderStructuredContent_addStructuredContent(
			testGraphQLStructuredContentFolderStructuredContent_getStructuredContentFolderId(),
			randomStructuredContent());
	}

	protected Long
			testGraphQLStructuredContentFolderStructuredContent_getStructuredContentFolderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected StructuredContent
			testGraphQLStructuredContentFolderStructuredContent_addStructuredContent(
				Long structuredContentFolderId,
				StructuredContent structuredContent)
		throws Exception {

		JSONDeserializer<StructuredContent> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(StructuredContent.class)) {

			if (getGraphQLValue(field.get(structuredContent)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(structuredContent)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createStructuredContentFolderStructuredContent",
						new HashMap<String, Object>() {
							{
								put(
									"structuredContentFolderId",
									structuredContentFolderId);
								put("structuredContent", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createStructuredContentFolderStructuredContent"),
			StructuredContent.class);
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
		StructuredContent structuredContent,
		List<StructuredContent> structuredContents) {

		boolean contains = false;

		for (StructuredContent item : structuredContents) {
			if (equals(structuredContent, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			structuredContents + " does not contain " + structuredContent,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		StructuredContent structuredContent1,
		StructuredContent structuredContent2) {

		Assert.assertTrue(
			structuredContent1 + " does not equal " + structuredContent2,
			equals(structuredContent1, structuredContent2));
	}

	protected void assertEquals(
		List<StructuredContent> structuredContents1,
		List<StructuredContent> structuredContents2) {

		Assert.assertEquals(
			structuredContents1.size(), structuredContents2.size());

		for (int i = 0; i < structuredContents1.size(); i++) {
			StructuredContent structuredContent1 = structuredContents1.get(i);
			StructuredContent structuredContent2 = structuredContents2.get(i);

			assertEquals(structuredContent1, structuredContent2);
		}
	}

	protected void assertEquals(Rating rating1, Rating rating2) {
		Assert.assertTrue(
			rating1 + " does not equal " + rating2, equals(rating1, rating2));
	}

	protected void assertEqualsIgnoringOrder(
		List<StructuredContent> structuredContents1,
		List<StructuredContent> structuredContents2) {

		Assert.assertEquals(
			structuredContents1.size(), structuredContents2.size());

		for (StructuredContent structuredContent1 : structuredContents1) {
			boolean contains = false;

			for (StructuredContent structuredContent2 : structuredContents2) {
				if (equals(structuredContent1, structuredContent2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				structuredContents2 + " does not contain " + structuredContent1,
				contains);
		}
	}

	protected void assertValid(StructuredContent structuredContent)
		throws Exception {

		boolean valid = true;

		if (structuredContent.getDateCreated() == null) {
			valid = false;
		}

		if (structuredContent.getDateModified() == null) {
			valid = false;
		}

		if (structuredContent.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				structuredContent.getAssetLibraryKey(),
				testDepotEntryGroup.getGroupKey()) &&
			!Objects.equals(
				structuredContent.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (structuredContent.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("aggregateRating", additionalAssertFieldName)) {
				if (structuredContent.getAggregateRating() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetLibraryKey", additionalAssertFieldName)) {
				if (structuredContent.getAssetLibraryKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (structuredContent.getAvailableLanguages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("contentFields", additionalAssertFieldName)) {
				if (structuredContent.getContentFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"contentStructureId", additionalAssertFieldName)) {

				if (structuredContent.getContentStructureId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (structuredContent.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (structuredContent.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dateExpired", additionalAssertFieldName)) {
				if (structuredContent.getDateExpired() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (structuredContent.getDatePublished() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (structuredContent.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (structuredContent.getDescription_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (structuredContent.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (structuredContent.getFriendlyUrlPath() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyUrlPath_i18n", additionalAssertFieldName)) {

				if (structuredContent.getFriendlyUrlPath_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (structuredContent.getKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (structuredContent.getKeywords() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (structuredContent.getNeverExpire() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("numberOfComments", additionalAssertFieldName)) {
				if (structuredContent.getNumberOfComments() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (structuredContent.getPermissions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (structuredContent.getPriority() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("relatedContents", additionalAssertFieldName)) {
				if (structuredContent.getRelatedContents() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("renderedContents", additionalAssertFieldName)) {
				if (structuredContent.getRenderedContents() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"structuredContentFolderId", additionalAssertFieldName)) {

				if (structuredContent.getStructuredContentFolderId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("subscribed", additionalAssertFieldName)) {
				if (structuredContent.getSubscribed() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryBriefs", additionalAssertFieldName)) {

				if (structuredContent.getTaxonomyCategoryBriefs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryIds", additionalAssertFieldName)) {

				if (structuredContent.getTaxonomyCategoryIds() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (structuredContent.getTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title_i18n", additionalAssertFieldName)) {
				if (structuredContent.getTitle_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("uuid", additionalAssertFieldName)) {
				if (structuredContent.getUuid() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (structuredContent.getViewableBy() == null) {
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

	protected void assertValid(Page<StructuredContent> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<StructuredContent> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<StructuredContent> structuredContents =
			page.getItems();

		int size = structuredContents.size();

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
					com.liferay.headless.delivery.dto.v1_0.StructuredContent.
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
		StructuredContent structuredContent1,
		StructuredContent structuredContent2) {

		if (structuredContent1 == structuredContent2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)structuredContent1.getActions(),
						(Map)structuredContent2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("aggregateRating", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getAggregateRating(),
						structuredContent2.getAggregateRating())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						structuredContent1.getAvailableLanguages(),
						structuredContent2.getAvailableLanguages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("contentFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getContentFields(),
						structuredContent2.getContentFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"contentStructureId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						structuredContent1.getContentStructureId(),
						structuredContent2.getContentStructureId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getCreator(),
						structuredContent2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getCustomFields(),
						structuredContent2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getDateCreated(),
						structuredContent2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateExpired", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getDateExpired(),
						structuredContent2.getDateExpired())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getDateModified(),
						structuredContent2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getDatePublished(),
						structuredContent2.getDatePublished())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getDescription(),
						structuredContent2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)structuredContent1.getDescription_i18n(),
						(Map)structuredContent2.getDescription_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						structuredContent1.getExternalReferenceCode(),
						structuredContent2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getFriendlyUrlPath(),
						structuredContent2.getFriendlyUrlPath())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyUrlPath_i18n", additionalAssertFieldName)) {

				if (!equals(
						(Map)structuredContent1.getFriendlyUrlPath_i18n(),
						(Map)structuredContent2.getFriendlyUrlPath_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getId(),
						structuredContent2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getKey(),
						structuredContent2.getKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getKeywords(),
						structuredContent2.getKeywords())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getNeverExpire(),
						structuredContent2.getNeverExpire())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("numberOfComments", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getNumberOfComments(),
						structuredContent2.getNumberOfComments())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getPermissions(),
						structuredContent2.getPermissions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getPriority(),
						structuredContent2.getPriority())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("relatedContents", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getRelatedContents(),
						structuredContent2.getRelatedContents())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("renderedContents", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getRenderedContents(),
						structuredContent2.getRenderedContents())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"structuredContentFolderId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						structuredContent1.getStructuredContentFolderId(),
						structuredContent2.getStructuredContentFolderId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("subscribed", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getSubscribed(),
						structuredContent2.getSubscribed())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryBriefs", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						structuredContent1.getTaxonomyCategoryBriefs(),
						structuredContent2.getTaxonomyCategoryBriefs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryIds", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						structuredContent1.getTaxonomyCategoryIds(),
						structuredContent2.getTaxonomyCategoryIds())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getTitle(),
						structuredContent2.getTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)structuredContent1.getTitle_i18n(),
						(Map)structuredContent2.getTitle_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("uuid", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getUuid(),
						structuredContent2.getUuid())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						structuredContent1.getViewableBy(),
						structuredContent2.getViewableBy())) {

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

		if (!(_structuredContentResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_structuredContentResource;

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
		StructuredContent structuredContent) {

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

		if (entityFieldName.equals("aggregateRating")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("assetLibraryKey")) {
			Object object = structuredContent.getAssetLibraryKey();

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

		if (entityFieldName.equals("availableLanguages")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("contentFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("contentStructureId")) {
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
				Date date = structuredContent.getDateCreated();

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

				sb.append(_format.format(structuredContent.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateExpired")) {
			if (operator.equals("between")) {
				Date date = structuredContent.getDateExpired();

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

				sb.append(_format.format(structuredContent.getDateExpired()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = structuredContent.getDateModified();

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

				sb.append(_format.format(structuredContent.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("datePublished")) {
			if (operator.equals("between")) {
				Date date = structuredContent.getDatePublished();

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

				sb.append(_format.format(structuredContent.getDatePublished()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = structuredContent.getDescription();

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

		if (entityFieldName.equals("description_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = structuredContent.getExternalReferenceCode();

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
			Object object = structuredContent.getFriendlyUrlPath();

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

		if (entityFieldName.equals("friendlyUrlPath_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("key")) {
			Object object = structuredContent.getKey();

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

		if (entityFieldName.equals("keywords")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("neverExpire")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("numberOfComments")) {
			sb.append(String.valueOf(structuredContent.getNumberOfComments()));

			return sb.toString();
		}

		if (entityFieldName.equals("permissions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priority")) {
			sb.append(String.valueOf(structuredContent.getPriority()));

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

		if (entityFieldName.equals("structuredContentFolderId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("subscribed")) {
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
			Object object = structuredContent.getTitle();

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

		if (entityFieldName.equals("title_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("uuid")) {
			Object object = structuredContent.getUuid();

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

	protected StructuredContent randomStructuredContent() throws Exception {
		return new StructuredContent() {
			{
				assetLibraryKey = String.valueOf(
					testDepotEntry.getDepotEntryId());
				contentStructureId = RandomTestUtil.randomLong();
				dateCreated = RandomTestUtil.nextDate();
				dateExpired = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				datePublished = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				friendlyUrlPath = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				neverExpire = RandomTestUtil.randomBoolean();
				numberOfComments = RandomTestUtil.randomInt();
				priority = RandomTestUtil.randomDouble();
				siteId = testGroup.getGroupId();
				structuredContentFolderId = RandomTestUtil.randomLong();
				subscribed = RandomTestUtil.randomBoolean();
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
				uuid = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected StructuredContent randomIrrelevantStructuredContent()
		throws Exception {

		StructuredContent randomIrrelevantStructuredContent =
			randomStructuredContent();

		randomIrrelevantStructuredContent.setAssetLibraryKey(
			String.valueOf(irrelevantDepotEntry.getDepotEntryId()));

		randomIrrelevantStructuredContent.setSiteId(
			irrelevantGroup.getGroupId());

		return randomIrrelevantStructuredContent;
	}

	protected StructuredContent randomPatchStructuredContent()
		throws Exception {

		return randomStructuredContent();
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

	protected StructuredContentResource structuredContentResource;
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
		LogFactoryUtil.getLog(BaseStructuredContentResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.delivery.resource.v1_0.StructuredContentResource
			_structuredContentResource;

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