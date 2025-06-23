/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.admin.user.client.dto.v1_0.SharedAsset;
import com.liferay.headless.admin.user.client.http.HttpInvoker;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.pagination.Pagination;
import com.liferay.headless.admin.user.client.resource.v1_0.SharedAssetResource;
import com.liferay.headless.admin.user.client.serdes.v1_0.SharedAssetSerDes;
import com.liferay.petra.function.UnsafeTriConsumer;
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
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseSharedAssetResourceTestCase {

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

		_sharedAssetResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		sharedAssetResource = SharedAssetResource.builder(
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

		SharedAsset sharedAsset1 = randomSharedAsset();

		String json = objectMapper.writeValueAsString(sharedAsset1);

		SharedAsset sharedAsset2 = SharedAssetSerDes.toDTO(json);

		Assert.assertTrue(equals(sharedAsset1, sharedAsset2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		SharedAsset sharedAsset = randomSharedAsset();

		String json1 = objectMapper.writeValueAsString(sharedAsset);
		String json2 = SharedAssetSerDes.toJSON(sharedAsset);

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

		SharedAsset sharedAsset = randomSharedAsset();

		sharedAsset.setAssetType(regex);
		sharedAsset.setClassName(regex);
		sharedAsset.setExternalReferenceCode(regex);
		sharedAsset.setFileTypeIcon(regex);
		sharedAsset.setFileTypeIconColor(regex);
		sharedAsset.setSiteName(regex);
		sharedAsset.setTitle(regex);

		String json = SharedAssetSerDes.toJSON(sharedAsset);

		Assert.assertFalse(json.contains(regex));

		sharedAsset = SharedAssetSerDes.toDTO(json);

		Assert.assertEquals(regex, sharedAsset.getAssetType());
		Assert.assertEquals(regex, sharedAsset.getClassName());
		Assert.assertEquals(regex, sharedAsset.getExternalReferenceCode());
		Assert.assertEquals(regex, sharedAsset.getFileTypeIcon());
		Assert.assertEquals(regex, sharedAsset.getFileTypeIconColor());
		Assert.assertEquals(regex, sharedAsset.getSiteName());
		Assert.assertEquals(regex, sharedAsset.getTitle());
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedByMePage()
		throws Exception {

		Page<SharedAsset> page =
			sharedAssetResource.getMyUserAccountSharedAssetsSharedByMePage(
				null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		SharedAsset sharedAsset1 =
			testGetMyUserAccountSharedAssetsSharedByMePage_addSharedAsset(
				randomSharedAsset());

		SharedAsset sharedAsset2 =
			testGetMyUserAccountSharedAssetsSharedByMePage_addSharedAsset(
				randomSharedAsset());

		page = sharedAssetResource.getMyUserAccountSharedAssetsSharedByMePage(
			null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(sharedAsset1, (List<SharedAsset>)page.getItems());
		assertContains(sharedAsset2, (List<SharedAsset>)page.getItems());
		assertValid(
			page,
			testGetMyUserAccountSharedAssetsSharedByMePage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetMyUserAccountSharedAssetsSharedByMePage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedByMePageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		SharedAsset sharedAsset1 = randomSharedAsset();

		sharedAsset1 =
			testGetMyUserAccountSharedAssetsSharedByMePage_addSharedAsset(
				sharedAsset1);

		for (EntityField entityField : entityFields) {
			Page<SharedAsset> page =
				sharedAssetResource.getMyUserAccountSharedAssetsSharedByMePage(
					null, null,
					getFilterString(entityField, "between", sharedAsset1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(sharedAsset1),
				(List<SharedAsset>)page.getItems());
		}
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedByMePageWithFilterDoubleEquals()
		throws Exception {

		testGetMyUserAccountSharedAssetsSharedByMePageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedByMePageWithFilterStringContains()
		throws Exception {

		testGetMyUserAccountSharedAssetsSharedByMePageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedByMePageWithFilterStringEquals()
		throws Exception {

		testGetMyUserAccountSharedAssetsSharedByMePageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedByMePageWithFilterStringStartsWith()
		throws Exception {

		testGetMyUserAccountSharedAssetsSharedByMePageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetMyUserAccountSharedAssetsSharedByMePageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		SharedAsset sharedAsset1 =
			testGetMyUserAccountSharedAssetsSharedByMePage_addSharedAsset(
				randomSharedAsset());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		SharedAsset sharedAsset2 =
			testGetMyUserAccountSharedAssetsSharedByMePage_addSharedAsset(
				randomSharedAsset());

		for (EntityField entityField : entityFields) {
			Page<SharedAsset> page =
				sharedAssetResource.getMyUserAccountSharedAssetsSharedByMePage(
					null, null,
					getFilterString(entityField, operator, sharedAsset1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(sharedAsset1),
				(List<SharedAsset>)page.getItems());
		}
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedByMePageWithPagination()
		throws Exception {

		Page<SharedAsset> sharedAssetsPage =
			sharedAssetResource.getMyUserAccountSharedAssetsSharedByMePage(
				null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			sharedAssetsPage.getTotalCount());

		SharedAsset sharedAsset1 =
			testGetMyUserAccountSharedAssetsSharedByMePage_addSharedAsset(
				randomSharedAsset());

		SharedAsset sharedAsset2 =
			testGetMyUserAccountSharedAssetsSharedByMePage_addSharedAsset(
				randomSharedAsset());

		SharedAsset sharedAsset3 =
			testGetMyUserAccountSharedAssetsSharedByMePage_addSharedAsset(
				randomSharedAsset());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<SharedAsset> page1 =
				sharedAssetResource.getMyUserAccountSharedAssetsSharedByMePage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(sharedAsset1, (List<SharedAsset>)page1.getItems());

			Page<SharedAsset> page2 =
				sharedAssetResource.getMyUserAccountSharedAssetsSharedByMePage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(sharedAsset2, (List<SharedAsset>)page2.getItems());

			Page<SharedAsset> page3 =
				sharedAssetResource.getMyUserAccountSharedAssetsSharedByMePage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(sharedAsset3, (List<SharedAsset>)page3.getItems());
		}
		else {
			Page<SharedAsset> page1 =
				sharedAssetResource.getMyUserAccountSharedAssetsSharedByMePage(
					null, null, null, Pagination.of(1, totalCount + 2), null);

			List<SharedAsset> sharedAssets1 =
				(List<SharedAsset>)page1.getItems();

			Assert.assertEquals(
				sharedAssets1.toString(), totalCount + 2, sharedAssets1.size());

			Page<SharedAsset> page2 =
				sharedAssetResource.getMyUserAccountSharedAssetsSharedByMePage(
					null, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<SharedAsset> sharedAssets2 =
				(List<SharedAsset>)page2.getItems();

			Assert.assertEquals(
				sharedAssets2.toString(), 1, sharedAssets2.size());

			Page<SharedAsset> page3 =
				sharedAssetResource.getMyUserAccountSharedAssetsSharedByMePage(
					null, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(sharedAsset1, (List<SharedAsset>)page3.getItems());
			assertContains(sharedAsset2, (List<SharedAsset>)page3.getItems());
			assertContains(sharedAsset3, (List<SharedAsset>)page3.getItems());
		}
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedByMePageWithSortDateTime()
		throws Exception {

		testGetMyUserAccountSharedAssetsSharedByMePageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, sharedAsset1, sharedAsset2) -> {
				BeanTestUtil.setProperty(
					sharedAsset1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedByMePageWithSortDouble()
		throws Exception {

		testGetMyUserAccountSharedAssetsSharedByMePageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, sharedAsset1, sharedAsset2) -> {
				BeanTestUtil.setProperty(
					sharedAsset1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					sharedAsset2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedByMePageWithSortInteger()
		throws Exception {

		testGetMyUserAccountSharedAssetsSharedByMePageWithSort(
			EntityField.Type.INTEGER,
			(entityField, sharedAsset1, sharedAsset2) -> {
				BeanTestUtil.setProperty(
					sharedAsset1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					sharedAsset2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedByMePageWithSortString()
		throws Exception {

		testGetMyUserAccountSharedAssetsSharedByMePageWithSort(
			EntityField.Type.STRING,
			(entityField, sharedAsset1, sharedAsset2) -> {
				Class<?> clazz = sharedAsset1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						sharedAsset1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						sharedAsset2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						sharedAsset1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						sharedAsset2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						sharedAsset1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						sharedAsset2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetMyUserAccountSharedAssetsSharedByMePageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, SharedAsset, SharedAsset, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		SharedAsset sharedAsset1 = randomSharedAsset();
		SharedAsset sharedAsset2 = randomSharedAsset();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, sharedAsset1, sharedAsset2);
		}

		sharedAsset1 =
			testGetMyUserAccountSharedAssetsSharedByMePage_addSharedAsset(
				sharedAsset1);

		sharedAsset2 =
			testGetMyUserAccountSharedAssetsSharedByMePage_addSharedAsset(
				sharedAsset2);

		Page<SharedAsset> page =
			sharedAssetResource.getMyUserAccountSharedAssetsSharedByMePage(
				null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<SharedAsset> ascPage =
				sharedAssetResource.getMyUserAccountSharedAssetsSharedByMePage(
					null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(sharedAsset1, (List<SharedAsset>)ascPage.getItems());
			assertContains(sharedAsset2, (List<SharedAsset>)ascPage.getItems());

			Page<SharedAsset> descPage =
				sharedAssetResource.getMyUserAccountSharedAssetsSharedByMePage(
					null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				sharedAsset2, (List<SharedAsset>)descPage.getItems());
			assertContains(
				sharedAsset1, (List<SharedAsset>)descPage.getItems());
		}
	}

	protected SharedAsset
			testGetMyUserAccountSharedAssetsSharedByMePage_addSharedAsset(
				SharedAsset sharedAsset)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedWithMePage()
		throws Exception {

		Page<SharedAsset> page =
			sharedAssetResource.getMyUserAccountSharedAssetsSharedWithMePage(
				null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		SharedAsset sharedAsset1 =
			testGetMyUserAccountSharedAssetsSharedWithMePage_addSharedAsset(
				randomSharedAsset());

		SharedAsset sharedAsset2 =
			testGetMyUserAccountSharedAssetsSharedWithMePage_addSharedAsset(
				randomSharedAsset());

		page = sharedAssetResource.getMyUserAccountSharedAssetsSharedWithMePage(
			null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(sharedAsset1, (List<SharedAsset>)page.getItems());
		assertContains(sharedAsset2, (List<SharedAsset>)page.getItems());
		assertValid(
			page,
			testGetMyUserAccountSharedAssetsSharedWithMePage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetMyUserAccountSharedAssetsSharedWithMePage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedWithMePageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		SharedAsset sharedAsset1 = randomSharedAsset();

		sharedAsset1 =
			testGetMyUserAccountSharedAssetsSharedWithMePage_addSharedAsset(
				sharedAsset1);

		for (EntityField entityField : entityFields) {
			Page<SharedAsset> page =
				sharedAssetResource.
					getMyUserAccountSharedAssetsSharedWithMePage(
						null, null,
						getFilterString(entityField, "between", sharedAsset1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(sharedAsset1),
				(List<SharedAsset>)page.getItems());
		}
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedWithMePageWithFilterDoubleEquals()
		throws Exception {

		testGetMyUserAccountSharedAssetsSharedWithMePageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedWithMePageWithFilterStringContains()
		throws Exception {

		testGetMyUserAccountSharedAssetsSharedWithMePageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedWithMePageWithFilterStringEquals()
		throws Exception {

		testGetMyUserAccountSharedAssetsSharedWithMePageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedWithMePageWithFilterStringStartsWith()
		throws Exception {

		testGetMyUserAccountSharedAssetsSharedWithMePageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetMyUserAccountSharedAssetsSharedWithMePageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		SharedAsset sharedAsset1 =
			testGetMyUserAccountSharedAssetsSharedWithMePage_addSharedAsset(
				randomSharedAsset());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		SharedAsset sharedAsset2 =
			testGetMyUserAccountSharedAssetsSharedWithMePage_addSharedAsset(
				randomSharedAsset());

		for (EntityField entityField : entityFields) {
			Page<SharedAsset> page =
				sharedAssetResource.
					getMyUserAccountSharedAssetsSharedWithMePage(
						null, null,
						getFilterString(entityField, operator, sharedAsset1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(sharedAsset1),
				(List<SharedAsset>)page.getItems());
		}
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedWithMePageWithPagination()
		throws Exception {

		Page<SharedAsset> sharedAssetsPage =
			sharedAssetResource.getMyUserAccountSharedAssetsSharedWithMePage(
				null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			sharedAssetsPage.getTotalCount());

		SharedAsset sharedAsset1 =
			testGetMyUserAccountSharedAssetsSharedWithMePage_addSharedAsset(
				randomSharedAsset());

		SharedAsset sharedAsset2 =
			testGetMyUserAccountSharedAssetsSharedWithMePage_addSharedAsset(
				randomSharedAsset());

		SharedAsset sharedAsset3 =
			testGetMyUserAccountSharedAssetsSharedWithMePage_addSharedAsset(
				randomSharedAsset());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<SharedAsset> page1 =
				sharedAssetResource.
					getMyUserAccountSharedAssetsSharedWithMePage(
						null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(sharedAsset1, (List<SharedAsset>)page1.getItems());

			Page<SharedAsset> page2 =
				sharedAssetResource.
					getMyUserAccountSharedAssetsSharedWithMePage(
						null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(sharedAsset2, (List<SharedAsset>)page2.getItems());

			Page<SharedAsset> page3 =
				sharedAssetResource.
					getMyUserAccountSharedAssetsSharedWithMePage(
						null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(sharedAsset3, (List<SharedAsset>)page3.getItems());
		}
		else {
			Page<SharedAsset> page1 =
				sharedAssetResource.
					getMyUserAccountSharedAssetsSharedWithMePage(
						null, null, null, Pagination.of(1, totalCount + 2),
						null);

			List<SharedAsset> sharedAssets1 =
				(List<SharedAsset>)page1.getItems();

			Assert.assertEquals(
				sharedAssets1.toString(), totalCount + 2, sharedAssets1.size());

			Page<SharedAsset> page2 =
				sharedAssetResource.
					getMyUserAccountSharedAssetsSharedWithMePage(
						null, null, null, Pagination.of(2, totalCount + 2),
						null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<SharedAsset> sharedAssets2 =
				(List<SharedAsset>)page2.getItems();

			Assert.assertEquals(
				sharedAssets2.toString(), 1, sharedAssets2.size());

			Page<SharedAsset> page3 =
				sharedAssetResource.
					getMyUserAccountSharedAssetsSharedWithMePage(
						null, null, null, Pagination.of(1, (int)totalCount + 3),
						null);

			assertContains(sharedAsset1, (List<SharedAsset>)page3.getItems());
			assertContains(sharedAsset2, (List<SharedAsset>)page3.getItems());
			assertContains(sharedAsset3, (List<SharedAsset>)page3.getItems());
		}
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedWithMePageWithSortDateTime()
		throws Exception {

		testGetMyUserAccountSharedAssetsSharedWithMePageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, sharedAsset1, sharedAsset2) -> {
				BeanTestUtil.setProperty(
					sharedAsset1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedWithMePageWithSortDouble()
		throws Exception {

		testGetMyUserAccountSharedAssetsSharedWithMePageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, sharedAsset1, sharedAsset2) -> {
				BeanTestUtil.setProperty(
					sharedAsset1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					sharedAsset2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedWithMePageWithSortInteger()
		throws Exception {

		testGetMyUserAccountSharedAssetsSharedWithMePageWithSort(
			EntityField.Type.INTEGER,
			(entityField, sharedAsset1, sharedAsset2) -> {
				BeanTestUtil.setProperty(
					sharedAsset1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					sharedAsset2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetMyUserAccountSharedAssetsSharedWithMePageWithSortString()
		throws Exception {

		testGetMyUserAccountSharedAssetsSharedWithMePageWithSort(
			EntityField.Type.STRING,
			(entityField, sharedAsset1, sharedAsset2) -> {
				Class<?> clazz = sharedAsset1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						sharedAsset1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						sharedAsset2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						sharedAsset1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						sharedAsset2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						sharedAsset1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						sharedAsset2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetMyUserAccountSharedAssetsSharedWithMePageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, SharedAsset, SharedAsset, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		SharedAsset sharedAsset1 = randomSharedAsset();
		SharedAsset sharedAsset2 = randomSharedAsset();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, sharedAsset1, sharedAsset2);
		}

		sharedAsset1 =
			testGetMyUserAccountSharedAssetsSharedWithMePage_addSharedAsset(
				sharedAsset1);

		sharedAsset2 =
			testGetMyUserAccountSharedAssetsSharedWithMePage_addSharedAsset(
				sharedAsset2);

		Page<SharedAsset> page =
			sharedAssetResource.getMyUserAccountSharedAssetsSharedWithMePage(
				null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<SharedAsset> ascPage =
				sharedAssetResource.
					getMyUserAccountSharedAssetsSharedWithMePage(
						null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(sharedAsset1, (List<SharedAsset>)ascPage.getItems());
			assertContains(sharedAsset2, (List<SharedAsset>)ascPage.getItems());

			Page<SharedAsset> descPage =
				sharedAssetResource.
					getMyUserAccountSharedAssetsSharedWithMePage(
						null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				sharedAsset2, (List<SharedAsset>)descPage.getItems());
			assertContains(
				sharedAsset1, (List<SharedAsset>)descPage.getItems());
		}
	}

	protected SharedAsset
			testGetMyUserAccountSharedAssetsSharedWithMePage_addSharedAsset(
				SharedAsset sharedAsset)
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

	protected void assertContains(
		SharedAsset sharedAsset, List<SharedAsset> sharedAssets) {

		boolean contains = false;

		for (SharedAsset item : sharedAssets) {
			if (equals(sharedAsset, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			sharedAssets + " does not contain " + sharedAsset, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		SharedAsset sharedAsset1, SharedAsset sharedAsset2) {

		Assert.assertTrue(
			sharedAsset1 + " does not equal " + sharedAsset2,
			equals(sharedAsset1, sharedAsset2));
	}

	protected void assertEquals(
		List<SharedAsset> sharedAssets1, List<SharedAsset> sharedAssets2) {

		Assert.assertEquals(sharedAssets1.size(), sharedAssets2.size());

		for (int i = 0; i < sharedAssets1.size(); i++) {
			SharedAsset sharedAsset1 = sharedAssets1.get(i);
			SharedAsset sharedAsset2 = sharedAssets2.get(i);

			assertEquals(sharedAsset1, sharedAsset2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<SharedAsset> sharedAssets1, List<SharedAsset> sharedAssets2) {

		Assert.assertEquals(sharedAssets1.size(), sharedAssets2.size());

		for (SharedAsset sharedAsset1 : sharedAssets1) {
			boolean contains = false;

			for (SharedAsset sharedAsset2 : sharedAssets2) {
				if (equals(sharedAsset1, sharedAsset2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				sharedAssets2 + " does not contain " + sharedAsset1, contains);
		}
	}

	protected void assertValid(SharedAsset sharedAsset) throws Exception {
		boolean valid = true;

		if (sharedAsset.getDateCreated() == null) {
			valid = false;
		}

		if (sharedAsset.getDateModified() == null) {
			valid = false;
		}

		if (sharedAsset.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actionIds", additionalAssertFieldName)) {
				if (sharedAsset.getActionIds() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (sharedAsset.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetType", additionalAssertFieldName)) {
				if (sharedAsset.getAssetType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("className", additionalAssertFieldName)) {
				if (sharedAsset.getClassName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("classPK", additionalAssertFieldName)) {
				if (sharedAsset.getClassPK() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (sharedAsset.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (sharedAsset.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("fileTypeIcon", additionalAssertFieldName)) {
				if (sharedAsset.getFileTypeIcon() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"fileTypeIconColor", additionalAssertFieldName)) {

				if (sharedAsset.getFileTypeIconColor() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shareable", additionalAssertFieldName)) {
				if (sharedAsset.getShareable() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("siteName", additionalAssertFieldName)) {
				if (sharedAsset.getSiteName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (sharedAsset.getTitle() == null) {
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

	protected void assertValid(Page<SharedAsset> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<SharedAsset> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<SharedAsset> sharedAssets = page.getItems();

		int size = sharedAssets.size();

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
					com.liferay.headless.admin.user.dto.v1_0.SharedAsset.
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
		SharedAsset sharedAsset1, SharedAsset sharedAsset2) {

		if (sharedAsset1 == sharedAsset2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actionIds", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sharedAsset1.getActionIds(),
						sharedAsset2.getActionIds())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)sharedAsset1.getActions(),
						(Map)sharedAsset2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assetType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sharedAsset1.getAssetType(),
						sharedAsset2.getAssetType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("className", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sharedAsset1.getClassName(),
						sharedAsset2.getClassName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("classPK", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sharedAsset1.getClassPK(), sharedAsset2.getClassPK())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sharedAsset1.getCreator(), sharedAsset2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sharedAsset1.getDateCreated(),
						sharedAsset2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sharedAsset1.getDateModified(),
						sharedAsset2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sharedAsset1.getExternalReferenceCode(),
						sharedAsset2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fileTypeIcon", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sharedAsset1.getFileTypeIcon(),
						sharedAsset2.getFileTypeIcon())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"fileTypeIconColor", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sharedAsset1.getFileTypeIconColor(),
						sharedAsset2.getFileTypeIconColor())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sharedAsset1.getId(), sharedAsset2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shareable", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sharedAsset1.getShareable(),
						sharedAsset2.getShareable())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("siteName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sharedAsset1.getSiteName(),
						sharedAsset2.getSiteName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sharedAsset1.getTitle(), sharedAsset2.getTitle())) {

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

		if (!(_sharedAssetResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_sharedAssetResource;

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
		EntityField entityField, String operator, SharedAsset sharedAsset) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("actionIds")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("assetType")) {
			Object object = sharedAsset.getAssetType();

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

		if (entityFieldName.equals("className")) {
			Object object = sharedAsset.getClassName();

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

		if (entityFieldName.equals("classPK")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = sharedAsset.getDateCreated();

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

				sb.append(_format.format(sharedAsset.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = sharedAsset.getDateModified();

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

				sb.append(_format.format(sharedAsset.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = sharedAsset.getExternalReferenceCode();

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

		if (entityFieldName.equals("fileTypeIcon")) {
			Object object = sharedAsset.getFileTypeIcon();

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

		if (entityFieldName.equals("fileTypeIconColor")) {
			Object object = sharedAsset.getFileTypeIconColor();

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

		if (entityFieldName.equals("shareable")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("siteName")) {
			Object object = sharedAsset.getSiteName();

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
			Object object = sharedAsset.getTitle();

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

	protected SharedAsset randomSharedAsset() throws Exception {
		return new SharedAsset() {
			{
				assetType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				className = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				classPK = RandomTestUtil.randomLong();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				fileTypeIcon = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				fileTypeIconColor = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				shareable = RandomTestUtil.randomBoolean();
				siteName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected SharedAsset randomIrrelevantSharedAsset() throws Exception {
		SharedAsset randomIrrelevantSharedAsset = randomSharedAsset();

		return randomIrrelevantSharedAsset;
	}

	protected SharedAsset randomPatchSharedAsset() throws Exception {
		return randomSharedAsset();
	}

	protected SharedAssetResource sharedAssetResource;
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
		LogFactoryUtil.getLog(BaseSharedAssetResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.user.resource.v1_0.SharedAssetResource
		_sharedAssetResource;

}