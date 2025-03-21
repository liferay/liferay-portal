/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.layout.seo.service.LayoutSEOSiteLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SITE_SETTINGS,
		"mvc.command.name=/layout/edit_open_graph_site_settings"
	},
	service = MVCActionCommand.class
)
public class EditOpenGraphSiteSettingsMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		boolean openGraphEnabled = ParamUtil.getBoolean(
			actionRequest, "openGraphEnabled", true);
		Map<Locale, String> openGraphImageAltMap =
			_localization.getLocalizationMap(
				actionRequest, "openGraphImageAlt");
		long openGraphImageFileEntryId = ParamUtil.getLong(
			actionRequest, "openGraphImageFileEntryId");

		_layoutSEOSiteLocalService.updateLayoutSEOSite(
			_portal.getUserId(actionRequest), themeDisplay.getScopeGroupId(),
			openGraphEnabled, openGraphImageAltMap, openGraphImageFileEntryId,
			ServiceContextFactory.getInstance(
				Group.class.getName(), actionRequest));
	}

	@Reference
	private LayoutSEOSiteLocalService _layoutSEOSiteLocalService;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

}