/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.odata.entity.DateTimeEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.staging.StagingGroupHelper;

import jakarta.portlet.GenericPortlet;
import jakarta.portlet.Portlet;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Vendel Toreki
 * @author Petteri Karttunen
 */
@FeatureFlag("LPD-35914")
@RunWith(Arquillian.class)
public class BatchEnginePortletDataHandlerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule liferayIntegrationTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws PortalException {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-35914");
	}

	@AfterClass
	public static void tearDownClass() throws PortalException {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), false, "LPD-35914");
	}

	@Test
	@TestInfo("LPD-51604")
	public void testEnableLocalStaging() throws Exception {
		Group group = GroupTestUtil.addGroup();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.exportimport.internal.lifecycle." +
					"LoggerExportImportLifecycleListener",
				LoggerTestUtil.ERROR)) {

			_stagingLocalService.enableLocalStaging(
				TestPropsValues.getUserId(), group, false, false,
				ServiceContextTestUtil.getServiceContext(
					group.getGroupId(), TestPropsValues.getUserId()));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			for (LogEntry logEntry : logEntries) {
				String message = logEntry.getMessage();

				Assert.assertFalse(
					message,
					message.contains(
						"Portlet export failed for portlet com_liferay_object" +
							"_web_internal_object_definitions_portlet" +
								"_ObjectDefinitionsPortlet"));
			}

			Assert.assertTrue(logEntries.toString(), logEntries.isEmpty());
		}
	}

	@Test
	@TestInfo("LPD-61995")
	public void testEnableLocalStagingWithSiteScopedObjectDefinition()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		_addObjectDefinition(ObjectDefinitionConstants.SCOPE_SITE);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.exportimport.internal.lifecycle." +
					"ExportImportProcessCallbackLifecycleListener",
				LoggerTestUtil.ERROR)) {

			_stagingLocalService.enableLocalStaging(
				TestPropsValues.getUserId(), group, false, false,
				ServiceContextTestUtil.getServiceContext(
					group.getGroupId(), TestPropsValues.getUserId()));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.toString(), logEntries.isEmpty());
		}
	}

	@Test
	@TestInfo("LPD-50142")
	public void testExportImportCompanyGroupObjectEntries() throws Exception {
		_testExportImportObjectEntriesToSameGroup(
			_stagingGroupHelper.fetchCompanyGroup(
				TestPropsValues.getCompanyId()),
			ObjectDefinitionConstants.SCOPE_COMPANY);
	}

	@Test
	public void testExportImportCompanyGroupObjectEntriesWithError()
		throws Exception {

		Group group = _stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());

		ObjectDefinition objectDefinition = _addObjectDefinition(
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry[] objectEntries = _addObjectEntries(
			3, 0L, objectDefinition);

		File larFile = _exportLayouts(
			false, group.getGroupId(), false, new long[0], objectDefinition);

		_deleteObjectEntries(objectEntries);

		ObjectEntry objectEntry = objectEntries[1];

		Map<String, Serializable> values = objectEntry.getValues();

		ObjectEntry duplicateObjectEntry = _addObjectEntry(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, objectDefinition,
			values.get(_OBJECT_FIELD_NAME_TEXT));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal.strategy." +
					"OnErrorContinueBatchEngineImportStrategy",
				LoggerTestUtil.OFF)) {

			_importLayouts(
				false, false, larFile, group.getGroupId(), objectDefinition);
		}

		List<ObjectEntry> objectEntriesList =
			_objectEntryLocalService.getObjectEntries(
				GroupConstants.DEFAULT_PARENT_GROUP_ID,
				objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertEquals(
			objectEntriesList.toString(), 3, objectEntriesList.size());

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				objectEntry.getExternalReferenceCode(),
				objectEntry.getGroupId(),
				objectDefinition.getObjectDefinitionId()));
		Assert.assertNotEquals(
			objectEntry.getExternalReferenceCode(),
			duplicateObjectEntry.getExternalReferenceCode());
		Assert.assertTrue(
			ListUtil.exists(
				_batchEngineImportTaskLocalService.getBatchEngineImportTasks(
					BatchEngineTaskExecuteStatus.COMPLETED.toString()),
				batchEngineImportTask -> Objects.equals(
					batchEngineImportTask.getTaskItemDelegateName(),
					objectDefinition.getName())));
	}

	@Test
	@TestInfo("LPD-61997")
	public void testExportImportCompanyGroupObjectEntriesWithRelatedObjectEntries()
		throws Exception {

		Group group = _stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());

		_testExportImportObjectEntriesWithRelatedObjectEntries(
			group, ObjectDefinitionConstants.SCOPE_COMPANY,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		_testExportImportObjectEntriesWithRelatedObjectEntries(
			group, ObjectDefinitionConstants.SCOPE_COMPANY,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
	}

	@FeatureFlag("LPD-35443")
	@Test
	@TestInfo("LPD-64365")
	public void testExportImportLayoutsToOtherSite() throws Exception {
		Group group1 = GroupTestUtil.addGroup();

		Layout layout1 = LayoutTestUtil.addTypePortletLayout(group1);
		Layout layout2 = LayoutTestUtil.addTypePortletLayout(group1);

		File larFile = _exportLayouts(
			false, group1.getGroupId(), true, false,
			new long[] {layout1.getLayoutId()});

		Group group2 = GroupTestUtil.addGroup();

		_importLayouts(false, false, larFile, group2.getGroupId(), true, false);

		layout1 = _layoutLocalService.fetchLayoutByExternalReferenceCode(
			layout1.getExternalReferenceCode(), group2.getGroupId());

		Assert.assertNotNull(layout1);

		layout2 = _layoutLocalService.fetchLayoutByExternalReferenceCode(
			layout2.getExternalReferenceCode(), group2.getGroupId());

		Assert.assertNull(layout2);
	}

	@Test
	@TestInfo("LPD-54863")
	public void testExportImportObjectEntriesWithErrorReport()
		throws Exception {

		_testExportImportObjectEntriesWithErrorReport(
			_stagingGroupHelper.fetchCompanyGroup(
				TestPropsValues.getCompanyId()),
			ObjectDefinitionConstants.SCOPE_COMPANY);
		_testExportImportObjectEntriesWithErrorReport(
			GroupTestUtil.addGroup(), ObjectDefinitionConstants.SCOPE_SITE);
	}

	@Ignore("LPD-40798")
	@Test
	@TestInfo("LPD-57756")
	public void testExportImportPriorityWithSiteScopeObjectEntries()
		throws Exception {

		Group group1 = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(group1);

		LayoutTestUtil.addPortletToLayout(
			layout, JournalContentPortletKeys.JOURNAL_CONTENT);

		ObjectDefinition objectDefinition = _addObjectDefinition(
			ObjectDefinitionConstants.SCOPE_SITE);

		ObjectEntry[] objectEntries = _addObjectEntries(
			3, group1.getGroupId(), objectDefinition);

		File larFile = _exportLayouts(
			false, group1.getGroupId(), false,
			new long[] {layout.getLayoutId()}, objectDefinition);

		Group group2 = GroupTestUtil.addGroup();

		BundleContext bundleContext = FrameworkUtil.getBundle(
			getClass()
		).getBundleContext();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				StagedModelDataHandler.class,
				new BaseStagedModelDataHandler<Layout>() {

					@Override
					public String[] getClassNames() {
						return new String[] {Layout.class.getName()};
					}

					@Override
					protected void doExportStagedModel(
							PortletDataContext portletDataContext,
							Layout layout)
						throws Exception {
					}

					@Override
					protected void doImportStagedModel(
							PortletDataContext portletDataContext,
							Layout layout)
						throws Exception {

						throw new PortletDataException();
					}

				},
				HashMapDictionaryBuilder.<String, Object>put(
					"model.class.name", Layout.class.getName()
				).put(
					"service.ranking", Integer.MAX_VALUE
				).build());

		try {
			_importLayouts(
				false, true, larFile, group2.getGroupId(), objectDefinition);
			Assert.fail();
		}
		catch (PortletDataException portletDataException) {
			List<ObjectEntry> objectEntriesList =
				_objectEntryLocalService.getObjectEntries(
					group2.getGroupId(),
					objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

			Assert.assertEquals(
				Arrays.toString(objectEntries), objectEntries.length,
				objectEntriesList.size());
		}
		finally {
			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}
		}
	}

	@Test
	@TestInfo("LPD-64361")
	public void testExportImportPrivateLayoutsToOtherSite() throws Exception {
		Group group1 = GroupTestUtil.addGroup();

		Layout layout1 = LayoutTestUtil.addTypePortletLayout(group1, true);
		Layout layout2 = LayoutTestUtil.addTypePortletLayout(group1, true);

		File larFile = _exportLayouts(
			false, group1.getGroupId(), false, true,
			new long[] {layout1.getLayoutId()});

		Group group2 = GroupTestUtil.addGroup();

		_importLayouts(false, false, larFile, group2.getGroupId(), false, true);

		layout1 = _layoutLocalService.fetchLayoutByExternalReferenceCode(
			layout1.getExternalReferenceCode(), group2.getGroupId());

		Assert.assertNotNull(layout1);

		layout2 = _layoutLocalService.fetchLayoutByExternalReferenceCode(
			layout2.getExternalReferenceCode(), group2.getGroupId());

		Assert.assertNull(layout2);
	}

	@Test
	public void testExportImportSiteObjectEntriesToOtherSite()
		throws Exception {

		ObjectDefinition objectDefinition = _addObjectDefinition(
			ObjectDefinitionConstants.SCOPE_SITE);

		Group group1 = GroupTestUtil.addGroup();

		ObjectEntry[] objectEntries = _addObjectEntries(
			3, group1.getGroupId(), objectDefinition);

		File larFile = _exportLayouts(
			false, group1.getGroupId(), false, new long[0], objectDefinition);

		Group group2 = GroupTestUtil.addGroup();

		_importLayouts(
			false, false, larFile, group2.getGroupId(), objectDefinition);

		List<ObjectEntry> objectEntriesList =
			_objectEntryLocalService.getObjectEntries(
				group2.getGroupId(), objectDefinition.getObjectDefinitionId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			Arrays.toString(objectEntries), objectEntriesList.size(),
			objectEntries.length);
	}

	@Test
	public void testExportImportSiteObjectEntriesToSameSite() throws Exception {
		_testExportImportObjectEntriesToSameGroup(
			GroupTestUtil.addGroup(), ObjectDefinitionConstants.SCOPE_SITE);
	}

	@Test
	@TestInfo("LPD-61997")
	public void testExportImportSiteObjectEntriesWithRelatedObjectEntries()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		_testExportImportObjectEntriesWithRelatedObjectEntries(
			group, ObjectDefinitionConstants.SCOPE_SITE,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		_testExportImportObjectEntriesWithRelatedObjectEntries(
			group, ObjectDefinitionConstants.SCOPE_SITE,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
	}

	@Test
	@TestInfo("LPD-58645")
	public void testExportImportWithDifferentScopedObjectEntries()
		throws Exception {

		Group group1 = GroupTestUtil.addGroupWithType(
			GroupConstants.TYPE_DEPOT);

		Layout layout = LayoutTestUtil.addTypePortletLayout(group1);

		ObjectDefinition objectDefinition = _addObjectDefinition(
			ObjectDefinitionConstants.SCOPE_SITE);

		_addObjectEntries(3, group1.getGroupId(), objectDefinition);

		File larFile = _exportLayouts(
			false, group1.getGroupId(), false,
			new long[] {layout.getLayoutId()}, objectDefinition);

		Group group2 = GroupTestUtil.addGroupWithType(
			GroupConstants.TYPE_DEPOT);

		_importLayouts(
			false, true, larFile, group2.getGroupId(), objectDefinition);

		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getObjectEntries(
				group2.getGroupId(), objectDefinition.getObjectDefinitionId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(objectEntries.toString(), 0, objectEntries.size());
	}

	@Test
	@TestInfo("LPD-50142")
	public void testExportIndividualDeletionsCompanyGroup() throws Exception {
		Group group = _stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());

		ObjectDefinition objectDefinition1 = _addObjectDefinition(
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry[] objectEntries = _addObjectEntries(
			3, GroupConstants.DEFAULT_PARENT_GROUP_ID, objectDefinition1);

		_deleteObjectEntries(objectEntries);

		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						RandomTestUtil.randomString(), _OBJECT_FIELD_NAME_TEXT,
						false)),
				ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry objectEntry = _addObjectEntry(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, objectDefinition2,
			RandomTestUtil.randomString());

		_deleteObjectEntries(objectEntry);

		File file = _exportLayouts(
			true, group.getGroupId(), false, new long[0], objectDefinition1);

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				_getExternalReferenceCodes(objectEntries)
			).toString(),
			_getExternalReferenceCodesJSONArray(
				objectDefinition1.getName(), file, group.getGroupId()
			).toString(),
			JSONCompareMode.LENIENT);
		JSONAssert.assertEquals(
			JSONUtil.putAll(
			).toString(),
			_getClassExternalReferenceCodesJSONArray(
				file, group.getGroupId()
			).toString(),
			JSONCompareMode.STRICT);

		file = _exportLayouts(
			true, group.getGroupId(), true, new long[0], objectDefinition2);

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				objectEntry.getExternalReferenceCode()
			).toString(),
			_getExternalReferenceCodesJSONArray(
				objectDefinition2.getName(), file, group.getGroupId()
			).toString(),
			JSONCompareMode.LENIENT);
		JSONAssert.assertEquals(
			JSONUtil.putAll(
			).toString(),
			_getClassExternalReferenceCodesJSONArray(
				file, group.getGroupId()
			).toString(),
			JSONCompareMode.STRICT);

		file = _exportLayouts(
			true, group.getGroupId(), false, new long[0], objectDefinition1,
			objectDefinition2);

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				_getExternalReferenceCodes(objectEntries)
			).toString(),
			_getExternalReferenceCodesJSONArray(
				objectDefinition1.getName(), file, group.getGroupId()
			).toString(),
			JSONCompareMode.LENIENT);
		JSONAssert.assertEquals(
			JSONUtil.putAll(
				objectEntry.getExternalReferenceCode()
			).toString(),
			_getExternalReferenceCodesJSONArray(
				objectDefinition2.getName(), file, group.getGroupId()
			).toString(),
			JSONCompareMode.LENIENT);
		JSONAssert.assertEquals(
			JSONUtil.putAll(
			).toString(),
			_getClassExternalReferenceCodesJSONArray(
				file, group.getGroupId()
			).toString(),
			JSONCompareMode.STRICT);
	}

	@Test
	@TestInfo("LPD-65748")
	public void testGetExportModelCount() throws Exception {
		String portletId = RandomTestUtil.randomString();

		Assert.assertNull(
			_portletDataHandlerProvider.provide(
				TestPropsValues.getCompanyId(), portletId));

		try (SafeCloseable safeCloseable1 = _registerServiceWithSafeCloseable(
				Portlet.class,
				new GenericPortlet() {
				},
				MapUtil.singletonDictionary("jakarta.portlet.name", portletId));
			SafeCloseable safeCloseable2 = _registerServiceWithSafeCloseable(
				VulcanBatchEngineTaskItemDelegate.class,
				new TestExportImportVulcanBatchEngineTaskItemDelegate(
					filter -> {
						if (filter != null) {
							return Page.of(Arrays.asList(new TestItem(1)));
						}

						return Page.of(
							Arrays.asList(
								new TestItem(1), new TestItem(2),
								new TestItem(3)));
					},
					portletId),
				HashMapDictionaryBuilder.put(
					"batch.engine.task.item.delegate", "true"
				).put(
					"batch.engine.task.item.delegate.class.name",
					TestItem.class.getName()
				).put(
					"batch.engine.task.item.delegate.name",
					RandomTestUtil.randomString()
				).put(
					"companyId", String.valueOf(TestPropsValues.getCompanyId())
				).put(
					"export.import.vulcan.batch.engine.task.item.delegate",
					"true"
				).build())) {

			// Filter is not null

			Thread.sleep(1000);

			PortletDataHandler portletDataHandler =
				_portletDataHandlerProvider.provide(
					TestPropsValues.getCompanyId(), portletId);

			Assert.assertEquals(
				1,
				portletDataHandler.getExportModelCount(
					_getManifestSummary(
						PortletDataContextFactoryUtil.
							createExportPortletDataContext(
								TestPropsValues.getCompanyId(), 0L,
								new HashMap<>(),
								new Date(System.currentTimeMillis() - 10000),
								new Date(System.currentTimeMillis() - 5000),
								null),
						portletDataHandler)));

			// Filter is null

			Assert.assertEquals(
				3,
				portletDataHandler.getExportModelCount(
					_getManifestSummary(
						PortletDataContextFactoryUtil.
							createExportPortletDataContext(
								TestPropsValues.getCompanyId(), 0L,
								new HashMap<>(), null, null, null),
						portletDataHandler)));
		}
	}

	@Test
	public void testGetExportModelCountWithObjectEntries() throws Exception {
		_testGetExportModelCount(
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			_addObjectDefinition(ObjectDefinitionConstants.SCOPE_COMPANY));
		_testGetExportModelCount(
			TestPropsValues.getGroupId(),
			_addObjectDefinition(ObjectDefinitionConstants.SCOPE_SITE));
	}

	@Test
	@TestInfo("LPD-49421")
	public void testImportIndividualDeletionsCompanyGroup() throws Exception {
		Group group = _stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());

		ObjectDefinition objectDefinition = _addObjectDefinition(
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry[] objectEntries = _addObjectEntries(
			3, GroupConstants.DEFAULT_PARENT_GROUP_ID, objectDefinition);

		File larFile1 = _exportLayouts(
			false, group.getGroupId(), false, new long[0], objectDefinition);

		_deleteObjectEntries(objectEntries[0], objectEntries[1]);

		File larFile2 = _exportLayouts(
			true, group.getGroupId(), false, new long[0], objectDefinition);

		_deleteObjectEntries(objectEntries[2]);

		_importLayouts(
			false, false, larFile1, group.getGroupId(), objectDefinition);

		_assertObjectEntries(
			false, objectDefinition.getObjectDefinitionId(), objectEntries);

		_importLayouts(
			false, false, larFile2, group.getGroupId(), objectDefinition);

		_assertObjectEntries(
			false, objectDefinition.getObjectDefinitionId(), objectEntries);

		_importLayouts(
			true, false, larFile2, group.getGroupId(), objectDefinition);

		_assertObjectEntries(
			false, objectDefinition.getObjectDefinitionId(), objectEntries[2]);
		_assertNull(
			objectDefinition.getObjectDefinitionId(), objectEntries[0],
			objectEntries[1]);
	}

	public static class TestItem implements Serializable {

		public TestItem(long id) {
			this.id = id;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		protected long id;

	}

	private DLFileEntry _addDLFileEntry(String content, long groupId)
		throws Exception {

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), groupId, 0,
			TempFileEntryUtil.getTempFileName(
				RandomTestUtil.randomString() + ".txt"),
			ContentTypes.TEXT_PLAIN, RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			new ByteArrayInputStream(content.getBytes()), 0, null, null, null,
			ServiceContextTestUtil.getServiceContext());

		return _dlFileEntryLocalService.getFileEntry(
			fileEntry.getFileEntryId());
	}

	private ObjectDefinition _addObjectDefinition(String scope)
		throws Exception {

		String objectDefinitionName = ObjectDefinitionTestUtil.getRandomName();

		return ObjectDefinitionTestUtil.publishObjectDefinition(
			objectDefinitionName,
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
					ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
					RandomTestUtil.randomString(),
					_OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA,
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.
								NAME_ACCEPTED_FILE_EXTENSIONS
						).value(
							"txt"
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_FILE_SOURCE
						).value(
							ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
						).value(
							"100"
						).build()),
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
					ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
					RandomTestUtil.randomString(),
					_OBJECT_FIELD_NAME_ATTACHMENT_SHOW_FILES_IN_DOCS_AND_MEDIA,
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.
								NAME_ACCEPTED_FILE_EXTENSIONS
						).value(
							"txt"
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_FILE_SOURCE
						).value(
							ObjectFieldSettingConstants.VALUE_USER_COMPUTER
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.
								NAME_SHOW_FILES_IN_DOCS_AND_MEDIA
						).value(
							Boolean.TRUE.toString()
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.
								NAME_STORAGE_DL_FOLDER_PATH
						).value(
							StringPool.SLASH + objectDefinitionName
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
						).value(
							"100"
						).build()),
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
					ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
					RandomTestUtil.randomString(),
					_OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER,
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.
								NAME_ACCEPTED_FILE_EXTENSIONS
						).value(
							"txt"
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_FILE_SOURCE
						).value(
							ObjectFieldSettingConstants.VALUE_USER_COMPUTER
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
						).value(
							"100"
						).build()),
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME_TEXT,
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_UNIQUE_VALUES
						).value(
							Boolean.TRUE.toString()
						).build()),
					false)),
			scope);
	}

	private ObjectEntry[] _addObjectEntries(
			int count, long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		ObjectEntry[] objectEntries = new ObjectEntry[count];

		for (int i = 0; i < count; i++) {
			objectEntries[i] = _addObjectEntry(
				groupId, objectDefinition, RandomTestUtil.randomString());
		}

		return objectEntries;
	}

	private ObjectEntry _addObjectEntry(
			long groupId, ObjectDefinition objectDefinition,
			Serializable objectFieldValue)
		throws Exception {

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		DLFileEntry dlFileEntry = _addDLFileEntry(
			_OBJECT_FIELD_VALUE_ATTACHMENT_DOCS_AND_MEDIA,
			company.getGroupId());

		FileEntry tempFileEntry1 = _addTempFileEntry(
			objectDefinition,
			_OBJECT_FIELD_VALUE_ATTACHMENT_SHOW_FILES_IN_DOCS_AND_MEDIA);
		FileEntry tempFileEntry2 = _addTempFileEntry(
			objectDefinition, _OBJECT_FIELD_VALUE_ATTACHMENT_USER_COMPUTER);

		return _objectEntryLocalService.addObjectEntry(
			groupId, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA,
				dlFileEntry.getFileEntryId()
			).put(
				_OBJECT_FIELD_NAME_ATTACHMENT_SHOW_FILES_IN_DOCS_AND_MEDIA,
				tempFileEntry1.getFileEntryId()
			).put(
				_OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER,
				tempFileEntry2.getFileEntryId()
			).put(
				_OBJECT_FIELD_NAME_TEXT, objectFieldValue
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private FileEntry _addTempFileEntry(
			ObjectDefinition objectDefinition, String tempFileName)
		throws Exception {

		return TempFileEntryUtil.addTempFileEntry(
			TestPropsValues.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getPortletId(),
			TempFileEntryUtil.getTempFileName(tempFileName + ".txt"),
			FileUtil.createTempFile(tempFileName.getBytes()),
			ContentTypes.TEXT_PLAIN);
	}

	private void _assertNull(
		long objectDefinitionId, ObjectEntry... objectEntries) {

		for (ObjectEntry objectEntry : objectEntries) {
			Assert.assertNull(
				_objectEntryLocalService.fetchObjectEntry(
					objectEntry.getExternalReferenceCode(),
					objectEntry.getGroupId(), objectDefinitionId));
		}
	}

	private void _assertObjectEntries(
			boolean empty, long objectDefinitionId,
			ObjectEntry... objectEntries)
		throws Exception {

		for (ObjectEntry objectEntry : objectEntries) {
			ObjectEntry importedObjectEntry =
				_objectEntryLocalService.getObjectEntry(
					objectEntry.getExternalReferenceCode(),
					objectEntry.getGroupId(), objectDefinitionId);

			if (empty) {
				Assert.assertEquals(
					WorkflowConstants.STATUS_EMPTY,
					importedObjectEntry.getStatus());

				return;
			}

			DLFileEntry dlFileEntry = _dlFileEntryLocalService.getFileEntry(
				MapUtil.getLong(
					importedObjectEntry.getValues(),
					_OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA));

			Assert.assertEquals(
				StringPool.BLANK,
				StringUtil.read(dlFileEntry.getContentStream()));

			dlFileEntry = _dlFileEntryLocalService.getFileEntry(
				MapUtil.getLong(
					importedObjectEntry.getValues(),
					_OBJECT_FIELD_NAME_ATTACHMENT_SHOW_FILES_IN_DOCS_AND_MEDIA));

			Assert.assertEquals(
				StringPool.BLANK,
				StringUtil.read(dlFileEntry.getContentStream()));

			dlFileEntry = _dlFileEntryLocalService.getFileEntry(
				MapUtil.getLong(
					importedObjectEntry.getValues(),
					_OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER));

			Assert.assertEquals(
				_OBJECT_FIELD_VALUE_ATTACHMENT_USER_COMPUTER,
				StringUtil.read(dlFileEntry.getContentStream()));
		}
	}

	private void _deleteObjectEntries(ObjectEntry... objectEntries)
		throws Exception {

		for (ObjectEntry objectEntry : objectEntries) {
			_objectEntryLocalService.deleteObjectEntry(objectEntry);

			long fileEntryId = MapUtil.getLong(
				objectEntry.getValues(),
				_OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA);

			if (fileEntryId != 0) {
				_dlFileEntryLocalService.deleteFileEntry(fileEntryId);
			}

			fileEntryId = MapUtil.getLong(
				objectEntry.getValues(),
				_OBJECT_FIELD_NAME_ATTACHMENT_SHOW_FILES_IN_DOCS_AND_MEDIA);

			if (fileEntryId != 0) {
				_dlFileEntryLocalService.deleteFileEntry(fileEntryId);
			}
		}
	}

	private File _exportLayouts(
			boolean deletions, long groupId,
			boolean includeLayoutSetLayoutsPortlet, boolean privateLayouts,
			long[] layoutIds, ObjectDefinition... objectDefinitions)
		throws Exception {

		return _exportImportLocalService.exportLayoutsAsFile(
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportLayoutSettingsMap(
							TestPropsValues.getUser(), groupId, privateLayouts,
							layoutIds,
							_getExportImportParameterMap(
								deletions, includeLayoutSetLayoutsPortlet,
								Arrays.asList(objectDefinitions)))));
	}

	private File _exportLayouts(
			boolean deletions, long groupId, boolean privateLayouts,
			long[] layoutIds, ObjectDefinition... objectDefinitions)
		throws Exception {

		return _exportLayouts(
			deletions, groupId, false, privateLayouts, layoutIds,
			objectDefinitions);
	}

	private String _getBatchFileNameWithPath(String fileName, long groupId) {
		return StringBundler.concat(
			"group/", groupId, StringPool.FORWARD_SLASH, fileName);
	}

	private JSONArray _getClassExternalReferenceCodesJSONArray(
			File file, long groupId)
		throws Exception {

		try (ZipFile zipFile = new ZipFile(file)) {
			ZipEntry zipEntry = zipFile.getEntry(
				_getBatchFileNameWithPath(
					"deletion-system-events.xml", groupId));

			if (zipEntry == null) {
				throw new FileNotFoundException();
			}

			Document document = _saxReader.read(
				zipFile.getInputStream(zipEntry));

			Element rootElement = document.getRootElement();

			return JSONUtil.toJSONArray(
				rootElement.elements("deletion-system-event"),
				deletionSystemEventElement ->
					deletionSystemEventElement.attributeValue(
						"class-external-reference-code"),
				exception -> {
					throw new RuntimeException(exception);
				});
		}
	}

	private JSONArray _getExportedObjectEntriesJSONArray(
			String className, File file, long groupId)
		throws Exception {

		try (ZipFile zipFile = new ZipFile(file)) {
			ZipEntry zipEntry = zipFile.getEntry(
				_getBatchFileNameWithPath(className + ".json", groupId));

			return JSONFactoryUtil.createJSONArray(
				StringUtil.read(zipFile.getInputStream(zipEntry)));
		}
	}

	private Map<String, String[]> _getExportImportParameterMap(
		boolean deletions, boolean includeLayoutSetLayoutsPortlet,
		List<ObjectDefinition> objectDefinitions) {

		Map<String, String[]> parameterMap = HashMapBuilder.put(
			PortletDataHandlerKeys.DELETIONS,
			new String[] {Boolean.toString(deletions)}
		).put(
			PortletDataHandlerKeys.PERMISSIONS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.PERMISSIONS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_SETUP_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			"PORTLET_DATA_com_liferay_layout_admin_web_portlet_" +
				"LayoutSetLayoutsPortlet",
			() -> {
				if (includeLayoutSetLayoutsPortlet) {
					return new String[] {Boolean.TRUE.toString()};
				}

				return null;
			}
		).build();

		objectDefinitions.forEach(
			objectDefinition -> parameterMap.put(
				PortletDataHandlerKeys.PORTLET_DATA + "_" +
					objectDefinition.getPortletId(),
				new String[] {Boolean.TRUE.toString()}));

		return parameterMap;
	}

	private String[] _getExternalReferenceCodes(ObjectEntry... objectEntries) {
		String[] externalReferenceCodes = new String[objectEntries.length];

		for (int i = 0; i < objectEntries.length; i++) {
			externalReferenceCodes[i] =
				objectEntries[i].getExternalReferenceCode();
		}

		return externalReferenceCodes;
	}

	private JSONArray _getExternalReferenceCodesJSONArray(
			String className, File file, long groupId)
		throws Exception {

		try (ZipFile zipFile = new ZipFile(file)) {
			ZipEntry zipEntry = zipFile.getEntry(
				_getBatchFileNameWithPath(
					className + "_deletions.json", groupId));

			if (zipEntry == null) {
				throw new FileNotFoundException();
			}

			JSONArray jsonArray1 = JSONFactoryUtil.createJSONArray();

			JSONArray jsonArray2 = JSONFactoryUtil.createJSONArray(
				StringUtil.read(zipFile.getInputStream(zipEntry)));

			for (int i = 0; i < jsonArray2.length(); i++) {
				JSONObject jsonObject = jsonArray2.getJSONObject(i);

				jsonArray1.put(jsonObject.getString("externalReferenceCode"));
			}

			return jsonArray1;
		}
	}

	private LogCapture _getLogCapture(boolean expectError) {
		LogCapture logCapture = null;

		if (expectError) {
			logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal.strategy." +
					"OnErrorContinueBatchEngineImportStrategy",
				LoggerTestUtil.ERROR);
		}

		return logCapture;
	}

	private ManifestSummary _getManifestSummary(
			PortletDataContext portletDataContext,
			PortletDataHandler portletDataHandler)
		throws Exception {

		portletDataContext.setManifestSummary(new ManifestSummary());

		portletDataHandler.prepareManifestSummary(portletDataContext);

		return portletDataContext.getManifestSummary();
	}

	private long _getObjectEntryGroupId(long groupId, String scope) {
		if (Objects.equals(ObjectDefinitionConstants.SCOPE_COMPANY, scope)) {
			return GroupConstants.DEFAULT_PARENT_GROUP_ID;
		}

		return groupId;
	}

	private String _getObjectEntryScopeKey(Group group, String scope) {
		if (Objects.equals(ObjectDefinitionConstants.SCOPE_COMPANY, scope)) {
			return null;
		}

		return group.getGroupKey();
	}

	private ExportImportConfiguration _importLayouts(
			boolean deletions, boolean expectError, File file, long groupId,
			boolean includeLayoutSetLayoutsPortlet, boolean privateLayout,
			ObjectDefinition... objectDefinitions)
		throws Exception {

		try (LogCapture logCapture = _getLogCapture(expectError)) {
			ExportImportConfiguration exportImportConfiguration =
				_exportImportConfigurationLocalService.
					addDraftExportImportConfiguration(
						TestPropsValues.getUserId(),
						ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
						ExportImportConfigurationSettingsMapFactoryUtil.
							buildImportLayoutSettingsMap(
								TestPropsValues.getUser(), groupId,
								privateLayout, null,
								_getExportImportParameterMap(
									deletions, includeLayoutSetLayoutsPortlet,
									Arrays.asList(objectDefinitions))));

			if (deletions) {
				_exportImportLocalService.importLayoutsDataDeletions(
					exportImportConfiguration, file);
			}

			_exportImportLocalService.importLayouts(
				exportImportConfiguration, file);

			return exportImportConfiguration;
		}
	}

	private ExportImportConfiguration _importLayouts(
			boolean deletions, boolean expectError, File file, long groupId,
			ObjectDefinition... objectDefinitions)
		throws Exception {

		return _importLayouts(
			deletions, expectError, file, groupId, false, false,
			objectDefinitions);
	}

	private <S> SafeCloseable _registerServiceWithSafeCloseable(
		Class<S> clazz, S service, Dictionary<String, ?> properties) {

		Bundle bundle = FrameworkUtil.getBundle(
			BatchEnginePortletDataHandlerTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<S> serviceRegistration =
			bundleContext.registerService(clazz, service, properties);

		return serviceRegistration::unregister;
	}

	private void _testExportImportObjectEntriesToSameGroup(
			Group group, String scope)
		throws Exception {

		ObjectDefinition objectDefinition = _addObjectDefinition(scope);

		ObjectEntry[] objectEntries = _addObjectEntries(
			3, _getObjectEntryGroupId(group.getGroupId(), scope),
			objectDefinition);

		File larFile = _exportLayouts(
			false, group.getGroupId(), false, new long[0], objectDefinition);

		_deleteObjectEntries(objectEntries);

		_importLayouts(
			false, false, larFile, group.getGroupId(), objectDefinition);

		_assertObjectEntries(
			false, objectDefinition.getObjectDefinitionId(), objectEntries);
	}

	private void _testExportImportObjectEntriesWithErrorReport(
			Group group, String scope)
		throws Exception {

		ObjectDefinition objectDefinition = _addObjectDefinition(scope);

		ObjectEntry objectEntry = _addObjectEntry(
			_getObjectEntryGroupId(group.getGroupId(), scope), objectDefinition,
			StringUtil.randomString());

		String originalExternalReferenceCode =
			objectEntry.getExternalReferenceCode();

		File file = _exportLayouts(
			false, group.getGroupId(), false, new long[0], objectDefinition);

		objectEntry.setExternalReferenceCode(StringUtil.randomString());

		objectEntry = _objectEntryLocalService.updateObjectEntry(objectEntry);

		ExportImportConfiguration exportImportConfiguration = _importLayouts(
			false, true, file, group.getGroupId(), objectDefinition);

		List<ExportImportReportEntry> exportImportReportEntries =
			_exportImportReportEntryLocalService.getExportImportReportEntries(
				TestPropsValues.getCompanyId(),
				exportImportConfiguration.getExportImportConfigurationId());

		Assert.assertEquals(
			exportImportReportEntries.toString(), 1,
			exportImportReportEntries.size());

		ExportImportReportEntry exportImportReportEntry =
			exportImportReportEntries.get(0);

		Assert.assertEquals(
			originalExternalReferenceCode,
			exportImportReportEntry.getClassExternalReferenceCode());
		Assert.assertEquals(
			_portal.getClassNameId(objectDefinition.getClassName()),
			exportImportReportEntry.getClassNameId());
		Assert.assertEquals(
			objectEntry.getPrimaryKey(), exportImportReportEntry.getClassPK());
		Assert.assertEquals(
			objectEntry.getGroupId(), exportImportReportEntry.getGroupId());
		Assert.assertEquals(
			objectDefinition.getShortName(),
			exportImportReportEntry.getModelName());
		Assert.assertEquals(scope, exportImportReportEntry.getScope());
		Assert.assertEquals(
			ExportImportReportEntryConstants.TYPE_ERROR,
			exportImportReportEntry.getType());
	}

	private void _testExportImportObjectEntriesWithRelatedObjectEntries(
			boolean childFirst, Group group, String scope, String type)
		throws Exception {

		ObjectDefinition objectDefinition1 = _addObjectDefinition(scope);

		ObjectEntry[] objectEntries1 = _addObjectEntries(
			3, _getObjectEntryGroupId(group.getGroupId(), scope),
			objectDefinition1);

		ObjectDefinition objectDefinition2 = _addObjectDefinition(scope);

		ObjectEntry[] objectEntries2 = _addObjectEntries(
			3, _getObjectEntryGroupId(group.getGroupId(), scope),
			objectDefinition2);

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, objectDefinition1,
				objectDefinition2,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
				StringUtil.randomId(), type);

		for (int i = 0; i < objectEntries1.length; i++) {
			ObjectRelationshipTestUtil.relateObjectEntries(
				objectEntries1[i].getPrimaryKey(),
				objectEntries2[i].getPrimaryKey(), objectRelationship,
				TestPropsValues.getUserId());
		}

		File larFile = _exportLayouts(
			false, group.getGroupId(), false, new long[0], objectDefinition1,
			objectDefinition2);

		JSONArray exportedObjectEntriesJSONArray =
			_getExportedObjectEntriesJSONArray(
				objectDefinition2.getName(), larFile, group.getGroupId());

		if (Objects.equals(
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY, type)) {

			for (int i = 0; i < objectEntries1.length; i++) {
				ObjectEntry objectEntry = objectEntries1[i];

				JSONAssert.assertEquals(
					JSONUtil.put(
						_toSimplifiedObjectEntryJSONObject(
							group, objectEntry, scope)
					).toString(),
					exportedObjectEntriesJSONArray.getJSONObject(
						i
					).getJSONArray(
						objectRelationship.getName()
					).toString(),
					JSONCompareMode.STRICT);
			}

			exportedObjectEntriesJSONArray = _getExportedObjectEntriesJSONArray(
				objectDefinition1.getName(), larFile, group.getGroupId());

			for (int i = 0; i < objectEntries2.length; i++) {
				ObjectEntry objectEntry = objectEntries2[i];

				JSONAssert.assertEquals(
					JSONUtil.put(
						_toSimplifiedObjectEntryJSONObject(
							group, objectEntry, scope)
					).toString(),
					exportedObjectEntriesJSONArray.getJSONObject(
						i
					).getJSONArray(
						objectRelationship.getName()
					).toString(),
					JSONCompareMode.STRICT);
			}
		}
		else {
			for (int i = 0; i < objectEntries1.length; i++) {
				ObjectEntry objectEntry = objectEntries1[i];

				JSONAssert.assertEquals(
					_toSimplifiedObjectEntryJSONObject(
						group, objectEntry, scope
					).toString(),
					exportedObjectEntriesJSONArray.getJSONObject(
						i
					).getJSONObject(
						objectRelationship.getName()
					).toString(),
					JSONCompareMode.STRICT);
			}
		}

		_deleteObjectEntries(objectEntries1);
		_deleteObjectEntries(objectEntries2);

		if (childFirst) {
			_importLayouts(
				false, false, larFile, group.getGroupId(), objectDefinition2);

			_assertObjectEntries(
				true, objectDefinition1.getObjectDefinitionId(),
				objectEntries1);
			_assertObjectEntries(
				false, objectDefinition2.getObjectDefinitionId(),
				objectEntries2);

			_importLayouts(
				false, false, larFile, group.getGroupId(), objectDefinition1);

			_assertObjectEntries(
				false, objectDefinition1.getObjectDefinitionId(),
				objectEntries1);
		}
		else {
			_importLayouts(
				false, false, larFile, group.getGroupId(), objectDefinition1);

			_assertObjectEntries(
				false, objectDefinition1.getObjectDefinitionId(),
				objectEntries1);

			if (Objects.equals(
					ObjectRelationshipConstants.TYPE_MANY_TO_MANY, type)) {

				_assertObjectEntries(
					true, objectDefinition2.getObjectDefinitionId(),
					objectEntries2);
			}
			else if (Objects.equals(
						ObjectRelationshipConstants.TYPE_ONE_TO_MANY, type)) {

				for (ObjectEntry objectEntry : objectEntries2) {
					AssertUtils.assertFailure(
						NoSuchObjectEntryException.class,
						String.format(
							"No ObjectEntry exists with the key {" +
								"externalReferenceCode=%s, groupId=%s, " +
									"companyId=%s, objectDefinitionId=%s}",
							objectEntry.getExternalReferenceCode(),
							objectEntry.getGroupId(),
							objectEntry.getCompanyId(),
							objectDefinition2.getObjectDefinitionId()),
						() -> _objectEntryLocalService.getObjectEntry(
							objectEntry.getExternalReferenceCode(),
							objectEntry.getGroupId(),
							objectDefinition2.getObjectDefinitionId()));
				}
			}

			_importLayouts(
				false, false, larFile, group.getGroupId(), objectDefinition2);

			_assertObjectEntries(
				false, objectDefinition2.getObjectDefinitionId(),
				objectEntries2);
		}
	}

	private void _testExportImportObjectEntriesWithRelatedObjectEntries(
			Group group, String scope, String type)
		throws Exception {

		_testExportImportObjectEntriesWithRelatedObjectEntries(
			false, group, scope, type);
		_testExportImportObjectEntriesWithRelatedObjectEntries(
			true, group, scope, type);
	}

	private void _testGetExportModelCount(
			long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		PortletDataHandler portletDataHandler =
			_portletDataHandlerProvider.provide(
				objectDefinition.getPortletId());

		Assert.assertEquals(
			0,
			portletDataHandler.getExportModelCount(
				_getManifestSummary(
					PortletDataContextFactoryUtil.
						createExportPortletDataContext(
							objectDefinition.getCompanyId(), groupId,
							new HashMap<>(), null, null, null),
					portletDataHandler)));

		ObjectEntry[] siteScopedObjectEntries = _addObjectEntries(
			3, groupId, objectDefinition);

		Assert.assertEquals(
			siteScopedObjectEntries.length,
			portletDataHandler.getExportModelCount(
				_getManifestSummary(
					PortletDataContextFactoryUtil.
						createExportPortletDataContext(
							objectDefinition.getCompanyId(), groupId,
							new HashMap<>(), null, null, null),
					portletDataHandler)));

		Assert.assertEquals(
			0,
			portletDataHandler.getExportModelCount(
				_getManifestSummary(
					PortletDataContextFactoryUtil.
						createExportPortletDataContext(
							objectDefinition.getCompanyId(), groupId,
							new HashMap<>(),
							new Date(System.currentTimeMillis() - 10000),
							new Date(System.currentTimeMillis() - 5000), null),
					portletDataHandler)));
	}

	/**
	 * @see com.liferay.object.rest.internal.dto.v1_0.converter.ObjectEntryDTOConverter#_toSimplifiedObjectEntry(
	 *      com.liferay.object.rest.dto.v1_0.ObjectEntry, ObjectDefinition,
	 *      ObjectEntryVersion, ObjectEntry)
	 */
	private JSONObject _toSimplifiedObjectEntryJSONObject(
		Group group, ObjectEntry objectEntry, String scope) {

		return JSONUtil.put(
			"externalReferenceCode", objectEntry.getExternalReferenceCode()
		).put(
			"scopeId", (Long)_getObjectEntryGroupId(group.getGroupId(), scope)
		).put(
			"scopeKey", _getObjectEntryScopeKey(group, scope)
		);
	}

	private static final String _OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA =
		"x" + RandomTestUtil.randomString();

	private static final String
		_OBJECT_FIELD_NAME_ATTACHMENT_SHOW_FILES_IN_DOCS_AND_MEDIA =
			"x" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER =
		"x" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_NAME_TEXT =
		"x" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_VALUE_ATTACHMENT_DOCS_AND_MEDIA =
		RandomTestUtil.randomString();

	private static final String
		_OBJECT_FIELD_VALUE_ATTACHMENT_SHOW_FILES_IN_DOCS_AND_MEDIA =
			RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_VALUE_ATTACHMENT_USER_COMPUTER =
		RandomTestUtil.randomString();

	@Inject
	private BatchEngineImportTaskLocalService
		_batchEngineImportTaskLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private PortletDataHandlerProvider _portletDataHandlerProvider;

	@Inject
	private SAXReader _saxReader;

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

	@Inject
	private StagingLocalService _stagingLocalService;

	private static class TestExportImportVulcanBatchEngineTaskItemDelegate
		implements EntityModelResource,
				   ExportImportVulcanBatchEngineTaskItemDelegate<TestItem>,
				   VulcanBatchEngineTaskItemDelegate<TestItem> {

		public TestExportImportVulcanBatchEngineTaskItemDelegate(
			Function<Filter, Page<TestItem>> function, String portletId) {

			_function = function;
			_portletId = portletId;
		}

		@Override
		public void create(
			Collection<TestItem> items, Map<String, Serializable> parameters) {
		}

		@Override
		public void delete(
			Collection<TestItem> items, Map<String, Serializable> parameters) {
		}

		@Override
		public EntityModel getEntityModel(
			Map<String, List<String>> multivaluedMap) {

			return _getEntityModel();
		}

		@Override
		public EntityModel getEntityModel(MultivaluedMap<?, ?> multivaluedMap)
			throws Exception {

			return _getEntityModel();
		}

		@Override
		public ExportImportDescriptor getExportImportDescriptor() {
			return new ExportImportDescriptor() {

				@Override
				public String getItemClassName() {
					return _itemClassName;
				}

				@Override
				public String getPortletId() {
					return _portletId;
				}

				@Override
				public Scope getScope() {
					return Scope.COMPANY;
				}

			};
		}

		@Override
		public Page<TestItem> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search) {

			return _function.apply(filter);
		}

		@Override
		public void setContextBatchUnsafeBiConsumer(
			UnsafeBiConsumer
				<Collection<TestItem>,
				 UnsafeFunction<TestItem, TestItem, Exception>, Exception>
					contextBatchUnsafeBiConsumer) {
		}

		@Override
		public void setContextCompany(Company contextCompany) {
		}

		@Override
		public void setContextUriInfo(UriInfo uriInfo) {
		}

		@Override
		public void setContextUser(User contextUser) {
		}

		@Override
		public void setGroupLocalService(GroupLocalService groupLocalService) {
		}

		@Override
		public void setLanguageId(String languageId) {
		}

		@Override
		public void setResourceActionLocalService(
			ResourceActionLocalService resourceActionLocalService) {
		}

		@Override
		public void setResourcePermissionLocalService(
			ResourcePermissionLocalService resourcePermissionLocalService) {
		}

		@Override
		public void setRoleLocalService(RoleLocalService roleLocalService) {
		}

		@Override
		public void update(
			Collection<TestItem> testItems,
			Map<String, Serializable> parameters) {
		}

		private EntityModel _getEntityModel() {
			return new EntityModel() {

				@Override
				public Map<String, EntityField> getEntityFieldsMap() {
					return HashMapBuilder.<String, EntityField>put(
						"dateModified",
						new DateTimeEntityField(
							"dateModified", locale -> "dateModified",
							locale -> "dateModified")
					).build();
				}

				@Override
				public String getName() {
					return "TestEntityModel";
				}

			};
		}

		private static String _itemClassName = RandomTestUtil.randomString();

		private final Function<Filter, Page<TestItem>> _function;
		private final String _portletId;

	}

}