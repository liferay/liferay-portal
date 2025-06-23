/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.commerce.delivery.cart.client.dto.v1_0.Attachment;
import com.liferay.headless.commerce.delivery.cart.client.http.HttpInvoker;
import com.liferay.headless.commerce.delivery.cart.client.pagination.Page;
import com.liferay.headless.commerce.delivery.cart.client.pagination.Pagination;
import com.liferay.headless.commerce.delivery.cart.client.resource.v1_0.AttachmentResource;
import com.liferay.headless.commerce.delivery.cart.client.serdes.v1_0.AttachmentSerDes;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;
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
 * @author Andrea Sbarra
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

		attachment.setExternalReferenceCode(regex);
		attachment.setTitle(regex);
		attachment.setUrl(regex);

		String json = AttachmentSerDes.toJSON(attachment);

		Assert.assertFalse(json.contains(regex));

		attachment = AttachmentSerDes.toDTO(json);

		Assert.assertEquals(regex, attachment.getExternalReferenceCode());
		Assert.assertEquals(regex, attachment.getTitle());
		Assert.assertEquals(regex, attachment.getUrl());
	}

	@Test
	public void testDeleteCartAttachment() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Attachment attachment = testDeleteCartAttachment_addAttachment();

		assertHttpResponseStatusCode(
			204,
			attachmentResource.deleteCartAttachmentHttpResponse(
				attachment.getId(), testDeleteCartAttachment_getCartId()));
	}

	protected Attachment testDeleteCartAttachment_addAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testDeleteCartAttachment_getCartId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testDeleteCartByExternalReferenceCodeAttachmentByExternalReferenceCodeAttachmentExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Attachment attachment =
			testDeleteCartByExternalReferenceCodeAttachmentByExternalReferenceCodeAttachmentExternalReferenceCode_addAttachment();

		assertHttpResponseStatusCode(
			204,
			attachmentResource.
				deleteCartByExternalReferenceCodeAttachmentByExternalReferenceCodeAttachmentExternalReferenceCodeHttpResponse(
					attachment.getExternalReferenceCode(),
					testDeleteCartByExternalReferenceCodeAttachmentByExternalReferenceCodeAttachmentExternalReferenceCode_getExternalReferenceCode(
						attachment)));
	}

	protected Attachment
			testDeleteCartByExternalReferenceCodeAttachmentByExternalReferenceCodeAttachmentExternalReferenceCode_addAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteCartByExternalReferenceCodeAttachmentByExternalReferenceCodeAttachmentExternalReferenceCode_getExternalReferenceCode(
				Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetCartAttachmentsPage() throws Exception {
		Long cartId = testGetCartAttachmentsPage_getCartId();
		Long irrelevantCartId =
			testGetCartAttachmentsPage_getIrrelevantCartId();

		Page<Attachment> page = attachmentResource.getCartAttachmentsPage(
			cartId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantCartId != null) {
			Attachment irrelevantAttachment =
				testGetCartAttachmentsPage_addAttachment(
					irrelevantCartId, randomIrrelevantAttachment());

			page = attachmentResource.getCartAttachmentsPage(
				irrelevantCartId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAttachment, (List<Attachment>)page.getItems());
			assertValid(
				page,
				testGetCartAttachmentsPage_getExpectedActions(
					irrelevantCartId));
		}

		Attachment attachment1 = testGetCartAttachmentsPage_addAttachment(
			cartId, randomAttachment());

		Attachment attachment2 = testGetCartAttachmentsPage_addAttachment(
			cartId, randomAttachment());

		page = attachmentResource.getCartAttachmentsPage(
			cartId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(attachment1, (List<Attachment>)page.getItems());
		assertContains(attachment2, (List<Attachment>)page.getItems());
		assertValid(
			page, testGetCartAttachmentsPage_getExpectedActions(cartId));
	}

	protected Map<String, Map<String, String>>
			testGetCartAttachmentsPage_getExpectedActions(Long cartId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetCartAttachmentsPageWithPagination() throws Exception {
		Long cartId = testGetCartAttachmentsPage_getCartId();

		Page<Attachment> attachmentsPage =
			attachmentResource.getCartAttachmentsPage(cartId, null);

		int totalCount = GetterUtil.getInteger(attachmentsPage.getTotalCount());

		Attachment attachment1 = testGetCartAttachmentsPage_addAttachment(
			cartId, randomAttachment());

		Attachment attachment2 = testGetCartAttachmentsPage_addAttachment(
			cartId, randomAttachment());

		Attachment attachment3 = testGetCartAttachmentsPage_addAttachment(
			cartId, randomAttachment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Attachment> page1 = attachmentResource.getCartAttachmentsPage(
				cartId,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(attachment1, (List<Attachment>)page1.getItems());

			Page<Attachment> page2 = attachmentResource.getCartAttachmentsPage(
				cartId,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(attachment2, (List<Attachment>)page2.getItems());

			Page<Attachment> page3 = attachmentResource.getCartAttachmentsPage(
				cartId,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(attachment3, (List<Attachment>)page3.getItems());
		}
		else {
			Page<Attachment> page1 = attachmentResource.getCartAttachmentsPage(
				cartId, Pagination.of(1, totalCount + 2));

			List<Attachment> attachments1 = (List<Attachment>)page1.getItems();

			Assert.assertEquals(
				attachments1.toString(), totalCount + 2, attachments1.size());

			Page<Attachment> page2 = attachmentResource.getCartAttachmentsPage(
				cartId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Attachment> attachments2 = (List<Attachment>)page2.getItems();

			Assert.assertEquals(
				attachments2.toString(), 1, attachments2.size());

			Page<Attachment> page3 = attachmentResource.getCartAttachmentsPage(
				cartId, Pagination.of(1, (int)totalCount + 3));

			assertContains(attachment1, (List<Attachment>)page3.getItems());
			assertContains(attachment2, (List<Attachment>)page3.getItems());
			assertContains(attachment3, (List<Attachment>)page3.getItems());
		}
	}

	protected Attachment testGetCartAttachmentsPage_addAttachment(
			Long cartId, Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetCartAttachmentsPage_getCartId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetCartAttachmentsPage_getIrrelevantCartId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetCartByExternalReferenceCodeAttachmentsPage()
		throws Exception {

		String externalReferenceCode =
			testGetCartByExternalReferenceCodeAttachmentsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetCartByExternalReferenceCodeAttachmentsPage_getIrrelevantExternalReferenceCode();

		Page<Attachment> page =
			attachmentResource.getCartByExternalReferenceCodeAttachmentsPage(
				externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			Attachment irrelevantAttachment =
				testGetCartByExternalReferenceCodeAttachmentsPage_addAttachment(
					irrelevantExternalReferenceCode,
					randomIrrelevantAttachment());

			page =
				attachmentResource.
					getCartByExternalReferenceCodeAttachmentsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAttachment, (List<Attachment>)page.getItems());
			assertValid(
				page,
				testGetCartByExternalReferenceCodeAttachmentsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		Attachment attachment1 =
			testGetCartByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, randomAttachment());

		Attachment attachment2 =
			testGetCartByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, randomAttachment());

		page = attachmentResource.getCartByExternalReferenceCodeAttachmentsPage(
			externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(attachment1, (List<Attachment>)page.getItems());
		assertContains(attachment2, (List<Attachment>)page.getItems());
		assertValid(
			page,
			testGetCartByExternalReferenceCodeAttachmentsPage_getExpectedActions(
				externalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetCartByExternalReferenceCodeAttachmentsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetCartByExternalReferenceCodeAttachmentsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetCartByExternalReferenceCodeAttachmentsPage_getExternalReferenceCode();

		Page<Attachment> attachmentsPage =
			attachmentResource.getCartByExternalReferenceCodeAttachmentsPage(
				externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(attachmentsPage.getTotalCount());

		Attachment attachment1 =
			testGetCartByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, randomAttachment());

		Attachment attachment2 =
			testGetCartByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, randomAttachment());

		Attachment attachment3 =
			testGetCartByExternalReferenceCodeAttachmentsPage_addAttachment(
				externalReferenceCode, randomAttachment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Attachment> page1 =
				attachmentResource.
					getCartByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(attachment1, (List<Attachment>)page1.getItems());

			Page<Attachment> page2 =
				attachmentResource.
					getCartByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(attachment2, (List<Attachment>)page2.getItems());

			Page<Attachment> page3 =
				attachmentResource.
					getCartByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(attachment3, (List<Attachment>)page3.getItems());
		}
		else {
			Page<Attachment> page1 =
				attachmentResource.
					getCartByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<Attachment> attachments1 = (List<Attachment>)page1.getItems();

			Assert.assertEquals(
				attachments1.toString(), totalCount + 2, attachments1.size());

			Page<Attachment> page2 =
				attachmentResource.
					getCartByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Attachment> attachments2 = (List<Attachment>)page2.getItems();

			Assert.assertEquals(
				attachments2.toString(), 1, attachments2.size());

			Page<Attachment> page3 =
				attachmentResource.
					getCartByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(attachment1, (List<Attachment>)page3.getItems());
			assertContains(attachment2, (List<Attachment>)page3.getItems());
			assertContains(attachment3, (List<Attachment>)page3.getItems());
		}
	}

	protected Attachment
			testGetCartByExternalReferenceCodeAttachmentsPage_addAttachment(
				String externalReferenceCode, Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetCartByExternalReferenceCodeAttachmentsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetCartByExternalReferenceCodeAttachmentsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testPostCartAttachmentByBase64() throws Exception {
		Attachment randomAttachment = randomAttachment();

		Attachment postAttachment =
			testPostCartAttachmentByBase64_addAttachment(randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
	}

	protected Attachment testPostCartAttachmentByBase64_addAttachment(
			Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostCartByExternalReferenceCodeAttachmentByBase64()
		throws Exception {

		Attachment randomAttachment = randomAttachment();

		Attachment postAttachment =
			testPostCartByExternalReferenceCodeAttachmentByBase64_addAttachment(
				randomAttachment);

		assertEquals(randomAttachment, postAttachment);
		assertValid(postAttachment);
	}

	protected Attachment
			testPostCartByExternalReferenceCodeAttachmentByBase64_addAttachment(
				Attachment attachment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
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

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (attachment.getExternalReferenceCode() == null) {
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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.delivery.cart.dto.v1_0.
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

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						attachment1.getExternalReferenceCode(),
						attachment2.getExternalReferenceCode())) {

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

		if (entityFieldName.equals("id")) {
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
			sb.append(String.valueOf(attachment.getType()));

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
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
				type = RandomTestUtil.randomInt();
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
		com.liferay.headless.commerce.delivery.cart.resource.v1_0.
			AttachmentResource _attachmentResource;

}