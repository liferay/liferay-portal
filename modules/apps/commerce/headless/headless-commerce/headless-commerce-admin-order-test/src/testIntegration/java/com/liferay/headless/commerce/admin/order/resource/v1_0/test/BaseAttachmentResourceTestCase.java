/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.commerce.admin.order.client.dto.v1_0.Attachment;
import com.liferay.headless.commerce.admin.order.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.order.client.pagination.Page;
import com.liferay.headless.commerce.admin.order.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.order.client.resource.v1_0.AttachmentResource;
import com.liferay.headless.commerce.admin.order.client.serdes.v1_0.AttachmentSerDes;
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
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.MultivaluedHashMap;

import java.lang.reflect.Method;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public abstract class BaseAttachmentResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

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

		_attachmentResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		attachmentResource = AttachmentResource.builder(
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

		Attachment attachment1 = randomAttachment();

		String json = objectMapper.writeValueAsString(attachment1);

		Attachment attachment2 = AttachmentSerDes.toDTO(json);

		Assert.assertTrue(equals(attachment1, attachment2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Attachment attachment = randomAttachment();

		String json1 = objectMapper.writeValueAsString(attachment);
		String json2 = AttachmentSerDes.toJSON(attachment);

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

		Attachment attachment = randomAttachment();

		attachment.setAttachment(regex);
		attachment.setExtension(regex);
		attachment.setExternalReferenceCode(regex);
		attachment.setFileName(regex);
		attachment.setTitle(regex);
		attachment.setType(regex);
		attachment.setTypeLabel(regex);
		attachment.setUrl(regex);

		String json = AttachmentSerDes.toJSON(attachment);

		Assert.assertFalse(json.contains(regex));

		attachment = AttachmentSerDes.toDTO(json);

		Assert.assertEquals(regex, attachment.getAttachment());
		Assert.assertEquals(regex, attachment.getExtension());
		Assert.assertEquals(regex, attachment.getExternalReferenceCode());
		Assert.assertEquals(regex, attachment.getFileName());
		Assert.assertEquals(regex, attachment.getTitle());
		Assert.assertEquals(regex, attachment.getType());
		Assert.assertEquals(regex, attachment.getTypeLabel());
		Assert.assertEquals(regex, attachment.getUrl());
	}

	@Test
	public void testDeleteOrderAttachment() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Attachment attachment = testDeleteOrderAttachment_addAttachment();

		assertHttpResponseStatusCode(
			204,
			attachmentResource.deleteOrderAttachmentHttpResponse(
				testDeleteOrderAttachment_getOrderId(), attachment.getId()));

		assertHttpResponseStatusCode(
			404,
			attachmentResource.getOrderAttachmentHttpResponse(
				testDeleteOrderAttachment_getOrderId(), attachment.getId()));
		assertHttpResponseStatusCode(
			404,
			attachmentResource.getOrderAttachmentHttpResponse(
				testDeleteOrderAttachment_getOrderId(), 0L));
	}

	protected Attachment testDeleteOrderAttachment_addAttachment()
		throws Exception {

		return testPostOrderAttachment_addAttachment(randomAttachment());
	}

	protected Long testDeleteOrderAttachment_getOrderId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrderAttachment() throws Exception {

		// No namespace

		Attachment attachment1 =
			testGraphQLDeleteOrderAttachment_addAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrderAttachment",
						new HashMap<String, Object>() {
							{
								put(
									"orderId",
									testGraphQLDeleteOrderAttachment_getOrderId());
								put("attachmentId", attachment1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteOrderAttachment"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"orderAttachment",
					new HashMap<String, Object>() {
						{
							put(
								"orderId",
								testGraphQLDeleteOrderAttachment_getOrderId());
							put("attachmentId", attachment1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Attachment attachment2 =
			testGraphQLDeleteOrderAttachment_addAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"deleteOrderAttachment",
							new HashMap<String, Object>() {
								{
									put(
										"orderId",
										testGraphQLDeleteOrderAttachment_getOrderId());
									put("attachmentId", attachment2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
				"Object/deleteOrderAttachment"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminOrder_v1_0",
					new GraphQLField(
						"orderAttachment",
						new HashMap<String, Object>() {
							{
								put(
									"orderId",
									testGraphQLDeleteOrderAttachment_getOrderId());
								put("attachmentId", attachment2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Long testGraphQLDeleteOrderAttachment_getOrderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Attachment testGraphQLDeleteOrderAttachment_addAttachment()
		throws Exception {

		return testGraphQLAttachment_addAttachment();
	}

	@Test
	public void testDeleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Attachment attachment =
			testDeleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_addAttachment();

		assertHttpResponseStatusCode(
			204,
			attachmentResource.
				deleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_getExternalReferenceCode(
						attachment),
					attachment.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			attachmentResource.
				getOrderByExternalReferenceCodeAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_getExternalReferenceCode(
						attachment),
					attachment.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			attachmentResource.
				getOrderByExternalReferenceCodeAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_getExternalReferenceCode(
						attachment),
					"-"));
	}

	protected Attachment
			testDeleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_addAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_getExternalReferenceCode(
				Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode()
		throws Exception {

		// No namespace

		Attachment attachment1 =
			testGraphQLDeleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_addAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										testGraphQLDeleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_getExternalReferenceCode(
											attachment1) + "\"");
								put(
									"attachmentExternalReferenceCode",
									"\"" +
										attachment1.getExternalReferenceCode() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"orderByExternalReferenceCodeAttachmentByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" +
									testGraphQLDeleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_getExternalReferenceCode(
										attachment1) + "\"");
							put(
								"attachmentExternalReferenceCode",
								"\"" + attachment1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Attachment attachment2 =
			testGraphQLDeleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_addAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"deleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											testGraphQLDeleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_getExternalReferenceCode(
												attachment2) + "\"");
									put(
										"attachmentExternalReferenceCode",
										"\"" +
											attachment2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
				"Object/deleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminOrder_v1_0",
					new GraphQLField(
						"orderByExternalReferenceCodeAttachmentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										testGraphQLDeleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_getExternalReferenceCode(
											attachment2) + "\"");
								put(
									"attachmentExternalReferenceCode",
									"\"" +
										attachment2.getExternalReferenceCode() +
											"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected String
			testGraphQLDeleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_getExternalReferenceCode(
				Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Attachment
			testGraphQLDeleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_addAttachment()
		throws Exception {

		return testGraphQLAttachment_addAttachment();
	}

	@Test
	public void testGetOrderAttachment() throws Exception {
		Attachment postAttachment = testGetOrderAttachment_addAttachment();

		Attachment getAttachment = attachmentResource.getOrderAttachment(
			testGetOrderAttachment_getOrderId(), postAttachment.getId());

		assertEquals(postAttachment, getAttachment);
		assertValid(getAttachment);
	}

	protected Attachment testGetOrderAttachment_addAttachment()
		throws Exception {

		return testPostOrderAttachment_addAttachment(randomAttachment());
	}

	protected Long testGetOrderAttachment_getOrderId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOrderAttachment() throws Exception {
		Attachment attachment = testGraphQLGetOrderAttachment_addAttachment();

		// No namespace

		Assert.assertTrue(
			equals(
				attachment,
				AttachmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"orderAttachment",
								new HashMap<String, Object>() {
									{
										put(
											"orderId",
											testGraphQLGetOrderAttachment_getOrderId());
										put("attachmentId", attachment.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/orderAttachment"))));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Assert.assertTrue(
			equals(
				attachment,
				AttachmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminOrder_v1_0",
								new GraphQLField(
									"orderAttachment",
									new HashMap<String, Object>() {
										{
											put(
												"orderId",
												testGraphQLGetOrderAttachment_getOrderId());
											put(
												"attachmentId",
												attachment.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminOrder_v1_0",
						"Object/orderAttachment"))));
	}

	protected Long testGraphQLGetOrderAttachment_getOrderId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOrderAttachmentNotFound() throws Exception {
		Long irrelevantOrderId = RandomTestUtil.randomLong();
		Long irrelevantAttachmentId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"orderAttachment",
						new HashMap<String, Object>() {
							{
								put("orderId", irrelevantOrderId);
								put("attachmentId", irrelevantAttachmentId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"orderAttachment",
							new HashMap<String, Object>() {
								{
									put("orderId", irrelevantOrderId);
									put("attachmentId", irrelevantAttachmentId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Attachment testGraphQLGetOrderAttachment_addAttachment()
		throws Exception {

		return testGraphQLAttachment_addAttachment();
	}

	@Test
	public void testGetOrderAttachmentsPage() throws Exception {
		Long orderId = testGetOrderAttachmentsPage_getOrderId();
		Long irrelevantOrderId =
			testGetOrderAttachmentsPage_getIrrelevantOrderId();

		Page<Attachment> page = attachmentResource.getOrderAttachmentsPage(
			orderId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantOrderId != null) {
			Attachment irrelevantAttachment =
				testGetOrderAttachmentsPage_addAttachment(
					irrelevantOrderId, randomIrrelevantAttachment());

			page = attachmentResource.getOrderAttachmentsPage(
				irrelevantOrderId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAttachment, (List<Attachment>)page.getItems());
			assertValid(
				page,
				testGetOrderAttachmentsPage_getExpectedActions(
					irrelevantOrderId));
		}

		Attachment attachment1 = testGetOrderAttachmentsPage_addAttachment(
			orderId, randomAttachment());

		Attachment attachment2 = testGetOrderAttachmentsPage_addAttachment(
			orderId, randomAttachment());

		page = attachmentResource.getOrderAttachmentsPage(
			orderId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(attachment1, (List<Attachment>)page.getItems());
		assertContains(attachment2, (List<Attachment>)page.getItems());
		assertValid(
			page, testGetOrderAttachmentsPage_getExpectedActions(orderId));
	}

	protected Map<String, Map<String, String>>
			testGetOrderAttachmentsPage_getExpectedActions(Long orderId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrderAttachmentsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long orderId = testGetOrderAttachmentsPage_getOrderId();

		Attachment attachment1 = randomAttachment();

		attachment1 = testGetOrderAttachmentsPage_addAttachment(
			orderId, attachment1);

		for (EntityField entityField : entityFields) {
			Page<Attachment> page = attachmentResource.getOrderAttachmentsPage(
				orderId, null,
				getFilterString(entityField, "between", attachment1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(attachment1),
				(List<Attachment>)page.getItems());
		}
	}

	@Test
	public void testGetOrderAttachmentsPageWithFilterDoubleEquals()
		throws Exception {

		testGetOrderAttachmentsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetOrderAttachmentsPageWithFilterStringContains()
		throws Exception {

		testGetOrderAttachmentsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrderAttachmentsPageWithFilterStringEquals()
		throws Exception {

		testGetOrderAttachmentsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrderAttachmentsPageWithFilterStringStartsWith()
		throws Exception {

		testGetOrderAttachmentsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetOrderAttachmentsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long orderId = testGetOrderAttachmentsPage_getOrderId();

		Attachment attachment1 = testGetOrderAttachmentsPage_addAttachment(
			orderId, randomAttachment());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Attachment attachment2 = testGetOrderAttachmentsPage_addAttachment(
			orderId, randomAttachment());

		for (EntityField entityField : entityFields) {
			Page<Attachment> page = attachmentResource.getOrderAttachmentsPage(
				orderId, null,
				getFilterString(entityField, operator, attachment1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(attachment1),
				(List<Attachment>)page.getItems());
		}
	}

	@Test
	public void testGetOrderAttachmentsPageWithPagination() throws Exception {
		Long orderId = testGetOrderAttachmentsPage_getOrderId();

		Page<Attachment> attachmentsPage =
			attachmentResource.getOrderAttachmentsPage(
				orderId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(attachmentsPage.getTotalCount());

		Attachment attachment1 = testGetOrderAttachmentsPage_addAttachment(
			orderId, randomAttachment());

		Attachment attachment2 = testGetOrderAttachmentsPage_addAttachment(
			orderId, randomAttachment());

		Attachment attachment3 = testGetOrderAttachmentsPage_addAttachment(
			orderId, randomAttachment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Attachment> page1 = attachmentResource.getOrderAttachmentsPage(
				orderId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(attachment1, (List<Attachment>)page1.getItems());

			Page<Attachment> page2 = attachmentResource.getOrderAttachmentsPage(
				orderId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(attachment2, (List<Attachment>)page2.getItems());

			Page<Attachment> page3 = attachmentResource.getOrderAttachmentsPage(
				orderId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(attachment3, (List<Attachment>)page3.getItems());
		}
		else {
			Page<Attachment> page1 = attachmentResource.getOrderAttachmentsPage(
				orderId, null, null, Pagination.of(1, totalCount + 2), null);

			List<Attachment> attachments1 = (List<Attachment>)page1.getItems();

			Assert.assertEquals(
				attachments1.toString(), totalCount + 2, attachments1.size());

			Page<Attachment> page2 = attachmentResource.getOrderAttachmentsPage(
				orderId, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Attachment> attachments2 = (List<Attachment>)page2.getItems();

			Assert.assertEquals(
				attachments2.toString(), 1, attachments2.size());

			Page<Attachment> page3 = attachmentResource.getOrderAttachmentsPage(
				orderId, null, null, Pagination.of(1, (int)totalCount + 3),
				null);

			assertContains(attachment1, (List<Attachment>)page3.getItems());
			assertContains(attachment2, (List<Attachment>)page3.getItems());
			assertContains(attachment3, (List<Attachment>)page3.getItems());
		}
	}

	@Test
	public void testGetOrderAttachmentsPageWithSortDateTime() throws Exception {
		testGetOrderAttachmentsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, attachment1, attachment2) -> {
				BeanTestUtil.setProperty(
					attachment1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetOrderAttachmentsPageWithSortDouble() throws Exception {
		testGetOrderAttachmentsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, attachment1, attachment2) -> {
				BeanTestUtil.setProperty(
					attachment1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					attachment2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetOrderAttachmentsPageWithSortInteger() throws Exception {
		testGetOrderAttachmentsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, attachment1, attachment2) -> {
				BeanTestUtil.setProperty(attachment1, entityField.getName(), 0);
				BeanTestUtil.setProperty(attachment2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetOrderAttachmentsPageWithSortString() throws Exception {
		testGetOrderAttachmentsPageWithSort(
			EntityField.Type.STRING,
			(entityField, attachment1, attachment2) -> {
				Class<?> clazz = attachment1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						attachment1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						attachment2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						attachment1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						attachment2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						attachment1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						attachment2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetOrderAttachmentsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Attachment, Attachment, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long orderId = testGetOrderAttachmentsPage_getOrderId();

		Attachment attachment1 = randomAttachment();
		Attachment attachment2 = randomAttachment();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, attachment1, attachment2);
		}

		attachment1 = testGetOrderAttachmentsPage_addAttachment(
			orderId, attachment1);

		attachment2 = testGetOrderAttachmentsPage_addAttachment(
			orderId, attachment2);

		Page<Attachment> page = attachmentResource.getOrderAttachmentsPage(
			orderId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Attachment> ascPage =
				attachmentResource.getOrderAttachmentsPage(
					orderId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(attachment1, (List<Attachment>)ascPage.getItems());
			assertContains(attachment2, (List<Attachment>)ascPage.getItems());

			Page<Attachment> descPage =
				attachmentResource.getOrderAttachmentsPage(
					orderId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(attachment2, (List<Attachment>)descPage.getItems());
			assertContains(attachment1, (List<Attachment>)descPage.getItems());
		}
	}

	protected Attachment testGetOrderAttachmentsPage_addAttachment(
			Long orderId, Attachment attachment)
		throws Exception {

		return attachmentResource.postOrderAttachment(orderId, attachment);
	}

	protected Long testGetOrderAttachmentsPage_getOrderId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetOrderAttachmentsPage_getIrrelevantOrderId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetOrderByExternalReferenceCodeAttachmentByExternalReferenceCode()
		throws Exception {

		Attachment postAttachment =
			testGetOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_addAttachment();

		Attachment getAttachment =
			attachmentResource.
				getOrderByExternalReferenceCodeAttachmentByExternalReferenceCode(
					testGetOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_getExternalReferenceCode(
						postAttachment),
					postAttachment.getExternalReferenceCode());

		assertEquals(postAttachment, getAttachment);
		assertValid(getAttachment);
	}

	protected Attachment
			testGetOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_addAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_getExternalReferenceCode(
				Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOrderByExternalReferenceCodeAttachmentByExternalReferenceCode()
		throws Exception {

		Attachment attachment =
			testGraphQLGetOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_addAttachment();

		// No namespace

		Assert.assertTrue(
			equals(
				attachment,
				AttachmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"orderByExternalReferenceCodeAttachmentByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												testGraphQLGetOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_getExternalReferenceCode(
													attachment) + "\"");
										put(
											"attachmentExternalReferenceCode",
											"\"" +
												attachment.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/orderByExternalReferenceCodeAttachmentByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Assert.assertTrue(
			equals(
				attachment,
				AttachmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminOrder_v1_0",
								new GraphQLField(
									"orderByExternalReferenceCodeAttachmentByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													testGraphQLGetOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_getExternalReferenceCode(
														attachment) + "\"");
											put(
												"attachmentExternalReferenceCode",
												"\"" +
													attachment.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminOrder_v1_0",
						"Object/orderByExternalReferenceCodeAttachmentByExternalReferenceCode"))));
	}

	protected String
			testGraphQLGetOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_getExternalReferenceCode(
				Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOrderByExternalReferenceCodeAttachmentByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";
		String irrelevantAttachmentExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"orderByExternalReferenceCodeAttachmentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
								put(
									"attachmentExternalReferenceCode",
									irrelevantAttachmentExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"orderByExternalReferenceCodeAttachmentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
									put(
										"attachmentExternalReferenceCode",
										irrelevantAttachmentExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Attachment
			testGraphQLGetOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_addAttachment()
		throws Exception {

		return testGraphQLAttachment_addAttachment();
	}

	@Test
	public void testGetOrderByExternalReferenceCodeAttachmentsPage()
		throws Exception {

		String externalReferenceCode =
			testGetOrderByExternalReferenceCodeAttachmentsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetOrderByExternalReferenceCodeAttachmentsPage_getIrrelevantExternalReferenceCode();

		Page<Attachment> page =
			attachmentResource.getOrderByExternalReferenceCodeAttachmentsPage(
				externalReferenceCode, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			Attachment irrelevantAttachment =
				testGetOrderByExternalReferenceCodeAttachmentsPage_addAttachment(
					irrelevantExternalReferenceCode,
					randomIrrelevantAttachment());

			page =
				attachmentResource.
					getOrderByExternalReferenceCodeAttachmentsPage(
						irrelevantExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAttachment, (List<Attachment>)page.getItems());
			assertValid(
				page,
				testGetOrderByExternalReferenceCodeAttachmentsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		Attachment attachment1 =
			testGetOrderByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, randomAttachment());

		Attachment attachment2 =
			testGetOrderByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, randomAttachment());

		page =
			attachmentResource.getOrderByExternalReferenceCodeAttachmentsPage(
				externalReferenceCode, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(attachment1, (List<Attachment>)page.getItems());
		assertContains(attachment2, (List<Attachment>)page.getItems());
		assertValid(
			page,
			testGetOrderByExternalReferenceCodeAttachmentsPage_getExpectedActions(
				externalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetOrderByExternalReferenceCodeAttachmentsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrderByExternalReferenceCodeAttachmentsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetOrderByExternalReferenceCodeAttachmentsPage_getExternalReferenceCode();

		Attachment attachment1 = randomAttachment();

		attachment1 =
			testGetOrderByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, attachment1);

		for (EntityField entityField : entityFields) {
			Page<Attachment> page =
				attachmentResource.
					getOrderByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode, null,
						getFilterString(entityField, "between", attachment1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(attachment1),
				(List<Attachment>)page.getItems());
		}
	}

	@Test
	public void testGetOrderByExternalReferenceCodeAttachmentsPageWithFilterDoubleEquals()
		throws Exception {

		testGetOrderByExternalReferenceCodeAttachmentsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetOrderByExternalReferenceCodeAttachmentsPageWithFilterStringContains()
		throws Exception {

		testGetOrderByExternalReferenceCodeAttachmentsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrderByExternalReferenceCodeAttachmentsPageWithFilterStringEquals()
		throws Exception {

		testGetOrderByExternalReferenceCodeAttachmentsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrderByExternalReferenceCodeAttachmentsPageWithFilterStringStartsWith()
		throws Exception {

		testGetOrderByExternalReferenceCodeAttachmentsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetOrderByExternalReferenceCodeAttachmentsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetOrderByExternalReferenceCodeAttachmentsPage_getExternalReferenceCode();

		Attachment attachment1 =
			testGetOrderByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, randomAttachment());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Attachment attachment2 =
			testGetOrderByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, randomAttachment());

		for (EntityField entityField : entityFields) {
			Page<Attachment> page =
				attachmentResource.
					getOrderByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode, null,
						getFilterString(entityField, operator, attachment1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(attachment1),
				(List<Attachment>)page.getItems());
		}
	}

	@Test
	public void testGetOrderByExternalReferenceCodeAttachmentsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetOrderByExternalReferenceCodeAttachmentsPage_getExternalReferenceCode();

		Page<Attachment> attachmentsPage =
			attachmentResource.getOrderByExternalReferenceCodeAttachmentsPage(
				externalReferenceCode, null, null, null, null);

		int totalCount = GetterUtil.getInteger(attachmentsPage.getTotalCount());

		Attachment attachment1 =
			testGetOrderByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, randomAttachment());

		Attachment attachment2 =
			testGetOrderByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, randomAttachment());

		Attachment attachment3 =
			testGetOrderByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, randomAttachment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Attachment> page1 =
				attachmentResource.
					getOrderByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(attachment1, (List<Attachment>)page1.getItems());

			Page<Attachment> page2 =
				attachmentResource.
					getOrderByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(attachment2, (List<Attachment>)page2.getItems());

			Page<Attachment> page3 =
				attachmentResource.
					getOrderByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(attachment3, (List<Attachment>)page3.getItems());
		}
		else {
			Page<Attachment> page1 =
				attachmentResource.
					getOrderByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<Attachment> attachments1 = (List<Attachment>)page1.getItems();

			Assert.assertEquals(
				attachments1.toString(), totalCount + 2, attachments1.size());

			Page<Attachment> page2 =
				attachmentResource.
					getOrderByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Attachment> attachments2 = (List<Attachment>)page2.getItems();

			Assert.assertEquals(
				attachments2.toString(), 1, attachments2.size());

			Page<Attachment> page3 =
				attachmentResource.
					getOrderByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(attachment1, (List<Attachment>)page3.getItems());
			assertContains(attachment2, (List<Attachment>)page3.getItems());
			assertContains(attachment3, (List<Attachment>)page3.getItems());
		}
	}

	@Test
	public void testGetOrderByExternalReferenceCodeAttachmentsPageWithSortDateTime()
		throws Exception {

		testGetOrderByExternalReferenceCodeAttachmentsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, attachment1, attachment2) -> {
				BeanTestUtil.setProperty(
					attachment1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetOrderByExternalReferenceCodeAttachmentsPageWithSortDouble()
		throws Exception {

		testGetOrderByExternalReferenceCodeAttachmentsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, attachment1, attachment2) -> {
				BeanTestUtil.setProperty(
					attachment1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					attachment2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetOrderByExternalReferenceCodeAttachmentsPageWithSortInteger()
		throws Exception {

		testGetOrderByExternalReferenceCodeAttachmentsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, attachment1, attachment2) -> {
				BeanTestUtil.setProperty(attachment1, entityField.getName(), 0);
				BeanTestUtil.setProperty(attachment2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetOrderByExternalReferenceCodeAttachmentsPageWithSortString()
		throws Exception {

		testGetOrderByExternalReferenceCodeAttachmentsPageWithSort(
			EntityField.Type.STRING,
			(entityField, attachment1, attachment2) -> {
				Class<?> clazz = attachment1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						attachment1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						attachment2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						attachment1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						attachment2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						attachment1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						attachment2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetOrderByExternalReferenceCodeAttachmentsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Attachment, Attachment, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetOrderByExternalReferenceCodeAttachmentsPage_getExternalReferenceCode();

		Attachment attachment1 = randomAttachment();
		Attachment attachment2 = randomAttachment();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, attachment1, attachment2);
		}

		attachment1 =
			testGetOrderByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, attachment1);

		attachment2 =
			testGetOrderByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, attachment2);

		Page<Attachment> page =
			attachmentResource.getOrderByExternalReferenceCodeAttachmentsPage(
				externalReferenceCode, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Attachment> ascPage =
				attachmentResource.
					getOrderByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(attachment1, (List<Attachment>)ascPage.getItems());
			assertContains(attachment2, (List<Attachment>)ascPage.getItems());

			Page<Attachment> descPage =
				attachmentResource.
					getOrderByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(attachment2, (List<Attachment>)descPage.getItems());
			assertContains(attachment1, (List<Attachment>)descPage.getItems());
		}
	}

	protected Attachment
			testGetOrderByExternalReferenceCodeAttachmentsPage_addAttachment(
				String externalReferenceCode, Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrderByExternalReferenceCodeAttachmentsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrderByExternalReferenceCodeAttachmentsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetOrderByExternalReferenceCodeAttachmentsPage()
		throws Exception {

		String externalReferenceCode =
			testGetOrderByExternalReferenceCodeAttachmentsPage_getExternalReferenceCode();

		GraphQLField graphQLField = new GraphQLField(
			"orderByExternalReferenceCodeAttachments",
			new HashMap<String, Object>() {
				{
					put(
						"externalReferenceCode",
						"\"" + externalReferenceCode + "\"");
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject orderByExternalReferenceCodeAttachmentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/orderByExternalReferenceCodeAttachments");

		long totalCount =
			orderByExternalReferenceCodeAttachmentsJSONObject.getLong(
				"totalCount");

		Attachment attachment1 =
			testGraphQLGetOrderByExternalReferenceCodeAttachmentsPageOrderAttachment_addAttachment(
				externalReferenceCode, randomAttachment());

		Attachment attachment2 =
			testGraphQLGetOrderByExternalReferenceCodeAttachmentsPageOrderAttachment_addAttachment(
				externalReferenceCode, randomAttachment());

		orderByExternalReferenceCodeAttachmentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/orderByExternalReferenceCodeAttachments");

		Assert.assertEquals(
			totalCount + 2,
			orderByExternalReferenceCodeAttachmentsJSONObject.getLong(
				"totalCount"));

		assertContains(
			attachment1,
			Arrays.asList(
				AttachmentSerDes.toDTOs(
					orderByExternalReferenceCodeAttachmentsJSONObject.getString(
						"items"))));
		assertContains(
			attachment2,
			Arrays.asList(
				AttachmentSerDes.toDTOs(
					orderByExternalReferenceCodeAttachmentsJSONObject.getString(
						"items"))));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		orderByExternalReferenceCodeAttachmentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
				"JSONObject/orderByExternalReferenceCodeAttachments");

		Assert.assertEquals(
			totalCount + 2,
			orderByExternalReferenceCodeAttachmentsJSONObject.getLong(
				"totalCount"));

		assertContains(
			attachment1,
			Arrays.asList(
				AttachmentSerDes.toDTOs(
					orderByExternalReferenceCodeAttachmentsJSONObject.getString(
						"items"))));
		assertContains(
			attachment2,
			Arrays.asList(
				AttachmentSerDes.toDTOs(
					orderByExternalReferenceCodeAttachmentsJSONObject.getString(
						"items"))));
	}

	protected Attachment
			testGraphQLGetOrderByExternalReferenceCodeAttachmentsPageOrderAttachment_addAttachment(
				String externalReferenceCode, Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchOrderAttachment() throws Exception {
		Attachment postAttachment = testPatchOrderAttachment_addAttachment();

		Attachment randomPatchAttachment = randomPatchAttachment();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Attachment patchAttachment = attachmentResource.patchOrderAttachment(
			null, postAttachment.getId(), randomPatchAttachment);

		Attachment expectedPatchAttachment = postAttachment.clone();

		BeanTestUtil.copyProperties(
			randomPatchAttachment, expectedPatchAttachment);

		Attachment getAttachment = attachmentResource.getOrderAttachment(
			null, patchAttachment.getId());

		assertEquals(expectedPatchAttachment, getAttachment);
		assertValid(getAttachment);
	}

	protected Attachment testPatchOrderAttachment_addAttachment()
		throws Exception {

		return testPostOrderAttachment_addAttachment(randomAttachment());
	}

	@Test
	public void testPatchOrderByExternalReferenceCodeAttachmentByExternalReferenceCode()
		throws Exception {

		Attachment postAttachment =
			testPatchOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_addAttachment();

		Attachment randomPatchAttachment = randomPatchAttachment();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Attachment patchAttachment =
			attachmentResource.
				patchOrderByExternalReferenceCodeAttachmentByExternalReferenceCode(
					postAttachment.getExternalReferenceCode(),
					postAttachment.getExternalReferenceCode(),
					randomPatchAttachment);

		Attachment expectedPatchAttachment = postAttachment.clone();

		BeanTestUtil.copyProperties(
			randomPatchAttachment, expectedPatchAttachment);

		Attachment getAttachment =
			attachmentResource.
				getOrderByExternalReferenceCodeAttachmentByExternalReferenceCode(
					patchAttachment.getExternalReferenceCode(),
					patchAttachment.getExternalReferenceCode());

		assertEquals(expectedPatchAttachment, getAttachment);
		assertValid(getAttachment);
	}

	protected Attachment
			testPatchOrderByExternalReferenceCodeAttachmentByExternalReferenceCode_addAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOrderAttachment() throws Exception {
		Attachment randomAttachment = randomAttachment();

		Attachment postAttachment = testPostOrderAttachment_addAttachment(
			randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
	}

	protected Attachment testPostOrderAttachment_addAttachment(
			Attachment attachment)
		throws Exception {

		return attachmentResource.postOrderAttachment(
			testGetOrderAttachmentsPage_getOrderId(), attachment);
	}

	@Test
	public void testPostOrderByExternalReferenceCodeAttachment()
		throws Exception {

		Attachment randomAttachment = randomAttachment();

		Attachment postAttachment =
			testPostOrderByExternalReferenceCodeAttachment_addAttachment(
				randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
	}

	protected Attachment
			testPostOrderByExternalReferenceCodeAttachment_addAttachment(
				Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostOrderByExternalReferenceCodeAttachment()
		throws Exception {

		Attachment randomAttachment = randomAttachment();

		Attachment attachment = testGraphQLOrderAttachment_addAttachment(
			testGraphQLPostOrderByExternalReferenceCodeAttachment_getOrderId(),
			randomAttachment);

		Assert.assertTrue(equals(randomAttachment, attachment));
	}

	protected Long
			testGraphQLPostOrderByExternalReferenceCodeAttachment_getOrderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected Attachment testGraphQLAttachment_addAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Attachment testGraphQLOrderAttachment_addAttachment()
		throws Exception {

		return testGraphQLOrderAttachment_addAttachment(
			testGraphQLOrderAttachment_getOrderId(), randomAttachment());
	}

	protected Long testGraphQLOrderAttachment_getOrderId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Attachment testGraphQLOrderAttachment_addAttachment(
			Long orderId, Attachment attachment)
		throws Exception {

		JSONDeserializer<Attachment> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(Attachment.class)) {

			if (getGraphQLValue(field.get(attachment)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(attachment)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createAttachment",
						new HashMap<String, Object>() {
							{
								put("orderId", orderId);
								put("attachment", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createAttachment"),
			Attachment.class);
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
		Attachment attachment, List<Attachment> attachments) {

		boolean contains = false;

		for (Attachment item : attachments) {
			if (equals(attachment, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			attachments + " does not contain " + attachment, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		Attachment attachment1, Attachment attachment2) {

		Assert.assertTrue(
			attachment1 + " does not equal " + attachment2,
			equals(attachment1, attachment2));
	}

	protected void assertEquals(
		List<Attachment> attachments1, List<Attachment> attachments2) {

		Assert.assertEquals(attachments1.size(), attachments2.size());

		for (int i = 0; i < attachments1.size(); i++) {
			Attachment attachment1 = attachments1.get(i);
			Attachment attachment2 = attachments2.get(i);

			assertEquals(attachment1, attachment2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Attachment> attachments1, List<Attachment> attachments2) {

		Assert.assertEquals(attachments1.size(), attachments2.size());

		for (Attachment attachment1 : attachments1) {
			boolean contains = false;

			for (Attachment attachment2 : attachments2) {
				if (equals(attachment1, attachment2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				attachments2 + " does not contain " + attachment1, contains);
		}
	}

	protected void assertValid(Attachment attachment) throws Exception {
		boolean valid = true;

		if (attachment.getDateModified() == null) {
			valid = false;
		}

		if (attachment.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (attachment.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("attachment", additionalAssertFieldName)) {
				if (attachment.getAttachment() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("extension", additionalAssertFieldName)) {
				if (attachment.getExtension() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (attachment.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("fileName", additionalAssertFieldName)) {
				if (attachment.getFileName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (attachment.getPriority() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("restricted", additionalAssertFieldName)) {
				if (attachment.getRestricted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (attachment.getTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (attachment.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("typeLabel", additionalAssertFieldName)) {
				if (attachment.getTypeLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("url", additionalAssertFieldName)) {
				if (attachment.getUrl() == null) {
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

	protected void assertValid(Page<Attachment> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Attachment> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Attachment> attachments = page.getItems();

		int size = attachments.size();

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
					com.liferay.headless.commerce.admin.order.dto.v1_0.
						Attachment.class)) {

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

	protected boolean equals(Attachment attachment1, Attachment attachment2) {
		if (attachment1 == attachment2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)attachment1.getActions(),
						(Map)attachment2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("attachment", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getAttachment(),
						attachment2.getAttachment())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getDateModified(),
						attachment2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("extension", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getExtension(),
						attachment2.getExtension())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						attachment1.getExternalReferenceCode(),
						attachment2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fileName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getFileName(), attachment2.getFileName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getId(), attachment2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getPriority(), attachment2.getPriority())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("restricted", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getRestricted(),
						attachment2.getRestricted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getTitle(), attachment2.getTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getType(), attachment2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("typeLabel", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getTypeLabel(),
						attachment2.getTypeLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("url", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getUrl(), attachment2.getUrl())) {

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

		if (!(_attachmentResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_attachmentResource;

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
		EntityField entityField, String operator, Attachment attachment) {

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

		if (entityFieldName.equals("attachment")) {
			Object object = attachment.getAttachment();

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

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = attachment.getDateModified();

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

				sb.append(_format.format(attachment.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("extension")) {
			Object object = attachment.getExtension();

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
			Object object = attachment.getExternalReferenceCode();

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
			Object object = attachment.getFileName();

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

		if (entityFieldName.equals("priority")) {
			sb.append(String.valueOf(attachment.getPriority()));

			return sb.toString();
		}

		if (entityFieldName.equals("restricted")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("title")) {
			Object object = attachment.getTitle();

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

		if (entityFieldName.equals("type")) {
			Object object = attachment.getType();

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
			Object object = attachment.getTypeLabel();

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

		if (entityFieldName.equals("url")) {
			Object object = attachment.getUrl();

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

	protected Attachment randomAttachment() throws Exception {
		return new Attachment() {
			{
				attachment = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateModified = RandomTestUtil.nextDate();
				extension = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				fileName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				priority = RandomTestUtil.randomDouble();
				restricted = RandomTestUtil.randomBoolean();
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
				type = StringUtil.toLowerCase(RandomTestUtil.randomString());
				typeLabel = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				url = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected Attachment randomIrrelevantAttachment() throws Exception {
		Attachment randomIrrelevantAttachment = randomAttachment();

		return randomIrrelevantAttachment;
	}

	protected Attachment randomPatchAttachment() throws Exception {
		return randomAttachment();
	}

	protected AttachmentResource attachmentResource;
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
		LogFactoryUtil.getLog(BaseAttachmentResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.admin.order.resource.v1_0.
			AttachmentResource _attachmentResource;

}
// LIFERAY-REST-BUILDER-HASH:-1158376067