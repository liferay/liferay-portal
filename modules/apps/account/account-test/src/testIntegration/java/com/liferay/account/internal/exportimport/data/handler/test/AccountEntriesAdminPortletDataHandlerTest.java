/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.exportimport.data.handler.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.account.service.test.util.AccountGroupTestUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.staging.StagingGroupHelper;

import java.io.File;

import java.util.List;
import java.util.Objects;

import org.hamcrest.CoreMatchers;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@FeatureFlag("LPD-35914")
@RunWith(Arquillian.class)
public class AccountEntriesAdminPortletDataHandlerTest {

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

	@Test
	public void testExportImportAccountEntries() throws Exception {
		AccountGroup accountGroup = AccountGroupTestUtil.addAccountGroup(
			_accountGroupLocalService, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		Group group = _stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null,
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getCompanyId(), group.getGroupId(),
				TestPropsValues.getUserId()));

		_accountGroupRelLocalService.addAccountGroupRel(
			accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
			accountEntry.getAccountEntryId());

		Organization organization = _organizationLocalService.addOrganization(
			TestPropsValues.getUserId(), 0, RandomTestUtil.randomString(),
			false);

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry.getAccountEntryId(), organization.getOrganizationId());

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
									AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
								new String[] {Boolean.TRUE.toString()}
							).build())));

		_accountEntryLocalService.deleteAccountEntry(
			accountEntry.getAccountEntryId());
		_accountGroupLocalService.deleteAccountGroup(
			accountGroup.getAccountGroupId());
		_organizationLocalService.deleteOrganization(
			organization.getOrganizationId());

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
									AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
								new String[] {Boolean.TRUE.toString()}
							).build()));

		_exportImportLocalService.importLayouts(
			exportImportConfiguration, larFile);

		accountEntry =
			_accountEntryLocalService.fetchAccountEntryByExternalReferenceCode(
				accountEntry.getExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, accountEntry.getStatus());

		List<ExportImportReportEntry> exportImportReportEntries =
			_exportImportReportEntryLocalService.getExportImportReportEntries(
				TestPropsValues.getCompanyId(),
				exportImportConfiguration.getExportImportConfigurationId());

		Assert.assertEquals(
			exportImportReportEntries.toString(), 2,
			exportImportReportEntries.size());

		accountGroup =
			_accountGroupLocalService.fetchAccountGroupByExternalReferenceCode(
				accountGroup.getExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, accountGroup.getStatus());

		String accountGroupExternalReferenceCode =
			accountGroup.getExternalReferenceCode();

		Assert.assertTrue(
			ListUtil.exists(
				exportImportReportEntries,
				exportImportReportEntry ->
					Objects.equals(
						exportImportReportEntry.getClassExternalReferenceCode(),
						accountGroupExternalReferenceCode) &&
					(exportImportReportEntry.getType() ==
						ExportImportReportEntryConstants.TYPE_EMPTY)));

		organization =
			_organizationLocalService.fetchOrganizationByExternalReferenceCode(
				organization.getExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, organization.getStatus());

		String organizationExternalReferenceCode =
			organization.getExternalReferenceCode();

		Assert.assertTrue(
			ListUtil.exists(
				exportImportReportEntries,
				exportImportReportEntry ->
					Objects.equals(
						exportImportReportEntry.getClassExternalReferenceCode(),
						organizationExternalReferenceCode) &&
					(exportImportReportEntry.getType() ==
						ExportImportReportEntryConstants.TYPE_EMPTY)));
	}

	@Test
	public void testPortletDataHandlerRegistration() throws Exception {
		PortletDataHandler portletDataHandler =
			_portletDataHandlerProvider.provide(
				TestPropsValues.getCompanyId(),
				AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN);

		Assert.assertThat(
			ClassUtil.getClassName(portletDataHandler),
			CoreMatchers.containsString("BatchEnginePortletDataHandler"));
		Assert.assertEquals(
			"com.liferay.headless.admin.user.dto.v1_0.Account",
			portletDataHandler.getClassNames()[0]);
		Assert.assertEquals(
			AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
			portletDataHandler.getName());
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	@Inject
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@Inject
	private PortletDataHandlerProvider _portletDataHandlerProvider;

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

}