/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.resource.v1_0.test;

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
import com.liferay.headless.asset.library.client.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.client.dto.v1_0.Role;
import com.liferay.headless.asset.library.client.http.HttpInvoker;
import com.liferay.headless.asset.library.client.pagination.Page;
import com.liferay.headless.asset.library.client.pagination.Pagination;
import com.liferay.headless.asset.library.client.permission.Permission;
import com.liferay.headless.asset.library.client.resource.v1_0.AssetLibraryResource;
import com.liferay.headless.asset.library.client.serdes.v1_0.AssetLibrarySerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
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
 * @author Roberto Díaz
 * @generated
 */
@Generated("")
public abstract class BaseAssetLibraryResourceTestCase {

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

		_assetLibraryResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		assetLibraryResource = AssetLibraryResource.builder(
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

		permissionsAssetLibraryResource = AssetLibraryResource.builder(
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

		AssetLibrary assetLibrary1 = randomAssetLibrary();

		String json = objectMapper.writeValueAsString(assetLibrary1);

		AssetLibrary assetLibrary2 = AssetLibrarySerDes.toDTO(json);

		Assert.assertTrue(equals(assetLibrary1, assetLibrary2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		AssetLibrary assetLibrary = randomAssetLibrary();

		String json1 = objectMapper.writeValueAsString(assetLibrary);
		String json2 = AssetLibrarySerDes.toJSON(assetLibrary);

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

		AssetLibrary assetLibrary = randomAssetLibrary();

		assetLibrary.setAssetLibraryKey(regex);
		assetLibrary.setDescription(regex);
		assetLibrary.setExternalReferenceCode(regex);
		assetLibrary.setName(regex);

		String json = AssetLibrarySerDes.toJSON(assetLibrary);

		Assert.assertFalse(json.contains(regex));

		assetLibrary = AssetLibrarySerDes.toDTO(json);

		Assert.assertEquals(regex, assetLibrary.getAssetLibraryKey());
		Assert.assertEquals(regex, assetLibrary.getDescription());
		Assert.assertEquals(regex, assetLibrary.getExternalReferenceCode());
		Assert.assertEquals(regex, assetLibrary.getName());
	}

	@Test
	public void testDeleteAssetLibrary() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		AssetLibrary assetLibrary = testDeleteAssetLibrary_addAssetLibrary();

		assertHttpResponseStatusCode(
			204,
			assetLibraryResource.deleteAssetLibraryHttpResponse(
				assetLibrary.getId()));

		assertHttpResponseStatusCode(
			404,
			assetLibraryResource.getAssetLibraryHttpResponse(
				assetLibrary.getId()));
		assertHttpResponseStatusCode(
			404, assetLibraryResource.getAssetLibraryHttpResponse(0L));
	}

	protected AssetLibrary testDeleteAssetLibrary_addAssetLibrary()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testDeleteAssetLibraryBatch() throws Exception {
		AssetLibrary assetLibrary1 =
			testDeleteAssetLibraryBatch_addAssetLibrary();

		testDeleteAssetLibraryBatch_deleteAssetLibrary(
			202, assetLibrary1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			assetLibraryResource.getAssetLibraryHttpResponse(
				assetLibrary1.getId()));

		assetLibrary1 = testDeleteAssetLibraryBatch_addAssetLibrary();

		testDeleteAssetLibraryBatch_deleteAssetLibrary(
			202, null, assetLibrary1.getId());

		assertHttpResponseStatusCode(
			404,
			assetLibraryResource.getAssetLibraryHttpResponse(
				assetLibrary1.getId()));

		assetLibrary1 = testDeleteAssetLibraryBatch_addAssetLibrary();
		AssetLibrary assetLibrary2 =
			testDeleteAssetLibraryBatch_addAssetLibrary();

		testDeleteAssetLibraryBatch_deleteAssetLibrary(
			202, assetLibrary2.getExternalReferenceCode(),
			assetLibrary1.getId());

		assertHttpResponseStatusCode(
			404,
			assetLibraryResource.getAssetLibraryHttpResponse(
				assetLibrary1.getId()));
		assertHttpResponseStatusCode(
			200,
			assetLibraryResource.getAssetLibraryHttpResponse(
				assetLibrary2.getId()));

		testDeleteAssetLibraryBatch_deleteAssetLibrary(
			202, assetLibrary2.getExternalReferenceCode(),
			assetLibrary1.getId());

		assertHttpResponseStatusCode(
			404,
			assetLibraryResource.getAssetLibraryHttpResponse(
				assetLibrary2.getId()));
	}

	protected AssetLibrary testDeleteAssetLibraryBatch_addAssetLibrary()
		throws Exception {

		return testDeleteAssetLibrary_addAssetLibrary();
	}

	protected void testDeleteAssetLibraryBatch_deleteAssetLibrary(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			assetLibraryResource.deleteAssetLibraryBatchHttpResponse(
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
	public void testDeleteAssetLibraryByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AssetLibrary assetLibrary =
			testDeleteAssetLibraryByExternalReferenceCode_addAssetLibrary();

		assertHttpResponseStatusCode(
			204,
			assetLibraryResource.
				deleteAssetLibraryByExternalReferenceCodeHttpResponse(
					assetLibrary.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			assetLibraryResource.
				getAssetLibraryByExternalReferenceCodeHttpResponse(
					assetLibrary.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			assetLibraryResource.
				getAssetLibraryByExternalReferenceCodeHttpResponse("-"));
	}

	protected AssetLibrary
			testDeleteAssetLibraryByExternalReferenceCode_addAssetLibrary()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testDeleteAssetLibraryByExternalReferenceCodePin()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AssetLibrary assetLibrary =
			testDeleteAssetLibraryByExternalReferenceCodePin_addAssetLibrary();

		assertHttpResponseStatusCode(
			204,
			assetLibraryResource.
				deleteAssetLibraryByExternalReferenceCodePinHttpResponse(
					assetLibrary.getExternalReferenceCode()));
	}

	protected AssetLibrary
			testDeleteAssetLibraryByExternalReferenceCodePin_addAssetLibrary()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testDeleteAssetLibraryPin() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		AssetLibrary assetLibrary = testDeleteAssetLibraryPin_addAssetLibrary();

		assertHttpResponseStatusCode(
			204,
			assetLibraryResource.deleteAssetLibraryPinHttpResponse(
				assetLibrary.getId()));
	}

	protected AssetLibrary testDeleteAssetLibraryPin_addAssetLibrary()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetAssetLibrariesPage() throws Exception {
		Page<AssetLibrary> page = assetLibraryResource.getAssetLibrariesPage(
			null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		AssetLibrary assetLibrary1 = testGetAssetLibrariesPage_addAssetLibrary(
			randomAssetLibrary());

		AssetLibrary assetLibrary2 = testGetAssetLibrariesPage_addAssetLibrary(
			randomAssetLibrary());

		page = assetLibraryResource.getAssetLibrariesPage(
			null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(assetLibrary1, (List<AssetLibrary>)page.getItems());
		assertContains(assetLibrary2, (List<AssetLibrary>)page.getItems());
		assertValid(page, testGetAssetLibrariesPage_getExpectedActions());

		for (AssetLibrary assetLibrary : page.getItems()) {
			Assert.assertNull(assetLibrary.getPermissions());
		}

		page = permissionsAssetLibraryResource.getAssetLibrariesPage(
			null, null, null, Pagination.of(1, 10), null);

		for (AssetLibrary assetLibrary : page.getItems()) {
			Assert.assertNotNull(assetLibrary.getPermissions());
		}

		assetLibraryResource.deleteAssetLibrary(assetLibrary1.getId());

		assetLibraryResource.deleteAssetLibrary(assetLibrary2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibrariesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAssetLibrariesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		AssetLibrary assetLibrary1 = randomAssetLibrary();

		assetLibrary1 = testGetAssetLibrariesPage_addAssetLibrary(
			assetLibrary1);

		for (EntityField entityField : entityFields) {
			Page<AssetLibrary> page =
				assetLibraryResource.getAssetLibrariesPage(
					null, null,
					getFilterString(entityField, "between", assetLibrary1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(assetLibrary1),
				(List<AssetLibrary>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibrariesPageWithFilterDoubleEquals()
		throws Exception {

		testGetAssetLibrariesPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAssetLibrariesPageWithFilterStringContains()
		throws Exception {

		testGetAssetLibrariesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibrariesPageWithFilterStringEquals()
		throws Exception {

		testGetAssetLibrariesPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibrariesPageWithFilterStringStartsWith()
		throws Exception {

		testGetAssetLibrariesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetAssetLibrariesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		AssetLibrary assetLibrary1 = testGetAssetLibrariesPage_addAssetLibrary(
			randomAssetLibrary());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AssetLibrary assetLibrary2 = testGetAssetLibrariesPage_addAssetLibrary(
			randomAssetLibrary());

		for (EntityField entityField : entityFields) {
			Page<AssetLibrary> page =
				assetLibraryResource.getAssetLibrariesPage(
					null, null,
					getFilterString(entityField, operator, assetLibrary1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(assetLibrary1),
				(List<AssetLibrary>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibrariesPageWithPagination() throws Exception {
		Page<AssetLibrary> assetLibrariesPage =
			assetLibraryResource.getAssetLibrariesPage(
				null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			assetLibrariesPage.getTotalCount());

		AssetLibrary assetLibrary1 = testGetAssetLibrariesPage_addAssetLibrary(
			randomAssetLibrary());

		AssetLibrary assetLibrary2 = testGetAssetLibrariesPage_addAssetLibrary(
			randomAssetLibrary());

		AssetLibrary assetLibrary3 = testGetAssetLibrariesPage_addAssetLibrary(
			randomAssetLibrary());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AssetLibrary> page1 =
				assetLibraryResource.getAssetLibrariesPage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(assetLibrary1, (List<AssetLibrary>)page1.getItems());

			Page<AssetLibrary> page2 =
				assetLibraryResource.getAssetLibrariesPage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(assetLibrary2, (List<AssetLibrary>)page2.getItems());

			Page<AssetLibrary> page3 =
				assetLibraryResource.getAssetLibrariesPage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(assetLibrary3, (List<AssetLibrary>)page3.getItems());
		}
		else {
			Page<AssetLibrary> page1 =
				assetLibraryResource.getAssetLibrariesPage(
					null, null, null, Pagination.of(1, totalCount + 2), null);

			List<AssetLibrary> assetLibraries1 =
				(List<AssetLibrary>)page1.getItems();

			Assert.assertEquals(
				assetLibraries1.toString(), totalCount + 2,
				assetLibraries1.size());

			Page<AssetLibrary> page2 =
				assetLibraryResource.getAssetLibrariesPage(
					null, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AssetLibrary> assetLibraries2 =
				(List<AssetLibrary>)page2.getItems();

			Assert.assertEquals(
				assetLibraries2.toString(), 1, assetLibraries2.size());

			Page<AssetLibrary> page3 =
				assetLibraryResource.getAssetLibrariesPage(
					null, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(assetLibrary1, (List<AssetLibrary>)page3.getItems());
			assertContains(assetLibrary2, (List<AssetLibrary>)page3.getItems());
			assertContains(assetLibrary3, (List<AssetLibrary>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetLibrariesPageWithSortDateTime() throws Exception {
		testGetAssetLibrariesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, assetLibrary1, assetLibrary2) -> {
				BeanTestUtil.setProperty(
					assetLibrary1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetLibrariesPageWithSortDouble() throws Exception {
		testGetAssetLibrariesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, assetLibrary1, assetLibrary2) -> {
				BeanTestUtil.setProperty(
					assetLibrary1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					assetLibrary2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetLibrariesPageWithSortInteger() throws Exception {
		testGetAssetLibrariesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, assetLibrary1, assetLibrary2) -> {
				BeanTestUtil.setProperty(
					assetLibrary1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					assetLibrary2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetLibrariesPageWithSortString() throws Exception {
		testGetAssetLibrariesPageWithSort(
			EntityField.Type.STRING,
			(entityField, assetLibrary1, assetLibrary2) -> {
				Class<?> clazz = assetLibrary1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						assetLibrary1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						assetLibrary2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						assetLibrary1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						assetLibrary2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						assetLibrary1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						assetLibrary2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAssetLibrariesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, AssetLibrary, AssetLibrary, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		AssetLibrary assetLibrary1 = randomAssetLibrary();
		AssetLibrary assetLibrary2 = randomAssetLibrary();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, assetLibrary1, assetLibrary2);
		}

		assetLibrary1 = testGetAssetLibrariesPage_addAssetLibrary(
			assetLibrary1);

		assetLibrary2 = testGetAssetLibrariesPage_addAssetLibrary(
			assetLibrary2);

		Page<AssetLibrary> page = assetLibraryResource.getAssetLibrariesPage(
			null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<AssetLibrary> ascPage =
				assetLibraryResource.getAssetLibrariesPage(
					null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				assetLibrary1, (List<AssetLibrary>)ascPage.getItems());
			assertContains(
				assetLibrary2, (List<AssetLibrary>)ascPage.getItems());

			Page<AssetLibrary> descPage =
				assetLibraryResource.getAssetLibrariesPage(
					null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				assetLibrary2, (List<AssetLibrary>)descPage.getItems());
			assertContains(
				assetLibrary1, (List<AssetLibrary>)descPage.getItems());
		}
	}

	protected AssetLibrary testGetAssetLibrariesPage_addAssetLibrary(
			AssetLibrary assetLibrary)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetAssetLibrariesPinnedByMePage() throws Exception {
		Page<AssetLibrary> page =
			assetLibraryResource.getAssetLibrariesPinnedByMePage(
				Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		AssetLibrary assetLibrary1 =
			testGetAssetLibrariesPinnedByMePage_addAssetLibrary(
				randomAssetLibrary());

		AssetLibrary assetLibrary2 =
			testGetAssetLibrariesPinnedByMePage_addAssetLibrary(
				randomAssetLibrary());

		page = assetLibraryResource.getAssetLibrariesPinnedByMePage(
			Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(assetLibrary1, (List<AssetLibrary>)page.getItems());
		assertContains(assetLibrary2, (List<AssetLibrary>)page.getItems());
		assertValid(
			page, testGetAssetLibrariesPinnedByMePage_getExpectedActions());

		assetLibraryResource.deleteAssetLibrary(assetLibrary1.getId());

		assetLibraryResource.deleteAssetLibrary(assetLibrary2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibrariesPinnedByMePage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAssetLibrariesPinnedByMePageWithPagination()
		throws Exception {

		Page<AssetLibrary> assetLibrariesPage =
			assetLibraryResource.getAssetLibrariesPinnedByMePage(null);

		int totalCount = GetterUtil.getInteger(
			assetLibrariesPage.getTotalCount());

		AssetLibrary assetLibrary1 =
			testGetAssetLibrariesPinnedByMePage_addAssetLibrary(
				randomAssetLibrary());

		AssetLibrary assetLibrary2 =
			testGetAssetLibrariesPinnedByMePage_addAssetLibrary(
				randomAssetLibrary());

		AssetLibrary assetLibrary3 =
			testGetAssetLibrariesPinnedByMePage_addAssetLibrary(
				randomAssetLibrary());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AssetLibrary> page1 =
				assetLibraryResource.getAssetLibrariesPinnedByMePage(
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(assetLibrary1, (List<AssetLibrary>)page1.getItems());

			Page<AssetLibrary> page2 =
				assetLibraryResource.getAssetLibrariesPinnedByMePage(
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(assetLibrary2, (List<AssetLibrary>)page2.getItems());

			Page<AssetLibrary> page3 =
				assetLibraryResource.getAssetLibrariesPinnedByMePage(
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(assetLibrary3, (List<AssetLibrary>)page3.getItems());
		}
		else {
			Page<AssetLibrary> page1 =
				assetLibraryResource.getAssetLibrariesPinnedByMePage(
					Pagination.of(1, totalCount + 2));

			List<AssetLibrary> assetLibraries1 =
				(List<AssetLibrary>)page1.getItems();

			Assert.assertEquals(
				assetLibraries1.toString(), totalCount + 2,
				assetLibraries1.size());

			Page<AssetLibrary> page2 =
				assetLibraryResource.getAssetLibrariesPinnedByMePage(
					Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AssetLibrary> assetLibraries2 =
				(List<AssetLibrary>)page2.getItems();

			Assert.assertEquals(
				assetLibraries2.toString(), 1, assetLibraries2.size());

			Page<AssetLibrary> page3 =
				assetLibraryResource.getAssetLibrariesPinnedByMePage(
					Pagination.of(1, (int)totalCount + 3));

			assertContains(assetLibrary1, (List<AssetLibrary>)page3.getItems());
			assertContains(assetLibrary2, (List<AssetLibrary>)page3.getItems());
			assertContains(assetLibrary3, (List<AssetLibrary>)page3.getItems());
		}
	}

	protected AssetLibrary testGetAssetLibrariesPinnedByMePage_addAssetLibrary(
			AssetLibrary assetLibrary)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetAssetLibrary() throws Exception {
		AssetLibrary postAssetLibrary = testGetAssetLibrary_addAssetLibrary();

		AssetLibrary getAssetLibrary = assetLibraryResource.getAssetLibrary(
			postAssetLibrary.getId());

		assertEquals(postAssetLibrary, getAssetLibrary);
		assertValid(getAssetLibrary);

		Assert.assertNull(getAssetLibrary.getPermissions());

		getAssetLibrary = permissionsAssetLibraryResource.getAssetLibrary(
			postAssetLibrary.getId());

		Assert.assertNotNull(getAssetLibrary.getPermissions());
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		AssetLibrary postAssetLibrary = testGetAssetLibrary_addAssetLibrary();

		AssetLibrary getAssetLibrary = assetLibraryResource.getAssetLibrary(
			postAssetLibrary.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.asset.library.dto.v1_0.AssetLibrary"
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

		Object item = vulcanCRUDItemDelegate.getItem(postAssetLibrary.getId());

		assertEquals(
			getAssetLibrary, AssetLibrarySerDes.toDTO(item.toString()));
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

	protected AssetLibrary testGetAssetLibrary_addAssetLibrary()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCode() throws Exception {
		AssetLibrary postAssetLibrary =
			testGetAssetLibraryByExternalReferenceCode_addAssetLibrary();

		AssetLibrary getAssetLibrary =
			assetLibraryResource.getAssetLibraryByExternalReferenceCode(
				postAssetLibrary.getExternalReferenceCode());

		assertEquals(postAssetLibrary, getAssetLibrary);
		assertValid(getAssetLibrary);

		Assert.assertNull(getAssetLibrary.getPermissions());

		getAssetLibrary =
			permissionsAssetLibraryResource.
				getAssetLibraryByExternalReferenceCode(
					postAssetLibrary.getExternalReferenceCode());

		Assert.assertNotNull(getAssetLibrary.getPermissions());
	}

	protected AssetLibrary
			testGetAssetLibraryByExternalReferenceCode_addAssetLibrary()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodePermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AssetLibrary postAssetLibrary =
			testGetAssetLibraryByExternalReferenceCodePermissionsPage_addAssetLibrary();

		Page<Permission> page =
			assetLibraryResource.
				getAssetLibraryByExternalReferenceCodePermissionsPage(
					postAssetLibrary.getExternalReferenceCode(),
					RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected AssetLibrary
			testGetAssetLibraryByExternalReferenceCodePermissionsPage_addAssetLibrary()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetAssetLibraryPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		AssetLibrary postAssetLibrary =
			testGetAssetLibraryPermissionsPage_addAssetLibrary();

		Page<Permission> page =
			assetLibraryResource.getAssetLibraryPermissionsPage(
				postAssetLibrary.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected AssetLibrary testGetAssetLibraryPermissionsPage_addAssetLibrary()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchAssetLibrary() throws Exception {
		AssetLibrary postAssetLibrary = testPatchAssetLibrary_addAssetLibrary();

		AssetLibrary randomPatchAssetLibrary = randomPatchAssetLibrary();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AssetLibrary patchAssetLibrary = assetLibraryResource.patchAssetLibrary(
			postAssetLibrary.getId(), randomPatchAssetLibrary);

		AssetLibrary expectedPatchAssetLibrary = postAssetLibrary.clone();

		BeanTestUtil.copyProperties(
			randomPatchAssetLibrary, expectedPatchAssetLibrary);

		AssetLibrary getAssetLibrary = assetLibraryResource.getAssetLibrary(
			testDepotEntry.getDepotEntryId());

		assertEquals(expectedPatchAssetLibrary, getAssetLibrary);
		assertValid(getAssetLibrary);
	}

	protected AssetLibrary testPatchAssetLibrary_addAssetLibrary()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchAssetLibraryByExternalReferenceCode()
		throws Exception {

		AssetLibrary postAssetLibrary =
			testPatchAssetLibraryByExternalReferenceCode_addAssetLibrary();

		AssetLibrary randomPatchAssetLibrary = randomPatchAssetLibrary();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AssetLibrary patchAssetLibrary =
			assetLibraryResource.patchAssetLibraryByExternalReferenceCode(
				postAssetLibrary.getExternalReferenceCode(),
				randomPatchAssetLibrary);

		AssetLibrary expectedPatchAssetLibrary = postAssetLibrary.clone();

		BeanTestUtil.copyProperties(
			randomPatchAssetLibrary, expectedPatchAssetLibrary);

		AssetLibrary getAssetLibrary =
			assetLibraryResource.getAssetLibraryByExternalReferenceCode(
				patchAssetLibrary.getExternalReferenceCode());

		assertEquals(expectedPatchAssetLibrary, getAssetLibrary);
		assertValid(getAssetLibrary);
	}

	protected AssetLibrary
			testPatchAssetLibraryByExternalReferenceCode_addAssetLibrary()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAssetLibrary() throws Exception {
		AssetLibrary randomAssetLibrary = randomAssetLibrary();

		AssetLibrary postAssetLibrary = testPostAssetLibrary_addAssetLibrary(
			randomAssetLibrary);

		assertEquals(randomAssetLibrary, postAssetLibrary);
		assertValid(postAssetLibrary);

		AssetLibrary randomPermissionsAssetLibrary1 =
			randomPermissionsAssetLibrary();

		AssetLibrary postPermissionsAssetLibrary1 =
			testPostAssetLibrary_addAssetLibrary(
				randomPermissionsAssetLibrary1);

		Assert.assertNull(postPermissionsAssetLibrary1.getPermissions());

		AssetLibrary randomPermissionsAssetLibrary2 =
			randomPermissionsAssetLibrary();

		AssetLibrary postPermissionsAssetLibrary2 =
			testPostAssetLibrary_addPermissionsAssetLibrary(
				randomPermissionsAssetLibrary2);

		Assert.assertNotNull(postPermissionsAssetLibrary2.getPermissions());
	}

	protected AssetLibrary testPostAssetLibrary_addAssetLibrary(
			AssetLibrary assetLibrary)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected AssetLibrary testPostAssetLibrary_addPermissionsAssetLibrary(
			AssetLibrary assetLibrary)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutAssetLibraryByExternalReferenceCode() throws Exception {
		AssetLibrary postAssetLibrary =
			testPutAssetLibraryByExternalReferenceCode_addAssetLibrary();

		AssetLibrary randomAssetLibrary = randomAssetLibrary();

		AssetLibrary putAssetLibrary =
			assetLibraryResource.putAssetLibraryByExternalReferenceCode(
				postAssetLibrary.getExternalReferenceCode(),
				randomAssetLibrary);

		assertEquals(randomAssetLibrary, putAssetLibrary);
		assertValid(putAssetLibrary);

		Assert.assertNull(putAssetLibrary.getPermissions());

		AssetLibrary getAssetLibrary =
			assetLibraryResource.getAssetLibraryByExternalReferenceCode(
				putAssetLibrary.getExternalReferenceCode());

		assertEquals(randomAssetLibrary, getAssetLibrary);
		assertValid(getAssetLibrary);

		AssetLibrary randomPermissionsAssetLibrary =
			randomPermissionsAssetLibrary();

		putAssetLibrary =
			assetLibraryResource.putAssetLibraryByExternalReferenceCode(
				postAssetLibrary.getExternalReferenceCode(),
				randomPermissionsAssetLibrary);

		assertEquals(randomPermissionsAssetLibrary, putAssetLibrary);
		assertValid(putAssetLibrary);

		Assert.assertNull(putAssetLibrary.getPermissions());

		putAssetLibrary =
			permissionsAssetLibraryResource.
				putAssetLibraryByExternalReferenceCode(
					postAssetLibrary.getExternalReferenceCode(),
					randomPermissionsAssetLibrary);

		Assert.assertNotNull(putAssetLibrary.getPermissions());

		AssetLibrary newAssetLibrary =
			testPutAssetLibraryByExternalReferenceCode_createAssetLibrary();

		putAssetLibrary =
			assetLibraryResource.putAssetLibraryByExternalReferenceCode(
				newAssetLibrary.getExternalReferenceCode(), newAssetLibrary);

		assertEquals(newAssetLibrary, putAssetLibrary);
		assertValid(putAssetLibrary);

		getAssetLibrary =
			assetLibraryResource.getAssetLibraryByExternalReferenceCode(
				putAssetLibrary.getExternalReferenceCode());

		assertEquals(newAssetLibrary, getAssetLibrary);

		Assert.assertEquals(
			newAssetLibrary.getExternalReferenceCode(),
			putAssetLibrary.getExternalReferenceCode());
	}

	protected AssetLibrary
			testPutAssetLibraryByExternalReferenceCode_addAssetLibrary()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected AssetLibrary
			testPutAssetLibraryByExternalReferenceCode_createAssetLibrary()
		throws Exception {

		return randomAssetLibrary();
	}

	@Test
	public void testPutAssetLibraryByExternalReferenceCodePermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AssetLibrary assetLibrary =
			testPutAssetLibraryByExternalReferenceCodePermissionsPage_addAssetLibrary();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			assetLibraryResource.
				putAssetLibraryByExternalReferenceCodePermissionsPageHttpResponse(
					assetLibrary.getExternalReferenceCode(),
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
			assetLibraryResource.
				putAssetLibraryByExternalReferenceCodePermissionsPageHttpResponse(
					assetLibrary.getExternalReferenceCode(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"-"});
								setRoleName("-");
							}
						}
					}));
	}

	protected AssetLibrary
			testPutAssetLibraryByExternalReferenceCodePermissionsPage_addAssetLibrary()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutAssetLibraryByExternalReferenceCodePin()
		throws Exception {

		AssetLibrary postAssetLibrary =
			testPutAssetLibraryByExternalReferenceCodePin_addAssetLibrary();

		AssetLibrary randomAssetLibrary = randomAssetLibrary();

		AssetLibrary putAssetLibrary =
			assetLibraryResource.putAssetLibraryByExternalReferenceCodePin(
				postAssetLibrary.getExternalReferenceCode());

		assertEquals(randomAssetLibrary, putAssetLibrary);
		assertValid(putAssetLibrary);

		AssetLibrary getAssetLibrary =
			testPutAssetLibraryByExternalReferenceCodePin_getAssetLibrary(
				putAssetLibrary.getExternalReferenceCode());

		assertEquals(randomAssetLibrary, getAssetLibrary);
		assertValid(getAssetLibrary);
	}

	protected AssetLibrary
		testPutAssetLibraryByExternalReferenceCodePin_getAssetLibrary(
			String externalReferenceCode) {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected AssetLibrary
			testPutAssetLibraryByExternalReferenceCodePin_addAssetLibrary()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutAssetLibraryPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		AssetLibrary assetLibrary =
			testPutAssetLibraryPermissionsPage_addAssetLibrary();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			assetLibraryResource.putAssetLibraryPermissionsPageHttpResponse(
				assetLibrary.getId(),
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
			assetLibraryResource.putAssetLibraryPermissionsPageHttpResponse(
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

	protected AssetLibrary testPutAssetLibraryPermissionsPage_addAssetLibrary()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutAssetLibraryPin() throws Exception {
		AssetLibrary postAssetLibrary =
			testPutAssetLibraryPin_addAssetLibrary();

		AssetLibrary randomAssetLibrary = randomAssetLibrary();

		AssetLibrary putAssetLibrary = assetLibraryResource.putAssetLibraryPin(
			postAssetLibrary.getId());

		assertEquals(randomAssetLibrary, putAssetLibrary);
		assertValid(putAssetLibrary);

		AssetLibrary getAssetLibrary = testPutAssetLibraryPin_getAssetLibrary(
			putAssetLibrary.getId());

		assertEquals(randomAssetLibrary, getAssetLibrary);
		assertValid(getAssetLibrary);
	}

	protected AssetLibrary testPutAssetLibraryPin_getAssetLibrary(
		Long assetLibraryId) {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected AssetLibrary testPutAssetLibraryPin_addAssetLibrary()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		AssetLibrary assetLibrary1 =
			testBatchEngineDeleteImportTask_addAssetLibrary();

		testBatchEngineDeleteImportTask_deleteAssetLibrary(
			200, assetLibrary1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			assetLibraryResource.getAssetLibraryHttpResponse(
				assetLibrary1.getId()));

		assetLibrary1 = testBatchEngineDeleteImportTask_addAssetLibrary();

		testBatchEngineDeleteImportTask_deleteAssetLibrary(
			200, null, assetLibrary1.getId());

		assertHttpResponseStatusCode(
			404,
			assetLibraryResource.getAssetLibraryHttpResponse(
				assetLibrary1.getId()));

		assetLibrary1 = testBatchEngineDeleteImportTask_addAssetLibrary();
		AssetLibrary assetLibrary2 =
			testBatchEngineDeleteImportTask_addAssetLibrary();

		testBatchEngineDeleteImportTask_deleteAssetLibrary(
			200, assetLibrary2.getExternalReferenceCode(),
			assetLibrary1.getId());

		assertHttpResponseStatusCode(
			404,
			assetLibraryResource.getAssetLibraryHttpResponse(
				assetLibrary1.getId()));
		assertHttpResponseStatusCode(
			200,
			assetLibraryResource.getAssetLibraryHttpResponse(
				assetLibrary2.getId()));

		testBatchEngineDeleteImportTask_deleteAssetLibrary(
			200, assetLibrary2.getExternalReferenceCode(),
			assetLibrary1.getId());

		assertHttpResponseStatusCode(
			404,
			assetLibraryResource.getAssetLibraryHttpResponse(
				assetLibrary2.getId()));
	}

	protected AssetLibrary testBatchEngineDeleteImportTask_addAssetLibrary()
		throws Exception {

		return testDeleteAssetLibrary_addAssetLibrary();
	}

	protected void testBatchEngineDeleteImportTask_deleteAssetLibrary(
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
				"com.liferay.headless.asset.library.dto.v1_0.AssetLibrary",
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

	protected void assertContains(
		AssetLibrary assetLibrary, List<AssetLibrary> assetLibraries) {

		boolean contains = false;

		for (AssetLibrary item : assetLibraries) {
			if (equals(assetLibrary, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			assetLibraries + " does not contain " + assetLibrary, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		AssetLibrary assetLibrary1, AssetLibrary assetLibrary2) {

		Assert.assertTrue(
			assetLibrary1 + " does not equal " + assetLibrary2,
			equals(assetLibrary1, assetLibrary2));
	}

	protected void assertEquals(
		List<AssetLibrary> assetLibraries1,
		List<AssetLibrary> assetLibraries2) {

		Assert.assertEquals(assetLibraries1.size(), assetLibraries2.size());

		for (int i = 0; i < assetLibraries1.size(); i++) {
			AssetLibrary assetLibrary1 = assetLibraries1.get(i);
			AssetLibrary assetLibrary2 = assetLibraries2.get(i);

			assertEquals(assetLibrary1, assetLibrary2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<AssetLibrary> assetLibraries1,
		List<AssetLibrary> assetLibraries2) {

		Assert.assertEquals(assetLibraries1.size(), assetLibraries2.size());

		for (AssetLibrary assetLibrary1 : assetLibraries1) {
			boolean contains = false;

			for (AssetLibrary assetLibrary2 : assetLibraries2) {
				if (equals(assetLibrary1, assetLibrary2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				assetLibraries2 + " does not contain " + assetLibrary1,
				contains);
		}
	}

	protected void assertValid(AssetLibrary assetLibrary) throws Exception {
		boolean valid = true;

		if (assetLibrary.getDateCreated() == null) {
			valid = false;
		}

		if (assetLibrary.getDateModified() == null) {
			valid = false;
		}

		if (assetLibrary.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				assetLibrary.getAssetLibraryKey(),
				testDepotEntryGroup.getGroupKey()) &&
			!Objects.equals(assetLibrary.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (assetLibrary.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetLibraryKey", additionalAssertFieldName)) {
				if (assetLibrary.getAssetLibraryKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("connectedSites", additionalAssertFieldName)) {
				if (assetLibrary.getConnectedSites() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creatorUserId", additionalAssertFieldName)) {
				if (assetLibrary.getCreatorUserId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (assetLibrary.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (assetLibrary.getDescription_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (assetLibrary.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (assetLibrary.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (assetLibrary.getName_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfConnectedSites", additionalAssertFieldName)) {

				if (assetLibrary.getNumberOfConnectedSites() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfUserAccounts", additionalAssertFieldName)) {

				if (assetLibrary.getNumberOfUserAccounts() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfUserGroups", additionalAssertFieldName)) {

				if (assetLibrary.getNumberOfUserGroups() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (assetLibrary.getPermissions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("settings", additionalAssertFieldName)) {
				if (assetLibrary.getSettings() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (assetLibrary.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("userAccounts", additionalAssertFieldName)) {
				if (assetLibrary.getUserAccounts() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("userGroups", additionalAssertFieldName)) {
				if (assetLibrary.getUserGroups() == null) {
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

	protected void assertValid(Page<AssetLibrary> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<AssetLibrary> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<AssetLibrary> assetLibraries = page.getItems();

		int size = assetLibraries.size();

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
					com.liferay.headless.asset.library.dto.v1_0.AssetLibrary.
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
		AssetLibrary assetLibrary1, AssetLibrary assetLibrary2) {

		if (assetLibrary1 == assetLibrary2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)assetLibrary1.getActions(),
						(Map)assetLibrary2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("connectedSites", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetLibrary1.getConnectedSites(),
						assetLibrary2.getConnectedSites())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creatorUserId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetLibrary1.getCreatorUserId(),
						assetLibrary2.getCreatorUserId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetLibrary1.getDateCreated(),
						assetLibrary2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetLibrary1.getDateModified(),
						assetLibrary2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetLibrary1.getDescription(),
						assetLibrary2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)assetLibrary1.getDescription_i18n(),
						(Map)assetLibrary2.getDescription_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						assetLibrary1.getExternalReferenceCode(),
						assetLibrary2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetLibrary1.getId(), assetLibrary2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetLibrary1.getName(), assetLibrary2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)assetLibrary1.getName_i18n(),
						(Map)assetLibrary2.getName_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfConnectedSites", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						assetLibrary1.getNumberOfConnectedSites(),
						assetLibrary2.getNumberOfConnectedSites())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfUserAccounts", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						assetLibrary1.getNumberOfUserAccounts(),
						assetLibrary2.getNumberOfUserAccounts())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfUserGroups", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						assetLibrary1.getNumberOfUserGroups(),
						assetLibrary2.getNumberOfUserGroups())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetLibrary1.getPermissions(),
						assetLibrary2.getPermissions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("settings", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetLibrary1.getSettings(),
						assetLibrary2.getSettings())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetLibrary1.getType(), assetLibrary2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("userAccounts", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetLibrary1.getUserAccounts(),
						assetLibrary2.getUserAccounts())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("userGroups", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetLibrary1.getUserGroups(),
						assetLibrary2.getUserGroups())) {

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

		if (!(_assetLibraryResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_assetLibraryResource;

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
		EntityField entityField, String operator, AssetLibrary assetLibrary) {

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

		if (entityFieldName.equals("assetLibraryKey")) {
			Object object = assetLibrary.getAssetLibraryKey();

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

		if (entityFieldName.equals("connectedSites")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("creatorUserId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = assetLibrary.getDateCreated();

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

				sb.append(_format.format(assetLibrary.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = assetLibrary.getDateModified();

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

				sb.append(_format.format(assetLibrary.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = assetLibrary.getDescription();

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
			Object object = assetLibrary.getExternalReferenceCode();

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

		if (entityFieldName.equals("name")) {
			Object object = assetLibrary.getName();

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

		if (entityFieldName.equals("numberOfConnectedSites")) {
			sb.append(String.valueOf(assetLibrary.getNumberOfConnectedSites()));

			return sb.toString();
		}

		if (entityFieldName.equals("numberOfUserAccounts")) {
			sb.append(String.valueOf(assetLibrary.getNumberOfUserAccounts()));

			return sb.toString();
		}

		if (entityFieldName.equals("numberOfUserGroups")) {
			sb.append(String.valueOf(assetLibrary.getNumberOfUserGroups()));

			return sb.toString();
		}

		if (entityFieldName.equals("permissions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("settings")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("type")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("userAccounts")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("userGroups")) {
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

	protected AssetLibrary randomAssetLibrary() throws Exception {
		return new AssetLibrary() {
			{
				assetLibraryKey = String.valueOf(
					testDepotEntry.getDepotEntryId());
				creatorUserId = RandomTestUtil.randomLong();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				numberOfConnectedSites = RandomTestUtil.randomInt();
				numberOfUserAccounts = RandomTestUtil.randomInt();
				numberOfUserGroups = RandomTestUtil.randomInt();
				siteId = testGroup.getGroupId();
			}
		};
	}

	protected AssetLibrary randomIrrelevantAssetLibrary() throws Exception {
		AssetLibrary randomIrrelevantAssetLibrary = randomAssetLibrary();

		randomIrrelevantAssetLibrary.setAssetLibraryKey(
			String.valueOf(irrelevantDepotEntry.getDepotEntryId()));

		randomIrrelevantAssetLibrary.setSiteId(irrelevantGroup.getGroupId());

		return randomIrrelevantAssetLibrary;
	}

	protected AssetLibrary randomPatchAssetLibrary() throws Exception {
		return randomAssetLibrary();
	}

	protected AssetLibrary randomPermissionsAssetLibrary() throws Exception {
		AssetLibrary assetLibrary = randomAssetLibrary();

		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assetLibrary.setPermissions(
			new Permission[] {
				new Permission() {
					{
						setActionIds(new String[] {"VIEW"});
						setRoleName(role.getName());
					}
				}
			});

		return assetLibrary;
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

	protected AssetLibraryResource assetLibraryResource;
	protected ImportTaskResource importTaskResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected AssetLibraryResource permissionsAssetLibraryResource;
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
		LogFactoryUtil.getLog(BaseAssetLibraryResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.asset.library.resource.v1_0.AssetLibraryResource
			_assetLibraryResource;

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