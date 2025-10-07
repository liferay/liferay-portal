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
import com.liferay.headless.delivery.client.dto.v1_0.KnowledgeBaseAttachment;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.resource.v1_0.KnowledgeBaseAttachmentResource;
import com.liferay.headless.delivery.client.serdes.v1_0.KnowledgeBaseAttachmentSerDes;
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
public abstract class BaseKnowledgeBaseAttachmentResourceTestCase {

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

		_knowledgeBaseAttachmentResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		knowledgeBaseAttachmentResource =
			KnowledgeBaseAttachmentResource.builder(
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

		KnowledgeBaseAttachment knowledgeBaseAttachment1 =
			randomKnowledgeBaseAttachment();

		String json = objectMapper.writeValueAsString(knowledgeBaseAttachment1);

		KnowledgeBaseAttachment knowledgeBaseAttachment2 =
			KnowledgeBaseAttachmentSerDes.toDTO(json);

		Assert.assertTrue(
			equals(knowledgeBaseAttachment1, knowledgeBaseAttachment2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		KnowledgeBaseAttachment knowledgeBaseAttachment =
			randomKnowledgeBaseAttachment();

		String json1 = objectMapper.writeValueAsString(knowledgeBaseAttachment);
		String json2 = KnowledgeBaseAttachmentSerDes.toJSON(
			knowledgeBaseAttachment);

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

		KnowledgeBaseAttachment knowledgeBaseAttachment =
			randomKnowledgeBaseAttachment();

		knowledgeBaseAttachment.setContentUrl(regex);
		knowledgeBaseAttachment.setContentValue(regex);
		knowledgeBaseAttachment.setEncodingFormat(regex);
		knowledgeBaseAttachment.setExternalReferenceCode(regex);
		knowledgeBaseAttachment.setFileExtension(regex);
		knowledgeBaseAttachment.setTitle(regex);

		String json = KnowledgeBaseAttachmentSerDes.toJSON(
			knowledgeBaseAttachment);

		Assert.assertFalse(json.contains(regex));

		knowledgeBaseAttachment = KnowledgeBaseAttachmentSerDes.toDTO(json);

		Assert.assertEquals(regex, knowledgeBaseAttachment.getContentUrl());
		Assert.assertEquals(regex, knowledgeBaseAttachment.getContentValue());
		Assert.assertEquals(regex, knowledgeBaseAttachment.getEncodingFormat());
		Assert.assertEquals(
			regex, knowledgeBaseAttachment.getExternalReferenceCode());
		Assert.assertEquals(regex, knowledgeBaseAttachment.getFileExtension());
		Assert.assertEquals(regex, knowledgeBaseAttachment.getTitle());
	}

	@Test
	public void testDeleteKnowledgeBaseAttachment() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		KnowledgeBaseAttachment knowledgeBaseAttachment =
			testDeleteKnowledgeBaseAttachment_addKnowledgeBaseAttachment();

		assertHttpResponseStatusCode(
			204,
			knowledgeBaseAttachmentResource.
				deleteKnowledgeBaseAttachmentHttpResponse(
					knowledgeBaseAttachment.getId()));

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				getKnowledgeBaseAttachmentHttpResponse(
					knowledgeBaseAttachment.getId()));
		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				getKnowledgeBaseAttachmentHttpResponse(0L));
	}

	protected KnowledgeBaseAttachment
			testDeleteKnowledgeBaseAttachment_addKnowledgeBaseAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteKnowledgeBaseAttachment() throws Exception {

		// No namespace

		KnowledgeBaseAttachment knowledgeBaseAttachment1 =
			testGraphQLDeleteKnowledgeBaseAttachment_addKnowledgeBaseAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteKnowledgeBaseAttachment",
						new HashMap<String, Object>() {
							{
								put(
									"knowledgeBaseAttachmentId",
									knowledgeBaseAttachment1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteKnowledgeBaseAttachment"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"knowledgeBaseAttachment",
					new HashMap<String, Object>() {
						{
							put(
								"knowledgeBaseAttachmentId",
								knowledgeBaseAttachment1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		KnowledgeBaseAttachment knowledgeBaseAttachment2 =
			testGraphQLDeleteKnowledgeBaseAttachment_addKnowledgeBaseAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteKnowledgeBaseAttachment",
							new HashMap<String, Object>() {
								{
									put(
										"knowledgeBaseAttachmentId",
										knowledgeBaseAttachment2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteKnowledgeBaseAttachment"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"knowledgeBaseAttachment",
						new HashMap<String, Object>() {
							{
								put(
									"knowledgeBaseAttachmentId",
									knowledgeBaseAttachment2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected KnowledgeBaseAttachment
			testGraphQLDeleteKnowledgeBaseAttachment_addKnowledgeBaseAttachment()
		throws Exception {

		return testGraphQLKnowledgeBaseAttachment_addKnowledgeBaseAttachment();
	}

	@Test
	public void testDeleteKnowledgeBaseAttachmentBatch() throws Exception {
		KnowledgeBaseAttachment knowledgeBaseAttachment1 =
			testDeleteKnowledgeBaseAttachmentBatch_addKnowledgeBaseAttachment();

		testDeleteKnowledgeBaseAttachmentBatch_deleteKnowledgeBaseAttachment(
			202, null, knowledgeBaseAttachment1.getId());

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				getKnowledgeBaseAttachmentHttpResponse(
					knowledgeBaseAttachment1.getId()));
	}

	protected KnowledgeBaseAttachment
			testDeleteKnowledgeBaseAttachmentBatch_addKnowledgeBaseAttachment()
		throws Exception {

		return testDeleteKnowledgeBaseAttachment_addKnowledgeBaseAttachment();
	}

	protected void
			testDeleteKnowledgeBaseAttachmentBatch_deleteKnowledgeBaseAttachment(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			knowledgeBaseAttachmentResource.
				deleteKnowledgeBaseAttachmentBatchHttpResponse(
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
	public void testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		KnowledgeBaseAttachment knowledgeBaseAttachment =
			testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment();

		assertHttpResponseStatusCode(
			204,
			knowledgeBaseAttachmentResource.
				deleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId(),
					testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode(),
					knowledgeBaseAttachment.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				getSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId(),
					testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode(),
					knowledgeBaseAttachment.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				getSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId(),
					testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode(),
					"-"));
	}

	protected KnowledgeBaseAttachment
			testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode()
		throws Exception {

		// No namespace

		KnowledgeBaseAttachment knowledgeBaseAttachment1 =
			testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" +
										testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId() +
											"\"");

								put(
									"knowledgeBaseArticleExternalReferenceCode",
									"\"" +
										testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" +
										knowledgeBaseAttachment1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"knowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"siteKey",
								"\"" +
									testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId() +
										"\"");

							put(
								"knowledgeBaseArticleExternalReferenceCode",
								"\"" +
									testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode() +
										"\"");
							put(
								"externalReferenceCode",
								"\"" +
									knowledgeBaseAttachment1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		KnowledgeBaseAttachment knowledgeBaseAttachment2 =
			testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" +
											testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId() +
												"\"");

									put(
										"knowledgeBaseArticleExternalReferenceCode",
										"\"" +
											testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode() +
												"\"");
									put(
										"externalReferenceCode",
										"\"" +
											knowledgeBaseAttachment2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"knowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" +
										testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId() +
											"\"");

								put(
									"knowledgeBaseArticleExternalReferenceCode",
									"\"" +
										testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" +
										knowledgeBaseAttachment2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Long
			testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected KnowledgeBaseAttachment
			testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment()
		throws Exception {

		return testGraphQLSiteKnowledgeBaseAttachment_addKnowledgeBaseAttachment();
	}

	@Test
	public void testGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage()
		throws Exception {

		Long knowledgeBaseArticleId =
			testGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage_getKnowledgeBaseArticleId();
		Long irrelevantKnowledgeBaseArticleId =
			testGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage_getIrrelevantKnowledgeBaseArticleId();

		Page<KnowledgeBaseAttachment> page =
			knowledgeBaseAttachmentResource.
				getKnowledgeBaseArticleKnowledgeBaseAttachmentsPage(
					knowledgeBaseArticleId);

		long totalCount = page.getTotalCount();

		if (irrelevantKnowledgeBaseArticleId != null) {
			KnowledgeBaseAttachment irrelevantKnowledgeBaseAttachment =
				testGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage_addKnowledgeBaseAttachment(
					irrelevantKnowledgeBaseArticleId,
					randomIrrelevantKnowledgeBaseAttachment());

			page =
				knowledgeBaseAttachmentResource.
					getKnowledgeBaseArticleKnowledgeBaseAttachmentsPage(
						irrelevantKnowledgeBaseArticleId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantKnowledgeBaseAttachment,
				(List<KnowledgeBaseAttachment>)page.getItems());
			assertValid(
				page,
				testGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage_getExpectedActions(
					irrelevantKnowledgeBaseArticleId));
		}

		KnowledgeBaseAttachment knowledgeBaseAttachment1 =
			testGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage_addKnowledgeBaseAttachment(
				knowledgeBaseArticleId, randomKnowledgeBaseAttachment());

		KnowledgeBaseAttachment knowledgeBaseAttachment2 =
			testGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage_addKnowledgeBaseAttachment(
				knowledgeBaseArticleId, randomKnowledgeBaseAttachment());

		page =
			knowledgeBaseAttachmentResource.
				getKnowledgeBaseArticleKnowledgeBaseAttachmentsPage(
					knowledgeBaseArticleId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			knowledgeBaseAttachment1,
			(List<KnowledgeBaseAttachment>)page.getItems());
		assertContains(
			knowledgeBaseAttachment2,
			(List<KnowledgeBaseAttachment>)page.getItems());
		assertValid(
			page,
			testGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage_getExpectedActions(
				knowledgeBaseArticleId));

		knowledgeBaseAttachmentResource.deleteKnowledgeBaseAttachment(
			knowledgeBaseAttachment1.getId());

		knowledgeBaseAttachmentResource.deleteKnowledgeBaseAttachment(
			knowledgeBaseAttachment2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage_getExpectedActions(
				Long knowledgeBaseArticleId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/knowledge-base-articles/{knowledgeBaseArticleId}/knowledge-base-attachments/batch".
				replace(
					"{knowledgeBaseArticleId}",
					String.valueOf(knowledgeBaseArticleId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	protected KnowledgeBaseAttachment
			testGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage_addKnowledgeBaseAttachment(
				Long knowledgeBaseArticleId,
				KnowledgeBaseAttachment knowledgeBaseAttachment)
		throws Exception {

		return knowledgeBaseAttachmentResource.
			postKnowledgeBaseArticleKnowledgeBaseAttachment(
				knowledgeBaseArticleId, knowledgeBaseAttachment,
				getMultipartFiles());
	}

	protected Long
			testGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage_getKnowledgeBaseArticleId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage_getIrrelevantKnowledgeBaseArticleId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage()
		throws Exception {

		Long knowledgeBaseArticleId =
			testGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage_getKnowledgeBaseArticleId();

		GraphQLField graphQLField = new GraphQLField(
			"knowledgeBaseArticleKnowledgeBaseAttachments",
			new HashMap<String, Object>() {
				{
					put("knowledgeBaseArticleId", knowledgeBaseArticleId);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject knowledgeBaseArticleKnowledgeBaseAttachmentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/knowledgeBaseArticleKnowledgeBaseAttachments");

		long totalCount =
			knowledgeBaseArticleKnowledgeBaseAttachmentsJSONObject.getLong(
				"totalCount");

		KnowledgeBaseAttachment knowledgeBaseAttachment1 =
			testGraphQLKnowledgeBaseArticleKnowledgeBaseAttachment_addKnowledgeBaseAttachment(
				knowledgeBaseArticleId, randomKnowledgeBaseAttachment());

		KnowledgeBaseAttachment knowledgeBaseAttachment2 =
			testGraphQLKnowledgeBaseArticleKnowledgeBaseAttachment_addKnowledgeBaseAttachment(
				knowledgeBaseArticleId, randomKnowledgeBaseAttachment());

		knowledgeBaseArticleKnowledgeBaseAttachmentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/knowledgeBaseArticleKnowledgeBaseAttachments");

		Assert.assertEquals(
			totalCount + 2,
			knowledgeBaseArticleKnowledgeBaseAttachmentsJSONObject.getLong(
				"totalCount"));

		assertContains(
			knowledgeBaseAttachment1,
			Arrays.asList(
				KnowledgeBaseAttachmentSerDes.toDTOs(
					knowledgeBaseArticleKnowledgeBaseAttachmentsJSONObject.
						getString("items"))));
		assertContains(
			knowledgeBaseAttachment2,
			Arrays.asList(
				KnowledgeBaseAttachmentSerDes.toDTOs(
					knowledgeBaseArticleKnowledgeBaseAttachmentsJSONObject.
						getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		knowledgeBaseArticleKnowledgeBaseAttachmentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("headlessDelivery_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"JSONObject/knowledgeBaseArticleKnowledgeBaseAttachments");

		Assert.assertEquals(
			totalCount + 2,
			knowledgeBaseArticleKnowledgeBaseAttachmentsJSONObject.getLong(
				"totalCount"));

		assertContains(
			knowledgeBaseAttachment1,
			Arrays.asList(
				KnowledgeBaseAttachmentSerDes.toDTOs(
					knowledgeBaseArticleKnowledgeBaseAttachmentsJSONObject.
						getString("items"))));
		assertContains(
			knowledgeBaseAttachment2,
			Arrays.asList(
				KnowledgeBaseAttachmentSerDes.toDTOs(
					knowledgeBaseArticleKnowledgeBaseAttachmentsJSONObject.
						getString("items"))));
	}

	@Test
	public void testGetKnowledgeBaseAttachment() throws Exception {
		KnowledgeBaseAttachment postKnowledgeBaseAttachment =
			testGetKnowledgeBaseAttachment_addKnowledgeBaseAttachment();

		KnowledgeBaseAttachment getKnowledgeBaseAttachment =
			knowledgeBaseAttachmentResource.getKnowledgeBaseAttachment(
				postKnowledgeBaseAttachment.getId());

		assertEquals(postKnowledgeBaseAttachment, getKnowledgeBaseAttachment);
		assertValid(getKnowledgeBaseAttachment);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		KnowledgeBaseAttachment postKnowledgeBaseAttachment =
			testGetKnowledgeBaseAttachment_addKnowledgeBaseAttachment();

		KnowledgeBaseAttachment getKnowledgeBaseAttachment =
			knowledgeBaseAttachmentResource.getKnowledgeBaseAttachment(
				postKnowledgeBaseAttachment.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.delivery.dto.v1_0.KnowledgeBaseAttachment"
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
			postKnowledgeBaseAttachment.getId());

		assertEquals(
			getKnowledgeBaseAttachment,
			KnowledgeBaseAttachmentSerDes.toDTO(item.toString()));
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

	protected KnowledgeBaseAttachment
			testGetKnowledgeBaseAttachment_addKnowledgeBaseAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetKnowledgeBaseAttachment() throws Exception {
		KnowledgeBaseAttachment knowledgeBaseAttachment =
			testGraphQLGetKnowledgeBaseAttachment_addKnowledgeBaseAttachment();

		// No namespace

		Assert.assertTrue(
			equals(
				knowledgeBaseAttachment,
				KnowledgeBaseAttachmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"knowledgeBaseAttachment",
								new HashMap<String, Object>() {
									{
										put(
											"knowledgeBaseAttachmentId",
											knowledgeBaseAttachment.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/knowledgeBaseAttachment"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				knowledgeBaseAttachment,
				KnowledgeBaseAttachmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"knowledgeBaseAttachment",
									new HashMap<String, Object>() {
										{
											put(
												"knowledgeBaseAttachmentId",
												knowledgeBaseAttachment.
													getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/knowledgeBaseAttachment"))));
	}

	@Test
	public void testGraphQLGetKnowledgeBaseAttachmentNotFound()
		throws Exception {

		Long irrelevantKnowledgeBaseAttachmentId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"knowledgeBaseAttachment",
						new HashMap<String, Object>() {
							{
								put(
									"knowledgeBaseAttachmentId",
									irrelevantKnowledgeBaseAttachmentId);
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
							"knowledgeBaseAttachment",
							new HashMap<String, Object>() {
								{
									put(
										"knowledgeBaseAttachmentId",
										irrelevantKnowledgeBaseAttachmentId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected KnowledgeBaseAttachment
			testGraphQLGetKnowledgeBaseAttachment_addKnowledgeBaseAttachment()
		throws Exception {

		return testGraphQLKnowledgeBaseAttachment_addKnowledgeBaseAttachment();
	}

	@Test
	public void testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode()
		throws Exception {

		KnowledgeBaseAttachment postKnowledgeBaseAttachment =
			testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment();

		KnowledgeBaseAttachment getKnowledgeBaseAttachment =
			knowledgeBaseAttachmentResource.
				getSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode(
					testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId(),
					testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode(),
					postKnowledgeBaseAttachment.getExternalReferenceCode());

		assertEquals(postKnowledgeBaseAttachment, getKnowledgeBaseAttachment);
		assertValid(getKnowledgeBaseAttachment);
	}

	protected KnowledgeBaseAttachment
			testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode()
		throws Exception {

		KnowledgeBaseAttachment knowledgeBaseAttachment =
			testGraphQLGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment();

		// No namespace

		Assert.assertTrue(
			equals(
				knowledgeBaseAttachment,
				KnowledgeBaseAttachmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"knowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												testGraphQLGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId() +
													"\"");

										put(
											"knowledgeBaseArticleExternalReferenceCode",
											"\"" +
												testGraphQLGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode() +
													"\"");
										put(
											"externalReferenceCode",
											"\"" +
												knowledgeBaseAttachment.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/knowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				knowledgeBaseAttachment,
				KnowledgeBaseAttachmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"knowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													testGraphQLGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId() +
														"\"");

											put(
												"knowledgeBaseArticleExternalReferenceCode",
												"\"" +
													testGraphQLGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode() +
														"\"");
											put(
												"externalReferenceCode",
												"\"" +
													knowledgeBaseAttachment.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/knowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode"))));
	}

	protected Long
			testGraphQLGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGraphQLGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantKnowledgeBaseArticleExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";
		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"knowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put(
									"knowledgeBaseArticleExternalReferenceCode",
									irrelevantKnowledgeBaseArticleExternalReferenceCode);
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
							"knowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put(
										"knowledgeBaseArticleExternalReferenceCode",
										irrelevantKnowledgeBaseArticleExternalReferenceCode);
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected KnowledgeBaseAttachment
			testGraphQLGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment()
		throws Exception {

		return testGraphQLSiteKnowledgeBaseAttachment_addKnowledgeBaseAttachment();
	}

	@Test
	public void testPostKnowledgeBaseArticleKnowledgeBaseAttachment()
		throws Exception {

		KnowledgeBaseAttachment randomKnowledgeBaseAttachment =
			randomKnowledgeBaseAttachment();

		Map<String, File> multipartFiles = getMultipartFiles();

		KnowledgeBaseAttachment postKnowledgeBaseAttachment =
			testPostKnowledgeBaseArticleKnowledgeBaseAttachment_addKnowledgeBaseAttachment(
				randomKnowledgeBaseAttachment, multipartFiles);

		assertEquals(
			randomKnowledgeBaseAttachment, postKnowledgeBaseAttachment);
		assertValid(postKnowledgeBaseAttachment);

		assertValid(postKnowledgeBaseAttachment, multipartFiles);
	}

	protected KnowledgeBaseAttachment
			testPostKnowledgeBaseArticleKnowledgeBaseAttachment_addKnowledgeBaseAttachment(
				KnowledgeBaseAttachment knowledgeBaseAttachment,
				Map<String, File> multipartFiles)
		throws Exception {

		return knowledgeBaseAttachmentResource.
			postKnowledgeBaseArticleKnowledgeBaseAttachment(
				testGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage_getKnowledgeBaseArticleId(),
				knowledgeBaseAttachment, multipartFiles);
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		KnowledgeBaseAttachment knowledgeBaseAttachment1 =
			testBatchEngineDeleteImportTask_addKnowledgeBaseAttachment();

		testBatchEngineDeleteImportTask_deleteKnowledgeBaseAttachment(
			200, null, knowledgeBaseAttachment1.getId());

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				getKnowledgeBaseAttachmentHttpResponse(
					knowledgeBaseAttachment1.getId()));
	}

	protected KnowledgeBaseAttachment
			testBatchEngineDeleteImportTask_addKnowledgeBaseAttachment()
		throws Exception {

		return testDeleteKnowledgeBaseAttachment_addKnowledgeBaseAttachment();
	}

	protected void
			testBatchEngineDeleteImportTask_deleteKnowledgeBaseAttachment(
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
				"com.liferay.headless.delivery.dto.v1_0.KnowledgeBaseAttachment",
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

	protected KnowledgeBaseAttachment
			testGraphQLKnowledgeBaseAttachment_addKnowledgeBaseAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected KnowledgeBaseAttachment
			testGraphQLSiteKnowledgeBaseAttachment_addKnowledgeBaseAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected KnowledgeBaseAttachment
			testGraphQLKnowledgeBaseArticleKnowledgeBaseAttachment_addKnowledgeBaseAttachment()
		throws Exception {

		return testGraphQLKnowledgeBaseArticleKnowledgeBaseAttachment_addKnowledgeBaseAttachment(
			testGraphQLKnowledgeBaseArticleKnowledgeBaseAttachment_getKnowledgeBaseArticleId(),
			randomKnowledgeBaseAttachment());
	}

	protected Long
			testGraphQLKnowledgeBaseArticleKnowledgeBaseAttachment_getKnowledgeBaseArticleId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected KnowledgeBaseAttachment
			testGraphQLKnowledgeBaseArticleKnowledgeBaseAttachment_addKnowledgeBaseAttachment(
				Long knowledgeBaseArticleId,
				KnowledgeBaseAttachment knowledgeBaseAttachment)
		throws Exception {

		JSONDeserializer<KnowledgeBaseAttachment> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(KnowledgeBaseAttachment.class)) {

			if (getGraphQLValue(field.get(knowledgeBaseAttachment)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(knowledgeBaseAttachment)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createKnowledgeBaseArticleKnowledgeBaseAttachment",
						new HashMap<String, Object>() {
							{
								put(
									"knowledgeBaseArticleId",
									knowledgeBaseArticleId);
								put("knowledgeBaseAttachment", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createKnowledgeBaseArticleKnowledgeBaseAttachment"),
			KnowledgeBaseAttachment.class);
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
		KnowledgeBaseAttachment knowledgeBaseAttachment,
		List<KnowledgeBaseAttachment> knowledgeBaseAttachments) {

		boolean contains = false;

		for (KnowledgeBaseAttachment item : knowledgeBaseAttachments) {
			if (equals(knowledgeBaseAttachment, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			knowledgeBaseAttachments + " does not contain " +
				knowledgeBaseAttachment,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		KnowledgeBaseAttachment knowledgeBaseAttachment1,
		KnowledgeBaseAttachment knowledgeBaseAttachment2) {

		Assert.assertTrue(
			knowledgeBaseAttachment1 + " does not equal " +
				knowledgeBaseAttachment2,
			equals(knowledgeBaseAttachment1, knowledgeBaseAttachment2));
	}

	protected void assertEquals(
		List<KnowledgeBaseAttachment> knowledgeBaseAttachments1,
		List<KnowledgeBaseAttachment> knowledgeBaseAttachments2) {

		Assert.assertEquals(
			knowledgeBaseAttachments1.size(), knowledgeBaseAttachments2.size());

		for (int i = 0; i < knowledgeBaseAttachments1.size(); i++) {
			KnowledgeBaseAttachment knowledgeBaseAttachment1 =
				knowledgeBaseAttachments1.get(i);
			KnowledgeBaseAttachment knowledgeBaseAttachment2 =
				knowledgeBaseAttachments2.get(i);

			assertEquals(knowledgeBaseAttachment1, knowledgeBaseAttachment2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<KnowledgeBaseAttachment> knowledgeBaseAttachments1,
		List<KnowledgeBaseAttachment> knowledgeBaseAttachments2) {

		Assert.assertEquals(
			knowledgeBaseAttachments1.size(), knowledgeBaseAttachments2.size());

		for (KnowledgeBaseAttachment knowledgeBaseAttachment1 :
				knowledgeBaseAttachments1) {

			boolean contains = false;

			for (KnowledgeBaseAttachment knowledgeBaseAttachment2 :
					knowledgeBaseAttachments2) {

				if (equals(
						knowledgeBaseAttachment1, knowledgeBaseAttachment2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				knowledgeBaseAttachments2 + " does not contain " +
					knowledgeBaseAttachment1,
				contains);
		}
	}

	protected void assertValid(KnowledgeBaseAttachment knowledgeBaseAttachment)
		throws Exception {

		boolean valid = true;

		if (knowledgeBaseAttachment.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("contentUrl", additionalAssertFieldName)) {
				if (knowledgeBaseAttachment.getContentUrl() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("contentValue", additionalAssertFieldName)) {
				if (knowledgeBaseAttachment.getContentValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (knowledgeBaseAttachment.getEncodingFormat() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (knowledgeBaseAttachment.getExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("fileExtension", additionalAssertFieldName)) {
				if (knowledgeBaseAttachment.getFileExtension() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sizeInBytes", additionalAssertFieldName)) {
				if (knowledgeBaseAttachment.getSizeInBytes() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (knowledgeBaseAttachment.getTitle() == null) {
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
			KnowledgeBaseAttachment knowledgeBaseAttachment,
			Map<String, File> multipartFiles)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertValid(Page<KnowledgeBaseAttachment> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<KnowledgeBaseAttachment> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<KnowledgeBaseAttachment> knowledgeBaseAttachments =
			page.getItems();

		int size = knowledgeBaseAttachments.size();

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
					com.liferay.headless.delivery.dto.v1_0.
						KnowledgeBaseAttachment.class)) {

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
		KnowledgeBaseAttachment knowledgeBaseAttachment1,
		KnowledgeBaseAttachment knowledgeBaseAttachment2) {

		if (knowledgeBaseAttachment1 == knowledgeBaseAttachment2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("contentUrl", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseAttachment1.getContentUrl(),
						knowledgeBaseAttachment2.getContentUrl())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("contentValue", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseAttachment1.getContentValue(),
						knowledgeBaseAttachment2.getContentValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseAttachment1.getEncodingFormat(),
						knowledgeBaseAttachment2.getEncodingFormat())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						knowledgeBaseAttachment1.getExternalReferenceCode(),
						knowledgeBaseAttachment2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fileExtension", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseAttachment1.getFileExtension(),
						knowledgeBaseAttachment2.getFileExtension())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseAttachment1.getId(),
						knowledgeBaseAttachment2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sizeInBytes", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseAttachment1.getSizeInBytes(),
						knowledgeBaseAttachment2.getSizeInBytes())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseAttachment1.getTitle(),
						knowledgeBaseAttachment2.getTitle())) {

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

		if (!(_knowledgeBaseAttachmentResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_knowledgeBaseAttachmentResource;

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
		KnowledgeBaseAttachment knowledgeBaseAttachment) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("contentUrl")) {
			Object object = knowledgeBaseAttachment.getContentUrl();

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
			Object object = knowledgeBaseAttachment.getContentValue();

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
			Object object = knowledgeBaseAttachment.getEncodingFormat();

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
			Object object = knowledgeBaseAttachment.getExternalReferenceCode();

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
			Object object = knowledgeBaseAttachment.getFileExtension();

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
			Object object = knowledgeBaseAttachment.getTitle();

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

	protected KnowledgeBaseAttachment randomKnowledgeBaseAttachment()
		throws Exception {

		return new KnowledgeBaseAttachment() {
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

	protected KnowledgeBaseAttachment randomIrrelevantKnowledgeBaseAttachment()
		throws Exception {

		KnowledgeBaseAttachment randomIrrelevantKnowledgeBaseAttachment =
			randomKnowledgeBaseAttachment();

		return randomIrrelevantKnowledgeBaseAttachment;
	}

	protected KnowledgeBaseAttachment randomPatchKnowledgeBaseAttachment()
		throws Exception {

		return randomKnowledgeBaseAttachment();
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

	protected KnowledgeBaseAttachmentResource knowledgeBaseAttachmentResource;
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
		LogFactoryUtil.getLog(
			BaseKnowledgeBaseAttachmentResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.delivery.resource.v1_0.
			KnowledgeBaseAttachmentResource _knowledgeBaseAttachmentResource;

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