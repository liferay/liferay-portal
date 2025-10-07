/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.resource.v2_0.test;

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
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceModifier;
import com.liferay.headless.commerce.admin.pricing.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Page;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.pricing.client.resource.v2_0.PriceModifierResource;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0.PriceModifierSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
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
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public abstract class BasePriceModifierResourceTestCase {

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

		_priceModifierResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		priceModifierResource = PriceModifierResource.builder(
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

		PriceModifier priceModifier1 = randomPriceModifier();

		String json = objectMapper.writeValueAsString(priceModifier1);

		PriceModifier priceModifier2 = PriceModifierSerDes.toDTO(json);

		Assert.assertTrue(equals(priceModifier1, priceModifier2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PriceModifier priceModifier = randomPriceModifier();

		String json1 = objectMapper.writeValueAsString(priceModifier);
		String json2 = PriceModifierSerDes.toJSON(priceModifier);

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

		PriceModifier priceModifier = randomPriceModifier();

		priceModifier.setExternalReferenceCode(regex);
		priceModifier.setModifierType(regex);
		priceModifier.setPriceListExternalReferenceCode(regex);
		priceModifier.setTarget(regex);
		priceModifier.setTitle(regex);

		String json = PriceModifierSerDes.toJSON(priceModifier);

		Assert.assertFalse(json.contains(regex));

		priceModifier = PriceModifierSerDes.toDTO(json);

		Assert.assertEquals(regex, priceModifier.getExternalReferenceCode());
		Assert.assertEquals(regex, priceModifier.getModifierType());
		Assert.assertEquals(
			regex, priceModifier.getPriceListExternalReferenceCode());
		Assert.assertEquals(regex, priceModifier.getTarget());
		Assert.assertEquals(regex, priceModifier.getTitle());
	}

	@Test
	public void testDeletePriceModifier() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceModifier priceModifier =
			testDeletePriceModifier_addPriceModifier();

		assertHttpResponseStatusCode(
			204,
			priceModifierResource.deletePriceModifierHttpResponse(
				priceModifier.getId()));

		assertHttpResponseStatusCode(
			404,
			priceModifierResource.getPriceModifierHttpResponse(
				priceModifier.getId()));
		assertHttpResponseStatusCode(
			404, priceModifierResource.getPriceModifierHttpResponse(0L));
	}

	protected PriceModifier testDeletePriceModifier_addPriceModifier()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeletePriceModifier() throws Exception {

		// No namespace

		PriceModifier priceModifier1 =
			testGraphQLDeletePriceModifier_addPriceModifier();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deletePriceModifier",
						new HashMap<String, Object>() {
							{
								put("id", priceModifier1.getId());
							}
						})),
				"JSONObject/data", "Object/deletePriceModifier"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"priceModifier",
					new HashMap<String, Object>() {
						{
							put("id", priceModifier1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminPricing_v2_0

		PriceModifier priceModifier2 =
			testGraphQLDeletePriceModifier_addPriceModifier();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"deletePriceModifier",
							new HashMap<String, Object>() {
								{
									put("id", priceModifier2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPricing_v2_0",
				"Object/deletePriceModifier"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminPricing_v2_0",
					new GraphQLField(
						"priceModifier",
						new HashMap<String, Object>() {
							{
								put("id", priceModifier2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected PriceModifier testGraphQLDeletePriceModifier_addPriceModifier()
		throws Exception {

		return testGraphQLPriceModifier_addPriceModifier();
	}

	@Test
	public void testDeletePriceModifierBatch() throws Exception {
		PriceModifier priceModifier1 =
			testDeletePriceModifierBatch_addPriceModifier();

		testDeletePriceModifierBatch_deletePriceModifier(
			202, priceModifier1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			priceModifierResource.getPriceModifierHttpResponse(
				priceModifier1.getId()));

		priceModifier1 = testDeletePriceModifierBatch_addPriceModifier();

		testDeletePriceModifierBatch_deletePriceModifier(
			202, null, priceModifier1.getId());

		assertHttpResponseStatusCode(
			404,
			priceModifierResource.getPriceModifierHttpResponse(
				priceModifier1.getId()));

		priceModifier1 = testDeletePriceModifierBatch_addPriceModifier();
		PriceModifier priceModifier2 =
			testDeletePriceModifierBatch_addPriceModifier();

		testDeletePriceModifierBatch_deletePriceModifier(
			202, priceModifier2.getExternalReferenceCode(),
			priceModifier1.getId());

		assertHttpResponseStatusCode(
			404,
			priceModifierResource.getPriceModifierHttpResponse(
				priceModifier1.getId()));
		assertHttpResponseStatusCode(
			200,
			priceModifierResource.getPriceModifierHttpResponse(
				priceModifier2.getId()));

		testDeletePriceModifierBatch_deletePriceModifier(
			202, priceModifier2.getExternalReferenceCode(),
			priceModifier1.getId());

		assertHttpResponseStatusCode(
			404,
			priceModifierResource.getPriceModifierHttpResponse(
				priceModifier2.getId()));
	}

	protected PriceModifier testDeletePriceModifierBatch_addPriceModifier()
		throws Exception {

		return testDeletePriceModifier_addPriceModifier();
	}

	protected void testDeletePriceModifierBatch_deletePriceModifier(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			priceModifierResource.deletePriceModifierBatchHttpResponse(
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
	public void testDeletePriceModifierByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceModifier priceModifier =
			testDeletePriceModifierByExternalReferenceCode_addPriceModifier();

		assertHttpResponseStatusCode(
			204,
			priceModifierResource.
				deletePriceModifierByExternalReferenceCodeHttpResponse(
					priceModifier.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			priceModifierResource.
				getPriceModifierByExternalReferenceCodeHttpResponse(
					priceModifier.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			priceModifierResource.
				getPriceModifierByExternalReferenceCodeHttpResponse("-"));
	}

	protected PriceModifier
			testDeletePriceModifierByExternalReferenceCode_addPriceModifier()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeletePriceModifierByExternalReferenceCode()
		throws Exception {

		// No namespace

		PriceModifier priceModifier1 =
			testGraphQLDeletePriceModifierByExternalReferenceCode_addPriceModifier();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deletePriceModifierByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										priceModifier1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deletePriceModifierByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"priceModifierByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" +
									priceModifier1.getExternalReferenceCode() +
										"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminPricing_v2_0

		PriceModifier priceModifier2 =
			testGraphQLDeletePriceModifierByExternalReferenceCode_addPriceModifier();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"deletePriceModifierByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											priceModifier2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPricing_v2_0",
				"Object/deletePriceModifierByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminPricing_v2_0",
					new GraphQLField(
						"priceModifierByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										priceModifier2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected PriceModifier
			testGraphQLDeletePriceModifierByExternalReferenceCode_addPriceModifier()
		throws Exception {

		return testGraphQLPriceModifier_addPriceModifier();
	}

	@Test
	public void testGetPriceListByExternalReferenceCodePriceModifiersPage()
		throws Exception {

		String externalReferenceCode =
			testGetPriceListByExternalReferenceCodePriceModifiersPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetPriceListByExternalReferenceCodePriceModifiersPage_getIrrelevantExternalReferenceCode();

		Page<PriceModifier> page =
			priceModifierResource.
				getPriceListByExternalReferenceCodePriceModifiersPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			PriceModifier irrelevantPriceModifier =
				testGetPriceListByExternalReferenceCodePriceModifiersPage_addPriceModifier(
					irrelevantExternalReferenceCode,
					randomIrrelevantPriceModifier());

			page =
				priceModifierResource.
					getPriceListByExternalReferenceCodePriceModifiersPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPriceModifier, (List<PriceModifier>)page.getItems());
			assertValid(
				page,
				testGetPriceListByExternalReferenceCodePriceModifiersPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		PriceModifier priceModifier1 =
			testGetPriceListByExternalReferenceCodePriceModifiersPage_addPriceModifier(
				externalReferenceCode, randomPriceModifier());

		PriceModifier priceModifier2 =
			testGetPriceListByExternalReferenceCodePriceModifiersPage_addPriceModifier(
				externalReferenceCode, randomPriceModifier());

		page =
			priceModifierResource.
				getPriceListByExternalReferenceCodePriceModifiersPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(priceModifier1, (List<PriceModifier>)page.getItems());
		assertContains(priceModifier2, (List<PriceModifier>)page.getItems());
		assertValid(
			page,
			testGetPriceListByExternalReferenceCodePriceModifiersPage_getExpectedActions(
				externalReferenceCode));

		priceModifierResource.deletePriceModifier(priceModifier1.getId());

		priceModifierResource.deletePriceModifier(priceModifier2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetPriceListByExternalReferenceCodePriceModifiersPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPriceListByExternalReferenceCodePriceModifiersPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetPriceListByExternalReferenceCodePriceModifiersPage_getExternalReferenceCode();

		Page<PriceModifier> priceModifiersPage =
			priceModifierResource.
				getPriceListByExternalReferenceCodePriceModifiersPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			priceModifiersPage.getTotalCount());

		PriceModifier priceModifier1 =
			testGetPriceListByExternalReferenceCodePriceModifiersPage_addPriceModifier(
				externalReferenceCode, randomPriceModifier());

		PriceModifier priceModifier2 =
			testGetPriceListByExternalReferenceCodePriceModifiersPage_addPriceModifier(
				externalReferenceCode, randomPriceModifier());

		PriceModifier priceModifier3 =
			testGetPriceListByExternalReferenceCodePriceModifiersPage_addPriceModifier(
				externalReferenceCode, randomPriceModifier());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PriceModifier> page1 =
				priceModifierResource.
					getPriceListByExternalReferenceCodePriceModifiersPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				priceModifier1, (List<PriceModifier>)page1.getItems());

			Page<PriceModifier> page2 =
				priceModifierResource.
					getPriceListByExternalReferenceCodePriceModifiersPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				priceModifier2, (List<PriceModifier>)page2.getItems());

			Page<PriceModifier> page3 =
				priceModifierResource.
					getPriceListByExternalReferenceCodePriceModifiersPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				priceModifier3, (List<PriceModifier>)page3.getItems());
		}
		else {
			Page<PriceModifier> page1 =
				priceModifierResource.
					getPriceListByExternalReferenceCodePriceModifiersPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<PriceModifier> priceModifiers1 =
				(List<PriceModifier>)page1.getItems();

			Assert.assertEquals(
				priceModifiers1.toString(), totalCount + 2,
				priceModifiers1.size());

			Page<PriceModifier> page2 =
				priceModifierResource.
					getPriceListByExternalReferenceCodePriceModifiersPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PriceModifier> priceModifiers2 =
				(List<PriceModifier>)page2.getItems();

			Assert.assertEquals(
				priceModifiers2.toString(), 1, priceModifiers2.size());

			Page<PriceModifier> page3 =
				priceModifierResource.
					getPriceListByExternalReferenceCodePriceModifiersPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				priceModifier1, (List<PriceModifier>)page3.getItems());
			assertContains(
				priceModifier2, (List<PriceModifier>)page3.getItems());
			assertContains(
				priceModifier3, (List<PriceModifier>)page3.getItems());
		}
	}

	protected PriceModifier
			testGetPriceListByExternalReferenceCodePriceModifiersPage_addPriceModifier(
				String externalReferenceCode, PriceModifier priceModifier)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPriceListByExternalReferenceCodePriceModifiersPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPriceListByExternalReferenceCodePriceModifiersPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetPriceListIdPriceModifiersPage() throws Exception {
		Long id = testGetPriceListIdPriceModifiersPage_getId();
		Long irrelevantId =
			testGetPriceListIdPriceModifiersPage_getIrrelevantId();

		Page<PriceModifier> page =
			priceModifierResource.getPriceListIdPriceModifiersPage(
				id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			PriceModifier irrelevantPriceModifier =
				testGetPriceListIdPriceModifiersPage_addPriceModifier(
					irrelevantId, randomIrrelevantPriceModifier());

			page = priceModifierResource.getPriceListIdPriceModifiersPage(
				irrelevantId, null, null, Pagination.of(1, (int)totalCount + 1),
				null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPriceModifier, (List<PriceModifier>)page.getItems());
			assertValid(
				page,
				testGetPriceListIdPriceModifiersPage_getExpectedActions(
					irrelevantId));
		}

		PriceModifier priceModifier1 =
			testGetPriceListIdPriceModifiersPage_addPriceModifier(
				id, randomPriceModifier());

		PriceModifier priceModifier2 =
			testGetPriceListIdPriceModifiersPage_addPriceModifier(
				id, randomPriceModifier());

		page = priceModifierResource.getPriceListIdPriceModifiersPage(
			id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(priceModifier1, (List<PriceModifier>)page.getItems());
		assertContains(priceModifier2, (List<PriceModifier>)page.getItems());
		assertValid(
			page, testGetPriceListIdPriceModifiersPage_getExpectedActions(id));

		priceModifierResource.deletePriceModifier(priceModifier1.getId());

		priceModifierResource.deletePriceModifier(priceModifier2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetPriceListIdPriceModifiersPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPriceListIdPriceModifiersPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetPriceListIdPriceModifiersPage_getId();

		PriceModifier priceModifier1 = randomPriceModifier();

		priceModifier1 = testGetPriceListIdPriceModifiersPage_addPriceModifier(
			id, priceModifier1);

		for (EntityField entityField : entityFields) {
			Page<PriceModifier> page =
				priceModifierResource.getPriceListIdPriceModifiersPage(
					id, null,
					getFilterString(entityField, "between", priceModifier1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(priceModifier1),
				(List<PriceModifier>)page.getItems());
		}
	}

	@Test
	public void testGetPriceListIdPriceModifiersPageWithFilterDoubleEquals()
		throws Exception {

		testGetPriceListIdPriceModifiersPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetPriceListIdPriceModifiersPageWithFilterStringContains()
		throws Exception {

		testGetPriceListIdPriceModifiersPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetPriceListIdPriceModifiersPageWithFilterStringEquals()
		throws Exception {

		testGetPriceListIdPriceModifiersPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetPriceListIdPriceModifiersPageWithFilterStringStartsWith()
		throws Exception {

		testGetPriceListIdPriceModifiersPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetPriceListIdPriceModifiersPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetPriceListIdPriceModifiersPage_getId();

		PriceModifier priceModifier1 =
			testGetPriceListIdPriceModifiersPage_addPriceModifier(
				id, randomPriceModifier());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceModifier priceModifier2 =
			testGetPriceListIdPriceModifiersPage_addPriceModifier(
				id, randomPriceModifier());

		for (EntityField entityField : entityFields) {
			Page<PriceModifier> page =
				priceModifierResource.getPriceListIdPriceModifiersPage(
					id, null,
					getFilterString(entityField, operator, priceModifier1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(priceModifier1),
				(List<PriceModifier>)page.getItems());
		}
	}

	@Test
	public void testGetPriceListIdPriceModifiersPageWithPagination()
		throws Exception {

		Long id = testGetPriceListIdPriceModifiersPage_getId();

		Page<PriceModifier> priceModifiersPage =
			priceModifierResource.getPriceListIdPriceModifiersPage(
				id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			priceModifiersPage.getTotalCount());

		PriceModifier priceModifier1 =
			testGetPriceListIdPriceModifiersPage_addPriceModifier(
				id, randomPriceModifier());

		PriceModifier priceModifier2 =
			testGetPriceListIdPriceModifiersPage_addPriceModifier(
				id, randomPriceModifier());

		PriceModifier priceModifier3 =
			testGetPriceListIdPriceModifiersPage_addPriceModifier(
				id, randomPriceModifier());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PriceModifier> page1 =
				priceModifierResource.getPriceListIdPriceModifiersPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				priceModifier1, (List<PriceModifier>)page1.getItems());

			Page<PriceModifier> page2 =
				priceModifierResource.getPriceListIdPriceModifiersPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				priceModifier2, (List<PriceModifier>)page2.getItems());

			Page<PriceModifier> page3 =
				priceModifierResource.getPriceListIdPriceModifiersPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				priceModifier3, (List<PriceModifier>)page3.getItems());
		}
		else {
			Page<PriceModifier> page1 =
				priceModifierResource.getPriceListIdPriceModifiersPage(
					id, null, null, Pagination.of(1, totalCount + 2), null);

			List<PriceModifier> priceModifiers1 =
				(List<PriceModifier>)page1.getItems();

			Assert.assertEquals(
				priceModifiers1.toString(), totalCount + 2,
				priceModifiers1.size());

			Page<PriceModifier> page2 =
				priceModifierResource.getPriceListIdPriceModifiersPage(
					id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PriceModifier> priceModifiers2 =
				(List<PriceModifier>)page2.getItems();

			Assert.assertEquals(
				priceModifiers2.toString(), 1, priceModifiers2.size());

			Page<PriceModifier> page3 =
				priceModifierResource.getPriceListIdPriceModifiersPage(
					id, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(
				priceModifier1, (List<PriceModifier>)page3.getItems());
			assertContains(
				priceModifier2, (List<PriceModifier>)page3.getItems());
			assertContains(
				priceModifier3, (List<PriceModifier>)page3.getItems());
		}
	}

	@Test
	public void testGetPriceListIdPriceModifiersPageWithSortDateTime()
		throws Exception {

		testGetPriceListIdPriceModifiersPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, priceModifier1, priceModifier2) -> {
				BeanTestUtil.setProperty(
					priceModifier1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetPriceListIdPriceModifiersPageWithSortDouble()
		throws Exception {

		testGetPriceListIdPriceModifiersPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, priceModifier1, priceModifier2) -> {
				BeanTestUtil.setProperty(
					priceModifier1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					priceModifier2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetPriceListIdPriceModifiersPageWithSortInteger()
		throws Exception {

		testGetPriceListIdPriceModifiersPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, priceModifier1, priceModifier2) -> {
				BeanTestUtil.setProperty(
					priceModifier1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					priceModifier2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetPriceListIdPriceModifiersPageWithSortString()
		throws Exception {

		testGetPriceListIdPriceModifiersPageWithSort(
			EntityField.Type.STRING,
			(entityField, priceModifier1, priceModifier2) -> {
				Class<?> clazz = priceModifier1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						priceModifier1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						priceModifier2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						priceModifier1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						priceModifier2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						priceModifier1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						priceModifier2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetPriceListIdPriceModifiersPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, PriceModifier, PriceModifier, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetPriceListIdPriceModifiersPage_getId();

		PriceModifier priceModifier1 = randomPriceModifier();
		PriceModifier priceModifier2 = randomPriceModifier();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, priceModifier1, priceModifier2);
		}

		priceModifier1 = testGetPriceListIdPriceModifiersPage_addPriceModifier(
			id, priceModifier1);

		priceModifier2 = testGetPriceListIdPriceModifiersPage_addPriceModifier(
			id, priceModifier2);

		Page<PriceModifier> page =
			priceModifierResource.getPriceListIdPriceModifiersPage(
				id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PriceModifier> ascPage =
				priceModifierResource.getPriceListIdPriceModifiersPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				priceModifier1, (List<PriceModifier>)ascPage.getItems());
			assertContains(
				priceModifier2, (List<PriceModifier>)ascPage.getItems());

			Page<PriceModifier> descPage =
				priceModifierResource.getPriceListIdPriceModifiersPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				priceModifier2, (List<PriceModifier>)descPage.getItems());
			assertContains(
				priceModifier1, (List<PriceModifier>)descPage.getItems());
		}
	}

	protected PriceModifier
			testGetPriceListIdPriceModifiersPage_addPriceModifier(
				Long id, PriceModifier priceModifier)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetPriceListIdPriceModifiersPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetPriceListIdPriceModifiersPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetPriceModifier() throws Exception {
		PriceModifier postPriceModifier =
			testGetPriceModifier_addPriceModifier();

		PriceModifier getPriceModifier = priceModifierResource.getPriceModifier(
			postPriceModifier.getId());

		assertEquals(postPriceModifier, getPriceModifier);
		assertValid(getPriceModifier);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		PriceModifier postPriceModifier =
			testGetPriceModifier_addPriceModifier();

		PriceModifier getPriceModifier = priceModifierResource.getPriceModifier(
			postPriceModifier.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifier"
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

		Object item = vulcanCRUDItemDelegate.getItem(postPriceModifier.getId());

		assertEquals(
			getPriceModifier, PriceModifierSerDes.toDTO(item.toString()));
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

	protected PriceModifier testGetPriceModifier_addPriceModifier()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPriceModifier() throws Exception {
		PriceModifier priceModifier =
			testGraphQLGetPriceModifier_addPriceModifier();

		// No namespace

		Assert.assertTrue(
			equals(
				priceModifier,
				PriceModifierSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"priceModifier",
								new HashMap<String, Object>() {
									{
										put("id", priceModifier.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/priceModifier"))));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertTrue(
			equals(
				priceModifier,
				PriceModifierSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminPricing_v2_0",
								new GraphQLField(
									"priceModifier",
									new HashMap<String, Object>() {
										{
											put("id", priceModifier.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminPricing_v2_0",
						"Object/priceModifier"))));
	}

	@Test
	public void testGraphQLGetPriceModifierNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"priceModifier",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"priceModifier",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected PriceModifier testGraphQLGetPriceModifier_addPriceModifier()
		throws Exception {

		return testGraphQLPriceModifier_addPriceModifier();
	}

	@Test
	public void testGetPriceModifierByExternalReferenceCode() throws Exception {
		PriceModifier postPriceModifier =
			testGetPriceModifierByExternalReferenceCode_addPriceModifier();

		PriceModifier getPriceModifier =
			priceModifierResource.getPriceModifierByExternalReferenceCode(
				postPriceModifier.getExternalReferenceCode());

		assertEquals(postPriceModifier, getPriceModifier);
		assertValid(getPriceModifier);
	}

	protected PriceModifier
			testGetPriceModifierByExternalReferenceCode_addPriceModifier()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPriceModifierByExternalReferenceCode()
		throws Exception {

		PriceModifier priceModifier =
			testGraphQLGetPriceModifierByExternalReferenceCode_addPriceModifier();

		// No namespace

		Assert.assertTrue(
			equals(
				priceModifier,
				PriceModifierSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"priceModifierByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												priceModifier.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/priceModifierByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertTrue(
			equals(
				priceModifier,
				PriceModifierSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminPricing_v2_0",
								new GraphQLField(
									"priceModifierByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													priceModifier.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminPricing_v2_0",
						"Object/priceModifierByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetPriceModifierByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"priceModifierByExternalReferenceCode",
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

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"priceModifierByExternalReferenceCode",
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

	protected PriceModifier
			testGraphQLGetPriceModifierByExternalReferenceCode_addPriceModifier()
		throws Exception {

		return testGraphQLPriceModifier_addPriceModifier();
	}

	@Test
	public void testPatchPriceModifier() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPatchPriceModifierByExternalReferenceCode()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testPostPriceListByExternalReferenceCodePriceModifier()
		throws Exception {

		PriceModifier randomPriceModifier = randomPriceModifier();

		PriceModifier postPriceModifier =
			testPostPriceListByExternalReferenceCodePriceModifier_addPriceModifier(
				randomPriceModifier);

		assertEquals(randomPriceModifier, postPriceModifier);
		assertValid(postPriceModifier);
	}

	protected PriceModifier
			testPostPriceListByExternalReferenceCodePriceModifier_addPriceModifier(
				PriceModifier priceModifier)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostPriceListIdPriceModifier() throws Exception {
		PriceModifier randomPriceModifier = randomPriceModifier();

		PriceModifier postPriceModifier =
			testPostPriceListIdPriceModifier_addPriceModifier(
				randomPriceModifier);

		assertEquals(randomPriceModifier, postPriceModifier);
		assertValid(postPriceModifier);
	}

	protected PriceModifier testPostPriceListIdPriceModifier_addPriceModifier(
			PriceModifier priceModifier)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		PriceModifier priceModifier1 =
			testBatchEngineDeleteImportTask_addPriceModifier();

		testBatchEngineDeleteImportTask_deletePriceModifier(
			200, priceModifier1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			priceModifierResource.getPriceModifierHttpResponse(
				priceModifier1.getId()));

		priceModifier1 = testBatchEngineDeleteImportTask_addPriceModifier();

		testBatchEngineDeleteImportTask_deletePriceModifier(
			200, null, priceModifier1.getId());

		assertHttpResponseStatusCode(
			404,
			priceModifierResource.getPriceModifierHttpResponse(
				priceModifier1.getId()));

		priceModifier1 = testBatchEngineDeleteImportTask_addPriceModifier();
		PriceModifier priceModifier2 =
			testBatchEngineDeleteImportTask_addPriceModifier();

		testBatchEngineDeleteImportTask_deletePriceModifier(
			200, priceModifier2.getExternalReferenceCode(),
			priceModifier1.getId());

		assertHttpResponseStatusCode(
			404,
			priceModifierResource.getPriceModifierHttpResponse(
				priceModifier1.getId()));
		assertHttpResponseStatusCode(
			200,
			priceModifierResource.getPriceModifierHttpResponse(
				priceModifier2.getId()));

		testBatchEngineDeleteImportTask_deletePriceModifier(
			200, priceModifier2.getExternalReferenceCode(),
			priceModifier1.getId());

		assertHttpResponseStatusCode(
			404,
			priceModifierResource.getPriceModifierHttpResponse(
				priceModifier2.getId()));
	}

	protected PriceModifier testBatchEngineDeleteImportTask_addPriceModifier()
		throws Exception {

		return testDeletePriceModifier_addPriceModifier();
	}

	protected void testBatchEngineDeleteImportTask_deletePriceModifier(
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
				"com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifier",
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

	protected PriceModifier testGraphQLPriceModifier_addPriceModifier()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		PriceModifier priceModifier, List<PriceModifier> priceModifiers) {

		boolean contains = false;

		for (PriceModifier item : priceModifiers) {
			if (equals(priceModifier, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			priceModifiers + " does not contain " + priceModifier, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PriceModifier priceModifier1, PriceModifier priceModifier2) {

		Assert.assertTrue(
			priceModifier1 + " does not equal " + priceModifier2,
			equals(priceModifier1, priceModifier2));
	}

	protected void assertEquals(
		List<PriceModifier> priceModifiers1,
		List<PriceModifier> priceModifiers2) {

		Assert.assertEquals(priceModifiers1.size(), priceModifiers2.size());

		for (int i = 0; i < priceModifiers1.size(); i++) {
			PriceModifier priceModifier1 = priceModifiers1.get(i);
			PriceModifier priceModifier2 = priceModifiers2.get(i);

			assertEquals(priceModifier1, priceModifier2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<PriceModifier> priceModifiers1,
		List<PriceModifier> priceModifiers2) {

		Assert.assertEquals(priceModifiers1.size(), priceModifiers2.size());

		for (PriceModifier priceModifier1 : priceModifiers1) {
			boolean contains = false;

			for (PriceModifier priceModifier2 : priceModifiers2) {
				if (equals(priceModifier1, priceModifier2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				priceModifiers2 + " does not contain " + priceModifier1,
				contains);
		}
	}

	protected void assertValid(PriceModifier priceModifier) throws Exception {
		boolean valid = true;

		if (priceModifier.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (priceModifier.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (priceModifier.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (priceModifier.getDisplayDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (priceModifier.getExpirationDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (priceModifier.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("modifierAmount", additionalAssertFieldName)) {
				if (priceModifier.getModifierAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("modifierType", additionalAssertFieldName)) {
				if (priceModifier.getModifierType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (priceModifier.getNeverExpire() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListExternalReferenceCode",
					additionalAssertFieldName)) {

				if (priceModifier.getPriceListExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priceListId", additionalAssertFieldName)) {
				if (priceModifier.getPriceListId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceModifierCategories", additionalAssertFieldName)) {

				if (priceModifier.getPriceModifierCategories() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceModifierProductGroups", additionalAssertFieldName)) {

				if (priceModifier.getPriceModifierProductGroups() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceModifierProducts", additionalAssertFieldName)) {

				if (priceModifier.getPriceModifierProducts() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (priceModifier.getPriority() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("target", additionalAssertFieldName)) {
				if (priceModifier.getTarget() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (priceModifier.getTitle() == null) {
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

	protected void assertValid(Page<PriceModifier> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PriceModifier> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PriceModifier> priceModifiers = page.getItems();

		int size = priceModifiers.size();

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
					com.liferay.headless.commerce.admin.pricing.dto.v2_0.
						PriceModifier.class)) {

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
		PriceModifier priceModifier1, PriceModifier priceModifier2) {

		if (priceModifier1 == priceModifier2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)priceModifier1.getActions(),
						(Map)priceModifier2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceModifier1.getActive(),
						priceModifier2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceModifier1.getDisplayDate(),
						priceModifier2.getDisplayDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceModifier1.getExpirationDate(),
						priceModifier2.getExpirationDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceModifier1.getExternalReferenceCode(),
						priceModifier2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceModifier1.getId(), priceModifier2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("modifierAmount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceModifier1.getModifierAmount(),
						priceModifier2.getModifierAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("modifierType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceModifier1.getModifierType(),
						priceModifier2.getModifierType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceModifier1.getNeverExpire(),
						priceModifier2.getNeverExpire())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceModifier1.getPriceListExternalReferenceCode(),
						priceModifier2.getPriceListExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priceListId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceModifier1.getPriceListId(),
						priceModifier2.getPriceListId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceModifierCategories", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceModifier1.getPriceModifierCategories(),
						priceModifier2.getPriceModifierCategories())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceModifierProductGroups", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceModifier1.getPriceModifierProductGroups(),
						priceModifier2.getPriceModifierProductGroups())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceModifierProducts", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceModifier1.getPriceModifierProducts(),
						priceModifier2.getPriceModifierProducts())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceModifier1.getPriority(),
						priceModifier2.getPriority())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("target", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceModifier1.getTarget(),
						priceModifier2.getTarget())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceModifier1.getTitle(), priceModifier2.getTitle())) {

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

		if (!(_priceModifierResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_priceModifierResource;

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
		EntityField entityField, String operator, PriceModifier priceModifier) {

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

		if (entityFieldName.equals("active")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("displayDate")) {
			if (operator.equals("between")) {
				Date date = priceModifier.getDisplayDate();

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

				sb.append(_format.format(priceModifier.getDisplayDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("expirationDate")) {
			if (operator.equals("between")) {
				Date date = priceModifier.getExpirationDate();

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

				sb.append(_format.format(priceModifier.getExpirationDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = priceModifier.getExternalReferenceCode();

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

		if (entityFieldName.equals("modifierAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("modifierType")) {
			Object object = priceModifier.getModifierType();

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

		if (entityFieldName.equals("neverExpire")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceListExternalReferenceCode")) {
			Object object = priceModifier.getPriceListExternalReferenceCode();

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

		if (entityFieldName.equals("priceListId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceModifierCategories")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceModifierProductGroups")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceModifierProducts")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priority")) {
			sb.append(String.valueOf(priceModifier.getPriority()));

			return sb.toString();
		}

		if (entityFieldName.equals("target")) {
			Object object = priceModifier.getTarget();

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

		if (entityFieldName.equals("title")) {
			Object object = priceModifier.getTitle();

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

	protected PriceModifier randomPriceModifier() throws Exception {
		return new PriceModifier() {
			{
				active = RandomTestUtil.randomBoolean();
				displayDate = RandomTestUtil.nextDate();
				expirationDate = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				modifierType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				neverExpire = RandomTestUtil.randomBoolean();
				priceListExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				priceListId = RandomTestUtil.randomLong();
				priority = RandomTestUtil.randomDouble();
				target = StringUtil.toLowerCase(RandomTestUtil.randomString());
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected PriceModifier randomIrrelevantPriceModifier() throws Exception {
		PriceModifier randomIrrelevantPriceModifier = randomPriceModifier();

		return randomIrrelevantPriceModifier;
	}

	protected PriceModifier randomPatchPriceModifier() throws Exception {
		return randomPriceModifier();
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

	protected PriceModifierResource priceModifierResource;
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
		LogFactoryUtil.getLog(BasePriceModifierResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.pricing.resource.v2_0.
		PriceModifierResource _priceModifierResource;

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