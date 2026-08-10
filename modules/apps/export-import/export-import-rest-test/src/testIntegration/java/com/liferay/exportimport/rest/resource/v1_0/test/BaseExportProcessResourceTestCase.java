/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

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
import com.liferay.exportimport.rest.client.dto.v1_0.ExportProcess;
import com.liferay.exportimport.rest.client.dto.v1_0.ProcessProgress;
import com.liferay.exportimport.rest.client.dto.v1_0.Type;
import com.liferay.exportimport.rest.client.http.HttpInvoker;
import com.liferay.exportimport.rest.client.pagination.Page;
import com.liferay.exportimport.rest.client.pagination.Pagination;
import com.liferay.exportimport.rest.client.resource.v1_0.ExportProcessResource;
import com.liferay.exportimport.rest.client.serdes.v1_0.ExportProcessSerDes;
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
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
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
import com.liferay.portal.kernel.util.Time;
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
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public abstract class BaseExportProcessResourceTestCase {

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

		_exportProcessResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		exportProcessResource = ExportProcessResource.builder(
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

		ExportProcess exportProcess1 = randomExportProcess();

		String json = objectMapper.writeValueAsString(exportProcess1);

		ExportProcess exportProcess2 = ExportProcessSerDes.toDTO(json);

		Assert.assertTrue(equals(exportProcess1, exportProcess2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ExportProcess exportProcess = randomExportProcess();

		String json1 = objectMapper.writeValueAsString(exportProcess);
		String json2 = ExportProcessSerDes.toJSON(exportProcess);

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

		ExportProcess exportProcess = randomExportProcess();

		exportProcess.setErrorMessage(regex);
		exportProcess.setName(regex);

		String json = ExportProcessSerDes.toJSON(exportProcess);

		Assert.assertFalse(json.contains(regex));

		exportProcess = ExportProcessSerDes.toDTO(json);

		Assert.assertEquals(regex, exportProcess.getErrorMessage());
		Assert.assertEquals(regex, exportProcess.getName());
	}

	@Test
	public void testDeleteExportProcess() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ExportProcess exportProcess =
			testDeleteExportProcess_addExportProcess();

		assertHttpResponseStatusCode(
			204,
			exportProcessResource.deleteExportProcessHttpResponse(
				exportProcess.getId()));

		assertHttpResponseStatusCode(
			404,
			exportProcessResource.getExportProcessHttpResponse(
				exportProcess.getId()));
		assertHttpResponseStatusCode(
			404, exportProcessResource.getExportProcessHttpResponse(0L));
	}

	protected ExportProcess testDeleteExportProcess_addExportProcess()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testDeleteExportProcessBatch() throws Exception {
		ExportProcess exportProcess1 =
			testDeleteExportProcessBatch_addExportProcess();

		testDeleteExportProcessBatch_deleteExportProcess(
			202, null, exportProcess1.getId());

		assertHttpResponseStatusCode(
			404,
			exportProcessResource.getExportProcessHttpResponse(
				exportProcess1.getId()));
	}

	protected ExportProcess testDeleteExportProcessBatch_addExportProcess()
		throws Exception {

		return testDeleteExportProcess_addExportProcess();
	}

	protected void testDeleteExportProcessBatch_deleteExportProcess(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			exportProcessResource.deleteExportProcessBatchHttpResponse(
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
	public void testGetAssetLibraryExportProcessesPage() throws Exception {
		String assetLibraryExternalReferenceCode =
			testGetAssetLibraryExportProcessesPage_getAssetLibraryExternalReferenceCode();
		String irrelevantAssetLibraryExternalReferenceCode =
			testGetAssetLibraryExportProcessesPage_getIrrelevantAssetLibraryExternalReferenceCode();

		Page<ExportProcess> page =
			exportProcessResource.getAssetLibraryExportProcessesPage(
				assetLibraryExternalReferenceCode, null, null, null,
				Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryExternalReferenceCode != null) {
			ExportProcess irrelevantExportProcess =
				testGetAssetLibraryExportProcessesPage_addExportProcess(
					irrelevantAssetLibraryExternalReferenceCode,
					randomIrrelevantExportProcess());

			page = exportProcessResource.getAssetLibraryExportProcessesPage(
				irrelevantAssetLibraryExternalReferenceCode, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantExportProcess, (List<ExportProcess>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryExportProcessesPage_getExpectedActions(
					irrelevantAssetLibraryExternalReferenceCode));
		}

		ExportProcess exportProcess1 =
			testGetAssetLibraryExportProcessesPage_addExportProcess(
				assetLibraryExternalReferenceCode, randomExportProcess());

		ExportProcess exportProcess2 =
			testGetAssetLibraryExportProcessesPage_addExportProcess(
				assetLibraryExternalReferenceCode, randomExportProcess());

		page = exportProcessResource.getAssetLibraryExportProcessesPage(
			assetLibraryExternalReferenceCode, null, null, null,
			Pagination.of(1, (int)totalCount + 2), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(exportProcess1, (List<ExportProcess>)page.getItems());
		assertContains(exportProcess2, (List<ExportProcess>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryExportProcessesPage_getExpectedActions(
				assetLibraryExternalReferenceCode));

		exportProcessResource.deleteExportProcess(exportProcess1.getId());

		exportProcessResource.deleteExportProcess(exportProcess2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryExportProcessesPage_getExpectedActions(
				String assetLibraryExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			("http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/export-import/v1.0/asset-libraries/{assetLibraryExternalReferenceCode}/export-processes/batch").
					replace(
						"{assetLibraryExternalReferenceCode}",
						String.valueOf(assetLibraryExternalReferenceCode)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryExportProcessesPageWithPagination()
		throws Exception {

		String assetLibraryExternalReferenceCode =
			testGetAssetLibraryExportProcessesPage_getAssetLibraryExternalReferenceCode();

		Page<ExportProcess> exportProcessesPage =
			exportProcessResource.getAssetLibraryExportProcessesPage(
				assetLibraryExternalReferenceCode, null, null, null, null,
				null);

		int totalCount = GetterUtil.getInteger(
			exportProcessesPage.getTotalCount());

		ExportProcess exportProcess1 =
			testGetAssetLibraryExportProcessesPage_addExportProcess(
				assetLibraryExternalReferenceCode, randomExportProcess());

		ExportProcess exportProcess2 =
			testGetAssetLibraryExportProcessesPage_addExportProcess(
				assetLibraryExternalReferenceCode, randomExportProcess());

		ExportProcess exportProcess3 =
			testGetAssetLibraryExportProcessesPage_addExportProcess(
				assetLibraryExternalReferenceCode, randomExportProcess());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ExportProcess> page1 =
				exportProcessResource.getAssetLibraryExportProcessesPage(
					assetLibraryExternalReferenceCode, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				exportProcess1, (List<ExportProcess>)page1.getItems());

			Page<ExportProcess> page2 =
				exportProcessResource.getAssetLibraryExportProcessesPage(
					assetLibraryExternalReferenceCode, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				exportProcess2, (List<ExportProcess>)page2.getItems());

			Page<ExportProcess> page3 =
				exportProcessResource.getAssetLibraryExportProcessesPage(
					assetLibraryExternalReferenceCode, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				exportProcess3, (List<ExportProcess>)page3.getItems());
		}
		else {
			Page<ExportProcess> page1 =
				exportProcessResource.getAssetLibraryExportProcessesPage(
					assetLibraryExternalReferenceCode, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<ExportProcess> exportProcesses1 =
				(List<ExportProcess>)page1.getItems();

			Assert.assertEquals(
				exportProcesses1.toString(), totalCount + 2,
				exportProcesses1.size());

			Page<ExportProcess> page2 =
				exportProcessResource.getAssetLibraryExportProcessesPage(
					assetLibraryExternalReferenceCode, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ExportProcess> exportProcesses2 =
				(List<ExportProcess>)page2.getItems();

			Assert.assertEquals(
				exportProcesses2.toString(), 1, exportProcesses2.size());

			Page<ExportProcess> page3 =
				exportProcessResource.getAssetLibraryExportProcessesPage(
					assetLibraryExternalReferenceCode, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				exportProcess1, (List<ExportProcess>)page3.getItems());
			assertContains(
				exportProcess2, (List<ExportProcess>)page3.getItems());
			assertContains(
				exportProcess3, (List<ExportProcess>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryExportProcessesPageWithSortDateTime()
		throws Exception {

		testGetAssetLibraryExportProcessesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, exportProcess1, exportProcess2) -> {
				BeanTestUtil.setProperty(
					exportProcess1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetLibraryExportProcessesPageWithSortDouble()
		throws Exception {

		testGetAssetLibraryExportProcessesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, exportProcess1, exportProcess2) -> {
				BeanTestUtil.setProperty(
					exportProcess1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					exportProcess2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetLibraryExportProcessesPageWithSortInteger()
		throws Exception {

		testGetAssetLibraryExportProcessesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, exportProcess1, exportProcess2) -> {
				BeanTestUtil.setProperty(
					exportProcess1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					exportProcess2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetLibraryExportProcessesPageWithSortString()
		throws Exception {

		testGetAssetLibraryExportProcessesPageWithSort(
			EntityField.Type.STRING,
			(entityField, exportProcess1, exportProcess2) -> {
				Class<?> clazz = exportProcess1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						exportProcess1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						exportProcess2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						exportProcess1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						exportProcess2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						exportProcess1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						exportProcess2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAssetLibraryExportProcessesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ExportProcess, ExportProcess, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String assetLibraryExternalReferenceCode =
			testGetAssetLibraryExportProcessesPage_getAssetLibraryExternalReferenceCode();

		ExportProcess exportProcess1 = randomExportProcess();
		ExportProcess exportProcess2 = randomExportProcess();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, exportProcess1, exportProcess2);
		}

		exportProcess1 =
			testGetAssetLibraryExportProcessesPage_addExportProcess(
				assetLibraryExternalReferenceCode, exportProcess1);

		exportProcess2 =
			testGetAssetLibraryExportProcessesPage_addExportProcess(
				assetLibraryExternalReferenceCode, exportProcess2);

		Page<ExportProcess> page =
			exportProcessResource.getAssetLibraryExportProcessesPage(
				assetLibraryExternalReferenceCode, null, null, null, null,
				null);

		for (EntityField entityField : entityFields) {
			Page<ExportProcess> ascPage =
				exportProcessResource.getAssetLibraryExportProcessesPage(
					assetLibraryExternalReferenceCode, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				exportProcess1, (List<ExportProcess>)ascPage.getItems());
			assertContains(
				exportProcess2, (List<ExportProcess>)ascPage.getItems());

			Page<ExportProcess> descPage =
				exportProcessResource.getAssetLibraryExportProcessesPage(
					assetLibraryExternalReferenceCode, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				exportProcess2, (List<ExportProcess>)descPage.getItems());
			assertContains(
				exportProcess1, (List<ExportProcess>)descPage.getItems());
		}
	}

	protected ExportProcess
			testGetAssetLibraryExportProcessesPage_addExportProcess(
				String assetLibraryExternalReferenceCode,
				ExportProcess exportProcess)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAssetLibraryExportProcessesPage_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return testDepotEntryGroup.getExternalReferenceCode();
	}

	protected String
			testGetAssetLibraryExportProcessesPage_getIrrelevantAssetLibraryExternalReferenceCode()
		throws Exception {

		return irrelevantDepotEntryGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetAssetLibraryPortletExportProcessesPage()
		throws Exception {

		String assetLibraryExternalReferenceCode =
			testGetAssetLibraryPortletExportProcessesPage_getAssetLibraryExternalReferenceCode();
		String irrelevantAssetLibraryExternalReferenceCode =
			testGetAssetLibraryPortletExportProcessesPage_getIrrelevantAssetLibraryExternalReferenceCode();
		String portletId =
			testGetAssetLibraryPortletExportProcessesPage_getPortletId();
		String irrelevantPortletId =
			testGetAssetLibraryPortletExportProcessesPage_getIrrelevantPortletId();

		Page<ExportProcess> page =
			exportProcessResource.getAssetLibraryPortletExportProcessesPage(
				assetLibraryExternalReferenceCode, portletId, null, null, null,
				Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if ((irrelevantAssetLibraryExternalReferenceCode != null) &&
			(irrelevantPortletId != null)) {

			ExportProcess irrelevantExportProcess =
				testGetAssetLibraryPortletExportProcessesPage_addExportProcess(
					irrelevantAssetLibraryExternalReferenceCode,
					irrelevantPortletId, randomIrrelevantExportProcess());

			page =
				exportProcessResource.getAssetLibraryPortletExportProcessesPage(
					irrelevantAssetLibraryExternalReferenceCode,
					irrelevantPortletId, null, null, null,
					Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantExportProcess, (List<ExportProcess>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryPortletExportProcessesPage_getExpectedActions(
					irrelevantAssetLibraryExternalReferenceCode,
					irrelevantPortletId));
		}

		ExportProcess exportProcess1 =
			testGetAssetLibraryPortletExportProcessesPage_addExportProcess(
				assetLibraryExternalReferenceCode, portletId,
				randomExportProcess());

		ExportProcess exportProcess2 =
			testGetAssetLibraryPortletExportProcessesPage_addExportProcess(
				assetLibraryExternalReferenceCode, portletId,
				randomExportProcess());

		page = exportProcessResource.getAssetLibraryPortletExportProcessesPage(
			assetLibraryExternalReferenceCode, portletId, null, null, null,
			Pagination.of(1, (int)totalCount + 2), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(exportProcess1, (List<ExportProcess>)page.getItems());
		assertContains(exportProcess2, (List<ExportProcess>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryPortletExportProcessesPage_getExpectedActions(
				assetLibraryExternalReferenceCode, portletId));

		exportProcessResource.deleteExportProcess(exportProcess1.getId());

		exportProcessResource.deleteExportProcess(exportProcess2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryPortletExportProcessesPage_getExpectedActions(
				String assetLibraryExternalReferenceCode, String portletId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryPortletExportProcessesPageWithPagination()
		throws Exception {

		String assetLibraryExternalReferenceCode =
			testGetAssetLibraryPortletExportProcessesPage_getAssetLibraryExternalReferenceCode();
		String portletId =
			testGetAssetLibraryPortletExportProcessesPage_getPortletId();

		Page<ExportProcess> exportProcessesPage =
			exportProcessResource.getAssetLibraryPortletExportProcessesPage(
				assetLibraryExternalReferenceCode, portletId, null, null, null,
				null, null);

		int totalCount = GetterUtil.getInteger(
			exportProcessesPage.getTotalCount());

		ExportProcess exportProcess1 =
			testGetAssetLibraryPortletExportProcessesPage_addExportProcess(
				assetLibraryExternalReferenceCode, portletId,
				randomExportProcess());

		ExportProcess exportProcess2 =
			testGetAssetLibraryPortletExportProcessesPage_addExportProcess(
				assetLibraryExternalReferenceCode, portletId,
				randomExportProcess());

		ExportProcess exportProcess3 =
			testGetAssetLibraryPortletExportProcessesPage_addExportProcess(
				assetLibraryExternalReferenceCode, portletId,
				randomExportProcess());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ExportProcess> page1 =
				exportProcessResource.getAssetLibraryPortletExportProcessesPage(
					assetLibraryExternalReferenceCode, portletId, null, null,
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				exportProcess1, (List<ExportProcess>)page1.getItems());

			Page<ExportProcess> page2 =
				exportProcessResource.getAssetLibraryPortletExportProcessesPage(
					assetLibraryExternalReferenceCode, portletId, null, null,
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				exportProcess2, (List<ExportProcess>)page2.getItems());

			Page<ExportProcess> page3 =
				exportProcessResource.getAssetLibraryPortletExportProcessesPage(
					assetLibraryExternalReferenceCode, portletId, null, null,
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				exportProcess3, (List<ExportProcess>)page3.getItems());
		}
		else {
			Page<ExportProcess> page1 =
				exportProcessResource.getAssetLibraryPortletExportProcessesPage(
					assetLibraryExternalReferenceCode, portletId, null, null,
					null, Pagination.of(1, totalCount + 2), null);

			List<ExportProcess> exportProcesses1 =
				(List<ExportProcess>)page1.getItems();

			Assert.assertEquals(
				exportProcesses1.toString(), totalCount + 2,
				exportProcesses1.size());

			Page<ExportProcess> page2 =
				exportProcessResource.getAssetLibraryPortletExportProcessesPage(
					assetLibraryExternalReferenceCode, portletId, null, null,
					null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ExportProcess> exportProcesses2 =
				(List<ExportProcess>)page2.getItems();

			Assert.assertEquals(
				exportProcesses2.toString(), 1, exportProcesses2.size());

			Page<ExportProcess> page3 =
				exportProcessResource.getAssetLibraryPortletExportProcessesPage(
					assetLibraryExternalReferenceCode, portletId, null, null,
					null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				exportProcess1, (List<ExportProcess>)page3.getItems());
			assertContains(
				exportProcess2, (List<ExportProcess>)page3.getItems());
			assertContains(
				exportProcess3, (List<ExportProcess>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryPortletExportProcessesPageWithSortDateTime()
		throws Exception {

		testGetAssetLibraryPortletExportProcessesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, exportProcess1, exportProcess2) -> {
				BeanTestUtil.setProperty(
					exportProcess1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetLibraryPortletExportProcessesPageWithSortDouble()
		throws Exception {

		testGetAssetLibraryPortletExportProcessesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, exportProcess1, exportProcess2) -> {
				BeanTestUtil.setProperty(
					exportProcess1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					exportProcess2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetLibraryPortletExportProcessesPageWithSortInteger()
		throws Exception {

		testGetAssetLibraryPortletExportProcessesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, exportProcess1, exportProcess2) -> {
				BeanTestUtil.setProperty(
					exportProcess1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					exportProcess2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetLibraryPortletExportProcessesPageWithSortString()
		throws Exception {

		testGetAssetLibraryPortletExportProcessesPageWithSort(
			EntityField.Type.STRING,
			(entityField, exportProcess1, exportProcess2) -> {
				Class<?> clazz = exportProcess1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						exportProcess1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						exportProcess2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						exportProcess1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						exportProcess2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						exportProcess1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						exportProcess2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAssetLibraryPortletExportProcessesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ExportProcess, ExportProcess, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String assetLibraryExternalReferenceCode =
			testGetAssetLibraryPortletExportProcessesPage_getAssetLibraryExternalReferenceCode();
		String portletId =
			testGetAssetLibraryPortletExportProcessesPage_getPortletId();

		ExportProcess exportProcess1 = randomExportProcess();
		ExportProcess exportProcess2 = randomExportProcess();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, exportProcess1, exportProcess2);
		}

		exportProcess1 =
			testGetAssetLibraryPortletExportProcessesPage_addExportProcess(
				assetLibraryExternalReferenceCode, portletId, exportProcess1);

		exportProcess2 =
			testGetAssetLibraryPortletExportProcessesPage_addExportProcess(
				assetLibraryExternalReferenceCode, portletId, exportProcess2);

		Page<ExportProcess> page =
			exportProcessResource.getAssetLibraryPortletExportProcessesPage(
				assetLibraryExternalReferenceCode, portletId, null, null, null,
				null, null);

		for (EntityField entityField : entityFields) {
			Page<ExportProcess> ascPage =
				exportProcessResource.getAssetLibraryPortletExportProcessesPage(
					assetLibraryExternalReferenceCode, portletId, null, null,
					null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				exportProcess1, (List<ExportProcess>)ascPage.getItems());
			assertContains(
				exportProcess2, (List<ExportProcess>)ascPage.getItems());

			Page<ExportProcess> descPage =
				exportProcessResource.getAssetLibraryPortletExportProcessesPage(
					assetLibraryExternalReferenceCode, portletId, null, null,
					null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				exportProcess2, (List<ExportProcess>)descPage.getItems());
			assertContains(
				exportProcess1, (List<ExportProcess>)descPage.getItems());
		}
	}

	protected ExportProcess
			testGetAssetLibraryPortletExportProcessesPage_addExportProcess(
				String assetLibraryExternalReferenceCode, String portletId,
				ExportProcess exportProcess)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAssetLibraryPortletExportProcessesPage_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return testDepotEntryGroup.getExternalReferenceCode();
	}

	protected String
			testGetAssetLibraryPortletExportProcessesPage_getIrrelevantAssetLibraryExternalReferenceCode()
		throws Exception {

		return irrelevantDepotEntryGroup.getExternalReferenceCode();
	}

	protected String
			testGetAssetLibraryPortletExportProcessesPage_getPortletId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAssetLibraryPortletExportProcessesPage_getIrrelevantPortletId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetExportProcess() throws Exception {
		ExportProcess postExportProcess =
			testGetExportProcess_addExportProcess();

		ExportProcess getExportProcess = exportProcessResource.getExportProcess(
			postExportProcess.getId());

		assertEquals(postExportProcess, getExportProcess);
		assertValid(getExportProcess);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ExportProcess postExportProcess =
			testGetExportProcess_addExportProcess();

		ExportProcess getExportProcess = exportProcessResource.getExportProcess(
			postExportProcess.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.exportimport.rest.dto.v1_0.ExportProcess"
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

		Object item = vulcanCRUDItemDelegate.getItem(postExportProcess.getId());

		assertEquals(
			getExportProcess, ExportProcessSerDes.toDTO(item.toString()));
	}

	protected HttpServletRequest
		testVulcanCRUDItemDelegate_getHttpServletRequest() {

		return new MockHttpServletRequest() {

			@Override
			public StringBuffer getRequestURL() {
				return new StringBuffer(
					StringBundler.concat(
						"http://localhost:",
						String.valueOf(PortalUtil.getPortalServerPort(false)),
						"/o/v1.0/", RandomTestUtil.randomString(), "/",
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
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false), "/o/",
						applicationPath, resourcePath));
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
				return URI.create(
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false), "/o/",
						applicationPath));
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

	protected ExportProcess testGetExportProcess_addExportProcess()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetExportProcessContent() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testGetExportProcessesPage() throws Exception {
		Page<ExportProcess> page = exportProcessResource.getExportProcessesPage(
			null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		ExportProcess exportProcess1 =
			testGetExportProcessesPage_addExportProcess(randomExportProcess());

		ExportProcess exportProcess2 =
			testGetExportProcessesPage_addExportProcess(randomExportProcess());

		page = exportProcessResource.getExportProcessesPage(
			null, null, null, Pagination.of(1, (int)totalCount + 2), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(exportProcess1, (List<ExportProcess>)page.getItems());
		assertContains(exportProcess2, (List<ExportProcess>)page.getItems());
		assertValid(page, testGetExportProcessesPage_getExpectedActions());

		exportProcessResource.deleteExportProcess(exportProcess1.getId());

		exportProcessResource.deleteExportProcess(exportProcess2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetExportProcessesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetExportProcessesPageWithPagination() throws Exception {
		Page<ExportProcess> exportProcessesPage =
			exportProcessResource.getExportProcessesPage(
				null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			exportProcessesPage.getTotalCount());

		ExportProcess exportProcess1 =
			testGetExportProcessesPage_addExportProcess(randomExportProcess());

		ExportProcess exportProcess2 =
			testGetExportProcessesPage_addExportProcess(randomExportProcess());

		ExportProcess exportProcess3 =
			testGetExportProcessesPage_addExportProcess(randomExportProcess());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ExportProcess> page1 =
				exportProcessResource.getExportProcessesPage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				exportProcess1, (List<ExportProcess>)page1.getItems());

			Page<ExportProcess> page2 =
				exportProcessResource.getExportProcessesPage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				exportProcess2, (List<ExportProcess>)page2.getItems());

			Page<ExportProcess> page3 =
				exportProcessResource.getExportProcessesPage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				exportProcess3, (List<ExportProcess>)page3.getItems());
		}
		else {
			Page<ExportProcess> page1 =
				exportProcessResource.getExportProcessesPage(
					null, null, null, Pagination.of(1, totalCount + 2), null);

			List<ExportProcess> exportProcesses1 =
				(List<ExportProcess>)page1.getItems();

			Assert.assertEquals(
				exportProcesses1.toString(), totalCount + 2,
				exportProcesses1.size());

			Page<ExportProcess> page2 =
				exportProcessResource.getExportProcessesPage(
					null, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ExportProcess> exportProcesses2 =
				(List<ExportProcess>)page2.getItems();

			Assert.assertEquals(
				exportProcesses2.toString(), 1, exportProcesses2.size());

			Page<ExportProcess> page3 =
				exportProcessResource.getExportProcessesPage(
					null, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(
				exportProcess1, (List<ExportProcess>)page3.getItems());
			assertContains(
				exportProcess2, (List<ExportProcess>)page3.getItems());
			assertContains(
				exportProcess3, (List<ExportProcess>)page3.getItems());
		}
	}

	@Test
	public void testGetExportProcessesPageWithSortDateTime() throws Exception {
		testGetExportProcessesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, exportProcess1, exportProcess2) -> {
				BeanTestUtil.setProperty(
					exportProcess1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetExportProcessesPageWithSortDouble() throws Exception {
		testGetExportProcessesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, exportProcess1, exportProcess2) -> {
				BeanTestUtil.setProperty(
					exportProcess1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					exportProcess2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetExportProcessesPageWithSortInteger() throws Exception {
		testGetExportProcessesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, exportProcess1, exportProcess2) -> {
				BeanTestUtil.setProperty(
					exportProcess1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					exportProcess2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetExportProcessesPageWithSortString() throws Exception {
		testGetExportProcessesPageWithSort(
			EntityField.Type.STRING,
			(entityField, exportProcess1, exportProcess2) -> {
				Class<?> clazz = exportProcess1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						exportProcess1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						exportProcess2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						exportProcess1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						exportProcess2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						exportProcess1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						exportProcess2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetExportProcessesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ExportProcess, ExportProcess, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		ExportProcess exportProcess1 = randomExportProcess();
		ExportProcess exportProcess2 = randomExportProcess();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, exportProcess1, exportProcess2);
		}

		exportProcess1 = testGetExportProcessesPage_addExportProcess(
			exportProcess1);

		exportProcess2 = testGetExportProcessesPage_addExportProcess(
			exportProcess2);

		Page<ExportProcess> page = exportProcessResource.getExportProcessesPage(
			null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ExportProcess> ascPage =
				exportProcessResource.getExportProcessesPage(
					null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				exportProcess1, (List<ExportProcess>)ascPage.getItems());
			assertContains(
				exportProcess2, (List<ExportProcess>)ascPage.getItems());

			Page<ExportProcess> descPage =
				exportProcessResource.getExportProcessesPage(
					null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				exportProcess2, (List<ExportProcess>)descPage.getItems());
			assertContains(
				exportProcess1, (List<ExportProcess>)descPage.getItems());
		}
	}

	protected ExportProcess testGetExportProcessesPage_addExportProcess(
			ExportProcess exportProcess)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetPortletExportProcessesPage() throws Exception {
		String portletId = testGetPortletExportProcessesPage_getPortletId();
		String irrelevantPortletId =
			testGetPortletExportProcessesPage_getIrrelevantPortletId();

		Page<ExportProcess> page =
			exportProcessResource.getPortletExportProcessesPage(
				portletId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantPortletId != null) {
			ExportProcess irrelevantExportProcess =
				testGetPortletExportProcessesPage_addExportProcess(
					irrelevantPortletId, randomIrrelevantExportProcess());

			page = exportProcessResource.getPortletExportProcessesPage(
				irrelevantPortletId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantExportProcess, (List<ExportProcess>)page.getItems());
			assertValid(
				page,
				testGetPortletExportProcessesPage_getExpectedActions(
					irrelevantPortletId));
		}

		ExportProcess exportProcess1 =
			testGetPortletExportProcessesPage_addExportProcess(
				portletId, randomExportProcess());

		ExportProcess exportProcess2 =
			testGetPortletExportProcessesPage_addExportProcess(
				portletId, randomExportProcess());

		page = exportProcessResource.getPortletExportProcessesPage(
			portletId, null, null, null, Pagination.of(1, (int)totalCount + 2),
			null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(exportProcess1, (List<ExportProcess>)page.getItems());
		assertContains(exportProcess2, (List<ExportProcess>)page.getItems());
		assertValid(
			page,
			testGetPortletExportProcessesPage_getExpectedActions(portletId));

		exportProcessResource.deleteExportProcess(exportProcess1.getId());

		exportProcessResource.deleteExportProcess(exportProcess2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetPortletExportProcessesPage_getExpectedActions(
				String portletId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPortletExportProcessesPageWithPagination()
		throws Exception {

		String portletId = testGetPortletExportProcessesPage_getPortletId();

		Page<ExportProcess> exportProcessesPage =
			exportProcessResource.getPortletExportProcessesPage(
				portletId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			exportProcessesPage.getTotalCount());

		ExportProcess exportProcess1 =
			testGetPortletExportProcessesPage_addExportProcess(
				portletId, randomExportProcess());

		ExportProcess exportProcess2 =
			testGetPortletExportProcessesPage_addExportProcess(
				portletId, randomExportProcess());

		ExportProcess exportProcess3 =
			testGetPortletExportProcessesPage_addExportProcess(
				portletId, randomExportProcess());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ExportProcess> page1 =
				exportProcessResource.getPortletExportProcessesPage(
					portletId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				exportProcess1, (List<ExportProcess>)page1.getItems());

			Page<ExportProcess> page2 =
				exportProcessResource.getPortletExportProcessesPage(
					portletId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				exportProcess2, (List<ExportProcess>)page2.getItems());

			Page<ExportProcess> page3 =
				exportProcessResource.getPortletExportProcessesPage(
					portletId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				exportProcess3, (List<ExportProcess>)page3.getItems());
		}
		else {
			Page<ExportProcess> page1 =
				exportProcessResource.getPortletExportProcessesPage(
					portletId, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<ExportProcess> exportProcesses1 =
				(List<ExportProcess>)page1.getItems();

			Assert.assertEquals(
				exportProcesses1.toString(), totalCount + 2,
				exportProcesses1.size());

			Page<ExportProcess> page2 =
				exportProcessResource.getPortletExportProcessesPage(
					portletId, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ExportProcess> exportProcesses2 =
				(List<ExportProcess>)page2.getItems();

			Assert.assertEquals(
				exportProcesses2.toString(), 1, exportProcesses2.size());

			Page<ExportProcess> page3 =
				exportProcessResource.getPortletExportProcessesPage(
					portletId, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				exportProcess1, (List<ExportProcess>)page3.getItems());
			assertContains(
				exportProcess2, (List<ExportProcess>)page3.getItems());
			assertContains(
				exportProcess3, (List<ExportProcess>)page3.getItems());
		}
	}

	@Test
	public void testGetPortletExportProcessesPageWithSortDateTime()
		throws Exception {

		testGetPortletExportProcessesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, exportProcess1, exportProcess2) -> {
				BeanTestUtil.setProperty(
					exportProcess1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetPortletExportProcessesPageWithSortDouble()
		throws Exception {

		testGetPortletExportProcessesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, exportProcess1, exportProcess2) -> {
				BeanTestUtil.setProperty(
					exportProcess1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					exportProcess2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetPortletExportProcessesPageWithSortInteger()
		throws Exception {

		testGetPortletExportProcessesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, exportProcess1, exportProcess2) -> {
				BeanTestUtil.setProperty(
					exportProcess1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					exportProcess2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetPortletExportProcessesPageWithSortString()
		throws Exception {

		testGetPortletExportProcessesPageWithSort(
			EntityField.Type.STRING,
			(entityField, exportProcess1, exportProcess2) -> {
				Class<?> clazz = exportProcess1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						exportProcess1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						exportProcess2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						exportProcess1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						exportProcess2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						exportProcess1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						exportProcess2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetPortletExportProcessesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ExportProcess, ExportProcess, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String portletId = testGetPortletExportProcessesPage_getPortletId();

		ExportProcess exportProcess1 = randomExportProcess();
		ExportProcess exportProcess2 = randomExportProcess();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, exportProcess1, exportProcess2);
		}

		exportProcess1 = testGetPortletExportProcessesPage_addExportProcess(
			portletId, exportProcess1);

		exportProcess2 = testGetPortletExportProcessesPage_addExportProcess(
			portletId, exportProcess2);

		Page<ExportProcess> page =
			exportProcessResource.getPortletExportProcessesPage(
				portletId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ExportProcess> ascPage =
				exportProcessResource.getPortletExportProcessesPage(
					portletId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				exportProcess1, (List<ExportProcess>)ascPage.getItems());
			assertContains(
				exportProcess2, (List<ExportProcess>)ascPage.getItems());

			Page<ExportProcess> descPage =
				exportProcessResource.getPortletExportProcessesPage(
					portletId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				exportProcess2, (List<ExportProcess>)descPage.getItems());
			assertContains(
				exportProcess1, (List<ExportProcess>)descPage.getItems());
		}
	}

	protected ExportProcess testGetPortletExportProcessesPage_addExportProcess(
			String portletId, ExportProcess exportProcess)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String testGetPortletExportProcessesPage_getPortletId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String testGetPortletExportProcessesPage_getIrrelevantPortletId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetSiteExportProcessesPage() throws Exception {
		String siteExternalReferenceCode =
			testGetSiteExportProcessesPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteExportProcessesPage_getIrrelevantSiteExternalReferenceCode();

		Page<ExportProcess> page =
			exportProcessResource.getSiteExportProcessesPage(
				siteExternalReferenceCode, null, null, null,
				Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteExternalReferenceCode != null) {
			ExportProcess irrelevantExportProcess =
				testGetSiteExportProcessesPage_addExportProcess(
					irrelevantSiteExternalReferenceCode,
					randomIrrelevantExportProcess());

			page = exportProcessResource.getSiteExportProcessesPage(
				irrelevantSiteExternalReferenceCode, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantExportProcess, (List<ExportProcess>)page.getItems());
			assertValid(
				page,
				testGetSiteExportProcessesPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode));
		}

		ExportProcess exportProcess1 =
			testGetSiteExportProcessesPage_addExportProcess(
				siteExternalReferenceCode, randomExportProcess());

		ExportProcess exportProcess2 =
			testGetSiteExportProcessesPage_addExportProcess(
				siteExternalReferenceCode, randomExportProcess());

		page = exportProcessResource.getSiteExportProcessesPage(
			siteExternalReferenceCode, null, null, null,
			Pagination.of(1, (int)totalCount + 2), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(exportProcess1, (List<ExportProcess>)page.getItems());
		assertContains(exportProcess2, (List<ExportProcess>)page.getItems());
		assertValid(
			page,
			testGetSiteExportProcessesPage_getExpectedActions(
				siteExternalReferenceCode));

		exportProcessResource.deleteExportProcess(exportProcess1.getId());

		exportProcessResource.deleteExportProcess(exportProcess2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteExportProcessesPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			("http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/export-import/v1.0/sites/{siteExternalReferenceCode}/export-processes/batch").
					replace(
						"{siteExternalReferenceCode}",
						String.valueOf(siteExternalReferenceCode)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteExportProcessesPageWithPagination()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteExportProcessesPage_getSiteExternalReferenceCode();

		Page<ExportProcess> exportProcessesPage =
			exportProcessResource.getSiteExportProcessesPage(
				siteExternalReferenceCode, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			exportProcessesPage.getTotalCount());

		ExportProcess exportProcess1 =
			testGetSiteExportProcessesPage_addExportProcess(
				siteExternalReferenceCode, randomExportProcess());

		ExportProcess exportProcess2 =
			testGetSiteExportProcessesPage_addExportProcess(
				siteExternalReferenceCode, randomExportProcess());

		ExportProcess exportProcess3 =
			testGetSiteExportProcessesPage_addExportProcess(
				siteExternalReferenceCode, randomExportProcess());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ExportProcess> page1 =
				exportProcessResource.getSiteExportProcessesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				exportProcess1, (List<ExportProcess>)page1.getItems());

			Page<ExportProcess> page2 =
				exportProcessResource.getSiteExportProcessesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				exportProcess2, (List<ExportProcess>)page2.getItems());

			Page<ExportProcess> page3 =
				exportProcessResource.getSiteExportProcessesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				exportProcess3, (List<ExportProcess>)page3.getItems());
		}
		else {
			Page<ExportProcess> page1 =
				exportProcessResource.getSiteExportProcessesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<ExportProcess> exportProcesses1 =
				(List<ExportProcess>)page1.getItems();

			Assert.assertEquals(
				exportProcesses1.toString(), totalCount + 2,
				exportProcesses1.size());

			Page<ExportProcess> page2 =
				exportProcessResource.getSiteExportProcessesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ExportProcess> exportProcesses2 =
				(List<ExportProcess>)page2.getItems();

			Assert.assertEquals(
				exportProcesses2.toString(), 1, exportProcesses2.size());

			Page<ExportProcess> page3 =
				exportProcessResource.getSiteExportProcessesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				exportProcess1, (List<ExportProcess>)page3.getItems());
			assertContains(
				exportProcess2, (List<ExportProcess>)page3.getItems());
			assertContains(
				exportProcess3, (List<ExportProcess>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteExportProcessesPageWithSortDateTime()
		throws Exception {

		testGetSiteExportProcessesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, exportProcess1, exportProcess2) -> {
				BeanTestUtil.setProperty(
					exportProcess1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteExportProcessesPageWithSortDouble()
		throws Exception {

		testGetSiteExportProcessesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, exportProcess1, exportProcess2) -> {
				BeanTestUtil.setProperty(
					exportProcess1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					exportProcess2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteExportProcessesPageWithSortInteger()
		throws Exception {

		testGetSiteExportProcessesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, exportProcess1, exportProcess2) -> {
				BeanTestUtil.setProperty(
					exportProcess1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					exportProcess2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteExportProcessesPageWithSortString()
		throws Exception {

		testGetSiteExportProcessesPageWithSort(
			EntityField.Type.STRING,
			(entityField, exportProcess1, exportProcess2) -> {
				Class<?> clazz = exportProcess1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						exportProcess1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						exportProcess2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						exportProcess1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						exportProcess2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						exportProcess1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						exportProcess2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteExportProcessesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ExportProcess, ExportProcess, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteExportProcessesPage_getSiteExternalReferenceCode();

		ExportProcess exportProcess1 = randomExportProcess();
		ExportProcess exportProcess2 = randomExportProcess();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, exportProcess1, exportProcess2);
		}

		exportProcess1 = testGetSiteExportProcessesPage_addExportProcess(
			siteExternalReferenceCode, exportProcess1);

		exportProcess2 = testGetSiteExportProcessesPage_addExportProcess(
			siteExternalReferenceCode, exportProcess2);

		Page<ExportProcess> page =
			exportProcessResource.getSiteExportProcessesPage(
				siteExternalReferenceCode, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ExportProcess> ascPage =
				exportProcessResource.getSiteExportProcessesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				exportProcess1, (List<ExportProcess>)ascPage.getItems());
			assertContains(
				exportProcess2, (List<ExportProcess>)ascPage.getItems());

			Page<ExportProcess> descPage =
				exportProcessResource.getSiteExportProcessesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				exportProcess2, (List<ExportProcess>)descPage.getItems());
			assertContains(
				exportProcess1, (List<ExportProcess>)descPage.getItems());
		}
	}

	protected ExportProcess testGetSiteExportProcessesPage_addExportProcess(
			String siteExternalReferenceCode, ExportProcess exportProcess)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteExportProcessesPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteExportProcessesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetSitePortletExportProcessesPage() throws Exception {
		String siteExternalReferenceCode =
			testGetSitePortletExportProcessesPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSitePortletExportProcessesPage_getIrrelevantSiteExternalReferenceCode();
		String portletId = testGetSitePortletExportProcessesPage_getPortletId();
		String irrelevantPortletId =
			testGetSitePortletExportProcessesPage_getIrrelevantPortletId();

		Page<ExportProcess> page =
			exportProcessResource.getSitePortletExportProcessesPage(
				siteExternalReferenceCode, portletId, null, null, null,
				Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if ((irrelevantSiteExternalReferenceCode != null) &&
			(irrelevantPortletId != null)) {

			ExportProcess irrelevantExportProcess =
				testGetSitePortletExportProcessesPage_addExportProcess(
					irrelevantSiteExternalReferenceCode, irrelevantPortletId,
					randomIrrelevantExportProcess());

			page = exportProcessResource.getSitePortletExportProcessesPage(
				irrelevantSiteExternalReferenceCode, irrelevantPortletId, null,
				null, null, Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantExportProcess, (List<ExportProcess>)page.getItems());
			assertValid(
				page,
				testGetSitePortletExportProcessesPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode, irrelevantPortletId));
		}

		ExportProcess exportProcess1 =
			testGetSitePortletExportProcessesPage_addExportProcess(
				siteExternalReferenceCode, portletId, randomExportProcess());

		ExportProcess exportProcess2 =
			testGetSitePortletExportProcessesPage_addExportProcess(
				siteExternalReferenceCode, portletId, randomExportProcess());

		page = exportProcessResource.getSitePortletExportProcessesPage(
			siteExternalReferenceCode, portletId, null, null, null,
			Pagination.of(1, (int)totalCount + 2), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(exportProcess1, (List<ExportProcess>)page.getItems());
		assertContains(exportProcess2, (List<ExportProcess>)page.getItems());
		assertValid(
			page,
			testGetSitePortletExportProcessesPage_getExpectedActions(
				siteExternalReferenceCode, portletId));

		exportProcessResource.deleteExportProcess(exportProcess1.getId());

		exportProcessResource.deleteExportProcess(exportProcess2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSitePortletExportProcessesPage_getExpectedActions(
				String siteExternalReferenceCode, String portletId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSitePortletExportProcessesPageWithPagination()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSitePortletExportProcessesPage_getSiteExternalReferenceCode();
		String portletId = testGetSitePortletExportProcessesPage_getPortletId();

		Page<ExportProcess> exportProcessesPage =
			exportProcessResource.getSitePortletExportProcessesPage(
				siteExternalReferenceCode, portletId, null, null, null, null,
				null);

		int totalCount = GetterUtil.getInteger(
			exportProcessesPage.getTotalCount());

		ExportProcess exportProcess1 =
			testGetSitePortletExportProcessesPage_addExportProcess(
				siteExternalReferenceCode, portletId, randomExportProcess());

		ExportProcess exportProcess2 =
			testGetSitePortletExportProcessesPage_addExportProcess(
				siteExternalReferenceCode, portletId, randomExportProcess());

		ExportProcess exportProcess3 =
			testGetSitePortletExportProcessesPage_addExportProcess(
				siteExternalReferenceCode, portletId, randomExportProcess());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ExportProcess> page1 =
				exportProcessResource.getSitePortletExportProcessesPage(
					siteExternalReferenceCode, portletId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				exportProcess1, (List<ExportProcess>)page1.getItems());

			Page<ExportProcess> page2 =
				exportProcessResource.getSitePortletExportProcessesPage(
					siteExternalReferenceCode, portletId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				exportProcess2, (List<ExportProcess>)page2.getItems());

			Page<ExportProcess> page3 =
				exportProcessResource.getSitePortletExportProcessesPage(
					siteExternalReferenceCode, portletId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				exportProcess3, (List<ExportProcess>)page3.getItems());
		}
		else {
			Page<ExportProcess> page1 =
				exportProcessResource.getSitePortletExportProcessesPage(
					siteExternalReferenceCode, portletId, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<ExportProcess> exportProcesses1 =
				(List<ExportProcess>)page1.getItems();

			Assert.assertEquals(
				exportProcesses1.toString(), totalCount + 2,
				exportProcesses1.size());

			Page<ExportProcess> page2 =
				exportProcessResource.getSitePortletExportProcessesPage(
					siteExternalReferenceCode, portletId, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ExportProcess> exportProcesses2 =
				(List<ExportProcess>)page2.getItems();

			Assert.assertEquals(
				exportProcesses2.toString(), 1, exportProcesses2.size());

			Page<ExportProcess> page3 =
				exportProcessResource.getSitePortletExportProcessesPage(
					siteExternalReferenceCode, portletId, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				exportProcess1, (List<ExportProcess>)page3.getItems());
			assertContains(
				exportProcess2, (List<ExportProcess>)page3.getItems());
			assertContains(
				exportProcess3, (List<ExportProcess>)page3.getItems());
		}
	}

	@Test
	public void testGetSitePortletExportProcessesPageWithSortDateTime()
		throws Exception {

		testGetSitePortletExportProcessesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, exportProcess1, exportProcess2) -> {
				BeanTestUtil.setProperty(
					exportProcess1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSitePortletExportProcessesPageWithSortDouble()
		throws Exception {

		testGetSitePortletExportProcessesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, exportProcess1, exportProcess2) -> {
				BeanTestUtil.setProperty(
					exportProcess1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					exportProcess2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSitePortletExportProcessesPageWithSortInteger()
		throws Exception {

		testGetSitePortletExportProcessesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, exportProcess1, exportProcess2) -> {
				BeanTestUtil.setProperty(
					exportProcess1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					exportProcess2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSitePortletExportProcessesPageWithSortString()
		throws Exception {

		testGetSitePortletExportProcessesPageWithSort(
			EntityField.Type.STRING,
			(entityField, exportProcess1, exportProcess2) -> {
				Class<?> clazz = exportProcess1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						exportProcess1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						exportProcess2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						exportProcess1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						exportProcess2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						exportProcess1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						exportProcess2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSitePortletExportProcessesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ExportProcess, ExportProcess, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSitePortletExportProcessesPage_getSiteExternalReferenceCode();
		String portletId = testGetSitePortletExportProcessesPage_getPortletId();

		ExportProcess exportProcess1 = randomExportProcess();
		ExportProcess exportProcess2 = randomExportProcess();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, exportProcess1, exportProcess2);
		}

		exportProcess1 = testGetSitePortletExportProcessesPage_addExportProcess(
			siteExternalReferenceCode, portletId, exportProcess1);

		exportProcess2 = testGetSitePortletExportProcessesPage_addExportProcess(
			siteExternalReferenceCode, portletId, exportProcess2);

		Page<ExportProcess> page =
			exportProcessResource.getSitePortletExportProcessesPage(
				siteExternalReferenceCode, portletId, null, null, null, null,
				null);

		for (EntityField entityField : entityFields) {
			Page<ExportProcess> ascPage =
				exportProcessResource.getSitePortletExportProcessesPage(
					siteExternalReferenceCode, portletId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				exportProcess1, (List<ExportProcess>)ascPage.getItems());
			assertContains(
				exportProcess2, (List<ExportProcess>)ascPage.getItems());

			Page<ExportProcess> descPage =
				exportProcessResource.getSitePortletExportProcessesPage(
					siteExternalReferenceCode, portletId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				exportProcess2, (List<ExportProcess>)descPage.getItems());
			assertContains(
				exportProcess1, (List<ExportProcess>)descPage.getItems());
		}
	}

	protected ExportProcess
			testGetSitePortletExportProcessesPage_addExportProcess(
				String siteExternalReferenceCode, String portletId,
				ExportProcess exportProcess)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSitePortletExportProcessesPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSitePortletExportProcessesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	protected String testGetSitePortletExportProcessesPage_getPortletId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSitePortletExportProcessesPage_getIrrelevantPortletId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostAssetLibraryExportProcess() throws Exception {
		ExportProcess randomExportProcess = randomExportProcess();

		ExportProcess postExportProcess =
			testPostAssetLibraryExportProcess_addExportProcess(
				randomExportProcess);

		assertEquals(randomExportProcess, postExportProcess);
		assertValid(postExportProcess);
	}

	protected ExportProcess testPostAssetLibraryExportProcess_addExportProcess(
			ExportProcess exportProcess)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAssetLibraryPortletExportProcess() throws Exception {
		ExportProcess randomExportProcess = randomExportProcess();

		ExportProcess postExportProcess =
			testPostAssetLibraryPortletExportProcess_addExportProcess(
				randomExportProcess);

		assertEquals(randomExportProcess, postExportProcess);
		assertValid(postExportProcess);
	}

	protected ExportProcess
			testPostAssetLibraryPortletExportProcess_addExportProcess(
				ExportProcess exportProcess)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostExportProcess() throws Exception {
		ExportProcess randomExportProcess = randomExportProcess();

		ExportProcess postExportProcess =
			testPostExportProcess_addExportProcess(randomExportProcess);

		assertEquals(randomExportProcess, postExportProcess);
		assertValid(postExportProcess);
	}

	protected ExportProcess testPostExportProcess_addExportProcess(
			ExportProcess exportProcess)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostExportProcessRelaunch() throws Exception {
		ExportProcess randomExportProcess = randomExportProcess();

		ExportProcess postExportProcess =
			testPostExportProcessRelaunch_addExportProcess(randomExportProcess);

		assertEquals(randomExportProcess, postExportProcess);
		assertValid(postExportProcess);
	}

	protected ExportProcess testPostExportProcessRelaunch_addExportProcess(
			ExportProcess exportProcess)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostPortletExportProcess() throws Exception {
		ExportProcess randomExportProcess = randomExportProcess();

		ExportProcess postExportProcess =
			testPostPortletExportProcess_addExportProcess(randomExportProcess);

		assertEquals(randomExportProcess, postExportProcess);
		assertValid(postExportProcess);
	}

	protected ExportProcess testPostPortletExportProcess_addExportProcess(
			ExportProcess exportProcess)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSiteExportProcess() throws Exception {
		ExportProcess randomExportProcess = randomExportProcess();

		ExportProcess postExportProcess =
			testPostSiteExportProcess_addExportProcess(randomExportProcess);

		assertEquals(randomExportProcess, postExportProcess);
		assertValid(postExportProcess);
	}

	protected ExportProcess testPostSiteExportProcess_addExportProcess(
			ExportProcess exportProcess)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSitePortletExportProcess() throws Exception {
		ExportProcess randomExportProcess = randomExportProcess();

		ExportProcess postExportProcess =
			testPostSitePortletExportProcess_addExportProcess(
				randomExportProcess);

		assertEquals(randomExportProcess, postExportProcess);
		assertValid(postExportProcess);
	}

	protected ExportProcess testPostSitePortletExportProcess_addExportProcess(
			ExportProcess exportProcess)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ExportProcess exportProcess1 =
			testBatchEngineDeleteImportTask_addExportProcess();

		testBatchEngineDeleteImportTask_deleteExportProcess(
			200, null, exportProcess1.getId());

		assertHttpResponseStatusCode(
			404,
			exportProcessResource.getExportProcessHttpResponse(
				exportProcess1.getId()));
	}

	protected ExportProcess testBatchEngineDeleteImportTask_addExportProcess()
		throws Exception {

		return testDeleteExportProcess_addExportProcess();
	}

	protected void testBatchEngineDeleteImportTask_deleteExportProcess(
			int expectedStatusCode, String externalReferenceCode, Long id,
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
				"com.liferay.exportimport.rest.dto.v1_0.ExportProcess", null,
				null, null, null,
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

	@Test
	public void testGetExportProcessProgress() throws Exception {
		ExportProcess postExportProcess =
			testGetExportProcess_addExportProcess();

		ProcessProgress postProcessProgress =
			testGetExportProcessProgress_addProcessProgress(
				postExportProcess.getId(), randomProcessProgress());

		ProcessProgress getProcessProgress =
			exportProcessResource.getExportProcessProgress(
				postExportProcess.getId());

		assertEquals(postProcessProgress, getProcessProgress);
		assertValid(getProcessProgress);
	}

	protected ProcessProgress testGetExportProcessProgress_addProcessProgress(
			long exportProcessId, ProcessProgress processProgress)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		ExportProcess exportProcess, List<ExportProcess> exportProcesses) {

		boolean contains = false;

		for (ExportProcess item : exportProcesses) {
			if (equals(exportProcess, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			exportProcesses + " does not contain " + exportProcess, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ExportProcess exportProcess1, ExportProcess exportProcess2) {

		Assert.assertTrue(
			exportProcess1 + " does not equal " + exportProcess2,
			equals(exportProcess1, exportProcess2));
	}

	protected void assertEquals(
		List<ExportProcess> exportProcesses1,
		List<ExportProcess> exportProcesses2) {

		Assert.assertEquals(exportProcesses1.size(), exportProcesses2.size());

		for (int i = 0; i < exportProcesses1.size(); i++) {
			ExportProcess exportProcess1 = exportProcesses1.get(i);
			ExportProcess exportProcess2 = exportProcesses2.get(i);

			assertEquals(exportProcess1, exportProcess2);
		}
	}

	protected void assertEquals(
		ProcessProgress processProgress1, ProcessProgress processProgress2) {

		Assert.assertTrue(
			processProgress1 + " does not equal " + processProgress2,
			equals(processProgress1, processProgress2));
	}

	protected void assertEqualsIgnoringOrder(
		List<ExportProcess> exportProcesses1,
		List<ExportProcess> exportProcesses2) {

		Assert.assertEquals(exportProcesses1.size(), exportProcesses2.size());

		for (ExportProcess exportProcess1 : exportProcesses1) {
			boolean contains = false;

			for (ExportProcess exportProcess2 : exportProcesses2) {
				if (equals(exportProcess1, exportProcess2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				exportProcesses2 + " does not contain " + exportProcess1,
				contains);
		}
	}

	protected void assertValid(ExportProcess exportProcess) throws Exception {
		boolean valid = true;

		if (exportProcess.getDateCreated() == null) {
			valid = false;
		}

		if (exportProcess.getDateModified() == null) {
			valid = false;
		}

		if (exportProcess.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (exportProcess.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dateCompleted", additionalAssertFieldName)) {
				if (exportProcess.getDateCompleted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("errorMessage", additionalAssertFieldName)) {
				if (exportProcess.getErrorMessage() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (exportProcess.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (exportProcess.getStatus() == null) {
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

	protected void assertValid(Page<ExportProcess> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ExportProcess> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ExportProcess> exportProcesses = page.getItems();

		int size = exportProcesses.size();

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

	protected void assertValid(ProcessProgress processProgress) {
		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalProcessProgressAssertFieldNames()) {

			if (Objects.equals("percentage", additionalAssertFieldName)) {
				if (processProgress.getPercentage() == null) {
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

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected String[] getAdditionalProcessProgressAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		graphQLFields.add(new GraphQLField("id"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.exportimport.rest.dto.v1_0.ExportProcess.
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
		ExportProcess exportProcess1, ExportProcess exportProcess2) {

		if (exportProcess1 == exportProcess2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						exportProcess1.getCreator(),
						exportProcess2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCompleted", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						exportProcess1.getDateCompleted(),
						exportProcess2.getDateCompleted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						exportProcess1.getDateCreated(),
						exportProcess2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						exportProcess1.getDateModified(),
						exportProcess2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("errorMessage", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						exportProcess1.getErrorMessage(),
						exportProcess2.getErrorMessage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						exportProcess1.getId(), exportProcess2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						exportProcess1.getName(), exportProcess2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						exportProcess1.getStatus(),
						exportProcess2.getStatus())) {

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

	protected boolean equals(
		ProcessProgress processProgress1, ProcessProgress processProgress2) {

		if (processProgress1 == processProgress2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalProcessProgressAssertFieldNames()) {

			if (Objects.equals("percentage", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						processProgress1.getPercentage(),
						processProgress2.getPercentage())) {

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

		if (!(_exportProcessResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_exportProcessResource;

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
		EntityField entityField, String operator, ExportProcess exportProcess) {

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

		if (entityFieldName.equals("dateCompleted")) {
			if (operator.equals("between")) {
				Date date = exportProcess.getDateCompleted();

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

				sb.append(_format.format(exportProcess.getDateCompleted()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = exportProcess.getDateCreated();

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

				sb.append(_format.format(exportProcess.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = exportProcess.getDateModified();

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

				sb.append(_format.format(exportProcess.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("errorMessage")) {
			Object object = exportProcess.getErrorMessage();

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
			Object object = exportProcess.getName();

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

		if (entityFieldName.equals("status")) {
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

	protected ExportProcess randomExportProcess() throws Exception {
		return new ExportProcess() {
			{
				dateCompleted = RandomTestUtil.nextDate();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				errorMessage = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected ExportProcess randomIrrelevantExportProcess() throws Exception {
		ExportProcess randomIrrelevantExportProcess = randomExportProcess();

		return randomIrrelevantExportProcess;
	}

	protected ExportProcess randomPatchExportProcess() throws Exception {
		return randomExportProcess();
	}

	protected ProcessProgress randomProcessProgress() throws Exception {
		return new ProcessProgress() {
			{
				percentage = RandomTestUtil.randomInt();
			}
		};
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

	protected ExportProcessResource exportProcessResource;
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
		LogFactoryUtil.getLog(BaseExportProcessResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.exportimport.rest.resource.v1_0.ExportProcessResource
		_exportProcessResource;

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
// LIFERAY-REST-BUILDER-HASH:495681531