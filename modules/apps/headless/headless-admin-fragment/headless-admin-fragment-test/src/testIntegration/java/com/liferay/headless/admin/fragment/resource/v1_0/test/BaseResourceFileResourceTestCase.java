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

import com.liferay.headless.admin.fragment.client.dto.v1_0.ResourceFile;
import com.liferay.headless.admin.fragment.client.http.HttpInvoker;
import com.liferay.headless.admin.fragment.client.pagination.Page;
import com.liferay.headless.admin.fragment.client.pagination.Pagination;
import com.liferay.headless.admin.fragment.client.resource.v1_0.ResourceFileResource;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.ResourceFileSerDes;
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
public abstract class BaseResourceFileResourceTestCase {

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

		_resourceFileResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		resourceFileResource = ResourceFileResource.builder(
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

		ResourceFile resourceFile1 = randomResourceFile();

		String json = objectMapper.writeValueAsString(resourceFile1);

		ResourceFile resourceFile2 = ResourceFileSerDes.toDTO(json);

		Assert.assertTrue(equals(resourceFile1, resourceFile2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ResourceFile resourceFile = randomResourceFile();

		String json1 = objectMapper.writeValueAsString(resourceFile);
		String json2 = ResourceFileSerDes.toJSON(resourceFile);

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

		ResourceFile resourceFile = randomResourceFile();

		resourceFile.setExternalReferenceCode(regex);
		resourceFile.setFragmentSetExternalReferenceCode(regex);
		resourceFile.setName(regex);
		resourceFile.setResourceFolderExternalReferenceCode(regex);

		String json = ResourceFileSerDes.toJSON(resourceFile);

		Assert.assertFalse(json.contains(regex));

		resourceFile = ResourceFileSerDes.toDTO(json);

		Assert.assertEquals(regex, resourceFile.getExternalReferenceCode());
		Assert.assertEquals(
			regex, resourceFile.getFragmentSetExternalReferenceCode());
		Assert.assertEquals(regex, resourceFile.getName());
		Assert.assertEquals(
			regex, resourceFile.getResourceFolderExternalReferenceCode());
	}

	@Test
	public void testDeleteSiteResourceFile() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ResourceFile resourceFile =
			testDeleteSiteResourceFile_addResourceFile();

		assertHttpResponseStatusCode(
			204,
			resourceFileResource.deleteSiteResourceFileHttpResponse(
				testDeleteSiteResourceFile_getSiteExternalReferenceCode(),
				resourceFile.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			resourceFileResource.getSiteResourceFileHttpResponse(
				testDeleteSiteResourceFile_getSiteExternalReferenceCode(),
				resourceFile.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			resourceFileResource.getSiteResourceFileHttpResponse(
				testDeleteSiteResourceFile_getSiteExternalReferenceCode(),
				"-"));
	}

	protected ResourceFile testDeleteSiteResourceFile_addResourceFile()
		throws Exception {

		return resourceFileResource.postSiteResourceFile(
			testGroup.getExternalReferenceCode(), randomResourceFile());
	}

	protected String testDeleteSiteResourceFile_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetSiteFragmentSetResourceFilesPage() throws Exception {
		String siteExternalReferenceCode =
			testGetSiteFragmentSetResourceFilesPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteFragmentSetResourceFilesPage_getIrrelevantSiteExternalReferenceCode();
		String fragmentSetExternalReferenceCode =
			testGetSiteFragmentSetResourceFilesPage_getFragmentSetExternalReferenceCode();
		String irrelevantFragmentSetExternalReferenceCode =
			testGetSiteFragmentSetResourceFilesPage_getIrrelevantFragmentSetExternalReferenceCode();

		Page<ResourceFile> page =
			resourceFileResource.getSiteFragmentSetResourceFilesPage(
				siteExternalReferenceCode, fragmentSetExternalReferenceCode,
				Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantSiteExternalReferenceCode != null) &&
			(irrelevantFragmentSetExternalReferenceCode != null)) {

			ResourceFile irrelevantResourceFile =
				testGetSiteFragmentSetResourceFilesPage_addResourceFile(
					irrelevantSiteExternalReferenceCode,
					irrelevantFragmentSetExternalReferenceCode,
					randomIrrelevantResourceFile());

			page = resourceFileResource.getSiteFragmentSetResourceFilesPage(
				irrelevantSiteExternalReferenceCode,
				irrelevantFragmentSetExternalReferenceCode,
				Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantResourceFile, (List<ResourceFile>)page.getItems());
			assertValid(
				page,
				testGetSiteFragmentSetResourceFilesPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode,
					irrelevantFragmentSetExternalReferenceCode));
		}

		ResourceFile resourceFile1 =
			testGetSiteFragmentSetResourceFilesPage_addResourceFile(
				siteExternalReferenceCode, fragmentSetExternalReferenceCode,
				randomResourceFile());

		ResourceFile resourceFile2 =
			testGetSiteFragmentSetResourceFilesPage_addResourceFile(
				siteExternalReferenceCode, fragmentSetExternalReferenceCode,
				randomResourceFile());

		page = resourceFileResource.getSiteFragmentSetResourceFilesPage(
			siteExternalReferenceCode, fragmentSetExternalReferenceCode,
			Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(resourceFile1, (List<ResourceFile>)page.getItems());
		assertContains(resourceFile2, (List<ResourceFile>)page.getItems());
		assertValid(
			page,
			testGetSiteFragmentSetResourceFilesPage_getExpectedActions(
				siteExternalReferenceCode, fragmentSetExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetSiteFragmentSetResourceFilesPage_getExpectedActions(
				String siteExternalReferenceCode,
				String fragmentSetExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteFragmentSetResourceFilesPageWithPagination()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteFragmentSetResourceFilesPage_getSiteExternalReferenceCode();
		String fragmentSetExternalReferenceCode =
			testGetSiteFragmentSetResourceFilesPage_getFragmentSetExternalReferenceCode();

		Page<ResourceFile> resourceFilesPage =
			resourceFileResource.getSiteFragmentSetResourceFilesPage(
				siteExternalReferenceCode, fragmentSetExternalReferenceCode,
				null);

		int totalCount = GetterUtil.getInteger(
			resourceFilesPage.getTotalCount());

		ResourceFile resourceFile1 =
			testGetSiteFragmentSetResourceFilesPage_addResourceFile(
				siteExternalReferenceCode, fragmentSetExternalReferenceCode,
				randomResourceFile());

		ResourceFile resourceFile2 =
			testGetSiteFragmentSetResourceFilesPage_addResourceFile(
				siteExternalReferenceCode, fragmentSetExternalReferenceCode,
				randomResourceFile());

		ResourceFile resourceFile3 =
			testGetSiteFragmentSetResourceFilesPage_addResourceFile(
				siteExternalReferenceCode, fragmentSetExternalReferenceCode,
				randomResourceFile());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ResourceFile> page1 =
				resourceFileResource.getSiteFragmentSetResourceFilesPage(
					siteExternalReferenceCode, fragmentSetExternalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(resourceFile1, (List<ResourceFile>)page1.getItems());

			Page<ResourceFile> page2 =
				resourceFileResource.getSiteFragmentSetResourceFilesPage(
					siteExternalReferenceCode, fragmentSetExternalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(resourceFile2, (List<ResourceFile>)page2.getItems());

			Page<ResourceFile> page3 =
				resourceFileResource.getSiteFragmentSetResourceFilesPage(
					siteExternalReferenceCode, fragmentSetExternalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(resourceFile3, (List<ResourceFile>)page3.getItems());
		}
		else {
			Page<ResourceFile> page1 =
				resourceFileResource.getSiteFragmentSetResourceFilesPage(
					siteExternalReferenceCode, fragmentSetExternalReferenceCode,
					Pagination.of(1, totalCount + 2));

			List<ResourceFile> resourceFiles1 =
				(List<ResourceFile>)page1.getItems();

			Assert.assertEquals(
				resourceFiles1.toString(), totalCount + 2,
				resourceFiles1.size());

			Page<ResourceFile> page2 =
				resourceFileResource.getSiteFragmentSetResourceFilesPage(
					siteExternalReferenceCode, fragmentSetExternalReferenceCode,
					Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ResourceFile> resourceFiles2 =
				(List<ResourceFile>)page2.getItems();

			Assert.assertEquals(
				resourceFiles2.toString(), 1, resourceFiles2.size());

			Page<ResourceFile> page3 =
				resourceFileResource.getSiteFragmentSetResourceFilesPage(
					siteExternalReferenceCode, fragmentSetExternalReferenceCode,
					Pagination.of(1, (int)totalCount + 3));

			assertContains(resourceFile1, (List<ResourceFile>)page3.getItems());
			assertContains(resourceFile2, (List<ResourceFile>)page3.getItems());
			assertContains(resourceFile3, (List<ResourceFile>)page3.getItems());
		}
	}

	protected ResourceFile
			testGetSiteFragmentSetResourceFilesPage_addResourceFile(
				String siteExternalReferenceCode,
				String fragmentSetExternalReferenceCode,
				ResourceFile resourceFile)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteFragmentSetResourceFilesPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteFragmentSetResourceFilesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteFragmentSetResourceFilesPage_getFragmentSetExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteFragmentSetResourceFilesPage_getIrrelevantFragmentSetExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetSiteResourceFile() throws Exception {
		ResourceFile postResourceFile =
			testGetSiteResourceFile_addResourceFile();

		ResourceFile getResourceFile = resourceFileResource.getSiteResourceFile(
			testGetSiteResourceFile_getSiteExternalReferenceCode(),
			postResourceFile.getExternalReferenceCode());

		assertEquals(postResourceFile, getResourceFile);
		assertValid(getResourceFile);
	}

	protected ResourceFile testGetSiteResourceFile_addResourceFile()
		throws Exception {

		return resourceFileResource.postSiteResourceFile(
			testGroup.getExternalReferenceCode(), randomResourceFile());
	}

	protected String testGetSiteResourceFile_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetSiteResourceFilesPage() throws Exception {
		String siteExternalReferenceCode =
			testGetSiteResourceFilesPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteResourceFilesPage_getIrrelevantSiteExternalReferenceCode();

		Page<ResourceFile> page = resourceFileResource.getSiteResourceFilesPage(
			siteExternalReferenceCode, null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantSiteExternalReferenceCode != null) {
			ResourceFile irrelevantResourceFile =
				testGetSiteResourceFilesPage_addResourceFile(
					irrelevantSiteExternalReferenceCode,
					randomIrrelevantResourceFile());

			page = resourceFileResource.getSiteResourceFilesPage(
				irrelevantSiteExternalReferenceCode, null,
				Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantResourceFile, (List<ResourceFile>)page.getItems());
			assertValid(
				page,
				testGetSiteResourceFilesPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode));
		}

		ResourceFile resourceFile1 =
			testGetSiteResourceFilesPage_addResourceFile(
				siteExternalReferenceCode, randomResourceFile());

		ResourceFile resourceFile2 =
			testGetSiteResourceFilesPage_addResourceFile(
				siteExternalReferenceCode, randomResourceFile());

		page = resourceFileResource.getSiteResourceFilesPage(
			siteExternalReferenceCode, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(resourceFile1, (List<ResourceFile>)page.getItems());
		assertContains(resourceFile2, (List<ResourceFile>)page.getItems());
		assertValid(
			page,
			testGetSiteResourceFilesPage_getExpectedActions(
				siteExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetSiteResourceFilesPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			("http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/headless-admin-fragment/v1.0/sites/{siteExternalReferenceCode}/resource-files/batch").
					replace(
						"{siteExternalReferenceCode}",
						String.valueOf(siteExternalReferenceCode)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteResourceFilesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteResourceFilesPage_getSiteExternalReferenceCode();

		ResourceFile resourceFile1 = randomResourceFile();

		resourceFile1 = testGetSiteResourceFilesPage_addResourceFile(
			siteExternalReferenceCode, resourceFile1);

		for (EntityField entityField : entityFields) {
			Page<ResourceFile> page =
				resourceFileResource.getSiteResourceFilesPage(
					siteExternalReferenceCode,
					getFilterString(entityField, "between", resourceFile1),
					Pagination.of(1, 2));

			assertEquals(
				Collections.singletonList(resourceFile1),
				(List<ResourceFile>)page.getItems());
		}
	}

	@Test
	public void testGetSiteResourceFilesPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteResourceFilesPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteResourceFilesPageWithFilterStringContains()
		throws Exception {

		testGetSiteResourceFilesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteResourceFilesPageWithFilterStringEquals()
		throws Exception {

		testGetSiteResourceFilesPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteResourceFilesPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteResourceFilesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteResourceFilesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteResourceFilesPage_getSiteExternalReferenceCode();

		ResourceFile resourceFile1 =
			testGetSiteResourceFilesPage_addResourceFile(
				siteExternalReferenceCode, randomResourceFile());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ResourceFile resourceFile2 =
			testGetSiteResourceFilesPage_addResourceFile(
				siteExternalReferenceCode, randomResourceFile());

		for (EntityField entityField : entityFields) {
			Page<ResourceFile> page =
				resourceFileResource.getSiteResourceFilesPage(
					siteExternalReferenceCode,
					getFilterString(entityField, operator, resourceFile1),
					Pagination.of(1, 2));

			assertEquals(
				Collections.singletonList(resourceFile1),
				(List<ResourceFile>)page.getItems());
		}
	}

	@Test
	public void testGetSiteResourceFilesPageWithPagination() throws Exception {
		String siteExternalReferenceCode =
			testGetSiteResourceFilesPage_getSiteExternalReferenceCode();

		Page<ResourceFile> resourceFilesPage =
			resourceFileResource.getSiteResourceFilesPage(
				siteExternalReferenceCode, null, null);

		int totalCount = GetterUtil.getInteger(
			resourceFilesPage.getTotalCount());

		ResourceFile resourceFile1 =
			testGetSiteResourceFilesPage_addResourceFile(
				siteExternalReferenceCode, randomResourceFile());

		ResourceFile resourceFile2 =
			testGetSiteResourceFilesPage_addResourceFile(
				siteExternalReferenceCode, randomResourceFile());

		ResourceFile resourceFile3 =
			testGetSiteResourceFilesPage_addResourceFile(
				siteExternalReferenceCode, randomResourceFile());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ResourceFile> page1 =
				resourceFileResource.getSiteResourceFilesPage(
					siteExternalReferenceCode, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(resourceFile1, (List<ResourceFile>)page1.getItems());

			Page<ResourceFile> page2 =
				resourceFileResource.getSiteResourceFilesPage(
					siteExternalReferenceCode, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(resourceFile2, (List<ResourceFile>)page2.getItems());

			Page<ResourceFile> page3 =
				resourceFileResource.getSiteResourceFilesPage(
					siteExternalReferenceCode, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(resourceFile3, (List<ResourceFile>)page3.getItems());
		}
		else {
			Page<ResourceFile> page1 =
				resourceFileResource.getSiteResourceFilesPage(
					siteExternalReferenceCode, null,
					Pagination.of(1, totalCount + 2));

			List<ResourceFile> resourceFiles1 =
				(List<ResourceFile>)page1.getItems();

			Assert.assertEquals(
				resourceFiles1.toString(), totalCount + 2,
				resourceFiles1.size());

			Page<ResourceFile> page2 =
				resourceFileResource.getSiteResourceFilesPage(
					siteExternalReferenceCode, null,
					Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ResourceFile> resourceFiles2 =
				(List<ResourceFile>)page2.getItems();

			Assert.assertEquals(
				resourceFiles2.toString(), 1, resourceFiles2.size());

			Page<ResourceFile> page3 =
				resourceFileResource.getSiteResourceFilesPage(
					siteExternalReferenceCode, null,
					Pagination.of(1, (int)totalCount + 3));

			assertContains(resourceFile1, (List<ResourceFile>)page3.getItems());
			assertContains(resourceFile2, (List<ResourceFile>)page3.getItems());
			assertContains(resourceFile3, (List<ResourceFile>)page3.getItems());
		}
	}

	protected ResourceFile testGetSiteResourceFilesPage_addResourceFile(
			String siteExternalReferenceCode, ResourceFile resourceFile)
		throws Exception {

		return resourceFileResource.postSiteResourceFile(
			siteExternalReferenceCode, resourceFile);
	}

	protected String testGetSiteResourceFilesPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteResourceFilesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetSiteResourceFolderResourceFilesPage() throws Exception {
		String siteExternalReferenceCode =
			testGetSiteResourceFolderResourceFilesPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteResourceFolderResourceFilesPage_getIrrelevantSiteExternalReferenceCode();
		String resourceFolderExternalReferenceCode =
			testGetSiteResourceFolderResourceFilesPage_getResourceFolderExternalReferenceCode();
		String irrelevantResourceFolderExternalReferenceCode =
			testGetSiteResourceFolderResourceFilesPage_getIrrelevantResourceFolderExternalReferenceCode();

		Page<ResourceFile> page =
			resourceFileResource.getSiteResourceFolderResourceFilesPage(
				siteExternalReferenceCode, resourceFolderExternalReferenceCode,
				Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantSiteExternalReferenceCode != null) &&
			(irrelevantResourceFolderExternalReferenceCode != null)) {

			ResourceFile irrelevantResourceFile =
				testGetSiteResourceFolderResourceFilesPage_addResourceFile(
					irrelevantSiteExternalReferenceCode,
					irrelevantResourceFolderExternalReferenceCode,
					randomIrrelevantResourceFile());

			page = resourceFileResource.getSiteResourceFolderResourceFilesPage(
				irrelevantSiteExternalReferenceCode,
				irrelevantResourceFolderExternalReferenceCode,
				Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantResourceFile, (List<ResourceFile>)page.getItems());
			assertValid(
				page,
				testGetSiteResourceFolderResourceFilesPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode,
					irrelevantResourceFolderExternalReferenceCode));
		}

		ResourceFile resourceFile1 =
			testGetSiteResourceFolderResourceFilesPage_addResourceFile(
				siteExternalReferenceCode, resourceFolderExternalReferenceCode,
				randomResourceFile());

		ResourceFile resourceFile2 =
			testGetSiteResourceFolderResourceFilesPage_addResourceFile(
				siteExternalReferenceCode, resourceFolderExternalReferenceCode,
				randomResourceFile());

		page = resourceFileResource.getSiteResourceFolderResourceFilesPage(
			siteExternalReferenceCode, resourceFolderExternalReferenceCode,
			Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(resourceFile1, (List<ResourceFile>)page.getItems());
		assertContains(resourceFile2, (List<ResourceFile>)page.getItems());
		assertValid(
			page,
			testGetSiteResourceFolderResourceFilesPage_getExpectedActions(
				siteExternalReferenceCode,
				resourceFolderExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetSiteResourceFolderResourceFilesPage_getExpectedActions(
				String siteExternalReferenceCode,
				String resourceFolderExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteResourceFolderResourceFilesPageWithPagination()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteResourceFolderResourceFilesPage_getSiteExternalReferenceCode();
		String resourceFolderExternalReferenceCode =
			testGetSiteResourceFolderResourceFilesPage_getResourceFolderExternalReferenceCode();

		Page<ResourceFile> resourceFilesPage =
			resourceFileResource.getSiteResourceFolderResourceFilesPage(
				siteExternalReferenceCode, resourceFolderExternalReferenceCode,
				null);

		int totalCount = GetterUtil.getInteger(
			resourceFilesPage.getTotalCount());

		ResourceFile resourceFile1 =
			testGetSiteResourceFolderResourceFilesPage_addResourceFile(
				siteExternalReferenceCode, resourceFolderExternalReferenceCode,
				randomResourceFile());

		ResourceFile resourceFile2 =
			testGetSiteResourceFolderResourceFilesPage_addResourceFile(
				siteExternalReferenceCode, resourceFolderExternalReferenceCode,
				randomResourceFile());

		ResourceFile resourceFile3 =
			testGetSiteResourceFolderResourceFilesPage_addResourceFile(
				siteExternalReferenceCode, resourceFolderExternalReferenceCode,
				randomResourceFile());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ResourceFile> page1 =
				resourceFileResource.getSiteResourceFolderResourceFilesPage(
					siteExternalReferenceCode,
					resourceFolderExternalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(resourceFile1, (List<ResourceFile>)page1.getItems());

			Page<ResourceFile> page2 =
				resourceFileResource.getSiteResourceFolderResourceFilesPage(
					siteExternalReferenceCode,
					resourceFolderExternalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(resourceFile2, (List<ResourceFile>)page2.getItems());

			Page<ResourceFile> page3 =
				resourceFileResource.getSiteResourceFolderResourceFilesPage(
					siteExternalReferenceCode,
					resourceFolderExternalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(resourceFile3, (List<ResourceFile>)page3.getItems());
		}
		else {
			Page<ResourceFile> page1 =
				resourceFileResource.getSiteResourceFolderResourceFilesPage(
					siteExternalReferenceCode,
					resourceFolderExternalReferenceCode,
					Pagination.of(1, totalCount + 2));

			List<ResourceFile> resourceFiles1 =
				(List<ResourceFile>)page1.getItems();

			Assert.assertEquals(
				resourceFiles1.toString(), totalCount + 2,
				resourceFiles1.size());

			Page<ResourceFile> page2 =
				resourceFileResource.getSiteResourceFolderResourceFilesPage(
					siteExternalReferenceCode,
					resourceFolderExternalReferenceCode,
					Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ResourceFile> resourceFiles2 =
				(List<ResourceFile>)page2.getItems();

			Assert.assertEquals(
				resourceFiles2.toString(), 1, resourceFiles2.size());

			Page<ResourceFile> page3 =
				resourceFileResource.getSiteResourceFolderResourceFilesPage(
					siteExternalReferenceCode,
					resourceFolderExternalReferenceCode,
					Pagination.of(1, (int)totalCount + 3));

			assertContains(resourceFile1, (List<ResourceFile>)page3.getItems());
			assertContains(resourceFile2, (List<ResourceFile>)page3.getItems());
			assertContains(resourceFile3, (List<ResourceFile>)page3.getItems());
		}
	}

	protected ResourceFile
			testGetSiteResourceFolderResourceFilesPage_addResourceFile(
				String siteExternalReferenceCode,
				String resourceFolderExternalReferenceCode,
				ResourceFile resourceFile)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteResourceFolderResourceFilesPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteResourceFolderResourceFilesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteResourceFolderResourceFilesPage_getResourceFolderExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteResourceFolderResourceFilesPage_getIrrelevantResourceFolderExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testPostSiteFragmentSetResourceFile() throws Exception {
		ResourceFile randomResourceFile = randomResourceFile();

		ResourceFile postResourceFile =
			testPostSiteFragmentSetResourceFile_addResourceFile(
				randomResourceFile);

		assertEquals(randomResourceFile, postResourceFile);
		assertValid(postResourceFile);
	}

	protected ResourceFile testPostSiteFragmentSetResourceFile_addResourceFile(
			ResourceFile resourceFile)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSiteResourceFile() throws Exception {
		ResourceFile randomResourceFile = randomResourceFile();

		ResourceFile postResourceFile =
			testPostSiteResourceFile_addResourceFile(randomResourceFile);

		assertEquals(randomResourceFile, postResourceFile);
		assertValid(postResourceFile);
	}

	protected ResourceFile testPostSiteResourceFile_addResourceFile(
			ResourceFile resourceFile)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteResourceFile() throws Exception {
		ResourceFile postResourceFile =
			testPutSiteResourceFile_addResourceFile();

		ResourceFile randomResourceFile = randomResourceFile();

		ResourceFile putResourceFile = resourceFileResource.putSiteResourceFile(
			testPutSiteResourceFile_getSiteExternalReferenceCode(),
			postResourceFile.getExternalReferenceCode(), randomResourceFile);

		assertEquals(randomResourceFile, putResourceFile);
		assertValid(putResourceFile);

		ResourceFile getResourceFile = resourceFileResource.getSiteResourceFile(
			testPutSiteResourceFile_getSiteExternalReferenceCode(),
			putResourceFile.getExternalReferenceCode());

		assertEquals(randomResourceFile, getResourceFile);
		assertValid(getResourceFile);
	}

	protected ResourceFile testPutSiteResourceFile_addResourceFile()
		throws Exception {

		return resourceFileResource.postSiteResourceFile(
			testGroup.getExternalReferenceCode(), randomResourceFile());
	}

	protected String testPutSiteResourceFile_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ResourceFile resourceFile1 =
			testBatchEngineDeleteImportTask_addSiteResourceFile();

		testBatchEngineDeleteImportTask_deleteResourceFile(
			200, resourceFile1.getExternalReferenceCode(),
			"siteExternalReferenceCode", testGroup.getExternalReferenceCode());

		assertHttpResponseStatusCode(
			404,
			resourceFileResource.getSiteResourceFileHttpResponse(
				testBatchEngineDeleteImportTask_getSiteExternalReferenceCode(),
				resourceFile1.getExternalReferenceCode()));
	}

	protected ResourceFile testBatchEngineDeleteImportTask_addSiteResourceFile()
		throws Exception {

		return testDeleteSiteResourceFile_addResourceFile();
	}

	protected void testBatchEngineDeleteImportTask_deleteResourceFile(
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
				"com.liferay.headless.admin.fragment.dto.v1_0.ResourceFile",
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
		ResourceFile resourceFile, List<ResourceFile> resourceFiles) {

		boolean contains = false;

		for (ResourceFile item : resourceFiles) {
			if (equals(resourceFile, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			resourceFiles + " does not contain " + resourceFile, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ResourceFile resourceFile1, ResourceFile resourceFile2) {

		Assert.assertTrue(
			resourceFile1 + " does not equal " + resourceFile2,
			equals(resourceFile1, resourceFile2));
	}

	protected void assertEquals(
		List<ResourceFile> resourceFiles1, List<ResourceFile> resourceFiles2) {

		Assert.assertEquals(resourceFiles1.size(), resourceFiles2.size());

		for (int i = 0; i < resourceFiles1.size(); i++) {
			ResourceFile resourceFile1 = resourceFiles1.get(i);
			ResourceFile resourceFile2 = resourceFiles2.get(i);

			assertEquals(resourceFile1, resourceFile2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ResourceFile> resourceFiles1, List<ResourceFile> resourceFiles2) {

		Assert.assertEquals(resourceFiles1.size(), resourceFiles2.size());

		for (ResourceFile resourceFile1 : resourceFiles1) {
			boolean contains = false;

			for (ResourceFile resourceFile2 : resourceFiles2) {
				if (equals(resourceFile1, resourceFile2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				resourceFiles2 + " does not contain " + resourceFile1,
				contains);
		}
	}

	protected void assertValid(ResourceFile resourceFile) throws Exception {
		boolean valid = true;

		if (resourceFile.getDateCreated() == null) {
			valid = false;
		}

		if (resourceFile.getDateModified() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (resourceFile.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (resourceFile.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("fileURLReference", additionalAssertFieldName)) {
				if (resourceFile.getFileURLReference() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("fragmentSet", additionalAssertFieldName)) {
				if (resourceFile.getFragmentSet() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"fragmentSetExternalReferenceCode",
					additionalAssertFieldName)) {

				if (resourceFile.getFragmentSetExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (resourceFile.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("resourceFolder", additionalAssertFieldName)) {
				if (resourceFile.getResourceFolder() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"resourceFolderExternalReferenceCode",
					additionalAssertFieldName)) {

				if (resourceFile.getResourceFolderExternalReferenceCode() ==
						null) {

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

	protected void assertValid(Page<ResourceFile> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ResourceFile> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ResourceFile> resourceFiles = page.getItems();

		int size = resourceFiles.size();

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
					com.liferay.headless.admin.fragment.dto.v1_0.ResourceFile.
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
		ResourceFile resourceFile1, ResourceFile resourceFile2) {

		if (resourceFile1 == resourceFile2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						resourceFile1.getCreator(),
						resourceFile2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						resourceFile1.getDateCreated(),
						resourceFile2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						resourceFile1.getDateModified(),
						resourceFile2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						resourceFile1.getExternalReferenceCode(),
						resourceFile2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fileURLReference", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						resourceFile1.getFileURLReference(),
						resourceFile2.getFileURLReference())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fragmentSet", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						resourceFile1.getFragmentSet(),
						resourceFile2.getFragmentSet())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"fragmentSetExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						resourceFile1.getFragmentSetExternalReferenceCode(),
						resourceFile2.getFragmentSetExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						resourceFile1.getName(), resourceFile2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("resourceFolder", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						resourceFile1.getResourceFolder(),
						resourceFile2.getResourceFolder())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"resourceFolderExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						resourceFile1.getResourceFolderExternalReferenceCode(),
						resourceFile2.
							getResourceFolderExternalReferenceCode())) {

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

		if (!(_resourceFileResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_resourceFileResource;

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
		EntityField entityField, String operator, ResourceFile resourceFile) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = resourceFile.getDateCreated();

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

				sb.append(_format.format(resourceFile.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = resourceFile.getDateModified();

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

				sb.append(_format.format(resourceFile.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = resourceFile.getExternalReferenceCode();

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

		if (entityFieldName.equals("fileURLReference")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("fragmentSet")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("fragmentSetExternalReferenceCode")) {
			Object object = resourceFile.getFragmentSetExternalReferenceCode();

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

		if (entityFieldName.equals("name")) {
			Object object = resourceFile.getName();

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

		if (entityFieldName.equals("resourceFolder")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("resourceFolderExternalReferenceCode")) {
			Object object =
				resourceFile.getResourceFolderExternalReferenceCode();

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

	protected ResourceFile randomResourceFile() throws Exception {
		return new ResourceFile() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				fragmentSetExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				resourceFolderExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected ResourceFile randomIrrelevantResourceFile() throws Exception {
		ResourceFile randomIrrelevantResourceFile = randomResourceFile();

		return randomIrrelevantResourceFile;
	}

	protected ResourceFile randomPatchResourceFile() throws Exception {
		return randomResourceFile();
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

	protected ResourceFileResource resourceFileResource;
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
		LogFactoryUtil.getLog(BaseResourceFileResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.admin.fragment.resource.v1_0.ResourceFileResource
			_resourceFileResource;

}
// LIFERAY-REST-BUILDER-HASH:-611288577