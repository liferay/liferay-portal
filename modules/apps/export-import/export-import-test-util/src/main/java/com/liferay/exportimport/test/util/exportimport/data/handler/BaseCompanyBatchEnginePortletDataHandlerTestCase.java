/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test.util.exportimport.data.handler;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.staging.StagingGroupHelper;

import java.io.File;

import java.util.Date;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author Alberto Javier Moreno Lage
 */
public abstract class BaseCompanyBatchEnginePortletDataHandlerTestCase<T>
	extends BaseBatchEnginePortletDataHandlerTestCase<T> {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_targetCompany = CompanyTestUtil.addCompany();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_companyLocalService.deleteCompany(_targetCompany);
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_group = _stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());
		_targetGroup = _stagingGroupHelper.fetchCompanyGroup(
			_targetCompany.getCompanyId());
	}

	@Override
	protected Role addRole() throws Exception {
		Role role = super.addRole();

		_roleLocalService.addRole(
			role.getExternalReferenceCode(), _targetUser.getUserId(), null, 0,
			role.getName(), null, null, RoleConstants.TYPE_REGULAR, null, null);

		return role;
	}

	@Override
	protected User addUser() throws Exception {
		User user = super.addUser();

		_targetUser = _userLocalService.updateExternalReferenceCode(
			UserTestUtil.addUser(_targetCompany),
			user.getExternalReferenceCode());

		return user;
	}

	@Override
	protected void exportImport(
			Date endDate, Map<String, String[]> parameterMap, Date startDate)
		throws Exception {

		ExportImportConfiguration exportImportConfiguration =
			addExportImportConfiguration(
				endDate,
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildExportLayoutSettingsMap(
						TestPropsValues.getUser(), _group.getGroupId(), false,
						new long[0], parameterMap),
				startDate,
				ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT);

		File larFile = _exportImportLocalService.exportLayoutsAsFile(
			exportImportConfiguration);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(_targetUser));

			exportImportConfiguration = updateExportImportConfiguration(
				exportImportConfiguration,
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildImportLayoutSettingsMap(
						_targetUser, _targetGroup.getGroupId(), false, null,
						parameterMap),
				_targetGroup.getGroupId(), _targetUser);

			_exportImportLocalService.importLayoutsDataDeletions(
				exportImportConfiguration, larFile);

			_exportImportLocalService.importLayouts(
				exportImportConfiguration, larFile);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);

			FileUtil.delete(larFile);
		}
	}

	@Override
	protected DataLevel getDataLevel() {
		return DataLevel.PORTAL;
	}

	@Override
	protected Group getGroup() {
		return _group;
	}

	@Override
	protected Group getTargetGroup() {
		return _targetGroup;
	}

	protected User getTargetUser() {
		return _targetUser;
	}

	@Inject
	private static CompanyLocalService _companyLocalService;

	private static Company _targetCompany;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	private Group _group;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

	private Group _targetGroup;
	private User _targetUser;

	@Inject
	private UserLocalService _userLocalService;

}