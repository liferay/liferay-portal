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
import com.liferay.headless.delivery.client.dto.v1_0.WikiPageAttachment;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.resource.v1_0.WikiPageAttachmentResource;
import com.liferay.headless.delivery.client.serdes.v1_0.WikiPageAttachmentSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
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
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
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
public abstract class BaseWikiPageAttachmentResourceTestCase {

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

		_wikiPageAttachmentResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		wikiPageAttachmentResource = WikiPageAttachmentResource.builder(
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

		WikiPageAttachment wikiPageAttachment1 = randomWikiPageAttachment();

		String json = objectMapper.writeValueAsString(wikiPageAttachment1);

		WikiPageAttachment wikiPageAttachment2 = WikiPageAttachmentSerDes.toDTO(
			json);

		Assert.assertTrue(equals(wikiPageAttachment1, wikiPageAttachment2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		WikiPageAttachment wikiPageAttachment = randomWikiPageAttachment();

		String json1 = objectMapper.writeValueAsString(wikiPageAttachment);
		String json2 = WikiPageAttachmentSerDes.toJSON(wikiPageAttachment);

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

		WikiPageAttachment wikiPageAttachment = randomWikiPageAttachment();

		wikiPageAttachment.setContentUrl(regex);
		wikiPageAttachment.setContentValue(regex);
		wikiPageAttachment.setEncodingFormat(regex);
		wikiPageAttachment.setExternalReferenceCode(regex);
		wikiPageAttachment.setFileExtension(regex);
		wikiPageAttachment.setTitle(regex);

		String json = WikiPageAttachmentSerDes.toJSON(wikiPageAttachment);

		Assert.assertFalse(json.contains(regex));

		wikiPageAttachment = WikiPageAttachmentSerDes.toDTO(json);

		Assert.assertEquals(regex, wikiPageAttachment.getContentUrl());
		Assert.assertEquals(regex, wikiPageAttachment.getContentValue());
		Assert.assertEquals(regex, wikiPageAttachment.getEncodingFormat());
		Assert.assertEquals(
			regex, wikiPageAttachment.getExternalReferenceCode());
		Assert.assertEquals(regex, wikiPageAttachment.getFileExtension());
		Assert.assertEquals(regex, wikiPageAttachment.getTitle());
	}

	@Test
	public void testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiPageAttachment wikiPageAttachment =
			testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_addWikiPageAttachment();

		assertHttpResponseStatusCode(
			204,
			wikiPageAttachmentResource.
				deleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId(),
					testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode(),
					wikiPageAttachment.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			wikiPageAttachmentResource.
				getSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId(),
					testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode(),
					wikiPageAttachment.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			wikiPageAttachmentResource.
				getSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId(),
					testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode(),
					"-"));
	}

	protected WikiPageAttachment
			testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_addWikiPageAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode()
		throws Exception {

		// No namespace

		WikiPageAttachment wikiPageAttachment1 =
			testGraphQLDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_addWikiPageAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" +
										testGraphQLDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId() +
											"\"");

								put(
									"wikiPageExternalReferenceCode",
									"\"" +
										testGraphQLDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" +
										wikiPageAttachment1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"wikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"siteKey",
								"\"" +
									testGraphQLDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId() +
										"\"");

							put(
								"wikiPageExternalReferenceCode",
								"\"" +
									testGraphQLDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode() +
										"\"");
							put(
								"externalReferenceCode",
								"\"" +
									wikiPageAttachment1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		WikiPageAttachment wikiPageAttachment2 =
			testGraphQLDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_addWikiPageAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" +
											testGraphQLDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId() +
												"\"");

									put(
										"wikiPageExternalReferenceCode",
										"\"" +
											testGraphQLDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode() +
												"\"");
									put(
										"externalReferenceCode",
										"\"" +
											wikiPageAttachment2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"wikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" +
										testGraphQLDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId() +
											"\"");

								put(
									"wikiPageExternalReferenceCode",
									"\"" +
										testGraphQLDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" +
										wikiPageAttachment2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Long
			testGraphQLDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGraphQLDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected WikiPageAttachment
			testGraphQLDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_addWikiPageAttachment()
		throws Exception {

		return testGraphQLSiteWikiPageAttachment_addWikiPageAttachment();
	}

	@Test
	public void testDeleteWikiPageAttachment() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiPageAttachment wikiPageAttachment =
			testDeleteWikiPageAttachment_addWikiPageAttachment();

		assertHttpResponseStatusCode(
			204,
			wikiPageAttachmentResource.deleteWikiPageAttachmentHttpResponse(
				wikiPageAttachment.getId()));

		assertHttpResponseStatusCode(
			404,
			wikiPageAttachmentResource.getWikiPageAttachmentHttpResponse(
				wikiPageAttachment.getId()));
		assertHttpResponseStatusCode(
			404,
			wikiPageAttachmentResource.getWikiPageAttachmentHttpResponse(0L));
	}

	protected WikiPageAttachment
			testDeleteWikiPageAttachment_addWikiPageAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteWikiPageAttachment() throws Exception {

		// No namespace

		WikiPageAttachment wikiPageAttachment1 =
			testGraphQLDeleteWikiPageAttachment_addWikiPageAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteWikiPageAttachment",
						new HashMap<String, Object>() {
							{
								put(
									"wikiPageAttachmentId",
									wikiPageAttachment1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteWikiPageAttachment"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"wikiPageAttachment",
					new HashMap<String, Object>() {
						{
							put(
								"wikiPageAttachmentId",
								wikiPageAttachment1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		WikiPageAttachment wikiPageAttachment2 =
			testGraphQLDeleteWikiPageAttachment_addWikiPageAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteWikiPageAttachment",
							new HashMap<String, Object>() {
								{
									put(
										"wikiPageAttachmentId",
										wikiPageAttachment2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteWikiPageAttachment"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"wikiPageAttachment",
						new HashMap<String, Object>() {
							{
								put(
									"wikiPageAttachmentId",
									wikiPageAttachment2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected WikiPageAttachment
			testGraphQLDeleteWikiPageAttachment_addWikiPageAttachment()
		throws Exception {

		return testGraphQLWikiPageAttachment_addWikiPageAttachment();
	}

	@Test
	public void testDeleteWikiPageAttachmentBatch() throws Exception {
		WikiPageAttachment wikiPageAttachment1 =
			testDeleteWikiPageAttachmentBatch_addWikiPageAttachment();

		testDeleteWikiPageAttachmentBatch_deleteWikiPageAttachment(
			202, null, wikiPageAttachment1.getId());

		assertHttpResponseStatusCode(
			404,
			wikiPageAttachmentResource.getWikiPageAttachmentHttpResponse(
				wikiPageAttachment1.getId()));
	}

	protected WikiPageAttachment
			testDeleteWikiPageAttachmentBatch_addWikiPageAttachment()
		throws Exception {

		return testDeleteWikiPageAttachment_addWikiPageAttachment();
	}

	protected void testDeleteWikiPageAttachmentBatch_deleteWikiPageAttachment(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			wikiPageAttachmentResource.
				deleteWikiPageAttachmentBatchHttpResponse(
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
	public void testGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode()
		throws Exception {

		WikiPageAttachment postWikiPageAttachment =
			testGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_addWikiPageAttachment();

		WikiPageAttachment getWikiPageAttachment =
			wikiPageAttachmentResource.
				getSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode(
					testGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId(),
					testGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode(),
					postWikiPageAttachment.getExternalReferenceCode());

		assertEquals(postWikiPageAttachment, getWikiPageAttachment);
		assertValid(getWikiPageAttachment);
	}

	protected WikiPageAttachment
			testGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_addWikiPageAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode()
		throws Exception {

		WikiPageAttachment wikiPageAttachment =
			testGraphQLGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_addWikiPageAttachment();

		// No namespace

		Assert.assertTrue(
			equals(
				wikiPageAttachment,
				WikiPageAttachmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"wikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												testGraphQLGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId() +
													"\"");

										put(
											"wikiPageExternalReferenceCode",
											"\"" +
												testGraphQLGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode() +
													"\"");
										put(
											"externalReferenceCode",
											"\"" +
												wikiPageAttachment.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/wikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				wikiPageAttachment,
				WikiPageAttachmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"wikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													testGraphQLGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId() +
														"\"");

											put(
												"wikiPageExternalReferenceCode",
												"\"" +
													testGraphQLGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode() +
														"\"");
											put(
												"externalReferenceCode",
												"\"" +
													wikiPageAttachment.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/wikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode"))));
	}

	protected Long
			testGraphQLGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGraphQLGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantWikiPageExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";
		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"wikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put(
									"wikiPageExternalReferenceCode",
									irrelevantWikiPageExternalReferenceCode);
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
							"wikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put(
										"wikiPageExternalReferenceCode",
										irrelevantWikiPageExternalReferenceCode);
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected WikiPageAttachment
			testGraphQLGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_addWikiPageAttachment()
		throws Exception {

		return testGraphQLSiteWikiPageAttachment_addWikiPageAttachment();
	}

	@Test
	public void testGetWikiPageAttachment() throws Exception {
		WikiPageAttachment postWikiPageAttachment =
			testGetWikiPageAttachment_addWikiPageAttachment();

		WikiPageAttachment getWikiPageAttachment =
			wikiPageAttachmentResource.getWikiPageAttachment(
				postWikiPageAttachment.getId());

		assertEquals(postWikiPageAttachment, getWikiPageAttachment);
		assertValid(getWikiPageAttachment);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		WikiPageAttachment postWikiPageAttachment =
			testGetWikiPageAttachment_addWikiPageAttachment();

		WikiPageAttachment getWikiPageAttachment =
			wikiPageAttachmentResource.getWikiPageAttachment(
				postWikiPageAttachment.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.delivery.dto.v1_0.WikiPageAttachment"
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
			postWikiPageAttachment.getId());

		assertEquals(
			getWikiPageAttachment,
			WikiPageAttachmentSerDes.toDTO(item.toString()));
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

	protected WikiPageAttachment
			testGetWikiPageAttachment_addWikiPageAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetWikiPageAttachment() throws Exception {
		WikiPageAttachment wikiPageAttachment =
			testGraphQLGetWikiPageAttachment_addWikiPageAttachment();

		// No namespace

		Assert.assertTrue(
			equals(
				wikiPageAttachment,
				WikiPageAttachmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"wikiPageAttachment",
								new HashMap<String, Object>() {
									{
										put(
											"wikiPageAttachmentId",
											wikiPageAttachment.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/wikiPageAttachment"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				wikiPageAttachment,
				WikiPageAttachmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"wikiPageAttachment",
									new HashMap<String, Object>() {
										{
											put(
												"wikiPageAttachmentId",
												wikiPageAttachment.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/wikiPageAttachment"))));
	}

	@Test
	public void testGraphQLGetWikiPageAttachmentNotFound() throws Exception {
		Long irrelevantWikiPageAttachmentId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"wikiPageAttachment",
						new HashMap<String, Object>() {
							{
								put(
									"wikiPageAttachmentId",
									irrelevantWikiPageAttachmentId);
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
							"wikiPageAttachment",
							new HashMap<String, Object>() {
								{
									put(
										"wikiPageAttachmentId",
										irrelevantWikiPageAttachmentId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected WikiPageAttachment
			testGraphQLGetWikiPageAttachment_addWikiPageAttachment()
		throws Exception {

		return testGraphQLWikiPageAttachment_addWikiPageAttachment();
	}

	@Test
	public void testGetWikiPageWikiPageAttachmentsPage() throws Exception {
		Long wikiPageId =
			testGetWikiPageWikiPageAttachmentsPage_getWikiPageId();
		Long irrelevantWikiPageId =
			testGetWikiPageWikiPageAttachmentsPage_getIrrelevantWikiPageId();

		Page<WikiPageAttachment> page =
			wikiPageAttachmentResource.getWikiPageWikiPageAttachmentsPage(
				wikiPageId);

		long totalCount = page.getTotalCount();

		if (irrelevantWikiPageId != null) {
			WikiPageAttachment irrelevantWikiPageAttachment =
				testGetWikiPageWikiPageAttachmentsPage_addWikiPageAttachment(
					irrelevantWikiPageId, randomIrrelevantWikiPageAttachment());

			page =
				wikiPageAttachmentResource.getWikiPageWikiPageAttachmentsPage(
					irrelevantWikiPageId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantWikiPageAttachment,
				(List<WikiPageAttachment>)page.getItems());
			assertValid(
				page,
				testGetWikiPageWikiPageAttachmentsPage_getExpectedActions(
					irrelevantWikiPageId));
		}

		WikiPageAttachment wikiPageAttachment1 =
			testGetWikiPageWikiPageAttachmentsPage_addWikiPageAttachment(
				wikiPageId, randomWikiPageAttachment());

		WikiPageAttachment wikiPageAttachment2 =
			testGetWikiPageWikiPageAttachmentsPage_addWikiPageAttachment(
				wikiPageId, randomWikiPageAttachment());

		page = wikiPageAttachmentResource.getWikiPageWikiPageAttachmentsPage(
			wikiPageId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			wikiPageAttachment1, (List<WikiPageAttachment>)page.getItems());
		assertContains(
			wikiPageAttachment2, (List<WikiPageAttachment>)page.getItems());
		assertValid(
			page,
			testGetWikiPageWikiPageAttachmentsPage_getExpectedActions(
				wikiPageId));

		wikiPageAttachmentResource.deleteWikiPageAttachment(
			wikiPageAttachment1.getId());

		wikiPageAttachmentResource.deleteWikiPageAttachment(
			wikiPageAttachment2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetWikiPageWikiPageAttachmentsPage_getExpectedActions(
				Long wikiPageId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/wiki-pages/{wikiPageId}/wiki-page-attachments/batch".
				replace("{wikiPageId}", String.valueOf(wikiPageId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	protected WikiPageAttachment
			testGetWikiPageWikiPageAttachmentsPage_addWikiPageAttachment(
				Long wikiPageId, WikiPageAttachment wikiPageAttachment)
		throws Exception {

		return wikiPageAttachmentResource.postWikiPageWikiPageAttachment(
			wikiPageId, wikiPageAttachment, getMultipartFiles());
	}

	protected Long testGetWikiPageWikiPageAttachmentsPage_getWikiPageId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWikiPageWikiPageAttachmentsPage_getIrrelevantWikiPageId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetWikiPageWikiPageAttachmentsPage()
		throws Exception {

		Long wikiPageId =
			testGetWikiPageWikiPageAttachmentsPage_getWikiPageId();

		GraphQLField graphQLField = new GraphQLField(
			"wikiPageWikiPageAttachments",
			new HashMap<String, Object>() {
				{
					put("wikiPageId", wikiPageId);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject wikiPageWikiPageAttachmentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/wikiPageWikiPageAttachments");

		long totalCount = wikiPageWikiPageAttachmentsJSONObject.getLong(
			"totalCount");

		WikiPageAttachment wikiPageAttachment1 =
			testGraphQLWikiPageWikiPageAttachment_addWikiPageAttachment(
				wikiPageId, randomWikiPageAttachment());

		WikiPageAttachment wikiPageAttachment2 =
			testGraphQLWikiPageWikiPageAttachment_addWikiPageAttachment(
				wikiPageId, randomWikiPageAttachment());

		wikiPageWikiPageAttachmentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/wikiPageWikiPageAttachments");

		Assert.assertEquals(
			totalCount + 2,
			wikiPageWikiPageAttachmentsJSONObject.getLong("totalCount"));

		assertContains(
			wikiPageAttachment1,
			Arrays.asList(
				WikiPageAttachmentSerDes.toDTOs(
					wikiPageWikiPageAttachmentsJSONObject.getString("items"))));
		assertContains(
			wikiPageAttachment2,
			Arrays.asList(
				WikiPageAttachmentSerDes.toDTOs(
					wikiPageWikiPageAttachmentsJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		wikiPageWikiPageAttachmentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/wikiPageWikiPageAttachments");

		Assert.assertEquals(
			totalCount + 2,
			wikiPageWikiPageAttachmentsJSONObject.getLong("totalCount"));

		assertContains(
			wikiPageAttachment1,
			Arrays.asList(
				WikiPageAttachmentSerDes.toDTOs(
					wikiPageWikiPageAttachmentsJSONObject.getString("items"))));
		assertContains(
			wikiPageAttachment2,
			Arrays.asList(
				WikiPageAttachmentSerDes.toDTOs(
					wikiPageWikiPageAttachmentsJSONObject.getString("items"))));
	}

	@Test
	public void testPostWikiPageWikiPageAttachment() throws Exception {
		WikiPageAttachment randomWikiPageAttachment =
			randomWikiPageAttachment();

		Map<String, File> multipartFiles = getMultipartFiles();

		WikiPageAttachment postWikiPageAttachment =
			testPostWikiPageWikiPageAttachment_addWikiPageAttachment(
				randomWikiPageAttachment, multipartFiles);

		assertEquals(randomWikiPageAttachment, postWikiPageAttachment);
		assertValid(postWikiPageAttachment);

		assertValid(postWikiPageAttachment, multipartFiles);
	}

	protected WikiPageAttachment
			testPostWikiPageWikiPageAttachment_addWikiPageAttachment(
				WikiPageAttachment wikiPageAttachment,
				Map<String, File> multipartFiles)
		throws Exception {

		return wikiPageAttachmentResource.postWikiPageWikiPageAttachment(
			testGetWikiPageWikiPageAttachmentsPage_getWikiPageId(),
			wikiPageAttachment, multipartFiles);
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		WikiPageAttachment wikiPageAttachment1 =
			testBatchEngineDeleteImportTask_addWikiPageAttachment();

		testBatchEngineDeleteImportTask_deleteWikiPageAttachment(
			200, null, wikiPageAttachment1.getId());

		assertHttpResponseStatusCode(
			404,
			wikiPageAttachmentResource.getWikiPageAttachmentHttpResponse(
				wikiPageAttachment1.getId()));
	}

	protected WikiPageAttachment
			testBatchEngineDeleteImportTask_addWikiPageAttachment()
		throws Exception {

		return testDeleteWikiPageAttachment_addWikiPageAttachment();
	}

	protected void testBatchEngineDeleteImportTask_deleteWikiPageAttachment(
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
				"com.liferay.headless.delivery.dto.v1_0.WikiPageAttachment",
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

	protected WikiPageAttachment
			testGraphQLSiteWikiPageAttachment_addWikiPageAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected WikiPageAttachment
			testGraphQLWikiPageAttachment_addWikiPageAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected WikiPageAttachment
			testGraphQLWikiPageWikiPageAttachment_addWikiPageAttachment()
		throws Exception {

		return testGraphQLWikiPageWikiPageAttachment_addWikiPageAttachment(
			testGraphQLWikiPageWikiPageAttachment_getWikiPageId(),
			randomWikiPageAttachment());
	}

	protected Long testGraphQLWikiPageWikiPageAttachment_getWikiPageId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected WikiPageAttachment
			testGraphQLWikiPageWikiPageAttachment_addWikiPageAttachment(
				Long wikiPageId, WikiPageAttachment wikiPageAttachment)
		throws Exception {

		JSONDeserializer<WikiPageAttachment> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(WikiPageAttachment.class)) {

			if (getGraphQLValue(field.get(wikiPageAttachment)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(wikiPageAttachment)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createWikiPageWikiPageAttachment",
						new HashMap<String, Object>() {
							{
								put("wikiPageId", wikiPageId);
								put("wikiPageAttachment", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createWikiPageWikiPageAttachment"),
			WikiPageAttachment.class);
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
		WikiPageAttachment wikiPageAttachment,
		List<WikiPageAttachment> wikiPageAttachments) {

		boolean contains = false;

		for (WikiPageAttachment item : wikiPageAttachments) {
			if (equals(wikiPageAttachment, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			wikiPageAttachments + " does not contain " + wikiPageAttachment,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		WikiPageAttachment wikiPageAttachment1,
		WikiPageAttachment wikiPageAttachment2) {

		Assert.assertTrue(
			wikiPageAttachment1 + " does not equal " + wikiPageAttachment2,
			equals(wikiPageAttachment1, wikiPageAttachment2));
	}

	protected void assertEquals(
		List<WikiPageAttachment> wikiPageAttachments1,
		List<WikiPageAttachment> wikiPageAttachments2) {

		Assert.assertEquals(
			wikiPageAttachments1.size(), wikiPageAttachments2.size());

		for (int i = 0; i < wikiPageAttachments1.size(); i++) {
			WikiPageAttachment wikiPageAttachment1 = wikiPageAttachments1.get(
				i);
			WikiPageAttachment wikiPageAttachment2 = wikiPageAttachments2.get(
				i);

			assertEquals(wikiPageAttachment1, wikiPageAttachment2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<WikiPageAttachment> wikiPageAttachments1,
		List<WikiPageAttachment> wikiPageAttachments2) {

		Assert.assertEquals(
			wikiPageAttachments1.size(), wikiPageAttachments2.size());

		for (WikiPageAttachment wikiPageAttachment1 : wikiPageAttachments1) {
			boolean contains = false;

			for (WikiPageAttachment wikiPageAttachment2 :
					wikiPageAttachments2) {

				if (equals(wikiPageAttachment1, wikiPageAttachment2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				wikiPageAttachments2 + " does not contain " +
					wikiPageAttachment1,
				contains);
		}
	}

	protected void assertValid(WikiPageAttachment wikiPageAttachment)
		throws Exception {

		boolean valid = true;

		if (wikiPageAttachment.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (wikiPageAttachment.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("contentUrl", additionalAssertFieldName)) {
				if (wikiPageAttachment.getContentUrl() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("contentValue", additionalAssertFieldName)) {
				if (wikiPageAttachment.getContentValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (wikiPageAttachment.getEncodingFormat() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (wikiPageAttachment.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("fileExtension", additionalAssertFieldName)) {
				if (wikiPageAttachment.getFileExtension() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sizeInBytes", additionalAssertFieldName)) {
				if (wikiPageAttachment.getSizeInBytes() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (wikiPageAttachment.getTitle() == null) {
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
			WikiPageAttachment wikiPageAttachment,
			Map<String, File> multipartFiles)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertValid(Page<WikiPageAttachment> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<WikiPageAttachment> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<WikiPageAttachment> wikiPageAttachments =
			page.getItems();

		int size = wikiPageAttachments.size();

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
					com.liferay.headless.delivery.dto.v1_0.WikiPageAttachment.
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
		WikiPageAttachment wikiPageAttachment1,
		WikiPageAttachment wikiPageAttachment2) {

		if (wikiPageAttachment1 == wikiPageAttachment2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)wikiPageAttachment1.getActions(),
						(Map)wikiPageAttachment2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("contentUrl", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPageAttachment1.getContentUrl(),
						wikiPageAttachment2.getContentUrl())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("contentValue", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPageAttachment1.getContentValue(),
						wikiPageAttachment2.getContentValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPageAttachment1.getEncodingFormat(),
						wikiPageAttachment2.getEncodingFormat())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						wikiPageAttachment1.getExternalReferenceCode(),
						wikiPageAttachment2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fileExtension", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPageAttachment1.getFileExtension(),
						wikiPageAttachment2.getFileExtension())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPageAttachment1.getId(),
						wikiPageAttachment2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sizeInBytes", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPageAttachment1.getSizeInBytes(),
						wikiPageAttachment2.getSizeInBytes())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPageAttachment1.getTitle(),
						wikiPageAttachment2.getTitle())) {

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

		if (!(_wikiPageAttachmentResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_wikiPageAttachmentResource;

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
		WikiPageAttachment wikiPageAttachment) {

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

		if (entityFieldName.equals("contentUrl")) {
			Object object = wikiPageAttachment.getContentUrl();

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
			Object object = wikiPageAttachment.getContentValue();

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

		if (entityFieldName.equals("encodingFormat")) {
			Object object = wikiPageAttachment.getEncodingFormat();

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
			Object object = wikiPageAttachment.getExternalReferenceCode();

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
			Object object = wikiPageAttachment.getFileExtension();

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

		if (entityFieldName.equals("sizeInBytes")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("title")) {
			Object object = wikiPageAttachment.getTitle();

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

	protected WikiPageAttachment randomWikiPageAttachment() throws Exception {
		return new WikiPageAttachment() {
			{
				contentUrl = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				contentValue = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				encodingFormat = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				fileExtension = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				sizeInBytes = RandomTestUtil.randomLong();
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected WikiPageAttachment randomIrrelevantWikiPageAttachment()
		throws Exception {

		WikiPageAttachment randomIrrelevantWikiPageAttachment =
			randomWikiPageAttachment();

		return randomIrrelevantWikiPageAttachment;
	}

	protected WikiPageAttachment randomPatchWikiPageAttachment()
		throws Exception {

		return randomWikiPageAttachment();
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

	protected WikiPageAttachmentResource wikiPageAttachmentResource;
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
		LogFactoryUtil.getLog(BaseWikiPageAttachmentResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.delivery.resource.v1_0.WikiPageAttachmentResource
			_wikiPageAttachmentResource;

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