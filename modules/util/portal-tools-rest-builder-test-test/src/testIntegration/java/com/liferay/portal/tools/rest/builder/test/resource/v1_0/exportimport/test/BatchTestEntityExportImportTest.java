/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.action.ImportTaskPreAction;
import com.liferay.batch.engine.context.ImportTaskContext;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.tools.rest.builder.test.client.custom.field.CustomField;
import com.liferay.portal.tools.rest.builder.test.client.custom.field.CustomValue;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.BatchTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.CompanyTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.http.HttpInvoker;
import com.liferay.portal.tools.rest.builder.test.client.pagination.Page;
import com.liferay.portal.tools.rest.builder.test.client.resource.v1_0.BatchTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.client.resource.v1_0.CompanyTestEntityResource;
import com.liferay.staging.StagingGroupHelper;

import java.io.File;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alejandro Tardín
 */
@FeatureFlag("LPD-35914")
@RunWith(Arquillian.class)
public class BatchTestEntityExportImportTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
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

	@Before
	public void setUp() throws Exception {
		Group testGroup = GroupTestUtil.addGroup();

		Company testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		User testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		_batchTestEntityResource = BatchTestEntityResource.builder(
		).authentication(
			testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields",
			"customFields.attributeType,nestedField,relatedCompanyTestEntity"
		).build();
		_companyTestEntityResource = CompanyTestEntityResource.builder(
		).authentication(
			testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	public void tearDown() throws Exception {
		Page<BatchTestEntity> batchTestEntitiesPage =
			_batchTestEntityResource.getBatchTestEntitiesPage();

		for (BatchTestEntity batchTestEntity :
				batchTestEntitiesPage.getItems()) {

			_batchTestEntityResource.
				deleteBatchTestEntityByExternalReferenceCode(
					batchTestEntity.getExternalReferenceCode());
		}
	}

	@Test
	public void testExportImport() throws Exception {
		Page<BatchTestEntity> batchTestEntitiesPage =
			_batchTestEntityResource.getBatchTestEntitiesPage();

		long totalCount = batchTestEntitiesPage.getTotalCount();

		BatchTestEntity batchTestEntity1 =
			_batchTestEntityResource.postBatchTestEntity(
				new BatchTestEntity() {
					{
						customFields = new CustomField[] {
							new CustomField() {
								{
									attributeType = AttributeType.STRING;
									customValue = new CustomValue() {
										{
											data =
												RandomTestUtil.randomString();
										}
									};
									dataType = "Text";
									name = RandomTestUtil.randomString();
								}
							}
						};
						externalReferenceCode = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						id = RandomTestUtil.randomLong();
						name = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						nestedField = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
					}
				});
		BatchTestEntity batchTestEntity2 =
			_batchTestEntityResource.postBatchTestEntity(
				new BatchTestEntity() {
					{
						customFields = new CustomField[] {
							new CustomField() {
								{
									attributeType = AttributeType.INTEGER;
									customValue = new CustomValue() {
										{
											data = RandomTestUtil.randomInt();
										}
									};
									dataType = "Integer";
									name = RandomTestUtil.randomString();
								}
							}
						};
						externalReferenceCode = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						id = RandomTestUtil.randomLong();
						name = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						nestedField = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
					}
				});

		batchTestEntitiesPage =
			_batchTestEntityResource.getBatchTestEntitiesPage();

		Assert.assertEquals(
			totalCount + 2, batchTestEntitiesPage.getTotalCount());

		Group group = _stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());

		File larFile = _exportImportLocalService.exportLayoutsAsFile(
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportLayoutSettingsMap(
							TestPropsValues.getUser(), group.getGroupId(),
							false, new long[0],
							HashMapBuilder.put(
								PortletDataHandlerKeys.PORTLET_DATA,
								new String[] {Boolean.TRUE.toString()}
							).put(
								PortletDataHandlerKeys.PORTLET_DATA + "_" +
									"com_liferay_portal_tools_rest_builder_" +
										"test_portlet_BatchTestEntityPortlet",
								new String[] {Boolean.TRUE.toString()}
							).build())));

		_batchTestEntityResource.deleteBatchTestEntityByExternalReferenceCode(
			batchTestEntity1.getExternalReferenceCode());
		_batchTestEntityResource.deleteBatchTestEntityByExternalReferenceCode(
			batchTestEntity2.getExternalReferenceCode());

		batchTestEntitiesPage =
			_batchTestEntityResource.getBatchTestEntitiesPage();

		Assert.assertEquals(totalCount, batchTestEntitiesPage.getTotalCount());

		_exportImportLocalService.importLayouts(
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportLayoutSettingsMap(
							TestPropsValues.getUser(), group.getGroupId(),
							false, new long[0],
							HashMapBuilder.put(
								PortletDataHandlerKeys.PORTLET_DATA,
								new String[] {Boolean.TRUE.toString()}
							).put(
								PortletDataHandlerKeys.PORTLET_DATA + "_" +
									"com_liferay_portal_tools_rest_builder_" +
										"test_portlet_BatchTestEntityPortlet",
								new String[] {Boolean.TRUE.toString()}
							).build())),
			larFile);

		batchTestEntitiesPage =
			_batchTestEntityResource.getBatchTestEntitiesPage();

		Assert.assertEquals(
			totalCount + 2, batchTestEntitiesPage.getTotalCount());

		_assertEquals(
			batchTestEntity1,
			_batchTestEntityResource.getBatchTestEntityByExternalReferenceCode(
				batchTestEntity1.getExternalReferenceCode()));
		_assertEquals(
			batchTestEntity2,
			_batchTestEntityResource.getBatchTestEntityByExternalReferenceCode(
				batchTestEntity2.getExternalReferenceCode()));
	}

	@Test
	@TestInfo("LPD-49899")
	public void testExportImportEmptyRelatedEntity() throws Exception {
		Page<BatchTestEntity> batchTestEntitiesPage =
			_batchTestEntityResource.getBatchTestEntitiesPage();

		long totalCount = batchTestEntitiesPage.getTotalCount();

		String externalReferenceCode1 = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		CompanyTestEntity companyTestEntity1 =
			_companyTestEntityResource.postCompanyTestEntity(
				new CompanyTestEntity() {
					{
						externalReferenceCode = externalReferenceCode1;
					}
				});

		BatchTestEntity batchTestEntity1 =
			_batchTestEntityResource.postBatchTestEntity(
				new BatchTestEntity() {
					{
						externalReferenceCode = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						name = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						nestedField = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						relatedCompanyTestEntity = companyTestEntity1;
					}
				});

		String externalReferenceCode2 = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		CompanyTestEntity companyTestEntity2 =
			_companyTestEntityResource.postCompanyTestEntity(
				new CompanyTestEntity() {
					{
						externalReferenceCode = externalReferenceCode2;
					}
				});

		BatchTestEntity batchTestEntity2 =
			_batchTestEntityResource.postBatchTestEntity(
				new BatchTestEntity() {
					{
						externalReferenceCode = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						name = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						nestedField = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						relatedCompanyTestEntity = companyTestEntity2;
					}
				});

		batchTestEntitiesPage =
			_batchTestEntityResource.getBatchTestEntitiesPage();

		Assert.assertEquals(
			totalCount + 2, batchTestEntitiesPage.getTotalCount());

		Group group = _stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());

		File larFile = _exportImportLocalService.exportLayoutsAsFile(
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportLayoutSettingsMap(
							TestPropsValues.getUser(), group.getGroupId(),
							false, new long[0],
							HashMapBuilder.put(
								PortletDataHandlerKeys.PORTLET_DATA,
								new String[] {Boolean.TRUE.toString()}
							).put(
								PortletDataHandlerKeys.PORTLET_DATA + "_" +
									"com_liferay_portal_tools_rest_builder_" +
										"test_portlet_BatchTestEntityPortlet",
								new String[] {Boolean.TRUE.toString()}
							).build())));

		_batchTestEntityResource.deleteBatchTestEntityByExternalReferenceCode(
			batchTestEntity1.getExternalReferenceCode());
		_batchTestEntityResource.deleteBatchTestEntityByExternalReferenceCode(
			batchTestEntity2.getExternalReferenceCode());
		_companyTestEntityResource.
			deleteCompanyTestEntityByExternalReferenceCode(
				companyTestEntity1.getExternalReferenceCode());
		_companyTestEntityResource.
			deleteCompanyTestEntityByExternalReferenceCode(
				companyTestEntity2.getExternalReferenceCode());

		batchTestEntitiesPage =
			_batchTestEntityResource.getBatchTestEntitiesPage();

		Assert.assertEquals(totalCount, batchTestEntitiesPage.getTotalCount());

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportLayoutSettingsMap(
							TestPropsValues.getUser(), group.getGroupId(),
							false, new long[0],
							HashMapBuilder.put(
								PortletDataHandlerKeys.PORTLET_DATA,
								new String[] {Boolean.TRUE.toString()}
							).put(
								PortletDataHandlerKeys.PORTLET_DATA + "_" +
									"com_liferay_portal_tools_rest_builder_" +
										"test_portlet_BatchTestEntityPortlet",
								new String[] {Boolean.TRUE.toString()}
							).build()));

		_exportImportLocalService.importLayouts(
			exportImportConfiguration, larFile);

		batchTestEntitiesPage =
			_batchTestEntityResource.getBatchTestEntitiesPage();

		Assert.assertEquals(
			totalCount + 2, batchTestEntitiesPage.getTotalCount());

		_assertEquals(
			batchTestEntity1,
			_batchTestEntityResource.getBatchTestEntityByExternalReferenceCode(
				batchTestEntity1.getExternalReferenceCode()));
		_assertEquals(
			batchTestEntity2,
			_batchTestEntityResource.getBatchTestEntityByExternalReferenceCode(
				batchTestEntity2.getExternalReferenceCode()));

		List<ExportImportReportEntry> exportImportReportEntries =
			_exportImportReportEntryLocalService.getExportImportReportEntries(
				TestPropsValues.getCompanyId(),
				exportImportConfiguration.getExportImportConfigurationId());

		Assert.assertEquals(
			exportImportReportEntries.toString(), 2,
			exportImportReportEntries.size());

		_assertEquals(
			com.liferay.portal.tools.rest.builder.test.dto.v1_0.
				CompanyTestEntity.class,
			null, externalReferenceCode1,
			ExportImportReportEntryConstants.TYPE_EMPTY,
			exportImportReportEntries.get(0));
		_assertEquals(
			com.liferay.portal.tools.rest.builder.test.dto.v1_0.
				CompanyTestEntity.class,
			null, externalReferenceCode2,
			ExportImportReportEntryConstants.TYPE_EMPTY,
			exportImportReportEntries.get(1));
	}

	@Test
	@TestInfo("LPD-49899")
	public void testExportImportErrorRelatedEntity() throws Exception {
		Page<BatchTestEntity> batchTestEntitiesPage =
			_batchTestEntityResource.getBatchTestEntitiesPage();

		long totalCount = batchTestEntitiesPage.getTotalCount();

		String externalReferenceCode1 = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		CompanyTestEntity companyTestEntity1 =
			_companyTestEntityResource.postCompanyTestEntity(
				new CompanyTestEntity() {
					{
						externalReferenceCode = externalReferenceCode1;
					}
				});

		BatchTestEntity batchTestEntity1 =
			_batchTestEntityResource.postBatchTestEntity(
				new BatchTestEntity() {
					{
						externalReferenceCode = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						name = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						nestedField = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						relatedCompanyTestEntity = companyTestEntity1;
					}
				});

		CompanyTestEntity companyTestEntity2 =
			_companyTestEntityResource.postCompanyTestEntity(
				new CompanyTestEntity() {
					{
						externalReferenceCode = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
					}
				});

		String externalReferenceCode2 = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		BatchTestEntity batchTestEntity2 =
			_batchTestEntityResource.postBatchTestEntity(
				new BatchTestEntity() {
					{
						externalReferenceCode = externalReferenceCode2;
						name = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						nestedField = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						relatedCompanyTestEntity = companyTestEntity2;
					}
				});

		batchTestEntitiesPage =
			_batchTestEntityResource.getBatchTestEntitiesPage();

		Assert.assertEquals(
			totalCount + 2, batchTestEntitiesPage.getTotalCount());

		Group group = _stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());

		File larFile = _exportImportLocalService.exportLayoutsAsFile(
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportLayoutSettingsMap(
							TestPropsValues.getUser(), group.getGroupId(),
							false, new long[0],
							HashMapBuilder.put(
								PortletDataHandlerKeys.PORTLET_DATA,
								new String[] {Boolean.TRUE.toString()}
							).put(
								PortletDataHandlerKeys.PORTLET_DATA + "_" +
									"com_liferay_portal_tools_rest_builder_" +
										"test_portlet_BatchTestEntityPortlet",
								new String[] {Boolean.TRUE.toString()}
							).build())));

		_batchTestEntityResource.deleteBatchTestEntityByExternalReferenceCode(
			batchTestEntity1.getExternalReferenceCode());
		_batchTestEntityResource.deleteBatchTestEntityByExternalReferenceCode(
			batchTestEntity2.getExternalReferenceCode());
		_companyTestEntityResource.
			deleteCompanyTestEntityByExternalReferenceCode(
				companyTestEntity1.getExternalReferenceCode());
		_companyTestEntityResource.
			deleteCompanyTestEntityByExternalReferenceCode(
				companyTestEntity2.getExternalReferenceCode());

		batchTestEntitiesPage =
			_batchTestEntityResource.getBatchTestEntitiesPage();

		Assert.assertEquals(totalCount, batchTestEntitiesPage.getTotalCount());

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportLayoutSettingsMap(
							TestPropsValues.getUser(), group.getGroupId(),
							false, new long[0],
							HashMapBuilder.put(
								PortletDataHandlerKeys.PORTLET_DATA,
								new String[] {Boolean.TRUE.toString()}
							).put(
								PortletDataHandlerKeys.PORTLET_DATA + "_" +
									"com_liferay_portal_tools_rest_builder_" +
										"test_portlet_BatchTestEntityPortlet",
								new String[] {Boolean.TRUE.toString()}
							).build()));

		Bundle bundle = FrameworkUtil.getBundle(
			BatchTestEntityExportImportTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		String errorMessage = RandomTestUtil.randomString();

		ServiceRegistration<ImportTaskPreAction> serviceRegistration =
			bundleContext.registerService(
				ImportTaskPreAction.class,
				new TestImportTaskPreAction(
					errorMessage, externalReferenceCode2),
				null);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal.strategy." +
					"OnErrorContinueBatchEngineImportStrategy",
				LoggerTestUtil.ERROR)) {

			_exportImportLocalService.importLayouts(
				exportImportConfiguration, larFile);
		}
		finally {
			serviceRegistration.unregister();
		}

		batchTestEntitiesPage =
			_batchTestEntityResource.getBatchTestEntitiesPage();

		Assert.assertEquals(
			totalCount + 1, batchTestEntitiesPage.getTotalCount());

		_assertEquals(
			batchTestEntity1,
			_batchTestEntityResource.getBatchTestEntityByExternalReferenceCode(
				batchTestEntity1.getExternalReferenceCode()));

		HttpInvoker.HttpResponse httpResponse =
			_batchTestEntityResource.
				getBatchTestEntityByExternalReferenceCodeHttpResponse(
					batchTestEntity2.getExternalReferenceCode());

		Assert.assertEquals(404, httpResponse.getStatusCode());

		List<ExportImportReportEntry> exportImportReportEntries =
			_exportImportReportEntryLocalService.getExportImportReportEntries(
				TestPropsValues.getCompanyId(),
				exportImportConfiguration.getExportImportConfigurationId());

		Assert.assertEquals(
			exportImportReportEntries.toString(), 2,
			exportImportReportEntries.size());

		_assertEquals(
			com.liferay.portal.tools.rest.builder.test.dto.v1_0.
				CompanyTestEntity.class,
			null, externalReferenceCode1,
			ExportImportReportEntryConstants.TYPE_EMPTY,
			exportImportReportEntries.get(0));
		_assertEquals(
			com.liferay.portal.tools.rest.builder.test.dto.v1_0.BatchTestEntity.
				class,
			errorMessage, externalReferenceCode2,
			ExportImportReportEntryConstants.TYPE_ERROR,
			exportImportReportEntries.get(1));
	}

	private void _assertEquals(
		BatchTestEntity batchTestEntity1, BatchTestEntity batchTestEntity2) {

		Assert.assertEquals(
			batchTestEntity1.getCustomFields(),
			batchTestEntity2.getCustomFields());
		Assert.assertEquals(
			batchTestEntity1.getExternalReferenceCode(),
			batchTestEntity2.getExternalReferenceCode());
		Assert.assertEquals(
			batchTestEntity1.getName(), batchTestEntity2.getName());
		Assert.assertEquals(
			batchTestEntity1.getNestedField(),
			batchTestEntity2.getNestedField());

		CompanyTestEntity relatedCompanyTestEntity1 =
			batchTestEntity1.getRelatedCompanyTestEntity();
		CompanyTestEntity relatedCompanyTestEntity2 =
			batchTestEntity1.getRelatedCompanyTestEntity();

		if ((relatedCompanyTestEntity1 != null) &&
			(relatedCompanyTestEntity2 != null)) {

			Assert.assertEquals(
				relatedCompanyTestEntity1.getExternalReferenceCode(),
				relatedCompanyTestEntity2.getExternalReferenceCode());
		}
		else {
			Assert.assertEquals(
				relatedCompanyTestEntity1, relatedCompanyTestEntity2);
		}
	}

	private void _assertEquals(
		Class<?> expectedClass, String expectedErrorMessage,
		String expectedExternalReferenceCode, int expectedType,
		ExportImportReportEntry exportImportReportEntry) {

		Assert.assertEquals(
			expectedExternalReferenceCode,
			exportImportReportEntry.getClassExternalReferenceCode());
		Assert.assertEquals(
			expectedClass.getName(), exportImportReportEntry.getClassName());
		Assert.assertEquals(
			expectedErrorMessage, exportImportReportEntry.getErrorMessage());

		if (expectedErrorMessage == null) {
			Assert.assertNull(exportImportReportEntry.getErrorStacktrace());
		}
		else {
			String errorStacktrace =
				exportImportReportEntry.getErrorStacktrace();

			Assert.assertTrue(errorStacktrace.contains(expectedErrorMessage));
		}

		Assert.assertEquals(
			ExportImportReportEntryConstants.STATUS_UNRESOLVED,
			exportImportReportEntry.getStatus());
		Assert.assertEquals(expectedType, exportImportReportEntry.getType());
	}

	private BatchTestEntityResource _batchTestEntityResource;
	private CompanyTestEntityResource _companyTestEntityResource;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

	private class TestImportTaskPreAction implements ImportTaskPreAction {

		public TestImportTaskPreAction(
			String errorMessage, String externalReferenceCode) {

			_errorMessage = errorMessage;
			_externalReferenceCode = externalReferenceCode;
		}

		@Override
		public void run(
			BatchEngineImportTask batchEngineImportTask,
			BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate,
			ImportTaskContext importTaskContext, Object item) {

			com.liferay.portal.tools.rest.builder.test.dto.v1_0.BatchTestEntity
				batchTestEntity =
					(com.liferay.portal.tools.rest.builder.test.dto.v1_0.
						BatchTestEntity)item;

			if (StringUtil.equals(
					batchTestEntity.getExternalReferenceCode(),
					_externalReferenceCode)) {

				throw new UnsupportedOperationException(_errorMessage);
			}
		}

		private final String _errorMessage;
		private final String _externalReferenceCode;

	}

}