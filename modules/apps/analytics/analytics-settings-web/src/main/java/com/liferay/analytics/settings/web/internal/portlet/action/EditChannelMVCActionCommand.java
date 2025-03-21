/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.portlet.action;

import com.liferay.analytics.settings.web.internal.util.AnalyticsSettingsUtil;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André Miranda
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"mvc.command.name=/analytics_settings/edit_channel"
	},
	service = MVCActionCommand.class
)
public class EditChannelMVCActionCommand extends BaseAnalyticsMVCActionCommand {

	@Override
	protected void updateConfigurationProperties(
			ActionRequest actionRequest,
			Dictionary<String, Object> configurationProperties)
		throws Exception {

		String[] selectedGroupIds = ParamUtil.getStringValues(
			actionRequest, "rowIds");

		List<String> removedGroupIds = _notifyAnalyticsCloudUpdateChannel(
			actionRequest, ParamUtil.getString(actionRequest, "channelId"),
			selectedGroupIds);

		Set<String> liferayAnalyticsGroupIds = SetUtil.fromArray(
			(String[])configurationProperties.get("syncedGroupIds"));

		Collections.addAll(liferayAnalyticsGroupIds, selectedGroupIds);

		liferayAnalyticsGroupIds.removeAll(removedGroupIds);

		_notifyAnalyticsCloudSitesSelected(
			actionRequest, liferayAnalyticsGroupIds);

		_updateCompanyPreferences(actionRequest, liferayAnalyticsGroupIds);

		configurationProperties.put(
			"syncedGroupIds", liferayAnalyticsGroupIds.toArray(new String[0]));
	}

	private JSONObject _buildGroupJSONObject(
		Group group, ThemeDisplay themeDisplay) {

		JSONObject groupJSONObject = JSONUtil.put(
			"id", String.valueOf(group.getGroupId()));

		try {
			return groupJSONObject.put(
				"name", group.getDescriptiveName(themeDisplay.getLocale()));
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", themeDisplay.getLocale(), getClass());

			return groupJSONObject.put(
				"name", _language.get(resourceBundle, "unknown"));
		}
	}

	private void _notifyAnalyticsCloudSitesSelected(
			ActionRequest actionRequest, Set<String> liferayAnalyticsGroupIds)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!AnalyticsSettingsUtil.isAnalyticsEnabled(
				themeDisplay.getCompanyId())) {

			return;
		}

		boolean sitesSelected = true;

		if (liferayAnalyticsGroupIds.isEmpty()) {
			sitesSelected = false;
		}

		HttpResponse httpResponse = AnalyticsSettingsUtil.doPut(
			JSONUtil.put("sitesSelected", sitesSelected),
			themeDisplay.getCompanyId(),
			String.format(
				"api/1.0/data-sources/%s/details",
				AnalyticsSettingsUtil.getDataSourceId(
					themeDisplay.getCompanyId())));

		StatusLine statusLine = httpResponse.getStatusLine();

		if (statusLine.getStatusCode() == HttpStatus.SC_FORBIDDEN) {
			checkResponse(themeDisplay.getCompanyId(), httpResponse);

			return;
		}

		if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
			throw new PortalException(
				"Unable to update data source details: " +
					EntityUtils.toString(httpResponse.getEntity()));
		}
	}

	private List<String> _notifyAnalyticsCloudUpdateChannel(
			ActionRequest actionRequest, String channelId,
			String[] selectedGroupIds)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!AnalyticsSettingsUtil.isAnalyticsEnabled(
				themeDisplay.getCompanyId())) {

			return Collections.emptyList();
		}

		HttpResponse httpResponse = AnalyticsSettingsUtil.doPatch(
			JSONUtil.put(
				"dataSourceId",
				AnalyticsSettingsUtil.getDataSourceId(
					themeDisplay.getCompanyId())
			).put(
				"groups",
				JSONUtil.toJSONArray(
					TransformUtil.transformToList(
						selectedGroupIds,
						selectedGroupId -> groupLocalService.fetchGroup(
							Long.valueOf(selectedGroupId))),
					group -> _buildGroupJSONObject(group, themeDisplay))
			),
			themeDisplay.getCompanyId(), "api/1.0/channels/" + channelId);

		StatusLine statusLine = httpResponse.getStatusLine();

		if (statusLine.getStatusCode() == HttpStatus.SC_FORBIDDEN) {
			checkResponse(themeDisplay.getCompanyId(), httpResponse);

			return Collections.emptyList();
		}

		if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
			throw new PortalException(
				"Unable to create channels: " +
					EntityUtils.toString(httpResponse.getEntity()));
		}

		return _updateTypeSettingsProperties(
			channelId, EntityUtils.toString(httpResponse.getEntity()),
			selectedGroupIds);
	}

	private Set<String> _updateCompanyPreferences(
			ActionRequest actionRequest, Set<String> liferayAnalyticsGroupIds)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_companyService.updatePreferences(
			themeDisplay.getCompanyId(),
			UnicodePropertiesBuilder.create(
				true
			).put(
				"liferayAnalyticsGroupIds",
				StringUtil.merge(liferayAnalyticsGroupIds, StringPool.COMMA)
			).build());

		return liferayAnalyticsGroupIds;
	}

	private List<String> _updateTypeSettingsProperties(
			String channelId, String json, String[] selectedGroupIds)
		throws Exception {

		JSONObject responseJSONObject = _jsonFactory.createJSONObject(json);

		String[] removedGroupIds = JSONUtil.toStringArray(
			responseJSONObject.getJSONArray("removedGroupIds"));

		removeChannelId(removedGroupIds);

		for (String selectedGroupId : selectedGroupIds) {
			Group group = groupLocalService.fetchGroup(
				GetterUtil.getLong(selectedGroupId));

			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			typeSettingsUnicodeProperties.put("analyticsChannelId", channelId);

			group.setTypeSettingsProperties(typeSettingsUnicodeProperties);

			groupLocalService.updateGroup(group);
		}

		return Arrays.asList(removedGroupIds);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditChannelMVCActionCommand.class);

	@Reference
	private CompanyService _companyService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}