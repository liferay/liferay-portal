/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.dashboard.web.internal.portlet.action;

import com.liferay.account.model.AccountEntry;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.commerce.dashboard.web.internal.constants.CommerceDashboardPortletKeys;
import com.liferay.commerce.dashboard.web.internal.display.context.CommerceDashboardForecastDisplayContext;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Riccardo Ferrari
 */
@Component(
	property = "jakarta.portlet.name=" + CommerceDashboardPortletKeys.COMMERCE_DASHBOARD_FORECASTS_CHART,
	service = ConfigurationAction.class
)
public class CommerceDashboardForecastConfigurationAction
	extends DefaultConfigurationAction {

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		try {
			CommerceDashboardForecastDisplayContext
				commerceDashboardForecastDisplayContext =
					new CommerceDashboardForecastDisplayContext(
						_accountEntryModelResourcePermission,
						_assetCategoryLocalService, _configurationProvider,
						httpServletRequest);

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				commerceDashboardForecastDisplayContext);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		setPreference(
			actionRequest, "assetCategoryExternalReferenceCodes",
			StringUtil.merge(
				TransformUtil.transform(
					serviceContext.getAssetCategoryIds(),
					assetCategoryId -> {
						AssetCategory assetCategory =
							_assetCategoryLocalService.fetchAssetCategory(
								assetCategoryId);

						if (assetCategory == null) {
							return null;
						}

						return assetCategory.getExternalReferenceCode();
					},
					String.class)));

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceDashboardForecastConfigurationAction.class);

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

}