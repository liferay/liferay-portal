/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.dashboard.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.commerce.dashboard.web.internal.configuration.CommerceDashboardForecastPortletInstanceConfiguration;
import com.liferay.commerce.dashboard.web.internal.display.context.helper.CommerceDashboardForecastRequestHelper;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Riccardo Ferrari
 */
public class CommerceDashboardForecastDisplayContext {

	public CommerceDashboardForecastDisplayContext(
			ModelResourcePermission<AccountEntry>
				accountEntryModelResourcePermission,
			AssetCategoryLocalService assetCategoryLocalService,
			ConfigurationProvider configurationProvider,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		_accountEntryModelResourcePermission =
			accountEntryModelResourcePermission;
		_assetCategoryLocalService = assetCategoryLocalService;

		_commerceDashboardForecastRequestHelper =
			new CommerceDashboardForecastRequestHelper(httpServletRequest);

		_commerceDashboardForecastPortletInstanceConfiguration =
			configurationProvider.getPortletInstanceConfiguration(
				CommerceDashboardForecastPortletInstanceConfiguration.class,
				_commerceDashboardForecastRequestHelper.getThemeDisplay());
	}

	public String getAssetCategoryIds() {
		ThemeDisplay themeDisplay =
			_commerceDashboardForecastRequestHelper.getThemeDisplay();

		return StringUtil.merge(
			TransformUtil.transform(
				StringUtil.split(
					_commerceDashboardForecastPortletInstanceConfiguration.
						assetCategoryExternalReferenceCodes()),
				assetCategoryExternalReferenceCode -> {
					AssetCategory assetCategory =
						_assetCategoryLocalService.
							fetchAssetCategoryByExternalReferenceCode(
								assetCategoryExternalReferenceCode,
								themeDisplay.getScopeGroupId());

					if (assetCategory == null) {
						assetCategory =
							_assetCategoryLocalService.
								fetchAssetCategoryByExternalReferenceCode(
									assetCategoryExternalReferenceCode,
									themeDisplay.getCompanyGroupId());
					}

					if (assetCategory == null) {
						return null;
					}

					return String.valueOf(assetCategory.getCategoryId());
				},
				String.class));
	}

	public boolean hasViewPermission() {
		PermissionChecker permissionChecker =
			_commerceDashboardForecastRequestHelper.getPermissionChecker();

		try {
			return _accountEntryModelResourcePermission.contains(
				permissionChecker,
				_commerceDashboardForecastRequestHelper.getAccountEntryId(),
				ActionKeys.VIEW);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceDashboardForecastDisplayContext.class);

	private final ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;
	private final AssetCategoryLocalService _assetCategoryLocalService;
	private final CommerceDashboardForecastPortletInstanceConfiguration
		_commerceDashboardForecastPortletInstanceConfiguration;
	private final CommerceDashboardForecastRequestHelper
		_commerceDashboardForecastRequestHelper;

}