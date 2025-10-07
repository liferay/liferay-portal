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
import com.liferay.headless.delivery.client.dto.v1_0.ContentStructure;
import com.liferay.headless.delivery.client.dto.v1_0.Field;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.permission.Permission;
import com.liferay.headless.delivery.client.resource.v1_0.ContentStructureResource;
import com.liferay.headless.delivery.client.serdes.v1_0.ContentStructureSerDes;
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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseContentStructureResourceTestCase {

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

		_contentStructureResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		contentStructureResource = ContentStructureResource.builder(
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

		ContentStructure contentStructure1 = randomContentStructure();

		String json = objectMapper.writeValueAsString(contentStructure1);

		ContentStructure contentStructure2 = ContentStructureSerDes.toDTO(json);

		Assert.assertTrue(equals(contentStructure1, contentStructure2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ContentStructure contentStructure = randomContentStructure();

		String json1 = objectMapper.writeValueAsString(contentStructure);
		String json2 = ContentStructureSerDes.toJSON(contentStructure);

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

		ContentStructure contentStructure = randomContentStructure();

		contentStructure.setAssetLibraryKey(regex);
		contentStructure.setDescription(regex);
		contentStructure.setName(regex);

		String json = ContentStructureSerDes.toJSON(contentStructure);

		Assert.assertFalse(json.contains(regex));

		contentStructure = ContentStructureSerDes.toDTO(json);

		Assert.assertEquals(regex, contentStructure.getAssetLibraryKey());
		Assert.assertEquals(regex, contentStructure.getDescription());
		Assert.assertEquals(regex, contentStructure.getName());
	}

	@Test
	public void testGetAssetLibraryContentStructurePermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ContentStructure postContentStructure =
			testGetAssetLibraryContentStructurePermissionsPage_addContentStructure();

		Page<Permission> page =
			contentStructureResource.
				getAssetLibraryContentStructurePermissionsPage(
					testDepotEntry.getDepotEntryId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected ContentStructure
			testGetAssetLibraryContentStructurePermissionsPage_addContentStructure()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetAssetLibraryContentStructuresPage() throws Exception {
		Long assetLibraryId =
			testGetAssetLibraryContentStructuresPage_getAssetLibraryId();
		Long irrelevantAssetLibraryId =
			testGetAssetLibraryContentStructuresPage_getIrrelevantAssetLibraryId();

		Page<ContentStructure> page =
			contentStructureResource.getAssetLibraryContentStructuresPage(
				assetLibraryId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryId != null) {
			ContentStructure irrelevantContentStructure =
				testGetAssetLibraryContentStructuresPage_addContentStructure(
					irrelevantAssetLibraryId,
					randomIrrelevantContentStructure());

			page =
				contentStructureResource.getAssetLibraryContentStructuresPage(
					irrelevantAssetLibraryId, null, null, null,
					Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantContentStructure,
				(List<ContentStructure>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryContentStructuresPage_getExpectedActions(
					irrelevantAssetLibraryId));
		}

		ContentStructure contentStructure1 =
			testGetAssetLibraryContentStructuresPage_addContentStructure(
				assetLibraryId, randomContentStructure());

		ContentStructure contentStructure2 =
			testGetAssetLibraryContentStructuresPage_addContentStructure(
				assetLibraryId, randomContentStructure());

		page = contentStructureResource.getAssetLibraryContentStructuresPage(
			assetLibraryId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			contentStructure1, (List<ContentStructure>)page.getItems());
		assertContains(
			contentStructure2, (List<ContentStructure>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryContentStructuresPage_getExpectedActions(
				assetLibraryId));
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryContentStructuresPage_getExpectedActions(
				Long assetLibraryId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryContentStructuresPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryContentStructuresPage_getAssetLibraryId();

		ContentStructure contentStructure1 = randomContentStructure();

		contentStructure1 =
			testGetAssetLibraryContentStructuresPage_addContentStructure(
				assetLibraryId, contentStructure1);

		for (EntityField entityField : entityFields) {
			Page<ContentStructure> page =
				contentStructureResource.getAssetLibraryContentStructuresPage(
					assetLibraryId, null, null,
					getFilterString(entityField, "between", contentStructure1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(contentStructure1),
				(List<ContentStructure>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryContentStructuresPageWithFilterDoubleEquals()
		throws Exception {

		testGetAssetLibraryContentStructuresPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAssetLibraryContentStructuresPageWithFilterStringContains()
		throws Exception {

		testGetAssetLibraryContentStructuresPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryContentStructuresPageWithFilterStringEquals()
		throws Exception {

		testGetAssetLibraryContentStructuresPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryContentStructuresPageWithFilterStringStartsWith()
		throws Exception {

		testGetAssetLibraryContentStructuresPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetAssetLibraryContentStructuresPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryContentStructuresPage_getAssetLibraryId();

		ContentStructure contentStructure1 =
			testGetAssetLibraryContentStructuresPage_addContentStructure(
				assetLibraryId, randomContentStructure());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ContentStructure contentStructure2 =
			testGetAssetLibraryContentStructuresPage_addContentStructure(
				assetLibraryId, randomContentStructure());

		for (EntityField entityField : entityFields) {
			Page<ContentStructure> page =
				contentStructureResource.getAssetLibraryContentStructuresPage(
					assetLibraryId, null, null,
					getFilterString(entityField, operator, contentStructure1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(contentStructure1),
				(List<ContentStructure>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryContentStructuresPageWithPagination()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryContentStructuresPage_getAssetLibraryId();

		Page<ContentStructure> contentStructuresPage =
			contentStructureResource.getAssetLibraryContentStructuresPage(
				assetLibraryId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			contentStructuresPage.getTotalCount());

		ContentStructure contentStructure1 =
			testGetAssetLibraryContentStructuresPage_addContentStructure(
				assetLibraryId, randomContentStructure());

		ContentStructure contentStructure2 =
			testGetAssetLibraryContentStructuresPage_addContentStructure(
				assetLibraryId, randomContentStructure());

		ContentStructure contentStructure3 =
			testGetAssetLibraryContentStructuresPage_addContentStructure(
				assetLibraryId, randomContentStructure());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ContentStructure> page1 =
				contentStructureResource.getAssetLibraryContentStructuresPage(
					assetLibraryId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				contentStructure1, (List<ContentStructure>)page1.getItems());

			Page<ContentStructure> page2 =
				contentStructureResource.getAssetLibraryContentStructuresPage(
					assetLibraryId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				contentStructure2, (List<ContentStructure>)page2.getItems());

			Page<ContentStructure> page3 =
				contentStructureResource.getAssetLibraryContentStructuresPage(
					assetLibraryId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				contentStructure3, (List<ContentStructure>)page3.getItems());
		}
		else {
			Page<ContentStructure> page1 =
				contentStructureResource.getAssetLibraryContentStructuresPage(
					assetLibraryId, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<ContentStructure> contentStructures1 =
				(List<ContentStructure>)page1.getItems();

			Assert.assertEquals(
				contentStructures1.toString(), totalCount + 2,
				contentStructures1.size());

			Page<ContentStructure> page2 =
				contentStructureResource.getAssetLibraryContentStructuresPage(
					assetLibraryId, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ContentStructure> contentStructures2 =
				(List<ContentStructure>)page2.getItems();

			Assert.assertEquals(
				contentStructures2.toString(), 1, contentStructures2.size());

			Page<ContentStructure> page3 =
				contentStructureResource.getAssetLibraryContentStructuresPage(
					assetLibraryId, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				contentStructure1, (List<ContentStructure>)page3.getItems());
			assertContains(
				contentStructure2, (List<ContentStructure>)page3.getItems());
			assertContains(
				contentStructure3, (List<ContentStructure>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryContentStructuresPageWithSortDateTime()
		throws Exception {

		testGetAssetLibraryContentStructuresPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, contentStructure1, contentStructure2) -> {
				BeanTestUtil.setProperty(
					contentStructure1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetLibraryContentStructuresPageWithSortDouble()
		throws Exception {

		testGetAssetLibraryContentStructuresPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, contentStructure1, contentStructure2) -> {
				BeanTestUtil.setProperty(
					contentStructure1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					contentStructure2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetLibraryContentStructuresPageWithSortInteger()
		throws Exception {

		testGetAssetLibraryContentStructuresPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, contentStructure1, contentStructure2) -> {
				BeanTestUtil.setProperty(
					contentStructure1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					contentStructure2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetLibraryContentStructuresPageWithSortString()
		throws Exception {

		testGetAssetLibraryContentStructuresPageWithSort(
			EntityField.Type.STRING,
			(entityField, contentStructure1, contentStructure2) -> {
				Class<?> clazz = contentStructure1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						contentStructure1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						contentStructure2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						contentStructure1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						contentStructure2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						contentStructure1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						contentStructure2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAssetLibraryContentStructuresPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ContentStructure, ContentStructure, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryContentStructuresPage_getAssetLibraryId();

		ContentStructure contentStructure1 = randomContentStructure();
		ContentStructure contentStructure2 = randomContentStructure();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, contentStructure1, contentStructure2);
		}

		contentStructure1 =
			testGetAssetLibraryContentStructuresPage_addContentStructure(
				assetLibraryId, contentStructure1);

		contentStructure2 =
			testGetAssetLibraryContentStructuresPage_addContentStructure(
				assetLibraryId, contentStructure2);

		Page<ContentStructure> page =
			contentStructureResource.getAssetLibraryContentStructuresPage(
				assetLibraryId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ContentStructure> ascPage =
				contentStructureResource.getAssetLibraryContentStructuresPage(
					assetLibraryId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				contentStructure1, (List<ContentStructure>)ascPage.getItems());
			assertContains(
				contentStructure2, (List<ContentStructure>)ascPage.getItems());

			Page<ContentStructure> descPage =
				contentStructureResource.getAssetLibraryContentStructuresPage(
					assetLibraryId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				contentStructure2, (List<ContentStructure>)descPage.getItems());
			assertContains(
				contentStructure1, (List<ContentStructure>)descPage.getItems());
		}
	}

	protected ContentStructure
			testGetAssetLibraryContentStructuresPage_addContentStructure(
				Long assetLibraryId, ContentStructure contentStructure)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAssetLibraryContentStructuresPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Long
			testGetAssetLibraryContentStructuresPage_getIrrelevantAssetLibraryId()
		throws Exception {

		return irrelevantDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGetContentStructure() throws Exception {
		ContentStructure postContentStructure =
			testGetContentStructure_addContentStructure();

		ContentStructure getContentStructure =
			contentStructureResource.getContentStructure(
				postContentStructure.getId());

		assertEquals(postContentStructure, getContentStructure);
		assertValid(getContentStructure);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ContentStructure postContentStructure =
			testGetContentStructure_addContentStructure();

		ContentStructure getContentStructure =
			contentStructureResource.getContentStructure(
				postContentStructure.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.delivery.dto.v1_0.ContentStructure"
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
			postContentStructure.getId());

		assertEquals(
			getContentStructure, ContentStructureSerDes.toDTO(item.toString()));
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

	protected ContentStructure testGetContentStructure_addContentStructure()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetContentStructure() throws Exception {
		ContentStructure contentStructure =
			testGraphQLGetContentStructure_addContentStructure();

		// No namespace

		Assert.assertTrue(
			equals(
				contentStructure,
				ContentStructureSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"contentStructure",
								new HashMap<String, Object>() {
									{
										put(
											"contentStructureId",
											contentStructure.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/contentStructure"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				contentStructure,
				ContentStructureSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"contentStructure",
									new HashMap<String, Object>() {
										{
											put(
												"contentStructureId",
												contentStructure.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/contentStructure"))));
	}

	@Test
	public void testGraphQLGetContentStructureNotFound() throws Exception {
		Long irrelevantContentStructureId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"contentStructure",
						new HashMap<String, Object>() {
							{
								put(
									"contentStructureId",
									irrelevantContentStructureId);
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
							"contentStructure",
							new HashMap<String, Object>() {
								{
									put(
										"contentStructureId",
										irrelevantContentStructureId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ContentStructure
			testGraphQLGetContentStructure_addContentStructure()
		throws Exception {

		return testGraphQLContentStructure_addContentStructure();
	}

	@Test
	public void testGetContentStructurePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ContentStructure postContentStructure =
			testGetContentStructurePermissionsPage_addContentStructure();

		Page<Permission> page =
			contentStructureResource.getContentStructurePermissionsPage(
				postContentStructure.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected ContentStructure
			testGetContentStructurePermissionsPage_addContentStructure()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteContentStructurePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ContentStructure postContentStructure =
			testGetSiteContentStructurePermissionsPage_addContentStructure();

		Page<Permission> page =
			contentStructureResource.getSiteContentStructurePermissionsPage(
				testGroup.getGroupId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected ContentStructure
			testGetSiteContentStructurePermissionsPage_addContentStructure()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteContentStructuresPage() throws Exception {
		Long siteId = testGetSiteContentStructuresPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteContentStructuresPage_getIrrelevantSiteId();

		Page<ContentStructure> page =
			contentStructureResource.getSiteContentStructuresPage(
				siteId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			ContentStructure irrelevantContentStructure =
				testGetSiteContentStructuresPage_addContentStructure(
					irrelevantSiteId, randomIrrelevantContentStructure());

			page = contentStructureResource.getSiteContentStructuresPage(
				irrelevantSiteId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantContentStructure,
				(List<ContentStructure>)page.getItems());
			assertValid(
				page,
				testGetSiteContentStructuresPage_getExpectedActions(
					irrelevantSiteId));
		}

		ContentStructure contentStructure1 =
			testGetSiteContentStructuresPage_addContentStructure(
				siteId, randomContentStructure());

		ContentStructure contentStructure2 =
			testGetSiteContentStructuresPage_addContentStructure(
				siteId, randomContentStructure());

		page = contentStructureResource.getSiteContentStructuresPage(
			siteId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			contentStructure1, (List<ContentStructure>)page.getItems());
		assertContains(
			contentStructure2, (List<ContentStructure>)page.getItems());
		assertValid(
			page, testGetSiteContentStructuresPage_getExpectedActions(siteId));
	}

	protected Map<String, Map<String, String>>
			testGetSiteContentStructuresPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteContentStructuresPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteContentStructuresPage_getSiteId();

		ContentStructure contentStructure1 = randomContentStructure();

		contentStructure1 =
			testGetSiteContentStructuresPage_addContentStructure(
				siteId, contentStructure1);

		for (EntityField entityField : entityFields) {
			Page<ContentStructure> page =
				contentStructureResource.getSiteContentStructuresPage(
					siteId, null, null,
					getFilterString(entityField, "between", contentStructure1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(contentStructure1),
				(List<ContentStructure>)page.getItems());
		}
	}

	@Test
	public void testGetSiteContentStructuresPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteContentStructuresPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteContentStructuresPageWithFilterStringContains()
		throws Exception {

		testGetSiteContentStructuresPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteContentStructuresPageWithFilterStringEquals()
		throws Exception {

		testGetSiteContentStructuresPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteContentStructuresPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteContentStructuresPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteContentStructuresPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteContentStructuresPage_getSiteId();

		ContentStructure contentStructure1 =
			testGetSiteContentStructuresPage_addContentStructure(
				siteId, randomContentStructure());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ContentStructure contentStructure2 =
			testGetSiteContentStructuresPage_addContentStructure(
				siteId, randomContentStructure());

		for (EntityField entityField : entityFields) {
			Page<ContentStructure> page =
				contentStructureResource.getSiteContentStructuresPage(
					siteId, null, null,
					getFilterString(entityField, operator, contentStructure1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(contentStructure1),
				(List<ContentStructure>)page.getItems());
		}
	}

	@Test
	public void testGetSiteContentStructuresPageWithPagination()
		throws Exception {

		Long siteId = testGetSiteContentStructuresPage_getSiteId();

		Page<ContentStructure> contentStructuresPage =
			contentStructureResource.getSiteContentStructuresPage(
				siteId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			contentStructuresPage.getTotalCount());

		ContentStructure contentStructure1 =
			testGetSiteContentStructuresPage_addContentStructure(
				siteId, randomContentStructure());

		ContentStructure contentStructure2 =
			testGetSiteContentStructuresPage_addContentStructure(
				siteId, randomContentStructure());

		ContentStructure contentStructure3 =
			testGetSiteContentStructuresPage_addContentStructure(
				siteId, randomContentStructure());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ContentStructure> page1 =
				contentStructureResource.getSiteContentStructuresPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				contentStructure1, (List<ContentStructure>)page1.getItems());

			Page<ContentStructure> page2 =
				contentStructureResource.getSiteContentStructuresPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				contentStructure2, (List<ContentStructure>)page2.getItems());

			Page<ContentStructure> page3 =
				contentStructureResource.getSiteContentStructuresPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				contentStructure3, (List<ContentStructure>)page3.getItems());
		}
		else {
			Page<ContentStructure> page1 =
				contentStructureResource.getSiteContentStructuresPage(
					siteId, null, null, null, Pagination.of(1, totalCount + 2),
					null);

			List<ContentStructure> contentStructures1 =
				(List<ContentStructure>)page1.getItems();

			Assert.assertEquals(
				contentStructures1.toString(), totalCount + 2,
				contentStructures1.size());

			Page<ContentStructure> page2 =
				contentStructureResource.getSiteContentStructuresPage(
					siteId, null, null, null, Pagination.of(2, totalCount + 2),
					null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ContentStructure> contentStructures2 =
				(List<ContentStructure>)page2.getItems();

			Assert.assertEquals(
				contentStructures2.toString(), 1, contentStructures2.size());

			Page<ContentStructure> page3 =
				contentStructureResource.getSiteContentStructuresPage(
					siteId, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				contentStructure1, (List<ContentStructure>)page3.getItems());
			assertContains(
				contentStructure2, (List<ContentStructure>)page3.getItems());
			assertContains(
				contentStructure3, (List<ContentStructure>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteContentStructuresPageWithSortDateTime()
		throws Exception {

		testGetSiteContentStructuresPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, contentStructure1, contentStructure2) -> {
				BeanTestUtil.setProperty(
					contentStructure1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteContentStructuresPageWithSortDouble()
		throws Exception {

		testGetSiteContentStructuresPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, contentStructure1, contentStructure2) -> {
				BeanTestUtil.setProperty(
					contentStructure1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					contentStructure2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteContentStructuresPageWithSortInteger()
		throws Exception {

		testGetSiteContentStructuresPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, contentStructure1, contentStructure2) -> {
				BeanTestUtil.setProperty(
					contentStructure1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					contentStructure2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteContentStructuresPageWithSortString()
		throws Exception {

		testGetSiteContentStructuresPageWithSort(
			EntityField.Type.STRING,
			(entityField, contentStructure1, contentStructure2) -> {
				Class<?> clazz = contentStructure1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						contentStructure1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						contentStructure2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						contentStructure1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						contentStructure2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						contentStructure1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						contentStructure2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteContentStructuresPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ContentStructure, ContentStructure, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteContentStructuresPage_getSiteId();

		ContentStructure contentStructure1 = randomContentStructure();
		ContentStructure contentStructure2 = randomContentStructure();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, contentStructure1, contentStructure2);
		}

		contentStructure1 =
			testGetSiteContentStructuresPage_addContentStructure(
				siteId, contentStructure1);

		contentStructure2 =
			testGetSiteContentStructuresPage_addContentStructure(
				siteId, contentStructure2);

		Page<ContentStructure> page =
			contentStructureResource.getSiteContentStructuresPage(
				siteId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ContentStructure> ascPage =
				contentStructureResource.getSiteContentStructuresPage(
					siteId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				contentStructure1, (List<ContentStructure>)ascPage.getItems());
			assertContains(
				contentStructure2, (List<ContentStructure>)ascPage.getItems());

			Page<ContentStructure> descPage =
				contentStructureResource.getSiteContentStructuresPage(
					siteId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				contentStructure2, (List<ContentStructure>)descPage.getItems());
			assertContains(
				contentStructure1, (List<ContentStructure>)descPage.getItems());
		}
	}

	protected ContentStructure
			testGetSiteContentStructuresPage_addContentStructure(
				Long siteId, ContentStructure contentStructure)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetSiteContentStructuresPage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long testGetSiteContentStructuresPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testPutAssetLibraryContentStructurePermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ContentStructure contentStructure =
			testPutAssetLibraryContentStructurePermissionsPage_addContentStructure();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			contentStructureResource.
				putAssetLibraryContentStructurePermissionsPageHttpResponse(
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
			contentStructureResource.
				putAssetLibraryContentStructurePermissionsPageHttpResponse(
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

	protected ContentStructure
			testPutAssetLibraryContentStructurePermissionsPage_addContentStructure()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutContentStructurePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ContentStructure contentStructure =
			testPutContentStructurePermissionsPage_addContentStructure();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			contentStructureResource.
				putContentStructurePermissionsPageHttpResponse(
					contentStructure.getId(),
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
			contentStructureResource.
				putContentStructurePermissionsPageHttpResponse(
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

	protected ContentStructure
			testPutContentStructurePermissionsPage_addContentStructure()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteContentStructurePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ContentStructure contentStructure =
			testPutSiteContentStructurePermissionsPage_addContentStructure();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			contentStructureResource.
				putSiteContentStructurePermissionsPageHttpResponse(
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
			contentStructureResource.
				putSiteContentStructurePermissionsPageHttpResponse(
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

	protected ContentStructure
			testPutSiteContentStructurePermissionsPage_addContentStructure()
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

	protected ContentStructure testGraphQLContentStructure_addContentStructure()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		ContentStructure contentStructure,
		List<ContentStructure> contentStructures) {

		boolean contains = false;

		for (ContentStructure item : contentStructures) {
			if (equals(contentStructure, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			contentStructures + " does not contain " + contentStructure,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ContentStructure contentStructure1,
		ContentStructure contentStructure2) {

		Assert.assertTrue(
			contentStructure1 + " does not equal " + contentStructure2,
			equals(contentStructure1, contentStructure2));
	}

	protected void assertEquals(
		List<ContentStructure> contentStructures1,
		List<ContentStructure> contentStructures2) {

		Assert.assertEquals(
			contentStructures1.size(), contentStructures2.size());

		for (int i = 0; i < contentStructures1.size(); i++) {
			ContentStructure contentStructure1 = contentStructures1.get(i);
			ContentStructure contentStructure2 = contentStructures2.get(i);

			assertEquals(contentStructure1, contentStructure2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ContentStructure> contentStructures1,
		List<ContentStructure> contentStructures2) {

		Assert.assertEquals(
			contentStructures1.size(), contentStructures2.size());

		for (ContentStructure contentStructure1 : contentStructures1) {
			boolean contains = false;

			for (ContentStructure contentStructure2 : contentStructures2) {
				if (equals(contentStructure1, contentStructure2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				contentStructures2 + " does not contain " + contentStructure1,
				contains);
		}
	}

	protected void assertValid(ContentStructure contentStructure)
		throws Exception {

		boolean valid = true;

		if (contentStructure.getDateCreated() == null) {
			valid = false;
		}

		if (contentStructure.getDateModified() == null) {
			valid = false;
		}

		if (contentStructure.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				contentStructure.getAssetLibraryKey(),
				testDepotEntryGroup.getGroupKey()) &&
			!Objects.equals(
				contentStructure.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("assetLibraryKey", additionalAssertFieldName)) {
				if (contentStructure.getAssetLibraryKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (contentStructure.getAvailableLanguages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"contentStructureFields", additionalAssertFieldName)) {

				if (contentStructure.getContentStructureFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (contentStructure.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (contentStructure.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (contentStructure.getDescription_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (contentStructure.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (contentStructure.getName_i18n() == null) {
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

	protected void assertValid(Page<ContentStructure> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ContentStructure> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ContentStructure> contentStructures =
			page.getItems();

		int size = contentStructures.size();

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

		graphQLFields.add(new GraphQLField("id"));

		graphQLFields.add(new GraphQLField("siteId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.delivery.dto.v1_0.ContentStructure.
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
		ContentStructure contentStructure1,
		ContentStructure contentStructure2) {

		if (contentStructure1 == contentStructure2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						contentStructure1.getAvailableLanguages(),
						contentStructure2.getAvailableLanguages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"contentStructureFields", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						contentStructure1.getContentStructureFields(),
						contentStructure2.getContentStructureFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentStructure1.getCreator(),
						contentStructure2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentStructure1.getDateCreated(),
						contentStructure2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentStructure1.getDateModified(),
						contentStructure2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentStructure1.getDescription(),
						contentStructure2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)contentStructure1.getDescription_i18n(),
						(Map)contentStructure2.getDescription_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentStructure1.getId(), contentStructure2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentStructure1.getName(),
						contentStructure2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)contentStructure1.getName_i18n(),
						(Map)contentStructure2.getName_i18n())) {

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

		if (!(_contentStructureResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_contentStructureResource;

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
		ContentStructure contentStructure) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("assetLibraryKey")) {
			Object object = contentStructure.getAssetLibraryKey();

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

		if (entityFieldName.equals("contentStructureFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = contentStructure.getDateCreated();

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

				sb.append(_format.format(contentStructure.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = contentStructure.getDateModified();

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

				sb.append(_format.format(contentStructure.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = contentStructure.getDescription();

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

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = contentStructure.getName();

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

		if (entityFieldName.equals("siteId")) {
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

	protected ContentStructure randomContentStructure() throws Exception {
		return new ContentStructure() {
			{
				assetLibraryKey = String.valueOf(
					testDepotEntry.getDepotEntryId());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				siteId = testGroup.getGroupId();
			}
		};
	}

	protected ContentStructure randomIrrelevantContentStructure()
		throws Exception {

		ContentStructure randomIrrelevantContentStructure =
			randomContentStructure();

		randomIrrelevantContentStructure.setAssetLibraryKey(
			String.valueOf(irrelevantDepotEntry.getDepotEntryId()));

		randomIrrelevantContentStructure.setSiteId(
			irrelevantGroup.getGroupId());

		return randomIrrelevantContentStructure;
	}

	protected ContentStructure randomPatchContentStructure() throws Exception {
		return randomContentStructure();
	}

	protected ContentStructureResource contentStructureResource;
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
		LogFactoryUtil.getLog(BaseContentStructureResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.delivery.resource.v1_0.ContentStructureResource
		_contentStructureResource;

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