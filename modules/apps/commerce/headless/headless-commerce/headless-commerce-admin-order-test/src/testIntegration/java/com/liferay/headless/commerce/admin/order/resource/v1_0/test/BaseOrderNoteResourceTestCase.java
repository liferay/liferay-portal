/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
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

import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderNote;
import com.liferay.headless.commerce.admin.order.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.order.client.pagination.Page;
import com.liferay.headless.commerce.admin.order.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.order.client.resource.v1_0.OrderNoteResource;
import com.liferay.headless.commerce.admin.order.client.serdes.v1_0.OrderNoteSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
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
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
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
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public abstract class BaseOrderNoteResourceTestCase {

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

		_orderNoteResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		orderNoteResource = OrderNoteResource.builder(
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

		OrderNote orderNote1 = randomOrderNote();

		String json = objectMapper.writeValueAsString(orderNote1);

		OrderNote orderNote2 = OrderNoteSerDes.toDTO(json);

		Assert.assertTrue(equals(orderNote1, orderNote2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		OrderNote orderNote = randomOrderNote();

		String json1 = objectMapper.writeValueAsString(orderNote);
		String json2 = OrderNoteSerDes.toJSON(orderNote);

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

		OrderNote orderNote = randomOrderNote();

		orderNote.setAuthor(regex);
		orderNote.setContent(regex);
		orderNote.setExternalReferenceCode(regex);
		orderNote.setOrderExternalReferenceCode(regex);

		String json = OrderNoteSerDes.toJSON(orderNote);

		Assert.assertFalse(json.contains(regex));

		orderNote = OrderNoteSerDes.toDTO(json);

		Assert.assertEquals(regex, orderNote.getAuthor());
		Assert.assertEquals(regex, orderNote.getContent());
		Assert.assertEquals(regex, orderNote.getExternalReferenceCode());
		Assert.assertEquals(regex, orderNote.getOrderExternalReferenceCode());
	}

	@Test
	public void testDeleteOrderNote() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderNote orderNote = testDeleteOrderNote_addOrderNote();

		assertHttpResponseStatusCode(
			204,
			orderNoteResource.deleteOrderNoteHttpResponse(orderNote.getId()));

		assertHttpResponseStatusCode(
			404, orderNoteResource.getOrderNoteHttpResponse(orderNote.getId()));
		assertHttpResponseStatusCode(
			404, orderNoteResource.getOrderNoteHttpResponse(0L));
	}

	protected OrderNote testDeleteOrderNote_addOrderNote() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrderNote() throws Exception {

		// No namespace

		OrderNote orderNote1 = testGraphQLDeleteOrderNote_addOrderNote();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrderNote",
						new HashMap<String, Object>() {
							{
								put("id", orderNote1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteOrderNote"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"orderNote",
					new HashMap<String, Object>() {
						{
							put("id", orderNote1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminOrder_v1_0

		OrderNote orderNote2 = testGraphQLDeleteOrderNote_addOrderNote();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"deleteOrderNote",
							new HashMap<String, Object>() {
								{
									put("id", orderNote2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
				"Object/deleteOrderNote"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminOrder_v1_0",
					new GraphQLField(
						"orderNote",
						new HashMap<String, Object>() {
							{
								put("id", orderNote2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected OrderNote testGraphQLDeleteOrderNote_addOrderNote()
		throws Exception {

		return testGraphQLOrderNote_addOrderNote();
	}

	@Test
	public void testDeleteOrderNoteBatch() throws Exception {
		OrderNote orderNote1 = testDeleteOrderNoteBatch_addOrderNote();

		testDeleteOrderNoteBatch_deleteOrderNote(
			202, orderNote1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			orderNoteResource.getOrderNoteHttpResponse(orderNote1.getId()));

		orderNote1 = testDeleteOrderNoteBatch_addOrderNote();

		testDeleteOrderNoteBatch_deleteOrderNote(202, null, orderNote1.getId());

		assertHttpResponseStatusCode(
			404,
			orderNoteResource.getOrderNoteHttpResponse(orderNote1.getId()));

		orderNote1 = testDeleteOrderNoteBatch_addOrderNote();
		OrderNote orderNote2 = testDeleteOrderNoteBatch_addOrderNote();

		testDeleteOrderNoteBatch_deleteOrderNote(
			202, orderNote2.getExternalReferenceCode(), orderNote1.getId());

		assertHttpResponseStatusCode(
			404,
			orderNoteResource.getOrderNoteHttpResponse(orderNote1.getId()));
		assertHttpResponseStatusCode(
			200,
			orderNoteResource.getOrderNoteHttpResponse(orderNote2.getId()));

		testDeleteOrderNoteBatch_deleteOrderNote(
			202, orderNote2.getExternalReferenceCode(), orderNote1.getId());

		assertHttpResponseStatusCode(
			404,
			orderNoteResource.getOrderNoteHttpResponse(orderNote2.getId()));
	}

	protected OrderNote testDeleteOrderNoteBatch_addOrderNote()
		throws Exception {

		return testDeleteOrderNote_addOrderNote();
	}

	protected void testDeleteOrderNoteBatch_deleteOrderNote(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			orderNoteResource.deleteOrderNoteBatchHttpResponse(
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
	public void testDeleteOrderNoteByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		OrderNote orderNote =
			testDeleteOrderNoteByExternalReferenceCode_addOrderNote();

		assertHttpResponseStatusCode(
			204,
			orderNoteResource.
				deleteOrderNoteByExternalReferenceCodeHttpResponse(
					orderNote.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			orderNoteResource.getOrderNoteByExternalReferenceCodeHttpResponse(
				orderNote.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			orderNoteResource.getOrderNoteByExternalReferenceCodeHttpResponse(
				"-"));
	}

	protected OrderNote
			testDeleteOrderNoteByExternalReferenceCode_addOrderNote()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrderNoteByExternalReferenceCode()
		throws Exception {

		// No namespace

		OrderNote orderNote1 =
			testGraphQLDeleteOrderNoteByExternalReferenceCode_addOrderNote();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrderNoteByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										orderNote1.getExternalReferenceCode() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteOrderNoteByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"orderNoteByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + orderNote1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminOrder_v1_0

		OrderNote orderNote2 =
			testGraphQLDeleteOrderNoteByExternalReferenceCode_addOrderNote();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"deleteOrderNoteByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											orderNote2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessCommerceAdminOrder_v1_0",
				"Object/deleteOrderNoteByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminOrder_v1_0",
					new GraphQLField(
						"orderNoteByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										orderNote2.getExternalReferenceCode() +
											"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected OrderNote
			testGraphQLDeleteOrderNoteByExternalReferenceCode_addOrderNote()
		throws Exception {

		return testGraphQLOrderNote_addOrderNote();
	}

	@Test
	public void testGetOrderByExternalReferenceCodeOrderNotesPage()
		throws Exception {

		String externalReferenceCode =
			testGetOrderByExternalReferenceCodeOrderNotesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetOrderByExternalReferenceCodeOrderNotesPage_getIrrelevantExternalReferenceCode();

		Page<OrderNote> page =
			orderNoteResource.getOrderByExternalReferenceCodeOrderNotesPage(
				externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			OrderNote irrelevantOrderNote =
				testGetOrderByExternalReferenceCodeOrderNotesPage_addOrderNote(
					irrelevantExternalReferenceCode,
					randomIrrelevantOrderNote());

			page =
				orderNoteResource.getOrderByExternalReferenceCodeOrderNotesPage(
					irrelevantExternalReferenceCode,
					Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantOrderNote, (List<OrderNote>)page.getItems());
			assertValid(
				page,
				testGetOrderByExternalReferenceCodeOrderNotesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		OrderNote orderNote1 =
			testGetOrderByExternalReferenceCodeOrderNotesPage_addOrderNote(
				externalReferenceCode, randomOrderNote());

		OrderNote orderNote2 =
			testGetOrderByExternalReferenceCodeOrderNotesPage_addOrderNote(
				externalReferenceCode, randomOrderNote());

		page = orderNoteResource.getOrderByExternalReferenceCodeOrderNotesPage(
			externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(orderNote1, (List<OrderNote>)page.getItems());
		assertContains(orderNote2, (List<OrderNote>)page.getItems());
		assertValid(
			page,
			testGetOrderByExternalReferenceCodeOrderNotesPage_getExpectedActions(
				externalReferenceCode));

		orderNoteResource.deleteOrderNote(orderNote1.getId());

		orderNoteResource.deleteOrderNote(orderNote2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOrderByExternalReferenceCodeOrderNotesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrderByExternalReferenceCodeOrderNotesPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetOrderByExternalReferenceCodeOrderNotesPage_getExternalReferenceCode();

		Page<OrderNote> orderNotesPage =
			orderNoteResource.getOrderByExternalReferenceCodeOrderNotesPage(
				externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(orderNotesPage.getTotalCount());

		OrderNote orderNote1 =
			testGetOrderByExternalReferenceCodeOrderNotesPage_addOrderNote(
				externalReferenceCode, randomOrderNote());

		OrderNote orderNote2 =
			testGetOrderByExternalReferenceCodeOrderNotesPage_addOrderNote(
				externalReferenceCode, randomOrderNote());

		OrderNote orderNote3 =
			testGetOrderByExternalReferenceCodeOrderNotesPage_addOrderNote(
				externalReferenceCode, randomOrderNote());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<OrderNote> page1 =
				orderNoteResource.getOrderByExternalReferenceCodeOrderNotesPage(
					externalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(orderNote1, (List<OrderNote>)page1.getItems());

			Page<OrderNote> page2 =
				orderNoteResource.getOrderByExternalReferenceCodeOrderNotesPage(
					externalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(orderNote2, (List<OrderNote>)page2.getItems());

			Page<OrderNote> page3 =
				orderNoteResource.getOrderByExternalReferenceCodeOrderNotesPage(
					externalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(orderNote3, (List<OrderNote>)page3.getItems());
		}
		else {
			Page<OrderNote> page1 =
				orderNoteResource.getOrderByExternalReferenceCodeOrderNotesPage(
					externalReferenceCode, Pagination.of(1, totalCount + 2));

			List<OrderNote> orderNotes1 = (List<OrderNote>)page1.getItems();

			Assert.assertEquals(
				orderNotes1.toString(), totalCount + 2, orderNotes1.size());

			Page<OrderNote> page2 =
				orderNoteResource.getOrderByExternalReferenceCodeOrderNotesPage(
					externalReferenceCode, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<OrderNote> orderNotes2 = (List<OrderNote>)page2.getItems();

			Assert.assertEquals(orderNotes2.toString(), 1, orderNotes2.size());

			Page<OrderNote> page3 =
				orderNoteResource.getOrderByExternalReferenceCodeOrderNotesPage(
					externalReferenceCode,
					Pagination.of(1, (int)totalCount + 3));

			assertContains(orderNote1, (List<OrderNote>)page3.getItems());
			assertContains(orderNote2, (List<OrderNote>)page3.getItems());
			assertContains(orderNote3, (List<OrderNote>)page3.getItems());
		}
	}

	protected OrderNote
			testGetOrderByExternalReferenceCodeOrderNotesPage_addOrderNote(
				String externalReferenceCode, OrderNote orderNote)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrderByExternalReferenceCodeOrderNotesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrderByExternalReferenceCodeOrderNotesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetOrderIdOrderNotesPage() throws Exception {
		Long id = testGetOrderIdOrderNotesPage_getId();
		Long irrelevantId = testGetOrderIdOrderNotesPage_getIrrelevantId();

		Page<OrderNote> page = orderNoteResource.getOrderIdOrderNotesPage(
			id, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			OrderNote irrelevantOrderNote =
				testGetOrderIdOrderNotesPage_addOrderNote(
					irrelevantId, randomIrrelevantOrderNote());

			page = orderNoteResource.getOrderIdOrderNotesPage(
				irrelevantId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantOrderNote, (List<OrderNote>)page.getItems());
			assertValid(
				page,
				testGetOrderIdOrderNotesPage_getExpectedActions(irrelevantId));
		}

		OrderNote orderNote1 = testGetOrderIdOrderNotesPage_addOrderNote(
			id, randomOrderNote());

		OrderNote orderNote2 = testGetOrderIdOrderNotesPage_addOrderNote(
			id, randomOrderNote());

		page = orderNoteResource.getOrderIdOrderNotesPage(
			id, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(orderNote1, (List<OrderNote>)page.getItems());
		assertContains(orderNote2, (List<OrderNote>)page.getItems());
		assertValid(page, testGetOrderIdOrderNotesPage_getExpectedActions(id));

		orderNoteResource.deleteOrderNote(orderNote1.getId());

		orderNoteResource.deleteOrderNote(orderNote2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOrderIdOrderNotesPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrderIdOrderNotesPageWithPagination() throws Exception {
		Long id = testGetOrderIdOrderNotesPage_getId();

		Page<OrderNote> orderNotesPage =
			orderNoteResource.getOrderIdOrderNotesPage(id, null);

		int totalCount = GetterUtil.getInteger(orderNotesPage.getTotalCount());

		OrderNote orderNote1 = testGetOrderIdOrderNotesPage_addOrderNote(
			id, randomOrderNote());

		OrderNote orderNote2 = testGetOrderIdOrderNotesPage_addOrderNote(
			id, randomOrderNote());

		OrderNote orderNote3 = testGetOrderIdOrderNotesPage_addOrderNote(
			id, randomOrderNote());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<OrderNote> page1 = orderNoteResource.getOrderIdOrderNotesPage(
				id,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(orderNote1, (List<OrderNote>)page1.getItems());

			Page<OrderNote> page2 = orderNoteResource.getOrderIdOrderNotesPage(
				id,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(orderNote2, (List<OrderNote>)page2.getItems());

			Page<OrderNote> page3 = orderNoteResource.getOrderIdOrderNotesPage(
				id,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(orderNote3, (List<OrderNote>)page3.getItems());
		}
		else {
			Page<OrderNote> page1 = orderNoteResource.getOrderIdOrderNotesPage(
				id, Pagination.of(1, totalCount + 2));

			List<OrderNote> orderNotes1 = (List<OrderNote>)page1.getItems();

			Assert.assertEquals(
				orderNotes1.toString(), totalCount + 2, orderNotes1.size());

			Page<OrderNote> page2 = orderNoteResource.getOrderIdOrderNotesPage(
				id, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<OrderNote> orderNotes2 = (List<OrderNote>)page2.getItems();

			Assert.assertEquals(orderNotes2.toString(), 1, orderNotes2.size());

			Page<OrderNote> page3 = orderNoteResource.getOrderIdOrderNotesPage(
				id, Pagination.of(1, (int)totalCount + 3));

			assertContains(orderNote1, (List<OrderNote>)page3.getItems());
			assertContains(orderNote2, (List<OrderNote>)page3.getItems());
			assertContains(orderNote3, (List<OrderNote>)page3.getItems());
		}
	}

	protected OrderNote testGetOrderIdOrderNotesPage_addOrderNote(
			Long id, OrderNote orderNote)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetOrderIdOrderNotesPage_getId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetOrderIdOrderNotesPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetOrderNote() throws Exception {
		OrderNote postOrderNote = testGetOrderNote_addOrderNote();

		OrderNote getOrderNote = orderNoteResource.getOrderNote(
			postOrderNote.getId());

		assertEquals(postOrderNote, getOrderNote);
		assertValid(getOrderNote);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		OrderNote postOrderNote = testGetOrderNote_addOrderNote();

		OrderNote getOrderNote = orderNoteResource.getOrderNote(
			postOrderNote.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.order.dto.v1_0.OrderNote"
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

		Object item = vulcanCRUDItemDelegate.getItem(postOrderNote.getId());

		assertEquals(getOrderNote, OrderNoteSerDes.toDTO(item.toString()));
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

	protected OrderNote testGetOrderNote_addOrderNote() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOrderNote() throws Exception {
		OrderNote orderNote = testGraphQLGetOrderNote_addOrderNote();

		// No namespace

		Assert.assertTrue(
			equals(
				orderNote,
				OrderNoteSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"orderNote",
								new HashMap<String, Object>() {
									{
										put("id", orderNote.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/orderNote"))));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Assert.assertTrue(
			equals(
				orderNote,
				OrderNoteSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminOrder_v1_0",
								new GraphQLField(
									"orderNote",
									new HashMap<String, Object>() {
										{
											put("id", orderNote.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminOrder_v1_0",
						"Object/orderNote"))));
	}

	@Test
	public void testGraphQLGetOrderNoteNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"orderNote",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
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
							"orderNote",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected OrderNote testGraphQLGetOrderNote_addOrderNote()
		throws Exception {

		return testGraphQLOrderNote_addOrderNote();
	}

	@Test
	public void testGetOrderNoteByExternalReferenceCode() throws Exception {
		OrderNote postOrderNote =
			testGetOrderNoteByExternalReferenceCode_addOrderNote();

		OrderNote getOrderNote =
			orderNoteResource.getOrderNoteByExternalReferenceCode(
				postOrderNote.getExternalReferenceCode());

		assertEquals(postOrderNote, getOrderNote);
		assertValid(getOrderNote);
	}

	protected OrderNote testGetOrderNoteByExternalReferenceCode_addOrderNote()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOrderNoteByExternalReferenceCode()
		throws Exception {

		OrderNote orderNote =
			testGraphQLGetOrderNoteByExternalReferenceCode_addOrderNote();

		// No namespace

		Assert.assertTrue(
			equals(
				orderNote,
				OrderNoteSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"orderNoteByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												orderNote.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/orderNoteByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Assert.assertTrue(
			equals(
				orderNote,
				OrderNoteSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminOrder_v1_0",
								new GraphQLField(
									"orderNoteByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													orderNote.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminOrder_v1_0",
						"Object/orderNoteByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetOrderNoteByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"orderNoteByExternalReferenceCode",
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

		// Using the namespace headlessCommerceAdminOrder_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminOrder_v1_0",
						new GraphQLField(
							"orderNoteByExternalReferenceCode",
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

	protected OrderNote
			testGraphQLGetOrderNoteByExternalReferenceCode_addOrderNote()
		throws Exception {

		return testGraphQLOrderNote_addOrderNote();
	}

	@Test
	public void testPatchOrderNote() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPatchOrderNoteByExternalReferenceCode() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPostOrderByExternalReferenceCodeOrderNote()
		throws Exception {

		OrderNote randomOrderNote = randomOrderNote();

		OrderNote postOrderNote =
			testPostOrderByExternalReferenceCodeOrderNote_addOrderNote(
				randomOrderNote);

		assertEquals(randomOrderNote, postOrderNote);
		assertValid(postOrderNote);
	}

	protected OrderNote
			testPostOrderByExternalReferenceCodeOrderNote_addOrderNote(
				OrderNote orderNote)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOrderIdOrderNote() throws Exception {
		OrderNote randomOrderNote = randomOrderNote();

		OrderNote postOrderNote = testPostOrderIdOrderNote_addOrderNote(
			randomOrderNote);

		assertEquals(randomOrderNote, postOrderNote);
		assertValid(postOrderNote);
	}

	protected OrderNote testPostOrderIdOrderNote_addOrderNote(
			OrderNote orderNote)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		OrderNote orderNote1 = testBatchEngineDeleteImportTask_addOrderNote();

		testBatchEngineDeleteImportTask_deleteOrderNote(
			200, orderNote1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			orderNoteResource.getOrderNoteHttpResponse(orderNote1.getId()));

		orderNote1 = testBatchEngineDeleteImportTask_addOrderNote();

		testBatchEngineDeleteImportTask_deleteOrderNote(
			200, null, orderNote1.getId());

		assertHttpResponseStatusCode(
			404,
			orderNoteResource.getOrderNoteHttpResponse(orderNote1.getId()));

		orderNote1 = testBatchEngineDeleteImportTask_addOrderNote();
		OrderNote orderNote2 = testBatchEngineDeleteImportTask_addOrderNote();

		testBatchEngineDeleteImportTask_deleteOrderNote(
			200, orderNote2.getExternalReferenceCode(), orderNote1.getId());

		assertHttpResponseStatusCode(
			404,
			orderNoteResource.getOrderNoteHttpResponse(orderNote1.getId()));
		assertHttpResponseStatusCode(
			200,
			orderNoteResource.getOrderNoteHttpResponse(orderNote2.getId()));

		testBatchEngineDeleteImportTask_deleteOrderNote(
			200, orderNote2.getExternalReferenceCode(), orderNote1.getId());

		assertHttpResponseStatusCode(
			404,
			orderNoteResource.getOrderNoteHttpResponse(orderNote2.getId()));
	}

	protected OrderNote testBatchEngineDeleteImportTask_addOrderNote()
		throws Exception {

		return testDeleteOrderNote_addOrderNote();
	}

	protected void testBatchEngineDeleteImportTask_deleteOrderNote(
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
				"com.liferay.headless.commerce.admin.order.dto.v1_0.OrderNote",
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

	protected OrderNote testGraphQLOrderNote_addOrderNote() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		OrderNote orderNote, List<OrderNote> orderNotes) {

		boolean contains = false;

		for (OrderNote item : orderNotes) {
			if (equals(orderNote, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			orderNotes + " does not contain " + orderNote, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(OrderNote orderNote1, OrderNote orderNote2) {
		Assert.assertTrue(
			orderNote1 + " does not equal " + orderNote2,
			equals(orderNote1, orderNote2));
	}

	protected void assertEquals(
		List<OrderNote> orderNotes1, List<OrderNote> orderNotes2) {

		Assert.assertEquals(orderNotes1.size(), orderNotes2.size());

		for (int i = 0; i < orderNotes1.size(); i++) {
			OrderNote orderNote1 = orderNotes1.get(i);
			OrderNote orderNote2 = orderNotes2.get(i);

			assertEquals(orderNote1, orderNote2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<OrderNote> orderNotes1, List<OrderNote> orderNotes2) {

		Assert.assertEquals(orderNotes1.size(), orderNotes2.size());

		for (OrderNote orderNote1 : orderNotes1) {
			boolean contains = false;

			for (OrderNote orderNote2 : orderNotes2) {
				if (equals(orderNote1, orderNote2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				orderNotes2 + " does not contain " + orderNote1, contains);
		}
	}

	protected void assertValid(OrderNote orderNote) throws Exception {
		boolean valid = true;

		if (orderNote.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (orderNote.getAuthor() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("content", additionalAssertFieldName)) {
				if (orderNote.getContent() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (orderNote.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderExternalReferenceCode", additionalAssertFieldName)) {

				if (orderNote.getOrderExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderId", additionalAssertFieldName)) {
				if (orderNote.getOrderId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("restricted", additionalAssertFieldName)) {
				if (orderNote.getRestricted() == null) {
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

	protected void assertValid(Page<OrderNote> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<OrderNote> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<OrderNote> orderNotes = page.getItems();

		int size = orderNotes.size();

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
						OrderNote.class)) {

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

	protected boolean equals(OrderNote orderNote1, OrderNote orderNote2) {
		if (orderNote1 == orderNote2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderNote1.getAuthor(), orderNote2.getAuthor())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("content", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderNote1.getContent(), orderNote2.getContent())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderNote1.getExternalReferenceCode(),
						orderNote2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderNote1.getId(), orderNote2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						orderNote1.getOrderExternalReferenceCode(),
						orderNote2.getOrderExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderNote1.getOrderId(), orderNote2.getOrderId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("restricted", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						orderNote1.getRestricted(),
						orderNote2.getRestricted())) {

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

		if (!(_orderNoteResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_orderNoteResource;

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
		EntityField entityField, String operator, OrderNote orderNote) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("author")) {
			Object object = orderNote.getAuthor();

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

		if (entityFieldName.equals("content")) {
			Object object = orderNote.getContent();

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
			Object object = orderNote.getExternalReferenceCode();

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

		if (entityFieldName.equals("orderExternalReferenceCode")) {
			Object object = orderNote.getOrderExternalReferenceCode();

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

		if (entityFieldName.equals("orderId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("restricted")) {
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

	protected OrderNote randomOrderNote() throws Exception {
		return new OrderNote() {
			{
				author = StringUtil.toLowerCase(RandomTestUtil.randomString());
				content = StringUtil.toLowerCase(RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				orderExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				orderId = RandomTestUtil.randomLong();
				restricted = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected OrderNote randomIrrelevantOrderNote() throws Exception {
		OrderNote randomIrrelevantOrderNote = randomOrderNote();

		return randomIrrelevantOrderNote;
	}

	protected OrderNote randomPatchOrderNote() throws Exception {
		return randomOrderNote();
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

	protected OrderNoteResource orderNoteResource;
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
		LogFactoryUtil.getLog(BaseOrderNoteResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.admin.order.resource.v1_0.
			OrderNoteResource _orderNoteResource;

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