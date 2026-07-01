/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.admin.fragment.client.dto.v1_0.BasicFragment;
import com.liferay.headless.admin.fragment.client.dto.v1_0.FormFragment;
import com.liferay.headless.admin.fragment.client.dto.v1_0.Fragment;
import com.liferay.headless.admin.fragment.client.http.HttpInvoker;
import com.liferay.headless.admin.fragment.client.pagination.Page;
import com.liferay.headless.admin.fragment.client.pagination.Pagination;
import com.liferay.headless.admin.fragment.client.resource.v1_0.FragmentResource;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.FragmentSerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
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
import com.liferay.portal.kernel.util.PortalUtil;
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
import java.util.function.Supplier;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public abstract class BaseFragmentResourceTestCase {

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

		_fragmentResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		fragmentResource = FragmentResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
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

		Fragment fragment1 = randomFragment();

		String json = objectMapper.writeValueAsString(fragment1);

		Fragment fragment2 = FragmentSerDes.toDTO(json);

		Assert.assertTrue(equals(fragment1, fragment2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Fragment fragment = randomFragment();

		String json1 = objectMapper.writeValueAsString(fragment);
		String json2 = FragmentSerDes.toJSON(fragment);

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

		Fragment fragment = randomFragment();

		fragment.setExternalReferenceCode(regex);
		fragment.setIcon(regex);
		fragment.setKey(regex);
		fragment.setName(regex);

		String json = FragmentSerDes.toJSON(fragment);

		Assert.assertFalse(json.contains(regex));

		fragment = FragmentSerDes.toDTO(json);

		Assert.assertEquals(regex, fragment.getExternalReferenceCode());
		Assert.assertEquals(regex, fragment.getIcon());
		Assert.assertEquals(regex, fragment.getKey());
		Assert.assertEquals(regex, fragment.getName());
	}

	@Test
	public void testDeleteSiteFragment() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Fragment fragment = testDeleteSiteFragment_addFragment();

		assertHttpResponseStatusCode(
			204,
			fragmentResource.deleteSiteFragmentHttpResponse(
				testDeleteSiteFragment_getSiteExternalReferenceCode(),
				fragment.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			fragmentResource.getSiteFragmentHttpResponse(
				testDeleteSiteFragment_getSiteExternalReferenceCode(),
				fragment.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			fragmentResource.getSiteFragmentHttpResponse(
				testDeleteSiteFragment_getSiteExternalReferenceCode(), "-"));
	}

	protected Fragment testDeleteSiteFragment_addFragment() throws Exception {
		return fragmentResource.postSiteFragment(
			testGroup.getExternalReferenceCode(), randomFragment());
	}

	protected String testDeleteSiteFragment_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetSiteFragment() throws Exception {
		Fragment postFragment = testGetSiteFragment_addFragment();

		Fragment getFragment = fragmentResource.getSiteFragment(
			testGetSiteFragment_getSiteExternalReferenceCode(),
			postFragment.getExternalReferenceCode());

		assertEquals(postFragment, getFragment);
		assertValid(getFragment);
	}

	protected Fragment testGetSiteFragment_addFragment() throws Exception {
		return fragmentResource.postSiteFragment(
			testGroup.getExternalReferenceCode(), randomFragment());
	}

	protected String testGetSiteFragment_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetSiteFragmentSetFragmentsPage() throws Exception {
		String siteExternalReferenceCode =
			testGetSiteFragmentSetFragmentsPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteFragmentSetFragmentsPage_getIrrelevantSiteExternalReferenceCode();
		String fragmentSetExternalReferenceCode =
			testGetSiteFragmentSetFragmentsPage_getFragmentSetExternalReferenceCode();
		String irrelevantFragmentSetExternalReferenceCode =
			testGetSiteFragmentSetFragmentsPage_getIrrelevantFragmentSetExternalReferenceCode();

		Page<Fragment> page = fragmentResource.getSiteFragmentSetFragmentsPage(
			siteExternalReferenceCode, fragmentSetExternalReferenceCode,
			Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantSiteExternalReferenceCode != null) &&
			(irrelevantFragmentSetExternalReferenceCode != null)) {

			Fragment irrelevantFragment =
				testGetSiteFragmentSetFragmentsPage_addFragment(
					irrelevantSiteExternalReferenceCode,
					irrelevantFragmentSetExternalReferenceCode,
					randomIrrelevantFragment());

			page = fragmentResource.getSiteFragmentSetFragmentsPage(
				irrelevantSiteExternalReferenceCode,
				irrelevantFragmentSetExternalReferenceCode,
				Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantFragment, (List<Fragment>)page.getItems());
			assertValid(
				page,
				testGetSiteFragmentSetFragmentsPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode,
					irrelevantFragmentSetExternalReferenceCode));
		}

		Fragment fragment1 = testGetSiteFragmentSetFragmentsPage_addFragment(
			siteExternalReferenceCode, fragmentSetExternalReferenceCode,
			randomFragment());

		Fragment fragment2 = testGetSiteFragmentSetFragmentsPage_addFragment(
			siteExternalReferenceCode, fragmentSetExternalReferenceCode,
			randomFragment());

		page = fragmentResource.getSiteFragmentSetFragmentsPage(
			siteExternalReferenceCode, fragmentSetExternalReferenceCode,
			Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(fragment1, (List<Fragment>)page.getItems());
		assertContains(fragment2, (List<Fragment>)page.getItems());
		assertValid(
			page,
			testGetSiteFragmentSetFragmentsPage_getExpectedActions(
				siteExternalReferenceCode, fragmentSetExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetSiteFragmentSetFragmentsPage_getExpectedActions(
				String siteExternalReferenceCode,
				String fragmentSetExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteFragmentSetFragmentsPageWithPagination()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteFragmentSetFragmentsPage_getSiteExternalReferenceCode();
		String fragmentSetExternalReferenceCode =
			testGetSiteFragmentSetFragmentsPage_getFragmentSetExternalReferenceCode();

		Page<Fragment> fragmentsPage =
			fragmentResource.getSiteFragmentSetFragmentsPage(
				siteExternalReferenceCode, fragmentSetExternalReferenceCode,
				null);

		int totalCount = GetterUtil.getInteger(fragmentsPage.getTotalCount());

		Fragment fragment1 = testGetSiteFragmentSetFragmentsPage_addFragment(
			siteExternalReferenceCode, fragmentSetExternalReferenceCode,
			randomFragment());

		Fragment fragment2 = testGetSiteFragmentSetFragmentsPage_addFragment(
			siteExternalReferenceCode, fragmentSetExternalReferenceCode,
			randomFragment());

		Fragment fragment3 = testGetSiteFragmentSetFragmentsPage_addFragment(
			siteExternalReferenceCode, fragmentSetExternalReferenceCode,
			randomFragment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Fragment> page1 =
				fragmentResource.getSiteFragmentSetFragmentsPage(
					siteExternalReferenceCode, fragmentSetExternalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(fragment1, (List<Fragment>)page1.getItems());

			Page<Fragment> page2 =
				fragmentResource.getSiteFragmentSetFragmentsPage(
					siteExternalReferenceCode, fragmentSetExternalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(fragment2, (List<Fragment>)page2.getItems());

			Page<Fragment> page3 =
				fragmentResource.getSiteFragmentSetFragmentsPage(
					siteExternalReferenceCode, fragmentSetExternalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(fragment3, (List<Fragment>)page3.getItems());
		}
		else {
			Page<Fragment> page1 =
				fragmentResource.getSiteFragmentSetFragmentsPage(
					siteExternalReferenceCode, fragmentSetExternalReferenceCode,
					Pagination.of(1, totalCount + 2));

			List<Fragment> fragments1 = (List<Fragment>)page1.getItems();

			Assert.assertEquals(
				fragments1.toString(), totalCount + 2, fragments1.size());

			Page<Fragment> page2 =
				fragmentResource.getSiteFragmentSetFragmentsPage(
					siteExternalReferenceCode, fragmentSetExternalReferenceCode,
					Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Fragment> fragments2 = (List<Fragment>)page2.getItems();

			Assert.assertEquals(fragments2.toString(), 1, fragments2.size());

			Page<Fragment> page3 =
				fragmentResource.getSiteFragmentSetFragmentsPage(
					siteExternalReferenceCode, fragmentSetExternalReferenceCode,
					Pagination.of(1, (int)totalCount + 3));

			assertContains(fragment1, (List<Fragment>)page3.getItems());
			assertContains(fragment2, (List<Fragment>)page3.getItems());
			assertContains(fragment3, (List<Fragment>)page3.getItems());
		}
	}

	protected Fragment testGetSiteFragmentSetFragmentsPage_addFragment(
			String siteExternalReferenceCode,
			String fragmentSetExternalReferenceCode, Fragment fragment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteFragmentSetFragmentsPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteFragmentSetFragmentsPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteFragmentSetFragmentsPage_getFragmentSetExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteFragmentSetFragmentsPage_getIrrelevantFragmentSetExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetSiteFragmentsPage() throws Exception {
		String siteExternalReferenceCode =
			testGetSiteFragmentsPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteFragmentsPage_getIrrelevantSiteExternalReferenceCode();

		Page<Fragment> page = fragmentResource.getSiteFragmentsPage(
			siteExternalReferenceCode, null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantSiteExternalReferenceCode != null) {
			Fragment irrelevantFragment = testGetSiteFragmentsPage_addFragment(
				irrelevantSiteExternalReferenceCode,
				randomIrrelevantFragment());

			page = fragmentResource.getSiteFragmentsPage(
				irrelevantSiteExternalReferenceCode, null,
				Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantFragment, (List<Fragment>)page.getItems());
			assertValid(
				page,
				testGetSiteFragmentsPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode));
		}

		Fragment fragment1 = testGetSiteFragmentsPage_addFragment(
			siteExternalReferenceCode, randomFragment());

		Fragment fragment2 = testGetSiteFragmentsPage_addFragment(
			siteExternalReferenceCode, randomFragment());

		page = fragmentResource.getSiteFragmentsPage(
			siteExternalReferenceCode, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(fragment1, (List<Fragment>)page.getItems());
		assertContains(fragment2, (List<Fragment>)page.getItems());
		assertValid(
			page,
			testGetSiteFragmentsPage_getExpectedActions(
				siteExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetSiteFragmentsPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			("http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/headless-admin-fragment/v1.0/sites/{siteExternalReferenceCode}/fragments/batch").
					replace(
						"{siteExternalReferenceCode}",
						String.valueOf(siteExternalReferenceCode)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteFragmentsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteFragmentsPage_getSiteExternalReferenceCode();

		Fragment fragment1 = randomFragment();

		fragment1 = testGetSiteFragmentsPage_addFragment(
			siteExternalReferenceCode, fragment1);

		for (EntityField entityField : entityFields) {
			Page<Fragment> page = fragmentResource.getSiteFragmentsPage(
				siteExternalReferenceCode,
				getFilterString(entityField, "between", fragment1),
				Pagination.of(1, 2));

			assertEquals(
				Collections.singletonList(fragment1),
				(List<Fragment>)page.getItems());
		}
	}

	@Test
	public void testGetSiteFragmentsPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteFragmentsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteFragmentsPageWithFilterStringContains()
		throws Exception {

		testGetSiteFragmentsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteFragmentsPageWithFilterStringEquals()
		throws Exception {

		testGetSiteFragmentsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteFragmentsPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteFragmentsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteFragmentsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteFragmentsPage_getSiteExternalReferenceCode();

		Fragment fragment1 = testGetSiteFragmentsPage_addFragment(
			siteExternalReferenceCode, randomFragment());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Fragment fragment2 = testGetSiteFragmentsPage_addFragment(
			siteExternalReferenceCode, randomFragment());

		for (EntityField entityField : entityFields) {
			Page<Fragment> page = fragmentResource.getSiteFragmentsPage(
				siteExternalReferenceCode,
				getFilterString(entityField, operator, fragment1),
				Pagination.of(1, 2));

			assertEquals(
				Collections.singletonList(fragment1),
				(List<Fragment>)page.getItems());
		}
	}

	@Test
	public void testGetSiteFragmentsPageWithPagination() throws Exception {
		String siteExternalReferenceCode =
			testGetSiteFragmentsPage_getSiteExternalReferenceCode();

		Page<Fragment> fragmentsPage = fragmentResource.getSiteFragmentsPage(
			siteExternalReferenceCode, null, null);

		int totalCount = GetterUtil.getInteger(fragmentsPage.getTotalCount());

		Fragment fragment1 = testGetSiteFragmentsPage_addFragment(
			siteExternalReferenceCode, randomFragment());

		Fragment fragment2 = testGetSiteFragmentsPage_addFragment(
			siteExternalReferenceCode, randomFragment());

		Fragment fragment3 = testGetSiteFragmentsPage_addFragment(
			siteExternalReferenceCode, randomFragment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Fragment> page1 = fragmentResource.getSiteFragmentsPage(
				siteExternalReferenceCode, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(fragment1, (List<Fragment>)page1.getItems());

			Page<Fragment> page2 = fragmentResource.getSiteFragmentsPage(
				siteExternalReferenceCode, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(fragment2, (List<Fragment>)page2.getItems());

			Page<Fragment> page3 = fragmentResource.getSiteFragmentsPage(
				siteExternalReferenceCode, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(fragment3, (List<Fragment>)page3.getItems());
		}
		else {
			Page<Fragment> page1 = fragmentResource.getSiteFragmentsPage(
				siteExternalReferenceCode, null,
				Pagination.of(1, totalCount + 2));

			List<Fragment> fragments1 = (List<Fragment>)page1.getItems();

			Assert.assertEquals(
				fragments1.toString(), totalCount + 2, fragments1.size());

			Page<Fragment> page2 = fragmentResource.getSiteFragmentsPage(
				siteExternalReferenceCode, null,
				Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Fragment> fragments2 = (List<Fragment>)page2.getItems();

			Assert.assertEquals(fragments2.toString(), 1, fragments2.size());

			Page<Fragment> page3 = fragmentResource.getSiteFragmentsPage(
				siteExternalReferenceCode, null,
				Pagination.of(1, (int)totalCount + 3));

			assertContains(fragment1, (List<Fragment>)page3.getItems());
			assertContains(fragment2, (List<Fragment>)page3.getItems());
			assertContains(fragment3, (List<Fragment>)page3.getItems());
		}
	}

	protected Fragment testGetSiteFragmentsPage_addFragment(
			String siteExternalReferenceCode, Fragment fragment)
		throws Exception {

		return fragmentResource.postSiteFragment(
			siteExternalReferenceCode, fragment);
	}

	protected String testGetSiteFragmentsPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteFragmentsPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Test
	public void testPostSiteFragment() throws Exception {
		Fragment randomFragment = randomFragment();

		Fragment postFragment = testPostSiteFragment_addFragment(
			randomFragment);

		assertEquals(randomFragment, postFragment);
		assertValid(postFragment);

		BasicFragment basicFragment = new BasicFragment() {
			{
				cacheable = RandomTestUtil.randomBoolean();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				icon = StringUtil.toLowerCase(RandomTestUtil.randomString());
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				marketplace = RandomTestUtil.randomBoolean();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				readOnly = RandomTestUtil.randomBoolean();

				type = Type.create("BasicFragment");
			}
		};

		assertEquals(
			basicFragment, testPostSiteFragment_addFragment(basicFragment));

		FormFragment formFragment = new FormFragment() {
			{
				cacheable = RandomTestUtil.randomBoolean();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				icon = StringUtil.toLowerCase(RandomTestUtil.randomString());
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				marketplace = RandomTestUtil.randomBoolean();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				readOnly = RandomTestUtil.randomBoolean();

				type = Type.create("FormFragment");
			}
		};

		assertEquals(
			formFragment, testPostSiteFragment_addFragment(formFragment));
	}

	protected Fragment testPostSiteFragment_addFragment(Fragment fragment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSiteFragmentSetFragment() throws Exception {
		Fragment randomFragment = randomFragment();

		Fragment postFragment = testPostSiteFragmentSetFragment_addFragment(
			randomFragment);

		assertEquals(randomFragment, postFragment);
		assertValid(postFragment);

		BasicFragment basicFragment = new BasicFragment() {
			{
				cacheable = RandomTestUtil.randomBoolean();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				icon = StringUtil.toLowerCase(RandomTestUtil.randomString());
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				marketplace = RandomTestUtil.randomBoolean();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				readOnly = RandomTestUtil.randomBoolean();

				type = Type.create("BasicFragment");
			}
		};

		assertEquals(
			basicFragment,
			testPostSiteFragmentSetFragment_addFragment(basicFragment));

		FormFragment formFragment = new FormFragment() {
			{
				cacheable = RandomTestUtil.randomBoolean();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				icon = StringUtil.toLowerCase(RandomTestUtil.randomString());
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				marketplace = RandomTestUtil.randomBoolean();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				readOnly = RandomTestUtil.randomBoolean();

				type = Type.create("FormFragment");
			}
		};

		assertEquals(
			formFragment,
			testPostSiteFragmentSetFragment_addFragment(formFragment));
	}

	protected Fragment testPostSiteFragmentSetFragment_addFragment(
			Fragment fragment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteFragment() throws Exception {
		Fragment postFragment = testPutSiteFragment_addFragment();

		Fragment randomFragment = randomFragment();

		Fragment putFragment = fragmentResource.putSiteFragment(
			testPutSiteFragment_getSiteExternalReferenceCode(),
			postFragment.getExternalReferenceCode(), randomFragment);

		assertEquals(randomFragment, putFragment);
		assertValid(putFragment);

		Fragment getFragment = fragmentResource.getSiteFragment(
			testPutSiteFragment_getSiteExternalReferenceCode(),
			putFragment.getExternalReferenceCode());

		assertEquals(randomFragment, getFragment);
		assertValid(getFragment);
	}

	protected Fragment testPutSiteFragment_addFragment() throws Exception {
		return fragmentResource.postSiteFragment(
			testGroup.getExternalReferenceCode(), randomFragment());
	}

	protected String testPutSiteFragment_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Fragment fragment1 = testBatchEngineDeleteImportTask_addSiteFragment();

		testBatchEngineDeleteImportTask_deleteFragment(
			200, fragment1.getExternalReferenceCode(),
			"siteExternalReferenceCode", testGroup.getExternalReferenceCode());

		assertHttpResponseStatusCode(
			404,
			fragmentResource.getSiteFragmentHttpResponse(
				testBatchEngineDeleteImportTask_getSiteExternalReferenceCode(),
				fragment1.getExternalReferenceCode()));
	}

	protected Fragment testBatchEngineDeleteImportTask_addSiteFragment()
		throws Exception {

		return testDeleteSiteFragment_addFragment();
	}

	protected void testBatchEngineDeleteImportTask_deleteFragment(
			int expectedStatusCode, String externalReferenceCode,
			String... parameters)
		throws Exception {

		ImportTaskResource importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).parameters(
			parameters
		).build();

		HttpResponse httpResponse =
			importTaskResource.deleteImportTaskHttpResponse(
				"com.liferay.headless.admin.fragment.dto.v1_0.Fragment", null,
				null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	protected String
			testBatchEngineDeleteImportTask_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertContains(Fragment fragment, List<Fragment> fragments) {
		boolean contains = false;

		for (Fragment item : fragments) {
			if (equals(fragment, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			fragments + " does not contain " + fragment, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Fragment fragment1, Fragment fragment2) {
		Assert.assertTrue(
			fragment1 + " does not equal " + fragment2,
			equals(fragment1, fragment2));
	}

	protected void assertEquals(
		List<Fragment> fragments1, List<Fragment> fragments2) {

		Assert.assertEquals(fragments1.size(), fragments2.size());

		for (int i = 0; i < fragments1.size(); i++) {
			Fragment fragment1 = fragments1.get(i);
			Fragment fragment2 = fragments2.get(i);

			assertEquals(fragment1, fragment2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Fragment> fragments1, List<Fragment> fragments2) {

		Assert.assertEquals(fragments1.size(), fragments2.size());

		for (Fragment fragment1 : fragments1) {
			boolean contains = false;

			for (Fragment fragment2 : fragments2) {
				if (equals(fragment1, fragment2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				fragments2 + " does not contain " + fragment1, contains);
		}
	}

	protected void assertValid(Fragment fragment) throws Exception {
		boolean valid = true;

		if (fragment.getDateCreated() == null) {
			valid = false;
		}

		if (fragment.getDateModified() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("cacheable", additionalAssertFieldName)) {
				if (fragment.getCacheable() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (fragment.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (fragment.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("fragmentSet", additionalAssertFieldName)) {
				if (fragment.getFragmentSet() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("fragmentVersions", additionalAssertFieldName)) {
				if (fragment.getFragmentVersions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("icon", additionalAssertFieldName)) {
				if (fragment.getIcon() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (fragment.getKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("marketplace", additionalAssertFieldName)) {
				if (fragment.getMarketplace() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (fragment.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("readOnly", additionalAssertFieldName)) {
				if (fragment.getReadOnly() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"thumbnailURLReference", additionalAssertFieldName)) {

				if (fragment.getThumbnailURLReference() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (fragment.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("fieldTypes", additionalAssertFieldName)) {
				if (!(fragment instanceof FormFragment)) {
					continue;
				}

				if (((FormFragment)fragment).getFieldTypes() == null) {
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

	protected void assertValid(Page<Fragment> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Fragment> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Fragment> fragments = page.getItems();

		int size = fragments.size();

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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.admin.fragment.dto.v1_0.Fragment.
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

	protected boolean equals(Fragment fragment1, Fragment fragment2) {
		if (fragment1 == fragment2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("cacheable", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragment1.getCacheable(), fragment2.getCacheable())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragment1.getCreator(), fragment2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragment1.getDateCreated(),
						fragment2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragment1.getDateModified(),
						fragment2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						fragment1.getExternalReferenceCode(),
						fragment2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fragmentSet", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragment1.getFragmentSet(),
						fragment2.getFragmentSet())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fragmentVersions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragment1.getFragmentVersions(),
						fragment2.getFragmentVersions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("icon", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragment1.getIcon(), fragment2.getIcon())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragment1.getKey(), fragment2.getKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("marketplace", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragment1.getMarketplace(),
						fragment2.getMarketplace())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragment1.getName(), fragment2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("readOnly", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragment1.getReadOnly(), fragment2.getReadOnly())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"thumbnailURLReference", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						fragment1.getThumbnailURLReference(),
						fragment2.getThumbnailURLReference())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragment1.getType(), fragment2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fieldTypes", additionalAssertFieldName)) {
				if (!(fragment1 instanceof FormFragment) ||
					!(fragment2 instanceof FormFragment)) {

					continue;
				}

				if (!Objects.deepEquals(
						((FormFragment)fragment1).getFieldTypes(),
						((FormFragment)fragment2).getFieldTypes())) {

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

		if (!(_fragmentResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_fragmentResource;

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
		EntityField entityField, String operator, Fragment fragment) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("cacheable")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = fragment.getDateCreated();

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

				sb.append(_format.format(fragment.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = fragment.getDateModified();

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

				sb.append(_format.format(fragment.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = fragment.getExternalReferenceCode();

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

		if (entityFieldName.equals("fragmentSet")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("fragmentVersions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("icon")) {
			Object object = fragment.getIcon();

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

		if (entityFieldName.equals("key")) {
			Object object = fragment.getKey();

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

		if (entityFieldName.equals("marketplace")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = fragment.getName();

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

		if (entityFieldName.equals("readOnly")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("thumbnailURLReference")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("type")) {
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
		httpInvoker.path(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/graphql");
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

	protected Fragment randomFragment() throws Exception {
		List<Supplier<Fragment>> suppliers = Arrays.asList(
			() -> {
				BasicFragment fragment = new BasicFragment();

				fragment.setCacheable(RandomTestUtil.randomBoolean());
				fragment.setDateCreated(RandomTestUtil.nextDate());
				fragment.setDateModified(RandomTestUtil.nextDate());
				fragment.setExternalReferenceCode(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				fragment.setIcon(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				fragment.setKey(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				fragment.setMarketplace(RandomTestUtil.randomBoolean());
				fragment.setName(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				fragment.setReadOnly(RandomTestUtil.randomBoolean());

				fragment.setType(Fragment.Type.create("BasicFragment"));

				return fragment;
			},
			() -> {
				FormFragment fragment = new FormFragment();

				fragment.setCacheable(RandomTestUtil.randomBoolean());
				fragment.setDateCreated(RandomTestUtil.nextDate());
				fragment.setDateModified(RandomTestUtil.nextDate());
				fragment.setExternalReferenceCode(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				fragment.setIcon(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				fragment.setKey(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				fragment.setMarketplace(RandomTestUtil.randomBoolean());
				fragment.setName(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				fragment.setReadOnly(RandomTestUtil.randomBoolean());

				fragment.setType(Fragment.Type.create("FormFragment"));

				return fragment;
			});

		Supplier<Fragment> supplier = suppliers.get(
			RandomTestUtil.randomInt(0, suppliers.size() - 1));

		return supplier.get();
	}

	protected Fragment randomIrrelevantFragment() throws Exception {
		Fragment randomIrrelevantFragment = randomFragment();

		return randomIrrelevantFragment;
	}

	protected Fragment randomPatchFragment() throws Exception {
		return randomFragment();
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

	protected FragmentResource fragmentResource;
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
		LogFactoryUtil.getLog(BaseFragmentResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.fragment.resource.v1_0.FragmentResource
		_fragmentResource;

}
// LIFERAY-REST-BUILDER-HASH:-344471918