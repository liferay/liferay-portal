/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.sites.kernel.util.Sites;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SITE_SETTINGS,
		"mvc.command.name=/site_admin/edit_analytics"
	},
	service = MVCActionCommand.class
)
public class EditAnalyticsMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long liveGroupId = ParamUtil.getLong(actionRequest, "liveGroupId");

		Group liveGroup = _groupLocalService.getGroup(liveGroupId);

		UnicodeProperties typeSettingsUnicodeProperties =
			liveGroup.getTypeSettingsProperties();

		String[] analyticsTypes = PrefsPropsUtil.getStringArray(
			themeDisplay.getCompanyId(), PropsKeys.ADMIN_ANALYTICS_TYPES,
			StringPool.NEW_LINE);

		for (String analyticsType : analyticsTypes) {
			if (StringUtil.equalsIgnoreCase(analyticsType, "google")) {
				String googleAnalyticsCreateCustomConfiguration =
					ParamUtil.getString(
						actionRequest,
						"googleAnalyticsCreateCustomConfiguration",
						typeSettingsUnicodeProperties.getProperty(
							"googleAnalyticsCreateCustomConfiguration"));

				typeSettingsUnicodeProperties.setProperty(
					"googleAnalyticsCreateCustomConfiguration",
					googleAnalyticsCreateCustomConfiguration);

				String googleAnalyticsCustomConfiguration = ParamUtil.getString(
					actionRequest, "googleAnalyticsCustomConfiguration",
					typeSettingsUnicodeProperties.getProperty(
						"googleAnalyticsCustomConfiguration"));

				typeSettingsUnicodeProperties.setProperty(
					"googleAnalyticsCustomConfiguration",
					googleAnalyticsCustomConfiguration);

				String googleAnalyticsId = ParamUtil.getString(
					actionRequest, "googleAnalyticsId",
					typeSettingsUnicodeProperties.getProperty(
						"googleAnalyticsId"));

				typeSettingsUnicodeProperties.setProperty(
					"googleAnalyticsId", googleAnalyticsId);
			}
			else if (StringUtil.equalsIgnoreCase(
						analyticsType, "googleAnalytics4")) {

				String googleAnalytics4CustomConfiguration =
					ParamUtil.getString(
						actionRequest, "googleAnalytics4CustomConfiguration",
						typeSettingsUnicodeProperties.getProperty(
							"googleAnalytics4CustomConfiguration"));

				typeSettingsUnicodeProperties.setProperty(
					"googleAnalytics4CustomConfiguration",
					googleAnalytics4CustomConfiguration);

				String googleAnalytics4Id = ParamUtil.getString(
					actionRequest, "googleAnalytics4Id",
					typeSettingsUnicodeProperties.getProperty(
						"googleAnalytics4Id"));

				typeSettingsUnicodeProperties.setProperty(
					"googleAnalytics4Id", googleAnalytics4Id);
			}
			else {
				String analyticsScript = ParamUtil.getString(
					actionRequest, Sites.ANALYTICS_PREFIX + analyticsType,
					typeSettingsUnicodeProperties.getProperty(analyticsType));

				typeSettingsUnicodeProperties.setProperty(
					Sites.ANALYTICS_PREFIX + analyticsType, analyticsScript);
			}
		}

		UnicodeProperties formTypeSettingsUnicodeProperties =
			PropertiesParamUtil.getProperties(
				actionRequest, "TypeSettingsProperties--");

		typeSettingsUnicodeProperties.putAll(formTypeSettingsUnicodeProperties);

		if (liveGroup.hasStagingGroup()) {
			Group stagingGroup = liveGroup.getStagingGroup();

			UnicodeProperties stagedGroupTypeSettingsUnicodeProperties =
				stagingGroup.getTypeSettingsProperties();

			stagedGroupTypeSettingsUnicodeProperties.putAll(
				formTypeSettingsUnicodeProperties);

			_groupService.updateGroup(
				stagingGroup.getGroupId(),
				stagedGroupTypeSettingsUnicodeProperties.toString());
		}

		_groupService.updateGroup(
			liveGroup.getGroupId(), typeSettingsUnicodeProperties.toString());
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private GroupService _groupService;

}