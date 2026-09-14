/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.roles.admin.constants.RolesAdminPortletKeys;
import com.liferay.staging.StagingGroupHelper;

import java.io.File;

import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Balazs Breier
 */
@RunWith(Arquillian.class)
public class RolesAdminPortletDataHandlerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = _stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition();

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), _objectDefinition.getResourceName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), _role.getRoleId(),
			ObjectActionKeys.ADD_OBJECT_ENTRY);
	}

	@Test
	public void testExportImportRolesWithMissingObjectDefinition()
		throws Exception {

		File larFile = _exportImportLocalService.exportLayoutsAsFile(
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportLayoutSettingsMap(
							TestPropsValues.getUser(), _group.getGroupId(),
							false, new long[0],
							HashMapBuilder.put(
								PortletDataHandlerKeys.PORTLET_DATA,
								new String[] {Boolean.TRUE.toString()}
							).put(
								PortletDataHandlerKeys.PORTLET_DATA +
									StringPool.UNDERLINE +
										RolesAdminPortletKeys.ROLES_ADMIN,
								new String[] {Boolean.TRUE.toString()}
							).build())));

		String objectDefinitionExternalReferenceCode =
			_objectDefinition.getExternalReferenceCode();
		String resourceName = _objectDefinition.getResourceName();
		String roleExternalReferenceCode = _role.getExternalReferenceCode();

		_roleLocalService.deleteRole(_role.getRoleId());

		_objectDefinitionLocalService.deleteObjectDefinition(
			_objectDefinition.getObjectDefinitionId());

		ExportImportConfiguration exportImportConfiguration = _importRoles(
			larFile);

		_role = _roleLocalService.fetchRoleByExternalReferenceCode(
			roleExternalReferenceCode, TestPropsValues.getCompanyId());

		Assert.assertNotNull(_role);

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(), resourceName,
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				_role.getRoleId(), ObjectActionKeys.ADD_OBJECT_ENTRY));

		String unresolvedPermissionName = StringBundler.concat(
			"com.liferay.object#", objectDefinitionExternalReferenceCode,
			StringPool.POUND, ObjectActionKeys.ADD_OBJECT_ENTRY);

		List<ExportImportReportEntry> exportImportReportEntries =
			_exportImportReportEntryLocalService.getExportImportReportEntries(
				TestPropsValues.getCompanyId(),
				exportImportConfiguration.getExportImportConfigurationId());

		Assert.assertTrue(
			exportImportReportEntries.toString(),
			ListUtil.exists(
				exportImportReportEntries,
				exportImportReportEntry -> _isUnresolvedPermissionWarning(
					exportImportReportEntry, roleExternalReferenceCode,
					unresolvedPermissionName)));
	}

	@Test
	public void testExportImportRolesWithRecreatedObjectDefinition()
		throws Exception {

		File larFile = _exportImportLocalService.exportLayoutsAsFile(
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportLayoutSettingsMap(
							TestPropsValues.getUser(), _group.getGroupId(),
							false, new long[0],
							HashMapBuilder.put(
								PortletDataHandlerKeys.PORTLET_DATA,
								new String[] {Boolean.TRUE.toString()}
							).put(
								PortletDataHandlerKeys.PORTLET_DATA +
									StringPool.UNDERLINE +
										RolesAdminPortletKeys.ROLES_ADMIN,
								new String[] {Boolean.TRUE.toString()}
							).build())));

		String objectDefinitionExternalReferenceCode =
			_objectDefinition.getExternalReferenceCode();
		String roleExternalReferenceCode = _role.getExternalReferenceCode();

		_roleLocalService.deleteRole(_role.getRoleId());

		_objectDefinitionLocalService.deleteObjectDefinition(
			_objectDefinition.getObjectDefinitionId());

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		_objectDefinition =
			_objectDefinitionLocalService.updateExternalReferenceCode(
				objectDefinition.getObjectDefinitionId(),
				objectDefinitionExternalReferenceCode);

		_importRoles(larFile);

		_role = _roleLocalService.fetchRoleByExternalReferenceCode(
			roleExternalReferenceCode, TestPropsValues.getCompanyId());

		Assert.assertNotNull(_role);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				_objectDefinition.getResourceName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				_role.getRoleId(), ObjectActionKeys.ADD_OBJECT_ENTRY));
	}

	private ExportImportConfiguration _importRoles(File larFile)
		throws Exception {

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportLayoutSettingsMap(
							TestPropsValues.getUser(), _group.getGroupId(),
							false, new long[0],
							HashMapBuilder.put(
								PortletDataHandlerKeys.PORTLET_DATA,
								new String[] {Boolean.TRUE.toString()}
							).put(
								PortletDataHandlerKeys.PORTLET_DATA +
									StringPool.UNDERLINE +
										RolesAdminPortletKeys.ROLES_ADMIN,
								new String[] {Boolean.TRUE.toString()}
							).build()));

		_exportImportLocalService.importLayouts(
			exportImportConfiguration, larFile);

		return exportImportConfiguration;
	}

	private boolean _isUnresolvedPermissionWarning(
		ExportImportReportEntry exportImportReportEntry,
		String roleExternalReferenceCode, String unresolvedPermissionName) {

		if (!Objects.equals(
				exportImportReportEntry.getClassExternalReferenceCode(),
				roleExternalReferenceCode) ||
			(exportImportReportEntry.getType() !=
				ExportImportReportEntryConstants.TYPE_WARNING)) {

			return false;
		}

		String errorMessage = exportImportReportEntry.getErrorMessage();

		return errorMessage.contains(unresolvedPermissionName);
	}

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	private Group _group;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

}