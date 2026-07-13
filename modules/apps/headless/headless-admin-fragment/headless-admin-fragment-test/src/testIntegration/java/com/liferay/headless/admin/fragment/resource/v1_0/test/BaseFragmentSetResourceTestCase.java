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

import com.liferay.headless.admin.fragment.client.dto.v1_0.FragmentSet;
import com.liferay.headless.admin.fragment.client.http.HttpInvoker;
import com.liferay.headless.admin.fragment.client.pagination.Page;
import com.liferay.headless.admin.fragment.client.pagination.Pagination;
import com.liferay.headless.admin.fragment.client.resource.v1_0.FragmentSetResource;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.FragmentSetSerDes;
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
public abstract class BaseFragmentSetResourceTestCase {

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

		_fragmentSetResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		fragmentSetResource = FragmentSetResource.builder(
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

		FragmentSet fragmentSet1 = randomFragmentSet();

		String json = objectMapper.writeValueAsString(fragmentSet1);

		FragmentSet fragmentSet2 = FragmentSetSerDes.toDTO(json);

		Assert.assertTrue(equals(fragmentSet1, fragmentSet2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		FragmentSet fragmentSet = randomFragmentSet();

		String json1 = objectMapper.writeValueAsString(fragmentSet);
		String json2 = FragmentSetSerDes.toJSON(fragmentSet);

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

		FragmentSet fragmentSet = randomFragmentSet();

		fragmentSet.setDescription(regex);
		fragmentSet.setExternalReferenceCode(regex);
		fragmentSet.setKey(regex);
		fragmentSet.setName(regex);

		String json = FragmentSetSerDes.toJSON(fragmentSet);

		Assert.assertFalse(json.contains(regex));

		fragmentSet = FragmentSetSerDes.toDTO(json);

		Assert.assertEquals(regex, fragmentSet.getDescription());
		Assert.assertEquals(regex, fragmentSet.getExternalReferenceCode());
		Assert.assertEquals(regex, fragmentSet.getKey());
		Assert.assertEquals(regex, fragmentSet.getName());
	}

	@Test
	public void testDeleteDesignLibraryFragmentSet() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		FragmentSet fragmentSet =
			testDeleteDesignLibraryFragmentSet_addFragmentSet();

		assertHttpResponseStatusCode(
			204,
			fragmentSetResource.deleteDesignLibraryFragmentSetHttpResponse(
				testDeleteDesignLibraryFragmentSet_getDesignLibraryExternalReferenceCode(),
				fragmentSet.getExternalReferenceCode()));
	}

	protected FragmentSet testDeleteDesignLibraryFragmentSet_addFragmentSet()
		throws Exception {

		return fragmentSetResource.postSiteFragmentSet(
			testGroup.getExternalReferenceCode(), randomFragmentSet());
	}

	protected String
			testDeleteDesignLibraryFragmentSet_getDesignLibraryExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testDeleteSiteFragmentSet() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		FragmentSet fragmentSet = testDeleteSiteFragmentSet_addFragmentSet();

		assertHttpResponseStatusCode(
			204,
			fragmentSetResource.deleteSiteFragmentSetHttpResponse(
				testDeleteSiteFragmentSet_getSiteExternalReferenceCode(),
				fragmentSet.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			fragmentSetResource.getSiteFragmentSetHttpResponse(
				testDeleteSiteFragmentSet_getSiteExternalReferenceCode(),
				fragmentSet.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			fragmentSetResource.getSiteFragmentSetHttpResponse(
				testDeleteSiteFragmentSet_getSiteExternalReferenceCode(), "-"));
	}

	protected FragmentSet testDeleteSiteFragmentSet_addFragmentSet()
		throws Exception {

		return fragmentSetResource.postSiteFragmentSet(
			testGroup.getExternalReferenceCode(), randomFragmentSet());
	}

	protected String testDeleteSiteFragmentSet_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetSiteFragmentSet() throws Exception {
		FragmentSet postFragmentSet = testGetSiteFragmentSet_addFragmentSet();

		FragmentSet getFragmentSet = fragmentSetResource.getSiteFragmentSet(
			testGetSiteFragmentSet_getSiteExternalReferenceCode(),
			postFragmentSet.getExternalReferenceCode());

		assertEquals(postFragmentSet, getFragmentSet);
		assertValid(getFragmentSet);
	}

	protected FragmentSet testGetSiteFragmentSet_addFragmentSet()
		throws Exception {

		return fragmentSetResource.postSiteFragmentSet(
			testGroup.getExternalReferenceCode(), randomFragmentSet());
	}

	protected String testGetSiteFragmentSet_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetSiteFragmentSetsPage() throws Exception {
		String siteExternalReferenceCode =
			testGetSiteFragmentSetsPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteFragmentSetsPage_getIrrelevantSiteExternalReferenceCode();

		Page<FragmentSet> page = fragmentSetResource.getSiteFragmentSetsPage(
			siteExternalReferenceCode, null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantSiteExternalReferenceCode != null) {
			FragmentSet irrelevantFragmentSet =
				testGetSiteFragmentSetsPage_addFragmentSet(
					irrelevantSiteExternalReferenceCode,
					randomIrrelevantFragmentSet());

			page = fragmentSetResource.getSiteFragmentSetsPage(
				irrelevantSiteExternalReferenceCode, null,
				Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantFragmentSet, (List<FragmentSet>)page.getItems());
			assertValid(
				page,
				testGetSiteFragmentSetsPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode));
		}

		FragmentSet fragmentSet1 = testGetSiteFragmentSetsPage_addFragmentSet(
			siteExternalReferenceCode, randomFragmentSet());

		FragmentSet fragmentSet2 = testGetSiteFragmentSetsPage_addFragmentSet(
			siteExternalReferenceCode, randomFragmentSet());

		page = fragmentSetResource.getSiteFragmentSetsPage(
			siteExternalReferenceCode, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(fragmentSet1, (List<FragmentSet>)page.getItems());
		assertContains(fragmentSet2, (List<FragmentSet>)page.getItems());
		assertValid(
			page,
			testGetSiteFragmentSetsPage_getExpectedActions(
				siteExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetSiteFragmentSetsPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			("http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/headless-admin-fragment/v1.0/sites/{siteExternalReferenceCode}/fragment-sets/batch").
					replace(
						"{siteExternalReferenceCode}",
						String.valueOf(siteExternalReferenceCode)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteFragmentSetsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteFragmentSetsPage_getSiteExternalReferenceCode();

		FragmentSet fragmentSet1 = randomFragmentSet();

		fragmentSet1 = testGetSiteFragmentSetsPage_addFragmentSet(
			siteExternalReferenceCode, fragmentSet1);

		for (EntityField entityField : entityFields) {
			Page<FragmentSet> page =
				fragmentSetResource.getSiteFragmentSetsPage(
					siteExternalReferenceCode,
					getFilterString(entityField, "between", fragmentSet1),
					Pagination.of(1, 2));

			assertEquals(
				Collections.singletonList(fragmentSet1),
				(List<FragmentSet>)page.getItems());
		}
	}

	@Test
	public void testGetSiteFragmentSetsPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteFragmentSetsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteFragmentSetsPageWithFilterStringContains()
		throws Exception {

		testGetSiteFragmentSetsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteFragmentSetsPageWithFilterStringEquals()
		throws Exception {

		testGetSiteFragmentSetsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteFragmentSetsPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteFragmentSetsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteFragmentSetsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteFragmentSetsPage_getSiteExternalReferenceCode();

		FragmentSet fragmentSet1 = testGetSiteFragmentSetsPage_addFragmentSet(
			siteExternalReferenceCode, randomFragmentSet());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		FragmentSet fragmentSet2 = testGetSiteFragmentSetsPage_addFragmentSet(
			siteExternalReferenceCode, randomFragmentSet());

		for (EntityField entityField : entityFields) {
			Page<FragmentSet> page =
				fragmentSetResource.getSiteFragmentSetsPage(
					siteExternalReferenceCode,
					getFilterString(entityField, operator, fragmentSet1),
					Pagination.of(1, 2));

			assertEquals(
				Collections.singletonList(fragmentSet1),
				(List<FragmentSet>)page.getItems());
		}
	}

	@Test
	public void testGetSiteFragmentSetsPageWithPagination() throws Exception {
		String siteExternalReferenceCode =
			testGetSiteFragmentSetsPage_getSiteExternalReferenceCode();

		Page<FragmentSet> fragmentSetsPage =
			fragmentSetResource.getSiteFragmentSetsPage(
				siteExternalReferenceCode, null, null);

		int totalCount = GetterUtil.getInteger(
			fragmentSetsPage.getTotalCount());

		FragmentSet fragmentSet1 = testGetSiteFragmentSetsPage_addFragmentSet(
			siteExternalReferenceCode, randomFragmentSet());

		FragmentSet fragmentSet2 = testGetSiteFragmentSetsPage_addFragmentSet(
			siteExternalReferenceCode, randomFragmentSet());

		FragmentSet fragmentSet3 = testGetSiteFragmentSetsPage_addFragmentSet(
			siteExternalReferenceCode, randomFragmentSet());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<FragmentSet> page1 =
				fragmentSetResource.getSiteFragmentSetsPage(
					siteExternalReferenceCode, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(fragmentSet1, (List<FragmentSet>)page1.getItems());

			Page<FragmentSet> page2 =
				fragmentSetResource.getSiteFragmentSetsPage(
					siteExternalReferenceCode, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(fragmentSet2, (List<FragmentSet>)page2.getItems());

			Page<FragmentSet> page3 =
				fragmentSetResource.getSiteFragmentSetsPage(
					siteExternalReferenceCode, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(fragmentSet3, (List<FragmentSet>)page3.getItems());
		}
		else {
			Page<FragmentSet> page1 =
				fragmentSetResource.getSiteFragmentSetsPage(
					siteExternalReferenceCode, null,
					Pagination.of(1, totalCount + 2));

			List<FragmentSet> fragmentSets1 =
				(List<FragmentSet>)page1.getItems();

			Assert.assertEquals(
				fragmentSets1.toString(), totalCount + 2, fragmentSets1.size());

			Page<FragmentSet> page2 =
				fragmentSetResource.getSiteFragmentSetsPage(
					siteExternalReferenceCode, null,
					Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<FragmentSet> fragmentSets2 =
				(List<FragmentSet>)page2.getItems();

			Assert.assertEquals(
				fragmentSets2.toString(), 1, fragmentSets2.size());

			Page<FragmentSet> page3 =
				fragmentSetResource.getSiteFragmentSetsPage(
					siteExternalReferenceCode, null,
					Pagination.of(1, (int)totalCount + 3));

			assertContains(fragmentSet1, (List<FragmentSet>)page3.getItems());
			assertContains(fragmentSet2, (List<FragmentSet>)page3.getItems());
			assertContains(fragmentSet3, (List<FragmentSet>)page3.getItems());
		}
	}

	protected FragmentSet testGetSiteFragmentSetsPage_addFragmentSet(
			String siteExternalReferenceCode, FragmentSet fragmentSet)
		throws Exception {

		return fragmentSetResource.postSiteFragmentSet(
			siteExternalReferenceCode, fragmentSet);
	}

	protected String testGetSiteFragmentSetsPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteFragmentSetsPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Test
	public void testPostSiteFragmentSet() throws Exception {
		FragmentSet randomFragmentSet = randomFragmentSet();

		FragmentSet postFragmentSet = testPostSiteFragmentSet_addFragmentSet(
			randomFragmentSet);

		assertEquals(randomFragmentSet, postFragmentSet);
		assertValid(postFragmentSet);
	}

	protected FragmentSet testPostSiteFragmentSet_addFragmentSet(
			FragmentSet fragmentSet)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteFragmentSet() throws Exception {
		FragmentSet postFragmentSet = testPutSiteFragmentSet_addFragmentSet();

		FragmentSet randomFragmentSet = randomFragmentSet();

		FragmentSet putFragmentSet = fragmentSetResource.putSiteFragmentSet(
			testPutSiteFragmentSet_getSiteExternalReferenceCode(),
			postFragmentSet.getExternalReferenceCode(), randomFragmentSet);

		assertEquals(randomFragmentSet, putFragmentSet);
		assertValid(putFragmentSet);

		FragmentSet getFragmentSet = fragmentSetResource.getSiteFragmentSet(
			testPutSiteFragmentSet_getSiteExternalReferenceCode(),
			putFragmentSet.getExternalReferenceCode());

		assertEquals(randomFragmentSet, getFragmentSet);
		assertValid(getFragmentSet);
	}

	protected FragmentSet testPutSiteFragmentSet_addFragmentSet()
		throws Exception {

		return fragmentSetResource.postSiteFragmentSet(
			testGroup.getExternalReferenceCode(), randomFragmentSet());
	}

	protected String testPutSiteFragmentSet_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		FragmentSet fragmentSet1 =
			testBatchEngineDeleteImportTask_addSiteFragmentSet();

		testBatchEngineDeleteImportTask_deleteFragmentSet(
			200, fragmentSet1.getExternalReferenceCode(),
			"siteExternalReferenceCode", testGroup.getExternalReferenceCode());

		assertHttpResponseStatusCode(
			404,
			fragmentSetResource.getSiteFragmentSetHttpResponse(
				testBatchEngineDeleteImportTask_getSiteExternalReferenceCode(),
				fragmentSet1.getExternalReferenceCode()));
	}

	protected FragmentSet testBatchEngineDeleteImportTask_addSiteFragmentSet()
		throws Exception {

		return testDeleteSiteFragmentSet_addFragmentSet();
	}

	protected void testBatchEngineDeleteImportTask_deleteFragmentSet(
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
				"com.liferay.headless.admin.fragment.dto.v1_0.FragmentSet",
				null, null, null, null,
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

	protected void assertContains(
		FragmentSet fragmentSet, List<FragmentSet> fragmentSets) {

		boolean contains = false;

		for (FragmentSet item : fragmentSets) {
			if (equals(fragmentSet, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			fragmentSets + " does not contain " + fragmentSet, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		FragmentSet fragmentSet1, FragmentSet fragmentSet2) {

		Assert.assertTrue(
			fragmentSet1 + " does not equal " + fragmentSet2,
			equals(fragmentSet1, fragmentSet2));
	}

	protected void assertEquals(
		List<FragmentSet> fragmentSets1, List<FragmentSet> fragmentSets2) {

		Assert.assertEquals(fragmentSets1.size(), fragmentSets2.size());

		for (int i = 0; i < fragmentSets1.size(); i++) {
			FragmentSet fragmentSet1 = fragmentSets1.get(i);
			FragmentSet fragmentSet2 = fragmentSets2.get(i);

			assertEquals(fragmentSet1, fragmentSet2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<FragmentSet> fragmentSets1, List<FragmentSet> fragmentSets2) {

		Assert.assertEquals(fragmentSets1.size(), fragmentSets2.size());

		for (FragmentSet fragmentSet1 : fragmentSets1) {
			boolean contains = false;

			for (FragmentSet fragmentSet2 : fragmentSets2) {
				if (equals(fragmentSet1, fragmentSet2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				fragmentSets2 + " does not contain " + fragmentSet1, contains);
		}
	}

	protected void assertValid(FragmentSet fragmentSet) throws Exception {
		boolean valid = true;

		if (fragmentSet.getDateCreated() == null) {
			valid = false;
		}

		if (fragmentSet.getDateModified() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (fragmentSet.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (fragmentSet.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (fragmentSet.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (fragmentSet.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (fragmentSet.getKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("marketplace", additionalAssertFieldName)) {
				if (fragmentSet.getMarketplace() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (fragmentSet.getName() == null) {
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

	protected void assertValid(Page<FragmentSet> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<FragmentSet> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<FragmentSet> fragmentSets = page.getItems();

		int size = fragmentSets.size();

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
					com.liferay.headless.admin.fragment.dto.v1_0.FragmentSet.
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
		FragmentSet fragmentSet1, FragmentSet fragmentSet2) {

		if (fragmentSet1 == fragmentSet2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)fragmentSet1.getActions(),
						(Map)fragmentSet2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragmentSet1.getCreator(), fragmentSet2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragmentSet1.getDateCreated(),
						fragmentSet2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragmentSet1.getDateModified(),
						fragmentSet2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragmentSet1.getDescription(),
						fragmentSet2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						fragmentSet1.getExternalReferenceCode(),
						fragmentSet2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragmentSet1.getKey(), fragmentSet2.getKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("marketplace", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragmentSet1.getMarketplace(),
						fragmentSet2.getMarketplace())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragmentSet1.getName(), fragmentSet2.getName())) {

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

		if (!(_fragmentSetResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_fragmentSetResource;

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
		EntityField entityField, String operator, FragmentSet fragmentSet) {

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

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = fragmentSet.getDateCreated();

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

				sb.append(_format.format(fragmentSet.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = fragmentSet.getDateModified();

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

				sb.append(_format.format(fragmentSet.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = fragmentSet.getDescription();

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
			Object object = fragmentSet.getExternalReferenceCode();

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
			Object object = fragmentSet.getKey();

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
			Object object = fragmentSet.getName();

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

	protected FragmentSet randomFragmentSet() throws Exception {
		return new FragmentSet() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				marketplace = RandomTestUtil.randomBoolean();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected FragmentSet randomIrrelevantFragmentSet() throws Exception {
		FragmentSet randomIrrelevantFragmentSet = randomFragmentSet();

		return randomIrrelevantFragmentSet;
	}

	protected FragmentSet randomPatchFragmentSet() throws Exception {
		return randomFragmentSet();
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

	protected FragmentSetResource fragmentSetResource;
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
		LogFactoryUtil.getLog(BaseFragmentSetResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.admin.fragment.resource.v1_0.FragmentSetResource
			_fragmentSetResource;

}
// LIFERAY-REST-BUILDER-HASH:-1318768798