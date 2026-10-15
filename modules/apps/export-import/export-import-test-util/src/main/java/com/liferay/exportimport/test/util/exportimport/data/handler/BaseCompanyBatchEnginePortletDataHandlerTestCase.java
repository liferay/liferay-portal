/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test.util.exportimport.data.handler;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportLocalServiceUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

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
		CompanyLocalServiceUtil.deleteCompany(_targetCompany);
	}

	@Before
	@Override
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			BaseCompanyBatchEnginePortletDataHandlerTestCase.class);

		BundleContext bundleContext = bundle.getBundleContext();

		StagingGroupHelper stagingGroupHelper = bundleContext.getService(
			bundleContext.getServiceReference(StagingGroupHelper.class));

		group = stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());
		targetGroup = stagingGroupHelper.fetchCompanyGroup(
			_targetCompany.getCompanyId());

		targetUser = UserTestUtil.addCompanyAdminUser(_targetCompany);

		super.setUp();
	}

	@Override
	protected Role addRole() throws Exception {
		Role role = super.addRole();

		_roleLocalService.addRole(
			role.getExternalReferenceCode(), targetUser.getUserId(), null, 0,
			role.getName(), null, null, RoleConstants.TYPE_REGULAR, null, null);

		return role;
	}

	@Override
	protected User addUser() throws Exception {
		User user = _userLocalService.updateExternalReferenceCode(
			super.addUser(), RandomTestUtil.randomString());

		_userLocalService.updateExternalReferenceCode(
			UserTestUtil.addUser(_targetCompany),
			user.getExternalReferenceCode());

		return user;
	}

	@Override
	protected void exportImport(
			Map<String, String[]> parameterMap, Date startDate, Date endDate)
		throws Exception {

		ExportImportConfiguration exportImportConfiguration =
			addExportConfiguration(
				ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildExportLayoutSettingsMap(
						TestPropsValues.getUser(), group.getGroupId(), false,
						new long[0], parameterMap),
				startDate, endDate);

		File larFile = ExportImportLocalServiceUtil.exportLayoutsAsFile(
			exportImportConfiguration);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(targetUser));

			exportImportConfiguration = updateImportConfiguration(
				exportImportConfiguration, targetUser,
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildImportLayoutSettingsMap(
						targetUser, targetGroup.getGroupId(), false, null,
						parameterMap),
				targetGroup.getGroupId());

			ExportImportLocalServiceUtil.importLayoutsDataDeletions(
				exportImportConfiguration, larFile);

			ExportImportLocalServiceUtil.importLayouts(
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

	private static Company _targetCompany;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}