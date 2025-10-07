/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Attachment;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.AttachmentResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.AttachmentSerDes;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Zoltán Takács
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
		attachment.setCdnURL(regex);
		attachment.setContentType(regex);
		attachment.setExternalReferenceCode(regex);
		attachment.setFileEntryExternalReferenceCode(regex);
		attachment.setFileEntryGroupExternalReferenceCode(regex);
		attachment.setSrc(regex);

		String json = AttachmentSerDes.toJSON(attachment);

		Assert.assertFalse(json.contains(regex));

		attachment = AttachmentSerDes.toDTO(json);

		Assert.assertEquals(regex, attachment.getAttachment());
		Assert.assertEquals(regex, attachment.getCdnURL());
		Assert.assertEquals(regex, attachment.getContentType());
		Assert.assertEquals(regex, attachment.getExternalReferenceCode());
		Assert.assertEquals(
			regex, attachment.getFileEntryExternalReferenceCode());
		Assert.assertEquals(
			regex, attachment.getFileEntryGroupExternalReferenceCode());
		Assert.assertEquals(regex, attachment.getSrc());
	}

	@Test
	public void testDeleteAttachment() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Attachment attachment = testDeleteAttachment_addAttachment();

		assertHttpResponseStatusCode(
			204,
			attachmentResource.deleteAttachmentHttpResponse(
				attachment.getId()));
	}

	protected Attachment testDeleteAttachment_addAttachment() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteAttachment() throws Exception {

		// No namespace

		Attachment attachment1 = testGraphQLDeleteAttachment_addAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAttachment",
						new HashMap<String, Object>() {
							{
								put("id", attachment1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteAttachment"));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Attachment attachment2 = testGraphQLDeleteAttachment_addAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteAttachment",
							new HashMap<String, Object>() {
								{
									put("id", attachment2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteAttachment"));
	}

	protected Attachment testGraphQLDeleteAttachment_addAttachment()
		throws Exception {

		return testGraphQLAttachment_addAttachment();
	}

	@Test
	public void testDeleteAttachmentBatch() throws Exception {
		Attachment attachment1 = testDeleteAttachmentBatch_addAttachment();

		testDeleteAttachmentBatch_deleteAttachment(
			202, attachment1.getExternalReferenceCode(), null);

		attachment1 = testDeleteAttachmentBatch_addAttachment();

		testDeleteAttachmentBatch_deleteAttachment(
			202, null, attachment1.getId());

		attachment1 = testDeleteAttachmentBatch_addAttachment();
		Attachment attachment2 = testDeleteAttachmentBatch_addAttachment();

		testDeleteAttachmentBatch_deleteAttachment(
			202, attachment2.getExternalReferenceCode(), attachment1.getId());

		testDeleteAttachmentBatch_deleteAttachment(
			202, attachment2.getExternalReferenceCode(), attachment1.getId());
	}

	protected Attachment testDeleteAttachmentBatch_addAttachment()
		throws Exception {

		return testDeleteAttachment_addAttachment();
	}

	protected void testDeleteAttachmentBatch_deleteAttachment(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			attachmentResource.deleteAttachmentBatchHttpResponse(
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
	public void testDeleteAttachmentByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Attachment attachment =
			testDeleteAttachmentByExternalReferenceCode_addAttachment();

		assertHttpResponseStatusCode(
			204,
			attachmentResource.
				deleteAttachmentByExternalReferenceCodeHttpResponse(
					attachment.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			attachmentResource.getAttachmentByExternalReferenceCodeHttpResponse(
				attachment.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			attachmentResource.getAttachmentByExternalReferenceCodeHttpResponse(
				"-"));
	}

	protected Attachment
			testDeleteAttachmentByExternalReferenceCode_addAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteAttachmentByExternalReferenceCode()
		throws Exception {

		// No namespace

		Attachment attachment1 =
			testGraphQLDeleteAttachmentByExternalReferenceCode_addAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAttachmentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										attachment1.getExternalReferenceCode() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteAttachmentByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"attachmentByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + attachment1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Attachment attachment2 =
			testGraphQLDeleteAttachmentByExternalReferenceCode_addAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteAttachmentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											attachment2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteAttachmentByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"attachmentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										attachment2.getExternalReferenceCode() +
											"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Attachment
			testGraphQLDeleteAttachmentByExternalReferenceCode_addAttachment()
		throws Exception {

		return testGraphQLAttachment_addAttachment();
	}

	@Test
	public void testGetAttachmentByExternalReferenceCode() throws Exception {
		Attachment postAttachment =
			testGetAttachmentByExternalReferenceCode_addAttachment();

		Attachment getAttachment =
			attachmentResource.getAttachmentByExternalReferenceCode(
				postAttachment.getExternalReferenceCode());

		assertEquals(postAttachment, getAttachment);
		assertValid(getAttachment);
	}

	protected Attachment
			testGetAttachmentByExternalReferenceCode_addAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAttachmentByExternalReferenceCode()
		throws Exception {

		Attachment attachment =
			testGraphQLGetAttachmentByExternalReferenceCode_addAttachment();

		// No namespace

		Assert.assertTrue(
			equals(
				attachment,
				AttachmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"attachmentByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												attachment.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/attachmentByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				attachment,
				AttachmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"attachmentByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													attachment.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/attachmentByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetAttachmentByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"attachmentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"attachmentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Attachment
			testGraphQLGetAttachmentByExternalReferenceCode_addAttachment()
		throws Exception {

		return testGraphQLAttachment_addAttachment();
	}

	@Test
	public void testGetProductByExternalReferenceCodeAttachmentsPage()
		throws Exception {

		String externalReferenceCode =
			testGetProductByExternalReferenceCodeAttachmentsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetProductByExternalReferenceCodeAttachmentsPage_getIrrelevantExternalReferenceCode();

		Page<Attachment> page =
			attachmentResource.getProductByExternalReferenceCodeAttachmentsPage(
				externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			Attachment irrelevantAttachment =
				testGetProductByExternalReferenceCodeAttachmentsPage_addAttachment(
					irrelevantExternalReferenceCode,
					randomIrrelevantAttachment());

			page =
				attachmentResource.
					getProductByExternalReferenceCodeAttachmentsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAttachment, (List<Attachment>)page.getItems());
			assertValid(
				page,
				testGetProductByExternalReferenceCodeAttachmentsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		Attachment attachment1 =
			testGetProductByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, randomAttachment());

		Attachment attachment2 =
			testGetProductByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, randomAttachment());

		page =
			attachmentResource.getProductByExternalReferenceCodeAttachmentsPage(
				externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(attachment1, (List<Attachment>)page.getItems());
		assertContains(attachment2, (List<Attachment>)page.getItems());
		assertValid(
			page,
			testGetProductByExternalReferenceCodeAttachmentsPage_getExpectedActions(
				externalReferenceCode));

		attachmentResource.deleteAttachment(attachment1.getId());

		attachmentResource.deleteAttachment(attachment2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetProductByExternalReferenceCodeAttachmentsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductByExternalReferenceCodeAttachmentsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetProductByExternalReferenceCodeAttachmentsPage_getExternalReferenceCode();

		Page<Attachment> attachmentsPage =
			attachmentResource.getProductByExternalReferenceCodeAttachmentsPage(
				externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(attachmentsPage.getTotalCount());

		Attachment attachment1 =
			testGetProductByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, randomAttachment());

		Attachment attachment2 =
			testGetProductByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, randomAttachment());

		Attachment attachment3 =
			testGetProductByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, randomAttachment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Attachment> page1 =
				attachmentResource.
					getProductByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(attachment1, (List<Attachment>)page1.getItems());

			Page<Attachment> page2 =
				attachmentResource.
					getProductByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(attachment2, (List<Attachment>)page2.getItems());

			Page<Attachment> page3 =
				attachmentResource.
					getProductByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(attachment3, (List<Attachment>)page3.getItems());
		}
		else {
			Page<Attachment> page1 =
				attachmentResource.
					getProductByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<Attachment> attachments1 = (List<Attachment>)page1.getItems();

			Assert.assertEquals(
				attachments1.toString(), totalCount + 2, attachments1.size());

			Page<Attachment> page2 =
				attachmentResource.
					getProductByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Attachment> attachments2 = (List<Attachment>)page2.getItems();

			Assert.assertEquals(
				attachments2.toString(), 1, attachments2.size());

			Page<Attachment> page3 =
				attachmentResource.
					getProductByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(attachment1, (List<Attachment>)page3.getItems());
			assertContains(attachment2, (List<Attachment>)page3.getItems());
			assertContains(attachment3, (List<Attachment>)page3.getItems());
		}
	}

	protected Attachment
			testGetProductByExternalReferenceCodeAttachmentsPage_addAttachment(
				String externalReferenceCode, Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductByExternalReferenceCodeAttachmentsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductByExternalReferenceCodeAttachmentsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetProductByExternalReferenceCodeImagesPage()
		throws Exception {

		String externalReferenceCode =
			testGetProductByExternalReferenceCodeImagesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetProductByExternalReferenceCodeImagesPage_getIrrelevantExternalReferenceCode();

		Page<Attachment> page =
			attachmentResource.getProductByExternalReferenceCodeImagesPage(
				externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			Attachment irrelevantAttachment =
				testGetProductByExternalReferenceCodeImagesPage_addAttachment(
					irrelevantExternalReferenceCode,
					randomIrrelevantAttachment());

			page =
				attachmentResource.getProductByExternalReferenceCodeImagesPage(
					irrelevantExternalReferenceCode,
					Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAttachment, (List<Attachment>)page.getItems());
			assertValid(
				page,
				testGetProductByExternalReferenceCodeImagesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		Attachment attachment1 =
			testGetProductByExternalReferenceCodeImagesPage_addAttachment(
				externalReferenceCode, randomAttachment());

		Attachment attachment2 =
			testGetProductByExternalReferenceCodeImagesPage_addAttachment(
				externalReferenceCode, randomAttachment());

		page = attachmentResource.getProductByExternalReferenceCodeImagesPage(
			externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(attachment1, (List<Attachment>)page.getItems());
		assertContains(attachment2, (List<Attachment>)page.getItems());
		assertValid(
			page,
			testGetProductByExternalReferenceCodeImagesPage_getExpectedActions(
				externalReferenceCode));

		attachmentResource.deleteAttachment(attachment1.getId());

		attachmentResource.deleteAttachment(attachment2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetProductByExternalReferenceCodeImagesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductByExternalReferenceCodeImagesPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetProductByExternalReferenceCodeImagesPage_getExternalReferenceCode();

		Page<Attachment> attachmentsPage =
			attachmentResource.getProductByExternalReferenceCodeImagesPage(
				externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(attachmentsPage.getTotalCount());

		Attachment attachment1 =
			testGetProductByExternalReferenceCodeImagesPage_addAttachment(
				externalReferenceCode, randomAttachment());

		Attachment attachment2 =
			testGetProductByExternalReferenceCodeImagesPage_addAttachment(
				externalReferenceCode, randomAttachment());

		Attachment attachment3 =
			testGetProductByExternalReferenceCodeImagesPage_addAttachment(
				externalReferenceCode, randomAttachment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Attachment> page1 =
				attachmentResource.getProductByExternalReferenceCodeImagesPage(
					externalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(attachment1, (List<Attachment>)page1.getItems());

			Page<Attachment> page2 =
				attachmentResource.getProductByExternalReferenceCodeImagesPage(
					externalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(attachment2, (List<Attachment>)page2.getItems());

			Page<Attachment> page3 =
				attachmentResource.getProductByExternalReferenceCodeImagesPage(
					externalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(attachment3, (List<Attachment>)page3.getItems());
		}
		else {
			Page<Attachment> page1 =
				attachmentResource.getProductByExternalReferenceCodeImagesPage(
					externalReferenceCode, Pagination.of(1, totalCount + 2));

			List<Attachment> attachments1 = (List<Attachment>)page1.getItems();

			Assert.assertEquals(
				attachments1.toString(), totalCount + 2, attachments1.size());

			Page<Attachment> page2 =
				attachmentResource.getProductByExternalReferenceCodeImagesPage(
					externalReferenceCode, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Attachment> attachments2 = (List<Attachment>)page2.getItems();

			Assert.assertEquals(
				attachments2.toString(), 1, attachments2.size());

			Page<Attachment> page3 =
				attachmentResource.getProductByExternalReferenceCodeImagesPage(
					externalReferenceCode,
					Pagination.of(1, (int)totalCount + 3));

			assertContains(attachment1, (List<Attachment>)page3.getItems());
			assertContains(attachment2, (List<Attachment>)page3.getItems());
			assertContains(attachment3, (List<Attachment>)page3.getItems());
		}
	}

	protected Attachment
			testGetProductByExternalReferenceCodeImagesPage_addAttachment(
				String externalReferenceCode, Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductByExternalReferenceCodeImagesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductByExternalReferenceCodeImagesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetProductIdAttachmentsPage() throws Exception {
		Long id = testGetProductIdAttachmentsPage_getId();
		Long irrelevantId = testGetProductIdAttachmentsPage_getIrrelevantId();

		Page<Attachment> page = attachmentResource.getProductIdAttachmentsPage(
			id, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			Attachment irrelevantAttachment =
				testGetProductIdAttachmentsPage_addAttachment(
					irrelevantId, randomIrrelevantAttachment());

			page = attachmentResource.getProductIdAttachmentsPage(
				irrelevantId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAttachment, (List<Attachment>)page.getItems());
			assertValid(
				page,
				testGetProductIdAttachmentsPage_getExpectedActions(
					irrelevantId));
		}

		Attachment attachment1 = testGetProductIdAttachmentsPage_addAttachment(
			id, randomAttachment());

		Attachment attachment2 = testGetProductIdAttachmentsPage_addAttachment(
			id, randomAttachment());

		page = attachmentResource.getProductIdAttachmentsPage(
			id, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(attachment1, (List<Attachment>)page.getItems());
		assertContains(attachment2, (List<Attachment>)page.getItems());
		assertValid(
			page, testGetProductIdAttachmentsPage_getExpectedActions(id));

		attachmentResource.deleteAttachment(attachment1.getId());

		attachmentResource.deleteAttachment(attachment2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetProductIdAttachmentsPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductIdAttachmentsPageWithPagination()
		throws Exception {

		Long id = testGetProductIdAttachmentsPage_getId();

		Page<Attachment> attachmentsPage =
			attachmentResource.getProductIdAttachmentsPage(id, null);

		int totalCount = GetterUtil.getInteger(attachmentsPage.getTotalCount());

		Attachment attachment1 = testGetProductIdAttachmentsPage_addAttachment(
			id, randomAttachment());

		Attachment attachment2 = testGetProductIdAttachmentsPage_addAttachment(
			id, randomAttachment());

		Attachment attachment3 = testGetProductIdAttachmentsPage_addAttachment(
			id, randomAttachment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Attachment> page1 =
				attachmentResource.getProductIdAttachmentsPage(
					id,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(attachment1, (List<Attachment>)page1.getItems());

			Page<Attachment> page2 =
				attachmentResource.getProductIdAttachmentsPage(
					id,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(attachment2, (List<Attachment>)page2.getItems());

			Page<Attachment> page3 =
				attachmentResource.getProductIdAttachmentsPage(
					id,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(attachment3, (List<Attachment>)page3.getItems());
		}
		else {
			Page<Attachment> page1 =
				attachmentResource.getProductIdAttachmentsPage(
					id, Pagination.of(1, totalCount + 2));

			List<Attachment> attachments1 = (List<Attachment>)page1.getItems();

			Assert.assertEquals(
				attachments1.toString(), totalCount + 2, attachments1.size());

			Page<Attachment> page2 =
				attachmentResource.getProductIdAttachmentsPage(
					id, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Attachment> attachments2 = (List<Attachment>)page2.getItems();

			Assert.assertEquals(
				attachments2.toString(), 1, attachments2.size());

			Page<Attachment> page3 =
				attachmentResource.getProductIdAttachmentsPage(
					id, Pagination.of(1, (int)totalCount + 3));

			assertContains(attachment1, (List<Attachment>)page3.getItems());
			assertContains(attachment2, (List<Attachment>)page3.getItems());
			assertContains(attachment3, (List<Attachment>)page3.getItems());
		}
	}

	protected Attachment testGetProductIdAttachmentsPage_addAttachment(
			Long id, Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProductIdAttachmentsPage_getId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProductIdAttachmentsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetProductIdImagesPage() throws Exception {
		Long id = testGetProductIdImagesPage_getId();
		Long irrelevantId = testGetProductIdImagesPage_getIrrelevantId();

		Page<Attachment> page = attachmentResource.getProductIdImagesPage(
			id, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			Attachment irrelevantAttachment =
				testGetProductIdImagesPage_addAttachment(
					irrelevantId, randomIrrelevantAttachment());

			page = attachmentResource.getProductIdImagesPage(
				irrelevantId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAttachment, (List<Attachment>)page.getItems());
			assertValid(
				page,
				testGetProductIdImagesPage_getExpectedActions(irrelevantId));
		}

		Attachment attachment1 = testGetProductIdImagesPage_addAttachment(
			id, randomAttachment());

		Attachment attachment2 = testGetProductIdImagesPage_addAttachment(
			id, randomAttachment());

		page = attachmentResource.getProductIdImagesPage(
			id, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(attachment1, (List<Attachment>)page.getItems());
		assertContains(attachment2, (List<Attachment>)page.getItems());
		assertValid(page, testGetProductIdImagesPage_getExpectedActions(id));

		attachmentResource.deleteAttachment(attachment1.getId());

		attachmentResource.deleteAttachment(attachment2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetProductIdImagesPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductIdImagesPageWithPagination() throws Exception {
		Long id = testGetProductIdImagesPage_getId();

		Page<Attachment> attachmentsPage =
			attachmentResource.getProductIdImagesPage(id, null);

		int totalCount = GetterUtil.getInteger(attachmentsPage.getTotalCount());

		Attachment attachment1 = testGetProductIdImagesPage_addAttachment(
			id, randomAttachment());

		Attachment attachment2 = testGetProductIdImagesPage_addAttachment(
			id, randomAttachment());

		Attachment attachment3 = testGetProductIdImagesPage_addAttachment(
			id, randomAttachment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Attachment> page1 = attachmentResource.getProductIdImagesPage(
				id,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(attachment1, (List<Attachment>)page1.getItems());

			Page<Attachment> page2 = attachmentResource.getProductIdImagesPage(
				id,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(attachment2, (List<Attachment>)page2.getItems());

			Page<Attachment> page3 = attachmentResource.getProductIdImagesPage(
				id,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(attachment3, (List<Attachment>)page3.getItems());
		}
		else {
			Page<Attachment> page1 = attachmentResource.getProductIdImagesPage(
				id, Pagination.of(1, totalCount + 2));

			List<Attachment> attachments1 = (List<Attachment>)page1.getItems();

			Assert.assertEquals(
				attachments1.toString(), totalCount + 2, attachments1.size());

			Page<Attachment> page2 = attachmentResource.getProductIdImagesPage(
				id, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Attachment> attachments2 = (List<Attachment>)page2.getItems();

			Assert.assertEquals(
				attachments2.toString(), 1, attachments2.size());

			Page<Attachment> page3 = attachmentResource.getProductIdImagesPage(
				id, Pagination.of(1, (int)totalCount + 3));

			assertContains(attachment1, (List<Attachment>)page3.getItems());
			assertContains(attachment2, (List<Attachment>)page3.getItems());
			assertContains(attachment3, (List<Attachment>)page3.getItems());
		}
	}

	protected Attachment testGetProductIdImagesPage_addAttachment(
			Long id, Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProductIdImagesPage_getId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProductIdImagesPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPatchAttachmentByExternalReferenceCode() throws Exception {
		Attachment postAttachment =
			testPatchAttachmentByExternalReferenceCode_addAttachment();

		Attachment randomPatchAttachment = randomPatchAttachment();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Attachment patchAttachment =
			attachmentResource.patchAttachmentByExternalReferenceCode(
				postAttachment.getExternalReferenceCode(),
				randomPatchAttachment);

		Attachment expectedPatchAttachment = postAttachment.clone();

		BeanTestUtil.copyProperties(
			randomPatchAttachment, expectedPatchAttachment);

		Attachment getAttachment =
			attachmentResource.getAttachmentByExternalReferenceCode(
				patchAttachment.getExternalReferenceCode());

		assertEquals(expectedPatchAttachment, getAttachment);
		assertValid(getAttachment);
	}

	protected Attachment
			testPatchAttachmentByExternalReferenceCode_addAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductByExternalReferenceCodeAttachment()
		throws Exception {

		Attachment randomAttachment = randomAttachment();

		Attachment postAttachment =
			testPostProductByExternalReferenceCodeAttachment_addAttachment(
				randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
	}

	protected Attachment
			testPostProductByExternalReferenceCodeAttachment_addAttachment(
				Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductByExternalReferenceCodeAttachmentByBase64()
		throws Exception {

		Attachment randomAttachment = randomAttachment();

		Attachment postAttachment =
			testPostProductByExternalReferenceCodeAttachmentByBase64_addAttachment(
				randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
	}

	protected Attachment
			testPostProductByExternalReferenceCodeAttachmentByBase64_addAttachment(
				Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductByExternalReferenceCodeAttachmentByUrl()
		throws Exception {

		Attachment randomAttachment = randomAttachment();

		Attachment postAttachment =
			testPostProductByExternalReferenceCodeAttachmentByUrl_addAttachment(
				randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
	}

	protected Attachment
			testPostProductByExternalReferenceCodeAttachmentByUrl_addAttachment(
				Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductByExternalReferenceCodeImage() throws Exception {
		Attachment randomAttachment = randomAttachment();

		Attachment postAttachment =
			testPostProductByExternalReferenceCodeImage_addAttachment(
				randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
	}

	protected Attachment
			testPostProductByExternalReferenceCodeImage_addAttachment(
				Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductByExternalReferenceCodeImageByBase64()
		throws Exception {

		Attachment randomAttachment = randomAttachment();

		Attachment postAttachment =
			testPostProductByExternalReferenceCodeImageByBase64_addAttachment(
				randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
	}

	protected Attachment
			testPostProductByExternalReferenceCodeImageByBase64_addAttachment(
				Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductByExternalReferenceCodeImageByUrl()
		throws Exception {

		Attachment randomAttachment = randomAttachment();

		Attachment postAttachment =
			testPostProductByExternalReferenceCodeImageByUrl_addAttachment(
				randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
	}

	protected Attachment
			testPostProductByExternalReferenceCodeImageByUrl_addAttachment(
				Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductIdAttachment() throws Exception {
		Attachment randomAttachment = randomAttachment();

		Attachment postAttachment = testPostProductIdAttachment_addAttachment(
			randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
	}

	protected Attachment testPostProductIdAttachment_addAttachment(
			Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductIdAttachmentByBase64() throws Exception {
		Attachment randomAttachment = randomAttachment();

		Attachment postAttachment =
			testPostProductIdAttachmentByBase64_addAttachment(randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
	}

	protected Attachment testPostProductIdAttachmentByBase64_addAttachment(
			Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductIdAttachmentByUrl() throws Exception {
		Attachment randomAttachment = randomAttachment();

		Attachment postAttachment =
			testPostProductIdAttachmentByUrl_addAttachment(randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
	}

	protected Attachment testPostProductIdAttachmentByUrl_addAttachment(
			Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductIdImage() throws Exception {
		Attachment randomAttachment = randomAttachment();

		Attachment postAttachment = testPostProductIdImage_addAttachment(
			randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
	}

	protected Attachment testPostProductIdImage_addAttachment(
			Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductIdImageByBase64() throws Exception {
		Attachment randomAttachment = randomAttachment();

		Attachment postAttachment =
			testPostProductIdImageByBase64_addAttachment(randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
	}

	protected Attachment testPostProductIdImageByBase64_addAttachment(
			Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductIdImageByUrl() throws Exception {
		Attachment randomAttachment = randomAttachment();

		Attachment postAttachment = testPostProductIdImageByUrl_addAttachment(
			randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
	}

	protected Attachment testPostProductIdImageByUrl_addAttachment(
			Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutAttachmentByExternalReferenceCode() throws Exception {
		Attachment postAttachment =
			testPutAttachmentByExternalReferenceCode_addAttachment();

		Attachment randomAttachment = randomAttachment();

		Attachment putAttachment =
			attachmentResource.putAttachmentByExternalReferenceCode(
				postAttachment.getExternalReferenceCode(), randomAttachment);

		assertEquals(randomAttachment, putAttachment);
		assertValid(putAttachment);

		Attachment getAttachment =
			attachmentResource.getAttachmentByExternalReferenceCode(
				putAttachment.getExternalReferenceCode());

		assertEquals(randomAttachment, getAttachment);
		assertValid(getAttachment);

		Attachment newAttachment =
			testPutAttachmentByExternalReferenceCode_createAttachment();

		putAttachment = attachmentResource.putAttachmentByExternalReferenceCode(
			newAttachment.getExternalReferenceCode(), newAttachment);

		assertEquals(newAttachment, putAttachment);
		assertValid(putAttachment);

		getAttachment = attachmentResource.getAttachmentByExternalReferenceCode(
			putAttachment.getExternalReferenceCode());

		assertEquals(newAttachment, getAttachment);

		Assert.assertEquals(
			newAttachment.getExternalReferenceCode(),
			putAttachment.getExternalReferenceCode());
	}

	protected Attachment
			testPutAttachmentByExternalReferenceCode_addAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Attachment
			testPutAttachmentByExternalReferenceCode_createAttachment()
		throws Exception {

		return randomAttachment();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Attachment attachment1 =
			testBatchEngineDeleteImportTask_addAttachment();

		testBatchEngineDeleteImportTask_deleteAttachment(
			200, attachment1.getExternalReferenceCode(), null);

		attachment1 = testBatchEngineDeleteImportTask_addAttachment();

		testBatchEngineDeleteImportTask_deleteAttachment(
			200, null, attachment1.getId());

		attachment1 = testBatchEngineDeleteImportTask_addAttachment();
		Attachment attachment2 =
			testBatchEngineDeleteImportTask_addAttachment();

		testBatchEngineDeleteImportTask_deleteAttachment(
			200, attachment2.getExternalReferenceCode(), attachment1.getId());

		testBatchEngineDeleteImportTask_deleteAttachment(
			200, attachment2.getExternalReferenceCode(), attachment1.getId());
	}

	protected Attachment testBatchEngineDeleteImportTask_addAttachment()
		throws Exception {

		return testDeleteAttachment_addAttachment();
	}

	protected void testBatchEngineDeleteImportTask_deleteAttachment(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.Attachment",
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

	protected Attachment testGraphQLAttachment_addAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
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

		if (attachment.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("attachment", additionalAssertFieldName)) {
				if (attachment.getAttachment() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("cdnEnabled", additionalAssertFieldName)) {
				if (attachment.getCdnEnabled() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("cdnURL", additionalAssertFieldName)) {
				if (attachment.getCdnURL() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("contentType", additionalAssertFieldName)) {
				if (attachment.getContentType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (attachment.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (attachment.getDisplayDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (attachment.getExpirationDate() == null) {
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

			if (Objects.equals(
					"fileEntryExternalReferenceCode",
					additionalAssertFieldName)) {

				if (attachment.getFileEntryExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"fileEntryGroupExternalReferenceCode",
					additionalAssertFieldName)) {

				if (attachment.getFileEntryGroupExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("fileEntryId", additionalAssertFieldName)) {
				if (attachment.getFileEntryId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("galleryEnabled", additionalAssertFieldName)) {
				if (attachment.getGalleryEnabled() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (attachment.getNeverExpire() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("options", additionalAssertFieldName)) {
				if (attachment.getOptions() == null) {
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

			if (Objects.equals("src", additionalAssertFieldName)) {
				if (attachment.getSrc() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("tags", additionalAssertFieldName)) {
				if (attachment.getTags() == null) {
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
					com.liferay.headless.commerce.admin.catalog.dto.v1_0.
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

			if (Objects.equals("attachment", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getAttachment(),
						attachment2.getAttachment())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("cdnEnabled", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getCdnEnabled(),
						attachment2.getCdnEnabled())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("cdnURL", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getCdnURL(), attachment2.getCdnURL())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("contentType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getContentType(),
						attachment2.getContentType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getCustomFields(),
						attachment2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getDisplayDate(),
						attachment2.getDisplayDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getExpirationDate(),
						attachment2.getExpirationDate())) {

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

			if (Objects.equals(
					"fileEntryExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						attachment1.getFileEntryExternalReferenceCode(),
						attachment2.getFileEntryExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"fileEntryGroupExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						attachment1.getFileEntryGroupExternalReferenceCode(),
						attachment2.getFileEntryGroupExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fileEntryId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getFileEntryId(),
						attachment2.getFileEntryId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("galleryEnabled", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getGalleryEnabled(),
						attachment2.getGalleryEnabled())) {

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

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getNeverExpire(),
						attachment2.getNeverExpire())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("options", additionalAssertFieldName)) {
				if (!equals(
						(Map)attachment1.getOptions(),
						(Map)attachment2.getOptions())) {

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

			if (Objects.equals("src", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getSrc(), attachment2.getSrc())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("tags", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						attachment1.getTags(), attachment2.getTags())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!equals(
						(Map)attachment1.getTitle(),
						(Map)attachment2.getTitle())) {

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

		if (entityFieldName.equals("cdnEnabled")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("cdnURL")) {
			Object object = attachment.getCdnURL();

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

		if (entityFieldName.equals("contentType")) {
			Object object = attachment.getContentType();

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

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("displayDate")) {
			if (operator.equals("between")) {
				Date date = attachment.getDisplayDate();

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

				sb.append(_format.format(attachment.getDisplayDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("expirationDate")) {
			if (operator.equals("between")) {
				Date date = attachment.getExpirationDate();

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

				sb.append(_format.format(attachment.getExpirationDate()));
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

		if (entityFieldName.equals("fileEntryExternalReferenceCode")) {
			Object object = attachment.getFileEntryExternalReferenceCode();

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

		if (entityFieldName.equals("fileEntryGroupExternalReferenceCode")) {
			Object object = attachment.getFileEntryGroupExternalReferenceCode();

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

		if (entityFieldName.equals("fileEntryId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("galleryEnabled")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("neverExpire")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("options")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priority")) {
			sb.append(String.valueOf(attachment.getPriority()));

			return sb.toString();
		}

		if (entityFieldName.equals("src")) {
			Object object = attachment.getSrc();

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

		if (entityFieldName.equals("tags")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("title")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("type")) {
			sb.append(String.valueOf(attachment.getType()));

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
				cdnEnabled = RandomTestUtil.randomBoolean();
				cdnURL = StringUtil.toLowerCase(RandomTestUtil.randomString());
				contentType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				displayDate = RandomTestUtil.nextDate();
				expirationDate = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				fileEntryExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				fileEntryGroupExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				fileEntryId = RandomTestUtil.randomLong();
				galleryEnabled = RandomTestUtil.randomBoolean();
				id = RandomTestUtil.randomLong();
				neverExpire = RandomTestUtil.randomBoolean();
				priority = RandomTestUtil.randomDouble();
				src = StringUtil.toLowerCase(RandomTestUtil.randomString());
				type = RandomTestUtil.randomInt();
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

	protected AttachmentResource attachmentResource;
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
		LogFactoryUtil.getLog(BaseAttachmentResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.admin.catalog.resource.v1_0.
			AttachmentResource _attachmentResource;

}