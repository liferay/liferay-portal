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
import com.liferay.headless.asset.library.client.dto.v1_0.ConnectedSite;
import com.liferay.headless.asset.library.client.http.HttpInvoker;
import com.liferay.headless.asset.library.client.pagination.Page;
import com.liferay.headless.asset.library.client.pagination.Pagination;
import com.liferay.headless.asset.library.client.resource.v1_0.ConnectedSiteResource;
import com.liferay.headless.asset.library.client.serdes.v1_0.ConnectedSiteSerDes;
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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.JAXRSWhiteboardTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
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
 * @author Roberto Díaz
 * @generated
 */
@Generated("")
public abstract class BaseConnectedSiteResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		JAXRSWhiteboardTestUtil.ensureReady();
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

		_connectedSiteResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		connectedSiteResource = ConnectedSiteResource.builder(
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
		DepotEntryLocalServiceUtil.deleteDepotEntry(irrelevantDepotEntry);
		DepotEntryLocalServiceUtil.deleteDepotEntry(testDepotEntry);

		GroupTestUtil.deleteGroup(irrelevantGroup);
		GroupTestUtil.deleteGroup(testGroup);
	}

	@Test
	public void testClientSerDesToDTO() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ConnectedSite connectedSite1 = randomConnectedSite();

		String json = objectMapper.writeValueAsString(connectedSite1);

		ConnectedSite connectedSite2 = ConnectedSiteSerDes.toDTO(json);

		Assert.assertTrue(equals(connectedSite1, connectedSite2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ConnectedSite connectedSite = randomConnectedSite();

		String json1 = objectMapper.writeValueAsString(connectedSite);
		String json2 = ConnectedSiteSerDes.toJSON(connectedSite);

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

		ConnectedSite connectedSite = randomConnectedSite();

		connectedSite.setDescriptiveName(regex);
		connectedSite.setExternalReferenceCode(regex);
		connectedSite.setLogo(regex);
		connectedSite.setName(regex);

		String json = ConnectedSiteSerDes.toJSON(connectedSite);

		Assert.assertFalse(json.contains(regex));

		connectedSite = ConnectedSiteSerDes.toDTO(json);

		Assert.assertEquals(regex, connectedSite.getDescriptiveName());
		Assert.assertEquals(regex, connectedSite.getExternalReferenceCode());
		Assert.assertEquals(regex, connectedSite.getLogo());
		Assert.assertEquals(regex, connectedSite.getName());
	}

	@Test
	public void testDeleteAssetLibraryConnectedSite() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ConnectedSite connectedSite =
			testDeleteAssetLibraryConnectedSite_addConnectedSite();

		assertHttpResponseStatusCode(
			204,
			connectedSiteResource.deleteAssetLibraryConnectedSiteHttpResponse(
				testDeleteAssetLibraryConnectedSite_getAssetLibraryExternalReferenceCode(),
				connectedSite.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			connectedSiteResource.getAssetLibraryConnectedSiteHttpResponse(
				testDeleteAssetLibraryConnectedSite_getAssetLibraryExternalReferenceCode(),
				connectedSite.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			connectedSiteResource.getAssetLibraryConnectedSiteHttpResponse(
				testDeleteAssetLibraryConnectedSite_getAssetLibraryExternalReferenceCode(),
				"-"));
	}

	protected ConnectedSite
			testDeleteAssetLibraryConnectedSite_addConnectedSite()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteAssetLibraryConnectedSite_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return testDepotEntryGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetAssetLibraryConnectedSite() throws Exception {
		ConnectedSite postConnectedSite =
			testGetAssetLibraryConnectedSite_addConnectedSite();

		ConnectedSite getConnectedSite =
			connectedSiteResource.getAssetLibraryConnectedSite(
				testGetAssetLibraryConnectedSite_getAssetLibraryExternalReferenceCode(),
				postConnectedSite.getExternalReferenceCode());

		assertEquals(postConnectedSite, getConnectedSite);
		assertValid(getConnectedSite);
	}

	protected ConnectedSite testGetAssetLibraryConnectedSite_addConnectedSite()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAssetLibraryConnectedSite_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return testDepotEntryGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetAssetLibraryConnectedSitesPage() throws Exception {
		String assetLibraryExternalReferenceCode =
			testGetAssetLibraryConnectedSitesPage_getAssetLibraryExternalReferenceCode();
		String irrelevantAssetLibraryExternalReferenceCode =
			testGetAssetLibraryConnectedSitesPage_getIrrelevantAssetLibraryExternalReferenceCode();

		Page<ConnectedSite> page =
			connectedSiteResource.getAssetLibraryConnectedSitesPage(
				assetLibraryExternalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryExternalReferenceCode != null) {
			ConnectedSite irrelevantConnectedSite =
				testGetAssetLibraryConnectedSitesPage_addConnectedSite(
					irrelevantAssetLibraryExternalReferenceCode,
					randomIrrelevantConnectedSite());

			page = connectedSiteResource.getAssetLibraryConnectedSitesPage(
				irrelevantAssetLibraryExternalReferenceCode,
				Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantConnectedSite, (List<ConnectedSite>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryConnectedSitesPage_getExpectedActions(
					irrelevantAssetLibraryExternalReferenceCode));
		}

		ConnectedSite connectedSite1 =
			testGetAssetLibraryConnectedSitesPage_addConnectedSite(
				assetLibraryExternalReferenceCode, randomConnectedSite());

		ConnectedSite connectedSite2 =
			testGetAssetLibraryConnectedSitesPage_addConnectedSite(
				assetLibraryExternalReferenceCode, randomConnectedSite());

		page = connectedSiteResource.getAssetLibraryConnectedSitesPage(
			assetLibraryExternalReferenceCode,
			Pagination.of(1, (int)totalCount + 2));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(connectedSite1, (List<ConnectedSite>)page.getItems());
		assertContains(connectedSite2, (List<ConnectedSite>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryConnectedSitesPage_getExpectedActions(
				assetLibraryExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryConnectedSitesPage_getExpectedActions(
				String assetLibraryExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryConnectedSitesPageWithPagination()
		throws Exception {

		String assetLibraryExternalReferenceCode =
			testGetAssetLibraryConnectedSitesPage_getAssetLibraryExternalReferenceCode();

		Page<ConnectedSite> connectedSitesPage =
			connectedSiteResource.getAssetLibraryConnectedSitesPage(
				assetLibraryExternalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			connectedSitesPage.getTotalCount());

		ConnectedSite connectedSite1 =
			testGetAssetLibraryConnectedSitesPage_addConnectedSite(
				assetLibraryExternalReferenceCode, randomConnectedSite());

		ConnectedSite connectedSite2 =
			testGetAssetLibraryConnectedSitesPage_addConnectedSite(
				assetLibraryExternalReferenceCode, randomConnectedSite());

		ConnectedSite connectedSite3 =
			testGetAssetLibraryConnectedSitesPage_addConnectedSite(
				assetLibraryExternalReferenceCode, randomConnectedSite());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ConnectedSite> page1 =
				connectedSiteResource.getAssetLibraryConnectedSitesPage(
					assetLibraryExternalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				connectedSite1, (List<ConnectedSite>)page1.getItems());

			Page<ConnectedSite> page2 =
				connectedSiteResource.getAssetLibraryConnectedSitesPage(
					assetLibraryExternalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				connectedSite2, (List<ConnectedSite>)page2.getItems());

			Page<ConnectedSite> page3 =
				connectedSiteResource.getAssetLibraryConnectedSitesPage(
					assetLibraryExternalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				connectedSite3, (List<ConnectedSite>)page3.getItems());
		}
		else {
			Page<ConnectedSite> page1 =
				connectedSiteResource.getAssetLibraryConnectedSitesPage(
					assetLibraryExternalReferenceCode,
					Pagination.of(1, totalCount + 2));

			List<ConnectedSite> connectedSites1 =
				(List<ConnectedSite>)page1.getItems();

			Assert.assertEquals(
				connectedSites1.toString(), totalCount + 2,
				connectedSites1.size());

			Page<ConnectedSite> page2 =
				connectedSiteResource.getAssetLibraryConnectedSitesPage(
					assetLibraryExternalReferenceCode,
					Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ConnectedSite> connectedSites2 =
				(List<ConnectedSite>)page2.getItems();

			Assert.assertEquals(
				connectedSites2.toString(), 1, connectedSites2.size());

			Page<ConnectedSite> page3 =
				connectedSiteResource.getAssetLibraryConnectedSitesPage(
					assetLibraryExternalReferenceCode,
					Pagination.of(1, (int)totalCount + 3));

			assertContains(
				connectedSite1, (List<ConnectedSite>)page3.getItems());
			assertContains(
				connectedSite2, (List<ConnectedSite>)page3.getItems());
			assertContains(
				connectedSite3, (List<ConnectedSite>)page3.getItems());
		}
	}

	protected ConnectedSite
			testGetAssetLibraryConnectedSitesPage_addConnectedSite(
				String assetLibraryExternalReferenceCode,
				ConnectedSite connectedSite)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAssetLibraryConnectedSitesPage_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return testDepotEntryGroup.getExternalReferenceCode();
	}

	protected String
			testGetAssetLibraryConnectedSitesPage_getIrrelevantAssetLibraryExternalReferenceCode()
		throws Exception {

		return irrelevantDepotEntryGroup.getExternalReferenceCode();
	}

	@Test
	public void testPutAssetLibraryConnectedSite() throws Exception {
		ConnectedSite postConnectedSite =
			testPutAssetLibraryConnectedSite_addConnectedSite();

		ConnectedSite randomConnectedSite = randomConnectedSite();

		ConnectedSite putConnectedSite =
			connectedSiteResource.putAssetLibraryConnectedSite(
				testPutAssetLibraryConnectedSite_getAssetLibraryExternalReferenceCode(),
				postConnectedSite.getExternalReferenceCode(),
				randomConnectedSite);

		assertEquals(randomConnectedSite, putConnectedSite);
		assertValid(putConnectedSite);

		ConnectedSite getConnectedSite =
			connectedSiteResource.getAssetLibraryConnectedSite(
				testPutAssetLibraryConnectedSite_getAssetLibraryExternalReferenceCode(),
				putConnectedSite.getExternalReferenceCode());

		assertEquals(randomConnectedSite, getConnectedSite);
		assertValid(getConnectedSite);
	}

	protected ConnectedSite testPutAssetLibraryConnectedSite_addConnectedSite()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPutAssetLibraryConnectedSite_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return testDepotEntryGroup.getExternalReferenceCode();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ConnectedSite connectedSite1 =
			testBatchEngineDeleteImportTask_addAssetLibraryConnectedSite();

		testBatchEngineDeleteImportTask_deleteConnectedSite(
			200, connectedSite1.getExternalReferenceCode(),
			"assetLibraryExternalReferenceCode",
			testDepotEntryGroup.getExternalReferenceCode());

		assertHttpResponseStatusCode(
			404,
			connectedSiteResource.getAssetLibraryConnectedSiteHttpResponse(
				testBatchEngineDeleteImportTask_getAssetLibraryExternalReferenceCode(),
				connectedSite1.getExternalReferenceCode()));
	}

	protected ConnectedSite
			testBatchEngineDeleteImportTask_addAssetLibraryConnectedSite()
		throws Exception {

		return testDeleteAssetLibraryConnectedSite_addConnectedSite();
	}

	protected void testBatchEngineDeleteImportTask_deleteConnectedSite(
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
				"com.liferay.headless.asset.library.dto.v1_0.ConnectedSite",
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
			testBatchEngineDeleteImportTask_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return testDepotEntryGroup.getExternalReferenceCode();
	}

	protected void assertContains(
		ConnectedSite connectedSite, List<ConnectedSite> connectedSites) {

		boolean contains = false;

		for (ConnectedSite item : connectedSites) {
			if (equals(connectedSite, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			connectedSites + " does not contain " + connectedSite, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ConnectedSite connectedSite1, ConnectedSite connectedSite2) {

		Assert.assertTrue(
			connectedSite1 + " does not equal " + connectedSite2,
			equals(connectedSite1, connectedSite2));
	}

	protected void assertEquals(
		List<ConnectedSite> connectedSites1,
		List<ConnectedSite> connectedSites2) {

		Assert.assertEquals(connectedSites1.size(), connectedSites2.size());

		for (int i = 0; i < connectedSites1.size(); i++) {
			ConnectedSite connectedSite1 = connectedSites1.get(i);
			ConnectedSite connectedSite2 = connectedSites2.get(i);

			assertEquals(connectedSite1, connectedSite2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ConnectedSite> connectedSites1,
		List<ConnectedSite> connectedSites2) {

		Assert.assertEquals(connectedSites1.size(), connectedSites2.size());

		for (ConnectedSite connectedSite1 : connectedSites1) {
			boolean contains = false;

			for (ConnectedSite connectedSite2 : connectedSites2) {
				if (equals(connectedSite1, connectedSite2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				connectedSites2 + " does not contain " + connectedSite1,
				contains);
		}
	}

	protected void assertValid(ConnectedSite connectedSite) throws Exception {
		boolean valid = true;

		if (connectedSite.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("descriptiveName", additionalAssertFieldName)) {
				if (connectedSite.getDescriptiveName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"descriptiveName_i18n", additionalAssertFieldName)) {

				if (connectedSite.getDescriptiveName_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (connectedSite.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("logo", additionalAssertFieldName)) {
				if (connectedSite.getLogo() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (connectedSite.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (connectedSite.getName_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("searchable", additionalAssertFieldName)) {
				if (connectedSite.getSearchable() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("stagingType", additionalAssertFieldName)) {
				if (connectedSite.getStagingType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (connectedSite.getType() == null) {
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

	protected void assertValid(Page<ConnectedSite> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ConnectedSite> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ConnectedSite> connectedSites = page.getItems();

		int size = connectedSites.size();

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
					com.liferay.headless.asset.library.dto.v1_0.ConnectedSite.
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
		ConnectedSite connectedSite1, ConnectedSite connectedSite2) {

		if (connectedSite1 == connectedSite2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("descriptiveName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						connectedSite1.getDescriptiveName(),
						connectedSite2.getDescriptiveName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"descriptiveName_i18n", additionalAssertFieldName)) {

				if (!equals(
						(Map)connectedSite1.getDescriptiveName_i18n(),
						(Map)connectedSite2.getDescriptiveName_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						connectedSite1.getExternalReferenceCode(),
						connectedSite2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						connectedSite1.getId(), connectedSite2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("logo", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						connectedSite1.getLogo(), connectedSite2.getLogo())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						connectedSite1.getName(), connectedSite2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)connectedSite1.getName_i18n(),
						(Map)connectedSite2.getName_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("searchable", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						connectedSite1.getSearchable(),
						connectedSite2.getSearchable())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("stagingType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						connectedSite1.getStagingType(),
						connectedSite2.getStagingType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						connectedSite1.getType(), connectedSite2.getType())) {

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

		if (!(_connectedSiteResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_connectedSiteResource;

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
		EntityField entityField, String operator, ConnectedSite connectedSite) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("descriptiveName")) {
			Object object = connectedSite.getDescriptiveName();

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

		if (entityFieldName.equals("descriptiveName_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = connectedSite.getExternalReferenceCode();

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

		if (entityFieldName.equals("logo")) {
			Object object = connectedSite.getLogo();

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
			Object object = connectedSite.getName();

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

		if (entityFieldName.equals("searchable")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("stagingType")) {
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

	protected ConnectedSite randomConnectedSite() throws Exception {
		return new ConnectedSite() {
			{
				descriptiveName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				logo = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				searchable = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected ConnectedSite randomIrrelevantConnectedSite() throws Exception {
		ConnectedSite randomIrrelevantConnectedSite = randomConnectedSite();

		return randomIrrelevantConnectedSite;
	}

	protected ConnectedSite randomPatchConnectedSite() throws Exception {
		return randomConnectedSite();
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

	protected ConnectedSiteResource connectedSiteResource;
	protected ImportTaskResource importTaskResource;
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
		LogFactoryUtil.getLog(BaseConnectedSiteResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.asset.library.resource.v1_0.ConnectedSiteResource
			_connectedSiteResource;

}
// LIFERAY-REST-BUILDER-HASH:-1699971052