/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.resource.v1_0.test;

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
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.TaxonomyVocabulary;
import com.liferay.headless.admin.taxonomy.client.http.HttpInvoker;
import com.liferay.headless.admin.taxonomy.client.pagination.Page;
import com.liferay.headless.admin.taxonomy.client.pagination.Pagination;
import com.liferay.headless.admin.taxonomy.client.permission.Permission;
import com.liferay.headless.admin.taxonomy.client.resource.v1_0.TaxonomyVocabularyResource;
import com.liferay.headless.admin.taxonomy.client.serdes.v1_0.TaxonomyVocabularySerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
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
public abstract class BaseTaxonomyVocabularyResourceTestCase {

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

		_taxonomyVocabularyResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		taxonomyVocabularyResource = TaxonomyVocabularyResource.builder(
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

		permissionsTaxonomyVocabularyResource =
			TaxonomyVocabularyResource.builder(
			).authentication(
				_testCompanyAdminUser.getEmailAddress(),
				PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(), 8080, "http"
			).locale(
				LocaleUtil.getDefault()
			).parameter(
				"nestedFields", "permissions"
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

		TaxonomyVocabulary taxonomyVocabulary1 = randomTaxonomyVocabulary();

		String json = objectMapper.writeValueAsString(taxonomyVocabulary1);

		TaxonomyVocabulary taxonomyVocabulary2 = TaxonomyVocabularySerDes.toDTO(
			json);

		Assert.assertTrue(equals(taxonomyVocabulary1, taxonomyVocabulary2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		TaxonomyVocabulary taxonomyVocabulary = randomTaxonomyVocabulary();

		String json1 = objectMapper.writeValueAsString(taxonomyVocabulary);
		String json2 = TaxonomyVocabularySerDes.toJSON(taxonomyVocabulary);

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

		TaxonomyVocabulary taxonomyVocabulary = randomTaxonomyVocabulary();

		taxonomyVocabulary.setAssetLibraryKey(regex);
		taxonomyVocabulary.setDescription(regex);
		taxonomyVocabulary.setExternalReferenceCode(regex);
		taxonomyVocabulary.setName(regex);
		taxonomyVocabulary.setSiteExternalReferenceCode(regex);

		String json = TaxonomyVocabularySerDes.toJSON(taxonomyVocabulary);

		Assert.assertFalse(json.contains(regex));

		taxonomyVocabulary = TaxonomyVocabularySerDes.toDTO(json);

		Assert.assertEquals(regex, taxonomyVocabulary.getAssetLibraryKey());
		Assert.assertEquals(regex, taxonomyVocabulary.getDescription());
		Assert.assertEquals(
			regex, taxonomyVocabulary.getExternalReferenceCode());
		Assert.assertEquals(regex, taxonomyVocabulary.getName());
		Assert.assertEquals(
			regex, taxonomyVocabulary.getSiteExternalReferenceCode());
	}

	@Test
	public void testDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyVocabulary taxonomyVocabulary =
			testDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary();

		assertHttpResponseStatusCode(
			204,
			taxonomyVocabularyResource.
				deleteAssetLibraryTaxonomyVocabularyByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId(),
					taxonomyVocabulary.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			taxonomyVocabularyResource.
				getAssetLibraryTaxonomyVocabularyByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId(),
					taxonomyVocabulary.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			taxonomyVocabularyResource.
				getAssetLibraryTaxonomyVocabularyByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId(),
					"-"));
	}

	protected TaxonomyVocabulary
			testDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary()
		throws Exception {

		return taxonomyVocabularyResource.postAssetLibraryTaxonomyVocabulary(
			testDepotEntry.getDepotEntryId(), randomTaxonomyVocabulary());
	}

	protected Long
			testDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode()
		throws Exception {

		// No namespace

		TaxonomyVocabulary taxonomyVocabulary1 =
			testGraphQLDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryId",
									"\"" +
										testGraphQLDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" +
										taxonomyVocabulary1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"assetLibraryTaxonomyVocabularyByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"assetLibraryId",
								"\"" +
									testGraphQLDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId() +
										"\"");
							put(
								"externalReferenceCode",
								"\"" +
									taxonomyVocabulary1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminTaxonomy_v1_0

		TaxonomyVocabulary taxonomyVocabulary2 =
			testGraphQLDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminTaxonomy_v1_0",
						new GraphQLField(
							"deleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"assetLibraryId",
										"\"" +
											testGraphQLDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId() +
												"\"");
									put(
										"externalReferenceCode",
										"\"" +
											taxonomyVocabulary2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminTaxonomy_v1_0",
				"Object/deleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminTaxonomy_v1_0",
					new GraphQLField(
						"assetLibraryTaxonomyVocabularyByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryId",
									"\"" +
										testGraphQLDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" +
										taxonomyVocabulary2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Long
			testGraphQLDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected TaxonomyVocabulary
			testGraphQLDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary()
		throws Exception {

		return testGraphQLAssetLibraryTaxonomyVocabulary_addTaxonomyVocabulary();
	}

	@Test
	public void testDeleteSiteTaxonomyVocabularyByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyVocabulary taxonomyVocabulary =
			testDeleteSiteTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary();

		assertHttpResponseStatusCode(
			204,
			taxonomyVocabularyResource.
				deleteSiteTaxonomyVocabularyByExternalReferenceCodeHttpResponse(
					taxonomyVocabulary.getSiteId(),
					taxonomyVocabulary.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			taxonomyVocabularyResource.
				getSiteTaxonomyVocabularyByExternalReferenceCodeHttpResponse(
					taxonomyVocabulary.getSiteId(),
					taxonomyVocabulary.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			taxonomyVocabularyResource.
				getSiteTaxonomyVocabularyByExternalReferenceCodeHttpResponse(
					taxonomyVocabulary.getSiteId(), "-"));
	}

	protected TaxonomyVocabulary
			testDeleteSiteTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary()
		throws Exception {

		return taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
			testGroup.getGroupId(), randomTaxonomyVocabulary());
	}

	@Test
	public void testGraphQLDeleteSiteTaxonomyVocabularyByExternalReferenceCode()
		throws Exception {

		// No namespace

		TaxonomyVocabulary taxonomyVocabulary1 =
			testGraphQLDeleteSiteTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteTaxonomyVocabularyByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + taxonomyVocabulary1.getSiteId() +
										"\"");
								put(
									"externalReferenceCode",
									"\"" +
										taxonomyVocabulary1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteTaxonomyVocabularyByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"taxonomyVocabularyByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"siteKey",
								"\"" + taxonomyVocabulary1.getSiteId() + "\"");
							put(
								"externalReferenceCode",
								"\"" +
									taxonomyVocabulary1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminTaxonomy_v1_0

		TaxonomyVocabulary taxonomyVocabulary2 =
			testGraphQLDeleteSiteTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminTaxonomy_v1_0",
						new GraphQLField(
							"deleteSiteTaxonomyVocabularyByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + taxonomyVocabulary2.getSiteId() +
											"\"");
									put(
										"externalReferenceCode",
										"\"" +
											taxonomyVocabulary2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminTaxonomy_v1_0",
				"Object/deleteSiteTaxonomyVocabularyByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminTaxonomy_v1_0",
					new GraphQLField(
						"taxonomyVocabularyByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + taxonomyVocabulary2.getSiteId() +
										"\"");
								put(
									"externalReferenceCode",
									"\"" +
										taxonomyVocabulary2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected TaxonomyVocabulary
			testGraphQLDeleteSiteTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary()
		throws Exception {

		return testGraphQLSiteTaxonomyVocabulary_addTaxonomyVocabulary();
	}

	@Test
	public void testDeleteTaxonomyVocabulary() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyVocabulary taxonomyVocabulary =
			testDeleteTaxonomyVocabulary_addTaxonomyVocabulary();

		assertHttpResponseStatusCode(
			204,
			taxonomyVocabularyResource.deleteTaxonomyVocabularyHttpResponse(
				taxonomyVocabulary.getId()));

		assertHttpResponseStatusCode(
			404,
			taxonomyVocabularyResource.getTaxonomyVocabularyHttpResponse(
				taxonomyVocabulary.getId()));
		assertHttpResponseStatusCode(
			404,
			taxonomyVocabularyResource.getTaxonomyVocabularyHttpResponse(0L));
	}

	protected TaxonomyVocabulary
			testDeleteTaxonomyVocabulary_addTaxonomyVocabulary()
		throws Exception {

		return taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
			testGroup.getGroupId(), randomTaxonomyVocabulary());
	}

	@Test
	public void testGraphQLDeleteTaxonomyVocabulary() throws Exception {

		// No namespace

		TaxonomyVocabulary taxonomyVocabulary1 =
			testGraphQLDeleteTaxonomyVocabulary_addTaxonomyVocabulary();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteTaxonomyVocabulary",
						new HashMap<String, Object>() {
							{
								put(
									"taxonomyVocabularyId",
									taxonomyVocabulary1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteTaxonomyVocabulary"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"taxonomyVocabulary",
					new HashMap<String, Object>() {
						{
							put(
								"taxonomyVocabularyId",
								taxonomyVocabulary1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminTaxonomy_v1_0

		TaxonomyVocabulary taxonomyVocabulary2 =
			testGraphQLDeleteTaxonomyVocabulary_addTaxonomyVocabulary();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminTaxonomy_v1_0",
						new GraphQLField(
							"deleteTaxonomyVocabulary",
							new HashMap<String, Object>() {
								{
									put(
										"taxonomyVocabularyId",
										taxonomyVocabulary2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminTaxonomy_v1_0",
				"Object/deleteTaxonomyVocabulary"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminTaxonomy_v1_0",
					new GraphQLField(
						"taxonomyVocabulary",
						new HashMap<String, Object>() {
							{
								put(
									"taxonomyVocabularyId",
									taxonomyVocabulary2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected TaxonomyVocabulary
			testGraphQLDeleteTaxonomyVocabulary_addTaxonomyVocabulary()
		throws Exception {

		return testGraphQLTaxonomyVocabulary_addTaxonomyVocabulary();
	}

	@Test
	public void testDeleteTaxonomyVocabularyBatch() throws Exception {
		TaxonomyVocabulary taxonomyVocabulary1 =
			testDeleteTaxonomyVocabularyBatch_addTaxonomyVocabulary();

		testDeleteTaxonomyVocabularyBatch_deleteTaxonomyVocabulary(
			202, null, taxonomyVocabulary1.getId());

		assertHttpResponseStatusCode(
			404,
			taxonomyVocabularyResource.getTaxonomyVocabularyHttpResponse(
				taxonomyVocabulary1.getId()));
	}

	protected TaxonomyVocabulary
			testDeleteTaxonomyVocabularyBatch_addTaxonomyVocabulary()
		throws Exception {

		return testDeleteTaxonomyVocabulary_addTaxonomyVocabulary();
	}

	protected void testDeleteTaxonomyVocabularyBatch_deleteTaxonomyVocabulary(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			taxonomyVocabularyResource.
				deleteTaxonomyVocabularyBatchHttpResponse(
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
	public void testGetAssetLibraryTaxonomyVocabulariesPage() throws Exception {
		Long assetLibraryId =
			testGetAssetLibraryTaxonomyVocabulariesPage_getAssetLibraryId();
		Long irrelevantAssetLibraryId =
			testGetAssetLibraryTaxonomyVocabulariesPage_getIrrelevantAssetLibraryId();

		Page<TaxonomyVocabulary> page =
			taxonomyVocabularyResource.getAssetLibraryTaxonomyVocabulariesPage(
				assetLibraryId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryId != null) {
			TaxonomyVocabulary irrelevantTaxonomyVocabulary =
				testGetAssetLibraryTaxonomyVocabulariesPage_addTaxonomyVocabulary(
					irrelevantAssetLibraryId,
					randomIrrelevantTaxonomyVocabulary());

			page =
				taxonomyVocabularyResource.
					getAssetLibraryTaxonomyVocabulariesPage(
						irrelevantAssetLibraryId, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantTaxonomyVocabulary,
				(List<TaxonomyVocabulary>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryTaxonomyVocabulariesPage_getExpectedActions(
					irrelevantAssetLibraryId));
		}

		TaxonomyVocabulary taxonomyVocabulary1 =
			testGetAssetLibraryTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				assetLibraryId, randomTaxonomyVocabulary());

		TaxonomyVocabulary taxonomyVocabulary2 =
			testGetAssetLibraryTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				assetLibraryId, randomTaxonomyVocabulary());

		page =
			taxonomyVocabularyResource.getAssetLibraryTaxonomyVocabulariesPage(
				assetLibraryId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			taxonomyVocabulary1, (List<TaxonomyVocabulary>)page.getItems());
		assertContains(
			taxonomyVocabulary2, (List<TaxonomyVocabulary>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryTaxonomyVocabulariesPage_getExpectedActions(
				assetLibraryId));

		for (TaxonomyVocabulary taxonomyVocabulary : page.getItems()) {
			Assert.assertNull(taxonomyVocabulary.getPermissions());
		}

		page =
			permissionsTaxonomyVocabularyResource.
				getAssetLibraryTaxonomyVocabulariesPage(
					assetLibraryId, null, null, null, Pagination.of(1, 10),
					null);

		for (TaxonomyVocabulary taxonomyVocabulary : page.getItems()) {
			Assert.assertNotNull(taxonomyVocabulary.getPermissions());
		}

		taxonomyVocabularyResource.deleteTaxonomyVocabulary(
			taxonomyVocabulary1.getId());

		taxonomyVocabularyResource.deleteTaxonomyVocabulary(
			taxonomyVocabulary2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryTaxonomyVocabulariesPage_getExpectedActions(
				Long assetLibraryId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-vocabularies/batch".
				replace("{assetLibraryId}", String.valueOf(assetLibraryId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryTaxonomyVocabulariesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryTaxonomyVocabulariesPage_getAssetLibraryId();

		TaxonomyVocabulary taxonomyVocabulary1 = randomTaxonomyVocabulary();

		taxonomyVocabulary1 =
			testGetAssetLibraryTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				assetLibraryId, taxonomyVocabulary1);

		for (EntityField entityField : entityFields) {
			Page<TaxonomyVocabulary> page =
				taxonomyVocabularyResource.
					getAssetLibraryTaxonomyVocabulariesPage(
						assetLibraryId, null, null,
						getFilterString(
							entityField, "between", taxonomyVocabulary1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(taxonomyVocabulary1),
				(List<TaxonomyVocabulary>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryTaxonomyVocabulariesPageWithFilterDoubleEquals()
		throws Exception {

		testGetAssetLibraryTaxonomyVocabulariesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAssetLibraryTaxonomyVocabulariesPageWithFilterStringContains()
		throws Exception {

		testGetAssetLibraryTaxonomyVocabulariesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryTaxonomyVocabulariesPageWithFilterStringEquals()
		throws Exception {

		testGetAssetLibraryTaxonomyVocabulariesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryTaxonomyVocabulariesPageWithFilterStringStartsWith()
		throws Exception {

		testGetAssetLibraryTaxonomyVocabulariesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetAssetLibraryTaxonomyVocabulariesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryTaxonomyVocabulariesPage_getAssetLibraryId();

		TaxonomyVocabulary taxonomyVocabulary1 =
			testGetAssetLibraryTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				assetLibraryId, randomTaxonomyVocabulary());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyVocabulary taxonomyVocabulary2 =
			testGetAssetLibraryTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				assetLibraryId, randomTaxonomyVocabulary());

		for (EntityField entityField : entityFields) {
			Page<TaxonomyVocabulary> page =
				taxonomyVocabularyResource.
					getAssetLibraryTaxonomyVocabulariesPage(
						assetLibraryId, null, null,
						getFilterString(
							entityField, operator, taxonomyVocabulary1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(taxonomyVocabulary1),
				(List<TaxonomyVocabulary>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryTaxonomyVocabulariesPageWithPagination()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryTaxonomyVocabulariesPage_getAssetLibraryId();

		Page<TaxonomyVocabulary> taxonomyVocabulariesPage =
			taxonomyVocabularyResource.getAssetLibraryTaxonomyVocabulariesPage(
				assetLibraryId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			taxonomyVocabulariesPage.getTotalCount());

		TaxonomyVocabulary taxonomyVocabulary1 =
			testGetAssetLibraryTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				assetLibraryId, randomTaxonomyVocabulary());

		TaxonomyVocabulary taxonomyVocabulary2 =
			testGetAssetLibraryTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				assetLibraryId, randomTaxonomyVocabulary());

		TaxonomyVocabulary taxonomyVocabulary3 =
			testGetAssetLibraryTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				assetLibraryId, randomTaxonomyVocabulary());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<TaxonomyVocabulary> page1 =
				taxonomyVocabularyResource.
					getAssetLibraryTaxonomyVocabulariesPage(
						assetLibraryId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				taxonomyVocabulary1,
				(List<TaxonomyVocabulary>)page1.getItems());

			Page<TaxonomyVocabulary> page2 =
				taxonomyVocabularyResource.
					getAssetLibraryTaxonomyVocabulariesPage(
						assetLibraryId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				taxonomyVocabulary2,
				(List<TaxonomyVocabulary>)page2.getItems());

			Page<TaxonomyVocabulary> page3 =
				taxonomyVocabularyResource.
					getAssetLibraryTaxonomyVocabulariesPage(
						assetLibraryId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				taxonomyVocabulary3,
				(List<TaxonomyVocabulary>)page3.getItems());
		}
		else {
			Page<TaxonomyVocabulary> page1 =
				taxonomyVocabularyResource.
					getAssetLibraryTaxonomyVocabulariesPage(
						assetLibraryId, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<TaxonomyVocabulary> taxonomyVocabularies1 =
				(List<TaxonomyVocabulary>)page1.getItems();

			Assert.assertEquals(
				taxonomyVocabularies1.toString(), totalCount + 2,
				taxonomyVocabularies1.size());

			Page<TaxonomyVocabulary> page2 =
				taxonomyVocabularyResource.
					getAssetLibraryTaxonomyVocabulariesPage(
						assetLibraryId, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<TaxonomyVocabulary> taxonomyVocabularies2 =
				(List<TaxonomyVocabulary>)page2.getItems();

			Assert.assertEquals(
				taxonomyVocabularies2.toString(), 1,
				taxonomyVocabularies2.size());

			Page<TaxonomyVocabulary> page3 =
				taxonomyVocabularyResource.
					getAssetLibraryTaxonomyVocabulariesPage(
						assetLibraryId, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				taxonomyVocabulary1,
				(List<TaxonomyVocabulary>)page3.getItems());
			assertContains(
				taxonomyVocabulary2,
				(List<TaxonomyVocabulary>)page3.getItems());
			assertContains(
				taxonomyVocabulary3,
				(List<TaxonomyVocabulary>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryTaxonomyVocabulariesPageWithSortDateTime()
		throws Exception {

		testGetAssetLibraryTaxonomyVocabulariesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, taxonomyVocabulary1, taxonomyVocabulary2) -> {
				BeanTestUtil.setProperty(
					taxonomyVocabulary1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetLibraryTaxonomyVocabulariesPageWithSortDouble()
		throws Exception {

		testGetAssetLibraryTaxonomyVocabulariesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, taxonomyVocabulary1, taxonomyVocabulary2) -> {
				BeanTestUtil.setProperty(
					taxonomyVocabulary1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					taxonomyVocabulary2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetLibraryTaxonomyVocabulariesPageWithSortInteger()
		throws Exception {

		testGetAssetLibraryTaxonomyVocabulariesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, taxonomyVocabulary1, taxonomyVocabulary2) -> {
				BeanTestUtil.setProperty(
					taxonomyVocabulary1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					taxonomyVocabulary2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetLibraryTaxonomyVocabulariesPageWithSortString()
		throws Exception {

		testGetAssetLibraryTaxonomyVocabulariesPageWithSort(
			EntityField.Type.STRING,
			(entityField, taxonomyVocabulary1, taxonomyVocabulary2) -> {
				Class<?> clazz = taxonomyVocabulary1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						taxonomyVocabulary1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						taxonomyVocabulary2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						taxonomyVocabulary1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						taxonomyVocabulary2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						taxonomyVocabulary1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						taxonomyVocabulary2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAssetLibraryTaxonomyVocabulariesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, TaxonomyVocabulary, TaxonomyVocabulary, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryTaxonomyVocabulariesPage_getAssetLibraryId();

		TaxonomyVocabulary taxonomyVocabulary1 = randomTaxonomyVocabulary();
		TaxonomyVocabulary taxonomyVocabulary2 = randomTaxonomyVocabulary();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, taxonomyVocabulary1, taxonomyVocabulary2);
		}

		taxonomyVocabulary1 =
			testGetAssetLibraryTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				assetLibraryId, taxonomyVocabulary1);

		taxonomyVocabulary2 =
			testGetAssetLibraryTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				assetLibraryId, taxonomyVocabulary2);

		Page<TaxonomyVocabulary> page =
			taxonomyVocabularyResource.getAssetLibraryTaxonomyVocabulariesPage(
				assetLibraryId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<TaxonomyVocabulary> ascPage =
				taxonomyVocabularyResource.
					getAssetLibraryTaxonomyVocabulariesPage(
						assetLibraryId, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				taxonomyVocabulary1,
				(List<TaxonomyVocabulary>)ascPage.getItems());
			assertContains(
				taxonomyVocabulary2,
				(List<TaxonomyVocabulary>)ascPage.getItems());

			Page<TaxonomyVocabulary> descPage =
				taxonomyVocabularyResource.
					getAssetLibraryTaxonomyVocabulariesPage(
						assetLibraryId, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				taxonomyVocabulary2,
				(List<TaxonomyVocabulary>)descPage.getItems());
			assertContains(
				taxonomyVocabulary1,
				(List<TaxonomyVocabulary>)descPage.getItems());
		}
	}

	protected TaxonomyVocabulary
			testGetAssetLibraryTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				Long assetLibraryId, TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		return taxonomyVocabularyResource.postAssetLibraryTaxonomyVocabulary(
			assetLibraryId, taxonomyVocabulary);
	}

	protected Long
			testGetAssetLibraryTaxonomyVocabulariesPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Long
			testGetAssetLibraryTaxonomyVocabulariesPage_getIrrelevantAssetLibraryId()
		throws Exception {

		return irrelevantDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLGetAssetLibraryTaxonomyVocabulariesPage()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryTaxonomyVocabulariesPage_getAssetLibraryId();

		GraphQLField graphQLField = new GraphQLField(
			"assetLibraryTaxonomyVocabularies",
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

		JSONObject assetLibraryTaxonomyVocabulariesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryTaxonomyVocabularies");

		long totalCount = assetLibraryTaxonomyVocabulariesJSONObject.getLong(
			"totalCount");

		TaxonomyVocabulary taxonomyVocabulary1 =
			testGraphQLAssetLibraryTaxonomyVocabulary_addTaxonomyVocabulary(
				assetLibraryId, randomTaxonomyVocabulary());

		TaxonomyVocabulary taxonomyVocabulary2 =
			testGraphQLAssetLibraryTaxonomyVocabulary_addTaxonomyVocabulary(
				assetLibraryId, randomTaxonomyVocabulary());

		assetLibraryTaxonomyVocabulariesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryTaxonomyVocabularies");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryTaxonomyVocabulariesJSONObject.getLong("totalCount"));

		assertContains(
			taxonomyVocabulary1,
			Arrays.asList(
				TaxonomyVocabularySerDes.toDTOs(
					assetLibraryTaxonomyVocabulariesJSONObject.getString(
						"items"))));
		assertContains(
			taxonomyVocabulary2,
			Arrays.asList(
				TaxonomyVocabularySerDes.toDTOs(
					assetLibraryTaxonomyVocabulariesJSONObject.getString(
						"items"))));

		// Using the namespace headlessAdminTaxonomy_v1_0

		assetLibraryTaxonomyVocabulariesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminTaxonomy_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/headlessAdminTaxonomy_v1_0",
				"JSONObject/assetLibraryTaxonomyVocabularies");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryTaxonomyVocabulariesJSONObject.getLong("totalCount"));

		assertContains(
			taxonomyVocabulary1,
			Arrays.asList(
				TaxonomyVocabularySerDes.toDTOs(
					assetLibraryTaxonomyVocabulariesJSONObject.getString(
						"items"))));
		assertContains(
			taxonomyVocabulary2,
			Arrays.asList(
				TaxonomyVocabularySerDes.toDTOs(
					assetLibraryTaxonomyVocabulariesJSONObject.getString(
						"items"))));
	}

	@Test
	public void testGetAssetLibraryTaxonomyVocabularyByExternalReferenceCode()
		throws Exception {

		TaxonomyVocabulary postTaxonomyVocabulary =
			testGetAssetLibraryTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary();

		TaxonomyVocabulary getTaxonomyVocabulary =
			taxonomyVocabularyResource.
				getAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
					testGetAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId(),
					postTaxonomyVocabulary.getExternalReferenceCode());

		assertEquals(postTaxonomyVocabulary, getTaxonomyVocabulary);
		assertValid(getTaxonomyVocabulary);

		Assert.assertNull(getTaxonomyVocabulary.getPermissions());

		getTaxonomyVocabulary =
			permissionsTaxonomyVocabularyResource.
				getAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
					testGetAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId(),
					postTaxonomyVocabulary.getExternalReferenceCode());

		Assert.assertNotNull(getTaxonomyVocabulary.getPermissions());
	}

	protected TaxonomyVocabulary
			testGetAssetLibraryTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary()
		throws Exception {

		return taxonomyVocabularyResource.postAssetLibraryTaxonomyVocabulary(
			testDepotEntry.getDepotEntryId(), randomTaxonomyVocabulary());
	}

	protected Long
			testGetAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLGetAssetLibraryTaxonomyVocabularyByExternalReferenceCode()
		throws Exception {

		TaxonomyVocabulary taxonomyVocabulary =
			testGraphQLGetAssetLibraryTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary();

		// No namespace

		Assert.assertTrue(
			equals(
				taxonomyVocabulary,
				TaxonomyVocabularySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"assetLibraryTaxonomyVocabularyByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"assetLibraryId",
											"\"" +
												testGraphQLGetAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId() +
													"\"");
										put(
											"externalReferenceCode",
											"\"" +
												taxonomyVocabulary.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/assetLibraryTaxonomyVocabularyByExternalReferenceCode"))));

		// Using the namespace headlessAdminTaxonomy_v1_0

		Assert.assertTrue(
			equals(
				taxonomyVocabulary,
				TaxonomyVocabularySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminTaxonomy_v1_0",
								new GraphQLField(
									"assetLibraryTaxonomyVocabularyByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"assetLibraryId",
												"\"" +
													testGraphQLGetAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId() +
														"\"");
											put(
												"externalReferenceCode",
												"\"" +
													taxonomyVocabulary.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminTaxonomy_v1_0",
						"Object/assetLibraryTaxonomyVocabularyByExternalReferenceCode"))));
	}

	protected Long
			testGraphQLGetAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLGetAssetLibraryTaxonomyVocabularyByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"assetLibraryTaxonomyVocabularyByExternalReferenceCode",
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

		// Using the namespace headlessAdminTaxonomy_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminTaxonomy_v1_0",
						new GraphQLField(
							"assetLibraryTaxonomyVocabularyByExternalReferenceCode",
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

	protected TaxonomyVocabulary
			testGraphQLGetAssetLibraryTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary()
		throws Exception {

		return testGraphQLAssetLibraryTaxonomyVocabulary_addTaxonomyVocabulary();
	}

	@Test
	public void testGetAssetLibraryTaxonomyVocabularyPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyVocabulary postTaxonomyVocabulary =
			testGetAssetLibraryTaxonomyVocabularyPermissionsPage_addTaxonomyVocabulary();

		Page<Permission> page =
			taxonomyVocabularyResource.
				getAssetLibraryTaxonomyVocabularyPermissionsPage(
					testDepotEntry.getDepotEntryId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected TaxonomyVocabulary
			testGetAssetLibraryTaxonomyVocabularyPermissionsPage_addTaxonomyVocabulary()
		throws Exception {

		return taxonomyVocabularyResource.postAssetLibraryTaxonomyVocabulary(
			testDepotEntry.getDepotEntryId(), randomTaxonomyVocabulary());
	}

	@Test
	public void testGraphQLGetAssetLibraryTaxonomyVocabularyPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyVocabulary postTaxonomyVocabulary =
			testGraphQLGetAssetLibraryTaxonomyVocabularyPermissionsPage_addTaxonomyVocabulary();

		GraphQLField graphQLField = new GraphQLField(
			"assetLibraryTaxonomyVocabularyPermissions",
			new HashMap<String, Object>() {
				{
					put(
						"assetLibraryId",
						"\"" +
							testGraphQLGetAssetLibraryTaxonomyVocabularyPermissionsPage_getAssetLibraryId() +
								"\"");
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject assetLibraryTaxonomyVocabularyPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryTaxonomyVocabularyPermissions");

		Assert.assertNotNull(
			assetLibraryTaxonomyVocabularyPermissionsJSONObject);
	}

	protected Long
			testGraphQLGetAssetLibraryTaxonomyVocabularyPermissionsPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected TaxonomyVocabulary
			testGraphQLGetAssetLibraryTaxonomyVocabularyPermissionsPage_addTaxonomyVocabulary()
		throws Exception {

		return testGraphQLAssetLibraryTaxonomyVocabulary_addTaxonomyVocabulary();
	}

	@Test
	public void testGetSiteTaxonomyVocabulariesPage() throws Exception {
		Long siteId = testGetSiteTaxonomyVocabulariesPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteTaxonomyVocabulariesPage_getIrrelevantSiteId();

		Page<TaxonomyVocabulary> page =
			taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
				siteId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			TaxonomyVocabulary irrelevantTaxonomyVocabulary =
				testGetSiteTaxonomyVocabulariesPage_addTaxonomyVocabulary(
					irrelevantSiteId, randomIrrelevantTaxonomyVocabulary());

			page = taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
				irrelevantSiteId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantTaxonomyVocabulary,
				(List<TaxonomyVocabulary>)page.getItems());
			assertValid(
				page,
				testGetSiteTaxonomyVocabulariesPage_getExpectedActions(
					irrelevantSiteId));
		}

		TaxonomyVocabulary taxonomyVocabulary1 =
			testGetSiteTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				siteId, randomTaxonomyVocabulary());

		TaxonomyVocabulary taxonomyVocabulary2 =
			testGetSiteTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				siteId, randomTaxonomyVocabulary());

		page = taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
			siteId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			taxonomyVocabulary1, (List<TaxonomyVocabulary>)page.getItems());
		assertContains(
			taxonomyVocabulary2, (List<TaxonomyVocabulary>)page.getItems());
		assertValid(
			page,
			testGetSiteTaxonomyVocabulariesPage_getExpectedActions(siteId));

		for (TaxonomyVocabulary taxonomyVocabulary : page.getItems()) {
			Assert.assertNull(taxonomyVocabulary.getPermissions());
		}

		page =
			permissionsTaxonomyVocabularyResource.
				getSiteTaxonomyVocabulariesPage(
					siteId, null, null, null, Pagination.of(1, 10), null);

		for (TaxonomyVocabulary taxonomyVocabulary : page.getItems()) {
			Assert.assertNotNull(taxonomyVocabulary.getPermissions());
		}

		taxonomyVocabularyResource.deleteTaxonomyVocabulary(
			taxonomyVocabulary1.getId());

		taxonomyVocabularyResource.deleteTaxonomyVocabulary(
			taxonomyVocabulary2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteTaxonomyVocabulariesPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-vocabularies/batch".
				replace("{siteId}", String.valueOf(siteId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteTaxonomyVocabulariesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteTaxonomyVocabulariesPage_getSiteId();

		TaxonomyVocabulary taxonomyVocabulary1 = randomTaxonomyVocabulary();

		taxonomyVocabulary1 =
			testGetSiteTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				siteId, taxonomyVocabulary1);

		for (EntityField entityField : entityFields) {
			Page<TaxonomyVocabulary> page =
				taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
					siteId, null, null,
					getFilterString(
						entityField, "between", taxonomyVocabulary1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(taxonomyVocabulary1),
				(List<TaxonomyVocabulary>)page.getItems());
		}
	}

	@Test
	public void testGetSiteTaxonomyVocabulariesPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteTaxonomyVocabulariesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteTaxonomyVocabulariesPageWithFilterStringContains()
		throws Exception {

		testGetSiteTaxonomyVocabulariesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteTaxonomyVocabulariesPageWithFilterStringEquals()
		throws Exception {

		testGetSiteTaxonomyVocabulariesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteTaxonomyVocabulariesPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteTaxonomyVocabulariesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteTaxonomyVocabulariesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteTaxonomyVocabulariesPage_getSiteId();

		TaxonomyVocabulary taxonomyVocabulary1 =
			testGetSiteTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				siteId, randomTaxonomyVocabulary());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyVocabulary taxonomyVocabulary2 =
			testGetSiteTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				siteId, randomTaxonomyVocabulary());

		for (EntityField entityField : entityFields) {
			Page<TaxonomyVocabulary> page =
				taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
					siteId, null, null,
					getFilterString(entityField, operator, taxonomyVocabulary1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(taxonomyVocabulary1),
				(List<TaxonomyVocabulary>)page.getItems());
		}
	}

	@Test
	public void testGetSiteTaxonomyVocabulariesPageWithPagination()
		throws Exception {

		Long siteId = testGetSiteTaxonomyVocabulariesPage_getSiteId();

		Page<TaxonomyVocabulary> taxonomyVocabulariesPage =
			taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
				siteId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			taxonomyVocabulariesPage.getTotalCount());

		TaxonomyVocabulary taxonomyVocabulary1 =
			testGetSiteTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				siteId, randomTaxonomyVocabulary());

		TaxonomyVocabulary taxonomyVocabulary2 =
			testGetSiteTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				siteId, randomTaxonomyVocabulary());

		TaxonomyVocabulary taxonomyVocabulary3 =
			testGetSiteTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				siteId, randomTaxonomyVocabulary());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<TaxonomyVocabulary> page1 =
				taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				taxonomyVocabulary1,
				(List<TaxonomyVocabulary>)page1.getItems());

			Page<TaxonomyVocabulary> page2 =
				taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				taxonomyVocabulary2,
				(List<TaxonomyVocabulary>)page2.getItems());

			Page<TaxonomyVocabulary> page3 =
				taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				taxonomyVocabulary3,
				(List<TaxonomyVocabulary>)page3.getItems());
		}
		else {
			Page<TaxonomyVocabulary> page1 =
				taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
					siteId, null, null, null, Pagination.of(1, totalCount + 2),
					null);

			List<TaxonomyVocabulary> taxonomyVocabularies1 =
				(List<TaxonomyVocabulary>)page1.getItems();

			Assert.assertEquals(
				taxonomyVocabularies1.toString(), totalCount + 2,
				taxonomyVocabularies1.size());

			Page<TaxonomyVocabulary> page2 =
				taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
					siteId, null, null, null, Pagination.of(2, totalCount + 2),
					null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<TaxonomyVocabulary> taxonomyVocabularies2 =
				(List<TaxonomyVocabulary>)page2.getItems();

			Assert.assertEquals(
				taxonomyVocabularies2.toString(), 1,
				taxonomyVocabularies2.size());

			Page<TaxonomyVocabulary> page3 =
				taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
					siteId, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				taxonomyVocabulary1,
				(List<TaxonomyVocabulary>)page3.getItems());
			assertContains(
				taxonomyVocabulary2,
				(List<TaxonomyVocabulary>)page3.getItems());
			assertContains(
				taxonomyVocabulary3,
				(List<TaxonomyVocabulary>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteTaxonomyVocabulariesPageWithSortDateTime()
		throws Exception {

		testGetSiteTaxonomyVocabulariesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, taxonomyVocabulary1, taxonomyVocabulary2) -> {
				BeanTestUtil.setProperty(
					taxonomyVocabulary1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteTaxonomyVocabulariesPageWithSortDouble()
		throws Exception {

		testGetSiteTaxonomyVocabulariesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, taxonomyVocabulary1, taxonomyVocabulary2) -> {
				BeanTestUtil.setProperty(
					taxonomyVocabulary1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					taxonomyVocabulary2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteTaxonomyVocabulariesPageWithSortInteger()
		throws Exception {

		testGetSiteTaxonomyVocabulariesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, taxonomyVocabulary1, taxonomyVocabulary2) -> {
				BeanTestUtil.setProperty(
					taxonomyVocabulary1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					taxonomyVocabulary2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteTaxonomyVocabulariesPageWithSortString()
		throws Exception {

		testGetSiteTaxonomyVocabulariesPageWithSort(
			EntityField.Type.STRING,
			(entityField, taxonomyVocabulary1, taxonomyVocabulary2) -> {
				Class<?> clazz = taxonomyVocabulary1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						taxonomyVocabulary1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						taxonomyVocabulary2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						taxonomyVocabulary1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						taxonomyVocabulary2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						taxonomyVocabulary1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						taxonomyVocabulary2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteTaxonomyVocabulariesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, TaxonomyVocabulary, TaxonomyVocabulary, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteTaxonomyVocabulariesPage_getSiteId();

		TaxonomyVocabulary taxonomyVocabulary1 = randomTaxonomyVocabulary();
		TaxonomyVocabulary taxonomyVocabulary2 = randomTaxonomyVocabulary();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, taxonomyVocabulary1, taxonomyVocabulary2);
		}

		taxonomyVocabulary1 =
			testGetSiteTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				siteId, taxonomyVocabulary1);

		taxonomyVocabulary2 =
			testGetSiteTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				siteId, taxonomyVocabulary2);

		Page<TaxonomyVocabulary> page =
			taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
				siteId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<TaxonomyVocabulary> ascPage =
				taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
					siteId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				taxonomyVocabulary1,
				(List<TaxonomyVocabulary>)ascPage.getItems());
			assertContains(
				taxonomyVocabulary2,
				(List<TaxonomyVocabulary>)ascPage.getItems());

			Page<TaxonomyVocabulary> descPage =
				taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
					siteId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				taxonomyVocabulary2,
				(List<TaxonomyVocabulary>)descPage.getItems());
			assertContains(
				taxonomyVocabulary1,
				(List<TaxonomyVocabulary>)descPage.getItems());
		}
	}

	protected TaxonomyVocabulary
			testGetSiteTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				Long siteId, TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		return taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
			siteId, taxonomyVocabulary);
	}

	protected Long testGetSiteTaxonomyVocabulariesPage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long testGetSiteTaxonomyVocabulariesPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteTaxonomyVocabulariesPage() throws Exception {
		Long siteId = testGetSiteTaxonomyVocabulariesPage_getSiteId();

		GraphQLField graphQLField = new GraphQLField(
			"taxonomyVocabularies",
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

		JSONObject taxonomyVocabulariesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/taxonomyVocabularies");

		long totalCount = taxonomyVocabulariesJSONObject.getLong("totalCount");

		TaxonomyVocabulary taxonomyVocabulary1 =
			testGraphQLSiteTaxonomyVocabulary_addTaxonomyVocabulary(
				siteId, randomTaxonomyVocabulary());

		TaxonomyVocabulary taxonomyVocabulary2 =
			testGraphQLSiteTaxonomyVocabulary_addTaxonomyVocabulary(
				siteId, randomTaxonomyVocabulary());

		taxonomyVocabulariesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/taxonomyVocabularies");

		Assert.assertEquals(
			totalCount + 2,
			taxonomyVocabulariesJSONObject.getLong("totalCount"));

		assertContains(
			taxonomyVocabulary1,
			Arrays.asList(
				TaxonomyVocabularySerDes.toDTOs(
					taxonomyVocabulariesJSONObject.getString("items"))));
		assertContains(
			taxonomyVocabulary2,
			Arrays.asList(
				TaxonomyVocabularySerDes.toDTOs(
					taxonomyVocabulariesJSONObject.getString("items"))));

		// Using the namespace headlessAdminTaxonomy_v1_0

		taxonomyVocabulariesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessAdminTaxonomy_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessAdminTaxonomy_v1_0",
			"JSONObject/taxonomyVocabularies");

		Assert.assertEquals(
			totalCount + 2,
			taxonomyVocabulariesJSONObject.getLong("totalCount"));

		assertContains(
			taxonomyVocabulary1,
			Arrays.asList(
				TaxonomyVocabularySerDes.toDTOs(
					taxonomyVocabulariesJSONObject.getString("items"))));
		assertContains(
			taxonomyVocabulary2,
			Arrays.asList(
				TaxonomyVocabularySerDes.toDTOs(
					taxonomyVocabulariesJSONObject.getString("items"))));
	}

	@Test
	public void testGetSiteTaxonomyVocabularyByExternalReferenceCode()
		throws Exception {

		TaxonomyVocabulary postTaxonomyVocabulary =
			testGetSiteTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary();

		TaxonomyVocabulary getTaxonomyVocabulary =
			taxonomyVocabularyResource.
				getSiteTaxonomyVocabularyByExternalReferenceCode(
					postTaxonomyVocabulary.getSiteId(),
					postTaxonomyVocabulary.getExternalReferenceCode());

		assertEquals(postTaxonomyVocabulary, getTaxonomyVocabulary);
		assertValid(getTaxonomyVocabulary);

		Assert.assertNull(getTaxonomyVocabulary.getPermissions());

		getTaxonomyVocabulary =
			permissionsTaxonomyVocabularyResource.
				getSiteTaxonomyVocabularyByExternalReferenceCode(
					postTaxonomyVocabulary.getSiteId(),
					postTaxonomyVocabulary.getExternalReferenceCode());

		Assert.assertNotNull(getTaxonomyVocabulary.getPermissions());
	}

	protected TaxonomyVocabulary
			testGetSiteTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary()
		throws Exception {

		return taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
			testGroup.getGroupId(), randomTaxonomyVocabulary());
	}

	@Test
	public void testGraphQLGetSiteTaxonomyVocabularyByExternalReferenceCode()
		throws Exception {

		TaxonomyVocabulary taxonomyVocabulary =
			testGraphQLGetSiteTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary();

		// No namespace

		Assert.assertTrue(
			equals(
				taxonomyVocabulary,
				TaxonomyVocabularySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"taxonomyVocabularyByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												taxonomyVocabulary.getSiteId() +
													"\"");
										put(
											"externalReferenceCode",
											"\"" +
												taxonomyVocabulary.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/taxonomyVocabularyByExternalReferenceCode"))));

		// Using the namespace headlessAdminTaxonomy_v1_0

		Assert.assertTrue(
			equals(
				taxonomyVocabulary,
				TaxonomyVocabularySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminTaxonomy_v1_0",
								new GraphQLField(
									"taxonomyVocabularyByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													taxonomyVocabulary.
														getSiteId() + "\"");
											put(
												"externalReferenceCode",
												"\"" +
													taxonomyVocabulary.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminTaxonomy_v1_0",
						"Object/taxonomyVocabularyByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSiteTaxonomyVocabularyByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"taxonomyVocabularyByExternalReferenceCode",
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

		// Using the namespace headlessAdminTaxonomy_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminTaxonomy_v1_0",
						new GraphQLField(
							"taxonomyVocabularyByExternalReferenceCode",
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

	protected TaxonomyVocabulary
			testGraphQLGetSiteTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary()
		throws Exception {

		return testGraphQLSiteTaxonomyVocabulary_addTaxonomyVocabulary();
	}

	@Test
	public void testGetSiteTaxonomyVocabularyPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyVocabulary postTaxonomyVocabulary =
			testGetSiteTaxonomyVocabularyPermissionsPage_addTaxonomyVocabulary();

		Page<Permission> page =
			taxonomyVocabularyResource.getSiteTaxonomyVocabularyPermissionsPage(
				testGroup.getGroupId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected TaxonomyVocabulary
			testGetSiteTaxonomyVocabularyPermissionsPage_addTaxonomyVocabulary()
		throws Exception {

		return taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
			testGroup.getGroupId(), randomTaxonomyVocabulary());
	}

	@Test
	public void testGraphQLGetSiteTaxonomyVocabularyPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyVocabulary postTaxonomyVocabulary =
			testGraphQLGetSiteTaxonomyVocabularyPermissionsPage_addTaxonomyVocabulary();

		GraphQLField graphQLField = new GraphQLField(
			"siteTaxonomyVocabularyPermissions",
			new HashMap<String, Object>() {
				{
					put(
						"siteKey",
						"\"" + postTaxonomyVocabulary.getSiteId() + "\"");
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject siteTaxonomyVocabularyPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/siteTaxonomyVocabularyPermissions");

		Assert.assertNotNull(siteTaxonomyVocabularyPermissionsJSONObject);
	}

	protected TaxonomyVocabulary
			testGraphQLGetSiteTaxonomyVocabularyPermissionsPage_addTaxonomyVocabulary()
		throws Exception {

		return testGraphQLSiteTaxonomyVocabulary_addTaxonomyVocabulary();
	}

	@Test
	public void testGetTaxonomyVocabulary() throws Exception {
		TaxonomyVocabulary postTaxonomyVocabulary =
			testGetTaxonomyVocabulary_addTaxonomyVocabulary();

		TaxonomyVocabulary getTaxonomyVocabulary =
			taxonomyVocabularyResource.getTaxonomyVocabulary(
				postTaxonomyVocabulary.getId());

		assertEquals(postTaxonomyVocabulary, getTaxonomyVocabulary);
		assertValid(getTaxonomyVocabulary);

		Assert.assertNull(getTaxonomyVocabulary.getPermissions());

		getTaxonomyVocabulary =
			permissionsTaxonomyVocabularyResource.getTaxonomyVocabulary(
				postTaxonomyVocabulary.getId());

		Assert.assertNotNull(getTaxonomyVocabulary.getPermissions());
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		TaxonomyVocabulary postTaxonomyVocabulary =
			testGetTaxonomyVocabulary_addTaxonomyVocabulary();

		TaxonomyVocabulary getTaxonomyVocabulary =
			taxonomyVocabularyResource.getTaxonomyVocabulary(
				postTaxonomyVocabulary.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyVocabulary"
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
			postTaxonomyVocabulary.getId());

		assertEquals(
			getTaxonomyVocabulary,
			TaxonomyVocabularySerDes.toDTO(item.toString()));
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

	protected TaxonomyVocabulary
			testGetTaxonomyVocabulary_addTaxonomyVocabulary()
		throws Exception {

		return taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
			testGroup.getGroupId(), randomTaxonomyVocabulary());
	}

	@Test
	public void testGraphQLGetTaxonomyVocabulary() throws Exception {
		TaxonomyVocabulary taxonomyVocabulary =
			testGraphQLGetTaxonomyVocabulary_addTaxonomyVocabulary();

		// No namespace

		Assert.assertTrue(
			equals(
				taxonomyVocabulary,
				TaxonomyVocabularySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"taxonomyVocabulary",
								new HashMap<String, Object>() {
									{
										put(
											"taxonomyVocabularyId",
											taxonomyVocabulary.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/taxonomyVocabulary"))));

		// Using the namespace headlessAdminTaxonomy_v1_0

		Assert.assertTrue(
			equals(
				taxonomyVocabulary,
				TaxonomyVocabularySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminTaxonomy_v1_0",
								new GraphQLField(
									"taxonomyVocabulary",
									new HashMap<String, Object>() {
										{
											put(
												"taxonomyVocabularyId",
												taxonomyVocabulary.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminTaxonomy_v1_0",
						"Object/taxonomyVocabulary"))));
	}

	@Test
	public void testGraphQLGetTaxonomyVocabularyNotFound() throws Exception {
		Long irrelevantTaxonomyVocabularyId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"taxonomyVocabulary",
						new HashMap<String, Object>() {
							{
								put(
									"taxonomyVocabularyId",
									irrelevantTaxonomyVocabularyId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminTaxonomy_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminTaxonomy_v1_0",
						new GraphQLField(
							"taxonomyVocabulary",
							new HashMap<String, Object>() {
								{
									put(
										"taxonomyVocabularyId",
										irrelevantTaxonomyVocabularyId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected TaxonomyVocabulary
			testGraphQLGetTaxonomyVocabulary_addTaxonomyVocabulary()
		throws Exception {

		return testGraphQLTaxonomyVocabulary_addTaxonomyVocabulary();
	}

	@Test
	public void testGetTaxonomyVocabularyPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyVocabulary postTaxonomyVocabulary =
			testGetTaxonomyVocabularyPermissionsPage_addTaxonomyVocabulary();

		Page<Permission> page =
			taxonomyVocabularyResource.getTaxonomyVocabularyPermissionsPage(
				postTaxonomyVocabulary.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected TaxonomyVocabulary
			testGetTaxonomyVocabularyPermissionsPage_addTaxonomyVocabulary()
		throws Exception {

		return taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
			testGroup.getGroupId(), randomTaxonomyVocabulary());
	}

	@Test
	public void testGraphQLGetTaxonomyVocabularyPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyVocabulary postTaxonomyVocabulary =
			testGraphQLGetTaxonomyVocabularyPermissionsPage_addTaxonomyVocabulary();

		GraphQLField graphQLField = new GraphQLField(
			"taxonomyVocabularyPermissions",
			new HashMap<String, Object>() {
				{
					put("taxonomyVocabularyId", postTaxonomyVocabulary.getId());
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject taxonomyVocabularyPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/taxonomyVocabularyPermissions");

		Assert.assertNotNull(taxonomyVocabularyPermissionsJSONObject);
	}

	protected TaxonomyVocabulary
			testGraphQLGetTaxonomyVocabularyPermissionsPage_addTaxonomyVocabulary()
		throws Exception {

		return testGraphQLTaxonomyVocabulary_addTaxonomyVocabulary();
	}

	@Test
	public void testPatchTaxonomyVocabulary() throws Exception {
		TaxonomyVocabulary postTaxonomyVocabulary =
			testPatchTaxonomyVocabulary_addTaxonomyVocabulary();

		TaxonomyVocabulary randomPatchTaxonomyVocabulary =
			randomPatchTaxonomyVocabulary();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyVocabulary patchTaxonomyVocabulary =
			taxonomyVocabularyResource.patchTaxonomyVocabulary(
				postTaxonomyVocabulary.getId(), randomPatchTaxonomyVocabulary);

		TaxonomyVocabulary expectedPatchTaxonomyVocabulary =
			postTaxonomyVocabulary.clone();

		BeanTestUtil.copyProperties(
			randomPatchTaxonomyVocabulary, expectedPatchTaxonomyVocabulary);

		TaxonomyVocabulary getTaxonomyVocabulary =
			taxonomyVocabularyResource.getTaxonomyVocabulary(
				patchTaxonomyVocabulary.getId());

		assertEquals(expectedPatchTaxonomyVocabulary, getTaxonomyVocabulary);
		assertValid(getTaxonomyVocabulary);
	}

	protected TaxonomyVocabulary
			testPatchTaxonomyVocabulary_addTaxonomyVocabulary()
		throws Exception {

		return taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
			testGroup.getGroupId(), randomTaxonomyVocabulary());
	}

	@Test
	public void testPostAssetLibraryTaxonomyVocabulary() throws Exception {
		TaxonomyVocabulary randomTaxonomyVocabulary =
			randomTaxonomyVocabulary();

		TaxonomyVocabulary postTaxonomyVocabulary =
			testPostAssetLibraryTaxonomyVocabulary_addTaxonomyVocabulary(
				randomTaxonomyVocabulary);

		assertEquals(randomTaxonomyVocabulary, postTaxonomyVocabulary);
		assertValid(postTaxonomyVocabulary);

		TaxonomyVocabulary randomPermissionsTaxonomyVocabulary1 =
			randomPermissionsTaxonomyVocabulary();

		TaxonomyVocabulary postPermissionsTaxonomyVocabulary1 =
			testPostAssetLibraryTaxonomyVocabulary_addTaxonomyVocabulary(
				randomPermissionsTaxonomyVocabulary1);

		Assert.assertNull(postPermissionsTaxonomyVocabulary1.getPermissions());

		TaxonomyVocabulary randomPermissionsTaxonomyVocabulary2 =
			randomPermissionsTaxonomyVocabulary();

		TaxonomyVocabulary postPermissionsTaxonomyVocabulary2 =
			testPostAssetLibraryTaxonomyVocabulary_addPermissionsTaxonomyVocabulary(
				randomPermissionsTaxonomyVocabulary2);

		Assert.assertNotNull(
			postPermissionsTaxonomyVocabulary2.getPermissions());
	}

	protected TaxonomyVocabulary
			testPostAssetLibraryTaxonomyVocabulary_addTaxonomyVocabulary(
				TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		return taxonomyVocabularyResource.postAssetLibraryTaxonomyVocabulary(
			testGetAssetLibraryTaxonomyVocabulariesPage_getAssetLibraryId(),
			taxonomyVocabulary);
	}

	protected TaxonomyVocabulary
			testPostAssetLibraryTaxonomyVocabulary_addPermissionsTaxonomyVocabulary(
				TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		return permissionsTaxonomyVocabularyResource.
			postAssetLibraryTaxonomyVocabulary(
				testGetAssetLibraryTaxonomyVocabulariesPage_getAssetLibraryId(),
				taxonomyVocabulary);
	}

	@Test
	public void testGraphQLPostAssetLibraryTaxonomyVocabulary()
		throws Exception {

		TaxonomyVocabulary randomTaxonomyVocabulary =
			randomTaxonomyVocabulary();

		TaxonomyVocabulary taxonomyVocabulary =
			testGraphQLAssetLibraryTaxonomyVocabulary_addTaxonomyVocabulary(
				testDepotEntry.getDepotEntryId(), randomTaxonomyVocabulary);

		Assert.assertTrue(equals(randomTaxonomyVocabulary, taxonomyVocabulary));
	}

	@Test
	public void testPostSiteTaxonomyVocabulary() throws Exception {
		TaxonomyVocabulary randomTaxonomyVocabulary =
			randomTaxonomyVocabulary();

		TaxonomyVocabulary postTaxonomyVocabulary =
			testPostSiteTaxonomyVocabulary_addTaxonomyVocabulary(
				randomTaxonomyVocabulary);

		assertEquals(randomTaxonomyVocabulary, postTaxonomyVocabulary);
		assertValid(postTaxonomyVocabulary);

		TaxonomyVocabulary randomPermissionsTaxonomyVocabulary1 =
			randomPermissionsTaxonomyVocabulary();

		TaxonomyVocabulary postPermissionsTaxonomyVocabulary1 =
			testPostSiteTaxonomyVocabulary_addTaxonomyVocabulary(
				randomPermissionsTaxonomyVocabulary1);

		Assert.assertNull(postPermissionsTaxonomyVocabulary1.getPermissions());

		TaxonomyVocabulary randomPermissionsTaxonomyVocabulary2 =
			randomPermissionsTaxonomyVocabulary();

		TaxonomyVocabulary postPermissionsTaxonomyVocabulary2 =
			testPostSiteTaxonomyVocabulary_addPermissionsTaxonomyVocabulary(
				randomPermissionsTaxonomyVocabulary2);

		Assert.assertNotNull(
			postPermissionsTaxonomyVocabulary2.getPermissions());
	}

	protected TaxonomyVocabulary
			testPostSiteTaxonomyVocabulary_addTaxonomyVocabulary(
				TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		return taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
			testGetSiteTaxonomyVocabulariesPage_getSiteId(),
			taxonomyVocabulary);
	}

	protected TaxonomyVocabulary
			testPostSiteTaxonomyVocabulary_addPermissionsTaxonomyVocabulary(
				TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		return permissionsTaxonomyVocabularyResource.postSiteTaxonomyVocabulary(
			testGetSiteTaxonomyVocabulariesPage_getSiteId(),
			taxonomyVocabulary);
	}

	@Test
	public void testGraphQLPostSiteTaxonomyVocabulary() throws Exception {
		TaxonomyVocabulary randomTaxonomyVocabulary =
			randomTaxonomyVocabulary();

		TaxonomyVocabulary taxonomyVocabulary =
			testGraphQLSiteTaxonomyVocabulary_addTaxonomyVocabulary(
				testGroup.getGroupId(), randomTaxonomyVocabulary);

		Assert.assertTrue(equals(randomTaxonomyVocabulary, taxonomyVocabulary));
	}

	@Test
	public void testPutAssetLibraryTaxonomyVocabularyByExternalReferenceCode()
		throws Exception {

		TaxonomyVocabulary postTaxonomyVocabulary =
			testPutAssetLibraryTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary();

		TaxonomyVocabulary randomTaxonomyVocabulary =
			randomTaxonomyVocabulary();

		TaxonomyVocabulary putTaxonomyVocabulary =
			taxonomyVocabularyResource.
				putAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
					testPutAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId(),
					postTaxonomyVocabulary.getExternalReferenceCode(),
					randomTaxonomyVocabulary);

		assertEquals(randomTaxonomyVocabulary, putTaxonomyVocabulary);
		assertValid(putTaxonomyVocabulary);

		Assert.assertNull(putTaxonomyVocabulary.getPermissions());

		TaxonomyVocabulary getTaxonomyVocabulary =
			taxonomyVocabularyResource.
				getAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
					testPutAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId(),
					putTaxonomyVocabulary.getExternalReferenceCode());

		assertEquals(randomTaxonomyVocabulary, getTaxonomyVocabulary);
		assertValid(getTaxonomyVocabulary);

		TaxonomyVocabulary randomPermissionsTaxonomyVocabulary =
			randomPermissionsTaxonomyVocabulary();

		putTaxonomyVocabulary =
			taxonomyVocabularyResource.
				putAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
					testPutAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId(),
					postTaxonomyVocabulary.getExternalReferenceCode(),
					randomPermissionsTaxonomyVocabulary);

		assertEquals(
			randomPermissionsTaxonomyVocabulary, putTaxonomyVocabulary);
		assertValid(putTaxonomyVocabulary);

		Assert.assertNull(putTaxonomyVocabulary.getPermissions());

		putTaxonomyVocabulary =
			permissionsTaxonomyVocabularyResource.
				putAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
					testPutAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId(),
					postTaxonomyVocabulary.getExternalReferenceCode(),
					randomPermissionsTaxonomyVocabulary);

		Assert.assertNotNull(putTaxonomyVocabulary.getPermissions());

		TaxonomyVocabulary newTaxonomyVocabulary =
			testPutAssetLibraryTaxonomyVocabularyByExternalReferenceCode_createTaxonomyVocabulary();

		putTaxonomyVocabulary =
			taxonomyVocabularyResource.
				putAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
					testPutAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId(),
					newTaxonomyVocabulary.getExternalReferenceCode(),
					newTaxonomyVocabulary);

		assertEquals(newTaxonomyVocabulary, putTaxonomyVocabulary);
		assertValid(putTaxonomyVocabulary);

		getTaxonomyVocabulary =
			taxonomyVocabularyResource.
				getAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
					testPutAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId(),
					putTaxonomyVocabulary.getExternalReferenceCode());

		assertEquals(newTaxonomyVocabulary, getTaxonomyVocabulary);

		Assert.assertEquals(
			newTaxonomyVocabulary.getExternalReferenceCode(),
			putTaxonomyVocabulary.getExternalReferenceCode());
	}

	protected TaxonomyVocabulary
			testPutAssetLibraryTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary()
		throws Exception {

		return taxonomyVocabularyResource.postAssetLibraryTaxonomyVocabulary(
			testDepotEntry.getDepotEntryId(), randomTaxonomyVocabulary());
	}

	protected Long
			testPutAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected TaxonomyVocabulary
			testPutAssetLibraryTaxonomyVocabularyByExternalReferenceCode_createTaxonomyVocabulary()
		throws Exception {

		return randomTaxonomyVocabulary();
	}

	@Test
	public void testPutAssetLibraryTaxonomyVocabularyPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyVocabulary taxonomyVocabulary =
			testPutAssetLibraryTaxonomyVocabularyPermissionsPage_addTaxonomyVocabulary();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			taxonomyVocabularyResource.
				putAssetLibraryTaxonomyVocabularyPermissionsPageHttpResponse(
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
			taxonomyVocabularyResource.
				putAssetLibraryTaxonomyVocabularyPermissionsPageHttpResponse(
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

	protected TaxonomyVocabulary
			testPutAssetLibraryTaxonomyVocabularyPermissionsPage_addTaxonomyVocabulary()
		throws Exception {

		return taxonomyVocabularyResource.postAssetLibraryTaxonomyVocabulary(
			testDepotEntry.getDepotEntryId(), randomTaxonomyVocabulary());
	}

	@Test
	public void testPutSiteTaxonomyVocabularyByExternalReferenceCode()
		throws Exception {

		TaxonomyVocabulary postTaxonomyVocabulary =
			testPutSiteTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary();

		TaxonomyVocabulary randomTaxonomyVocabulary =
			randomTaxonomyVocabulary();

		TaxonomyVocabulary putTaxonomyVocabulary =
			taxonomyVocabularyResource.
				putSiteTaxonomyVocabularyByExternalReferenceCode(
					postTaxonomyVocabulary.getSiteId(),
					postTaxonomyVocabulary.getExternalReferenceCode(),
					randomTaxonomyVocabulary);

		assertEquals(randomTaxonomyVocabulary, putTaxonomyVocabulary);
		assertValid(putTaxonomyVocabulary);

		Assert.assertNull(putTaxonomyVocabulary.getPermissions());

		TaxonomyVocabulary getTaxonomyVocabulary =
			taxonomyVocabularyResource.
				getSiteTaxonomyVocabularyByExternalReferenceCode(
					putTaxonomyVocabulary.getSiteId(),
					putTaxonomyVocabulary.getExternalReferenceCode());

		assertEquals(randomTaxonomyVocabulary, getTaxonomyVocabulary);
		assertValid(getTaxonomyVocabulary);

		TaxonomyVocabulary randomPermissionsTaxonomyVocabulary =
			randomPermissionsTaxonomyVocabulary();

		putTaxonomyVocabulary =
			taxonomyVocabularyResource.
				putSiteTaxonomyVocabularyByExternalReferenceCode(
					postTaxonomyVocabulary.getSiteId(),
					postTaxonomyVocabulary.getExternalReferenceCode(),
					randomPermissionsTaxonomyVocabulary);

		assertEquals(
			randomPermissionsTaxonomyVocabulary, putTaxonomyVocabulary);
		assertValid(putTaxonomyVocabulary);

		Assert.assertNull(putTaxonomyVocabulary.getPermissions());

		putTaxonomyVocabulary =
			permissionsTaxonomyVocabularyResource.
				putSiteTaxonomyVocabularyByExternalReferenceCode(
					postTaxonomyVocabulary.getSiteId(),
					postTaxonomyVocabulary.getExternalReferenceCode(),
					randomPermissionsTaxonomyVocabulary);

		Assert.assertNotNull(putTaxonomyVocabulary.getPermissions());

		TaxonomyVocabulary newTaxonomyVocabulary =
			testPutSiteTaxonomyVocabularyByExternalReferenceCode_createTaxonomyVocabulary();

		putTaxonomyVocabulary =
			taxonomyVocabularyResource.
				putSiteTaxonomyVocabularyByExternalReferenceCode(
					newTaxonomyVocabulary.getSiteId(),
					newTaxonomyVocabulary.getExternalReferenceCode(),
					newTaxonomyVocabulary);

		assertEquals(newTaxonomyVocabulary, putTaxonomyVocabulary);
		assertValid(putTaxonomyVocabulary);

		getTaxonomyVocabulary =
			taxonomyVocabularyResource.
				getSiteTaxonomyVocabularyByExternalReferenceCode(
					putTaxonomyVocabulary.getSiteId(),
					putTaxonomyVocabulary.getExternalReferenceCode());

		assertEquals(newTaxonomyVocabulary, getTaxonomyVocabulary);

		Assert.assertEquals(
			newTaxonomyVocabulary.getExternalReferenceCode(),
			putTaxonomyVocabulary.getExternalReferenceCode());
	}

	protected TaxonomyVocabulary
			testPutSiteTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary()
		throws Exception {

		return taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
			testGroup.getGroupId(), randomTaxonomyVocabulary());
	}

	protected TaxonomyVocabulary
			testPutSiteTaxonomyVocabularyByExternalReferenceCode_createTaxonomyVocabulary()
		throws Exception {

		return randomTaxonomyVocabulary();
	}

	@Test
	public void testPutSiteTaxonomyVocabularyPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyVocabulary taxonomyVocabulary =
			testPutSiteTaxonomyVocabularyPermissionsPage_addTaxonomyVocabulary();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			taxonomyVocabularyResource.
				putSiteTaxonomyVocabularyPermissionsPageHttpResponse(
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
			taxonomyVocabularyResource.
				putSiteTaxonomyVocabularyPermissionsPageHttpResponse(
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

	protected TaxonomyVocabulary
			testPutSiteTaxonomyVocabularyPermissionsPage_addTaxonomyVocabulary()
		throws Exception {

		return taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
			testGroup.getGroupId(), randomTaxonomyVocabulary());
	}

	@Test
	public void testPutTaxonomyVocabulary() throws Exception {
		TaxonomyVocabulary postTaxonomyVocabulary =
			testPutTaxonomyVocabulary_addTaxonomyVocabulary();

		TaxonomyVocabulary randomTaxonomyVocabulary =
			randomTaxonomyVocabulary();

		TaxonomyVocabulary putTaxonomyVocabulary =
			taxonomyVocabularyResource.putTaxonomyVocabulary(
				postTaxonomyVocabulary.getId(), randomTaxonomyVocabulary);

		assertEquals(randomTaxonomyVocabulary, putTaxonomyVocabulary);
		assertValid(putTaxonomyVocabulary);

		Assert.assertNull(putTaxonomyVocabulary.getPermissions());

		TaxonomyVocabulary getTaxonomyVocabulary =
			taxonomyVocabularyResource.getTaxonomyVocabulary(
				putTaxonomyVocabulary.getId());

		assertEquals(randomTaxonomyVocabulary, getTaxonomyVocabulary);
		assertValid(getTaxonomyVocabulary);

		TaxonomyVocabulary randomPermissionsTaxonomyVocabulary =
			randomPermissionsTaxonomyVocabulary();

		putTaxonomyVocabulary =
			taxonomyVocabularyResource.putTaxonomyVocabulary(
				postTaxonomyVocabulary.getId(),
				randomPermissionsTaxonomyVocabulary);

		assertEquals(
			randomPermissionsTaxonomyVocabulary, putTaxonomyVocabulary);
		assertValid(putTaxonomyVocabulary);

		Assert.assertNull(putTaxonomyVocabulary.getPermissions());

		putTaxonomyVocabulary =
			permissionsTaxonomyVocabularyResource.putTaxonomyVocabulary(
				postTaxonomyVocabulary.getId(),
				randomPermissionsTaxonomyVocabulary);

		Assert.assertNotNull(putTaxonomyVocabulary.getPermissions());
	}

	protected TaxonomyVocabulary
			testPutTaxonomyVocabulary_addTaxonomyVocabulary()
		throws Exception {

		return taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
			testGroup.getGroupId(), randomTaxonomyVocabulary());
	}

	@Test
	public void testPutTaxonomyVocabularyPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaxonomyVocabulary taxonomyVocabulary =
			testPutTaxonomyVocabularyPermissionsPage_addTaxonomyVocabulary();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			taxonomyVocabularyResource.
				putTaxonomyVocabularyPermissionsPageHttpResponse(
					taxonomyVocabulary.getId(),
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
			taxonomyVocabularyResource.
				putTaxonomyVocabularyPermissionsPageHttpResponse(
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

	protected TaxonomyVocabulary
			testPutTaxonomyVocabularyPermissionsPage_addTaxonomyVocabulary()
		throws Exception {

		return taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
			testGroup.getGroupId(), randomTaxonomyVocabulary());
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		TaxonomyVocabulary taxonomyVocabulary1 =
			testBatchEngineDeleteImportTask_addTaxonomyVocabulary();

		testBatchEngineDeleteImportTask_deleteTaxonomyVocabulary(
			200, null, taxonomyVocabulary1.getId());

		assertHttpResponseStatusCode(
			404,
			taxonomyVocabularyResource.getTaxonomyVocabularyHttpResponse(
				taxonomyVocabulary1.getId()));
	}

	protected TaxonomyVocabulary
			testBatchEngineDeleteImportTask_addTaxonomyVocabulary()
		throws Exception {

		return testDeleteTaxonomyVocabulary_addTaxonomyVocabulary();
	}

	protected void testBatchEngineDeleteImportTask_deleteTaxonomyVocabulary(
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
				"com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyVocabulary",
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

	protected TaxonomyVocabulary
			testGraphQLAssetLibraryTaxonomyVocabulary_addTaxonomyVocabulary()
		throws Exception {

		return testGraphQLAssetLibraryTaxonomyVocabulary_addTaxonomyVocabulary(
			testDepotEntry.getDepotEntryId(), randomTaxonomyVocabulary());
	}

	protected TaxonomyVocabulary
			testGraphQLAssetLibraryTaxonomyVocabulary_addTaxonomyVocabulary(
				Long assetLibraryId, TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		JSONDeserializer<TaxonomyVocabulary> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(TaxonomyVocabulary.class)) {

			if (getGraphQLValue(field.get(taxonomyVocabulary)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(taxonomyVocabulary)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createAssetLibraryTaxonomyVocabulary",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryId",
									"\"" + assetLibraryId + "\"");
								put("taxonomyVocabulary", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createAssetLibraryTaxonomyVocabulary"),
			TaxonomyVocabulary.class);
	}

	protected TaxonomyVocabulary
			testGraphQLSiteTaxonomyVocabulary_addTaxonomyVocabulary()
		throws Exception {

		return testGraphQLSiteTaxonomyVocabulary_addTaxonomyVocabulary(
			testGroup.getGroupId(), randomTaxonomyVocabulary());
	}

	protected TaxonomyVocabulary
			testGraphQLSiteTaxonomyVocabulary_addTaxonomyVocabulary(
				Long siteId, TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		JSONDeserializer<TaxonomyVocabulary> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(TaxonomyVocabulary.class)) {

			if (getGraphQLValue(field.get(taxonomyVocabulary)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(taxonomyVocabulary)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteTaxonomyVocabulary",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("taxonomyVocabulary", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteTaxonomyVocabulary"),
			TaxonomyVocabulary.class);
	}

	protected TaxonomyVocabulary
			testGraphQLTaxonomyVocabulary_addTaxonomyVocabulary()
		throws Exception {

		return testGraphQLTaxonomyVocabulary_addTaxonomyVocabulary(
			testGroup.getGroupId(), randomTaxonomyVocabulary());
	}

	protected TaxonomyVocabulary
			testGraphQLTaxonomyVocabulary_addTaxonomyVocabulary(
				Long siteId, TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		JSONDeserializer<TaxonomyVocabulary> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(TaxonomyVocabulary.class)) {

			if (getGraphQLValue(field.get(taxonomyVocabulary)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(taxonomyVocabulary)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteTaxonomyVocabulary",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("taxonomyVocabulary", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteTaxonomyVocabulary"),
			TaxonomyVocabulary.class);
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
		TaxonomyVocabulary taxonomyVocabulary,
		List<TaxonomyVocabulary> taxonomyVocabularies) {

		boolean contains = false;

		for (TaxonomyVocabulary item : taxonomyVocabularies) {
			if (equals(taxonomyVocabulary, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			taxonomyVocabularies + " does not contain " + taxonomyVocabulary,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		TaxonomyVocabulary taxonomyVocabulary1,
		TaxonomyVocabulary taxonomyVocabulary2) {

		Assert.assertTrue(
			taxonomyVocabulary1 + " does not equal " + taxonomyVocabulary2,
			equals(taxonomyVocabulary1, taxonomyVocabulary2));
	}

	protected void assertEquals(
		List<TaxonomyVocabulary> taxonomyVocabularies1,
		List<TaxonomyVocabulary> taxonomyVocabularies2) {

		Assert.assertEquals(
			taxonomyVocabularies1.size(), taxonomyVocabularies2.size());

		for (int i = 0; i < taxonomyVocabularies1.size(); i++) {
			TaxonomyVocabulary taxonomyVocabulary1 = taxonomyVocabularies1.get(
				i);
			TaxonomyVocabulary taxonomyVocabulary2 = taxonomyVocabularies2.get(
				i);

			assertEquals(taxonomyVocabulary1, taxonomyVocabulary2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<TaxonomyVocabulary> taxonomyVocabularies1,
		List<TaxonomyVocabulary> taxonomyVocabularies2) {

		Assert.assertEquals(
			taxonomyVocabularies1.size(), taxonomyVocabularies2.size());

		for (TaxonomyVocabulary taxonomyVocabulary1 : taxonomyVocabularies1) {
			boolean contains = false;

			for (TaxonomyVocabulary taxonomyVocabulary2 :
					taxonomyVocabularies2) {

				if (equals(taxonomyVocabulary1, taxonomyVocabulary2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				taxonomyVocabularies2 + " does not contain " +
					taxonomyVocabulary1,
				contains);
		}
	}

	protected void assertValid(TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		boolean valid = true;

		if (taxonomyVocabulary.getDateCreated() == null) {
			valid = false;
		}

		if (taxonomyVocabulary.getDateModified() == null) {
			valid = false;
		}

		if (taxonomyVocabulary.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				taxonomyVocabulary.getAssetLibraryKey(),
				testDepotEntryGroup.getGroupKey()) &&
			!Objects.equals(
				taxonomyVocabulary.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (taxonomyVocabulary.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetLibraries", additionalAssertFieldName)) {
				if (taxonomyVocabulary.getAssetLibraries() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetLibraryKey", additionalAssertFieldName)) {
				if (taxonomyVocabulary.getAssetLibraryKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetTypes", additionalAssertFieldName)) {
				if (taxonomyVocabulary.getAssetTypes() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (taxonomyVocabulary.getAvailableLanguages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (taxonomyVocabulary.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (taxonomyVocabulary.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (taxonomyVocabulary.getDescription_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (taxonomyVocabulary.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("multiValued", additionalAssertFieldName)) {
				if (taxonomyVocabulary.getMultiValued() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (taxonomyVocabulary.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (taxonomyVocabulary.getName_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfTaxonomyCategories", additionalAssertFieldName)) {

				if (taxonomyVocabulary.getNumberOfTaxonomyCategories() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (taxonomyVocabulary.getPermissions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"siteExternalReferenceCode", additionalAssertFieldName)) {

				if (taxonomyVocabulary.getSiteExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (taxonomyVocabulary.getViewableBy() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("visibilityType", additionalAssertFieldName)) {
				if (taxonomyVocabulary.getVisibilityType() == null) {
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

	protected void assertValid(Page<TaxonomyVocabulary> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<TaxonomyVocabulary> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<TaxonomyVocabulary> taxonomyVocabularies =
			page.getItems();

		int size = taxonomyVocabularies.size();

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
					com.liferay.headless.admin.taxonomy.dto.v1_0.
						TaxonomyVocabulary.class)) {

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
		TaxonomyVocabulary taxonomyVocabulary1,
		TaxonomyVocabulary taxonomyVocabulary2) {

		if (taxonomyVocabulary1 == taxonomyVocabulary2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)taxonomyVocabulary1.getActions(),
						(Map)taxonomyVocabulary2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assetLibraries", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyVocabulary1.getAssetLibraries(),
						taxonomyVocabulary2.getAssetLibraries())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assetTypes", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyVocabulary1.getAssetTypes(),
						taxonomyVocabulary2.getAssetTypes())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taxonomyVocabulary1.getAvailableLanguages(),
						taxonomyVocabulary2.getAvailableLanguages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyVocabulary1.getCreator(),
						taxonomyVocabulary2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyVocabulary1.getDateCreated(),
						taxonomyVocabulary2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyVocabulary1.getDateModified(),
						taxonomyVocabulary2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyVocabulary1.getDescription(),
						taxonomyVocabulary2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)taxonomyVocabulary1.getDescription_i18n(),
						(Map)taxonomyVocabulary2.getDescription_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taxonomyVocabulary1.getExternalReferenceCode(),
						taxonomyVocabulary2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyVocabulary1.getId(),
						taxonomyVocabulary2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("multiValued", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyVocabulary1.getMultiValued(),
						taxonomyVocabulary2.getMultiValued())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyVocabulary1.getName(),
						taxonomyVocabulary2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)taxonomyVocabulary1.getName_i18n(),
						(Map)taxonomyVocabulary2.getName_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfTaxonomyCategories", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taxonomyVocabulary1.getNumberOfTaxonomyCategories(),
						taxonomyVocabulary2.getNumberOfTaxonomyCategories())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyVocabulary1.getPermissions(),
						taxonomyVocabulary2.getPermissions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"siteExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						taxonomyVocabulary1.getSiteExternalReferenceCode(),
						taxonomyVocabulary2.getSiteExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyVocabulary1.getViewableBy(),
						taxonomyVocabulary2.getViewableBy())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("visibilityType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taxonomyVocabulary1.getVisibilityType(),
						taxonomyVocabulary2.getVisibilityType())) {

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

		if (!(_taxonomyVocabularyResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_taxonomyVocabularyResource;

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
		TaxonomyVocabulary taxonomyVocabulary) {

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

		if (entityFieldName.equals("assetLibraries")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("assetLibraryKey")) {
			Object object = taxonomyVocabulary.getAssetLibraryKey();

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

		if (entityFieldName.equals("assetTypes")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("availableLanguages")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = taxonomyVocabulary.getDateCreated();

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

				sb.append(_format.format(taxonomyVocabulary.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = taxonomyVocabulary.getDateModified();

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

				sb.append(_format.format(taxonomyVocabulary.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = taxonomyVocabulary.getDescription();

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
			Object object = taxonomyVocabulary.getExternalReferenceCode();

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

		if (entityFieldName.equals("multiValued")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = taxonomyVocabulary.getName();

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

		if (entityFieldName.equals("numberOfTaxonomyCategories")) {
			sb.append(
				String.valueOf(
					taxonomyVocabulary.getNumberOfTaxonomyCategories()));

			return sb.toString();
		}

		if (entityFieldName.equals("permissions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("siteExternalReferenceCode")) {
			Object object = taxonomyVocabulary.getSiteExternalReferenceCode();

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

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("viewableBy")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("visibilityType")) {
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

	protected TaxonomyVocabulary randomTaxonomyVocabulary() throws Exception {
		return new TaxonomyVocabulary() {
			{
				assetLibraryKey = String.valueOf(
					testDepotEntry.getDepotEntryId());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				multiValued = RandomTestUtil.randomBoolean();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				numberOfTaxonomyCategories = RandomTestUtil.randomInt();
				siteExternalReferenceCode =
					testGroup.getExternalReferenceCode();
				siteId = testGroup.getGroupId();
			}
		};
	}

	protected TaxonomyVocabulary randomIrrelevantTaxonomyVocabulary()
		throws Exception {

		TaxonomyVocabulary randomIrrelevantTaxonomyVocabulary =
			randomTaxonomyVocabulary();

		randomIrrelevantTaxonomyVocabulary.setAssetLibraryKey(
			String.valueOf(irrelevantDepotEntry.getDepotEntryId()));

		randomIrrelevantTaxonomyVocabulary.setSiteExternalReferenceCode(
			irrelevantGroup.getExternalReferenceCode());

		randomIrrelevantTaxonomyVocabulary.setSiteId(
			irrelevantGroup.getGroupId());

		return randomIrrelevantTaxonomyVocabulary;
	}

	protected TaxonomyVocabulary randomPatchTaxonomyVocabulary()
		throws Exception {

		return randomTaxonomyVocabulary();
	}

	protected TaxonomyVocabulary randomPermissionsTaxonomyVocabulary()
		throws Exception {

		TaxonomyVocabulary taxonomyVocabulary = randomTaxonomyVocabulary();

		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		taxonomyVocabulary.setPermissions(
			new Permission[] {
				new Permission() {
					{
						setActionIds(new String[] {"VIEW"});
						setRoleName(role.getName());
					}
				}
			});

		return taxonomyVocabulary;
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

	protected TaxonomyVocabularyResource taxonomyVocabularyResource;
	protected ImportTaskResource importTaskResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected TaxonomyVocabularyResource permissionsTaxonomyVocabularyResource;
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
		LogFactoryUtil.getLog(BaseTaxonomyVocabularyResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.admin.taxonomy.resource.v1_0.
			TaxonomyVocabularyResource _taxonomyVocabularyResource;

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