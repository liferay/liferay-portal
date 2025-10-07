/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.portlet.action;

import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactory;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.exception.LARFileNameException;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SessionTreeJSClicks;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Kocsis
 * @author Máté Thurzó
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ExportImportPortletKeys.COMPANY_EXPORT,
		"jakarta.portlet.name=" + ExportImportPortletKeys.EXPORT,
		"mvc.command.name=/export_import/export_layouts"
	},
	service = MVCActionCommand.class
)
public class ExportLayoutsMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		hideDefaultSuccessMessage(actionRequest);

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (Validator.isNull(cmd)) {
			SessionMessages.add(
				actionRequest,
				_portal.getPortletId(actionRequest) +
					SessionMessages.KEY_SUFFIX_FORCE_SEND_REDIRECT);

			hideDefaultSuccessMessage(actionRequest);

			return;
		}

		setLayoutIdMap(actionRequest);

		try {
			_exportImportService.exportLayoutsAsFileInBackground(
				getExportImportConfiguration(actionRequest));

			sendRedirect(actionRequest, actionResponse);
		}
		catch (Exception exception) {
			SessionErrors.add(actionRequest, exception.getClass());

			if (!(exception instanceof LARFileNameException)) {
				_log.error(exception);
			}
		}
	}

	protected ExportImportConfiguration getExportImportConfiguration(
			ActionRequest actionRequest)
		throws Exception {

		Map<String, Serializable> exportLayoutSettingsMap = null;

		long exportImportConfigurationId = ParamUtil.getLong(
			actionRequest, "exportImportConfigurationId");

		if (exportImportConfigurationId > 0) {
			ExportImportConfiguration exportImportConfiguration =
				_exportImportConfigurationLocalService.
					fetchExportImportConfiguration(exportImportConfigurationId);

			if (exportImportConfiguration != null) {
				exportLayoutSettingsMap =
					exportImportConfiguration.getSettingsMap();
			}
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		boolean privateLayout = ParamUtil.getBoolean(
			actionRequest, "privateLayout");

		if (exportLayoutSettingsMap == null) {
			long groupId = ParamUtil.getLong(actionRequest, "liveGroupId");

			exportLayoutSettingsMap =
				_exportImportConfigurationSettingsMapFactory.
					buildExportLayoutSettingsMap(
						themeDisplay.getUserId(), groupId, privateLayout,
						_getLayoutIds(actionRequest),
						actionRequest.getParameterMap(),
						themeDisplay.getLocale(), themeDisplay.getTimeZone());
		}

		String taskName = ParamUtil.getString(actionRequest, "name");

		if (Validator.isNull(taskName)) {
			if (FeatureFlagManagerUtil.isEnabled(
					themeDisplay.getCompanyId(), "LPD-35914")) {

				taskName = _language.get(actionRequest.getLocale(), "export");
			}
			else {
				Group group = themeDisplay.getScopeGroup();

				if (group.isPrivateLayoutsEnabled()) {
					if (privateLayout) {
						taskName = _language.get(
							actionRequest.getLocale(), "private-pages");
					}
					else {
						taskName = _language.get(
							actionRequest.getLocale(), "public-pages");
					}
				}
				else {
					taskName = _language.get(
						actionRequest.getLocale(), "pages");
				}
			}
		}

		return _exportImportConfigurationLocalService.
			addDraftExportImportConfiguration(
				themeDisplay.getUserId(), taskName,
				ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
				exportLayoutSettingsMap);
	}

	protected void setLayoutIdMap(ActionRequest actionRequest) {
		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		boolean privateLayout = ParamUtil.getBoolean(
			actionRequest, "privateLayout");

		String treeId = ParamUtil.getString(actionRequest, "treeId");

		actionRequest.setAttribute(
			"layoutIdMap",
			_exportImportHelper.getSelectedLayoutsJSON(
				groupId, privateLayout,
				SessionTreeJSClicks.getOpenNodes(
					httpServletRequest, treeId + "SelectedNode")));
	}

	private long[] _getLayoutIds(PortletRequest portletRequest)
		throws Exception {

		return _exportImportHelper.getLayoutIds(portletRequest);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportLayoutsMVCActionCommand.class);

	@Reference
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Reference
	private ExportImportConfigurationSettingsMapFactory
		_exportImportConfigurationSettingsMapFactory;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private ExportImportService _exportImportService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}