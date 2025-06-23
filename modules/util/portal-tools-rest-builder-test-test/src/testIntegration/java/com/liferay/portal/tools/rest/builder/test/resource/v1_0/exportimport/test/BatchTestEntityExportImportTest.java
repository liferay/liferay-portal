/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.BatchTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.pagination.Page;
import com.liferay.portal.tools.rest.builder.test.client.resource.v1_0.BatchTestEntityResource;
import com.liferay.portal.util.PropsValues;
import com.liferay.staging.StagingGroupHelper;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
	public static void setUpClass() {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			CompanyConstants.SYSTEM, true, "LPD-35914");
	}

	@AfterClass
	public static void tearDownClass() {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			CompanyConstants.SYSTEM, false, "LPD-35914");
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
		).build();
	}

	@Test
	public void test() throws Exception {
		Page<BatchTestEntity> batchTestEntitiesPage =
			_batchTestEntityResource.getBatchTestEntitiesPage();

		long totalCount = batchTestEntitiesPage.getTotalCount();

		BatchTestEntity batchTestEntity1 =
			_batchTestEntityResource.postBatchTestEntity(
				new BatchTestEntity() {
					{
						externalReferenceCode = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						id = RandomTestUtil.randomLong();
						name = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
					}
				});
		BatchTestEntity batchTestEntity2 =
			_batchTestEntityResource.postBatchTestEntity(
				new BatchTestEntity() {
					{
						externalReferenceCode = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						id = RandomTestUtil.randomLong();
						name = StringUtil.toLowerCase(
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
							).build())),
			larFile);

		batchTestEntitiesPage =
			_batchTestEntityResource.getBatchTestEntitiesPage();

		Assert.assertEquals(
			totalCount + 2, batchTestEntitiesPage.getTotalCount());
	}

	private BatchTestEntityResource _batchTestEntityResource;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

}