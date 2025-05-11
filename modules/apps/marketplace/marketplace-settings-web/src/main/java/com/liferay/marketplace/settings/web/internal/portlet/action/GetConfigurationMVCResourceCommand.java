/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.settings.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.marketplace.constants.MarketplaceActionKeys;
import com.liferay.marketplace.constants.MarketplacePortletKeys;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Keven Leone
 */
@Component(
	property = {
		"javax.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"javax.portlet.name=com_liferay_commerce_channel_web_internal_portlet_CommerceChannelsPortlet",
		"javax.portlet.name=com_liferay_fragment_web_portlet_FragmentPortlet",
		"javax.portlet.name=com_liferay_layout_content_page_editor_web_internal_portlet_ContentPageEditorPortlet",
		"mvc.command.name=/marketplace_settings/get_configuration"
	},
	service = MVCResourceCommand.class
)
public class GetConfigurationMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletPermissionUtil.check(
			themeDisplay.getPermissionChecker(), MarketplacePortletKeys.GENERAL,
			MarketplaceActionKeys.GET_AUTHORIZATION);

		boolean authorized = Validator.isNotNull(
			PrefsPropsUtil.getString(
				themeDisplay.getCompanyId(), "marketplaceAccessToken"));

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"authorized", authorized
			).put(
				"data",
				() -> {
					if (!authorized) {
						return null;
					}

					return JSONUtil.put(
						"serviceURL",
						PrefsPropsUtil.getString(
							themeDisplay.getCompanyId(),
							"marketplaceServiceURL")
					).put(
						"settings",
						_jsonFactory.createJSONObject(
							PrefsPropsUtil.getString(
								themeDisplay.getCompanyId(),
								"marketplaceSettings"))
					).put(
						"url",
						PrefsPropsUtil.getString(PropsKeys.MARKETPLACE_URL)
					);
				}
			));
	}

	@Reference
	private JSONFactory _jsonFactory;

}